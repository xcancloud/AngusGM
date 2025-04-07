# 公共数据表

[English](COMMON_LINK_SCHEMA.md) | [中文](COMMON_LINK_SCHEMA_zh.md)

本项目利用Federated/DBlink数据库技术，聚合来自各种业务数据库的公共数据，以提供统一的数据源和JPA Repository访问接口。

***主要好处：***
- ***简化使用***：JPA存储库访问比RESTful API调用更简单
- ***增强性能***：消除HTTP协议解析和JSON序列化开销
- ***简化编码***：直接重用现有的实体定义和JPA存储库，而无需创建OpenFeign接口或DTO/VO对象

***所有公共数据表统一通过 Federated/DBlink 逻辑存储在 `xcan_commonlink` 库，各业务服务配置连接到 `xcan_commonlink` 库来统一访问。***

## 对外供提公共数据表

#### 创建公共数据表 - MySQL

公共数据表使用 MySQL [联邦存储引擎](http://wiki.xcan.work/pages/viewpage.action?pageId=14647418) 方式来实现。

##### 创建授权用户 commonlink

`commonlink` 用户用于统一访问 Federated/DBlink server，每个业务数据库服务器都需要创建授权该用户。

###### 1. 创建库和用户

```sql
-- 创建库
CREATE DATABASE `xcan_commonlink` DEFAULT CHARACTER SET utf8mb4 ;

-- 创建用户（注意：线上环境需要在每个PXC节点创建用户）
CREATE USER 'commonlink'@'%' IDENTIFIED BY 'your_password';
grant all privileges on xcan_commonlink.* to commonlink@'%' identified by 'your_password';
FLUSH PRIVILEGES;
```

###### 2. 用户授权

***注意：Angus系列应用默认只需授权`commonlink`用户查询表权限。***

```sql
-- GM -----
-- UC ------------------------------
GRANT select ON `xcan_gm`.`tenant` TO `commonlink`@`%`;
GRANT select ON `xcan_gm`.`user0` TO `commonlink`@`%`;
GRANT select ON `xcan_gm`.`dept` TO `commonlink`@`%`;
GRANT select ON `xcan_gm`.`dept_user` TO `commonlink`@`%`;
GRANT select ON `xcan_gm`.`group0` TO `commonlink`@`%`;
GRANT select ON `xcan_gm`.`group_user` TO `commonlink`@`%`;
GRANT select ON `xcan_gm`.`org_tag` TO `commonlink`@`%`;
GRANT select ON `xcan_gm`.`org_tag_target` TO `commonlink`@`%`;
-- AAS ------------------------------
GRANT select ON `xcan_gm`.`service` TO `commonlink`@`%`;
GRANT select ON `xcan_gm`.`api` TO `commonlink`@`%`;
GRANT select ON `xcan_gm`.`app` TO `commonlink`@`%`;
GRANT select ON `xcan_gm`.`oauth2_user` TO `commonlink`@`%`;
GRANT select ON `xcan_gm`.`auth_policy` TO `commonlink`@`%`;
GRANT select ON `xcan_gm`.`to_role` TO `commonlink`@`%`;
GRANT select ON `xcan_gm`.`to_user` TO `commonlink`@`%`;
GRANT select ON `xcan_gm`.`to_role_user` TO `commonlink`@`%`;
GRANT select ON `xcan_gm`.`app_open` TO `commonlink`@`%`;
-- WPUSH ------------------------------
GRANT select ON `xcan_gm`.`message_center_online` TO `commonlink`@`%`;
-- COMMON ------------------------------
GRANT select ON `xcan_gm`.`c_i18n_messages` TO `commonlink`@`%`;
GRANT select ON `xcan_gm`.`c_setting` TO `commonlink`@`%`;
GRANT select ON `xcan_gm`.`c_setting_tenant` TO `commonlink`@`%`;
GRANT select ON `xcan_gm`.`c_setting_tenant_quota` TO `commonlink`@`%`;
GRANT select ON `xcan_gm`.`c_setting_user` TO `commonlink`@`%`;
FLUSH PRIVILEGES;
-- STORE ----------------------------------- 
GRANT select ON `xcan_store`.`goods` TO `commonlink`@`%`;
GRANT select ON `xcan_store`.`store_goods` TO `commonlink`@`%`;
FLUSH PRIVILEGES;
```

##### 创建联邦表

###### 1. 创建联邦 server

```sql
-- GM 库
CREATE
SERVER xcan_gm_link
FOREIGN DATA WRAPPER mysql
OPTIONS (USER 'commonlink', PASSWORD 'your_password', HOST 'your_mysql_host', PORT 3306, DATABASE 'xcan_gm');
-- STORE 库
CREATE
SERVER xcan_store_link
FOREIGN DATA WRAPPER mysql
OPTIONS (USER 'commonlink', PASSWORD 'your_password', HOST 'your_mysql_host', PORT 3306, DATABASE 'xcan_store');
```

##### 2. 创建联邦表

***注意：联邦表是在 xcan_commonlink 库下创建。**

```sql
-- GM
CREATE TABLE `tenant`
(
    `id`                 bigint(20) NOT NULL COMMENT 'Primary key ID',
    `no`                 varchar(20)  NOT NULL COMMENT 'Code',
    `name`               varchar(100) NOT NULL DEFAULT '' COMMENT 'Tenant name',
    `type`               varchar(30)  NOT NULL DEFAULT '-1' COMMENT 'Tenant type: -1-Unknown; 1-Individual; 2-Enterprise; 3-Government/Institution',
    `source`             varchar(20)  NOT NULL COMMENT 'Tenant source: PLAT_REGISTER-Platform registration; BACK_ADD-Backend added',
    `real_name_status`   varchar(20)  NOT NULL DEFAULT '0' COMMENT 'Real-name verification status: PENDING-Pending review, PASSED-Approved, FAILURE-Rejected',
    `status`             varchar(20)  NOT NULL DEFAULT '0' COMMENT 'Tenant status: 1-Enabled; 2-Canceling; 3-Canceled; 4-Disabled',
    `apply_cancel_date`  datetime              DEFAULT '2001-01-01 00:00:00' COMMENT 'Cancellation application date',
    `address`            varchar(160)          DEFAULT '' COMMENT 'Mailing address',
    `locked`             int(1) NOT NULL DEFAULT '0' COMMENT 'Lock status: 0-Unlocked; 1-Locked',
    `last_lock_date`     datetime              DEFAULT NULL COMMENT 'Last lock date',
    `lock_start_date`    datetime              DEFAULT NULL COMMENT 'Lock start date',
    `lock_end_date`      datetime              DEFAULT NULL COMMENT 'Lock end date',
    `remark`             varchar(200)          DEFAULT '' COMMENT 'Remarks',
    `created_by`         bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Creator',
    `created_date`       datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Creation date',
    `last_modified_by`   bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Last modifier',
    `last_modified_date` datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Last modification date',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                  `idx_name` (`name`) USING BTREE,
    KEY                  `idx_created_by` (`created_by`) USING BTREE,
    KEY                  `idx_last_modified_by` (`last_modified_by`) USING BTREE,
    KEY                  `idx_created_date` (`created_date`) USING BTREE,
    KEY                  `idx_last_modified_date` (`last_modified_date`) USING BTREE,
    KEY                  `idx_apply_cancel_date` (`apply_cancel_date`) USING BTREE,
    KEY                  `idx_real_name_status` (`real_name_status`) USING BTREE,
    KEY                  `idx_status` (`status`) USING BTREE,
    KEY                  `idx_source` (`source`) USING BTREE,
    KEY                  `idx_type` (`type`) USING BTREE,
    KEY                  `idx_locked_flag` (`locked`) USING BTREE,
    KEY                  `idx_lock_start_date` (`lock_start_date`) USING BTREE,
    KEY                  `idx_lock_end_date` (`lock_end_date`) USING BTREE,
    KEY                  `idx_summary_group` (`source`,`type`,`status`,`real_name_status`,`locked`,`created_date`) USING BTREE
    -- ,FULLTEXT KEY `fx_name_no` (`name`,`no`) /*!50100 WITH PARSER `ngram` */ 
) ENGINE = FEDERATED CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = 'Tenant'  CONNECTION = 'xcan_gm_link/tenant';

CREATE TABLE `user0`
(
    `id`                          bigint(20) unsigned NOT NULL COMMENT 'Primary key ID',
    `username`                    varchar(100) NOT NULL DEFAULT '' COMMENT 'Username',
    `first_name`                  varchar(100) NOT NULL DEFAULT '' COMMENT 'First name',
    `last_name`                   varchar(100) NOT NULL DEFAULT '' COMMENT 'Last name',
    `full_name`                   varchar(100) NOT NULL DEFAULT '' COMMENT 'Full name',
    `itc`                         varchar(8)            DEFAULT '' COMMENT 'International dialing code',
    `country`                     varchar(16)           DEFAULT '' COMMENT 'Country code',
    `email`                       varchar(100) NOT NULL DEFAULT '' COMMENT 'Registered email',
    `mobile`                      varchar(16)  NOT NULL DEFAULT '' COMMENT 'Registered mobile number',
    `signup_account_type`         varchar(16)           DEFAULT 'NOOP' COMMENT 'Registration account type: MOBILE-Mobile; EMAIL-Email; NOOP=No operation',
    `signup_account`              varchar(100)          DEFAULT '' COMMENT 'Tenant registration account (mobile/email)',
    `signup_device_id`            varchar(100)          DEFAULT NULL COMMENT 'Registration device ID',
    `landline`                    varchar(40)           DEFAULT '' COMMENT 'Landline',
    `tenant_id`                   bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Associated tenant ID',
    `tenant_name`                 varchar(100) NOT NULL DEFAULT '' COMMENT 'Tenant name',
    `avatar`                      varchar(400)          DEFAULT '' COMMENT 'Avatar URL',
    `title`                       varchar(100)          DEFAULT '' COMMENT 'Job title',
    `gender`                      varchar(10)           DEFAULT '' COMMENT 'Gender: MALE-Male; FEMALE-Female; UNKNOWN-Unknown',
    `address`                     varchar(200)          DEFAULT NULL COMMENT 'Contact address',
    `source`                      varchar(40)  NOT NULL DEFAULT '' COMMENT 'User source: PLAT_REGISTER-Platform registration; BACK_ADD-Backend added; THIRD_PARTY_LOGIN-Third-party login; LDAP_SYNCHRONIZE-LDAP sync',
    `directory_id`                bigint(20) DEFAULT NULL COMMENT 'Directory ID',
    `main_dept_id`                bigint(20) DEFAULT '-1' COMMENT 'Primary department ID',
    `online`                      int(1) NOT NULL COMMENT 'Online status',
    `online_date`                 datetime              DEFAULT NULL COMMENT 'Last online date',
    `offline_date`                datetime              DEFAULT NULL COMMENT 'Last offline date',
    `dept_head`                   int(1) DEFAULT '0' COMMENT 'Department head flag',
    `sys_admin`                   int(1) NOT NULL DEFAULT '0' COMMENT 'System administrator: 0-Regular user; 1-Admin',
    `expired`                     int(1) NOT NULL COMMENT 'Expiration flag: 0-Not expired; 1-Expired',
    `expired_date`                datetime              DEFAULT NULL COMMENT 'Expiration date',
    `enabled`                     int(1) NOT NULL DEFAULT '0' COMMENT 'User status: 0-Disabled; 1-Enabled',
    `disable_reason`              varchar(200)          DEFAULT '' COMMENT 'Disable reason',
    `deleted`                     int(1) NOT NULL DEFAULT '0' COMMENT 'Deletion status: 0-Not deleted; 1-Deleted',
    `locked`                      int(1) NOT NULL COMMENT 'Lock status: 0-Unlocked; 1-Locked',
    `last_lock_date`              datetime              DEFAULT NULL COMMENT 'Last lock date',
    `lock_start_date`             datetime              DEFAULT NULL COMMENT 'Lock start date',
    `lock_end_date`               datetime              DEFAULT NULL COMMENT 'Lock end date',
    `last_modified_password_date` datetime              DEFAULT NULL COMMENT 'Last password modification date',
    `created_by`                  bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Creator',
    `created_date`                datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Creation date',
    `last_modified_by`            bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Last modifier',
    `last_modified_date`          datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Last modification date',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_username` (`username`) USING BTREE,
    KEY                           `idx_source` (`source`) USING BTREE,
    KEY                           `idx_gender` (`gender`) USING BTREE,
    KEY                           `idx_admin_flag` (`sys_admin`) USING BTREE,
    KEY                           `idx_enabled_flag` (`enabled`) USING BTREE,
    KEY                           `idx_locked_flag` (`locked`) USING BTREE,
    KEY                           `idx_lock_start_date` (`lock_start_date`) USING BTREE,
    KEY                           `idx_lock_end_date` (`lock_end_date`) USING BTREE,
    KEY                           `idx_fullname` (`full_name`) USING BTREE,
    KEY                           `idx_directory_id` (`directory_id`),
    KEY                           `idx_deleted_flag` (`deleted`) USING BTREE,
    KEY                           `uidx_tenantId_mobile` (`mobile`,`tenant_id`) USING BTREE,
    KEY                           `uidx_tenantId_email` (`email`,`tenant_id`) USING BTREE,
    KEY                           `idx_summary_group` (`source`,`sys_admin`,`enabled`,`locked`,`gender`,`created_date`) USING BTREE,
    KEY                           `idx_summary_tenant_group` (`tenant_id`,`source`,`sys_admin`,`enabled`,`locked`,`gender`,`created_date`) USING BTREE
    -- ,FULLTEXT KEY `fx_name_mobile_title_username` (`full_name`,`mobile`,`title`,`username`) /*!50100 WITH PARSER `ngram` */
) ENGINE=FEDERATED DEFAULT ROW_FORMAT=DYNAMIC COMMENT='User' CONNECTION='xcan_gm_link/user0';

