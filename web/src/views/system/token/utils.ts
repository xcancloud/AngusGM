/**
 * Utility functions for system token module
 * Contains helper functions extracted from token components
 */

import type { ColumnsProps, Token, ServiceSelectionData } from './types';

/**
 * Check if token is expired
 */
export const isTokenExpired = (item: Token): boolean => {
  return new Date(item.expiredDate) < new Date();
};

/**
 * Get token expiration status color
 */
export const getTokenExpirationColor = (item: Token): string => {
  return isTokenExpired(item) ? 'orange' : 'green';
};

/**
 * Get token expiration status text
 */
export const getTokenExpirationText = (item: Token, t: (key: string) => string): string => {
  return isTokenExpired(item)
    ? t('systemToken.messages.expired')
    : t('systemToken.messages.notExpired');
};

/**
 * Process service selection data to grant data format
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
 */
export const getSelectedServiceCodes = (dataSource: ServiceSelectionData[]): string[] => {
  return dataSource.map(i => i.serviceCode).filter(Boolean) as string[];
};

/**
 * Validate service selection
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
