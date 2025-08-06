// Organization Types
export enum OrgTargetType {
  USER = 'USER',
  DEPT = 'DEPT',
  GROUP = 'GROUP'
}

export enum AuthOrgType {
  TENANT = 'TENANT',
  USER = 'USER',
  DEPT = 'DEPT',
  GROUP = 'GROUP'
}

// Group Types
export enum GroupSource {
  BACKGROUND_ADDED = 'BACKGROUND_ADDED',
  LDAP_SYNCHRONIZE = 'LDAP_SYNCHRONIZE'
}

// Application Types
export enum AppFuncType {
  MENU = 'MENU',
  BUTTON = 'BUTTON',
  PANEL = 'PANEL'
}

// Client Sources
export enum ClientSource {
  XCAN_TP_SIGNIN = 'XCAN_TP_SIGNIN',
  XCAN_OP_SIGNIN = 'XCAN_OP_SIGNIN',
  XCAN_SYS_INTROSPECT = 'XCAN_SYS_INTROSPECT',
  XCAN_SYS_TOKEN = 'XCAN_SYS_TOKEN',
  XCAN_2P_SIGNIN = 'XCAN_2P_SIGNIN'
}

// Policy Types
export enum PolicyType {
  PRE_DEFINED = 'PRE_DEFINED',
  USER_DEFINED = 'USER_DEFINED'
}

export enum PolicyGrantScope {
  TENANT_SYS_ADMIN = 'TENANT_SYS_ADMIN',
  TENANT_ALL_USER = 'TENANT_ALL_USER',
  TENANT_ORG = 'TENANT_ORG'
}

export enum PolicyGrantStage {
  MANUAL = 'MANUAL',
  SIGNUP_SUCCESS = 'SIGNUP_SUCCESS'
}

// Service Types
export enum ServiceSource {
  BACK_ADD = 'BACK_ADD',
  EUREKA = 'EUREKA'
}

export enum ApiProxyType {
  NO_PROXY = 'NO_PROXY',
  CLIENT_PROXY = 'CLIENT_PROXY',
  SERVER_PROXY = 'SERVER_PROXY',
  CLOUD_PROXY = 'CLOUD_PROXY'
}

// Check Settings
export enum SmokeCheckSetting {
  SERVICE_AVAILABLE = 'SERVICE_AVAILABLE',
  API_AVAILABLE = 'API_AVAILABLE',
  USER_DEFINED_ASSERTION = 'USER_DEFINED_ASSERTION'
}

export enum SecurityCheckSetting {
  NOT_SECURITY_CODE = 'NOT_SECURITY_CODE',
  IS_SECURITY_CODE = 'IS_SECURITY_CODE',
  USER_DEFINED_ASSERTION = 'USER_DEFINED_ASSERTION'
}

// Theme Types
export enum Area {
  NAVIGATION = 'NAVIGATION',
  CONTENT = 'CONTENT'
}

export enum ThemeType {
  DEFAULT = 'DEFAULT',
  CUSTOM = 'CUSTOM'
}

export enum ThemeCode {
  GRAY = 'GRAY',
  DARK = 'DARK'
}

// Status Types
export enum Status {
  ENABLED = 'ENABLED',
  NORMAL = 'NORMAL',
  NOT_EXPIRED = 'NOT_EXPIRED',
  EXPIRED = 'EXPIRED',
  DISABLED = 'DISABLED',
  LOCKED = 'LOCKED',
  DELETED = 'DELETED'
}

export enum Scope {
  GLOBAL_MANAGEMENT = 'GLOBAL_MANAGEMENT',
  APP = 'APP'
}

export enum EventPushStatus {
  PENDING = 'PENDING',
  PUSHING = 'PUSHING',
  PUSH_SUCCESS = 'PUSH_SUCCESS',
  PUSH_FAIL = 'PUSH_FAIL',
  IGNORED = 'IGNORED'
}

export enum MessageStatus {
  SENT = 'SENT',
  PENDING = 'PENDING',
  FAILURE = 'FAILURE'
}

export enum MessageReadTab {
  ALL = 'ALL',
  READ = 'READ',
  UNREAD = 'UNREAD'
}

export enum SentType {
  SEND_NOW = 'SEND_NOW',
  TIMING_SEND = 'TIMING_SEND'
}

export enum NoticeScope {
  GLOBAL = 'GLOBAL',
  APP = 'APP'
}

export enum MessageReceiveType {
  SITE = 'SITE',
  EMAIL = 'EMAIL'
}

