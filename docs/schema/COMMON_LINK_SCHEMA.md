# Public Data Tables

[English](COMMON_LINK_SCHEMA.md) | [中文](COMMON_LINK_SCHEMA_zh.md)

Leveraging Federated/DBlink database technology, this project aggregates public data from various business databases to provide a unified data source and JPA Repository access interfaces.

***Key Benefits:***
- ***Simplified Usage***: JPA Repository access is more straightforward than RESTful API calls
- ***Enhanced Performance***: Eliminates HTTP protocol parsing and JSON serialization overhead
- ***Reduced Coding***: Directly reuse existing entity definitions and JPA Repositories without creating OpenFeign interfaces or DTO/VO objects

***All public tables are logically stored in the `xcan_commonlink` database via Federated/DBlink technology. Business services access these tables by connecting to `xcan_commonlink`.***

## Available Public Tables

#### Creating Public Tables - MySQL

Public tables are implemented using MySQL [Federated Storage Engine](http://wiki.xcan.work/pages/viewpage.action?pageId=14647418).

##### Creating Commonlink User

The `commonlink` user is used for unified access to Federated/DBlink servers. This user must be created and authorized on each business database server.

###### 1. Create Database and User
```sql
-- Create database
CREATE DATABASE `xcan_commonlink` DEFAULT CHARACTER SET utf8mb4;

-- Create user
-- **** Note: For production environments, create user on all PXC nodes ****
CREATE USER 'commonlink'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON xcan_commonlink.* TO 'commonlink'@'%' IDENTIFIED BY 'your_password';
FLUSH PRIVILEGES;
```

###### 2. Grant Permissions
***Note: Angus applications only require SELECT privileges for `commonlink` user by default.***

```sql
-- GM -----
GRANT SELECT ON `xcan_gm`.`tenant` TO `commonlink`@`%`;
GRANT SELECT ON `xcan_gm`.`user0` TO `commonlink`@`%`;
GRANT SELECT ON `xcan_gm`.`dept` TO `commonlink`@`%`;
GRANT SELECT ON `xcan_gm`.`dept_user` TO `commonlink`@`%`;
GRANT SELECT ON `xcan_gm`.`group0` TO `commonlink`@`%`;
GRANT SELECT ON `xcan_gm`.`group_user` TO `commonlink`@`%`;
GRANT SELECT ON `xcan_gm`.`org_tag` TO `commonlink`@`%`;
GRANT SELECT ON `xcan_gm`.`org_tag_target` TO `commonlink`@`%`;
GRANT SELECT ON `xcan_gm`.`service` TO `commonlink`@`%`;
GRANT SELECT ON `xcan_gm`.`api` TO `commonlink`@`%`;
GRANT SELECT ON `xcan_gm`.`app` TO `commonlink`@`%`;
GRANT SELECT ON `xcan_gm`.`oauth2_user` TO `commonlink`@`%`;
GRANT SELECT ON `xcan_gm`.`auth_policy` TO `commonlink`@`%`;
GRANT SELECT ON `xcan_gm`.`to_role` TO `commonlink`@`%`;
GRANT SELECT ON `xcan_gm`.`to_user` TO `commonlink`@`%`;
GRANT SELECT ON `xcan_gm`.`to_role_user` TO `commonlink`@`%`;
GRANT SELECT ON `xcan_gm`.`app_open` TO `commonlink`@`%`;
GRANT SELECT ON `xcan_gm`.`c_i18n_messages` TO `commonlink`@`%`;
GRANT SELECT ON `xcan_gm`.`c_setting` TO `commonlink`@`%`;
GRANT SELECT ON `xcan_gm`.`c_setting_tenant` TO `commonlink`@`%`;
GRANT SELECT ON `xcan_gm`.`c_setting_tenant_quota` TO `commonlink`@`%`;
GRANT SELECT ON `xcan_gm`.`c_setting_user` TO `commonlink`@`%`;
FLUSH PRIVILEGES;
```

##### Create Federated Tables

###### 1. Create Federated Servers
```sql
-- GM database
CREATE
SERVER xcan_gm_link
FOREIGN DATA WRAPPER mysql
OPTIONS (USER 'commonlink', PASSWORD 'your_password', HOST 'your_mysql_host', PORT 3306, DATABASE 'xcan_gm');

-- STORE database
CREATE
SERVER xcan_store_link
FOREIGN DATA WRAPPER mysql
OPTIONS (USER 'commonlink', PASSWORD 'your_password', HOST 'your_mysql_host', PORT 3306, DATABASE 'xcan_store');
```

###### 2. Create Federated Tables
***Note: Federated tables are created under xcan_commonlink database.***

```sql
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
    `modified_by`   bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Last modifier',
    `modified_date` datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Last modification date',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                  `idx_name` (`name`) USING BTREE,
    KEY                  `idx_created_by` (`created_by`) USING BTREE,
    KEY                  `idx_modified_by` (`modified_by`) USING BTREE,
    KEY                  `idx_created_date` (`created_date`) USING BTREE,
    KEY                  `idx_modified_date` (`modified_date`) USING BTREE,
    KEY                  `idx_apply_cancel_date` (`apply_cancel_date`) USING BTREE,
    KEY                  `idx_real_name_status` (`real_name_status`) USING BTREE,
    KEY                  `idx_status` (`status`) USING BTREE,
    KEY                  `idx_source` (`source`) USING BTREE,
    KEY                  `idx_type` (`type`) USING BTREE,
    KEY                  `idx_locked` (`locked`) USING BTREE,
    KEY                  `idx_lock_start_date` (`lock_start_date`) USING BTREE,
    KEY                  `idx_lock_end_date` (`lock_end_date`) USING BTREE,
    KEY                  `idx_summary_group` (`source`,`type`,`status`,`real_name_status`,`locked`,`created_date`) USING BTREE
    -- ,FULLTEXT KEY `fx_name_no` (`name`,`no`) /*!50100 WITH PARSER `ngram` */ 
) ENGINE = FEDERATED COMMENT = 'Tenant'  CONNECTION = 'xcan_gm_link/tenant';

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
    `modified_by`            bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Last modifier',
    `modified_date`          datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Last modification date',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_username` (`username`) USING BTREE,
    KEY                           `idx_source` (`source`) USING BTREE,
    KEY                           `idx_gender` (`gender`) USING BTREE,
    KEY                           `idx_admin` (`sys_admin`) USING BTREE,
    KEY                           `idx_enabled` (`enabled`) USING BTREE,
    KEY                           `idx_locked` (`locked`) USING BTREE,
    KEY                           `idx_lock_start_date` (`lock_start_date`) USING BTREE,
    KEY                           `idx_lock_end_date` (`lock_end_date`) USING BTREE,
    KEY                           `idx_fullname` (`full_name`) USING BTREE,
    KEY                           `idx_directory_id` (`directory_id`),
    KEY                           `idx_deleted` (`deleted`) USING BTREE,
    KEY                           `uidx_tenantId_mobile` (`mobile`,`tenant_id`) USING BTREE,
    KEY                           `uidx_tenantId_email` (`email`,`tenant_id`) USING BTREE,
    KEY                           `idx_summary_group` (`source`,`sys_admin`,`enabled`,`locked`,`gender`,`created_date`) USING BTREE,
    KEY                           `idx_summary_tenant_group` (`tenant_id`,`source`,`sys_admin`,`enabled`,`locked`,`gender`,`created_date`) USING BTREE
    -- ,FULLTEXT KEY `fx_name_mobile_title_username` (`full_name`,`mobile`,`title`,`username`) /*!50100 WITH PARSER `ngram` */
) ENGINE=FEDERATED COMMENT='User' CONNECTION='xcan_gm_link/user0';

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
    `modified_by`   bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Last modifier',
    `modified_date` datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Last modification date',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                  `idx_pid` (`pid`) USING BTREE,
    KEY                  `idx_parent_like_id` (`parent_like_id`) USING BTREE,
    KEY                  `idx_name` (`name`) USING BTREE,
    KEY                  `idx_code` (`code`) USING BTREE,
    KEY                  `idx_tenant_id` (`tenant_id`) USING BTREE,
    KEY                  `idx_created_date` (`created_date`) USING BTREE,
    KEY                  `idx_level` (`level`) USING BTREE
    -- ,FULLTEXT KEY `fx_name_code` (`code`,`name`) /*!50100 WITH PARSER `ngram` */
) ENGINE=FEDERATED COMMENT='Department'  CONNECTION = 'xcan_gm_link/dept';

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
) ENGINE=FEDERATED COMMENT='Department-user relationship'  CONNECTION = 'xcan_gm_link/dept_user';

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
    `modified_by`     bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Last modifier',
    `modified_date`   datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Last modification date',
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
) ENGINE=FEDERATED COMMENT='User group' CONNECTION='xcan_gm_link/group0';

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
) ENGINE=FEDERATED COMMENT='User group-member association' CONNECTION='xcan_gm_link/group_user';

