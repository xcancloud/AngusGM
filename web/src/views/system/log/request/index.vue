

<template>
  <div class="h-full">
    <!-- System hints showing log retention policy -->
    <Hints
      :text="t('log.request.messages.description', { clearBeforeDay: state.clearBeforeDay })"
      class="mb-1" />

    <PureCard class="p-3.5" style="height: calc(100% - 24px);">
      <!-- Search and statistics section -->
      <div ref="globalSearch">
        <Statistics
          resource="ApiLogs"
          :barTitle="t('log.request.messages.newRequests')"
          dateType="DAY"
          :router="GM"
          :visible="state.showCount" />

        <div class="flex items-start justify-between text-3 leading-3 mb-2">
          <SearchPanel
            :options="options"
            class="flex-1 mr-2"
            @change="searchChange" />

          <div>
            <IconCount
              v-model:value="state.showCount"
              class="mt-1.5 text-3.5" />
            <IconRefresh
              class="ml-2 mt-1.5 text-3.5"
              :loading="state.loading"
              :disabled="state.disabled"
              @click="getList" />
          </div>
        </div>
      </div>

      <!-- Main content area with log list and details -->
      <div
        :style="{ height: `calc(100% - ${state.globalSearchHeight}px)` }"
        class="flex">
        <!-- Left panel: API log list -->
        <Spin
          :spinning="state.loading"
          class="w-100 border-r flex flex-col flex-none border-theme-divider h-full">
          <template v-if="hasData">
            <div
              class="relative text-3 leading-3 flex-1 overflow-hidden hover:overflow-y-auto pr-1.75"
              style="scrollbar-gutter: stable;">
              <div
                v-for="(item, index) in state.tableList"
                :key="item.id"
                ref="tabItemRefs"
                class="p-2 space-y-3.5 cursor-pointer border-theme-divider transition-colors duration-200"
                :class="{
                  'border-b': index < state.tableList.length - 1,
                  'bg-theme-tabs-selected border-l-2 border-selected': isItemSelected(item, state.selectedApi?.id || '')
                }"
                @click="handleClick(item)">
                <!-- API name and status row -->
                <div class="flex items-center justify-between space-x-5">
                  <div class="truncate font-medium">
                    <Tooltip
                      :title="item?.apiName"
                      placement="bottomLeft">
                      {{ item?.apiName }}
                    </Tooltip>
                  </div>
                  <div
                    class="whitespace-nowrap flex items-center"
                    :class="getStatusTextColor(item.status || '')">
                    <Icon
                      :icon="getStatusIcon(item.status || '')"
                      class="mr-1" />
                    {{ item.status }}
                  </div>
                </div>

                <!-- HTTP method and URI row -->
                <div class="flex items-center space-x-3.5">
                  <div :class="getHttpMethodColor(item?.method || '', textColor)">{{ item?.method }}</div>
                  <div class="truncate">
                    <Tooltip
                      :title="item.uri"
                      placement="bottomLeft">
                      {{ item?.uri }}
                    </Tooltip>
                  </div>
                </div>
              </div>
            </div>

            <!-- Pagination controls -->
            <Pagination
              :current="state.params.pageNo"
              :pageSize="state.params.pageSize"
              :pageSizeOptions="pageSizeOptions as (string | number)[]"
              :total="state.total"
              :hideOnSinglePage="false"
              :showTotal="(total: number, range: [number, number]) => `${range[0]}-${range[1]} of ${total} items`"
              showLessItems
              showSizeChanger
              size="small"
              class="text-right mt-2 mr-2"
              @change="paginationChange" />
          </template>

          <template v-else>
            <NoData class="flex-1" />
          </template>
        </Spin>

        <!-- Right panel: Tabbed detail view -->
        <Tabs
          v-model:activeKey="state.activeKey"
          size="small"
          class="flex-1 ml-3.5 h-full"
          :class="{ '-mr-3.5': state.activeKey !== 0 }">
          <!-- Basic information tab -->
          <TabPane
            :key="0"
            :tab="t('log.request.tabs.basic')">
            <template v-if="state.detail">
              <Grid
                :columns="gridColumns"
                :dataSource="state.detail"
                :colon="false"
                :labelSpacing="40"
                class="-ml-2 grid-row" />
            </template>
            <template v-else>
              <NoData class="flex-1 -mt-6" />
            </template>
          </TabPane>

          <!-- Request details tab -->
          <TabPane
            :key="1"
            :tab="t('log.request.tabs.request')"
            forceRender>
            <template v-if="state.detail">
              <ApiRequest :data="state.detail" />
            </template>
            <template v-else>
              <NoData class="flex-1 -mt-6" />
            </template>
          </TabPane>

          <!-- Response details tab -->
          <TabPane
            :key="2"
            :tab="t('log.request.tabs.response')"
            forceRender>
            <template v-if="state.detail">
              <ApiResponse :data="state.detail" />
            </template>
            <template v-else>
              <NoData class="flex-1 -mt-6" />
            </template>
          </TabPane>
        </Tabs>
      </div>
    </PureCard>
  </div>
