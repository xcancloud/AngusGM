<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { Hints, Image, PureCard, Table } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import { PageQuery, GM } from '@xcan-angus/infra';

import { setting, userLog } from '@/api';
import { OperationLogRecord } from './PropsType';

const Statistics = defineAsyncComponent(() => import('@/views/system/log/operation/statistics/index.vue'));
const SearchPanel = defineAsyncComponent(() => import('@/views/system/log/operation/searchPanel/index.vue'));

const { t } = useI18n();

// Component state management
const showCount = ref(true);

// Pagination and query parameters with correct typing
const params = ref<PageQuery>({ pageNo: 1, pageSize: 10, filters: [], fullTextSearch: true });
const total = ref(0);
const loading = ref(false);
const tableList = ref<OperationLogRecord[]>([]);

/**
 * Handle search criteria changes and reset pagination
 * @param data - Search filters data containing key, value, and operation
 */
const searchChange = async (data:{filters:{ key: string; op: string; value: string|string[]; }[]}) => {
  params.value.pageNo = 1; // Reset to first page when search changes
  // Type assertion to convert between compatible filter types
  params.value.filters = data.filters as any;
  await getList();
};

/**
 * Fetch operation log list with current parameters
 * Includes loading state management and error handling
 */
const getList = async () => {
  // Prevent multiple simultaneous requests
  if (loading.value) {
    return;
  }

  loading.value = true;
  try {
    const [error, { data = { list: [], total: 0 } }] = await userLog.getOperationLogList(params.value);
    if (error) {
      return;
    }

    tableList.value = data.list;
    total.value = +data.total;
  } finally {
    loading.value = false;
  }
};

// Computed pagination object for table component
const pagination = computed(() => ({
  current: params.value.pageNo,
  pageSize: params.value.pageSize,
  total: total.value
}));

/**
 * Handle table pagination, sorting, and filtering changes
 * @param _pagination - Pagination object from table
 * @param _filters - Filter object from table
 * @param sorter - Sorting configuration with orderBy and orderSort
 */
const tableChange = async (_pagination: any, _filters: any, sorter: {
  orderBy: string;
  orderSort: PageQuery.OrderSort
}) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  params.value.orderBy = sorter.orderBy;
  params.value.orderSort = sorter.orderSort;
  await getList();
};

// Configuration for log retention period
const clearBeforeDay = ref<string>();

/**
 * Fetch system settings for operation log configuration
 * Retrieves log retention period settings
 */
const getSettings = async () => {
  const [error, { data }] = await setting.getSettingDetail('OPERATION_LOG_CONFIG');
  if (error) {
    return;
  }

  clearBeforeDay.value = data.operationLog?.clearBeforeDay;
};

/**
 * Toggle statistics display visibility
 */
const toggleOpenCount = () => {
  showCount.value = !showCount.value;
};

// Lifecycle hook - initialize component
onMounted(() => {
  getSettings();
  getList();
});

// Table column definitions with sorting and custom rendering
const columns = [
  {
    key: 'id',
    title: t('log.operation.columns.id'),
    dataIndex: 'id',
    hide: true
  },
  {
    key: 'fullName',
    title: t('log.operation.columns.operator'),
    dataIndex: 'fullName',
    width: '15%',
    sorter: true // Enable sorting for operator column
  },
  {
    key: 'description',
    title: t('log.operation.columns.operationContent'),
    dataIndex: 'description',
    width: '35%'
  },
  {
    key: 'resource',
    title: t('log.operation.columns.operationResource'),
    dataIndex: 'resource',
    width: '15%',
    customRender: ({ text }) => text?.message // Custom render for resource display
  },
  {
    key: 'resourceName',
    title: t('log.operation.columns.operationResourceName'),
    dataIndex: 'resourceName',
    groupName: 'resource', // Group with resource column
    width: '20%'
  },
  {
    key: 'resourceId',
    title: t('log.operation.columns.operationResourceId'),
    dataIndex: 'resourceId',
    groupName: 'resource', // Group with resource column
    hide: true,
    width: '20%'
  },
  {
    key: 'optDate',
    title: t('log.operation.columns.operationDate'),
    dataIndex: 'optDate',
    width: '15%',
    sorter: true, // Enable sorting for date column
    customCell: () => {
      return { style: 'white-space:nowrap;' }; // Prevent date wrapping
    }
  }
];
</script>

<template>
  <div class="flex flex-col min-h-full">
    <!-- System hint about log retention period -->
    <Hints
      :text="t('log.operation.messages.description', { days: clearBeforeDay })"
      class="mb-1" />

    <PureCard class="flex-1 p-3.5">
      <!-- Statistics component for operation logs -->
      <Statistics
        resource="OperationLog"
        :barTitle="t('log.operation.statistics.newOperations')"
        dateType="DAY"
        :router="GM"
        :visible="showCount" />

      <!-- Search panel with filters and quick actions -->
      <SearchPanel
        :loading="loading"
        :showCount="showCount"
        @openCount="toggleOpenCount"
        @refresh="getList"
        @change="searchChange" />

      <!-- Data table with pagination and sorting -->
      <Table
        :loading="loading"
        :dataSource="tableList"
        :columns="columns"
        :pagination="pagination"
        rowKey="id"
        size="small"
        :noDataSize="'small'"
        :noDataText="t('common.messages.noData')"
        class="mt-2"
        @change="tableChange">
        <!-- Custom cell rendering for operator column -->
        <template #bodyCell="{column, record}">
          <template v-if="column.dataIndex === 'fullName'">
            <div class="flex items-center">
              <Image
                type="avatar"
                :src="record.avatar"
                class="w-4 h-4 rounded-full mr-1" />
              <span class="flex-1 truncate" :title="record.fullName">{{ record.fullName }}</span>
            </div>
          </template>
        </template>
      </Table>
    </PureCard>
  </div>
</template>
