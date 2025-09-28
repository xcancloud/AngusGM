<script setup lang="ts">
import { onMounted, reactive, ref, defineAsyncComponent } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import { modal, Table, PureCard, SearchPanel, notification, Hints, IconCount, IconRefresh, ButtonAuth } from '@xcan-angus/vue-ui';
import { app, GM, enumUtils } from '@xcan-angus/infra';

import { Tooltip } from 'ant-design-vue';

import { notice } from '@/api';
import type { NoticeDataType, PaginationType, SearchParamsType } from './types';
import { getQueryParams, getSearchOptions, getTableColumns } from './utils';
import { ChartType, DateRangeType } from '@/components/dashboard/enums';
import { SentType, NoticeScope } from '@/enums/enums';

// Lazy load Statistics component for better performance
const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));
const Dashboard = defineAsyncComponent(() => import('@/components/dashboard/Dashboard.vue'));

const router = useRouter();
const { t } = useI18n();

// Pagination configuration for notice list
const pagination = reactive<PaginationType>({
  pageSize: 10,
  current: 1,
  total: 0
});

// UI state management
const showCount = ref(true); // Control statistics display
const searchParams = ref<SearchParamsType[]>([]); // Search and filter parameters
const loading = ref(false); // Loading state for API calls
const disabled = ref(false); // Disable refresh button during operations
const noticeData = ref<NoticeDataType[]>([]); // Notice data list

/**
 * Fetch notice list from API
 * Handles loading states and updates pagination total
 */
const getNoticeList = async () => {
  if (loading.value) {
    return; // Prevent multiple simultaneous requests
  }
  const params = getQueryParams(pagination, searchParams.value);
  loading.value = true;
  const [error, res] = await notice.getNoticeList(params);
  loading.value = false;
  if (error) {
    return;
  }

  pagination.total = Number(res.data.total || 0);
  noticeData.value = res.data?.list || [];
};

/**
 * Handle pagination changes
 * Updates page size and current page, then refetches data
 */
const changePage = async (page) => {
  const { pageSize, current } = page;
  pagination.pageSize = pageSize;
  pagination.current = current;
  disabled.value = true;
  await getNoticeList();
  disabled.value = false;
};

/**
 * Handle search panel changes
 * Updates search parameters and resets to first page
 */
const changeSearchPanel = async (value) => {
  searchParams.value = value;
  pagination.current = 1; // Reset to first page when search changes
  disabled.value = true;
  await getNoticeList();
  disabled.value = false;
};

/**
 * Navigate to send notice page
 */
const toSendNotice = () => {
  router.push('/messages/notification/send');
};

/**
 * Delete notice with confirmation dialog
 * Shows confirmation modal and handles deletion process
 */
const deleteNotice = (item: NoticeDataType) => {
  modal.confirm({
    centered: true,
    title: t('common.messages.delete'),
    content: t('notification.messages.confirmDelete'),
    async onOk () {
      const [error] = await notice.deleteNotice([item.id as string]);
      if (error) {
        notification.error(error.message);
      }
      notification.success('common.messages.deleteSuccess');

      // Adjust pagination if current page becomes empty
      if (pagination.current > 1 && noticeData.value.length === 1) {
        pagination.current = pagination.current - 1;
      }
      disabled.value = true;
      await getNoticeList();
      disabled.value = false;
    }
  });
};

// Get search options and table columns from utility functions
const searchOptions = getSearchOptions(t);
const columns = getTableColumns(t);


const dashboardConfig = {
  charts: [
      {
        type: ChartType.LINE,
        title: t('statistics.metrics.newNotification'),
        field: 'created_date'
      },
      {
        type: ChartType.PIE,
        title: [t('statistics.metrics.sendScope'), t('statistics.metrics.sendType')],
        field: ['scope', 'send_type'],
        enumKey: [
          enumUtils.enumToMessages(NoticeScope),

          enumUtils.enumToMessages(SentType),

        ],
        legendPosition: ['right', 'right']
      }
    ],
    layout: {
      cols: 2,
      gap: 16
    }
}

// Initialize data on component mount
onMounted(() => {
  getNoticeList();
});

</script>
<template>
  <div class="flex flex-col min-h-full">
    <!-- Global tip for notice management -->
    <Hints :text="t('notification.globalTip')" class="mb-1" />
    <PureCard class="p-3.5 flex-1">
      <!-- Statistics component showing notice metrics -->
      <!-- <Statistics
        resource="Notice"
        :barTitle="t('statistics.metrics.newNotification')"
        :router="GM"
        dateType="YEAR"
        :visible="showCount" /> -->
      <Dashboard
        class="py-3"
        :config="dashboardConfig"
        :apiRouter="GM"
        resource="Notice"
        :dateType="DateRangeType.YEAR"
        :showChartParam="true"
        pieItemClass="!w-120" />

      <!-- Search panel and action buttons -->
      <div class="flex items-start my-2 justify-between">
        <SearchPanel
          :options="searchOptions"
          class="flex-1"
          @change="changeSearchPanel" />
        <div class="flex items-center space-x-2">
          <!-- Send notice button with permission check -->
          <ButtonAuth
            code="NoticePublish"
            type="primary"
            icon="icon-fabu"
            @click="toSendNotice" />
          <!-- Toggle statistics visibility -->
          <IconCount v-model:value="showCount" />
          <!-- Refresh button -->
          <IconRefresh
            :loading="loading"
            :disabled="disabled"
            @click="getNoticeList" />
        </div>
      </div>

      <!-- Notice data table -->
      <Table
        :columns="columns"
        :loading="loading"
        :dataSource="noticeData"
        :pagination="pagination"
        rowKey="id"
        size="small"
        @change="changePage">
        <template #bodyCell="{record, column, text}">
          <!-- Content column with tooltip for long text -->
          <template v-if="column.dataIndex === 'content'">
            <Tooltip :title="text" placement="topLeft">
              <div class="truncate">{{ text }}</div>
            </Tooltip>
          </template>

          <!-- Action column with delete button -->
          <template v-if="column.key === 'action'">
            <ButtonAuth
              code="NoticeDelete"
              type="text"
              icon="icon-lajitong"
              @click="deleteNotice(record)" />
          </template>

          <!-- Send type column showing enum message -->
          <template v-if="column.key === 'sendType'">
            {{ record.sendType.message }}
          </template>

          <!-- Scope column showing enum message -->
          <template v-if="column.key === 'scope'">
            {{ record.scope.message }}
          </template>

          <!-- ID column with link to detail page -->
          <template v-if="column.key === 'id'">
            <RouterLink
              v-if="app.has('NoticeDetail')"
              :to="`/messages/notification/${record.id}`"
              class="text-theme-special text-theme-text-hover whitespace-nowrap">
              {{ record.id }}
            </RouterLink>
          </template>

          <!-- Timing date column showing date or placeholder -->
          <template v-if="column.key==='timingDate'">
            {{ record.sendType?.value === SentType.TIMING_SEND ? record.timingDate : '--' }}
          </template>
        </template>
      </Table>
    </PureCard>
  </div>
</template>
<style scoped>
/* Custom form control styling */
.ant-form-horizontal :deep(.ant-form-item-control) {
  flex: 1 1 50%;
  max-width: 600px;
}
</style>
