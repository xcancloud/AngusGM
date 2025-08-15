import type { TableColumn, PaginationConfig, QuotaQueryParams, EditionType, AppOption } from './types';

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
 * Process app list data for select options
 * @param appList - Raw app list data from API
 * @returns Processed app options for select component
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
 * @param value - Quota value to validate
 * @param min - Minimum allowed value
 * @param max - Maximum allowed value
 * @returns True if valid, false otherwise
 */
export const isValidQuotaValue = (value: number, min: number, max: number): boolean => {
  if (value === null || value === undefined) return false;
  if (typeof value !== 'number') return false;
  if (isNaN(value)) return false;

  return value >= min && value <= max;
};

/**
 * Check if edition type is cloud service
 * @param editionType - Edition type to check
 * @returns True if cloud service, false otherwise
 */
export const isCloudService = (editionType: EditionType): boolean => {
  return editionType === 'CLOUD_SERVICE';
};

/**
 * Get API domain for cloud tips
 * @param DomainManager - Domain manager class from infrastructure
 * @param AppOrServiceRoute - Route enum from infrastructure
 * @returns API domain string
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
 * @param baseParams - Base query parameters
 * @param appCode - Optional app code filter
 * @returns Query parameters object
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
 * @param params - Current query parameters
 * @param pagination - New pagination values
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
 * @param value - Boolean value to format
 * @param t - i18n translation function
 * @returns Localized status text
 */
export const formatBooleanStatus = (value: boolean, t: (key: string) => string): string => {
  return value ? t('common.status.yes') : t('common.status.no');
};

/**
 * Get quota name display value
 * @param name - Quota name object
 * @returns Display value or fallback
 */
export const getQuotaNameDisplay = (name: { value: string; message: string }): string => {
  return name?.value || name?.message || '--';
};

/**
 * Get quota name message
 * @param name - Quota name object
 * @returns Message value or fallback
 */
export const getQuotaNameMessage = (name: { value: string; message: string }): string => {
  return name?.message || name?.value || '--';
};

/**
 * Create cloud service table columns configuration
 * Defines the structure and display properties for cloud service quota table
 * @param t - i18n translation function
 * @returns Array of table column configurations
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
 * @param t - i18n translation function
 * @returns Array of table column configurations
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
 * @param editionType - Edition type (CLOUD_SERVICE or PRIVATE_SERVICE)
 * @param t - i18n translation function
 * @returns Array of table column configurations
 */
export const getQuotaTableColumns = (editionType: EditionType, t: (key: string) => string): TableColumn[] => {
  if (editionType === 'CLOUD_SERVICE') {
    return createCloudQuotaColumns(t);
  }
  return createPrivateQuotaColumns(t);
};
