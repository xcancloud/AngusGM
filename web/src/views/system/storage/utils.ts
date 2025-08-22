/**
 * Utility functions for system storage module
 * Contains helper functions extracted from storage components
 */

import type { StorageColumn, ValidationState, FormFieldValues } from './types';
import { STORAGE_TYPES, ERROR_CODES } from './types';

/**
 * Check if storage type is local storage
 */
export const isLocalStorage = (storeType: string | undefined): boolean => {
  return storeType === STORAGE_TYPES.LOCAL;
};

/**
 * Check if storage type is AWS S3 storage
 */
export const isAwsS3Storage = (storeType: string | undefined): boolean => {
  return storeType === STORAGE_TYPES.AWS_S3;
};

/**
 * Validate form fields based on storage type
 */
export const validateStorageForm = (
  formValues: FormFieldValues,
  storeType: string | undefined
): { isValid: boolean; validationState: ValidationState } => {
  const validationState: ValidationState = {
    pathRule: false,
    proxyAddressRule: false,
    endpointRule: false,
    accessKeyRule: false,
    secretKeyRule: false
  };

  // Validate proxy address (required for all storage types)
  if (!formValues.proxyAddress) {
    validationState.proxyAddressRule = true;
  }

  // Validate local storage specific fields
  if (isLocalStorage(storeType) && !formValues.localDir) {
    validationState.pathRule = true;
  }

  // Validate AWS S3 specific fields
  if (isAwsS3Storage(storeType)) {
    if (!formValues.endpoint) {
      validationState.endpointRule = true;
    }
    if (!formValues.accessKey) {
      validationState.accessKeyRule = true;
    }
    if (!formValues.secretKey) {
      validationState.secretKeyRule = true;
    }
  }

  const isValid = !Object.values(validationState).some(rule => rule);

  return { isValid, validationState };
};

/**
 * Check if storage settings have changed
 */
export const hasStorageChanges = (
  currentValues: FormFieldValues,
  originalStorage: any,
  storeType: string | undefined
): boolean => {
  if (!originalStorage) {
    return false;
  }

  // Check if storage type changed
  if (storeType !== originalStorage.storeType?.value) {
    return true;
  }

  // Check local storage changes
  if (isLocalStorage(storeType)) {
    if (currentValues.localDir !== originalStorage.localDir ||
        currentValues.proxyAddress !== originalStorage.proxyAddress) {
      return true;
    }
  }

  // Check AWS S3 changes
  if (isAwsS3Storage(storeType)) {
    if (currentValues.region !== originalStorage.region ||
        currentValues.endpoint !== originalStorage.endpoint ||
        currentValues.accessKey !== originalStorage.accessKey ||
        currentValues.secretKey !== originalStorage.secretKey) {
      return true;
    }
  }

  return false;
};

/**
 * Reset validation rules to initial state
 * @returns Initial validation state
 */
export const resetValidationRules = (): ValidationState => ({
  pathRule: false,
  proxyAddressRule: false,
  endpointRule: false,
  accessKeyRule: false,
  secretKeyRule: false
});

/**
 * Reset form values to current storage settings
 */
export const resetFormValues = (storage: any): FormFieldValues => {
  if (!storage) {
    return {
      localDir: undefined,
      proxyAddress: undefined,
      region: undefined,
      endpoint: undefined,
      accessKey: undefined,
      secretKey: undefined
    };
  }

  const currentData = JSON.parse(JSON.stringify(storage));
  return {
    proxyAddress: currentData?.proxyAddress,
    localDir: currentData?.localDir,
    accessKey: currentData?.accessKey,
    secretKey: currentData?.secretKey,
    region: currentData?.region,
    endpoint: currentData?.endpoint
  };
};

/**
 * Check if error code requires force confirmation
 */
export const requiresForceConfirmation = (errorCode: string): boolean => {
  return errorCode === ERROR_CODES.BST001;
};

/**
 * Format storage path for display
 */
export const formatStoragePath = (path: string, maxLength = 50): string => {
  if (!path) return '';

  if (path.length <= maxLength) {
    return path;
  }

  return `${path.substring(0, maxLength)}...`;
};

/**
 * Validate storage endpoint URL format
 */
export const isValidEndpointUrl = (endpoint: string): boolean => {
  if (!endpoint) return false;

  try {
    new URL(endpoint);
    return true;
  } catch {
    return false;
  }
};

/**
 * Validate local directory path format
 */
export const isValidLocalPath = (path: string): boolean => {
  if (!path) return false;

  // Basic path validation - can be enhanced based on requirements
  const pathRegex = /^[a-zA-Z0-9\/\\\-_.:]+$/;
  return pathRegex.test(path);
};

/**
 * Get storage type display name
 */
export const getStorageTypeDisplayName = (
  storeType: string | undefined,
  t: (key: string) => string
): string => {
  if (isLocalStorage(storeType)) {
    return t('storage.types.local');
  }
  if (isAwsS3Storage(storeType)) {
    return t('storage.types.awsS3');
  }
  return storeType || '';
};

/**
 * Create app storage table columns configuration
 * Defines the structure and display properties for app storage display
 */
export const createAppStorageColumns = (t: (key: string) => string): StorageColumn[][] => [
  [
    { dataIndex: 'homeDir', label: t('storage.columns.homeDir') },
    { dataIndex: 'workDir', label: t('storage.columns.workDir') },
    { dataIndex: 'dataDir', label: t('storage.columns.dataDir') },
    { dataIndex: 'logsDir', label: t('storage.columns.logsDir') },
    { dataIndex: 'tmpDir', label: t('storage.columns.tmpDir') },
    { dataIndex: 'confDir', label: t('storage.columns.confDir') }
  ]
];

/**
 * Create AWS S3 storage table columns configuration
 * Defines the structure and display properties for AWS S3 storage
 */
export const createAwsStorageColumns = (t: (key: string) => string): StorageColumn[][] => [
  [
    {
      dataIndex: 'proxyAddress',
      label: t('storage.columns.proxyAddress'),
      required: true
    },
    {
      dataIndex: 'region',
      label: t('storage.columns.region')
    },
    {
      dataIndex: 'endpoint',
      label: t('storage.columns.endpoint'),
      required: true
    },
    {
      dataIndex: 'accessKey',
      label: t('storage.columns.accessKey'),
      required: true
    },
    {
      dataIndex: 'secretKey',
      label: t('storage.columns.secretKey'),
      required: true
    }
  ]
];

/**
 * Create local storage table columns configuration
 * Defines the structure and display properties for local storage
 */
export const createLocalStorageColumns = (t: (key: string) => string): StorageColumn[][] => [
  [
    {
      dataIndex: 'origin',
      label: t('storage.columns.proxyAddress'),
      required: true
    },
    {
      dataIndex: 'url',
      label: t('storage.columns.url'),
      required: true
    }
  ]
];