CREATE TABLE `org_tag`
(
    `id`                 bigint(20) NOT NULL COMMENT 'ID',
    `name`               varchar(100) NOT NULL COMMENT 'Tag name',
    `tenant_id`          bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Associated tenant ID',
    `created_by`         bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Creator',
    `created_date`       datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Creation date',
    `modified_by`   bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Last modifier',
    `modified_date` datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Last modification date',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `idx_tenant_id_name` (`name`,`tenant_id`) USING BTREE,
    KEY                  `idx_create_date` (`created_date`) USING BTREE,
    KEY                  `idx_tenant_id` (`tenant_id`) USING BTREE
    -- ,FULLTEXT KEY `fx_name` (`name`) /*!50100 WITH PARSER `ngram` */
) ENGINE=FEDERATED COMMENT='Tag' CONNECTION = 'xcan_gm_link/org_tag';

CREATE TABLE `org_tag_target`
(
    `id`           bigint(20) NOT NULL COMMENT 'ID',
    `tag_id`       bigint(20) NOT NULL COMMENT 'Tag ID',
    `target_type`  varchar(16) NOT NULL COMMENT 'Target type: department, group, user',
    `target_id`    bigint(20) NOT NULL COMMENT 'Target ID',
    `tenant_id`    bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Tenant ID',
    `created_by`   bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Created by',
    `created_date` datetime    NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Creation time',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_tag_target_id` (`tag_id`,`target_id`) USING BTREE,
    KEY            `idx_tag_id` (`tag_id`) USING BTREE,
    KEY            `idx_target_id` (`target_id`) USING BTREE,
    KEY            `idx_tenant_id` (`tenant_id`) USING BTREE,
    KEY            `idx_target_type` (`target_type`) USING BTREE,
    KEY            `idx_summary_group` (`created_date`,`target_type`) USING BTREE,
    KEY            `idx_summary_tenant_group` (`tenant_id`,`created_date`,`target_type`) USING BTREE
) ENGINE=FEDERATED COMMENT='Tag target' CONNECTION = 'xcan_gm_link/org_tag_target';

