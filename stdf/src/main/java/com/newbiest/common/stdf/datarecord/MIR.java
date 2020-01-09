package com.newbiest.common.stdf.datarecord;

import com.google.common.collect.Sets;
import com.newbiest.common.stdf.input.DataFile;
import com.newbiest.common.stdf.input.DataRecord;
import com.newbiest.common.stdf.input.DataRecordType;
import com.newbiest.common.stdf.input.DataRecordTypeFactory;
import com.newbiest.common.stdf.read.FieldReader;
import lombok.Data;

import java.nio.ByteBuffer;
import java.util.LinkedHashSet;

/**
 * 参考 stdf_v4_types.xml
 * Created by guoxunbo on 2020-01-09 17:10
 */
@Data
public class MIR implements DataRecord {

    public static final String RECORD_TYPE = "MIR";
    public static final String RECORD_TYPE_DESCRIPTION = "MasterInformationRecord";

    public static final String ATTRIBUTE_LOT_ID = "LOT_ID";
    public static final String ATTRIBUTE_PART_TYPE = "PART_TYP";
    public static final String ATTRIBUTE_JOB_NAME = "JOB_NAM";
    public static final String ATTRIBUTE_NODE_NAME = "NODE_NAM";
    public static final String ATTRIBUTE_TESTER_TYPE = "TSTR_TYP";
    public static final String ATTRIBUTE_SETUP_TIME = "SETUP_T";
    public static final String ATTRIBUTE_START_TIME = "START_T";
    public static final String ATTRIBUTE_OPERATOR_NAME = "OPER_NAM";
    public static final String ATTRIBUTE_TEST_MODE = "MODE_COD";
    public static final String ATTRIBUTE_STATION_NUMBER = "STAT_NUM";
    public static final String ATTRIBUTE_SUB_LOT_ID = "SBLOT_ID";
    public static final String ATTRIBUTE_TEST_CODE = "TEST_COD";
    public static final String ATTRIBUTE_RETEST_CODE = "RTST_COD";
    public static final String ATTRIBUTE_JOB_REVISION = "JOB_REV";
    public static final String ATTRIBUTE_EXECUTIVE_TYPE = "EXEC_TYP";
    public static final String ATTRIBUTE_EXECUTIVE_VERSION = "EXEC_VER";
    public static final String ATTRIBUTE_PROTECT_CODE = "PROT_COD";
    public static final String ATTRIBUTE_COMMAND_CODE= "CMOD_COD";
    public static final String ATTRIBUTE_BURN_IN_TIME= "BURN_TIM";
    public static final String ATTRIBUTE_TEST_TEMPERATURE= "TST_TEMP";
    public static final String ATTRIBUTE_USER_TEXT= "USER_TXT";
    public static final String ATTRIBUTE_AUXILIARY_FILE= "AUX_FILE";
    public static final String ATTRIBUTE_PACKAGE_TYPE= "PKG_TYP";
    public static final String ATTRIBUTE_FAMILY_ID= "FAMLY_ID";
    public static final String ATTRIBUTE_DATE_CODE= "DATE_COD";
    public static final String ATTRIBUTE_FACILITY_ID= "FACIL_ID";
    public static final String ATTRIBUTE_FLOOR_ID = "FLOOR_ID";
    public static final String ATTRIBUTE_PROCESS_ID= "PROC_ID";
    public static final String ATTRIBUTE_OPER_FRQ = "OPER_FRQ";
    public static final String ATTRIBUTE_SPEC_NAME = "SPEC_NAM";
    public static final String ATTRIBUTE_SPEC_VERSION = "SPEC_VER";
    public static final String ATTRIBUTE_FLOW_ID = "FLOW_ID";
    public static final String ATTRIBUTE_SETUP_ID = "SETUP_ID";
    public static final String ATTRIBUTE_DESIGN_REVISION = "DSGN_REV";
    public static final String ATTRIBUTE_ENG_LOT_ID = "ENG_ID";
    public static final String ATTRIBUTE_ROM_CODE = "ROM_COD";
    public static final String ATTRIBUTE_SERIAL_NUMBER = "SERL_NUM";
    public static final String ATTRIBUTE_SUPER_NAME = "SUPR_NAM";

