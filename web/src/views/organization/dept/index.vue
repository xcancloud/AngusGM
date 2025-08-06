<script setup lang="ts">
import { computed, defineAsyncComponent, reactive, ref, nextTick } from 'vue';
import { useI18n } from 'vue-i18n';
import { Dropdown, Menu, MenuItem, TabPane, Tabs, Tag } from 'ant-design-vue';
import {
  AsyncComponent,
  ButtonAuth,
  Card,
  Icon,
  IconCount,
  IconRefresh,
  Image,
  Input,
  modal,
  notification,
  PureCard,
  Select,
  Table,
  Tree
} from '@xcan-angus/vue-ui';
import { app, duration, GM, utils } from '@xcan-angus/infra';
import { debounce } from 'throttle-debounce';

import { DataType, TreeRecordType, UserRecordType } from './PropsType';
import { auth, dept } from '@/api';

// Type definitions for filter operations and search parameters
type FilterOp =
  | 'EQUAL'
  | 'NOT_EQUAL'
  | 'GREATER_THAN'
  | 'GREATER_THAN_EQUAL'
  | 'LESS_THAN'
  | 'LESS_THAN_EQUAL'
  | 'CONTAIN'
  | 'NOT_CONTAIN'
  | 'MATCH_END'
  | 'MATCH'
  | 'IN'
  | 'NOT_IN';

type Filters = { key: string; value: string; op: FilterOp }[];

interface SearchParams {
  pageNo?: number;
  pageSize?: number;
  filters?: Filters;
  orderBy?: string;
  orderSort?: 'ASC' | 'DESC';
}

// Department state interface
interface DeptState {
  sortForm: Record<string, any>;
  searchForm: any[];
  loading: boolean;
  dataSource: DataType[];
  currentRecord: DataType | undefined;
  addDeptVisible: boolean;
  editDeptNameVisible: boolean;
  userLoading: boolean;
  userDataSource: UserRecordType[];
  currentSelectedNode: TreeRecordType;
  concatUserSource: UserRecordType[];
}

// Department info interface
interface DeptInfo {
  createdByName: string;
  createdDate: string;
  tags: { id: string; name: string }[];
  level: string;
  name: string;
  code: string;
  id: string;
  lastModifiedDate: string;
  lastModifiedByName: string;
}

// Async component definitions
const SelectTargetModal = defineAsyncComponent(() => import('@/components/TagModal/index.vue'));
const AddDeptModal = defineAsyncComponent(() => import('@/views/organization/dept/components/add/index.vue'));
const EditModal = defineAsyncComponent(() => import('@/views/organization/dept/components/edit/index.vue'));
const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));
const UserModal = defineAsyncComponent(() => import('@/components/UserModal/index.vue'));
const MoveDeptModal = defineAsyncComponent(() => import('./components/move/index.vue'));
const PolicyModal = defineAsyncComponent(() => import('@/components/PolicyModal/index.vue'));

const { t } = useI18n();

// Reactive state management
const showCount = ref(true); // Controls statistics panel visibility
const state = reactive<DeptState>({
  sortForm: {},
  searchForm: [],
  loading: false,
  dataSource: [],
  currentRecord: undefined,
  addDeptVisible: false,
  editDeptNameVisible: false,
  userLoading: false,
  currentSelectedNode: { id: undefined, name: undefined, pid: undefined },
  userDataSource: [],
  concatUserSource: []
});

// Tree and search related refs
const treeSelect = ref();
const searchDeptId = ref<string>();
const searchTagId = ref<string>();
const selectedDept = ref<string>();

// Search parameters and pagination
const params = ref<SearchParams>({ pageNo: 1, pageSize: 10, filters: [] });
const total = ref(0);

// Loading states
const userLoading = ref(false);
const treeLoading = ref(false);
const refreshDisabled = ref(false);
const userLoadDisabled = ref(false);
const policyLoadDisabled = ref(false);
const policyUpdateLoading = ref(false);

