/**
 * Utility functions for system version module
 * Contains helper functions extracted from version components
 */

import type { EditionTypeIcons, GridColumns } from './types';

/**
 * Edition type icon mapping
 * Maps edition types to their corresponding icon classes
 */
export const EDITION_TYPE_ICONS: EditionTypeIcons = {
  DATACENTER: 'icon-shujuzhongxin',
  CLOUD_SERVICE: 'icon-yunfuwu',
  ENTERPRISE: 'icon-qiye',
  COMMUNITY: 'icon-shequ'
};

/**
 * Get edition type icon based on edition type key
 * Returns appropriate icon class for different edition types
 * @param key - Edition type key
 * @returns Icon class name
 */
export const getEditionTypeIcon = (key: string): string => {
  return EDITION_TYPE_ICONS[key as keyof EditionTypeIcons] || '';
};

/**
 * Update column visibility based on upgrade status
 * Hides features column when no features are available
 * @param columns - Grid columns to update
 */
export const updateColumnsVisibility = (columns: any[]): void => {
  for (let i = 0; i < columns.length; i++) {
    if (columns[i].dataIndex === 'features') {
      columns[i].hide = true;
      break;
    }
  }
};

/**
 * Calculate remaining days between two dates
 * @param endDate - End date string
 * @param startDate - Start date string (defaults to current date)
 * @returns Number of remaining days
 */
export const calculateRemainingDays = (endDate: string, startDate?: string): number => {
  const end = new Date(endDate);
  const start = startDate ? new Date(startDate) : new Date();
  const diffTime = end.getTime() - start.getTime();
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
};

/**
 * Format date for display
 * @param dateString - Date string to format
 * @param format - Date format (defaults to 'YYYY-MM-DD')
 * @returns Formatted date string
 */
export const formatDate = (dateString: string, format = 'YYYY-MM-DD'): string => {
  if (!dateString) return '';

  const date = new Date(dateString);
  if (isNaN(date.getTime())) return '';

  // Simple date formatting - can be enhanced with dayjs or date-fns
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');

  return format
    .replace('YYYY', String(year))
    .replace('MM', month)
    .replace('DD', day);
};

/**
 * Validate license information
 * @param license - License object to validate
 * @returns Validation result object
 */
export const validateLicense = (license: any): { isValid: boolean, errors: string[] } => {
  const errors: string[] = [];

  if (!license?.licenseNo) {
    errors.push('License number is required');
  }

  if (!license?.beginDate) {
    errors.push('Begin date is required');
  }

  if (!license?.endDate) {
    errors.push('End date is required');
  }

  if (license?.beginDate && license?.endDate) {
    const beginDate = new Date(license.beginDate);
    const endDate = new Date(license.endDate);

    if (beginDate >= endDate) {
      errors.push('Begin date must be before end date');
    }
  }

  return {
    isValid: errors.length === 0,
    errors
  };
};

/**
 * Create cloud edition grid columns configuration
 * Defines the layout and fields for cloud service version display
 * @param t - i18n translation function
 * @returns Grid columns configuration
 */
export const createCloudEditionColumns = (t: (key: string) => string): GridColumns => [
  [
    {
      label: t('version.columns.editionType'),
      dataIndex: 'editionType'
    },
    {
      label: t('version.columns.goodsCode'),
      dataIndex: 'goodsCode'
    },
    {
      label: t('version.columns.goodsVersion'),
      dataIndex: 'goodsVersion'
    },
    {
      label: t('version.columns.provider'),
      dataIndex: 'provider'
    },
    {
      label: t('version.columns.publisher'),
      dataIndex: 'issuer'
    },
    {
      label: t('version.columns.holder'),
      dataIndex: 'holder'
    },
    {
      label: t('version.columns.beginDate'),
      dataIndex: 'beginDate'
    },
    {
      label: t('version.columns.endDate'),
      dataIndex: 'endDate'
    }
  ]
];

/**
 * Create private edition grid columns configuration
 * Defines the layout and fields for private version display
 * Includes additional fields like license number and MD5 signature
 * @param t - i18n translation function
 * @returns Grid columns configuration
 */
export const createPrivateEditionColumns = (t: (key: string) => string): GridColumns => [
  [
    {
      label: t('version.columns.editionType'),
      dataIndex: 'editionType'
    },
    {
      label: t('version.columns.goodsCode'),
      dataIndex: 'goodsCode'
    },
    {
      label: t('version.columns.goodsVersion'),
      dataIndex: 'goodsVersion'
    },
    {
      label: t('version.columns.provider'),
      dataIndex: 'provider'
    },
    {
      label: t('version.columns.publisher'),
      dataIndex: 'issuer'
    },
    {
      label: t('version.columns.holder'),
      dataIndex: 'holder'
    },
    {
      label: t('version.columns.licenseNo'),
      dataIndex: 'licenseNo'
    },
    {
      label: t('version.columns.beginDate'),
      dataIndex: 'beginDate'
    },
    {
      label: t('version.columns.endDate'),
      dataIndex: 'endDate'
    },
    {
      label: t('version.columns.signature'),
      dataIndex: 'signature'
    }
  ]
];

/**
 * Create current version grid columns configuration
 * Shows version introduction and current version information
 * @param t - i18n translation function
 * @returns Grid columns configuration
 */
export const createCurrentVersionColumns = (t: (key: string) => string): GridColumns => [
  [
    {
      label: t('version.columns.introduction'),
      dataIndex: 'introduction'
    },
    {
      label: t('version.columns.features'),
      dataIndex: 'features',
      hide: true
    },
    {
      label: t('version.columns.currentVersion'),
      dataIndex: 'currentVersion'
    }
  ]
];

/**
 * Create upgradeable version grid columns configuration
 * Shows version introduction and upgrade information
 * @param t - i18n translation function
 * @returns Grid columns configuration
 */
export const createUpgradeableColumns = (t: (key: string) => string): GridColumns => [
  [
    {
      label: t('version.columns.introduction'),
      dataIndex: 'introduction'
    },
    {
      label: t('version.columns.features'),
      dataIndex: 'features',
      hide: true
    },
    {
      label: t('version.columns.currentVersion'),
      dataIndex: 'currentVersion'
    }
  ]
];