CREATE TABLE `dept`
(
    `id`                 bigint(20) unsigned NOT NULL COMMENT 'Primary key ID',
    `tenant_id`          bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Tenant ID',
    `code`               varchar(32)  NOT NULL DEFAULT '' COMMENT 'Department code',
    `parent_like_id`     varchar(200)          DEFAULT '' COMMENT 'Hierarchical department ID path (concatenated with "-")',
    `name`               varchar(100) NOT NULL DEFAULT '' COMMENT 'Organization name',
    `pid`                bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Parent organization ID',
    `level`              int(11) NOT NULL COMMENT 'Organization level',
    `created_by`         bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Creator',
    `created_date`       datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Creation date',
    `last_modified_by`   bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Last modifier',
    `last_modified_date` datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Last modification date',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                  `idx_pid` (`pid`) USING BTREE,
    KEY                  `idx_parent_like_id` (`parent_like_id`) USING BTREE,
    KEY                  `idx_name` (`name`) USING BTREE,
    KEY                  `idx_code` (`code`) USING BTREE,
    KEY                  `idx_tenant_id` (`tenant_id`) USING BTREE,
    KEY                  `idx_created_date` (`created_date`) USING BTREE,
    KEY                  `idx_level` (`level`) USING BTREE
    -- ,FULLTEXT KEY `fx_name_code` (`code`,`name`) /*!50100 WITH PARSER `ngram` */
) ENGINE=FEDERATED DEFAULT COMMENT='Department'  CONNECTION = 'xcan_gm_link/dept';

