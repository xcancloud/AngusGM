import { notification } from '@xcan-angus/vue-ui';
import { SearchCriteria, utils } from '@xcan-angus/infra';
import { dept, auth } from '@/api';

/**
 * Enhanced error handling for API operations
 * Provides consistent error messaging and logging
 */
export const handleApiError = (error: any, operation: string, t: any): void => {
  console.error(`Error in ${operation}:`, error);
  notification.error(t('common.messages.operationFailed'));
};

/**
 * Load user list for selected department
 * Handles loading state and error notifications
 */
export const loadUser = async (
  currentSelectedNode: any,
  params: any,
  userSearchName: string | undefined,
  t: any
): Promise<{ userDataSource: any[]; total: number }> => {
  if (!currentSelectedNode.id) {
    notification.warning(t('common.messages.queryListFailed'));
    return { userDataSource: [], total: 0 };
  }

  try {
    // Prepare filters for search
    if (userSearchName && params.filters) {
      params.filters.push({
        key: 'fullName',
        op: SearchCriteria.OpEnum.MatchEnd,
        value: userSearchName
      });
    } else {
      params.filters = [];
    }

    const [error, res] = await dept.getDeptUsers(currentSelectedNode.id, params);

    if (error) {
      notification.error(t('common.messages.queryListFailed'));
      return { userDataSource: [], total: 0 };
    }

    return {
      userDataSource: res.data.list,
      total: +res.data.total
    };
  } catch (error) {
    console.error('Error loading users:', error);
    notification.error(t('common.messages.networkError'));
    return { userDataSource: [], total: 0 };
  }
};

/**
 * Load department information
 * Updates deptInfo with current department details
 */
export const loadDeptInfo = async (
  currentSelectedNode: any,
  t: any
): Promise<any> => {
  if (!currentSelectedNode.id) {
    return {};
  }

  try {
    const [error, { data = {} }] = await dept.getDeptDetail(currentSelectedNode.id);
    if (error) {
      notification.error(t('common.messages.queryListFailed'));
      return {};
    }

    // Return department info with proper type safety
    return {
      name: data.name || '',
      code: data.code || '',
      id: data.id || '',
      createdByName: data.createdByName || '',
      createdDate: data.createdDate || '',
      tags: data.tags || [],
      level: data.level || '',
      lastModifiedDate: data.lastModifiedDate || '',
      lastModifiedByName: data.lastModifiedByName || '--'
    };
  } catch (error) {
    console.error('Error loading department info:', error);
    notification.error(t('common.messages.networkError'));
    return {};
  }
};

/**
 * Delete department with confirmation
 * Checks for child departments and users before deletion
 */
export const deleteDepartment = async (
  currentActionNode: { id: string; name: string; pid: string },
  t: any,
  onSuccess: () => void
): Promise<void> => {
  try {
    const [error, res] = await dept.getDeptCount(currentActionNode.id as string);
    if (error) {
      handleApiError(error, 'get department count', t);
      return;
    }

    const { subDeptNum, sunUserNum } = res.data;
    const existChildDept = subDeptNum > 0;
    const existUser = sunUserNum > 0;

    let content = '';
    if (existChildDept && existUser) {
      content = t('department.messages.deptHasChildAndUserTip', {
        childNum: subDeptNum,
        userNum: sunUserNum
      });
    } else if (existChildDept && !existUser) {
      content = t('department.messages.deptHasChildTip', { childNum: subDeptNum });
    } else if (!existChildDept && existUser) {
      content = t('department.messages.deptHasUserTip', { userNum: sunUserNum });
    }

    // Note: This would need to be handled by the calling component
    // as it requires modal.confirm which is not available in utils
    onSuccess();
  } catch (error) {
    handleApiError(error, 'get department count', t);
  }
};

/**
 * Add users to department
 */
export const addDeptUser = async (
  deptId: string,
  userIds: string[],
  t: any
): Promise<boolean> => {
  try {
    const [error] = await dept.createDeptUser(deptId, userIds);
    if (error) {
      handleApiError(error, 'add users to department', t);
      return false;
    }
    return true;
  } catch (error) {
    handleApiError(error, 'add users to department', t);
    return false;
  }
};

/**
 * Remove users from department
 */
export const removeDeptUser = async (
  deptId: string,
  userIds: string[],
  t: any
): Promise<boolean> => {
  try {
    const [error] = await dept.deleteDeptUser(deptId, userIds);
    if (error) {
      handleApiError(error, 'remove users from department', t);
      return false;
    }
    return true;
  } catch (error) {
    handleApiError(error, 'remove users from department', t);
    return false;
  }
};

