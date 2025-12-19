-- @formatter:off

-- ----------------------------
-- Table structure for api
-- ----------------------------
DROP TABLE IF EXISTS `api`;
CREATE TABLE `api` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `name` varchar(600) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '名称',
  `operation_id` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '编码，对应OAS operationId',
  `scopes` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '对应OAS安全需求',
  `uri` varchar(800) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '接口地址',
  `method` varchar(16) COLLATE utf8mb4_bin NOT NULL COMMENT '请求方法',
  `type` varchar(16) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '接口类型(ApiType)',
  `description` text COLLATE utf8mb4_bin COMMENT '描述',
  `resource_name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '资源名',
  `resource_description` varchar(200) COLLATE utf8mb4_bin NOT NULL COMMENT '资源描述',
  `enabled` int(1) NOT NULL COMMENT '有效状态',
  `service_id` bigint(20) NOT NULL COMMENT '服务ID',
  `service_code` varchar(80) COLLATE utf8mb4_bin NOT NULL COMMENT '所属服务编码',
  `service_name` varchar(200) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '所属服务名称',
  `service_enabled` int(1) DEFAULT NULL COMMENT '服务启用状态',
  `sync` int(1) NOT NULL COMMENT '是否是同步接口',
  `swagger_deleted` int(1) DEFAULT NULL COMMENT '接口是否从Swagger删除',
  `created_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  `modified_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '最后修改人',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_service_code` (`operation_id`,`service_code`) USING BTREE,
  KEY `idx_service_name` (`service_name`) USING BTREE,
  KEY `uidx_name` (`name`) USING BTREE,
  KEY `idx_service_code` (`service_code`) USING BTREE,
  KEY `idx_summary_group` (`method`,`type`,`enabled`,`created_date`) USING BTREE,
  FULLTEXT KEY `fx_name_code_service_name_description` (`name`,`operation_id`,`service_name`,`description`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='接口表';

-- ----------------------------
-- Table structure for api_log
-- ----------------------------
DROP TABLE IF EXISTS `api_log`;
CREATE TABLE `api_log` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `client_id` varchar(80) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '访问客户端ID',
  `client_source` varchar(20) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '访问客户端来源',
  `remote` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '访问者地址',
  `tenant_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '访问租户ID',
  `tenant_name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '-1' COMMENT '访问租户名称',
  `user_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '访问用户ID',
  `full_name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '访问用户名称',
  `request_id` varchar(40) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '请求ID',
  `service_code` varchar(80) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '服务编码',
  `service_name` varchar(80) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '服务名称',
  `instance_id` varchar(40) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '实例ID',
  `api_type` varchar(16) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '接口类型(ApiType)：API, OPEN_API, DOOR_API, PUB_API, FILE, DOOR_FILE, PUB_FILE, VIEW, PUB_VIEW',
  `api_code` varchar(80) COLLATE utf8mb4_bin DEFAULT '' COMMENT 'API编码',
  `api_name` varchar(160) COLLATE utf8mb4_bin DEFAULT '' COMMENT 'API名称',
  `resource_name` varchar(80) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '资源名称',
  `method` varchar(16) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '请求方法',
  `uri` varchar(200) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '请求URI',
  `status` int(11) NOT NULL COMMENT 'Http请求状态',
  `request_date` datetime NOT NULL COMMENT '服务接收到请求时间',
  `query_param` text COLLATE utf8mb4_bin COMMENT '查询参数',
  `request_headers` text COLLATE utf8mb4_bin COMMENT '请求头',
  `request_body` mediumtext COLLATE utf8mb4_bin COMMENT '请求体',
  `request_size` int(11) NOT NULL DEFAULT '0' COMMENT '请求报文大小',
  `response_date` timestamp NULL DEFAULT NULL COMMENT '服务响应返回时间',
  `response_headers` text COLLATE utf8mb4_bin COMMENT '响应头',
  `response_body` mediumtext COLLATE utf8mb4_bin COMMENT '响应体',
  `response_size` int(11) NOT NULL DEFAULT '0' COMMENT '响应报文大小',
  `success` int(11) NOT NULL COMMENT '成功标记：1-成功；0-失败',
  `elapsed_millis` bigint(20) NOT NULL DEFAULT '0' COMMENT '请求耗时(单位ms)',
  `created_date` timestamp NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_service_code` (`service_code`) USING BTREE,
  KEY `idx_request_id` (`request_id`) USING BTREE,
  KEY `idx_api_code` (`api_code`) USING BTREE,
  KEY `idx_created_date` (`created_date`) USING BTREE,
  KEY `idx_request_date` (`request_date`) USING BTREE,
  KEY `idx_method` (`method`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_api_type` (`api_type`) USING BTREE,
  KEY `idx_client_source` (`client_source`) USING BTREE,
  KEY `idx_uri` (`uri`) USING BTREE,
  KEY `idx_success` (`success`) USING BTREE,
  KEY `idx_summary_group` (`request_date`,`api_type`,`method`,`status`,`success`,`client_source`) USING BTREE,
  KEY `idx_summary_tenant_group` (`tenant_id`,`request_date`,`api_type`,`method`,`status`,`success`,`client_source`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='API日志';

-- ----------------------------
-- Table structure for app
-- ----------------------------
DROP TABLE IF EXISTS `app`;
CREATE TABLE `app` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `code` varchar(80) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '编码',
  `name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '名称',
  `show_name` varchar(40) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '展示名称',
  `icon` varchar(200) COLLATE utf8mb4_bin NOT NULL COMMENT '图标',
  `type` varchar(10) COLLATE utf8mb4_bin NOT NULL COMMENT '应用类型：CLOUD_APP、BASE_APP、OP_APP',
  `edition_type` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '版本类型',
  `description` varchar(200) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '描述',
  `auth_ctrl` int(1) DEFAULT NULL COMMENT '授权控制标志态：0-不控制；1-控制',
  `enabled` int(1) NOT NULL COMMENT '启用状态：0-禁用；1-启用',
  `url` varchar(200) COLLATE utf8mb4_bin NOT NULL COMMENT 'URL',
  `sequence` int(11) NOT NULL DEFAULT '1' COMMENT '序号，值越小越靠前',
  `api_ids` varchar(800) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '关联接口编码',
  `version` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '应用版本：major(主版本号)、minor(次版本号)、patch(修订号)',
  `open_stage` varchar(16) COLLATE utf8mb4_bin NOT NULL COMMENT '开通阶段：(SINUP)注册、AUTH_PASSED(实名认证通过户) 、OPEN_SUCCESS(开通成功)',
  `client_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '所属端ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `created_by` bigint(20) NOT NULL COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  `modified_by` bigint(20) NOT NULL COMMENT '最后修改人',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_code_edition_version` (`code`,`edition_type`,`version`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_enabled_fl_id_sequence` (`enabled`,`id`,`sequence`) USING BTREE,
  KEY `uidx_show_name_name` (`show_name`,`code`) USING BTREE,
  KEY `idx_edition_type` (`edition_type`) USING BTREE,
  KEY `idx_version` (`version`) USING BTREE,
  FULLTEXT KEY `fx_name_code_show_name_description` (`code`,`name`,`show_name`,`description`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='应用表';

-- ----------------------------
-- Table structure for app_func
-- ----------------------------
DROP TABLE IF EXISTS `app_func`;
CREATE TABLE `app_func` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `code` varchar(80) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '编码，规则名称英文蛇形表示',
  `name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '名称',
  `show_name` varchar(40) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '展示名称',
  `pid` bigint(20) NOT NULL DEFAULT '-1' COMMENT '上级功能ID',
  `icon` varchar(200) COLLATE utf8mb4_bin NOT NULL COMMENT '图标地址',
  `type` varchar(10) COLLATE utf8mb4_bin NOT NULL COMMENT '功能类型：MENU、BUTTON、PANEL',
  `description` varchar(200) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '描述',
  `auth_ctrl` int(1) NOT NULL COMMENT '授权控制标志态',
  `enabled` int(1) NOT NULL COMMENT '启用状态',
  `url` varchar(200) COLLATE utf8mb4_bin NOT NULL COMMENT 'URL',
  `app_id` bigint(20) NOT NULL COMMENT '所属应用ID',
  `sequence` int(11) NOT NULL DEFAULT '1' COMMENT '序号，值越小越靠前',
  `api_ids` varchar(800) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '关联接口ID列表',
  `client_id` varchar(32) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '所属端ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `created_by` bigint(20) NOT NULL COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  `modified_by` bigint(20) NOT NULL COMMENT '最后修改人',
  `modified_date` datetime NOT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_app_id_code` (`app_id`,`code`) USING BTREE,
  KEY `idx_name` (`name`) USING BTREE,
  KEY `idx_pid` (`pid`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_code` (`code`) USING BTREE,
  KEY `idx_enabled` (`enabled`) USING BTREE,
  KEY `idx_sequence` (`sequence`) USING BTREE,
  KEY `idx_client_id` (`client_id`) USING BTREE,
  KEY `idx_type` (`type`) USING BTREE,
  FULLTEXT KEY `fx_name_code_description` (`code`,`name`,`description`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='应用功能关联表';

-- ----------------------------
-- Table structure for app_open
-- ----------------------------
DROP TABLE IF EXISTS `app_open`;
CREATE TABLE `app_open` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `app_id` bigint(20) NOT NULL COMMENT '开通应用ID',
  `app_code` varchar(80) COLLATE utf8mb4_bin NOT NULL COMMENT '开通应用编码',
  `app_type` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '应用类型',
  `edition_type` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '版本类型',
  `version` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '开通版本版本',
  `client_id` varchar(80) COLLATE utf8mb4_bin NOT NULL COMMENT '应用端ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '开通租户ID',
  `user_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '开通用户ID',
  `open_date` datetime NOT NULL COMMENT '开通时间',
  `expiration_date` datetime NOT NULL COMMENT '到期时间',
  `expiration_deleted` int(1) NOT NULL COMMENT '过期删除标志',
  `op_client_open` int(1) NOT NULL COMMENT '运营端开通标志',
  `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_created_date` (`created_date`) USING BTREE,
  KEY `idx_expiration_date` (`expiration_date`) USING BTREE,
  KEY `idx_expiration_deleted` (`expiration_deleted`) USING BTREE,
  KEY `idx_op_client_open` (`op_client_open`) USING BTREE,
  KEY `idx_app_id` (`app_id`) USING BTREE,
  KEY `idx_client_id` (`client_id`) USING BTREE,
  KEY `idx_app_code` (`app_code`) USING BTREE,
  KEY `idx_version` (`version`),
  KEY `idx_edition_type` (`edition_type`),
  KEY `idx_tenant_id_app` (`tenant_id`,`edition_type`,`app_code`,`version`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='应用开通表';

-- ----------------------------
-- Table structure for auth_api_authority
-- ----------------------------
DROP TABLE IF EXISTS `auth_api_authority`;
CREATE TABLE `auth_api_authority` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `source` varchar(16) COLLATE utf8mb4_bin NOT NULL COMMENT '来源类型：APP、APP_FUNC、SYSTEM_TOKEN',
  `source_id` bigint(20) NOT NULL COMMENT '来源ID',
  `source_enabled` int(1) NOT NULL COMMENT '来源启用状态',
  `app_id` bigint(20) DEFAULT NULL COMMENT '应用ID',
  `app_enabled` int(1) DEFAULT NULL COMMENT '应用启用状态',
  `api_id` bigint(20) NOT NULL COMMENT '接口ID',
  `api_operation_id` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '接口操作ID，对应OSA3 operationId',
  `api_scopes` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '对应OAS安全需求',
  `api_enabled` int(1) NOT NULL COMMENT '接口启用状态',
  `service_id` bigint(20) DEFAULT NULL COMMENT '服务ID',
  `service_code` varchar(80) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '服务编码',
  `service_enabled` int(1) DEFAULT NULL COMMENT '服务启用状态',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_service_code` (`service_code`) USING BTREE,
  KEY `idx_source` (`source`) USING BTREE,
  KEY `idx_api_enabled` (`api_enabled`) USING BTREE,
  KEY `idx_service_enabled` (`service_enabled`) USING BTREE,
  KEY `idx_service_id` (`service_id`) USING BTREE,
  KEY `idx_app_id` (`app_id`) USING BTREE,
  KEY `idx_app_enabled` (`app_enabled`) USING BTREE,
  KEY `idx_source_enabled` (`source_enabled`) USING BTREE,
  KEY `idx_source_id` (`source_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='授权接口权限';

-- ----------------------------
-- Table structure for auth_policy
-- ----------------------------
DROP TABLE IF EXISTS `auth_policy`;
CREATE TABLE `auth_policy` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `name` varchar(32) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '名称',
  `code` varchar(80) COLLATE utf8mb4_bin NOT NULL COMMENT '编码，规则名称英文蛇形表示',
  `enabled` int(1) NOT NULL COMMENT '有效状态',
  `type` varchar(20) COLLATE utf8mb4_bin NOT NULL DEFAULT '1' COMMENT '策略分类',
  `default0` int(1) NOT NULL COMMENT '默认权限策略',
  `grant_stage` varchar(20) COLLATE utf8mb4_bin NOT NULL DEFAULT '-1' COMMENT '授权阶段',
  `description` varchar(200) COLLATE utf8mb4_bin DEFAULT '' COMMENT '描述',
  `app_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '所属应用ID',
  `client_id` varchar(80) COLLATE utf8mb4_bin NOT NULL COMMENT '应用所属端',
  `tenant_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '租户ID',
  `created_by` bigint(20) NOT NULL COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  `modified_by` bigint(20) NOT NULL COMMENT '最后修改人',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_name_app_id` (`name`,`tenant_id`,`app_id`) USING BTREE,
  UNIQUE KEY `uidx_code_app_id` (`code`,`tenant_id`,`app_id`) USING BTREE,
  KEY `idx_app_id` (`app_id`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_type` (`type`) USING BTREE,
  KEY `idx_client_id` (`client_id`) USING BTREE,
  KEY `idx_enabled` (`enabled`) USING BTREE,
  FULLTEXT KEY `fx_code_name_description` (`code`,`name`,`description`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='授权策略表';

-- ----------------------------
-- Table structure for auth_policy_func
-- ----------------------------
DROP TABLE IF EXISTS `auth_policy_func`;
CREATE TABLE `auth_policy_func` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `policy_id` bigint(20) NOT NULL COMMENT '策略ID',
  `app_id` bigint(20) NOT NULL COMMENT '应用主键ID',
  `func_id` bigint(20) NOT NULL COMMENT '功能ID',
  `func_type` varchar(11) COLLATE utf8mb4_bin NOT NULL COMMENT '功能类型：MENU、BUTTON、PANEL',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `created_by` bigint(20) NOT NULL COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_func_id` (`policy_id`) USING BTREE,
  KEY `idx_app_id` (`app_id`) USING BTREE,
  KEY `idx_app_pid` (`func_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='授权策略功能关联表';

-- ----------------------------
-- Table structure for auth_policy_org
-- ----------------------------
DROP TABLE IF EXISTS `auth_policy_org`;
CREATE TABLE `auth_policy_org` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `policy_id` bigint(20) NOT NULL COMMENT '策略ID',
  `policy_type` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '策略类型',
  `org_id` bigint(20) NOT NULL COMMENT '授权组织人员ID',
  `org_type` varchar(16) COLLATE utf8mb4_bin NOT NULL COMMENT '授权类型',
  `grant_scope` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '授权范围',
  `open_auth` int(1) NOT NULL COMMENT '开通授权初始化标志',
  `app_id` bigint(20) NOT NULL COMMENT '应用ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `default0` int(11) NOT NULL COMMENT '租户用户默认策略标识',
  `created_by` bigint(20) NOT NULL COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_created_date` (`created_date`) USING BTREE,
  KEY `idx_grant_scope` (`grant_scope`) USING BTREE,
  KEY `idx_default` (`default0`) USING BTREE,
  KEY `idx_open_auth` (`open_auth`) USING BTREE,
  KEY `idx_org_id` (`org_id`) USING BTREE,
  KEY `idx_policy_id` (`policy_id`) USING BTREE,
  KEY `idx_org_type` (`org_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='授权策略租户关联表';

-- ----------------------------
-- Table structure for auth_user_token
-- ----------------------------
DROP TABLE IF EXISTS `auth_user_token`;
CREATE TABLE `auth_user_token` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `name` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '名称',
  `value` varchar(200) COLLATE utf8mb4_bin NOT NULL COMMENT '加密令牌',
  `expired_date` datetime NOT NULL COMMENT '过期时间',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `created_by` bigint(20) NOT NULL COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_created_by_name` (`created_by`,`name`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户令牌表\n';

-- ----------------------------
-- Table structure for c_country
-- ----------------------------
DROP TABLE IF EXISTS `c_country`;
CREATE TABLE `c_country` (
  `id` bigint(20) NOT NULL COMMENT '主键id',
  `code` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '编码',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '名称',
  `chinese_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '中文名称',
  `iso2` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '国家码（二位）',
  `iso3` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '国家码（三位）',
  `abbr` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '英文简称',
  `open` int(1) NOT NULL DEFAULT '1' COMMENT '开通标志',
  `geo` json DEFAULT NULL COMMENT '地址位置数据',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_name` (`name`) USING BTREE,
  UNIQUE KEY `uidx_chinese_nameme` (`chinese_name`) USING BTREE,
  KEY `idx_iso2` (`iso2`) USING BTREE,
  KEY `idx_iso3` (`iso3`) USING BTREE,
  KEY `uidx_code` (`code`) USING BTREE,
  KEY `uidx_abbr` (`abbr`) USING BTREE,
  FULLTEXT KEY `fx_name` (`name`,`chinese_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='国家表';

-- ----------------------------
-- Table structure for c_district
-- ----------------------------
DROP TABLE IF EXISTS `c_district`;
CREATE TABLE `c_district` (
  `id` bigint(20) NOT NULL COMMENT '主键id',
  `code` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '行政区编码',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '行政区名称',
  `pin_yin` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '拼音',
  `parent_code` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '父行政区编码',
  `simple_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '行政区简称',
  `level` int(11) NOT NULL COMMENT '区域等级：0-国家；1-省；2-市；3-区、县',
  `country_code` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '国家编码',
  `country_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '国家名称',
  `city_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '城市编码',
  `zip_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '邮政编码',
  `mer_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '组合名称',
  `lng` float NOT NULL COMMENT '经度',
  `lat` float NOT NULL COMMENT '纬度',
  `geo` json DEFAULT NULL COMMENT '地址位置数据',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_code` (`code`) USING BTREE,
  KEY `idx_name` (`name`) USING BTREE,
  KEY `idx_parent_code` (`parent_code`) USING BTREE,
  KEY `idx_sample_name` (`simple_name`) USING BTREE,
  KEY `idx_city_code` (`city_code`) USING BTREE,
  KEY `idx_zip_code` (`zip_code`) USING BTREE,
  KEY `idx_country_code` (`country_code`) USING BTREE,
  FULLTEXT KEY `fx_name` (`name`,`code`,`pin_yin`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='区域表';

-- ----------------------------
-- Table structure for c_i18n_messages
-- ----------------------------
DROP TABLE IF EXISTS `c_i18n_messages`;
CREATE TABLE `c_i18n_messages` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `type` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '分类',
  `language` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '语言',
  `default_message` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '默认消息',
  `i18n_message` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '国际化消息',
  `private0` int(1) NOT NULL COMMENT '是否私有化',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_key_language_default_message` (`type`,`language`,`default_message`) USING BTREE,
  KEY `idx_type` (`type`) USING BTREE,
  KEY `idx_language` (`language`) USING BTREE,
  KEY `idx_default_message` (`default_message`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='国际化资源表';

-- ----------------------------
-- Table structure for c_setting
-- ----------------------------
DROP TABLE IF EXISTS `c_setting`;
CREATE TABLE `c_setting` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `key` varchar(32) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '配置参数key',
  `value` varchar(16000) COLLATE utf8mb4_bin NOT NULL COMMENT '配置参数值',
  `global_default` int(1) NOT NULL COMMENT '是否全局默认值标志',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_key` (`key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='配置表';

-- ----------------------------
-- Table structure for c_setting_tenant
-- ----------------------------
DROP TABLE IF EXISTS `c_setting_tenant`;
CREATE TABLE `c_setting_tenant` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `invitation_code` varchar(80) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '邀请注册码，检索冗余',
  `locale_data` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户国际化设置',
  `func_data` varchar(320) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户平台默认功能指标',
  `perf_data` varchar(320) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户平台默认性能指标',
  `stability_data` varchar(320) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户平台默认稳定性指标',
  `security_data` varchar(1200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户账号安全设置',
  `server_api_proxy_data` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '服务端Api 代理配置',
  `tester_event_data` varchar(2000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'AngusTester事件通知类型配置',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_tenant_id` (`tenant_id`) USING BTREE,
  UNIQUE KEY `uidx_invitation_code` (`invitation_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='租户设置';

-- ----------------------------
-- Table structure for c_setting_tenant_quota
-- ----------------------------
DROP TABLE IF EXISTS `c_setting_tenant_quota`;
CREATE TABLE `c_setting_tenant_quota` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `app_code` varchar(80) COLLATE utf8mb4_bin NOT NULL COMMENT '所属应用编码',
  `service_code` varchar(80) COLLATE utf8mb4_bin NOT NULL COMMENT '所属服务编码',
  `name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '资源名称',
  `allow_change` int(1) NOT NULL COMMENT '是否允许修改配额',
  `license_ctrl` int(1) NOT NULL COMMENT '是否许可控制配置',
  `calc_remaining` int(1) NOT NULL COMMENT '是否计算剩余配额',
  `quota` bigint(20) NOT NULL COMMENT '当前生效配额值',
  `min` bigint(20) NOT NULL DEFAULT '0' COMMENT '最小允许配置值',
  `max` bigint(20) NOT NULL DEFAULT '0' COMMENT '最大允许配置值',
  `capacity` bigint(20) NOT NULL COMMENT '总容量（所有租户上线）',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_tenant_id_name` (`tenant_id`,`name`) USING BTREE,
  KEY `idx_app_code` (`app_code`) USING BTREE,
  KEY `idx_service_code` (`service_code`) USING BTREE,
  KEY `idx_resource` (`name`) USING BTREE,
  KEY `idx_quota` (`quota`) USING BTREE,
  KEY `idx_allow_change` (`allow_change`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  FULLTEXT KEY `fx_name` (`name`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='租户配额设置';

-- ----------------------------
-- Table structure for c_setting_user
-- ----------------------------
DROP TABLE IF EXISTS `c_setting_user`;
CREATE TABLE `c_setting_user` (
  `id` bigint(20) unsigned NOT NULL COMMENT '主键ID',
  `preference` varchar(800) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '偏好设置',
  `api_proxy` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户代理API配置',
  `social_bind` varchar(1200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '三方登录用户绑定信息',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户';

-- ----------------------------
-- Table structure for dept
-- ----------------------------
DROP TABLE IF EXISTS `dept`;
CREATE TABLE `dept` (
  `id` bigint(20) unsigned NOT NULL COMMENT '主键ID',
  `tenant_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '租户ID',
  `code` varchar(32) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '部门编码',
  `parent_like_id` varchar(200) COLLATE utf8mb4_bin DEFAULT '' COMMENT '部门系统ID：所以父部门ID符号“-”相连',
  `name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '机构名称',
  `pid` bigint(20) NOT NULL DEFAULT '-1' COMMENT '上级机构ID',
  `level` int(11) NOT NULL COMMENT '部分层级',
  `created_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  `modified_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '最后修改人',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_pid` (`pid`) USING BTREE,
  KEY `idx_parent_like_id` (`parent_like_id`) USING BTREE,
  KEY `idx_name` (`name`) USING BTREE,
  KEY `idx_code` (`code`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_created_date` (`created_date`) USING BTREE,
  KEY `idx_level` (`level`) USING BTREE,
  FULLTEXT KEY `fx_name_code` (`code`,`name`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='部门';

-- ----------------------------
-- Table structure for dept_user
-- ----------------------------
DROP TABLE IF EXISTS `dept_user`;
CREATE TABLE `dept_user` (
  `id` bigint(20) unsigned NOT NULL COMMENT '主键ID',
  `dept_id` bigint(100) NOT NULL DEFAULT '-1' COMMENT '部门编码',
  `user_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '用户id',
  `main_dept` int(1) NOT NULL COMMENT '是否主部门',
  `dept_head` int(1) NOT NULL DEFAULT '0' COMMENT '是否部门负责人',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `created_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_dept_id` (`dept_id`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `uidx_dept_user_id` (`dept_id`,`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='部门用户关系表';

-- ----------------------------
-- Table structure for email
-- ----------------------------
DROP TABLE IF EXISTS `email`;
CREATE TABLE `email` (
  `id` bigint(20) NOT NULL COMMENT 'PK',
  `biz_key` varchar(80) COLLATE utf8mb4_bin DEFAULT '',
  `language` varchar(40) COLLATE utf8mb4_bin NOT NULL COMMENT '语言',
  `template_code` varchar(80) COLLATE utf8mb4_bin DEFAULT NULL,
  `out_id` varchar(80) COLLATE utf8mb4_bin NOT NULL,
  `type` varchar(16) COLLATE utf8mb4_bin NOT NULL,
  `subject` varchar(400) COLLATE utf8mb4_bin NOT NULL DEFAULT '',
  `from_addr` varchar(80) COLLATE utf8mb4_bin DEFAULT '',
  `to_addr_data` json NOT NULL,
  `cc_addr_data` json DEFAULT NULL,
  `verification_code` int(1) NOT NULL,
  `verification_code_valid_second` int(11) DEFAULT '-1',
  `html` int(1) NOT NULL,
  `send_status` varchar(16) COLLATE utf8mb4_bin NOT NULL,
  `failure_reason` varchar(1000) COLLATE utf8mb4_bin DEFAULT NULL,
  `content` text COLLATE utf8mb4_bin,
  `template_param_data` json DEFAULT NULL,
  `attachment_data` json DEFAULT NULL,
  `send_retry_num` int(11) NOT NULL DEFAULT '0',
  `expected_send_date` datetime DEFAULT NULL,
  `actual_send_date` datetime DEFAULT NULL,
  `urgent` int(1) NOT NULL DEFAULT '0',
  `batch` int(1) NOT NULL DEFAULT '0',
  `send_tenant_id` bigint(20) NOT NULL,
  `send_user_id` bigint(20) NOT NULL DEFAULT '-1',
  `receive_object_type` varchar(16) COLLATE utf8mb4_bin DEFAULT NULL,
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00',
  `created_by` bigint(21) NOT NULL DEFAULT '-1',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00',
  `modified_by` bigint(21) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_out_id` (`out_id`) USING BTREE,
  KEY `idx_from_addr` (`from_addr`) USING BTREE,
  KEY `idx_send_tenant_id` (`send_tenant_id`) USING BTREE,
  KEY `idx_expected_send_date` (`expected_send_date`) USING BTREE,
  KEY `idx_actual_send_date` (`actual_send_date`) USING BTREE,
  KEY `idx_biz_key` (`biz_key`) USING BTREE,
  KEY `idx_send_status` (`send_status`) USING BTREE,
  KEY `idx_receive_object_type` (`receive_object_type`) USING BTREE,
  KEY `idx_summary_group` (`actual_send_date`,`send_status`,`urgent`,`verification_code`,`batch`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='邮件表';

-- ----------------------------
-- Table structure for email_server
-- ----------------------------
DROP TABLE IF EXISTS `email_server`;
CREATE TABLE `email_server` (
  `id` bigint(20) NOT NULL COMMENT 'PK',
  `name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '',
  `protocol` varchar(16) COLLATE utf8mb4_bin NOT NULL DEFAULT '',
  `remark` varchar(200) COLLATE utf8mb4_bin DEFAULT '',
  `enabled` int(1) NOT NULL,
  `host` varchar(200) COLLATE utf8mb4_bin NOT NULL,
  `port` int(11) NOT NULL,
  `start_tls_enabled` int(1) NOT NULL,
  `ssl_enabled` int(1) NOT NULL,
  `auth_enabled` int(1) NOT NULL,
  `auth_account_data` json DEFAULT NULL,
  `subject_prefix` varchar(40) COLLATE utf8mb4_bin DEFAULT NULL,
  `created_by` bigint(20) NOT NULL DEFAULT '-1',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00',
  `modified_by` bigint(20) NOT NULL DEFAULT '-1',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `udix_name` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='邮件服务器表';

-- ----------------------------
-- Table structure for email_template
-- ----------------------------
DROP TABLE IF EXISTS `email_template`;
CREATE TABLE `email_template` (
  `id` bigint(20) NOT NULL COMMENT 'PK',
  `code` varchar(80) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '模版编码',
  `name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '模板名称',
  `language` varchar(40) COLLATE utf8mb4_bin NOT NULL COMMENT '语言',
  `subject` varchar(400) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '主题',
  `content` text COLLATE utf8mb4_bin NOT NULL COMMENT '内容',
  `verification_code` int(1) NOT NULL COMMENT '是否验证码邮件模版',
  `verification_code_valid_second` int(11) DEFAULT '-1' COMMENT '验证有效期',
  `private0` int(1) NOT NULL COMMENT '是否私有化',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_code_language` (`code`,`language`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='邮件模版表';

-- ----------------------------
-- Table structure for email_template_biz
-- ----------------------------
DROP TABLE IF EXISTS `email_template_biz`;
CREATE TABLE `email_template_biz` (
  `template_code` varchar(80) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '授权模版编码',
  `biz_key` varchar(80) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '授权业务Key',
  `private0` int(1) NOT NULL COMMENT '是否私有化',
  PRIMARY KEY (`biz_key`) USING BTREE,
  UNIQUE KEY `uidx_biz_key` (`biz_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='邮件模版业务表';

-- ----------------------------
-- Table structure for event
-- ----------------------------
DROP TABLE IF EXISTS `event`;
CREATE TABLE `event` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `code` varchar(80) COLLATE utf8mb4_bin NOT NULL COMMENT '事件编码',
  `name` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '事件名称',
  `type` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '事件类型',
  `e_key` varchar(80) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '事件KEY',
  `description` varchar(400) COLLATE utf8mb4_bin NOT NULL COMMENT '事件描述',
  `tenant_id` bigint(20) DEFAULT '-1' COMMENT '租户ID',
  `tenant_name` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '租户名称',
  `user_id` bigint(20) DEFAULT '-1' COMMENT '用户ID',
  `full_name` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '用户名称',
  `target_type` varchar(40) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '资源类型',
  `target_id` varchar(40) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '资源ID',
  `target_name` varchar(400) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '资源名称',
  `source_data` json NOT NULL COMMENT '原始事件内容',
  `app_code` varchar(40) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
  `service_code` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '服务编码',
  `push_status` varchar(30) COLLATE utf8mb4_bin NOT NULL COMMENT '推送状态',
  `push_msg` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '推送结果消息',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_created_date` (`created_date`) USING BTREE,
  KEY `idx_push_status` (`push_status`) USING BTREE,
  KEY `idx_type` (`type`) USING BTREE,
  KEY `idx_code` (`code`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_e_key` (`e_key`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_target_type` (`target_type`) USING BTREE,
  KEY `idx_target_id` (`target_id`) USING BTREE,
  KEY `idx_target_name` (`target_name`) USING BTREE,
  KEY `idx_app_code` (`app_code`) USING BTREE,
  KEY `idx_service_code` (`service_code`) USING BTREE,
  KEY `idx_summary_group` (`created_date`,`type`,`push_status`) USING BTREE,
  KEY `idx_summary_tenant_group` (`tenant_id`,`created_date`,`type`,`push_status`) USING BTREE,
  FULLTEXT KEY `fx_description_code_ekey` (`description`,`code`,`e_key`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='事件表';

-- ----------------------------
-- Table structure for event_channel
-- ----------------------------
DROP TABLE IF EXISTS `event_channel`;
CREATE TABLE `event_channel` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `type` varchar(32) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '接收通道类型',
  `name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '名称',
  `address` varchar(2000) COLLATE utf8mb4_bin NOT NULL COMMENT '地址',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `created_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  `modified_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '最后修改人',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `id_create_date` (`created_date`) USING BTREE,
  KEY `idx_channel_type` (`type`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='事件通道表';

-- ----------------------------
-- Table structure for event_push
-- ----------------------------
DROP TABLE IF EXISTS `event_push`;
CREATE TABLE `event_push` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `event_id` bigint(20) NOT NULL COMMENT '事件id',
  `type` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '事件类型',
  `name` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '机器人名称（关键字）',
  `secret` varchar(800) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '签名密钥',
  `content` text COLLATE utf8mb4_bin COMMENT '推送内容',
  `channel_type` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '推送渠道',
  `address` varchar(400) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '接收地址',
  `push` int(1) NOT NULL COMMENT '发送状态',
  `push_msg` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '推送结果消息',
  `retry_times` bigint(20) NOT NULL COMMENT '重试次数',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_event_id` (`event_id`) USING BTREE,
  KEY `idx_type` (`type`) USING BTREE,
  KEY `idx_push` (`push`) USING BTREE,
  KEY `idx_retry_times` (`retry_times`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='事件推送表';

-- ----------------------------
-- Table structure for event_template
-- ----------------------------
DROP TABLE IF EXISTS `event_template`;
CREATE TABLE `event_template` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `event_code` varchar(80) COLLATE utf8mb4_bin NOT NULL COMMENT '事件编码',
  `event_name` varchar(80) COLLATE utf8mb4_bin NOT NULL COMMENT '事件名称',
  `event_type` varchar(16) COLLATE utf8mb4_bin NOT NULL COMMENT '事件类型',
  `e_key` varchar(80) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '错误码',
  `target_type` varchar(40) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '资源类型',
  `app_code` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
  `private0` int(1) NOT NULL COMMENT '是否私有化标志',
  `allowed_channel_type_data` json DEFAULT NULL COMMENT '允许租户配置的通道类型',
  `created_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  `modified_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '最后修改人',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_event_name` (`event_name`) USING BTREE,
  UNIQUE KEY `uidx_event_code_ekey` (`event_code`,`e_key`) USING BTREE,
  KEY `idx_event_type` (`event_type`) USING BTREE,
  KEY `idx_event_code` (`event_code`),
  KEY `idx_big_biz_key` (`app_code`) USING BTREE,
  KEY `idx_biz_key` (`target_type`) USING BTREE,
  KEY `idx_created_date` (`created_date`) USING BTREE,
  KEY `idx_created_by` (`created_by`) USING BTREE,
  KEY `idx_ekey` (`e_key`) USING BTREE,
  FULLTEXT KEY `fx_event_name_code_ekey` (`event_name`,`event_code`,`e_key`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='事件模版表';

-- ----------------------------
-- Table structure for event_template_channel
-- ----------------------------
DROP TABLE IF EXISTS `event_template_channel`;
CREATE TABLE `event_template_channel` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `template_id` bigint(20) NOT NULL COMMENT '模板ID',
  `channel_id` bigint(20) NOT NULL COMMENT '接收通道ID',
  `channel_type` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '接收通道类型',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_template_id` (`template_id`) USING BTREE,
  KEY `idx_channel_id` (`channel_id`) USING BTREE,
  KEY `idx_channel_type` (`channel_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='事件通道表';

-- ----------------------------
-- Table structure for event_template_receiver
-- ----------------------------
DROP TABLE IF EXISTS `event_template_receiver`;
CREATE TABLE `event_template_receiver` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `template_id` bigint(20) NOT NULL COMMENT '模板ID',
  `receiver_type_data` json NOT NULL COMMENT '接收人类型',
  `receiver_ids_data` json NOT NULL COMMENT '接收人IDs',
  `notice_type_data` json NOT NULL COMMENT '接收消息渠道类型',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_template_id` (`template_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='事件模版接收表';

-- ----------------------------
-- Table structure for group0
-- ----------------------------
DROP TABLE IF EXISTS `group0`;
CREATE TABLE `group0` (
  `id` bigint(20) unsigned NOT NULL COMMENT '主键ID',
  `name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '名称',
  `code` varchar(80) COLLATE utf8mb4_bin NOT NULL COMMENT '编码',
  `enabled` int(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `source` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '用户组来源：BACK_ADD-后台添加； LDAP_SYNCHRONIZE-Ldap同步',
  `directory_id` bigint(20) DEFAULT NULL COMMENT '用户目录ID',
  `directory_gid_number` varchar(40) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户目录组成员ID',
  `remark` varchar(200) COLLATE utf8mb4_bin DEFAULT '' COMMENT '备注',
  `tenant_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '租户ID，默认-1表示未关联租户，当用户自定义角色时必须',
  `created_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  `modified_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '最后修改人',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_tenant_id_code` (`tenant_id`,`code`) USING BTREE,
  KEY `idx_code` (`code`) USING BTREE,
  KEY `idx_name` (`name`) USING BTREE,
  KEY `idx_create_date` (`created_date`) USING BTREE,
  KEY `idx_source` (`source`) USING BTREE,
  KEY `idx_directory_id` (`directory_id`) USING BTREE,
  KEY `idx_summary_group` (`enabled`,`source`,`created_date`) USING BTREE,
  KEY `idx_summary_tenant_group` (`tenant_id`,`enabled`,`source`,`created_date`) USING BTREE,
  FULLTEXT KEY `fx_name_tag_value` (`name`,`code`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户组';

-- ----------------------------
-- Table structure for group_user
-- ----------------------------
DROP TABLE IF EXISTS `group_user`;
CREATE TABLE `group_user` (
  `id` bigint(20) unsigned NOT NULL COMMENT '主键ID',
  `group_id` bigint(20) NOT NULL COMMENT '用户组ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `directory_id` bigint(20) DEFAULT NULL COMMENT '用户目录ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `created_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_group_user_id` (`group_id`,`user_id`) USING BTREE,
  KEY `idx_group_id` (`group_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_directory_id` (`directory_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户组用户关联表';

-- ----------------------------
-- Table structure for id_config
-- ----------------------------
DROP TABLE IF EXISTS `id_config`;
CREATE TABLE `id_config` (
  `pk` varchar(40) COLLATE utf8mb4_bin NOT NULL,
  `biz_key` varchar(80) COLLATE utf8mb4_bin NOT NULL,
  `format` varchar(16) COLLATE utf8mb4_bin NOT NULL,
  `prefix` varchar(4) COLLATE utf8mb4_bin NOT NULL,
  `date_format` varchar(8) COLLATE utf8mb4_bin DEFAULT NULL,
  `seq_length` int(11) NOT NULL,
  `mode` varchar(8) COLLATE utf8mb4_bin NOT NULL,
  `scope` varchar(16) COLLATE utf8mb4_bin NOT NULL,
  `tenant_id` bigint(20) NOT NULL DEFAULT '-1',
  `max_id` bigint(20) NOT NULL DEFAULT '0',
  `step` bigint(20) NOT NULL,
  `create_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00',
  PRIMARY KEY (`pk`) USING BTREE,
  UNIQUE KEY `uidx_biz_key_tenant_id` (`biz_key`,`tenant_id`) USING BTREE,
  KEY `uidx_tenant_id` (`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for instance
-- ----------------------------
DROP TABLE IF EXISTS `instance`;
CREATE TABLE `instance` (
  `pk` varchar(40) COLLATE utf8mb4_bin NOT NULL,
  `id` bigint(21) NOT NULL,
  `host` varchar(160) COLLATE utf8mb4_bin NOT NULL DEFAULT '',
  `port` varchar(40) COLLATE utf8mb4_bin NOT NULL DEFAULT '',
  `instance_type` varchar(40) COLLATE utf8mb4_bin NOT NULL DEFAULT '',
  `create_date` datetime NOT NULL,
  `modified_date` datetime NOT NULL,
  PRIMARY KEY (`pk`) USING BTREE,
  UNIQUE KEY `uidx_host_port` (`host`,`port`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `title` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '标题',
  `content` varchar(8000) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '消息内容',
  `receive_type` varchar(16) COLLATE utf8mb4_bin NOT NULL DEFAULT '0' COMMENT '发送类型：SITE-站内消息; EMAIL-邮件消息；',
  `send_type` varchar(16) COLLATE utf8mb4_bin NOT NULL DEFAULT '1' COMMENT '发送方式：SEND_NOW-立即发送 ；TIMED_SEND-定时发送',
  `timing_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '定时发送时间',
  `status` varchar(16) COLLATE utf8mb4_bin NOT NULL DEFAULT '1' COMMENT '消息状态：PENDING-待发送；SENT-已发送',
  `failure_reason` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '消息发送失败原因',
  `sent_num` int(11) NOT NULL DEFAULT '0' COMMENT '已发送人数',
  `read_num` int(11) NOT NULL DEFAULT '0' COMMENT '已阅读人数',
  `send_date` datetime DEFAULT '2001-01-01 00:00:00' COMMENT '实际发送时间',
  `deleted` int(1) NOT NULL DEFAULT '0' COMMENT '删除标识',
  `deleted_date` datetime DEFAULT NULL COMMENT '删除时间',
  `receive_tenant_id` bigint(20) DEFAULT '-1' COMMENT '接收租户ID',
  `receive_tenant_name` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '接收租户名称',
  `receive_object_type` varchar(16) COLLATE utf8mb4_bin NOT NULL DEFAULT '0' COMMENT '接收对象类型：USER、DEPT、GROUP; TENANT(租户下所有用户)、ALL_USER(平台所有用户)',
  `receive_object_data` json DEFAULT NULL COMMENT '接收对象ID和名称',
  `tenant_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '发送租户ID',
  `created_by` bigint(20) NOT NULL COMMENT '创建人',
  `created_by_name` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '创建人名称',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_timing_date` (`timing_date`) USING BTREE,
  KEY `idx_created_date` (`created_date`) USING BTREE,
  KEY `idx_deleted` (`deleted`) USING BTREE,
  KEY `idx_receive_tenant_id` (`receive_tenant_id`) USING BTREE,
  KEY `idx_receive_object_type` (`receive_object_type`) USING BTREE,
  KEY `idx_receive_type` (`receive_type`) USING BTREE,
  KEY `idx_send_type` (`send_type`) USING BTREE,
  KEY `idx_sent_num` (`sent_num`) USING BTREE,
  KEY `idx_read_num` (`read_num`),
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_summary_group` (`created_date`,`receive_type`,`status`) USING BTREE,
  KEY `idx_summary_tenant_group` (`tenant_id`,`created_date`,`receive_type`,`status`) USING BTREE,
  FULLTEXT KEY `fx_title` (`title`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='消息表';

-- ----------------------------
-- Table structure for message_center_online
-- ----------------------------
DROP TABLE IF EXISTS `message_center_online`;
CREATE TABLE `message_center_online` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `full_name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '用户姓名',
  `user_agent` varchar(200) COLLATE utf8mb4_bin NOT NULL COMMENT '用户终端',
  `device_id` varchar(160) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '设备ID',
  `remote_address` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '访问ID地址',
  `session_id` varchar(40) COLLATE utf8mb4_bin NOT NULL COMMENT '回话ID',
  `online` int(1) NOT NULL COMMENT '是否在线',
  `online_date` datetime DEFAULT NULL COMMENT '上次上线时间',
  `offline_date` datetime DEFAULT NULL COMMENT '上次下线时间',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`),
  KEY `idx_full_name` (`full_name`),
  KEY `idx_online_date` (`online_date`),
  KEY `idx_offline_date` (`offline_date`),
  KEY `idx_session_id` (`session_id`),
  FULLTEXT KEY `fx_fullname` (`full_name`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='消息中心在线用户';

-- ----------------------------
-- Table structure for message_sent
-- ----------------------------
DROP TABLE IF EXISTS `message_sent`;
CREATE TABLE `message_sent` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `message_id` bigint(20) NOT NULL COMMENT '消息ID',
  `receive_tenant_id` bigint(20) DEFAULT '-1' COMMENT '接收租户ID',
  `receive_user_id` bigint(20) NOT NULL COMMENT '接收用户ID',
  `read` int(1) NOT NULL DEFAULT '0' COMMENT '是否已阅读消息',
  `sent_date` datetime DEFAULT '2001-01-01 00:00:00' COMMENT '发送时间',
  `read_date` datetime DEFAULT '2001-01-01 00:00:00' COMMENT '阅读时间',
  `deleted` int(1) NOT NULL DEFAULT '0' COMMENT '删除标识',
  `deleted_date` datetime DEFAULT '2001-01-01 00:00:00' COMMENT '删除时间',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_message_id` (`message_id`) USING BTREE,
  KEY `idx_receive_tenant_id` (`receive_tenant_id`) USING BTREE,
  KEY `idx_receive_user_id` (`receive_user_id`) USING BTREE,
  KEY `idx_created_date` (`created_date`) USING BTREE,
  KEY `idx_read` (`read`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='已发送消息表';

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `content` varchar(2000) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '公告内容',
  `scope` varchar(16) COLLATE utf8mb4_bin NOT NULL DEFAULT '0' COMMENT '公告范围：GLOBAL-全局; APP-应用；',
  `app_id` bigint(20) DEFAULT NULL COMMENT '应用ID',
  `send_type` varchar(16) COLLATE utf8mb4_bin NOT NULL DEFAULT '1' COMMENT '发送方式：SEND_NOW-立即发送 ；TIMED_SEND-定时发送',
  `timing_date` datetime DEFAULT '2001-01-01 00:00:00' COMMENT '定时发送时间',
  `expiration_date` datetime NOT NULL COMMENT '过期时间',
  `tenant_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '发送租户ID',
  `created_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '消息发送人ID',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_timing_date` (`timing_date`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_send_type` (`send_type`) USING BTREE,
  KEY `idx_scope` (`scope`) USING BTREE,
  KEY `idx_created_date` (`created_date`) USING BTREE,
  KEY `idx_app_id` (`app_id`) USING BTREE,
  FULLTEXT KEY `fx_content` (`content`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='通知表';

-- ----------------------------
-- Table structure for oauth2_authorities
-- ----------------------------
DROP TABLE IF EXISTS `oauth2_authorities`;
CREATE TABLE `oauth2_authorities` (
  `username` varchar(100) COLLATE utf8mb4_bin NOT NULL,
  `authority` varchar(100) COLLATE utf8mb4_bin NOT NULL,
  UNIQUE KEY `idx_auth_username` (`username`,`authority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for oauth2_authorization
-- ----------------------------
DROP TABLE IF EXISTS `oauth2_authorization`;
CREATE TABLE `oauth2_authorization` (
  `id` varchar(100) COLLATE utf8mb4_bin NOT NULL,
  `registered_client_id` varchar(100) COLLATE utf8mb4_bin NOT NULL,
  `principal_name` varchar(200) COLLATE utf8mb4_bin NOT NULL,
  `authorization_grant_type` varchar(100) COLLATE utf8mb4_bin NOT NULL,
  `authorized_scopes` varchar(1000) COLLATE utf8mb4_bin DEFAULT NULL,
  `attributes` blob,
  `state` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL,
  `authorization_code_value` blob,
  `authorization_code_issued_at` timestamp NULL DEFAULT NULL,
  `authorization_code_expires_at` timestamp NULL DEFAULT NULL,
  `authorization_code_metadata` blob,
  `access_token_value` blob,
  `access_token_issued_at` timestamp NULL DEFAULT NULL,
  `access_token_expires_at` timestamp NULL DEFAULT NULL,
  `access_token_metadata` blob,
  `access_token_type` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL,
  `access_token_scopes` varchar(1000) COLLATE utf8mb4_bin DEFAULT NULL,
  `oidc_id_token_value` blob,
  `oidc_id_token_issued_at` timestamp NULL DEFAULT NULL,
  `oidc_id_token_expires_at` timestamp NULL DEFAULT NULL,
  `oidc_id_token_metadata` blob,
  `refresh_token_value` blob,
  `refresh_token_issued_at` timestamp NULL DEFAULT NULL,
  `refresh_token_expires_at` timestamp NULL DEFAULT NULL,
  `refresh_token_metadata` blob,
  `user_allow_duplicate_login` tinyint(1) DEFAULT '1',
  `user_code_value` blob,
  `user_code_issued_at` timestamp NULL DEFAULT NULL,
  `user_code_expires_at` timestamp NULL DEFAULT NULL,
  `user_code_metadata` blob,
  `device_code_value` blob,
  `device_code_issued_at` timestamp NULL DEFAULT NULL,
  `device_code_expires_at` timestamp NULL DEFAULT NULL,
  `device_code_metadata` blob,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for oauth2_authorization_consent
-- ----------------------------
DROP TABLE IF EXISTS `oauth2_authorization_consent`;
CREATE TABLE `oauth2_authorization_consent` (
  `registered_client_id` varchar(100) COLLATE utf8mb4_bin NOT NULL,
  `principal_name` varchar(200) COLLATE utf8mb4_bin NOT NULL,
  `authorities` varchar(1000) COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`registered_client_id`,`principal_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for oauth2_registered_client
-- ----------------------------
DROP TABLE IF EXISTS `oauth2_registered_client`;
CREATE TABLE `oauth2_registered_client` (
  `id` varchar(40) COLLATE utf8mb4_bin NOT NULL COMMENT '主键ID',
  `client_id` varchar(80) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT 'ID，对应KeyID',
  `client_name` varchar(200) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '名称',
  `client_authentication_methods` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '认证方法',
  `authorization_grant_types` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '授权类型',
  `redirect_uris` varchar(800) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '重定向URIs',
  `post_logout_redirect_uris` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'POST退出登录重定向URIs',
  `scopes` varchar(2000) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT 'Scope授权',
  `platform` varchar(40) COLLATE utf8mb4_bin NOT NULL COMMENT '接入平台：XCAN_TP、XCAN_OP',
  `source` varchar(40) COLLATE utf8mb4_bin NOT NULL COMMENT '客户端来源',
  `client_id_issued_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发行时间',
  `client_secret` varchar(200) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '密码，对应KeySecret',
  `client_secret_expires_at` timestamp NULL DEFAULT NULL COMMENT '过期时间',
  `biz_tag` varchar(100) COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '业务标识',
  `client_settings` varchar(1000) COLLATE utf8mb4_bin NOT NULL COMMENT '客户端设置参数',
  `token_settings` varchar(1000) COLLATE utf8mb4_bin NOT NULL COMMENT '令牌设置参数',
  `description` varchar(200) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '描述',
  `enabled` int(1) NOT NULL DEFAULT '1' COMMENT '启用标记',
  `tenant_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '租户ID，默认-1不限制接入租户',
  `tenant_name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '租户名称',
  `created_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  `modified_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '最后修改人',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_client_id` (`client_id`) USING BTREE,
  UNIQUE KEY `uidx_name` (`client_name`) USING BTREE,
  KEY `idx_create_time` (`created_date`) USING BTREE,
  KEY `idx_biz_tag` (`biz_tag`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='接入端';

-- ----------------------------
-- Table structure for oauth2_user
-- ----------------------------
DROP TABLE IF EXISTS `oauth2_user`;
CREATE TABLE `oauth2_user` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `username` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '用户名',
  `password` varchar(160) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '用户密码',
  `enabled` int(1) NOT NULL DEFAULT '1' COMMENT '是否启用状态',
  `account_non_expired` int(1) NOT NULL DEFAULT '1' COMMENT '账号是否未过期',
  `account_non_locked` int(1) NOT NULL DEFAULT '1' COMMENT '账号是否未锁定',
  `credentials_non_expired` int(1) NOT NULL DEFAULT '1' COMMENT '密码是否未过期',
  `first_name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '名字',
  `last_name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '姓',
  `full_name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '全名',
  `mobile` varchar(16) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '注册手机号',
  `email` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '注册邮箱',
  `sys_admin` int(1) NOT NULL DEFAULT '0' COMMENT '是否系统管理员',
  `to_user` int(1) NOT NULL DEFAULT '0' COMMENT '是否运营用户',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `tenant_name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '租户名称',
  `tenant_real_name_status` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '是否实名认证(0未认证 1已认证)',
  `main_dept_id` bigint(20) DEFAULT NULL COMMENT '用户主部门ID',
  `password_strength` varchar(10) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '密码强度：WEAK-弱，MEDIUM-中，STRONG-强',
  `password_expired_date` datetime DEFAULT NULL COMMENT '密码到期时间',
  `last_modified_password_date` datetime DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改密码的时间',
  `expired_date` datetime DEFAULT NULL COMMENT '用户账号过期时间',
  `deleted` int(1) NOT NULL DEFAULT '0' COMMENT '用户账号是否已删除',
  `directory_id` bigint(20) DEFAULT NULL COMMENT '用户LDAP目录ID',
  `default_language` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户默认语言',
  `default_time_zone` varchar(40) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户默认时区',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_tenant_real_name_status` (`tenant_real_name_status`) USING BTREE,
  KEY `idx_passd_expired_date` (`password_expired_date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='授权用户';

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `request_id` varchar(40) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '请求ID',
  `client_id` varchar(40) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '访问端ID',
  `resource` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '操作资源类型',
  `resource_name` varchar(400) COLLATE utf8mb4_bin NOT NULL COMMENT '操作资源名称',
  `resource_id` varchar(40) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '操作资源ID',
  `type` varchar(80) COLLATE utf8mb4_bin NOT NULL COMMENT '操作类型',
  `tenant_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '租户ID',
  `tenant_name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '租户名称',
  `user_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '操作用户ID',
  `full_name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '操作用户名称',
  `opt_date` datetime DEFAULT '2001-01-01 00:00:00' COMMENT '操作时间',
  `description` varchar(1600) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '操作描述',
  `detail` varchar(1600) COLLATE utf8mb4_bin DEFAULT '' COMMENT '操作详细描述',
  `private0` int(1) NOT NULL DEFAULT '0' COMMENT '是否私有化操作',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_fullname` (`full_name`) USING BTREE,
  KEY `idx_opt_date` (`opt_date`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_request_id` (`request_id`) USING BTREE,
  KEY `idx_reource` (`resource`),
  KEY `idx_resource_id` (`resource_id`),
  KEY `idx_type` (`type`),
  KEY `idx_private0` (`private0`),
  FULLTEXT KEY `fx_description` (`description`,`detail`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='操作日志';

-- ----------------------------
-- Table structure for org_tag
-- ----------------------------
DROP TABLE IF EXISTS `org_tag`;
CREATE TABLE `org_tag` (
  `id` bigint(20) NOT NULL COMMENT 'ID',
  `name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '标签名称',
  `tenant_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '所属租户ID',
  `created_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  `modified_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '最后修改人',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_tenant_id_name` (`name`,`tenant_id`) USING BTREE,
  KEY `idx_create_date` (`created_date`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  FULLTEXT KEY `fx_name` (`name`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='标签';

-- ----------------------------
-- Table structure for org_tag_target
-- ----------------------------
DROP TABLE IF EXISTS `org_tag_target`;
CREATE TABLE `org_tag_target` (
  `id` bigint(20) NOT NULL COMMENT 'ID',
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID',
  `target_type` varchar(16) COLLATE utf8mb4_bin NOT NULL COMMENT '目标类型：部门、组、用户',
  `target_id` bigint(20) NOT NULL COMMENT '目标ID',
  `tenant_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '租户ID',
  `created_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_tag_target_id` (`tag_id`,`target_id`) USING BTREE,
  KEY `idx_tag_id` (`tag_id`) USING BTREE,
  KEY `idx_target_id` (`target_id`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_target_type` (`target_type`) USING BTREE,
  KEY `idx_summary_group` (`created_date`,`target_type`) USING BTREE,
  KEY `idx_summary_tenant_group` (`tenant_id`,`created_date`,`target_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='标签对象';

-- ----------------------------
-- Table structure for service
-- ----------------------------
DROP TABLE IF EXISTS `service`;
CREATE TABLE `service` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `code` varchar(80) COLLATE utf8mb4_bin NOT NULL COMMENT '编码',
  `name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '名称',
  `description` varchar(200) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '描述',
  `source` varchar(16) COLLATE utf8mb4_bin NOT NULL COMMENT '来源：BACK_ADD、EUREKA、NOCAS、CONSUL',
  `enabled` int(1) NOT NULL COMMENT '有效状态',
  `api_num` int(11) NOT NULL DEFAULT '0' COMMENT '接口数',
  `route_path` varchar(80) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '网关路由路径',
  `url` varchar(400) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '服务地址',
  `health_url` varchar(400) COLLATE utf8mb4_bin NOT NULL COMMENT '健康检查地址',
  `api_doc_url` varchar(400) COLLATE utf8mb4_bin NOT NULL COMMENT 'API文档地址',
  `created_by` bigint(21) NOT NULL COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  `modified_by` bigint(21) NOT NULL COMMENT '最后修改人',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_code` (`code`) USING BTREE,
  KEY `idx_name` (`name`) USING BTREE,
  KEY `idx_summary_group` (`created_date`,`source`,`enabled`) USING BTREE,
  FULLTEXT KEY `fx_name_code_description` (`name`,`code`,`description`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='服务表';

-- ----------------------------
-- Table structure for sms
-- ----------------------------
DROP TABLE IF EXISTS `sms`;
CREATE TABLE `sms` (
  `id` bigint(20) NOT NULL COMMENT 'ID',
  `biz_key` varchar(80) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '业务key',
  `language` varchar(40) COLLATE utf8mb4_bin NOT NULL COMMENT '语言',
  `template_code` varchar(80) COLLATE utf8mb4_bin NOT NULL COMMENT '模版编码',
  `out_id` varchar(80) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '外部标识',
  `third_input_param` varchar(2000) COLLATE utf8mb4_bin NOT NULL COMMENT '请求第三方发送短信入参',
  `third_output_param` varchar(2000) COLLATE utf8mb4_bin NOT NULL COMMENT '请求第三方发送短信结果',
  `input_param_data` json NOT NULL COMMENT '业务发送短信请求参数',
  `verification_code` int(1) NOT NULL COMMENT '是否验证码',
  `batch` int(1) NOT NULL COMMENT '是否批量',
  `send_tenant_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '发送租户ID',
  `send_user_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '发送用户ID',
  `urgent` int(1) NOT NULL DEFAULT '0' COMMENT '是否加急',
  `send_status` varchar(16) COLLATE utf8mb4_bin NOT NULL COMMENT '发送状态',
  `failure_reason` varchar(800) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '发送失败原因',
  `send_retry_num` int(11) NOT NULL DEFAULT '0' COMMENT '发送重试次数',
  `actual_send_date` datetime DEFAULT NULL COMMENT '实际发送时间',
  `expected_send_date` datetime DEFAULT NULL COMMENT '期望发送时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_expected_send_date` (`expected_send_date`) USING BTREE,
  KEY `idx_send_status` (`send_status`) USING BTREE,
  KEY `idx_send_retry_num` (`send_retry_num`) USING BTREE,
  KEY `idx_out_id` (`out_id`) USING BTREE,
  KEY `idx_send_tenant_id` (`send_tenant_id`) USING BTREE,
  KEY `idx_actual_send_date` (`actual_send_date`) USING BTREE,
  KEY `idx_biz_key` (`biz_key`) USING BTREE,
  KEY `idx_template_code` (`template_code`) USING BTREE,
  KEY `idx_language` (`language`) USING BTREE,
  KEY `idx_verification_code` (`verification_code`) USING BTREE,
  KEY `idx_batch` (`batch`) USING BTREE,
  KEY `idx_send_user_id` (`send_user_id`) USING BTREE,
  KEY `idx_urgent` (`urgent`) USING BTREE,
  KEY `idx_summary_group` (`actual_send_date`,`send_status`,`urgent`,`verification_code`,`batch`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='短信表';

-- ----------------------------
-- Table structure for sms_channel
-- ----------------------------
DROP TABLE IF EXISTS `sms_channel`;
CREATE TABLE `sms_channel` (
  `id` bigint(20) NOT NULL,
  `name` varchar(80) COLLATE utf8mb4_bin NOT NULL COMMENT '通道名称',
  `enabled` int(1) NOT NULL COMMENT '是否生效',
  `logo` varchar(800) COLLATE utf8mb4_bin NOT NULL COMMENT '通道logo',
  `endpoint` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '短信API端',
  `access_key_secret` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '短信API accessKey 密钥',
  `access_key_id` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '短信API accessKey ID',
  `third_channel_no` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '三方短信通道、华为云必须参数',
  `created_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='短信通道表';

-- ----------------------------
-- Table structure for sms_template
-- ----------------------------
DROP TABLE IF EXISTS `sms_template`;
CREATE TABLE `sms_template` (
  `id` bigint(20) NOT NULL COMMENT 'PK',
  `code` varchar(80) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '模板编码',
  `name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '模板名称',
  `language` varchar(40) COLLATE utf8mb4_bin NOT NULL COMMENT '语言',
  `third_code` varchar(80) COLLATE utf8mb4_bin DEFAULT '' COMMENT '第三方模板编码\n',
  `content` text COLLATE utf8mb4_bin NOT NULL COMMENT '内容',
  `signature` varchar(80) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '模版签名',
  `channel_id` bigint(20) NOT NULL COMMENT '通道ID',
  `verification_code` int(1) NOT NULL COMMENT '是否验证码短信模版',
  `verification_code_valid_second` int(11) DEFAULT '-1' COMMENT '验证码过期时间',
  `private0` int(1) NOT NULL DEFAULT '0' COMMENT '是否私有化短信模版',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `uidx_code_language` (`code`,`language`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='短信模板表';

-- ----------------------------
-- Table structure for sms_template_biz
-- ----------------------------
DROP TABLE IF EXISTS `sms_template_biz`;
CREATE TABLE `sms_template_biz` (
  `template_code` varchar(40) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '模板编码',
  `biz_key` varchar(80) COLLATE utf8mb4_bin NOT NULL COMMENT '业务key',
  `private0` int(1) NOT NULL COMMENT '是否私有化业务模版',
  PRIMARY KEY (`biz_key`) USING BTREE,
  UNIQUE KEY `udix_biz_key` (`biz_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='短信模版业务表';

-- ----------------------------
-- Table structure for system_token
-- ----------------------------
DROP TABLE IF EXISTS `system_token`;
CREATE TABLE `system_token` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `name` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '名称',
  `value` varchar(200) COLLATE utf8mb4_bin NOT NULL COMMENT '加密令牌',
  `expired_date` datetime NOT NULL COMMENT '过期时间',
  `auth_type` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '资源授权类型',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `created_by` bigint(20) NOT NULL COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_tenant_name` (`tenant_id`,`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='系统令牌表\n';

-- ----------------------------
-- Table structure for system_token_resource
-- ----------------------------
DROP TABLE IF EXISTS `system_token_resource`;
CREATE TABLE `system_token_resource` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `service_code` varchar(80) COLLATE utf8mb4_bin NOT NULL COMMENT '服务编码',
  `resource` varchar(80) COLLATE utf8mb4_bin NOT NULL COMMENT '授权资源',
  `authority` varchar(160) COLLATE utf8mb4_bin NOT NULL COMMENT '授权权限(接口ID或ACL)',
  `system_token_id` bigint(20) NOT NULL COMMENT '名称',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_system_token_id` (`system_token_id`) USING BTREE,
  KEY `idx_resource` (`resource`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='系统令牌授权资源表';

-- ----------------------------
-- Table structure for tenant
-- ----------------------------
DROP TABLE IF EXISTS `tenant`;
CREATE TABLE `tenant` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `no` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '编号',
  `name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '租户名称',
  `type` varchar(30) COLLATE utf8mb4_bin NOT NULL DEFAULT '-1' COMMENT '租户类型：-1-未知；1-个人；2-企业；3-政府及事业单位',
  `source` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '租户来源：PLAT_REGISTER-平台注册；BACK_ADD-后台添加；',
  `real_name_status` varchar(20) COLLATE utf8mb4_bin NOT NULL DEFAULT '0' COMMENT '租户实名认证状态：PENDING-待审核,PASSED-审核通过(已实名),FAILURE-审核失败;',
  `status` varchar(20) COLLATE utf8mb4_bin NOT NULL DEFAULT '0' COMMENT '租户状态：1-启用；2-注销中；3-已注销；4-禁用；',
  `apply_cancel_date` datetime DEFAULT '2001-01-01 00:00:00' COMMENT '申请注销时间',
  `address` varchar(160) COLLATE utf8mb4_bin DEFAULT '' COMMENT '通讯地址',
  `locked` int(1) NOT NULL DEFAULT '0' COMMENT '锁定状态：0-未锁定；1-已锁定',
  `last_lock_date` datetime DEFAULT NULL COMMENT '最后锁定时间',
  `lock_start_date` datetime DEFAULT NULL COMMENT '锁定开始时间',
  `lock_end_date` datetime DEFAULT NULL COMMENT '锁定结束时间',
  `remark` varchar(200) COLLATE utf8mb4_bin DEFAULT '' COMMENT '备注',
  `created_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  `modified_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '最后修改人',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_name` (`name`) USING BTREE,
  KEY `idx_created_by` (`created_by`) USING BTREE,
  KEY `idx_modified_by` (`modified_by`) USING BTREE,
  KEY `idx_created_date` (`created_date`) USING BTREE,
  KEY `idx_modified_date` (`modified_date`) USING BTREE,
  KEY `idx_apply_cancel_date` (`apply_cancel_date`) USING BTREE,
  KEY `idx_real_name_status` (`real_name_status`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_source` (`source`) USING BTREE,
  KEY `idx_type` (`type`) USING BTREE,
  KEY `idx_locked` (`locked`) USING BTREE,
  KEY `idx_lock_start_date` (`lock_start_date`) USING BTREE,
  KEY `idx_lock_end_date` (`lock_end_date`) USING BTREE,
  KEY `idx_summary_group` (`source`,`type`,`status`,`real_name_status`,`locked`,`created_date`) USING BTREE,
  FULLTEXT KEY `fx_name_no` (`name`,`no`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='租户';

-- ----------------------------
-- Table structure for tenant_cert_audit
-- ----------------------------
DROP TABLE IF EXISTS `tenant_cert_audit`;
CREATE TABLE `tenant_cert_audit` (
  `id` bigint(20) unsigned NOT NULL COMMENT '主键ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `status` varchar(20) COLLATE utf8mb4_bin NOT NULL DEFAULT '0' COMMENT '审核状态：0-未提交认证；1-待审核；2-审核通过；3-未通过',
  `type` varchar(20) COLLATE utf8mb4_bin NOT NULL DEFAULT '-1' COMMENT '租户类型：-1-未知；1-个人；2-企业；3-政府及事业单位',
  `personal_cert_data` json DEFAULT NULL COMMENT '认证用户证照信息：certNo-证照号；cert_front_pic_url-证照正面照片；cert_back_pic_url-证照背面照片',
  `enterprise_cert_data` json DEFAULT NULL COMMENT '企业营业执照信息：creditCode-统一社会信用代码；businessLicensePicUrl-营业执照照片',
  `enterprise_legal_personcert_data` json DEFAULT NULL COMMENT '认证法人证照信息：certNo-法人证照号；certFrontPicUrl-法人证照正面照片；certBackPicUrl-法人证照背面照片；cert_back_pic_url-证照背面照片',
  `government_cert_data` json DEFAULT NULL COMMENT '政府及事业单位机构信息：orgCode-组织机构代码；orgCodePicUrl-组织机构代码证照片',
  `audit_record_data` json DEFAULT NULL COMMENT '审核结果：status（审核状态）1-待审核；2-审核通过；3-未通过；reason-审核原因；date-审核时间；userId-审核人',
  `auto_audit` int(1) NOT NULL COMMENT '是否自动审核标志',
  `created_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  `modified_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '最后修改人',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `udx_tenant_id` (`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='租户审核';

-- ----------------------------
-- Table structure for to_role
-- ----------------------------
DROP TABLE IF EXISTS `to_role`;
CREATE TABLE `to_role` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '名称',
  `code` varchar(80) COLLATE utf8mb4_bin NOT NULL COMMENT '编码，规则名称英文蛇形表示',
  `enabled` int(1) NOT NULL COMMENT '有效状态：0-禁用；1-启用',
  `description` varchar(200) COLLATE utf8mb4_bin DEFAULT '' COMMENT '描述',
  `app_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '所属应用ID',
  `created_by` bigint(20) NOT NULL COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  `modified_by` bigint(20) NOT NULL COMMENT '最后修改人',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_code` (`code`) USING BTREE,
  UNIQUE KEY `uidx_name` (`name`) USING BTREE,
  KEY `idx_app_id` (`app_id`) USING BTREE,
  FULLTEXT KEY `fx_name_code` (`name`,`code`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='运营策略（租户运营角色）表（非私有化表）';

-- ----------------------------
-- Table structure for to_role_user
-- ----------------------------
DROP TABLE IF EXISTS `to_role_user`;
CREATE TABLE `to_role_user` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `to_role_id` bigint(20) NOT NULL COMMENT '租户运营角色ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `created_by` bigint(20) NOT NULL COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_policy_user` (`to_role_id`,`user_id`) USING BTREE,
  KEY `idx_policy_id` (`to_role_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_created_date` (`created_date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='策略用户关联表（非私有化表）';

-- ----------------------------
-- Table structure for to_user
-- ----------------------------
DROP TABLE IF EXISTS `to_user`;
CREATE TABLE `to_user` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `created_by` bigint(20) NOT NULL COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uid_user_id` (`user_id`) USING BTREE,
  KEY `idx_created_date` (`created_date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='策略用户关联表（非私有化表）';

-- ----------------------------
-- Table structure for user0
-- ----------------------------
DROP TABLE IF EXISTS `user0`;
CREATE TABLE `user0` (
  `id` bigint(20) unsigned NOT NULL COMMENT '主键ID',
  `username` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '用户名',
  `first_name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '名字',
  `last_name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '姓',
  `full_name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '全名',
  `itc` varchar(8) COLLATE utf8mb4_bin DEFAULT '' COMMENT '国际电话区号',
  `country` varchar(16) COLLATE utf8mb4_bin DEFAULT '' COMMENT '国家编码',
  `email` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '注册邮箱',
  `mobile` varchar(16) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '注册手机号',
  `signup_account_type` varchar(16) COLLATE utf8mb4_bin DEFAULT 'NOOP' COMMENT '注册账号类型：MOBILE-手机号；EMAIL-邮箱；NOOP=未操作',
  `signup_account` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '租户注册账号（手机号或邮箱）',
  `signup_device_id` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '注册设备ID',
  `landline` varchar(40) COLLATE utf8mb4_bin DEFAULT '' COMMENT '座机',
  `tenant_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '所属租户ID',
  `tenant_name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '租户名称',
  `avatar` varchar(400) COLLATE utf8mb4_bin DEFAULT '' COMMENT '用户头像地址',
  `title` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '职务',
  `gender` varchar(10) COLLATE utf8mb4_bin DEFAULT '' COMMENT '性别：MALE-男；FEMALE-女；UNKNOWN-未知；',
  `address` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '联系地址',
  `source` varchar(40) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '用户来源：PLAT_REGISTER-平台注册；BACK_ADD-后台添加；THIRD_PARTY_LOGIN-三方登录; LDAP_SYNCHRONIZE-Ldap同步',
  `directory_id` bigint(20) DEFAULT NULL COMMENT '用户目录ID',
  `main_dept_id` bigint(20) DEFAULT '-1' COMMENT '主部门ID',
  `online` int(1) NOT NULL COMMENT '是否在线',
  `online_date` datetime DEFAULT NULL COMMENT '上次上线时间',
  `offline_date` datetime DEFAULT NULL COMMENT '上次下线时间',
  `dept_head` int(1) DEFAULT '0' COMMENT '是否部门负责人',
  `sys_admin` int(1) NOT NULL DEFAULT '0' COMMENT '是否系统管理员：0-一般用户；1-系统管理员；',
  `expired` int(1) NOT NULL COMMENT '到期标记：0-未到期；1-已到期',
  `expired_date` datetime DEFAULT NULL COMMENT '到期时间',
  `enabled` int(1) NOT NULL DEFAULT '0' COMMENT '用户状态：0-禁用；1-启用',
  `disable_reason` varchar(200) COLLATE utf8mb4_bin DEFAULT '' COMMENT '禁用原因',
  `deleted` int(1) NOT NULL DEFAULT '0' COMMENT '删除状态：0-未删除；1-已删除',
  `locked` int(1) NOT NULL COMMENT '锁定状态：0-未锁定；1-已锁定',
  `last_lock_date` datetime DEFAULT NULL COMMENT '最后锁定时间',
  `lock_start_date` datetime DEFAULT NULL COMMENT '锁定开始时间',
  `lock_end_date` datetime DEFAULT NULL COMMENT '锁定结束时间',
  `last_modified_password_date` datetime DEFAULT NULL COMMENT '最后修改密码的时间',
  `created_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  `modified_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '最后修改人',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_username` (`username`) USING BTREE,
  KEY `idx_source` (`source`) USING BTREE,
  KEY `idx_gender` (`gender`) USING BTREE,
  KEY `idx_admin` (`sys_admin`) USING BTREE,
  KEY `idx_enabled` (`enabled`) USING BTREE,
  KEY `idx_locked` (`locked`) USING BTREE,
  KEY `idx_lock_start_date` (`lock_start_date`) USING BTREE,
  KEY `idx_lock_end_date` (`lock_end_date`) USING BTREE,
  KEY `idx_fullname` (`full_name`) USING BTREE,
  KEY `idx_directory_id` (`directory_id`),
  KEY `idx_deleted` (`deleted`) USING BTREE,
  KEY `uidx_tenantId_mobile` (`mobile`,`tenant_id`) USING BTREE,
  KEY `uidx_tenantId_email` (`email`,`tenant_id`) USING BTREE,
  KEY `idx_summary_group` (`source`,`sys_admin`,`enabled`,`locked`,`gender`,`created_date`) USING BTREE,
  KEY `idx_summary_tenant_group` (`tenant_id`,`source`,`sys_admin`,`enabled`,`locked`,`gender`,`created_date`) USING BTREE,
  FULLTEXT KEY `fx_name_mobile_title_username` (`full_name`,`mobile`,`title`,`username`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户';

-- ----------------------------
-- Table structure for user_directory
-- ----------------------------
DROP TABLE IF EXISTS `user_directory`;
CREATE TABLE `user_directory` (
  `id` bigint(20) unsigned NOT NULL COMMENT '主键ID',
  `name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '服务名',
  `sequence` int(11) NOT NULL COMMENT '排序值，值小的同步优先级高',
  `enabled` int(1) NOT NULL COMMENT '启用/禁用状态',
  `server_data` json NOT NULL COMMENT '服务配置',
  `schema_data` json NOT NULL COMMENT '模式配置',
  `user_schema_data` json NOT NULL COMMENT '用户模式配置',
  `group_schema_data` json DEFAULT NULL COMMENT '用户组模式配置',
  `membership_schema_data` json DEFAULT NULL COMMENT '组成员模式配置',
  `created_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  `modified_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '最后修改人',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='租户Ldap设置';

-- ----------------------------
-- Table structure for web_tag
-- ----------------------------
DROP TABLE IF EXISTS `web_tag`;
CREATE TABLE `web_tag` (
  `id` bigint(20) NOT NULL COMMENT 'ID',
  `name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '标签名称',
  `description` varchar(200) COLLATE utf8mb4_bin NOT NULL COMMENT '描述',
  `tenant_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '所属租户ID',
  `created_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  `modified_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '最后修改人',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_create_date` (`created_date`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  FULLTEXT KEY `idx_name` (`name`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='应用标签表';

-- ----------------------------
-- Table structure for web_tag_target
-- ----------------------------
DROP TABLE IF EXISTS `web_tag_target`;
CREATE TABLE `web_tag_target` (
  `id` bigint(20) NOT NULL COMMENT 'ID',
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID',
  `target_type` varchar(16) COLLATE utf8mb4_bin NOT NULL COMMENT '目标类型：APP、FUNC',
  `target_id` bigint(20) NOT NULL COMMENT '目标ID',
  `tenant_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '租户ID',
  `created_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_tag_id` (`tag_id`) USING BTREE,
  KEY `idx_target_id` (`target_id`) USING BTREE,
  KEY `idx_target_type` (`target_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='应用标签对象表';

-- ----------------------------
-- Table structure for bucket
-- ----------------------------
DROP TABLE IF EXISTS `bucket`;
CREATE TABLE `bucket` (
  `id` bigint(20) unsigned NOT NULL COMMENT '主键Id',
  `name` varchar(40) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '名称',
  `acl` varchar(24) COLLATE utf8mb4_bin NOT NULL COMMENT '桶访问控制类型：私有、公共读、公共读写',
  `tenant_created` int(1) NOT NULL COMMENT '租户创建桶标识',
  `created_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  `modified_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '最后修改人',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unix_name` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='对象桶';

-- ----------------------------
-- Table structure for bucket_biz_config
-- ----------------------------
DROP TABLE IF EXISTS `bucket_biz_config`;
CREATE TABLE `bucket_biz_config` (
  `id` bigint(20) NOT NULL COMMENT 'PK',
  `biz_key` varchar(80) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '接入业务Key',
  `bucket_name` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '授权桶名称',
  `remark` varchar(200) COLLATE utf8mb4_bin NOT NULL COMMENT '备注',
  `public_access` int(1) NOT NULL COMMENT '是否公开文件访问：公开访问文件（/pubapi）、私有访问文件（/api）',
  `public_token_auth` int(1) NOT NULL COMMENT '公开令牌访问接口（私有访问文件也需要使用私有令牌认证）',
  `encrypt` int(1) NOT NULL COMMENT '是否对文件加密存储',
  `multi_tenant_ctrl` int(1) NOT NULL COMMENT '是否开启多租户控制(只针对下载时)',
  `enabled_auth` int(1) NOT NULL COMMENT '开启空间认证标志',
  `allow_tenant_created` int(1) NOT NULL COMMENT '允许租户业务创建桶标识',
  `app_code` varchar(80) COLLATE utf8mb4_bin NOT NULL COMMENT '应用编码',
  `app_admin_code` varchar(80) COLLATE utf8mb4_bin NOT NULL COMMENT '应用管理员编码',
  `cache_age` int(11) NOT NULL DEFAULT '0' COMMENT '浏览器缓存时长，单位秒',
  `private0` int(1) DEFAULT NULL COMMENT '是否私有化',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_biz_key` (`biz_key`) USING BTREE,
  KEY `idx_bucket_name` (`bucket_name`) USING BTREE,
  KEY `idx_tenant_biz` (`enabled_auth`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='桶和业务授权表';

-- ----------------------------
-- Table structure for object_file
-- ----------------------------
DROP TABLE IF EXISTS `object_file`;
CREATE TABLE `object_file` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `project_id` bigint(20) NOT NULL COMMENT '项目ID',
  `name` varchar(400) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '文件或文件夹名称',
  `unique_name` varchar(400) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '存储时唯一文件或文件夹名称',
  `oid` bigint(20) DEFAULT NULL COMMENT '关联对象ID',
  `path` varchar(600) COLLATE utf8mb4_bin DEFAULT '' COMMENT '文件或文件夹相对路径（prefix+name）/ S3对象Key',
  `size` bigint(20) NOT NULL DEFAULT '0' COMMENT '文件或文件夹实际上传大小，和物理存储磁盘可能不一致的',
  `content_type` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '文件格式',
  `store_address` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '本地存储完整路径 / S3对象的URL地址',
  `store_type` varchar(16) COLLATE utf8mb4_bin NOT NULL COMMENT '存储平台类型',
  `space_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '父文件夹ID',
  `parent_directory_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '父文件夹ID',
  `instance_id` varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '本地存储实例ID',
  `biz_key` varchar(80) COLLATE utf8mb4_bin NOT NULL COMMENT '业务类型',
  `bucket_name` varchar(32) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '桶名称',
  `upload_id` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '分片上传ID',
  `upload_type` varchar(32) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '上传类型：NORMAL、INTERRUPTION_POINT_CONTINUE、PART',
  `completed` int(1) DEFAULT '0' COMMENT '分片上传/断点续传是否上传完成标识',
  `store_deleted` int(1) NOT NULL DEFAULT '0' COMMENT '存储文件删除状态',
  `deleted_retry_num` int(11) NOT NULL DEFAULT '0' COMMENT '重试删除次数',
  `public_token` varchar(40) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '公开访问令牌',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `created_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  `modified_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '最后修改人',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_upload_id` (`upload_id`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_parent_dir_id` (`parent_directory_id`) USING BTREE,
  KEY `idx_store_deleted` (`store_deleted`) USING BTREE,
  KEY `idx_path` (`path`) USING BTREE,
  KEY `idx_space_id` (`space_id`) USING BTREE,
  KEY `idx_instance_id` (`instance_id`) USING BTREE,
  KEY `idx_deleted_retry_num` (`deleted_retry_num`) USING BTREE,
  KEY `idx_oid` (`oid`) USING BTREE,
  KEY `idx_size` (`size`) USING BTREE,
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='对象文件元数据';

-- ----------------------------
-- Table structure for object_space
-- ----------------------------
DROP TABLE IF EXISTS `object_space`;
CREATE TABLE `object_space` (
  `id` bigint(20) NOT NULL COMMENT 'PK',
  `project_id` bigint(20) DEFAULT NULL COMMENT '项目ID',
  `name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '空间名称',
  `biz_key` varchar(80) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '接入业务Key',
  `bucket_name` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '授权桶名称',
  `remark` varchar(200) COLLATE utf8mb4_bin DEFAULT '' COMMENT '备注',
  `quota_size` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '允许最大字节数',
  `auth` int(1) NOT NULL COMMENT '是否权限控制',
  `tenant_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '授权租户ID',
  `customized` int(11) NOT NULL COMMENT '是否客户化业务',
  `created_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  `modified_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '最后修改人',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_tenant_name_biz_key` (`tenant_id`,`name`,`biz_key`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_create_by` (`created_by`) USING BTREE,
  KEY `idx_created_date` (`created_date`) USING BTREE,
  KEY `idx_bucket_name` (`bucket_name`) USING BTREE,
  KEY `idx_auth` (`auth`) USING BTREE,
  KEY `idx_name` (`name`) USING BTREE,
  KEY `idx_project_id` (`project_id`),
  KEY `idx_customized` (`customized`) USING BTREE,
  FULLTEXT KEY `fx_name` (`name`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='授权存储表';

-- ----------------------------
-- Table structure for object_space_auth
-- ----------------------------
DROP TABLE IF EXISTS `object_space_auth`;
CREATE TABLE `object_space_auth` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `space_id` bigint(20) NOT NULL COMMENT '场景ID',
  `auth_object_type` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '授权对象：用户|部门|组',
  `auth_object_id` bigint(20) NOT NULL COMMENT '授权对象ID',
  `auth_data` json NOT NULL COMMENT '权限：查看|编辑|删除|发送请求|测试|授权|分享',
  `creator` int(1) NOT NULL COMMENT '创建人授权标识',
  `tenant_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '租户ID',
  `created_by` bigint(20) NOT NULL COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_space_id_auth_creator` (`space_id`,`auth_object_id`,`auth_object_type`,`creator`) USING BTREE,
  KEY `idx_created_by` (`created_by`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_auth_object_id` (`auth_object_id`) USING BTREE,
  KEY `idx_space_id` (`space_id`) USING BTREE,
  KEY `idx_created_date` (`created_date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='对象空间授权';

-- ----------------------------
-- Table structure for object_space_object
-- ----------------------------
DROP TABLE IF EXISTS `object_space_object`;
CREATE TABLE `object_space_object` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `project_id` bigint(20) NOT NULL COMMENT '项目ID',
  `name` varchar(400) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '目录/文件名称(上传文件名最大400)',
  `type` varchar(16) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '文件类型：FILE、DIRECTORY',
  `store_type` varchar(16) COLLATE utf8mb4_bin NOT NULL COMMENT '存储平台类型',
  `fid` bigint(20) DEFAULT NULL COMMENT '关联对象文件ID',
  `level` int(11) NOT NULL COMMENT '目录层级，最大5',
  `size` bigint(20) NOT NULL DEFAULT '0' COMMENT '目录/文件总大小',
  `space_id` bigint(20) NOT NULL COMMENT '空间ID',
  `parent_directory_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '父文件夹ID',
  `parent_like_id` varchar(200) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '所有父子文件夹ID符号“-”相连（200字符最大支持10级）',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `created_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
  `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
  `modified_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '最后修改人',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_parent_dir_id` (`parent_directory_id`) USING BTREE,
  KEY `idx_parent_like_id` (`parent_like_id`) USING BTREE,
  KEY `idx_space_id` (`space_id`) USING BTREE,
  KEY `idx_created_by` (`created_by`) USING BTREE,
  KEY `idx_created_date` (`created_date`) USING BTREE,
  KEY `idx_type` (`type`) USING BTREE,
  KEY `idx_fid` (`fid`) USING BTREE,
  KEY `idx_store_type` (`store_type`) USING BTREE,
  KEY `idx_project_id` (`project_id`),
  KEY `idx_level_size_sub_num` (`level`,`size`) USING BTREE,
  FULLTEXT KEY `fx_name` (`name`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='对象文件元数据';

-- ----------------------------
-- Table structure for object_space_share
-- ----------------------------
DROP TABLE IF EXISTS `object_space_share`;
CREATE TABLE `object_space_share` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `space_id` bigint(20) NOT NULL COMMENT '分享空间ID',
  `share_type` varchar(20) COLLATE utf8mb4_bin NOT NULL DEFAULT 'SPACE_OBJECTS',
  `all` int(1) NOT NULL COMMENT '分享空间全部对象标志',
  `object_ids` json DEFAULT NULL COMMENT '分享对象IDs',
  `quick_object_id` bigint(20) DEFAULT NULL COMMENT '快速分享对象ID',
  `url` varchar(400) COLLATE utf8mb4_bin NOT NULL COMMENT '分享URL',
  `expired` int(1) NOT NULL COMMENT '分享是否过期',
  `expired_duration` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '过期时长',
  `expired_date` datetime DEFAULT NULL COMMENT '分享过期时间',
  `public0` int(1) NOT NULL COMMENT '公开或私有标志',
  `public_token` varchar(40) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '公开访问令牌',
  `password` varchar(40) COLLATE utf8mb4_bin DEFAULT '' COMMENT '私有密码',
  `remark` varchar(200) COLLATE utf8mb4_bin DEFAULT '' COMMENT '备注',
  `tenant_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '租户ID',
  `created_by` bigint(20) NOT NULL COMMENT '创建人',
  `created_date` datetime NOT NULL COMMENT '创建时间',
  `modified_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '最后修改人',
  `modified_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_space_id` (`space_id`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_quick_object_id` (`quick_object_id`),
  FULLTEXT KEY `fx_remark` (`remark`) /*!50100 WITH PARSER `ngram` */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='空间分享';

-- ----------------------------
-- Table structure for storage_setting
-- ----------------------------
DROP TABLE IF EXISTS `storage_setting`;
CREATE TABLE `storage_setting` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `pkey` varchar(32) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '配置参数key',
  `pvalue` json NOT NULL COMMENT '配置参数值',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uidx_tid_key` (`pkey`) USING BTREE,
  KEY `idx_key` (`pkey`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='系统设置';

-- ----------------------------
-- Table structure for app_installed
-- ----------------------------
DROP TABLE IF EXISTS `app_installed`;
CREATE TABLE `app_installed`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `goods_id` bigint(20) NOT NULL COMMENT '商品ID',
  `edition_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '版本类型',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '商品类型',
  `code` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '商品编码',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '商品名称',
  `version` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '商品版本',
  `icon_text` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT 'Icon内容',
  `tags` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '商品标签',
  `introduction` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '商品介绍',
  `information` varchar(6000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '描述',
  `features` varchar(3200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '升级功能介绍',
  `charge` int(11) NOT NULL COMMENT '收费标记',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '订单号',
  `purchase_by_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '-1' COMMENT '购买人',
  `purchase_date` datetime NULL DEFAULT '2001-01-01 00:00:00' COMMENT '购买时间',
  `platform` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '操作系统',
  `issuer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '发行者：发行商品租户名称',
  `license_no` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '许可编号',
  `expired_date` datetime NULL DEFAULT '2001-01-01 00:00:00' COMMENT '过期时间',
  `server` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '服务器信息',
  `apply_edition_type` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '适用应用版本类型：云服务版(CLOUD_SERVICE)/数据中心版(DATACENTER)/企业版(ENTERPRISE)/社区版(COMMUNITY)',
  `apply_app_code` varchar(160) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '适用应用编码',
  `apply_version` varchar(160) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '适用应用版本范围：最小版本号、最大值版本号',
  `downward_version` varchar(160) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '向下兼容版本范围：最小版本号、最大值版本号',
  `upgrade_from_goods_id` bigint(20) NULL DEFAULT -1 COMMENT '升级商品ID',
  `upgrade_from_code` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '升级商品编码',
  `upgrade_from_version` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '升级商品版本',
  `install_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '安装类型',
  `install_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '安装状态',
  `install_message` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '安装状态描述',
  `install_main_app` int(11) NULL DEFAULT NULL COMMENT '安装主用标志，false时安装子应用',
  `install_app_code` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '安装应用/子应用编码',
  `relative_install_paths` varchar(800) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '相对主目录安装路径',
  `install_instance_id` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '安装实例ID',
  `online_install` int(11) NOT NULL COMMENT '在线安装标志',
  `uninstallable` int(11) NOT NULL COMMENT '可卸载标识',
  `uninstall` int(11) NOT NULL COMMENT '卸载标识',
  `tenant_id` bigint(20) NOT NULL COMMENT '安装租户ID',
  `created_by` bigint(20) NULL DEFAULT -1 COMMENT '安装人ID',
  `created_by_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '安装人名称',
  `created_date` datetime NOT NULL COMMENT '安装时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_created_date`(`created_date`) USING BTREE,
  INDEX `idx_edition_type`(`edition_type`) USING BTREE,
  INDEX `idx_type`(`type`) USING BTREE,
  INDEX `idx_install_type`(`install_type`) USING BTREE,
  INDEX `idx_goods_id`(`goods_id`) USING BTREE,
  INDEX `idx_install_status`(`install_status`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id`) USING BTREE,
  INDEX `idx_upgrade_goods_id`(`upgrade_from_goods_id`) USING BTREE,
  INDEX `dix_code_version_edition`(`code`, `version`, `edition_type`) USING BTREE,
  INDEX `idx_platform`(`platform`) USING BTREE,
  INDEX `idx_version`(`version`) USING BTREE,
  INDEX `idx_summary_tenant_group`(`tenant_id`, `created_date`, `edition_type`, `type`, `charge`, `install_type`, `install_status`) USING BTREE,
  INDEX `idx_summary_group`(`created_date`, `edition_type`, `type`, `charge`, `install_type`, `install_status`) USING BTREE,
  INDEX `idx_uninstall`(`uninstall`) USING BTREE,
  INDEX `idx_online_install`(`online_install`) USING BTREE,
  INDEX `idx_uninstallable`(`uninstallable`) USING BTREE,
  INDEX `idx_purchase_date`(`purchase_date`) USING BTREE,
  FULLTEXT INDEX `fx_name_code_intro`(`name`, `code`, `introduction`) WITH PARSER `ngram`
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '商店商品安装记录表（私有化表）' ;

-- ----------------------------
-- Table structure for license_installed
-- ----------------------------
DROP TABLE IF EXISTS `license_installed`;
CREATE TABLE `license_installed`  (
  `id` bigint(20) NOT NULL COMMENT 'ID',
  `main` int(11) NOT NULL DEFAULT 0 COMMENT '主许可标志：1-主许可、0-非主许可',
  `license_no` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '许可编号',
  `main_license_no` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '主应用许可编号',
  `issued_date` datetime NOT NULL COMMENT '发行日期',
  `expired_date` datetime NOT NULL COMMENT '有效期结束日期',
  `info` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '许可说明',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '证书文件',
  `provider` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '产品提供者',
  `issuer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '产品发行者',
  `holder_id` bigint(20) NULL DEFAULT NULL COMMENT '产品持有者ID（租户ID）',
  `holder` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '产品持有者名称（租户名称）',
  `install_edition_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '安装环境版本类型',
  `goods_edition_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '商品版本类型：云服务版(CLOUD_SERVICE)/数据中心版(DATACENTER)/企业版(ENTERPRISE)/社区版(COMMUNITY)',
  `goods_id` bigint(20) NULL DEFAULT NULL COMMENT '商品ID',
  `goods_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '商品类型',
  `goods_code` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '商品编码',
  `goods_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '商品名称',
  `goods_version` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '版本',
  `signature` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '证书MD5签名',
  `order_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '产品购买订单号（可选，社区版本没有）',
  `subject` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '主题',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uidx_license_no`(`license_no`) USING BTREE,
  UNIQUE INDEX `uidx_goods_edition_type`(`goods_code`, `goods_edition_type`) USING BTREE,
  UNIQUE INDEX `uidx_goods_id`(`goods_id`) USING BTREE,
  INDEX `idx_holder_id`(`holder_id`) USING BTREE,
  INDEX `idx_install_edition_type`(`install_edition_type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '租户私有化安装许可（私有化表）';
-- @formatter:on
