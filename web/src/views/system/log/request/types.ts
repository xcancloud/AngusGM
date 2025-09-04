/**
 * Request log data record type interface
 * Represents a single API log entry in the list view
 */
export interface DataRecordType {
  /** Unique identifier for the log entry */
  id: string;
  /** Human-readable name of the API endpoint */
  apiName: string;
  /** Internal API identifier */
  apiId: string;
  /** HTTP method used (GET, POST, PUT, DELETE, etc.) */
  method: string;
  /** Request URI path */
  uri: string;
  /** Request ID for tracking purposes */
  requestId?: string;
  /** Service code identifier */
  serviceCode?: string;
  /** Service name for display */
  serviceName?: string;
  /** API code identifier */
  apiCode?: string;
  /** HTTP status code */
  status?: string;
  /** Request processing time in milliseconds */
  elapsedMillis?: number;
  /** User ID who made the request */
  userId?: string;
  /** Timestamp when the request was made */
  requestDate?: string;
  /** Whether the request was successful */
  success?: boolean;
  /** Instance identifier */
  instanceId?: string;
  /** Resource name being accessed */
  resourceName?: string;
}

/**
 * Request log detailed information type interface
 * Contains comprehensive information about a specific API request/response
 */
export interface DataInfoType {
  /** Unique identifier for the log entry */
  id: string;
  /** Internal API identifier */
  apiId: string;
  /** HTTP method used */
  method: string;
  /** Protocol used (HTTP/HTTPS) */
  protocol: string;
  /** Query parameters as string */
  queryParam: string;
  /** Remote client information */
  remote: string;
  /** Request body content */
  requestBody: string;
  /** Timestamp when request was received */
  requestDate: string;
  /** Request headers as key-value pairs */
  requestHeaders: Record<string, any>;
  /** Response body content */
  responseBody: string;
  /** Timestamp when response was sent */
  responseDate: string;
  /** Response headers as key-value pairs */
  responseHeaders: Record<string, any>;
  /** Size of the response in bytes */
  responseSize: string;
  /** HTTP status code */
  status: string;
  /** Tenant identifier for multi-tenancy */
  tenantId: string;
  /** Request URI path */
  uri: string;
  /** Full request URL */
  url: string;
  /** Request processing time in milliseconds */
  elapsedMillis?: number;
  /** User ID who made the request */
  userId?: string;
  /** Whether the request was successful */
  success?: boolean;
  /** Service code identifier */
  serviceCode?: string;
  /** Service name for display */
  serviceName?: string;
  /** API code identifier */
  apiCode?: string;
  /** API name for display */
  apiName?: string;
  /** Instance identifier */
  instanceId?: string;
  /** Resource name being accessed */
  resourceName?: string;
}

/**
 * Search filter interface for log queries
 * Defines the structure of search criteria
 */
export interface LogSearchFilter {
  /** Field key to search on */
  key: string;
  /** Search operation (equal, contains, etc.) */
  op: string;
  /** Search value */
  value: string | string[] | number | boolean;
}

/**
 * Component props interface for API request component
 */
export interface ApiRequestProps {
  /** Request log data to display */
  data: DataInfoType;
}

/**
 * Component props interface for API response component
 */
export interface ApiResponseProps {
  /** Response log data to display */
  data: DataInfoType;
}

/**
 * Row configuration interface for grid layouts
 */
export interface RowConfig {
  /** Spacing between columns */
  gutter: number;
  /** Label column span */
  labelCol: number;
  /** Value column span */
  valueCol: number;
}

/**
 * Header item interface for request/response headers
 */
export interface HeaderItem {
  /** Header key */
  key: string;
  /** Header value */
  value: any;
}

/**
 * Tab configuration interface
 */
export interface TabConfig {
  /** Tab display name */
  name: string;
  /** Tab value identifier */
  value: string;
}

/**
 * Component state interface for main request log component
 */
export interface RequestLogState {
  /** Whether to show statistics */
  showCount: boolean;
  /** Query parameters */
  params: {
    pageNo: number;
    pageSize: number;
    filters: any[];
  };
  /** Total count of records */
  total: number;
  /** List of log records */
  tableList: DataRecordType[];
  /** Loading state */
  loading: boolean;
  /** Disabled state */
  disabled: boolean;
  /** Detailed log information */
  detail?: DataInfoType;
  /** Selected API log */
  selectedApi?: DataRecordType;
  /** Active tab key */
  activeKey: number;
  /** Search bar height for layout */
  globalSearchHeight: number;
  /** Log retention period */
  clearBeforeDay?: string;
}

/**
 * Search option configuration interface
 */
export interface SearchOption {
  /** Field key to search on */
  valueKey: string;
  /** Placeholder text */
  placeholder: string;
  /** Input type */
  type: 'input' | 'select' | 'select-app' | 'select-enum' | 'select-dept' | 'select-group' | 'select-intl' | 'select-itc' | 'select-user' | 'select-service' | 'select-tag' | 'select-tenant' | 'date' | 'date-range' | 'tree-select';
  /** Search operation */
  op?: 'EQUAL' | 'GREATER_THAN' | 'GREATER_THAN_EQUAL' | 'IN' | 'LESS_THAN' | 'LESS_THAN_EQUAL' | 'MATCH' | 'MATCH_END' | 'NOT_EQUAL' | 'NOT_IN';
  /** Whether to allow clearing */
  allowClear: boolean;
  /** Additional options for select types */
  options?: Array<{ label: string; value: any }>;
  /** Action URL for select types */
  action?: string;
  /** Field name mapping for select types */
  fieldNames?: { label: string; value: string };
  /** Whether to show search */
  showSearch?: boolean;
  /** Data type for number inputs */
  dataType?: string;
  /** Minimum value for number inputs */
  min?: number;
  /** Maximum value for number inputs */
  max?: number;
  /** Enum key for enum selects */
  enumKey?: any;
}

/**
 * Grid column configuration interface
 */
export interface GridColumn {
  /** Column label */
  label: string;
  /** Data field name */
  dataIndex: string;
}

/**
 * HTTP method color mapping interface
 */
export interface HttpMethodColors {
  GET: string;
  POST: string;
  DELETE: string;
  PATCH: string;
  PUT: string;
  OPTIONS: string;
  HEAD: string;
  TRACE: string;
}
