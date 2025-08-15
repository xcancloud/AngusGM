/**
 * Utility functions for system token module
 * Contains helper functions extracted from token components
 */

import type { ColumnsProps, Token, ServiceSelectionData } from './types';

/**
 * Check if token is expired
 * @param item - Token item to check
 * @returns True if token is expired, false otherwise
 */
export const isTokenExpired = (item: Token): boolean => {
  return new Date(item.expiredDate) < new Date();
};

/**
 * Get token expiration status color
 * @param item - Token item to check
 * @returns Color string for badge display
 */
export const getTokenExpirationColor = (item: Token): string => {
  return isTokenExpired(item) ? 'orange' : 'green';
};

/**
 * Get token expiration status text
 * @param item - Token item to check
 * @param t - i18n translation function
 * @returns Localized expiration status text
 */
export const getTokenExpirationText = (item: Token, t: (key: string) => string): string => {
  return isTokenExpired(item)
    ? t('systemToken.messages.expired')
    : t('systemToken.messages.notExpired');
};

/**
 * Process service selection data to grant data format
 * @param dataSource - Array of service selection data
 * @returns Processed grant data array
 */
export const processServiceSelectionData = (dataSource: ServiceSelectionData[]): any[] => {
  const result: any[] = [];
  dataSource.forEach(data => {
    Object.keys(data.source).forEach(sourceName => {
      result.push({
        resource: sourceName,
        apiIds: data.source[sourceName]
      });
    });
  });
  return result;
};

/**
 * Get selected service codes from data source
 * @param dataSource - Array of service selection data
 * @returns Array of selected service codes
 */
export const getSelectedServiceCodes = (dataSource: ServiceSelectionData[]): string[] => {
  return dataSource.map(i => i.serviceCode).filter(Boolean) as string[];
};

/**
 * Validate service selection
 * @param serviceCode - Selected service code
 * @param dataSource - Current data source
 * @param t - i18n translation function
 * @returns Validation result object
 */
export const validateServiceSelection = (
  serviceCode: string | undefined,
  dataSource: ServiceSelectionData[],
  t: (key: string) => string
): { isValid: boolean; errorMessage?: string } => {
  if (!serviceCode) {
    return {
      isValid: false,
      errorMessage: t('systemToken.messages.servicePlaceholder')
    };
  }

  if (dataSource.find(service => service.serviceCode === serviceCode)) {
    return {
      isValid: false,
      errorMessage: t('systemToken.messages.serviceTip')
    };
  }

  return { isValid: true };
};

/**
 * Calculate days until token expiration
 * @param expiredDate - Token expiration date
 * @returns Number of days until expiration (negative if expired)
 */
export const calculateDaysUntilExpiration = (expiredDate: string): number => {
  const expiration = new Date(expiredDate);
  const now = new Date();
  const diffTime = expiration.getTime() - now.getTime();
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
};

/**
 * Create table columns configuration for token management
 * Defines the structure and display properties for the token table
 * @param t - i18n translation function
 * @returns Array of table column configurations
 */
export const createTokenTableColumns = (t: (key: string) => string): ColumnsProps[] => [
  {
    key: 'name',
    title: t('systemToken.columns.name'),
    dataIndex: 'name',
    localesCode: 'systemToken.columns.name'
  },
  {
    key: 'authType',
    title: t('systemToken.columns.authType'),
    dataIndex: 'authType',
    localesCode: 'systemToken.columns.authType'
  },
  {
    key: 'expiredDate',
    title: t('systemToken.columns.expiredDate'),
    dataIndex: 'expiredDate',
    localesCode: 'systemToken.columns.expiredDate'
  },
  {
    key: 'token',
    title: t('systemToken.columns.token'),
    dataIndex: 'token',
    localesCode: 'systemToken.columns.token',
    width: '550px'
  },
  {
    key: 'expired',
    title: t('systemToken.columns.expired'),
    dataIndex: 'expired',
    localesCode: 'systemToken.columns.expired'
  },
  {
    key: 'createdByName',
    title: t('systemToken.columns.createdByName'),
    dataIndex: 'createdByName',
    localesCode: 'common.columns.createdByName'
  },
  {
    key: 'createdDate',
    title: t('systemToken.columns.createdDate'),
    dataIndex: 'createdDate',
    localesCode: 'common.columns.createdDate'
  },
  {
    key: 'action',
    title: t('systemToken.columns.action'),
    dataIndex: 'action',
    localesCode: 'systemToken.columns.action',
    width: 200
  }
];