CREATE TABLE `dept_user`
(
    `id`           bigint(20) unsigned NOT NULL COMMENT 'Primary key ID',
    `dept_id`      bigint(100) NOT NULL DEFAULT '-1' COMMENT 'Department code',
    `user_id`      bigint(20) NOT NULL DEFAULT '-1' COMMENT 'User ID',
    `main_dept`    int(1) NOT NULL COMMENT 'Primary department flag',
    `dept_head`    int(1) NOT NULL DEFAULT '0' COMMENT 'Department head flag',
    `tenant_id`    bigint(20) NOT NULL COMMENT 'Tenant ID',
    `created_by`   bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Creator',
    `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Creation date',
    PRIMARY KEY (`id`) USING BTREE,
    KEY            `idx_user_id` (`user_id`) USING BTREE,
    KEY            `idx_dept_id` (`dept_id`) USING BTREE,
    KEY            `idx_tenant_id` (`tenant_id`) USING BTREE,
    KEY            `uidx_dept_user_id` (`dept_id`,`user_id`) USING BTREE
) ENGINE=FEDERATED DEFAULT COMMENT='Department-user relationship'  CONNECTION = 'xcan_gm_link/dept_user';

CREATE TABLE `group0`
(
    `id`                   bigint(20) unsigned NOT NULL COMMENT 'Primary key ID',
    `name`                 varchar(100) NOT NULL DEFAULT '' COMMENT 'Name',
    `code`                 varchar(80)  NOT NULL COMMENT 'Code',
    `enabled`              int(1) NOT NULL DEFAULT '1' COMMENT 'Enabled status',
    `source`               varchar(20)  NOT NULL COMMENT 'User group source: BACK_ADD-Backend added; LDAP_SYNCHRONIZE-LDAP sync',
    `directory_id`         bigint(20) DEFAULT NULL COMMENT 'Directory ID',
    `directory_gid_number` varchar(40)           DEFAULT NULL COMMENT 'Directory group member ID',
    `remark`               varchar(200)          DEFAULT '' COMMENT 'Remarks',
    `tenant_id`            bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Tenant ID (-1 indicates unassociated tenant)',
    `created_by`           bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Creator',
    `created_date`         datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Creation date',
    `last_modified_by`     bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Last modifier',
    `last_modified_date`   datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Last modification date',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_tenant_id_code` (`tenant_id`,`code`) USING BTREE,
    KEY                    `idx_code` (`code`) USING BTREE,
    KEY                    `idx_name` (`name`) USING BTREE,
    KEY                    `idx_create_date` (`created_date`) USING BTREE,
    KEY                    `idx_source` (`source`) USING BTREE,
    KEY                    `idx_directory_id` (`directory_id`) USING BTREE,
    KEY                    `idx_summary_group` (`enabled`,`source`,`created_date`) USING BTREE,
    KEY                    `idx_summary_tenant_group` (`tenant_id`,`enabled`,`source`,`created_date`) USING BTREE
    -- , FULLTEXT KEY `fx_name_tag_value` (`name`,`code`) /*!50100 WITH PARSER `ngram` */
) ENGINE=FEDERATED DEFAULT COMMENT='User group' CONNECTION='xcan_gm_link/group0';

CREATE TABLE `group_user`
(
    `id`           bigint(20) unsigned NOT NULL COMMENT 'Primary key ID',
    `group_id`     bigint(20) NOT NULL COMMENT 'User group ID',
    `user_id`      bigint(20) NOT NULL COMMENT 'User ID',
    `directory_id` bigint(20) DEFAULT NULL COMMENT 'Directory ID',
    `tenant_id`    bigint(20) NOT NULL COMMENT 'Tenant ID',
    `created_by`   bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Creator',
    `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Creation date',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_group_user_id` (`group_id`,`user_id`) USING BTREE,
    KEY            `idx_group_id` (`group_id`) USING BTREE,
    KEY            `idx_user_id` (`user_id`) USING BTREE,
    KEY            `idx_tenant_id` (`tenant_id`) USING BTREE,
    KEY            `idx_directory_id` (`directory_id`) USING BTREE
) ENGINE=FEDERATED DEFAULT COMMENT='User group-member association' CONNECTION='xcan_gm_link/group_user';

CREATE TABLE `org_tag`
(
    `id`                 bigint(20) NOT NULL COMMENT 'ID',
    `name`               varchar(100) NOT NULL COMMENT 'Tag name',
    `tenant_id`          bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Associated tenant ID',
    `created_by`         bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Creator',
    `created_date`       datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Creation date',
    `last_modified_by`   bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Last modifier',
    `last_modified_date` datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Last modification date',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `idx_tenant_id_name` (`name`,`tenant_id`) USING BTREE,
    KEY                  `idx_create_date` (`created_date`) USING BTREE,
    KEY                  `idx_tenant_id` (`tenant_id`) USING BTREE
    -- ,FULLTEXT KEY `fx_name` (`name`) /*!50100 WITH PARSER `ngram` */
) ENGINE=FEDERATED DEFAULT COMMENT='Tag' CONNECTION = 'xcan_gm_link/org_tag';

