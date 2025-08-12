<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { IconCount, IconRefresh, PureCard, SearchPanel, Table } from '@xcan-angus/vue-ui';
import { PageQuery, SearchCriteria, app, GM, ProcessStatus } from '@xcan-angus/infra';
import { Badge } from 'ant-design-vue';

import { email } from '@/api';
import { EmailRecord, EmailSendStatus } from './PropsType';

// Lazy load Statistics component for better performance
const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));

const { t } = useI18n();

// Reactive state management
const emailList = ref<EmailRecord[]>([]);
const showCount = ref(true);
const loading = ref(false);
const total = ref(0);

// Pagination and search parameters
const params = ref<PageQuery>({
  pageNo: 1,
  pageSize: 10,
  filters: []
});

// Computed pagination object for table component
const pagination = computed(() => ({
  current: params.value.pageNo,
  pageSize: params.value.pageSize,
  total: total.value
}));

// Table column definitions with optimized rendering
const columns = [
  {
    title: t('email.columns.id'),
    dataIndex: 'id',
    key: 'id',
    width: '9%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('email.columns.subject'),
    dataIndex: 'subject',
    key: 'subject',
    width: '15%'
  },
  {
    title: t('email.columns.sendStatus'),
    dataIndex: 'sendStatus',
    key: 'sendStatus',
    width: '7%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('email.columns.sendUserId'),
    dataIndex: 'sendId',
    key: 'sendId',
    width: '9%',
    customRender: ({ text }): string => text && text !== '-1' ? text : '--',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('email.columns.templateCode'),
    dataIndex: 'templateCode',
    key: 'templateCode',
    width: '15%',
    customRender: ({ text }): string => text || '--'
  },
  {
    title: t('email.columns.urgent'),
    dataIndex: 'urgent',
    key: 'urgent',
    width: '6%',
    customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
  },
  {
    title: t('email.columns.verificationCode'),
    dataIndex: 'verificationCode',
    key: 'verificationCode',
    width: '6%',
    customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
  },
  {
    title: t('email.columns.batch'),
    dataIndex: 'batch',
    key: 'batch',
    width: '6%',
    customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
  },
  {
    title: t('email.columns.sendTime'),
    dataIndex: 'actualSendDate',
    key: 'actualSendDate',
    sorter: true,
    width: '9%',
    customRender: ({ text }): string => text || '--'
  },
  {
    title: t('email.columns.expectedTime'),
    dataIndex: 'expectedSendDate',
    key: 'expectedSendDate',
    sorter: true,
    width: '9%',
    customRender: ({ text }): string => text || '--'
  }
];

// Status color mapping for better maintainability
const STATUS_COLORS: Record<EmailSendStatus, string> = {
  SUCCESS: 'rgba(82,196,26,1)', // Green for success
  PENDING: 'rgba(255,165,43,1)', // Orange for pending
  FAILURE: 'rgba(245,34,45,1)' // Red for failure
};

/**
 * Load email records from API with error handling
 */
const loadEmailList = async (): Promise<void> => {
  if (loading.value) return; // Prevent duplicate requests

  loading.value = true;
  try {
    const [error, { data = { list: [], total: 0 } }] = await email.getEmailList(params.value);

    if (error) {
      console.error('Failed to load email list:', error);
      return;
    }

    emailList.value = data.list;
    total.value = +data.total;
  } catch (err) {
    console.error('Unexpected error loading email list:', err);
  } finally {
    loading.value = false;
  }
};

/**
 * Handle search criteria changes and reset pagination
 */
const searchChange = (data: { key: string; value: string; op: SearchCriteria.OpEnum; }[]): void => {
  params.value.pageNo = 1; // Reset to first page on new search
  params.value.filters = data;
  loadEmailList();
};

/**
 * Handle table pagination, sorting, and filtering changes
 */
const tableChange = (_pagination: any, _filters: any, sorter: any): void => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  params.value.orderBy = sorter.orderBy;
  params.value.orderSort = sorter.orderSort;
  loadEmailList();
};

/**
 * Refresh email list data
 */
const handleRefresh = (): void => {
  if (loading.value) return;
  loadEmailList();
};

