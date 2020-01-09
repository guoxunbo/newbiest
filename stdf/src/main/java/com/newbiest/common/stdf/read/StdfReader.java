package com.newbiest.common.stdf.read;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.common.stdf.datarecord.FAR;
import com.newbiest.common.stdf.input.*;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import static com.newbiest.common.constant.StdfExceptions.STDF_UNEXPECTED_RECORD_HEADER;

/**
 * Created by guoxunbo on 2019-11-28 18:30
 */
@Slf4j
public class StdfReader implements DataFileReader {

    public static final String STDF_TYPE_1 = "stdf";
    public static final String STDF_TYPE_2 = "std_1";
    public static final String STDF_TYPE_3 = "std";
    public static final short CRLF_CODE = 13;

    public static final byte[] HEAD_BYTES = new byte[4];
    public static final byte[] BUFFER_BYTES = new byte[400];

    private static final TreeSet<Long> keys;
    private static Set<BigInteger> keyBlackList = Sets.newTreeSet();
    private ReadableByteChannel inputChannel;
    private DataFile stdfFile;
    private StdfUser user;

    private ByteBuffer header = ByteBuffer.wrap(HEAD_BYTES).order(ByteOrder.LITTLE_ENDIAN);
    private ByteBuffer buffer = ByteBuffer.wrap(BUFFER_BYTES);
    private boolean crlfError = false;
    private boolean trailingByteRead = false;
    private boolean bytesSkipped = false;
    private boolean littleEndian = true;

    private boolean farFound = false;
    private int recordNumber;
    private long bytesRead = 0L;
    private byte[] trailingByteArray;

    static {
        DataFileReaderFactory.registerReader(STDF_TYPE_1, StdfReader.class);
        DataFileReaderFactory.registerReader(STDF_TYPE_2, StdfReader.class);
        DataFileReaderFactory.registerReader(STDF_TYPE_3, StdfReader.class);
        keys = Sets.newTreeSet();
        keys.add(new Long(797007349L));
    }

    @Override
    public void addRecordUser(RecordUser recordUser) {

    }

    @Override
    public void readFile(InputStream inputStream, DataFile dataFile) throws Exception {
        if (inputStream != null) {
            this.inputChannel = Channels.newChannel(inputStream);
            this.stdfFile = dataFile;
            this.read();
        }
    }

    private Long getFileSize() {
        if (this.stdfFile != null ) {
            File sourceFile = this.stdfFile.getSourceFile();
            if (sourceFile != null && sourceFile.exists()) {
                return sourceFile.length();
            }
        }
        return -1L;
    }

    private void read() throws Exception {
        if (user == null) {
            user = new StdfSimpleUser();
        }

        boolean finished = false;
        boolean acceptableFinishSeen = false;
        long oldBytesRead = 0L;
        Stopwatch stopwatch = Stopwatch.createStarted();
        long fileSize = getFileSize();
        long skippedBytes = 0L;
        while (!finished) {
            if (!this.readHeader()) {
                finished = true;
                break;
            }
            String typeId = StdfTypeFactory.UNKNOWN_TYPE_KEY;
            int length = 0;
            short type = 0;
            short subType = 0;
            String lastMsg = null;

            while(StdfTypeFactory.UNKNOWN_TYPE_KEY.equals(typeId) && !finished) {
                header.position(0);
                length = FieldReader.readUnsignedShort(header);
                type = header.get();
                subType = header.get();
                typeId = StdfTypeFactory.getType(type, subType);
                if (StdfTypeFactory.UNKNOWN_TYPE_KEY.equals(typeId)) {
                    log.warn(String.format("Unknown type of [%s] and subType of [%s]", type, subType));
                }
                skippedBytes++;
                if(!farFound && subType == CRLF_CODE) {
                    crlfError = true;
                    crlfCorrect(header);
                    if(fileSize > 0L && skippedBytes > fileSize) {
                        finished = true;
                    }
                } else {
                    //TODO
                    for(int i = 0; i < 3; i++) {
                        header.put(i, header.get(i + 1));
                    }
                    try {
                        header.position(3);
                        int bytesReadHere = inputChannel.read(header);
                        if(bytesReadHere == -1) {
                            throw new EOFException();
                        } else {
                            if(bytesReadHere != 1) {
                                log.warn("Read more than one byte");
                            }
                        }
                        bytesRead++;
                    } catch(EOFException eof) {
                        log.warn("File ended with corrupt data");
                        bytesSkipped = true;
                        finished = true;
                    }
                }
            }
            if (skippedBytes > 0) {
//                parseErrors.addError((new StringBuilder("Skipped ")).append(skippedBytes).append(" corrupt bytes ending about offset ").append(bytesRead - 4L).append(" followed by record ").append(recordNumber).toString());
                log.warn(String.format("Skipped [%s] corrupt bytes ending about offset [%s] followed by record [%s]", skippedBytes, bytesRead - 4, recordNumber));
                bytesSkipped = true;
                skippedBytes = 0;
            }
            boolean readOkay = true;
            if (finished) {
                readOkay = false;
            } else if (FAR.RECORD_TYPE.equals(typeId)) {
                if (length > 2) {
                    length = 2;
                }
                this.readFar();
                this.farFound = true;
            } else {
                if (!this.farFound && this.recordNumber == 1) {
                    this.littleEndian = true;
                    this.header.rewind();
                    int bigLength = FieldReader.readUnsignedShort(header);
                    if(bigLength < length) {
                        littleEndian = false;
                        length = bigLength;
                        log.warn("No FAR found, assuming big endian");
                    } else {
                        log.warn("No FAR found, assuming little endian");
                    }
                }
                readOkay = readRecordData(length);
            }
            if (!readOkay) {
                finished = true;
            } else {
                acceptableFinishSeen = this.processRecord(length, type, subType, this.bytesRead - (long)length);
            }
        }


        stopwatch.stop();
        if (log.isDebugEnabled()) {
            log.debug(String.format("Read StdfFile elapsed [%s] milliseconds", stopwatch.elapsed(TimeUnit.MILLISECONDS)));
        }
    }