CREATE TABLE `org_tag_target`
(
    `id`           bigint(20) NOT NULL COMMENT 'ID',
    `tag_id`       bigint(20) NOT NULL COMMENT '标签ID',
    `target_type`  varchar(16) NOT NULL COMMENT '目标类型：部门、组、用户',
    `target_id`    bigint(20) NOT NULL COMMENT '目标ID',
    `tenant_id`    bigint(20) NOT NULL DEFAULT '-1' COMMENT '租户ID',
    `created_by`   bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
    `created_date` datetime    NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_tag_target_id` (`tag_id`,`target_id`) USING BTREE,
    KEY            `idx_tag_id` (`tag_id`) USING BTREE,
    KEY            `idx_target_id` (`target_id`) USING BTREE,
    KEY            `idx_tenant_id` (`tenant_id`) USING BTREE,
    KEY            `idx_target_type` (`target_type`) USING BTREE,
    KEY            `idx_summary_group` (`created_date`,`target_type`) USING BTREE,
    KEY            `idx_summary_tenant_group` (`tenant_id`,`created_date`,`target_type`) USING BTREE
) ENGINE=FEDERATED DEFAULT COMMENT='标签对象' CONNECTION = 'xcan_gm_link/org_tag_target';

-- AAS
CREATE TABLE `service`
(
    `id`                 bigint(20) NOT NULL COMMENT '主键ID',
    `code`               varchar(80)  NOT NULL COMMENT '编码',
    `name`               varchar(100) NOT NULL DEFAULT '' COMMENT '名称',
    `description`        varchar(200) NOT NULL DEFAULT '' COMMENT '描述',
    `source`             varchar(16)  NOT NULL COMMENT '来源：BACK_ADD、EUREKA、NOCAS、CONSUL',
    `enabled`            int(1) NOT NULL COMMENT '有效状态',
    `api_num`            int(11) NOT NULL DEFAULT '0' COMMENT '接口数',
    `route_path`         varchar(80)  NOT NULL DEFAULT '' COMMENT '网关路由路径',
    `url`                varchar(400) NOT NULL DEFAULT '' COMMENT '服务地址',
    `health_url`         varchar(400) NOT NULL COMMENT '健康检查地址',
    `api_doc_url`        varchar(400) NOT NULL COMMENT 'API文档地址',
    `created_by`         bigint(21) NOT NULL COMMENT '创建人',
    `created_date`       datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
    `last_modified_by`   bigint(21) NOT NULL COMMENT '最后修改人',
    `last_modified_date` datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_code` (`code`) USING BTREE,
    KEY                  `idx_name` (`name`) USING BTREE,
    KEY                  `idx_summary_group` (`created_date`,`source`,`enabled`) USING BTREE
    -- ,FULLTEXT KEY `fx_name_code_description` (`name`,`code`,`description`) /*!50100 WITH PARSER `ngram` */
) ENGINE=FEDERATED DEFAULT COMMENT='服务表' CONNECTION='xcan_gm_link/service';

CREATE TABLE `api`
(
    `id`                   bigint(20) NOT NULL COMMENT '主键ID',
    `name`                 varchar(600) NOT NULL DEFAULT '' COMMENT '名称',
    `operation_id`         varchar(100) NOT NULL COMMENT '编码，对应OAS operationId',
    `scopes`               varchar(100)          DEFAULT NULL COMMENT '对应OAS安全需求',
    `uri`                  varchar(800) NOT NULL DEFAULT '' COMMENT '接口地址',
    `method`               varchar(16)  NOT NULL COMMENT '请求方法',
    `type`                 varchar(16)  NOT NULL DEFAULT '' COMMENT '接口类型(ApiType)',
    `description`          text COMMENT '描述',
    `resource_name`        varchar(100) NOT NULL COMMENT '资源名',
    `resource_description` varchar(200) NOT NULL COMMENT '资源描述',
    `enabled`              int(1) NOT NULL COMMENT '有效状态',
    `service_id`           bigint(20) NOT NULL COMMENT '服务ID',
    `service_code`         varchar(80)  NOT NULL COMMENT '所属服务编码',
    `service_name`         varchar(200) NOT NULL DEFAULT '' COMMENT '所属服务名称',
    `service_enabled`      int(1) DEFAULT NULL COMMENT '服务启用状态',
    `sync`                 int(1) NOT NULL COMMENT '是否是同步接口',
    `swagger_deleted`      int(1) DEFAULT NULL COMMENT '接口是否从Swagger删除',
    `created_by`           bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
    `created_date`         datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
    `last_modified_by`     bigint(20) NOT NULL DEFAULT '-1' COMMENT '最后修改人',
    `last_modified_date`   datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_service_code` (`operation_id`,`service_code`) USING BTREE,
    KEY                    `idx_service_name` (`service_name`) USING BTREE,
    KEY                    `uidx_name` (`name`) USING BTREE,
    KEY                    `idx_service_code` (`service_code`) USING BTREE,
    KEY                    `idx_summary_group` (`method`,`type`,`enabled`,`created_date`) USING BTREE,
    -- ,FULLTEXT KEY `fx_name_code_service_name_description` (`name`,`operation_id`,`service_name`,`description`) /*!50100 WITH PARSER `ngram` */
) ENGINE=FEDERATED DEFAULT COMMENT='接口表' CONNECTION='xcan_gm_link/api';

CREATE TABLE `app`
(
    `id`                 bigint(20) NOT NULL COMMENT '主键ID',
    `code`               varchar(80)  NOT NULL DEFAULT '' COMMENT '编码',
    `name`               varchar(100) NOT NULL DEFAULT '' COMMENT '名称',
    `show_name`          varchar(40)  NOT NULL DEFAULT '' COMMENT '展示名称',
    `icon`               varchar(200) NOT NULL COMMENT '图标',
    `type`               varchar(10)  NOT NULL COMMENT '应用类型：CLOUD_APP、BASE_APP、OP_APP',
    `edition_type`       varchar(20)           DEFAULT NULL COMMENT '版本类型',
    `description`        varchar(200) NOT NULL DEFAULT '' COMMENT '描述',
    `auth_ctrl`          int(1) DEFAULT NULL COMMENT '授权控制标志态：0-不控制；1-控制',
    `enabled`            int(1) NOT NULL COMMENT '启用状态：0-禁用；1-启用',
    `url`                varchar(200) NOT NULL COMMENT 'URL',
    `sequence`           int(11) NOT NULL DEFAULT '1' COMMENT '序号，值越小越靠前',
    `api_ids`            varchar(800)          DEFAULT NULL COMMENT '关联接口编码',
    `version`            varchar(20)  NOT NULL COMMENT '应用版本：major(主版本号)、minor(次版本号)、patch(修订号)',
    `open_stage`         varchar(16)  NOT NULL COMMENT '开通阶段：(SINUP)注册、AUTH_PASSED(实名认证通过户) 、OPEN_SUCCESS(开通成功)',
    `client_id`          varchar(32)           DEFAULT NULL COMMENT '所属端ID',
    `tenant_id`          bigint(20) NOT NULL COMMENT '租户ID',
    `created_by`         bigint(20) NOT NULL COMMENT '创建人',
    `created_date`       datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
    `last_modified_by`   bigint(20) NOT NULL COMMENT '最后修改人',
    `last_modified_date` datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_code_edition_version` (`code`,`edition_type`,`version`) USING BTREE,
    KEY                  `idx_tenant_id` (`tenant_id`) USING BTREE,
    KEY                  `idx_enabled_fl_id_sequence` (`enabled`,`id`,`sequence`) USING BTREE,
    KEY                  `uidx_show_name_name` (`show_name`,`code`) USING BTREE,
    KEY                  `idx_edition_type` (`edition_type`) USING BTREE,
    KEY                  `idx_version` (`version`) USING BTREE
    -- ,FULLTEXT KEY `fx_name_code_show_name_description` (`code`,`name`,`show_name`,`description`) /*!50100 WITH PARSER `ngram` */
) ENGINE=FEDERATED DEFAULT COMMENT='应用表' CONNECTION='xcan_gm_link/app';