// Modal visibility states
const addModalVisible = ref(false);
const editModalVisible = ref(false);
const editTagVisible = ref(false);
const userVisible = ref(false);
const moveVisible = ref(false);
const policyVisible = ref(false);

// Form data
const addPid = ref('-1');
const userSearchName = ref<string>();
const isRefresh = ref(false);
const userUpdateLoading = ref(false);
const notify = ref(0);

// Current action node for operations
const currentActionNode = ref<{ id: string; name: string; pid: string }>({ id: '', name: '', pid: '' });

// Department info reactive object
const deptInfo = reactive<DeptInfo>({
  createdByName: '',
  createdDate: '',
  tags: [],
  level: '',
  name: '',
  code: '',
  id: '',
  lastModifiedDate: '',
  lastModifiedByName: ''
});

// Policy related state
const policyData = ref([]);
const policyPagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
});
const policyFilters = ref<{ key: string; value: string; op: FilterOp }[]>([]);
const policyLoading = ref(false);

// Computed properties
const pagination = computed(() => ({
  current: params.value.pageNo,
  pageSize: params.value.pageSize,
  total: total.value
}));

const getSearchDeptParams = computed(() => ({
  tagId: searchTagId.value,
  fullTextSearch: true
}));

const treeParams = computed(() => ({
  pid: -1,
  pageSize: 30,
  tagId: searchTagId.value,
  orderBy: 'createdDate',
  orderSort: 'ASC'
}));

const treeFieldNames = { title: 'name', key: 'id', children: 'hasSubDept' };

// Tree selection change handler
const changeSelect = async (
  _selectedKeys: string[],
  selected: boolean,
  info: { id: string | undefined; name: string | undefined; pid: string | undefined }
): Promise<void> => {
  const { id, name, pid } = info;
  
  if (selected) {
    state.currentSelectedNode = { id, name, pid };
    params.value.pageNo = 1;
    policyPagination.current = 1;
    
    try {
      userLoadDisabled.value = true;
      await loadUser();
      userLoadDisabled.value = false;
      
      await loadDeptInfo();
      
      policyLoadDisabled.value = true;
      await getDeptPolicy();
      policyLoadDisabled.value = false;
    } catch (error) {
      console.error('Error in changeSelect:', error);
      notification.error(t('common.messages.operationFailed'));
    }
  } else {
    state.currentSelectedNode = { id: undefined, name: undefined, pid: undefined };
  }
};

/**
 * Load user list for selected department
 * Handles loading state and error notifications
 */