    public static final short HEAD_TYPE = 1;
    public static final short HEAD_SUB_TYPE = 10;

    private String lotId;
    private String partType;
    private String jobName;
    private String nodeName;
    private String testerType;
    private Long setupTime;
    private Long startTime;
    private String operatorName;
    private String testMode;
    private short stationNumber;
    private String subLotId;
    private String testCode;
    private String retestCode;
    private String jobRevision;
    private String executiveType;
    private String executiveVersion;
    private String protectCode;
    private String commandCode;
    private int burnInTime;
    private String testTemperature;
    private String userText;
    private String auxiliaryFile;
    private String packageType;

    private String familyId;
    private String dateCode;
    private String facilityId;
    private String floorId;
    private String processId;
    private String operationFReq;
    private String specName;
    private String specVersion;
    private String flowId;
    private String setupId;

    private String designRevision;
    private String engLotId;
    private String romCode;
    private String serialNumber;
    private String superName;

    private transient int recordNumber;
    private transient DataFile sourceFile;

    static {
        addType();
    }

    public static void addType() {
        LinkedHashSet<String> attributeSet = Sets.newLinkedHashSet();
        attributeSet.add(ATTRIBUTE_LOT_ID);
        attributeSet.add(ATTRIBUTE_PART_TYPE);
        attributeSet.add(ATTRIBUTE_JOB_NAME);
        attributeSet.add(ATTRIBUTE_NODE_NAME);
        attributeSet.add(ATTRIBUTE_TESTER_TYPE);
        attributeSet.add(ATTRIBUTE_SETUP_TIME);
        attributeSet.add(ATTRIBUTE_START_TIME);
        attributeSet.add(ATTRIBUTE_OPERATOR_NAME);
        attributeSet.add(ATTRIBUTE_TEST_MODE);
        attributeSet.add(ATTRIBUTE_STATION_NUMBER);
        attributeSet.add(ATTRIBUTE_SUB_LOT_ID);
        attributeSet.add(ATTRIBUTE_TEST_CODE);
        attributeSet.add(ATTRIBUTE_RETEST_CODE);
        attributeSet.add(ATTRIBUTE_JOB_REVISION);
        attributeSet.add(ATTRIBUTE_EXECUTIVE_TYPE);
        attributeSet.add(ATTRIBUTE_EXECUTIVE_VERSION);
        attributeSet.add(ATTRIBUTE_PROTECT_CODE);
        attributeSet.add(ATTRIBUTE_COMMAND_CODE);
        attributeSet.add(ATTRIBUTE_BURN_IN_TIME);
        attributeSet.add(ATTRIBUTE_TEST_TEMPERATURE);
        attributeSet.add(ATTRIBUTE_USER_TEXT);
        attributeSet.add(ATTRIBUTE_AUXILIARY_FILE);
        attributeSet.add(ATTRIBUTE_PACKAGE_TYPE);
        attributeSet.add(ATTRIBUTE_FAMILY_ID);
        attributeSet.add(ATTRIBUTE_DATE_CODE);
        attributeSet.add(ATTRIBUTE_FACILITY_ID);
        attributeSet.add(ATTRIBUTE_FLOOR_ID);
        attributeSet.add(ATTRIBUTE_PROCESS_ID);
        attributeSet.add(ATTRIBUTE_OPER_FRQ);
        attributeSet.add(ATTRIBUTE_SPEC_NAME);
        attributeSet.add(ATTRIBUTE_SPEC_VERSION);
        attributeSet.add(ATTRIBUTE_FLOW_ID);
        attributeSet.add(ATTRIBUTE_SETUP_ID);
        attributeSet.add(ATTRIBUTE_DESIGN_REVISION);
        attributeSet.add(ATTRIBUTE_ENG_LOT_ID);
        attributeSet.add(ATTRIBUTE_ROM_CODE);
        attributeSet.add(ATTRIBUTE_SERIAL_NUMBER);
        attributeSet.add(ATTRIBUTE_SUPER_NAME);
        DataRecordTypeFactory.putRecordType(RECORD_TYPE, new DataRecordType(RECORD_TYPE, RECORD_TYPE_DESCRIPTION, attributeSet));
    }

