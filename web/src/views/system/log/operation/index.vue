<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, reactive } from 'vue';
import { Hints, Image, PureCard, Table } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import { GM, enumUtils } from '@xcan-angus/infra';

import { setting, userLog } from '@/api';
import { OperationLogState, PaginationConfig, TableChangeParams, SearchCriteria } from './types';
import {
  createOperationLogColumns, createPaginationConfig, processOperationLogList, updatePaginationParams,
  updateSortingParams, resetPagination, extractSystemSettings, toggleStatisticsVisibility
} from './utils';

import { ChartType, DateRangeType } from '@/components/dashboard/enums';
import {  OperationResourceType } from '@/enums/enums';

// Async component imports
const SearchPanel = defineAsyncComponent(() => import('@/views/system/log/operation/searchPanel/index.vue'));
const Dashboard = defineAsyncComponent(() => import('@/components/dashboard/Dashboard.vue'));

const { t } = useI18n();

// Reactive state management
const state = reactive<OperationLogState>({
  params: {
    pageNo: 1,
    pageSize: 10,
    filters: [],
    fullTextSearch: true
  },
  total: 0,
  tableList: [],
  loading: false,
  showCount: true,
  clearBeforeDay: undefined
});

/**
 * Handle search criteria changes and reset pagination
 */
const searchChange = async (data: SearchCriteria): Promise<void> => {
  resetPagination(state.params);
  state.params.filters = data.filters as any;
  await getList();
};

/**
 * Fetch operation log list with current parameters
 * Includes loading state management and error handling
 */
const getList = async (): Promise<void> => {
  // Prevent multiple simultaneous requests
  if (state.loading) {
    return;
  }

  state.loading = true;
  try {
    const [error, { data = { list: [], total: 0 } }] = await userLog.getOperationLogList(state.params);
    if (error) {
      return;
    }

    state.tableList = processOperationLogList(data.list);
    state.total = +data.total;
  } finally {
    state.loading = false;
  }
};

/**
 * Handle table pagination, sorting, and filtering changes
 */
const tableChange = async (_pagination: any, _filters: any, sorter: TableChangeParams['sorter']): Promise<void> => {
  updatePaginationParams(state.params, _pagination);
  updateSortingParams(state.params, sorter);
  await getList();
};

/**
 * Fetch system settings for operation log configuration
 * Retrieves log retention period settings
 */
const getSettings = async (): Promise<void> => {
  const [error, response] = await setting.getSettingDetail('OPERATION_LOG_CONFIG');
  if (error) {
    return;
  }

  const settings = extractSystemSettings(response);
  if (settings) {
    state.clearBeforeDay = settings.clearBeforeDay;
  }
};

/**
 * Toggle statistics display visibility
 */
const toggleOpenCount = (): void => {
  state.showCount = toggleStatisticsVisibility(state.showCount);
};

// Computed pagination object for table component
const pagination = computed<PaginationConfig>(() => {
  return createPaginationConfig(state.params.pageNo, state.params.pageSize, state.total);
});

// Create table columns using utility function
const columns = computed(() => createOperationLogColumns(t));


const dashboardConfig = {
  charts: [
      {
        type: ChartType.LINE,
        title: t('log.operation.statistics.newOperations'),
        field: 'opt_date'
      },
      {
        type: ChartType.PIE,
        title: [t('log.operation.statistics.resource')],
        field: ['resource'],
        enumKey: [
          enumUtils.enumToMessages(OperationResourceType),
        ],
        legendPosition: ['right'],
        legendGroupSize: [9]
      }
    ],
    layout: {
      cols: 2,
      gap: 16
    }
}

// Lifecycle hook - initialize component
onMounted(() => {
  getSettings();
  getList();
});
</script>

<template>
  <div class="flex flex-col min-h-full">
    <!-- System hint about log retention period -->
    <Hints
      :text="t('log.operation.messages.description', { days: state.clearBeforeDay })"
      class="mb-1" />

    <PureCard class="flex-1 p-3.5">
      <!-- Statistics component for operation logs -->
      <!-- <Statistics
        resource="OperationLog"
        :barTitle="t('log.operation.statistics.newOperations')"
        dateType="DAY"
        :router="GM"
        :visible="state.showCount" /> -->
      <Dashboard
        class="py-3"
        :config="dashboardConfig"
        :apiRouter="GM"
        resource="OperationLog"
        :dateType="DateRangeType.DAY"
        dataKey="opt_date"
        :showChartParam="true" />

      <!-- Search panel with filters and quick actions -->
      <SearchPanel
        :loading="state.loading"
        :showCount="state.showCount"
        @openCount="toggleOpenCount"
        @refresh="getList"
        @change="searchChange" />

      <!-- Data table with pagination and sorting -->
      <Table
        :loading="state.loading"
        :dataSource="state.tableList"
        :columns="columns"
        :pagination="pagination"
        rowKey="id"
        size="small"
        :noDataSize="'small'"
        :noDataText="t('common.messages.noData')"
        class="mt-2"
        @change="tableChange">
        <!-- Custom cell rendering for operator column -->
        <template #bodyCell="{ column, record }">
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
