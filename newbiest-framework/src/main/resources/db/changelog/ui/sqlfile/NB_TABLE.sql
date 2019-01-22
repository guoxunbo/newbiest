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

 Date: 22/01/2019 11:13:23
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for NB_TABLE
-- ----------------------------
DROP TABLE IF EXISTS `NB_TABLE`;
CREATE TABLE `NB_TABLE` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `name` varchar(64) DEFAULT NULL COMMENT '名字',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `category` varchar(32) DEFAULT NULL COMMENT '模块',
  `table_name` varchar(32) DEFAULT NULL COMMENT '数据库表名',
  `style` bigint(20) DEFAULT '1' COMMENT '样式',
  `MODEL_NAME` varchar(32) DEFAULT NULL COMMENT '对应的Java Model的名称',
  `MODEL_CLASS` varchar(64) DEFAULT NULL COMMENT '对应的Java Model的全称,包括package',
  `WHERE_CLAUSE` varchar(128) DEFAULT NULL COMMENT '查询条件 每次查询都会带上',
  `ORDER_BY` varchar(128) DEFAULT NULL COMMENT '排序条件',
  `INIT_WHERE_CLAUSE` varchar(128) DEFAULT NULL COMMENT '初始的查询条件,只在刚生产动态页面时有效',
  `GRID_Y_BASIC` bigint(20) DEFAULT NULL COMMENT '定义动态页面中基本信息块的列数',
  `GRID_Y_QUERY` bigint(20) DEFAULT NULL COMMENT '定义查询页面中查询条件块的列数',
  `LABEL` varchar(64) DEFAULT NULL COMMENT '英文标签',
  `LABEL_ZH` varchar(64) DEFAULT NULL COMMENT '中文标签',
  `LABEL_RES` varchar(64) DEFAULT NULL COMMENT '其他语言标签',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_TABLE_ORG_RRN_NAME` (`org_rrn`,`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8 COMMENT='table Info';

-- ----------------------------
-- Records of NB_TABLE
-- ----------------------------
INSERT INTO `NB_TABLE` VALUES (1, 'Y', 0, 'NBOrg', '区域', NULL, 'NB_ORG', 2, 'NBOrg', 'com.newbiest.security.model.NBOrg', '', NULL, NULL, NULL, NULL, 'OrgManager', '区域管理', NULL);
INSERT INTO `NB_TABLE` VALUES (2, 'Y', 0, 'NBTable', '动态表管理', NULL, 'NB_TABLE', 2, 'NBTable', 'com.newbiest.base.ui.model.NBTable', '', NULL, NULL, NULL, NULL, 'TableManager', '表管理', NULL);
INSERT INTO `NB_TABLE` VALUES (65, 'Y', 0, 'NBTab', 'Tab信息', NULL, 'NB_TAB', 2, 'NBTab', 'com.newbiest.base.ui.model.NBTab', NULL, NULL, NULL, NULL, NULL, 'TabManager', 'Tab管理', NULL);
INSERT INTO `NB_TABLE` VALUES (66, 'Y', 0, 'NBField', '字段信息', NULL, 'NB_FIELD', 2, 'NBField', 'com.newbiest.base.ui.model.NBField', NULL, NULL, NULL, NULL, NULL, 'FieldManager', '栏位管理', NULL);
INSERT INTO `NB_TABLE` VALUES (67, 'Y', 0, 'NBSysRefName', '系统栏位参考名称', NULL, 'NB_REFERENCE_NAME', 2, 'NBSystemReferenceName', 'com.newbiest.base.ui.model.NBSystemReferenceName', NULL, NULL, NULL, NULL, NULL, 'SysRefNameManager', '系统栏位参考名称管理', NULL);
INSERT INTO `NB_TABLE` VALUES (68, 'Y', 0, 'NBRefTable', '动态栏位参考表', NULL, 'NB_REFERENCE_TABLE', 2, 'NBReferenceTable', 'com.newbiest.base.ui.model.NBReferenceTable', NULL, NULL, NULL, NULL, NULL, 'RefTableManager', '栏位参考表管理', NULL);
INSERT INTO `NB_TABLE` VALUES (69, 'Y', 0, 'NBSystemRefList', '系统栏位参考值', NULL, 'NB_REFERENCE_LIST', 2, 'NBSystemReferenceList', 'com.newbiest.base.ui.model.NBSystemReferenceList', NULL, NULL, NULL, NULL, NULL, 'SysRefList', '系统栏位参考值管理', NULL);
INSERT INTO `NB_TABLE` VALUES (70, 'Y', 0, 'NBOwenerRefName', '区域栏位参考名称', NULL, 'NB_REFERENCE_NAME', 2, 'NBOwnerReferenceName', 'com.newbiest.base.ui.model.NBOwnerReferenceName', NULL, NULL, NULL, NULL, NULL, 'OwnerRefNameManager', '区域栏位参考名称管理', NULL);
INSERT INTO `NB_TABLE` VALUES (71, 'Y', 0, 'NBOwnerRefList', '区域栏位参考值', NULL, 'NB_REFERENCE_LIST', 2, 'NBOwnerReferenceList', 'com.newbiest.base.ui.model.NBOwnerReferenceList', NULL, NULL, NULL, NULL, NULL, 'OwnerRefListManager', '区域栏位参考值管理', NULL);
INSERT INTO `NB_TABLE` VALUES (72, 'Y', 0, 'NBUser', '用户管理', NULL, 'NB_USER', 2, 'NBUser', 'com.newbiest.security.model.NBUser', NULL, NULL, NULL, NULL, NULL, 'UserManager', '用户管理', NULL);
INSERT INTO `NB_TABLE` VALUES (73, 'Y', 0, 'NBMessage', '异常码管理', NULL, 'NB_MESSAGE', 2, 'NBMessage', 'com.newbiest.base.model.NBMessage', NULL, NULL, NULL, NULL, NULL, 'ErrorCodeManager', '异常码管理', NULL);
INSERT INTO `NB_TABLE` VALUES (74, 'Y', 0, 'NBRole', '角色管理', NULL, 'NB_ROLE', 2, 'NBRole', 'com.newbiest.security.model.NBRole', NULL, NULL, NULL, NULL, NULL, 'RoleManager', '角色管理', NULL);
INSERT INTO `NB_TABLE` VALUES (75, 'Y', 0, 'RMSEquipment', '设备管理(RMS)', NULL, 'RMS_EQUIPMENT', 2, 'Equipment', 'com.newbiest.rms.model.Equipment', NULL, NULL, NULL, NULL, NULL, 'Equipment(RMS)Manager', '设备(RMS)管理', NULL);
INSERT INTO `NB_TABLE` VALUES (76, 'Y', 0, 'RMSRecipe', 'Recipe(PPID)管理', NULL, 'RMS_RECIPE', 2, 'Recipe', 'com.newbiest.rms.model.Recipe', '', NULL, NULL, NULL, NULL, 'RecipeManager', 'Recipe(PPID)管理1', NULL);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
