-- @formatter:off

-- ----------------------------
-- Table data for id_config
-- ----------------------------
INSERT INTO `id_config` (`pk`, `biz_key`, `format`, `prefix`, `date_format`, `seq_length`, `mode`, `scope`, `tenant_id`, `max_id`, `step`, `create_date`, `last_modified_date`)
                VALUES ('180601', 'tenant', 'PREFIX_DATE_SEQ', 'T', 'YYYYMM', 6, 'REDIS', 'PLATFORM', -1, 0, 100, '2024-01-01 00:00:00', '2024-01-01 00:00:00');
INSERT INTO `id_config` (`pk`, `biz_key`, `format`, `prefix`, `date_format`, `seq_length`, `mode`, `scope`, `tenant_id`, `max_id`, `step`, `create_date`, `last_modified_date`)
                VALUES ('180602', 'user', 'PREFIX_DATE_SEQ', 'U', 'YYYYMM', 5, 'REDIS', 'PLATFORM', -1, 0, 500, '2024-01-01 00:00:00', '2024-01-01 00:00:00');
INSERT INTO `id_config` (`pk`, `biz_key`, `format`, `prefix`, `date_format`, `seq_length`, `mode`, `scope`, `tenant_id`, `max_id`, `step`, `create_date`, `last_modified_date`)
                VALUES ('181101', 'invitationCode', 'PREFIX_DATE_SEQ', 'IC', 'YYYYMM', 5, 'REDIS', 'PLATFORM', -1, 0, 100, '2024-01-01 00:00:00', '2024-01-01 00:00:00');

-- ----------------------------
-- Table data for tenant
-- ----------------------------
INSERT INTO `tenant` (`id`, `no`, `name`, `type`, `source`, `real_name_status`, `status`, `apply_cancel_date`, `address`, `locked`, `last_lock_date`, `lock_start_date`,
                      `lock_end_date`, `remark`, `created_by`, `created_date`, `last_modified_by`, `last_modified_date`)
            VALUES (:TENANT_ID, 'T202401010000', ':TENANT_NAME', 'UNKNOWN', 'PLATFORM_SIGNUP', 'NOT_SUBMITTED', 'ENABLED', NULL, '', 0, NULL, NULL,
                        NULL, '', -1, '2024-01-01 00:00:00', -1, '2024-01-01 00:00:00');

-- ----------------------------
-- Table data for user0
-- ----------------------------
INSERT INTO `user0` (`id`, `username`, `first_name`, `last_name`, `full_name`, `itc`, `country`, `email`, `mobile`, `signup_account_type`, `signup_account`, `signup_device_id`,
                     `landline`, `tenant_id`, `tenant_name`, `avatar`, `title`, `gender`, `address`, `source`, `directory_id`, `main_dept_id`, `online`, `online_date`,
                     `offline_date`, `dept_head`, `sys_admin`, `expired`, `expired_date`, `enabled`, `disable_reason`, `deleted`, `locked`, `last_lock_date`, `lock_start_date`,
                     `lock_end_date`, `last_modified_password_date`, `created_by`, `created_date`, `last_modified_by`, `last_modified_date`)
            VALUES (:GM_ADMIN_USER_ID, ':GM_ADMIN_USERNAME', '', '', ':GM_ADMIN_FULL_NAME', '86', 'CN', ':GM_ADMIN_EMAIL', '', 'EMAIL', ':GM_ADMIN_EMAIL', NULL,
                    '', :TENANT_ID, ':TENANT_NAME', NULL, NULL, 'UNKNOWN', NULL, 'BACKGROUND_ADDED', NULL, NULL, 0, NULL,
                    NULL, 0, 1, 0, NULL, 1, '', 0, 0, NULL, NULL,
                    NULL, NULL, -1, '2024-01-01 00:00:00', -1, '2024-01-01 00:00:00');

-- ----------------------------
-- Table data for oauth2_user
-- ----------------------------
INSERT INTO `oauth2_user` (`id`, `username`, `password`, `enabled`, `account_non_expired`, `account_non_locked`, `credentials_non_expired`, `first_name`, `last_name`, `full_name`,
                           `mobile`, `email`, `sys_admin`, `to_user`, `tenant_id`, `tenant_name`, `tenant_real_name_status`, `main_dept_id`, `password_strength`, `password_expired_date`,
                           `last_modified_password_date`, `expired_date`, `deleted`, `directory_id`, `default_language`, `default_time_zone`)
                VALUES (:GM_ADMIN_USER_ID, ':GM_ADMIN_USERNAME', ':GM_ADMIN_ENCRYPTED_PASSWORD', 1, 1, 1, 1, '', '', ':GM_ADMIN_FULL_NAME',
                            '', ':GM_ADMIN_EMAIL', 1, 0, :TENANT_ID, ':TENANT_NAME', 'AUDITED', NULL, 'MEDIUM', NULL,
                            '2024-01-01 00:00:00', NULL, 0, NULL, NULL, NULL);

-- ----------------------------
-- Table data for client
-- ----------------------------
INSERT INTO `oauth2_registered_client` (`id`, `client_id`, `client_name`, `client_authentication_methods`, `authorization_grant_types`, `redirect_uris`, `post_logout_redirect_uris`,
                                        `scopes`, `platform`, `source`, `client_id_issued_at`, `client_secret`, `client_secret_expires_at`, `biz_tag`, `client_settings`, `token_settings`,
                                        `description`, `enabled`, `tenant_id`, `tenant_name`, `created_by`, `created_date`, `last_modified_by`, `last_modified_date`)
                                VALUES ('1', 'xcan_tp', 'Tenant Client', 'client_secret_post,client_secret_basic', 'refresh_token,password', '', '',
                                        'user_trust', 'XCAN_TP', 'XCAN_TP_SIGNIN', '2025-04-07 11:44:52', '{noop}6917ae827c964acc8dd7638fe0581b67', NULL, '', '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-authorization-consent\":false}', '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.token.reuse-refresh-tokens\":false,\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",36000.000000000],\"settings.token.access-token-format\":{\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\",\"value\":\"reference\"},\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",72000.000000000],\"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",300.000000000],\"settings.token.device-code-time-to-live\":[\"java.time.Duration\",300.000000000]}',
                                        'Tenant-side access client', 1, :TENANT_ID, ':TENANT_NAME', -1, '2024-01-01 00:00:00', -1, '2024-01-01 00:00:00');
INSERT INTO `oauth2_registered_client` (`id`, `client_id`, `client_name`, `client_authentication_methods`, `authorization_grant_types`, `redirect_uris`, `post_logout_redirect_uris`,
                                        `scopes`, `platform`, `source`, `client_id_issued_at`, `client_secret`, `client_secret_expires_at`, `biz_tag`, `client_settings`, `token_settings`,
                                        `description`, `enabled`, `tenant_id`, `tenant_name`, `created_by`, `created_date`, `last_modified_by`, `last_modified_date`)
                                VALUES ('6', ':OAUTH2_INTROSPECT_CLIENT_ID', 'Client Credentials Introspect Client', 'client_secret_post,client_secret_basic', 'client_credentials', '', '',
                                        'inner_api_trust,opaque_token_introspect_trust', 'XCAN_INNER', 'XCAN_SYS_INTROSPECT', '2025-04-07 13:03:17', '{noop}:OAUTH2_INTROSPECT_CLIENT_SECRET', NULL, '', '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-authorization-consent\":false}', '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.token.access-token-format\":{\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\",\"value\":\"reference\"},\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",36000000000.000000000]}',
                                        'Internal resource service authentication token client (for /innerapi)', 1, :TENANT_ID, ':TENANT_NAME', -1, '2024-01-01 00:00:00', -1, '2024-01-01 00:00:00');

-- ----------------------------
-- Table data for event_template
-- ----------------------------
INSERT INTO `event_template` (`id`, `event_code`, `event_name`, `event_type`, `e_key`, `target_type`, `app_code`, `private0`, `allowed_channel_type_data`,
                              `created_by`, `created_date`, `last_modified_by`, `last_modified_date`)
                    VALUES (1, 'ProtocolError', '协议错误', 'PROTOCOL', NULL, NULL, 'AngusGM', 1, NULL, -1, '2024-01-01 00:00:00', -1, '2024-01-01 00:00:00');
INSERT INTO `event_template` (`id`, `event_code`, `event_name`, `event_type`, `e_key`, `target_type`, `app_code`, `private0`, `allowed_channel_type_data`,
                              `created_by`, `created_date`, `last_modified_by`, `last_modified_date`)
                    VALUES (2, 'UnauthorizedError', '未经授权', 'SECURITY', NULL, NULL, 'AngusGM', 1, NULL, -1, '2024-01-01 00:00:00', -1, '2024-01-01 00:00:00');
INSERT INTO `event_template` (`id`, `event_code`, `event_name`, `event_type`, `e_key`, `target_type`, `app_code`, `private0`, `allowed_channel_type_data`,
                              `created_by`, `created_date`, `last_modified_by`, `last_modified_date`)
                    VALUES (3, 'ForbiddenError', '禁止访问', 'SECURITY', NULL, NULL, 'AngusGM', 1, NULL, -1, '2024-01-01 00:00:00', -1, '2024-01-01 00:00:00');
