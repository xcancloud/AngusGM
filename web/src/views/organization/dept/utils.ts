import { Ref, nextTick, reactive } from 'vue';
import { useI18n } from 'vue-i18n';
import { notification, modal } from '@xcan-angus/vue-ui';
import { PageQuery, SearchCriteria, utils } from '@xcan-angus/infra';
import { debounce } from 'throttle-debounce';
import { duration } from '@xcan-angus/infra';
import { dept, auth } from '@/api';
import type {
  DeptState,
  DeptInfo,
  TreeRecordType,
  UserRecordType,
  CurrentActionNode,
  PolicyPagination,
  DeptCountResponse,
  NavigationResponse
} from './types';

/**
 * <p>
 * Enhanced error handling for API operations
 * </p>
 * Provides consistent error messaging and logging
 */
export const handleApiError = (error: any, operation: string): void => {
  console.error(`Error in ${operation}:`, error);
  const { t } = useI18n();
  notification.error(t('common.messages.operationFailed'));
};

/**
 * <p>
 * Load user list for selected department
 * </p>
 * Handles loading state and error notifications
 */
export const loadUser = async (
  state: DeptState,
  params: Ref<PageQuery>,
  total: Ref<number>,
  userLoading: Ref<boolean>,
  userSearchName: Ref<string | undefined>
): Promise<void> => {
  const { t } = useI18n();
  
  if (!state.currentSelectedNode.id) {
    notification.warning(t('common.messages.queryListFailed'));
    return;
  }

  if (userLoading.value) {
    return;
  }

  try {
    userLoading.value = true;

    // Prepare filters for search
    if (userSearchName.value && params.value.filters) {
      params.value.filters.push({ 
        key: 'fullName', 
        op: SearchCriteria.OpEnum.MatchEnd, 
        value: userSearchName.value 
      });
    } else {
      params.value.filters = [];
    }

    const [error, res] = await dept.getDeptUsers(state.currentSelectedNode.id, params.value);

    if (error) {
      notification.error(t('common.messages.queryListFailed'));
      return;
    }

    state.userDataSource = res.data.list;
    total.value = +res.data.total;
  } catch (error) {
    console.error('Error loading users:', error);
    notification.error(t('common.messages.networkError'));
  } finally {
    userLoading.value = false;
  }
};

/**
 * <p>
 * Load department information
 * </p>
 * Updates deptInfo with current department details
 */
export const loadDeptInfo = async (
  state: DeptState,
  deptInfo: DeptInfo
): Promise<void> => {
  const { t } = useI18n();
  
  if (!state.currentSelectedNode.id) return;

  try {
    const [error, { data = {} }] = await dept.getDeptDetail(state.currentSelectedNode.id);
    if (error) {
      notification.error(t('common.messages.queryListFailed'));
      return;
    }

    // Update department info with proper type safety
    Object.assign(deptInfo, {
      name: data.name || '',
      code: data.code || '',
      id: data.id || '',
      createdByName: data.createdByName || '',
      createdDate: data.createdDate || '',
      tags: data.tags || [],
      level: data.level || '',
      lastModifiedDate: data.lastModifiedDate || '',
      lastModifiedByName: data.lastModifiedByName || '--'
    });
  } catch (error) {
    console.error('Error loading department info:', error);
    notification.error(t('common.messages.networkError'));
  }
};

/**
 * <p>
 * Add users to department
 * </p>
 */
export const addDeptUser = async (
  userIds: string[],
  state: DeptState,
  userUpdateLoading: Ref<boolean>,
  isRefresh: Ref<boolean>
): Promise<void> => {
  const { t } = useI18n();

  try {
    userUpdateLoading.value = true;
    const [error] = await dept.createDeptUser(state.currentSelectedNode.id as string, userIds);
    if (error) {
      handleApiError(error, 'add users to department');
      return;
    }

    isRefresh.value = true;
  } catch (error) {
    handleApiError(error, 'add users to department');
  } finally {
    userUpdateLoading.value = false;
  }
};

/**
 * <p>
 * Remove users from department
 * </p>
 */
