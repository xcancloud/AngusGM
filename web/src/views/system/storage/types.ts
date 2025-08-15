/**
 * Storage setting interface for configuration
 */
export interface StorageSetting {
  storeType: { value: string };
  proxyAddress: string;
  localDir: string;
  accessKey: string;
  secretKey: string;
  region: string;
  endpoint: string;
}

/**
 * Storage column configuration interface
 */
export interface StorageColumn {
  dataIndex: string;
  label: string;
  required?: boolean;
}

/**
 * Storage parameters interface for API calls
 */
export interface StorageParams {
  accessKey: string;
  endpoint: string;
  force: boolean;
  proxyAddress: string;
  localDir: string;
  region: string;
  secretKey: string;
  storeType: string;
}

/**
 * Service option interface for dropdowns
 */
export interface ServiceOption {
  label: string;
  value: string;
}

/**
 * App storage data interface
 */
export interface AppStorageData {
  homeDir: string;
  workDir: string;
  dataDir: string;
  logsDir: string;
  tmpDir: string;
  confDir: string;
}

/**
 * Storage type constants
 */
export const STORAGE_TYPES = {
  LOCAL: 'LOCAL',
  AWS_S3: 'AWS_S3'
} as const;

/**
 * Error code constants
 */
export const ERROR_CODES = {
  BST001: 'BST001'
} as const;

/**
 * Storage type union type
 */
export type StorageType = typeof STORAGE_TYPES[keyof typeof STORAGE_TYPES];

/**
 * Form validation state interface
 */
export interface ValidationState {
  pathRule: boolean;
  proxyAddressRule: boolean;
  endpointRule: boolean;
  accessKeyRule: boolean;
  secretKeyRule: boolean;
}

/**
 * Component state interface for business storage
 */
export interface BusinessStorageState {
  enumData: any[];
  storage: StorageSetting | undefined;
  storeType: string | undefined;
  isEdit: boolean;
  force: boolean;
  editLoading: boolean;
}

/**
 * Form field values interface
 */
export interface FormFieldValues {
  localDir: string | undefined;
  proxyAddress: string | undefined;
  region: string | undefined;
  endpoint: string | undefined;
  accessKey: string | undefined;
  secretKey: string | undefined;
}

/**
 * App storage component state interface
 */
export interface AppStorageState {
  serviceOptions: ServiceOption[];
  instancesOptions: ServiceOption[];
  service: string | undefined;
  instances: string | undefined;
  dataSource: AppStorageData | undefined;
}