INSERT INTO `event_template` (`id`, `event_code`, `event_name`, `event_type`, `e_key`, `target_type`, `app_code`, `private0`, `allowed_channel_type_data`,
                              `created_by`, `created_date`, `last_modified_by`, `last_modified_date`)
                    VALUES (4, 'BusinessError', '业务错误', 'BUSINESS', NULL, NULL, 'AngusGM', 1, NULL, -1, '2024-01-01 00:00:00', -1, '2024-01-01 00:00:00');
INSERT INTO `event_template` (`id`, `event_code`, `event_name`, `event_type`, `e_key`, `target_type`, `app_code`, `private0`, `allowed_channel_type_data`,
                              `created_by`, `created_date`, `last_modified_by`, `last_modified_date`)
                    VALUES (5, 'SystemError', '系统错误', 'SYSTEM', NULL, NULL, 'AngusGM', 1, NULL, -1, '2024-01-01 00:00:00', -1, '2024-01-01 00:00:00');
INSERT INTO `event_template` (`id`, `event_code`, `event_name`, `event_type`, `e_key`, `target_type`, `app_code`, `private0`, `allowed_channel_type_data`,
                              `created_by`, `created_date`, `last_modified_by`, `last_modified_date`)
                    VALUES (6, 'QuotaError', '配额错误', 'QUOTA', NULL, NULL, 'AngusGM', 1, NULL, -1, '2024-01-01 00:00:00', -1, '2024-01-01 00:00:00');

-- ----------------------------
-- Table data for c_setting
-- ----------------------------
INSERT INTO `c_setting` (`id`, `key`, `value`, `global_default`) VALUES (1, 'LOCALE', '{\"defaultLanguage\":\"zh_CN\"}', 1);
INSERT INTO `c_setting` (`id`, `key`, `value`, `global_default`) VALUES (3, 'SECURITY', '{\"signinLimit\":{\"enabled\":true,\"signoutPeriodInMinutes\":30,\"passwordErrorIntervalInMinutes\":30,\"lockedPasswordErrorNum\":6,\"lockedDurationInMinutes\":120},\"signupAllow\":{\"enabled\":false},\"passwordPolicy\":{\"minLength\":10},\"alarm\":{\"enabled\":false}}', 1);
INSERT INTO `c_setting` (`id`, `key`, `value`, `global_default`) VALUES (4, 'QUOTA', '[{\"name\":\"User\",\"appCode\":\"AngusGM\",\"serviceCode\":\"XCAN-UC.BOOT\",\"allowChange\":false,\"licenseCtrl\":true,\"calcRemaining\":true,\"quota\":10,\"min\":10,\"max\":1000000,\"capacity\":-1},{\"name\":\"UserDept\",\"appCode\":\"AngusGM\",\"serviceCode\":\"XCAN-UC.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":true,\"quota\":5,\"min\":1,\"max\":2000,\"capacity\":-1},{\"name\":\"UserGroup\",\"appCode\":\"AngusGM\",\"serviceCode\":\"XCAN-UC.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":true,\"quota\":200,\"min\":1,\"max\":2000,\"capacity\":-1},{\"name\":\"Dept\",\"appCode\":\"AngusGM\",\"serviceCode\":\"XCAN-UC.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":true,\"quota\":200,\"min\":1,\"max\":20000,\"capacity\":-1},{\"name\":\"DeptLevel\",\"appCode\":\"AngusGM\",\"serviceCode\":\"XCAN-UC.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":true,\"quota\":5,\"min\":1,\"max\":10,\"capacity\":-1},{\"name\":\"DeptUser\",\"appCode\":\"AngusGM\",\"serviceCode\":\"XCAN-UC.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":true,\"quota\":200,\"min\":1,\"max\":2000,\"capacity\":-1},{\"name\":\"Group\",\"appCode\":\"AngusGM\",\"serviceCode\":\"XCAN-UC.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":true,\"quota\":200,\"min\":1,\"max\":20000,\"capacity\":-1},{\"name\":\"GroupUser\",\"appCode\":\"AngusGM\",\"serviceCode\":\"XCAN-UC.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":true,\"quota\":200,\"min\":1,\"max\":2000,\"capacity\":-1},{\"name\":\"OrgTag\",\"appCode\":\"AngusGM\",\"serviceCode\":\"XCAN-UC.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":true,\"quota\":200,\"min\":1,\"max\":20000,\"capacity\":-1},{\"name\":\"OrgTargetTag\",\"appCode\":\"AngusGM\",\"serviceCode\":\"XCAN-UC.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":false,\"quota\":10,\"min\":1,\"max\":2000,\"capacity\":-1},{\"name\":\"PolicyCustom\",\"appCode\":\"AngusGM\",\"serviceCode\":\"XCAN-AAS.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":true,\"quota\":200,\"min\":1,\"max\":20000,\"capacity\":-1},{\"name\":\"SystemToken\",\"appCode\":\"AngusGM\",\"serviceCode\":\"XCAN-AAS.BOOT\",\"allowChange\":false,\"licenseCtrl\":true,\"calcRemaining\":true,\"quota\":10,\"min\":1,\"max\":2000,\"capacity\":-1},{\"name\":\"UserToken\",\"appCode\":\"AngusGM\",\"serviceCode\":\"XCAN-AAS.BOOT\",\"allowChange\":false,\"licenseCtrl\":true,\"calcRemaining\":true,\"quota\":3,\"min\":1,\"max\":2000,\"capacity\":-1},{\"name\":\"FileStore\",\"appCode\":\"AngusGM\",\"serviceCode\":\"XCAN-STORAGE.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":true,\"quota\":2147483648,\"min\":2147483648,\"max\":2199023255552,\"capacity\":-1},{\"name\":\"DataSpace\",\"appCode\":\"AngusGM\",\"serviceCode\":\"XCAN-STORAGE.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":true,\"quota\":2000,\"min\":1,\"max\":100000,\"capacity\":-1},{\"name\":\"AngusTesterProject\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":true,\"quota\":2000,\"min\":1,\"max\":1000000,\"capacity\":-1},{\"name\":\"AngusTesterServices\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":true,\"quota\":2000,\"min\":1,\"max\":1000000,\"capacity\":-1},{\"name\":\"AngusTesterApis\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":true,\"quota\":20000,\"min\":1,\"max\":1000000,\"capacity\":-1},{\"name\":\"AngusTesterServicesApis\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":false,\"quota\":1000,\"min\":1,\"max\":2000,\"capacity\":-1},{\"name\":\"AngusTesterVariable\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":true,\"quota\":20000,\"min\":1,\"max\":1000000,\"capacity\":-1},{\"name\":\"AngusTesterTargetVariable\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":false,\"quota\":200,\"min\":1,\"max\":2000,\"capacity\":-1},{\"name\":\"AngusTesterDataset\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":true,\"quota\":20000,\"min\":1,\"max\":1000000,\"capacity\":-1},{\"name\":\"AngusTesterTargetDataset\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":false,\"quota\":200,\"min\":1,\"max\":2000,\"capacity\":-1},{\"name\":\"AngusTesterScenario\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":true,\"quota\":20000,\"min\":1,\"max\":1000000,\"capacity\":-1},{\"name\":\"AngusTesterScenarioApis\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":false,\"quota\":100,\"min\":1,\"max\":2000,\"capacity\":-1},{\"name\":\"AngusTesterMockService\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":true,\"quota\":100,\"min\":1,\"max\":1000000,\"capacity\":-1},{\"name\":\"AngusTesterMockServiceApis\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":false,\"quota\":1000,\"min\":1,\"max\":2000,\"capacity\":-1},{\"name\":\"AngusTesterMockApisResponse\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":false,\"quota\":50,\"min\":1,\"max\":2000,\"capacity\":-1},{\"name\":\"AngusTesterMockIterations\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":false,\"quota\":5000000,\"min\":1,\"max\":5000000000,\"capacity\":-1},{\"name\":\"AngusTesterMockDatasource\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":true,\"quota\":200,\"min\":1,\"max\":1000000,\"capacity\":-1},{\"name\":\"AngusTesterNode\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":false,\"licenseCtrl\":true,\"calcRemaining\":true,\"quota\":0,\"min\":0,\"max\":1000,\"capacity\":-1},{\"name\":\"AngusTesterConcurrency\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":false,\"licenseCtrl\":true,\"calcRemaining\":true,\"quota\":0,\"min\":0,\"max\":2000000,\"capacity\":-1},{\"name\":\"AngusTesterConcurrentTask\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":false,\"licenseCtrl\":true,\"calcRemaining\":true,\"quota\":0,\"min\":0,\"max\":1000,\"capacity\":-1},{\"name\":\"AngusTesterScript\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":false,\"licenseCtrl\":true,\"calcRemaining\":true,\"quota\":100000,\"min\":1,\"max\":10000000,\"capacity\":-1},{\"name\":\"AngusTesterExecution\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":false,\"licenseCtrl\":true,\"calcRemaining\":true,\"quota\":100000,\"min\":1,\"max\":10000000,\"capacity\":-1},{\"name\":\"AngusTesterExecutionDebug\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":false,\"licenseCtrl\":true,\"calcRemaining\":true,\"quota\":100000,\"min\":1,\"max\":10000000,\"capacity\":-1},{\"name\":\"AngusTesterReport\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":false,\"licenseCtrl\":true,\"calcRemaining\":true,\"quota\":100000,\"min\":1,\"max\":10000000,\"capacity\":-1},{\"name\":\"AngusTesterSprint\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":false,\"licenseCtrl\":true,\"calcRemaining\":true,\"quota\":2000,\"min\":1,\"max\":1000000,\"capacity\":-1},{\"name\":\"AngusTesterTask\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":false,\"licenseCtrl\":true,\"calcRemaining\":true,\"quota\":200000,\"min\":1,\"max\":50000000,\"capacity\":-1},{\"name\":\"AngusTesterSprintTask\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":false,\"licenseCtrl\":true,\"calcRemaining\":true,\"quota\":5000,\"min\":1,\"max\":50000,\"capacity\":-1},{\"name\":\"AngusTesterFuncPlan\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":false,\"quota\":500,\"min\":1,\"max\":1000000,\"capacity\":-1},{\"name\":\"AngusTesterFuncCase\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":false,\"quota\":50000,\"min\":1,\"max\":5000000,\"capacity\":-1},{\"name\":\"AngusTesterFuncPlanCase\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":false,\"quota\":5000,\"min\":1,\"max\":50000,\"capacity\":-1},{\"name\":\"AngusTesterTag\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":false,\"quota\":5000,\"min\":1,\"max\":1000000,\"capacity\":-1},{\"name\":\"AngusTesterModule\",\"appCode\":\"AngusTester\",\"serviceCode\":\"XCAN-ANGUSTESTER.BOOT\",\"allowChange\":true,\"licenseCtrl\":false,\"calcRemaining\":false,\"quota\":5000,\"min\":1,\"max\":1000000,\"capacity\":-1}]', 1);
INSERT INTO `c_setting` (`id`, `key`, `value`, `global_default`) VALUES (5, 'SOCIAL', '{}', 0);
INSERT INTO `c_setting` (`id`, `key`, `value`, `global_default`) VALUES (6, 'PREF_INDICATOR', '{\"threads\":5000,\"rampUpThreads\":100,\"rampUpInterval\":\"1min\",\"duration\":\"50min\",\"art\":\"500\",\"percentile\":{\"value\":\"P90\",\"message\":\"P90\"},\"tps\":500,\"errorRate\":0.01}', 1);
INSERT INTO `c_setting` (`id`, `key`, `value`, `global_default`) VALUES (7, 'STABILITY_INDICATOR', '{\"threads\":200,\"duration\":\"30min\",\"tps\":500,\"art\":\"500\",\"percentile\":{\"value\":\"P90\",\"message\":\"P90\"},\"errorRate\":0.01,\"cpu\":75.0,\"memory\":75.0,\"disk\":75.0,\"network\":75.0}', 1);
INSERT INTO `c_setting` (`id`, `key`, `value`, `global_default`) VALUES (8, 'HEALTH_CHECK', '{\"enabled\":true,\"alarmWay\":[{\"value\":\"SMS\",\"message\":\"短信\"},{\"value\":\"EMAIL\",\"message\":\"电子邮件\"}],\"receiveUser\":[{\"id\":\"125581233815027713\",\"fullname\":\"卡卡西\"},{\"id\":\"125503728982687744\",\"fullname\":\"SSSQQQ\"},{\"id\":\"125377229310918665\",\"fullname\":\"东方奇\"}],\"healthCheckDate\":null}', 0);
INSERT INTO `c_setting` (`id`, `key`, `value`, `global_default`) VALUES (9, 'OPERATION_LOG_CONFIG', '{\"enabled\":true,\"clearBeforeDay\":30}', 0);
INSERT INTO `c_setting` (`id`, `key`, `value`, `global_default`) VALUES (10, 'API_LOG_CONFIG', '{}', 0);
INSERT INTO `c_setting` (`id`, `key`, `value`, `global_default`) VALUES (11, 'SYSTEM_LOG_CONFIG', '{\"clearWay\":\"CLEAR_BEFORE_DAY\",\"clearBeforeDay\":15,\"compressionBeforeDay\":15,\"compressionMovePath\":\"/data/backup/logs\",\"diskUsageExceedsRate\":0.8,\"diskUsageExceedsAndClearBeforeDay\":15}', 0);
-- Note: In a private environment, change the default value from 200 to 500.
INSERT INTO `c_setting` (`id`, `key`, `value`, `global_default`) VALUES (12, 'MAX_RESOURCE_ACTIVITIES', '500', 1);
-- Note: In a private environment, change the default value from 7 to 90 (-1 means no cleanup).
INSERT INTO `c_setting` (`id`, `key`, `value`, `global_default`) VALUES (13, 'MAX_METRICS_DAYS', '90', 1);
INSERT INTO `c_setting` (`id`, `key`, `value`, `global_default`) VALUES (14, 'FUNC_INDICATOR', '{\"smoke\":true,\"smokeCheckSetting\":{\"value\":\"API_AVAILABLE\",\"message\":\"xcm.enum.SmokeCheckSetting.API_AVAILABLE\"},\"security\":true,\"securityCheckSetting\":{\"value\":\"NOT_SECURITY_CODE\",\"message\":\"xcm.enum.SecurityCheckSetting.NOT_SECURITY_CODE\"}}', 1);
INSERT INTO `c_setting` (`id`, `key`, `value`, `global_default`) VALUES (15, 'TESTER_EVENT', '[{\"eventCode\":\"TaskWillOverdue\",\"noticeTypes\":[\"IN_SITE\",\"EMAIL\"]},{\"eventCode\":\"TaskOverdue\",\"noticeTypes\":[\"IN_SITE\",\"EMAIL\"]},{\"eventCode\":\"TaskAssignment\",\"noticeTypes\":[\"IN_SITE\",\"EMAIL\"]},{\"eventCode\":\"TaskPendingConfirmation\",\"noticeTypes\":[\"IN_SITE\",\"EMAIL\"]},{\"eventCode\":\"TaskModification\",\"noticeTypes\":[\"IN_SITE\",\"EMAIL\"]},{\"eventCode\":\"FunctionCaseWillOverdue\",\"noticeTypes\":[\"IN_SITE\",\"EMAIL\"]},{\"eventCode\":\"FunctionCaseOverdue\",\"noticeTypes\":[\"IN_SITE\",\"EMAIL\"]},{\"eventCode\":\"FunctionCaseAssignment\",\"noticeTypes\":[\"IN_SITE\",\"EMAIL\"]},{\"eventCode\":\"FunctionCaseModification\",\"noticeTypes\":[\"IN_SITE\",\"EMAIL\"]},{\"eventCode\":\"ApisModification\",\"noticeTypes\":[\"IN_SITE\",\"EMAIL\"]},{\"eventCode\":\"ScenarioModification\",\"noticeTypes\":[\"IN_SITE\",\"EMAIL\"]},{\"eventCode\":\"ReportGenerationSuccessful\",\"noticeTypes\":[\"IN_SITE\",\"EMAIL\"]},{\"eventCode\":\"ExecutionTestCompleted\",\"noticeTypes\":[\"IN_SITE\",\"EMAIL\"]},{\"eventCode\":\"ExecutionTestFailed\",\"noticeTypes\":[\"IN_SITE\",\"EMAIL\"]}]', 1);