CREATE TABLE `oauth2_user`
(
    `id`                          bigint(20) NOT NULL COMMENT '主键ID',
    `username`                    varchar(100) NOT NULL DEFAULT '' COMMENT '用户名',
    `password`                    varchar(160) NOT NULL DEFAULT '' COMMENT '用户密码',
    `enabled`                     int(1) NOT NULL DEFAULT '1' COMMENT '是否启用状态',
    `account_non_expired`         int(1) NOT NULL DEFAULT '1' COMMENT '账号是否未过期',
    `account_non_locked`          int(1) NOT NULL DEFAULT '1' COMMENT '账号是否未锁定',
    `credentials_non_expired`     int(1) NOT NULL DEFAULT '1' COMMENT '密码是否未过期',
    `first_name`                  varchar(100) NOT NULL DEFAULT '' COMMENT '名字',
    `last_name`                   varchar(100) NOT NULL DEFAULT '' COMMENT '姓',
    `full_name`                   varchar(100) NOT NULL DEFAULT '' COMMENT '全名',
    `mobile`                      varchar(16)  NOT NULL DEFAULT '' COMMENT '注册手机号',
    `email`                       varchar(100) NOT NULL DEFAULT '' COMMENT '注册邮箱',
    `sys_admin`                   int(1) NOT NULL DEFAULT '0' COMMENT '是否系统管理员',
    `to_user`                     int(1) NOT NULL DEFAULT '0' COMMENT '是否运营用户',
    `tenant_id`                   bigint(20) NOT NULL COMMENT '租户ID',
    `tenant_name`                 varchar(100) NOT NULL DEFAULT '' COMMENT '租户名称',
    `tenant_real_name_status`     varchar(20)  NOT NULL COMMENT '是否实名认证(0未认证 1已认证)',
    `main_dept_id`                bigint(20) DEFAULT NULL COMMENT '用户主部门ID',
    `password_strength`           varchar(10)  NOT NULL DEFAULT '' COMMENT '密码强度：WEAK-弱，MEDIUM-中，STRONG-强',
    `password_expired_date`       datetime              DEFAULT NULL COMMENT '密码到期时间',
    `last_modified_password_date` datetime              DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改密码的时间',
    `expired_date`                datetime              DEFAULT NULL COMMENT '用户账号过期时间',
    `deleted`                     int(1) NOT NULL DEFAULT '0' COMMENT '用户账号是否已删除',
    `directory_id`                bigint(20) DEFAULT NULL COMMENT '用户LDAP目录ID',
    `default_language`            varchar(20)           DEFAULT NULL COMMENT '用户默认语言',
    `default_time_zone`           varchar(40)           DEFAULT NULL COMMENT '用户默认时区',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                           `idx_tenant_id` (`tenant_id`) USING BTREE,
    KEY                           `idx_tenant_real_name_status` (`tenant_real_name_status`) USING BTREE,
    KEY                           `idx_passd_expired_date` (`passwordd_expired_date`) USING BTREE
) ENGINE=FEDERATED DEFAULT COMMENT='用户' CONNECTION='xcan_gm_link/oauth2_user';

