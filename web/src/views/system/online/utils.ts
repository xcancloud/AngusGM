import type { TableColumn, SearchOption, PaginationConfig, OnlineUser, LogoutParams } from './types';

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
 * Process online user list data and add loading state
 * @param userList - Raw user list data from API
 * @returns Processed users with loading state
 */
export const processOnlineUserList = (userList: OnlineUser[]): OnlineUser[] => {
  if (!Array.isArray(userList)) return [];

  return userList.map(item => ({
    ...item,
    loading: false
  }));
};

/**
 * Create logout parameters for API call
 * @param userId - User ID to logout
 * @returns Logout parameters object
 */
export const createLogoutParams = (userId: string): LogoutParams => ({
  receiveObjectIds: [userId],
  receiveObjectType: 'USER',
  broadcast: false
});

/**
 * Get online status color for badge
 * @param isOnline - Whether user is online
 * @returns Color string for badge styling
 */
export const getOnlineStatusColor = (isOnline: boolean): string => {
  return isOnline ? 'rgba(82,196,26,1)' : 'rgba(217, 217, 217,1)';
};

/**
 * Get online status text for display
 * @param isOnline - Whether user is online
 * @param t - i18n translation function
 * @returns Localized status text
 */
export const getOnlineStatusText = (isOnline: boolean, t: (key: string) => string): string => {
  return isOnline ? t('onlineUser.messages.online') : t('onlineUser.messages.offline');
};

/**
 * Update pagination parameters
 * @param params - Current query parameters
 * @param pagination - New pagination values
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
 * @param params - Current query parameters
 * @param sorter - New sorting values
 */
export const updateSortingParams = (
  params: { orderBy: string; orderSort: string },
  sorter: { orderBy: string; orderSort: string }
): void => {
  params.orderBy = sorter.orderBy;
  params.orderSort = sorter.orderSort;
};

/**
 * Reset pagination to first page
 * @param params - Query parameters to reset
 */
export const resetPagination = (params: { pageNo: number }): void => {
  params.pageNo = 1;
};

/**
 * Create table columns configuration for online users
 * Defines the structure and display properties for the online users table
 * @param t - i18n translation function
 * @returns Array of table column configurations
 */
export const createOnlineUserColumns = (t: (key: string) => string): TableColumn[] => [
  {
    title: t('onlineUser.columns.id'),
    dataIndex: 'id',
    key: 'id',
    hide: true
  },
  {
    title: t('onlineUser.columns.user'),
    dataIndex: 'fullName',
    key: 'fullName',
    width: '10%',
    sorter: true
  },
  {
    title: t('onlineUser.columns.onlineStatus'),
    dataIndex: 'online',
    key: 'online',
    width: '8%'
  },
  {
    title: t('onlineUser.columns.upTime'),
    dataIndex: 'onlineDate',
    key: 'onlineDate',
    width: '12%',
    customRender: ({ text }) => text || '--',
    sorter: {
      compare: (a: any, b: any) => a.onlineDate > b.onlineDate ? 1 : -1
    }
  },
  {
    title: t('onlineUser.columns.offlineTime'),
    dataIndex: 'offlineDate',
    key: 'offlineDate',
    width: '12%',
    customRender: ({ text }) => text || '--',
    sorter: {
      compare: (a: any, b: any) => a.offlineDate > b.offlineDate ? 1 : -1
    }
  },
  {
    title: t('onlineUser.columns.terminalEquipment'),
    dataIndex: 'userAgent',
    key: 'userAgent',
    width: '42%',
    groupName: 'userAgent',
    customRender: ({ text }) => text || '--'
  },
  {
    title: t('onlineUser.columns.deviceId'),
    dataIndex: 'deviceId',
    key: 'deviceId',
    width: '42%',
    hide: true,
    groupName: 'userAgent',
    customRender: ({ text }) => text || '--'
  },
  {
    title: t('onlineUser.columns.ip'),
    dataIndex: 'remoteAddress',
    key: 'remoteAddress',
    width: '8%'
  },
  {
    title: t('onlineUser.columns.logOut'),
    dataIndex: 'option',
    key: 'option',
    width: '8%',
    align: 'center'
  }
];

/**
 * Create search options configuration for online users
 * Defines the search and filter options for the online users list
 * @param t - i18n translation function
 * @returns Array of search option configurations
 */
export const createOnlineUserSearchOptions = (t: (key: string) => string): SearchOption[] => [
  {
    placeholder: t('onlineUser.placeholder.searchNameIp'),
    valueKey: 'fullName',
    type: 'input',
    op: 'EQUAL',
    allowClear: true
  },
  {
    placeholder: t('onlineUser.placeholder.searchDeviceId'),
    valueKey: 'deviceId',
    type: 'input',
    op: 'EQUAL',
    allowClear: true
  },
  {
    valueKey: 'onlineDate',
    type: 'date-range',
    placeholder: [t('onlineUser.placeholder.onlineTimeFrom'), t('onlineUser.placeholder.onlineTimeTo')],
    allowClear: true
  },
  {
    valueKey: 'offlineDate',
    type: 'date-range',
    placeholder: [t('onlineUser.placeholder.offlineTimeFrom'), t('onlineUser.placeholder.offlineTimeTo')],
    allowClear: true
  }
];