-- AAS
CREATE TABLE `service`
(
    `id`                 bigint(20) NOT NULL COMMENT 'Primary key ID',
    `code`               varchar(80)  NOT NULL COMMENT 'Code',
    `name`               varchar(100) NOT NULL DEFAULT '' COMMENT 'Name',
    `description`        varchar(200) NOT NULL DEFAULT '' COMMENT 'Description',
    `source`             varchar(16)  NOT NULL COMMENT 'Source: BACK_ADD, EUREKA, NOCAS, CONSUL',
    `enabled`            int(1) NOT NULL COMMENT 'Enabled status',
    `api_num`            int(11) NOT NULL DEFAULT '0' COMMENT 'Number of APIs',
    `route_path`         varchar(80)  NOT NULL DEFAULT '' COMMENT 'Gateway route path',
    `url`                varchar(400) NOT NULL DEFAULT '' COMMENT 'Service URL',
    `health_url`         varchar(400) NOT NULL COMMENT 'Health check URL',
    `api_doc_url`        varchar(400) NOT NULL COMMENT 'API documentation URL',
    `created_by`         bigint(21) NOT NULL COMMENT 'Created by',
    `created_date`       datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Creation time',
    `modified_by`   bigint(21) NOT NULL COMMENT 'Last modified by',
    `modified_date` datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Last modification time',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_code` (`code`) USING BTREE,
    KEY                  `idx_name` (`name`) USING BTREE,
    KEY                  `idx_summary_group` (`created_date`,`source`,`enabled`) USING BTREE
    -- ,FULLTEXT KEY `fx_name_code_description` (`name`,`code`,`description`) /*!50100 WITH PARSER `ngram` */
) ENGINE=FEDERATED COMMENT='Service table' CONNECTION='xcan_gm_link/service';

