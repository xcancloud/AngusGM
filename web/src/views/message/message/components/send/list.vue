<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import {
  Grid, Hints, Icon, IconRequired, Image, Input, NoData, notification, PureCard, SelectEnum, Spin
} from '@xcan-angus/vue-ui';
import { debounce } from 'throttle-debounce';
import { duration, ReceiveObjectType } from '@xcan-angus/infra';
import { Checkbox, CheckboxGroup, Divider, Pagination, Popover, Tooltip, Tree } from 'ant-design-vue';
import { dept, group, user } from '@/api';

/**
 * Component props interface
 * Defines the properties passed from parent component
 */
interface Props {
  receiveObjectType: ReceiveObjectType;
  userList: { id: number, fullName: string }[];
  deptList: { id: number, name: string }[];
  groupList: { id: number, name: string }[];
}

withDefaults(defineProps<Props>(), {
  userList: () => [],
  deptList: () => [],
  groupList: () => []
});

/**
 * Component emits interface
 * Defines the events that this component can emit to parent
 */
const emit = defineEmits<{
  (e: 'update:receiveObjectType', value: ReceiveObjectType): void,
  (e: 'update:userList', value: { id: number, name: string }[]): void,
  (e: 'update:groupList', value: { id: number, name: string }[]): void,
  (e: 'update:deptList', value: { id: number, name: string }[]): void,
}>();

const { t } = useI18n();

/**
 * Current recipient object type
 * Determines which type of recipients to display and manage
 */
const receiveType = ref<ReceiveObjectType>(ReceiveObjectType.USER);

/**
 * Tenant selection disabled state
 * Prevents tenant selection for certain recipient types
 */
const tenantDisabled = ref(false);

/**
 * First load flag for loading states
 * Used to show appropriate loading indicators
 */
const isFirstLoad = ref(true);

/**
 * Loading state for API operations
 * Shows loading spinner during data fetching
 */
const loading = ref(false);

/**
 * Total count of available items
 * Used for pagination calculations
 */
const total = ref(0);

/**
 * Check if recipient type should be excluded
 * Filters out certain recipient types from selection
 */
const excludes = ({ value }: { value: any }) => {
  return [ReceiveObjectType.TO_POLICY, ReceiveObjectType.POLICY, ReceiveObjectType.ALL].includes(value);
};

/**
 * User data list for display
 * Contains user information for recipient selection
 */
const userDataList = ref<{ id: number, fullName: string, avatar?: '', isEdit?: boolean }[]>([]);

/**
 * Search and pagination parameters
 * Manages API request parameters for data fetching
 */
const params = ref<{ pageNo: number, pageSize: number, filters: any[], fullTextSearch: boolean }>({
  pageNo: 1,
  pageSize: 30,
  filters: [],
  fullTextSearch: true
});

/**
 * Fetch user list from API
 * Retrieves paginated user data for recipient selection
 */
const getUserList = async () => {
  loading.value = true;

  try {
    const [error, { data }] = await user.getUserList(params.value);

    if (error) {
      return;
    }

    userDataList.value = data.list;
    total.value = +data.total;
  } finally {
    loading.value = false;
    isFirstLoad.value = false;
  }
};

/**
 * Handle pagination changes
 * Updates page parameters and fetches corresponding data
 */
const paginationChange = (page: number, size: number) => {
  params.value.pageNo = page;
  params.value.pageSize = size;

  switch (receiveType.value) {
    case ReceiveObjectType.USER:
      getUserList();
      break;
    case ReceiveObjectType.GROUP:
      getGroupList();
      break;
    case ReceiveObjectType.DEPT:
      getDeptList();
      break;
  }
};

/**
 * Selected recipients list
 * Tracks which recipients are currently selected
 */
const checkedList = ref<{ id: string, name: string }[]>([]);

/**
 * Handle recipient object type change
 * Resets data and fetches new recipient list when type changes
 */
