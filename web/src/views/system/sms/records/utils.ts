import type { TableColumn, GridColumn, SearchOption, SmsSendStatus, PaginationConfig } from './types';

/**
 * Get color for SMS send status badge
 * @param value - SMS send status
 * @returns Color string for badge styling
 */
export const getSendStatusColor = (value: SmsSendStatus): string => {
  switch (value) {
    case 'SUCCESS': // Success
      return 'rgba(82,196,26,1)';
    case 'PENDING': // Pending
      return 'rgba(255,165,43,1)';
    case 'FAILURE': // Failure
      return 'rgba(245,34,45,1)';
    default:
      return 'rgba(128,128,128,1)'; // Default gray
  }
};

/**
 * Create pagination configuration object
 * @param current - Current page number
 * @param pageSize - Page size
 * @param total - Total number of records
 * @returns Pagination configuration object
 */
export const createPaginationConfig = (
  current: number,
  pageSize: number,
  total: number
): PaginationConfig => ({
  current,
  pageSize,
  total
});

/**
 * Parse third-party output parameters
 * @param thirdOutputParam - Raw third-party output parameters
 * @returns Parsed parameters object
 */
export const parseThirdPartyOutputParams = (thirdOutputParam: any): any => {
  if (!thirdOutputParam) return null;

  try {
    // Remove escape characters and parse JSON
    const cleanedParam = thirdOutputParam.replace(/\\/g, '');
    return JSON.parse(cleanedParam);
  } catch (error) {
    console.error('Failed to parse third-party output params:', error);
    return thirdOutputParam;
  }
};

/**
 * Format JSON for display
 * @param data - Data to format
 * @returns Formatted JSON string
 */
export const formatJsonForDisplay = (data: any): string => {
  if (!data) return '';

  try {
    return JSON.stringify(data, null, 2);
  } catch (error) {
    console.error('Failed to format JSON:', error);
    return String(data);
  }
};

/**
 * Create table columns configuration for SMS records
 * Defines the structure and display properties for the SMS records table
 * @param t - i18n translation function
 * @returns Array of table column configurations
 */
export const createSmsRecordsColumns = (t: (key: string) => string): TableColumn[] => [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: '10%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('sms.columns.sendStatus'),
    dataIndex: 'sendStatus',
    key: 'sendStatus',
    width: '8%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('sms.columns.sendUserId'),
    dataIndex: 'sendUserId',
    key: 'sendUserId',
    width: '10%',
    customRender: ({ text }): string => text && text !== '-1' ? text : '--',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('sms.columns.templateCode'),
    dataIndex: 'templateCode',
    key: 'templateCode',
    width: '20%',
    customRender: ({ text }): string => text || '--'
  },
  {
    title: t('sms.columns.urgent'),
    dataIndex: 'urgent',
    key: 'urgent',
    width: '7%',
    customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
  },
  {
    title: t('sms.columns.verificationCode'),
    dataIndex: 'verificationCode',
    key: 'verificationCode',
    width: '7%',
    customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
  },
  {
    title: t('sms.columns.batch'),
    dataIndex: 'batch',
    key: 'batch',
    width: '7%',
    customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
  },
  {
    title: t('sms.columns.actualSendDate'),
    dataIndex: 'actualSendDate',
    key: 'actualSendDate',
    sorter: true,
    width: '10%',
    customRender: ({ text }): string => text || '--'
  },
  {
    title: t('sms.columns.expectedSendDate'),
    dataIndex: 'expectedSendDate',
    key: 'expectedSendDate',
    sorter: true,
    width: '10%',
    customRender: ({ text }): string => text || '--'
  }
];

/**
 * Create grid columns configuration for SMS record detail
 * Defines the structure and display properties for the detail grid
 * @param t - i18n translation function
 * @returns Array of grid column configurations
 */
export const createSmsRecordDetailColumns = (t: (key: string) => string): GridColumn[][] => [
  [
    {
      label: 'ID',
      dataIndex: 'id'
    },
    {
      label: t('sms.columns.sendStatus'),
      dataIndex: 'sendStatus'
    },
    {
      label: t('sms.columns.sendTenantId'),
      dataIndex: 'sendTenantId'
    },
    {
      label: t('sms.columns.sendUserId'),
      dataIndex: 'sendId'
    },
    {
      label: t('sms.columns.templateCode'),
      dataIndex: 'templateCode'
    },
    {
      label: t('sms.columns.urgent'),
      dataIndex: 'urgent',
      customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
    },
    {
      label: t('sms.columns.verificationCode'),
      dataIndex: 'verificationCode',
      customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
    },
    {
      label: t('sms.columns.failureReason'),
      dataIndex: 'failureReason'
    }
  ],
  [
    {
      label: t('sms.columns.actualSendDate'),
      dataIndex: 'actualSendDate'
    },
    {
      label: t('sms.columns.expectedSendDate'),
      dataIndex: 'expectedSendDate'
    },
    {
      label: t('sms.columns.outId'),
      dataIndex: 'outId'
    },
    {
      label: t('sms.columns.bizKey'),
      dataIndex: 'bizKey'
    },
    {
      label: t('sms.columns.language'),
      dataIndex: 'language',
      customRender: ({ text }): string => text?.message || '--'
    },
    {
      label: t('sms.columns.batch'),
      dataIndex: 'batch',
      customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
    }
  ]
];

/**
 * Create search options configuration for SMS records
 * Defines the search and filter options for the records list
 * @param t - i18n translation function
 * @param ProcessStatus - Process status enum from infrastructure
 * @returns Array of search option configurations
 */
export const createSmsRecordsSearchOptions = (
  t: (key: string) => string,
  ProcessStatus: any
): SearchOption[] => [
  {
    valueKey: 'sendUserId',
    type: 'select-user',
    allowClear: true,
    placeholder: t('sms.placeholder.selectSendUser'),
    axiosConfig: { headers: { 'XC-Opt-Tenant-Id': '' } }
  },
  {
    valueKey: 'sendStatus',
    type: 'select-enum',
    enumKey: ProcessStatus,
    placeholder: t('sms.placeholder.selectSendStatus'),
    allowClear: true
  },
  {
    valueKey: 'templateCode',
    type: 'input',
    placeholder: t('sms.placeholder.queryTemplateCode'),
    allowClear: true
  },
  {
    valueKey: 'urgent',
    type: 'select',
    options: [{ value: true, label: t('common.status.yes') }, { value: false, label: t('common.status.no') }],
    placeholder: t('sms.placeholder.isUrgent'),
    allowClear: true
  },
  {
    valueKey: 'verificationCode',
    type: 'select',
    options: [{ value: true, label: t('common.status.yes') }, { value: false, label: t('common.status.no') }],
    placeholder: t('sms.placeholder.isVerificationCode'),
    allowClear: true
  },
  {
    valueKey: 'batch',
    type: 'select',
    options: [{ value: true, label: t('common.status.yes') }, { value: false, label: t('common.status.no') }],
    placeholder: t('sms.placeholder.isBatch'),
    allowClear: true
  },
  {
    valueKey: 'actualSendDate',
    type: 'date-range',
    placeholder: [t('sms.placeholder.sendTimeRange'), t('sms.placeholder.sendTimeRange')],
    allowClear: true
  },
  {
    valueKey: 'expectedSendDate',
    type: 'date-range',
    placeholder: [t('sms.placeholder.expectedTimeRange'), t('sms.placeholder.expectedTimeRange')],
    allowClear: true
  }
];
