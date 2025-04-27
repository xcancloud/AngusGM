<script setup lang="ts">
import { reactive, computed, defineAsyncComponent, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Tag, Tabs, TabPane, Dropdown, Menu, MenuItem } from 'ant-design-vue';
import {
  ButtonAuth,
  modal,
  notification,
  PureCard,
  Icon,
  AsyncComponent,
  Image,
  Table,
  Input,
  Select,
  Tree,
  Card,
  IconRefresh,
  IconCount
} from '@xcan-angus/vue-ui';
import { app, utils, duration, GM } from '@xcan-angus/tools';
import { debounce } from 'throttle-debounce';

import { DataType, TreeRecordType, UserRecordType } from './PropsType';
import { auth, dept } from '@/api';

type FilterOp =
  'EQUAL'
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
  | 'NOT_IN'
type Filters = { key: string, value: string, op: FilterOp }[]
type SearchParams = {
  pageNo?: number;
  pageSize?: number;
  filters?: Filters;
  orderBy?: string;
  orderSort?: 'ASC' | 'DESC';
}

const SelectTargetModal = defineAsyncComponent(() => import('@/components/TagModal/index.vue'));
const AddDeptModal = defineAsyncComponent(() => import('@/views/organization/dept/components/add/index.vue'));
const EditModal = defineAsyncComponent(() => import('@/views/organization/dept/components/edit/index.vue'));
const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));
const UserModal = defineAsyncComponent(() => import('@/components/UserModal/index.vue'));
const MoveDeptModal = defineAsyncComponent(() => import('./components/move/index.vue'));
const PolicyModal = defineAsyncComponent(() => import('@/components/PolicyModal/index.vue'));

const { t } = useI18n();

const showCount = ref(true);
const state = reactive<{
  sortForm: any;
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
}>({
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

const treeSelect = ref();
const changeSelect = async (_selectedKeys: string[], selected: boolean, info: {
  id: string | undefined,
  name: string | undefined,
  pid: string | undefined
}) => {
  const { id, name, pid } = info;
  if (selected) {
    state.currentSelectedNode = { id, name, pid };
    params.value.pageNo = 1;
    policyPagination.current = 1;
    userLoadDisabled.value = true;
    await loadUser();
    userLoadDisabled.value = false;
    loadDeptInfo();
    policyLoadDisabled.value = true;
    await getDeptPolicy();
    policyLoadDisabled.value = false;
  } else {
    state.currentSelectedNode = { id: undefined, name: undefined, pid: undefined };
  }
};

const params = ref<SearchParams>({ pageNo: 1, pageSize: 10, filters: [] });
const total = ref(0);
const userLoading = ref(false);
const loadUser = async () => {
  if (!state.currentSelectedNode.id) {
    notification.warning(t('loadUserWarn'));
    return;
  }

  if (userLoading.value) {
    return;
  }

  if (userSearchName.value && params.value.filters) {
    params.value.filters.push({ key: 'fullName', op: 'MATCH_END', value: userSearchName.value });
  } else {
    params.value.filters = [];
  }
  userLoading.value = true;
  const [error, res] = await dept.getDeptUsers(state.currentSelectedNode.id, params.value);
  userLoading.value = false;
  if (error) {
    return;
  }

  state.userDataSource = res.data.list;
  total.value = +res.data.total;
};

const addModalVisible = ref(false);
const addPid = ref('-1');
const addDept = (pid = '-1' as string) => {
  addModalVisible.value = true;
  addPid.value = pid;
};
const closeAdd = () => {
  addModalVisible.value = false;
};
const saveAdd = (value) => {
  closeAdd();
  if (value.pid && +value.pid < 0 && !searchDeptId.value) {
    notify.value += 1;
  } else {
    treeSelect.value.add(value);
  }
};

const editModalVisible = ref(false);
const editDeptName = () => {
  editModalVisible.value = true;
};
const closeEditName = () => {
  editModalVisible.value = false;
};
const saveEditName = (name) => {
  closeEditName();
  treeSelect.value.edit({
    id: currentActionNode.value.id,
    name
  });
};

const del = async () => {
  const [error, res] = await dept.getCountNum(currentActionNode.value.id as string);
  if (error) {
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
      const [error] = await dept.deleteDept({ ids: [currentActionNode.value.id as string] });
      if (error) {
        return;
      }
      notification.success(t('delSuccess'));
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
    }
  });
};

// 关联标签
const editTagVisible = ref(false);
const editTag = async () => {
  editTagVisible.value = true;
};

// 用户搜索
const userSearchName = ref();
const searchLoadUser = debounce(duration.search, () => {
  params.value.pageNo = 1;
  loadUser();
});

// 关联用户
const userVisible = ref(false);
const concatUser = async () => {
  userVisible.value = true;
};

