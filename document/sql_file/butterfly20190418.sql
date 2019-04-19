-- MySQL dump 10.13  Distrib 5.7.18, for macos10.12 (x86_64)
--
-- Host: localhost    Database: butterfly
-- ------------------------------------------------------
-- Server version	5.7.18

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `COM_CONTEXT`
--

DROP TABLE IF EXISTS `COM_CONTEXT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `COM_CONTEXT` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `name` varchar(32) DEFAULT NULL COMMENT '名称',
  `description` varchar(32) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_CONTEXT_ORG_RRN_NAME` (`org_rrn`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='Context Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `COM_CONTEXT`
--

LOCK TABLES `COM_CONTEXT` WRITE;
/*!40000 ALTER TABLE `COM_CONTEXT` DISABLE KEYS */;
INSERT INTO `COM_CONTEXT` VALUES (1,'Y',0,'Recipe','Recipe'),(2,'Y',0,'RecipeEquipment','RecipeEquipment');
/*!40000 ALTER TABLE `COM_CONTEXT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `COM_CONTEXT_VALUE`
--

DROP TABLE IF EXISTS `COM_CONTEXT_VALUE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `COM_CONTEXT_VALUE` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `context_rrn` varchar(32) DEFAULT NULL COMMENT 'Context主键',
  `status` varchar(32) DEFAULT NULL COMMENT '状态',
  `field_value1` varchar(32) DEFAULT NULL COMMENT '查询栏位1',
  `field_value2` varchar(32) DEFAULT NULL COMMENT '查询栏位2',
  `field_value3` varchar(32) DEFAULT NULL COMMENT '查询栏位3',
  `field_value4` varchar(32) DEFAULT NULL COMMENT '查询栏位4',
  `field_value5` varchar(32) DEFAULT NULL COMMENT '查询栏位5',
  `field_value6` varchar(32) DEFAULT NULL COMMENT '查询栏位6',
  `field_value7` varchar(32) DEFAULT NULL COMMENT '查询栏位7',
  `field_value8` varchar(32) DEFAULT NULL COMMENT '查询栏位8',
  `field_value9` varchar(32) DEFAULT NULL COMMENT '查询栏位9',
  `field_value10` varchar(32) DEFAULT NULL COMMENT '查询栏位10',
  `result_value1` varchar(32) DEFAULT NULL COMMENT '结果栏位1',
  `result_value2` varchar(32) DEFAULT NULL COMMENT '结果栏位2',
  `result_value3` varchar(32) DEFAULT NULL COMMENT '结果栏位3',
  `result_value4` varchar(32) DEFAULT NULL COMMENT '结果栏位4',
  `result_value5` varchar(32) DEFAULT NULL COMMENT '结果栏位5',
  `result_value6` varchar(32) DEFAULT NULL COMMENT '结果栏位6',
  `result_value7` varchar(32) DEFAULT NULL COMMENT '结果栏位7',
  `result_value8` varchar(32) DEFAULT NULL COMMENT '结果栏位8',
  `result_value9` varchar(32) DEFAULT NULL COMMENT '结果栏位9',
  `result_value10` varchar(32) DEFAULT NULL COMMENT '结果栏位10',
  PRIMARY KEY (`object_rrn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Context Value Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `COM_CONTEXT_VALUE`
--

LOCK TABLES `COM_CONTEXT_VALUE` WRITE;
/*!40000 ALTER TABLE `COM_CONTEXT_VALUE` DISABLE KEYS */;
/*!40000 ALTER TABLE `COM_CONTEXT_VALUE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `COM_GENERATOR_RULE`
--

DROP TABLE IF EXISTS `COM_GENERATOR_RULE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `COM_GENERATOR_RULE` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `name` varchar(64) DEFAULT NULL COMMENT '名称',
  `DESCRIPTION` varchar(64) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_GENERATOR_RULE_ORG_RRN_NAME` (`org_rrn`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='generator rule Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `COM_GENERATOR_RULE`
--

LOCK TABLES `COM_GENERATOR_RULE` WRITE;
/*!40000 ALTER TABLE `COM_GENERATOR_RULE` DISABLE KEYS */;
INSERT INTO `COM_GENERATOR_RULE` VALUES (1,'Y',1,'2019-01-23 18:52:38',NULL,'2019-04-11 16:22:04','admin',2,'CreateMLot','创建物料批次');
/*!40000 ALTER TABLE `COM_GENERATOR_RULE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `COM_GENERATOR_RULE_LINE`
--

DROP TABLE IF EXISTS `COM_GENERATOR_RULE_LINE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `COM_GENERATOR_RULE_LINE` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `RULE_RRN` bigint(20) DEFAULT NULL COMMENT 'Rule的主键',
  `SEQ_NO` bigint(20) DEFAULT NULL COMMENT '顺序',
  `DATA_TYPE` varchar(64) DEFAULT NULL COMMENT '数据类型',
  `REFERENCE_NAME` varchar(64) DEFAULT NULL COMMENT '用户栏位参考值的名称',
  `FIXED_STRING` varchar(64) DEFAULT NULL COMMENT '固定字符串',
  `DATE_TYPE` varchar(64) DEFAULT NULL COMMENT '日期类型',
  `SPECIFIC_DATE` datetime DEFAULT NULL COMMENT '指定的时间',
  `DATE_FORMAT` varchar(64) DEFAULT NULL COMMENT '日期格式',
  `SEQUENCE_TYPE` varchar(64) DEFAULT NULL COMMENT '序列号类型 Digits/Radix',
  `SEQUENCE_DIRECTION` varchar(64) DEFAULT NULL COMMENT '自增方向',
  `LENGTH` bigint(20) DEFAULT NULL COMMENT '长度',
  `EXCLUDE` varchar(32) DEFAULT NULL COMMENT '去除条件',
  `EXCLUDE_TYPE` varchar(32) DEFAULT NULL COMMENT '去除类型 All/Include',
  `MIN` varchar(32) DEFAULT NULL COMMENT '最小值',
  `MAX` varchar(32) DEFAULT NULL COMMENT '最大值',
  `VARIABLE_TYPE` varchar(64) DEFAULT NULL COMMENT '参数类型 Parameter/DBValue',
  `PARAMETER` varchar(64) DEFAULT NULL COMMENT '参数',
  `TABLE_NAME` varchar(64) DEFAULT NULL COMMENT '表名 当ParamterType是DBValue的时候',
  `COLUMN_NAME` varchar(64) DEFAULT NULL COMMENT '列名 当ParamterType是DBValue的时候',
  `WHERE_CLAUSE` varchar(64) DEFAULT NULL COMMENT '条件 当ParamterType是DBValue的时候',
  `VARIABLE_DIRECTION` varchar(64) DEFAULT NULL COMMENT '参数值方向 Left/Right',
  `START_POSITION` bigint(20) DEFAULT NULL COMMENT '起始下标',
  `PLACEHOLDER` varchar(1) DEFAULT NULL COMMENT '占位符 当paramter对应的值的长度不够的时候进行补位。',
  PRIMARY KEY (`object_rrn`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8 COMMENT='Generator rule line Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `COM_GENERATOR_RULE_LINE`
--

LOCK TABLES `COM_GENERATOR_RULE_LINE` WRITE;
/*!40000 ALTER TABLE `COM_GENERATOR_RULE_LINE` DISABLE KEYS */;
INSERT INTO `COM_GENERATOR_RULE_LINE` VALUES (20,'Y',1,1,3,'F',NULL,'2',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(21,'Y',1,1,2,'F',NULL,'3',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(22,'Y',1,1,1,'D',NULL,NULL,'SYSTEM',NULL,'dd',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(23,'Y',1,1,4,'S',NULL,NULL,NULL,NULL,NULL,'Digits','Up',4,NULL,'All','1','9999',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `COM_GENERATOR_RULE_LINE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `COM_GENERATOR_SEQUENCE`
--

DROP TABLE IF EXISTS `COM_GENERATOR_SEQUENCE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `COM_GENERATOR_SEQUENCE` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `name` varchar(64) DEFAULT NULL COMMENT '名称',
  `NEXT_SEQ` bigint(20) DEFAULT NULL COMMENT '下个值',
  `GENERATOR_LINE_RRN` bigint(20) DEFAULT NULL COMMENT 'GeneratorRuleLine的主键',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_SEQUENCE_ORG_RRN_NAME` (`org_rrn`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='Sequence Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `COM_GENERATOR_SEQUENCE`
--

LOCK TABLES `COM_GENERATOR_SEQUENCE` WRITE;
/*!40000 ALTER TABLE `COM_GENERATOR_SEQUENCE` DISABLE KEYS */;
INSERT INTO `COM_GENERATOR_SEQUENCE` VALUES (3,'Y',1,'2019-03-06 17:07:15',NULL,'2019-03-06 17:07:27',NULL,4,'0632_',4,23),(4,'Y',1,'2019-03-07 10:29:32',NULL,'2019-03-07 15:37:09',NULL,21,'0732_',21,23),(5,'Y',1,'2019-03-25 17:29:51',NULL,'2019-03-25 17:29:51',NULL,2,'2532_',2,23),(6,'Y',1,'2019-03-26 11:50:13',NULL,'2019-03-26 14:34:16',NULL,3,'2632_',3,23),(7,'Y',1,'2019-03-27 13:38:05',NULL,'2019-03-27 17:39:28',NULL,10,'2732_',10,23),(8,'Y',1,'2019-03-28 16:57:44',NULL,'2019-03-28 16:57:44',NULL,2,'2832_',2,23),(9,'Y',1,'2019-04-03 10:23:35',NULL,'2019-04-03 17:36:21',NULL,6,'0332_',6,23);
/*!40000 ALTER TABLE `COM_GENERATOR_SEQUENCE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `COM_SM_EVENT`
--

DROP TABLE IF EXISTS `COM_SM_EVENT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `COM_SM_EVENT` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `name` varchar(64) DEFAULT NULL COMMENT '名称',
  `DESCRIPTION` varchar(64) DEFAULT NULL COMMENT '描述',
  `CATEGORY` varchar(64) DEFAULT NULL COMMENT '类别',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_EVENT_ORG_RRN_NAME_CATEGORY` (`org_rrn`,`name`,`CATEGORY`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='Event Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `COM_SM_EVENT`
--

LOCK TABLES `COM_SM_EVENT` WRITE;
/*!40000 ALTER TABLE `COM_SM_EVENT` DISABLE KEYS */;
INSERT INTO `COM_SM_EVENT` VALUES (1,'Y',1,'2019-02-15 13:39:41',NULL,'2019-03-07 10:24:40','admin',2,'Receive','接收','MATERIAL'),(3,'Y',1,'2019-02-15 13:39:58',NULL,'2019-03-07 10:40:32','admin',3,'StockIn','入库','MATERIAL'),(4,'Y',1,'2019-02-15 13:42:59',NULL,'2019-03-07 11:07:13','admin',4,'StockOut','出库','MATERIAL'),(5,'Y',1,'2019-03-27 14:41:33','admin','2019-03-27 14:41:33',NULL,1,'Pick','领料','MATERIAL'),(6,'Y',1,'2019-03-27 17:19:04','admin','2019-03-27 17:19:04',NULL,1,'Check','盘点','MATERIAL'),(7,'Y',1,'2019-03-29 18:27:46','admin','2019-03-29 18:27:46',NULL,1,'Consume','消耗','MATERIAL'),(8,'Y',1,'2019-03-29 18:49:42','admin','2019-03-29 18:49:42',NULL,1,'UseUp','使用完(当批次数量为0的时候会触发此事件)','MATERIAL'),(9,'Y',1,'2019-04-03 10:57:59','admin','2019-04-03 10:57:59',NULL,1,'Package','包装','MATERIAL');
/*!40000 ALTER TABLE `COM_SM_EVENT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `COM_SM_EVENT_STATUS`
--

DROP TABLE IF EXISTS `COM_SM_EVENT_STATUS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `COM_SM_EVENT_STATUS` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `EVENT_RRN` bigint(20) DEFAULT NULL COMMENT '事件主键',
  `CHECK_FLAG` varchar(64) DEFAULT NULL COMMENT '检查标记 Reject Allow',
  `SOURCE_STATUS_CATEGORY` varchar(64) DEFAULT NULL COMMENT '源状态大类',
  `SOURCE_STATUS` varchar(64) DEFAULT NULL,
  `SOURCE_SUB_STATUS` varchar(64) DEFAULT NULL,
  `TARGET_STATUS_CATEGORY` varchar(64) DEFAULT NULL COMMENT '目标状态大类',
  `TARGET_STATUS` varchar(64) DEFAULT NULL,
  `TARGET_SUB_STATUS` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`object_rrn`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='Event Status Change Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `COM_SM_EVENT_STATUS`
--

LOCK TABLES `COM_SM_EVENT_STATUS` WRITE;
/*!40000 ALTER TABLE `COM_SM_EVENT_STATUS` DISABLE KEYS */;
INSERT INTO `COM_SM_EVENT_STATUS` VALUES (1,'Y',1,1,'Allow','Create','Create','','Receive','Receive',''),(2,'Y',1,NULL,'Allow','Receive','Receive','','Stock','In',''),(3,'Y',1,3,'Allow','Receive','Receive','','Stock','In',''),(4,'Y',1,NULL,'Allow','Stock','In','','Stock','Out',''),(5,'Y',1,4,'Allow','Stock','In','*','Stock','Out','*'),(6,'Y',1,5,'Allow','Stock','In','*','Use','Wait','*'),(7,'Y',1,6,'Allow','Stock','In','*','Stock','In','*'),(8,'Y',1,7,'Allow','Use','Wait','*','Use','Wait','*'),(9,'Y',1,8,'Allow','Use','Wait','*','Fin','UseUp','*'),(10,'Y',1,9,'Allow','Use','Wait','*','Fin','Package','*');
/*!40000 ALTER TABLE `COM_SM_EVENT_STATUS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `COM_SM_STATUS`
--

DROP TABLE IF EXISTS `COM_SM_STATUS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `COM_SM_STATUS` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `name` varchar(64) DEFAULT NULL COMMENT '名称',
  `DESCRIPTION` varchar(64) DEFAULT NULL COMMENT '描述',
  `CATEGORY` varchar(64) DEFAULT NULL COMMENT '类别',
  `AVAILABLE_FLAG` varchar(1) DEFAULT 'Y' COMMENT '是否可用',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_STATUS_ORG_RRN_NAME_CATEGORY` (`org_rrn`,`name`,`CATEGORY`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='Status Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `COM_SM_STATUS`
--

LOCK TABLES `COM_SM_STATUS` WRITE;
/*!40000 ALTER TABLE `COM_SM_STATUS` DISABLE KEYS */;
INSERT INTO `COM_SM_STATUS` VALUES (1,'Y',1,'2019-02-15 13:26:41',NULL,'2019-02-15 13:26:41',NULL,1,'Receive','接收','MATERIAL',NULL),(2,'Y',1,'2019-02-15 13:26:53',NULL,'2019-02-15 13:26:53',NULL,1,'In','在库','MATERIAL',NULL),(3,'Y',1,'2019-02-15 13:27:05',NULL,'2019-02-15 13:27:05',NULL,1,'Out','出库','MATERIAL',NULL),(4,'Y',1,'2019-02-15 13:29:01',NULL,'2019-02-15 13:29:01',NULL,1,'Reject','拒绝','MATERIAL',NULL),(5,'Y',1,'2019-02-15 13:38:46',NULL,'2019-02-15 13:38:46',NULL,1,'UseUp','用完','MATERIAL',NULL),(6,'Y',1,'2019-03-06 19:01:59','admin','2019-03-07 10:43:31','admin',2,'Create','创建','MATERIAL','N'),(7,'Y',1,'2019-03-27 14:42:43','admin','2019-03-27 14:42:43',NULL,1,'Wait','等待使用','MATERIAL',NULL),(8,'Y',1,'2019-04-03 10:58:50','admin','2019-04-03 10:58:59','admin',2,'Package','包装','MATERIAL','N');
/*!40000 ALTER TABLE `COM_SM_STATUS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `COM_SM_STATUS_CATEGORY`
--

DROP TABLE IF EXISTS `COM_SM_STATUS_CATEGORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `COM_SM_STATUS_CATEGORY` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `name` varchar(64) DEFAULT NULL COMMENT '名称',
  `DESCRIPTION` varchar(64) DEFAULT NULL COMMENT '描述',
  `CATEGORY` varchar(64) DEFAULT NULL COMMENT '类别',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_STATUS_CATEGORY_ORG_RRN_NAME_CATEGORY` (`org_rrn`,`name`,`CATEGORY`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='Status Category Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `COM_SM_STATUS_CATEGORY`
--

LOCK TABLES `COM_SM_STATUS_CATEGORY` WRITE;
/*!40000 ALTER TABLE `COM_SM_STATUS_CATEGORY` DISABLE KEYS */;
INSERT INTO `COM_SM_STATUS_CATEGORY` VALUES (1,'Y',1,'2019-02-15 13:36:04',NULL,'2019-02-15 13:36:04',NULL,1,'Receive','接收','MATERIAL'),(3,'Y',1,'2019-02-15 13:36:25',NULL,'2019-03-07 10:42:57','admin',2,'Stock','库存状态','MATERIAL'),(4,'Y',1,'2019-02-15 13:36:34',NULL,'2019-02-15 13:37:18',NULL,2,'Wait','出库等待','MATERIAL'),(5,'Y',1,'2019-02-15 13:36:43',NULL,'2019-02-15 13:36:43',NULL,1,'Reject','拒绝','MATERIAL'),(6,'Y',1,'2019-02-15 13:37:26',NULL,'2019-02-15 13:37:26',NULL,1,'Use','使用中','MATERIAL'),(8,'Y',1,'2019-02-15 13:39:03',NULL,'2019-02-15 13:39:03',NULL,1,'Scrap','报废','MATERIAL'),(9,'Y',1,'2019-03-06 19:05:45','admin','2019-03-07 10:43:07','admin',2,'Create','创建','MATERIAL'),(10,'Y',1,'2019-03-29 18:50:09','admin','2019-03-29 18:50:09',NULL,1,'Fin','结束','MATERIAL');
/*!40000 ALTER TABLE `COM_SM_STATUS_CATEGORY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `COM_SM_STATUS_MODEL`
--

DROP TABLE IF EXISTS `COM_SM_STATUS_MODEL`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `COM_SM_STATUS_MODEL` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `name` varchar(64) DEFAULT NULL COMMENT '名称',
  `DESCRIPTION` varchar(64) DEFAULT NULL COMMENT '描述',
  `CATEGORY` varchar(64) DEFAULT NULL COMMENT '类别',
  `INITIAL_STATE_CATEGORY` varchar(64) DEFAULT NULL COMMENT '初始化状态大类',
  `INITIAL_STATE` varchar(64) DEFAULT NULL COMMENT '初始化状态',
  `INITIAL_SUB_STATE` varchar(64) DEFAULT NULL COMMENT '初始化状态小类',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_STATUS_MODEL_ORG_RRN_NAME_CATEGORY` (`org_rrn`,`name`,`CATEGORY`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='Status Model Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `COM_SM_STATUS_MODEL`
--

LOCK TABLES `COM_SM_STATUS_MODEL` WRITE;
/*!40000 ALTER TABLE `COM_SM_STATUS_MODEL` DISABLE KEYS */;
INSERT INTO `COM_SM_STATUS_MODEL` VALUES (1,'Y',1,'2019-02-15 17:23:05',NULL,'2019-03-29 18:28:32','admin',13,'Normal','正常的物料状态模型','MATERIAL','Create','Create',NULL);
/*!40000 ALTER TABLE `COM_SM_STATUS_MODEL` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `COM_SM_STATUS_MODEL_EVENT`
--

DROP TABLE IF EXISTS `COM_SM_STATUS_MODEL_EVENT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `COM_SM_STATUS_MODEL_EVENT` (
  `MODEL_RRN` bigint(20) DEFAULT NULL COMMENT '状态模型主键',
  `EVENT_RRN` bigint(20) DEFAULT NULL COMMENT '事件主键'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Status Model Event Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `COM_SM_STATUS_MODEL_EVENT`
--

LOCK TABLES `COM_SM_STATUS_MODEL_EVENT` WRITE;
/*!40000 ALTER TABLE `COM_SM_STATUS_MODEL_EVENT` DISABLE KEYS */;
INSERT INTO `COM_SM_STATUS_MODEL_EVENT` VALUES (1,7),(1,6),(1,5),(1,1),(1,3),(1,4);
/*!40000 ALTER TABLE `COM_SM_STATUS_MODEL_EVENT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `COM_SM_STATUS_MODEL_EVENT_ROLE`
--

DROP TABLE IF EXISTS `COM_SM_STATUS_MODEL_EVENT_ROLE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `COM_SM_STATUS_MODEL_EVENT_ROLE` (
  `ROLE_RRN` bigint(20) DEFAULT NULL COMMENT '角色主键',
  `MODEL_EVENT_RRN` bigint(20) DEFAULT NULL COMMENT '状态模型事件主键'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Status Model Event Role Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `COM_SM_STATUS_MODEL_EVENT_ROLE`
--

LOCK TABLES `COM_SM_STATUS_MODEL_EVENT_ROLE` WRITE;
/*!40000 ALTER TABLE `COM_SM_STATUS_MODEL_EVENT_ROLE` DISABLE KEYS */;
/*!40000 ALTER TABLE `COM_SM_STATUS_MODEL_EVENT_ROLE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `COM_SM_SUB_STATUS`
--

DROP TABLE IF EXISTS `COM_SM_SUB_STATUS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `COM_SM_SUB_STATUS` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `name` varchar(64) DEFAULT NULL COMMENT '名称',
  `DESCRIPTION` varchar(64) DEFAULT NULL COMMENT '描述',
  `CATEGORY` varchar(64) DEFAULT NULL COMMENT '类别',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_SUB_STATUS_ORG_RRN_NAME_CATEGORY` (`org_rrn`,`name`,`CATEGORY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Sub Status Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `COM_SM_SUB_STATUS`
--

LOCK TABLES `COM_SM_SUB_STATUS` WRITE;
/*!40000 ALTER TABLE `COM_SM_SUB_STATUS` DISABLE KEYS */;
/*!40000 ALTER TABLE `COM_SM_SUB_STATUS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `DATABASECHANGELOG`
--

DROP TABLE IF EXISTS `DATABASECHANGELOG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DATABASECHANGELOG` (
  `ID` varchar(255) NOT NULL,
  `AUTHOR` varchar(255) NOT NULL,
  `FILENAME` varchar(255) NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `ORDEREXECUTED` int(11) NOT NULL,
  `EXECTYPE` varchar(10) NOT NULL,
  `MD5SUM` varchar(35) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `COMMENTS` varchar(255) DEFAULT NULL,
  `TAG` varchar(255) DEFAULT NULL,
  `LIQUIBASE` varchar(20) DEFAULT NULL,
  `CONTEXTS` varchar(255) DEFAULT NULL,
  `LABELS` varchar(255) DEFAULT NULL,
  `DEPLOYMENT_ID` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `DATABASECHANGELOG`
--

LOCK TABLES `DATABASECHANGELOG` WRITE;
/*!40000 ALTER TABLE `DATABASECHANGELOG` DISABLE KEYS */;
INSERT INTO `DATABASECHANGELOG` VALUES ('Init-NB_AUTHORITY-Table','Xunbo Guo','classpath:db/changelog/db.changelog.authority.yaml','2019-01-22 12:15:38',1,'EXECUTED','8:1a18549d8c9d15e9c0578d8f81739188','sqlFile','Init Table to NB_AUTHORITY',NULL,'3.6.1',NULL,NULL,'8130537385'),('Init-NB_MESSAGE-Table','Xunbo Guo','classpath:db/changelog/db.changelog.message.yaml','2019-01-22 12:15:38',2,'EXECUTED','8:5ba936f54ce9db3c49629d41411f3076','sqlFile','Init Table to NB_MESSAGE',NULL,'3.6.1',NULL,NULL,'8130537385'),('Init-NB_ORG-Table','Xunbo Guo','classpath:db/changelog/db.changelog.org.yaml','2019-01-22 12:15:38',3,'EXECUTED','8:efb657d936fe8c7c7d57a807e38cd451','sqlFile','Init Table to NB_ORG',NULL,'3.6.1',NULL,NULL,'8130537385'),('Init-NB_QUERY-Table','Xunbo Guo','classpath:db/changelog/db.changelog.query.yaml','2019-01-22 12:15:38',4,'EXECUTED','8:674a70998b5acb3ec9cd6f2e36f9792d','sqlFile','Init Table to NB_QUERY',NULL,'3.6.1',NULL,NULL,'8130537385'),('Init-NB_RELATION-Table','Xunbo Guo','classpath:db/changelog/db.changelog.relation.yaml','2019-01-22 12:15:38',5,'EXECUTED','8:91a2bf82ddfc82f5f372a96f18486ed5','sqlFile','Init Table to NB_RELATION',NULL,'3.6.1',NULL,NULL,'8130537385'),('Init-NB_ROLE-Table','Xunbo Guo','classpath:db/changelog/db.changelog.role.yaml','2019-01-22 12:15:38',6,'EXECUTED','8:2ab927fc28908e27a4832962b47c0ed5','sqlFile','Init Table to NB_ROLE',NULL,'3.6.1',NULL,NULL,'8130537385'),('Init-NB_ROLE_AUTHORITY-Table','Xunbo Guo','classpath:db/changelog/db.changelog.role_authority.yaml','2019-01-22 12:15:38',7,'EXECUTED','8:de591a07dcec0dceaf279f9ce1c79a03','sqlFile','Init Table to NB_ROLE_AUTHORITY',NULL,'3.6.1',NULL,NULL,'8130537385'),('Init-NB_ROLE_HIS-Table','Xunbo Guo','classpath:db/changelog/db.changelog.role_his.yaml','2019-01-22 12:15:38',8,'EXECUTED','8:89813e73d1a08aac915063d19f780d5d','sqlFile','Init Table to NB_ROLE_HIS',NULL,'3.6.1',NULL,NULL,'8130537385'),('Init-NB_USER-Table','Xunbo Guo','classpath:db/changelog/db.changelog.user.yaml','2019-01-22 12:15:38',9,'EXECUTED','8:01d133d272a2d4753bf105ed99416c87','sqlFile','Init Table to NB_USER',NULL,'3.6.1',NULL,NULL,'8130537385'),('Init-NB_USER_ORG-Table','Xunbo Guo','classpath:db/changelog/db.changelog.user_org.yaml','2019-01-22 12:15:38',10,'EXECUTED','8:a6ed0c66bb4f29f00dfe45d10e1d89ba','sqlFile','Init Table to NB_USER_ORG',NULL,'3.6.1',NULL,NULL,'8130537385'),('Init-NB_USER_ROLE-Table','Xunbo Guo','classpath:db/changelog/db.changelog.user_role.yaml','2019-01-22 12:15:38',11,'EXECUTED','8:64c3714d4b761a306be3a7b42406f36d','sqlFile','Init Table to NB_USER_ROLE',NULL,'3.6.1',NULL,NULL,'8130537385'),('Init-NB_USER_HIS-Table','Xunbo Guo','classpath:db/changelog/db.changelog.user_his.yaml','2019-01-22 12:15:38',12,'EXECUTED','8:649d7e11555f8ba5fb1f21aeb122b4c9','sqlFile','Init Table to NB_USER_HIS',NULL,'3.6.1',NULL,NULL,'8130537385'),('Init-NB_TABLE-Table','Xunbo Guo','classpath:db/changelog/ui/db.changelog.table.yaml','2019-01-22 12:15:38',13,'EXECUTED','8:844b094800bb341720a632d2dc2407ff','sqlFile','Init Table to NB_TABLE',NULL,'3.6.1',NULL,NULL,'8130537385'),('Init-NB_TAB-Table','Xunbo Guo','classpath:db/changelog/ui/db.changelog.tab.yaml','2019-01-22 12:15:38',14,'EXECUTED','8:c54012c4e60c671a9938458d9436048d','sqlFile','Init Table to NB_TAB',NULL,'3.6.1',NULL,NULL,'8130537385'),('Init-NB_FIELD-Table','Xunbo Guo','classpath:db/changelog/ui/db.changelog.field.yaml','2019-01-22 12:15:38',15,'EXECUTED','8:2611a4f712de1ce67230c61ab63ad89c','sqlFile','Init Table to NB_FIELD',NULL,'3.6.1',NULL,NULL,'8130537385'),('Init-NB_REFERENCE_TABLE-Table','Xunbo Guo','classpath:db/changelog/ui/db.changelog.ref_table.yaml','2019-01-22 12:15:38',16,'EXECUTED','8:a32240422964958117863d09300bf4b7','sqlFile','Init Table to NB_REFERENCE_TABLE',NULL,'3.6.1',NULL,NULL,'8130537385'),('Init-NB_REFERENCE_NAME-Table','Xunbo Guo','classpath:db/changelog/ui/db.changelog.ref_name.yaml','2019-01-22 12:15:38',17,'EXECUTED','8:6a76de06fa850732a4dac3dc929c1ad9','sqlFile','Init Table to NB_REFERENCE_NAME',NULL,'3.6.1',NULL,NULL,'8130537385'),('Init-NB_REFERENCE_LIST-Table','Xunbo Guo','classpath:db/changelog/ui/db.changelog.ref_list.yaml','2019-01-22 12:15:38',18,'EXECUTED','8:fe4520ba64538129a8adf7400a293a05','sqlFile','Init Table to NB_REFERENCE_LIST',NULL,'3.6.1',NULL,NULL,'8130537385'),('create-rms_equipment-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.equipment.yaml','2019-01-22 12:15:39',19,'EXECUTED','8:eb490288ccfab9ae1f0175b16f8d36a2','createTable tableName=RMS_EQUIPMENT','Create table RMS_EQUIPMENT',NULL,'3.6.1',NULL,NULL,'8130539182'),('add-unique-constraint-for-rms_equipment-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.2.equipment.yaml','2019-01-22 12:15:39',20,'EXECUTED','8:6ccd9a4c44af8a6fba7d04e0fa1fc216','addUniqueConstraint constraintName=UK_EQUIPMENT_ORG_RRN_EQP_ID, tableName=RMS_EQUIPMENT','Apply unique constraint (org_rrn, equipment_id) for table RMS_EQUIPMENT',NULL,'3.6.1',NULL,NULL,'8130539182'),('create-recipe-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.recipe.yaml','2019-01-22 12:15:39',21,'EXECUTED','8:f8aeae8134d35fd62f4f637a4a0c6cf5','createTable tableName=RMS_RECIPE','Create table RMS_RECIPE',NULL,'3.6.1',NULL,NULL,'8130539182'),('add-unique-constraint-for-recipe-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.2.recipe.yaml','2019-01-22 12:15:39',22,'EXECUTED','8:d157dabb0ec80dcbdf2fd077ea1ccd17','addUniqueConstraint constraintName=UK_RECIPE_ORG_RRN_NAME, tableName=RMS_RECIPE','Apply unique constraint (org_rrn, name) for table RMS_RECIPE',NULL,'3.6.1',NULL,NULL,'8130539182'),('create-recipe_equipment-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.recipe_equipment.yaml','2019-01-22 12:15:39',23,'EXECUTED','8:dc91d55159bb3282d677d2b1d0862e69','createTable tableName=RMS_RECIPE_EQUIPMENT','Create table RMS_RECIPE_EQUIPMENT',NULL,'3.6.1',NULL,NULL,'8130539182'),('create-recipe_equipment-his-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.recipe_equipment_his.yaml','2019-01-22 12:15:39',24,'EXECUTED','8:51c5693b768debe4e9810fb6d51b2b8c','createTable tableName=RMS_RECIPE_EQUIPMENT_HIS','Create table RMS_RECIPE_EQUIPMENT_HIS',NULL,'3.6.1',NULL,NULL,'8130539182'),('create-recipe_equipment_parameter-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.recipe_equipment_parameter.yaml','2019-01-22 12:15:39',25,'EXECUTED','8:e135fe79104126c1c070fa875ac9e81f','createTable tableName=RMS_RECIPE_EQUIPMENT_PARAMETER','Create table RMS_RECIPE_EQUIPMENT_PARAMETER',NULL,'3.6.1',NULL,NULL,'8130539182'),('add-unique-constraint-for-rms_equipment_parameter-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.2.recipe_equipment_parameter.yaml','2019-01-22 12:15:39',26,'EXECUTED','8:c804ed9b6863fe69445508811a377e5d','addUniqueConstraint constraintName=UK_RECIPE_EQP_PARAM_1, tableName=RMS_RECIPE_EQUIPMENT_PARAMETER','Apply unique constraint (recipe_equipment_rrn, parameter_name) for table RMS_RECIPE_EQUIPMENT_PARAMETER',NULL,'3.6.1',NULL,NULL,'8130539182'),('create-recipe_equipment_temp_parameter-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.recipe_equipment_parameter_temp.yaml','2019-01-22 12:15:39',27,'EXECUTED','8:7e056774d20fa9bc0306310b41edfbad','createTable tableName=RMS_RECIPE_EQUIPMENT_PARAM_TMP','Create table RMS_RECIPE_EQUIPMENT_PARAM_TMP',NULL,'3.6.1',NULL,NULL,'8130539182'),('create-com_context-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.context.yaml','2019-01-22 12:20:05',28,'EXECUTED','8:0a44e160b600afefa911f1e03d7361dc','createTable tableName=COM_CONTEXT','Create table COM_CONTEXT',NULL,'3.6.1',NULL,NULL,'8130805234'),('insert-context-table_rms_data','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.context.yaml','2019-01-22 12:20:05',29,'EXECUTED','8:437685fabaea5c45bf7ef546097cb623','sqlFile','Insert rms on context message to COM_CONTEXT',NULL,'3.6.1',NULL,NULL,'8130805234'),('add-unique-constraint-for-context-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.2.context.yaml','2019-01-22 12:20:05',30,'EXECUTED','8:a7a8db9797c0b1663498a3aa5f278aa9','addUniqueConstraint constraintName=UK_CONTEXT_ORG_RRN_NAME, tableName=COM_CONTEXT','Apply unique constraint (org_rrn, name) for table COM_CONTEXT',NULL,'3.6.1',NULL,NULL,'8130805234'),('create-com_context_value-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.context_value.yaml','2019-01-22 12:20:05',31,'EXECUTED','8:427dd9ad90a1083c651ba93aff8ed943','createTable tableName=COM_CONTEXT_VALUE','Create table COM_CONTEXT_VALUE',NULL,'3.6.1',NULL,NULL,'8130805234'),('create-sequence-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.sequence.yaml','2019-01-23 18:28:23',32,'EXECUTED','8:c0f3f01eb682e7b910f2cfe8685d958d','createTable tableName=COM_GENERATOR_SEQUENCE','Create table COM_GENERATOR_SEQUENCE',NULL,'3.6.1',NULL,NULL,'8239302779'),('add-unique-constraint-for-sequence-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.sequence.yaml','2019-01-23 18:28:23',33,'EXECUTED','8:98fb088240bb9de797e36e21ff2bde0f','addUniqueConstraint constraintName=UK_SEQUENCE_ORG_RRN_NAME, tableName=COM_GENERATOR_SEQUENCE','Apply unique constraint (org_rrn, name) for COM_GENERATOR_SEQUENCE',NULL,'3.6.1',NULL,NULL,'8239302779'),('create-generator_rule-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.generator_rule.yaml','2019-01-23 18:28:23',34,'EXECUTED','8:f9c056f52871bf599c9a8ddd874b2263','createTable tableName=COM_GENERATOR_RULE','Create table COM_GENERATOR_RULE',NULL,'3.6.1',NULL,NULL,'8239302779'),('add-unique-constraint-for-generator-rule-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.generator_rule.yaml','2019-01-23 18:28:23',35,'EXECUTED','8:fad016386f664e2571f5e833ead9e89d','addUniqueConstraint constraintName=UK_GENERATOR_RULE_ORG_RRN_NAME, tableName=COM_GENERATOR_RULE','Apply unique constraint (org_rrn, name) for COM_GENERATOR_RULE',NULL,'3.6.1',NULL,NULL,'8239302779'),('create-generator_rule_line-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.generator_rule_line.yaml','2019-01-23 18:28:23',36,'EXECUTED','8:c7c27a6c5f9cb04099afa205b5aa24d2','createTable tableName=COM_GENERATOR_RULE_LINE','Create table COM_GENERATOR_RULE_LINE',NULL,'3.6.1',NULL,NULL,'8239302779'),('create-status_machine_status_category-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.status_category.yaml','2019-02-13 18:09:46',37,'EXECUTED','8:60954cc11892cc34c728b09499f8bd84','createTable tableName=COM_SM_STATUS_CATEGORY','Create table COM_SM_STATUS_CATEGORY',NULL,'3.6.1',NULL,NULL,'0052586341'),('add-unique-constraint-for-status-category-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.status_category.yaml','2019-02-13 18:09:46',38,'EXECUTED','8:873e172844f06e1055bafe8728990a51','addUniqueConstraint constraintName=UK_STATUS_CATEGORY_ORG_RRN_NAME_CATEGORY, tableName=COM_SM_STATUS_CATEGORY','Apply unique constraint (org_rrn, name, category) for COM_SM_STATUS_CATEGORY',NULL,'3.6.1',NULL,NULL,'0052586341'),('create-status_machine_status-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.status.yaml','2019-02-13 18:09:46',39,'EXECUTED','8:b7c3d20026c6a77db01aa9150683dba2','createTable tableName=COM_SM_STATUS','Create table COM_SM_STATUS',NULL,'3.6.1',NULL,NULL,'0052586341'),('add-unique-constraint-for-status-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.status.yaml','2019-02-13 18:09:46',40,'EXECUTED','8:581199dcaff8e7e877220a07858ae007','addUniqueConstraint constraintName=UK_STATUS_ORG_RRN_NAME_CATEGORY, tableName=COM_SM_STATUS','Apply unique constraint (org_rrn, name, category) for COM_SM_STATUS',NULL,'3.6.1',NULL,NULL,'0052586341'),('create-status_machine_sub_status-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.sub_status.yaml','2019-02-13 18:09:46',41,'EXECUTED','8:8aa90fcff4c2b8761cdff38374f57dab','createTable tableName=COM_SM_SUB_STATUS','Create table COM_SM_SUB_STATUS',NULL,'3.6.1',NULL,NULL,'0052586341'),('add-unique-constraint-for-sub-status-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.sub_status.yaml','2019-02-13 18:09:46',42,'EXECUTED','8:bfc5be4e49df4b155150e586bc22bab2','addUniqueConstraint constraintName=UK_SUB_STATUS_ORG_RRN_NAME_CATEGORY, tableName=COM_SM_SUB_STATUS','Apply unique constraint (org_rrn, name, category) for COM_SM_SUB_STATUS',NULL,'3.6.1',NULL,NULL,'0052586341'),('create-status_machine_event-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.event.yaml','2019-02-13 18:09:46',43,'EXECUTED','8:fb34d5e5950670253c4cfa0ecadc1467','createTable tableName=COM_SM_EVENT','Create table COM_SM_EVENT',NULL,'3.6.1',NULL,NULL,'0052586341'),('add-unique-constraint-for-event-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.event.yaml','2019-02-13 18:09:46',44,'EXECUTED','8:9a62a76ce9249388f6fcbddf3a4659a2','addUniqueConstraint constraintName=UK_EVENT_ORG_RRN_NAME_CATEGORY, tableName=COM_SM_EVENT','Apply unique constraint (org_rrn, name, category) for COM_SM_EVENT',NULL,'3.6.1',NULL,NULL,'0052586341'),('create-status_machine_event_status-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.event_status.yaml','2019-02-13 18:09:46',45,'EXECUTED','8:956fc45394365d1d494df4d994676198','createTable tableName=COM_SM_EVENT_STATUS','Create table COM_SM_EVENT_STATUS',NULL,'3.6.1',NULL,NULL,'0052586341'),('create-status_machine_status_model-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.status_model.yaml','2019-02-13 18:09:46',46,'EXECUTED','8:5b74599d7d5f4b0834e90fe7d0ed9c68','createTable tableName=COM_SM_STATUS_MODEL','Create table COM_SM_STATUS_MODEL',NULL,'3.6.1',NULL,NULL,'0052586341'),('add-unique-constraint-for-status-model-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.status_model.yaml','2019-02-13 18:09:46',47,'EXECUTED','8:7f0f7efef66ea297c40b31632defbdde','addUniqueConstraint constraintName=UK_STATUS_MODEL_ORG_RRN_NAME_CATEGORY, tableName=COM_SM_STATUS_MODEL','Apply unique constraint (org_rrn, name, category) for COM_SM_STATUS_MODEL',NULL,'3.6.1',NULL,NULL,'0052586341'),('create-status_machine_status_model_event-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.status_model_event.yaml','2019-02-13 18:09:46',48,'EXECUTED','8:39b0fb193b6eb60b31e4bf429055b9d6','createTable tableName=COM_SM_STATUS_MODEL_EVENT','Create table COM_SM_STATUS_MODEL_EVENT',NULL,'3.6.1',NULL,NULL,'0052586341'),('create-status_machine_status_model-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.status_model_event_role.yaml','2019-02-13 18:09:46',49,'EXECUTED','8:d28daccc5a7c2d4430831e7f9cb12727','createTable tableName=COM_SM_STATUS_MODEL_EVENT_ROLE','Create table COM_SM_STATUS_MODEL_EVENT_ROLE',NULL,'3.6.1',NULL,NULL,'0052586341'),('drop-COM_SM_STATUS_MODEL_EVENT_Column_0.0.2','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.2.status_model_event.yaml','2019-02-20 11:31:52',50,'EXECUTED','8:0cffabc917506b91956f869b01780c6b','dropColumn columnName=object_rrn, tableName=COM_SM_STATUS_MODEL_EVENT; dropColumn columnName=active_flag, tableName=COM_SM_STATUS_MODEL_EVENT; dropColumn columnName=org_rrn, tableName=COM_SM_STATUS_MODEL_EVENT; dropColumn columnName=created, table...','Drop COM_SM_STATUS_MODEL_EVENT_Column Column(object_rrn,active_flag..SEQ_NO,LIMIT_COUNT)',NULL,'3.6.1',NULL,NULL,'0633512084'),('create-mms_material-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.material.yaml','2019-02-20 16:43:14',51,'EXECUTED','8:ae86754839d0971d65f28a6d6bea1930','createTable tableName=MMS_MATERIAL','Create table MMS_MATERIAL',NULL,'3.6.1',NULL,NULL,'0652194824'),('add-unique-constraint-for-material-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.material.yaml','2019-02-20 16:43:14',52,'EXECUTED','8:7f71995142a5d05488117cf827eee489','addUniqueConstraint constraintName=UK_MATERIAL_ORG_RRN_NAME_CLASS_VERSION, tableName=MMS_MATERIAL','Apply unique constraint (org_rrn, name,class,version) for MMS_MATERIAL',NULL,'3.6.1',NULL,NULL,'0652194824'),('modify-status_machine_event_status-column-0.0.2','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.2.event_status.yaml','2019-02-27 19:29:47',57,'EXECUTED','8:a253d12b97866bbd1093c5bb4628b99d','renameColumn newColumnName=SOURCE_STATUS, oldColumnName=SOURCE_STATE, tableName=COM_SM_EVENT_STATUS; renameColumn newColumnName=SOURCE_SUB_STATUS, oldColumnName=SOURCE_SUB_STATE, tableName=COM_SM_EVENT_STATUS; renameColumn newColumnName=TARGET_STA...','Rename SOURCE_STATE->SOURCE_STATUS, SOURCE_SUB_STATE->SOURCE_SUB_STATUS, TARGET_STATE-> TARGET_STATUS, TARGET_SUB_STATE->TARGET_SUB_STATUS',NULL,'3.6.1',NULL,NULL,'1266987530'),('create-mms_warehouse-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.warehouse.yaml','2019-03-02 12:07:34',62,'EXECUTED','8:9b9123a0fef5d7aaba740cc2dc9c9668','createTable tableName=MMS_WAREHOUSE','Create table MMS_WAREHOUSE',NULL,'3.6.1',NULL,NULL,'1499654259'),('add-unique-constraint-for-mms_warehouse-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.warehouse.yaml','2019-03-02 12:07:34',63,'EXECUTED','8:96be01fe9e0740992ab807173f166951','addUniqueConstraint constraintName=UK_MMS_WAREHOUSE_ORG_RRN_NAME, tableName=MMS_WAREHOUSE','Apply unique constraint (org_rrn, NAME) for MMS_WAREHOUSE',NULL,'3.6.1',NULL,NULL,'1499654259'),('create-mms_material-history-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.material_his.yaml','2019-03-07 14:57:56',64,'EXECUTED','8:648aeb4b44d8505bb5695b51936e9774','createTable tableName=MMS_MATERIAL_HIS','Create table MMS_MATERIAL_HIS',NULL,'3.6.1',NULL,NULL,'1941876495'),('create-mms_material_lot_inventory-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.material_lot_inventory.yaml','2019-03-25 17:26:02',68,'EXECUTED','8:1330af2bdb25b0714e30353644e23e40','createTable tableName=MMS_MATERIAL_LOT_INVENTORY','Create table MMS_MATERIAL_LOT_INVENTORY',NULL,'3.6.1',NULL,NULL,'3505962009'),('create-mms_material_lot-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.material_lot.yaml','2019-04-03 10:08:00',69,'EXECUTED','8:4851d963d6657cdbaf2b8fcc98fc8434','createTable tableName=MMS_MATERIAL_LOT','Create table MMS_MATERIAL_LOT',NULL,'3.6.1',NULL,NULL,'4257280157'),('add-unique-constraint-for-material_lot-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.material_lot.yaml','2019-04-03 10:08:00',70,'EXECUTED','8:17cf1e07c4c67979eba04fed204de645','addUniqueConstraint constraintName=UK_MMS_MATERIAL_LOT_ORG_RRN_MATERIAL_LOT_ID, tableName=MMS_MATERIAL_LOT','Apply unique constraint (org_rrn, MATERIAL_LOT_ID) for MMS_MATERIAL_LOT',NULL,'3.6.1',NULL,NULL,'4257280157'),('create-mms_material_lot_his-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.material_lot_his.yaml','2019-04-03 10:08:00',71,'EXECUTED','8:5ba44c57992b4123692997ea45231ba4','createTable tableName=MMS_MATERIAL_LOT_HIS','Create table MMS_MATERIAL_LOT_HIS',NULL,'3.6.1',NULL,NULL,'4257280157'),('create-mms_package_type-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.package_type.yaml','2019-04-03 16:08:44',72,'EXECUTED','8:228817fcfbc91a043e4be780011a16b2','createTable tableName=MMS_PACKAGE_TYPE','Create table MMS_PACKAGE_TYPE',NULL,'3.6.1',NULL,NULL,'4278923949'),('add-unique-constraint-for-package_type-table','Xunbo Guo','classpath:db/changelog/db.changelog-0.0.1.package_type.yaml','2019-04-03 16:08:44',73,'EXECUTED','8:fb898b23f02394411f00c947944ae858','addUniqueConstraint constraintName=UK_PACKAGE_TYPE_ORG_RRN_NAME_CLASS, tableName=MMS_PACKAGE_TYPE','Apply unique constraint (org_rrn, name,class) for MMS_PACKAGE_TYPE',NULL,'3.6.1',NULL,NULL,'4278923949'),('NB_FIELD-remove-column_persist_flag','Xunbo Guo','classpath:db/changelog/ui/db.changelog.field_0.0.4.yaml','2019-04-04 13:38:47',74,'EXECUTED','8:99c354d2566ee568d567f903462c7f89','dropColumn columnName=PERSIST_FLAG, tableName=NB_FIELD','NB_FIELD remove column persist_flag',NULL,'3.6.1',NULL,NULL,'4356327020'),('NB_FIELD-remove-column_export_flag','Xunbo Guo','classpath:db/changelog/ui/db.changelog.field_0.0.5.yaml','2019-04-04 13:40:22',75,'EXECUTED','8:b10cf78381c33f142318c964ba5fa90f','dropColumn columnName=export_flag, tableName=NB_FIELD','NB_FIELD remove column export_flag',NULL,'3.6.1',NULL,NULL,'4356422335'),('NB_FIELD-remove-column_min_max_value_add-column_negative_flag','Xunbo Guo','classpath:db/changelog/ui/db.changelog.field_0.0.6.yaml','2019-04-12 11:41:02',76,'EXECUTED','8:29a49f730248f7065794e1590e835cba','dropColumn columnName=min_value, tableName=NB_FIELD; dropColumn columnName=max_value, tableName=NB_FIELD; addColumn tableName=NB_FIELD','NB_FIELD remove column min_value,max_value and add column negative_flag',NULL,'3.6.1',NULL,NULL,'5040462288');
/*!40000 ALTER TABLE `DATABASECHANGELOG` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `DATABASECHANGELOGLOCK`
--

DROP TABLE IF EXISTS `DATABASECHANGELOGLOCK`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DATABASECHANGELOGLOCK` (
  `ID` int(11) NOT NULL,
  `LOCKED` bit(1) NOT NULL,
  `LOCKGRANTED` datetime DEFAULT NULL,
  `LOCKEDBY` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `DATABASECHANGELOGLOCK`
--

LOCK TABLES `DATABASECHANGELOGLOCK` WRITE;
/*!40000 ALTER TABLE `DATABASECHANGELOGLOCK` DISABLE KEYS */;
INSERT INTO `DATABASECHANGELOGLOCK` VALUES (1,'\0',NULL,NULL);
/*!40000 ALTER TABLE `DATABASECHANGELOGLOCK` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MMS_MATERIAL`
--

DROP TABLE IF EXISTS `MMS_MATERIAL`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MMS_MATERIAL` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `name` varchar(64) DEFAULT NULL COMMENT '名称',
  `DESCRIPTION` varchar(64) DEFAULT NULL COMMENT '描述',
  `VERSION` bigint(20) DEFAULT NULL COMMENT '版本',
  `STATUS` varchar(64) DEFAULT NULL COMMENT '状态',
  `ACTIVE_USER` varchar(64) DEFAULT NULL COMMENT '激活人',
  `ACTIVE_TIME` datetime DEFAULT NULL COMMENT '激活时间',
  `CLASS` varchar(64) DEFAULT NULL COMMENT '类别',
  `STATUS_MODEL_RRN` bigint(20) DEFAULT NULL COMMENT '状态模型主键',
  `MATERIAL_CATEGORY` varchar(64) DEFAULT NULL COMMENT '物料类别',
  `MATERIAL_TYPE` varchar(64) DEFAULT NULL COMMENT '物料类型',
  `STORE_UOM` varchar(64) DEFAULT NULL COMMENT '库存单位',
  `SAFETY_STORE_QTY` bigint(20) DEFAULT NULL COMMENT '安全库存',
  `MAX_STORE_QTY` bigint(20) DEFAULT NULL COMMENT '最大库存',
  `WAREHOUSE_RRN` bigint(20) DEFAULT NULL COMMENT '默认仓库',
  `EFFECTIVE_LIFE` bigint(20) DEFAULT NULL COMMENT '有效时长',
  `WARNING_LIFE` bigint(20) DEFAULT NULL COMMENT '警告时长 当物料批次达到此时长的时候触发警告',
  `EFFECTIVE_UNIT` varchar(32) DEFAULT NULL COMMENT '有效时长单位',
  `DELIVERY_POLICY` varchar(64) DEFAULT NULL COMMENT '物料出库策略',
  `RESERVED1` varchar(64) DEFAULT NULL COMMENT '预留栏位1',
  `RESERVED2` varchar(64) DEFAULT NULL COMMENT '预留栏位2',
  `RESERVED3` varchar(64) DEFAULT NULL COMMENT '预留栏位3',
  `RESERVED4` varchar(64) DEFAULT NULL COMMENT '预留栏位4',
  `RESERVED5` varchar(64) DEFAULT NULL COMMENT '预留栏位5',
  `RESERVED6` varchar(64) DEFAULT NULL COMMENT '预留栏位6',
  `RESERVED7` varchar(64) DEFAULT NULL COMMENT '预留栏位7',
  `RESERVED8` varchar(64) DEFAULT NULL COMMENT '预留栏位8',
  `RESERVED9` varchar(64) DEFAULT NULL COMMENT '预留栏位9',
  `RESERVED10` varchar(64) DEFAULT NULL COMMENT '预留栏位10',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_MATERIAL_ORG_RRN_NAME_CLASS_VERSION` (`org_rrn`,`name`,`CLASS`,`VERSION`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='Material Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MMS_MATERIAL`
--

LOCK TABLES `MMS_MATERIAL` WRITE;
/*!40000 ALTER TABLE `MMS_MATERIAL` DISABLE KEYS */;
INSERT INTO `MMS_MATERIAL` VALUES (2,'Y',1,'2019-03-07 15:18:36','admin','2019-03-07 15:18:36','admin',1,'1070100010','测试物料',1,'Active',NULL,NULL,'RAW',1,'Material',NULL,'个',NULL,NULL,1,10,8,'Hour',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,'Y',1,'2019-03-28 17:02:51','admin','2019-03-29 18:50:46','admin',2,'107EXP','DG',1,'Active','admin','2019-03-28 17:02:51','RAW',1,'Material',NULL,NULL,NULL,NULL,1,5,1,'Hour',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `MMS_MATERIAL` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MMS_MATERIAL_HIS`
--

DROP TABLE IF EXISTS `MMS_MATERIAL_HIS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MMS_MATERIAL_HIS` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `MATERIAL_RRN` bigint(20) DEFAULT NULL COMMENT '物料主键',
  `name` varchar(64) DEFAULT NULL COMMENT '名称',
  `DESCRIPTION` varchar(64) DEFAULT NULL COMMENT '描述',
  `VERSION` bigint(20) DEFAULT NULL COMMENT '版本',
  `STATUS` varchar(64) DEFAULT NULL COMMENT '状态',
  `ACTIVE_USER` varchar(64) DEFAULT NULL COMMENT '激活人',
  `ACTIVE_TIME` datetime DEFAULT NULL COMMENT '激活时间',
  `STATUS_MODEL_RRN` bigint(20) DEFAULT NULL COMMENT '状态模型主键',
  `MATERIAL_CATEGORY` varchar(64) DEFAULT NULL COMMENT '物料类别',
  `MATERIAL_TYPE` varchar(64) DEFAULT NULL COMMENT '物料类型',
  `STORE_UOM` varchar(64) DEFAULT NULL COMMENT '库存单位',
  `SAFETY_STORE_QTY` bigint(20) DEFAULT NULL COMMENT '安全库存',
  `MAX_STORE_QTY` bigint(20) DEFAULT NULL COMMENT '最大库存',
  `WAREHOUSE_RRN` bigint(20) DEFAULT NULL COMMENT '默认仓库',
  `EFFECTIVE_LIFE` bigint(20) DEFAULT NULL COMMENT '有效时长',
  `WARNING_LIFE` bigint(20) DEFAULT NULL COMMENT '警告时长 当物料批次达到此时长的时候触发警告',
  `EFFECTIVE_UNIT` varchar(32) DEFAULT NULL COMMENT '有效时长单位',
  `DELIVERY_POLICY` varchar(64) DEFAULT NULL COMMENT '物料出库策略',
  `RESERVED1` varchar(64) DEFAULT NULL COMMENT '预留栏位1',
  `RESERVED2` varchar(64) DEFAULT NULL COMMENT '预留栏位2',
  `RESERVED3` varchar(64) DEFAULT NULL COMMENT '预留栏位3',
  `RESERVED4` varchar(64) DEFAULT NULL COMMENT '预留栏位4',
  `RESERVED5` varchar(64) DEFAULT NULL COMMENT '预留栏位5',
  `RESERVED6` varchar(64) DEFAULT NULL COMMENT '预留栏位6',
  `RESERVED7` varchar(64) DEFAULT NULL COMMENT '预留栏位7',
  `RESERVED8` varchar(64) DEFAULT NULL COMMENT '预留栏位8',
  `RESERVED9` varchar(64) DEFAULT NULL COMMENT '预留栏位9',
  `RESERVED10` varchar(64) DEFAULT NULL COMMENT '预留栏位10',
  `HISTORY_SEQ` varchar(64) DEFAULT NULL COMMENT '历史序列号',
  `TRANS_TYPE` varchar(64) DEFAULT NULL COMMENT '操作类型',
  `ACTION_CODE` varchar(64) DEFAULT NULL COMMENT '动作码',
  `ACTION_REASON` varchar(64) DEFAULT NULL COMMENT '原因',
  `ACTION_COMMENT` varchar(64) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`object_rrn`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='Material History Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MMS_MATERIAL_HIS`
--

LOCK TABLES `MMS_MATERIAL_HIS` WRITE;
/*!40000 ALTER TABLE `MMS_MATERIAL_HIS` DISABLE KEYS */;
INSERT INTO `MMS_MATERIAL_HIS` VALUES (1,'Y',1,'2019-03-07 15:18:36','admin','2019-03-07 15:18:36','admin',1,2,'1070100010','测试物料',1,'Active',NULL,NULL,1,'Material',NULL,'个',NULL,NULL,1,10,8,'Hour',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'f19846ff-7f91-4b6c-9894-cf76cde4e981','CreateAndActive',NULL,NULL,NULL),(2,'Y',1,'2019-03-28 17:02:51','admin','2019-03-28 17:02:51','admin',1,3,'107EXP','DG',1,'Active','admin','2019-03-28 17:02:51',2,'Material',NULL,NULL,NULL,NULL,1,5,1,'Hour',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'0af08b0f-c537-4829-909b-c51ad1dffcac','CreateAndActive',NULL,NULL,NULL),(3,'Y',1,'2019-03-29 18:50:47','admin','2019-03-29 18:50:47','admin',2,3,'107EXP','DG',1,'Active','admin','2019-03-28 17:02:51',1,'Material',NULL,NULL,NULL,NULL,1,5,1,'Hour',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'b672309e-c0cf-46a1-819a-e976185d4ec8','Update',NULL,NULL,NULL);
/*!40000 ALTER TABLE `MMS_MATERIAL_HIS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MMS_MATERIAL_LOT`
--

DROP TABLE IF EXISTS `MMS_MATERIAL_LOT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MMS_MATERIAL_LOT` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `MATERIAL_LOT_ID` varchar(64) DEFAULT NULL COMMENT '物料批次号',
  `GRADE` varchar(64) DEFAULT NULL COMMENT '等级',
  `RECEIVE_QTY` bigint(20) DEFAULT NULL COMMENT '接收数量',
  `CURRENT_QTY` bigint(20) DEFAULT NULL COMMENT '当前数量',
  `RESERVED_QTY` bigint(20) DEFAULT NULL COMMENT '预留数量',
  `STATUS_MODEL_RRN` bigint(20) DEFAULT NULL COMMENT '状态模型主键',
  `STATUS_CATEGORY` varchar(64) DEFAULT NULL COMMENT '状态大类',
  `STATUS` varchar(64) DEFAULT NULL COMMENT '状态',
  `PRE_STATUS_CATEGORY` varchar(64) DEFAULT NULL COMMENT '前置状态大类',
  `PRE_STATUS` varchar(64) DEFAULT NULL COMMENT '前置状态',
  `HOLD_STATE` varchar(64) DEFAULT NULL COMMENT 'Hold状态',
  `PARENT_MATERIAL_LOT_ID` varchar(64) DEFAULT NULL COMMENT '母批的批号',
  `PARENT_MATERIAL_LOT_RRN` bigint(20) DEFAULT NULL COMMENT '母批的批次主键',
  `SUB_MATERIAL_LOT_FLAG` varchar(1) DEFAULT NULL COMMENT '子批标志',
  `PACKED_FLAG` varchar(1) DEFAULT NULL COMMENT '被包装标志',
  `WORK_ORDER_ID` varchar(64) DEFAULT NULL COMMENT '工单号',
  `RECEIVE_DATE` datetime DEFAULT NULL COMMENT '接收日期',
  `MATERIAL_RRN` bigint(20) DEFAULT NULL COMMENT '物料主键',
  `MATERIAL_NAME` varchar(64) DEFAULT NULL COMMENT '物料名称',
  `MATERIAL_VERSION` bigint(20) DEFAULT NULL COMMENT '物料版本',
  `MATERIAL_DESC` varchar(64) DEFAULT NULL COMMENT '物料描述',
  `MATERIAL_CATEGORY` varchar(64) DEFAULT NULL COMMENT '物料类别',
  `MATERIAL_TYPE` varchar(64) DEFAULT NULL COMMENT '物料类型',
  `STORE_UOM` varchar(64) DEFAULT NULL COMMENT '库存单位',
  `EFFECTIVE_LIFE` bigint(20) DEFAULT NULL COMMENT '有效时长',
  `WARNING_LIFE` bigint(20) DEFAULT NULL COMMENT '警告时长 当物料批次达到此时长的时候触发警告',
  `EFFECTIVE_UNIT` varchar(32) DEFAULT NULL COMMENT '有效时长单位',
  `RESERVED1` varchar(64) DEFAULT NULL COMMENT '预留栏位1',
  `RESERVED2` varchar(64) DEFAULT NULL COMMENT '预留栏位2',
  `RESERVED3` varchar(64) DEFAULT NULL COMMENT '预留栏位3',
  `RESERVED4` varchar(64) DEFAULT NULL COMMENT '预留栏位4',
  `RESERVED5` varchar(64) DEFAULT NULL COMMENT '预留栏位5',
  `RESERVED6` varchar(64) DEFAULT NULL COMMENT '预留栏位6',
  `RESERVED7` varchar(64) DEFAULT NULL COMMENT '预留栏位7',
  `RESERVED8` varchar(64) DEFAULT NULL COMMENT '预留栏位8',
  `RESERVED9` varchar(64) DEFAULT NULL COMMENT '预留栏位9',
  `RESERVED10` varchar(64) DEFAULT NULL COMMENT '预留栏位10',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_MMS_MATERIAL_LOT_ORG_RRN_MATERIAL_LOT_ID` (`org_rrn`,`MATERIAL_LOT_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='Material Lot Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MMS_MATERIAL_LOT`
--

LOCK TABLES `MMS_MATERIAL_LOT` WRITE;
/*!40000 ALTER TABLE `MMS_MATERIAL_LOT` DISABLE KEYS */;
INSERT INTO `MMS_MATERIAL_LOT` VALUES (3,'Y',1,'2019-04-03 10:29:03','admin','2019-04-12 11:52:54','admin',12,'03320003',NULL,111,100,0,1,'Use','Wait','Use','Wait','Off',NULL,NULL,NULL,NULL,NULL,'2019-04-03 10:29:03',2,'1070100010',1,'测试物料','Material',NULL,'个',10,8,'Hour',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(4,'Y',1,'2019-04-03 10:55:33','admin','2019-04-03 10:55:33','admin',2,'03320004','A',1,1,0,1,'Receive','Receive','Create','Create','Off',NULL,NULL,NULL,NULL,NULL,'2019-04-03 10:55:33',2,'1070100010',1,'测试物料','Material',NULL,'个',10,8,'Hour',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(5,'Y',1,'2019-04-03 17:36:21','admin','2019-04-03 17:36:21','admin',2,'03320005','A',1,1,0,1,'Receive','Receive','Create','Create','Off',NULL,NULL,NULL,NULL,NULL,'2019-04-03 17:36:21',2,'1070100010',1,'测试物料','Material',NULL,'个',10,8,'Hour',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `MMS_MATERIAL_LOT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MMS_MATERIAL_LOT_HIS`
--

DROP TABLE IF EXISTS `MMS_MATERIAL_LOT_HIS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MMS_MATERIAL_LOT_HIS` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `MATERIAL_LOT_ID` varchar(64) DEFAULT NULL COMMENT '物料批次号',
  `GRADE` varchar(64) DEFAULT NULL COMMENT '等级',
  `RECEIVE_QTY` bigint(20) DEFAULT NULL COMMENT '接收数量',
  `CURRENT_QTY` bigint(20) DEFAULT NULL COMMENT '当前数量',
  `RESERVED_QTY` bigint(20) DEFAULT NULL COMMENT '预留数量',
  `STATUS_MODEL_RRN` bigint(20) DEFAULT NULL COMMENT '状态模型主键',
  `STATUS_CATEGORY` varchar(64) DEFAULT NULL COMMENT '状态大类',
  `STATUS` varchar(64) DEFAULT NULL COMMENT '状态',
  `PRE_STATUS_CATEGORY` varchar(64) DEFAULT NULL COMMENT '前置状态大类',
  `PRE_STATUS` varchar(64) DEFAULT NULL COMMENT '前置状态',
  `HOLD_STATE` varchar(64) DEFAULT NULL COMMENT 'Hold状态',
  `PARENT_MATERIAL_LOT_ID` varchar(64) DEFAULT NULL COMMENT '母批的批号',
  `PARENT_MATERIAL_LOT_RRN` bigint(20) DEFAULT NULL COMMENT '母批的批次主键',
  `SUB_MATERIAL_LOT_FLAG` varchar(1) DEFAULT NULL COMMENT '子批标志',
  `PACKED_FLAG` varchar(1) DEFAULT NULL COMMENT '被包装标志',
  `WORK_ORDER_ID` varchar(64) DEFAULT NULL COMMENT '工单号',
  `RECEIVE_DATE` datetime DEFAULT NULL COMMENT '接收日期',
  `MATERIAL_RRN` bigint(20) DEFAULT NULL COMMENT '物料主键',
  `MATERIAL_NAME` varchar(64) DEFAULT NULL COMMENT '物料名称',
  `MATERIAL_VERSION` bigint(20) DEFAULT NULL COMMENT '物料版本',
  `MATERIAL_DESC` varchar(64) DEFAULT NULL COMMENT '物料描述',
  `MATERIAL_CATEGORY` varchar(64) DEFAULT NULL COMMENT '物料类别',
  `MATERIAL_TYPE` varchar(64) DEFAULT NULL COMMENT '物料类型',
  `STORE_UOM` varchar(64) DEFAULT NULL COMMENT '库存单位',
  `EFFECTIVE_LIFE` bigint(20) DEFAULT NULL COMMENT '有效时长',
  `WARNING_LIFE` bigint(20) DEFAULT NULL COMMENT '警告时长 当物料批次达到此时长的时候触发警告',
  `EFFECTIVE_UNIT` varchar(32) DEFAULT NULL COMMENT '有效时长单位',
  `HISTORY_SEQ` varchar(64) DEFAULT NULL COMMENT '历史序列号',
  `TRANS_TYPE` varchar(64) DEFAULT NULL COMMENT '事务类型',
  `TRANS_QTY` bigint(20) DEFAULT NULL COMMENT '操作数量',
  `TRANS_WAREHOUSE_ID` varchar(64) DEFAULT NULL COMMENT '操作仓库',
  `TRANS_STORAGE_TYPE` varchar(64) DEFAULT NULL COMMENT '操作库位类型',
  `TRANS_STORAGE_ID` varchar(64) DEFAULT NULL COMMENT '操作库位号',
  `TARGET_WAREHOUSE_ID` varchar(64) DEFAULT NULL COMMENT '目标仓库',
  `TARGET_STORAGE_TYPE` varchar(64) DEFAULT NULL COMMENT '目标库位类型',
  `TARGET_STORAGE_ID` varchar(64) DEFAULT NULL COMMENT '目标库位号',
  `ACTION_CODE` varchar(64) DEFAULT NULL COMMENT '动作码',
  `ACTION_REASON` varchar(64) DEFAULT NULL COMMENT '原因',
  `ACTION_COMMENT` varchar(64) DEFAULT NULL COMMENT '备注',
  `RESERVED1` varchar(64) DEFAULT NULL COMMENT '预留栏位1',
  `RESERVED2` varchar(64) DEFAULT NULL COMMENT '预留栏位2',
  `RESERVED3` varchar(64) DEFAULT NULL COMMENT '预留栏位3',
  `RESERVED4` varchar(64) DEFAULT NULL COMMENT '预留栏位4',
  `RESERVED5` varchar(64) DEFAULT NULL COMMENT '预留栏位5',
  `RESERVED6` varchar(64) DEFAULT NULL COMMENT '预留栏位6',
  `RESERVED7` varchar(64) DEFAULT NULL COMMENT '预留栏位7',
  `RESERVED8` varchar(64) DEFAULT NULL COMMENT '预留栏位8',
  `RESERVED9` varchar(64) DEFAULT NULL COMMENT '预留栏位9',
  `RESERVED10` varchar(64) DEFAULT NULL COMMENT '预留栏位10',
  PRIMARY KEY (`object_rrn`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COMMENT='Material Lot History Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MMS_MATERIAL_LOT_HIS`
--

LOCK TABLES `MMS_MATERIAL_LOT_HIS` WRITE;
/*!40000 ALTER TABLE `MMS_MATERIAL_LOT_HIS` DISABLE KEYS */;
INSERT INTO `MMS_MATERIAL_LOT_HIS` VALUES (1,'Y',1,'2019-04-03 10:29:03','admin','2019-04-03 10:29:03','admin',1,'03320003',NULL,111,111,0,1,'Create','Create',NULL,NULL,'Off',NULL,NULL,NULL,NULL,NULL,'2019-04-03 10:29:03',2,'1070100010',1,'测试物料','Material',NULL,'个',10,8,'Hour','7e338f7f-8739-4385-a34b-ba468112d63d','Create',111,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,'Y',1,'2019-04-03 10:29:03','admin','2019-04-03 10:29:03','admin',2,'03320003',NULL,111,111,0,1,'Receive','Receive','Create','Create','Off',NULL,NULL,NULL,NULL,NULL,'2019-04-03 10:29:03',2,'1070100010',1,'测试物料','Material',NULL,'个',10,8,'Hour','7e338f7f-8739-4385-a34b-ba468112d63d','Receive',111,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,'Y',1,'2019-04-03 10:29:03','admin','2019-04-03 10:29:03','admin',3,'03320003',NULL,111,111,0,1,'Stock','In','Receive','Receive','Off',NULL,NULL,NULL,NULL,NULL,'2019-04-03 10:29:03',2,'1070100010',1,'测试物料','Material',NULL,'个',10,8,'Hour','7e338f7f-8739-4385-a34b-ba468112d63d','StockIn',111,NULL,NULL,NULL,'VWarehouse1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(4,'Y',1,'2019-04-03 10:54:42','admin','2019-04-03 10:54:42','admin',4,'03320003',NULL,111,111,0,1,'Use','Wait','Stock','In','Off',NULL,NULL,NULL,NULL,NULL,'2019-04-03 10:29:03',2,'1070100010',1,'测试物料','Material',NULL,'个',10,8,'Hour','922f01b0-5213-4e0d-b03e-68b21119d4ad','Pick',111,'VWarehouse1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(5,'Y',1,'2019-04-03 10:54:51','admin','2019-04-03 10:54:51','admin',6,'03320003',NULL,111,110,0,1,'Use','Wait','Use','Wait','Off',NULL,NULL,NULL,NULL,NULL,'2019-04-03 10:29:03',2,'1070100010',1,'测试物料','Material',NULL,'个',10,8,'Hour','74c81fd7-6cb1-4254-bb32-ad4135fcbfa4','Consume',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(6,'Y',1,'2019-04-03 10:55:08','admin','2019-04-03 10:55:08','admin',7,'03320003',NULL,111,108,0,1,'Use','Wait','Use','Wait','Off',NULL,NULL,NULL,NULL,NULL,'2019-04-03 10:29:03',2,'1070100010',1,'测试物料','Material',NULL,'个',10,8,'Hour','685f6501-69e2-4afc-901a-17000ac9b9fd','Consume',2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(7,'Y',1,'2019-04-03 10:55:13','admin','2019-04-03 10:55:13','admin',8,'03320003',NULL,111,105,0,1,'Use','Wait','Use','Wait','Off',NULL,NULL,NULL,NULL,NULL,'2019-04-03 10:29:03',2,'1070100010',1,'测试物料','Material',NULL,'个',10,8,'Hour','1ac79f5d-614a-4432-8920-66939ff47356','Consume',3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(8,'Y',1,'2019-04-03 10:55:21','admin','2019-04-03 10:55:21','admin',9,'03320003',NULL,111,101,0,1,'Use','Wait','Use','Wait','Off',NULL,NULL,NULL,NULL,NULL,'2019-04-03 10:29:03',2,'1070100010',1,'测试物料','Material',NULL,'个',10,8,'Hour','8087d0af-43f5-4534-8035-e1252273a237','Consume',4,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(9,'Y',1,'2019-04-03 10:55:33','admin','2019-04-03 10:55:33','admin',1,'03320004','A',1,1,0,1,'Create','Create',NULL,NULL,'Off',NULL,NULL,NULL,NULL,NULL,'2019-04-03 10:55:33',2,'1070100010',1,'测试物料','Material',NULL,'个',10,8,'Hour','7e0c58c5-c698-45c5-932a-3eb848d8fd8e','Create',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(10,'Y',1,'2019-04-03 10:55:33','admin','2019-04-03 10:55:33','admin',2,'03320004','A',1,1,0,1,'Receive','Receive','Create','Create','Off',NULL,NULL,NULL,NULL,NULL,'2019-04-03 10:55:33',2,'1070100010',1,'测试物料','Material',NULL,'个',10,8,'Hour','7e0c58c5-c698-45c5-932a-3eb848d8fd8e','Receive',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(11,'Y',1,'2019-04-03 17:36:21','admin','2019-04-03 17:36:21','admin',1,'03320005','A',1,1,0,1,'Create','Create',NULL,NULL,'Off',NULL,NULL,NULL,NULL,NULL,'2019-04-03 17:36:21',2,'1070100010',1,'测试物料','Material',NULL,'个',10,8,'Hour','9a2f825a-44f3-4c3b-b644-10b0c975d2d4','Create',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(12,'Y',1,'2019-04-03 17:36:21','admin','2019-04-03 17:36:21','admin',2,'03320005','A',1,1,0,1,'Receive','Receive','Create','Create','Off',NULL,NULL,NULL,NULL,NULL,'2019-04-03 17:36:21',2,'1070100010',1,'测试物料','Material',NULL,'个',10,8,'Hour','9a2f825a-44f3-4c3b-b644-10b0c975d2d4','Receive',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(13,'Y',1,'2019-04-03 18:03:44','admin','2019-04-03 18:03:44','admin',10,'03320003',NULL,111,100,0,1,'Use','Wait','Use','Wait','Off',NULL,NULL,NULL,NULL,NULL,'2019-04-03 10:29:03',2,'1070100010',1,'测试物料','Material',NULL,'个',10,8,'Hour','0069185c-41ae-4f35-add9-4779ecaa65cd','Consume',1,NULL,NULL,NULL,NULL,NULL,NULL,'Normal',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(14,'Y',1,'2019-04-03 18:04:12','admin','2019-04-03 18:04:12','admin',11,'03320003',NULL,111,99,0,1,'Use','Wait','Use','Wait','Off',NULL,NULL,NULL,NULL,NULL,'2019-04-03 10:29:03',2,'1070100010',1,'测试物料','Material',NULL,'个',10,8,'Hour','1984d5dd-a5ab-4927-b0bb-9c5ee6145a99','Consume',1,NULL,NULL,NULL,NULL,NULL,NULL,'Normal','3','42',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(15,'Y',1,'2019-04-12 11:52:54','admin','2019-04-12 11:52:54','admin',12,'03320003',NULL,111,100,0,1,'Use','Wait','Use','Wait','Off',NULL,NULL,NULL,NULL,NULL,'2019-04-03 10:29:03',2,'1070100010',1,'测试物料','Material',NULL,'个',10,8,'Hour','a0724294-87e6-4edc-95d9-e91351eba090','Consume',-1,NULL,NULL,NULL,NULL,NULL,NULL,'Normal',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `MMS_MATERIAL_LOT_HIS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MMS_MATERIAL_LOT_INVENTORY`
--

DROP TABLE IF EXISTS `MMS_MATERIAL_LOT_INVENTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MMS_MATERIAL_LOT_INVENTORY` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `MATERIAL_LOT_RRN` bigint(20) DEFAULT NULL COMMENT '物料批次主键',
  `MATERIAL_LOT_ID` varchar(64) DEFAULT NULL COMMENT '物料批次号',
  `MATERIAL_NAME` varchar(64) DEFAULT NULL COMMENT '物料名称',
  `MATERIAL_DESC` varchar(64) DEFAULT NULL COMMENT '物料描述',
  `WAREHOUSE_RRN` bigint(20) DEFAULT NULL COMMENT '仓库主键',
  `WAREHOUSE_ID` varchar(64) DEFAULT NULL COMMENT '仓库号',
  `STORAGE_RRN` bigint(20) DEFAULT NULL COMMENT '库位主键',
  `STORAGE_TYPE` bigint(20) DEFAULT NULL COMMENT '库位类型',
  `STORAGE_ID` bigint(20) DEFAULT NULL COMMENT '库位号',
  `STOCK_QTY` bigint(20) DEFAULT NULL COMMENT '库存数',
  PRIMARY KEY (`object_rrn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Material Lot inventory Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MMS_MATERIAL_LOT_INVENTORY`
--

LOCK TABLES `MMS_MATERIAL_LOT_INVENTORY` WRITE;
/*!40000 ALTER TABLE `MMS_MATERIAL_LOT_INVENTORY` DISABLE KEYS */;
/*!40000 ALTER TABLE `MMS_MATERIAL_LOT_INVENTORY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MMS_PACKAGE_TYPE`
--

DROP TABLE IF EXISTS `MMS_PACKAGE_TYPE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MMS_PACKAGE_TYPE` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `name` varchar(64) DEFAULT NULL COMMENT '名称',
  `DESCRIPTION` varchar(64) DEFAULT NULL COMMENT '描述',
  `CLASS` varchar(64) DEFAULT NULL COMMENT '类别',
  `PACK_ID_RULE` varchar(64) DEFAULT NULL COMMENT '包装选用的ID生成规则',
  `SOURCE_MATERIAL_TYPE` varchar(64) DEFAULT NULL COMMENT '源物料类型',
  `PACKED_COUNT_TYPE` varchar(64) DEFAULT NULL COMMENT '包装后批次的数量计数类型',
  `MAX_QTY_COUNT_TYPE` varchar(64) DEFAULT NULL COMMENT '包装前的物料批次数量计数类型',
  `MAX_QTY` varchar(64) DEFAULT NULL COMMENT '包装的最大数量',
  `RESERVED1` varchar(64) DEFAULT NULL COMMENT '预留栏位1',
  `RESERVED2` varchar(64) DEFAULT NULL COMMENT '预留栏位2',
  `RESERVED3` varchar(64) DEFAULT NULL COMMENT '预留栏位3',
  `RESERVED4` varchar(64) DEFAULT NULL COMMENT '预留栏位4',
  `RESERVED5` varchar(64) DEFAULT NULL COMMENT '预留栏位5',
  `RESERVED6` varchar(64) DEFAULT NULL COMMENT '预留栏位6',
  `RESERVED7` varchar(64) DEFAULT NULL COMMENT '预留栏位7',
  `RESERVED8` varchar(64) DEFAULT NULL COMMENT '预留栏位8',
  `RESERVED9` varchar(64) DEFAULT NULL COMMENT '预留栏位9',
  `RESERVED10` varchar(64) DEFAULT NULL COMMENT '预留栏位10',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_PACKAGE_TYPE_ORG_RRN_NAME_CLASS` (`org_rrn`,`name`,`CLASS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Package Type Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MMS_PACKAGE_TYPE`
--

LOCK TABLES `MMS_PACKAGE_TYPE` WRITE;
/*!40000 ALTER TABLE `MMS_PACKAGE_TYPE` DISABLE KEYS */;
/*!40000 ALTER TABLE `MMS_PACKAGE_TYPE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MMS_WAREHOUSE`
--

DROP TABLE IF EXISTS `MMS_WAREHOUSE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MMS_WAREHOUSE` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `NAME` varchar(64) DEFAULT NULL COMMENT '仓库号',
  `DESCRIPTION` varchar(64) DEFAULT NULL COMMENT '描述',
  `WAREHOUSE_TYPE` bigint(20) DEFAULT NULL COMMENT '仓库类型',
  `WAREHOUSE_GROUP` bigint(20) DEFAULT NULL COMMENT '仓库组',
  `VIRTUAL_FLAG` varchar(1) DEFAULT NULL COMMENT '是否是虚拟仓库',
  `TRANSFER_DATA_FLAG` varchar(1) DEFAULT NULL COMMENT '是否发送数据给其他系统 比如WMS。ERP等等',
  `RESERVED1` varchar(64) DEFAULT NULL COMMENT '预留栏位1',
  `RESERVED2` varchar(64) DEFAULT NULL COMMENT '预留栏位2',
  `RESERVED3` varchar(64) DEFAULT NULL COMMENT '预留栏位3',
  `RESERVED4` varchar(64) DEFAULT NULL COMMENT '预留栏位4',
  `RESERVED5` varchar(64) DEFAULT NULL COMMENT '预留栏位5',
  `RESERVED6` varchar(64) DEFAULT NULL COMMENT '预留栏位6',
  `RESERVED7` varchar(64) DEFAULT NULL COMMENT '预留栏位7',
  `RESERVED8` varchar(64) DEFAULT NULL COMMENT '预留栏位8',
  `RESERVED9` varchar(64) DEFAULT NULL COMMENT '预留栏位9',
  `RESERVED10` varchar(64) DEFAULT NULL COMMENT '预留栏位10',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_MMS_WAREHOUSE_ORG_RRN_NAME` (`org_rrn`,`NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='Warehouse Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MMS_WAREHOUSE`
--

LOCK TABLES `MMS_WAREHOUSE` WRITE;
/*!40000 ALTER TABLE `MMS_WAREHOUSE` DISABLE KEYS */;
INSERT INTO `MMS_WAREHOUSE` VALUES (1,'Y',1,'2019-03-02 12:07:56','admin','2019-03-02 12:07:56',NULL,1,'VWarehouse1','虚拟仓库1',NULL,NULL,'Y','N',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,'Y',1,'2019-03-26 17:56:25','admin','2019-03-26 17:56:25',NULL,1,'VWarehouse2','虚拟仓库2',NULL,NULL,'Y','N',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `MMS_WAREHOUSE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NB_AUTHORITY`
--

DROP TABLE IF EXISTS `NB_AUTHORITY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8 COMMENT='Authority Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NB_AUTHORITY`
--

LOCK TABLES `NB_AUTHORITY` WRITE;
/*!40000 ALTER TABLE `NB_AUTHORITY` DISABLE KEYS */;
INSERT INTO `NB_AUTHORITY` VALUES (1,'Y',0,'SystemManager','系统管理',NULL,'M','Framework','setting','System Manager','系统管理',NULL,10,NULL,'/System'),(3,'Y',0,'SecurityManager','安全管理',NULL,'M','Framework','lock','Security Manager','安全管理',NULL,20,NULL,'/Security'),(4,'Y',0,'DynamicTableManager','动态表管理',1,'M','Framework','table','Online Table Manager','Online表单管理',NULL,10,2,'/System/OnlineTableManager'),(5,'Y',0,'DynamicFieldManager','动态栏位管理',1,'M','Framework','qrcode','Online Field Manager','Online栏位管理',NULL,20,66,'/System/OnlineFieldManager'),(6,'Y',0,'DynamicTabManager','动态Tab管理',1,'M','Framework','switcher','Online Tab Manager','Online Tab管理',NULL,30,65,'/System/OnlineTabManager'),(7,'Y',0,'DynamicReferenceTableManager','动态栏位参考表管理',1,'M','Framework','table','Online Ref Table Manager','Online 栏位参考表管理',NULL,40,68,'/System/OnlineRefTableManager'),(8,'Y',0,'SysRefNameManager','系统栏位参考名称管理',1,'M','Framework','global','Sys Ref Name Manager','系统参考名称管理',NULL,50,67,'/System/SysRefNameManager'),(10,'Y',0,'OrgRefNameManager','区域栏位参考名称管理',1,'M','Framework','gold','Org Ref Name Manager','区域参考名称管理',NULL,70,70,'/System/OrgRefNameManager'),(14,'Y',0,'RMSManager','RMS管理',NULL,'M','RMS','home','Recipe Manager','RMS管理',NULL,30,NULL,'/Rms'),(15,'Y',0,'EquipmentManager','设备管理',14,'M','RMS','icon-shebei','Equipment Manager','设备管理',NULL,10,75,'/Rms/EquipmentManager'),(16,'Y',0,'RecipeManager','Recipe管理',14,'M','RMS','icon-recipe','Recipe Manager','Recipe管理',NULL,20,76,'/Rms/RecipeManager'),(17,'Y',0,'EquipmentRecipeManager','设备Recipe管理',14,'M','RMS','icon-shebeicanshu','Equipment Recipe Manager','设备Recipe管理',NULL,30,NULL,'/Rms/EquipmentRecipe'),(19,'Y',0,'UserManager','用户管理',3,'M','Framework','user','User Manager','用户管理',NULL,10,72,'/Security/UserManager'),(20,'Y',0,'RoleManager','角色管理',3,'M','Framework','team','Role Manager','角色管理',NULL,20,74,'/Security/RoleManager'),(21,'Y',0,'ErrorCodeManager','异常码管理',1,'M','Framework','message','ErrorCodeManager','异常码管理',NULL,80,73,'/System/MessageManager'),(23,'Y',0,'MonitoringManager','性能监控管理',NULL,'M','Framework','setting','Monitoring Manager','监控管理',NULL,200,NULL,'/Monitoring'),(24,'Y',0,'MaterialManager','物料管理',NULL,'M','MMS','icon-wuliao','Material Manager','物料管理',NULL,50,NULL,'/MMS'),(25,'Y',0,'LabelManager','标签管理',NULL,'M','LMS','icon-yiweima','LabelManager','标签管理',NULL,25,NULL,'/LMS'),(26,'Y',0,'IDGeneratorManager','ID生成规则管理',25,'M','LMS','icon-guize','IDGernerator','ID生成规则',NULL,10,77,'/LMS/IDGeneratorRuleManager'),(27,'Y',0,'LabelTemplateManager','标签模板管理',25,'M','LMS','icon-biaoqian','LblTemplateManager','标签模板管理',NULL,10,77,'/LMS/LabelTemplateManager'),(28,'Y',0,'MaterialSMManager','物料状态模型管理',24,'M','MMS','icon-wuliao','StatusModelManager','物料状态模型管理',NULL,10,86,'/MMS/StatusModelManager'),(29,'Y',0,'MaterialStateCategoryManager','物料状态大类管理',24,'M','MMS','icon-wuliao','StatusCategoryManager','物料状态大类管理',NULL,1,84,'/MMS/StatusCategoryManager'),(30,'Y',0,'MaterialStateManager','物料状态管理',24,'M','MMS','icon-wuliao','StatusManager','物料状态管理',NULL,2,83,'/MMS/StatusManager'),(31,'Y',0,'MaterialEventManager','物料事件管理',24,'M','MMS','icon-wuliao','EventManager','物料事件管理',NULL,3,85,'/MMS/EventManager'),(32,'Y',0,'RawMaterialManager','物料管理',24,'M','MMS','icon-wuliao','MaterialManager','物料管理',NULL,20,87,'/MMS/RawMaterialManager'),(33,'Y',0,'WarehouseManager','仓库管理',NULL,'M','MMS','icon-warehouse','Warehouse Manager','仓库管理',NULL,60,NULL,'/WMS'),(34,'Y',0,'WarehouseDefinition','仓库管理',33,'M','MMS','icon-warehouse','Warehouse Manager','仓库管理',NULL,10,89,'/WMS/WarehouseManager'),(35,'Y',0,'MaterialLotManager','物料批次管理',24,'M','MMS','icon-wuliao','MaterialLotManager','物料批次管理',NULL,20,92,'/MMS/MaterialLotManager'),(36,'Y',0,'MaterialLotInventoryManager','库存查询',33,'M','MMS','icon-kuaixiaoyidongduanicon-','MLotInventory Manager','库存管理',NULL,10,93,'/WMS/MLotInventoryManager'),(37,'Y',0,'MaterialLotHistoryManager','物料批次历史查询',24,'M','MMS','icon-history','MLotHistoryManager','物料批次历史管理',NULL,10,98,'/MMS/MaterialLotHistoryManager');
/*!40000 ALTER TABLE `NB_AUTHORITY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NB_FIELD`
--

DROP TABLE IF EXISTS `NB_FIELD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  `REF_LIST_NAME` varchar(32) DEFAULT NULL COMMENT '栏位所对应的系统参考值',
  `REFERENCE_RULE` varchar(32) DEFAULT NULL COMMENT '栏位的参考规则',
  `DEFAULT_VALUE` varchar(32) DEFAULT NULL COMMENT '默认值',
  `DISPLAY_FLAG` varchar(1) DEFAULT NULL COMMENT '栏位是否显示',
  `BASIC_FLAG` varchar(1) DEFAULT NULL COMMENT '栏位是否显示在基本信息中',
  `MAIN_FLAG` varchar(1) DEFAULT NULL COMMENT '栏位是否在表格中显示',
  `READONLY_FLAG` varchar(1) DEFAULT NULL COMMENT '栏位是否是只读',
  `REQUIRED_FLAG` varchar(1) DEFAULT NULL COMMENT '是否必输',
  `UPPER_FLAG` varchar(1) DEFAULT NULL COMMENT '是否转大写',
  `FROM_PARENT` varchar(1) DEFAULT NULL COMMENT '从父对象上取值 父对象必须为对象的field栏位中体现如user.name',
  `QUERY_FLAG` varchar(1) DEFAULT NULL COMMENT '是否是查询栏位',
  `ALL_LINE` varchar(1) DEFAULT NULL COMMENT '是否整行显示',
  `EDITABLE` varchar(1) DEFAULT NULL COMMENT '保存之后是否可编辑',
  `LABEL` varchar(64) DEFAULT NULL COMMENT '英文标签',
  `LABEL_ZH` varchar(64) DEFAULT NULL COMMENT '中文标签',
  `LABEL_RES` varchar(64) DEFAULT NULL COMMENT '其他语言标签',
  `REF_TABLE_NAME` varchar(64) DEFAULT NULL,
  `DISPLAY_LENGTH` bigint(20) DEFAULT '200' COMMENT '长度',
  `QUERY_REQUIRE_FLAG` varchar(1) DEFAULT 'N' COMMENT '查询必须',
  `negative_flag` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_FILED_TABLE_RRN_NAME` (`TABLE_RRN`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=229 DEFAULT CHARSET=utf8 COMMENT='field Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NB_FIELD`
--

LOCK TABLES `NB_FIELD` WRITE;
/*!40000 ALTER TABLE `NB_FIELD` DISABLE KEYS */;
INSERT INTO `NB_FIELD` VALUES (1,'Y',0,'name','名称','NAME',2,NULL,10,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','Y','N','N','Name','名称',NULL,NULL,150,'N',NULL),(2,'Y',0,'description','描述','DESCRIPTION',2,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N',NULL,'N',NULL,'Y','Desc','描述',NULL,NULL,200,'N',NULL),(3,'Y',0,'modelClass','对象全名','MODEL_CLASS',2,1,40,'text',NULL,NULL,NULL,NULL,'','Y','N','Y','N','Y','N','N','N','N','Y','ModelClass','对象全名',NULL,NULL,350,'N',NULL),(4,'Y',0,'tableName','表名','TABLE_NAME',2,1,30,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N','N','Y','TableName','表名',NULL,NULL,170,'N',NULL),(5,'Y',0,'objectRrn','主键','OBJECT_RRN',2,1,5,'text',NULL,NULL,NULL,NULL,NULL,'Y','','N',NULL,NULL,NULL,NULL,'',NULL,'Y','ObjectRrn','主键',NULL,NULL,100,'N',NULL),(6,'Y',0,'whereClause','查询条件','WHERE_CLAUSE',2,1,50,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','N','N','N','N','N','N','N','Y','WhereClause','查询条件',NULL,NULL,200,'N',NULL),(7,'Y',0,'initWhereClause','初始化查询条件','INIT_WHERE_CLAUSE',2,1,60,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','N','N','N','N','N','N','N','Y','InitWhereClause','初始化查询条件',NULL,NULL,200,'N',NULL),(8,'Y',0,'orderBy','排序','ORDER_BY',2,1,70,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','N','N','N','N','N','N','N','Y','OrderBy','排序条件',NULL,NULL,200,'N',NULL),(9,'Y',0,'modelName','类名','MODEL_NAME',2,1,35,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','Y','N','N','N','N','Y','ModelName','类名',NULL,NULL,200,'N',NULL),(10,'Y',0,'name','名称','NAME',66,NULL,10,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y',NULL,'Y',NULL,NULL,'Y',NULL,'N','Name','名称',NULL,NULL,150,'N',NULL),(11,'Y',0,'description','描述','DESCRIPTION',66,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y',NULL,'Y',NULL,NULL,'',NULL,'Y','Desc','描述',NULL,NULL,150,'N',NULL),(12,'Y',0,'seqNo','顺序号','SEQ_NO',66,NULL,30,'int',NULL,NULL,NULL,NULL,NULL,'Y','Y','N',NULL,'',NULL,NULL,'',NULL,'Y','Seq','顺序号',NULL,NULL,100,'N',NULL),(14,'Y',0,'tableRrn','动态表','TABLE_RRN',66,NULL,40,'referenceTable',NULL,NULL,NULL,NULL,NULL,'Y','Y','N','N','Y','N',NULL,'Y',NULL,'Y','TableName','动态表号',NULL,'NBTable',NULL,'N',NULL),(15,'Y',0,'tabRrn','动态Tab','TAB_RRN',66,NULL,50,'referenceTable',NULL,NULL,NULL,NULL,NULL,'Y','Y','N','N','N','N','N','N','N','Y','TabName','动态Tab',NULL,'NBTabByTable',100,'N','N'),(16,'Y',0,'defaultValue','默认值','DEFAULT_VALUE',66,6,190,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N','N','Y','Default','默认值',NULL,NULL,100,'N',NULL),(17,'Y',0,'label','英文','LABEL',66,6,210,'text',NULL,NULL,NULL,NULL,NULL,'Y','','Y',NULL,'Y',NULL,NULL,'',NULL,'Y','English','英文',NULL,NULL,150,'N',NULL),(18,'Y',0,'labelZh','中文','LABEL_ZH',66,6,200,'text',NULL,NULL,NULL,NULL,NULL,'Y','','Y',NULL,'Y',NULL,NULL,'',NULL,'Y','Chinese','中文',NULL,NULL,150,'N',NULL),(19,'Y',0,'displayType','显示类型','DISPLAY_TYPE',66,6,52,'sysRefList',NULL,NULL,'FieldDisplayType',NULL,NULL,'Y','N','Y','N','Y','N','N','N','N','Y','DisplayType','显示类型',NULL,NULL,120,'N',NULL),(21,'Y',0,'columnName','列名','COLUMN_NAME',66,NULL,15,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','N','N','N','N','N','N','N','Y','ColumnName','列名',NULL,NULL,200,'N',NULL),(22,'Y',0,'displayFlag','是否显示','DISPLAY_FLAG',66,4,10,'radio',NULL,NULL,NULL,NULL,'Y','Y','','',NULL,'',NULL,NULL,NULL,NULL,'Y','Display','显示',NULL,NULL,100,'N',NULL),(23,'Y',0,'basicFlag','基础信息','BASIC_FLAG',66,4,20,'radio',NULL,NULL,NULL,NULL,'N','Y',NULL,'',NULL,'',NULL,NULL,'N',NULL,'Y','Basic','基础信息',NULL,NULL,100,'N',NULL),(24,'Y',0,'mainFlag','列表显示','MAIN_FLAG',66,4,40,'radio',NULL,NULL,NULL,NULL,'N','Y',NULL,'',NULL,'',NULL,NULL,NULL,NULL,'Y','Main','列表显示',NULL,NULL,100,'N',NULL),(26,'Y',0,'readonlyFlag','只读','READONLY_FLAG',66,4,50,'radio',NULL,NULL,NULL,NULL,'N','Y',NULL,'',NULL,'',NULL,NULL,NULL,NULL,'Y','Readonly','只读',NULL,NULL,100,'N',NULL),(27,'Y',0,'editable','可编辑','EDITABLE',66,4,60,'radio',NULL,NULL,NULL,NULL,'Y','Y',NULL,NULL,NULL,'',NULL,NULL,NULL,NULL,'Y','Editable','可编辑',NULL,NULL,100,'N',NULL),(28,'Y',0,'upperFlag','自动转换大写','UPPER_FLAG',66,4,70,'radio',NULL,NULL,NULL,NULL,'N','Y',NULL,NULL,NULL,'',NULL,NULL,NULL,NULL,'Y','Upper','大写',NULL,NULL,100,'N',NULL),(29,'Y',0,'requiredFlag','必须','REQUIRE_FLAG',66,4,80,'radio',NULL,NULL,NULL,NULL,'N','Y',NULL,NULL,NULL,'',NULL,NULL,NULL,NULL,'Y','Require','必须',NULL,NULL,100,'N',NULL),(30,'Y',0,'refTableName','参考表','REF_TABLE_NAME',66,6,60,'referenceTable',NULL,NULL,NULL,NULL,NULL,'Y',NULL,'',NULL,'',NULL,NULL,NULL,NULL,'Y','RefTableName','参考表',NULL,'NBRefTable',100,'N',NULL),(31,'Y',0,'refListName','参考值','REF_LIST_NAME',66,6,70,'text',NULL,NULL,NULL,NULL,NULL,'Y',NULL,'',NULL,'',NULL,NULL,NULL,NULL,'Y','RefListName','参考值',NULL,NULL,100,'N',NULL),(33,'Y',0,'queryFlag','查询','QUERY_FLAG',66,5,10,'radio',NULL,NULL,NULL,NULL,NULL,'Y',NULL,NULL,NULL,'',NULL,NULL,NULL,NULL,'Y','Query','查询',NULL,NULL,100,'N',NULL),(34,'Y',0,'queryRequireFlag','查询','QUERY_REQUIRE_FLAG',66,5,20,'radio',NULL,NULL,NULL,NULL,NULL,'Y',NULL,NULL,NULL,'',NULL,NULL,NULL,NULL,'Y','QueryReq','查询必须',NULL,NULL,100,'N',NULL),(35,'Y',0,'name','名称','NAME',65,NULL,10,'text',NULL,NULL,'',NULL,'','Y','Y','Y','N','Y','N',NULL,'Y',NULL,'N','name','名称',NULL,'',200,'N',NULL),(36,'Y',0,'description','描述','DESCRIPTION',65,NULL,20,'text',NULL,NULL,'',NULL,'','Y','Y','Y','N',NULL,'N',NULL,'N',NULL,'Y','Desc','描述',NULL,'',200,'N',NULL),(37,'Y',0,'tableRrn','动态表号','TABLE_RRN',65,NULL,30,'referenceTable',NULL,NULL,NULL,NULL,NULL,'Y','Y','N','N','Y','N',NULL,'Y',NULL,'Y','Table','动态表号',NULL,'NBTable',NULL,'N',NULL),(38,'Y',0,'seqNo','序号','SEQ_NO',65,NULL,40,'int',NULL,NULL,NULL,NULL,NULL,'Y','Y',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Y','seqNo','序号',NULL,NULL,100,NULL,NULL),(39,'Y',0,'tabType','Tab类型','类型',65,8,50,'sysRefList',NULL,NULL,'TabType',NULL,'Field','Y','N','Y','N',NULL,'N',NULL,'N',NULL,'Y','TabType','Tab类型',NULL,NULL,100,'N',NULL),(40,'Y',0,'label','英文','LABEL',65,8,70,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','Y','N',NULL,'N',NULL,'Y','Label','英文',NULL,NULL,200,'N',NULL),(41,'Y',0,'labelZh','中文','LABEL_ZH',65,8,90,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','Y','N',NULL,'N',NULL,'Y','LabelZh','中文',NULL,NULL,200,'N',NULL),(42,'Y',0,'name','名称','NAME',67,NULL,10,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N',NULL,'Y',NULL,'N','name','名称',NULL,NULL,200,'N',NULL),(43,'Y',0,'description','描述','DESCRIPTION',67,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N',NULL,'N',NULL,'Y','Desc','描述',NULL,NULL,200,'N',NULL),(44,'Y',0,'refTableName','参考表','REF_TABLE_NAME',65,8,50,'referenceTable',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N',NULL,'Y','RefTable','动态表',NULL,'NBTableByName',150,'N',NULL),(45,'Y',0,'whereClause','查询条件','WHERE_CLAUSE',65,8,55,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N',NULL,'N',NULL,'Y','WhereClause','查询条件',NULL,NULL,200,'N',NULL),(46,'Y',0,'objectRrn','主键','OBJECT_RRN',65,NULL,1,'text',NULL,NULL,NULL,NULL,NULL,'N','N','N','N','N','N',NULL,'N',NULL,'N','ObjectRrn','主键',NULL,NULL,NULL,'N',NULL),(47,'Y',0,'name','名称','NAME',68,NULL,10,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N',NULL,'Y',NULL,'Y','Name','名称',NULL,NULL,150,'N',NULL),(48,'Y',0,'description','描述','DESCRIPTION',68,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N',NULL,'N',NULL,'Y','Desc','描述',NULL,NULL,200,'N',NULL),(49,'Y',0,'tableRrn','参考表','TABLE_RRN',68,NULL,30,'referenceTable',NULL,NULL,NULL,NULL,NULL,'Y','Y','N','N','Y','N',NULL,'N',NULL,'Y','RefTable','参考表',NULL,'NBTable',NULL,'N',NULL),(50,'Y',0,'keyField','关键字栏位','KEY_FIELD',68,NULL,40,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N',NULL,'N',NULL,'Y','Key','关键字栏位',NULL,NULL,200,'N',NULL),(51,'Y',0,'textField','显示栏位','TEXT_FIELD',68,NULL,50,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N',NULL,'N',NULL,'Y','Text','显示栏位',NULL,NULL,200,'N',NULL),(52,'Y',0,'whereClause','条件语句','WHERE_CLAUSE',68,NULL,70,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N',NULL,'N',NULL,'Y','WhereClause','条件语句',NULL,NULL,300,'N',NULL),(53,'Y',0,'orderBy','排序','ORDER_BY',68,NULL,80,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N',NULL,'N',NULL,'Y','OrderBy','排序',NULL,NULL,200,'N',NULL),(54,'Y',0,'key','关键字','KEY',69,NULL,10,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N',NULL,'N',NULL,'Y','Key','关键字',NULL,NULL,150,'N',NULL),(55,'Y',0,'description','描述','DESCRIPTION',69,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N',NULL,'N',NULL,'Y','Desc','描述',NULL,NULL,300,'N',NULL),(56,'Y',0,'value','显示信息','VALUE',69,NULL,30,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N',NULL,'N',NULL,'Y','value','显示信息',NULL,NULL,300,'N',NULL),(57,'Y',0,'seqNo','序号','SEQ_NO',69,NULL,5,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N',NULL,'N',NULL,'Y','SeqNo','序号',NULL,NULL,100,'N',NULL),(58,'Y',0,'editFlag','编辑','EDIT_FLAG',65,8,65,'radio',NULL,NULL,NULL,NULL,NULL,'Y','N','N','N','N','N',NULL,'N',NULL,'Y','Edit','编辑',NULL,NULL,NULL,'N',NULL),(59,'Y',0,'referenceName','参考名称','REFERENCE_NAME',69,NULL,20,'text',NULL,NULL,NULL,'name',NULL,'N','N','N','N','N','N','Y','N',NULL,'N','RefName','参考名称',NULL,NULL,NULL,'N',NULL),(60,'Y',0,'fromParent','来源于父值','FROM_PARENT',66,4,90,'radio',NULL,NULL,NULL,NULL,NULL,'Y','N','N','N','N','N',NULL,'N',NULL,'Y','FromParent','来源于父值',NULL,NULL,NULL,'N',NULL),(61,'Y',0,'referenceRule','参考规则','REFERENCE_RULE',66,4,100,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','N','N','N','N','N','N',NULL,'Y','RefRule','参考规则',NULL,NULL,NULL,'N',NULL),(62,'Y',0,'name','名称','NAME',70,NULL,10,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','Y',NULL,'Y','name','名称',NULL,NULL,NULL,'N',NULL),(63,'Y',0,'description','描述','DESCRIPTION',70,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N','N','N',NULL,'Y','Desc','描述',NULL,NULL,NULL,'N',NULL),(64,'Y',0,'seqNo','序号','SEQ_NO',71,NULL,5,'int',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','N',NULL,'Y','SeqNo','序号',NULL,NULL,100,'N',NULL),(65,'Y',0,'key','关键字','KEY',71,NULL,10,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','N',NULL,'Y','Key','关键字',NULL,NULL,150,'N',NULL),(66,'Y',0,'description','描述','DESCRIPTION',71,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','N',NULL,'Y','Desc','描述',NULL,NULL,300,'N',NULL),(67,'Y',0,'value','显示值','VALUE',71,NULL,40,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','N',NULL,'Y','Value','显示值',NULL,NULL,300,'N',NULL),(68,'Y',0,'referenceName','参考值','REFERENCE_NAME',71,NULL,50,'text',NULL,NULL,NULL,'name',NULL,'N','N','N','N','N','N','Y','N',NULL,'N','RefName','参考值',NULL,NULL,NULL,'N',NULL),(69,'Y',0,'username','用户名','USERNAME',72,NULL,10,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','Y',NULL,'N','Username','用户名',NULL,NULL,100,'N',NULL),(70,'Y',0,'description','描述','DESCRIPTION',72,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N','N','N',NULL,'Y','Desc','描述',NULL,NULL,200,'N',NULL),(71,'Y',0,'password','密码','PASSWORD',72,12,30,'password',NULL,NULL,NULL,NULL,NULL,'Y','N','N','N','N','N','N','N',NULL,'N','Password','密码',NULL,NULL,100,'N',NULL),(72,'Y',0,'email','邮箱','EMAIL',72,11,40,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N',NULL,'Y','Email','邮箱',NULL,NULL,200,'N',NULL),(73,'Y',0,'phone','电话','PHONE',72,11,50,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N',NULL,'Y','Phone','电话',NULL,NULL,150,'N',NULL),(74,'Y',0,'department','部门','DEPARTMENT',72,11,60,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N',NULL,'Y','Department','部门',NULL,NULL,100,'N',NULL),(75,'Y',0,'pwdChanged','密码修改日期','PWD_CHANGED',72,12,70,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','Y','N','N','N','N',NULL,'N','PwdChanged','密码修改日期',NULL,NULL,200,'N',NULL),(76,'Y',0,'pwdLife','密码周期','PWD_LIFE',72,12,70,'int',NULL,NULL,NULL,NULL,'365','Y','N','N','N','N','N','N','N',NULL,'Y','PwdLife(Day)','密码周期(天)',NULL,NULL,NULL,'N',NULL),(77,'Y',0,'pwdExpiry','密码到期时间','PWD_EXPRITY',72,NULL,80,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','N','N','N','N','N','N',NULL,'Y','PwdExpiry','密码到期时间',NULL,NULL,200,'N',NULL),(78,'Y',0,'pwdWrongCount','密码错误次数','PWD_WRONG_COUNT',72,12,90,'int',NULL,NULL,NULL,NULL,NULL,'Y','N','N','Y','N','N','N','N',NULL,'N','PwdWrongCount','密码错误次数',NULL,NULL,200,'N',NULL),(79,'Y',0,'lastLogon','最后登录时间','LAST_LOGON',72,12,100,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','Y','N','N','N','N',NULL,'N','LastLogon','最后登录时间',NULL,NULL,200,'N',NULL),(80,'Y',0,'keyId','消息码','KEY_ID',73,NULL,10,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N','N','Y',NULL,'Y','Code','消息码',NULL,NULL,300,'N',NULL),(81,'Y',0,'message','英文','MESSAGE',73,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N','N','N',NULL,'Y','Message','英文',NULL,NULL,300,'N',NULL),(82,'Y',0,'messageZh','中文','MESSAGE_ZH',73,NULL,30,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N','N','N',NULL,'Y','Chinese','中文',NULL,NULL,300,'N',NULL),(83,'Y',0,'roleId','名称','ROLE_ID',74,NULL,10,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','Y',NULL,'Y','RoleId','名称',NULL,NULL,200,'N',NULL),(84,'Y',0,'description','描述','DESCRIPTION',74,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N','N','N',NULL,'Y','Desc','描述',NULL,NULL,NULL,'N',NULL),(85,'Y',0,'equipmentId','设备号','EQUIPMENT_ID',75,NULL,10,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','Y','N','Y','EquipmentId','设备号',NULL,NULL,200,'N',NULL),(86,'Y',0,'equipmentType','设备类型','EQUIPMENT_TYPE',75,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N','N','N',NULL,'Y','EquipmentType','设备类型',NULL,NULL,200,'N',NULL),(87,'Y',0,'holdFlag','Hold状态','HOLD_FLAG',75,NULL,30,'radio',NULL,NULL,NULL,NULL,NULL,'Y','Y','N','Y','N','N','N','N','N','N','HoldFlag','Hold状态',NULL,NULL,200,'N',NULL),(88,'Y',0,'communicationFlag','通信状态','COMMUNICATION_FLAG',75,NULL,40,'radio',NULL,NULL,NULL,NULL,NULL,'Y','Y','N','Y','N','N','N','N','N','N','Communication','通信状态',NULL,NULL,200,'N',NULL),(89,'Y',0,'description','描述','DESCRIPTION',75,NULL,15,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N','N','N',NULL,'Y','Desc','描述',NULL,NULL,200,'N',NULL),(90,'Y',0,'name','名称','NAME',76,NULL,10,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','Y','N','Y','Name','名称',NULL,NULL,200,'N',NULL),(91,'Y',0,'description','描述','DESCRIPTION',76,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N','N','N',NULL,'Y','Desc','描述',NULL,NULL,200,'N',NULL),(92,'Y',0,'displayLength','长度','DISPLAY_LENGTH',66,6,175,'int',NULL,NULL,NULL,NULL,'200','Y','N','N','N','N','N','N','N','N','Y','DisplayLength','显示长度',NULL,NULL,200,'N',NULL),(93,'Y',0,'label','英文','LABEL',2,NULL,31,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','N','N','Y','Label','英文',NULL,NULL,150,'N',NULL),(94,'Y',0,'labelZh','中文','LABEL_ZH',2,NULL,31,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','N','N','Y','LabelZh','中文',NULL,NULL,150,'N',NULL),(95,'Y',0,'name','名称','NAME',77,NULL,10,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','Y','N','N','Name','名称',NULL,NULL,200,'N',NULL),(96,'Y',0,'description','描述','DESCRIPTION',77,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','N',NULL,'Y','Desc','描述',NULL,NULL,200,'N',NULL),(97,'Y',0,'seqNo','序号','SEQ_NO',78,NULL,10,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','Y','N','N','N','N',NULL,'N','Seq','序号',NULL,NULL,200,'N',NULL),(98,'Y',0,'dataType','数据类型','DATA_TYPE',78,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','Y','N','N','N','N',NULL,'Y','Type','类型',NULL,NULL,200,'N',NULL),(99,'Y',0,'dataType','数据类型','DATA_TYPE',79,NULL,10,'text',NULL,NULL,NULL,NULL,'F','Y','Y','N','Y','N','N','N','N',NULL,'Y','dataType','数据类型',NULL,NULL,200,'N',NULL),(100,'Y',0,'fixedString','固定字符','FIXED_STRING',79,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','N','N','Y','N','N','N',NULL,'Y','FixedStr','固定字符',NULL,NULL,200,'N',NULL),(101,'Y',0,'ruleRrn','规则主键','RULE_RRN',79,NULL,30,'text',NULL,NULL,NULL,'objectRrn',NULL,'N','N','N','N','N','N','Y','N',NULL,'N','RuleRrn','规则主键',NULL,NULL,200,'N',NULL),(102,'Y',0,'dataType','数据类型','DATA_TYPE',80,NULL,10,'text',NULL,NULL,NULL,NULL,'D','Y','Y','N','Y','Y','N','N','N',NULL,'N','DataType','数据类型',NULL,NULL,200,'N',NULL),(103,'Y',0,'dateFormat','日期类型','DATE_FORMAT',80,NULL,20,'sysRefList',NULL,NULL,'DateType',NULL,NULL,'Y','Y','N','N','Y','N','N','N','N','Y','dateFormat','日期类型',NULL,NULL,200,'N',NULL),(104,'Y',0,'referenceName','格式转换规则','REFERENCE_NAME',80,NULL,30,'userRefList',NULL,NULL,'DateTransferCode',NULL,NULL,'Y','Y','N','N','N','N','N','N',NULL,'Y','FormatCode','格式代码',NULL,NULL,200,'N',NULL),(105,'Y',0,'ruleRrn','规则主键','RULE_RRN',80,NULL,40,'text',NULL,NULL,NULL,'objectRrn',NULL,'N','N','N','N','N','N','Y','N','N','Y','RuleRrn','规则主键',NULL,NULL,200,'N',NULL),(106,'Y',0,'dataType','数据类型','DATA_TYPE',81,NULL,10,'text',NULL,NULL,NULL,NULL,'V','Y','Y','N','Y','Y','N','N','N',NULL,'Y','DataType','数据类型',NULL,NULL,200,'N',NULL),(107,'Y',0,'parameter','参数名称','PARAMETER',81,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','N','N','Y','N','N','N',NULL,'Y','Parameter','参数名称',NULL,NULL,200,'N',NULL),(108,'Y',0,'variableDirection','取值方向','VARIABLE_DIRECTION',81,NULL,30,'sysRefList',NULL,NULL,'VariableDirection',NULL,'Left','Y','Y','N','N','Y','N','N','N','N','Y','Direction','取值方向',NULL,NULL,200,'N',NULL),(109,'Y',0,'startPosition','起始位置','START_POSITION',81,NULL,40,'int',NULL,NULL,NULL,NULL,'1','Y','Y','N','N','Y','N','N','N',NULL,'Y','StartPos','起始位置',NULL,NULL,200,'N',NULL),(110,'Y',0,'length','长度','LENGTH',81,NULL,50,'int',NULL,NULL,NULL,NULL,NULL,'Y','Y','N','N','N','N','N','N',NULL,'Y','length','长度',NULL,NULL,200,'N',NULL),(111,'Y',0,'placeholder','长度不够的时候的占位符','PLACEHOLDER',81,NULL,60,'text',NULL,NULL,NULL,NULL,'0','Y','Y','N','N','N','N','N','N',NULL,'Y','Placeholder','占位符',NULL,NULL,200,'N',NULL),(112,'Y',0,'ruleRrn','规则主键','RULE_RRN',81,NULL,60,'text',NULL,NULL,NULL,'objectRrn',NULL,'N','N','N','N','N','N','Y','N',NULL,'N','RuleRrn','规则主键',NULL,NULL,200,'N',NULL),(113,'Y',0,'ruleRrn','规则主键','RULE_RRN',82,NULL,1,'text',NULL,NULL,NULL,'objectRrn',NULL,'N','N','N','Y','N','N','Y','N',NULL,'Y','规则主键','ruleRrn',NULL,NULL,200,'N',NULL),(114,'Y',0,'dataType','数据类型','DATA_TYPE',82,NULL,10,'text',NULL,NULL,NULL,NULL,'S','Y','Y','N','Y','Y','N','N','N',NULL,'Y','DataType','数据类型',NULL,NULL,200,'N',NULL),(115,'Y',0,'sequenceType','序列类型','SEQUENCE_TYPE',82,NULL,20,'text',NULL,NULL,NULL,NULL,'Digits','Y','Y','N','N','Y','N','N','N',NULL,'Y','SeqType','序列类型',NULL,NULL,200,'N',NULL),(116,'Y',0,'sequenceDirection','序列增长类型','SEQUENCE_DIRECTION',82,NULL,40,'sysRefList',NULL,NULL,'SequenceDirection',NULL,'Up','Y','Y','Y','N','Y','N','N','N','N','Y','SeqDirection','序列增长类型',NULL,NULL,200,'N',NULL),(117,'Y',0,'exclude','排除','EXCLUDE',82,NULL,50,'text',NULL,NULL,'',NULL,NULL,'Y','Y','N','N','N','N','N','N','N','Y','exclude','排除',NULL,NULL,200,'N',NULL),(118,'Y',0,'min','最小值','MIN',82,NULL,70,'int',NULL,NULL,NULL,NULL,NULL,'Y','Y','N','N','N','N','N','N',NULL,'Y','Min','最小值',NULL,NULL,200,'N',NULL),(119,'Y',0,'max','最大值','MAX',82,NULL,80,'int',NULL,NULL,NULL,NULL,NULL,'Y','Y','N','N','N','N','N','N',NULL,'Y','Max','最大值',NULL,NULL,200,'N',NULL),(120,'Y',0,'length','长度','LENGTH',82,NULL,45,'int',NULL,NULL,NULL,NULL,NULL,'Y','Y','N','N','N','N','N','N',NULL,'Y','Length','长度',NULL,NULL,200,'N',NULL),(121,'Y',0,'excludeType','排除类型','EXCLUDE_TYPE',82,NULL,55,'sysRefList',NULL,NULL,'SequenceExcludeType',NULL,NULL,'Y','Y','N','N','N','N','N','N',NULL,'Y','ExcludeType','排除类型',NULL,NULL,200,'N',NULL),(122,'Y',0,'name','名称','NAME',83,NULL,10,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','Y','N','Y','name','名称',NULL,NULL,200,'N',NULL),(123,'Y',0,'description','描述','DESCRIPTION',84,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','N',NULL,'Y','Description','描述',NULL,NULL,200,'N',NULL),(124,'Y',0,'name','名称','NAME',84,NULL,10,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','Y',NULL,'Y','name','名称',NULL,NULL,200,'N',NULL),(125,'Y',0,'description','描述','DESCRIPTION',83,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','N',NULL,'Y','Description','描述',NULL,NULL,200,'N',NULL),(126,'Y',0,'name','名称','NAME',85,NULL,10,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','Y',NULL,'Y','name','名称',NULL,NULL,200,'N',NULL),(127,'Y',0,'description','描述','DESCRIPTION',85,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','N','N','Y','description','描述',NULL,NULL,200,'N',NULL),(128,'Y',0,'name','名称','NAME',86,NULL,10,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','Y',NULL,'Y','name','名称',NULL,NULL,200,'N',NULL),(129,'Y',0,'description','描述','DESCRIPTION',86,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','N',NULL,'Y','description','描述',NULL,NULL,200,'N',NULL),(130,'Y',0,'initialStateCategory','初始状态大类','INITIAL_STATE_CATEGORY',86,NULL,30,'referenceTable',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','N','N','Y','InitStateCategory','初始大类',NULL,'MMStatusCategory',200,'N',NULL),(131,'Y',0,'initialState','初始状态','INITIAL_STATE',86,NULL,40,'referenceTable',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','N','N','Y','InitState','初始状态',NULL,'MMStatus',200,'N',NULL),(132,'Y',0,'name','名称','NAME',87,NULL,10,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','Y',NULL,'Y','DetailInfo','名称',NULL,NULL,200,'Y',NULL),(133,'Y',0,'description','描述','DESCRIPTION',87,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N','N','N',NULL,'Y','Desc','描述',NULL,NULL,200,'N',NULL),(134,'Y',0,'materialCategory','物料类别','MATERIAL_CATEGORY',87,NULL,30,'userRefList',NULL,NULL,'MaterialCategory',NULL,NULL,'Y','Y','Y','N','Y','N','N','N','N','Y','Category','物料类别',NULL,NULL,200,'N',NULL),(135,'Y',0,'materialType','物料类型','MATERIAL_TYPE',87,NULL,40,'userRefList',NULL,NULL,'MaterialType',NULL,NULL,'Y','Y','Y','N','N','N','N','N','N','Y','Type','物料类型',NULL,NULL,200,'N',NULL),(136,'Y',0,'storeUom','库存单位','STORE_UOM',87,NULL,50,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N','N','N',NULL,'Y','StoreUom','库存单位',NULL,NULL,200,'N',NULL),(137,'Y',0,'warehouseRrn','默认仓库','WAREHOUSE_RRN',87,14,60,'referenceTable',NULL,NULL,NULL,NULL,NULL,'Y','N','N','N','N','N','N','N','N','Y','Warehouse','默认仓库',NULL,'MMWarehouse',200,'N',NULL),(138,'Y',0,'deliveryPolicy','出库策略','DELIVERY_POLICY',87,14,70,'sysRefList',NULL,NULL,'DeliveryPolicy',NULL,NULL,'Y','N','N','N','N','N','N','N','N','Y','DeliveryPolicy','出库策略',NULL,NULL,200,'N',NULL),(139,'Y',0,'SafetyStoreQty','安全库存','SAFETY_STORE_QTY',87,14,80,'int',NULL,NULL,NULL,NULL,NULL,'Y','N','N','N','N','N','N','N',NULL,'Y','SSQty','安全库存',NULL,NULL,200,'N',NULL),(140,'Y',0,'maxStoreQty','最大库存','MAX_STORE_QTY',87,NULL,90,'int',NULL,NULL,NULL,NULL,NULL,'Y','N','N','N','N','N','N','N',NULL,'Y','MSQty','最大库存',NULL,NULL,200,'N',NULL),(141,'Y',0,'effectiveLife','有效时长','EFFECTIVE_LIFE',87,14,100,'double',NULL,NULL,NULL,NULL,NULL,'Y','N','N','N','N','N','N','N',NULL,'Y','EffectiveLife','有效时长',NULL,NULL,200,'N',NULL),(142,'Y',0,'warningLife','警告时长','WARNING_LIFE',87,14,110,'int',NULL,NULL,NULL,NULL,NULL,'Y','N','N','N','N','N','N','N','N','Y','WarningLife','警告时长',NULL,NULL,200,'N',NULL),(143,'Y',0,'effectiveUnit','有效时长单位','EFFECTIVE_UNIT',87,14,120,'sysRefList',NULL,NULL,'TimeUnit',NULL,NULL,'Y','N','N','N','N','N','N','N','N','Y','EffectiveUnit','有效时长单位',NULL,NULL,200,'N',NULL),(144,'Y',0,'statusModelRrn','状态模型','STATUS_MODEL_RRN',87,14,45,'referenceTable',NULL,NULL,NULL,NULL,NULL,'Y','Y','N','N','Y','N','N','N','N','Y','StatusModel','状态模型',NULL,'MMStatusModel',200,'N',NULL),(145,'Y',0,'name','名称','NAME',89,NULL,10,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','Y',NULL,'Y','name','名称',NULL,NULL,200,'N',NULL),(146,'Y',0,'description','描述','DESCRIPTION',89,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N','N','N',NULL,'Y','Desc','描述',NULL,NULL,200,'N',NULL),(147,'Y',0,'warehouseType','仓库类型','WAREHOUSE_TYPE',89,NULL,30,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N','N','N',NULL,'Y','WarehouseType','仓库类型',NULL,NULL,200,'N',NULL),(148,'Y',0,'warehouseGroup','仓库组别','WAREHOUSE_GROUP',89,NULL,40,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','N','N','N','N','N','N',NULL,'Y','WarehouseGroup','仓库组别',NULL,NULL,200,'N',NULL),(149,'Y',0,'virtualFlag','虚拟仓库','VIRTUAL_FLAG',89,NULL,50,'radio',NULL,NULL,NULL,NULL,'N','Y','Y','N','N','N','N','N','N','N','Y','Virtual','虚拟仓库',NULL,NULL,200,'N',NULL),(150,'Y',0,'material','物料',NULL,90,NULL,NULL,'text',NULL,NULL,NULL,NULL,NULL,'N','N','N','N','N','N','N','N',NULL,'N','Material','物料',NULL,NULL,200,'N',NULL),(151,'Y',0,'materialName','物料名称',NULL,90,NULL,10,'text',NULL,NULL,NULL,'name',NULL,'Y','Y','N','Y','N','N','Y','N','N','N','MaterialName','物料名称',NULL,NULL,200,'N',NULL),(152,'Y',0,'materialDesc','物料描述','',90,NULL,20,'text',NULL,NULL,NULL,'description',NULL,'Y','Y','N','Y','N','N','Y','N','N','N','materialDesc','物料描述',NULL,NULL,200,'N',NULL),(153,'Y',0,'transQty','数量','',90,NULL,30,'int',NULL,NULL,NULL,NULL,NULL,'Y','Y','N','N','Y','N','N','N','N','N','Qty','数量',NULL,NULL,200,'N',NULL),(154,'Y',0,'targetWarehouseRrn','仓库',NULL,90,NULL,25,'referenceTable',NULL,NULL,NULL,'warehouseRrn',NULL,'Y','Y','N','N','N','N','Y','N','N','Y','Warehouse','仓库',NULL,'MMWarehouse',200,'N',NULL),(155,'Y',0,'checkFlag','检查标记','CHECK_FLAG',91,NULL,10,'sysRefList',NULL,NULL,'EventStatusCheckFlag',NULL,NULL,'Y','Y','Y','N','Y','N','N','N','N','Y','CheckFlag','检查标记',NULL,NULL,100,'N',NULL),(156,'Y',0,'sourceStatusCategory','源状态大类','SOURCE_STATUS_CATEGORY',91,NULL,20,'referenceTable',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','N','N','Y','SourceSC','源状态大类',NULL,'MMStatusCategory',150,'N',NULL),(157,'Y',0,'sourceStatus','源状态','SOURCE_STATUS',91,NULL,30,'referenceTable',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','N','N','Y','SourceStatus','源状态',NULL,'MMStatus',150,'N',NULL),(158,'Y',0,'targetStatusCategory','目标状态大类','TARGET_STATUS_CATEGORY',91,NULL,40,'referenceTable',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','N','N','Y','TargetStatusCategory','目标状态大类',NULL,'MMStatusCategory',150,'N',NULL),(159,'Y',0,'targetStatus','目标状态','TARGET_STATUS',91,NULL,50,'referenceTable',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','N','N','Y','TargetStatus','目标状态',NULL,'MMStatus',150,'N',NULL),(160,'Y',0,'eventRrn','事件主键','EVENT_RRN',91,NULL,5,'text',NULL,NULL,NULL,'objectRrn',NULL,'N','N','N','N','N','N','Y','N',NULL,'N','EventRrn','事件主键',NULL,NULL,200,'N',NULL),(161,'Y',0,'objectRrn','主键','OBJECT_RRN',85,NULL,NULL,'int',NULL,NULL,NULL,NULL,NULL,'N','N','N','N','N','N','N','N',NULL,'N','ObjeceRrn','主键',NULL,NULL,200,'N',NULL),(162,'Y',0,'materialLotId','物料批次号','MATERIAL_LOT_ID',92,NULL,10,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N','N','Y','N','Y','MaterialLotId','物料批次号',NULL,NULL,200,'N',NULL),(163,'Y',0,'currentQty','数量','CURRENT_QTY',92,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','Y','N','N','N','N',NULL,'Y','CurrentQty','数量',NULL,NULL,200,'N',NULL),(164,'Y',0,'statusCategory','状态大类','STATUS_CATEGORY',92,NULL,30,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N',NULL,'Y','StatusCategory','状态大类',NULL,NULL,200,'N',NULL),(165,'Y',0,'status','状态','STATUS',92,NULL,40,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N',NULL,'Y','Status','状态',NULL,NULL,200,'N',NULL),(166,'Y',0,'materialName','物料名称','MATERIAL_NAME',92,NULL,22,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','Y','N','Y','MaterialName','物料名称',NULL,NULL,200,'N',NULL),(167,'Y',0,'materialDesc','物料描述','MATERIAL_DESC',92,NULL,27,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N','N','Y','MaterialDesc','物料描述',NULL,NULL,200,'N',NULL),(168,'Y',0,'receiveQty','接收数量','RECEIVE_QTY',92,NULL,50,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N','N','Y','ReceiveQty','接收数量',NULL,NULL,200,'N',NULL),(169,'Y',0,'receiveDate','接收日期','RECEIVE_DATE',92,NULL,45,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N',NULL,'Y','ReceiveDate','接收日期',NULL,NULL,200,'N',NULL),(170,'Y',0,'materialLotId','物料批次号',NULL,93,NULL,10,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N','N','Y','N','Y','MaterialLotId','物料批次号',NULL,NULL,200,'N',NULL),(171,'Y',0,'materialName','物料名称',NULL,93,NULL,20,'referenceTable',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N','N','Y','N','Y','MaterialName','物料名称',NULL,'MMMaterialByName',200,'N',NULL),(172,'Y',0,'materialDesc','物料描述',NULL,93,NULL,30,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N','N','N',NULL,'Y','MaterialDesc','物料描述',NULL,NULL,200,'N',NULL),(173,'Y',0,'warehouseId','仓库',NULL,93,NULL,40,'referenceTable',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N','N','Y','N','Y','WarehouseId','仓库',NULL,'MMWarehouseByName',200,'N',NULL),(174,'Y',0,'stockQty','库存数量',NULL,93,NULL,100,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N','N','N',NULL,'Y','StockQty','库存数量',NULL,NULL,200,'N',NULL),(175,'Y',0,'materialLotId','物料批次号',NULL,94,NULL,10,'text',NULL,NULL,NULL,'materialLotId',NULL,'Y','Y','N','Y','N','N','Y','N','N','N','MLotId','物料批次号',NULL,NULL,200,'N',NULL),(176,'Y',0,'materialName','物料名称',NULL,94,NULL,20,'text',NULL,NULL,NULL,'materialName',NULL,'Y','Y','N','Y','N','N','Y','N','N','N','MaterialName','物料名称',NULL,NULL,200,'N',NULL),(177,'Y',0,'materialDesc','物料描述',NULL,94,NULL,30,'text',NULL,NULL,NULL,'materialDesc',NULL,'Y','Y','N','Y','N','N','Y','N','N','N','MaterialDesc','物料描述',NULL,NULL,200,'N',NULL),(178,'Y',0,'warehouseId','源仓库',NULL,94,NULL,40,'text',NULL,NULL,NULL,'warehouseId',NULL,'Y','Y','N','Y','N','N','Y','N','N','Y','warehouseId','源仓库',NULL,NULL,200,'N',NULL),(179,'Y',0,'targetWarehouseRrn','目标仓库',NULL,94,NULL,50,'referenceTable',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','Y','N','N','N','N','Y','TargetWarehouseId','目标仓库',NULL,'MMWarehouse',200,'N',NULL),(180,'Y',0,'warehouseRrn','来源仓库主键',NULL,94,NULL,NULL,'text',NULL,NULL,NULL,'warehouseRrn',NULL,'N','N','N','N','N','N','Y','N',NULL,'N','WarehouseRrn','来源仓库主键',NULL,NULL,200,'N',NULL),(181,'Y',0,'stockQty','库存数量',NULL,94,NULL,45,'int',NULL,NULL,NULL,'stockQty',NULL,'Y','Y','N','Y','N','N','Y','N','N','N','StockQty','库存数量',NULL,NULL,200,'N',NULL),(186,'Y',0,'materialLotId','物料批次号',NULL,95,NULL,10,'text',NULL,NULL,NULL,'materialLotId',NULL,'Y','Y','N','Y','N','N','Y','N','N','N','MLotId','物料批次号',NULL,NULL,200,'N',NULL),(187,'Y',0,'materialName','物料名称',NULL,95,NULL,20,'text',NULL,NULL,NULL,'materialName',NULL,'Y','Y','N','Y','N','N','Y','N','N','N','MaterialName','物料名称',NULL,NULL,200,'N',NULL),(188,'Y',0,'materialDesc','物料描述',NULL,95,NULL,30,'text',NULL,NULL,NULL,'materialDesc',NULL,'Y','Y','N','Y','N','N','Y','N','N','N','MaterialDesc','物料描述',NULL,NULL,200,'N',NULL),(189,'Y',0,'warehouseId','仓库',NULL,95,NULL,40,'text',NULL,NULL,NULL,'warehouseId',NULL,'Y','Y','N','Y','N','N','Y','N','N','Y','warehouseId','仓库',NULL,NULL,200,'N',NULL),(190,'Y',0,'warehouseRrn','仓库主键',NULL,95,NULL,NULL,'text',NULL,NULL,NULL,'warehouseRrn',NULL,'N','N','N','N','N','N','Y','N',NULL,'N','WarehouseRrn','来源仓库主键',NULL,NULL,200,'N',NULL),(191,'Y',0,'stockQty','库存数量',NULL,95,NULL,45,'int',NULL,NULL,NULL,'stockQty',NULL,'Y','Y','N','Y','N','N','Y','N','N','N','StockQty','库存数量',NULL,NULL,200,'N',NULL),(192,'Y',0,'actionCode','动作码',NULL,95,16,10,'userRefList',NULL,NULL,'MLotInvCheckCode',NULL,NULL,'Y','N','N','N','N','N','N','N','N','Y','ActionCode','动作码',NULL,NULL,200,'N',NULL),(193,'Y',0,'actionReason','动作原因',NULL,95,16,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','N','N','N','N','N','N','N','Y','ActionReason','动作原因',NULL,NULL,200,'N',NULL),(194,'Y',0,'actionComment','动作备注',NULL,95,16,30,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','N','N','N','N','N','N','N','Y','ActionComment','动作备注',NULL,NULL,200,'N',NULL),(195,'Y',0,'currentQty','当前数量',NULL,95,NULL,110,'int',NULL,NULL,NULL,NULL,NULL,'Y','Y','N','N','N','N','N','N','N','Y','CurrentQty','当前数量',NULL,NULL,200,'N',NULL),(196,'Y',0,'actionCode','动作码',NULL,96,17,10,'userRefList',NULL,NULL,'MLotInvCheckCode',NULL,NULL,'Y','N','N','N','N','N','N','N','N','Y','ActionCode','动作码',NULL,NULL,200,'N',NULL),(197,'Y',0,'actionComment','动作备注',NULL,96,17,30,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','N','N','N','N','N','N','N','Y','ActionComment','动作备注',NULL,NULL,200,'N',NULL),(198,'Y',0,'actionReason','动作原因',NULL,96,17,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','N','N','N','N','N','N','N','Y','ActionReason','动作原因',NULL,NULL,200,'N',NULL),(199,'Y',0,'currentQty','当前数量',NULL,96,NULL,40,'int',NULL,NULL,NULL,'currentQty',NULL,'Y','Y','N','Y','N','N','Y','N','N','Y','CurrentQty','当前数量',NULL,NULL,200,'N',NULL),(200,'Y',0,'materialDesc','物料描述',NULL,96,NULL,30,'text',NULL,NULL,NULL,'materialDesc',NULL,'Y','Y','N','Y','N','N','Y','N','N','N','MaterialDesc','物料描述',NULL,NULL,200,'N',NULL),(201,'Y',0,'materialLotId','物料批次号',NULL,96,NULL,10,'text',NULL,NULL,NULL,'materialLotId',NULL,'Y','Y','N','Y','N','N','Y','N','N','N','MLotId','物料批次号',NULL,NULL,200,'N',NULL),(202,'Y',0,'materialName','物料名称',NULL,96,NULL,20,'text',NULL,NULL,NULL,'materialName',NULL,'Y','Y','N','Y','N','N','Y','N','N','N','MaterialName','物料名称',NULL,NULL,200,'N',NULL),(203,'Y',0,'transQty','消耗数量',NULL,96,NULL,60,'int',NULL,NULL,NULL,NULL,NULL,'Y','Y','N','N','Y','N','N','N','N','Y','ConsumeQty','消耗数量',NULL,NULL,200,'N','Y'),(204,'Y',0,'grade','等级',NULL,90,NULL,40,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','N','N','N','N','N','N',NULL,'Y','Grade','等级',NULL,NULL,200,'N',NULL),(205,'Y',0,'grade','等级',NULL,92,NULL,15,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N','N','N','N','Y','Grade','等级',NULL,NULL,200,'N',NULL),(206,'Y',0,'objectRrn','主键',NULL,77,NULL,0,'text',NULL,NULL,NULL,NULL,NULL,'N','N','N','N','N','N','N','N',NULL,'N','ObjectRrn','主键',NULL,NULL,200,'N',NULL),(207,'Y',0,'seqNo','序号',NULL,79,NULL,30,'text',NULL,NULL,NULL,NULL,NULL,'N','N','N','N','N','N','N','N',NULL,'Y','seqNo','序号',NULL,NULL,200,'N',NULL),(208,'Y',0,'materialLotId','物料批次号','MATERIAL_LOT_ID',98,NULL,1,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N','N','Y','N','Y','MaterialLotId','物料批次号',NULL,NULL,200,'N',NULL),(209,'Y',0,'grade','等级',NULL,98,NULL,15,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','N','N','N','N','N','N','Y','Grade','等级',NULL,NULL,50,'N',NULL),(210,'Y',0,'currentQty','数量','CURRENT_QTY',98,NULL,20,'text',NULL,NULL,NULL,NULL,NULL,'Y','Y','Y','Y','N','N','N','N','N','Y','CurrentQty','当前数量',NULL,NULL,50,'N',NULL),(211,'Y',0,'materialName','物料名称','MATERIAL_NAME',98,NULL,22,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N','N','Y','MaterialName','物料名称',NULL,NULL,200,'N',NULL),(212,'Y',0,'materialDesc','物料描述','MATERIAL_DESC',98,NULL,27,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N','N','Y','MaterialDesc','物料描述',NULL,NULL,200,'N',NULL),(213,'Y',0,'statusCategory','状态大类','STATUS_CATEGORY',98,NULL,30,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N','N','Y','StatusCategory','状态大类',NULL,NULL,70,'N',NULL),(214,'Y',0,'status','状态','STATUS',98,NULL,40,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N','N','Y','Status','状态',NULL,NULL,70,'N',NULL),(217,'Y',0,'created','操作时间',NULL,98,NULL,60,'calendarFromTo',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','Y','N','Y','created','操作时间',NULL,NULL,200,'N',NULL),(218,'Y',0,'createdBy','操作人',NULL,98,NULL,80,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N',NULL,'Y','CreatedBy','操作人',NULL,NULL,200,'N',NULL),(219,'Y',0,'preStatusCategory','前置状态大类',NULL,98,NULL,40,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N','N','Y','PreStatusCategory','前置状态大类',NULL,NULL,130,'N',NULL),(220,'Y',0,'preStatus','前置状态',NULL,98,NULL,50,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N','N','Y','PreStatus','前置状态',NULL,NULL,70,'N',NULL),(221,'Y',0,'transType','事务类型',NULL,98,NULL,90,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N',NULL,'Y','TransType','事务类型',NULL,NULL,80,'N',NULL),(222,'Y',0,'transQty','操作数量',NULL,98,NULL,100,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N',NULL,'Y','TransQty','操作数量',NULL,NULL,50,'N',NULL),(223,'Y',0,'transWarehouseId','源仓库',NULL,98,NULL,100,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N',NULL,'Y','SourceWarehouse','源仓库',NULL,NULL,200,'N',NULL),(224,'Y',0,'targetWarehouseId','目标仓库',NULL,98,NULL,150,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N',NULL,'Y','TargetWarehouse','目标仓库',NULL,NULL,200,'N',NULL),(225,'Y',0,'actionCode','原因码',NULL,98,NULL,200,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N','N','Y','ActionCode','动作码',NULL,NULL,200,'N',NULL),(226,'Y',0,'actionReason','动作原因',NULL,98,NULL,210,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N',NULL,'Y','ActionReason','动作原因',NULL,NULL,200,'N',NULL),(227,'Y',0,'actionComment','动作备注',NULL,98,NULL,220,'text',NULL,NULL,NULL,NULL,NULL,'Y','N','Y','N','N','N','N','N',NULL,'Y','ActionComment','动作备注',NULL,NULL,200,'N',NULL),(228,'Y',0,'negativeFlag','允许负数',NULL,66,6,195,'radio',NULL,NULL,NULL,NULL,NULL,'Y','N','N','N','N','N','N','N','N','Y','Negative','负数',NULL,NULL,200,'N','N');
/*!40000 ALTER TABLE `NB_FIELD` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NB_MESSAGE`
--

DROP TABLE IF EXISTS `NB_MESSAGE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NB_MESSAGE` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `key_id` varchar(64) DEFAULT NULL COMMENT '消息名称',
  `message` varchar(256) DEFAULT NULL COMMENT '英文消息',
  `message_zh` varchar(256) DEFAULT NULL COMMENT '中文消息',
  `message_res` varchar(256) DEFAULT NULL COMMENT '其他消息',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_MESSAGE_ORG_RRN_NAME` (`org_rrn`,`key_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COMMENT='I18n Message Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NB_MESSAGE`
--

LOCK TABLES `NB_MESSAGE` WRITE;
/*!40000 ALTER TABLE `NB_MESSAGE` DISABLE KEYS */;
INSERT INTO `NB_MESSAGE` VALUES (1,'Y',0,'2018-06-06 22:17:21','admin','2018-06-06 22:17:21','admin',1,'common.system_occurred_error','System error. Please call administrator','系统错误, 请联系管理员',NULL),(2,'Y',0,'2018-06-06 22:17:21','admin','2018-06-06 22:17:21','admin',1,'security.user_is_not_found','The user is not found','用户不存在',NULL),(3,'Y',0,'2018-06-06 22:17:21','admin','2018-06-06 22:17:21','admin',1,'security.user_is_not_in_validation','The user is not validation','请先修改密码',NULL),(4,'Y',0,'2018-06-06 22:17:21','admin','2018-06-06 22:17:21','admin',1,'security.wrong_pwd_more_than_count','Too many password errors ','密码错误次数太多，请重置密码',NULL),(5,'Y',0,'2018-06-06 22:17:21','admin','2018-06-06 22:17:21','admin',1,'security.user_pwd_is_incorrect','The pwd is incorrect','密码错误',NULL),(6,'Y',0,'2018-06-06 22:17:21','admin','2018-06-06 22:17:21','admin',1,'security.pwd_expiry','The pwd is expiry','密码过期，请修改密码',NULL),(7,'Y',0,'2018-08-08 16:26:19','admin','2018-08-08 16:26:19','admin',1,'com.generator_rule_is_not_exist','Generator Rule is not exist','生成规则不存在',NULL),(8,'Y',0,'2018-08-08 16:26:19','admin','2018-08-08 16:26:19','admin',1,'com.generator_id_more_than_size','Generator ID is more than size','ID超过长度限制',NULL),(9,'Y',0,'2018-08-08 16:26:19','admin','2018-08-08 16:26:19','admin',1,'com.generator_id_seq_error','Generator ID sequence error','生成ID序列号错误',NULL),(10,'Y',0,'2019-01-11 13:50:54','admin','2019-01-11 13:50:54','admin',1,'common.entity_is_not_newest','Entity is Update by another','记录被其他人更新',NULL),(11,'Y',0,'2019-02-26 15:12:12','admin','2019-02-26 15:12:12',NULL,1,'common.status_is_not_allow','Status is not allow','状态不允许',NULL),(12,'Y',0,'2019-03-06 16:53:07','admin','2019-03-06 16:53:07',NULL,1,' com.sm_event_status_is_not_allow','Event status isn\'t allow','事件状态不允许',NULL),(13,'Y',0,'2019-03-07 10:37:24','admin','2019-03-07 10:37:24',NULL,1,'com.sm_status_model_event_is_not_exist','SM event is not exist','模型事件不存在',NULL),(14,'Y',0,'2019-03-07 10:38:13','admin','2019-03-07 10:55:25','admin',2,'com.sm_event_status_is_not_allow','Event Status is not allow','事件状态不允许',NULL),(15,'Y',0,'2019-04-03 10:49:51','admin','2019-04-03 10:49:51',NULL,1,'mm.material_lot_in_inventory','MLot already in inv.','物料批次还在仓库中',NULL);
/*!40000 ALTER TABLE `NB_MESSAGE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NB_ORG`
--

DROP TABLE IF EXISTS `NB_ORG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NB_ORG` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `name` varchar(32) DEFAULT NULL COMMENT '名称',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_ORG_NAME` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='Org Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NB_ORG`
--

LOCK TABLES `NB_ORG` WRITE;
/*!40000 ALTER TABLE `NB_ORG` DISABLE KEYS */;
INSERT INTO `NB_ORG` VALUES (1,'Y',0,'ZhiXing','智行区域');
/*!40000 ALTER TABLE `NB_ORG` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NB_QUERY`
--

DROP TABLE IF EXISTS `NB_QUERY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NB_QUERY` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `name` varchar(32) DEFAULT NULL COMMENT '名称',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `query_text` varchar(1024) DEFAULT NULL COMMENT '查询的SQL',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_QUERY_ORG_RRN_NAME` (`org_rrn`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='QueryText Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NB_QUERY`
--

LOCK TABLES `NB_QUERY` WRITE;
/*!40000 ALTER TABLE `NB_QUERY` DISABLE KEYS */;
/*!40000 ALTER TABLE `NB_QUERY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NB_REFERENCE_LIST`
--

DROP TABLE IF EXISTS `NB_REFERENCE_LIST`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NB_REFERENCE_LIST` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `REFERENCE_NAME` varchar(64) DEFAULT NULL COMMENT '参考名称',
  `KEY_ID` varchar(256) DEFAULT NULL COMMENT '保存到数据库中的值',
  `DESCRIPTION` varchar(256) DEFAULT NULL COMMENT '描述',
  `VALUE` varchar(256) DEFAULT NULL COMMENT '显示值',
  `SEQ_NO` bigint(20) DEFAULT NULL COMMENT '序号',
  `category` varchar(32) DEFAULT NULL COMMENT '类别',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_REFLIST_REF_NAME_KEY_CATEGROY` (`REFERENCE_NAME`,`KEY_ID`,`category`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8 COMMENT='Reference list Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NB_REFERENCE_LIST`
--

LOCK TABLES `NB_REFERENCE_LIST` WRITE;
/*!40000 ALTER TABLE `NB_REFERENCE_LIST` DISABLE KEYS */;
INSERT INTO `NB_REFERENCE_LIST` VALUES (2,'Y',0,'Language','Chinese','中文','Chinese',10,'System'),(3,'Y',0,'Language','English','英文','English',20,'System'),(4,'Y',0,'FieldDataType','string','字符串','string',10,'System'),(5,'Y',0,'FieldDataType','int','整数','int',20,'System'),(6,'Y',0,'FieldDataType','double','小数','double',30,'System'),(7,'Y',0,'FieldDisplayType','text','文本','text',10,'System'),(8,'Y',0,'FieldDisplayType','password','密码','password',20,'System'),(9,'Y',0,'FieldDisplayType','calendar','日期','calendar',30,'System'),(10,'Y',0,'FieldDisplayType','calendarFromTo','calendarFromTo','calendarFromTo',40,'System'),(11,'Y',0,'FieldDisplayType','datetime','时间','datetime',50,'System'),(13,'Y',0,'FieldDisplayType','sysRefList','系统栏位参考值','sysRefList',70,'System'),(14,'Y',0,'FieldDisplayType','userRefList','用户栏位参考值','userRefList',80,'System'),(15,'Y',0,'FieldDisplayType','referenceTable','系统栏位参考表','referenceTable',90,'System'),(16,'Y',0,'FieldDisplayType','radio','选择框','radio',100,'System'),(17,'Y',0,'TabType','Field','栏位显示','Field',10,'System'),(18,'Y',0,'TabType','Table','表格显示','Table',20,'System'),(29,'Y',1,'FieldDisplayType','int','整数','int',12,'System'),(30,'Y',1,'FieldDisplayType','double','小数','double',13,'System'),(46,'Y',1,'RecipeCategory','Equipment','设备Recipe','Equipment',10,'System'),(55,'Y',1,'DateType','yyyy','年','yyyy',10,'System'),(56,'Y',1,'DateType','yy','取年后2位','yy',20,'System'),(57,'Y',1,'DateType','y','取年的最后一位','y',30,'System'),(58,'Y',1,'DateType','MM','月','MM',40,'System'),(59,'Y',1,'DateType','dd','天','dd',50,'System'),(60,'Y',1,'DateType','ww','周','ww',60,'System'),(61,'Y',1,'DateType','hh','小时','hh',70,'System'),(62,'Y',1,'DateType','ss','秒','ss',90,'System'),(64,'Y',1,'DateType','minute','分钟','minute',80,'System'),(65,'Y',1,'VariableDirection','Left','从左到右','Left',10,'System'),(66,'Y',1,'VariableDirection','Right','从右到左','Right',20,'System'),(67,'Y',1,'SequenceDirection','Up','递增','Up',10,'System'),(68,'Y',1,'SequenceDirection','Down','递减','Down',20,'System'),(69,'Y',1,'SequenceExcludeType','All','全部','All',10,'System'),(70,'Y',1,'SequenceExcludeType','Include','包含','Include',20,'System'),(71,'Y',1,'MaterialCategory','Wafer','Wafer','Wafer',10,'Owner'),(72,'Y',1,'MaterialCategory','Material','Material','Material',20,'Owner'),(73,'Y',1,'DeliveryPolicy','FIFO','先进先出','FIFO',10,'System'),(74,'Y',1,'TimeUnit','Day','天','Day',10,'System'),(75,'Y',1,'TimeUnit','Hour','小时','Hour',20,'System'),(76,'Y',1,'TimeUnit','Minute','分钟','Minute',30,'System'),(77,'Y',1,'EventStatusCheckFlag','Reject','拒绝','Reject',10,'System'),(78,'Y',1,'EventStatusCheckFlag','Allow','允许','Allow',20,'System'),(79,'Y',1,'MLotInvCheckCode','Normal','正常盘点','Normal',10,'Owner'),(80,'Y',1,'PackageCountType','One','包装之后就是数量就是1','One',10,'System'),(81,'Y',1,'PackageCountType','ByLot','物料批次个数来做数量','ByLot',20,'System'),(82,'Y',1,'PackageCountType','ByLotQty','物料批次上数量总和来作为数量','ByLotQty',30,'System');
/*!40000 ALTER TABLE `NB_REFERENCE_LIST` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NB_REFERENCE_NAME`
--

DROP TABLE IF EXISTS `NB_REFERENCE_NAME`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NB_REFERENCE_NAME` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `name` varchar(64) DEFAULT NULL COMMENT '名字',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `category` varchar(32) DEFAULT NULL COMMENT '类别',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_REF_NAME_ORG_RRN_NAME_CATEGORY` (`org_rrn`,`name`,`category`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8 COMMENT='Reference Name Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NB_REFERENCE_NAME`
--

LOCK TABLES `NB_REFERENCE_NAME` WRITE;
/*!40000 ALTER TABLE `NB_REFERENCE_NAME` DISABLE KEYS */;
INSERT INTO `NB_REFERENCE_NAME` VALUES (2,'Y',0,'Language','语言','System'),(3,'Y',0,'FieldDataType','栏位数据类型','System'),(4,'Y',0,'FieldDisplayType','栏位显示类型','System'),(5,'Y',0,'TabType','Tab类型','System'),(13,'Y',1,'RecipeCategory','Recipe类型','System'),(14,'Y',0,'DateType','日期类型','System'),(15,'Y',0,'DateTransferCode','时间类型转换比如1-A','Owner'),(16,'Y',0,'VariableDirection','参数取值方向','System'),(17,'Y',0,'SequenceDirection','序列取值方向','System'),(18,'Y',0,'SequenceExcludeType','序列排除类型','System'),(19,'Y',1,'MaterialCategory','物料类别','Owner'),(20,'Y',1,'MaterialType','物料类型','Owner'),(21,'Y',0,'DeliveryPolicy','出库策略','System'),(22,'Y',0,'TimeUnit','时间单位','System'),(23,'Y',0,'EventStatusCheckFlag','事件检查标记','System'),(24,'Y',1,'MLotInvCheckCode','盘点原因码','Owner'),(25,'Y',0,'PackageCountType','包装计数类型','System');
/*!40000 ALTER TABLE `NB_REFERENCE_NAME` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NB_REFERENCE_TABLE`
--

DROP TABLE IF EXISTS `NB_REFERENCE_TABLE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NB_REFERENCE_TABLE` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `name` varchar(64) DEFAULT NULL COMMENT '名字',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `TABLE_RRN` bigint(20) DEFAULT NULL COMMENT '对应nbTable的主键',
  `KEY_FIELD` varchar(64) DEFAULT NULL COMMENT '参考表所对应Key栏位, 保存到数据库中的值',
  `TEXT_FIELD` varchar(128) DEFAULT NULL COMMENT '参考表所对应Text栏位,显示在页面上的值',
  `WHERE_CLAUSE` varchar(128) DEFAULT NULL COMMENT '查询条件',
  `ORDER_BY` varchar(128) DEFAULT NULL COMMENT '排序条件',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_REF_TABLE_ORG_RRN_NAME` (`org_rrn`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COMMENT='ReferenceTable Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NB_REFERENCE_TABLE`
--

LOCK TABLES `NB_REFERENCE_TABLE` WRITE;
/*!40000 ALTER TABLE `NB_REFERENCE_TABLE` DISABLE KEYS */;
INSERT INTO `NB_REFERENCE_TABLE` VALUES (1,'Y',0,'NBOrg','区域',1,'objectRrn','name',NULL,NULL),(2,'Y',0,'NBTable','动态表',2,'objectRrn','name',NULL,NULL),(3,'Y',0,'NBTabByTable','动态Tab',65,'objectRrn','name','tableRrn = :tableRrn',NULL),(4,'Y',0,'NBRefTable','栏位参考表',68,'name','name',NULL,NULL),(5,'Y',0,'NBTableByName','动态表名称',2,'name','name',NULL,NULL),(6,'Y',0,'MMStatusCategory','物料状态大类',84,'name','name',NULL,NULL),(7,'Y',0,'MMStatus','物料状态',83,'name','name',NULL,NULL),(8,'Y',0,'MMStatusModel','物料状态模型',86,'objectRrn','name',NULL,NULL),(9,'Y',0,'MMWarehouse','仓库',89,'objectRrn','name',NULL,NULL),(10,'Y',0,'MMMaterialByName','根据名称查询物料',87,'name','name',NULL,NULL),(11,'Y',0,'MMWarehouseByName','根据名词查询仓库',89,'name','name',NULL,NULL),(12,'Y',0,'COMIdRuleByName','根据名称获取ID生成规则',77,'name','name',NULL,NULL);
/*!40000 ALTER TABLE `NB_REFERENCE_TABLE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NB_RELATION`
--

DROP TABLE IF EXISTS `NB_RELATION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NB_RELATION` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `relation_type` varchar(32) DEFAULT NULL COMMENT '关系类型 Sql/普通的SQL关联， Class/通过HQL关联',
  `source` varchar(32) DEFAULT NULL COMMENT '源对象',
  `source_relation_field` varchar(32) DEFAULT NULL COMMENT '源对象关联的栏位',
  `target` varchar(256) DEFAULT NULL COMMENT '目标对象',
  `target_relation_field` varchar(1024) DEFAULT NULL COMMENT '目标对象关联的栏位',
  PRIMARY KEY (`object_rrn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Relation Info. 在一些特定的环境下，模拟一些需要关系卡控的。比如在特定的需求下，这个用户组突然要关联一些东西，但是在起初的设计没有设计这块。通过这个实现';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NB_RELATION`
--

LOCK TABLES `NB_RELATION` WRITE;
/*!40000 ALTER TABLE `NB_RELATION` DISABLE KEYS */;
/*!40000 ALTER TABLE `NB_RELATION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NB_ROLE`
--

DROP TABLE IF EXISTS `NB_ROLE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NB_ROLE` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `role_id` varchar(64) DEFAULT NULL COMMENT '角色名称',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_ROLE_ORG_RRN_ROLE_ID` (`org_rrn`,`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='Role Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NB_ROLE`
--

LOCK TABLES `NB_ROLE` WRITE;
/*!40000 ALTER TABLE `NB_ROLE` DISABLE KEYS */;
INSERT INTO `NB_ROLE` VALUES (5,'Y',1,'2018-12-26 13:18:12',NULL,'2019-02-26 19:02:35',NULL,288,'TestRole','测试角色1');
/*!40000 ALTER TABLE `NB_ROLE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NB_ROLE_AUTHORITY`
--

DROP TABLE IF EXISTS `NB_ROLE_AUTHORITY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NB_ROLE_AUTHORITY` (
  `role_rrn` bigint(20) DEFAULT NULL COMMENT '角色主键',
  `authority_rrn` bigint(20) DEFAULT NULL COMMENT '功能主键',
  KEY `fk_role_authority_rolerrn` (`role_rrn`),
  KEY `fk_role_authority_authorityrrn` (`authority_rrn`),
  CONSTRAINT `fk_role_authority_authorityrrn` FOREIGN KEY (`authority_rrn`) REFERENCES `NB_AUTHORITY` (`object_rrn`),
  CONSTRAINT `fk_role_authority_rolerrn` FOREIGN KEY (`role_rrn`) REFERENCES `NB_ROLE` (`object_rrn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Role Authority Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NB_ROLE_AUTHORITY`
--

LOCK TABLES `NB_ROLE_AUTHORITY` WRITE;
/*!40000 ALTER TABLE `NB_ROLE_AUTHORITY` DISABLE KEYS */;
INSERT INTO `NB_ROLE_AUTHORITY` VALUES (5,3),(5,15),(5,16),(5,19),(5,20),(5,14);
/*!40000 ALTER TABLE `NB_ROLE_AUTHORITY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NB_ROLE_HIS`
--

DROP TABLE IF EXISTS `NB_ROLE_HIS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NB_ROLE_HIS` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `role_id` varchar(64) DEFAULT NULL COMMENT '角色名称',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `users` varchar(256) DEFAULT NULL COMMENT '所有用户',
  `authorities` varchar(256) DEFAULT NULL COMMENT '所有权限',
  `trans_type` varchar(32) DEFAULT NULL COMMENT '操作类型',
  `action_code` varchar(32) DEFAULT NULL COMMENT '原因码',
  `action_comment` varchar(256) DEFAULT NULL COMMENT '备注',
  `action_reason` varchar(256) DEFAULT NULL COMMENT '原因',
  `history_seq` varchar(64) DEFAULT NULL COMMENT '序列号 来找到同个事务的不同操作记录',
  PRIMARY KEY (`object_rrn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Role History Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NB_ROLE_HIS`
--

LOCK TABLES `NB_ROLE_HIS` WRITE;
/*!40000 ALTER TABLE `NB_ROLE_HIS` DISABLE KEYS */;
/*!40000 ALTER TABLE `NB_ROLE_HIS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NB_TAB`
--

DROP TABLE IF EXISTS `NB_TAB`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NB_TAB` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `name` varchar(64) DEFAULT NULL COMMENT '名字',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `TABLE_RRN` bigint(20) DEFAULT NULL COMMENT '动态表主键',
  `SEQ_NO` bigint(20) DEFAULT NULL COMMENT '顺序',
  `TAB_TYPE` varchar(256) DEFAULT 'Tab' COMMENT '样式',
  `LABEL` varchar(128) DEFAULT NULL COMMENT '英文标签',
  `LABEL_ZH` varchar(128) DEFAULT NULL COMMENT '中文标签',
  `LABEL_RES` varchar(128) DEFAULT NULL COMMENT '其他语言标签',
  `REF_TABLE_NAME` varchar(64) DEFAULT NULL COMMENT '关联的动态表名称',
  `EDIT_FLAG` varchar(1) DEFAULT 'N' COMMENT '可以编辑',
  `WHERE_CLAUSE` varchar(256) DEFAULT NULL COMMENT '当类型是table的时候和主对象的关联关系',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_TAB_TABLE_RRN_NAME` (`TABLE_RRN`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8 COMMENT='tab Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NB_TAB`
--

LOCK TABLES `NB_TAB` WRITE;
/*!40000 ALTER TABLE `NB_TAB` DISABLE KEYS */;
INSERT INTO `NB_TAB` VALUES (1,'Y',0,'BasicInfo','基本信息',2,10,'Field','BasicInfo','基本信息','',NULL,'N',NULL),(2,'Y',0,'TabInfo','Tab信息',2,20,'Table','TabInfo','Tab信息',NULL,'NBTab','N','tableRrn = :objectRrn'),(3,'Y',0,'FieldInfo','字段信息',2,30,'Table','FieldInfo','字段信息',NULL,'NBField','N','tableRrn = :objectRrn'),(4,'Y',0,'BasicInfo','基本信息',66,10,'Field','BasicInfo','基本信息',NULL,NULL,'N',NULL),(5,'Y',0,'QueryInfo','查询信息',66,30,'Field','QueryInfo','查询信息',NULL,NULL,'N',NULL),(6,'Y',0,'DisplayInfo','查询信息',66,20,'Field','DisplayInfo','显示信息',NULL,NULL,'N',NULL),(7,'Y',0,'FieldInfo','字段信息',65,10,'Table','FieldInfo','字段信息',NULL,'NBField',NULL,'tabRrn = :objectRrn'),(8,'Y',0,'BasicInfo','基本信息',65,5,'Field','BasicInfo','基本信息',NULL,NULL,NULL,NULL),(9,'Y',0,'SystemRefValueInfo','系统栏位参考值',67,10,'Table','SystemRefValueInfo','系统栏位参考值',NULL,'NBSystemRefList','Y','referenceName = :name'),(10,'Y',0,'OwnerRefValueInfo','区域参考值',70,10,'Table','OwnerRefValueInfo','区域栏位参考值',NULL,'NBOwnerRefList','Y','referenceName = :name'),(11,'Y',0,'ContactInfo','联系信息',72,10,'Field','ContactInfo','联系信息',NULL,NULL,'N',NULL),(12,'Y',0,'PasswordRelated','密码相关',72,5,'Field','PwdRelated','密码相关',NULL,NULL,'N',NULL),(13,'Y',0,'GeneratorRuleInfo','ID生成规则信息',77,10,'Field','GeneratorRuleInfo','ID生成规则信息',NULL,'COMGeneratorLine','N','ruleRrn= :objectRrn'),(14,'Y',0,'DetailInfo','详细信息',87,10,'Field','DetailInfo','详细信息',NULL,NULL,'N',NULL),(15,'Y',0,'MMEventStatusInfo','物料事件状态信息',85,10,'Table','EventStatusInfo','事件状态信息',NULL,'MMEventStatus','Y','eventRrn = :objectRrn'),(16,'Y',0,'ReasonInfo','盘点原因',95,10,'Field','Reason','原因',NULL,NULL,'N',NULL),(17,'Y',0,'ReasonInfo','原因',96,10,'Field','Reason','原因',NULL,NULL,'N',NULL);
/*!40000 ALTER TABLE `NB_TAB` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NB_TABLE`
--

DROP TABLE IF EXISTS `NB_TABLE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=utf8 COMMENT='table Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NB_TABLE`
--

LOCK TABLES `NB_TABLE` WRITE;
/*!40000 ALTER TABLE `NB_TABLE` DISABLE KEYS */;
INSERT INTO `NB_TABLE` VALUES (1,'Y',0,'NBOrg','区域',NULL,'NB_ORG',2,'NBOrg','com.newbiest.security.model.NBOrg','',NULL,NULL,NULL,NULL,'OrgManager','区域管理',NULL),(2,'Y',0,'NBTable','动态表管理',NULL,'NB_TABLE',2,'NBTable','com.newbiest.base.ui.model.NBTable','',NULL,NULL,NULL,NULL,'TableManager','表管理',NULL),(65,'Y',0,'NBTab','Tab信息',NULL,'NB_TAB',2,'NBTab','com.newbiest.base.ui.model.NBTab',NULL,NULL,NULL,NULL,NULL,'TabManager','Tab管理',NULL),(66,'Y',0,'NBField','字段信息',NULL,'NB_FIELD',2,'NBField','com.newbiest.base.ui.model.NBField',NULL,NULL,NULL,NULL,NULL,'FieldManager','栏位管理',NULL),(67,'Y',0,'NBSysRefName','系统栏位参考名称',NULL,'NB_REFERENCE_NAME',2,'NBSystemReferenceName','com.newbiest.base.ui.model.NBSystemReferenceName',NULL,NULL,NULL,NULL,NULL,'SysRefNameManager','系统栏位参考名称管理',NULL),(68,'Y',0,'NBRefTable','动态栏位参考表',NULL,'NB_REFERENCE_TABLE',2,'NBReferenceTable','com.newbiest.base.ui.model.NBReferenceTable',NULL,NULL,NULL,NULL,NULL,'RefTableManager','栏位参考表管理',NULL),(69,'Y',0,'NBSystemRefList','系统栏位参考值',NULL,'NB_REFERENCE_LIST',2,'NBSystemReferenceList','com.newbiest.base.ui.model.NBSystemReferenceList',NULL,NULL,NULL,NULL,NULL,'SysRefList','系统栏位参考值管理',NULL),(70,'Y',0,'NBOwenerRefName','区域栏位参考名称',NULL,'NB_REFERENCE_NAME',2,'NBOwnerReferenceName','com.newbiest.base.ui.model.NBOwnerReferenceName',NULL,NULL,NULL,NULL,NULL,'OwnerRefNameManager','区域栏位参考名称管理',NULL),(71,'Y',0,'NBOwnerRefList','区域栏位参考值',NULL,'NB_REFERENCE_LIST',2,'NBOwnerReferenceList','com.newbiest.base.ui.model.NBOwnerReferenceList',NULL,NULL,NULL,NULL,NULL,'OwnerRefListManager','区域栏位参考值管理',NULL),(72,'Y',0,'NBUser','用户管理',NULL,'NB_USER',2,'NBUser','com.newbiest.security.model.NBUser',NULL,NULL,NULL,NULL,NULL,'UserManager','用户管理',NULL),(73,'Y',0,'NBMessage','异常码管理',NULL,'NB_MESSAGE',2,'NBMessage','com.newbiest.base.model.NBMessage',NULL,NULL,NULL,NULL,NULL,'ErrorCodeManager','异常码管理',NULL),(74,'Y',0,'NBRole','角色管理',NULL,'NB_ROLE',2,'NBRole','com.newbiest.security.model.NBRole',NULL,NULL,NULL,NULL,NULL,'RoleManager','角色管理',NULL),(75,'Y',0,'RMSEquipment','设备管理(RMS)',NULL,'RMS_EQUIPMENT',2,'Equipment','com.newbiest.rms.model.Equipment',NULL,NULL,NULL,NULL,NULL,'Equipment(RMS)Manager','设备(RMS)管理',NULL),(76,'Y',0,'RMSRecipe','Recipe(PPID)管理',NULL,'RMS_RECIPE',2,'Recipe','com.newbiest.rms.model.Recipe','',NULL,NULL,NULL,NULL,'RecipeManager','Recipe(PPID)管理1',NULL),(77,'Y',0,'COMIDGernerateRule','ID生成规则',NULL,'COM_GENERATOR_RULE',2,'GeneratorRule','com.newbiest.common.idgenerator.model.GeneratorRule',NULL,NULL,NULL,NULL,NULL,'IDGernerateRule','ID生成规则',NULL),(78,'Y',0,'COMGeneratorLine','ID生成规则信息',NULL,'COM_GENERATOR_RULE_LINE',2,'GeneratorRuleLine','com.newbiest.common.idgenerator.model.GeneratorRuleLine',NULL,'seqNo',NULL,NULL,NULL,'GeneratorLine','ID生成规则信息',NULL),(79,'Y',0,'COMFixedStringLine','固定字符串',NULL,'COM_GENERATOR_RULE_LINE',2,'FixedStringRuleLine','com.newbiest.common.idgenerator.model.FixedStringRuleLine',NULL,NULL,NULL,NULL,NULL,'FixedString','固定字符串',NULL),(80,'Y',0,'COMDateLine','日期类型',NULL,'COM_GENERATOR_RULE_LINE',2,'DateRuleLine','com.newbiest.common.idgenerator.model.DateRuleLine',NULL,NULL,NULL,NULL,NULL,'DateLine','日期类型',NULL),(81,'Y',0,'COMVariableLine','生成规则的参数类型',NULL,'COM_GENERATOR_RULE_LINE',2,'VariableRuleLine','com.newbiest.common.idgenerator.model.VariableRuleLine',NULL,NULL,NULL,NULL,NULL,'VariableLine','生成规则的参数类型',NULL),(82,'Y',0,'COMSequenceLine','生成规则的序列类型',NULL,'COM_GENERATOR_RULE_LINE',2,'SequenceRuleLine','com.newbiest.common.idgenerator.model.SequenceRuleLine',NULL,NULL,NULL,NULL,NULL,'SequenceLine','序列类型',NULL),(83,'Y',0,'MMStatusManager','物料状态管理',NULL,'COM_SM_STATUS',2,'MaterialStatus','com.newbiest.mms.state.model.MaterialStatus',NULL,NULL,NULL,NULL,NULL,'MaterialStatusManager','物料状态管理',NULL),(84,'Y',0,'MMStatusCategoryManager','物料状态大类管理',NULL,'COM_SM_STATUS_CATEGORY',2,'MaterialStatusCategory','com.newbiest.mms.state.model.MaterialStatusCategory',NULL,NULL,NULL,NULL,NULL,'MaterialStatusCategoryManager','物料状态大类管理',NULL),(85,'Y',0,'MMEventManager','物料事件管理',NULL,'COM_SM_EVENT',2,'MaterialEvent','com.newbiest.mms.state.model.MaterialEvent',NULL,NULL,NULL,NULL,NULL,'MaterialEventManager','物料事件管理',NULL),(86,'Y',0,'MMStatusModelManager','物料状态模型管理',NULL,'COM_SM_STATUS_MODEL',2,'MaterialStatusModel','com.newbiest.mms.state.model.MaterialStatusModel',NULL,NULL,NULL,NULL,NULL,'MaterialStatusModelManager','物料状态模型管理',NULL),(87,'Y',0,'MMMaterial','物料管理',NULL,'MM_MATERIAL',2,'RawMaterial','com.newbiest.mms.model.RawMaterial',NULL,NULL,NULL,NULL,NULL,'MaterialManager','物料管理',NULL),(89,'Y',0,'MMSWarehouseManager','仓库管理',NULL,'MMS_WAREHOUSE',2,'Warehouse','com.newbiest.mms.model.Warehouse',NULL,NULL,NULL,NULL,NULL,'WarehouseManager','仓库管理',NULL),(90,'Y',0,'MMReceiveMLot','接收物料',NULL,'',2,'MaterialLotAction','com.newbiest.mms.dto.MaterialLotAction',NULL,NULL,NULL,NULL,NULL,'ReceiveMLot','接收物料',NULL),(91,'Y',0,'MMEventStatus','物料事件状态变更',NULL,'COM_SM_EVENT_STATUS',2,'EventStatus','com.newbiest.commom.sm.model.EventStatus','',NULL,NULL,NULL,NULL,'EventStatus','事件状态',NULL),(92,'Y',0,'MMMaterialLot','物料批次管理',NULL,'MMS_MATERIAL_LOT',2,'MaterialLot','com.newbiest.mms.model.MaterialLot',NULL,NULL,NULL,NULL,NULL,'MaterialLot','物料批次管理',NULL),(93,'Y',0,'MMLotInventory','物料批次库存管理',NULL,'MMS_MATERIAL_LOT_INVENTORY',2,'MaterialLotInventory','com.newbiest.mms.model.MaterialLotInventory',NULL,NULL,NULL,NULL,NULL,'MMLotInventory','物料批次库存管理',NULL),(94,'Y',0,'MMLotTransferInv','物料批次转库',NULL,NULL,2,'MaterialLotAction','com.newbiest.mms.dto.MaterialLotAction',NULL,NULL,NULL,NULL,NULL,'TransferInv','物料批次转库',NULL),(95,'Y',0,'MMLotInvCheck','物料批次库存盘点',NULL,NULL,2,'MaterialLotAction','com.newbiest.mms.dto.MaterialLotAction',NULL,NULL,NULL,NULL,NULL,'CheckInv','物料批次盘点',NULL),(96,'Y',0,'MMLotComsume','物料批次消耗',NULL,NULL,2,'MaterialLotAction','com.newbiest.mms.dto.MaterialLotAction',NULL,NULL,NULL,NULL,NULL,'MLotConsumerAction','物料批次消耗',NULL),(97,'Y',0,'MMPackageType','物料包装类型定义',NULL,'MMS_PACKAGE_TYPE',2,'MaterialLotPackageType','com.newbiest.mms.model.MaterialLotPackageType',NULL,NULL,NULL,NULL,NULL,'MMPackageType','物料包装类型定义',NULL),(98,'Y',0,'MMMaterialLotHis','物料批次历史',NULL,'MMS_MATERIAL_LOT_HIS',2,'MaterialLotHistory','com.newbiest.mms.model.MaterialLotHistory','','created ','1 != 1',NULL,NULL,'MaterialLotHis','物料批次历史',NULL);
/*!40000 ALTER TABLE `NB_TABLE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NB_USER`
--

DROP TABLE IF EXISTS `NB_USER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NB_USER` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `username` varchar(64) DEFAULT NULL COMMENT '用户名',
  `description` varchar(256) DEFAULT NULL COMMENT '描述 比如名字',
  `password` varchar(32) DEFAULT NULL COMMENT '密码',
  `pwd_changed` datetime DEFAULT NULL COMMENT '密码修改时间',
  `pwd_expiry` datetime DEFAULT NULL COMMENT '密码过期时间',
  `pwd_life` bigint(20) DEFAULT NULL COMMENT '密码有效期',
  `pwd_wrong_count` bigint(20) DEFAULT NULL COMMENT '密码错误次数',
  `phone` varchar(32) DEFAULT NULL COMMENT '电话',
  `department` varchar(32) DEFAULT NULL COMMENT '部门',
  `sex` varchar(1) DEFAULT NULL COMMENT '性别 M/Man F/Feman',
  `email` varchar(64) DEFAULT NULL COMMENT '邮箱地址',
  `in_valid_flag` varchar(1) DEFAULT NULL COMMENT '是否在密码有效期之内',
  `last_logon` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_ORG_RRN_USERNAME` (`org_rrn`,`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='User Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NB_USER`
--

LOCK TABLES `NB_USER` WRITE;
/*!40000 ALTER TABLE `NB_USER` DISABLE KEYS */;
INSERT INTO `NB_USER` VALUES (1,'Y',0,'2018-06-06 22:17:22',NULL,'2019-04-18 18:07:43',NULL,429,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,NULL,0,NULL,NULL,NULL,'11603652@qq.com','Y','2019-04-18 18:07:43'),(2,'Y',0,'2018-12-25 11:11:40',NULL,'2019-01-11 13:41:22',NULL,15,'test','测试用户1','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:01:29','2019-10-21 12:01:29',300,0,'',NULL,NULL,'116036512@qq.com','Y','2018-12-29 10:52:00');
/*!40000 ALTER TABLE `NB_USER` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NB_USER_HIS`
--

DROP TABLE IF EXISTS `NB_USER_HIS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NB_USER_HIS` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `username` varchar(64) DEFAULT NULL COMMENT '用户名',
  `description` varchar(256) DEFAULT NULL COMMENT '描述 比如名字',
  `password` varchar(32) DEFAULT NULL COMMENT '密码',
  `pwd_changed` datetime DEFAULT NULL COMMENT '密码修改时间',
  `pwd_expiry` datetime DEFAULT NULL COMMENT '密码过期时间',
  `pwd_life` bigint(20) DEFAULT NULL COMMENT '密码有效期',
  `pwd_wrong_count` bigint(20) DEFAULT NULL COMMENT '密码错误次数',
  `phone` varchar(32) DEFAULT NULL COMMENT '电话',
  `department` varchar(32) DEFAULT NULL COMMENT '部门',
  `sex` varchar(1) DEFAULT NULL COMMENT '性别 M/Man F/Feman',
  `email` varchar(64) DEFAULT NULL COMMENT '邮箱地址',
  `in_valid_flag` varchar(1) DEFAULT NULL COMMENT '是否在密码有效期之内',
  `role_list` varchar(256) DEFAULT NULL COMMENT '角色列表',
  `org_list` varchar(64) DEFAULT NULL COMMENT '区域列表',
  `last_logon` datetime DEFAULT CURRENT_TIMESTAMP,
  `trans_type` varchar(32) DEFAULT NULL COMMENT '操作类型',
  `action_code` varchar(32) DEFAULT NULL COMMENT '原因码',
  `action_comment` varchar(256) DEFAULT NULL COMMENT '备注',
  `action_reason` varchar(256) DEFAULT NULL COMMENT '原因',
  `history_seq` varchar(64) DEFAULT NULL COMMENT '序列号 来找到同个事务的不同操作记录',
  PRIMARY KEY (`object_rrn`)
) ENGINE=InnoDB AUTO_INCREMENT=211 DEFAULT CHARSET=utf8 COMMENT='User History Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NB_USER_HIS`
--

LOCK TABLES `NB_USER_HIS` WRITE;
/*!40000 ALTER TABLE `NB_USER_HIS` DISABLE KEYS */;
INSERT INTO `NB_USER_HIS` VALUES (59,'Y',0,'2019-01-22 13:32:02',NULL,'2019-01-22 13:32:02',NULL,281,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-22 13:32:02','LoginSuccess',NULL,NULL,NULL,NULL),(60,'Y',0,'2019-01-23 14:47:32',NULL,'2019-01-23 14:47:32',NULL,282,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-23 14:47:32','LoginSuccess',NULL,NULL,NULL,NULL),(61,'Y',0,'2019-01-23 15:22:58',NULL,'2019-01-23 15:22:58',NULL,283,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-23 15:22:58','LoginSuccess',NULL,NULL,NULL,NULL),(62,'Y',0,'2019-01-23 15:45:50',NULL,'2019-01-23 15:45:50',NULL,284,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-23 15:45:50','LoginSuccess',NULL,NULL,NULL,NULL),(63,'Y',0,'2019-01-23 15:53:49',NULL,'2019-01-23 15:53:49',NULL,285,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-23 15:53:49','LoginSuccess',NULL,NULL,NULL,NULL),(64,'Y',0,'2019-01-23 16:05:56',NULL,'2019-01-23 16:05:56',NULL,286,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-23 16:05:56','LoginSuccess',NULL,NULL,NULL,NULL),(65,'Y',0,'2019-01-23 16:07:56',NULL,'2019-01-23 16:07:56',NULL,287,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-23 16:07:56','LoginSuccess',NULL,NULL,NULL,NULL),(66,'Y',0,'2019-01-23 16:08:46',NULL,'2019-01-23 16:08:46',NULL,288,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-23 16:08:46','LoginSuccess',NULL,NULL,NULL,NULL),(67,'Y',0,'2019-01-23 16:08:59',NULL,'2019-01-23 16:08:59',NULL,289,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-23 16:08:59','LoginSuccess',NULL,NULL,NULL,NULL),(68,'Y',0,'2019-01-23 16:13:59',NULL,'2019-01-23 16:13:59',NULL,290,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-23 16:13:59','LoginSuccess',NULL,NULL,NULL,NULL),(69,'Y',0,'2019-01-23 18:40:31',NULL,'2019-01-23 18:40:31',NULL,291,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-23 18:40:31','LoginSuccess',NULL,NULL,NULL,NULL),(70,'Y',0,'2019-01-23 18:41:23',NULL,'2019-01-23 18:41:23',NULL,1,NULL,'创建物料批次','96e79218965eb72c92a549dd5a330112',NULL,'2019-11-19 18:41:23',300,NULL,NULL,NULL,NULL,NULL,'N',NULL,NULL,NULL,'Create',NULL,NULL,NULL,'f97bde17-c563-4835-9a2a-c64606876c2d'),(71,'Y',0,'2019-01-23 18:42:16',NULL,'2019-01-23 18:42:16',NULL,1,NULL,'1','96e79218965eb72c92a549dd5a330112',NULL,'2019-11-19 18:42:16',300,NULL,NULL,NULL,NULL,NULL,'N',NULL,NULL,NULL,'Create',NULL,NULL,NULL,'7a2085a9-7303-4032-95b4-44143140f7e2'),(72,'Y',0,'2019-01-23 18:42:31',NULL,'2019-01-23 18:42:31',NULL,1,NULL,'创建物料批次','96e79218965eb72c92a549dd5a330112',NULL,'2019-11-19 18:41:23',300,NULL,NULL,NULL,NULL,NULL,'N',NULL,NULL,NULL,'Delete',NULL,NULL,NULL,'1c128ba0-d23a-4e60-bb81-61fc627a30de'),(73,'Y',0,'2019-01-23 18:42:32',NULL,'2019-01-23 18:42:32',NULL,1,NULL,'1','96e79218965eb72c92a549dd5a330112',NULL,'2019-11-19 18:42:16',300,NULL,NULL,NULL,NULL,NULL,'N',NULL,NULL,NULL,'Delete',NULL,NULL,NULL,'329e5c04-7d9d-46fc-a58d-0fd6c5247a97'),(74,'Y',0,'2019-01-24 11:47:51',NULL,'2019-01-24 11:47:51',NULL,292,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-24 11:47:51','LoginSuccess',NULL,NULL,NULL,NULL),(75,'Y',0,'2019-01-24 18:20:03',NULL,'2019-01-24 18:20:03',NULL,293,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-24 18:20:03','LoginSuccess',NULL,NULL,NULL,NULL),(76,'Y',0,'2019-01-25 10:50:06',NULL,'2019-01-25 10:50:06',NULL,294,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-25 10:50:06','LoginSuccess',NULL,NULL,NULL,NULL),(77,'Y',0,'2019-01-26 10:33:08',NULL,'2019-01-26 10:33:08',NULL,295,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-26 10:33:08','LoginSuccess',NULL,NULL,NULL,NULL),(78,'Y',0,'2019-01-29 15:37:02',NULL,'2019-01-29 15:37:02',NULL,296,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-29 15:37:02','LoginSuccess',NULL,NULL,NULL,NULL),(79,'Y',0,'2019-01-29 17:09:22',NULL,'2019-01-29 17:09:22',NULL,297,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-29 17:09:22','LoginSuccess',NULL,NULL,NULL,NULL),(80,'Y',0,'2019-01-29 19:10:09',NULL,'2019-01-29 19:10:09',NULL,298,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-29 19:10:09','LoginSuccess',NULL,NULL,NULL,NULL),(81,'Y',0,'2019-01-30 09:45:45',NULL,'2019-01-30 09:45:45',NULL,299,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-30 09:45:45','LoginSuccess',NULL,NULL,NULL,NULL),(82,'Y',0,'2019-01-30 09:45:49',NULL,'2019-01-30 09:45:49',NULL,300,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-30 09:45:49','LoginSuccess',NULL,NULL,NULL,NULL),(83,'Y',0,'2019-01-30 10:14:00',NULL,'2019-01-30 10:14:00',NULL,301,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-30 10:14:00','LoginSuccess',NULL,NULL,NULL,NULL),(84,'Y',0,'2019-01-30 11:14:05',NULL,'2019-01-30 11:14:05',NULL,302,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-30 11:14:05','LoginSuccess',NULL,NULL,NULL,NULL),(85,'Y',0,'2019-01-30 15:40:54',NULL,'2019-01-30 15:40:54',NULL,303,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-01-30 15:40:54','LoginSuccess',NULL,NULL,NULL,NULL),(86,'Y',0,'2019-02-13 15:49:42',NULL,'2019-02-13 15:49:42',NULL,304,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-02-13 15:49:42','LoginSuccess',NULL,NULL,NULL,NULL),(87,'Y',0,'2019-02-13 15:55:17',NULL,'2019-02-13 15:55:17',NULL,305,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-02-13 15:55:17','LoginSuccess',NULL,NULL,NULL,NULL),(88,'Y',0,'2019-02-13 16:10:06',NULL,'2019-02-13 16:10:06',NULL,306,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-02-13 16:10:06','LoginSuccess',NULL,NULL,NULL,NULL),(89,'Y',0,'2019-02-14 11:13:56',NULL,'2019-02-14 11:13:56',NULL,307,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-02-14 11:13:55','LoginSuccess',NULL,NULL,NULL,NULL),(90,'Y',0,'2019-02-14 15:35:53',NULL,'2019-02-14 15:35:53',NULL,308,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-02-14 15:35:53','LoginSuccess',NULL,NULL,NULL,NULL),(91,'Y',0,'2019-02-15 10:55:46',NULL,'2019-02-15 10:55:46',NULL,309,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-02-15 10:55:46','LoginSuccess',NULL,NULL,NULL,NULL),(92,'Y',0,'2019-02-15 13:19:35',NULL,'2019-02-15 13:19:35',NULL,310,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-02-15 13:19:35','LoginSuccess',NULL,NULL,NULL,NULL),(93,'Y',0,'2019-02-19 14:48:14',NULL,'2019-02-19 14:48:14',NULL,311,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-02-19 14:48:14','LoginSuccess',NULL,NULL,NULL,NULL),(94,'Y',0,'2019-02-19 17:37:48',NULL,'2019-02-19 17:37:48',NULL,312,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-02-19 17:37:48','LoginSuccess',NULL,NULL,NULL,NULL),(95,'Y',0,'2019-02-19 20:08:26',NULL,'2019-02-19 20:08:26',NULL,313,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-02-19 20:08:26','LoginSuccess',NULL,NULL,NULL,NULL),(96,'Y',0,'2019-02-19 20:08:29',NULL,'2019-02-19 20:08:29',NULL,314,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-02-19 20:08:29','LoginSuccess',NULL,NULL,NULL,NULL),(97,'Y',0,'2019-02-20 11:24:11',NULL,'2019-02-20 11:24:11',NULL,315,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-02-20 11:24:11','LoginSuccess',NULL,NULL,NULL,NULL),(98,'Y',0,'2019-02-20 15:44:11',NULL,'2019-02-20 15:44:11',NULL,316,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-02-20 15:44:11','LoginSuccess',NULL,NULL,NULL,NULL),(99,'Y',0,'2019-02-20 16:54:55',NULL,'2019-02-20 16:54:55',NULL,317,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-02-20 16:54:54','LoginSuccess',NULL,NULL,NULL,NULL),(100,'Y',0,'2019-02-20 16:55:42',NULL,'2019-02-20 16:55:42',NULL,318,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-02-20 16:55:42','LoginSuccess',NULL,NULL,NULL,NULL),(101,'Y',0,'2019-02-25 13:43:18',NULL,'2019-02-25 13:43:18',NULL,319,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-02-25 13:43:17','LoginSuccess',NULL,NULL,NULL,NULL),(102,'Y',0,'2019-02-25 16:42:53',NULL,'2019-02-25 16:42:53',NULL,320,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-02-25 16:42:53','LoginSuccess',NULL,NULL,NULL,NULL),(103,'Y',0,'2019-02-25 17:56:44',NULL,'2019-02-25 17:56:44',NULL,321,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-02-25 17:56:43','LoginSuccess',NULL,NULL,NULL,NULL),(104,'Y',0,'2019-02-25 18:04:37',NULL,'2019-02-25 18:04:37',NULL,322,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-02-25 18:04:37','LoginSuccess',NULL,NULL,NULL,NULL),(105,'Y',0,'2019-02-26 14:26:30',NULL,'2019-02-26 14:26:30',NULL,323,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-02-26 14:26:30','LoginSuccess',NULL,NULL,NULL,NULL),(106,'Y',0,'2019-02-26 19:01:44',NULL,'2019-02-26 19:01:44',NULL,324,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-02-26 19:01:44','LoginSuccess',NULL,NULL,NULL,NULL),(107,'Y',0,'2019-03-02 11:45:06',NULL,'2019-03-02 11:45:06',NULL,325,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-02 11:45:06','LoginSuccess',NULL,NULL,NULL,NULL),(108,'Y',0,'2019-03-02 11:56:09',NULL,'2019-03-02 11:56:09',NULL,326,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-02 11:56:09','LoginSuccess',NULL,NULL,NULL,NULL),(109,'Y',0,'2019-03-02 11:56:57',NULL,'2019-03-02 11:56:57',NULL,327,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-02 11:56:57','LoginSuccess',NULL,NULL,NULL,NULL),(110,'Y',0,'2019-03-05 18:33:24',NULL,'2019-03-05 18:33:24',NULL,328,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-05 18:33:24','LoginSuccess',NULL,NULL,NULL,NULL),(111,'Y',0,'2019-03-06 14:56:53',NULL,'2019-03-06 14:56:53',NULL,329,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-06 14:56:52','LoginSuccess',NULL,NULL,NULL,NULL),(112,'Y',0,'2019-03-06 19:01:40',NULL,'2019-03-06 19:01:40',NULL,330,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-06 19:01:40','LoginSuccess',NULL,NULL,NULL,NULL),(113,'Y',0,'2019-03-06 19:05:23',NULL,'2019-03-06 19:05:23',NULL,331,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-06 19:05:23','LoginSuccess',NULL,NULL,NULL,NULL),(114,'Y',0,'2019-03-07 10:21:49',NULL,'2019-03-07 10:21:49',NULL,332,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-07 10:21:48','LoginSuccess',NULL,NULL,NULL,NULL),(115,'Y',0,'2019-03-07 16:44:30',NULL,'2019-03-07 16:44:30',NULL,333,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-07 16:44:30','LoginSuccess',NULL,NULL,NULL,NULL),(116,'Y',0,'2019-03-07 16:47:10',NULL,'2019-03-07 16:47:10',NULL,334,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-07 16:47:10','LoginSuccess',NULL,NULL,NULL,NULL),(117,'Y',0,'2019-03-07 21:16:47',NULL,'2019-03-07 21:16:47',NULL,335,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-07 21:16:47','LoginSuccess',NULL,NULL,NULL,NULL),(118,'Y',0,'2019-03-25 17:29:28',NULL,'2019-03-25 17:29:28',NULL,336,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-25 17:29:28','LoginSuccess',NULL,NULL,NULL,NULL),(119,'Y',0,'2019-03-26 09:43:02',NULL,'2019-03-26 09:43:02',NULL,337,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-26 09:43:02','LoginSuccess',NULL,NULL,NULL,NULL),(120,'Y',0,'2019-03-26 09:52:24',NULL,'2019-03-26 09:52:24',NULL,338,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-26 09:52:24','LoginSuccess',NULL,NULL,NULL,NULL),(121,'Y',0,'2019-03-26 14:32:43',NULL,'2019-03-26 14:32:43',NULL,339,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-26 14:32:43','LoginSuccess',NULL,NULL,NULL,NULL),(122,'Y',0,'2019-03-26 17:30:15',NULL,'2019-03-26 17:30:15',NULL,340,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-26 17:30:14','LoginSuccess',NULL,NULL,NULL,NULL),(123,'Y',0,'2019-03-27 13:36:58',NULL,'2019-03-27 13:36:58',NULL,341,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-27 13:36:58','LoginSuccess',NULL,NULL,NULL,NULL),(124,'Y',0,'2019-03-27 14:34:02',NULL,'2019-03-27 14:34:02',NULL,342,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-27 14:34:02','LoginSuccess',NULL,NULL,NULL,NULL),(125,'Y',0,'2019-03-27 15:48:21',NULL,'2019-03-27 15:48:21',NULL,343,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-27 15:48:20','LoginSuccess',NULL,NULL,NULL,NULL),(126,'Y',0,'2019-03-27 15:49:34',NULL,'2019-03-27 15:49:34',NULL,344,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-27 15:49:34','LoginSuccess',NULL,NULL,NULL,NULL),(127,'Y',0,'2019-03-27 18:46:00',NULL,'2019-03-27 18:46:00',NULL,345,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-27 18:46:00','LoginSuccess',NULL,NULL,NULL,NULL),(128,'Y',0,'2019-03-28 13:58:57',NULL,'2019-03-28 13:58:57',NULL,346,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-28 13:58:57','LoginSuccess',NULL,NULL,NULL,NULL),(129,'Y',0,'2019-03-28 16:50:07',NULL,'2019-03-28 16:50:07',NULL,347,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-28 16:50:07','LoginSuccess',NULL,NULL,NULL,NULL),(130,'Y',0,'2019-03-28 16:50:13',NULL,'2019-03-28 16:50:13',NULL,348,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-28 16:50:13','LoginSuccess',NULL,NULL,NULL,NULL),(131,'Y',0,'2019-03-28 16:50:30',NULL,'2019-03-28 16:50:30',NULL,349,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-28 16:50:30','LoginSuccess',NULL,NULL,NULL,NULL),(132,'Y',0,'2019-03-28 16:50:34',NULL,'2019-03-28 16:50:34',NULL,350,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-28 16:50:34','LoginSuccess',NULL,NULL,NULL,NULL),(133,'Y',0,'2019-03-28 16:50:51',NULL,'2019-03-28 16:50:51',NULL,351,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-28 16:50:51','LoginSuccess',NULL,NULL,NULL,NULL),(134,'Y',0,'2019-03-28 16:53:27',NULL,'2019-03-28 16:53:27',NULL,352,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-28 16:53:27','LoginSuccess',NULL,NULL,NULL,NULL),(135,'Y',0,'2019-03-28 16:55:05',NULL,'2019-03-28 16:55:05',NULL,353,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-28 16:55:05','LoginSuccess',NULL,NULL,NULL,NULL),(136,'Y',0,'2019-03-29 14:59:43',NULL,'2019-03-29 14:59:43',NULL,354,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-29 14:59:42','LoginSuccess',NULL,NULL,NULL,NULL),(137,'Y',0,'2019-03-29 15:22:36',NULL,'2019-03-29 15:22:36',NULL,355,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-03-29 15:22:36','LoginSuccess',NULL,NULL,NULL,NULL),(138,'Y',0,'2019-04-03 10:14:34',NULL,'2019-04-03 10:14:34',NULL,356,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-03 10:14:34','LoginSuccess',NULL,NULL,NULL,NULL),(139,'Y',0,'2019-04-03 14:49:39',NULL,'2019-04-03 14:49:39',NULL,357,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-03 14:49:39','LoginSuccess',NULL,NULL,NULL,NULL),(140,'Y',0,'2019-04-03 17:20:02',NULL,'2019-04-03 17:20:02',NULL,358,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-03 17:20:02','LoginSuccess',NULL,NULL,NULL,NULL),(141,'Y',0,'2019-04-03 17:20:30',NULL,'2019-04-03 17:20:30',NULL,359,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-03 17:20:30','LoginSuccess',NULL,NULL,NULL,NULL),(142,'Y',0,'2019-04-03 17:22:02',NULL,'2019-04-03 17:22:02',NULL,360,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-03 17:22:02','LoginSuccess',NULL,NULL,NULL,NULL),(143,'Y',0,'2019-04-03 17:56:33',NULL,'2019-04-03 17:56:33',NULL,361,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-03 17:56:33','LoginSuccess',NULL,NULL,NULL,NULL),(144,'Y',0,'2019-04-03 18:38:55',NULL,'2019-04-03 18:38:55',NULL,362,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-03 18:38:55','LoginSuccess',NULL,NULL,NULL,NULL),(145,'Y',0,'2019-04-04 11:06:52',NULL,'2019-04-04 11:06:52',NULL,363,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-04 11:06:52','LoginSuccess',NULL,NULL,NULL,NULL),(146,'Y',0,'2019-04-04 11:15:26',NULL,'2019-04-04 11:15:26',NULL,364,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-04 11:15:26','LoginSuccess',NULL,NULL,NULL,NULL),(147,'Y',0,'2019-04-04 11:31:48',NULL,'2019-04-04 11:31:48',NULL,365,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-04 11:31:48','LoginSuccess',NULL,NULL,NULL,NULL),(148,'Y',0,'2019-04-04 13:58:18',NULL,'2019-04-04 13:58:18',NULL,366,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-04 13:58:18','LoginSuccess',NULL,NULL,NULL,NULL),(149,'Y',0,'2019-04-11 10:57:15',NULL,'2019-04-11 10:57:15',NULL,367,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-11 10:57:15','LoginSuccess',NULL,NULL,NULL,NULL),(150,'Y',0,'2019-04-11 11:27:18',NULL,'2019-04-11 11:27:18',NULL,368,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-11 11:27:18','LoginSuccess',NULL,NULL,NULL,NULL),(151,'Y',0,'2019-04-11 15:26:26',NULL,'2019-04-11 15:26:26',NULL,369,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-11 15:26:26','LoginSuccess',NULL,NULL,NULL,NULL),(152,'Y',0,'2019-04-11 15:33:04',NULL,'2019-04-11 15:33:04',NULL,370,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-11 15:33:04','LoginSuccess',NULL,NULL,NULL,NULL),(153,'Y',0,'2019-04-11 17:19:27',NULL,'2019-04-11 17:19:27',NULL,371,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-11 17:19:27','LoginSuccess',NULL,NULL,NULL,NULL),(154,'Y',0,'2019-04-11 17:51:19',NULL,'2019-04-11 17:51:19',NULL,372,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-11 17:51:19','LoginSuccess',NULL,NULL,NULL,NULL),(155,'Y',0,'2019-04-11 19:03:20',NULL,'2019-04-11 19:03:20',NULL,373,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-11 19:03:20','LoginSuccess',NULL,NULL,NULL,NULL),(156,'Y',0,'2019-04-12 10:33:19',NULL,'2019-04-12 10:33:19',NULL,374,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-12 10:33:18','LoginSuccess',NULL,NULL,NULL,NULL),(157,'Y',0,'2019-04-12 11:25:32',NULL,'2019-04-12 11:25:32',NULL,375,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-12 11:25:32','LoginSuccess',NULL,NULL,NULL,NULL),(158,'Y',0,'2019-04-12 13:48:46',NULL,'2019-04-12 13:48:46',NULL,376,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-12 13:48:46','LoginSuccess',NULL,NULL,NULL,NULL),(159,'Y',0,'2019-04-12 13:50:56',NULL,'2019-04-12 13:50:56',NULL,377,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-12 13:50:56','LoginSuccess',NULL,NULL,NULL,NULL),(160,'Y',0,'2019-04-12 14:16:56',NULL,'2019-04-12 14:16:56',NULL,378,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-12 14:16:56','LoginSuccess',NULL,NULL,NULL,NULL),(161,'Y',0,'2019-04-12 14:49:29',NULL,'2019-04-12 14:49:29',NULL,379,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-12 14:49:29','LoginSuccess',NULL,NULL,NULL,NULL),(162,'Y',0,'2019-04-12 14:50:43',NULL,'2019-04-12 14:50:43',NULL,380,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-12 14:50:43','LoginSuccess',NULL,NULL,NULL,NULL),(163,'Y',0,'2019-04-12 16:49:03',NULL,'2019-04-12 16:49:03',NULL,381,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-12 16:49:03','LoginSuccess',NULL,NULL,NULL,NULL),(164,'Y',0,'2019-04-14 11:40:26',NULL,'2019-04-14 11:40:26',NULL,382,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-14 11:40:26','LoginSuccess',NULL,NULL,NULL,NULL),(165,'Y',0,'2019-04-14 11:54:14',NULL,'2019-04-14 11:54:14',NULL,383,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-14 11:54:14','LoginSuccess',NULL,NULL,NULL,NULL),(166,'Y',0,'2019-04-14 12:44:51',NULL,'2019-04-14 12:44:51',NULL,384,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-14 12:44:51','LoginSuccess',NULL,NULL,NULL,NULL),(167,'Y',0,'2019-04-14 13:47:45',NULL,'2019-04-14 13:47:45',NULL,385,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-14 13:47:45','LoginSuccess',NULL,NULL,NULL,NULL),(168,'Y',0,'2019-04-14 16:02:33',NULL,'2019-04-14 16:02:33',NULL,386,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-14 16:02:33','LoginSuccess',NULL,NULL,NULL,NULL),(169,'Y',0,'2019-04-14 16:21:21',NULL,'2019-04-14 16:21:21',NULL,387,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-14 16:21:21','LoginSuccess',NULL,NULL,NULL,NULL),(170,'Y',0,'2019-04-14 17:13:34',NULL,'2019-04-14 17:13:34',NULL,388,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-14 17:13:34','LoginSuccess',NULL,NULL,NULL,NULL),(171,'Y',0,'2019-04-14 17:39:06',NULL,'2019-04-14 17:39:06',NULL,389,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-14 17:39:06','LoginSuccess',NULL,NULL,NULL,NULL),(172,'Y',0,'2019-04-14 18:35:04',NULL,'2019-04-14 18:35:04',NULL,390,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-14 18:35:04','LoginSuccess',NULL,NULL,NULL,NULL),(173,'Y',0,'2019-04-14 18:35:27',NULL,'2019-04-14 18:35:27',NULL,391,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-14 18:35:27','LoginSuccess',NULL,NULL,NULL,NULL),(174,'Y',0,'2019-04-14 18:59:36',NULL,'2019-04-14 18:59:36',NULL,392,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-14 18:59:36','LoginSuccess',NULL,NULL,NULL,NULL),(175,'Y',0,'2019-04-14 19:05:25',NULL,'2019-04-14 19:05:25',NULL,393,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-14 19:05:25','LoginSuccess',NULL,NULL,NULL,NULL),(176,'Y',0,'2019-04-14 19:06:00',NULL,'2019-04-14 19:06:00',NULL,394,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-14 19:06:00','LoginSuccess',NULL,NULL,NULL,NULL),(177,'Y',0,'2019-04-14 19:06:37',NULL,'2019-04-14 19:06:37',NULL,395,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-14 19:06:37','LoginSuccess',NULL,NULL,NULL,NULL),(178,'Y',0,'2019-04-14 19:06:50',NULL,'2019-04-14 19:06:50',NULL,396,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-14 19:06:50','LoginSuccess',NULL,NULL,NULL,NULL),(179,'Y',0,'2019-04-14 19:07:38',NULL,'2019-04-14 19:07:38',NULL,397,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,1,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-14 19:06:50','LoginFail',NULL,NULL,NULL,NULL),(180,'Y',0,'2019-04-14 19:07:40',NULL,'2019-04-14 19:07:40',NULL,398,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-14 19:07:40','LoginSuccess',NULL,NULL,NULL,NULL),(181,'Y',0,'2019-04-14 19:23:44',NULL,'2019-04-14 19:23:44',NULL,399,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-14 19:23:44','LoginSuccess',NULL,NULL,NULL,NULL),(182,'Y',0,'2019-04-14 19:27:11',NULL,'2019-04-14 19:27:11',NULL,400,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-14 19:27:11','LoginSuccess',NULL,NULL,NULL,NULL),(183,'Y',0,'2019-04-14 19:34:19',NULL,'2019-04-14 19:34:19',NULL,401,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-14 19:34:19','LoginSuccess',NULL,NULL,NULL,NULL),(184,'Y',0,'2019-04-15 10:05:53',NULL,'2019-04-15 10:05:53',NULL,402,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-15 10:05:53','LoginSuccess',NULL,NULL,NULL,NULL),(185,'Y',0,'2019-04-15 10:10:03',NULL,'2019-04-15 10:10:03',NULL,403,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-15 10:10:03','LoginSuccess',NULL,NULL,NULL,NULL),(186,'Y',0,'2019-04-15 10:54:46',NULL,'2019-04-15 10:54:46',NULL,404,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-15 10:54:46','LoginSuccess',NULL,NULL,NULL,NULL),(187,'Y',0,'2019-04-15 11:33:44',NULL,'2019-04-15 11:33:44',NULL,405,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-15 11:33:44','LoginSuccess',NULL,NULL,NULL,NULL),(188,'Y',0,'2019-04-15 13:37:36',NULL,'2019-04-15 13:37:36',NULL,406,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-15 13:37:36','LoginSuccess',NULL,NULL,NULL,NULL),(189,'Y',0,'2019-04-16 19:00:29',NULL,'2019-04-16 19:00:29',NULL,407,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-16 19:00:29','LoginSuccess',NULL,NULL,NULL,NULL),(190,'Y',0,'2019-04-16 19:00:45',NULL,'2019-04-16 19:00:45',NULL,408,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-16 19:00:45','LoginSuccess',NULL,NULL,NULL,NULL),(191,'Y',0,'2019-04-16 19:00:55',NULL,'2019-04-16 19:00:55',NULL,409,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-16 19:00:55','LoginSuccess',NULL,NULL,NULL,NULL),(192,'Y',0,'2019-04-16 19:02:08',NULL,'2019-04-16 19:02:08',NULL,410,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-16 19:02:08','LoginSuccess',NULL,NULL,NULL,NULL),(193,'Y',0,'2019-04-16 19:03:16',NULL,'2019-04-16 19:03:16',NULL,411,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-16 19:03:16','LoginSuccess',NULL,NULL,NULL,NULL),(194,'Y',0,'2019-04-16 19:17:01',NULL,'2019-04-16 19:17:01',NULL,412,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-16 19:17:01','LoginSuccess',NULL,NULL,NULL,NULL),(195,'Y',0,'2019-04-16 19:17:05',NULL,'2019-04-16 19:17:05',NULL,413,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-16 19:17:05','LoginSuccess',NULL,NULL,NULL,NULL),(196,'Y',0,'2019-04-16 19:23:11',NULL,'2019-04-16 19:23:11',NULL,414,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-16 19:23:11','LoginSuccess',NULL,NULL,NULL,NULL),(197,'Y',0,'2019-04-16 19:44:38',NULL,'2019-04-16 19:44:38',NULL,415,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-16 19:44:38','LoginSuccess',NULL,NULL,NULL,NULL),(198,'Y',0,'2019-04-17 17:27:15',NULL,'2019-04-17 17:27:15',NULL,416,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-17 17:27:15','LoginSuccess',NULL,NULL,NULL,NULL),(199,'Y',0,'2019-04-18 17:50:24',NULL,'2019-04-18 17:50:24',NULL,417,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-18 17:50:24','LoginSuccess',NULL,NULL,NULL,NULL),(200,'Y',0,'2019-04-18 17:52:29',NULL,'2019-04-18 17:52:29',NULL,418,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-18 17:52:29','LoginSuccess',NULL,NULL,NULL,NULL),(201,'Y',0,'2019-04-18 17:53:34',NULL,'2019-04-18 17:53:34',NULL,419,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-18 17:53:34','LoginSuccess',NULL,NULL,NULL,NULL),(202,'Y',0,'2019-04-18 17:54:31',NULL,'2019-04-18 17:54:31',NULL,420,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-18 17:54:31','LoginSuccess',NULL,NULL,NULL,NULL),(203,'Y',0,'2019-04-18 17:55:42',NULL,'2019-04-18 17:55:42',NULL,421,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-18 17:55:42','LoginSuccess',NULL,NULL,NULL,NULL),(204,'Y',0,'2019-04-18 17:56:30',NULL,'2019-04-18 17:56:30',NULL,422,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-18 17:56:30','LoginSuccess',NULL,NULL,NULL,NULL),(205,'Y',0,'2019-04-18 17:57:14',NULL,'2019-04-18 17:57:14',NULL,423,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-18 17:57:14','LoginSuccess',NULL,NULL,NULL,NULL),(206,'Y',0,'2019-04-18 17:57:55',NULL,'2019-04-18 17:57:55',NULL,424,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-18 17:57:55','LoginSuccess',NULL,NULL,NULL,NULL),(207,'Y',0,'2019-04-18 18:00:58',NULL,'2019-04-18 18:00:58',NULL,425,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-18 18:00:58','LoginSuccess',NULL,NULL,NULL,NULL),(208,'Y',0,'2019-04-18 18:02:58',NULL,'2019-04-18 18:02:58',NULL,426,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-18 18:02:58','LoginSuccess',NULL,NULL,NULL,NULL),(209,'Y',0,'2019-04-18 18:05:05',NULL,'2019-04-18 18:05:05',NULL,427,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-18 18:05:05','LoginSuccess',NULL,NULL,NULL,NULL),(210,'Y',0,'2019-04-18 18:07:43',NULL,'2019-04-18 18:07:43',NULL,428,'admin','管理员','c4ca4238a0b923820dcc509a6f75849b','2018-12-25 12:02:04',NULL,300,0,NULL,NULL,NULL,'11603652@qq.com','Y','TestRole',NULL,'2019-04-18 18:07:43','LoginSuccess',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `NB_USER_HIS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NB_USER_ORG`
--

DROP TABLE IF EXISTS `NB_USER_ORG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NB_USER_ORG` (
  `org_rrn` bigint(20) DEFAULT NULL COMMENT '区域主键',
  `user_rrn` bigint(20) DEFAULT NULL COMMENT '用户主键',
  KEY `fk_user_org_userrrn` (`user_rrn`),
  KEY `fk_user_org_orgrrn` (`org_rrn`),
  CONSTRAINT `fk_user_org_orgrrn` FOREIGN KEY (`org_rrn`) REFERENCES `NB_ORG` (`object_rrn`),
  CONSTRAINT `fk_user_org_userrrn` FOREIGN KEY (`user_rrn`) REFERENCES `NB_USER` (`object_rrn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='User Org Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NB_USER_ORG`
--

LOCK TABLES `NB_USER_ORG` WRITE;
/*!40000 ALTER TABLE `NB_USER_ORG` DISABLE KEYS */;
INSERT INTO `NB_USER_ORG` VALUES (1,2);
/*!40000 ALTER TABLE `NB_USER_ORG` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NB_USER_ROLE`
--

DROP TABLE IF EXISTS `NB_USER_ROLE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NB_USER_ROLE` (
  `role_rrn` bigint(20) DEFAULT NULL COMMENT '角色主键',
  `user_rrn` bigint(20) DEFAULT NULL COMMENT '用户主键',
  KEY `fk_user_role_rolerrn` (`role_rrn`),
  KEY `fk_user_role_userrrn` (`user_rrn`),
  CONSTRAINT `fk_user_role_rolerrn` FOREIGN KEY (`role_rrn`) REFERENCES `NB_ROLE` (`object_rrn`),
  CONSTRAINT `fk_user_role_userrrn` FOREIGN KEY (`user_rrn`) REFERENCES `NB_USER` (`object_rrn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='User Role Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NB_USER_ROLE`
--

LOCK TABLES `NB_USER_ROLE` WRITE;
/*!40000 ALTER TABLE `NB_USER_ROLE` DISABLE KEYS */;
INSERT INTO `NB_USER_ROLE` VALUES (5,1);
/*!40000 ALTER TABLE `NB_USER_ROLE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `RMS_EQUIPMENT`
--

DROP TABLE IF EXISTS `RMS_EQUIPMENT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `RMS_EQUIPMENT` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `equipment_id` varchar(32) DEFAULT NULL COMMENT '设备号',
  `description` varchar(64) DEFAULT NULL COMMENT '描述',
  `equipment_type` varchar(32) DEFAULT NULL COMMENT '设备类型',
  `hold_flag` varchar(1) DEFAULT NULL COMMENT 'hold状态',
  `communication_flag` varchar(1) DEFAULT NULL COMMENT '通讯状态',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_EQUIPMENT_ORG_RRN_EQP_ID` (`org_rrn`,`equipment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Equipment Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `RMS_EQUIPMENT`
--

LOCK TABLES `RMS_EQUIPMENT` WRITE;
/*!40000 ALTER TABLE `RMS_EQUIPMENT` DISABLE KEYS */;
/*!40000 ALTER TABLE `RMS_EQUIPMENT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `RMS_RECIPE`
--

DROP TABLE IF EXISTS `RMS_RECIPE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `RMS_RECIPE` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `name` varchar(32) DEFAULT NULL COMMENT '名称',
  `description` varchar(64) DEFAULT NULL COMMENT '描述',
  `category` varchar(32) DEFAULT NULL COMMENT '种类',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_RECIPE_ORG_RRN_NAME` (`org_rrn`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Recipe Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `RMS_RECIPE`
--

LOCK TABLES `RMS_RECIPE` WRITE;
/*!40000 ALTER TABLE `RMS_RECIPE` DISABLE KEYS */;
/*!40000 ALTER TABLE `RMS_RECIPE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `RMS_RECIPE_EQUIPMENT`
--

DROP TABLE IF EXISTS `RMS_RECIPE_EQUIPMENT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `RMS_RECIPE_EQUIPMENT` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `recipe_name` varchar(32) DEFAULT NULL COMMENT 'recipe名称',
  `version` bigint(20) DEFAULT NULL COMMENT '版本',
  `class` varchar(32) DEFAULT NULL COMMENT '类别 Recipe Program',
  `status` varchar(32) DEFAULT NULL COMMENT '状态',
  `recipe_type` varchar(32) DEFAULT NULL COMMENT '类型',
  `equipment_id` varchar(32) DEFAULT NULL COMMENT '设备号',
  `equipment_type` varchar(32) DEFAULT NULL COMMENT '设备类型',
  `hold_state` varchar(32) DEFAULT NULL COMMENT 'Hold状态',
  `body` varchar(2048) DEFAULT NULL COMMENT 'Recipe Body(二进制内容)',
  `timestamp` datetime DEFAULT NULL COMMENT '时间戳',
  `check_sum` varchar(32) DEFAULT NULL COMMENT 'checkSum',
  `level_number` int(11) DEFAULT NULL COMMENT '层级',
  `golden_flag` varchar(1) DEFAULT NULL COMMENT '是否是GDRecipe',
  `program_name` varchar(32) DEFAULT NULL COMMENT '程序包名称',
  `program_version` varchar(32) DEFAULT NULL COMMENT '程序包版本',
  `program_subfix` varchar(32) DEFAULT NULL COMMENT '程序包后缀',
  `file_trans_type` varchar(32) DEFAULT NULL COMMENT '文件传输方式(EAP FTP SFTP)',
  `file_check_type` varchar(32) DEFAULT NULL,
  `from_ftp_id` varchar(32) DEFAULT NULL,
  `ftp_id` varchar(32) DEFAULT NULL COMMENT '当前的FTP',
  `full_path` varchar(256) DEFAULT NULL COMMENT '全路径',
  `active_time` datetime DEFAULT NULL COMMENT '激活时间',
  `active_type` varchar(32) DEFAULT NULL COMMENT '激活类型 ByWafer ByLot',
  `active_user` varchar(32) DEFAULT NULL COMMENT '激活者',
  `pattern` varchar(32) DEFAULT NULL COMMENT '模式',
  `reserved1` varchar(32) DEFAULT NULL COMMENT '预留栏位1',
  `reserved2` varchar(32) DEFAULT NULL COMMENT '预留栏位2',
  `reserved3` varchar(32) DEFAULT NULL COMMENT '预留栏位3',
  `reserved4` varchar(32) DEFAULT NULL COMMENT '预留栏位4',
  `reserved5` varchar(32) DEFAULT NULL COMMENT '预留栏位5',
  PRIMARY KEY (`object_rrn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='RecipeEquipment Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `RMS_RECIPE_EQUIPMENT`
--

LOCK TABLES `RMS_RECIPE_EQUIPMENT` WRITE;
/*!40000 ALTER TABLE `RMS_RECIPE_EQUIPMENT` DISABLE KEYS */;
/*!40000 ALTER TABLE `RMS_RECIPE_EQUIPMENT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `RMS_RECIPE_EQUIPMENT_HIS`
--

DROP TABLE IF EXISTS `RMS_RECIPE_EQUIPMENT_HIS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `RMS_RECIPE_EQUIPMENT_HIS` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `recipe_equipment_rrn` bigint(20) DEFAULT NULL COMMENT '对应数据的主键',
  `trans_type` varchar(32) DEFAULT NULL COMMENT '操作类型',
  `recipe_name` varchar(32) DEFAULT NULL COMMENT 'recipe名称',
  `version` bigint(20) DEFAULT NULL COMMENT '版本',
  `lot_id` varchar(32) DEFAULT NULL COMMENT '请求的批次号',
  `status` varchar(32) DEFAULT NULL COMMENT '状态',
  `recipe_type` varchar(32) DEFAULT NULL COMMENT '类型',
  `equipment_id` varchar(32) DEFAULT NULL COMMENT '设备号',
  `equipment_type` varchar(32) DEFAULT NULL COMMENT '设备类型',
  `hold_state` varchar(32) DEFAULT NULL COMMENT 'Hold状态',
  `body` varchar(2048) DEFAULT NULL COMMENT 'Recipe Body(二进制内容)',
  `timestamp` datetime DEFAULT NULL COMMENT '时间戳',
  `check_sum` varchar(32) DEFAULT NULL COMMENT 'checkSum',
  `level_number` int(11) DEFAULT NULL COMMENT '层级',
  `golden_flag` varchar(1) DEFAULT NULL COMMENT '是否是GDRecipe',
  `active_time` datetime DEFAULT NULL COMMENT '激活时间',
  `active_type` varchar(32) DEFAULT NULL COMMENT '激活类型 ByWafer ByLot',
  `active_user` varchar(32) DEFAULT NULL COMMENT '激活者',
  `pattern` varchar(32) DEFAULT NULL COMMENT '模式',
  `program_name` varchar(32) DEFAULT NULL COMMENT '程序包名称',
  `program_version` varchar(32) DEFAULT NULL COMMENT '程序包版本',
  `program_subfix` varchar(32) DEFAULT NULL COMMENT '程序包后缀',
  `file_trans_type` varchar(32) DEFAULT NULL COMMENT '文件传输方式(EAP FTP SFTP)',
  `from_ftp_id` varchar(32) DEFAULT NULL,
  `file_check_type` varchar(32) DEFAULT NULL,
  `ftp_id` varchar(32) DEFAULT NULL COMMENT '当前的FTP',
  `full_path` varchar(256) DEFAULT NULL COMMENT '全路径',
  `from_recipe_name` varchar(32) DEFAULT NULL,
  `from_recipe_version` bigint(20) DEFAULT NULL,
  `from_equipment_id` varchar(32) DEFAULT NULL,
  `from_equipment_type` varchar(32) DEFAULT NULL,
  `from_pattern` varchar(32) DEFAULT NULL,
  `action_code` varchar(32) DEFAULT NULL COMMENT '动作码',
  `action_reason` varchar(32) DEFAULT NULL COMMENT '动作原因',
  `action_comment` varchar(32) DEFAULT NULL COMMENT '备注',
  `history_seq` varchar(64) DEFAULT NULL COMMENT '序列号',
  `reserved1` varchar(32) DEFAULT NULL COMMENT '预留栏位1',
  `reserved2` varchar(32) DEFAULT NULL COMMENT '预留栏位2',
  `reserved3` varchar(32) DEFAULT NULL COMMENT '预留栏位3',
  `reserved4` varchar(32) DEFAULT NULL COMMENT '预留栏位4',
  `reserved5` varchar(32) DEFAULT NULL COMMENT '预留栏位5',
  PRIMARY KEY (`object_rrn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='RecipeEquipment Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `RMS_RECIPE_EQUIPMENT_HIS`
--

LOCK TABLES `RMS_RECIPE_EQUIPMENT_HIS` WRITE;
/*!40000 ALTER TABLE `RMS_RECIPE_EQUIPMENT_HIS` DISABLE KEYS */;
/*!40000 ALTER TABLE `RMS_RECIPE_EQUIPMENT_HIS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `RMS_RECIPE_EQUIPMENT_PARAMETER`
--

DROP TABLE IF EXISTS `RMS_RECIPE_EQUIPMENT_PARAMETER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `RMS_RECIPE_EQUIPMENT_PARAMETER` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `recipe_equipment_rrn` bigint(20) DEFAULT '0' COMMENT 'Recipe主键',
  `parameter_name` varchar(64) DEFAULT NULL COMMENT '参数名称',
  `parameter_desc` varchar(64) DEFAULT NULL COMMENT '参数描述',
  `parameter_group` varchar(64) DEFAULT NULL COMMENT '参数组',
  `seq_no` int(11) DEFAULT NULL COMMENT '序号',
  `data_type` varchar(32) DEFAULT NULL COMMENT '数据类型',
  `compare_flag` varchar(1) DEFAULT NULL COMMENT '是否比较',
  `validate_type` varchar(32) DEFAULT NULL COMMENT '验证类型',
  `min_value` varchar(32) DEFAULT NULL COMMENT '最小值',
  `max_value` varchar(32) DEFAULT NULL COMMENT '最大值',
  `default_value` varchar(32) DEFAULT NULL COMMENT '参数值',
  `special_parameter_flag` varchar(1) DEFAULT NULL COMMENT '是否允许onLine修改',
  `reserved1` varchar(32) DEFAULT NULL COMMENT '预留栏位1',
  `reserved2` varchar(32) DEFAULT NULL COMMENT '预留栏位2',
  `reserved3` varchar(32) DEFAULT NULL COMMENT '预留栏位3',
  `reserved4` varchar(32) DEFAULT NULL COMMENT '预留栏位4',
  `reserved5` varchar(32) DEFAULT NULL COMMENT '预留栏位5',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_RECIPE_EQP_PARAM_1` (`recipe_equipment_rrn`,`parameter_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Recipe Equipment Parameter Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `RMS_RECIPE_EQUIPMENT_PARAMETER`
--

LOCK TABLES `RMS_RECIPE_EQUIPMENT_PARAMETER` WRITE;
/*!40000 ALTER TABLE `RMS_RECIPE_EQUIPMENT_PARAMETER` DISABLE KEYS */;
/*!40000 ALTER TABLE `RMS_RECIPE_EQUIPMENT_PARAMETER` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `RMS_RECIPE_EQUIPMENT_PARAM_TMP`
--

DROP TABLE IF EXISTS `RMS_RECIPE_EQUIPMENT_PARAM_TMP`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `RMS_RECIPE_EQUIPMENT_PARAM_TMP` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `ecn_id` varchar(32) DEFAULT NULL COMMENT '工艺变更号UUID',
  `recipe_equipment_rrn` bigint(20) DEFAULT NULL COMMENT '设备Recipe主键',
  `parameter_name` varchar(64) DEFAULT NULL COMMENT '参数名称',
  `parameter_desc` varchar(64) DEFAULT NULL COMMENT '参数描述',
  `parameter_group` varchar(64) DEFAULT NULL COMMENT '参数组',
  `min_value` varchar(32) DEFAULT NULL COMMENT '最小值',
  `max_value` varchar(32) DEFAULT NULL COMMENT '最大值',
  `parameter_value` varchar(32) DEFAULT NULL COMMENT '具体值',
  `status` varchar(32) DEFAULT NULL COMMENT '状态',
  `expired_policy` varchar(32) DEFAULT NULL COMMENT '过期策略',
  `current_count` int(11) DEFAULT NULL COMMENT '当前次数',
  `life` int(11) DEFAULT NULL COMMENT '周期',
  PRIMARY KEY (`object_rrn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='RecipeEquipment Temp Parameter Info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `RMS_RECIPE_EQUIPMENT_PARAM_TMP`
--

LOCK TABLES `RMS_RECIPE_EQUIPMENT_PARAM_TMP` WRITE;
/*!40000 ALTER TABLE `RMS_RECIPE_EQUIPMENT_PARAM_TMP` DISABLE KEYS */;
/*!40000 ALTER TABLE `RMS_RECIPE_EQUIPMENT_PARAM_TMP` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-04-18 23:33:31