CREATE TABLE `api`
(
    `id`                   bigint(20) NOT NULL COMMENT 'Primary key ID',
    `name`                 varchar(600) NOT NULL DEFAULT '' COMMENT 'Name',
    `operation_id`         varchar(100) NOT NULL COMMENT 'Code, corresponds to OAS operationId',
    `scopes`               varchar(100)          DEFAULT NULL COMMENT 'Corresponds to OAS security requirements',
    `uri`                  varchar(800) NOT NULL DEFAULT '' COMMENT 'API endpoint',
    `method`               varchar(16)  NOT NULL COMMENT 'HTTP method',
    `type`                 varchar(16)  NOT NULL DEFAULT '' COMMENT 'API type (ApiType)',
    `description`          text COMMENT 'Description',
    `resource_name`        varchar(100) NOT NULL COMMENT 'Resource name',
    `resource_description` varchar(200) NOT NULL COMMENT 'Resource description',
    `enabled`              int(1) NOT NULL COMMENT 'Enabled status',
    `service_id`           bigint(20) NOT NULL COMMENT 'Service ID',
    `service_code`         varchar(80)  NOT NULL COMMENT 'Service code',
    `service_name`         varchar(200) NOT NULL DEFAULT '' COMMENT 'Service name',
    `service_enabled`      int(1) DEFAULT NULL COMMENT 'Service enabled status',
    `sync`                 int(1) NOT NULL COMMENT 'Is synchronous API',
    `swagger_deleted`      int(1) DEFAULT NULL COMMENT 'Deleted from Swagger',
    `created_by`           bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Created by',
    `created_date`         datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Creation time',
    `modified_by`     bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Last modified by',
    `modified_date`   datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Last modification time',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_service_code` (`operation_id`,`service_code`) USING BTREE,
    KEY                    `idx_service_name` (`service_name`) USING BTREE,
    KEY                    `uidx_name` (`name`) USING BTREE,
    KEY                    `idx_service_code` (`service_code`) USING BTREE,
    KEY                    `idx_summary_group` (`method`,`type`,`enabled`,`created_date`) USING BTREE
    -- ,FULLTEXT KEY `fx_name_code_service_name_description` (`name`,`operation_id`,`service_name`,`description`) /*!50100 WITH PARSER `ngram` */
) ENGINE=FEDERATED COMMENT='API table' CONNECTION='xcan_gm_link/api';

CREATE TABLE `app`
(
    `id`                 bigint(20) NOT NULL COMMENT 'Primary key ID',
    `code`               varchar(80)  NOT NULL DEFAULT '' COMMENT 'Code',
    `name`               varchar(100) NOT NULL DEFAULT '' COMMENT 'Name',
    `show_name`          varchar(40)  NOT NULL DEFAULT '' COMMENT 'Display name',
    `icon`               varchar(200) NOT NULL COMMENT 'Icon',
    `type`               varchar(10)  NOT NULL COMMENT 'App type: CLOUD_APP, BASE_APP, OP_APP',
    `edition_type`       varchar(20)           DEFAULT NULL COMMENT 'Edition type',
    `description`        varchar(200) NOT NULL DEFAULT '' COMMENT 'Description',
    `auth_ctrl`          int(1) DEFAULT NULL COMMENT 'Authorization control flag: 0-disabled, 1-enabled',
    `enabled`            int(1) NOT NULL COMMENT 'Enabled status: 0-disabled, 1-enabled',
    `url`                varchar(200) NOT NULL COMMENT 'URL',
    `sequence`           int(11) NOT NULL DEFAULT '1' COMMENT 'Order (lower value has higher priority)',
    `api_ids`            varchar(800)          DEFAULT NULL COMMENT 'Associated API IDs',
    `version`            varchar(20)  NOT NULL COMMENT 'Version: major, minor, patch',
    `open_stage`         varchar(16)  NOT NULL COMMENT 'Activation stage: SIGNUP, AUTH_PASSED, OPEN_SUCCESS',
    `client_id`          varchar(32)           DEFAULT NULL COMMENT 'Client ID',
    `tenant_id`          bigint(20) NOT NULL COMMENT 'Tenant ID',
    `created_by`         bigint(20) NOT NULL COMMENT 'Created by',
    `created_date`       datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Creation time',
    `modified_by`   bigint(20) NOT NULL COMMENT 'Last modified by',
    `modified_date` datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Last modification time',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_code_edition_version` (`code`,`edition_type`,`version`) USING BTREE,
    KEY                  `idx_tenant_id` (`tenant_id`) USING BTREE,
    KEY                  `idx_enabled_fl_id_sequence` (`enabled`,`id`,`sequence`) USING BTREE,
    KEY                  `uidx_show_name_name` (`show_name`,`code`) USING BTREE,
    KEY                  `idx_edition_type` (`edition_type`) USING BTREE,
    KEY                  `idx_version` (`version`) USING BTREE
    -- ,FULLTEXT KEY `fx_name_code_show_name_description` (`code`,`name`,`show_name`,`description`) /*!50100 WITH PARSER `ngram` */
) ENGINE=FEDERATED COMMENT='Application table' CONNECTION='xcan_gm_link/app';

