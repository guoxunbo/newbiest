package com.newbiest.liquibase;

import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.liquibase.config.AuthorityConfig;
import com.newbiest.liquibase.config.TableConfig;
import com.newbiest.liquibase.config.VCimAbstractLiquibaseConfig;
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
@DatabaseChange(name="createOnLineTable", description = "Inserts data to nb_table", priority = ChangeMetaData.PRIORITY_DEFAULT)
@Data
@Slf4j
public class CreateTableChange extends VCimAbstractChange  {

    public static final String TABLE_NAME = NBTable.class.getAnnotation(Table.class).name();

    private TableConfig nbTable;

    private String dbms = DBMS_ORACLE;

    private String existTableName;

    @DatabaseChangeProperty(requiredForDatabase="all")
    public TableConfig getNbTable() {
        return nbTable;
    }

    @Override
    public VCimAbstractLiquibaseConfig getObject() {
        return nbTable;
    }

    @Override
    public Class getEntityClass() {
        return NBTable.class;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public SqlStatement[] generateStatements(Database database) {
        InsertStatement statement = new InsertStatement(StringUtils.EMPTY, StringUtils.EMPTY, TABLE_NAME);
        List<ColumnConfig> columnConfigList = generatorColumnConfig(nbTable);
        for (ColumnConfig columnConfig : columnConfigList) {
            statement.addColumn(columnConfig);
        }
        return new SqlStatement[]{statement};
    }

    @Override
    public ValidationErrors customerValidate(ValidationErrors changeValidationErrors, Database database) {
        try {
            if (StringUtils.isNullOrEmpty(nbTable.getName())) {
                changeValidationErrors.addWarning(String.format("Table [%s]'s name is null.", nbTable.getName()));
            } else {
                nbTable.setName(nbTable.getName());
                NBTable nbTable = new NBTable();
                nbTable.setName(nbTable.getName());
                Long existTableRrn = getObjectRrnByName(database, nbTable, CreateTableChange.TABLE_NAME);
                if (existTableRrn != null) {
                    changeValidationErrors.addError(String.format("Table [%s]'s  data is existed", nbTable.getName(), nbTable.getTableName()));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            changeValidationErrors.addError(e.getMessage());
        }
        return changeValidationErrors;
    }

    @Override
    protected void customLoadLogic(ParsedNode parsedNode, ResourceAccessor resourceAccessor) throws ParsedNodeException {
        super.customLoadLogic(parsedNode, resourceAccessor);
    }

}
