<script setup lang="ts">
import { ref, watch, computed, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import {
  Hints,
  IconRequired,
  Image,
  PureCard,
  Input,
  SelectEnum,
  notification,
  Icon,
  Grid,
  Spin,
  NoData
} from '@xcan-angus/vue-ui';
import { debounce } from 'throttle-debounce';
import { duration } from '@xcan-angus/tools';
import { Divider, CheckboxGroup, Checkbox, Tooltip, Popover, Pagination, Tree } from 'ant-design-vue';
import { dept, user, group } from '@/api';

import { ReceiveObjectType } from './PropsType';

interface Props {
  receiveObjectType: ReceiveObjectType;
  userList: { id: string, fullName: string }[];
  deptList: { id: string, name: string }[];
  groupList: { id: string, name: string }[];
}

withDefaults(defineProps<Props>(), {
  userList: () => [],
  deptList: () => [],
  groupList: () => []
});

const emit = defineEmits<{
  (e: 'update:receiveObjectType', value: ReceiveObjectType): void,
  (e: 'update:userList', value: { id: string, fullName: string }[]): void,
  (e: 'update:groupList', value: { id: string, name: string }[]): void,
  (e: 'update:deptList', value: { id: string, name: string }[]): void,
}>();

const { t } = useI18n();

const receiveType = ref<ReceiveObjectType>('USER');

const excludes = ({ value }) => {
  return ['TO_POLICY', 'POLICY', 'ALL'].includes(value);
};

const tenantDisabled = ref(false);

const isFirstLoad = ref(true);
const loading = ref(false);
const total = ref(0);
// 用户列表
const userDataList = ref<{ id: string, fullName: string, avatar?: '', isEdit?: boolean }[]>([]);
const params = ref<{ pageNo: number, pageSize: number, filters: any[] }>({ pageNo: 1, pageSize: 30, filters: [] });
const loadUserList = async () => {
  loading.value = true;
  const [error, { data }] = await user.getUsers(params.value);
  loading.value = false;
  isFirstLoad.value = false;

  if (error) {
    return;
  }
  loading.value = false;
  userDataList.value = data.list;
  total.value = +data.total;
};

const paginationChange = (page: number, size: number) => {
  params.value.pageNo = page;
  params.value.pageSize = size;
  checkedList.value = [];
  indeterminate.value = false;
  checkedAll.value = false;
  switch (receiveType.value) {
    case 'USER':
      loadUserList();
      break;
    case 'GROUP':
      loadGroupList();
      break;
    case 'DEPT':
      loadDeptList();
      break;
  }
};

const checkedList = ref<string[]>([]);
const receiveObjectTypeChange = (value: ReceiveObjectType) => {
  params.value.pageNo = 1;
  checkedList.value = [];
  indeterminate.value = false;
  checkedAll.value = false;
  isFirstLoad.value = true;
  userDataList.value = [];
  deptDataList.value = [];
  deptTreeData.value = [];
  groupDataList.value = [];
  if (!['TENANT'].includes(value)) {
    tenantDisabled.value = false;
    switch (value) {
      case 'USER':
        loadUserList();
        break;
      case 'GROUP':
        loadGroupList();
        break;
      case 'DEPT':
        loadDeptList();
        break;
    }
  }

  emit('update:receiveObjectType', value);
};

const expandedKeys = ref<string[]>([]);

const groupDataList = ref<{ id: string, name: string, avatar?: '' }[]>([]);
const loadGroupList = async () => {
  loading.value = true;
  const [error, { data }] = await group.searchGroups(params.value);
  loading.value = false;
  if (error) {
    return;
  }
  groupDataList.value = data.list;
  total.value = +data.total;
};

const deptTreeData = ref<any[]>([]);
const deptDataList = ref<{ id: string, name: string, avatar?: '' }[]>([]);
const loadDeptList = async () => {
  loading.value = true;
  const [error, { data }] = await dept.searchDepts(params.value);
  loading.value = false;
  if (error) {
    return;
  }
  deptDataList.value = data.list;
  deptTreeData.value = buildTree(data.list);
  total.value = +data.total;
};

const buildTree = (_treeData) => {
  if (!_treeData?.length) {
    return [];
  }
  const result: any[] = [];
  const itemMap = {};
  for (const item of _treeData) {
    const id = item.id;
    const pid = item.pid;

    if (!itemMap[id]) {
      itemMap[id] = {
        children: []
      };
    }

    itemMap[id] = {
      ...item,
      children: itemMap[id].children
    };

    const treeItem = itemMap[id];

    if (pid === '-1') {
      result.push(treeItem);
    } else {
      if (!itemMap[pid]) {
        itemMap[pid] = {
          children: []
        };
      }
      itemMap[pid].children.push(treeItem);
    }
  }
  return result;
};

const handleCheck = (checkedKeys: string[]) => {
  checkedList.value = checkedKeys;
  if (!checkedList.value.length) {
    indeterminate.value = false;
    return;
  }

  let dataList;

  switch (receiveType.value) {
    case 'USER':
      dataList = userDataList.value;
      break;
    case 'GROUP':
      dataList = groupDataList.value;
      break;
    case 'DEPT':
      dataList = deptDataList.value;
      break;
    default:
      dataList = [];
  }

  if (dataList.length === checkedList.value.length) {
    checkedAll.value = true;
    indeterminate.value = false;
  } else {
    checkedAll.value = false;
    indeterminate.value = true;
  }
};

const disabled = computed(() => {
  return ['TENANT', 'ALL'].includes(receiveType.value);
});

const searchChange = debounce(duration.search, (event: any): void => {
  const value = event.target.value;
  checkedList.value = [];
  indeterminate.value = false;
  checkedAll.value = false;
  if (receiveType.value === 'USER') {
    params.value.filters = value ? [{ key: 'fullName', op: 'MATCH_END', value: value }] : [];
    loadUserList();
  } else {
    if (['DEPT', 'GROUP'].includes(receiveType.value)) {
      params.value.filters = value ? [{ key: 'name', op: 'MATCH_END', value: value }] : [];
      receiveType.value === 'DEPT' ? loadDeptList() : loadGroupList();
    }
  }
});

watch(() => checkedList.value, (newValue) => {
  if (receiveType.value === 'USER') {
    emit('update:userList', userDataList.value.filter(item => newValue.includes(item.id)));
  }

  if (receiveType.value === 'DEPT') {
    emit('update:deptList', deptDataList.value.filter(item => newValue.includes(item.id)));
  }

  if (receiveType.value === 'GROUP') {
    emit('update:groupList', groupDataList.value.filter(item => newValue.includes(item.id)));
  }
}, {
  immediate: true
});

// 全选
const indeterminate = ref<boolean>(false);
const checkedAll = ref<boolean>(false);
const onCheckAllChange = e => {
  if (checkedList.value.length >= 500 || deptDataList.value.length >= 500) {
    notification.error('最多选中500条数据');
    return;
  }
  checkedAll.value = e.target.checked;
  if (e.target.checked) {
    indeterminate.value = false;
    switch (receiveType.value) {
      case 'USER':
        checkedList.value = userDataList.value.map(item => item.id);
        break;
      case 'GROUP':
        checkedList.value = groupDataList.value.map(item => item.id);
        break;
      case 'DEPT':
        checkedList.value = deptDataList.value.map(item => item.id);
        break;
    }
  } else {
    checkedList.value = [];
    indeterminate.value = false;
  }
};

// 显示全选
const showAllSelect = computed(() => {
  switch (receiveType.value) {
    case 'USER':
      return !!userDataList.value.length;
    case 'GROUP':
      return !!groupDataList.value.length;
    case 'DEPT':
      return !!deptDataList.value.length;
  }
  return false;
});

onMounted(() => {
  emit('update:receiveObjectType', receiveType.value);
  loadUserList();
});

const getPopupContainer = (node: HTMLElement): HTMLElement => {
  if (node) {
    return node.parentNode as HTMLElement;
  }

  return document.body;
};
const columns = [
  [{ label: '名称', dataIndex: 'fullName' }, { label: 'ID', dataIndex: 'id' }]
];

const treeFieldNames = { title: 'name', key: 'id', children: 'children' };
</script>
<template>
  <PureCard class="w-100 h-full">
    <Spin class="h-full p-2 text-3 leading-3 flex flex-col" :spinning="loading">
      <div class="flex items-center whitespace-nowrap">
        <IconRequired class="mr-1" />
        {{ t('recipient') }}
        <SelectEnum
          v-model:value="receiveType"
          class="w-full ml-2"
          enumKey="ReceiveObjectType"
          :excludes="excludes"
          @change="receiveObjectTypeChange">
        </SelectEnum>
      </div>
      <Divider />
      <template v-if="receiveType !=='TENANT'">
        <Input
          :disabled="disabled"
          :placeholder="t('appName')"
          allowClear
          @change="searchChange" />
        <div class="flex-1 overflow-y-auto -mr-2 pt-2">
          <template v-if="receiveType === 'GROUP'">
            <template v-if="groupDataList.length">
              <CheckboxGroup
                :value="checkedList"
                class="space-y-2"
                @change="handleCheck">
                <div
                  v-for="item, in groupDataList"
                  :key="item.id"
                  class="flex-1 flex items-center">
                  <Checkbox
                    :value="item.id"
                    :disabled="checkedList.length >= 500 && !checkedList.includes(item.id)">
                  </Checkbox>
                  <Icon icon="icon-zu1" class="ml-2 mr-2 -mt-0.25 text-4" />
                  <Tooltip
                    placement="bottomLeft"
                    overlayClassName="ant-dropdown-sm"
                    :overlayStyle="{'max-width':'316px'}"
                    :getPopupContainer="getPopupContainer"
                    :title="item.name">
                    <div class=" truncate w-80 cursor-pointer leading-5">
                      {{ item.name }}
                    </div>
                  </Tooltip>
                </div>
              </CheckboxGroup>
            </template>
            <template v-else>
              <template v-if="!isFirstLoad && !loading">
                <NoData class="h-full" />
              </template>
            </template>
          </template>
          <template v-if="receiveType === 'USER'">
            <template v-if="userDataList.length">
              <CheckboxGroup
                :value="checkedList"
                class="space-y-2"
                @change="handleCheck">
                <div
                  v-for="item in userDataList"
                  :key="item.id"
                  class="relative flex-1 flex items-center">
                  <Checkbox
                    :value="item.id"
                    :disabled="checkedList.length >= 500 && !checkedList.includes(item.id)">
                  </Checkbox>
                  <Image
                    :src="item?.avatar"

                    type="avatar"
                    class="w-4 h-4 rounded-full ml-2 " />
                  <Popover
                    placement="bottomLeft"
                    overlayClassName="ant-dropdown-sm"
                    :mouseEnterDelay="0.3"
                    :overlayStyle="{width:'320px'}"
                    :getPopupContainer="getPopupContainer">
                    <template #content>
                      <Grid
                        :columns="columns"
                        :dataSource="item"
                        nowrap />
                    </template>
                    <div class="flex-1 truncate cursor-pointer leading-5 px-2 rounded bg-theme-menu-hover">
                      {{ item.fullName }}
                    </div>
                  </Popover>
                </div>
              </CheckboxGroup>
            </template>
            <template v-else>
              <template v-if="!isFirstLoad && !loading">
                <NoData class="h-full" />
              </template>
            </template>
          </template>
          <template v-if="receiveType === 'DEPT'">
            <template v-if="deptTreeData.length">
              <Tree
                v-model:expandedKeys="expandedKeys"
                v-model:checkedKeys="checkedList"
                :fieldNames="treeFieldNames"
                checkable
                showIcon
                class="text-3"
                :treeData="deptTreeData"
                @check="handleCheck">
                <template #icon>
                  <Icon icon="icon-bumen1" class="text-4 -ml-2" />
                </template>
              </Tree>
            </template>
            <template v-else>
              <template v-if="!isFirstLoad && !loading">
                <NoData class="h-full" />
              </template>
            </template>
          </template>
        </div>
        <Pagination
          :current="params.pageNo"
          :pageSize="params.pageSize"
          :total="total"
          :hideOnSinglePage="false"
          :showTotal="false"
          :showSizeChanger="false"
          showLessItems
          size="small"
          class="mt-2 mr-2"
          @change="paginationChange" />
        <template v-if="showAllSelect">
          <div class="border-t border-theme-divider mt-2 py-1 flex items-center">
            <Checkbox
              v-model:checked="checkedAll"
              class="text-3 leading-3"
              :indeterminate="indeterminate"
              @change="onCheckAllChange">
              {{ t('selectAll') }}
            </Checkbox>
            <Hints :text="t('sendTips')" class="mt-1.5" />
          </div>
        </template>
      </template>
    </Spin>
  </PureCard>
</template>