-- ----------------------------
-- Table data for c_setting_tenant
-- ----------------------------
INSERT INTO `c_setting_tenant` (`id`, `invitation_code`, `locale_data`, `func_data`, `perf_data`, `stability_data`, `security_data`, `server_api_proxy_data`, `tester_event_data`, `tenant_id`)
                        VALUES (1, NULL, '{\"defaultLanguage\": \"zh_CN\"}', '{\"smoke\": true, \"security\": true, \"smokeCheckSetting\": {\"value\": \"API_AVAILABLE\", \"message\": \"xcm.enum.SmokeCheckSetting.API_AVAILABLE\"}, \"securityCheckSetting\": {\"value\": \"NOT_SECURITY_CODE\", \"message\": \"xcm.enum.SecurityCheckSetting.NOT_SECURITY_CODE\"}}', '{\"art\": \"500\", \"tps\": 500, \"threads\": 5000, \"duration\": \"50min\", \"errorRate\": 0.01, \"percentile\": {\"value\": \"P95\", \"message\": \"95%采样\"}, \"rampUpThreads\": 100, \"rampUpInterval\": \"1min\"}', '{\"art\": \"200\", \"cpu\": 75, \"tps\": 5000, \"disk\": 75, \"memory\": 75, \"network\": 75, \"threads\": 2000, \"duration\": \"30min\", \"errorRate\": 0.01, \"percentile\": {\"value\": \"P90\", \"message\": \"90%采样\"}}', '{\"alarm\": {\"alarmWay\": null, \"enabled\": false, \"receiveUser\": null}, \"signinLimit\": {\"enabled\": true, \"lockedPasswordErrorNum\": 6, \"signoutPeriodInMinutes\": 30, \"lockedDurationInMinutes\": 120, \"passwordErrorIntervalInMinutes\": 30}, \"signupAllow\": {\"enabled\": false, \"invitationCode\": null}, \"passwordPolicy\": {\"minLength\": 10}}', '{\"url\": \"wss://bj-c1-prod-angusproxy.xcan.cloud/angusProxy\", \"enabled\": true}', '[{\"eventCode\": \"TaskWillOverdue\", \"noticeTypes\": [\"IN_SITE\", \"EMAIL\"]}, {\"eventCode\": \"TaskOverdue\", \"noticeTypes\": [\"IN_SITE\", \"EMAIL\"]}, {\"eventCode\": \"TaskAssignment\", \"noticeTypes\": [\"IN_SITE\", \"EMAIL\"]}, {\"eventCode\": \"TaskPendingConfirmation\", \"noticeTypes\": [\"IN_SITE\", \"EMAIL\"]}, {\"eventCode\": \"TaskModification\", \"noticeTypes\": [\"IN_SITE\", \"EMAIL\"]}, {\"eventCode\": \"FunctionCaseWillOverdue\", \"noticeTypes\": [\"IN_SITE\", \"EMAIL\"]}, {\"eventCode\": \"FunctionCaseOverdue\", \"noticeTypes\": [\"IN_SITE\", \"EMAIL\"]}, {\"eventCode\": \"FunctionCaseAssignment\", \"noticeTypes\": [\"IN_SITE\", \"EMAIL\"]}, {\"eventCode\": \"FunctionCaseModification\", \"noticeTypes\": [\"IN_SITE\", \"EMAIL\"]}, {\"eventCode\": \"ApisModification\", \"noticeTypes\": [\"IN_SITE\", \"EMAIL\"]}, {\"eventCode\": \"ScenarioModification\", \"noticeTypes\": [\"IN_SITE\", \"EMAIL\"]}, {\"eventCode\": \"ReportGenerationSuccessful\", \"noticeTypes\": [\"IN_SITE\", \"EMAIL\"]}, {\"eventCode\": \"ExecutionTestCompleted\", \"noticeTypes\": [\"IN_SITE\", \"EMAIL\"]}, {\"eventCode\": \"ExecutionTestFailed\", \"noticeTypes\": [\"IN_SITE\", \"EMAIL\"]}]', :TENANT_ID);