</template><script setup lang="ts">
import { defineAsyncComponent, onMounted, reactive, computed, nextTick, ref } from 'vue';
import { Pagination, TabPane, Tabs, Tooltip } from 'ant-design-vue';
import { Grid, Hints, Icon, IconCount, IconRefresh, NoData, PureCard, SearchPanel, Spin } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import { SearchCriteria, GM, HttpMethod } from '@xcan-angus/infra';
import elementResizeDetector from 'element-resize-detector';

import { setting, userLog } from '@/api';
import { DataRecordType, RequestLogState } from './types';
import {
  createSearchOptions, createGridColumns, createHttpMethodColors, createPageSizeOptions, resetPagination,
  updatePaginationParams, getHttpMethodColor, getStatusIcon, getStatusTextColor, isItemSelected
} from './utils';

// Lazy load components for better performance
const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));
const ApiRequest = defineAsyncComponent(() => import('./components/apiRequest.vue'));
const ApiResponse = defineAsyncComponent(() => import('./components/apiResponse.vue'));

const { t } = useI18n();

// Reactive state management
const state = reactive<RequestLogState>({
  showCount: true,
  params: { pageNo: 1, pageSize: 10, filters: [] },
  total: 0,
  tableList: [],
  loading: false,
  disabled: false,
  detail: undefined,
  selectedApi: undefined,
  activeKey: 0,
  globalSearchHeight: 0,
  clearBeforeDay: undefined
});

// UI state management
const tabItemRefs = reactive<HTMLElement[]>([]);
const globalSearch = ref<HTMLElement | null>(null);

// Element resize detector for dynamic height calculation
const erd = elementResizeDetector();

// Computed properties for better performance
const hasData = computed(() => state.tableList.length > 0);

// HTTP method color mapping for visual distinction
const textColor = computed(() => createHttpMethodColors());

// Pagination configuration
const pageSizeOptions = computed(() => createPageSizeOptions());

// Search options configuration for the search panel
const options = computed(() => createSearchOptions(t, GM, HttpMethod));

// Grid columns configuration for displaying log details
const gridColumns = computed(() => createGridColumns(t));

/**
 * Fetch API log list with pagination and filters
 * Includes debouncing to prevent multiple simultaneous requests
 */
const getList = async (): Promise<void> => {
  if (state.loading) {
    return;
  }

  state.loading = true;
  try {
    const [error, { data = { list: [], total: 0 } }] = await userLog.getApiLog(state.params);
    if (error) {
      return;
    }
    if (data.list?.length) {
      state.tableList = data.list;
      state.total = +data.total;
      state.selectedApi = data.list[0];
      await getDetail(data.list[0].id);
      return;
    }
    state.tableList = [];
    state.selectedApi = undefined;
    state.detail = undefined;
  } finally {
    state.loading = false;
  }
};

/**
 * Fetch detailed information for a specific API log entry
 */
const getDetail = async (id: string): Promise<void> => {
  if (!id) return;
  const [error, res] = await userLog.getApiLogDetail(id);
  if (error) {
    return;
  }
  state.detail = res.data;
};

/**
 * Handle search criteria changes and trigger new search
 */
const searchChange = async (data: SearchCriteria[]): Promise<void> => {
  resetPagination(state.params);
  state.params.filters = data;
  state.disabled = true;

  try {
    await getList();
  } finally {
    state.disabled = false;
  }
};

/**
 * Fetch system settings for API log configuration
 * Retrieves the number of days to keep logs before automatic cleanup
 */
const getSettings = async (): Promise<void> => {
  const [error, { data }] = await setting.getSettingDetail('API_LOG_CONFIG');
  if (error) {
    return;
  }

  state.clearBeforeDay = data.apiLog?.clearBeforeDay;
};

/**
 * Handle element resize for dynamic height calculation
 * Ensures proper layout when search bar height changes
 */
const resize = (element: HTMLElement): void => {
  state.globalSearchHeight = element.offsetHeight;
};

/**
 * Handle pagination changes and fetch new data
 */
const paginationChange = async (page: number, size: number): Promise<void> => {
  updatePaginationParams(state.params, page, size);
  state.disabled = true;

  try {
    await getList();
  } finally {
    state.disabled = false;
  }
};

/**
 * Handle API log item selection
 * Updates selected item and fetches detailed information
 */
const handleClick = async (item: DataRecordType): Promise<void> => {
  state.selectedApi = item;
  await getDetail(item.id);
};

// Lifecycle hooks
onMounted(async () => {
  // Initialize component data
  await Promise.all([
    getSettings(),
    getList()
  ]);

  // Set up resize detection for search bar
  await nextTick();
  if (globalSearch.value) {
    erd.listenTo(globalSearch.value, resize);
  }
});
</script>

<style scoped>
/* Custom styling for selected item border */
.border-selected {
  border-left-color: var(--border-divider-selected);
}

/* Grid row styling for better visual separation */
:deep(.grid-row > div) {
  padding-right: 8px;
  padding-left: 8px;
}

:deep(.grid-row > div > :first-child) {
  padding: 8px;
}

:deep(.grid-row > div:nth-child(2n)) {
  background-color: var(--table-header-bg);
}

/* Ensure proper scrolling for tab content */
:deep(.ant-tabs-content-holder) {
  overflow-y: auto;
}
</style>
