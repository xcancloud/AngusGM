
// TypeScript interfaces for better type safety
export interface StorageSetting {
  storeType: { value: string };
  proxyAddress: string;
  localDir: string;
  accessKey: string;
  secretKey: string;
  region: string;
  endpoint: string;
}

export interface StorageColumn {
  dataIndex: string;
  label: string;
  required?: boolean;
}

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

// TypeScript interfaces for better type safety
export interface ServiceOption {
  label: string;
  value: string;
}

export interface AppStorageData {
  homeDir: string;
  workDir: string;
  dataDir: string;
  logsDir: string;
  tmpDir: string;
  confDir: string;
}