    @Override
    public int getRecordNumber() {
        return this.recordNumber;
    }

    @Override
    public void setRecordNumber(int recordNumber) {
        this.recordNumber = recordNumber;
    }

    @Override
    public String getRecordType() {
        return RECORD_TYPE;
    }

    @Override
    public String getDescription() {
        return toString();
    }

    @Override
    public DataFile getSourceFile() {
        return sourceFile;
    }

    @Override
    public void setSourceFile(DataFile paramDataFile) {
        this.sourceFile = sourceFile;
    }

    public String toString() {
        return String.format("MIR => lotId is [%s] and subLotId is [%s]", this.lotId, this.subLotId);
    }

//    @Override
//    public void writeRecord(SaveInfo paramSaveInfo, ByteBuffer paramByteBuffer) {
//
//    }

    @Override
    public void readRecord(ByteBuffer buffer) {
        this.setupTime = FieldReader.readUnsignedInt(buffer);
        this.startTime = FieldReader.readUnsignedInt(buffer);
        this.stationNumber = FieldReader.readUnsignedByte(buffer);
        this.testMode = FieldReader.readFixedLengthString(buffer, 1);
        this.retestCode = FieldReader.readFixedLengthString(buffer, 1);
        this.protectCode = FieldReader.readFixedLengthString(buffer, 1);
        this.burnInTime = FieldReader.readUnsignedShort(buffer);
        this.commandCode = FieldReader.readFixedLengthString(buffer, 1);
        this.lotId = FieldReader.readVariableLengthString(buffer);
        this.partType = FieldReader.readVariableLengthString(buffer);
        this.nodeName = FieldReader.readVariableLengthString(buffer);
        this.testerType = FieldReader.readVariableLengthString(buffer);
        this.jobName = FieldReader.readVariableLengthString(buffer);
        this.jobRevision = FieldReader.readVariableLengthString(buffer);
        this.subLotId = FieldReader.readVariableLengthString(buffer);
        this.operatorName = FieldReader.readVariableLengthString(buffer);
        this.executiveType = FieldReader.readVariableLengthString(buffer);
        this.executiveVersion = FieldReader.readVariableLengthString(buffer);
        this.testCode = FieldReader.readVariableLengthString(buffer);
        this.testTemperature = FieldReader.readVariableLengthString(buffer);
        this.userText = FieldReader.readVariableLengthString(buffer);
        this.auxiliaryFile = FieldReader.readVariableLengthString(buffer);
        this.packageType = FieldReader.readVariableLengthString(buffer);
        this.familyId = FieldReader.readVariableLengthString(buffer);
        this.dateCode = FieldReader.readVariableLengthString(buffer);
        this.facilityId = FieldReader.readVariableLengthString(buffer);
        this.floorId = FieldReader.readVariableLengthString(buffer);
        this.processId = FieldReader.readVariableLengthString(buffer);
        this.operationFReq = FieldReader.readVariableLengthString(buffer);
        this.specName = FieldReader.readVariableLengthString(buffer);
        this.specVersion = FieldReader.readVariableLengthString(buffer);
        this.flowId = FieldReader.readVariableLengthString(buffer);
        this.setupId = FieldReader.readVariableLengthString(buffer);
        this.designRevision = FieldReader.readVariableLengthString(buffer);
        this.engLotId = FieldReader.readVariableLengthString(buffer);
        this.romCode = FieldReader.readVariableLengthString(buffer);
        this.serialNumber = FieldReader.readVariableLengthString(buffer);
        this.superName = FieldReader.readVariableLengthString(buffer);
    }
}