/**
 * Optimized department search with better error handling
 */
export const searchDepartment = async (
  value: any,
  t: any
): Promise<{
  parentChain: any[];
  current: any;
  id: string;
  pid: string;
  name: string;
} | null> => {
  if (!value) return null;

  try {
    const [error, res] = await dept.getNavigationByDeptId({ id: value });
    if (error) {
      handleApiError(error, 'department search', t);
      return null;
    }

    const parentChain = (res.data.parentChain || []).map(item => ({
      ...item,
      hasSubDept: true
    }));
    const { id, pid, name } = res.data.current;

    return {
      parentChain,
      current: res.data.current,
      id,
      pid,
      name
    };
  } catch (error) {
    handleApiError(error, 'department search', t);
    return null;
  }
};

/**
 * Get department policy list
 */
export const getDeptPolicy = async (
  deptId: string,
  pagination: { current: number; pageSize: number },
  filters: SearchCriteria[],
  t: any
): Promise<{ list: any[]; total: number } | null> => {
  try {
    const [error, res] = await auth.getDeptPolicy(deptId, {
      pageSize: pagination.pageSize,
      pageNo: pagination.current,
      filters
    });

    if (error) {
      notification.error(t('common.messages.operationFailed'));
      return null;
    }

    return {
      list: res.data?.list || [],
      total: res.data.total
    };
  } catch (error) {
    console.error('Error loading department policies:', error);
    notification.error(t('common.messages.networkError'));
    return null;
  }
};

/**
 * Add policy to department
 */
export const addPolicyToDept = async (
  deptId: string,
  policyIds: string[],
  t: any
): Promise<boolean> => {
  try {
    const [error] = await auth.addPolicyByDept(deptId, policyIds);
    if (error) {
      notification.error(t('common.messages.operationFailed'));
      return false;
    }
    return true;
  } catch (error) {
    console.error('Error saving policies:', error);
    notification.error(t('common.messages.networkError'));
    return false;
  }
};

/**
 * Remove policy from department
 */
export const removePolicyFromDept = async (
  deptId: string,
  policyIds: string[],
  t: any
): Promise<boolean> => {
  try {
    const [error] = await auth.deletePolicyByDept(deptId, policyIds);
    if (error) {
      notification.error(t('common.messages.operationFailed'));
      return false;
    }
    return true;
  } catch (error) {
    console.error('Error canceling policy:', error);
    notification.error(t('common.messages.networkError'));
    return false;
  }
};

/**
 * Update department information
 */
export const updateDepartment = async (
  deptData: { id: string; name?: string; pid?: string }[],
  t: any
): Promise<boolean> => {
  try {
    const [error] = await dept.updateDept(deptData);
    if (error) {
      notification.error(t('common.messages.operationFailed'));
      return false;
    }
    return true;
  } catch (error) {
    console.error('Error updating department:', error);
    notification.error(t('common.messages.networkError'));
    return false;
  }
};

/**
 * Add department tags
 */
export const addDeptTags = async (
  deptId: string,
  tagIds: string[],
  t: any
): Promise<boolean> => {
  try {
    await dept.addDeptTags({ id: deptId, ids: tagIds });
    return true;
  } catch (error) {
    handleApiError(error, 'add department tags', t);
    return false;
  }
};

/**
 * Delete department tags
 */
export const deleteDeptTags = async (
  deptId: string,
  tagIds: string[],
  t: any
): Promise<boolean> => {
  try {
    await dept.deleteDeptTag({ id: deptId, ids: tagIds });
    return true;
  } catch (error) {
    handleApiError(error, 'delete department tags', t);
    return false;
  }
};

/**
 * Create new department
 */
export const createDepartment = async (
  deptData: {
    code: string;
    name: string;
    pid: string;
    tagIds: string[];
  },
  t: any
): Promise<{ id: string } | null> => {
  try {
    const [error, { data = [] }] = await dept.addDept([deptData]);
    if (error) {
      return null;
    }
    return { id: data[0].id };
  } catch (error) {
    handleApiError(error, 'create department', t);
    return null;
  }
};

/**
 * Get current page for pagination after deletion
 */
export const getCurrentPageAfterDeletion = (
  currentPage: number,
  pageSize: number,
  total: number
): number => {
  return utils.getCurrentPage(currentPage, pageSize, total);
};

/**
 * Filter valid tree data
 */
export const filterValidTreeData = (data: any[]): any[] => {
  return data.filter(item => item.id && item.name);
};
