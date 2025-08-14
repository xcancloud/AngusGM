import type { EntityType } from './types';

/**
 * Get tenant authorization type name for display
 * Constructs a human-readable string representing the tenant authorization type
 * by checking currentDefault and openAuth flags on the record
 * @param record - Record containing authorization flags
 * @param t - i18n translation function
 * @returns Formatted authorization type string
 */
export const getTenantAuthTypeName = (record: any, t: (key: string) => string): string => {
  const result: string[] = [];
  if (record?.currentDefault) {
    result.push(t('permission.view.tenant.defaultAuth'));
  }
  if (record?.openAuth) {
    result.push(t('permission.view.tenant.openAuth'));
  }
  return result.join(',');
};

/**
 * Get entity type name for policy modal
 * Generates the appropriate entity type string for the policy modal
 * based on the currently selected tab
 * @param tab - Current active tab
 * @returns Entity type string
 */
export const getEntityType = (tab: 'USER' | 'DEPT' | 'GROUP'): EntityType => {
  return tab === 'USER'
    ? 'User'
    : tab === 'DEPT'
      ? 'Dept'
      : 'Group';
};

/**
 * Get entity type name for display
 * Generates the localized name for the current entity type
 * to be used in search placeholders and other UI elements
 * @param type - Entity type
 * @param t - i18n translation function
 * @returns Localized entity type name
 */
export const getEntityTypeName = (type: 'USER' | 'DEPT' | 'GROUP', t: (key: string) => string): string => {
  return type === 'DEPT'
    ? t('permission.check.dept')
    : type === 'GROUP'
      ? t('permission.check.group')
      : t('permission.check.user');
};

/**
 * Get entity type name for delete confirmation
 * Returns the appropriate entity type name for delete confirmation messages
 * @param tab - Current active tab
 * @param t - i18n translation function
 * @returns Entity type name for confirmation
 */
export const getEntityTypeNameForDelete = (tab: 'USER' | 'DEPT' | 'GROUP', t: (key: string) => string): string => {
  return tab === 'USER'
    ? t('permission.check.user')
    : tab === 'GROUP'
      ? t('permission.check.dept')
      : t('permission.check.group');
};

/**
 * Handle pagination changes
 * Updates pagination state and triggers data reload
 * @param page - Pagination change event
 * @param pagination - Current pagination state
 * @param loadFunction - Function to reload data
 */
export const handlePaginationChange = (
  page: { current: number, pageSize: number },
  pagination: { current: number, pageSize: number },
  loadFunction: () => void
) => {
  pagination.current = page.current;
  pagination.pageSize = page.pageSize;
  loadFunction();
};

/**
 * Create table columns for user policy table
 * Defines the structure and display properties for the user policy table
 * @param t - i18n translation function
 * @returns Array of table column configurations
 */
export const createUserPolicyColumns = (t: (key: string) => string) => [
  {
    key: 'id',
    title: t('permission.columns.assocPolicies.id'),
    dataIndex: 'id',
    width: '13%'
  },
  {
    key: 'name',
    title: t('permission.columns.assocPolicies.name'),
    dataIndex: 'name',
    width: '20%'
  },
  {
    key: 'code',
    title: t('permission.columns.assocPolicies.code'),
    dataIndex: 'code'
  },
  {
    key: 'orgType',
    title: t('permission.columns.assocPolicies.source'),
    dataIndex: 'orgType',
    width: '20%'
  },
  {
    key: 'authByName',
    title: t('permission.columns.assocPolicies.authByName'),
    dataIndex: 'authByName',
    width: '12%'
  },
  {
    key: 'authDate',
    title: t('permission.columns.assocPolicies.authDate'),
    dataIndex: 'authDate',
    width: '12%'
  },
  {
    key: 'action',
    title: t('common.actions.operation'),
    dataIndex: 'action',
    width: 82,
    align: 'center' as const
  }
];

/**
 * Create table columns for department/group policy table
 * Defines the structure and display properties for department and group tables
 * @param t - i18n translation function
 * @returns Array of table column configurations
 */
export const createDeptOrGroupPolicyColumns = (t: (key: string) => string) => [
  {
    key: 'id',
    title: t('permission.columns.assocPolicies.id'),
    dataIndex: 'id',
    width: '16%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'name',
    title: t('permission.columns.assocPolicies.name'),
    dataIndex: 'name',
    width: '20%'
  },
  {
    key: 'code',
    title: t('permission.columns.assocPolicies.code'),
    dataIndex: 'code'
  },
  {
    key: 'authByName',
    title: t('permission.columns.assocPolicies.authByName'),
    dataIndex: 'authByName',
    width: '15%'
  },
  {
    key: 'authDate',
    title: t('permission.columns.assocPolicies.authDate'),
    dataIndex: 'authDate',
    width: '16%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'action',
    title: t('common.actions.operation'),
    dataIndex: 'action',
    width: 82,
    align: 'center' as const,
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  }
];
