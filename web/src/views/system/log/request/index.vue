<script setup lang="ts">
import { defineAsyncComponent, onMounted, ref, computed, nextTick } from 'vue';
import { Pagination, TabPane, Tabs, Tooltip } from 'ant-design-vue';
import { Grid, Hints, Icon, IconCount, IconRefresh, NoData, PureCard, SearchPanel, Spin } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import { PageQuery, SearchCriteria, GM, HttpMethod } from '@xcan-angus/infra';
import elementResizeDetector from 'element-resize-detector';

import { setting, userLog } from '@/api';

// Lazy load components for better performance
const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));
const ApiRequest = defineAsyncComponent(() => import('./components/apiRequest.vue'));
const ApiResponse = defineAsyncComponent(() => import('./components/apiResponse.vue'));

const { t } = useI18n();

// Reactive state management
const showCount = ref(true);
const params = ref<PageQuery>({ pageNo: 1, pageSize: 10, filters: [] });
const total = ref(0);
const tableList = ref<any[]>([]);
const loading = ref(false);
const disabled = ref(false);
const detail = ref();
const selectedApi = ref();
const activeKey = ref(0);

// UI state management
const tabItemRefs = ref<HTMLElement[]>([]);
const searchBar = ref<HTMLElement | null>(null);
const searchBarHeight = ref(0);
const clearBeforeDay = ref<string>();

// Element resize detector for dynamic height calculation
const erd = elementResizeDetector();

// Computed properties for better performance
const hasData = computed(() => tableList.value.length > 0);
const isSelected = computed(() => (item: any) => selectedApi.value?.id === item?.id);

// HTTP method color mapping for visual distinction
const textColor = {
  GET: 'text-blue-bg',
  POST: 'text-success',
  DELETE: 'text-danger',
  PATCH: 'text-blue-bg1',
  PUT: 'text-orange-bg2',
  OPTIONS: 'text-blue-bg2',
  HEAD: 'text-orange-method',
  TRACE: 'text-purple-method'
} as const;

// Pagination configuration
const pageSizeOptions = ['10', '20', '30', '40', '50'] as const;

// Search options configuration for the search panel
const options: any[] = [
  {
    valueKey: 'id',
    placeholder: t('log.request.placeholder.searchLogId'),
    type: 'input' as const,
    op: SearchCriteria.OpEnum.Equal,
    allowClear: true
  },
  {
    valueKey: 'userId',
    placeholder: t('log.request.placeholder.selectUserId'),
    type: 'select-user' as const,
    allowClear: true
  },
  {
    valueKey: 'requestId',
    placeholder: t('log.request.placeholder.searchRequestId'),
    op: SearchCriteria.OpEnum.Equal,
    type: 'input' as const,
    allowClear: true
  },
  {
    valueKey: 'resourceName',
    placeholder: t('log.request.placeholder.searchOperationResource'),
    type: 'input' as const,
    op: SearchCriteria.OpEnum.Equal,
    allowClear: true
  },
  {
    valueKey: 'success',
    type: 'select' as const,
    allowClear: true,
    options: [
      {
        label: t('log.request.search.failed'),
        value: false
      },
      {
        label: t('log.request.search.success'),
        value: true
      }
    ],
    placeholder: t('log.request.placeholder.searchSuccess')
  },
  {
    valueKey: 'serviceCode',
    placeholder: t('log.request.placeholder.selectOrSearchService'),
    type: 'select' as const,
    action: `${GM}/service`,
    fieldNames: { label: 'name', value: 'code' },
    showSearch: true,
    allowClear: true
  },
  {
    valueKey: 'instanceId',
    placeholder: t('log.request.placeholder.searchInstanceId'),
    type: 'input' as const,
    op: SearchCriteria.OpEnum.Equal,
    allowClear: true
  },
  {
    valueKey: 'apiCode',
    placeholder: t('log.request.placeholder.searchApiCode'),
    type: 'input' as const,
    op: SearchCriteria.OpEnum.Equal,
    allowClear: true
  },
  {
    valueKey: 'method',
    placeholder: t('log.request.placeholder.searchRequestMethod'),
    type: 'select-enum' as const,
    enumKey: HttpMethod,
    allowClear: true
  },
  {
    valueKey: 'uri',
    placeholder: t('log.request.placeholder.searchRequestUri'),
    type: 'input' as const,
    op: SearchCriteria.OpEnum.Equal,
    allowClear: true
  },
  {
    valueKey: 'status',
    placeholder: t('log.request.placeholder.searchStatusCode'),
    type: 'input' as const,
    op: SearchCriteria.OpEnum.Equal,
    dataType: 'number',
    min: 0,
    max: 1000,
    allowClear: true
  },
  {
    valueKey: 'requestDate',
    type: 'date' as const,
    placeholder: t('log.request.placeholder.requestTime')
  }
];

