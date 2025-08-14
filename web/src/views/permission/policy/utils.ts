/**
 * Utility functions for permission policy management
 * Contains helper functions extracted from policy components
 */

import type { ApisType, DataRecordType } from './types';

/**
 * Regular expression to filter out certain resource names
 * Used for filtering API resources in policy management
 */
export const END_REG = /.*(Door|pub)$/g;

/**
 * Find parent paths for selected nodes in a tree structure
 * @param oldList - Previously selected nodes
 * @param pid - Parent ID to search for
 * @param dataSource - Tree data source
 * @returns Array of parent nodes
 */
export const findParentPath = (oldList: DataRecordType[], pid: string, dataSource: DataRecordType[]): DataRecordType[] => {
  const parentPaths: DataRecordType[] = [];

  // Convert tree to flat array
  const flattenTree = (list: DataRecordType[]): DataRecordType[] => {
    const res: DataRecordType[] = [];
    list.forEach(item => {
      res.push(item);
      item.children && res.push(...flattenTree(item.children));
    });
    return res;
  };

  const tempList = flattenTree(dataSource);

  // Find parent by ID recursively
  const findParent = (parentId: string) => {
    const item = tempList.find(v => v.id === parentId);
    if (item) {
      // Don't add if already exists or is self
      if (!oldList.find(v => v.id === item.id) && item.pid !== pid) {
        parentPaths.unshift(item);
      }
      if (`${item.pid}` !== '-1') {
        findParent(item.pid);
      }
    }
  };

  findParent(pid);
  return parentPaths;
};

/**
 * Process application functions and set disabled state
 * @param funcs - Raw function data
 * @returns Processed function tree with disabled state
 */
export const handleAppFunctions = (funcs: DataRecordType[] = []): DataRecordType[] => {
  if (!funcs.length) {
    return [];
  }

  function processTree (data: DataRecordType[]): DataRecordType[] {
    return data.map(item => {
      return {
        ...item,
        disabled: !item.authCtrl,
        children: processTree(item.children || [])
      };
    });
  }

  return processTree(funcs);
};

/**
 * Process function display data from checked nodes
 * @param checkedNodes - Array of checked tree nodes
 * @returns Processed function display object
 */
export const processFunctionDisplay = (checkedNodes: DataRecordType[]): { [key: string]: Array<ApisType> } => {
  const resultMap: { [key: string]: Array<ApisType> } = {};

  checkedNodes.forEach((item: DataRecordType) => {
    (item.apis || []).forEach(childItem => {
      // Deduplicate by API ID
      if (resultMap[childItem.resourceName]) {
        if (!resultMap[childItem.resourceName].find(v => v.id === childItem.id)) {
          resultMap[childItem.resourceName].push(childItem);
        }
      } else if (!childItem.resourceName.match(END_REG)) {
        resultMap[childItem.resourceName] = [childItem];
      }
    });
  });

  return resultMap;
};

/**
 * Get human-readable type name for display purposes
 * @param type - Target type enum
 * @param t - i18n translation function
 * @returns Localized display text
 */
export const getTypeName = (type: 'USER' | 'DEPT' | 'GROUP', t: (key: string) => string): string => {
  return type === 'USER'
    ? t('permission.policy.detail.target.user')
    : type === 'GROUP'
      ? t('permission.policy.detail.target.group')
      : t('permission.policy.detail.target.dept');
};

/**
 * Get configuration for add button text based on target type
 * @param type - Target type enum
 * @returns Authorization code for the button
 */
export const getAddButtonConfig = (type: 'USER' | 'DEPT' | 'GROUP'): string => {
  const config = {
    USER: 'AuthUserAdd',
    DEPT: 'AuthDeptAdd',
    GROUP: 'AuthGroupAdd'
  };
  return config[type];
};

/**
 * Get configuration for cancel button text based on target type
 * @param type - Target type enum
 * @returns Cancellation code for the button
 */
export const getCancelButtonConfig = (type: 'USER' | 'DEPT' | 'GROUP'): string => {
  const config = {
    USER: 'AuthUserCancel',
    DEPT: 'AuthDeptCancel',
    GROUP: 'AuthGroupCancel'
  };
  return config[type];
};

/**
 * Process data source to disable checkbox operations
 * Recursively processes the tree data to set appropriate disabled states
 * @param data - Tree data to process
 * @returns Array of checked nodes
 */
export const processDataSourceForCheckboxes = (data: DataRecordType[]): DataRecordType[] => {
  const checkedNodes: DataRecordType[] = [];

  const processTree = (list: DataRecordType[]) => {
    list.forEach((item) => {
      checkedNodes.push(item);
      if (item.children && Array.isArray(item.children)) {
        processTree(item.children);
      }
    });
  };

  processTree(data);
  return checkedNodes;
};

/**
 * Get user-defined policies from the record
 * @param record - Data record
 * @returns Filtered user-defined policies
 */
export const getUserDefinedPolicies = (record: any): any[] => {
  return record.defaultPolicies.filter((v: any) => v.type.value === 'USER_DEFINED');
};

/**
 * Get the currently selected user-defined policy ID
 * @param record - Data record
 * @returns Selected policy ID or undefined
 */
export const getUserDefinePolicyId = (record: any): string | undefined => {
  const userDefinedPolicies = getUserDefinedPolicies(record);
  return userDefinedPolicies.find(policy => policy.id === record.policyId) ? record.policyId : undefined;
};

/**
 * Check if policy is a default policy for tip display
 * @param id - Policy ID to check
 * @param defaultPolicies - Array of default policy IDs
 * @returns 1 if default, 0 if not
 */
export const showTip = (id: string, defaultPolicies: string[]): number => {
  return defaultPolicies.includes(id) ? 1 : 0;
};

/**
 * Provide safe text for possibly empty values
 * @param value - Value to check
 * @returns Value as string or dash if empty
 */
export const textOrDash = (value?: any): string => {
  return (value === undefined || value === null || value === '' ? '--' : String(value));
};

/**
 * Resolve an i18n key with a fallback plain text when the key is missing
 * @param key - i18n key to resolve
 * @param fallback - Fallback text if key is missing
 * @param t - i18n translation function
 * @returns Resolved text or fallback
 */
export const resolveI18nKey = (key: string, fallback: string, t: (key: string) => string): string => {
  const msg = t(key);
  return msg === key ? fallback : msg;
};
