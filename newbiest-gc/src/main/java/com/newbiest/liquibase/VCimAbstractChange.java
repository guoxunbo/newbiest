package com.newbiest.liquibase;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.EntityReflectUtils;
import com.newbiest.base.utils.PropertyUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.liquibase.config.VCimAbstractLiquibaseConfig;
import liquibase.change.AbstractChange;
import liquibase.change.ChangeFactory;
import liquibase.change.ChangeParameterMetaData;
import liquibase.change.ColumnConfig;
import liquibase.database.Database;
import liquibase.exception.ValidationErrors;
import liquibase.executor.ExecutorService;
import liquibase.statement.SequenceNextValueFunction;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.RawSqlStatement;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.reflections.ReflectionUtils;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by guoxunbo on 2020-03-03 12:56
 */
@Slf4j
@Data
public abstract class VCimAbstractChange extends AbstractChange {

    public static final String HIBERNATE_SEQUENCE = "HIBERNATE_SEQUENCE";

    public static final String DBMS_ORACLE = "oracle";

    /**
     * 当数据库数据存在的时候，是否跳过
     */
    protected String skipFlag = StringUtils.YES;

    @Override
    public boolean supports(Database database) {
        return true;
    }

    public abstract VCimAbstractLiquibaseConfig getObject();

    public abstract Class getEntityClass();

    public abstract String getTableName();

    @Override
    public ValidationErrors validate(Database database) {
        ValidationErrors changeValidationErrors = new ValidationErrors();
        try {
            // Record an error if a parameter is not set, but that parameter is required by database.
            for (ChangeParameterMetaData param : ChangeFactory.getInstance().getChangeMetaData(this).getParameters().values()) {
                if (param.isRequiredFor(database) && (param.getCurrentValue(this) == null)) {
                    changeValidationErrors.addError(param.getParameterName() + " is required for " +
                            ChangeFactory.getInstance().getChangeMetaData(this).getName() + " on " + database.getShortName());
                }
            }
            if (changeValidationErrors.hasErrors()) {
                return changeValidationErrors;
            }

            Long existObjectRrn = getObjectRrnByName(database, getObject(), getTableName());
            if (existObjectRrn != null) {
                String message = String.format("Object [%s]' is already exist", getObject());
                if (StringUtils.YES.equals(skipFlag)) {
                    changeValidationErrors.addWarning(message);
                } else {
                    changeValidationErrors.addError(message);
                }
                return changeValidationErrors;
            };
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            changeValidationErrors.addError(e.getMessage());
        }
        changeValidationErrors = customerValidate(changeValidationErrors, database);
        return changeValidationErrors;
    }

    public ValidationErrors customerValidate(ValidationErrors changeValidationErrors, Database database) {
        return changeValidationErrors;
    }

    /**
     * 检查栏位是否存在 以及是否具有domainClass的@Column栏位
     * @param fieldName
     */
    private void checkFiled(String fieldName, Class clazz) throws ClientException {

        List<Field> fields = Lists.newArrayList(ReflectionUtils.getAllFields(clazz));
        Optional optional = fields.stream()
                .filter(field -> EntityReflectUtils.checkFieldPersist(field) && field.getName().equals(fieldName)).findFirst();
        if (!optional.isPresent()) {
            throw new ClientParameterException(NewbiestException.COMMON_ENTITY_FIELD_IS_NOT_PERSIST, clazz.getName(), fieldName);
        }
    }

    /**
     * 验证数据是否存在。默认只验证名称即name栏位
     * @return
     * @throws Exception
     */
    protected Long getObjectRrnByName(Database database, Object object, String tableName) throws Exception{
        checkFiled("name", object.getClass());
        StringBuffer sqlBuffer = new StringBuffer("SELECT OBJECT_RRN ");
        sqlBuffer.append(" FROM ");
        sqlBuffer.append(tableName);
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" NAME = ");
        sqlBuffer.append("'" + PropertyUtils.getProperty(object, "name") + "'");

        RawSqlStatement rawSqlStatement = new RawSqlStatement(sqlBuffer.toString());
        List<Map<String, ?>> maps = ExecutorService.getInstance().getExecutor(database).queryForList(rawSqlStatement);
        if (CollectionUtils.isNotEmpty(maps)) {
            BigDecimal bigDecimal = (BigDecimal) maps.get(0).get("OBJECT_RRN");
            return bigDecimal.longValue();
        }
        return null;
    }

    /**
     * 判断是否支持SEQUENCE 取值
     * @param database
     * @return
     */
    public boolean supportSequence(Database database) {
        return DBMS_ORACLE.equals(database.getShortName());
    }

    @Override
    public String getConfirmationMessage() {
        return null;
    }

    /**
     * 生成主键的赋值方式。暂时只支持Sequence
     * 后续可以根据SequenceGenerator方式进行生成
     * @param field
     * @return
     */
    protected ColumnConfig generatorIdColumnConfig(Field field) {
        String sequenceName = HIBERNATE_SEQUENCE;
        ColumnConfig columnConfig = new ColumnConfig();
        columnConfig.setName(field.getAnnotation(Column.class).name());
        GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
        String generator = generatedValue.generator();
        if (!StringUtils.isNullOrEmpty(generator)) {
            SequenceGenerator sequenceGenerator = field.getAnnotation(SequenceGenerator.class);
            if (sequenceGenerator != null && generator.equals(sequenceGenerator.name()) ) {
                sequenceName = StringUtils.isNullOrEmpty(sequenceGenerator.sequenceName()) ? HIBERNATE_SEQUENCE : sequenceGenerator.sequenceName();
            }
        }
        SequenceNextValueFunction sequenceNextValueFunction = new SequenceNextValueFunction(sequenceName);
        columnConfig.setValueSequenceNext(sequenceNextValueFunction);
        return columnConfig;
    }

    protected List<ColumnConfig> generatorColumnConfig(Object object) {
        if (object == null) {
            return Lists.newArrayList();
        }
        List<Field> persistFields = ReflectionUtils.getAllFields(getEntityClass()).stream().filter(field -> EntityReflectUtils.checkFieldPersist(field)).collect(Collectors.toList());

        List<String> objectFiledNames = ReflectionUtils.getAllFields(object.getClass()).stream().map(Field :: getName).collect(Collectors.toList());

        List<ColumnConfig> columnConfigList = Lists.newArrayList();
        if (persistFields != null) {
            for (Field persistField : persistFields) {
                ColumnConfig columnConfig = new ColumnConfig();
                if(persistField.getName().equals("fields") || persistField.getName().equals("tabs")){
                    continue;
                }
                columnConfig.setName(persistField.getAnnotation(Column.class).name());
                if (persistField.getAnnotation(Id.class) != null) {
                    columnConfig = generatorIdColumnConfig(persistField);
                } else {
                    if (objectFiledNames.contains(persistField.getName())) {
                        Object value = PropertyUtils.getProperty(object, persistField.getName());
                        if (value != null) {
                            columnConfig.setValue(value.toString());
                        }
                    }
                }
                columnConfigList.add(columnConfig);
            }
        }
        return columnConfigList;
    }

    public abstract SqlStatement[] generateStatements(Database database);
}
