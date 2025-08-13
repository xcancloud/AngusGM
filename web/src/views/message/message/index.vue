<script setup lang="ts">
import { reactive, onMounted, computed, ref, defineAsyncComponent } from 'vue';
import { SearchPanel, Table, PureCard, IconCount, IconRefresh, ButtonAuth } from '@xcan-angus/vue-ui';
import { app, GM, ReceiveObjectType } from '@xcan-angus/infra';
import { MessageReceiveType, MessageStatus } from '@/enums/enums';
import { useI18n } from 'vue-i18n';
import { Badge } from 'ant-design-vue';

import { message } from '@/api';
import type { TableColumnType } from './types';

/**
 * Async component import for Statistics
 * Lazy loading to improve initial page load performance
 */
const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));

const { t } = useI18n();

// Search and filter state management
let searchForm: any = [];
const sortForm: Record<string, any> = {};
const receiveObjectType = ref();
const disabled = ref(false);
const showCount = ref(true);

/**
 * Component state management
 * Centralized reactive state for managing component behavior and data
 */
const state = reactive<{
  loading: boolean,
  columns: Array<TableColumnType>,
  dataSource: never[]
}>({
  loading: false,
  columns: [],
  dataSource: []
});

/**
 * Pagination configuration
 * Manages table pagination state including current page, page size, and total count
 */
const pagination = reactive({
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

  const params: Record<string, any> = {
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
const searchTable = async (searchParams: any[]) => {
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
const listChange = async (page: Record<string, any>, _filters: unknown, sorter: Record<string, any>) => {
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
 * Status color mapping for message status badges
 * Maps status values to appropriate badge colors for visual feedback
 */
const getStatusColor: {
  [key: string]: string
} = {
  PENDING: 'warning',
  SENT: 'success',
  FAILURE: 'error'
};

/**
 * Get status color for a given status key
 * Returns the appropriate color for status badge display
 */
const getStatusText = (key: string): string => {
  return getStatusColor[key];
};

/**
 * Search panel configuration options
 * Defines available search fields and their types for the search panel
 */
const searchOptions = computed(() => {
  return [
    {
      valueKey: 'title',
      allowClear: true,
      type: 'input' as const,
      placeholder: t('messages.placeholder.title')
    },
    {
      valueKey: 'receiveType',
      type: 'select-enum' as const,
      enumKey: MessageReceiveType,
      allowClear: true,
      placeholder: t('messages.placeholder.receiveType')
    },
    {
      valueKey: 'status',
      type: 'select-enum' as const,
      enumKey: MessageStatus,
      allowClear: true,
      placeholder: t('messages.placeholder.status')
    },
    {
      valueKey: 'receiveObjectType',
      type: 'select-enum' as const,
      enumKey: ReceiveObjectType,
      allowClear: true,
      placeholder: t('messages.placeholder.receiveObjectType')
    }
  ];
});

/**
 * Table column configuration
 * Defines the structure and display properties for the message table columns
 */
const columns = [
  {
    title: t('messages.columns.title'),
    key: 'title',
    dataIndex: 'title',
    width: '17%'
  },
  {
    title: t('messages.columns.receiveType'),
    key: 'receiveType',
    dataIndex: 'receiveType',
    width: '8%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('messages.columns.status'),
    key: 'status',
    dataIndex: 'status',
    width: '8%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('messages.columns.failureReason'),
    key: 'failureReason',
    dataIndex: 'failureReason',
    width: '17%'
  },
  {
    title: t('messages.columns.createdByName'),
    key: 'createdByName',
    dataIndex: 'createdByName',
    width: '8%'
  },
  {
    title: t('messages.columns.receiveObjectType'),
    key: 'receiveObjectType',
    dataIndex: 'receiveObjectType',
    width: '8%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('messages.columns.sentNum'),
    dataIndex: 'sentNum',
    key: 'sentNum',
    width: '8%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('messages.columns.readNum'),
    key: 'readNum',
    dataIndex: 'readNum',
    width: '8%'
  },
  {
    title: t('messages.columns.timingDate'),
    key: 'timingDate',
    dataIndex: 'timingDate',
    width: '10%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  }
];

/**
 * Initialize component on mount
 * Loads initial message data when component is mounted
 */
onMounted(() => {
  getMessageList();
});
</script>

<template>
  <PureCard class="min-h-full p-3.5">
    <!-- Statistics Section -->
    <Statistics
      resource="Message"
      :barTitle="t('statistics.metrics.newMessages')"
      :router="GM"
      dateType="YEAR"
      :visible="showCount" />

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
