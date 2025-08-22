import type { TableColumn, PaginationConfig, OperationLogRecord } from './types';

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
 * Process operation log list data
 */
export const processOperationLogList = (logList: OperationLogRecord[]): OperationLogRecord[] => {
  if (!Array.isArray(logList)) return [];
  return logList;
};

/**
 * Update pagination parameters
 */
export const updatePaginationParams = (
  params: { pageNo: number; pageSize: number },
  pagination: { current: number; pageSize: number }
): void => {
  const { current, pageSize } = pagination;
  params.pageNo = current;
  params.pageSize = pageSize;
};

/**
 * Update sorting parameters
 */
export const updateSortingParams = (
  params: { orderBy?: string; orderSort?: any },
  sorter: { orderBy: string; orderSort: any }
): void => {
  params.orderBy = sorter.orderBy;
  params.orderSort = sorter.orderSort;
};

/**
 * Reset pagination to first page
 */
export const resetPagination = (params: { pageNo: number }): void => {
  params.pageNo = 1;
};

/**
 * Extract system settings from API response
 */
export const extractSystemSettings = (response: any): { clearBeforeDay?: string } | undefined => {
  if (!response?.data) return undefined;

  return {
    clearBeforeDay: response.data.operationLog?.clearBeforeDay
  };
};

/**
 * Toggle statistics visibility
 */
export const toggleStatisticsVisibility = (currentState: boolean): boolean => {
  return !currentState;
};

/**
 * Create table columns configuration for operation logs
 * Defines the structure and display properties for the operation logs table
 */
export const createOperationLogColumns = (t: (key: string) => string): TableColumn[] => [
  {
    key: 'id',
    title: t('log.operation.columns.id'),
    dataIndex: 'id',
    hide: true
  },
  {
    key: 'fullName',
    title: t('log.operation.columns.operator'),
    dataIndex: 'fullName',
    width: '15%',
    sorter: true
  },
  {
    key: 'description',
    title: t('log.operation.columns.operationContent'),
    dataIndex: 'description',
    width: '35%'
  },
  {
    key: 'resource',
    title: t('log.operation.columns.operationResource'),
    dataIndex: 'resource',
    width: '15%',
    customRender: ({ text }) => text?.message
  },
  {
    key: 'resourceName',
    title: t('log.operation.columns.operationResourceName'),
    dataIndex: 'resourceName',
    groupName: 'resource',
    width: '20%'
  },
  {
    key: 'resourceId',
    title: t('log.operation.columns.operationResourceId'),
    dataIndex: 'resourceId',
    groupName: 'resource',
    hide: true,
    width: '20%'
  },
  {
    key: 'optDate',
    title: t('log.operation.columns.operationDate'),
    dataIndex: 'optDate',
    width: '15%',
    sorter: true,
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  }
];