-- ----------------------------
-- Table data for c_setting_tenant_quota
-- ----------------------------
INSERT INTO `c_setting_tenant_quota` (`id`, `app_code`, `service_code`, `name`, `allow_change`, `license_ctrl`, `calc_remaining`, `quota`, `min`, `max`, `capacity`, `tenant_id`)
                                VALUES (1, 'AngusGM', 'XCAN-ANGUSGM.BOOT', 'User', 0, 1, 1, 20, 1, 1000000, -1, :TENANT_ID);
INSERT INTO `c_setting_tenant_quota` (`id`, `app_code`, `service_code`, `name`, `allow_change`, `license_ctrl`, `calc_remaining`, `quota`, `min`, `max`, `capacity`, `tenant_id`)
                                VALUES (2, 'AngusGM', 'XCAN-ANGUSGM.BOOT', 'UserDept', 1, 0, 1, 5, 1, 2000, -1, :TENANT_ID);
INSERT INTO `c_setting_tenant_quota` (`id`, `app_code`, `service_code`, `name`, `allow_change`, `license_ctrl`, `calc_remaining`, `quota`, `min`, `max`, `capacity`, `tenant_id`)
                                VALUES (3, 'AngusGM', 'XCAN-ANGUSGM.BOOT', 'UserGroup', 1, 0, 1, 200, 1, 2000, -1, :TENANT_ID);
INSERT INTO `c_setting_tenant_quota` (`id`, `app_code`, `service_code`, `name`, `allow_change`, `license_ctrl`, `calc_remaining`, `quota`, `min`, `max`, `capacity`, `tenant_id`)
                                VALUES (4, 'AngusGM', 'XCAN-ANGUSGM.BOOT', 'Dept', 1, 0, 1, 500, 1, 20000, -1, :TENANT_ID);
INSERT INTO `c_setting_tenant_quota` (`id`, `app_code`, `service_code`, `name`, `allow_change`, `license_ctrl`, `calc_remaining`, `quota`, `min`, `max`, `capacity`, `tenant_id`)
                                VALUES (5, 'AngusGM', 'XCAN-ANGUSGM.BOOT', 'DeptLevel', 1, 0, 1, 5, 1, 10, -1, :TENANT_ID);
INSERT INTO `c_setting_tenant_quota` (`id`, `app_code`, `service_code`, `name`, `allow_change`, `license_ctrl`, `calc_remaining`, `quota`, `min`, `max`, `capacity`, `tenant_id`)
                                VALUES (6, 'AngusGM', 'XCAN-ANGUSGM.BOOT', 'DeptUser', 1, 0, 1, 200, 1, 2000, -1, :TENANT_ID);
INSERT INTO `c_setting_tenant_quota` (`id`, `app_code`, `service_code`, `name`, `allow_change`, `license_ctrl`, `calc_remaining`, `quota`, `min`, `max`, `capacity`, `tenant_id`)
                                VALUES (7, 'AngusGM', 'XCAN-ANGUSGM.BOOT', 'Group', 1, 0, 1, 200, 1, 20000, -1, :TENANT_ID);
INSERT INTO `c_setting_tenant_quota` (`id`, `app_code`, `service_code`, `name`, `allow_change`, `license_ctrl`, `calc_remaining`, `quota`, `min`, `max`, `capacity`, `tenant_id`)
                                VALUES (8, 'AngusGM', 'XCAN-ANGUSGM.BOOT', 'GroupUser', 1, 0, 1, 200, 1, 2000, -1, :TENANT_ID);
INSERT INTO `c_setting_tenant_quota` (`id`, `app_code`, `service_code`, `name`, `allow_change`, `license_ctrl`, `calc_remaining`, `quota`, `min`, `max`, `capacity`, `tenant_id`)
                                VALUES (9, 'AngusGM', 'XCAN-ANGUSGM.BOOT', 'OrgTag', 1, 0, 1, 200, 1, 20000, -1, :TENANT_ID);
INSERT INTO `c_setting_tenant_quota` (`id`, `app_code`, `service_code`, `name`, `allow_change`, `license_ctrl`, `calc_remaining`, `quota`, `min`, `max`, `capacity`, `tenant_id`)
                                VALUES (10, 'AngusGM', 'XCAN-ANGUSGM.BOOT', 'OrgTargetTag', 1, 0, 0, 10, 1, 2000, -1, :TENANT_ID);
INSERT INTO `c_setting_tenant_quota` (`id`, `app_code`, `service_code`, `name`, `allow_change`, `license_ctrl`, `calc_remaining`, `quota`, `min`, `max`, `capacity`, `tenant_id`)
                                VALUES (11, 'AngusGM', 'XCAN-ANGUSGM.BOOT', 'PolicyCustom', 1, 0, 1, 200, 1, 20000, -1, :TENANT_ID);
INSERT INTO `c_setting_tenant_quota` (`id`, `app_code`, `service_code`, `name`, `allow_change`, `license_ctrl`, `calc_remaining`, `quota`, `min`, `max`, `capacity`, `tenant_id`)
                                VALUES (12, 'AngusGM', 'XCAN-ANGUSGM.BOOT', 'FileStore', 1, 0, 1, 118111600640, 1073741824, 1099511627776, -1, :TENANT_ID);
INSERT INTO `c_setting_tenant_quota` (`id`, `app_code`, `service_code`, `name`, `allow_change`, `license_ctrl`, `calc_remaining`, `quota`, `min`, `max`, `capacity`, `tenant_id`)
                                VALUES (13, 'AngusGM', 'XCAN-ANGUSGM.BOOT', 'SystemToken', 0, 1, 1, 10, 1, 100, -1, :TENANT_ID);
INSERT INTO `c_setting_tenant_quota` (`id`, `app_code`, `service_code`, `name`, `allow_change`, `license_ctrl`, `calc_remaining`, `quota`, `min`, `max`, `capacity`, `tenant_id`)
                                VALUES (14, 'AngusGM', 'XCAN-ANGUSGM.BOOT', 'UserToken', 0, 1, 1, 3, 1, 5, -1, :TENANT_ID);
INSERT INTO `c_setting_tenant_quota` (`id`, `app_code`, `service_code`, `name`, `allow_change`, `license_ctrl`, `calc_remaining`, `quota`, `min`, `max`, `capacity`, `tenant_id`)
                                VALUES (15, 'AngusGM', 'XCAN-ANGUSGM.BOOT', 'DataSpace', 1, 0, 1, 2000, 1, 100000, -1, :TENANT_ID);

-- ----------------------------
-- Table data for c_setting_user
-- ----------------------------
INSERT INTO `c_setting_user` (`id`, `preference`, `api_proxy`, `social_bind`, `tenant_id`)
                    VALUES (:GM_ADMIN_USER_ID, '{\"language\": \"zh_CN\", \"themeCode\": \"light\"}', '{\"noProxy\": {\"name\": \"NO_PROXY\", \"enabled\": false}, \"cloudProxy\": {\"url\": \"wss://bj-c1-prod-angusproxy.xcan.cloud/angusProxy\", \"name\": \"CLOUD_PROXY\", \"enabled\": true}, \"clientProxy\": {\"url\": \"ws://localhost:6806/angusProxy\", \"name\": \"CLIENT_PROXY\", \"enabled\": false}, \"serverProxy\": {\"name\": \"SERVER_PROXY\", \"enabled\": false}}', NULL, :TENANT_ID);

-- ----------------------------
-- Table data for email_template
-- ----------------------------
INSERT INTO `email_template` (`id`, `code`, `name`, `language`, `subject`, `content`, `verification_code`, `verification_code_valid_second`, `private0`)
                    VALUES (1, 'VERIFICATION_CODE', '验证码消息', 'zh_CN', '验证码消息', '尊敬的XCan用户：\n	您的验证码为：${verificationCode}，当前验证码5分钟内有效，请勿泄漏于他人。如非本人操作，请忽略。\n\n祝您生活愉快！', 1, 300, 1);
