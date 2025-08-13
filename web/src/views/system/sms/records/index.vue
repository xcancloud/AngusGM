<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { IconCount, IconRefresh, PureCard, SearchPanel, Table } from '@xcan-angus/vue-ui';
import { PageQuery, SearchCriteria, app, GM, ProcessStatus } from '@xcan-angus/infra';
import { Badge } from 'ant-design-vue';

import { sms } from '@/api';
import { SmsRecord } from './types';

const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));

const { t } = useI18n();

// SMS records data
const smsList = ref<SmsRecord[]>([]);

// UI state management
const showCount = ref(true);
const loading = ref(false);

// Query parameters for pagination and filtering
const params = ref<PageQuery>({ pageNo: 1, pageSize: 10, filters: [] });
const total = ref(0);

const pagination = computed(() => {
  return {
    current: params.value.pageNo,
    pageSize: params.value.pageSize,
    total: total.value
  };
});

/**
 * Load SMS records list from API
 */
const loadSmsRecordList = async function () {
  loading.value = true;
  try {
    const [error, { data = { list: [], total: 0 } }] = await sms.getSmsList(params.value);
    if (error) {
      return;
    }
    smsList.value = data.list;
    total.value = +data.total;
  } finally {
    loading.value = false;
  }
};

/**
 * Handle search criteria changes
 * @param data - Search criteria array
 */
const searchChange = (data: { key: string; value: string; op: SearchCriteria.OpEnum; }[]) => {
  params.value.pageNo = 1;
  params.value.filters = data;
  loadSmsRecordList();
};

/**
 * Handle table pagination, filtering, and sorting changes
 * @param _pagination - Pagination object
 * @param _filters - Filter criteria
 * @param sorter - Sorting information
 */
const tableChange = (_pagination: any, _filters: any, sorter: any) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  params.value.orderBy = sorter.orderBy;
  params.value.orderSort = sorter.orderSort;
  loadSmsRecordList();
};

/**
 * Handle refresh button click
 */
const handleRefresh = () => {
  if (loading.value) {
    return;
  }
  loadSmsRecordList();
};

onMounted(() => {
  loadSmsRecordList();
});

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: '10%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('sms.columns.sendStatus'),
    dataIndex: 'sendStatus',
    key: 'sendStatus',
    width: '8%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('sms.columns.sendUserId'),
    dataIndex: 'sendUserId',
    key: 'sendUserId',
    width: '10%',
    customRender: ({ text }): string => text && text !== '-1' ? text : '--',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('sms.columns.templateCode'),
    dataIndex: 'templateCode',
    key: 'templateCode',
    width: '20%',
    customRender: ({ text }): string => text || '--'
  },
  {
    title: t('sms.columns.urgent'),
    dataIndex: 'urgent',
    key: 'urgent',
    width: '7%',
    customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
  },
  {
    title: t('sms.columns.verificationCode'),
    dataIndex: 'verificationCode',
    key: 'verificationCode',
    width: '7%',
    customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
  },
  {
    title: t('sms.columns.batch'),
    dataIndex: 'batch',
    key: 'batch',
    width: '7%',
    customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
  },
  {
    title: t('sms.columns.actualSendDate'),
    dataIndex: 'actualSendDate',
    key: 'actualSendDate',
    sorter: true,
    width: '10%',
    customRender: ({ text }): string => text || '--'
  },
  {
    title: t('sms.columns.expectedSendDate'),
    dataIndex: 'expectedSendDate',
    key: 'expectedSendDate',
    sorter: true,
    width: '10%',
    customRender: ({ text }): string => text || '--'
  }
];

const searchOptions = ref([
  {
    valueKey: 'sendUserId',
    type: 'select-user' as const,
    allowClear: true,
    placeholder: '选择发送用户',
    axiosConfig: { headers: { 'XC-Opt-Tenant-Id': '' } }
  },
  {
    valueKey: 'sendStatus',
    type: 'select-enum' as const,
    enumKey: ProcessStatus,
    placeholder: t('sms.placeholder.selectSendStatus'),
    allowClear: true
  },
  {
    valueKey: 'templateCode',
    type: 'input' as const,
    placeholder: t('sms.placeholder.queryTemplateCode'),
    allowClear: true
  },
  {
    valueKey: 'urgent',
    type: 'select' as const,
    options: [{ value: true, label: t('common.status.yes') }, { value: false, label: t('common.status.no') }],
    placeholder: t('sms.placeholder.isUrgent'),
    allowClear: true
  },
  {
    valueKey: 'verificationCode',
    type: 'select' as const,
    options: [{ value: true, label: t('common.status.yes') }, { value: false, label: t('common.status.no') }],
    placeholder: t('sms.placeholder.isVerificationCode'),
    allowClear: true
  },
  {
    valueKey: 'batch',
    type: 'select' as const,
    options: [{ value: true, label: t('common.status.yes') }, { value: false, label: t('common.status.no') }],
    placeholder: t('sms.placeholder.isBatch'),
    allowClear: true
  },
  {
    valueKey: 'actualSendDate',
    type: 'date-range' as const,
    placeholder: [t('sms.placeholder.sendTimeRange'), t('sms.placeholder.sendTimeRange')],
    allowClear: true
  },
  {
    valueKey: 'expectedSendDate',
    type: 'date-range' as const,
    placeholder: [t('sms.placeholder.expectedTimeRange'), t('sms.placeholder.expectedTimeRange')],
    allowClear: true
  }
]);

/**
 * Get color for SMS send status badge
 * @param value - SMS send status
 * @returns Color string for badge styling
 */
const getSendStatusColor = (value: 'SUCCESS' | 'PENDING' | 'FAILURE') => {
  switch (value) {
    case 'SUCCESS': // Success
      return 'rgba(82,196,26,1)';
    case 'PENDING': // Pending
      return 'rgba(255,165,43,1)';
    case 'FAILURE': // Failure
      return 'rgba(245,34,45,1)';
    default:
      return 'rgba(128,128,128,1)'; // Default gray
  }
};
</script>
<template>
  <PureCard class="flex-1 p-3.5">
    <!-- SMS statistics component -->
    <Statistics
      resource="Sms"
      :router="GM"
      :barTitle="t('sms.titles.smsRecords')"
      :visible="showCount" />

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
        <IconCount v-model:value="showCount" class="mr-2" />
        <!-- Refresh button -->
        <IconRefresh
          :loading="loading"
          @click="handleRefresh" />
      </div>
    </div>

    <!-- SMS records table -->
    <Table
      :loading="loading"
      :dataSource="smsList"
      :pagination="pagination"
      :columns="columns"
      rowKey="id"
      size="small"
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