CREATE TABLE `oauth2_user`
(
    `id`                          bigint(20) NOT NULL COMMENT 'Primary key ID',
    `username`                    varchar(100) NOT NULL DEFAULT '' COMMENT 'Username',
    `password`                    varchar(160) NOT NULL DEFAULT '' COMMENT 'Password',
    `enabled`                     int(1) NOT NULL DEFAULT '1' COMMENT 'Enabled status',
    `account_non_expired`         int(1) NOT NULL DEFAULT '1' COMMENT 'Account non-expired',
    `account_non_locked`          int(1) NOT NULL DEFAULT '1' COMMENT 'Account non-locked',
    `credentials_non_expired`     int(1) NOT NULL DEFAULT '1' COMMENT 'Credentials non-expired',
    `first_name`                  varchar(100) NOT NULL DEFAULT '' COMMENT 'First name',
    `last_name`                   varchar(100) NOT NULL DEFAULT '' COMMENT 'Last name',
    `full_name`                   varchar(100) NOT NULL DEFAULT '' COMMENT 'Full name',
    `mobile`                      varchar(16)  NOT NULL DEFAULT '' COMMENT 'Mobile number',
    `email`                       varchar(100) NOT NULL DEFAULT '' COMMENT 'Email',
    `sys_admin`                   int(1) NOT NULL DEFAULT '0' COMMENT 'Is system administrator',
    `to_user`                     int(1) NOT NULL DEFAULT '0' COMMENT 'Is operational user',
    `tenant_id`                   bigint(20) NOT NULL COMMENT 'Tenant ID',
    `tenant_name`                 varchar(100) NOT NULL DEFAULT '' COMMENT 'Tenant name',
    `tenant_real_name_status`     varchar(20)  NOT NULL COMMENT 'Real-name verification status (0-unverified, 1-verified)',
    `main_dept_id`                bigint(20) DEFAULT NULL COMMENT 'Primary department ID',
    `password_strength`           varchar(10)  NOT NULL DEFAULT '' COMMENT 'Password strength: WEAK, MEDIUM, STRONG',
    `password_expired_date`       datetime              DEFAULT NULL COMMENT 'Password expiration time',
    `last_modified_password_date` datetime              DEFAULT '2001-01-01 00:00:00' COMMENT 'Last password modification time',
    `expired_date`                datetime              DEFAULT NULL COMMENT 'Account expiration time',
    `deleted`                     int(1) NOT NULL DEFAULT '0' COMMENT 'Account deleted status',
    `directory_id`                bigint(20) DEFAULT NULL COMMENT 'LDAP directory ID',
    `default_language`            varchar(20)           DEFAULT NULL COMMENT 'Default language',
    `default_time_zone`           varchar(40)           DEFAULT NULL COMMENT 'Default timezone',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                           `idx_tenant_id` (`tenant_id`) USING BTREE,
    KEY                           `idx_tenant_real_name_status` (`tenant_real_name_status`) USING BTREE,
    KEY                           `idx_password_expired_date` (`password_expired_date`) USING BTREE
) ENGINE=FEDERATED COMMENT='User' CONNECTION='xcan_gm_link/oauth2_user';

