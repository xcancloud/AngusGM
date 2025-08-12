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
 * HTTP method type for type safety
 * Defines all supported HTTP methods
 */
export type HttpMethodType = 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH' | 'OPTIONS' | 'HEAD' | 'TRACE';

/**
 * HTTP status code type for validation
 * Defines valid HTTP status code ranges
 */
export type HttpStatusCode = number;

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
 * Pagination parameters interface
 * Defines pagination state for log queries
 */
export interface LogPaginationParams {
  /** Current page number (1-based) */
  pageNo: number;
  /** Number of items per page */
  pageSize: number;
  /** Total number of items */
  total: number;
  /** Search filters to apply */
  filters: LogSearchFilter[];
}

/**
 * API log response interface
 * Defines the structure of API responses for log queries
 */
export interface ApiLogResponse {
  /** List of log entries */
  list: DataRecordType[];
  /** Total count of matching entries */
  total: number;
  /** Current page number */
  pageNo: number;
  /** Number of items per page */
  pageSize: number;
}

/**
 * Log detail response interface
 * Defines the structure of detailed log information
 */
export interface LogDetailResponse {
  /** Detailed log information */
  data: DataInfoType;
}