CREATE TABLE `auth_policy`
(
    `id`                 bigint(20) NOT NULL COMMENT '主键ID',
    `name`               varchar(32) NOT NULL DEFAULT '' COMMENT '名称',
    `code`               varchar(80) NOT NULL COMMENT '编码，规则名称英文蛇形表示',
    `enabled_flag`       int(1) NOT NULL COMMENT '有效状态',
    `type`               varchar(20) NOT NULL DEFAULT '1' COMMENT '策略分类',
    `default0`           int(1) NOT NULL COMMENT '默认权限策略',
    `grant_stage`        varchar(20) NOT NULL DEFAULT '-1' COMMENT '授权阶段',
    `description`        varchar(200)         DEFAULT '' COMMENT '描述',
    `app_id`             bigint(20) NOT NULL DEFAULT '-1' COMMENT '所属应用ID',
    `client_id`          varchar(80) NOT NULL COMMENT '应用所属端',
    `tenant_id`          bigint(20) NOT NULL DEFAULT '-1' COMMENT '租户ID',
    `created_by`         bigint(20) NOT NULL COMMENT '创建人',
    `created_date`       datetime    NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
    `last_modified_by`   bigint(20) NOT NULL COMMENT '最后修改人',
    `last_modified_date` datetime    NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_name_app_id` (`name`,`tenant_id`,`app_id`) USING BTREE,
    UNIQUE KEY `uidx_code_app_id` (`code`,`tenant_id`,`app_id`) USING BTREE,
    KEY                  `idx_app_id` (`app_id`) USING BTREE,
    KEY                  `idx_tenant_id` (`tenant_id`) USING BTREE,
    KEY                  `idx_enabled_flag` (`enabled_flag`) USING BTREE,
    KEY                  `idx_type` (`type`) USING BTREE,
    KEY                  `idx_client_id` (`client_id`) USING BTREE
    -- ,FULLTEXT KEY `fx_code_name_description` (`code`,`name`,`description`) /*!50100 WITH PARSER `ngram` */
) ENGINE=FEDERATED DEFAULT COMMENT='授权策略' CONNECTION='xcan_gm_link/auth_policy';

CREATE TABLE `to_role`
(
    `id`                 bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`               varchar(100) NOT NULL DEFAULT '' COMMENT '名称',
    `code`               varchar(80)  NOT NULL COMMENT '编码，规则名称英文蛇形表示',
    `enabled`            int(1) NOT NULL COMMENT '有效状态：0-禁用；1-启用',
    `description`        varchar(200)          DEFAULT '' COMMENT '描述',
    `app_id`             bigint(20) NOT NULL DEFAULT '-1' COMMENT '所属应用ID',
    `created_by`         bigint(20) NOT NULL COMMENT '创建人',
    `created_date`       datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
    `last_modified_by`   bigint(20) NOT NULL COMMENT '最后修改人',
    `last_modified_date` datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_code` (`code`) USING BTREE,
    UNIQUE KEY `uidx_name` (`name`) USING BTREE,
    KEY                  `idx_app_id` (`app_id`) USING BTREE
    -- ,FULLTEXT KEY `fx_name_code` (`name`,`code`) /*!50100 WITH PARSER `ngram` */
) ENGINE=FEDERATED DEFAULT COMMENT='运营策略（租户运营角色）' CONNECTION='xcan_gm_link/to_role';

CREATE TABLE `to_user`
(
    `id`           bigint(20) NOT NULL COMMENT '主键ID',
    `user_id`      bigint(20) NOT NULL COMMENT '用户ID',
    `created_by`   bigint(20) NOT NULL COMMENT '创建人',
    `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uid_user_id` (`user_id`) USING BTREE,
    KEY            `idx_created_date` (`created_date`) USING BTREE
) ENGINE=FEDERATED DEFAULT COMMENT='策略用户关联表' CONNECTION='xcan_gm_link/to_user';

CREATE TABLE `to_role_user`
(
    `id`           bigint(20) NOT NULL COMMENT '主键ID',
    `to_role_id`   bigint(20) NOT NULL COMMENT '租户运营角色ID',
    `user_id`      bigint(20) NOT NULL COMMENT '用户ID',
    `created_by`   bigint(20) NOT NULL COMMENT '创建人',
    `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_policy_user` (`to_role_id`,`user_id`) USING BTREE,
    KEY            `idx_policy_id` (`to_role_id`) USING BTREE,
    KEY            `idx_user_id` (`user_id`) USING BTREE,
    KEY            `idx_created_date` (`created_date`) USING BTREE
) ENGINE=FEDERATED DEFAULT COMMENT='策略用户关联表' CONNECTION='xcan_gm_link/to_role_user';

CREATE TABLE `app_open`
(
    `id`                 bigint(20) NOT NULL COMMENT '主键ID',
    `app_id`             bigint(20) NOT NULL COMMENT '开通应用ID',
    `app_code`           varchar(80) NOT NULL COMMENT '开通应用编码',
    `app_type`           varchar(20) NOT NULL COMMENT '应用类型',
    `edition_type`       varchar(20)          DEFAULT NULL COMMENT '版本类型',
    `version`            varchar(20) NOT NULL COMMENT '开通版本版本',
    `client_id`          varchar(80) NOT NULL COMMENT '应用端ID',
    `tenant_id`          bigint(20) NOT NULL COMMENT '开通租户ID',
    `user_id`            bigint(20) NOT NULL DEFAULT '-1' COMMENT '开通用户ID',
    `open_date`          datetime    NOT NULL COMMENT '开通时间',
    `expiration_date`    datetime    NOT NULL COMMENT '到期时间',
    `expiration_deleted` int(1) NOT NULL COMMENT '过期删除标志',
    `op_client_open`     int(1) NOT NULL COMMENT '运营端开通标志',
    `created_date`       datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                  `idx_created_date` (`created_date`) USING BTREE,
    KEY                  `idx_expiration_date` (`expiration_date`) USING BTREE,
    KEY                  `idx_expiration_deleted_flag` (`expiration_deleted`) USING BTREE,
    KEY                  `idx_op_client_open_flag` (`op_client_open`) USING BTREE,
    KEY                  `idx_app_id` (`app_id`) USING BTREE,
    KEY                  `idx_client_id` (`client_id`) USING BTREE,
    KEY                  `idx_app_code` (`app_code`) USING BTREE,
    KEY                  `idx_version` (`version`),
    KEY                  `idx_edition_type` (`edition_type`),
    KEY                  `idx_tenant_id_app` (`tenant_id`,`edition_type`,`app_code`,`version`) USING BTREE
) ENGINE=FEDERATED DEFAULT COMMENT='应用开通表' CONNECTION='xcan_gm_link/app_open';

-- WPUSH
CREATE TABLE `message_center_online`
(
    `user_id`        bigint(20) NOT NULL COMMENT '用户ID',
    `tenant_id`      bigint(20) NOT NULL COMMENT '租户ID',
    `full_name`      varchar(100) NOT NULL COMMENT '用户姓名',
    `user_agent`     varchar(200) NOT NULL COMMENT '用户终端',
    `device_id`      varchar(160) DEFAULT NULL COMMENT '设备ID',
    `remote_address` varchar(32)  NOT NULL COMMENT '访问ID地址',
    `online`         int(1) NOT NULL COMMENT '是否在线',
    `online_date`    datetime     DEFAULT NULL COMMENT '上次上线时间',
    `offline_date`   datetime     DEFAULT NULL COMMENT '上次下线时间',
    PRIMARY KEY (`user_id`) USING BTREE,
    UNIQUE KEY `uidx_user_id` (`user_id`) USING BTREE,
    KEY              `idx_tenant_id` (`tenant_id`) USING BTREE
    -- ,FULLTEXT KEY `fx_full_name_remote_address` (`full_name`,`remote_address`) /*!50100 WITH PARSER `ngram` */
) ENGINE=FEDERATED DEFAULT COMMENT='消息中心在线用户' CONNECTION='xcan_gm_link/message_center_online';

-- COMMON
CREATE TABLE `c_i18n_messages`
(
    `id`              bigint(20) NOT NULL COMMENT '主键ID',
    `type`            varchar(50)  NOT NULL COMMENT '分类',
    `language`        varchar(20)  NOT NULL COMMENT '语言',
    `default_message` varchar(100) NOT NULL COMMENT '默认消息',
    `i18n_message`    varchar(100) NOT NULL COMMENT '国际化消息',
    `private0`        int(1) NOT NULL COMMENT '是否私有化',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_key_language_default_message` (`type`,`language`,`default_message`) USING BTREE,
    KEY               `idx_type` (`type`) USING BTREE,
    KEY               `idx_language` (`language`) USING BTREE
    -- ,KEY `idx_default_message` (`default_message`) USING BTREE
) ENGINE=FEDERATED DEFAULT COMMENT='国际化资源表' CONNECTION='xcan_gm_link/c_i18n_messages';

