<script setup lang='ts'>
import { computed, defineAsyncComponent, onMounted, reactive } from 'vue';
import { useI18n } from 'vue-i18n';
import { AsyncComponent, IconRefresh, PureCard, Select, Table } from '@xcan-angus/vue-ui';
import { app, appContext } from '@xcan-angus/infra';

import { Quota, QuotaState, EditionType, PaginationConfig } from '../types';
import {
  getQuotaTableColumns, createPaginationConfig, processAppListForOptions, createQuotaQueryParams,
  updatePaginationParams, formatBooleanStatus, getQuotaNameDisplay, getQuotaNameMessage
} from '../utils';
import { setting } from '@/api';

// Lazy load edit quota component for better performance
const EditQuota = defineAsyncComponent(() => import('@/views/system/quota/edit/index.vue'));

const { t } = useI18n();

// Reactive state management
const state = reactive<QuotaState>({
  editionType: '',
  appCode: undefined,
  loading: false,
  total: 0,
  params: { pageNo: 1, pageSize: 10 },
  tableList: [],
  disabled: false,
  visible: false,
  currQuota: undefined,
  options: []
});

/**
 * Initialize component data
 */
const init = async (): Promise<void> => {
  state.editionType = appContext.getEditionType();
  await loadList();
};

/**
 * Load quota list from API
 */
const loadList = async (): Promise<void> => {
  if (state.loading) {
    return;
  }

  state.loading = true;
  const queryParams = createQuotaQueryParams(state.params, state.appCode);
  const [error, { data = { list: [], total: 0 } }] = await setting.getTenantQuotaList(queryParams);
  state.loading = false;

  if (error) {
    return;
  }

  state.tableList = data.list;
  state.total = +data.total;
};

/**
 * Handle pagination changes
 * @param _pagination - Pagination object from table
 */
const changePagination = async (_pagination: any): Promise<void> => {
  updatePaginationParams(state.params, _pagination);
  state.disabled = true;
  await loadList();
  state.disabled = false;
};

/**
 * Handle app selection change
 */
const appChange = async (): Promise<void> => {
  state.disabled = true;
  await loadList();
  state.disabled = false;
};

/**
 * Open edit quota modal
 * @param rowData - Quota record to edit
 */
const openEditModal = (rowData: Quota): void => {
  state.currQuota = rowData;
  state.visible = true;
};

/**
 * Handle edit success
 */
const editSuccess = async (): Promise<void> => {
  state.disabled = true;
  await loadList();
  state.disabled = false;
};

/**
 * Load application list for select options
 */
const loadAppList = async (): Promise<void> => {
  const [, { data = [] }] = await setting.getTenantQuotaApp();
  state.options = processAppListForOptions(data);
};

// Computed pagination configuration
const pagination = computed<PaginationConfig>(() => {
  return createPaginationConfig(state.params.pageNo, state.params.pageSize, state.total);
});

// Create table columns using utility function
const columns = computed(() => {
  return getQuotaTableColumns(state.editionType as EditionType, t);
});

// Lifecycle hooks
onMounted(() => {
  init();
  loadAppList();
});
</script>

<template>
  <PureCard class="flex-1 p-3.5">
    <!-- Search and filter controls -->
    <div class="mb-2 flex justify-between items-center">
      <!-- App selection dropdown -->
      <Select
        v-model:value="state.appCode"
        showSearch
        allowClear
        :placeholder="t('quota.placeholder.selectApp')"
        :options="state.options"
        internal
        size="small"
        class="w-60"
        @change="appChange" />

      <!-- Refresh button -->
      <IconRefresh
        :loading="state.loading"
        :disabled="state.disabled"
        class="ml-2"
        @click="loadList" />
    </div>

    <!-- Quota table -->
    <Table
      :loading="state.loading"
      :dataSource="state.tableList"
      :columns="columns"
      :pagination="pagination"
      size="small"
      rowKey="id"
      :noDataSize="'small'"
      :noDataText="t('common.messages.noData')"
      @change="changePagination">
      <!-- Custom cell rendering -->
      <template #bodyCell="{ column, text, record }">
        <!-- Quota name column -->
        <template v-if="column.dataIndex === 'name'">
          {{ getQuotaNameMessage(text) }}
        </template>

        <!-- Quota value column -->
        <template v-if="column.dataIndex === 'value'">
          {{ getQuotaNameDisplay(record.name) }}
        </template>

        <!-- Allow change column -->
        <template v-if="column.dataIndex === 'allowChange'">
          {{ formatBooleanStatus(text, t) }}
        </template>

        <!-- Action column -->
        <template v-if="column.dataIndex === 'action'">
          <template v-if="record.allowChange && app.has('ResourceQuotaModify')">
            <a class="text-theme-text-hover cursor-pointer" @click="openEditModal(record)">
              {{ app.getName('ResourceQuotaModify') }}
            </a>
          </template>
          <template v-else>
            <a class="text-theme-sub-content">{{ app.getName('ResourceQuotaModify') }}</a>
          </template>
        </template>
      </template>
    </Table>

    <!-- Edit quota modal -->
    <AsyncComponent :visible="state.visible">
      <EditQuota
        v-if="state.currQuota"
        v-model:visible="state.visible"
        :default0="+state.currQuota.default0"
        :max="+state.currQuota.max"
        :min="+state.currQuota.min"
        :name="state.currQuota.name.value"
        @ok="editSuccess" />
    </AsyncComponent>
  </PureCard>
</template>