CREATE TABLE `auth_policy`
(
    `id`                 bigint(20) NOT NULL COMMENT 'Primary key ID',
    `name`               varchar(32) NOT NULL DEFAULT '' COMMENT 'Name',
    `code`               varchar(80) NOT NULL COMMENT 'Code (snake_case format)',
    `enabled`       int(1) NOT NULL COMMENT 'Enabled status',
    `type`               varchar(20) NOT NULL DEFAULT '1' COMMENT 'Policy category',
    `default0`           int(1) NOT NULL COMMENT 'Default authorization policy',
    `grant_stage`        varchar(20) NOT NULL DEFAULT '-1' COMMENT 'Authorization stage',
    `description`        varchar(200)         DEFAULT '' COMMENT 'Description',
    `app_id`             bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Associated application ID',
    `client_id`          varchar(80) NOT NULL COMMENT 'Client ID',
    `tenant_id`          bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Tenant ID',
    `created_by`         bigint(20) NOT NULL COMMENT 'Created by',
    `created_date`       datetime    NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Creation time',
    `modified_by`   bigint(20) NOT NULL COMMENT 'Last modified by',
    `modified_date` datetime    NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Last modification time',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_name_app_id` (`name`,`tenant_id`,`app_id`) USING BTREE,
    UNIQUE KEY `uidx_code_app_id` (`code`,`tenant_id`,`app_id`) USING BTREE,
    KEY                  `idx_app_id` (`app_id`) USING BTREE,
    KEY                  `idx_tenant_id` (`tenant_id`) USING BTREE,
    KEY                  `idx_enabled` (`enabled`) USING BTREE,
    KEY                  `idx_type` (`type`) USING BTREE,
    KEY                  `idx_client_id` (`client_id`) USING BTREE
) ENGINE=FEDERATED COMMENT='Authorization policy' CONNECTION='xcan_gm_link/auth_policy';

CREATE TABLE `to_role`
(
    `id`                 bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary key ID',
    `name`               varchar(100) NOT NULL DEFAULT '' COMMENT 'Name',
    `code`               varchar(80)  NOT NULL COMMENT 'Code (snake_case format)',
    `enabled`            int(1) NOT NULL COMMENT 'Enabled status: 0-disabled, 1-enabled',
    `description`        varchar(200)          DEFAULT '' COMMENT 'Description',
    `app_id`             bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Associated application ID',
    `created_by`         bigint(20) NOT NULL COMMENT 'Created by',
    `created_date`       datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Creation time',
    `modified_by`   bigint(20) NOT NULL COMMENT 'Last modified by',
    `modified_date` datetime     NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Last modification time',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_code` (`code`) USING BTREE,
    UNIQUE KEY `uidx_name` (`name`) USING BTREE,
    KEY                  `idx_app_id` (`app_id`) USING BTREE
) ENGINE=FEDERATED COMMENT='Operation policy (Tenant operation role)' CONNECTION='xcan_gm_link/to_role';

CREATE TABLE `to_user`
(
    `id`           bigint(20) NOT NULL COMMENT 'Primary key ID',
    `user_id`      bigint(20) NOT NULL COMMENT 'User ID',
    `created_by`   bigint(20) NOT NULL COMMENT 'Created by',
    `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Creation time',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uid_user_id` (`user_id`) USING BTREE,
    KEY            `idx_created_date` (`created_date`) USING BTREE
) ENGINE=FEDERATED COMMENT='Policy-user association table' CONNECTION='xcan_gm_link/to_user';

