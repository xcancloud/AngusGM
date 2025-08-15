/**
 * Quota type definition for tenant resource quota management
 */
export interface Quota {
  /** Unique identifier for the quota record */
  id: string;
  /** Application code that the quota belongs to */
  appCode: string;
  /** Service code identifier */
  serviceCode: string;
  /** Quota name with display value and message */
  name: {
    /** Display value for the quota name */
    value: string;
    /** Message description for the quota */
    message: string;
  };
  /** Whether the quota can be changed by users */
  allowChange: string;
  /** Calculated remaining quota value */
  calcRemaining: string;
  /** Current quota value */
  quota: string;
  /** Default quota value */
  default0: string;
  /** Minimum allowed quota value */
  min: string;
  /** Maximum allowed quota value */
  max: string;
  /** Tenant identifier */
  tenantId: string;
  /** Tenant name */
  tenantName: string;
}

/**
 * Table column configuration interface
 */
export interface TableColumn {
  key: string;
  title: string;
  dataIndex: string;
  width?: string;
}

/**
 * Pagination configuration interface
 */
export interface PaginationConfig {
  current: number;
  pageSize: number;
  total: number;
}

/**
 * Query parameters interface for quota list
 */
export interface QuotaQueryParams {
  pageNo: number;
  pageSize: number;
  appCode?: string;
}

/**
 * Component props interface for edit quota modal
 */
export interface EditQuotaProps {
  visible: boolean;
  default0: number;
  max: number;
  min: number;
  name: string;
}

/**
 * Component state interface for main quota component
 */
export interface QuotaState {
  editionType: string;
  appCode: string | undefined;
  loading: boolean;
  total: number;
  params: QuotaQueryParams;
  tableList: Quota[];
  disabled: boolean;
  visible: boolean;
  currQuota: Quota | undefined;
  options: Array<{ label: string; value: string }>;
}

/**
 * App option interface for select component
 */
export interface AppOption {
  label: string;
  value: string;
}

/**
 * Edition type values
 */
export type EditionType = 'CLOUD_SERVICE' | 'PRIVATE_SERVICE';

/**
 * Cloud tips component props interface
 */
export interface CloudTipsProps {
  min?: string;
  max?: string;
}