CREATE TABLE `c_setting`
(
    `id`             bigint(20) NOT NULL COMMENT '主键ID',
    `key`            varchar(32)    NOT NULL DEFAULT '' COMMENT '配置参数key',
    `value`          varchar(16000) NOT NULL COMMENT '配置参数值',
    `global_default` int(1) NOT NULL COMMENT '是否全局默认值标志',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_key` (`key`) USING BTREE
) ENGINE=FEDERATED DEFAULT COMMENT='配置表' CONNECTION='xcan_gm_link/c_setting';

CREATE TABLE `c_setting_tenant`
(
    `id`                    bigint(20) NOT NULL COMMENT '主键ID',
    `invitation_code`       varchar(80)   DEFAULT NULL COMMENT '邀请注册码，检索冗余',
    `locale_data`           varchar(200)  DEFAULT NULL COMMENT '租户国际化设置',
    `func_data`             varchar(320)  DEFAULT NULL COMMENT '租户平台默认功能指标',
    `perf_data`             varchar(320)  DEFAULT NULL COMMENT '租户平台默认性能指标',
    `stability_data`        varchar(320)  DEFAULT NULL COMMENT '租户平台默认稳定性指标',
    `security_data`         varchar(1200) DEFAULT NULL COMMENT '租户账号安全设置',
    `server_api_proxy_data` varchar(500)  DEFAULT NULL COMMENT '服务端Api 代理配置',
    `tester_event_data`     varchar(2000) DEFAULT NULL COMMENT 'AngusTester事件通知类型配置',
    `tenant_id`             bigint(20) NOT NULL COMMENT '租户ID',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_tenant_id` (`tenant_id`) USING BTREE,
    UNIQUE KEY `uidx_invitation_code` (`invitation_code`) USING BTREE
) ENGINE=FEDERATED DEFAULT COMMENT='租户设置' CONNECTION='xcan_gm_link/c_setting_tenant';

CREATE TABLE `c_setting_tenant_quota`
(
    `id`             bigint(20) NOT NULL COMMENT '主键ID',
    `app_code`       varchar(80)  NOT NULL COMMENT '所属应用编码',
    `service_code`   varchar(80)  NOT NULL COMMENT '所属服务编码',
    `name`           varchar(100) NOT NULL COMMENT '资源名称',
    `allow_change`   int(1) NOT NULL COMMENT '是否允许修改配额',
    `lcs_ctrl`       int(1) NOT NULL COMMENT '是否许可控制配置',
    `calc_remaining` int(1) NOT NULL COMMENT '是否计算剩余配额',
    `quota`          bigint(20) NOT NULL COMMENT '当前生效配额值',
    `min`            bigint(20) NOT NULL DEFAULT '0' COMMENT '最小允许配置值',
    `max`            bigint(20) NOT NULL DEFAULT '0' COMMENT '最大允许配置值',
    `capacity`       bigint(20) NOT NULL COMMENT '总容量（所有租户上线）',
    `tenant_id`      bigint(20) NOT NULL COMMENT '租户ID',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_tenant_id_name` (`tenant_id`,`name`) USING BTREE,
    KEY              `idx_app_code` (`app_code`) USING BTREE,
    KEY              `idx_service_code` (`service_code`) USING BTREE,
    KEY              `idx_resource` (`name`) USING BTREE,
    KEY              `idx_quota` (`quota`) USING BTREE,
    KEY              `idx_allow_change_flag` (`allow_change`) USING BTREE,
    KEY              `idx_tenant_id` (`tenant_id`) USING BTREE
    -- ,FULLTEXT KEY `fx_name` (`name`) /*!50100 WITH PARSER `ngram` */
) ENGINE=FEDERATED DEFAULT COMMENT='租户配额设置' CONNECTION='xcan_gm_link/c_setting_tenant_quota';

-- json -> varchar
CREATE TABLE `c_setting_user`
(
    `id`          bigint(20) unsigned NOT NULL COMMENT '主键ID',
    `preference`  varchar(800)  DEFAULT NULL COMMENT '偏好设置',
    `api_proxy`   varchar(500)  DEFAULT NULL COMMENT '用户代理API配置',
    `social_bind` varchar(1200) DEFAULT NULL COMMENT '三方登录用户绑定信息',
    `tenant_id`   bigint(20) NOT NULL COMMENT '租户ID',
    PRIMARY KEY (`id`) USING BTREE,
    KEY           `idx_tenant_id` (`tenant_id`) USING BTREE
) ENGINE=FEDERATED DEFAULT COMMENT='用户' CONNECTION='xcan_gm_link/c_setting_user';

-- STORE
CREATE TABLE `goods`
(
    `id`                 bigint(20) NOT NULL COMMENT '主键ID',
    `no`                 varchar(20)  NOT NULL COMMENT '商品编号',
    `edition_type`       varchar(16)  NOT NULL COMMENT '版本类型',
    `type`               varchar(16)  NOT NULL COMMENT '产品类型',
    `name`               varchar(100) NOT NULL COMMENT '产品名称',
    `code`               varchar(80)  NOT NULL COMMENT '产品编码',
    `version`            varchar(16)  NOT NULL COMMENT '产品版本',
    `signature`          varchar(800)          DEFAULT '' COMMENT '商品核心制品包SHA512签名',
    `signature_artifact` varchar(200)          DEFAULT '' COMMENT '商品核心制品包',
    `package_id`         bigint(20) DEFAULT NULL COMMENT '安装包ID',
    `allow_release`      int(11) NOT NULL COMMENT '允许发布标志',
    `online`             int(11) NOT NULL COMMENT '上架标志',
    `charge`             int(11) NOT NULL COMMENT '收费标记',
    `lcs_protection`     int(11) NOT NULL COMMENT '是否许可保护',
    `apply_edition_type` varchar(80)           DEFAULT NULL COMMENT '适用应用版本类型：云服务版(CLOUD_SERVICE)/数据中心版(DATACENTER)/企业版(ENTERPRISE)/社区版(COMMUNITY)',
    `apply_app_code`     varchar(160)          DEFAULT NULL COMMENT '适用应用编码',
    `apply_version`      varchar(160)          DEFAULT NULL COMMENT '适用应用版本范围：最小版本号、最大值版本号',
    `downward_version`   varchar(160)          DEFAULT NULL COMMENT '向下兼容版本范围：最小版本号、最大值版本号',
    `deleted`            int(11) NOT NULL COMMENT '删除标志',
    `created_by`         bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
    `created_date`       datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
    `last_modified_by`   bigint(20) NOT NULL DEFAULT '-1' COMMENT '最后修改人',
    `last_modified_date` datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_code_version_type_version` (`code`,`edition_type`,`version`) USING BTREE,
    KEY                  `idx_created_date` (`created_date`) USING BTREE,
    KEY                  `idx_release` (`allow_release`) USING BTREE,
    KEY                  `idx_online` (`online`) USING BTREE,
    KEY                  `idx_charge` (`charge`) USING BTREE,
    KEY                  `idx_package_id` (`package_id`) USING BTREE,
    KEY                  `idx_created_by` (`created_by`) USING BTREE,
    KEY                  `idx_deleted` (`deleted`) USING BTREE,
    KEY                  `idx_code` (`code`) USING BTREE,
    KEY                  `idx_apply_service_code` (`apply_app_code`) USING BTREE
    -- FULLTEXT KEY `fx_name` (`name`) /*!50100 WITH PARSER `ngram` */ 
) ENGINE=FEDERATED DEFAULT COMMENT='商品表' CONNECTION='xcan_store_link/goods';

