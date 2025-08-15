import type {
  TableColumn, GridColumn, SearchOption, PaginationParams, PaginationObject,
  EmailRecordsState, DetailState, StatusColorMapping, EmailRecord, EmailSendStatus
} from './types';
import { ProcessStatus } from '@xcan-angus/infra';

/**
 * Create status color mapping for email send statuses
 */
export const createStatusColorMapping = (): StatusColorMapping => ({
  SUCCESS: 'rgba(82,196,26,1)', // Green for success
  PENDING: 'rgba(255,165,43,1)', // Orange for pending
  FAILURE: 'rgba(245,34,45,1)' // Red for failure
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
 * Create initial email records state
 */
export const createInitialEmailRecordsState = (): EmailRecordsState => ({
  emailList: [],
  showCount: true,
  loading: false,
  total: 0,
  params: createInitialPaginationParams()
});

/**
 * Create initial detail state
 */
export const createInitialDetailState = (): DetailState => ({
  firstLoad: true,
  emailRecordInfo: undefined
});

/**
 * Update pagination parameters from table change
 */
export const updatePaginationParams = (
  params: PaginationParams,
  pagination: { current: number; pageSize: number },
  sorter?: { orderBy?: string; orderSort?: string }
): void => {
  params.pageNo = pagination.current;
  params.pageSize = pagination.pageSize;

  if (sorter) {
    params.orderBy = sorter.orderBy;
    params.orderSort = sorter.orderSort;
  }
};

/**
 * Reset pagination to first page
 */
export const resetPagination = (params: PaginationParams): void => {
  params.pageNo = 1;
};

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
 * Get status color based on send status value
 */
export const getSendStatusColor = (
  value: EmailSendStatus,
  statusColors: StatusColorMapping
): string => {
  return statusColors[value] || statusColors.FAILURE;
};

/**
 * Process email list response data
 */
export const processEmailListResponse = (response: any): { list: EmailRecord[]; total: number } => {
  const { data = { list: [], total: 0 } } = response;
  return {
    list: data.list || [],
    total: +data.total || 0
  };
};

/**
 * Check if email record can be viewed in detail
 */
export const canViewEmailDetail = (record: EmailRecord): boolean => {
  return !!(record && record.id);
};

/**
 * Format template parameters for display
 */
export const formatTemplateParams = (params: Record<string, any>): string => {
  if (!params || typeof params !== 'object') return '';
  return JSON.stringify(params, null, 2);
};

/**
 * Check if email has attachments
 */
export const hasAttachments = (attachments: any): boolean => {
  return Array.isArray(attachments) && attachments.length > 0;
};

/**
 * Get attachment display name
 */
export const getAttachmentDisplayName = (attachment: any): string => {
  return attachment?.name || 'Unknown';
};

/**
 * Get attachment download URL
 */
export const getAttachmentDownloadUrl = (attachment: any, accessToken: string | undefined): string => {
  if (!attachment?.url || !accessToken) return '#';
  return `${attachment.url}&access_token=${accessToken}`;
};

/**
 * Create table columns configuration for email records table
 */
export const createTableColumns = (t: (key: string) => string): TableColumn[] => [
  {
    title: t('email.columns.id'),
    dataIndex: 'id',
    key: 'id',
    width: '9%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('email.columns.subject'),
    dataIndex: 'subject',
    key: 'subject',
    width: '15%'
  },
  {
    title: t('email.columns.sendStatus'),
    dataIndex: 'sendStatus',
    key: 'sendStatus',
    width: '7%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('email.columns.sendUserId'),
    dataIndex: 'sendId',
    key: 'sendId',
    width: '9%',
    customRender: ({ text }): string => text && text !== '-1' ? text : '--',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('email.columns.templateCode'),
    dataIndex: 'templateCode',
    key: 'templateCode',
    width: '15%',
    customRender: ({ text }): string => text || '--'
  },
  {
    title: t('email.columns.urgent'),
    dataIndex: 'urgent',
    key: 'urgent',
    width: '6%',
    customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
  },
  {
    title: t('email.columns.verificationCode'),
    dataIndex: 'verificationCode',
    key: 'verificationCode',
    width: '6%',
    customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
  },
  {
    title: t('email.columns.batch'),
    dataIndex: 'batch',
    key: 'batch',
    width: '6%',
    customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
  },
  {
    title: t('email.columns.sendTime'),
    dataIndex: 'actualSendDate',
    key: 'actualSendDate',
    sorter: true,
    width: '9%',
    customRender: ({ text }): string => text || '--'
  },
  {
    title: t('email.columns.expectedTime'),
    dataIndex: 'expectedSendDate',
    key: 'expectedSendDate',
    sorter: true,
    width: '9%',
    customRender: ({ text }): string => text || '--'
  }
];

/**
 * Create search options configuration for search panel
 */
export const createSearchOptions = (t: (key: string) => string): SearchOption[] => [
  {
    valueKey: 'sendUserId',
    type: 'select-user',
    allowClear: true,
    placeholder: t('email.placeholder.selectSendUser'),
    axiosConfig: { headers: { 'XC-Opt-Tenant-Id': '' } }
  },
  {
    valueKey: 'sendStatus',
    type: 'select-enum',
    enumKey: ProcessStatus,
    placeholder: t('email.placeholder.selectSendStatus'),
    allowClear: true
  },
  {
    valueKey: 'templateCode',
    type: 'input',
    placeholder: t('email.placeholder.queryTemplateCode'),
    allowClear: true
  },
  {
    valueKey: 'urgent',
    type: 'select',
    options: [
      { value: true, label: t('common.status.yes') },
      { value: false, label: t('common.status.no') }
    ],
    placeholder: t('email.placeholder.isUrgent'),
    allowClear: true
  },
  {
    valueKey: 'verificationCode',
    type: 'select',
    options: [
      { value: true, label: t('common.status.yes') },
      { value: false, label: t('common.status.no') }
    ],
    placeholder: t('email.placeholder.isVerificationCode'),
    allowClear: true
  },
  {
    valueKey: 'batch',
    type: 'select',
    options: [
      { value: true, label: t('common.status.yes') },
      { value: false, label: t('common.status.no') }
    ],
    placeholder: t('email.placeholder.isBatch'),
    allowClear: true
  },
  {
    valueKey: 'html',
    type: 'select',
    options: [
      { value: true, label: t('common.status.yes') },
      { value: false, label: t('common.status.no') }
    ],
    placeholder: t('email.placeholder.isHtml'),
    allowClear: true
  },
  {
    valueKey: 'actualSendDate',
    type: 'date-range',
    placeholder: [
      t('email.placeholder.sendTimeRange'),
      t('email.placeholder.sendTimeRange')
    ],
    allowClear: true
  },
  {
    valueKey: 'expectedSendDate',
    type: 'date-range',
    placeholder: [
      t('email.placeholder.expectedTimeRange'),
      t('email.placeholder.expectedTimeRange')
    ],
    allowClear: true
  }
];

/**
 * Create grid columns configuration for detail view
 */
export const createGridColumns = (t: (key: string) => string): GridColumn[][] => [
  // Left column
  [
    {
      label: t('email.columns.id'),
      dataIndex: 'id'
    },
    {
      label: t('email.columns.emailType'),
      dataIndex: 'emailType'
    },
    {
      label: t('email.columns.sendStatus'),
      dataIndex: 'sendStatus'
    },
    {
      label: t('email.columns.sendTenantId'),
      dataIndex: 'sendTenantId'
    },
    {
      label: t('email.columns.sendUserId'),
      dataIndex: 'sendId'
    },
    {
      label: t('email.columns.templateCode'),
      dataIndex: 'templateCode'
    },
    {
      label: t('email.columns.urgent'),
      dataIndex: 'urgent',
      customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
    },
    {
      label: t('email.columns.verificationCode'),
      dataIndex: 'verificationCode',
      customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
    },
    {
      label: t('email.columns.batch'),
      dataIndex: 'batch',
      customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
    },
    {
      label: t('email.columns.subject'),
      dataIndex: 'subject'
    },
    {
      label: t('email.columns.content'),
      dataIndex: 'content'
    }
  ],
  // Right column
  [
    {
      label: t('email.columns.sendTime'),
      dataIndex: 'actualSendDate'
    },
    {
      label: t('email.columns.expectedTime'),
      dataIndex: 'expectedSendDate'
    },
    {
      label: t('email.columns.outId'),
      dataIndex: 'outId'
    },
    {
      label: t('email.columns.bizKey'),
      dataIndex: 'bizKey'
    },
    {
      label: t('email.columns.language'),
      dataIndex: 'language',
      customRender: ({ text }): string => text?.message || '--'
    },
    {
      label: t('email.columns.fromAddr'),
      dataIndex: 'fromAddr'
    },
    {
      label: t('email.columns.toAddress'),
      dataIndex: 'toAddress',
      customRender: ({ text }): string => text?.join(',') || '--'
    },
    {
      label: t('email.columns.html'),
      dataIndex: 'html',
      customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
    },
    {
      label: t('email.columns.failureReason'),
      dataIndex: 'failureReason'
    },
    {
      label: t('email.columns.attachments'),
      dataIndex: 'attachments'
    }
  ]
];
