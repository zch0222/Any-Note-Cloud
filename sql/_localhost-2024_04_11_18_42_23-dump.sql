-- MySQL dump 10.13  Distrib 8.3.0, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: anynote_config
-- ------------------------------------------------------
-- Server version	8.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `config_info`
--

DROP TABLE IF EXISTS `config_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `config_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin COMMENT 'source user',
  `src_ip` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'source ip',
  `app_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '' COMMENT '租户字段',
  `c_desc` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `c_use` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `effect` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `type` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `c_schema` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin,
  `encrypted_data_key` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '秘钥',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfo_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='config_info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config_info`
--

LOCK TABLES `config_info` WRITE;
/*!40000 ALTER TABLE `config_info` DISABLE KEYS */;
INSERT INTO `config_info` VALUES (1,'anynote-gateway-dev.yml','DEFAULT_GROUP','spring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  cloud:\n    gateway:\n      globalcors:\n        corsConfigurations:\n          \'[/**]\':\n            allowedOriginPatterns: \"*\"\n            allowed-methods: \"*\"\n            allowed-headers: \"*\"\n            allow-credentials: true\n            exposedHeaders: \"Content-Disposition,Content-Type,Cache-Control\"         \n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:       \n        # 系统模块\n        - id: anynote-system\n          uri: lb://anynote-system\n          predicates:\n            - Path=/api/system/**\n          filters:\n            - StripPrefix=2\n        - id: anynote-ai\n          uri: lb://anynote-ai\n          predicates:\n            - Path=/api/ai/**\n          filters:\n            - StripPrefix=2\n        # 认证模块\n        - id: anynote-auth\n          uri: lb://anynote-auth\n          predicates:\n            - Path=/api/auth/**\n          filters:\n            - StripPrefix=2\n        - id: anynote-note\n          uri: lb://anynote-note\n          predicates:\n            - Path=/api/note/**\n          filters:\n            - StripPrefix=2\n        - id: anynote-manage\n          uri: lb://anynote-manage\n          predicates:\n            - Path=/api/manage/**\n          filters:\n            - StripPrefix=2\n        - id: anynote-file\n          uri: lb://anynote-file\n          predicates:\n            - Path=/api/file/**\n          filters:\n            - StripPrefix=2\nanynote:\n  module:\n    name: \'anynote-gateway1\'\n\n# 测试属性\nruoyi:\n  # 名称\n  name: RuoYi\n  # 版本\n  version: 1.0.0\n\n# 安全配置\nsecurity:\n  # 超级管理员过滤\n  manage:\n    urls:\n      - /api/manage/**\n  # 验证码\n  captcha:\n    enabled: true\n    type: math\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n  # 不校验白名单\n  ignore:\n    whites:\n      - /auth/logout\n      - /api/auth/login\n      - /auth/register\n      - /*/v2/api-docs\n      - /csrf\n      # - /api/system/*','01e1514757956030502e8e2ab1f90dd8','2023-07-26 03:10:00','2024-04-10 10:59:41',NULL,'0:0:0:0:0:0:0:1','','0587fa28-1301-43db-a7a1-599c00fc3f70','','','','yaml','',''),(9,'anynote-system-dev.yml','DEFAULT_GROUP','# 数据库配置\nspring:\n  datasource:\n    type: com.alibaba.druid.pool.DruidDataSource\n    driverClassName: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/anynote?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: anynote\n    password: Anynote*1832\n  redis:\n    host: localhost\n    port: 6379\n    password:\n\n','2e2080cc0333b1719d19ae6decc69cc5','2023-07-27 08:56:01','2024-03-27 13:49:42',NULL,'154.7.179.45','','0587fa28-1301-43db-a7a1-599c00fc3f70','','','','yaml','',''),(24,'application-dev.yml','DEFAULT_GROUP','spring:\n  http:\n    multipart:\n      enabled: false\n  autoconfigure:\n    exclude: com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure\n  mvc:\n    pathmatch:\n      matching-strategy: ant_path_matcher\n  servlet:\n    encoding:\n      charset: utf-8\n      force: true\n      enabled: true\n    multipart:\n      max-file-size: 500MB\n      max-request-size: 500MB\n    \n\n\nanynote:\n  jwt-setting:\n    # token过期时间单位是分钟\n    tokenExpireTime: 10080\n    secret: yxlm\n  data:\n    rocketmq:\n      note-task-topic: note_task_topic\n      note-task-group: note_task_group\n      note-topic: note_topic\n      note-group: note_group\n      doc-topic: doc_topic\n      doc-group: doc_group\n  external-resources:\n    # 允许引用的资源链接\n    allowed-domains:\n      - anynote.obs.cn-east-3.myhuaweicloud.com\n\n# mybatis-plus配置\nmybatis-plus:\n  configuration:\n    #在映射实体或者属性时，将数据库中表名和字段名中的下划线去掉，按照驼峰命名法映射\n    map-underscore-to-camel-case: true\n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n  global-config:\n    db-config:\n      id-type: auto\n  mapper-locations: classpath:mapper/*.xml\n','d0f22f7e804176b51cfb2d03460c0b00','2023-07-28 09:20:40','2024-04-09 16:43:11',NULL,'0:0:0:0:0:0:0:1','','0587fa28-1301-43db-a7a1-599c00fc3f70','','','','yaml','',''),(29,'anynote-auth-dev.yml','DEFAULT_GROUP','spring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n','8bd9dada9a94822feeab40de55efced6','2023-09-25 13:12:56','2023-09-25 13:41:23',NULL,'116.148.33.74','','0587fa28-1301-43db-a7a1-599c00fc3f70','','','','yaml','',''),(37,'anynote-gateway-dev.yml','DEFAULT_GROUP','spring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  cloud:\n    gateway:\n      globalcors:\n        corsConfigurations:\n          \'[/**]\':\n            allowedOriginPatterns: \"*\"\n            allowed-methods: \"*\"\n            allowed-headers: \"*\"\n            allow-credentials: true\n            exposedHeaders: \"Content-Disposition,Content-Type,Cache-Control\"\n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:       \n        # 系统模块\n        - id: anynote-system\n          uri: lb://anynote-system\n          predicates:\n            - Path=/api/system/**\n          filters:\n            - StripPrefix=2\n        # 认证模块\n        - id: anynote-auth\n          uri: lb://anynote-auth\n          predicates:\n            - Path=/api/auth/**\n          filters:\n            - StripPrefix=2\nanynote:\n  module:\n    name: \'anynote-gateway1\'\n\n# 测试属性\nruoyi:\n  # 名称\n  name: RuoYi\n  # 版本\n  version: 1.0.0\n\n# 安全配置\nsecurity:\n  # 验证码\n  captcha:\n    enabled: true\n    type: math\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n  # 不校验白名单\n  ignore:\n    whites:\n      - /auth/logout\n      - /api/auth/login\n      - /auth/register\n      - /*/v2/api-docs\n      - /csrf\n      # - /api/system/*','911ad69f8c162cd8db0f25d8a09beb19','2023-09-26 07:16:47','2023-09-27 03:53:42','','192.168.186.1','','b33fac79-0b97-464e-9d1e-b01bb9a48e53','','','','yaml','',''),(38,'anynote-system-dev.yml','DEFAULT_GROUP','# 数据库配置\nspring:\n  datasource:\n    type: com.alibaba.druid.pool.DruidDataSource\n    driverClassName: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://111.229.158.174:3306/anynote?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: anynote\n    password: Anynote*1832\n  redis:\n    host: localhost\n    port: 6379\n    password:\n\n','e1eee1408cdd40d139cf3dd7b147a380','2023-09-26 07:16:47','2023-09-26 07:16:47',NULL,'116.148.33.74','','b33fac79-0b97-464e-9d1e-b01bb9a48e53','',NULL,NULL,'yaml',NULL,''),(39,'application-dev.yml','DEFAULT_GROUP','spring:\n  autoconfigure:\n    exclude: com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure\n  mvc:\n    pathmatch:\n      matching-strategy: ant_path_matcher\n\nanynote:\n  jwt-setting:\n    # token过期时间单位是分钟\n    tokenExpireTime: 30\n    secret: yxlm\n\n# mybatis-plus配置\nmybatis-plus:\n  configuration:\n    #在映射实体或者属性时，将数据库中表名和字段名中的下划线去掉，按照驼峰命名法映射\n    map-underscore-to-camel-case: true\n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n  global-config:\n    db-config:\n      id-type: auto\n  mapper-locations: classpath:mapper/*.xml','d70a09aa7a6501589265f206042fb0b3','2023-09-26 07:16:47','2023-09-26 07:16:47',NULL,'116.148.33.74','','b33fac79-0b97-464e-9d1e-b01bb9a48e53','',NULL,NULL,'yaml',NULL,''),(40,'anynote-auth-dev.yml','DEFAULT_GROUP','spring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n','8bd9dada9a94822feeab40de55efced6','2023-09-26 07:16:47','2023-09-26 07:16:47',NULL,'116.148.33.74','','b33fac79-0b97-464e-9d1e-b01bb9a48e53','',NULL,NULL,'yaml',NULL,''),(46,'anynote-note-dev.yml','DEFAULT_GROUP','# 数据库配置\nspring:\n  datasource:\n    type: com.alibaba.druid.pool.DruidDataSource\n    driverClassName: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/anynote?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: anynote\n    password: Anynote*1832\n  redis:\n    host: localhost\n    port: 6379\n    password:\n\nrocketmq:\n  name-server: localhost:9876\n  producer:\n    group: note-group\n\n','b8cf77d54c91a03f933640d337c69692','2023-09-27 05:22:42','2024-03-27 13:50:09',NULL,'154.7.179.45','','0587fa28-1301-43db-a7a1-599c00fc3f70','','','','yaml','',''),(51,'anynote-file-dev.yml','DEFAULT_GROUP','spring:\n  datasource:\n    type: com.alibaba.druid.pool.DruidDataSource\n    driverClassName: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/anynote?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: anynote\n    password: Anynote*1832\n  redis:\n    host: localhost\n    port: 6379\n    password:','baf625643a5a92004ec94d1deec45f63','2023-10-02 07:56:58','2024-03-27 13:50:28',NULL,'154.7.179.45','','0587fa28-1301-43db-a7a1-599c00fc3f70','','','','yaml','',''),(65,'anynote-manage-dev.yml','DEFAULT_GROUP','# 数据库配置\nspring:\n  datasource:\n    type: com.alibaba.druid.pool.DruidDataSource\n    driverClassName: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/anynote?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: anynote\n    password: Anynote*1832\n  redis:\n    host: localhost\n    port: 6379\n    password:','e58b01173e0cff3be3cb789ab35a2666','2023-11-11 11:16:06','2024-03-27 13:50:45',NULL,'154.7.179.45','','0587fa28-1301-43db-a7a1-599c00fc3f70','','','','yaml','',''),(87,'anynote-ai-dev.yml','DEFAULT_GROUP','spring:\n  datasource:\n    type: com.alibaba.druid.pool.DruidDataSource\n    driverClassName: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/anynote?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: anynote\n    password: Anynote*1832\n  redis:\n    host: localhost\n    port: 6379\n    password:','baf625643a5a92004ec94d1deec45f63','2024-04-10 10:58:59','2024-04-10 11:02:50',NULL,'0:0:0:0:0:0:0:1','','0587fa28-1301-43db-a7a1-599c00fc3f70','','','','yaml','','');
/*!40000 ALTER TABLE `config_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config_info_aggr`
--

DROP TABLE IF EXISTS `config_info_aggr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `config_info_aggr` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'group_id',
  `datum_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'datum_id',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '内容',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `app_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfoaggr_datagrouptenantdatum` (`data_id`,`group_id`,`tenant_id`,`datum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='增加租户字段';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config_info_aggr`
--

LOCK TABLES `config_info_aggr` WRITE;
/*!40000 ALTER TABLE `config_info_aggr` DISABLE KEYS */;
/*!40000 ALTER TABLE `config_info_aggr` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config_info_beta`
--

DROP TABLE IF EXISTS `config_info_beta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `config_info_beta` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'app_name',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'content',
  `beta_ips` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'betaIps',
  `md5` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin COMMENT 'source user',
  `src_ip` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'source ip',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '' COMMENT '租户字段',
  `encrypted_data_key` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '秘钥',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfobeta_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='config_info_beta';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config_info_beta`
--

LOCK TABLES `config_info_beta` WRITE;
/*!40000 ALTER TABLE `config_info_beta` DISABLE KEYS */;
/*!40000 ALTER TABLE `config_info_beta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config_info_tag`
--

DROP TABLE IF EXISTS `config_info_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `config_info_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '' COMMENT 'tenant_id',
  `tag_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'tag_id',
  `app_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'app_name',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin COMMENT 'source user',
  `src_ip` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'source ip',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfotag_datagrouptenanttag` (`data_id`,`group_id`,`tenant_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='config_info_tag';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config_info_tag`
--

LOCK TABLES `config_info_tag` WRITE;
/*!40000 ALTER TABLE `config_info_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `config_info_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config_tags_relation`
--

DROP TABLE IF EXISTS `config_tags_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `config_tags_relation` (
  `id` bigint NOT NULL COMMENT 'id',
  `tag_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'tag_name',
  `tag_type` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'tag_type',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '' COMMENT 'tenant_id',
  `nid` bigint NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`nid`),
  UNIQUE KEY `uk_configtagrelation_configidtag` (`id`,`tag_name`,`tag_type`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='config_tag_relation';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config_tags_relation`
--

LOCK TABLES `config_tags_relation` WRITE;
/*!40000 ALTER TABLE `config_tags_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `config_tags_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_capacity`
--

DROP TABLE IF EXISTS `group_capacity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_capacity` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
  `quota` int unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
  `usage` int unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
  `max_size` int unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数，，0表示使用默认值',
  `max_aggr_size` int unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='集群、各Group容量信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_capacity`
--

LOCK TABLES `group_capacity` WRITE;
/*!40000 ALTER TABLE `group_capacity` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_capacity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `his_config_info`
--

DROP TABLE IF EXISTS `his_config_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `his_config_info` (
  `id` bigint unsigned NOT NULL,
  `nid` bigint unsigned NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `app_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'app_name',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `md5` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `src_user` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin,
  `src_ip` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `op_type` char(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '' COMMENT '租户字段',
  `encrypted_data_key` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '秘钥',
  PRIMARY KEY (`nid`),
  KEY `idx_gmt_create` (`gmt_create`),
  KEY `idx_gmt_modified` (`gmt_modified`),
  KEY `idx_did` (`data_id`)
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='多租户改造';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `his_config_info`
--

LOCK TABLES `his_config_info` WRITE;
/*!40000 ALTER TABLE `his_config_info` DISABLE KEYS */;
INSERT INTO `his_config_info` VALUES (9,89,'anynote-system-dev.yml','DEFAULT_GROUP','','# 数据库配置\nspring:\n  datasource:\n    type: com.alibaba.druid.pool.DruidDataSource\n    driverClassName: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://47.99.127.211:3306/anynote?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: anynote\n    password: Anynote*1832\n  redis:\n    host: localhost\n    port: 6379\n    password:\n\n','379685ad4312ea2c35b424a97b16540f','2024-03-27 13:49:41','2024-03-27 13:49:42',NULL,'154.7.179.45','U','0587fa28-1301-43db-a7a1-599c00fc3f70',''),(46,90,'anynote-note-dev.yml','DEFAULT_GROUP','','# 数据库配置\nspring:\n  datasource:\n    type: com.alibaba.druid.pool.DruidDataSource\n    driverClassName: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://47.99.127.211:3306/anynote?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: anynote\n    password: Anynote*1832\n  redis:\n    host: localhost\n    port: 6379\n    password:\n\nrocketmq:\n  name-server: localhost:9876\n  producer:\n    group: note-group\n\n','5721b6ae82646942e67e98528623e6ea','2024-03-27 13:50:09','2024-03-27 13:50:09',NULL,'154.7.179.45','U','0587fa28-1301-43db-a7a1-599c00fc3f70',''),(51,91,'anynote-file-dev.yml','DEFAULT_GROUP','','spring:\n  datasource:\n    type: com.alibaba.druid.pool.DruidDataSource\n    driverClassName: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://47.99.127.211:3306/anynote?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: anynote\n    password: Anynote*1832\n  redis:\n    host: localhost\n    port: 6379\n    password:','1c76a0365587bab4e452055c4c3fa49e','2024-03-27 13:50:27','2024-03-27 13:50:28',NULL,'154.7.179.45','U','0587fa28-1301-43db-a7a1-599c00fc3f70',''),(65,92,'anynote-manage-dev.yml','DEFAULT_GROUP','','# 数据库配置\nspring:\n  datasource:\n    type: com.alibaba.druid.pool.DruidDataSource\n    driverClassName: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://47.99.127.211:3306/anynote?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: anynote\n    password: Anynote*1832\n  redis:\n    host: localhost\n    port: 6379\n    password:','ee68196050599d9fc0d335e5c2d9724f','2024-03-27 13:50:45','2024-03-27 13:50:45',NULL,'154.7.179.45','U','0587fa28-1301-43db-a7a1-599c00fc3f70',''),(24,93,'application-dev.yml','DEFAULT_GROUP','','spring:\n  http:\n    multipart:\n      enabled: false\n  autoconfigure:\n    exclude: com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure\n  mvc:\n    pathmatch:\n      matching-strategy: ant_path_matcher\n  servlet:\n    encoding:\n      charset: utf-8\n      force: true\n      enabled: true\n    multipart:\n      max-file-size: 500MB\n      max-request-size: 500MB\n    \n\n\nanynote:\n  jwt-setting:\n    # token过期时间单位是分钟\n    tokenExpireTime: 10080\n    secret: yxlm\n  data:\n    rocketmq:\n      note-task-topic: note_task_topic\n      note-task-group: note_task_group\n      note-topic: note_topic\n      note-group: note_group\n  external-resources:\n    # 允许引用的资源链接\n    allowed-domains:\n      - anynote.obs.cn-east-3.myhuaweicloud.com\n\n# mybatis-plus配置\nmybatis-plus:\n  configuration:\n    #在映射实体或者属性时，将数据库中表名和字段名中的下划线去掉，按照驼峰命名法映射\n    map-underscore-to-camel-case: true\n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n  global-config:\n    db-config:\n      id-type: auto\n  mapper-locations: classpath:mapper/*.xml\n','aef2da759c92cdf5dec14623bb87d2dd','2024-04-10 00:43:11','2024-04-09 16:43:11',NULL,'0:0:0:0:0:0:0:1','U','0587fa28-1301-43db-a7a1-599c00fc3f70',''),(0,94,'anynote-ai-dev.yml','DEFAULT_GROUP','','spring:\r\n  redis:\r\n    host: localhost\r\n    port: 6379\r\n    password:','9fcbf3486b77a337b0c1b42eb0e0f9b8','2024-04-10 18:58:59','2024-04-10 10:58:59',NULL,'0:0:0:0:0:0:0:1','I','0587fa28-1301-43db-a7a1-599c00fc3f70',''),(1,95,'anynote-gateway-dev.yml','DEFAULT_GROUP','','spring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  cloud:\n    gateway:\n      globalcors:\n        corsConfigurations:\n          \'[/**]\':\n            allowedOriginPatterns: \"*\"\n            allowed-methods: \"*\"\n            allowed-headers: \"*\"\n            allow-credentials: true\n            exposedHeaders: \"Content-Disposition,Content-Type,Cache-Control\"         \n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:       \n        # 系统模块\n        - id: anynote-system\n          uri: lb://anynote-system\n          predicates:\n            - Path=/api/system/**\n          filters:\n            - StripPrefix=2\n        # 认证模块\n        - id: anynote-auth\n          uri: lb://anynote-auth\n          predicates:\n            - Path=/api/auth/**\n          filters:\n            - StripPrefix=2\n        - id: anynote-note\n          uri: lb://anynote-note\n          predicates:\n            - Path=/api/note/**\n          filters:\n            - StripPrefix=2\n        - id: anynote-manage\n          uri: lb://anynote-manage\n          predicates:\n            - Path=/api/manage/**\n          filters:\n            - StripPrefix=2\n        - id: anynote-file\n          uri: lb://anynote-file\n          predicates:\n            - Path=/api/file/**\n          filters:\n            - StripPrefix=2\nanynote:\n  module:\n    name: \'anynote-gateway1\'\n\n# 测试属性\nruoyi:\n  # 名称\n  name: RuoYi\n  # 版本\n  version: 1.0.0\n\n# 安全配置\nsecurity:\n  # 超级管理员过滤\n  manage:\n    urls:\n      - /api/manage/**\n  # 验证码\n  captcha:\n    enabled: true\n    type: math\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n  # 不校验白名单\n  ignore:\n    whites:\n      - /auth/logout\n      - /api/auth/login\n      - /auth/register\n      - /*/v2/api-docs\n      - /csrf\n      # - /api/system/*','7b54198c2aa8e3ecde265bddb5a27708','2024-04-10 18:59:40','2024-04-10 10:59:41',NULL,'0:0:0:0:0:0:0:1','U','0587fa28-1301-43db-a7a1-599c00fc3f70',''),(87,96,'anynote-ai-dev.yml','DEFAULT_GROUP','','spring:\r\n  redis:\r\n    host: localhost\r\n    port: 6379\r\n    password:','9fcbf3486b77a337b0c1b42eb0e0f9b8','2024-04-10 19:02:50','2024-04-10 11:02:50',NULL,'0:0:0:0:0:0:0:1','U','0587fa28-1301-43db-a7a1-599c00fc3f70','');
/*!40000 ALTER TABLE `his_config_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permissions`
--

DROP TABLE IF EXISTS `permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permissions` (
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `resource` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `action` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  UNIQUE KEY `uk_role_permission` (`role`,`resource`,`action`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permissions`
--

LOCK TABLES `permissions` WRITE;
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  UNIQUE KEY `idx_user_role` (`username`,`role`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES ('nacos','ROLE_ADMIN');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tenant_capacity`
--

DROP TABLE IF EXISTS `tenant_capacity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tenant_capacity` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL DEFAULT '' COMMENT 'Tenant ID',
  `quota` int unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
  `usage` int unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
  `max_size` int unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数',
  `max_aggr_size` int unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='租户容量信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tenant_capacity`
--

LOCK TABLES `tenant_capacity` WRITE;
/*!40000 ALTER TABLE `tenant_capacity` DISABLE KEYS */;
/*!40000 ALTER TABLE `tenant_capacity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tenant_info`
--

DROP TABLE IF EXISTS `tenant_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tenant_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `kp` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'kp',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '' COMMENT 'tenant_id',
  `tenant_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '' COMMENT 'tenant_name',
  `tenant_desc` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'tenant_desc',
  `create_source` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'create_source',
  `gmt_create` bigint NOT NULL COMMENT '创建时间',
  `gmt_modified` bigint NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_info_kptenantid` (`kp`,`tenant_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='tenant_info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tenant_info`
--

LOCK TABLES `tenant_info` WRITE;
/*!40000 ALTER TABLE `tenant_info` DISABLE KEYS */;
INSERT INTO `tenant_info` VALUES (1,'1','0587fa28-1301-43db-a7a1-599c00fc3f70','dev','开发环境','nacos',1690340127460,1690340127460),(2,'1','b33fac79-0b97-464e-9d1e-b01bb9a48e53','local','本地开发','nacos',1695712594567,1695712594567);
/*!40000 ALTER TABLE `tenant_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('jack','$2a$10$7SQb5t4FbAgwrdFdh51znedrWGEvXE83qH85AG5UW7SVyY4UsU55C',1),('nacos','$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu',1),('nacos_admin','$2a$10$YRJGfMk6R/sDxtQunQcjLe3bnySrUUht/gsL2tt7SoQE3vZY0w0gG',1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-04-11 18:42:24
