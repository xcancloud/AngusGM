<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { IconCount, IconRefresh, PureCard, SearchPanel, Table } from '@xcan-angus/vue-ui';
import { app, GM } from '@xcan-angus/infra';
import { Badge } from 'ant-design-vue';

import { email } from '@/api';
import { EmailRecordsState, SearchChangeParams } from './types';
import {
  createTableColumns, createSearchOptions, createStatusColorMapping, createInitialEmailRecordsState,
  createPaginationObject, updatePaginationParams, resetPagination, updateSearchFilters,
  getSendStatusColor, processEmailListResponse, canViewEmailDetail
} from './utils';

// Lazy load Statistics component for better performance
const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));

const { t } = useI18n();

// Component state management
const state = reactive<EmailRecordsState>(createInitialEmailRecordsState());

// Table columns configuration
const columns = ref(createTableColumns(t));

// Search options configuration
const searchOptions = ref(createSearchOptions(t));

// Status color mapping
const statusColors = ref(createStatusColorMapping());

// Computed pagination object for table component
const pagination = computed(() =>
  createPaginationObject(state.params, state.total)
);

/**
 * Load email records from API with error handling
 */
const loadEmailList = async (): Promise<void> => {
  if (state.loading) return; // Prevent duplicate requests

  try {
    state.loading = true;
    const [error, response] = await email.getEmailList(state.params);

    if (error) {
      console.error('Failed to load email list:', error);
      return;
    }

    const { list, total } = processEmailListResponse(response);
    state.emailList = list;
    state.total = total;
  } catch (err) {
    console.error('Unexpected error loading email list:', err);
  } finally {
    state.loading = false;
  }
};

/**
 * Handle search criteria changes and reset pagination
 */
const searchChange = (filters: SearchChangeParams[]): void => {
  resetPagination(state.params); // Reset to first page on new search
  updateSearchFilters(state.params, filters);
  loadEmailList();
};

/**
 * Handle table pagination, sorting, and filtering changes
 */
const tableChange = (pagination: any, sorter: any): void => {
  updatePaginationParams(state.params, pagination, sorter);
  loadEmailList();
};

/**
 * Refresh email list data
 */
const handleRefresh = (): void => {
  if (state.loading) return;
  loadEmailList();
};

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
      :visible="state.showCount" />

    <!-- Search and control panel -->
    <div class="flex items-start mb-2">
      <SearchPanel
        :options="searchOptions"
        class="flex-1 mr-2"
        @change="searchChange" />
      <div class="flex items-center flex-none h-7">
        <IconCount v-model:value="state.showCount" class="mr-2" />
        <IconRefresh
          :loading="state.loading"
          @click="handleRefresh" />
      </div>
    </div>

    <!-- Email records table -->
    <Table
      :loading="state.loading"
      :dataSource="state.emailList"
      :pagination="pagination"
      :columns="columns"
      rowKey="id"
      size="small"
      @change="tableChange">
      <template #bodyCell="{ column, text, record }">
        <!-- ID column with detail link -->
        <template v-if="column.dataIndex === 'id'">
          <RouterLink
            v-if="app.has('MailSendRecordsDetail') && canViewEmailDetail(record)"
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
            :color="getSendStatusColor(text?.value, statusColors)"
            :text="text?.message" />
        </template>
      </template>
    </Table>
  </PureCard>
</template>
