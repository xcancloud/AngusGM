<script setup lang='ts'>
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { AsyncComponent, ButtonAuth, Hints, Icon, IconRefresh, Input, Table } from '@xcan-angus/vue-ui';
import { debounce } from 'throttle-debounce';
import { PageQuery, SearchCriteria, duration, utils } from '@xcan-angus/infra';

import { UserGroup } from '../types';
import { user } from '@/api';
import { createUserGroupColumns } from '../utils';

/**
 * Async component for group modal
 * Loaded only when needed to improve performance
 */
const GroupModal = defineAsyncComponent(() => import('@/components/GroupModal/index.vue'));

/**
 * Reactive state management for component
 */
const loading = ref(false); // Loading state for API calls
const params = ref<PageQuery>({ pageNo: 1, pageSize: 10, filters: [] }); // Search and pagination parameters
const total = ref(0); // Total number of groups for pagination
const count = ref(0); // Current group count for quota display
const isContUpdate = ref(true); // Whether to update count continuously
const dataList = ref<UserGroup[]>([]); // Group list data
const groupVisible = ref(false); // Group modal visibility
const updateLoading = ref(false); // Loading state for group update operations
const isRefresh = ref(false); // Refresh state flag for modal operations
const disabled = ref(false); // Disabled state for refresh button during operations

/**
 * Component props interface
 * Defines the properties passed to the user group component
 */
interface Props {
  userId: string; // User ID for group management
  hasAuth: boolean; // Whether user has permission to modify groups
}

const props = withDefaults(defineProps<Props>(), {
  userId: undefined,
  hasAuth: false
});

// Internationalization setup
const { t } = useI18n();

/**
 * Load user groups from API
 * Handles loading state and error handling
 */
const loadUserGroup = async () => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const [error, { data = { list: [], total: 0 } }] = await user.getUserGroup(props.userId, params.value);
  loading.value = false;
  if (error) {
    return;
  }

  dataList.value = data.list;
  total.value = +data.total;
  if (isContUpdate.value) {
    count.value = +data.total;
  }
};

/**
 * Open group modal for adding new groups
 */
const addGroup = () => {
  groupVisible.value = true;
};

/**
 * Handle group save from modal
 * Processes selected group IDs and handles add/delete operations
 * @param _groupIds - Array of group IDs to add
 * @param _groups - Array of group objects
 * @param deleteGroupIds - Array of group IDs to delete
 */
const saveGroup = async (_groupIds: string[], _groups: { id: string, name: string }[], deleteGroupIds: string[]) => {
  if (deleteGroupIds.length) {
    await delUserGroup(deleteGroupIds, 'Modal');
  }

  if (_groupIds.length) {
    await addUserGroup(_groupIds);
  }

  groupVisible.value = false;
  updateLoading.value = false;

  if (isRefresh.value) {
    disabled.value = true;
    await loadUserGroup();
    disabled.value = false;
    isRefresh.value = false;
  }
};

/**
 * Add groups to user
 * Calls API to associate groups with user
 * @param _groupIds - Array of group IDs to add
 */
const addUserGroup = async (_groupIds: string[]) => {
  updateLoading.value = true;
  const [error] = await user.addUserGroup(props.userId, _groupIds);
  if (error) {
    return;
  }
  isRefresh.value = true;
};

/**
 * Delete groups from user
 * Calls API to remove group associations
 * @param _groupIds - Array of group IDs to delete
 * @param type - Operation type ('Modal' or 'Table')
 */
const delUserGroup = async (_groupIds: string[], type?: 'Modal' | 'Table') => {
  updateLoading.value = true;
  const [error] = await user.deleteUserGroup(props.userId, _groupIds);
  if (type === 'Table') {
    updateLoading.value = false;
  }
  if (error) {
    return;
  }

  if (type === 'Modal') {
    isRefresh.value = true;
  }

  // Recalculate current page after deletion
  params.value.pageNo = utils.getCurrentPage(params.value.pageNo as number, params.value.pageSize as number, total.value);

  if (type === 'Table') {
    disabled.value = true;
    await loadUserGroup();
    disabled.value = false;
  }
};

