import type {
  TableColumn, PaginationParams, PaginationObject, ProtocolOption, FormRules, FormState,
  AddServerRequest, ReplaceServerRequest, UpdateServerRequest, DeleteServerRequest, TestServerConfigRequest
} from './types';

/**
 * Create protocol options for select dropdown
 */
export const createProtocolOptions = (): ProtocolOption[] => [
  { label: 'IMAP', value: 'IMAP' },
  { label: 'POP', value: 'POP3' },
  { label: 'SMTP', value: 'SMTP' }
];

/**
 * Create form validation rules
 */
export const createFormRules = (t: (key: string) => string): FormRules => ({
  name: { required: true, message: t('email.messages.addNameTip') },
  host: { required: true, message: t('email.messages.rule2') },
  port: { required: true, message: t('email.messages.rulePort') },
  authAccount: { required: true, message: t('email.messages.userRule11') },
  authPassword: { required: true, message: t('email.messages.userRule0') }
});

/**
 * Create initial pagination parameters
 */
export const createInitialPaginationParams = (): PaginationParams => ({
  pageNo: 1,
  pageSize: 10,
  filters: []
});

/**
 * Create pagination object for table component
 */
export const createPaginationObject = (params: PaginationParams, total: number): PaginationObject => ({
  current: params.pageNo,
  pageSize: params.pageSize,
  total: total
});

/**
 * Create initial form state
 */
export const createInitialFormState = (): FormState => ({
  name: '',
  protocol: 'SMTP',
  remark: '',
  enabled: false,
  host: '',
  port: '',
  startTlsEnabled: false,
  sslEnabled: false,
  authEnabled: false,
  authAccount: {
    account: '',
    password: ''
  },
  subjectPrefix: ''
});

/**
 * Create add server request parameters
 */
export const createAddServerRequest = (formState: FormState): AddServerRequest => {
  const { authAccount, ...others } = formState;
  return formState.authEnabled ? { authAccount, ...others } : others;
};

/**
 * Create replace server request parameters
 */
export const createReplaceServerRequest = (formState: FormState, id: string): ReplaceServerRequest => {
  const { authAccount, ...others } = formState;
  const baseParams = formState.authEnabled ? { authAccount, ...others } : others;
  return { ...baseParams, id };
};

/**
 * Create update server request parameters
 */
export const createUpdateServerRequest = (id: string, enabled: boolean): UpdateServerRequest => ({
  id,
  enabled: !enabled
});

/**
 * Create delete server request parameters
 */
export const createDeleteServerRequest = (id: string): DeleteServerRequest => ({
  ids: [id]
});

/**
 * Create test server config request parameters
 */
export const createTestServerConfigRequest = (serverId: string, emailAddress: string): TestServerConfigRequest => ({
  serverId,
  toAddress: [emailAddress.trim()]
});

/**
 * Update search filters
 */
export const updateSearchFilters = (
  params: PaginationParams,
  filters: any[]
): void => {
  params.filters = filters;
};

/**
 * Check if form has changes by comparing with original state
 */
export const hasFormChanges = (currentState: FormState, originalState: FormState | undefined): boolean => {
  if (!originalState) return true;

  // Deep comparison of form states
  return JSON.stringify(currentState) !== JSON.stringify(originalState);
};

/**
 * Validate email address format
 */
export const isValidEmailAddress = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email.trim());
};

/**
 * Format protocol display name
 */
export const formatProtocolDisplay = (protocol: any): string => {
  return protocol?.message || protocol || '';
};

/**
 * Get default status display text
 */
export const getDefaultStatusText = (enabled: boolean, t: (key: string) => string): string => {
  return enabled ? `(${t('email.messages.default')})` : '';
};

/**
 * Check if server can be set as default
 */
export const canSetAsDefault = (enabled: boolean): boolean => {
  return !enabled; // Can only set as default if not already enabled
};

/**
 * Check if server can be edited
 */
export const canEditServer = (record: any): boolean => {
  return !!(record && record.id);
};

/**
 * Check if server can be deleted
 */
export const canDeleteServer = (record: any): boolean => {
  return !!(record && record.id);
};

/**
 * Check if server can be tested
 */
export const canTestServer = (record: any): boolean => {
  return !!(record && record.id);
};

/**
 * Process server list response data
 */
export const processServerListResponse = (response: any): { list: any[]; total: number } => {
  const { data = { list: [], total: 0 } } = response;
  return {
    list: data.list || [],
    total: +data.total || 0
  };
};

/**
 * Remove server from local state
 */
export const removeServerFromState = (serverList: any[], id: string): any[] => {
  return serverList.filter(service => service.id !== id);
};

/**
 * Set only one server as default
 */
export const setSingleDefaultServer = (serverList: any[], defaultId: string): any[] => {
  return serverList.map(service => ({
    ...service,
    enabled: service.id === defaultId
  }));
};

/**
 * Get modal configuration constants
 */
export const getModalConfig = () => ({
  width: '540px',
  maxEmailLength: 100
});

/**
 * Format form data for display
 */
export const formatFormDataForDisplay = (data: any): FormState => {
  return {
    ...data,
    authAccount: data.authAccount || {
      account: '',
      password: ''
    }
  };
};

/**
 * Check if authentication is required for the form
 */
export const isAuthenticationRequired = (formState: FormState): boolean => {
  return formState.authEnabled === true;
};

/**
 * Get authentication field validation state
 */
export const getAuthFieldValidation = (authEnabled: boolean, fieldValue: string): boolean => {
  return authEnabled && !fieldValue?.trim();
};

/**
 * Create table columns configuration for mailbox services table
 */
export const createTableColumns = (t: (key: string) => string): TableColumn[] => [
  {
    title: t('email.columns.id'),
    dataIndex: 'id',
    key: 'id',
    width: '16%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('email.columns.name'),
    dataIndex: 'name',
    key: 'name',
    width: '24%'
  },
  {
    title: t('email.columns.protocol'),
    dataIndex: 'protocol',
    key: 'protocol',
    width: '12%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('email.columns.sendAddress'),
    dataIndex: 'host',
    key: 'host',
    width: '20%'
  },
  {
    title: t('email.columns.prefix'),
    dataIndex: 'subjectPrefix',
    key: 'subjectPrefix',
    width: '20%'
  },
  {
    title: t('email.columns.operation'),
    dataIndex: 'action',
    key: 'action',
    align: 'center',
    width: '8%'
  }
];