export const delDeptUser = async (
  userIds: string[],
  type: 'Table' | 'Modal' | undefined,
  state: DeptState,
  params: Ref<PageQuery>,
  total: Ref<number>,
  userUpdateLoading: Ref<boolean>,
  userLoadDisabled: Ref<boolean>,
  isRefresh: Ref<boolean>
): Promise<void> => {
  const { t } = useI18n();

  try {
    userUpdateLoading.value = true;
    const [error] = await dept.deleteDeptUser(state.currentSelectedNode.id as string, userIds);
    if (error) {
      handleApiError(error, 'remove users from department');
      return;
    }

    if (type === 'Modal') {
      isRefresh.value = true;
    }

    params.value.pageNo = utils.getCurrentPage(
      params.value.pageNo as number,
      params.value.pageSize as number,
      total.value
    );

    // Refresh table data for table operations
    if (type === 'Table') {
      userLoadDisabled.value = true;
      await loadUser(
        state,
        params,
        total,
        reactive({ value: false }),
        reactive({ value: undefined })
      );
      userLoadDisabled.value = false;
    }
  } catch (error) {
    handleApiError(error, 'remove users from department');
  } finally {
    userUpdateLoading.value = false;
  }
};

/**
 * <p>
 * Optimized department search with better error handling
 * </p>
 */
export const handleSearchDept = async (
  value: any,
  state: DeptState,
  selectedDept: Ref<string | undefined>,
  changeSelectHandler: (selectedKeys: string[], selected: boolean, info: TreeRecordType) => Promise<void>
): Promise<void> => {
  if (!value) return;

  try {
    const [error, res] = await dept.getNavigationByDeptId({ id: value });
    if (error) {
      handleApiError(error, 'department search');
      return;
    }

    const parentChain = (res.data.parentChain || []).map((item: any) => ({ 
      ...item, 
      hasSubDept: true 
    }));
    const { id, pid, name } = res.data.current;

    await nextTick();
    selectedDept.value = id;
    
    // Ensure all tree data has required properties and filter out undefined values
    const validParentChain = parentChain.filter((item: any) => item.id && item.name);
    const validCurrent = res.data.current.id && res.data.current.name ? res.data.current : null;
    
    // Filter out items with undefined id or name to ensure ITreeOption compatibility
    const filteredDataSource = validCurrent ? [...validParentChain, validCurrent] : validParentChain;
    state.dataSource = filteredDataSource.filter((item: any) => item.id && item.name);
    
    await changeSelectHandler([id], true, { id, pid, name });
  } catch (error) {
    handleApiError(error, 'department search');
  }
};

/**
 * <p>
 * Handle tag search
 * </p>
 */
export const handleSearchTag = (
  value: any,
  searchTagId: Ref<string | undefined>,
  searchDeptId: Ref<string | undefined>,
  state: DeptState,
  changeSelectHandler: (selectedKeys: string[], selected: boolean, info: TreeRecordType) => Promise<void>
): void => {
  searchTagId.value = value;
  if (searchDeptId.value) {
    searchDeptId.value = undefined;
  }
  changeSelectHandler([], false, state.currentSelectedNode);
};

/**
 * <p>
 * Delete department with confirmation
 * </p>
 * Checks for child departments and users before deletion
 */
