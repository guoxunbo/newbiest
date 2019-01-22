/*
 Navicat Premium Data Transfer

 Source Server         : butterfly
 Source Server Type    : MySQL
 Source Server Version : 50718
 Source Host           : localhost:3306
 Source Schema         : butterfly

 Target Server Type    : MySQL
 Target Server Version : 50718
 File Encoding         : 65001

 Date: 22/01/2019 11:11:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for NB_FIELD
-- ----------------------------
DROP TABLE IF EXISTS `NB_FIELD`;
CREATE TABLE `NB_FIELD` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `name` varchar(64) DEFAULT NULL COMMENT '名字',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `COLUMN_NAME` varchar(32) DEFAULT NULL COMMENT '列名',
  `TABLE_RRN` bigint(20) DEFAULT NULL COMMENT '动态表主键',
  `TAB_RRN` bigint(20) DEFAULT NULL COMMENT 'Tab主键',
  `SEQ_NO` bigint(20) DEFAULT NULL COMMENT '顺序',
  `DISPLAY_TYPE` varchar(32) DEFAULT NULL COMMENT '栏位显示类型 如text、password、refTable等',
  `NAMING_RULE` varchar(128) DEFAULT NULL COMMENT '命名规则 比如正则',
  `KEY_NUMBER` bigint(20) DEFAULT NULL COMMENT '栏位的关键字 将相同关键字的栏位设为同一个关键组 相同关健组中的栏位不同重复',
  `MIN_VALUE` varchar(32) DEFAULT NULL COMMENT '最小值',
  `MAX_VALUE` varchar(32) DEFAULT NULL COMMENT '最大值',
  `REF_LIST_NAME` varchar(32) DEFAULT NULL COMMENT '栏位所对应的系统参考值',
  `REFERENCE_RULE` varchar(32) DEFAULT NULL COMMENT '栏位的参考规则',
  `DEFAULT_VALUE` varchar(32) DEFAULT NULL COMMENT '默认值',
  `DISPLAY_FLAG` varchar(1) DEFAULT NULL COMMENT '栏位是否显示',
  `BASIC_FLAG` varchar(1) DEFAULT NULL COMMENT '栏位是否显示在基本信息中',
  `MAIN_FLAG` varchar(1) DEFAULT NULL COMMENT '栏位是否在表格中显示',
  `PERSIST_FLAG` varchar(1) DEFAULT NULL COMMENT '栏位是否保存数据库',
  `READONLY_FLAG` varchar(1) DEFAULT NULL COMMENT '栏位是否是只读',
  `REQUIRED_FLAG` varchar(1) DEFAULT NULL COMMENT '是否必输',
  `UPPER_FLAG` varchar(1) DEFAULT NULL COMMENT '是否转大写',
  `FROM_PARENT` varchar(1) DEFAULT NULL COMMENT '从父对象上取值 父对象必须为对象的field栏位中体现如user.name',
  `QUERY_FLAG` varchar(1) DEFAULT NULL COMMENT '是否是查询栏位',
  `EXPORT_FLAG` varchar(1) DEFAULT NULL COMMENT '是否导出',
  `ALL_LINE` varchar(1) DEFAULT NULL COMMENT '是否整行显示',
  `EDITABLE` varchar(1) DEFAULT NULL COMMENT '保存之后是否可编辑',
  `LABEL` varchar(64) DEFAULT NULL COMMENT '英文标签',
  `LABEL_ZH` varchar(64) DEFAULT NULL COMMENT '中文标签',
  `LABEL_RES` varchar(64) DEFAULT NULL COMMENT '其他语言标签',
  `REF_TABLE_NAME` varchar(64) DEFAULT NULL,
  `DISPLAY_LENGTH` bigint(20) DEFAULT '200' COMMENT '长度',
  `QUERY_REQUIRE_FLAG` varchar(1) DEFAULT 'N' COMMENT '查询必须',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_FILED_TABLE_RRN_NAME` (`TABLE_RRN`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=95 DEFAULT CHARSET=utf8 COMMENT='field Info';

-- ----------------------------
-- Records of NB_FIELD
-- ----------------------------
INSERT INTO `NB_FIELD` VALUES (1, 'Y', 0, 'name', '名称', 'NAME', 2, NULL, 10, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'Y', 'N', 'N', 'Y', 'N', 'N', 'N', 'Name', '名称', NULL, NULL, 150, 'N');
INSERT INTO `NB_FIELD` VALUES (2, 'Y', 0, 'description', '描述', 'DESCRIPTION', 2, NULL, 20, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'N', 'N', NULL, 'N', NULL, NULL, 'Y', 'Desc', '描述', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (3, 'Y', 0, 'modelClass', '对象全名', 'MODEL_CLASS', 2, 1, 40, 'text', NULL, NULL, NULL, NULL, NULL, NULL, '', 'Y', 'N', 'Y', 'Y', 'N', 'Y', 'N', 'N', 'N', 'N', 'N', 'Y', 'ModelClass', '对象全名', NULL, NULL, 350, 'N');
INSERT INTO `NB_FIELD` VALUES (4, 'Y', 0, 'tableName', '表名', 'TABLE_NAME', 2, 1, 30, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'N', 'Y', 'Y', 'N', 'Y', 'N', 'N', 'N', NULL, 'N', 'Y', 'TableName', '表名', NULL, NULL, 170, 'N');
INSERT INTO `NB_FIELD` VALUES (5, 'Y', 0, 'objectRrn', '主键', 'OBJECT_RRN', 2, 1, 5, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', '', 'N', 'Y', NULL, NULL, NULL, NULL, '', NULL, NULL, 'Y', 'ObjectRrn', '主键', NULL, NULL, 100, 'N');
INSERT INTO `NB_FIELD` VALUES (6, 'Y', 0, 'whereClause', '查询条件', 'WHERE_CLAUSE', 2, 1, 50, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'N', 'N', 'Y', 'N', 'N', 'N', 'N', 'N', NULL, 'N', 'Y', 'WhereClause', '查询条件', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (7, 'Y', 0, 'initWhereClause', '初始化查询条件', 'INIT_WHERE_CLAUSE', 2, 1, 60, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'N', 'N', 'Y', 'N', 'N', 'N', 'N', 'N', NULL, 'N', 'Y', 'InitWhereClause', '初始化查询条件', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (8, 'Y', 0, 'orderBy', '排序', 'ORDER_BY', 2, 1, 70, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'N', 'N', 'Y', 'N', 'N', 'N', 'N', 'N', NULL, 'N', 'Y', 'OrderBy', '排序条件', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (9, 'Y', 0, 'modelName', '类名', 'MODEL_NAME', 2, 1, 35, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'N', 'Y', 'Y', 'N', 'Y', 'N', 'N', 'N', 'N', 'N', 'Y', 'ModelName', '类名', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (10, 'Y', 0, 'name', '名称', 'NAME', 66, NULL, 10, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', NULL, 'Y', NULL, NULL, 'Y', NULL, NULL, 'N', 'Name', '名称', NULL, NULL, 150, 'N');
INSERT INTO `NB_FIELD` VALUES (11, 'Y', 0, 'description', '描述', 'DESCRIPTION', 66, NULL, 20, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', NULL, 'Y', NULL, NULL, '', NULL, NULL, 'Y', 'Desc', '描述', NULL, NULL, 150, 'N');
INSERT INTO `NB_FIELD` VALUES (12, 'Y', 0, 'seqNo', '顺序号', 'SEQ_NO', 66, NULL, 30, 'int', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'N', 'Y', NULL, '', NULL, NULL, '', NULL, NULL, 'Y', 'Seq', '顺序号', NULL, NULL, 100, 'N');
INSERT INTO `NB_FIELD` VALUES (14, 'Y', 0, 'tableRrn', '动态表', 'TABLE_RRN', 66, NULL, 40, 'referenceTable', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'N', 'Y', 'N', 'Y', 'N', NULL, 'Y', NULL, NULL, 'Y', 'TableName', '动态表号', NULL, 'NBTable', NULL, 'N');
INSERT INTO `NB_FIELD` VALUES (15, 'Y', 0, 'tabRrn', '动态Tab', 'TAB_RRN', 66, NULL, 50, 'referenceTable', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'N', 'Y', NULL, '', NULL, NULL, '', NULL, NULL, 'Y', 'TabName', '动态Tab', NULL, 'NBTabByTable', 100, 'N');
INSERT INTO `NB_FIELD` VALUES (16, 'Y', 0, 'defaultValue', '默认值', 'DEFAULT_VALUE', 66, 6, 190, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'N', 'Y', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'Default', '默认值', NULL, NULL, 100, 'N');
INSERT INTO `NB_FIELD` VALUES (17, 'Y', 0, 'label', '英文', 'LABEL', 66, 6, 210, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', '', 'Y', 'Y', NULL, 'Y', NULL, NULL, '', NULL, NULL, 'Y', 'English', '英文', NULL, NULL, 150, 'N');
INSERT INTO `NB_FIELD` VALUES (18, 'Y', 0, 'labelZh', '中文', 'LABEL_ZH', 66, 6, 200, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', '', 'Y', 'Y', NULL, 'Y', NULL, NULL, '', NULL, NULL, 'Y', 'Chinese', '中文', NULL, NULL, 150, 'N');
INSERT INTO `NB_FIELD` VALUES (19, 'Y', 0, 'displayType', '显示类型', 'DISPLAY_TYPE', 66, 6, 52, 'sysRefList', NULL, NULL, NULL, NULL, 'FieldDisplayType', NULL, NULL, 'Y', 'N', 'Y', 'Y', 'N', 'Y', 'N', 'N', 'N', 'N', 'N', 'Y', 'DisplayType', '显示类型', NULL, NULL, 120, 'N');
INSERT INTO `NB_FIELD` VALUES (21, 'Y', 0, 'columnName', '列名', 'COLUMN_NAME', 66, NULL, 15, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', NULL, 'Y', NULL, NULL, NULL, NULL, NULL, 'Y', 'ColumnName', '列名', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (22, 'Y', 0, 'displayFlag', '是否显示', 'DISPLAY_FLAG', 66, 4, 10, 'radio', NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', '', '', 'Y', NULL, '', NULL, NULL, NULL, NULL, NULL, 'Y', 'Display', '显示', NULL, NULL, 100, 'N');
INSERT INTO `NB_FIELD` VALUES (23, 'Y', 0, 'basicFlag', '基础信息', 'BASIC_FLAG', 66, 4, 20, 'radio', NULL, NULL, NULL, NULL, NULL, NULL, 'N', 'Y', NULL, '', 'Y', NULL, '', NULL, NULL, NULL, NULL, NULL, 'Y', 'Basic', '基础信息', NULL, NULL, 100, 'N');
INSERT INTO `NB_FIELD` VALUES (24, 'Y', 0, 'mainFlag', '列表显示', 'MAIN_FLAG', 66, 4, 40, 'radio', NULL, NULL, NULL, NULL, NULL, NULL, 'N', 'Y', NULL, '', 'Y', NULL, '', NULL, NULL, NULL, NULL, NULL, 'Y', 'Main', '列表显示', NULL, NULL, 100, 'N');
INSERT INTO `NB_FIELD` VALUES (25, 'Y', 0, 'persistFlag', '保存', 'PERSIST_FLAG', 66, 4, 40, 'radio', NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', NULL, '', 'Y', NULL, '', NULL, NULL, NULL, NULL, NULL, 'Y', 'Persist', '保存', NULL, NULL, 100, 'N');
INSERT INTO `NB_FIELD` VALUES (26, 'Y', 0, 'readonlyFlag', '只读', 'READONLY_FLAG', 66, 4, 50, 'radio', NULL, NULL, NULL, NULL, NULL, NULL, 'N', 'Y', NULL, '', 'Y', NULL, '', NULL, NULL, NULL, NULL, NULL, 'Y', 'Readonly', '只读', NULL, NULL, 100, 'N');
INSERT INTO `NB_FIELD` VALUES (27, 'Y', 0, 'editable', '可编辑', 'EDITABLE', 66, 4, 60, 'radio', NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', NULL, NULL, 'Y', NULL, '', NULL, NULL, NULL, NULL, NULL, 'Y', 'Editable', '可编辑', NULL, NULL, 100, 'N');
INSERT INTO `NB_FIELD` VALUES (28, 'Y', 0, 'upperFlag', '自动转换大写', 'UPPER_FLAG', 66, 4, 70, 'radio', NULL, NULL, NULL, NULL, NULL, NULL, 'N', 'Y', NULL, NULL, 'Y', NULL, '', NULL, NULL, NULL, NULL, NULL, 'Y', 'Upper', '大写', NULL, NULL, 100, 'N');
INSERT INTO `NB_FIELD` VALUES (29, 'Y', 0, 'requiredFlag', '必须', 'REQUIRE_FLAG', 66, 4, 80, 'radio', NULL, NULL, NULL, NULL, NULL, NULL, 'N', 'Y', NULL, NULL, 'Y', NULL, '', NULL, NULL, NULL, NULL, NULL, 'Y', 'Require', '必须', NULL, NULL, 100, 'N');
INSERT INTO `NB_FIELD` VALUES (30, 'Y', 0, 'refTableName', '参考表', 'REF_TABLE_NAME', 66, 6, 60, 'referenceTable', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', NULL, '', 'Y', NULL, '', NULL, NULL, NULL, NULL, NULL, 'Y', 'RefTableName', '参考表', NULL, 'NBRefTable', 100, 'N');
INSERT INTO `NB_FIELD` VALUES (31, 'Y', 0, 'refListName', '参考值', 'REF_LIST_NAME', 66, 6, 70, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', NULL, '', 'Y', NULL, '', NULL, NULL, NULL, NULL, NULL, 'Y', 'RefListName', '参考值', NULL, NULL, 100, 'N');
INSERT INTO `NB_FIELD` VALUES (33, 'Y', 0, 'queryFlag', '查询', 'QUERY_FLAG', 66, 5, 10, 'radio', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', NULL, NULL, 'Y', NULL, '', NULL, NULL, NULL, NULL, NULL, 'Y', 'Query', '查询', NULL, NULL, 100, 'N');
INSERT INTO `NB_FIELD` VALUES (34, 'Y', 0, 'queryRequireFlag', '查询', 'QUERY_REQUIRE_FLAG', 66, 5, 20, 'radio', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', NULL, NULL, 'Y', NULL, '', NULL, NULL, NULL, NULL, NULL, 'Y', 'QueryReq', '查询必须', NULL, NULL, 100, 'N');
INSERT INTO `NB_FIELD` VALUES (35, 'Y', 0, 'name', '名称', 'NAME', 65, NULL, 10, 'text', NULL, NULL, NULL, NULL, '', NULL, '', 'Y', 'Y', 'Y', 'Y', 'N', 'Y', 'N', NULL, 'Y', NULL, NULL, 'N', 'name', '名称', NULL, '', 200, 'N');
INSERT INTO `NB_FIELD` VALUES (36, 'Y', 0, 'description', '描述', 'DESCRIPTION', 65, NULL, 20, 'text', NULL, NULL, NULL, NULL, '', NULL, '', 'Y', 'Y', 'Y', 'Y', 'N', NULL, 'N', NULL, 'N', NULL, NULL, 'Y', 'Desc', '描述', NULL, '', 200, 'N');
INSERT INTO `NB_FIELD` VALUES (37, 'Y', 0, 'tableRrn', '动态表号', 'TABLE_RRN', 65, NULL, 30, 'referenceTable', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'N', 'Y', 'N', 'Y', 'N', NULL, 'Y', NULL, NULL, 'Y', 'Table', '动态表号', NULL, 'NBTable', NULL, 'N');
INSERT INTO `NB_FIELD` VALUES (38, 'Y', 0, 'seqNo', '序号', 'SEQ_NO', 65, NULL, 40, 'int', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', NULL, 'Y', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'seqNo', '序号', NULL, NULL, 100, NULL);
INSERT INTO `NB_FIELD` VALUES (39, 'Y', 0, 'tabType', 'Tab类型', '类型', 65, 8, 50, 'sysRefList', NULL, NULL, NULL, NULL, 'TabType', NULL, 'Field', 'Y', 'N', 'Y', 'Y', 'N', NULL, 'N', NULL, 'N', NULL, NULL, 'Y', 'TabType', 'Tab类型', NULL, NULL, 100, 'N');
INSERT INTO `NB_FIELD` VALUES (40, 'Y', 0, 'label', '英文', 'LABEL', 65, 8, 70, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'N', 'Y', 'Y', 'N', 'Y', 'N', NULL, 'N', NULL, NULL, 'Y', 'Label', '英文', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (41, 'Y', 0, 'labelZh', '中文', 'LABEL_ZH', 65, 8, 90, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'N', 'Y', 'Y', 'N', 'Y', 'N', NULL, 'N', NULL, NULL, 'Y', 'LabelZh', '中文', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (42, 'Y', 0, 'name', '名称', 'NAME', 67, NULL, 10, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'Y', 'N', NULL, 'Y', NULL, NULL, 'N', 'name', '名称', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (43, 'Y', 0, 'description', '描述', 'DESCRIPTION', 67, NULL, 20, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'N', 'N', NULL, 'N', NULL, NULL, 'Y', 'Desc', '描述', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (44, 'Y', 0, 'refTableName', '参考表', 'REF_TABLE_NAME', 65, 8, 50, 'referenceTable', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'N', 'Y', 'Y', 'N', 'N', 'N', 'N', 'N', NULL, NULL, 'Y', 'RefTable', '动态表', NULL, 'NBTableByName', 150, 'N');
INSERT INTO `NB_FIELD` VALUES (45, 'Y', 0, 'whereClause', '查询条件', 'WHERE_CLAUSE', 65, 8, 55, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'N', 'Y', 'Y', 'N', 'N', 'N', NULL, 'N', NULL, NULL, 'Y', 'WhereClause', '查询条件', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (46, 'Y', 0, 'objectRrn', '主键', 'OBJECT_RRN', 65, NULL, 1, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'N', 'N', 'N', 'Y', 'N', 'N', 'N', NULL, 'N', NULL, NULL, 'N', 'ObjectRrn', '主键', NULL, NULL, NULL, 'N');
INSERT INTO `NB_FIELD` VALUES (47, 'Y', 0, 'name', '名称', 'NAME', 68, NULL, 10, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'Y', 'N', NULL, 'Y', NULL, NULL, 'Y', 'Name', '名称', NULL, NULL, 150, 'N');
INSERT INTO `NB_FIELD` VALUES (48, 'Y', 0, 'description', '描述', 'DESCRIPTION', 68, NULL, 20, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'N', 'N', NULL, 'N', NULL, NULL, 'Y', 'Desc', '描述', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (49, 'Y', 0, 'tableRrn', '参考表', 'TABLE_RRN', 68, NULL, 30, 'referenceTable', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'N', 'Y', 'N', 'Y', 'N', NULL, 'N', NULL, NULL, 'Y', 'RefTable', '参考表', NULL, 'NBTable', NULL, 'N');
INSERT INTO `NB_FIELD` VALUES (50, 'Y', 0, 'keyField', '关键字栏位', 'KEY_FIELD', 68, NULL, 40, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'Y', 'N', NULL, 'N', NULL, NULL, 'Y', 'Key', '关键字栏位', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (51, 'Y', 0, 'textField', '显示栏位', 'TEXT_FIELD', 68, NULL, 50, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'Y', 'N', NULL, 'N', NULL, NULL, 'Y', 'Text', '显示栏位', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (52, 'Y', 0, 'whereClause', '条件语句', 'WHERE_CLAUSE', 68, NULL, 70, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'N', 'N', NULL, 'N', NULL, NULL, 'Y', 'WhereClause', '条件语句', NULL, NULL, 300, 'N');
INSERT INTO `NB_FIELD` VALUES (53, 'Y', 0, 'orderBy', '排序', 'ORDER_BY', 68, NULL, 80, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'N', 'N', NULL, 'N', NULL, NULL, 'Y', 'OrderBy', '排序', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (54, 'Y', 0, 'key', '关键字', 'KEY', 69, NULL, 10, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'Y', 'N', NULL, 'N', NULL, NULL, 'Y', 'Key', '关键字', NULL, NULL, 150, 'N');
INSERT INTO `NB_FIELD` VALUES (55, 'Y', 0, 'description', '描述', 'DESCRIPTION', 69, NULL, 20, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'N', 'N', NULL, 'N', NULL, NULL, 'Y', 'Desc', '描述', NULL, NULL, 300, 'N');
INSERT INTO `NB_FIELD` VALUES (56, 'Y', 0, 'value', '显示信息', 'VALUE', 69, NULL, 30, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'Y', 'N', NULL, 'N', NULL, NULL, 'Y', 'value', '显示信息', NULL, NULL, 300, 'N');
INSERT INTO `NB_FIELD` VALUES (57, 'Y', 0, 'seqNo', '序号', 'SEQ_NO', 69, NULL, 5, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'N', 'N', NULL, 'N', NULL, NULL, 'Y', 'SeqNo', '序号', NULL, NULL, 100, 'N');
INSERT INTO `NB_FIELD` VALUES (58, 'Y', 0, 'editFlag', '编辑', 'EDIT_FLAG', 65, 8, 65, 'radio', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'N', 'N', 'Y', 'N', 'N', 'N', NULL, 'N', NULL, NULL, 'Y', 'Edit', '编辑', NULL, NULL, NULL, 'N');
INSERT INTO `NB_FIELD` VALUES (59, 'Y', 0, 'referenceName', '参考名称', 'REFERENCE_NAME', 69, NULL, 20, 'text', NULL, NULL, NULL, NULL, NULL, 'name', NULL, 'N', 'N', 'N', 'Y', 'N', 'N', 'N', 'Y', 'N', NULL, NULL, 'N', 'RefName', '参考名称', NULL, NULL, NULL, 'N');
INSERT INTO `NB_FIELD` VALUES (60, 'Y', 0, 'fromParent', '来源于父值', 'FROM_PARENT', 66, 4, 90, 'radio', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'N', 'N', 'Y', 'N', 'N', 'N', NULL, 'N', NULL, NULL, 'Y', 'FromParent', '来源于父值', NULL, NULL, NULL, 'N');
INSERT INTO `NB_FIELD` VALUES (61, 'Y', 0, 'referenceRule', '参考规则', 'REFERENCE_RULE', 66, 4, 100, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'N', 'N', 'Y', 'N', 'N', 'N', 'N', 'N', NULL, NULL, 'Y', 'RefRule', '参考规则', NULL, NULL, NULL, 'N');
INSERT INTO `NB_FIELD` VALUES (62, 'Y', 0, 'name', '名称', 'NAME', 70, NULL, 10, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'Y', 'N', 'N', 'Y', NULL, NULL, 'Y', 'name', '名称', NULL, NULL, NULL, 'N');
INSERT INTO `NB_FIELD` VALUES (63, 'Y', 0, 'description', '描述', 'DESCRIPTION', 70, NULL, 20, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'N', 'N', 'N', 'N', NULL, NULL, 'Y', 'Desc', '描述', NULL, NULL, NULL, 'N');
INSERT INTO `NB_FIELD` VALUES (64, 'Y', 0, 'seqNo', '序号', 'SEQ_NO', 71, NULL, 5, 'int', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'Y', 'N', 'N', 'N', NULL, NULL, 'Y', 'SeqNo', '序号', NULL, NULL, 100, 'N');
INSERT INTO `NB_FIELD` VALUES (65, 'Y', 0, 'key', '关键字', 'KEY', 71, NULL, 10, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'Y', 'N', 'N', 'N', NULL, NULL, 'Y', 'Key', '关键字', NULL, NULL, 150, 'N');
INSERT INTO `NB_FIELD` VALUES (66, 'Y', 0, 'description', '描述', 'DESCRIPTION', 71, NULL, 20, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'Y', 'N', 'N', 'N', NULL, NULL, 'Y', 'Desc', '描述', NULL, NULL, 300, 'N');
INSERT INTO `NB_FIELD` VALUES (67, 'Y', 0, 'value', '显示值', 'VALUE', 71, NULL, 40, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'Y', 'N', 'N', 'N', NULL, NULL, 'Y', 'Value', '显示值', NULL, NULL, 300, 'N');
INSERT INTO `NB_FIELD` VALUES (68, 'Y', 0, 'referenceName', '参考值', 'REFERENCE_NAME', 71, NULL, 50, 'text', NULL, NULL, NULL, NULL, NULL, 'name', NULL, 'N', 'N', 'N', 'Y', 'N', 'N', 'N', 'Y', 'N', NULL, NULL, 'N', 'RefName', '参考值', NULL, NULL, NULL, 'N');
INSERT INTO `NB_FIELD` VALUES (69, 'Y', 0, 'username', '用户名', 'USERNAME', 72, NULL, 10, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'Y', 'N', 'N', 'Y', NULL, NULL, 'N', 'Username', '用户名', NULL, NULL, 100, 'N');
INSERT INTO `NB_FIELD` VALUES (70, 'Y', 0, 'description', '描述', 'DESCRIPTION', 72, NULL, 20, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'N', 'N', 'N', 'N', NULL, NULL, 'Y', 'Desc', '描述', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (71, 'Y', 0, 'password', '密码', 'PASSWORD', 72, 12, 30, 'password', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'N', 'N', 'Y', 'N', 'N', 'N', 'N', 'N', NULL, NULL, 'N', 'Password', '密码', NULL, NULL, 100, 'N');
INSERT INTO `NB_FIELD` VALUES (72, 'Y', 0, 'email', '邮箱', 'EMAIL', 72, 11, 40, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'N', 'Y', 'Y', 'N', 'N', 'N', 'N', 'N', NULL, NULL, 'Y', 'Email', '邮箱', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (73, 'Y', 0, 'phone', '电话', 'PHONE', 72, 11, 50, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'N', 'Y', 'Y', 'N', 'N', 'N', 'N', 'N', NULL, NULL, 'Y', 'Phone', '电话', NULL, NULL, 150, 'N');
INSERT INTO `NB_FIELD` VALUES (74, 'Y', 0, 'department', '部门', 'DEPARTMENT', 72, 11, 60, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'N', 'Y', 'Y', 'N', 'N', 'N', 'N', 'N', NULL, NULL, 'Y', 'Department', '部门', NULL, NULL, 100, 'N');
INSERT INTO `NB_FIELD` VALUES (75, 'Y', 0, 'pwdChanged', '密码修改日期', 'PWD_CHANGED', 72, 12, 70, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'N', 'Y', 'Y', 'Y', 'N', 'N', 'N', 'N', NULL, NULL, 'N', 'PwdChanged', '密码修改日期', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (76, 'Y', 0, 'pwdLife', '密码周期', 'PWD_LIFE', 72, 12, 70, 'int', NULL, NULL, NULL, NULL, NULL, NULL, '365', 'Y', 'N', 'N', 'Y', 'N', 'N', 'N', 'N', 'N', NULL, NULL, 'Y', 'PwdLife(Day)', '密码周期(天)', NULL, NULL, NULL, 'N');
INSERT INTO `NB_FIELD` VALUES (77, 'Y', 0, 'pwdExpiry', '密码到期时间', 'PWD_EXPRITY', 72, NULL, 80, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'N', 'N', 'Y', 'N', 'N', 'N', 'N', 'N', NULL, NULL, 'Y', 'PwdExpiry', '密码到期时间', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (78, 'Y', 0, 'pwdWrongCount', '密码错误次数', 'PWD_WRONG_COUNT', 72, 12, 90, 'int', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'N', 'N', 'Y', 'Y', 'N', 'N', 'N', 'N', NULL, NULL, 'N', 'PwdWrongCount', '密码错误次数', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (79, 'Y', 0, 'lastLogon', '最后登录时间', 'LAST_LOGON', 72, 12, 100, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'N', 'Y', 'Y', 'Y', 'N', 'N', 'N', 'N', NULL, NULL, 'N', 'LastLogon', '最后登录时间', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (80, 'Y', 0, 'keyId', '消息码', 'KEY_ID', 73, NULL, 10, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'N', 'N', 'N', 'Y', NULL, NULL, 'Y', 'Code', '消息码', NULL, NULL, 300, 'N');
INSERT INTO `NB_FIELD` VALUES (81, 'Y', 0, 'message', '英文', 'MESSAGE', 73, NULL, 20, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'N', 'N', 'N', 'N', NULL, NULL, 'Y', 'Message', '英文', NULL, NULL, 300, 'N');
INSERT INTO `NB_FIELD` VALUES (82, 'Y', 0, 'messageZh', '中文', 'MESSAGE_ZH', 73, NULL, 30, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'N', 'N', 'N', 'N', NULL, NULL, 'Y', 'Chinese', '中文', NULL, NULL, 300, 'N');
INSERT INTO `NB_FIELD` VALUES (83, 'Y', 0, 'roleId', '名称', 'ROLE_ID', 74, NULL, 10, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'Y', 'N', 'N', 'Y', NULL, NULL, 'Y', 'RoleId', '名称', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (84, 'Y', 0, 'description', '描述', 'DESCRIPTION', 74, NULL, 20, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'N', 'N', 'N', 'N', NULL, NULL, 'Y', 'Desc', '描述', NULL, NULL, NULL, 'N');
INSERT INTO `NB_FIELD` VALUES (85, 'Y', 0, 'equipmentId', '设备号', 'EQUIPMENT_ID', 75, NULL, 10, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'Y', 'N', 'N', 'Y', 'N', 'N', 'Y', 'EquipmentId', '设备号', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (86, 'Y', 0, 'equipmentType', '设备类型', 'EQUIPMENT_TYPE', 75, NULL, 20, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'N', 'N', 'N', 'N', NULL, NULL, 'Y', 'EquipmentType', '设备类型', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (87, 'Y', 0, 'holdFlag', 'Hold状态', 'HOLD_FLAG', 75, NULL, 30, 'radio', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'N', 'Y', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'HoldFlag', 'Hold状态', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (88, 'Y', 0, 'communicationFlag', '通信状态', 'COMMUNICATION_FLAG', 75, NULL, 40, 'radio', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'N', 'Y', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Communication', '通信状态', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (89, 'Y', 0, 'description', '描述', 'DESCRIPTION', 75, NULL, 15, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'N', 'N', 'N', 'N', NULL, NULL, 'Y', 'Desc', '描述', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (90, 'Y', 0, 'name', '名称', 'NAME', 76, NULL, 10, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'Y', 'N', 'N', 'Y', 'N', 'N', 'Y', 'Name', '名称', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (91, 'Y', 0, 'description', '描述', 'DESCRIPTION', 76, NULL, 20, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'N', 'N', 'N', 'N', NULL, NULL, 'Y', 'Desc', '描述', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (92, 'Y', 0, 'displayLength', '长度', 'DISPLAY_LENGTH', 66, 6, 175, 'int', NULL, NULL, NULL, NULL, NULL, NULL, '200', 'Y', 'N', 'N', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'DisplayLength', '显示长度', NULL, NULL, 200, 'N');
INSERT INTO `NB_FIELD` VALUES (93, 'Y', 0, 'label', '英文', 'LABEL', 2, NULL, 31, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'Y', 'N', 'N', 'N', NULL, 'N', 'Y', 'Label', '英文', NULL, NULL, 150, 'N');
INSERT INTO `NB_FIELD` VALUES (94, 'Y', 0, 'labelZh', '中文', 'LABEL_ZH', 2, NULL, 31, 'text', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 'Y', 'N', 'N', 'N', NULL, 'N', 'Y', 'LabelZh', '中文', NULL, NULL, 150, 'N');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