const receiveObjectTypeChange = (value: ReceiveObjectType) => {
  params.value.pageNo = 1;
  checkedList.value = [];
  indeterminate.value = false;
  isFirstLoad.value = true;
  userDataList.value = [];
  deptDataList.value = [];
  deptTreeData.value = [];
  groupDataList.value = [];

  if (![ReceiveObjectType.TENANT].includes(value)) {
    tenantDisabled.value = false;

    switch (value) {
      case ReceiveObjectType.USER:
        getUserList();
        break;
      case ReceiveObjectType.GROUP:
        getGroupList();
        break;
      case ReceiveObjectType.DEPT:
        getDeptList();
        break;
    }
  }

  emit('update:receiveObjectType', value);
};

/**
 * Expanded tree node keys
 * Tracks which tree nodes are expanded in department tree
 */
const expandedKeys = ref<string[]>([]);

/**
 * Group data list for display
 * Contains group information for recipient selection
 */
const groupDataList = ref<{ id: string, name: string, avatar?: '' }[]>([]);

/**
 * Fetch group list from API
 * Retrieves paginated group data for recipient selection
 */
const getGroupList = async () => {
  loading.value = true;

  try {
    const [error, { data }] = await group.getGroupList(params.value);

    if (error) {
      return;
    }

    groupDataList.value = data.list;
    total.value = +data.total;
  } finally {
    loading.value = false;
  }
};

/**
 * Department tree data structure
 * Hierarchical representation of departments for tree display
 */
const deptTreeData = ref<any[]>([]);

/**
 * Department data list for display
 * Contains department information for recipient selection
 */
const deptDataList = ref<{ id: string, name: string, avatar?: '' }[]>([]);

/**
 * Fetch department list from API
 * Retrieves paginated department data and builds tree structure
 */
const getDeptList = async () => {
  loading.value = true;

  try {
    const [error, { data }] = await dept.getDeptList(params.value);
    if (error) {
      return;
    }

    deptDataList.value = data.list;
    deptTreeData.value = buildTree(data.list);
    total.value = +data.total;
  } finally {
    loading.value = false;
  }
};

/**
 * Build hierarchical tree structure from flat list
 * Converts flat department list to nested tree structure
 */
