import type {
  SearchOption, TableColumn, EventRecord, StatusStyleMapping,
  PaginationParams, CheckContentConfig, GridColumn
} from './types';
import { EventPushStatus, EventType } from '@xcan-angus/infra';

/**
 * Create status style mapping for push status badges
 */
export const createStatusStyleMapping = (): StatusStyleMapping => ({
  PENDING: 'warning',
  PUSHING: 'processing',
  PUSH_SUCCESS: 'success',
  PUSH_FAIL: 'error',
  IGNORED: 'default'
});

/**
 * Get status style for push status
 */
export const getStatusStyle = (key: string, statusMapping: StatusStyleMapping): string => {
  return statusMapping[key] || 'default';
};

/**
 * Create initial pagination parameters
 */
export const createInitialPaginationParams = (): PaginationParams => ({
  pageNo: 1,
  pageSize: 10,
  orderSort: 'DESC',
  sortBy: 'createdDate',
  fullTextSearch: true,
  filters: []
});

/**
 * Create initial event record object
 */
export const createInitialEventRecord = (): EventRecord => ({
  description: '',
  eventCode: '',
  ekey: '',
  errMsg: '',
  eventContent: '',
  execNo: '',
  id: '',
  pushStatus: { value: '', message: '' },
  tenantId: '',
  tenantName: '',
  triggerTime: '',
  type: { value: '', message: '' }
});

/**
 * Create initial check content configuration
 */
export const createInitialCheckContentConfig = (): CheckContentConfig => ({
  dialogVisible: false,
  content: ''
});

/**
 * Create pagination object for table component
 */
export const createPaginationObject = (params: PaginationParams, total: number) => ({
  current: params.pageNo,
  pageSize: params.pageSize,
  total: total
});

/**
 * Update pagination parameters from table change
 */
export const updatePaginationParams = (
  params: PaginationParams,
  changeParams: { orderSort: string; sortBy: string; current: number; pageSize: number }
): void => {
  params.orderSort = changeParams.orderSort;
  params.sortBy = changeParams.sortBy;
  params.pageNo = changeParams.current;
  params.pageSize = changeParams.pageSize;
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
  filters: Record<string, string>[]
): void => {
  params.filters = filters;
};

/**
 * Check if event record has error
 */
export const hasEventError = (record: EventRecord): boolean => {
  return record.pushStatus?.value === 'PUSH_FAIL';
};

/**
 * Check if event record can be viewed
 */
export const canViewEvent = (record: EventRecord): boolean => {
  return !!(record.eventViewUrl);
};

/**
 * Check if event record can show receive config
 */
export const canShowReceiveConfig = (record: EventRecord): boolean => {
  return !!(record.eventCode && record.ekey);
};

/**
 * Process channel data for grid display
 */
export const processChannelData = (data: any[]): {
  dataSource: Record<string, any>;
  columns: GridColumn[][];
} => {
  const dataSource: Record<string, any> = {};
  const columns: GridColumn[][] = [[]];

  if (!Array.isArray(data)) {
    return { dataSource, columns };
  }

  data.forEach(channel => {
    if (channel.channelType && channel.channels) {
      dataSource[channel.channelType.value] = channel.channels;
      columns[0].push({
        dataIndex: channel.channelType.value,
        label: channel.channelType.message
      });
    }
  });

  return { dataSource, columns };
};

/**
 * Check if channel data is empty
 */
export const isChannelDataEmpty = (columns: GridColumn[][]): boolean => {
  return !columns[0] || columns[0].length === 0;
};

/**
 * Create search options configuration for search panel
 */
export const createSearchOptions = (t: (key: string) => string): SearchOption[] => [
  {
    placeholder: t('event.records.placeholder.searchEventCode'),
    valueKey: 'code',
    type: 'input',
    op: 'MATCH',
    allowClear: true
  },
  {
    valueKey: 'userId',
    type: 'select-user',
    allowClear: true,
    placeholder: t('event.records.placeholder.selectReceiver'),
    showSearch: true
  },
  {
    valueKey: 'type',
    type: 'select-enum',
    enumKey: EventType,
    allowClear: true,
    placeholder: t('event.records.placeholder.selectEventType')
  },
  {
    valueKey: 'pushStatus',
    type: 'select-enum',
    enumKey: EventPushStatus,
    allowClear: true,
    placeholder: t('event.records.placeholder.selectPushStatus')
  },
  {
    valueKey: 'createdDate',
    type: 'date-range',
    placeholder: ''
  }
];

/**
 * Create table columns configuration for event records table
 */
export const createTableColumns = (t: (key: string) => string): TableColumn[] => [
  {
    title: t('event.records.columns.id'),
    dataIndex: 'id',
    key: 'id',
    width: '13%'
  },
  {
    title: t('event.records.columns.receiverName'),
    dataIndex: 'fullName',
    groupName: 'user',
    key: 'receiverName',
    width: '13%'
  },
  {
    title: t('event.records.columns.receiverId'),
    dataIndex: 'userId',
    groupName: 'user',
    key: 'receiverId',
    hide: true,
    width: '13%'
  },
  {
    title: t('event.records.columns.eventCode'),
    dataIndex: 'code',
    key: 'eventCode',
    width: '8%',
    groupName: 'event',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('event.records.columns.eventName'),
    dataIndex: 'name',
    key: 'eventName',
    width: '8%',
    groupName: 'event',
    hide: true
  },
  {
    title: t('event.records.columns.eKey'),
    dataIndex: 'ekey',
    key: 'eKey',
    width: '10%',
    ellipsis: true,
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('event.records.columns.content'),
    dataIndex: 'description',
    key: 'description'
  },
  {
    title: t('event.records.columns.type'),
    dataIndex: 'type',
    key: 'type',
    width: '6%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('event.records.columns.pushStatus'),
    dataIndex: 'pushStatus',
    key: 'pushStatus',
    width: '8%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('event.records.columns.triggerTime'),
    dataIndex: 'createdDate',
    key: 'createdDate',
    width: '11%',
    sorter: {
      compare: (a: any, b: any) => a.createdDate > b.createdDate ? 1 : -1
    },
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('event.records.columns.operate'),
    dataIndex: 'action',
    key: 'action',
    width: 280,
    align: 'center'
  }
];