// 保存关联用户
const isRefresh = ref(false);
const userUpdateLoading = ref(false);
const userLoadDisabled = ref(false);
const userSave = async (_userIds: string[], _users: { id: string, fullName: string }[], deleteUserIds: string[]) => {
  // 如果有删除的用户
  if (deleteUserIds.length) {
    await delDeptUser(deleteUserIds, 'Modal');
  }

  // 如果有新增的用户
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
};

// 部门添加用户
const addDeptUser = async (userIds: string[]) => {
  userUpdateLoading.value = true;
  const [error] = await dept.createDeptUser(state.currentSelectedNode.id as string, userIds);
  userUpdateLoading.value = false;
  if (error) {
    return;
  }
  isRefresh.value = true;
};

// 删除部门用户
const delDeptUser = async (userIds: string[], type?: 'Table' | 'Modal') => {
  userUpdateLoading.value = true;
  const [error] = await dept.deleteDeptUser(state.currentSelectedNode.id as string, userIds);
  userUpdateLoading.value = false;
  if (type === 'Table') {
    userUpdateLoading.value = false;
  }
  if (error) {
    return;
  }

  if (type === 'Modal') {
    isRefresh.value = true;
  }

  params.value.pageNo = utils.getCurrentPage(params.value.pageNo as number, params.value.pageSize as number, total.value);
  // 要求表格操作不影响刷新图标
  if (type === 'Table') {
    userLoadDisabled.value = true;
    await loadUser();
    userLoadDisabled.value = false;
  }
};

const getSearchDeptParams = computed(() => {
  return {
    tagId: searchTagId.value
  };
});

// 部门搜索框
const searchDeptId = ref();
const selectedDept = ref<string>();
const handleSearchDept = async (value) => {
  if (!value) {
    return;
  }
  const [error, res] = await dept.getNavigationByDeptId({ id: value });
  if (error) {
    return;
  }
  const parentChain = (res.data.parentChain || []).map(item => ({ ...item, hasSubDept: true }));
  const { id, pid, name } = res.data.current;
  setTimeout(() => {
    selectedDept.value = id;
  });
  state.dataSource = [...parentChain, res.data.current];
  changeSelect([id], true, { id, pid, name });
};

// 标签搜索框
const searchTagId = ref();
const handleSearchTag = (value) => {
  searchTagId.value = value;
  if (searchDeptId.value) {
    searchDeptId.value = undefined;
  }
  changeSelect([], false, state.currentSelectedNode);
};

const treeFieldNames = { label: 'name', children: 'hasSubDept' };

const treeParams = computed(() => {
  return { pid: -1, pageSize: 30, tagId: searchTagId.value, orderBy: 'createdDate', orderSort: 'ASC' };
});

const hasDept = ref(false);
const loaded = (options) => {
  hasDept.value = !!options.length;
};

const tagSave = async (_tagIds: string[], _tags: { id: string, name: string }[], deleteTagIds: string[]) => {
  // 如果有新增的标签
  if (_tagIds.length) {
    await dept.addDeptTags({ id: currentActionNode.value.id as string, ids: _tagIds });
  }
  // 如果有删除的标签
  if (deleteTagIds.length) {
    await dept.deleteDeptTag({ id: currentActionNode.value.id as string, ids: deleteTagIds });
  }
  editTagVisible.value = false;
  loadDeptInfo();
};

const deptInfo = reactive({
  createdByName: '',
  createdDate: '',
  tags: [] as { id: string, name: string }[],
  level: '',
  name: '',
  code: '',
  id: '',
  lastModifiedDate: '',
  lastModifiedByName: ''
});
const loadDeptInfo = async () => {
  const [error, { data = {} }] = await dept.getDeptDetail(state.currentSelectedNode.id as string);
  if (error) {
    return;
  }
  deptInfo.name = data.name;
  deptInfo.code = data.code;
  deptInfo.id = data.id;
  deptInfo.createdByName = data.createdByName;
  deptInfo.createdDate = data.createdDate;
  deptInfo.tags = data.tags || [];
  deptInfo.level = data.level;
  deptInfo.lastModifiedDate = data.lastModifiedDate;
  deptInfo.lastModifiedByName = data.lastModifiedByName || '--';
};
const handleRefreshUser = () => {
  loadUser();
};

// 移动部门
const notify = ref(0);
const moveVisible = ref(false);
const openMove = () => {
  moveVisible.value = true;
};

const confirmMove = async (targetId) => {
  const [error] = await dept.updateDept([{ pid: targetId, id: currentActionNode.value.id }]);
  if (error) {
    return;
  }
  moveVisible.value = false;
  notify.value += 1;
};

const treeLoding = ref(false);
const handleRefreshDeptList = () => {
  notify.value++;
};

const pagination = computed(() => {
  return {
    current: params.value.pageNo,
    pageSize: params.value.pageSize,
    total: total.value
  };
});

const tableChange = async (_pagination) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  userLoadDisabled.value = true;
  await loadUser();
  userLoadDisabled.value = false;
};