export const deleteDepartment = async (
  currentActionNode: Ref<CurrentActionNode>,
  state: DeptState,
  treeSelect: any,
  searchDeptId: Ref<string | undefined>,
  notify: Ref<number>
): Promise<void> => {
  const { t } = useI18n();

  try {
    const [error, res] = await dept.getDeptCount(currentActionNode.value.id as string);
    if (error) {
      handleApiError(error, 'get department count');
      return;
    }

    const { subDeptNum, sunUserNum }: DeptCountResponse = res.data;
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

    modal.confirm({
      centered: true,
      title: t('department.actions.deleteDept'),
      content: t('department.messages.confirmDelete', { content }),
      onOk: async () => {
        try {
          const [error] = await dept.deleteDept({ ids: [currentActionNode.value.id as string] });
          if (error) {
            handleApiError(error, 'delete department');
            return;
          }

          notification.success(t('common.messages.deleteSuccess'));
          treeSelect.value.del(currentActionNode.value.id);

          if (currentActionNode.value.pid && +currentActionNode.value.pid < 0 && !searchDeptId.value) {
            notify.value += 1;
          }

          if (currentActionNode.value.id === state.currentSelectedNode.id) {
            state.currentSelectedNode.id = undefined;
            state.currentSelectedNode.pid = undefined;
            state.currentSelectedNode.name = undefined;
            state.userDataSource = [];
          }
        } catch (error) {
          handleApiError(error, 'delete department');
        }
      }
    });
  } catch (error) {
    handleApiError(error, 'get department count');
  }
};

/**
 * <p>
 * Save tag associations for department
 * </p>
 */
export const saveDeptTags = async (
  _tagIds: string[],
  _tags: { id: string; name: string }[],
  deleteTagIds: string[],
  currentActionNode: Ref<CurrentActionNode>,
  editTagVisible: Ref<boolean>,
  deptInfo: DeptInfo,
  state: DeptState
): Promise<void> => {
  try {
    // Add new tags
    if (_tagIds.length) {
      await dept.addDeptTags({ 
        id: currentActionNode.value.id as string, 
        ids: _tagIds 
      });
    }

    // Delete tags
    if (deleteTagIds.length) {
      await dept.deleteDeptTag({ 
        id: currentActionNode.value.id as string, 
        ids: deleteTagIds 
      });
    }

    editTagVisible.value = false;
    await loadDeptInfo(state, deptInfo);
  } catch (error) {
    handleApiError(error, 'save tags');
  }
};

/**
 * <p>
 * Move department to new parent
 * </p>
 */
export const moveDepartment = async (
  targetId: string,
  currentActionNode: Ref<CurrentActionNode>,
  moveVisible: Ref<boolean>,
  notify: Ref<number>
): Promise<void> => {
  const { t } = useI18n();

  try {
    const [error] = await dept.updateDept([{ 
      pid: targetId, 
      id: currentActionNode.value.id 
    }]);

    if (error) {
      notification.error(t('common.messages.operationFailed'));
      return;
    }

    moveVisible.value = false;
    notify.value += 1;
  } catch (error) {
    console.error('Error moving department:', error);
    notification.error(t('common.messages.networkError'));
  }
};

/**
 * <p>
 * Get department policies
 * </p>
 */
export const getDeptPolicy = async (
  state: DeptState,
  policyPagination: PolicyPagination,
  policyFilters: Ref<any[]>,
  policyLoading: Ref<boolean>,
  policyData: Ref<any[]>
): Promise<void> => {
  const { t } = useI18n();

  if (policyLoading.value) return;

  try {
    const { pageSize, current } = policyPagination;
    policyLoading.value = true;

    const [error, res] = await auth.getDeptPolicy(state.currentSelectedNode.id as string, {
      pageSize,
      pageNo: current,
      filters: policyFilters.value
    });

    if (error) {
      notification.error(t('common.messages.operationFailed'));
      return;
    }

    policyData.value = res.data?.list || [];
    policyPagination.total = res.data.total;
  } catch (error) {
    console.error('Error loading department policies:', error);
    notification.error(t('common.messages.networkError'));
  } finally {
    policyLoading.value = false;
  }
};

/**
 * <p>
 * Save policy associations for department
 * </p>
 */