CREATE TABLE `to_role_user`
(
    `id`           bigint(20) NOT NULL COMMENT 'Primary key ID',
    `to_role_id`   bigint(20) NOT NULL COMMENT 'Tenant operation role ID',
    `user_id`      bigint(20) NOT NULL COMMENT 'User ID',
    `created_by`   bigint(20) NOT NULL COMMENT 'Created by',
    `created_date` datetime NOT NULL DEFAULT '2001-01-01 00:00:00' COMMENT 'Creation time',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_policy_user` (`to_role_id`,`user_id`) USING BTREE,
    KEY            `idx_policy_id` (`to_role_id`) USING BTREE,
    KEY            `idx_user_id` (`user_id`) USING BTREE,
    KEY            `idx_created_date` (`created_date`) USING BTREE
) ENGINE=FEDERATED COMMENT='Policy-user association table' CONNECTION='xcan_gm_link/to_role_user';

CREATE TABLE `app_open`
(
    `id`                 bigint(20) NOT NULL COMMENT 'Primary key ID',
    `app_id`             bigint(20) NOT NULL COMMENT 'Activated application ID',
    `app_code`           varchar(80) NOT NULL COMMENT 'Activated application code',
    `app_type`           varchar(20) NOT NULL COMMENT 'Application type',
    `edition_type`       varchar(20)          DEFAULT NULL COMMENT 'Edition type',
    `version`            varchar(20) NOT NULL COMMENT 'Activated version',
    `client_id`          varchar(80) NOT NULL COMMENT 'Client ID',
    `tenant_id`          bigint(20) NOT NULL COMMENT 'Tenant ID',
    `user_id`            bigint(20) NOT NULL DEFAULT '-1' COMMENT 'User ID',
    `open_date`          datetime    NOT NULL COMMENT 'Activation time',
    `expiration_date`    datetime    NOT NULL COMMENT 'Expiration time',
    `expiration_deleted` int(1) NOT NULL COMMENT 'Expiration deletion flag',
    `op_client_open`     int(1) NOT NULL COMMENT 'Operation client activation flag',
    `created_date`       datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                  `idx_created_date` (`created_date`) USING BTREE,
    KEY                  `idx_expiration_date` (`expiration_date`) USING BTREE,
    KEY                  `idx_expiration_deleted` (`expiration_deleted`) USING BTREE,
    KEY                  `idx_op_client_open` (`op_client_open`) USING BTREE,
    KEY                  `idx_app_id` (`app_id`) USING BTREE,
    KEY                  `idx_client_id` (`client_id`) USING BTREE,
    KEY                  `idx_app_code` (`app_code`) USING BTREE,
    KEY                  `idx_version` (`version`),
    KEY                  `idx_edition_type` (`edition_type`),
    KEY                  `idx_tenant_id_app` (`tenant_id`,`edition_type`,`app_code`,`version`) USING BTREE
) ENGINE=FEDERATED COMMENT='Application activation table' CONNECTION='xcan_gm_link/app_open';

-- COMMON
CREATE TABLE `c_i18n_messages`
(
    `id`              bigint(20) NOT NULL COMMENT 'Primary key ID',
    `type`            varchar(50)  NOT NULL COMMENT 'Category',
    `language`        varchar(20)  NOT NULL COMMENT 'Language',
    `default_message` varchar(100) NOT NULL COMMENT 'Default message',
    `i18n_message`    varchar(100) NOT NULL COMMENT 'Internationalized message',
    `private0`        int(1) NOT NULL COMMENT 'Private flag',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_key_language_default_message` (`type`,`language`,`default_message`) USING BTREE,
    KEY               `idx_type` (`type`) USING BTREE,
    KEY               `idx_language` (`language`) USING BTREE
) ENGINE=FEDERATED COMMENT='Internationalization resource table' CONNECTION='xcan_gm_link/c_i18n_messages';

CREATE TABLE `c_setting`
(
    `id`             bigint(20) NOT NULL COMMENT 'Primary key ID',
    `key`            varchar(32)    NOT NULL DEFAULT '' COMMENT 'Configuration key',
    `value`          varchar(16000) NOT NULL COMMENT 'Configuration value',
    `global_default` int(1) NOT NULL COMMENT 'Global default flag',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_key` (`key`) USING BTREE
) ENGINE=FEDERATED COMMENT='Configuration table' CONNECTION='xcan_gm_link/c_setting';

CREATE TABLE `c_setting_tenant`
(
    `id`                    bigint(20) NOT NULL COMMENT 'Primary key ID',
    `invitation_code`       varchar(80)   DEFAULT NULL COMMENT 'Invitation code',
    `locale_data`           varchar(200)  DEFAULT NULL COMMENT 'Tenant localization settings',
    `func_data`             varchar(320)  DEFAULT NULL COMMENT 'Tenant default functional metrics',
    `perf_data`             varchar(320)  DEFAULT NULL COMMENT 'Tenant default performance metrics',
    `stability_data`        varchar(320)  DEFAULT NULL COMMENT 'Tenant default stability metrics',
    `security_data`         varchar(1200) DEFAULT NULL COMMENT 'Tenant security settings',
    `server_api_proxy_data` varchar(500)  DEFAULT NULL COMMENT 'Server API proxy configuration',
    `tester_event_data`     varchar(2000) DEFAULT NULL COMMENT 'AngusTester event notification configuration',
    `tenant_id`             bigint(20) NOT NULL COMMENT 'Tenant ID',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_tenant_id` (`tenant_id`) USING BTREE,
    UNIQUE KEY `uidx_invitation_code` (`invitation_code`) USING BTREE
) ENGINE=FEDERATED COMMENT='Tenant settings' CONNECTION='xcan_gm_link/c_setting_tenant';

