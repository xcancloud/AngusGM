/**
 * Search option configuration for department management
 * Defines search fields and their properties
 */
export const _searchOptions = [
  {
    placeholder: 'accountsDept.placeholder.p1',
    valueKey: 'name',
    type: 'input'
  },
  {
    value: 'tagId',
    type: 'select-tag',
    allowClear: true,
    valueKey: 'tag',
    placeholder: '选择标签'
  }
];

/**
 * Table column configuration for department list
 * Defines the structure and display properties of table columns
 */
export const _columns = [
  {
    title: 'accountsDept.columns.code',
    dataIndex: 'code'
  },
  {
    title: 'accountsDept.columns.name',
    dataIndex: 'name'
  },
  {
    title: 'accountsDept.columns.tenantName',
    dataIndex: 'tenantName'
  },
  {
    title: 'accountsDept.columns.createdDate',
    dataIndex: 'createdDate'
  },
  {
    title: 'accountsDept.columns.action',
    dataIndex: 'action',
    align: 'center'
  }
];

/**
 * Department data type interface
 * Represents the structure of department information
 */
export interface DataType {
  /** Unique identifier for the department */
  id: string | undefined;
  /** Department name */
  name: string | undefined;
  /** Parent department ID */
  pid: string | undefined;
  /** Department code */
  code: string | undefined;
  /** Creation date */
  createdDate: string | undefined;
  /** Tenant ID */
  tenantId: string | undefined;
  /** Tenant name */
  tenantName: string | undefined;
  /** Associated tags */
  tags: RecordType[];
}

/**
 * Tree record type for department hierarchy
 * Represents department nodes in the tree structure
 */
export interface TreeRecordType {
  /** Unique identifier for the department */
  id: string | undefined;
  /** Department name */
  name: string | undefined;
  /** Parent department ID */
  pid: string | undefined;
  /** Child departments */
  children?: TreeRecordType[];
  /** Associated tags */
  tags?: { id: string; name: string }[];
  /** Icon for the department */
  icon?: string;
}

/**
 * User record type for department users
 * Represents user information within a department
 */
export interface UserRecordType {
  /** Unique identifier for the user */
  id: string;
  /** Full name of the user */
  fullName: string | undefined;
  /** Department ID */
  deptId: string;
  /** User ID */
  userId: string;
  /** Department name */
  deptName: string | undefined;
  /** Creation date */
  createdDate: string | undefined;
  /** User avatar URL */
  avatar: string | undefined;
  /** Mobile number */
  mobile: string | undefined;
  /** Whether user is department head */
  deptHead: boolean;
  /** Whether this is the main department for the user */
  mainDept: boolean;
}

/**
 * Record type for general data records
 * Used for tags and other metadata
 */
export interface RecordType {
  /** Unique identifier */
  id: string;
  /** Record name */
  name: string;
  /** Additional properties */
  [key: string]: any;
}

/**
 * Search option type for advanced filtering
 * Defines the structure of search options
 */
export interface SearchOption {
  /** Placeholder text for the search field */
  placeholder: string;
  /** Key for the search value */
  valueKey: string;
  /** Type of search field */
  type: 'input' | 'select' | 'select-tag' | 'date';
  /** Whether to allow clearing the field */
  allowClear?: boolean;
  /** Value for select options */
  value?: string;
}

/**
 * Table column type for data display
 * Defines the structure of table columns
 */
export interface TableColumn {
  /** Column title */
  title: string;
  /** Data field name */
  dataIndex: string;
  /** Column alignment */
  align?: 'left' | 'center' | 'right';
  /** Column width */
  width?: string | number;
  /** Whether to show ellipsis for long text */
  ellipsis?: boolean;
  /** Custom cell renderer */
  customCell?: () => Record<string, any>;
}
