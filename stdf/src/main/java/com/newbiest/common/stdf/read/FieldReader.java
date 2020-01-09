package com.newbiest.common.stdf.read;

import java.io.Serializable;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

/**
 *
 * 0xff
 * 0xffff
 * 0xffffffffL
 * Created by guoxunbo on 2020-01-08 16:37
 */
public class FieldReader implements Serializable {

    static byte scratchLong[] = new byte[8];

    public static short readUnsignedByte(ByteBuffer byteBuffer) {
        return (short)(byteBuffer.get() & 0xff);
    }

    public static int readUnsignedShort(ByteBuffer byteBuffer) {
        return byteBuffer.getShort() & 0xffff;
    }

    public static long readUnsignedInt(ByteBuffer byteBuffer) {
        return (long)byteBuffer.getInt() & 0xffffffffL;
    }

    public static String readFixedLengthString(ByteBuffer bb, int length) {
        byte[] strBytes = new byte[length];
        bb.get(strBytes);
        String string = new String(strBytes);
        return string;
    }

    public static String readVariableLengthString(ByteBuffer tBuffer) {
        Buffer thisBuffer = tBuffer;
        if (tBuffer.position() >= tBuffer.limit()) {
            return null;
        } else {
            String string = null;
            short length = readUnsignedByte(tBuffer);
            if (length > 0) {
                if (length > tBuffer.remaining()) {
                    System.out.println("length > remaining");
                }
                try {
                    string = new String(tBuffer.array(), thisBuffer.position(), length);
                    thisBuffer.position(thisBuffer.position() + length);
                } catch (IllegalArgumentException var5) {
                    Logger.getLogger("spry.reader").warning("String index out of range");
                    throw var5;
                } catch (StringIndexOutOfBoundsException var6) {
                    Logger.getLogger("spry.reader").warning("String index out of range");
                    throw var6;
                }
            }

            return string;
        }
    }
}