// Operation Types
export enum OperationResourceType {
  TENANT = 'TENANT',
  DEPT = 'DEPT',
  GROUP = 'GROUP',
  USER = 'USER',
  USER_DIRECTORY = 'USER_DIRECTORY',
  USER_TOKEN = 'USER_TOKEN',
  ORG_TAG = 'ORG_TAG',
  AUTH_CLIENT = 'AUTH_CLIENT',
  AUTH_USER = 'AUTH_USER',
  SERVICE = 'SERVICE',
  API = 'API',
  APP = 'APP',
  APP_FUNC = 'APP_FUNC',
  WEB_TAG = 'WEB_TAG',
  POLICY = 'POLICY',
  POLICY_APP = 'POLICY_APP',
  POLICY_FUNC = 'POLICY_FUNC',
  POLICY_TENANT = 'POLICY_TENANT',
  POLICY_DEPT = 'POLICY_DEPT',
  POLICY_GROUP = 'POLICY_GROUP',
  POLICY_USER = 'POLICY_USER',
  EVENT = 'EVENT',
  EVENT_CHANNEL = 'EVENT_CHANNEL',
  EVENT_TEMPLATE = 'EVENT_TEMPLATE',
  EMAIL = 'EMAIL',
  EMAIL_SERVER = 'EMAIL_SERVER',
  EMAIL_TEMPLATE = 'EMAIL_TEMPLATE',
  SMS = 'SMS',
  SMS_CHANNEL = 'SMS_CHANNEL',
  SMS_TEMPLATE = 'SMS_TEMPLATE',
  TO_ROLE = 'TO_ROLE',
  TO_USER = 'TO_USER',
  MESSAGE = 'MESSAGE',
  NOTICE = 'NOTICE',
  SETTING = 'SETTING',
  SETTING_USER = 'SETTING_USER',
  SETTING_TENANT = 'SETTING_TENANT',
  SETTING_TENANT_QUOTA = 'SETTING_TENANT_QUOTA',
  SYSTEM_TOKEN = 'SYSTEM_TOKEN',
  OTHER = 'OTHER'
}

export enum ModifiedResourceType {
  SOCIAL = 'SOCIAL',
  AI_AGENT = 'AI_AGENT',
  API_LOG = 'API_LOG',
  OPERATION_LOG = 'OPERATION_LOG',
  SYSTEM_LOG = 'SYSTEM_LOG',
  MAX_RESOURCE_ACTIVITIES = 'MAX_RESOURCE_ACTIVITIES',
  MAX_METRICS_DAYS = 'MAX_METRICS_DAYS',
  TENANT_LOCALE = 'TENANT_LOCALE',
  TENANT_SECURITY = 'TENANT_SECURITY',
  TENANT_INVITATION_CODE = 'TENANT_INVITATION_CODE'
}

export enum DirectoryType {
  MicrosoftActiveDirectory = 'MicrosoftActiveDirectory',
  ApacheDS = 'ApacheDS',
  AppleOpenDirectory = 'AppleOpenDirectory',
  FedoraDS = 'FedoraDS',
  GenericLDAP = 'GenericLDAP',
  NovelleDirectory = 'NovelleDirectory',
  OpenDS = 'OpenDS',
  OpenLDAP = 'OpenLDAP',
  SunDirectory = 'SunDirectory'
}

export const enumNamespaceMap = new Map<any, string>([
  // Organization Types
  [OrgTargetType, 'enum.OrgTargetType'],
  [AuthOrgType, 'enum.AuthOrgType'],

  // Group Types
  [GroupSource, 'enum.GroupSource'],

  // Application Types
  [AppFuncType, 'enum.AppFuncType'],

  // Client Sources
  [ClientSource, 'enum.ClientSource'],

  // Policy Types
  [PolicyType, 'enum.PolicyType'],
  [PolicyGrantScope, 'enum.PolicyGrantScope'],
  [PolicyGrantStage, 'enum.PolicyGrantStage'],

  // Service Types
  [ServiceSource, 'enum.ServiceSource'],
  [ApiProxyType, 'enum.ApiProxyType'],

  // Check Settings
  [SmokeCheckSetting, 'enum.SmokeCheckSetting'],
  [SecurityCheckSetting, 'enum.SecurityCheckSetting'],

  // Theme Types
  [Area, 'enum.Area'],
  [ThemeType, 'enum.ThemeType'],
  [ThemeCode, 'enum.ThemeCode'],

  // Status Types
  [Status, 'enum.Status'],
  [Scope, 'enum.Scope'],

  // Notification Types
  [EventPushStatus, 'enum.EventPushStatus'],
  [MessageStatus, 'enum.MessageStatus'],
  [MessageReadTab, 'enum.MessageReadTab'],
  [SentType, 'enum.SentType'],
  [NoticeScope, 'enum.NoticeScope'],
  [MessageReceiveType, 'enum.MessageReceiveType'],

  // Operation Types
  [OperationResourceType, 'enum.OperationResourceType'],
  [ModifiedResourceType, 'enum.ModifiedResourceType'],

  [DirectoryType, 'enum.DirectoryType']
]);
