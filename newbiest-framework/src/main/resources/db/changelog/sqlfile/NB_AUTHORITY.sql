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

 Date: 22/01/2019 10:53:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for NB_AUTHORITY
-- ----------------------------
DROP TABLE IF EXISTS `NB_AUTHORITY`;
CREATE TABLE `NB_AUTHORITY` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `name` varchar(32) DEFAULT NULL COMMENT '名称',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `parent_rrn` bigint(20) DEFAULT NULL COMMENT '父级菜单主键',
  `authority_type` varchar(32) DEFAULT 'M' COMMENT '权限类型 M/Menu B/Button',
  `authority_category` varchar(32) DEFAULT 'Framework' COMMENT '权限类别 一般by系统划分',
  `image` varchar(32) DEFAULT NULL COMMENT '功能的图标',
  `label_en` varchar(64) DEFAULT NULL COMMENT '英文标题',
  `label_zh` varchar(64) DEFAULT NULL COMMENT '中文标题',
  `label_res` varchar(64) DEFAULT NULL COMMENT '其他语言',
  `seq_no` bigint(20) DEFAULT NULL COMMENT '序号',
  `table_rrn` bigint(20) DEFAULT NULL COMMENT '对应的NB_TABLE的主键',
  `url` varchar(256) DEFAULT NULL COMMENT 'url路径',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_AUTHORITY_ORG_RRN_NAME` (`org_rrn`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 COMMENT='Authority Info';

-- ----------------------------
-- Records of NB_AUTHORITY
-- ----------------------------
INSERT INTO `NB_AUTHORITY` VALUES (1, 'Y', 0, 'SystemManager', '系统管理', NULL, 'M', 'Framework', 'setting', 'System Manager', '系统管理', NULL, 10, NULL, '/System');
INSERT INTO `NB_AUTHORITY` VALUES (3, 'Y', 0, 'SecurityManager', '安全管理', NULL, 'M', 'Framework', 'lock', 'Security Manager', '安全管理', NULL, 20, NULL, '/Security');
INSERT INTO `NB_AUTHORITY` VALUES (4, 'Y', 0, 'DynamicTableManager', '动态表管理', 1, 'M', 'Framework', 'table', 'Online Table Manager', 'Online表单管理', NULL, 10, 2, '/System/OnlineTableManager');
INSERT INTO `NB_AUTHORITY` VALUES (5, 'Y', 0, 'DynamicFieldManager', '动态栏位管理', 1, 'M', 'Framework', 'qrcode', 'Online Field Manager', 'Online栏位管理', NULL, 20, 66, '/System/OnlineFieldManager');
INSERT INTO `NB_AUTHORITY` VALUES (6, 'Y', 0, 'DynamicTabManager', '动态Tab管理', 1, 'M', 'Framework', 'switcher', 'Online Tab Manager', 'Online Tab管理', NULL, 30, 65, '/System/OnlineTabManager');
INSERT INTO `NB_AUTHORITY` VALUES (7, 'Y', 0, 'DynamicReferenceTableManager', '动态栏位参考表管理', 1, 'M', 'Framework', 'table', 'Online Ref Table Manager', 'Online 栏位参考表管理', NULL, 40, 68, '/System/OnlineRefTableManager');
INSERT INTO `NB_AUTHORITY` VALUES (8, 'Y', 0, 'SysRefNameManager', '系统栏位参考名称管理', 1, 'M', 'Framework', 'global', 'Sys Ref Name Manager', '系统参考名称管理', NULL, 50, 67, '/System/SysRefNameManager');
INSERT INTO `NB_AUTHORITY` VALUES (10, 'Y', 0, 'OrgRefNameManager', '区域栏位参考名称管理', 1, 'M', 'Framework', 'gold', 'Org Ref Name Manager', '区域参考名称管理', NULL, 70, 70, '/System/OrgRefNameManager');
INSERT INTO `NB_AUTHORITY` VALUES (14, 'Y', 0, 'RMSManager', 'RMS管理', NULL, 'M', 'RMS', 'home', 'RMS Manager', 'RMS管理', NULL, 30, NULL, '/Rms');
INSERT INTO `NB_AUTHORITY` VALUES (15, 'Y', 0, 'EquipmentManager', '设备管理', 14, 'M', 'RMS', 'icon-shebei', 'Equipment Manager', '设备管理', NULL, 10, 75, '/Rms/EquipmentManager');
INSERT INTO `NB_AUTHORITY` VALUES (16, 'Y', 0, 'RecipeManager', 'Recipe管理', 14, 'M', 'RMS', 'icon-recipe', 'Recipe Manager', 'Recipe管理', NULL, 20, 76, '/Rms/RecipeManager');
INSERT INTO `NB_AUTHORITY` VALUES (17, 'Y', 0, 'EquipmentRecipeManager', '设备Recipe管理', 14, 'M', 'RMS', 'icon-shebeicanshu', 'Equipment Recipe Manager', '设备Recipe管理', NULL, 30, NULL, '/Rms/EquipmentRecipe');
INSERT INTO `NB_AUTHORITY` VALUES (19, 'Y', 0, 'UserManager', '用户管理', 3, 'M', 'Framework', 'user', 'User Manager', '用户管理', NULL, 10, 72, '/Security/UserManager');
INSERT INTO `NB_AUTHORITY` VALUES (20, 'Y', 0, 'RoleManager', '角色管理', 3, 'M', 'Framework', 'team', 'Role Manager', '角色管理', NULL, 20, 74, '/Security/RoleManager');
INSERT INTO `NB_AUTHORITY` VALUES (21, 'Y', 0, 'ErrorCodeManager', '异常码管理', 1, 'M', 'Framework', 'message', 'ErrorCodeManager', '异常码管理', NULL, 80, 73, '/System/MessageManager');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
