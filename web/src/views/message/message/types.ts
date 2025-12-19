/**
 * Table column configuration interface
 * Defines the structure for table column configuration including sorting, title, and custom rendering
 */
export interface TableColumnType {
  sorter?: boolean; // Enable sorting for this column
  title: string; // Column header text
  dataIndex: string; // Data field key for this column
  key: string; // Unique identifier for the column
  width?: string; // Column width specification
  slots?: { customRender: string }; // Custom rendering slot configuration
  customCell?: () => { style: string }; // Custom cell styling function
}

/**
 * Individual receive object data structure
 * Represents a single recipient of a message with basic information
 */
export interface ReceiveObjectDataType {
  id: string; // Unique identifier for the receive object
  avatar: string; // Avatar image URL or identifier
  name: string; // Display name of the receive object
  mobile: string; // Mobile phone number
}

/**
 * Complete message data structure
 * Contains all information about a message including recipients, status, and metadata
 */
export interface ReceiveObjectData {
  fullName?: string; // Full name of the message creator
  id?: string; // Unique message identifier
  readNum?: string; // Number of recipients who have read the message
  receiveObjectData?: Array<ReceiveObjectDataType>; // Array of individual recipients
  receiveObjectType?: Record<string, any>; // Type of receive object (user, dept, group)
  receiveObjectDataName?: string; // Display name for receive object type
  receiveObjectDataLength?: number; // Total number of recipients
  timingDate?: string; // Scheduled send time for the message
  sentNum?: string; // Number of recipients the message was sent to
  status?: Record<string, any>; // Current status of the message with value and message
  title?: string; // Message title/subject
  userId?: string; // ID of the user who created the message
  content?: string; // Message content/body
  sentType?: string; // Type of send operation (immediate, scheduled)
  receiveType?: Record<string, any>; // Type of receive operation (push, email, SMS)
  receiveTenantName?: string; // Name of the receiving tenant
  creator?: string; // Name of the message creator
  failureReason?: string; // Reason for message failure if applicable
}

/**
 * Search parameters interface
 * Defines the structure for search and filter parameters
 */
export interface SearchParams {
  key: string;
  value: any;
}

/**
 * Pagination configuration interface
 * Defines pagination state and configuration
 */
export interface PaginationConfig {
  current: number;
  pageSize: number;
  total: number;
}

/**
 * Sort configuration interface
 * Defines sorting parameters for table operations
 */
export interface SortConfig {
  orderBy?: string;
  orderSort?: string;
}

/**
 * Table change event parameters
 * Defines parameters for table pagination and sorting changes
 */
export interface TableChangeParams {
  current: number;
  pageSize: number;
}

/**
 * Message list API response interface
 * Defines the structure of the API response for message list
 */
export interface MessageListResponse {
  data: {
    total: number;
    list: ReceiveObjectData[];
  };
}

/**
 * Message search parameters interface
 * Defines parameters for searching messages
 */
export interface MessageSearchParams {
  filters: SearchParams[];
  pageNo: number;
  pageSize: number;
  fullTextSearch: boolean;
}

/**
 * Component state interface
 * Defines the reactive state structure for the message component
 */
export interface MessageComponentState {
  loading: boolean;
  columns: TableColumnType[];
  dataSource: ReceiveObjectData[];
}

/**
 * Search option configuration interface
 * Defines the structure for search panel options
 */
export interface SearchOption {
  valueKey: string;
  type: 'input' | 'select-enum';
  enumKey?: any;
  allowClear?: boolean;
  placeholder: string;
}
