import type {
  SearchOption, GridColumn, HttpMethodColors, HeaderItem, TabConfig, RowConfig
} from './types';

/**
 * Create row configuration for grid layouts
 * @returns Row configuration object
 */
export const createRowConfig = (): RowConfig => ({
  gutter: 15,
  labelCol: 6,
  valueCol: 18
});

/**
 * Create HTTP method color mapping for visual distinction
 * @returns Color mapping object for HTTP methods
 */
export const createHttpMethodColors = (): HttpMethodColors => ({
  GET: 'text-blue-bg',
  POST: 'text-success',
  DELETE: 'text-danger',
  PATCH: 'text-blue-bg1',
  PUT: 'text-orange-bg2',
  OPTIONS: 'text-blue-bg2',
  HEAD: 'text-orange-method',
  TRACE: 'text-purple-method'
} as const);

/**
 * Create pagination configuration options
 * @returns Array of page size options
 */
export const createPageSizeOptions = (): readonly string[] => {
  return ['10', '20', '30', '40', '50'] as const;
};

/**
 * Process headers data for display
 */
export const processHeaders = (headers: any): HeaderItem[] => {
  if (!headers) return [];

  let processedHeaders = headers;
  if (typeof headers === 'string') {
    try {
      processedHeaders = JSON.parse(headers);
    } catch {
      return [];
    }
  }

  const result: HeaderItem[] = [];
  for (const key in processedHeaders) {
    if (processedHeaders.hasOwnProperty(key)) {
      result.push({ key, value: processedHeaders[key] });
    }
  }
  return result;
};

/**
 * Process request body data for display
 */
export const processRequestBody = (requestBody: string): HeaderItem[] => {
  if (!requestBody) return [];

  try {
    const parsedBody = JSON.parse(requestBody);
    const result: HeaderItem[] = [];

    for (const key in parsedBody) {
      if (parsedBody.hasOwnProperty(key)) {
        result.push({ key, value: parsedBody[key] });
      }
    }
    return result;
  } catch {
    return [];
  }
};

/**
 * Check if value is an array
 */
export const isArrayValue = (value: any): boolean => {
  return Object.prototype.toString.call(value) === '[object Array]';
};

/**
 * Get HTTP method color class
 */
export const getHttpMethodColor = (method: string, colors: HttpMethodColors): string => {
  return colors[method as keyof HttpMethodColors] || 'text-gray-500';
};

/**
 * Check if HTTP status indicates success
 */
export const isHttpStatusSuccess = (status: string): boolean => {
  return status.startsWith('2');
};

/**
 * Get status icon based on HTTP status
 */
export const getStatusIcon = (status: string): string => {
  return isHttpStatusSuccess(status) ? 'icon-right' : 'icon-cuowu';
};

/**
 * Get status text color class
 */
export const getStatusTextColor = (status: string): string => {
  return isHttpStatusSuccess(status) ? 'text-success' : 'text-danger';
};

/**
 * Format elapsed time for display
 */
export const formatElapsedTime = (elapsedMillis: number): string => {
  if (!elapsedMillis) return '0ms';

  if (elapsedMillis < 1000) {
    return `${elapsedMillis}ms`;
  }

  const seconds = (elapsedMillis / 1000).toFixed(2);
  return `${seconds}s`;
};

/**
 * Validate search criteria
 */
export const isValidSearchCriteria = (criteria: any[]): boolean => {
  if (!Array.isArray(criteria)) return false;

  return criteria.every(item =>
    item &&
    typeof item.key === 'string' &&
    typeof item.op === 'string' &&
    item.value !== undefined
  );
};

/**
 * Reset pagination to first page
 */
export const resetPagination = (params: { pageNo: number }): void => {
  params.pageNo = 1;
};

/**
 * Update pagination parameters
 */
export const updatePaginationParams = (
  params: { pageNo: number; pageSize: number },
  page: number,
  size: number
): void => {
  params.pageNo = page;
  params.pageSize = size;
};

/**
 * Check if item is selected
 */
export const isItemSelected = (item: any, selectedId: string): boolean => {
  return item?.id === selectedId;
};

/**
 * Get content type for response formatting
 */
export const getContentType = (responseBody: string): string => {
  if (!responseBody) return 'text';

  try {
    JSON.parse(responseBody);
    return 'json';
  } catch {
    return 'text';
  }
};

/**
 * Format request/response data for display
 */