    private boolean processRecord(int length, short type, short subType, long offset) {
        this.buffer.position(0);
        DataRecord dataRecord = this.user.processRecord(length, type, subType, this.buffer, offset);
        boolean finished = this.user.isFinished();
        if (dataRecord != null) {
            dataRecord.setRecordNumber(recordNumber);
            dataRecord.setSourceFile(stdfFile);

        }

        return finished;
    }

    private boolean readRecordData(int bytes) throws IOException {
        sizeBuffer(bytes);
        int correctionBytes = 0;
        if (this.crlfError && this.trailingByteRead) {
            this.buffer.put(this.trailingByteArray[0]);
            this.trailingByteRead = false;
            correctionBytes = 1;
        }
        int readBytes = inputChannel.read(this.buffer) + correctionBytes;
        if (readBytes < bytes) {
            log.warn("Unexpected end of STDF");
            return false;
        } else {
            this.bytesRead += (long)(readBytes - correctionBytes);
            if (this.crlfError) {
                this.crlfCorrect(this.buffer);
            }
            return true;
        }
    }

    private void readFar() throws IOException {
        this.sizeBuffer(2);
        inputChannel.read(this.buffer);
        FAR far = new FAR();
        this.buffer.position(0);
        far.readRecord(this.buffer);
        this.defineFieldReader(far.getCpuType());
    }

    private void sizeBuffer(int bytes) {
        if (bytes > this.buffer.capacity()) {
            byte[] rawBytes = new byte[bytes];
            this.buffer = ByteBuffer.wrap(rawBytes);
            this.buffer.order(this.littleEndian ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
        } else {
            this.buffer.limit(bytes);
            this.buffer.position(0);
        }

    }

    private void defineFieldReader(short cpuType) {
        if (cpuType == 1) {
            this.buffer.order(ByteOrder.BIG_ENDIAN);
            this.header.order(ByteOrder.BIG_ENDIAN);
            this.littleEndian = false;
        } else if (cpuType == 2) {
            this.littleEndian = true;
            this.buffer.order(ByteOrder.LITTLE_ENDIAN);
            this.header.order(ByteOrder.LITTLE_ENDIAN);
        } else {
            log.warn("Unknown cpu type [%s] in FAR, so defaulting to little endian");
//            this.parseErrors.addError("Unknown cpu type " + cpuType + " in FAR, defaulting to little endian");
            this.littleEndian = true;
        }

    }

    private boolean readHeader() throws IOException {
        try {
            this.header.position(0);
            int correctionBytes = 0;
            if (this.crlfError && this.trailingByteRead) {
                this.header.put(this.trailingByteArray[0]);
                this.trailingByteRead = false;
                correctionBytes = 1;
            }

            int bytes = this.inputChannel.read(this.header) + correctionBytes;
            if (bytes > 0 && bytes < 4) {
                //TODO 记录日志
                if (log.isWarnEnabled()) {
                    log.warn(STDF_UNEXPECTED_RECORD_HEADER);
                }
//            this.parseErrors.addError("Unexpected end of STDF reading record header");
                return false;
            } else {
                ++this.recordNumber;
                this.bytesRead += (long)(bytes - correctionBytes);
                if (this.crlfError) {
                    this.crlfCorrect(this.header);
                }

                return true;
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }

    }

    private void crlfCorrect(ByteBuffer bb) throws IOException {
        //TODO
    }
    @Override
    public void abortReading() {

    }
}
