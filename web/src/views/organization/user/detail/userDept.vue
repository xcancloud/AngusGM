<script setup lang='ts'>
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { AsyncComponent, ButtonAuth, Hints, Icon, IconRefresh, Input, Table } from '@xcan-angus/vue-ui';
import { PageQuery, duration, utils, SearchCriteria } from '@xcan-angus/infra';
import { debounce } from 'throttle-debounce';

import { UserDept } from '../types';
import { user } from '@/api';
import { createUserDeptColumns } from '../utils';

/**
 * Async component for department modal
 * Loaded only when needed to improve performance
 */
const DeptModal = defineAsyncComponent(() => import('@/components/DeptModal/index.vue'));

/**
 * Reactive state management for component
 */
const loading = ref(false); // Loading state for API calls
const params = ref<PageQuery>({ pageNo: 1, pageSize: 10, filters: [] }); // Search and pagination parameters
const total = ref(0); // Total number of departments for pagination
const count = ref(0); // Current department count for quota display
const isContUpdate = ref(true); // Whether to update count continuously
const dataList = ref<UserDept[]>([]); // Department list data

const deptVisible = ref(false); // Department modal visibility
const updateLoading = ref(false); // Loading state for department update operations
const isRefresh = ref(false); // Refresh state flag for modal operations
const disabled = ref(false); // Disabled state for refresh button during operations

/**
 * Component props interface
 * Defines the properties passed to the user department component
 */
interface Props {
  userId: string; // User ID for department management
  hasAuth: boolean; // Whether user has permission to modify departments
}

const props = withDefaults(defineProps<Props>(), {
  userId: undefined,
  hasAuth: false
});

// Internationalization setup
const { t } = useI18n();

/**
 * Load user departments from API
 * Handles loading state and error handling
 */
const loadUserDept = async () => {
  loading.value = true;
  const [error, { data = { list: [], total: 0 } }] = await user.getUserDept(props.userId, params.value);
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
 * Open department modal for adding new departments
 */
const addDept = () => {
  deptVisible.value = true;
};

/**
 * Handle department save from modal
 * Processes selected department IDs and handles add/delete operations
 * Note: Delete operations must be performed before add operations
 * @param addIds - Array of department IDs to add
 * @param delIds - Array of department IDs to delete
 */
const deptSave = async (addIds: string[], delIds: string[]) => {
  // Note: Delete operations must be performed before add operations
  if (delIds.length) {
    await delUserDept(delIds, 'Modal');
  }

  if (addIds.length) {
    await addUserDept(addIds);
  }

  deptVisible.value = false;
  updateLoading.value = false;
  if (isRefresh.value) {
    disabled.value = true;
    await loadUserDept();
    disabled.value = false;
    isRefresh.value = false;
  }
};

/**
 * Add departments to user
 * Calls API to associate departments with user
 * @param _addIds - Array of department IDs to add
 */
const addUserDept = async (_addIds: string[]) => {
  updateLoading.value = true;
  const [error] = await user.addUserDept(props.userId, _addIds);
  if (error) {
    return;
  }
  isRefresh.value = true;
};

/**
 * Delete departments from user
 * Calls API to remove department associations
 * @param _delIds - Array of department IDs to delete
 * @param type - Operation type ('Modal' or 'Table')
 */
const delUserDept = async (_delIds: string[], type?: 'Modal' | 'Table') => {
  updateLoading.value = true;
  const [error] = await user.deleteUserDept(props.userId, { deptIds: _delIds });
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

  // Table operations should not affect refresh icon state
  if (type === 'Table') {
    disabled.value = true;
    await loadUserDept();
    disabled.value = false;
  }
};

/**
 * Cancel/Remove department from user
 * Calls delete function for table operations
 * @param id - Department ID to remove
 */
const handleCancel = async (id) => {
  await delUserDept([id], 'Table');
};

/**
 * Handle search input with debouncing
 * Filters departments by name with end matching
 * @param event - Input change event
 */
const handleSearch = debounce(duration.search, async (event: any) => {
  const value = event.target.value;
  params.value.pageNo = 1;
  if (value) {
    params.value.filters = [{ key: 'deptName', op: SearchCriteria.OpEnum.MatchEnd, value }];
  } else {
    params.value.filters = [];
  }

  disabled.value = true;
  isContUpdate.value = false;
  await loadUserDept();
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
  await loadUserDept();
  disabled.value = false;
};

/**
 * Table columns configuration
 * Defines the structure and behavior of each table column
 */
const columns = createUserDeptColumns(t);

/**
 * Lifecycle hook - initialize component on mount
 * Loads initial department data
 */
onMounted(() => {
  loadUserDept();
});
</script>
<template>
  <div>
    <!-- User department quota hints -->
    <Hints :text="t('department.userDeptQuotaTip', {num: count})" class="mb-1" />

    <!-- Search and action toolbar -->
    <div class="flex items-center justify-between mb-2">
      <!-- Department name search input -->
      <Input
        :placeholder="t('department.placeholder.name')"
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
        <!-- Add department button -->
        <ButtonAuth
          code="UserDeptAssociate"
          type="primary"
          icon="icon-tianjia"
          :disabled="props.hasAuth || total>=5"
          @click="addDept" />

        <!-- Refresh button -->
        <IconRefresh
          :loading="loading"
          :disabled="disabled"
          @click="loadUserDept" />
      </div>
    </div>

    <!-- Department data table -->
    <Table
      size="small"
      rowKey="id"
      :loading="loading"
      :dataSource="dataList"
      :columns="columns"
      :pagination="pagination"
      @change="handleChange">
      <!-- Custom cell renderers for table columns -->
      <template #bodyCell="{ column ,text,record }">
        <!-- Department name with icon -->
        <template v-if="column.dataIndex === 'deptName'">
          <div class="flex items-center">
            <Icon icon="icon-bumen1" class="text-4 mr-2" />
            <div class="w-full truncate" :title="text">{{ text }}</div>
          </div>
        </template>

        <!-- Action buttons for each department row -->
        <template v-if="column.dataIndex === 'action'">
          <ButtonAuth
            code="UserDeptUnassociate"
            type="text"
            :disabled="hasAuth"
            icon="icon-quxiao"
            @click="handleCancel(record.deptId)" />
        </template>
      </template>
    </Table>
  </div>

  <!-- Department modal for adding new departments -->
  <AsyncComponent :visible="deptVisible">
    <DeptModal
      v-if="deptVisible"
      v-model:visible="deptVisible"
      :updateLoading="updateLoading"
      :userId="userId"
      type="User"
      @change="deptSave" />
  </AsyncComponent>
</template>