/**
 * Get status color based on send status value
 */
const getSendStatusColor = (value: EmailSendStatus): string => {
  return STATUS_COLORS[value] || STATUS_COLORS.FAILURE;
};

// Search options configuration for the search panel
const searchOptions = ref([
  {
    valueKey: 'sendUserId',
    type: 'select-user' as const,
    allowClear: true,
    placeholder: t('email.placeholder.selectSendUser'),
    axiosConfig: { headers: { 'XC-Opt-Tenant-Id': '' } }
  },
  {
    valueKey: 'sendStatus',
    type: 'select-enum' as const,
    enumKey: ProcessStatus,
    placeholder: t('email.placeholder.selectSendStatus'),
    allowClear: true
  },
  {
    valueKey: 'templateCode',
    type: 'input' as const,
    placeholder: t('email.placeholder.queryTemplateCode'),
    allowClear: true
  },
  {
    valueKey: 'urgent',
    type: 'select' as const,
    options: [
      { value: true, label: t('common.status.yes') },
      { value: false, label: t('common.status.no') }
    ],
    placeholder: t('email.placeholder.isUrgent'),
    allowClear: true
  },
  {
    valueKey: 'verificationCode',
    type: 'select' as const,
    options: [
      { value: true, label: t('common.status.yes') },
      { value: false, label: t('common.status.no') }
    ],
    placeholder: t('email.placeholder.isVerificationCode'),
    allowClear: true
  },
  {
    valueKey: 'batch',
    type: 'select' as const,
    options: [
      { value: true, label: t('common.status.yes') },
      { value: false, label: t('common.status.no') }
    ],
    placeholder: t('email.placeholder.isBatch'),
    allowClear: true
  },
  {
    valueKey: 'html',
    type: 'select' as const,
    options: [
      { value: true, label: t('common.status.yes') },
      { value: false, label: t('common.status.no') }
    ],
    placeholder: t('email.placeholder.isHtml'),
    allowClear: true
  },
  {
    valueKey: 'actualSendDate',
    type: 'date-range' as const,
    placeholder: [
      t('email.placeholder.sendTimeRange'),
      t('email.placeholder.sendTimeRange')
    ],
    allowClear: true
  },
  {
    valueKey: 'expectedSendDate',
    type: 'date-range' as const,
    placeholder: [
      t('email.placeholder.expectedTimeRange'),
      t('email.placeholder.expectedTimeRange')
    ],
    allowClear: true
  }
]);

// Initialize data on component mount
onMounted(() => {
  loadEmailList();
});
</script>

<template>
  <PureCard class="flex-1 p-3.5">
    <!-- Statistics component for email metrics -->
    <Statistics
      resource="Email"
      :barTitle="t('statistics.metrics.newEmails')"
      :router="GM"
      :visible="showCount" />

    <!-- Search and control panel -->
    <div class="flex items-start mb-2">
      <SearchPanel
        :options="searchOptions"
        class="flex-1 mr-2"
        @change="searchChange" />
      <div class="flex items-center flex-none h-7">
        <IconCount v-model:value="showCount" class="mr-2" />
        <IconRefresh
          :loading="loading"
          @click="handleRefresh" />
      </div>
    </div>

    <!-- Email records table -->
    <Table
      :loading="loading"
      :dataSource="emailList"
      :pagination="pagination"
      :columns="columns"
      rowKey="id"
      size="small"
      @change="tableChange">
      <template #bodyCell="{ column, text, record }">
        <!-- ID column with detail link -->
        <template v-if="column.dataIndex === 'id'">
          <RouterLink
            v-if="app.has('MailSendRecordsDetail')"
            :to="`/system/email/records/${text}`"
            class="text-theme-special text-theme-text-hover">
            {{ text }}
          </RouterLink>
          <template v-else>
            {{ text }}
          </template>
        </template>

        <!-- Send status column with colored badge -->
        <template v-if="column.dataIndex === 'sendStatus'">
          <Badge
            :color="getSendStatusColor(text?.value)"
            :text="text?.message" />
        </template>
      </template>
    </Table>
  </PureCard>
</template>