export const formatDataForDisplay = (data: any): string => {
  if (!data) return '-';

  if (typeof data === 'string') {
    return data || '-';
  }

  if (typeof data === 'number') {
    return data.toString();
  }

  if (typeof data === 'boolean') {
    return data ? 'true' : 'false';
  }

  return JSON.stringify(data) || '-';
};

/**
 * Create search options configuration for the search panel
 */
export const createSearchOptions = (
  t: (key: string) => string,
  GM: any,
  HttpMethod: any
): SearchOption[] => [
  {
    valueKey: 'id',
    placeholder: t('log.request.placeholder.searchLogId'),
    type: 'input',
    op: 'EQUAL',
    allowClear: true
  },
  {
    valueKey: 'userId',
    placeholder: t('log.request.placeholder.selectUserId'),
    type: 'select-user',
    allowClear: true
  },
  {
    valueKey: 'requestId',
    placeholder: t('log.request.placeholder.searchRequestId'),
    op: 'EQUAL',
    type: 'input',
    allowClear: true
  },
  {
    valueKey: 'resourceName',
    placeholder: t('log.request.placeholder.searchOperationResource'),
    type: 'input',
    op: 'EQUAL',
    allowClear: true
  },
  {
    valueKey: 'success',
    type: 'select',
    allowClear: true,
    options: [
      {
        label: t('log.request.search.failed'),
        value: false
      },
      {
        label: t('log.request.search.success'),
        value: true
      }
    ],
    placeholder: t('log.request.placeholder.searchSuccess')
  },
  {
    valueKey: 'serviceCode',
    placeholder: t('log.request.placeholder.selectOrSearchService'),
    type: 'select',
    action: `${GM}/service`,
    fieldNames: { label: 'name', value: 'code' },
    showSearch: true,
    allowClear: true
  },
  {
    valueKey: 'instanceId',
    placeholder: t('log.request.placeholder.searchInstanceId'),
    type: 'input',
    op: 'EQUAL',
    allowClear: true
  },
  {
    valueKey: 'apiCode',
    placeholder: t('log.request.placeholder.searchApiCode'),
    type: 'input',
    op: 'EQUAL',
    allowClear: true
  },
  {
    valueKey: 'method',
    placeholder: t('log.request.placeholder.searchRequestMethod'),
    type: 'select-enum',
    enumKey: HttpMethod,
    allowClear: true
  },
  {
    valueKey: 'uri',
    placeholder: t('log.request.placeholder.searchRequestUri'),
    type: 'input',
    op: 'EQUAL',
    allowClear: true
  },
  {
    valueKey: 'status',
    placeholder: t('log.request.placeholder.searchStatusCode'),
    type: 'input',
    op: 'EQUAL',
    dataType: 'number',
    min: 0,
    max: 1000,
    allowClear: true
  },
  {
    valueKey: 'requestDate',
    type: 'date',
    allowClear: true,
    placeholder: t('log.request.placeholder.requestTime')
  }
];

/**
 * Create grid columns configuration for displaying log details
 */
export const createGridColumns = (t: (key: string) => string): GridColumn[][] => [
  [
    {
      label: t('log.request.columns.apiName'),
      dataIndex: 'apiName'
    },
    {
      label: t('log.request.columns.requestId'),
      dataIndex: 'requestId'
    },
    {
      label: t('log.request.columns.apiCode'),
      dataIndex: 'apiCode'
    },
    {
      label: t('log.request.columns.serviceName'),
      dataIndex: 'serviceName'
    },
    {
      label: t('log.request.columns.serviceCode'),
      dataIndex: 'serviceCode'
    },
    {
      label: t('log.request.columns.uri'),
      dataIndex: 'uri'
    },
    {
      label: t('log.request.columns.method'),
      dataIndex: 'method'
    },
    {
      label: t('log.request.columns.elapsedMillis'),
      dataIndex: 'elapsedMillis'
    },
    {
      label: t('log.request.columns.status'),
      dataIndex: 'status'
    }
  ]
];

/**
 * Create tab configuration for response component
 */
export const createResponseTabs = (t: (key: string) => string): TabConfig[] => [
  {
    name: t('log.request.messages.prettyFormat'),
    value: 'pretty'
  },
  {
    name: t('log.request.messages.rawFormat'),
    value: 'raw'
  },
  {
    name: t('log.request.messages.preview'),
    value: 'preview'
  }
];