/**
 * Cancel/Remove group from user
 * Calls delete function for table operations
 * @param id - Group ID to remove
 */
const handleCancel = async (id) => {
  await delUserGroup([id], 'Table');
};

/**
 * Handle search input with debouncing
 * Filters groups by name with end matching
 * @param event - Input change event
 */
const handleSearch = debounce(duration.search, async (event: any) => {
  const value = event.target.value;
  params.value.pageNo = 1;
  if (value) {
    params.value.filters = [{ key: 'groupName', op: SearchCriteria.OpEnum.MatchEnd, value }];
  } else {
    params.value.filters = [];
  }
  disabled.value = true;
  isContUpdate.value = false;
  await loadUserGroup();
  isContUpdate.value = true;
  disabled.value = false;
});

/**
 * Computed pagination object for table component
 * Provides reactive pagination data to the table
 */
const pagination = computed(() => {
  return {
    current: params.value.pageNo,
    pageSize: params.value.pageSize,
    total: total.value
  };
});

/**
 * Handle table pagination changes
 * Updates parameters and reloads data based on table interactions
 * @param _pagination - Pagination object from table
 */
const handleChange = async (_pagination) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  disabled.value = true;
  await loadUserGroup();
  disabled.value = false;
};

/**
 * Table columns configuration
 * Defines the structure and behavior of each table column
 */
const columns = createUserGroupColumns(t);

/**
 * Lifecycle hook - initialize component on mount
 * Loads initial group data
 */
onMounted(() => {
  loadUserGroup();
});
</script>
<template>
  <div>
    <!-- User group quota hints -->
    <Hints :text="t('user.userGroupQuotaTip', {num: count})" class="mb-1" />

    <!-- Search and action toolbar -->
    <div class="flex items-center justify-between mb-2">
      <!-- Group name search input -->
      <Input
        :placeholder="t('group.placeholder.name')"
        class="w-60"
        size="small"
        allowClear
        @change="handleSearch">
        <template #suffix>
          <Icon class="text-theme-content text-theme-text-hover text-3 leading-3" icon="icon-sousuo" />
        </template>
      </Input>

      <!-- Action buttons -->
      <div class="flex space-x-2 items-center">
        <!-- Add group button -->
        <ButtonAuth
          code="UserGroupAssociate"
          type="primary"
          icon="icon-tianjia"
          :disabled="hasAuth || total>=200"
          @click="addGroup" />

        <!-- Refresh button -->
        <IconRefresh
          :loading="loading"
          :disabled="disabled"
          @click="loadUserGroup" />
      </div>
    </div>

    <!-- Group data table -->
    <Table
      size="small"
      rowKey="id"
      :loading="loading"
      :dataSource="dataList"
      :columns="columns"
      :pagination="pagination"
      @change="handleChange">
      <!-- Custom cell renderers for table columns -->
      <template #bodyCell="{ column,text, record }">
        <!-- Group name with icon -->
        <template v-if="column.dataIndex === 'groupName'">
          <div class="flex items-center">
            <Icon icon="icon-zu1" class="text-4 mr-2" />
            <div class="w-full truncate" :title="text">{{ text }}</div>
          </div>
        </template>

        <!-- Action buttons for each group row -->
        <template v-if="column.dataIndex === 'action'">
          <ButtonAuth
            code="UserGroupUnassociate"
            type="text"
            :disabled="hasAuth"
            icon="icon-quxiao"
            @click="handleCancel(record.groupId)" />
        </template>
      </template>
    </Table>
  </div>

  <!-- Group modal for adding new groups -->
  <AsyncComponent :visible="groupVisible">
    <GroupModal
      v-if="groupVisible"
      v-model:visible="groupVisible"
      :userId="userId"
      :updateLoading="updateLoading"
      type="User"
      @change="saveGroup" />
  </AsyncComponent>
</template>
