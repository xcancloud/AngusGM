<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, reactive, ref } from 'vue';
import { ButtonAuth, IconCount, IconRefresh, PureCard, SearchPanel, Table } from '@xcan-angus/vue-ui';
import { app, GM, enumUtils } from '@xcan-angus/infra';
import { useI18n } from 'vue-i18n';
import { Badge } from 'ant-design-vue';
import { message } from '@/api';
import type {
  MessageComponentState, MessageSearchParams, PaginationConfig, SearchParams, SortConfig, TableChangeParams
} from './types';
import { createSearchOptions, createTableColumns, getStatusText } from './utils';

import { ChartType, DateRangeType } from '@/components/Dashboard/enums';
import { SentType, NoticeScope, MessageReceiveType, MessageStatus } from '@/enums/enums';

/**
 * Async component import for Statistics
 * Lazy loading to improve initial page load performance
 */
const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));
const Dashboard = defineAsyncComponent(() => import('@/components/Dashboard/Dashboard.vue'));

const { t } = useI18n();

// Search and filter state management
let searchForm: SearchParams[] = [];
const sortForm: SortConfig = {};
const receiveObjectType = ref();
const disabled = ref(false);
const showCount = ref(true);

/**
 * Component state management
 * Centralized reactive state for managing component behavior and data
 */
const state = reactive<MessageComponentState>({
  loading: false,
  columns: [],
  dataSource: []
});

/**
 * Pagination configuration
 * Manages table pagination state including current page, page size, and total count
 */
const pagination = reactive<PaginationConfig>({
  current: 1,
  pageSize: 10,
  total: 0
});

/**
 * Fetch message list from API
 * Retrieves paginated message data based on current search and sort parameters
 */
const getMessageList = async () => {
  if (state.loading) {
    return;
  }

  const params: MessageSearchParams = {
    filters: searchForm,
    pageNo: pagination.current,
    pageSize: pagination.pageSize,
    fullTextSearch: true
  };

  state.loading = true;

  try {
    const [error, res] = await message.searchMessageList(params);
    if (error) {
      return;
    }

    pagination.total = Number(res.data.total || 0);
    state.dataSource = res.data.list;
  } finally {
    state.loading = false;
  }
};

/**
 * Handle search table operations
 * Processes search parameters and triggers data refresh
 */
const searchTable = async (searchParams: SearchParams[]) => {
  receiveObjectType.value = searchParams.find(i => i.key === 'receiveObjectType')?.value || undefined;
  searchForm = searchParams.filter(i => i.key !== 'sendTenantId');
  pagination.current = 1;
  disabled.value = true;

  try {
    await getMessageList();
  } finally {
    disabled.value = false;
  }
};

/**
 * Handle table pagination and sorting changes
 * Updates pagination state and refreshes data when user changes page or sorts
 */
const listChange = async (page: TableChangeParams, _filters: unknown, sorter: Record<string, any>) => {
  if (sorter.orderBy) {
    sortForm.orderBy = sorter.orderBy;
    sortForm.orderSort = sorter.orderSort;
  }

  pagination.current = page.current;
  pagination.pageSize = page.pageSize;
  disabled.value = true;

  try {
    await getMessageList();
  } finally {
    disabled.value = false;
  }
};

/**
 * Search panel configuration options
 * Defines available search fields and their types for the search panel
 */
const searchOptions = computed(() => createSearchOptions());

/**
 * Table column configuration
 * Defines the structure and display properties for the message table columns
 */
const columns = computed(() => createTableColumns());

/**
 * Initialize component on mount
 * Loads initial message data when component is mounted
 */

const dashboardConfig = {
  charts: [
    {
      type: ChartType.LINE,
      title: t('statistics.metrics.newMessages'),
      field: 'created_date'
    },
    {
      type: ChartType.PIE,
      title: [t('statistics.metrics.receiveType'), t('statistics.metrics.sendStatus')],
      field: ['receive_type', 'status'],
      enumKey: [
        enumUtils.enumToMessages(MessageReceiveType),

        enumUtils.enumToMessages(MessageStatus)

      ],
      legendPosition: ['right', 'right']
    }
  ],
  layout: {
    cols: 2,
    gap: 16
  }
};

onMounted(() => {
  getMessageList();
});
</script>

<template>
  <PureCard class="min-h-full p-3.5">
    <!-- Statistics Section -->
    <Dashboard
      v-show="showCount"
      class="py-3"
      :config="dashboardConfig"
      :apiRouter="GM"
      resource="Message"
      :dateType="DateRangeType.YEAR"
      :showChartParam="true" />

    <!-- Search and Action Bar -->
    <div class="flex items-center justify-between my-2">
      <SearchPanel
        :options="searchOptions"
        class="flex-1"
        @change="searchTable" />
      <div class="flex items-center space-x-2">
        <ButtonAuth
          code="MessageSend"
          type="primary"
          icon="icon-tuisongtongzhi"
          href="/messages/message/send" />
        <IconCount v-model:value="showCount" />
        <IconRefresh
          :loading="state.loading"
          :disabled="disabled"
          @click="getMessageList" />
      </div>
    </div>

    <!-- Message Table -->
    <Table
      :columns="columns"
      :pagination="pagination"
      :dataSource="state.dataSource"
      :loading="state.loading"
      rowKey="id"
      size="small"
      noDataSize="small"
      noDataText="No data"
      @change="listChange">
      <template #bodyCell="{ column, record }">
        <!-- Message Title with Link -->
        <template v-if="column.dataIndex === 'title'">
          <RouterLink v-if="app.has('MessageDetail')" :to="`/messages/message/${record.id}`">
            <a
              :title="record.title"
              href="javascript:;"
              class="text-theme-special text-theme-text-hover">{{ record.title }}</a>
          </RouterLink>
        </template>

        <!-- Receive Type Display -->
        <template v-if="column.key === 'receiveType'">{{ record.receiveType.message }}</template>

        <!-- Status Badge -->
        <template v-if="column.key === 'status'">
          <Badge :status="getStatusText(record.status.value)" :text="record.status.message" />
        </template>

        <!-- Receive Object Type -->
        <template v-if="column.key === 'receiveObjectType'">
          {{ record.receiveObjectType?.message }}
        </template>

        <!-- Receive Tenant Name -->
        <template v-if="column.dataIndex === 'receiveTenantName'">
          {{ record.receiveTenantName || t('messages.allUsers') }}
        </template>

        <!-- Sent Number -->
        <template v-if="column.key === 'sentNum'">
          {{ +record.sentNum > -1 ? record.sentNum : '--' }}
        </template>
      </template>
    </Table>
  </PureCard>
</template>
