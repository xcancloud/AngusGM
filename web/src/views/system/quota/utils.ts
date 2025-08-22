import type { TableColumn, PaginationConfig, QuotaQueryParams, EditionType, AppOption } from './types';

/**
 * Create pagination configuration object
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
 * Process app list data for select options
 */
export const processAppListForOptions = (appList: string[]): AppOption[] => {
  if (!Array.isArray(appList)) return [];

  return appList.map(item => ({
    label: item,
    value: item
  }));
};

/**
 * Validate quota input value
 */
export const isValidQuotaValue = (value: number, min: number, max: number): boolean => {
  if (value === null || value === undefined) return false;
  if (typeof value !== 'number') return false;
  if (isNaN(value)) return false;

  return value >= min && value <= max;
};

/**
 * Check if edition type is cloud service
 */
export const isCloudService = (editionType: EditionType): boolean => {
  return editionType === 'CLOUD_SERVICE';
};

/**
 * Get API domain for cloud tips
 */
export const getApiDomain = (
  DomainManager: any,
  AppOrServiceRoute: any
): string => {
  const host = DomainManager.getInstance().getApiDomain(AppOrServiceRoute.www);
  return host + '/ticket?s=quota';
};

/**
 * Create query parameters for quota API call
 */
export const createQuotaQueryParams = (
  baseParams: QuotaQueryParams,
  appCode?: string
): QuotaQueryParams => {
  if (appCode) {
    return { ...baseParams, appCode };
  }
  return baseParams;
};

/**
 * Update pagination parameters
 */
export const updatePaginationParams = (
  params: QuotaQueryParams,
  pagination: { current: number; pageSize: number }
): void => {
  const { current, pageSize } = pagination;
  params.pageNo = current;
  params.pageSize = pageSize;
};

/**
 * Format boolean status for display
 */
export const formatBooleanStatus = (value: boolean, t: (key: string) => string): string => {
  return value ? t('common.status.yes') : t('common.status.no');
};

/**
 * Get quota name display value
 */
export const getQuotaNameDisplay = (name: { value: string; message: string }): string => {
  return name?.value || name?.message || '--';
};

/**
 * Get quota name message
 */
export const getQuotaNameMessage = (name: { value: string; message: string }): string => {
  return name?.message || name?.value || '--';
};

/**
 * Create cloud service table columns configuration
 * Defines the structure and display properties for cloud service quota table
 */
export const createCloudQuotaColumns = (t: (key: string) => string): TableColumn[] => [
  {
    key: 'value',
    title: t('quota.columns.resourceName'),
    dataIndex: 'value',
    width: '20%'
  },
  {
    key: 'name',
    title: t('quota.columns.quotaDescription'),
    dataIndex: 'name'
  },
  {
    key: 'appCode',
    title: t('quota.columns.appCode'),
    dataIndex: 'appCode',
    width: '10%'
  },
  {
    key: 'quota',
    title: t('quota.columns.currentQuota'),
    dataIndex: 'quota',
    width: '10%'
  },
  {
    key: 'default0',
    title: t('quota.columns.defaultQuota'),
    dataIndex: 'default0',
    width: '10%'
  },
  {
    key: 'max',
    title: t('quota.columns.allowUpperLimit'),
    dataIndex: 'max',
    width: '10%'
  }
];

/**
 * Create private service table columns configuration
 * Defines the structure and display properties for private service quota table
 */
export const createPrivateQuotaColumns = (t: (key: string) => string): TableColumn[] => [
  {
    key: 'name',
    title: t('quota.columns.resourceName'),
    dataIndex: 'name',
    width: '20%'
  },
  {
    key: 'value',
    title: t('quota.columns.quotaKey'),
    dataIndex: 'value',
    width: '20%'
  },
  {
    key: 'appCode',
    title: t('quota.columns.appCode'),
    dataIndex: 'appCode',
    width: '10%'
  },
  {
    key: 'quota',
    title: t('quota.columns.currentQuota'),
    dataIndex: 'quota',
    width: '10%'
  },
  {
    key: 'default0',
    title: t('quota.columns.defaultQuota'),
    dataIndex: 'default0',
    width: '10%'
  },
  {
    key: 'max',
    title: t('quota.columns.allowUpperLimit'),
    dataIndex: 'max',
    width: '10%'
  },
  {
    key: 'allowChange',
    title: t('quota.columns.allowChange'),
    dataIndex: 'allowChange',
    width: '10%'
  },
  {
    key: 'action',
    title: t('quota.columns.operation'),
    dataIndex: 'action',
    width: '10%'
  }
];

/**
 * Get appropriate table columns based on edition type
 */
export const getQuotaTableColumns = (editionType: EditionType, t: (key: string) => string): TableColumn[] => {
  if (editionType === 'CLOUD_SERVICE') {
    return createCloudQuotaColumns(t);
  }
  return createPrivateQuotaColumns(t);
};
