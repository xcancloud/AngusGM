/**
 * Utility functions for LDAP module
 */

import type {
  TableColumn,
  FormLayoutConfig,
  FormValidationRules,
  ValidationRule,
  ServerConfig,
  LdapBaseConfig,
  UserSchemaConfig,
  GroupSchemaConfig,
  MembershipConfig,
  TestResultData,
  LdapDirectoryConfig,
  LdapMainState,
  LdapDetailState,
  FormComponentState,
  AlertTypeMapping,
  FormFieldConfig,
  ComponentConfig,
  DirectoryType,
  TestResultStatus,
  ValidationResult
} from './types';
import { DirectoryType } from '@/enums/enums';

/**
 * Create table columns configuration for LDAP directory list
 */
export const createTableColumns = (t: (key: string) => string): TableColumn[] => [
  {
    title: t('ldap.columns.id'),
    dataIndex: 'id',
    width: '12%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('ldap.columns.name'),
    dataIndex: 'name',
    width: '10%'
  },
  {
    title: t('ldap.columns.host'),
    dataIndex: 'host',
    key: 'host',
    width: '10%'
  },
  {
    title: t('ldap.columns.port'),
    dataIndex: 'port',
    key: 'port',
    width: '5%'
  },
  {
    title: t('ldap.columns.username'),
    dataIndex: 'username',
    key: 'username'
  },
  {
    title: t('ldap.columns.status'),
    dataIndex: 'enabled',
    key: 'enabled',
    width: '5%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('ldap.columns.creator'),
    dataIndex: 'creator',
    key: 'creator',
    width: '7%'
  },
  {
    title: t('ldap.columns.createdDate'),
    dataIndex: 'createdDate',
    key: 'createdDate',
    width: '12%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('ldap.columns.operation'),
    dataIndex: 'operation',
    width: '14%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  }
];

/**
 * Create form layout configuration
 */
export const createFormLayoutConfig = (span = 9): FormLayoutConfig => ({
  span
});

/**
 * Create form validation rules for server configuration
 */
export const createServerValidationRules = (t: (key: string) => string): FormValidationRules => ({
  name: [{ required: true, message: t('ldap.info-1') }],
  directoryType: [{ required: true, message: t('请选择目录类型') }],
  username: [{ required: true, message: t('ldap.info-3') }],
  port: [{ required: true, message: t('ldap.info-4') }],
  password: [{ required: true, message: t('ldap.info-5') }],
  host: [{ required: true, message: t('ldap.info-6') }]
});

/**
 * Create form validation rules for LDAP base configuration
 */
export const createLdapBaseValidationRules = (t: (key: string) => string): FormValidationRules => ({
  baseDn: [{ required: true, message: t('ldap.model-1') }]
});

/**
 * Create form validation rules for user schema configuration
 */
export const createUserSchemaValidationRules = (t: (key: string) => string): FormValidationRules => ({
  objectClass: [{ required: true, message: t('ldap.user-1') }],
  objectFilter: [{ required: true, message: t('ldap.user-2') }],
  userIdAttribute: [{ required: true, message: t('ldap.user-3') }],
  passwordAttribute: [{ required: true, message: t('ldap.user-8') }],
  displayNameAttribute: [{ required: true, message: t('ldap.validation.displayNameAttributeRequired') }],
  firstNameAttribute: [{ required: true, message: t('ldap.validation.userFirstNameAttributeRequired') }],
  lastNameAttribute: [{ required: true, message: t('ldap.validation.userLastNameAttributeRequired') }],
  usernameAttribute: [{ required: true, message: t('ldap.validation.userUidAttributeRequired') }],
  emailAttribute: [{ required: true, message: t('ldap.validation.userEmailAttributeRequired') }]
});

/**
 * Create form validation rules for group schema configuration
 */
export const createGroupSchemaValidationRules = (t: (key: string) => string): FormValidationRules => ({
  objectFilter: [{ required: false, message: t('ldap.config-2') }],
  nameAttribute: [{ required: false, message: t('ldap.config-3') }],
  objectClass: [{ required: false, message: t('ldap.validation.groupObjectClassRequired') }]
});

/**
 * Create form validation rules for membership configuration
 */
export const createMembershipValidationRules = (t: (key: string) => string): FormValidationRules => ({
  groupMemberAttribute: [{ required: false, message: t('ldap.member-label-1') }],
  memberGroupAttribute: [{ required: false, message: t('ldap.member-label-2') }]
});

/**
 * Create initial server configuration
 */
export const createInitialServerConfig = (): ServerConfig => ({
  directoryType: DirectoryType.OpenLDAP,
  host: '',
  name: '',
  password: '',
  port: '',
  ssl: false,
  username: ''
});

/**
 * Create initial LDAP base configuration
 */
export const createInitialLdapBaseConfig = (): LdapBaseConfig => ({
  baseDn: '',
  additionalUserDn: '',
  additionalGroupDn: ''
});

/**
 * Create initial user schema configuration
 */
export const createInitialUserSchemaConfig = (): UserSchemaConfig => ({
  firstNameAttribute: '',
  lastNameAttribute: '',
  displayNameAttribute: '',
  emailAttribute: '',
  mobileAttribute: '',
  userIdAttribute: '',
  objectClass: '',
  objectFilter: '',
  passwordAttribute: '',
  usernameAttribute: '',
  ignoreSameIdentityUser: true
});

/**
 * Create initial group schema configuration
 */
export const createInitialGroupSchemaConfig = (): GroupSchemaConfig => ({
  descriptionAttribute: '',
  objectFilter: '',
  nameAttribute: '',
  objectClass: '',
  ignoreSameNameGroup: true
});

/**
 * Create initial membership configuration
 */
export const createInitialMembershipConfig = (): MembershipConfig => ({
  groupMemberAttribute: '',
  memberGroupAttribute: ''
});

/**
 * Create initial LDAP main state
 */
export const createInitialLdapMainState = (): LdapMainState => ({
  dataSource: [],
  listSpin: false,
  testVisible: false,
  testLoading: false,
  syncLoading: [],
  testRecord: { server: {} },
  showData: {} as TestResultData,
  connectMsg: '',
  userMsg: '',
  groupMsg: '',
  memberMsg: ''
});

/**
 * Create initial LDAP detail state
 */
export const createInitialLdapDetailState = (): LdapDetailState => ({
  activeKey: 0,
  groupMemberMode: undefined,
  groupMode: undefined,
  mode: undefined,
  server: undefined,
  userMode: undefined,
  childrenData: {
    server: null,
    groupSchema: null,
    membershipSchema: null,
    schema: null,
    userSchema: null
  },
  errorStop: true,
  saveLoading: false,
  testLoading: false,
  submitType: 'save',
  editShow: {},
  rulesCount: 5,
  submitRef: undefined
});

/**
 * Create initial form component state
 */
export const createInitialFormComponentState = (): FormComponentState => ({
  formRef: undefined,
  form: {},
  rules: {}
});

/**
 * Create alert type mapping
 */
export const createAlertTypeMapping = (): AlertTypeMapping => ({
  success: 'success',
  error: 'error',
  info: 'info'
});

/**
 * Get alert type based on success flag
 */
export const getAlertType = (flag: TestResultStatus): string => {
  if (flag === true) {
    return 'success';
  }
  if (flag === false) {
    return 'error';
  }
  return 'info';
};

/**
 * Process LDAP directory list response
 */
export const processLdapDirectoriesResponse = (response: any): LdapDirectoryConfig[] => {
  const resp = response.data;
  return resp.filter((item: { server: any }) => item.server !== null)
    .map(item => ({
      ...item,
      ...item.server,
      id: item.id,
      enabled: item.enabled
    }));
};

/**
 * Process LDAP directory detail response
 */
export const processLdapDirectoryDetailResponse = (response: any): LdapDirectoryConfig => {
  return response.data;
};

/**
 * Process LDAP test response
 */
export const processLdapTestResponse = (response: any): TestResultData => {
  return response.data;
};

/**
 * Create test record from directory record
 */
export const createTestRecord = (record: LdapDirectoryConfig): any => {
  const testRecord = JSON.parse(JSON.stringify(record));
  testRecord.server.password = '';
  return testRecord;
};

/**
 * Update form validation rules based on form data
 */
export const updateGroupSchemaValidationRules = (
  rules: FormValidationRules,
  form: GroupSchemaConfig
): void => {
  const isNull = Object.keys(form).every((key) =>
    (key !== 'ignoreSameNameGroup' && form[key as keyof GroupSchemaConfig] === '') ||
    key === 'ignoreSameNameGroup'
  );

  rules.objectFilter![0].required = !isNull;
  rules.nameAttribute![0].required = !isNull;
  rules.objectClass![0].required = !isNull;
};

/**
 * Update form validation rules based on form data
 */
export const updateMembershipValidationRules = (
  rules: FormValidationRules,
  form: MembershipConfig
): void => {
  const isNull = Object.keys(form).every((key) =>
    form[key as keyof MembershipConfig] === ''
  );

  rules.groupMemberAttribute![0].required = !isNull;
  rules.memberGroupAttribute![0].required = !isNull;
};

/**
 * Format test result message for user sync
 */
export const formatUserSyncMessage = (
  data: TestResultData,
  t: (key: string, params?: any) => string
): string => {
  if (data.userSuccess === null) {
    return t('ldap.messages.notExecuted');
  }

  if (data.userSuccess) {
    return t('ldap.messages.totalCount', { count: data.totalUserNum }) + '，' +
           t('ldap.messages.newCount', { count: data.addUserNum }) + '，' +
           t('ldap.messages.updateCount', { count: data.updateUserNum }) + '，' +
           t('ldap.messages.deleteCount', { count: data.deleteUserNum }) + '，' +
           t('ldap.messages.ignoreCount', { count: data.ignoreUserNum }) + '。';
  }

  return data.errorMessage || '';
};

/**
 * Format test result message for group sync
 */
export const formatGroupSyncMessage = (
  data: TestResultData,
  t: (key: string, params?: any) => string
): string => {
  if (data.groupSuccess === null) {
    return t('ldap.messages.notExecuted');
  }

  if (data.groupSuccess) {
    return t('ldap.messages.totalCount', { count: data.totalGroupNum }) + '，' +
           t('ldap.messages.newCount', { count: data.addGroupNum }) + '，' +
           t('ldap.messages.updateCount', { count: data.updateGroupNum }) + '，' +
           t('ldap.messages.deleteCount', { count: data.deleteGroupNum }) + '，' +
           t('ldap.messages.ignoreCount', { count: data.ignoreGroupNum }) + '。';
  }

  return data.errorMessage || '';
};

/**
 * Format test result message for membership sync
 */
export const formatMembershipSyncMessage = (
  data: TestResultData,
  t: (key: string, params?: any) => string
): string => {
  if (data.membershipSuccess === null) {
    return t('ldap.messages.notExecuted');
  }

  if (data.membershipSuccess) {
    return t('ldap.messages.newCount', { count: data.addMembershipNum }) + '，' +
           t('ldap.messages.deleteCount', { count: data.deleteMembershipNum }) + '。';
  }

  return data.errorMessage || '';
};

/**
 * Format connection test message
 */
export const formatConnectionMessage = (
  data: TestResultData,
  t: (key: string) => string
): string => {
  if (data.connectSuccess === true) {
    return t('ldap.messages.connectSuccess');
  }
  if (data.connectSuccess === false) {
    return t('ldap.messages.connectFailed') + ': ' + (data.errorMessage || '');
  }
  return t('ldap.messages.connectServer') + ': ' + t('ldap.messages.notExecuted');
};

/**
 * Check if directory can be moved up
 */
export const canMoveUp = (index: number): boolean => {
  return index > 0;
};

/**
 * Check if directory can be moved down
 */
export const canMoveDown = (index: number, totalCount: number): boolean => {
  return index + 1 < totalCount;
};

/**
 * Check if directory can be deleted
 */
export const canDeleteDirectory = (app: any): boolean => {
  return app.has('LdapDelete');
};

/**
 * Check if directory can be tested
 */
export const canTestDirectory = (app: any): boolean => {
  return app.has('LdapTest');
};

/**
 * Check if directory can be set as default
 */
export const canSetDefaultDirectory = (app: any): boolean => {
  return app.has('LdapSetDefault');
};

/**
 * Check if directory can be modified
 */
export const canModifyDirectory = (app: any): boolean => {
  return app.has('LdapModify');
};

/**
 * Check if directory can be synced
 */
export const canSyncDirectory = (app: any): boolean => {
  return app.has('LdapSyncData');
};

/**
 * Check if directory can be added
 */
export const canAddDirectory = (app: any): boolean => {
  return app.has('LdapAdd');
};

/**
 * Validate form data before submission
 */
export const validateFormData = (formData: any): boolean => {
  return Object.keys(formData).every(key => formData[key] !== null);
};

/**
 * Clean up form data for submission
 */
export const cleanFormDataForSubmission = (formData: any): any => {
  const cleanedData = { ...formData };

  // Remove unnecessary group keys if they are empty
  Object.keys(cleanedData).forEach(key => {
    if (['groupSchema', 'membershipSchema'].includes(key)) {
      const isNull = Object.keys(cleanedData[key]).every(ckey =>
        cleanedData[key][ckey] === '' || ckey === 'ignoreSameNameGroup'
      );
      if (isNull) {
        delete cleanedData[key];
      }
    }
  });

  return cleanedData;
};

/**
 * Create component configuration for form components
 */
export const createComponentConfig = (
  key: string,
  index: number,
  query?: any,
  className?: string
): ComponentConfig => ({
  key,
  index,
  query,
  class: className
});

/**
 * Get form field configuration for server info
 */
export const getServerInfoFieldConfigs = (t: (key: string) => string): FormFieldConfig[] => [
  {
    label: t('ldap.info-label-1'),
    name: 'name',
    placeholder: t('ldap.info-1'),
    maxlength: 100,
    required: true
  },
  {
    label: t('ldap.info-label-2'),
    name: 'directoryType',
    required: true
  },
  {
    label: t('ldap.info-label-3'),
    name: 'host',
    placeholder: t('ldap.info-6'),
    maxlength: 200,
    required: true
  },
  {
    label: t('ldap.info-label-4'),
    name: 'port',
    placeholder: t('ldap.info-4'),
    required: true
  },
  {
    label: t('ldap.info-label-5'),
    name: 'username',
    placeholder: t('ldap.info-3'),
    maxlength: 200,
    required: true
  },
  {
    label: t('ldap.info-label-6'),
    name: 'password',
    placeholder: t('ldap.info-5'),
    maxlength: 400,
    required: true
  }
];

/**
 * Get form field configuration for LDAP base config
 */
export const getLdapBaseFieldConfigs = (t: (key: string) => string): FormFieldConfig[] => [
  {
    label: t('ldap.model-label-1'),
    name: 'baseDn',
    placeholder: t('ldap.model-1'),
    maxlength: 400,
    required: true
  },
  {
    label: t('ldap.model-label-2'),
    name: 'additionalUserDn',
    placeholder: t('ldap.model-2'),
    maxlength: 400
  },
  {
    label: t('ldap.model-label-3'),
    name: 'additionalGroupDn',
    placeholder: t('ldap.model-3'),
    maxlength: 400
  }
];

/**
 * Get form field configuration for user schema
 */
export const getUserSchemaFieldConfigs = (t: (key: string) => string): FormFieldConfig[] => [
  {
    label: t('ldap.user-label-1'),
    name: 'objectClass',
    placeholder: t('ldap.user-1'),
    maxlength: 400,
    required: true
  },
  {
    label: t('ldap.user-label-2'),
    name: 'objectFilter',
    placeholder: t('ldap.user-2'),
    maxlength: 400,
    required: true
  },
  {
    label: t('ldap.user-label-3'),
    name: 'usernameAttribute',
    placeholder: t('ldap.user-3'),
    maxlength: 160,
    required: true
  },
  {
    label: t('ldap.user-label-4'),
    name: 'firstNameAttribute',
    placeholder: t('ldap.user-4'),
    maxlength: 160,
    required: true
  },
  {
    label: t('ldap.user-label-5'),
    name: 'lastNameAttribute',
    placeholder: t('ldap.user-5'),
    maxlength: 160,
    required: true
  },
  {
    label: t('ldap.labels.displayName'),
    name: 'displayNameAttribute',
    placeholder: t('ldap.user-5'),
    maxlength: 160,
    required: true
  },
  {
    label: t('ldap.user-label-6'),
    name: 'mobileAttribute',
    placeholder: t('ldap.user-6'),
    maxlength: 160
  },
  {
    label: t('ldap.user-label-7'),
    name: 'emailAttribute',
    placeholder: t('ldap.user-7'),
    maxlength: 160,
    required: true
  },
  {
    label: t('ldap.user-label-8'),
    name: 'passwordAttribute',
    placeholder: t('ldap.user-8'),
    maxlength: 160,
    required: true
  }
];

/**
 * Get form field configuration for group schema
 */
export const getGroupSchemaFieldConfigs = (t: (key: string) => string): FormFieldConfig[] => [
  {
    label: t('ldap.config-5'),
    name: 'objectClass',
    placeholder: t('ldap.config-1'),
    maxlength: 400
  },
  {
    label: t('ldap.config-6'),
    name: 'objectFilter',
    placeholder: t('ldap.config-2'),
    maxlength: 400
  },
  {
    label: t('ldap.config-7'),
    name: 'nameAttribute',
    placeholder: t('ldap.config-3'),
    maxlength: 160
  },
  {
    label: t('ldap.config-8'),
    name: 'descriptionAttribute',
    placeholder: t('ldap.config-4'),
    maxlength: 160
  }
];

/**
 * Get form field configuration for membership
 */
export const getMembershipFieldConfigs = (t: (key: string) => string): FormFieldConfig[] => [
  {
    label: t('ldap.member-label-1'),
    name: 'groupMemberAttribute',
    placeholder: t('ldap.member-1'),
    maxlength: 160
  },
  {
    label: t('ldap.member-label-2'),
    name: 'memberGroupAttribute',
    placeholder: t('ldap.member-2'),
    maxlength: 160
  }
];