// Grid columns configuration for displaying log details
const gridColumns = [
  [
    {
      label: t('log.request.columns.apiName'),
      dataIndex: 'apiName'
    },
    {
      label: t('log.request.columns.requestId'),
      dataIndex: 'requestId'
    },
    {
      label: t('log.request.columns.apiCode'),
      dataIndex: 'apiCode'
    },
    {
      label: t('log.request.columns.serviceName'),
      dataIndex: 'serviceName'
    },
    {
      label: t('log.request.columns.serviceCode'),
      dataIndex: 'serviceCode'
    },
    {
      label: t('log.request.columns.uri'),
      dataIndex: 'uri'
    },
    {
      label: t('log.request.columns.method'),
      dataIndex: 'method'
    },
    {
      label: t('log.request.columns.elapsedMillis'),
      dataIndex: 'elapsedMillis'
    },
    {
      label: t('log.request.columns.status'),
      dataIndex: 'status'
    }
  ]
];

/**
 * Fetch API log list with pagination and filters
 * Includes debouncing to prevent multiple simultaneous requests
 */
const getList = async function () {
  if (loading.value) {
    return;
  }

  loading.value = true;
  try {
    const [error, { data = { list: [], total: 0 } }] = await userLog.getApiLog(params.value);

    if (error) {
      return;
    }

    if (data.list?.length) {
      tableList.value = data.list;
      total.value = +data.total;
      selectedApi.value = data.list[0];
      await getDetail(data.list[0].id);
      return;
    }

    tableList.value = [];
    selectedApi.value = undefined;
    detail.value = undefined;
  } finally {
    loading.value = false;
  }
};

/**
 * Fetch detailed information for a specific API log entry
 * @param id - The unique identifier of the log entry
 */
const getDetail = async (id: string) => {
  if (!id) return;

  const [error, res] = await userLog.getApiLogDetail(id);
  if (error) {
    return;
  }

  detail.value = res.data;
};

/**
 * Handle search criteria changes and trigger new search
 * @param data - Array of search criteria with key, value, and operation
 */
const searchChange = async (data: { key: string; value: string; op: SearchCriteria.OpEnum; }[]) => {
  params.value.pageNo = 1;
  params.value.filters = data;
  disabled.value = true;

  try {
    await getList();
  } finally {
    disabled.value = false;
  }
};

/**
 * Fetch system settings for API log configuration
 * Retrieves the number of days to keep logs before automatic cleanup
 */
const getSettings = async () => {
  const [error, { data }] = await setting.getSettingDetail('API_LOG_CONFIG');
  if (error) {
    return;
  }

  clearBeforeDay.value = data.apiLog?.clearBeforeDay;
};

/**
 * Handle element resize for dynamic height calculation
 * Ensures proper layout when search bar height changes
 */
const resize = (element: HTMLElement) => {
  searchBarHeight.value = element.offsetHeight;
};

