<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, reactive } from 'vue';
import { useI18n } from 'vue-i18n';
import { IconCount, IconRefresh, PureCard, SearchPanel, Table } from '@xcan-angus/vue-ui';
import { app, GM, ProcessStatus } from '@xcan-angus/infra';
import { Badge } from 'ant-design-vue';

import { sms } from '@/api';
import { RecordsState, SearchCriteria as SearchCriteriaType } from './types';
import {
  createSmsRecordsColumns, createSmsRecordsSearchOptions, getSendStatusColor, createPaginationConfig
} from './utils';

const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));

const { t } = useI18n();

// Reactive state management
const state = reactive<RecordsState>({
  smsList: [],
  showCount: true,
  loading: false,
  params: { pageNo: 1, pageSize: 10, filters: [] },
  total: 0
});

// Computed pagination configuration
const pagination = computed(() => {
  return createPaginationConfig(state.params.pageNo, state.params.pageSize, state.total);
});

/**
 * Load SMS records list from API
 */
const loadSmsRecordList = async (): Promise<void> => {
  state.loading = true;
  try {
    const [error, { data = { list: [], total: 0 } }] = await sms.getSmsList(state.params);
    if (error) {
      return;
    }
    state.smsList = data.list;
    state.total = +data.total;
  } finally {
    state.loading = false;
  }
};

/**
 * Handle search criteria changes
 * @param data - Search criteria array
 */
const searchChange = (data: SearchCriteriaType[]): void => {
  state.params.pageNo = 1;
  state.params.filters = data;
  loadSmsRecordList();
};

/**
 * Handle table pagination, filtering, and sorting changes
 * @param _pagination - Pagination object
 * @param _filters - Filter criteria
 * @param sorter - Sorting information
 */
const tableChange = (_pagination: any, _filters: any, sorter: any): void => {
  const { current, pageSize } = _pagination;
  state.params.pageNo = current;
  state.params.pageSize = pageSize;
  state.params.orderBy = sorter.orderBy;
  state.params.orderSort = sorter.orderSort;
  loadSmsRecordList();
};

/**
 * Handle refresh button click
 */
const handleRefresh = (): void => {
  if (state.loading) {
    return;
  }
  loadSmsRecordList();
};

// Create table columns using utility function
const columns = createSmsRecordsColumns(t);

// Create search options using utility function
const searchOptions = createSmsRecordsSearchOptions(t, ProcessStatus);

// Lifecycle hook - initialize component on mount
onMounted(() => {
  loadSmsRecordList();
});
</script>

<template>
  <PureCard class="flex-1 p-3.5">
    <!-- SMS statistics component -->
    <Statistics
      resource="Sms"
      :router="GM"
      :barTitle="t('sms.titles.smsRecords')"
      :visible="state.showCount" />

    <!-- Search and filter controls -->
    <div class="flex items-start mb-2">
      <!-- Search panel for filtering records -->
      <SearchPanel
        :options="searchOptions"
        class="flex-1 mr-2"
        @change="searchChange" />

      <!-- Control buttons -->
      <div class="flex items-center flex-none h-7">
        <!-- Toggle statistics visibility -->
        <IconCount v-model:value="state.showCount" class="mr-2" />
        <!-- Refresh button -->
        <IconRefresh
          :loading="state.loading"
          @click="handleRefresh" />
      </div>
    </div>

    <!-- SMS records table -->
    <Table
      :loading="state.loading"
      :dataSource="state.smsList"
      :pagination="pagination"
      :columns="columns"
      rowKey="id"
      size="small"
      :noDataSize="'small'"
      :noDataText="t('common.noData')"
      @change="tableChange">
      <!-- Custom cell rendering -->
      <template #bodyCell="{column,text}">
        <!-- ID column with optional link to detail -->
        <template v-if="column.dataIndex === 'id'">
          <RouterLink
            v-if="app.has('SMSSendRecordsDetail')"
            :to="`/system/sms/records/${text}`"
            class="text-theme-special text-theme-text-hover">
            {{ text }}
          </RouterLink>
          <template v-else>
            {{ text }}
          </template>
        </template>

        <!-- Send status column with colored badge -->
        <template v-if="column.dataIndex === 'sendStatus'">
          <Badge :color="getSendStatusColor(text?.value)" :text="text?.message" />
        </template>
      </template>
    </Table>
  </PureCard>
</template>