export const saveDeptPolicies = async (
  addIds: string[],
  state: DeptState,
  policyVisible: Ref<boolean>,
  policyUpdateLoading: Ref<boolean>,
  policyLoadDisabled: Ref<boolean>,
  policyPagination: PolicyPagination,
  policyFilters: Ref<any[]>,
  policyLoading: Ref<boolean>,
  policyData: Ref<any[]>
): Promise<void> => {
  const { t } = useI18n();

  if (!addIds.length) {
    policyVisible.value = false;
    return;
  }

  try {
    policyUpdateLoading.value = true;
    const [error] = await auth.addPolicyByDept(state.currentSelectedNode.id as string, addIds);
    if (error) {
      notification.error(t('common.messages.operationFailed'));
      return;
    }

    policyVisible.value = false;
    policyLoadDisabled.value = true;
    await getDeptPolicy(state, policyPagination, policyFilters, policyLoading, policyData);
    policyLoadDisabled.value = false;
  } catch (error) {
    console.error('Error saving policies:', error);
    notification.error(t('common.messages.networkError'));
  } finally {
    policyUpdateLoading.value = false;
  }
};

/**
 * <p>
 * Cancel/remove policy from department
 * </p>
 */
export const cancelDeptPolicy = async (
  delId: string,
  state: DeptState,
  policyLoadDisabled: Ref<boolean>,
  policyPagination: PolicyPagination,
  policyFilters: Ref<any[]>,
  policyLoading: Ref<boolean>,
  policyData: Ref<any[]>
): Promise<void> => {
  const { t } = useI18n();

  try {
    const [error] = await auth.deletePolicyByDept(state.currentSelectedNode.id as string, [delId]);
    if (error) {
      notification.error(t('common.messages.operationFailed'));
      return;
    }

    policyLoadDisabled.value = true;
    await getDeptPolicy(state, policyPagination, policyFilters, policyLoading, policyData);
    policyLoadDisabled.value = false;
  } catch (error) {
    console.error('Error canceling policy:', error);
    notification.error(t('common.messages.networkError'));
  }
};

/**
 * <p>
 * Create debounced search function for users
 * </p>
 */
export const createUserSearchHandler = (
  state: DeptState,
  params: Ref<PageQuery>,
  total: Ref<number>,
  userLoading: Ref<boolean>,
  userSearchName: Ref<string | undefined>
) => {
  return debounce(duration.search, () => {
    params.value.pageNo = 1;
    loadUser(state, params, total, userLoading, userSearchName);
  });
};

/**
 * <p>
 * Create debounced search function for policies
 * </p>
 */
export const createPolicySearchHandler = (
  state: DeptState,
  policyPagination: PolicyPagination,
  policyFilters: Ref<any[]>,
  policyLoading: Ref<boolean>,
  policyData: Ref<any[]>,
  policyLoadDisabled: Ref<boolean>
) => {
  return debounce(duration.search, async (event: any): Promise<void> => {
    const value = event.target.value;
    policyPagination.current = 1;

    if (value) {
      policyFilters.value = [{ 
        key: 'name', 
        op: SearchCriteria.OpEnum.MatchEnd, 
        value: value 
      }];
    } else {
      policyFilters.value = [];
    }

    policyLoadDisabled.value = true;
    await getDeptPolicy(state, policyPagination, policyFilters, policyLoading, policyData);
    policyLoadDisabled.value = false;
  });
};

/**
 * <p>
 * Handle table pagination changes
 * </p>
 */
export const handleTableChange = async (
  _pagination: any,
  state: DeptState,
  params: Ref<PageQuery>,
  total: Ref<number>,
  userLoading: Ref<boolean>,
  userSearchName: Ref<string | undefined>,
  userLoadDisabled: Ref<boolean>
): Promise<void> => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;

  userLoadDisabled.value = true;
  await loadUser(state, params, total, userLoading, userSearchName);
  userLoadDisabled.value = false;
};

/**
 * <p>
 * Handle policy table pagination changes
 * </p>
 */
export const handlePolicyTableChange = async (
  page: any,
  state: DeptState,
  policyPagination: PolicyPagination,
  policyFilters: Ref<any[]>,
  policyLoading: Ref<boolean>,
  policyData: Ref<any[]>,
  policyLoadDisabled: Ref<boolean>
): Promise<void> => {
  policyPagination.current = page.current;
  policyPagination.pageSize = page.pageSize;

  policyLoadDisabled.value = true;
  await getDeptPolicy(state, policyPagination, policyFilters, policyLoading, policyData);
  policyLoadDisabled.value = false;
};