<script setup lang="ts">
// Vue Composition API imports
import { computed, defineAsyncComponent, nextTick, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';

// Ant Design Vue UI components
import { Dropdown, Menu, MenuItem, TabPane, Tabs } from 'ant-design-vue';

// Custom UI components from @xcan-angus/vue-ui
import {
  AsyncComponent, ButtonAuth, IconRefresh, Image, Input,
  notification, PureCard, Select, Table, Tree
} from '@xcan-angus/vue-ui';

// Infrastructure utilities and constants
import { app, duration, GM, PageQuery, SearchCriteria } from '@xcan-angus/infra';
import { debounce } from 'throttle-debounce';
import { OrgTargetType } from '@/enums/enums';

// Local type definitions and interfaces
import {
  DeptInfo, DeptState, ActionNode, PolicyPagination, TreeParams, SearchDeptParams, TableColumn
} from './types';

// API services for department and authentication operations
import { dept } from '@/api';
import { createAuthPolicyColumns } from '@/views/organization/user/utils';

// Utility functions for department operations
import {
  loadUser as loadUserUtil, loadDeptInfo as loadDeptInfoUtil, deleteDepartment, addDeptUser as addDeptUserUtil,
  removeDeptUser, searchDepartment, getDeptPolicy as getDeptPolicyUtil, addPolicyToDept, removePolicyFromDept,
  updateDepartment, addDeptTags, deleteDeptTags, getCurrentPageAfterDeletion, filterValidTreeData
} from './utils';

/**
 * Async Component Definitions
 *
 * These components are loaded dynamically to improve initial page load performance.
 * Each modal component is imported only when needed.
 */
const SelectTagModal = defineAsyncComponent(() => import('@/components/TagModal/index.vue'));
const UserModal = defineAsyncComponent(() => import('@/components/UserModal/index.vue'));
const PolicyModal = defineAsyncComponent(() => import('@/components/PolicyModal/index.vue'));

// Department operation modal components
const Info = defineAsyncComponent(() => import('@/views/organization/dept/info/index.vue'));
const AddDeptModal = defineAsyncComponent(() => import('@/views/organization/dept/add/index.vue'));
const EditModal = defineAsyncComponent(() => import('@/views/organization/dept/edit/index.vue'));
const MoveDeptModal = defineAsyncComponent(() => import('@/views/organization/dept/move/index.vue'));

// Internationalization setup
const { t } = useI18n();

/**
 * Reactive State Management
 *
 * Centralized state object that manages all component data and UI states.
 * Uses Vue 3's reactive system for automatic reactivity.
 */
const state = reactive<DeptState>({
  sortForm: {}, // Form data for sorting operations
  searchForm: [], // Form data for search operations
  loading: false, // Global loading state
  dataSource: [], // Department data source for tree display
  currentRecord: undefined, // Currently selected department record
  addDeptVisible: false, // Add department modal visibility
  editDeptNameVisible: false, // Edit department name modal visibility
  userLoading: false, // User loading state
  currentSelectedNode: { id: undefined, name: undefined, pid: undefined }, // Currently selected tree node
  userDataSource: [], // User data source for the selected department
  concatUserSource: [] // Concatenated user source for operations
});

/**
 * Tree and Search Related References
 *
 * These refs manage the department tree component and search functionality.
 */
const treeSelect = ref(); // Reference to the tree component
const searchDeptId = ref<string>(); // Search department ID for filtering
const searchTagId = ref<string>(); // Search tag ID for filtering
const selectedDept = ref<string>(); // Currently selected department ID

/**
 * Search Parameters and Pagination
 *
 * Manages search criteria and pagination state for user lists.
 */
const params = ref<PageQuery>({
  pageNo: 1, // Current page number
  pageSize: 10, // Number of items per page
  filters: [] // Search filters
});
const total = ref(0); // Total number of items

/**
 * Loading States
 *
 * Individual loading states for different operations to provide
 * granular control over UI feedback.
 */
const userLoading = ref(false); // User data loading state
const treeLoading = ref(false); // Tree data loading state
const refreshDisabled = ref(false); // Refresh button disabled state
const userLoadDisabled = ref(false); // User load operation disabled state
const policyLoadDisabled = ref(false); // Policy load operation disabled state
const policyUpdateLoading = ref(false); // Policy update loading state

/**
 * Modal Visibility States
 *
 * Controls the visibility of various modal dialogs for different operations.
 */
const addModalVisible = ref(false); // Add department modal
const editModalVisible = ref(false); // Edit department modal
const editTagVisible = ref(false); // Edit tags modal
const userVisible = ref(false); // User management modal
const moveVisible = ref(false); // Move department modal
const policyVisible = ref(false); // Policy management modal

/**
 * Form Data and Operation States
 *
 * Manages form inputs and operation-specific state variables.
 */
const addPid = ref('-1'); // Parent ID for new department
const userSearchName = ref<string>(); // User search name input
const isRefresh = ref(false); // Flag to trigger refresh after operations
const userUpdateLoading = ref(false); // User update operation loading state
const notify = ref(0); // Notification counter for tree refresh

/**
 * Current Action Node for Operations
 *
 * Stores the context of the currently selected node for right-click operations.
 * This is used by context menu actions like edit, delete, move, etc.
 */
const currentActionNode = ref<ActionNode>({ id: '', name: '', pid: '' });

/**
 * Department Information Object
 *
 * Reactive object that holds detailed information about the currently selected department.
 * This data is displayed in the info panel and used for various operations.
 */
const deptInfo = reactive<DeptInfo>({
  createdByName: '', // Name of the user who created the department
  createdDate: '', // Date when the department was created
  tags: [], // Associated tags for the department
  level: '', // Hierarchical level of the department
  name: '', // Department name
  code: '', // Department code
  id: '', // Department unique identifier
  lastModifiedDate: '', // Date of last modification
  lastModifiedByName: '' // Name of the user who last modified the department
});

/**
 * Policy Management State
 *
 * Manages policy-related data and pagination for the policy management tab.
 */
const policyData = ref<any[]>([]); // Policy data source
const policyPagination = reactive<PolicyPagination>({ // Policy pagination state
  current: 1, // Current page
  pageSize: 10, // Items per page
  total: 0 // Total number of policies
});
const policyFilters = ref<SearchCriteria[]>([]); // Policy search filters
const policyLoading = ref(false); // Policy loading state

/**
 * Computed Properties
 *
 * Derived values that automatically update when their dependencies change.
 * These provide reactive data transformations for the UI.
 */

/**
 * Pagination configuration for user tables
 * Automatically updates when page parameters or total count changes.
 */
const pagination = computed(() => ({
  current: params.value.pageNo,
  pageSize: params.value.pageSize,
  total: total.value
}));

/**
 * Search parameters for department search functionality
 * Provides reactive search criteria based on current tag selection.
 */
const getSearchDeptParams = computed<SearchDeptParams>(() => ({
  tagId: searchTagId.value,
  fullTextSearch: true
}));

/**
 * Tree loading parameters for department hierarchy
 * Configures how the department tree loads and displays data.
 */
const treeParams = computed<TreeParams>(() => ({
  pid: -1, // Root parent ID
  pageSize: 30, // Number of departments to load per request
  tagId: searchTagId.value, // Current tag filter
  orderBy: 'createdDate', // Sort by creation date
  orderSort: 'ASC' // Ascending order
}));

/**
 * Tree field mapping configuration
 * Maps component properties to tree component expected field names.
 */
const treeFieldNames = {
  title: 'name', // Display text for tree nodes
  key: 'id', // Unique identifier for tree nodes
  children: 'hasSubDept' // Child nodes indicator
};

/**
 * Tree Selection Change Handler
 *
 * Handles when a user selects a different department in the tree.
 * This is the main entry point for loading department-specific data.
 */
const changeSelect = async (
  _selectedKeys: string[],
  selected: boolean,
  info: { id: string | undefined; name: string | undefined; pid: string | undefined }
): Promise<void> => {
  const { id, name, pid } = info;

  if (selected) {
    // Update current selection state
    state.currentSelectedNode = { id, name, pid };

    // Reset pagination to first page for new selection
    params.value.pageNo = 1;
    policyPagination.current = 1;

    try {
      // Load users for the selected department
      userLoadDisabled.value = true;
      const userResult = await loadUserUtil(state.currentSelectedNode, params.value, userSearchName.value, t);
      state.userDataSource = userResult.userDataSource;
      total.value = userResult.total;
      userLoadDisabled.value = false;

      // Load department information
      const deptInfoResult = await loadDeptInfoUtil(state.currentSelectedNode, t);
      Object.assign(deptInfo, deptInfoResult);

      // Load department policies
      policyLoadDisabled.value = true;
      await getDeptPolicy();
      policyLoadDisabled.value = false;
    } catch (error) {
      console.error('Error in changeSelect:', error);
      notification.error(t('common.messages.networkError'));
    }
  } else {
    // Clear selection state when deselecting
    state.currentSelectedNode = { id: undefined, name: undefined, pid: undefined };
  }
};

/**
 * Load User List for Selected Department
 *
 * Fetches and displays the list of users associated with the currently selected department.
 * Handles loading states and error notifications.
 */
const loadUser = async (): Promise<void> => {
  if (userLoading.value) {
    return; // Prevent multiple simultaneous requests
  }

  try {
    userLoading.value = true;
    const userResult = await loadUserUtil(state.currentSelectedNode, params.value, userSearchName.value, t);
    state.userDataSource = userResult.userDataSource;
    total.value = userResult.total;
  } catch (error) {
    console.error('Error loading users:', error);
    notification.error(t('common.messages.networkError'));
  } finally {
    userLoading.value = false;
  }
};

/**
 * Load Department Information
 *
 * Fetches detailed information about the currently selected department
 * and updates the info panel display.
 */
const loadDeptInfo = async (): Promise<void> => {
  if (!state.currentSelectedNode.id) return;

  try {
    const deptInfoResult = await loadDeptInfoUtil(state.currentSelectedNode, t);
    Object.assign(deptInfo, deptInfoResult);
  } catch (error) {
    console.error('Error loading department info:', error);
    notification.error(t('common.messages.networkError'));
  }
};

/**
 * Add department modal handlers
 * Opens the add department modal and sets the parent department ID
 */
const addDept = (pid = '-1' as string): void => {
  addModalVisible.value = true;
  addPid.value = pid;
};

/**
 * Close add department modal
 * Hides the add department modal dialog
 */
const closeAdd = (): void => {
  addModalVisible.value = false;
};

/**
 * Save add department operation
 * Handles the successful creation of a new department
 * Updates the tree view and triggers refresh if needed
 */
const saveAdd = async (value: any): Promise<void> => {
  closeAdd();
  if (value.pid && +value.pid < 0 && !searchDeptId.value) {
    notify.value += 1;
  } else {
    treeSelect.value.add(value);
  }
};

/**
 * Edit department name modal handlers
 * Opens the edit department name modal dialog
 */
const editDeptName = (): void => {
  editModalVisible.value = true;
};

/**
 * Close edit department name modal
 * Hides the edit department name modal dialog
 */
const closeEditName = (): void => {
  editModalVisible.value = false;
};

/**
 * Save edit department name operation
 * Updates the department name in the tree view
 */
const saveEditName = (name: string): void => {
  closeEditName();
  treeSelect.value.edit({
    id: currentActionNode.value.id,
    name
  });
};

/**
 * Delete department with confirmation
 * Checks for child departments and users before deletion
 */
const deleteDept = async (): Promise<void> => {
  await deleteDepartment(currentActionNode.value, t, async () => {
    try {
      const [error] = await dept.deleteDept({ ids: [currentActionNode.value.id as string] });
      if (error) {
        notification.error(t('common.messages.operationFailed'));
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
      console.error('Error deleting department:', error);
      notification.error(t('common.messages.networkError'));
    }
  });
};

/**
 * Tag management handlers
 * Opens the edit tags modal for the selected department
 */
const editTag = async (): Promise<void> => {
  editTagVisible.value = true;
};

/**
 * Save department tags operation
 * Adds new tags and removes deleted tags from the department
 * Refreshes department information after tag updates
 */
const saveTag = async (
  _tagIds: string[],
  _tags: { id: string; name: string }[],
  deleteTagIds: string[]
): Promise<void> => {
  try {
    // Add new tags
    if (_tagIds.length) {
      await addDeptTags(currentActionNode.value.id as string, _tagIds, t);
    }

    // Delete tags
    if (deleteTagIds.length) {
      await deleteDeptTags(currentActionNode.value.id as string, deleteTagIds, t);
    }

    editTagVisible.value = false;
    await loadDeptInfo();
  } catch (error) {
    console.error('Error saving tags:', error);
    notification.error(t('common.messages.operationFailed'));
  }
};

/**
 * User search with debounced input
 * Triggers user search after a delay to prevent excessive API calls
 */
const searchLoadUser = debounce(duration.search, () => {
  params.value.pageNo = 1;
  loadUser();
});

/**
 * User association handlers
 * Opens the user association modal for managing department users
 */
const assocUser = async (): Promise<void> => {
  userVisible.value = true;
};

/**
 * Save user association operation
 * Handles adding new users and removing existing users from the department
 * Refreshes user data if needed
 */
const saveUser = async (
  _userIds: string[],
  _users: { id: string; fullName: string }[],
  deleteUserIds: string[]
): Promise<void> => {
  try {
    // Delete users if any
    if (deleteUserIds.length) {
      await delDeptUser(deleteUserIds, 'Modal');
    }

    // Add new users if any
    if (_userIds.length) {
      await addDeptUser(_userIds);
    }

    userVisible.value = false;
    userUpdateLoading.value = false;

    if (isRefresh.value) {
      userLoadDisabled.value = true;
      await loadUser();
      userLoadDisabled.value = false;
      isRefresh.value = false;
    }
  } catch (error) {
    console.error('Error saving users:', error);
    notification.error(t('common.messages.operationFailed'));
  }
};

/**
 * Add users to department
 * Associates selected users with the current department
 */
const addDeptUser = async (userIds: string[]): Promise<void> => {
  try {
    userUpdateLoading.value = true;
    const success = await addDeptUserUtil(state.currentSelectedNode.id as string, userIds, t);
    if (success) {
      isRefresh.value = true;
    }
  } catch (error) {
    console.error('Error adding users to department:', error);
    notification.error(t('common.messages.operationFailed'));
  } finally {
    userUpdateLoading.value = false;
  }
};

/**
 * Remove users from department
 * Disassociates users from the current department
 * Handles both table and modal operations with appropriate refresh logic
 */
const delDeptUser = async (userIds: string[], type?: 'Table' | 'Modal'): Promise<void> => {
  try {
    userUpdateLoading.value = true;
    const success = await removeDeptUser(state.currentSelectedNode.id as string, userIds, t);
    if (success) {
      if (type === 'Modal') {
        isRefresh.value = true;
      }

      params.value.pageNo = getCurrentPageAfterDeletion(
        params.value.pageNo as number,
        params.value.pageSize as number,
        total.value
      );

      // Refresh table data for table operations
      if (type === 'Table') {
        userLoadDisabled.value = true;
        await loadUser();
        userLoadDisabled.value = false;
      }
    }
  } catch (error) {
    console.error('Error removing users from department:', error);
    notification.error(t('common.messages.operationFailed'));
  } finally {
    userUpdateLoading.value = false;
  }
};

/**
 * Optimized department search with better error handling
 */
const handleSearchDept = async (value: any): Promise<void> => {
  if (!value) return;

  try {
    const searchResult = await searchDepartment(value, t);
    if (!searchResult) return;

    const { parentChain, current, id, pid, name } = searchResult;
    const validParentChain = parentChain.map(item => ({ ...item, hasSubDept: true }));

    await nextTick();
    selectedDept.value = id;

    // Ensure all tree data has required properties and filter out undefined values
    const validCurrent = current.id && current.name ? current : null;
    const filteredDataSource = validCurrent ? [...validParentChain, validCurrent] : validParentChain;
    state.dataSource = filterValidTreeData(filteredDataSource);

    await changeSelect([id], true, { id, pid, name });
  } catch (error) {
    console.error('Error in department search:', error);
    notification.error(t('common.messages.networkError'));
  }
};

/**
 * Handle tag search change
 * Updates tag filter and resets department search
 */
const handleSearchTag = (value: any): void => {
  searchTagId.value = value;
  if (searchDeptId.value) {
    searchDeptId.value = undefined;
  }
  changeSelect([], false, state.currentSelectedNode);
};

/**
 * Tree loading and refresh handlers
 * Tracks whether departments exist in the tree
 */
const hasDept = ref(false);

/**
 * Handle tree data loaded event
 * Updates the hasDept flag based on loaded data
 */
const loaded = (options: any[]): void => {
  hasDept.value = !!options.length;
};

/**
 * Handle user refresh action
 * Triggers reload of user data for current department
 */
const handleRefreshUser = (): void => {
  loadUser();
};

/**
 * Handle department list refresh action
 * Triggers reload of department tree data
 */
const handleRefreshDeptList = (): void => {
  notify.value++;
};

/**
 * Department move handlers
 * Opens the move department modal dialog
 */
const openMove = (): void => {
  moveVisible.value = true;
};

/**
 * Department move confirmation
 * Executes the department move operation to the target parent
 */
const moveConfirm = async (targetId: string): Promise<void> => {
  try {
    const success = await updateDepartment([{
      pid: targetId,
      id: currentActionNode.value.id
    }], t);

    if (success) {
      moveVisible.value = false;
      notify.value += 1;
    }
  } catch (error) {
    console.error('Error moving department:', error);
    notification.error(t('common.messages.networkError'));
  }
};

/**
 * Table pagination change handler
 * Updates pagination parameters and reloads user data
 */
const tableChange = async (_pagination: any): Promise<void> => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;

  userLoadDisabled.value = true;
  await loadUser();
  userLoadDisabled.value = false;
};

/**
 * Policy management handlers
 * Opens the policy management modal dialog
 */
const openPolicyModal = (): void => {
  policyVisible.value = true;
};

/**
 * Get department policies
 */
const getDeptPolicy = async (): Promise<void> => {
  if (policyLoading.value) return;

  try {
    const { pageSize, current } = policyPagination;
    policyLoading.value = true;

    const policyResult = await getDeptPolicyUtil(
      state.currentSelectedNode.id as string,
      { current, pageSize },
      policyFilters.value,
      t
    );

    if (policyResult) {
      policyData.value = policyResult.list;
      policyPagination.total = policyResult.total;
    }
  } catch (error) {
    console.error('Error loading department policies:', error);
    notification.error(t('common.messages.networkError'));
  } finally {
    policyLoading.value = false;
  }
};

/**
 * Save policy operation
 * Associates new policies with the current department
 */
const savePolicy = async (addIds: string[]): Promise<void> => {
  if (!addIds.length) {
    policyVisible.value = false;
    return;
  }

  try {
    policyUpdateLoading.value = true;
    const success = await addPolicyToDept(state.currentSelectedNode.id as string, addIds, t);

    if (success) {
      policyVisible.value = false;
      policyLoadDisabled.value = true;
      await getDeptPolicy();
      policyLoadDisabled.value = false;
    }
  } catch (error) {
    console.error('Error saving policies:', error);
    notification.error(t('common.messages.networkError'));
  } finally {
    policyUpdateLoading.value = false;
  }
};

/**
 * Handle policy cancellation
 * Removes a policy from the current department
 */
const handleCancel = async (delId: string): Promise<void> => {
  try {
    const success = await removePolicyFromDept(state.currentSelectedNode.id as string, [delId], t);

    if (success) {
      policyLoadDisabled.value = true;
      await getDeptPolicy();
      policyLoadDisabled.value = false;
    }
  } catch (error) {
    console.error('Error canceling policy:', error);
    notification.error(t('common.messages.networkError'));
  }
};

/**
 * Handle policy pagination change
 * Updates policy pagination and reloads policy data
 */
const changePolicyPage = async (page: any): Promise<void> => {
  policyPagination.current = page.current;
  policyPagination.pageSize = page.pageSize;

  policyLoadDisabled.value = true;
  await getDeptPolicy();
  policyLoadDisabled.value = false;
};

/**
 * Handle policy name search with debounced input
 * Filters policies by name and updates the policy list
 */
const policyNameChange = debounce(duration.search, async (event: any): Promise<void> => {
  const value = event.target.value;
  policyPagination.current = 1;

  if (value) {
    policyFilters.value = [{ key: 'name', op: SearchCriteria.OpEnum.MatchEnd, value: value }];
  } else {
    policyFilters.value = [];
  }

  policyLoadDisabled.value = true;
  await getDeptPolicy();
  policyLoadDisabled.value = false;
});

/**
 * Right-click menu handlers
 * Handle context menu actions for department operations
 */

/**
 * Right-click edit department name
 * Sets the current action node and opens edit modal
 */
const rightEditDeptName = (selected: any): void => {
  currentActionNode.value = selected;
  editDeptName();
};

/**
 * Right-click add department
 * Sets the current action node and opens add modal
 */
const rightAddDept = (selected: any): void => {
  currentActionNode.value = selected;
  addDept(selected.id);
};

/**
 * Right-click delete department
 * Sets the current action node and triggers delete operation
 */
const rightDel = (selected: any): void => {
  currentActionNode.value = selected;
  deleteDept();
};

/**
 * Right-click edit tags
 * Sets the current action node and opens tag edit modal
 */
const rightEditTag = (selected: any): void => {
  currentActionNode.value = selected;
  editTag();
};

/**
 * Right-click move department
 * Sets the current action node and opens move modal
 */
const rightOpenMove = (selected: any): void => {
  currentActionNode.value = selected;
  openMove();
};

// Table column definitions for user management
// Defines the structure and behavior of each column in the user table
const userColumns: TableColumn[] = [
  {
    title: t('user.columns.assocUser.id'),
    key: 'id',
    dataIndex: 'id',
    width: '22%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('user.columns.assocUser.name'),
    key: 'fullName',
    dataIndex: 'fullName',
    width: '22%',
    ellipsis: true
  },
  {
    title: t('user.columns.assocUser.createdByName'),
    key: 'createdByName',
    dataIndex: 'createdByName',
    width: '22%'
  },
  {
    title: t('user.columns.assocUser.createdDate'),
    key: 'createdDate',
    dataIndex: 'createdDate',
    width: '22%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('common.actions.operation'),
    key: 'action',
    dataIndex: 'action',
    width: '12%',
    align: 'center'
  }
];

/**
 * Table columns configuration for policy management
 * Defines the structure and behavior of each table column
 * Uses the shared policy column configuration from user types
 */
const policyColumns = createAuthPolicyColumns(t, OrgTargetType.DEPT);

</script>

<template>
  <PureCard class="p-3.5 flex flex-col h-full">
    <div class="flex space-x-2 flex-1 min-h-0">
      <!-- Department tree panel - Left sidebar for department hierarchy -->
      <PureCard class="w-100 p-3 h-full overflow-x-hidden dept-tree pr-2 border-r flex flex-col">
        <!-- Search and action controls - Top toolbar for search and operations -->
        <div class="flex items-center justify-between space-x-1 mb-2">
          <!-- Department search dropdown -->
          <Select
            v-model:value="searchDeptId"
            :showSearch="true"
            :action="`${GM}/dept`"
            :params="getSearchDeptParams"
            class="w-40"
            size="small"
            :allowClear="true"
            :fieldNames="{label: 'name', value: 'id'}"
            :placeholder="t('department.placeholder.name')"
            @change="handleSearchDept" />
          <!-- Tag filter dropdown -->
          <Select
            v-model:value="searchTagId"
            :allowClear="true"
            :fieldNames="{ label: 'name', value: 'id' }"
            class="w-40"
            size="small"
            showSearch
            :placeholder="t('department.placeholder.tag')"
            :action="`${GM}/org/tag`"
            @change="handleSearchTag" />
          <!-- Add department button -->
          <ButtonAuth
            code="DeptAdd"
            type="primary"
            icon="icon-tianjia"
            @click="addDept()" />
          <!-- Refresh tree button -->
          <IconRefresh
            :loading="treeLoading"
            :disabled="refreshDisabled"
            @click="handleRefreshDeptList" />
        </div>

        <!-- Department tree - Hierarchical display of departments -->
        <Tree
          ref="treeSelect"
          v-model:loading="treeLoading"
          v-model:selectedKey="selectedDept"
          :params="treeParams"
          :action="`${GM}/dept`"
          :fieldNames="treeFieldNames"
          :autoload="!searchDeptId"
          :treeData="state.dataSource"
          :notify="notify"
          class="dept-tree-container"
          :style="{height:hasDept ? '' : '0'}"
          :selectFirstOptions="true"
          @loaded="loaded"
          @select="changeSelect">
          <!-- Custom tree node template with context menu -->
          <template #default="item">
            <Dropdown :trigger="['contextmenu']" overlayClassName="ant-dropdown-sm">
              <div class="dept-tree-node" :title="item.name">
                <div class="dept-node-content">
                  <Icon icon="icon-bumen" class="dept-node-icon" />
                  <span class="dept-node-text">{{ item.name }}</span>
                </div>
              </div>
              <!-- Right-click context menu for department operations -->
              <template #overlay>
                <Menu class="dept-context-menu">
                  <!-- Edit department name menu item -->
                  <MenuItem
                    v-if="app.show('DeptModify')"
                    :disabled="!app.has('DeptModify')"
                    class="dept-menu-item"
                    @click="rightEditDeptName(item)">
                    <template #icon>
                      <Icon icon="icon-shuxie" class="menu-item-icon" />
                    </template>
                    {{ app.getName('DeptModify') }}
                  </MenuItem>
                  <!-- Add sub-department menu item -->
                  <MenuItem
                    v-if="app.show('DeptAdd')"
                    :disabled="!app.has('DeptAdd')"
                    class="dept-menu-item"
                    @click="rightAddDept(item)">
                    <template #icon>
                      <Icon icon="icon-tianjia" class="menu-item-icon" />
                    </template>
                    {{ app.getName('DeptAdd') }}
                  </MenuItem>
                  <!-- Delete department menu item -->
                  <MenuItem
                    v-if="app.show('DeptDelete')"
                    :disabled="!app.has('DeptDelete')"
                    class="dept-menu-item"
                    @click="rightDel(item)">
                    <template #icon>
                      <Icon icon="icon-lajitong" class="menu-item-icon" />
                    </template>
                    {{ app.getName('DeptDelete') }}
                  </MenuItem>
                  <!-- Edit department tags menu item -->
                  <MenuItem
                    v-if="app.show('DeptTagsAdd')"
                    :disabled="!app.has('DeptTagsAdd')"
                    class="dept-menu-item"
                    @click="rightEditTag(item)">
                    <template #icon>
                      <Icon icon="icon-biaoqian2" class="menu-item-icon" />
                    </template>
                    {{ app.getName('DeptTagsAdd') }}
                  </MenuItem>
                  <!-- Move department menu item -->
                  <MenuItem
                    v-if="app.show('Move')"
                    :disabled="!app.has('Move')"
                    class="dept-menu-item"
                    @click="rightOpenMove(item)">
                    <template #icon>
                      <Icon icon="icon-riqiyou" class="menu-item-icon" />
                    </template>
                    {{ app.getName('Move') }}
                  </MenuItem>
                </Menu>
              </template>
            </Dropdown>
          </template>
        </Tree>
      </PureCard>

      <!-- Content panel - Right side content area -->
      <div class="flex-1 flex flex-col overflow-y-auto">
        <!-- Department info card - Displays current department information -->
        <Info
          :node="state.currentSelectedNode"
          :deptInfo="deptInfo"
          @add="rightAddDept"
          @editName="rightEditDeptName"
          @delete="rightDel"
          @editTag="rightEditTag"
          @move="rightOpenMove" />
        <!-- Tab content - Main content area with tabs -->
        <div class="flex-1">
          <PureCard class="px-2 min-h-full">
            <Tabs
              v-show="state.currentSelectedNode.id"
              class="dept-tab"
              size="small">
              <!-- Users tab - Manages department user associations -->
              <TabPane key="user" :tab="t('department.tab.deptUsers')">
                <!-- User search and action controls -->
                <div class="flex item-center justify-between mb-2">
                  <!-- User search input -->
                  <Input
                    v-model:value="userSearchName"
                    :placeholder="t('user.placeholder.search')"
                    class="w-50"
                    size="small"
                    allowClear
                    @change="searchLoadUser">
                    <template #suffix>
                      <Icon
                        icon="icon-sousuo"
                        class="text-3.5 leading-3.5 text-theme-content cursor-pointer" />
                    </template>
                  </Input>
                  <!-- User management action buttons -->
                  <div class="flex items-center">
                    <!-- Associate users button -->
                    <ButtonAuth
                      class="mr-2"
                      code="DeptUserAssociate"
                      type="primary"
                      icon="icon-tianjia"
                      @click="assocUser" />
                    <!-- Refresh users button -->
                    <IconRefresh
                      :loading="userLoading"
                      :disabled="userLoadDisabled"
                      @click="handleRefreshUser" />
                  </div>
                </div>

                <!-- Users table - Displays department users with actions -->
                <Table
                  :dataSource="state.userDataSource"
                  rowKey="id"
                  :columns="userColumns"
                  :pagination="pagination"
                  :loading="userLoading"
                  size="small"
                  :noDataSize="'small'"
                  :noDataText="t('common.messages.noData')"
                  @change="tableChange">
                  <!-- Custom cell templates for table columns -->
                  <template #bodyCell="{ column,text, record }">
                    <!-- User name with avatar display -->
                    <template v-if="column.dataIndex === 'fullName'">
                      <div class="flex items-center" :title="text">
                        <Image
                          :src="record.avatar"
                          type="avatar"
                          class="w-5 h-5 rounded-full mr-2" />
                        {{ text }}
                      </div>
                    </template>
                    <!-- Action buttons for user operations -->
                    <template v-if="column.dataIndex === 'action'">
                      <ButtonAuth
                        code="DeptUserUnassociate"
                        type="text"
                        icon="icon-quxiao"
                        @click="delDeptUser([record.userId],'Table')" />
                    </template>
                  </template>
                </Table>
              </TabPane>

              <!-- Policy tab - Manages department authorization policies -->
              <TabPane key="strategy" :tab="t('department.tab.authPolicy')">
                <!-- Policy search and action controls -->
                <div class="flex items-center justify-between mb-2">
                  <!-- Policy name search input -->
                  <Input
                    :placeholder="t('permission.placeholder.policyName')"
                    class="w-60"
                    allowClear
                    @change="policyNameChange">
                    <template #suffix>
                      <Icon class="text-theme-content text-theme-text-hover text-3 leading-3" icon="icon-sousuo" />
                    </template>
                  </Input>
                  <!-- Policy management action buttons -->
                  <div class="flex space-x-2.5 items-center">
                    <!-- Add policy button -->
                    <ButtonAuth
                      code="DeptAuthorize"
                      type="primary"
                      icon="icon-tianjia"
                      @click="openPolicyModal" />
                    <!-- Refresh policies button -->
                    <IconRefresh
                      :loading="policyLoading"
                      :disabled="policyLoadDisabled"
                      @click="getDeptPolicy" />
                  </div>
                </div>

                <!-- Policy table - Displays department policies with actions -->
                <Table
                  :columns="policyColumns"
                  size="small"
                  :loading="policyLoading"
                  :dataSource="policyData"
                  :pagination="policyPagination"
                  :noDataSize="'small'"
                  :noDataText="t('common.messages.noData')"
                  @change="changePolicyPage">
                  <!-- Custom cell templates for policy table -->
                  <template #bodyCell="{ column,text,record }">
                    <!-- Policy name with description tooltip -->
                    <template v-if="column.dataIndex === 'name'">
                      <div :title="record.description">{{ text }}</div>
                    </template>
                    <!-- Action buttons for policy operations -->
                    <template v-if="column.dataIndex === 'action'">
                      <ButtonAuth
                        code="DeptAuthorizeCancel"
                        type="text"
                        icon="icon-quxiao"
                        @click="handleCancel(record.id)" />
                    </template>
                  </template>
                </Table>
              </TabPane>
            </Tabs>
          </PureCard>
        </div>
      </div>
    </div>
  </PureCard>

  <!-- Modal components - Various operation dialogs -->
  <!-- Add department modal -->
  <AsyncComponent :visible="addModalVisible">
    <AddDeptModal
      v-if="addModalVisible"
      v-model:visible="addModalVisible"
      :pname="currentActionNode.name"
      :pid="addPid"
      @close="closeAdd()"
      @save="saveAdd" />
  </AsyncComponent>

  <!-- Edit department name modal -->
  <AsyncComponent :visible="editModalVisible">
    <EditModal
      :id="currentActionNode.id"
      :visible="editModalVisible"
      :name="currentActionNode.name"
      @close="closeEditName()"
      @save="saveEditName" />
  </AsyncComponent>

  <!-- Edit department tags modal -->
  <AsyncComponent :visible="editTagVisible">
    <SelectTagModal
      v-model:visible="editTagVisible"
      :deptId="currentActionNode.id"
      type="Dept"
      @change="saveTag" />
  </AsyncComponent>

  <!-- User association modal -->
  <AsyncComponent :visible="userVisible">
    <UserModal
      v-if="userVisible"
      v-model:visible="userVisible"
      type="Dept"
      :updateLoading="userUpdateLoading"
      :deptId="state.currentSelectedNode.id"
      @change="saveUser" />
  </AsyncComponent>

  <!-- Move department modal -->
  <AsyncComponent :visible="moveVisible">
    <MoveDeptModal
      v-model:visible="moveVisible"
      :moveId="currentActionNode.id"
      :defaultPid="currentActionNode.pid"
      @ok="moveConfirm" />
  </AsyncComponent>

  <!-- Policy management modal -->
  <AsyncComponent :visible="policyVisible">
    <PolicyModal
      v-model:visible="policyVisible"
      :deptId="state.currentSelectedNode.id"
      :updateLoading="policyUpdateLoading"
      type="Dept"
      @change="savePolicy" />
  </AsyncComponent>
</template>

<style scoped>
/* Tab styling overrides for Ant Design Vue */
.dept-tab :deep(.ant-tabs-nav) {
  @apply mb-1.5;
}

.dept-tab :deep(.ant-tabs-nav::before) {
  @apply mb-1.5;
  display: none;
}

/* Department tree styling - Main container for the tree component */
.dept-tree-container {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

/* Individual tree node styling with hover and active states */
.dept-tree-node {
  display: flex;
  align-items: center;
  padding: 3px 3px;
  border-radius: 6px;
  transition: all 0.2s ease;
  cursor: pointer;
  margin: 2px 0;
}

/* Hover effect for tree nodes */
.dept-tree-node:hover {
  background-color: #f5f5f5;
  transform: translateX(2px);
}

/* Active state for tree nodes */
.dept-tree-node:active {
  background-color: #e6f7ff;
  transform: translateX(1px);
}

/* Tree node content layout */
.dept-node-content {
  display: flex;
  align-items: center;
  width: 100%;
  min-width: 0;
}

/* Tree node icon styling */
.dept-node-icon {
  font-size: 12px;
  color: #1890ff;
  flex-shrink: 0;
}

/* Tree node text styling */
.dept-node-text {
  color: #262626;
  font-size: 12px;
  font-weight: 400;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
  min-width: 0;
}

/* Enhanced tree styling - Override Ant Design Vue default styles */
:deep(.ant-tree-node-content-wrapper) {
  padding: 0 !important;
  border-radius: 6px;
  transition: all 0.2s ease;
}

/* Remove default hover background for tree nodes */
:deep(.ant-tree-node-content-wrapper:hover) {
  background-color: transparent !important;
}

/* Selected tree node styling */
:deep(.ant-tree-node-content-wrapper.ant-tree-node-selected) {
  background-color: #e6f7ff !important;
  border-radius: 6px;
}

/* Selected tree node content styling */
:deep(.ant-tree-node-content-wrapper.ant-tree-node-selected .dept-tree-node) {
  background-color: #e6f7ff;
}

/* Tree expand/collapse switcher styling */
:deep(.ant-tree-switcher) {
  margin-right: 4px;
  color: #8c8c8c;
}

/* Tree switcher hover state */
:deep(.ant-tree-switcher:hover) {
  color: #1890ff;
}

/* Tree indentation styling */
:deep(.ant-tree-indent) {
  margin-left: 8px;
}

/* Tree indentation unit width */
:deep(.ant-tree-indent-unit) {
  width: 16px;
}

/* Context menu styling - Right-click menu appearance */
.dept-context-menu {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border: 1px solid #f0f0f0;
  padding: 4px 0;
}

/* Individual context menu item styling */
.dept-menu-item {
  padding: 8px 16px;
  transition: all 0.2s ease;
  border-radius: 4px;
  margin: 2px 8px;
}

/* Context menu item hover state */
.dept-menu-item:hover {
  background-color: #f5f5f5;
  color: #1890ff;
}

/* Context menu item active state */
.dept-menu-item:active {
  background-color: #e6f7ff;
}

/* Context menu item icon styling */
.menu-item-icon {
  margin-right: 8px;
  font-size: 12px;
  color: #8c8c8c;
}

/* Context menu item icon hover state */
.dept-menu-item:hover .menu-item-icon {
  color: #1890ff;
}

/* Loading state styling for tree component */
:deep(.ant-tree-loading) {
  opacity: 0.7;
}

/* Loading state switcher styling */
:deep(.ant-tree-loading .ant-tree-switcher) {
  color: #1890ff;
}

/* Empty state styling for tree component */
:deep(.ant-tree-empty) {
  padding: 24px;
  text-align: center;
  color: #8c8c8c;
}

/* Tree container improvements - Enhanced visual appearance */
.dept-tree {
  border-right: 1px solid #f0f0f0;
  background-color: #fafafa;
  border-radius: 8px;
  margin-right: 16px;
}

/* Responsive design for department tree - Mobile optimization */
@media (max-width: 768px) {
  .dept-tree-container {
    padding: 4px 0;
  }

  .dept-tree-node {
    padding: 4px 6px;
  }

  .dept-node-text {
    font-size: 12px;
  }

  .dept-node-icon {
    font-size: 12px;
    margin-right: 6px;
  }
}
</style>