const buildTree = (_treeData: any[]) => {
  if (!_treeData?.length) {
    return [];
  }

  const result: any[] = [];
  const itemMap: any = {};

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

/**
 * Handle individual item checkbox change
 * Updates selected items list when checkbox is toggled
 */
const handleCheck = (event: InputEvent, obj: { id: string; name: string }) => {
  const checked = (event.target as HTMLInputElement).checked;

  if (checked) {
    checkedList.value.push(obj);
  } else {
    checkedList.value = checkedList.value.filter(item => item.id !== obj.id);
  }
};

/**
 * Handle department tree checkbox change
 * Updates selected items list when department tree checkbox is toggled
 */
const handleCheckDept = (_checkIds: string[], e: { checked: boolean, node: { id: string, name: string } }) => {
  const checked = e.checked;
  const obj = { id: e.node.id, name: e.node.name };

  if (checked) {
    checkedList.value.push(obj);
  } else {
    checkedList.value = checkedList.value.filter(item => item.id !== obj.id);
  }
};

/**
 * Computed disabled state
 * Determines if recipient selection should be disabled
 */
const disabled = computed(() => {
  return [ReceiveObjectType.TENANT, ReceiveObjectType.ALL].includes(receiveType.value);
});

/**
 * Handle search input changes with debouncing
 * Filters recipient data based on search input
 */
const searchChange = debounce(duration.search, (event: any): void => {
  const value = event.target.value;
  checkedList.value = [];

  if (receiveType.value === ReceiveObjectType.USER) {
    params.value.filters = value ? [{ key: 'fullName', op: 'MATCH_END', value: value }] : [];
    getUserList();
  } else {
    if ([ReceiveObjectType.DEPT, ReceiveObjectType.GROUP].includes(receiveType.value)) {
      params.value.filters = value ? [{ key: 'name', op: 'MATCH_END', value: value }] : [];
      receiveType.value === ReceiveObjectType.DEPT ? getDeptList() : getGroupList();
    }
  }
});

/**
 * Get popup container for tooltips and popovers
 * Ensures proper positioning of overlay elements
 */
const getPopupContainer = (node: HTMLElement): HTMLElement => {
  if (node) {
    return node.parentNode as HTMLElement;
  }
  return document.body;
};

/**
 * Grid columns configuration for user details
 * Defines layout for user information display in popover
 */
const columns = [
  [{ label: t('common.columns.name'), dataIndex: 'fullName' }, { label: 'ID', dataIndex: 'id' }]
];

/**
 * Tree field mapping configuration
 * Maps data fields to tree component expected properties
 */
const treeFieldNames = { title: 'name', key: 'id', children: 'children' };

/**
 * Computed checked item IDs
 * Extracts IDs from selected items for checkbox state management
 */
const checkedIds = computed(() => {
  return checkedList.value.map(i => i.id);
});

/**
 * Indeterminate checkbox state
 * Indicates partial selection state for select all functionality
 */
const indeterminate = ref<boolean>(false);

/**
 * Computed select all state
 * Determines if all visible items are currently selected
 */
const checkedAll = computed(() => {
  if (receiveType.value === ReceiveObjectType.USER) {
    return userDataList.value.every(item => {
      return checkedIds.value.includes(item.id);
    });
  }
  if (receiveType.value === ReceiveObjectType.GROUP) {
    return groupDataList.value.every(item => {
      return checkedIds.value.includes(item.id);
    });
  }
  if (receiveType.value === ReceiveObjectType.DEPT) {
    return deptDataList.value.every(item => {
      return checkedIds.value.includes(item.id);
    });
  }
  return false;
});

/**
 * Handle select all checkbox change
 * Selects or deselects all visible items based on checkbox state
 */
const onCheckAllChange = (e: any) => {
  if (checkedList.value.length >= 500 || deptDataList.value.length >= 500) {
    notification.error(t('messages.messages.receiveObjectLimit'));
    return;
  }

  if (e.target.checked) {
    indeterminate.value = false;

    switch (receiveType.value) {
      case ReceiveObjectType.USER:
        userDataList.value.forEach(item => {
          if (!checkedIds.value.includes(item.id)) {
            checkedList.value.push({ id: item.id, name: item.fullName });
          }
        });
        break;
      case ReceiveObjectType.GROUP:
        groupDataList.value.forEach(item => {
          if (!checkedIds.value.includes(item.id)) {
            checkedList.value.push({ id: item.id, name: item.name });
          }
        });
        break;
      case ReceiveObjectType.DEPT:
        deptDataList.value.forEach(item => {
          if (!checkedIds.value.includes(item.id)) {
            checkedList.value.push({ id: item.id, name: item.name });
          }
        });
        break;
    }
  } else {
    switch (receiveType.value) {
      case ReceiveObjectType.USER:
        checkedList.value = checkedList.value
          .filter(checked => !userDataList.value.map(item => item.id).includes(checked.id));
        break;
      case ReceiveObjectType.GROUP:
        checkedList.value = checkedList.value
          .filter(checked => !groupDataList.value.map(item => item.id).includes(checked.id));
        break;
      case ReceiveObjectType.DEPT:
        checkedList.value = checkedList.value
          .filter(checked => !deptDataList.value.map(item => item.id).includes(checked.id));
        break;
    }
    indeterminate.value = false;
  }
};

/**
 * Computed show select all state
 * Determines if select all checkbox should be visible
 */
const showAllSelect = computed(() => {
  switch (receiveType.value) {
    case ReceiveObjectType.USER:
      return !!userDataList.value.length;
    case ReceiveObjectType.GROUP:
      return !!groupDataList.value.length;
    case ReceiveObjectType.DEPT:
      return !!deptDataList.value.length;
  }
  return false;
});

/**
 * Watch selected items changes
 * Synchronizes selected items with parent component
 */
watch(() => checkedList.value, () => {
  if (receiveType.value === ReceiveObjectType.USER) {
    emit('update:userList', checkedList.value);
  }

  if (receiveType.value === ReceiveObjectType.DEPT) {
    emit('update:deptList', checkedList.value);
  }

  if (receiveType.value === ReceiveObjectType.GROUP) {
    emit('update:groupList', checkedList.value);
  }
}, {
  immediate: true
});

/**
 * Initialize component on mount
 * Sets up initial recipient type and loads user data
 */
onMounted(() => {
  emit('update:receiveObjectType', receiveType.value);
  getUserList();
});
</script>

<template>
  <PureCard class="w-100 h-full">
    <Spin class="h-full p-2 text-3 leading-3 flex flex-col" :spinning="loading">
      <!-- Recipient Type Selection Header -->
      <div class="flex items-center whitespace-nowrap">
        <IconRequired class="mr-1" />
        {{ t('messages.columns.recipient') }}
        <SelectEnum
          v-model:value="receiveType"
          class="w-full ml-2"
          :enumKey="ReceiveObjectType"
          :excludes="excludes"
          @change="receiveObjectTypeChange">
        </SelectEnum>
      </div>

      <Divider />

      <!-- Recipient Selection Content -->
      <template v-if="receiveType !== ReceiveObjectType.TENANT">
        <!-- Search Input -->
        <Input
          :disabled="disabled"
          :placeholder="t('messages.placeholder.name')"
          allowClear
          @change="searchChange" />

        <!-- Recipient List Container -->
        <div class="flex-1 overflow-y-auto -mr-2 pt-2">
          <!-- Group Recipients -->
          <template v-if="receiveType === 'GROUP'">
            <template v-if="groupDataList.length">
              <CheckboxGroup
                :value="checkedIds"
                class="space-y-2">
                <div
                  v-for="item in groupDataList"
                  :key="item.id"
                  class="flex-1 flex items-center">
                  <Checkbox
                    :value="item.id"
                    :disabled="checkedIds.length >= 500 && !checkedIds.includes(item.id)"
                    @change="handleCheck($event, {id: item.id, name: item.name})">
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

          <!-- User Recipients -->
          <template v-if="receiveType === ReceiveObjectType.USER">
            <template v-if="userDataList.length">
              <CheckboxGroup
                :value="checkedIds"
                class="space-y-2">
                <div
                  v-for="item in userDataList"
                  :key="item.id"
                  class="relative flex-1 flex items-center">
                  <Checkbox
                    :value="item.id"
                    :disabled="checkedIds.length >= 500 && !checkedIds.includes(item.id)"
                    @change="handleCheck($event, {id: item.id, name: item.fullName})">
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

          <!-- Department Recipients -->
          <template v-if="receiveType === ReceiveObjectType.DEPT">
            <template v-if="deptTreeData.length">
              <Tree
                v-model:expandedKeys="expandedKeys"
                :checkedKeys="checkedIds"
                :fieldNames="treeFieldNames"
                checkable
                showIcon
                checkStrictly
                class="text-3"
                :treeData="deptTreeData"
                @check="handleCheckDept">
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

        <!-- Pagination -->
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

        <!-- Select All Section -->
        <template v-if="showAllSelect">
          <div class="border-t border-theme-divider mt-2 py-1 flex items-center">
            <Checkbox
              :checked="checkedAll"
              class="text-3 leading-3"
              @change="onCheckAllChange">
              {{ t('common.form.selectAll') }}
            </Checkbox>
            <Hints :text="t('messages.messages.receiveObjectLimit')" class="mt-1.5" />
          </div>
        </template>
      </template>
    </Spin>
  </PureCard>
</template>