CREATE TABLE `c_setting_tenant_quota`
(
    `id`             bigint(20) NOT NULL COMMENT 'Primary key ID',
    `app_code`       varchar(80)  NOT NULL COMMENT 'Application code',
    `service_code`   varchar(80)  NOT NULL COMMENT 'Service code',
    `name`           varchar(100) NOT NULL COMMENT 'Resource name',
    `allow_change`   int(1) NOT NULL COMMENT 'Allow quota modification',
    `license_ctrl`       int(1) NOT NULL COMMENT 'License control',
    `calc_remaining` int(1) NOT NULL COMMENT 'Calculate remaining quota',
    `quota`          bigint(20) NOT NULL COMMENT 'Current effective quota',
    `min`            bigint(20) NOT NULL DEFAULT '0' COMMENT 'Minimum allowed value',
    `max`            bigint(20) NOT NULL DEFAULT '0' COMMENT 'Maximum allowed value',
    `capacity`       bigint(20) NOT NULL COMMENT 'Total capacity',
    `tenant_id`      bigint(20) NOT NULL COMMENT 'Tenant ID',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uidx_tenant_id_name` (`tenant_id`,`name`) USING BTREE,
    KEY              `idx_app_code` (`app_code`) USING BTREE,
    KEY              `idx_service_code` (`service_code`) USING BTREE,
    KEY              `idx_resource` (`name`) USING BTREE,
    KEY              `idx_quota` (`quota`) USING BTREE,
    KEY              `idx_allow_change` (`allow_change`) USING BTREE,
    KEY              `idx_tenant_id` (`tenant_id`) USING BTREE
) ENGINE=FEDERATED COMMENT='Tenant quota settings' CONNECTION='xcan_gm_link/c_setting_tenant_quota';

CREATE TABLE `c_setting_user`
(
    `id`          bigint(20) unsigned NOT NULL COMMENT 'Primary key ID',
    `preference`  varchar(800)  DEFAULT NULL COMMENT 'User preferences',
    `api_proxy`   varchar(500)  DEFAULT NULL COMMENT 'API proxy configuration',
    `social_bind` varchar(1200) DEFAULT NULL COMMENT 'Third-party login bindings',
    `tenant_id`   bigint(20) NOT NULL COMMENT 'Tenant ID',
    PRIMARY KEY (`id`) USING BTREE,
    KEY           `idx_tenant_id` (`tenant_id`) USING BTREE
) ENGINE=FEDERATED COMMENT='User settings' CONNECTION='xcan_gm_link/c_setting_user';
```

#### Creating Public Tables - PostgreSQL

TODO

#### Publishing Public Tables

###### Exposing Table Access APIs
Define corresponding domain objects and repositories in the `cloud.xcan.angus.api.commonlink` package of the API module. Move existing code to the commonlink package as needed.

###### Publishing API Package
Deploy the API package to private repository using `mvn deploy`.

## Integrating Public Tables

#### 1. Add Dependencies

Add auto-configuration dependency in Boot module:
```xml
<dependency>
  <groupId>cloud.xcan.angus</groupId>
  <artifactId>xcan-infra.web-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```

Add public table dependency in Core module (example for GM service):
```xml
<dependency>
  <groupId>cloud.xcan.angus</groupId>
  <artifactId>xcan-angusgm.api</artifactId>
  <version>1.0.0</version>
</dependency>
```

> All public table domains and repositories are under `cloud.xcan.angus.api.commonlink` package.

#### 2. Enable Auto-configuration

Configure application.yml:
```yaml
xcan:
  datasource:
    enabled: true
    commonlink:
      enabled: true
```

Configure environment-specific settings:
```yaml
## Local environment example
xcan:
  datasource:
    commonlink:
      mysql:
        url: jdbc:mysql://${COMMON_MYSQL_HOST:your_host}:${COMMON_MYSQL_PORT:3306}/${COMMON_MYSQL_DB:xcan_common}?useUnicode=true&characterEncoding=UTF-8
        username: ${COMMON_MYSQL_USER:commonlink}
        password: ${COMMON_MYSQL_PASSWORD:your_password}
```

#### 3. Usage in SpringBoot Applications
```java
@Resource
private UserRepo userRepo;
```