const columns = [
  {
    title: t('用户ID'),
    dataIndex: 'id',
    width: '22%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('userName1'),
    dataIndex: 'fullName',
    ellipsis: true
  },
  {
    title: t('associatedPerson'),
    dataIndex: 'createdByName',
    width: '20%'
  },
  {
    title: t('associatedTime'),
    dataIndex: 'createdDate',
    width: '20%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('operation'),
    dataIndex: 'action',
    width: 82,
    align: 'center'
  }
];
const policyColumns = [
  {
    title: t('策略ID'),
    dataIndex: 'id',
    width: '12%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('permissionsStrategy.auth.name'),
    dataIndex: 'name',
    ellipsis: true
  },
  {
    title: t('permissionsStrategy.detail.info.code'),
    dataIndex: 'code',
    width: '11%'
  },
  {
    title: t('授权人'),
    dataIndex: 'authByName',
    width: '11%'
  },
  {
    title: t('授权时间'),
    dataIndex: 'authDate',
    width: '11%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('operation'),
    dataIndex: 'action',
    width: 82,
    align: 'center'
  }
];
const policyVisible = ref(false);

const openPolicyModal = () => {
  policyVisible.value = true;
};

const policyData = ref([]);
const policyPagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
});
const policyFilters = ref<{
  key: string;
  value: string;
  op: FilterOp;
}[]>([]);

const policyLoading = ref(false);
const getDeptPolicy = async () => {
  if (policyLoading.value) {
    return;
  }
  const { pageSize, current } = policyPagination;
  policyLoading.value = true;
  const [error, res] = await auth.getDeptPolicy(state.currentSelectedNode.id as string, {
    pageSize,
    pageNo: current,
    filters: policyFilters.value
  });
  policyLoading.value = false;
  if (error) {
    return;
  }
  policyData.value = res.data?.list || [];
  policyPagination.total = res.data.total;
};
const policyLoadDisabled = ref(false);
const policyUpdateLoading = ref(false);
const policySave = async (addIds) => {
  if (!addIds.length) {
    policyVisible.value = false;
    return;
  }

  policyUpdateLoading.value = true;
  const [error] = await auth.addPolicyByDept(state.currentSelectedNode.id as string, addIds);
  policyUpdateLoading.value = false;
  policyVisible.value = false;
  if (error) {
    return;
  }
  policyLoadDisabled.value = true;
  await getDeptPolicy();
  policyLoadDisabled.value = false;
};

const handleCancel = async (delId) => {
  const [error] = await auth.deletePolicyByDept(state.currentSelectedNode.id as string, [delId]);
  if (error) {
    return;
  }
  policyLoadDisabled.value = true;
  await getDeptPolicy();
  policyLoadDisabled.value = false;
};

const changePolicyPage = async (page) => {
  policyPagination.current = page.current;
  policyPagination.pageSize = page.pageSize;
  policyLoadDisabled.value = true;
  await getDeptPolicy();
  policyLoadDisabled.value = false;
};

const policyNameChange = debounce(duration.search, async (event: any) => {
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

const refreshDisabled = ref(false);

// 当前选中或者操作的部门
const currentActionNode = ref<{ id: string, name: string, pid: string }>({});

const rightEditDeptName = (selected) => {
  currentActionNode.value = selected;
  editDeptName();
};
const rightAddDept = (selected) => {
  currentActionNode.value = selected;
  addDept(selected.id);
};

const rightDel = (selected) => {
  currentActionNode.value = selected;
  del();
};

const rightEditTag = (selected) => {
  currentActionNode.value = selected;
  editTag();
};
const rightOpenMove = (selected) => {
  currentActionNode.value = selected;
  openMove();
};
</script>
<template>
  <PureCard class="p-3.5 flex flex-col h-full">
    <Statistics
      resource="Dept"
      :geteway="GM"
      dateType="YEAR"
      :visible="showCount"
      :barTitle="t('sector')" />
    <div class="flex space-x-2 flex-1 min-h-0">
      <PureCard class="w-100 p-3 h-full overflow-x-hidden dept-tree pr-2 border-r flex flex-col">
        <div class="flex items-center justify-between space-x-1 mb-2">
          <Select
            v-model:value="searchDeptId"
            :showSearch="true"
            :action="`${GM}/dept/search`"
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
            :action="`${GM}/org/tag/search`"
            @change="handleSearchTag" />
          <ButtonAuth
            code="DeptAdd"
            type="primary"
            icon="icon-tianjia"
            @click="addDept()" />
          <IconRefresh
            :loading="treeLoding"
            :disabled="refreshDisabled"
            @click="handleRefreshDeptList" />
        </div>
        <Tree
          ref="treeSelect"
          v-model:loading="treeLoding"
          v-model:selectedKey="selectedDept"
          :params="treeParams"
          :action="`${GM}/dept/search`"
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
      <div class="flex-1 flex flex-col overflow-y-auto">
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
        <div class="flex-1">
          <PureCard class="px-2 min-h-full">
            <Tabs
              v-show="state.currentSelectedNode.id"
              class="dept-tab"
              size="small">
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