/**
 * Handle pagination changes and fetch new data
 * @param page - Current page number
 * @param size - Number of items per page
 */
const paginationChange = async (page: number, size: number) => {
  params.value.pageNo = page;
  params.value.pageSize = size;
  disabled.value = true;

  try {
    await getList();
  } finally {
    disabled.value = false;
  }
};

/**
 * Handle API log item selection
 * Updates selected item and fetches detailed information
 * @param item - The selected log item
 */
const handleClick = async (item: any) => {
  selectedApi.value = item;
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
  if (searchBar.value) {
    erd.listenTo(searchBar.value, resize);
  }
});
</script>

<template>
  <div class="h-full">
    <!-- System hints showing log retention policy -->
    <Hints
      :text="t('log.request.messages.description', { clearBeforeDay: clearBeforeDay })"
      class="mb-1" />

    <PureCard class="p-3.5" style="height: calc(100% - 24px);">
      <!-- Search and statistics section -->
      <div ref="searchBar">
        <Statistics
          resource="ApiLogs"
          :barTitle="t('log.request.messages.newRequests')"
          dateType="DAY"
          :router="GM"
          :visible="showCount" />

        <div class="flex items-start justify-between text-3 leading-3 mb-2">
          <SearchPanel
            :options="options"
            class="flex-1 mr-2"
            @change="searchChange" />

          <div>
            <IconCount
              v-model:value="showCount"
              class="mt-1.5 text-3.5" />
            <IconRefresh
              class="ml-2 mt-1.5 text-3.5"
              :loading="loading"
              :disabled="disabled"
              @click="getList" />
          </div>
        </div>
      </div>

      <!-- Main content area with log list and details -->
      <div
        :style="{height: `calc(100% - ${searchBarHeight}px)`}"
        class="flex">
        <!-- Left panel: API log list -->
        <Spin
          :spinning="loading"
          class="w-100 border-r flex flex-col flex-none border-theme-divider h-full">
          <template v-if="hasData">
            <div
              class="relative text-3 leading-3 flex-1 overflow-hidden hover:overflow-y-auto pr-1.75"
              style="scrollbar-gutter: stable;">
              <div
                v-for="(item, index) in tableList"
                :key="item.id"
                ref="tabItemRefs"
                class="p-2 space-y-3.5 cursor-pointer border-theme-divider transition-colors duration-200"
                :class="{
                  'border-b': index < tableList.length - 1,
                  'bg-theme-tabs-selected border-l-2 border-selected': isSelected(item)
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
                    :class="[item.status.substring(0, 2) === '20' ? 'text-success' : 'text-danger']">
                    <Icon
                      :icon="item.status === '200' ? 'icon-right' : 'icon-cuowu'"
                      class="mr-1" />
                    {{ item.status }}
                  </div>
                </div>

                <!-- HTTP method and URI row -->
                <div class="flex items-center space-x-3.5">
                  <div :class="textColor[item?.method]">{{ item?.method }}</div>
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
              :current="params.pageNo"
              :pageSize="params.pageSize"
              :pageSizeOptions="pageSizeOptions"
              :total="total"
              :hideOnSinglePage="false"
              :showTotal="false"
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
          v-model:activeKey="activeKey"
          size="small"
          class="flex-1 ml-3.5 h-full"
          :class="{'-mr-3.5': activeKey !== 0}">
          <!-- Basic information tab -->
          <TabPane
            :key="0"
            :tab="t('log.request.tabs.basic')">
            <template v-if="detail">
              <Grid
                :columns="gridColumns"
                :dataSource="detail"
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
            <template v-if="detail">
              <ApiRequest :data="detail" />
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
            <template v-if="detail">
              <ApiResponse :data="detail" />
            </template>
            <template v-else>
              <NoData class="flex-1 -mt-6" />
            </template>
          </TabPane>
        </Tabs>
      </div>
    </PureCard>
  </div>
</template>

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
