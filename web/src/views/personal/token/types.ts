/**
 * Token record interface for API responses
 */
export interface TokenRecord {
  id: string;
  name: string;
  expireDate: string;
  createdDate: string;
  open?: boolean;
  token?: string;
  expiredDate?: string;
}

/**
 * Props interface for TokenTable component
 */
export interface TokenTableProps {
  notify: string;
  tokenQuota: number;
}

/**
 * Token creation form parameters
 */
export interface CreateTokenParams {
  name: string;
  expireDate: string;
  password: string;
  [key: string]: unknown;
}

/**
 * Token deletion parameters
 */
export interface DeleteTokenParams {
  ids: string[];
}

/**
 * Table column configuration interface
 */
export interface TableColumn {
  title: string;
  dataIndex: string;
  key: string;
  ellipsis?: boolean;
  width: string;
}

/**
 * Pagination configuration interface
 */
export interface PaginationConfig {
  total: number;
}

/**
 * Component emit events interface
 */
export interface TokenTableEmits {
  (e: 'change', total: number): void;
}