INSERT INTO `email_template` (`id`, `code`, `name`, `language`, `subject`, `content`, `verification_code`, `verification_code_valid_second`, `private0`)
                    VALUES (5, 'SYS_EXCEPTION_NOTICE', '系统异常告警', 'zh_CN', '系统异常告警通知', '尊敬的XCan用户：\n	系统在${datetime}健康检查中检测到系统存在异常，异常原因：${exceptionReason}，详情请进入“全局管理>系统>故障诊断”查看。\n\n祝您生活愉快！', 0, -1, 1);
INSERT INTO `email_template` (`id`, `code`, `name`, `language`, `subject`, `content`, `verification_code`, `verification_code_valid_second`, `private0`)
                    VALUES (6, 'SYS_RECOVERY_NOTICE', '系统异常恢复', 'zh_CN', '系统异常恢复通知', '尊敬的XCan用户：\n	系统在${datetime}健康检查检测到的系统异常已经恢复，请您正常使用。\n\n祝您生活愉快！', 0, -1, 1);
INSERT INTO `email_template` (`id`, `code`, `name`, `language`, `subject`, `content`, `verification_code`, `verification_code_valid_second`, `private0`)
                    VALUES (7, 'SYS_SECURITY_NOTICE', '系统安全告警', 'zh_CN', '系统安全告警通知', '尊敬的XCan用户：\n	系统安全告警！检测到${object}，请尽快确认并处理。\n\n祝您生活愉快！', 0, -1, 1);
INSERT INTO `email_template` (`id`, `code`, `name`, `language`, `subject`, `content`, `verification_code`, `verification_code_valid_second`, `private0`)
                    VALUES (9, 'CHANNEL_TEST', '通道测试', 'zh_CN', '通道测试消息', '尊敬的XCan用户：\n	${channelType}通道配置测试消息，收到信息表示通道配置成功。\n\n祝您生活愉快！', 0, -1, 1);
INSERT INTO `email_template` (`id`, `code`, `name`, `language`, `subject`, `content`, `verification_code`, `verification_code_valid_second`, `private0`)
                    VALUES (200, 'TESTING_EXEC_STARTED', '测试执行开始', 'zh_CN', '测试执行开始提醒', '尊敬的XCan用户：\n	您创建或关联的测试任务“${name}“已开始执行。如果您想查看测试情况或终止执行，请登录系统进入“执行”查看。\n\n祝您生活愉快！', 0, -1, 1);
INSERT INTO `email_template` (`id`, `code`, `name`, `language`, `subject`, `content`, `verification_code`, `verification_code_valid_second`, `private0`)
                    VALUES (201, 'TESTING_EXEC_FINISHED', '测试执行完成', 'zh_CN', '测试执行完成提醒', '尊敬的XCan用户：\n	您创建或关联的测试任务“${name}“-[编号：${no}]已执行完成。如果您想查看测试结果，请登录系统进入“执行”查看。\n\n祝您生活愉快！', 0, -1, 1);
INSERT INTO `email_template` (`id`, `code`, `name`, `language`, `subject`, `content`, `verification_code`, `verification_code_valid_second`, `private0`)
                    VALUES (202, 'TESTING_EXEC_FAILED', '测试执行失败', 'zh_CN', '测试执行失败提醒', '尊敬的XCan用户：\n	您创建或关联的测试任务“${name}“-[编号：${no}]执行失败，原因：${cause}。如果您想查看具体测试结果，请登录系统进入“执行”查看。\n\n祝您生活愉快！', 0, -1, 1);
INSERT INTO `email_template` (`id`, `code`, `name`, `language`, `subject`, `content`, `verification_code`, `verification_code_valid_second`, `private0`)
                    VALUES (203, 'TESTING_REPORTER_SENT', '测试报告发送', 'zh_CN', '测试报告发送提醒', '尊敬的XCan用户：\n	您创建或关联的测试任务“${name}“-[编号：${no}]执行完成，如果您想查看测试详细结果，请登录系统进入“报告”查看。\n\n祝您生活愉快！', 0, -1, 1);
INSERT INTO `email_template` (`id`, `code`, `name`, `language`, `subject`, `content`, `verification_code`, `verification_code_valid_second`, `private0`)
                    VALUES (204, 'TESTING_TASK_OVERDUE', '测试任务逾期', 'zh_CN', '测试任务逾期提醒', '尊敬的XCan用户：\n	您有“${num}“个测试任务已逾期，请尽快登录系统查看并处理。\n\n祝您生活愉快！', 0, -1, 1);
INSERT INTO `email_template` (`id`, `code`, `name`, `language`, `subject`, `content`, `verification_code`, `verification_code_valid_second`, `private0`)
                    VALUES (1001, 'VERIFICATION_CODE', 'Verification Code Message', 'en', 'Verification Code Message', 'Dear XCan User:\nYour verification code is: ${verificationCode}, the current verification code is valid within 5 minutes, please do not disclose it to others. If it is not done by me, please ignore it.\n\nWish you a happy life!', 1, 300, 1);
INSERT INTO `email_template` (`id`, `code`, `name`, `language`, `subject`, `content`, `verification_code`, `verification_code_valid_second`, `private0`)
                    VALUES (1005, 'SYS_EXCEPTION_NOTICE', 'System Exception Alert', 'en', 'System Exception Alert Notification', 'Dear XCan User:\nThe system detected an exception in the health check of ${datetime}. The reason for the exception: ${exceptionReason}. For details, go to \"Global Management> System> Fault Diagnosis\" to view.\n\nWish you a happy life!', 0, -1, 1);
INSERT INTO `email_template` (`id`, `code`, `name`, `language`, `subject`, `content`, `verification_code`, `verification_code_valid_second`, `private0`)
                    VALUES (1006, 'SYS_RECOVERY_NOTICE', 'System Recovery ', 'en', 'System Recovery Alert Notification', 'Dear XCan User:\nThe system abnormality detected in the ${datetime} health check has been recovered, please use it normally.\n\nWish you a happy life!', 0, -1, 1);
INSERT INTO `email_template` (`id`, `code`, `name`, `language`, `subject`, `content`, `verification_code`, `verification_code_valid_second`, `private0`)
                    VALUES (1007, 'SYS_SECURITY_NOTICE', 'System Security Alert', 'en', 'System Security Alert Notification', 'Dear XCan User:\nThe system detected an exception in the health check of ${datetime}. The reason for the exception: ${exceptionReason}. For details, go to \"Global Management> System> Fault Diagnosis\" to view.\n\nWish you a happy life!', 0, -1, 1);
INSERT INTO `email_template` (`id`, `code`, `name`, `language`, `subject`, `content`, `verification_code`, `verification_code_valid_second`, `private0`)
                    VALUES (1009, 'CHANNEL_TEST', 'Channel Test Message', 'en', 'Channel Test Message', 'Dear XCan User:\n${channelType} channel configuration test message, receiving the message indicates that the channel configuration is successful.\n\nWish you a happy life!', 0, -1, 1);
INSERT INTO `email_template` (`id`, `code`, `name`, `language`, `subject`, `content`, `verification_code`, `verification_code_valid_second`, `private0`)
                    VALUES (1200, 'TESTING_EXEC_STARTED', 'Testing Execution Started', 'en', 'Testing Execution Started Reminder', 'Dear XCan User:\nThe test task \"${name}\"you created or associated has started executing. If you want to check the test status or terminate the execution, please login to the system and enter \"Execution\" to check.\n\nWish you a happy life!', 0, -1, 1);
INSERT INTO `email_template` (`id`, `code`, `name`, `language`, `subject`, `content`, `verification_code`, `verification_code_valid_second`, `private0`)
                    VALUES (1201, 'TESTING_EXEC_FINISHED', 'Testing Execution Finished', 'en', 'Testing Execution Finished Reminder', 'Dear XCan User:\nThe test task \"${name}\"-[no: ${no}] you created or associated has completed. If you want to view the test results, please login to the system and enter \"Execution\" to view.\n\nWish you a happy life!', 0, -1, 1);
INSERT INTO `email_template` (`id`, `code`, `name`, `language`, `subject`, `content`, `verification_code`, `verification_code_valid_second`, `private0`)
                    VALUES (1202, 'TESTING_EXEC_FAILED', 'Testing Execution Failed', 'en', 'Testing Execution Failed Reminder', 'Dear XCan User:\nThe test task \"${name}\"-[no: ${no}] you created or associated failed to execute, reason: ${cause}. If you want to view the specific test results, please login to the system and enter \"Execution\" to view.\n\nWish you a happy life!', 0, -1, 1);
INSERT INTO `email_template` (`id`, `code`, `name`, `language`, `subject`, `content`, `verification_code`, `verification_code_valid_second`, `private0`)
                    VALUES (1203, 'TESTING_REPORTER_SENT', 'Testing Reporter Sent', 'en', 'Testing Reporter Sent Reminder', 'Dear XCan User:\r\nThe test task \"${name}\"-[NO: ${no}] you created or associated has been executed. If you want to view the detailed test results, please login to the system and enter \"Report\" to view it.\r\n\r\nWish you a happy life!', 0, -1, 1);
