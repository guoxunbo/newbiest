package com.newbiest.liquibase;

import com.newbiest.base.ui.model.NBTable;
import com.newbiest.liquibase.config.VCimAbstractLiquibaseConfig;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.Database;
import liquibase.statement.SqlStatement;
import lombok.Data;

import javax.persistence.Table;

/**
 * Created by guoxunbo on 2020-02-26 15:32
 */
@DatabaseChange(name="createOnLineTable", description = "Inserts data to nb_table", priority = ChangeMetaData.PRIORITY_DEFAULT)
@Data
public class CreateTableChange extends VCimAbstractChange  {

    public static final String TABLE_NAME = NBTable.class.getAnnotation(Table.class).name();

    @Override
    public VCimAbstractLiquibaseConfig getObject() {
        return null;
    }

    @Override
    public Class getEntityClass() {
        return null;
    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public SqlStatement[] generateStatements(Database database) {
        return new SqlStatement[0];
    }

}
