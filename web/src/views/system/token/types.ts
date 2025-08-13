/**
 * Option interface for dropdown/select components
 */
export interface Option {
  value: string;
  label: string;
}

/**
 * Column configuration interface for table columns
 */
export interface Column {
  dataIndex: string;
  localesCode: string;
}

/**
 * Grant data interface for API/ACL permissions
 */
export interface GrantData {
  acls?: Array<string>;
  apiIds?: string[];
  resource: string;
}

/**
 * Service interface representing a service with its resources
 */
export interface Service {
  serviceCode: string;
  serviceName: string;
  title?: string;
  resources: Array<any>;
  spread?: boolean;
  list: Array<any>;
  open: boolean;
}

/**
 * Token interface representing a system token
 */
export interface Token {
  id: string;
  name: string;
  token?: string;
  showToken?: boolean;
  expiredDate: string;
  authType: {
    value: string;
    message: string;
  };
  createdByName?: string;
  createdDate: string;
}

/**
 * Table column properties interface
 */
export interface ColumnsProps {
  key: string;
  title: string;
  dataIndex: string;
  localesCode: string;
  width?: string | number;
}