CREATE TABLE `store_goods`
(
    `id`                  bigint(20) NOT NULL COMMENT '主键ID',
    `goods_id`            bigint(20) NOT NULL COMMENT '商品ID',
    `edition_type`        varchar(16)  NOT NULL COMMENT '版本类型',
    `type`                varchar(16)  NOT NULL COMMENT '商品类型',
    `name`                varchar(100) NOT NULL COMMENT '商品名称',
    `code`                varchar(80)  NOT NULL COMMENT '商品编号',
    `version`             varchar(16)  NOT NULL COMMENT '商品版本',
    `apply_edition_type`  varchar(80)           DEFAULT NULL COMMENT '适用应用版本类型：云服务版(CLOUD_SERVICE)/数据中心版(DATACENTER)/企业版(ENTERPRISE)/社区版(COMMUNITY)',
    `charge`              int(11) NOT NULL COMMENT '收费标记',
    `icon_url`            varchar(400) NOT NULL COMMENT '商品ICON',
    `pricing_url`         varchar(400)          DEFAULT NULL COMMENT '定价地址',
    `pricing_template_id` bigint(20) DEFAULT NULL COMMENT '定价模版ID',
    `introduction`        varchar(400) NOT NULL COMMENT '商品介绍',
    -- `banner_url` varchar(1600)  DEFAULT NULL COMMENT '商品介绍Banner地址',
    -- `information` varchar(6000)  DEFAULT NULL COMMENT '描述',
    `lcs_protection`      int(11) NOT NULL COMMENT '是否许可保护',
    -- `features` varchar(3200)  DEFAULT NULL COMMENT '升级功能介绍',
    -- `videos` varchar(1800)  NOT NULL COMMENT '介绍视频',
    `allow_comment`       int(11) NOT NULL COMMENT '是否支持评论',
    `comment_num`         int(11) NOT NULL COMMENT '评论数',
    `hot`                 int(11) NOT NULL COMMENT '是否热门',
    `star_num`            int(11) NOT NULL DEFAULT '0' COMMENT '点赞数',
    `online_status`       varchar(16)  NOT NULL COMMENT '上架状态',
    `online_by`           bigint(20) NOT NULL DEFAULT '-1' COMMENT '上架人ID',
    `online_date`         datetime              DEFAULT NULL COMMENT '上架时间',
    `offline_by`          bigint(20) NOT NULL DEFAULT '-1' COMMENT '下架人',
    `offline_date`        datetime              DEFAULT NULL COMMENT '下架时间',
    `created_by`          bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',
    `created_date`        datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '创建时间',
    `last_modified_by`    bigint(20) NOT NULL DEFAULT '-1' COMMENT '最后修改人',
    `last_modified_date`  datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT '最后修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_code_version_edition` (`code`,`version`,`edition_type`) USING BTREE,
    UNIQUE KEY `uidx_goods_id` (`goods_id`) USING BTREE,
    KEY                   `idx_charge` (`charge`) USING BTREE,
    KEY                   `idx_edition_type` (`edition_type`) USING BTREE,
    KEY                   `idx_product_type` (`type`) USING BTREE,
    KEY                   `idx_hot` (`hot`) USING BTREE,
    KEY                   `idx_online_status` (`online_status`) USING BTREE,
    KEY                   `idx_comment_num` (`comment_num`) USING BTREE,
    KEY                   `idx_star_num` (`star_num`) USING BTREE,
    KEY                   `idx_created_date` (`created_date`) USING BTREE
    -- FULLTEXT KEY `fx_name_code_intro_info` (`name`,`code`,`introduction`,`information`,`apply_edition_type`) /*!50100 WITH PARSER `ngram` */ 
) ENGINE=FEDERATED DEFAULT COMMENT='商店商品表' CONNECTION='xcan_store_link/store_goods';
```

#### 创建公共数据表 - Postgres SQL

TODO

#### 发布公共数据表

###### 提供数据表访问 API

在 API 模块 cloud.xcan.angus.api.commonlink 包下定义对外提供的公共数据表对应 domain 和 repository 对象。或者将已有代码移动到 api 对应 commonlink 包下。

###### 发布 api 包

使用 maven deploy 命令将 api 包发布到私服即可。

## 接入公共数据表

#### 1. 引入依赖

Boot 模块添加自动自动装配依赖，包含公共表数据源和对应 JPA 配置。

```xml

<dependency>
  <groupId>cloud.xcan.angus</groupId>
  <artifactId>xcan-infra.web-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```

Core 模块添加项目公共数据表依赖，以 GM 服务公共数据表 xcan-angusgm.api 为例：

```xml

<dependency>
  <groupId>cloud.xcan.angus</groupId>
  <artifactId>xcan-angusgm.api</artifactId>
  <version>1.0.0</version>
</dependency>
```

> 所有公共数据表 domain 和 repository 都在包路径 cloud.xcan.angus.api.commonlink 下。

#### 2. 开启自动装配

配置 application.yml：

```yml
xcan:
  datasource:
    enabled: true
    commonlink:
      enabled: true
```

配置 application-${profile.active}.yml:

```yml
## local 环境配置示例
xcan:
  trace:
    enabled: true
  datasource:
    extra:
      dbType: '@databaseType@'
    hikari:
      readOnly: false
      connectionTestQuery: SELECT 1 FROM DUAL
      poolName: postgresHikariCP
      idleTimeout: 600000
      maxLifetime: 1800000
      maximumPoolSize: 30
      minimumIdle: 10
    commonlink:
      mysql:
        driverClassName: com.mysql.cj.jdbc.Driver
        type: com.zaxxer.hikari.HikariDataSource
        url: jdbc:mysql://${COMMON_MYSQL_HOST:your_mysql_host}:${COMMON_MYSQL_PORT:3306}/${COMMON_MYSQL_DB:xcan_common}?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=${info.app.timezone}&rewriteBatchedStatements=true
        username: ${COMMON_MYSQL_USER:commonlink}
        password: ${COMMON_MYSQL_PWD:your_password}
```

#### 3. SpringBoot 项目下使用

```java
@Resource
private UserRepo userRepo;
```
