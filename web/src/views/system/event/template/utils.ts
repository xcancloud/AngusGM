import type {
  SearchOption, TableColumn, EventConfigList, ChannelOption, ChannelValues, AppItem
} from './types';
import { PageQuery, EventType } from '@xcan-angus/infra';

/**
 * Create initial pagination parameters
 */
export const createInitialPaginationParams = (): PageQuery => ({
  pageNo: 1,
  pageSize: 10,
  filters: [],
  fullTextSearch: true
});

/**
 * Create initial event config list object
 */
export const createInitialEventConfigList = (): EventConfigList => ({
  id: '',
  eKey: '',
  eventCode: '',
  eventName: '',
  eventType: undefined,
  allowedChannelTypes: [],
  pushSetting: [],
  bizKey: '',
  bigBizKey: ''
});

/**
 * Create initial channel values object
 */
export const createInitialChannelValues = (): ChannelValues => ({
  WEBHOOK: [],
  EMAIL: [],
  DINGTALK: [],
  WECHAT: []
});

/**
 * Create pagination object for table component
 */
export const createPaginationObject = (params: PageQuery, total: number) => ({
  current: params.pageNo,
  pageSize: params.pageSize,
  total: total
});

/**
 * Update pagination parameters from table change
 */
export const updatePaginationParams = (
  params: PageQuery,
  pagination: { current: number; pageSize: number }
): void => {
  params.pageNo = pagination.current;
  params.pageSize = pagination.pageSize;
};

/**
 * Reset pagination to first page
 */
export const resetPagination = (params: PageQuery): void => {
  params.pageNo = 1;
};

/**
 * Update search filters
 */
export const updateSearchFilters = (
  params: PageQuery,
  filters: Record<string, string>[]
): void => {
  params.filters = filters;
};

/**
 * Get target type name from enums
 */
export const getTargetTypeName = (
  value: string,
  targetTypeEnums: any[],
  t: (key: string) => string
): string => {
  const target = targetTypeEnums.find(i => i.value === value);
  return target?.message || t('event.template.messages.public');
};

/**
 * Get app name from app list
 */
export const getAppName = (value: string, appList: AppItem[]): string => {
  const target = appList.find(i => i.appCode === value);
  return target?.appShowName || ' ';
};

/**
 * Check if text needs truncation
 */
export const needsTruncation = (text: string, maxLength = 15): boolean => {
  return text !== null && text.length > maxLength;
};

/**
 * Truncate text with ellipsis
 */
export const truncateText = (text: string, maxLength = 15): string => {
  if (!text) return '';
  return text.length > maxLength ? text.slice(0, maxLength) + '...' : text;
};

/**
 * Get channel type display name
 */
export const getChannelTypeDisplayName = (channelTypes: any[]): string => {
  if (!Array.isArray(channelTypes)) return '';
  return channelTypes.map(type => type.message).join('，');
};

/**
 * Process app list for search options
 */
export const processAppListForSearch = (
  appList: AppItem[],
  searchOptions: SearchOption[]
): void => {
  if (searchOptions.length >= 3) {
    searchOptions[2].options = appList;
  }
};

/**
 * Load app options recursively
 */
export const loadAppOptionsRecursively = async (
  getList: (params: any) => Promise<any>,
  pageSize = 10
): Promise<AppItem[]> => {
  const [, { data = { list: [] } }] = await getList({ pageSize });
  const appList = data.list || [];

  if (+data.total > appList.length) {
    return loadAppOptionsRecursively(getList, +data.total);
  }

  return appList;
};

/**
 * Filter channels by type
 */
export const filterChannelsByType = (
  channels: any[],
  channelType: string
): string[] => {
  if (!Array.isArray(channels)) return [];

  return channels
    .filter(channel => channel.channelType?.value === channelType)
    .map(channel => channel.id);
};

/**
 * Get channel options by type
 */
export const getChannelOptionsByType = (
  type: string,
  webOptions: ChannelOption[],
  emailOptions: ChannelOption[],
  dingtalkOptions: ChannelOption[],
  wechatOptions: ChannelOption[]
): ChannelOption[] => {
  switch (type) {
    case 'WEBHOOK':
      return webOptions;
    case 'EMAIL':
      return emailOptions;
    case 'DINGTALK':
      return dingtalkOptions;
    case 'WECHAT':
      return wechatOptions;
    default:
      return [];
  }
};

/**
 * Get placeholder text for channel type
 */
export const getPlaceholderForChannelType = (
  key: string,
  t: (key: string) => string
): string => {
  const placeholderMap = {
    WEBHOOK: t('event.config.placeholder.selectHttpAddress'),
    EMAIL: t('event.config.placeholder.selectEmail'),
    DINGTALK: t('event.config.placeholder.selectDingTalk'),
    WECHAT: t('event.config.placeholder.selectWeChat')
  };

  return placeholderMap[key as keyof typeof placeholderMap] || '';
};

/**
 * Collect all selected channel IDs
 */
export const collectSelectedChannelIds = (
  selectedTypes: string[],
  channelValues: ChannelValues
): string[] => {
  let ids: string[] = [];

  for (const eventType of selectedTypes) {
    if (channelValues[eventType as keyof ChannelValues]) {
      ids = ids.concat(channelValues[eventType as keyof ChannelValues]);
    }
  }

  return ids;
};

/**
 * Check if event config can be configured
 */
export const canConfigureEvent = (record: EventConfigList): boolean => {
  return !!(record.id && record.id.trim());
};

/**
 * Create search options configuration for search panel
 */
export const createSearchOptions = (t: (key: string) => string): SearchOption[] => [
  {
    placeholder: t('event.template.placeholder.searchEventName'),
    valueKey: 'eventName',
    type: 'input',
    allowClear: true
  },
  {
    valueKey: 'eventType',
    type: 'select-enum',
    op: 'EQUAL',
    enumKey: EventType,
    allowClear: true,
    placeholder: t('event.template.placeholder.selectEventType')
  },
  {
    valueKey: 'appCode',
    type: 'select',
    placeholder: t('event.template.placeholder.selectApp'),
    options: [],
    fieldNames: {
      value: 'appCode',
      label: 'appShowName'
    }
  }
];

/**
 * Create table columns configuration for event template table
 */
export const createTableColumns = (t: (key: string) => string): TableColumn[] => [
  {
    title: t('event.template.columns.eventName'),
    dataIndex: 'eventName',
    key: 'eventName',
    width: '12%'
  },
  {
    title: t('event.template.columns.eventCode'),
    dataIndex: 'eventCode',
    key: 'eventCode',
    width: '12%'
  },
  {
    title: t('event.template.columns.type'),
    dataIndex: 'eventType',
    key: 'eventType',
    customRender: ({ text }): string => text?.message,
    width: '8%'
  },
  {
    title: t('event.template.columns.category'),
    dataIndex: 'targetType',
    key: 'targetType'
  },
  {
    title: t('event.template.columns.app'),
    dataIndex: 'appCode',
    key: 'appCode'
  },
  {
    title: t('event.template.columns.pushMethod'),
    dataIndex: 'allowedChannelTypes',
    key: 'allowedChannelTypes',
    width: '15%'
  },
  {
    title: t('event.template.columns.operate'),
    key: 'operate',
    dataIndex: 'operate',
    width: '8%'
  }
];
