package com.newbiest;

import org.hibernate.engine.query.spi.NamedParameterDescriptor;
import org.hibernate.engine.query.spi.OrdinalParameterDescriptor;
import org.hibernate.engine.query.spi.ParamLocationRecognizer;
import org.hibernate.engine.query.spi.ParameterMetadata;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by guoxunbo on 2018/2/16.
 */
public class Test {

    public static void main(String[] args) {
        String string = "INSERT INTO WIP_LOT (OBJECT_RRN, ORG_RRN, IS_ACTIVE, CREATED, CREATED_BY, UPDATED, UPDATED_BY, LOCK_VERSION, LOT_ID, WO_ID, PART_RRN, PART_NAME, PART_VERSION, PART_DESC, COM_CLASS, STATE, HOLD_STATE, GRADE1, GRADE2, JUDGE1, JUDGE2, SUBSTRATE_ID1, SUBSTRATE_ID2, MAIN_QTY, LOT_COMMENT, PRIORITY, DURABLE, POSITION, OWNER, PARENT_UNIT_RRN, SUB_UNIT_TYPE, IS_SUB_LOT ,IS_PILOT ,MANUFACTURE_TYPE ,LOT_TYPE ,IS_DISPATCH ) SELECT  HIBERNATE_SEQUENCE.NEXTVAL , :ORG_RRN , 'Y' , :CREATED , :CREATED_BY , :UPDATED , :UPDATED_BY , 1 , T1.LOT_ID , :WO_ID , T1.PART_RRN , T1.PART_NAME , T1.PART_VERSION , T1.PART_DESC , T1.COM_CLASS , T1.STATE , T1.HOLD_STATE , T1.GRADE1 , T1.GRADE2 , T1.JUDGE1 , T1.JUDGE2 , T1.SUBSTRATE_ID1 , T1.SUBSTRATE_ID2 , T1.MAIN_QTY , :ORG_NAME , :PRIORITY , :DURABLE , T1.POSITION, :OWNER , :CELL_PARENT_UNIT_RRN , 'ComponentUnit', 'Y' , T1.IS_PILOT , T1.MANUFACTURE_TYPE , T1.LOT_TYPE , T1.IS_DISPATCH  FROM  WIP_LOT@DL_LCDMES T1  WHERE  T1.ORG_RRN = :LCD_ORG_RRN  AND T1.PARENT_UNIT_RRN = :LCD_PARENT_UNIT_RRN";
        ParameterMetadata parameterMetadata = getParameterMetadata(string);
        System.out.println(parameterMetadata);

    }

    public static ParameterMetadata getParameterMetadata(String nativeQuery) {
        final ParamLocationRecognizer recognizer = ParamLocationRecognizer.parseLocations( nativeQuery );

        final int size = recognizer.getOrdinalParameterLocationList().size();
        final OrdinalParameterDescriptor[] ordinalDescriptors = new OrdinalParameterDescriptor[ size ];
        for ( int i = 0; i < size; i++ ) {
            final Integer position = recognizer.getOrdinalParameterLocationList().get( i );
            ordinalDescriptors[i] = new OrdinalParameterDescriptor( i, null, position );
        }

        final Map<String, NamedParameterDescriptor> namedParamDescriptorMap = new HashMap<String, NamedParameterDescriptor>();
        final Map<String, ParamLocationRecognizer.NamedParameterDescription> map = recognizer.getNamedParameterDescriptionMap();

        for ( final String name : map.keySet() ) {
            final ParamLocationRecognizer.NamedParameterDescription description = map.get( name );
            namedParamDescriptorMap.put(
                    name,
                    new NamedParameterDescriptor(
                            name,
                            null,
                            description.buildPositionsArray(),
                            description.isJpaStyle()
                    )
            );
        }

        return new ParameterMetadata( ordinalDescriptors, namedParamDescriptorMap );
    }
}
