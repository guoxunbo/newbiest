package com.newbiest.liquibase;

import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.liquibase.config.AuthorityConfig;
import com.newbiest.liquibase.config.VCimAbstractLiquibaseConfig;
import com.newbiest.security.model.NBAuthority;
import liquibase.change.ChangeMetaData;
import liquibase.change.ColumnConfig;
import liquibase.change.DatabaseChange;
import liquibase.change.DatabaseChangeProperty;
import liquibase.database.Database;
import liquibase.exception.ValidationErrors;
import liquibase.parser.core.ParsedNode;
import liquibase.parser.core.ParsedNodeException;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Table;
import java.util.List;

/**
 * Created by guoxunbo on 2020-02-26 15:32
 */
@DatabaseChange(name="createAuthority", description = "Inserts data to nb_authority", priority = ChangeMetaData.PRIORITY_DEFAULT)
@Data
@Slf4j
public class CreateAuthorityChange extends VCimAbstractChange  {

    public static final String TABLE_NAME = NBAuthority.class.getAnnotation(Table.class).name();

    private AuthorityConfig authority;

    private String dbms = DBMS_ORACLE;

    private Long existTableRrn;

    @Override
    public String getConfirmationMessage() {
        return null;
    }

    @DatabaseChangeProperty(requiredForDatabase="all")
    public AuthorityConfig getAuthority() {
        return authority;
    }

    @Override
    public VCimAbstractLiquibaseConfig getObject() {
        return authority;
    }

    @Override
    public Class getEntityClass() {
        return NBAuthority.class;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public ValidationErrors customerValidate(ValidationErrors changeValidationErrors, Database database) {
        try {
            if (StringUtils.isNullOrEmpty(authority.getTableName())) {
                changeValidationErrors.addWarning(String.format("Authority [%s]'s tableName is null. If it's a parent menu or button. ignore this warning", authority.getName()));
            } else {
                NBTable nbTable = new NBTable();
                nbTable.setName(authority.getTableName());
                Long existTableRrn = getObjectRrnByName(database, nbTable, CreateTableChange.TABLE_NAME);
                if (existTableRrn == null) {
                    changeValidationErrors.addError(String.format("Authority [%s]'s table [%s] data is not exist", authority.getName(), authority.getTableName()));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            changeValidationErrors.addError(e.getMessage());
        }
        return changeValidationErrors;
    }

    @Override
    public SqlStatement[] generateStatements(Database database) {
        InsertStatement statement = new InsertStatement(StringUtils.EMPTY, StringUtils.EMPTY, TABLE_NAME);
        List<ColumnConfig> columnConfigList = generatorColumnConfig(authority);
        for (ColumnConfig columnConfig : columnConfigList) {
            statement.addColumn(columnConfig);
        }
        return new SqlStatement[]{statement};
    }

    @Override
    protected void customLoadLogic(ParsedNode parsedNode, ResourceAccessor resourceAccessor) throws ParsedNodeException {
        super.customLoadLogic(parsedNode, resourceAccessor);
    }
}