INSERT INTO `email_template` (`id`, `code`, `name`, `language`, `subject`, `content`, `verification_code`, `verification_code_valid_second`, `private0`)
                    VALUES (1204, 'TESTING_TASK_OVERDUE', 'Testing Task Overdue', 'en', 'Testing Task Overdue Reminder', 'Dear XCan User:\nYou have \"${num}\" test tasks that are overdue, please login to the system as soon as possible to check and process them.\n\nWish you a happy life!', 0, -1, 1);

-- ----------------------------
-- Table data for email_template_biz
-- ----------------------------
INSERT INTO `email_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('VERIFICATION_CODE', 'BIND_EMAIL', 1);
INSERT INTO `email_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('CHANNEL_TEST', 'CHANNEL_TEST', 1);
INSERT INTO `email_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('VERIFICATION_CODE', 'MODIFY_EMAIL', 1);
INSERT INTO `email_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('VERIFICATION_CODE', 'PASSWORD_UPDATE', 1);
INSERT INTO `email_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('VERIFICATION_CODE', 'PASSWORD_FORGET', 1);
INSERT INTO `email_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('VERIFICATION_CODE', 'PAY_PASSWORD_UPDATE', 1);
INSERT INTO `email_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('VERIFICATION_CODE', 'SIGNIN', 1);
INSERT INTO `email_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('VERIFICATION_CODE', 'SIGNUP', 1);
INSERT INTO `email_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('VERIFICATION_CODE', 'SIGN_CANCEL', 1);
INSERT INTO `email_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('SYS_EXCEPTION_NOTICE', 'SYS_EXCEPTION_NOTICE', 1);
INSERT INTO `email_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('SYS_RECOVERY_NOTICE', 'SYS_RECOVERY_NOTICE', 1);
INSERT INTO `email_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('SYS_SECURITY_NOTICE', 'SYS_SECURITY_NOTICE', 1);
INSERT INTO `email_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('TESTING_EXEC_FAILED', 'TESTING_EXEC_FAILED', 1);
INSERT INTO `email_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('TESTING_EXEC_FINISHED', 'TESTING_EXEC_FINISHED', 1);
INSERT INTO `email_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('TESTING_EXEC_STARTED', 'TESTING_EXEC_STARTED', 1);
INSERT INTO `email_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('TESTING_REPORTER_SENT', 'TESTING_REPORTER_SENT', 1);
INSERT INTO `email_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('TESTING_TASK_OVERDUE', 'TESTING_TASK_OVERDUE', 1);

-- ----------------------------
-- Table data for sms_channel
-- ----------------------------
INSERT INTO `sms_channel` (`id`, `name`, `enabled`, `logo`, `endpoint`, `access_key_secret`, `access_key_id`, `third_channel_no`, `created_by`, `created_date`) VALUES (1, 'Aliyun SMS', 1, 'https://img.alicdn.com/tfs/TB13DzOjXP7gK0jSZFjXXc5aXXa-212-48.png', 'dysmsapi.aliyuncs.com', '', '', NULL, -1, '2024-01-01 00:00:00');
INSERT INTO `sms_channel` (`id`, `name`, `enabled`, `logo`, `endpoint`, `access_key_secret`, `access_key_id`, `third_channel_no`, `created_by`, `created_date`) VALUES (2, 'HuaweiCloud SMS', 0, 'https://res.hc-cdn.com/cnpm-header-and-footer/2.0.6/base/header-china/components/images/logo.svg', 'https://smsapi.cn-north-4.myhuaweicloud.com:443/sms/batchSendSms/v1', '', '', '', -1, '2024-01-01 00:00:00');

-- ----------------------------
-- Table data for sms_template
-- ----------------------------
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                    VALUES (1, 'VERIFICATION_CODE', '验证码消息', 'zh_CN', NULL, '您的验证码为：${verificationCode}，当前验证码5分钟内有效，请勿泄漏于他人。如非本人操作，请忽略。', '晓蚕云', -1, 1, 300, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (5, 'SYS_EXCEPTION_NOTICE', '系统异常告警', 'zh_CN', NULL, '系统在${datetime}健康检查中检测到系统存在异常，异常原因：${exceptionReason}，详情请进入“全局管理>系统>故障诊断”查看。', '晓蚕云', -1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (6, 'SYS_RECOVERY_NOTICE', '系统异常恢复', 'zh_CN', NULL, '系统在${datetime}健康检查检测到的系统异常已经恢复，请您正常使用。	', '晓蚕云', -1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (7, 'SYS_SECURITY_NOTICE', '系统安全告警', 'zh_CN', NULL, '系统安全告警！检测到${object}，请尽快确认并处理。', '晓蚕云', -1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (9, 'CHANNEL_TEST', '通道测试', 'zh_CN', NULL, '${channelType}通道配置测试消息，收到信息表示通道配置成功。', '晓蚕云', -1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (18, 'TESTING_EXEC_STARTED', '测试执行开始', 'zh_CN', NULL, '您创建或关联的测试任务“${name}“已开始执行。如果您想查看测试情况或终止执行，请登录系统进入“执行”查看。', '晓蚕云', -1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (19, 'TESTING_EXEC_FINISHED', '测试执行完成', 'zh_CN', NULL, '您创建或关联的测试任务“${name}“-[编号：${no}]已执行完成。如果您想查看测试结果，请登录系统进入“执行”查看。', '晓蚕云', -1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (20, 'TESTING_EXEC_FAILED', '测试执行失败', 'zh_CN', NULL, '您创建或关联的测试任务“${name}“-[编号：${no}]执行失败，原因：${cause}。如果您想查看具体测试结果，请登录系统进入“执行”查看。', '晓蚕云', -1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (21, 'TESTING_REPORTER_SENT', '测试报告发送', 'zh_CN', NULL, '您创建或关联的测试任务“${name}“-[编号：${no}]执行完成，如果您想查看测试详细结果，请登录系统进入“报告”查看。', '晓蚕云', -1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (22, 'TESTING_TASK_OVERDUE', '测试任务逾期', 'zh_CN', NULL, '您有“${num}“个测试任务已逾期，请尽快登录系统查看并处理。', '晓蚕云', -1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (28, 'EVENT_NOTICE', '事件通知', 'zh_CN', NULL, '系统在 ${date} 触发事件通知，事件内容：${description}。请及时查看或前往处理！\n\n', '晓蚕云', -1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (1001, 'VERIFICATION_CODE', 'Verification Code Message', 'en', NULL, 'Your verification code is: ${verificationCode}, the current verification code is valid within 5 minutes, please do not disclose it to others. If it is not done by me, please ignore it.', 'XcanCloud', -1, 1, 300, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (1005, 'SYS_EXCEPTION_NOTICE', 'System Exception', 'en', NULL, 'The system detected an exception in the health check of ${datetime}. The reason for the exception: ${exceptionReason}. For details, go to \"Global Management> System> Fault Diagnosis\" to view.	', 'XcanCloud', -1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (1006, 'SYS_RECOVERY_NOTICE', 'System Recovery', 'en', NULL, 'The system abnormality detected in the ${datetime} health check has been recovered, please use it normally.	', 'XcanCloud', -1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (1007, 'SYS_SECURITY_NOTICE', 'System Security', 'en', NULL, 'System security warning! ${object} is detected, please confirm and deal with it as soon as possible.	', 'XcanCloud', -1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (1009, 'CHANNEL_TEST', 'Channel Test', 'en', NULL, '${channelType} channel configuration test message, receiving the message indicates that the channel configuration is successful.', 'XcanCloud', -1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (1018, 'TESTING_EXEC_STARTED', 'Testing Execution Started', 'en', NULL, 'The test task \"${name}\"you created or associated has started executing. If you want to check the test status or terminate the execution, please login to the system and enter \"Execution\" to check.', 'XcanCloud', -1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (1019, 'TESTING_EXEC_FINISHED', 'Testing Execution Finished', 'en', NULL, 'The test task \"${name}\"-[no: ${no}] you created or associated has completed. If you want to view the test results, please login to the system and enter \"Execution\" to view.', 'XcanCloud', -1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (1020, 'TESTING_EXEC_FAILED', 'Testing Execution Failed', 'en', NULL, 'The test task \"${name}\"-[no: ${no}] you created or associated failed to execute, reason: ${cause}. If you want to view the specific test results, please login to the system and enter \"Execution\" to view.', 'XcanCloud', -1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (1021, 'TESTING_REPORTER_SENT', 'Testing Reporter Sent', 'en', NULL, 'The test task \"${name}\"-[ID: ${no}] you created or associated has been executed. If you want to view the detailed test results, please log in to the system and go to \"Report\" to view it.', 'XcanCloud', -1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (1022, 'TESTING_TASK_OVERDUE', 'Testing Task Overdue', 'en', NULL, 'You have \"${num}\" test tasks that are overdue, please login to the system as soon as possible to check and process them.', 'XcanCloud', -1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (1028, 'EVENT_NOTICE', 'Event Notice', 'en', NULL, 'The system triggered an event notification on ${date}, with event details: ${description}. Please check it promptly or go to handle it!', 'XcanCloud', -1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (100001, 'VERIFICATION_CODE', '验证码消息', 'zh_CN', 'SMS_230270435', '您的验证码为：${verificationCode}，当前验证码5分钟内有效，请勿泄漏于他人。如非本人操作，请忽略。', '晓蚕云', 1, 1, 300, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (100005, 'SYS_EXCEPTION_NOTICE', '系统异常告警', 'zh_CN', 'SMS_230240609', '系统在${datetime}健康检查中检测到系统存在异常，异常原因：${exceptionReason}，详情请进入“全局管理>系统>故障诊断”查看。', '晓蚕云', 1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (100006, 'SYS_RECOVERY_NOTICE', '系统异常恢复', 'zh_CN', 'SMS_237202119', '系统在${datetime}健康检查检测到的系统异常已经恢复，请您正常使用。	', '晓蚕云', 1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (100007, 'SYS_SECURITY_NOTICE', '系统安全告警', 'zh_CN', 'SMS_230270449', '系统安全告警！检测到${object}，请尽快确认并处理。', '晓蚕云', 1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (100009, 'CHANNEL_TEST', '通道测试', 'zh_CN', 'SMS_237571373', '${channelType}通道配置测试消息，收到信息表示通道配置成功。', '晓蚕云', 1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (100018, 'TESTING_EXEC_STARTED', '测试执行开始', 'zh_CN', 'SMS_245700035', '您创建或关联的测试任务“${name}“已开始执行。如果您想查看测试情况或终止执行，请登录系统进入“执行”查看。', '晓蚕云', 1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (100019, 'TESTING_EXEC_FINISHED', '测试执行完成', 'zh_CN', 'SMS_244950379', '您创建或关联的测试任务“${name}“-[编号：${no}]已执行完成。如果您想查看测试结果，请登录系统进入“执行”查看。', '晓蚕云', 1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (100020, 'TESTING_EXEC_FAILED', '测试执行失败', 'zh_CN', 'SMS_245100016', '您创建或关联的测试任务“${name}“-[编号：${no}]执行失败，原因：${cause}。如果您想查看具体测试结果，请登录系统进入“执行”查看。', '晓蚕云', 1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (100021, 'TESTING_REPORTER_SENT', '测试报告发送', 'zh_CN', 'SMS_244920378', '您创建或关联的测试任务“${name}“-[编号：${no}]执行完成，如果您想查看测试详细结果，请登录系统进入“报告”查看。', '晓蚕云', 1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (100022, 'TESTING_TASK_OVERDUE', '测试任务逾期', 'zh_CN', 'SMS_244995373', '您有“${num}“个测试任务已逾期，请尽快登录系统查看并处理。', '晓蚕云', 1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (100028, 'EVENT_NOTICE', '事件通知', 'zh_CN', 'SMS_472065026', '事件通知，事件内容：${description}，触发时间：${date}。\n\n', '晓蚕云', 1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (110001, 'VERIFICATION_CODE', 'Verification Code Message', 'en', 'SMS_237591269', 'Your verification code is: ${verificationCode}, the current verification code is valid within 5 minutes, please do not disclose it to others. If it is not done by me, please ignore it.', 'XcanCloud', 1, 1, 300, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (110005, 'SYS_EXCEPTION_NOTICE', 'System Exception', 'en', 'SMS_237561446', 'The system detected an exception in the health check of ${datetime}. The reason for the exception: ${exceptionReason}. For details, go to \"Global Management> System> Fault Diagnosis\" to view.	', 'XcanCloud', 1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (110006, 'SYS_RECOVERY_NOTICE', 'System Recovery', 'en', 'SMS_237207115', 'The system abnormality detected in the ${datetime} health check has been recovered, please use it normally.	', 'XcanCloud', 1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (110007, 'SYS_SECURITY_NOTICE', 'System Security', 'en', 'SMS_230655837', 'System security warning! ${object} is detected, please confirm and deal with it as soon as possible.	', 'XcanCloud', 1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (110009, 'CHANNEL_TEST', 'Channel Test', 'en', 'SMS_237586393', '${channelType} channel configuration test message, receiving the message indicates that the channel configuration is successful.', 'XcanCloud', 1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (110018, 'TESTING_EXEC_STARTED', 'Testing Execution Started', 'en', 'SMS_245190033', 'The test task \"${name}\"you created or associated has started executing. If you want to check the test status or terminate the execution, please login to the system and enter \"Execution\" to check.', 'XcanCloud', 1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (110019, 'TESTING_EXEC_FINISHED', 'Testing Execution Finished', 'en', 'SMS_245065274', 'The test task \"${name}\"-[no: ${no}] you created or associated has completed. If you want to view the test results, please login to the system and enter \"Execution\" to view.', 'XcanCloud', 1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (110020, 'TESTING_EXEC_FAILED', 'Testing Execution Failed', 'en', 'SMS_245205031', 'The test task \"${name}\"-[no: ${no}] you created or associated failed to execute, reason: ${cause}. If you want to view the specific test results, please login to the system and enter \"Execution\" to view.', 'XcanCloud', 1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (110021, 'TESTING_REPORTER_SENT', 'Testing Reporter Sent', 'en', 'SMS_245115052', 'The test task \"${name}\"-[ID: ${no}] you created or associated has been executed. If you want to view the detailed test results, please log in to the system and go to \"Report\" to view it.', 'XcanCloud', 1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (110022, 'TESTING_TASK_OVERDUE', 'Testing Task Overdue', 'en', 'SMS_245095219', 'You have \"${num}\" test tasks that are overdue, please login to the system as soon as possible to check and process them.', 'XcanCloud', 1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (110028, 'EVENT_NOTICE', 'Event Notice', 'en', NULL, 'The system triggered an event notification on ${date}, with event details: ${description}. Please check it promptly or go to handle it!', 'XcanCloud', -1, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (200001, 'VERIFICATION_CODE', '验证码消息', 'zh_CN', '7402ce3637f143488ae085d3ac9e8042', '您的验证码为: ${verificationCode}，当前验证码5分钟内有效，请勿泄漏于他人。如非本人操作，请忽略。', '晓蚕云', 2, 1, 300, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (200005, 'SYS_EXCEPTION_NOTICE', '系统异常告警', 'zh_CN', 'eaa53acb943e46cc908284c27d9c6aed', '系统在${datetime}健康检查中检测到系统存在异常，异常原因：${exceptionReason}，详情请进入“全局管理>系统>故障诊断”查看。', '晓蚕云', 2, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (200006, 'SYS_RECOVERY_NOTICE', '系统异常恢复', 'zh_CN', '75966764178040d3917fe6760c361dab', '系统在${datetime}健康检查检测到的系统异常已经恢复，请您正常使用。	', '晓蚕云', 2, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (200007, 'SYS_SECURITY_NOTICE', '系统安全告警', 'zh_CN', 'eaa53acb943e46cc908284c27d9c6aed', '系统安全告警！检测到${object}，请尽快确认并处理。', '晓蚕云', 2, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (200009, 'CHANNEL_TEST', '通道测试', 'zh_CN', 'f9292e1912e749b9a6a1902c6216cbb1', '${channelType}通道配置测试消息，收到信息表示通道配置成功。', '晓蚕云', 2, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (200018, 'TESTING_EXEC_STARTED', '测试执行开始', 'zh_CN', 'b4345de53d5547c2b1acf2df44b6befc', '您创建或关联的测试任务“${name}“已开始执行。如果您想查看测试情况或终止执行，请登录系统进入“执行”查看。', '晓蚕云', 2, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (200019, 'TESTING_EXEC_FINISHED', '测试执行完成', 'zh_CN', '0a3fbde45368436890758ff297a45bb3', '您创建或关联的测试任务“${name}“-[编号：${no}]已执行完成。如果您想查看测试结果，请登录系统进入“执行”查看。', '晓蚕云', 2, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (200020, 'TESTING_EXEC_FAILED', '测试执行失败', 'zh_CN', '307cc71e3e214317884bc8f50cb6e8bb', '您创建或关联的测试任务“${name}“-[编号：${no}]执行失败，原因：${cause}。如果您想查看具体测试结果，请登录系统进入“执行”查看。', '晓蚕云', 2, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (200021, 'TESTING_REPORTER_SENT', '测试报告发送', 'zh_CN', 'a9b7aeb08fba4a8987fccefb855ae484', '您创建或关联的测试任务“${name}“-[编号：${no}]执行完成，如果您想查看测试详细结果，请登录系统进入“报告”查看。', '晓蚕云', 2, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (200022, 'TESTING_TASK_OVERDUE', '测试任务逾期', 'zh_CN', 'c499f672b6524968b70d3d2e2677843d', '您有“${num}“个测试任务已逾期，请尽快登录系统查看并处理。', '晓蚕云', 2, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (210001, 'VERIFICATION_CODE', 'Verification Code Message', 'en', '99dbb01adb684af2b69e07bee3d81942', 'Your verification code is: ${verificationCode}, the current verification code is valid within 5 minutes, please do not disclose it to others. If it is not done by me, please ignore it.', 'XcanCloud', 2, 1, 300, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (210005, 'SYS_EXCEPTION_NOTICE', 'System Exception', 'en', '8d8f0928f2cf417c91ffad46c2ca3e0e', 'The system detected an exception in the health check of ${datetime}. The reason for the exception: ${exceptionReason}. For details, go to \"Global Management> System> Fault Diagnosis\" to view.	', 'XcanCloud', 2, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (210006, 'SYS_RECOVERY_NOTICE', 'System Recovery', 'en', 'f55e0c4433a94db68f35d1bfac7e8dc5', 'The system abnormality detected in the ${datetime} health check has been recovered, please use it normally.	', 'XcanCloud', 2, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (210007, 'SYS_SECURITY_NOTICE', 'System Security', 'en', 'ddd1f5facb9f4173814f5790ddd0159c', 'System security warning! ${object} is detected, please confirm and deal with it as soon as possible.	', 'XcanCloud', 2, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (210009, 'CHANNEL_TEST', 'Channel Test', 'en', '5354d90a44d441da9d4c41495e75be46', '${channelType} channel configuration test message, receiving the message indicates that the channel configuration is successful.', 'XcanCloud', 2, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (210018, 'TESTING_EXEC_STARTED', 'Testing Execution Started', 'en', 'e10563fd026d4ceb8f0e32321c742dcf', 'The test task \"${name}\"you created or associated has started executing. If you want to check the test status or terminate the execution, please login to the system and enter \"Execution\" to check.', 'XcanCloud', 2, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (210019, 'TESTING_EXEC_FINISHED', 'Testing Execution Finished', 'en', '5bd0411de8814b36b5488842bc4c086f', 'The test task \"${name}\"-[no: ${no}] you created or associated has completed. If you want to view the test results, please login to the system and enter \"Execution\" to view.', 'XcanCloud', 2, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (210020, 'TESTING_EXEC_FAILED', 'Testing Execution Failed', 'en', 'effc5556928d44e0b5bff35867161457', 'The test task \"${name}\"-[no: ${no}] you created or associated failed to execute, reason: ${cause}. If you want to view the specific test results, please login to the system and enter \"Execution\" to view.', 'XcanCloud', 2, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (210021, 'TESTING_REPORTER_SENT', 'Testing Reporter Sent', 'en', '7485762f115c4ebbbc93cead35c56460', 'The test task \"${name}\"-[ID: ${no}] you created or associated has been executed. If you want to view the detailed test results, please log in to the system and go to \"Report\" to view it.', 'XcanCloud', 2, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (210022, 'TESTING_TASK_OVERDUE', 'Testing Task Overdue', 'en', '5c189aea496d4fe0beb6cd251a569701', 'You have \"${num}\" test tasks that are overdue, please login to the system as soon as possible to check and process them.', 'XcanCloud', 2, 0, -1, 1);
INSERT INTO `sms_template` (`id`, `code`, `name`, `language`, `third_code`, `content`, `signature`, `channel_id`, `verification_code`, `verification_code_valid_second`, `private0`)
                     VALUES (210028, 'EVENT_NOTICE', 'Event Notice', 'en', NULL, 'The system triggered an event notification on ${date}, with event details: ${description}. Please check it promptly or go to handle it!', 'XcanCloud', -1, 0, -1, 1);

-- ----------------------------
-- Table data for sms_template_biz
-- ----------------------------
INSERT INTO `sms_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('VERIFICATION_CODE', 'BIND_MOBILE', 1);
INSERT INTO `sms_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('CHANNEL_TEST', 'CHANNEL_TEST', 1);
INSERT INTO `sms_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('VERIFICATION_CODE', 'MODIFY_MOBILE', 1);
INSERT INTO `sms_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('VERIFICATION_CODE', 'PASSWORD_UPDATE', 1);
INSERT INTO `sms_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('VERIFICATION_CODE', 'PASSWORD_FORGET', 1);
INSERT INTO `sms_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('VERIFICATION_CODE', 'PAY_PASSWORD_UPDATE', 1);
INSERT INTO `sms_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('VERIFICATION_CODE', 'SIGNIN', 1);
INSERT INTO `sms_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('VERIFICATION_CODE', 'SIGNUP', 1);
INSERT INTO `sms_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('VERIFICATION_CODE', 'SIGN_CANCEL', 1);
INSERT INTO `sms_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('SYS_EXCEPTION_NOTICE', 'SYS_EXCEPTION_NOTICE', 1);
INSERT INTO `sms_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('SYS_RECOVERY_NOTICE', 'SYS_RECOVERY_NOTICE', 1);
INSERT INTO `sms_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('SYS_SECURITY_NOTICE', 'SYS_SECURITY_NOTICE', 1);
INSERT INTO `sms_template_biz` (`template_code`, `biz_key`, `private0`) VALUES ('EVENT_NOTICE', 'EVENT_NOTICE', 1);

-- ----------------------------
-- Table data for bucket
-- ----------------------------
INSERT INTO `bucket` (`id`, `name`, `acl`, `tenant_created`, `created_by`, `created_date`, `last_modified_by`, `last_modified_date`)
                VALUES (1, 'xcan-baseapp', 'Private', 0, -1, '2024-01-01 00:00:00', -1, '2024-01-01 00:00:00');
INSERT INTO `bucket` (`id`, `name`, `acl`, `tenant_created`, `created_by`, `created_date`, `last_modified_by`, `last_modified_date`)
                VALUES (2, 'xcan-angustester', 'Private', 0, -1, '2024-01-01 00:00:00', -1, '2024-01-01 00:00:00');

-- ----------------------------
-- Table data for bucket_biz_config
-- ----------------------------
INSERT INTO `bucket_biz_config` (`id`, `biz_key`, `bucket_name`, `remark`, `public_access`, `public_token_auth`, `encrypt`, `multi_tenant_ctrl`,
                                 `enabled_auth`, `allow_tenant_created`, `app_code`, `app_admin_code`, `cache_age`, `private0`)
                VALUES (15, 'applicationIcon', 'xcan-baseapp', '应用Icon', 1, 1, 0, 0, 0, 0, 'AngusGM', '', 86400, 1);
INSERT INTO `bucket_biz_config` (`id`, `biz_key`, `bucket_name`, `remark`, `public_access`, `public_token_auth`, `encrypt`, `multi_tenant_ctrl`,
                                 `enabled_auth`, `allow_tenant_created`, `app_code`, `app_admin_code`, `cache_age`, `private0`)
                VALUES (16, 'appearance', 'xcan-baseapp', '外观', 1, 1, 0, 0, 0, 0, 'AngusGM', '', 2592000, 1);
INSERT INTO `bucket_biz_config` (`id`, `biz_key`, `bucket_name`, `remark`, `public_access`, `public_token_auth`, `encrypt`, `multi_tenant_ctrl`,
                                 `enabled_auth`, `allow_tenant_created`, `app_code`, `app_admin_code`, `cache_age`, `private0`)
                VALUES (19, 'avatar', 'xcan-baseapp', '头像图片', 1, 1, 0, 0, 0, 0, 'AngusGM', '', 2592000, 1);
INSERT INTO `bucket_biz_config` (`id`, `biz_key`, `bucket_name`, `remark`, `public_access`, `public_token_auth`, `encrypt`, `multi_tenant_ctrl`,
                                 `enabled_auth`, `allow_tenant_created`, `app_code`, `app_admin_code`, `cache_age`, `private0`)
                VALUES (23, 'messageFiles', 'xcan-baseapp', '消息文件', 1, 1, 0, 0, 0, 0, 'AngusGM', '', 2592000, 1);
INSERT INTO `bucket_biz_config` (`id`, `biz_key`, `bucket_name`, `remark`, `public_access`, `public_token_auth`, `encrypt`, `multi_tenant_ctrl`,
                                 `enabled_auth`, `allow_tenant_created`, `app_code`, `app_admin_code`, `cache_age`, `private0`)
                VALUES (100, 'angusTesterProjectAvatar', 'xcan-angustester', 'AngusTester项目图标', 1, 1, 0, 1, 0, 1, 'AngusTester', 'ANGUSTESTER_ADMIN', 2592000, 0);
INSERT INTO `bucket_biz_config` (`id`, `biz_key`, `bucket_name`, `remark`, `public_access`, `public_token_auth`, `encrypt`, `multi_tenant_ctrl`,
                                 `enabled_auth`, `allow_tenant_created`, `app_code`, `app_admin_code`, `cache_age`, `private0`)
                VALUES (101, 'angusTesterDataFiles', 'xcan-angustester', 'AngusTester文件数据', 0, 0, 0, 1, 1, 1, 'AngusTester', 'ANGUSTESTER_ADMIN', 2592000, 1);
INSERT INTO `bucket_biz_config` (`id`, `biz_key`, `bucket_name`, `remark`, `public_access`, `public_token_auth`, `encrypt`, `multi_tenant_ctrl`,
                                 `enabled_auth`, `allow_tenant_created`, `app_code`, `app_admin_code`, `cache_age`, `private0`)
                VALUES (102, 'angusTesterTaskAttachments', 'xcan-angustester', 'AngusTester任务附件', 1, 1, 0, 1, 0, 1, 'AngusTester', 'ANGUSTESTER_ADMIN', 2592000, 1);
INSERT INTO `bucket_biz_config` (`id`, `biz_key`, `bucket_name`, `remark`, `public_access`, `public_token_auth`, `encrypt`, `multi_tenant_ctrl`,
                                 `enabled_auth`, `allow_tenant_created`, `app_code`, `app_admin_code`, `cache_age`, `private0`)
                VALUES (104, 'angusTesterCaseAttachments', 'xcan-angustester', 'AngusTester测试用例附件', 1, 1, 0, 1, 0, 1, 'AngusTester', 'ANGUSTESTER_ADMIN', 2592000, 1);

-- @formatter:on