const loadUser = async (): Promise<void> => {
  if (!state.currentSelectedNode.id) {
    notification.warning(t('loadUserWarn'));
    return;
  }

  if (userLoading.value) {
    return;
  }

  try {
    // Prepare filters for search
    if (userSearchName.value && params.value.filters) {
      params.value.filters.push({ key: 'fullName', op: 'MATCH_END', value: userSearchName.value });
    } else {
      params.value.filters = [];
    }

    userLoading.value = true;
    const [error, res] = await dept.getDeptUsers(state.currentSelectedNode.id, params.value);
    
    if (error) {
      notification.error(t('common.messages.operationFailed'));
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
 * Load department information
 * Updates deptInfo with current department details
 */
const loadDeptInfo = async (): Promise<void> => {
  if (!state.currentSelectedNode.id) return;

  try {
    const [error, { data = {} }] = await dept.getDeptDetail(state.currentSelectedNode.id);
    
    if (error) {
      notification.error(t('common.messages.operationFailed'));
      return;
    }

    // Update department info
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
 * Add department modal handlers
 */
const addDept = (pid = '-1' as string): void => {
  addModalVisible.value = true;
  addPid.value = pid;
};

const closeAdd = (): void => {
  addModalVisible.value = false;
};

const saveAdd = (value: any): void => {
  closeAdd();
  if (value.pid && +value.pid < 0 && !searchDeptId.value) {
    notify.value += 1;
  } else {
    treeSelect.value.add(value);
  }
};

/**
 * Edit department name modal handlers
 */
const editDeptName = (): void => {
  editModalVisible.value = true;
};

const closeEditName = (): void => {
  editModalVisible.value = false;
};

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
const del = async (): Promise<void> => {
  try {
    const [error, res] = await dept.getDeptCount(currentActionNode.value.id as string);
    
    if (error) {
      notification.error(t('common.messages.operationFailed'));
      return;
    }

    const { subDeptNum, sunUserNum } = res.data;
    const existChildDept = subDeptNum > 0;
    const existUser = sunUserNum > 0;
    
    let content = '';
    if (existChildDept && existUser) {
      content = t('childAndUserTip', { childNum: subDeptNum, userNum: sunUserNum });
    } else if (existChildDept && !existUser) {
      content = t('tenantDelChildTip', { childNum: subDeptNum });
    } else if (!existChildDept && existUser) {
      content = t('tenantDelUserTip', { userNum: sunUserNum });
    }

    modal.confirm({
      centered: true,
      title: t('delDept'),
      content: t('delDeptTip', { content }),
      onOk: async () => {
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
          notification.error(t('common.messages.operationFailed'));
        }
      }
    });
  } catch (error) {
    console.error('Error getting department count:', error);
    notification.error(t('common.messages.networkError'));
  }
};

/**
 * Tag management handlers
 */
const editTag = async (): Promise<void> => {
  editTagVisible.value = true;
};

const tagSave = async (
  _tagIds: string[],
  _tags: { id: string; name: string }[],
  deleteTagIds: string[]
): Promise<void> => {
  try {
    // Add new tags
    if (_tagIds.length) {
      await dept.addDeptTags({ id: currentActionNode.value.id as string, ids: _tagIds });
    }
    
    // Delete tags
    if (deleteTagIds.length) {
      await dept.deleteDeptTag({ id: currentActionNode.value.id as string, ids: deleteTagIds });
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
 */
const searchLoadUser = debounce(duration.search, () => {
  params.value.pageNo = 1;
  loadUser();
});

/**
 * User association handlers
 */
const concatUser = async (): Promise<void> => {
  userVisible.value = true;
};

const userSave = async (
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
 */
const addDeptUser = async (userIds: string[]): Promise<void> => {
  try {
    userUpdateLoading.value = true;
    const [error] = await dept.createDeptUser(state.currentSelectedNode.id as string, userIds);
    
    if (error) {
      notification.error(t('common.messages.operationFailed'));
      return;
    }
    
    isRefresh.value = true;
  } catch (error) {
    console.error('Error adding users to department:', error);
    notification.error(t('common.messages.networkError'));
  } finally {
    userUpdateLoading.value = false;
  }
};

/**
 * Remove users from department
 */
const delDeptUser = async (userIds: string[], type?: 'Table' | 'Modal'): Promise<void> => {
  try {
    userUpdateLoading.value = true;
    const [error] = await dept.deleteDeptUser(state.currentSelectedNode.id as string, userIds);
    
    if (error) {
      notification.error(t('common.messages.operationFailed'));
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
      await loadUser();
      userLoadDisabled.value = false;
    }
  } catch (error) {
    console.error('Error removing users from department:', error);
    notification.error(t('common.messages.networkError'));
  } finally {
    userUpdateLoading.value = false;
  }
};

/**
 * Department search handlers
 */
const handleSearchDept = async (value: any): Promise<void> => {
  if (!value) return;

  try {
    const [error, res] = await dept.getNavigationByDeptId({ id: value });
    
    if (error) {
      notification.error(t('common.messages.operationFailed'));
      return;
    }

    const parentChain = (res.data.parentChain || []).map(item => ({ ...item, hasSubDept: true }));
    const { id, pid, name } = res.data.current;
    
    await nextTick();
    selectedDept.value = id;
    state.dataSource = [...parentChain, res.data.current];
    await changeSelect([id], true, { id, pid, name });
  } catch (error) {
    console.error('Error searching department:', error);
    notification.error(t('common.messages.networkError'));
  }
};

const handleSearchTag = (value: any): void => {
  searchTagId.value = value;
  if (searchDeptId.value) {
    searchDeptId.value = undefined;
  }
  changeSelect([], false, state.currentSelectedNode);
};

/**
 * Tree loading and refresh handlers
 */
const hasDept = ref(false);

const loaded = (options: any[]): void => {
  hasDept.value = !!options.length;
};

const handleRefreshUser = (): void => {
  loadUser();
};

const handleRefreshDeptList = (): void => {
  notify.value++;
};

/**
 * Department move handlers
 */
const openMove = (): void => {
  moveVisible.value = true;
};

const confirmMove = async (targetId: string): Promise<void> => {
  try {
    const [error] = await dept.updateDept([{ pid: targetId, id: currentActionNode.value.id }]);
    
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
 * Table pagination change handler
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
 */
const openPolicyModal = (): void => {
  policyVisible.value = true;
};

const getDeptPolicy = async (): Promise<void> => {
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

const policySave = async (addIds: string[]): Promise<void> => {
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
    await getDeptPolicy();
    policyLoadDisabled.value = false;
  } catch (error) {
    console.error('Error saving policies:', error);
    notification.error(t('common.messages.networkError'));
  } finally {
    policyUpdateLoading.value = false;
  }
};

const handleCancel = async (delId: string): Promise<void> => {
  try {
    const [error] = await auth.deletePolicyByDept(state.currentSelectedNode.id as string, [delId]);
    
    if (error) {
      notification.error(t('common.messages.operationFailed'));
      return;
    }
    
    policyLoadDisabled.value = true;
    await getDeptPolicy();
    policyLoadDisabled.value = false;
  } catch (error) {
    console.error('Error canceling policy:', error);
    notification.error(t('common.messages.networkError'));
  }
};

const changePolicyPage = async (page: any): Promise<void> => {
  policyPagination.current = page.current;
  policyPagination.pageSize = page.pageSize;
  
  policyLoadDisabled.value = true;
  await getDeptPolicy();
  policyLoadDisabled.value = false;
};

const policyNameChange = debounce(duration.search, async (event: any): Promise<void> => {
  const value = event.target.value;
  policyPagination.current = 1;
  
  if (value) {
    policyFilters.value = [{ key: 'name', op: 'MATCH_END', value: value }];
  } else {
    policyFilters.value = [];
  }
  
  policyLoadDisabled.value = true;
  await getDeptPolicy();
  policyLoadDisabled.value = false;
});

/**
 * Right-click menu handlers
 */
const rightEditDeptName = (selected: any): void => {
  currentActionNode.value = selected;
  editDeptName();
};

const rightAddDept = (selected: any): void => {
  currentActionNode.value = selected;
  addDept(selected.id);
};

const rightDel = (selected: any): void => {
  currentActionNode.value = selected;
  del();
};

const rightEditTag = (selected: any): void => {
  currentActionNode.value = selected;
  editTag();
};

const rightOpenMove = (selected: any): void => {
  currentActionNode.value = selected;
  openMove();
};

// Table column definitions
const columns = [
  {
    key: 'id',
    title: t('用户ID'),
    dataIndex: 'id',
    width: '22%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    key: 'fullName',
    title: t('userName1'),
    dataIndex: 'fullName',
    ellipsis: true
  },
  {
    key: 'createdByName',
    title: t('associatedPerson'),
    dataIndex: 'createdByName',
    width: '20%'
  },
  {
    key: 'createdDate',
    title: t('associatedTime'),
    dataIndex: 'createdDate',
    width: '20%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    key: 'action',
    title: t('operation'),
    dataIndex: 'action',
    width: 82,
    align: 'center'
  }
];

const policyColumns = [
  {
    key: 'id',
    title: t('策略ID'),
    dataIndex: 'id',
    width: '12%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    key: 'name',
    title: t('permissionsStrategy.auth.name'),
    dataIndex: 'name',
    ellipsis: true
  },
  {
    key: 'code',
    title: t('permissionsStrategy.detail.info.code'),
    dataIndex: 'code',
    width: '11%'
  },
  {
    key: 'authByName',
    title: t('授权人'),
    dataIndex: 'authByName',
    width: '11%'
  },
  {
    key: 'authDate',
    title: t('授权时间'),
    dataIndex: 'authDate',
    width: '11%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    key: 'action',
    title: t('operation'),
    dataIndex: 'action',
    width: 82,
    align: 'center'
  }
];
</script>

<template>
  <PureCard class="p-3.5 flex flex-col h-full">
    <!-- Statistics panel -->
    <Statistics
      resource="Dept"
      :barTitle="t('statistics.metrics.newDepartments')"
      :router="GM"
      dateType="YEAR"
      :visible="showCount" />
    
    <div class="flex space-x-2 flex-1 min-h-0">
      <!-- Department tree panel -->
      <PureCard class="w-100 p-3 h-full overflow-x-hidden dept-tree pr-2 border-r flex flex-col">
        <!-- Search and action controls -->
        <div class="flex items-center justify-between space-x-1 mb-2">
          <Select
            v-model:value="searchDeptId"
            :showSearch="true"
            :action="`${GM}/dept`"
            :params="getSearchDeptParams"
            class="w-40"
            size="small"
            :allowClear="true"
            :fieldNames="{label: 'name', value: 'id'}"
            :placeholder="t('deptPlaceholder')"
            @change="handleSearchDept" />
          <Select
            v-model:value="searchTagId"
            :allowClear="true"
            :fieldNames="{ label: 'name', value: 'id' }"
            class="w-40"
            size="small"
            showSearch
            :placeholder="t('tagPlaceholder')"
            :action="`${GM}/org/tag`"
            @change="handleSearchTag" />
          <ButtonAuth
            code="DeptAdd"
            type="primary"
            icon="icon-tianjia"
            @click="addDept()" />
          <IconRefresh
            :loading="treeLoading"
            :disabled="refreshDisabled"
            @click="handleRefreshDeptList" />
        </div>
        
        <!-- Department tree -->
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
          class="flex-1"
          :style="{height:hasDept ? '' : '0'}"
          :selectFirstOptions="true"
          @loaded="loaded"
          @select="changeSelect">
          <template #default="item">
            <Dropdown :trigger="['contextmenu']" overlayClassName="ant-dropdown-sm">
              <div class="flex items-center leading-7 truncate text-theme-content" :title="item.name">
                <Icon icon="icon-bumen" class="mr-1 text-gray-text" />
                {{ item.name }}
              </div>
              <template #overlay>
                <Menu>
                  <MenuItem
                    v-if="app.show('DeptModify')"
                    :disabled="!app.has('DeptModify')"
                    @click="rightEditDeptName(item)">
                    <template #icon>
                      <Icon icon="icon-shuxie" />
                    </template>
                    {{ app.getName('DeptModify') }}
                  </MenuItem>
                  <MenuItem
                    v-if="app.show('DeptAdd')"
                    :disabled="!app.has('DeptAdd')"
                    @click="rightAddDept(item)">
                    <template #icon>
                      <Icon icon="icon-tianjia" />
                    </template>
                    {{ app.getName('DeptAdd') }}
                  </MenuItem>
                  <MenuItem
                    v-if="app.show('DeptDelete')"
                    :disabled="!app.has('DeptDelete')"
                    @click="rightDel(item)">
                    <template #icon>
                      <Icon icon="icon-lajitong" />
                    </template>
                    {{ app.getName('DeptDelete') }}
                  </MenuItem>
                  <MenuItem
                    v-if="app.show('DeptTagsAdd')"
                    :disabled="!app.has('DeptTagsAdd')"
                    @click="rightEditTag(item)">
                    <template #icon>
                      <Icon icon="icon-biaoqian2" />
                    </template>
                    {{ app.getName('DeptTagsAdd') }}
                  </MenuItem>
                  <MenuItem
                    v-if="app.show('Move')"
                    :disabled="!app.has('Move')"
                    @click="rightOpenMove(item)">
                    <template #icon>
                      <Icon icon="icon-riqiyou" />
                    </template>
                    {{ app.getName('Move') }}
                  </MenuItem>
                </Menu>
              </template>
            </Dropdown>
          </template>
        </Tree>
      </PureCard>
      
      <!-- Content panel -->
      <div class="flex-1 flex flex-col overflow-y-auto">
        <!-- Department info card -->
        <Card v-show="state.currentSelectedNode.id" class="mb-2">
          <template #title>
            <span class="text-3">基本信息</span>
          </template>
          <template #rightExtra>
            <div class="flex items-center space-x-2.5">
              <ButtonAuth
                code="DeptModify"
                type="text"
                icon="icon-shuxie"
                @click="rightEditDeptName(state.currentSelectedNode)" />
              <ButtonAuth
                code="DeptAdd"
                type="text"
                icon="icon-tianjia"
                @click="rightAddDept(state.currentSelectedNode)" />
              <ButtonAuth
                code="DeptDelete"
                type="text"
                icon="icon-lajitong"
                @click="rightDel(state.currentSelectedNode)" />
              <ButtonAuth
                code="DeptTagsAdd"
                type="text"
                icon="icon-biaoqian2"
                @click="rightEditTag(state.currentSelectedNode)" />
              <ButtonAuth
                v-if="false"
                code="Move"
                type="text"
                icon="icon-riqiyou"
                @click="rightOpenMove(state.currentSelectedNode)" />
              <IconCount v-model:value="showCount" />
            </div>
          </template>
          
          <!-- Department information display -->
          <div v-show="state.currentSelectedNode.id" class="text-3">
            <div class="flex">
              <div class="flex-1">{{ t('name') + `: ${deptInfo.name || ''}` }}</div>
              <div class="flex-1">{{ t('code') + `: ${deptInfo.code || ''}` }}</div>
              <div class="flex-1">{{ t('ID') + `: ${deptInfo.id}` }}</div>
            </div>
            <div class="flex mt-2">
              <div class="flex-1">{{ t('founder') + `: ${deptInfo.createdByName || '--'}` }}</div>
              <div class="flex-1">{{ t('createdDate') + `: ${deptInfo.createdDate || ''}` }}</div>
              <div class="flex-1">{{ t('level') + `: ${deptInfo.level}` }}</div>
            </div>
            <div class="mt-2 flex">
              <div class="flex-1">{{ t('最后修改人') + `: ${deptInfo.lastModifiedByName || '--'}` }}</div>
              <div class="flex-1">{{ t('最后修改时间') + `: ${deptInfo.lastModifiedDate || '--'}` }}</div>
              <div class="flex-1">
                <span>{{ t('label') }}: </span>
                <Tag
                  v-for="tag in deptInfo.tags"
                  :key="tag.id"
                  class="mb-1">
                  {{ tag.name }}
                </Tag>
              </div>
            </div>
          </div>
        </Card>
        
        <!-- Tab content -->
        <div class="flex-1">
          <PureCard class="px-2 min-h-full">
            <Tabs
              v-show="state.currentSelectedNode.id"
              class="dept-tab"
              size="small">
              <!-- Users tab -->
              <TabPane key="user" :tab="t('permissionsStrategy.auth.dept')">
                <div class="flex item-center justify-between mb-2">
                  <Input
                    v-model:value="userSearchName"
                    :placeholder="t('searchUserName')"
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
                  <div class="flex items-center">
                    <ButtonAuth
                      class="mr-2"
                      code="DeptUserAssociate"
                      type="primary"
                      icon="icon-tianjia"
                      @click="concatUser" />
                    <IconRefresh
                      :loading="userLoading"
                      :disabled="userLoadDisabled"
                      @click="handleRefreshUser" />
                  </div>
                </div>
                
                <!-- Users table -->
                <Table
                  :dataSource="state.userDataSource"
                  rowKey="id"
                  :columns="columns"
                  :pagination="pagination"
                  :loading="userLoading"
                  size="small"
                  @change="tableChange">
                  <template #bodyCell="{ column,text, record }">
                    <template v-if="column.dataIndex === 'fullName'">
                      <div class="flex items-center" :title="text">
                        <Image
                          :src="record.avatar"
                          type="avatar"
                          class="w-5 h-5 rounded-full mr-2" />
                        {{ text }}
                      </div>
                    </template>
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
              
              <!-- Policy tab -->
              <TabPane key="strategy" :tab="t('authorizationPolicy')">
                <div class="flex items-center justify-between mb-2">
                  <Input
                    :placeholder="t('查询策略名称')"
                    class="w-60"
                    allowClear
                    @change="policyNameChange">
                    <template #suffix>
                      <Icon class="text-theme-content text-theme-text-hover text-3 leading-3" icon="icon-sousuo" />
                    </template>
                  </Input>
                  <div class="flex space-x-2.5 items-center">
                    <ButtonAuth
                      code="DeptAuthorize"
                      type="primary"
                      icon="icon-tianjia"
                      @click="openPolicyModal" />
                    <IconRefresh
                      :loading="policyLoading"
                      :disabled="policyLoadDisabled"
                      @click="getDeptPolicy" />
                  </div>
                </div>
                
                <!-- Policy table -->
                <Table
                  :columns="policyColumns"
                  size="small"
                  :loading="policyLoading"
                  :dataSource="policyData"
                  :pagination="policyPagination"
                  @change="changePolicyPage">
                  <template #bodyCell="{ column,text,record }">
                    <template v-if="column.dataIndex === 'name'">
                      <div :title="record.description">{{ text }}</div>
                    </template>
                    <template v-if="column.dataIndex === 'enabled'">
                      {{ text?'有效':'无效' }}
                    </template>
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
  
  <!-- Modal components -->
  <AsyncComponent :visible="addModalVisible">
    <AddDeptModal
      v-if="addModalVisible"
      v-model:visible="addModalVisible"
      :pname="currentActionNode.name"
      :pid="addPid"
      @close="closeAdd()"
      @save="saveAdd" />
  </AsyncComponent>
  
  <AsyncComponent :visible="editModalVisible">
    <EditModal
      :id="currentActionNode.id"
      :visible="editModalVisible"
      :name="currentActionNode.name"
      @close="closeEditName()"
      @save="saveEditName" />
  </AsyncComponent>
  
  <AsyncComponent :visible="editTagVisible">
    <SelectTargetModal
      v-model:visible="editTagVisible"
      :deptId="currentActionNode.id"
      type="Dept"
      @change="tagSave" />
  </AsyncComponent>
  
  <AsyncComponent :visible="userVisible">
    <UserModal
      v-if="userVisible"
      v-model:visible="userVisible"
      type="Dept"
      :updateLoading="userUpdateLoading"
      :deptId="state.currentSelectedNode.id"
      @change="userSave" />
  </AsyncComponent>
  
  <AsyncComponent :visible="moveVisible">
    <MoveDeptModal
      v-model:visible="moveVisible"
      :moveId="currentActionNode.id"
      :defaultPid="currentActionNode.pid"
      @ok="confirmMove" />
  </AsyncComponent>
  
  <AsyncComponent :visible="policyVisible">
    <PolicyModal
      v-model:visible="policyVisible"
      :deptId="state.currentSelectedNode.id"
      :updateLoading="policyUpdateLoading"
      type="Dept"
      @change="policySave" />
  </AsyncComponent>
</template>

<style scoped>
.dept-tab :deep(.ant-tabs-nav) {
  @apply mb-1.5;
}

.dept-tab :deep(.ant-tabs-nav::before) {
  @apply mb-1.5;
  display: none;
}
</style>
