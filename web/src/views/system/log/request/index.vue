<script setup lang="ts">
import { defineAsyncComponent, onMounted, ref } from 'vue';
import { Pagination, TabPane, Tabs, Tooltip } from 'ant-design-vue';
import { Grid, Hints, Icon, IconCount, IconRefresh, NoData, PureCard, SearchPanel, Spin } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import { GM, HttpMethod } from '@xcan-angus/infra';
import elementResizeDetector from 'element-resize-detector';

import { setting, userLog } from '@/api';

// TODO 页面控制台报错

type FilterOp =
  'EQUAL'
  | 'NOT_EQUAL'
  | 'GREATER_THAN'
  | 'GREATER_THAN_EQUAL'
  | 'LESS_THAN'
  | 'LESS_THAN_EQUAL'
  | 'CONTAIN'
  | 'NOT_CONTAIN'
  | 'MATCH_END'
  | 'MATCH'
  | 'IN'
  | 'NOT_IN'
type Filters = { key: string, value: string, op: FilterOp }[]
type SearchParams = {
  pageNo?: number;
  pageSize?: number;
  filters?: Filters;
  orderBy?: string;
  orderSort?: 'ASC' | 'DESC';
}

const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));
const ApiRequest = defineAsyncComponent(() => import('./components/apiRequest.vue'));
const ApiResponse = defineAsyncComponent(() => import('./components/apiResponse.vue'));

const { t } = useI18n();
const showCount = ref(true);
const params = ref<SearchParams>({ pageNo: 1, pageSize: 10, filters: [] });
const total = ref(0);

const tableList = ref<any[]>([]);

// 列表查询方法
const loading = ref(false);
const disabled = ref(false);
const getList = async function () {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const [error, { data = { list: [], total: 0 } }] = await userLog.getApiLog(params.value);
  loading.value = false;
  if (error) {
    return;
  }
  if (data.list?.length) {
    tableList.value = data.list;
    total.value = +data.total;
    selectedApi.value = data.list[0];
    getDetail(data.list[0].id);
    return;
  }

  tableList.value = [];
  selectedApi.value = undefined;
  detail.value = undefined;
};

const detail = ref();
const getDetail = async (id: string) => {
  const [error, res] = await userLog.getApiLogDetail(id);
  if (error) {
    return;
  }

  detail.value = res.data;
};

const searchChange = async (data: { key: string; value: string; op: FilterOp; }[]) => {
  params.value.pageNo = 1;
  params.value.filters = data;
  disabled.value = true;
  await getList();
  disabled.value = false;
};

const clearBeforeDay = ref<string>();
const getSettings = async () => {
  const [error, { data }] = await setting.getSettingDetail('API_LOG_CONFIG');
  if (error) {
    return;
  }

  clearBeforeDay.value = data.apiLog?.clearBeforeDay;
};

const tabItemRefs = ref<HTMLElement[]>([]);
const searchBar = ref(null);
const searchBarHeight = ref(0);
const erd = elementResizeDetector();
const resize = (element) => {
  searchBarHeight.value = element.offsetHeight;
};
onMounted(() => {
  getSettings();
  getList();
  if (!searchBar.value) {
    return;
  }

  erd.listenTo(searchBar.value, resize);
});

const selectedApi = ref();
const handleClick = (item) => {
  selectedApi.value = item;
  getDetail(item.id);
};

const pageSizeOptions = ['10', '20', '30', '40', '50'];

const paginationChange = async (page: number, size: number) => {
  params.value.pageNo = page;
  params.value.pageSize = size;
  disabled.value = true;
  await getList();
  disabled.value = false;
};

const options = [
  {
    valueKey: 'id',
    placeholder: t('查询日志ID'),
    type: 'input',
    op: 'EQUAL',
    allowClear: true
  },
  {
    valueKey: 'userId',
    type: 'select-user',
    allowClear: true
  },
  {
    valueKey: 'requestId',
    placeholder: t('查询请求ID'),
    op: 'EQUAL',
    type: 'input',
    allowClear: true
  },
  {
    valueKey: 'resourceName',
    placeholder: t('查询操作资源'),
    type: 'input',
    op: 'EQUAL',
    allowClear: true
  },
  {
    valueKey: 'success',
    type: 'select',
    allowClear: true,
    options: [
      {
        label: t('request-search-4'),
        value: false
      },
      {
        label: t('request-search-5'),
        value: true
      }
    ],
    placeholder: t('查询是否成功')
  },
  {
    valueKey: 'serviceCode',
    placeholder: t('选择或搜索服务'),
    type: 'select',
    action: `${GM}/service`,
    fieldNames: { label: 'name', value: 'code' },
    showSearch: true,
    allowClear: true
  },
  {
    valueKey: 'instanceId',
    placeholder: t('查询实例ID'),
    type: 'input',
    op: 'EQUAL',
    allowClear: true
  },
  {
    valueKey: 'apiCode',
    placeholder: t('查询接口编码'),
    type: 'input',
    op: 'EQUAL',
    allowClear: true
  },
  {
    valueKey: 'method',
    placeholder: t('查询请求方式'),
    type: 'select-enum',
    enumKey: HttpMethod,
    allowClear: true
  },
  {
    valueKey: 'uri',
    placeholder: t('查询请求URI'),
    type: 'input',
    op: 'EQUAL',
    allowClear: true
  },
  {
    valueKey: 'status',
    placeholder: t('查询状态码(0~1000)'),
    type: 'input',
    op: 'EQUAL',
    dataType: 'number',
    min: 0,
    max: 1000,
    allowClear: true
  },
  {
    valueKey: 'requestDate',
    type: 'date',
    placeholder: t('请求时间')
  }
];

const gridColumns = [
  [
    {
      label: t('request-mess-7'),
      dataIndex: 'apiName'
    },
    {
      label: t('request-mess-9-1'),
      dataIndex: 'requestId'
    },
    {
      label: t('request-mess-8'),
      dataIndex: 'apiCode'
    },
    {
      label: t('request-mess-9'),
      dataIndex: 'serviceName'
    },
    {
      label: t('request-mess-a'),
      dataIndex: 'serviceCode'
    },
    {
      label: t('request-mess-b'),
      dataIndex: 'uri'
    },
    {
      label: t('request-mess-c'),
      dataIndex: 'method'
    },
    {
      label: t('request-mess-d'),
      dataIndex: 'elapsedMillis'
    },
    {
      label: t('request-mess-i'),
      dataIndex: 'status'
    }
  ]
];

const textColor = {
  GET: 'text-blue-bg',
  POST: 'text-success',
  DELETE: 'text-danger',
  PATCH: 'text-blue-bg1',
  PUT: 'text-orange-bg2',
  OPTIONS: 'text-blue-bg2',
  HEAD: 'text-orange-method',
  TRACE: 'text-purple-method'
};

const activeKey = ref(0);
</script>
<template>
  <div class="h-full">
    <Hints :text="'查看“用户令牌”和“系统令牌”的访问请求日志。默认保存'+clearBeforeDay+'天的记录。'" class="mb-1" />
    <PureCard class="p-3.5" style="height: calc(100% - 24px);">
      <div ref="searchBar">
        <Statistics
          resource="ApiLogs"
          :barTitle="t('statistics.metrics.newRequests')"
          dateType="DAY"
          :router="GM"
          :visible="showCount" />
        <div class="flex items-start justify-between text-3 leading-3 mb-2">
          <SearchPanel
            :options="options"
            class="flex-1 mr-2"
            @change="searchChange" />
          <div>
            <IconCount v-model:value="showCount" class="mt-1.5 text-3.5" />
            <IconRefresh
              class="ml-2 mt-1.5 text-3.5"
              :loading="loading"
              :disabled="disabled"
              @click="getList" />
          </div>
        </div>
      </div>
      <div :style="{height: `calc(100% - ${searchBarHeight}px)`}" class="flex">
        <Spin
          :spinning="loading"
          class="w-100 border-r flex flex-col flex-none border-theme-divider h-full">
          <template v-if="tableList.length>0">
            <div
              class="relative text-3 leading-3 flex-1 overflow-hidden hover:overflow-y-auto pr-1.75"
              style="scrollbar-gutter: stable;">
              <div
                v-for="item,index in tableList"
                ref="tabItemRefs"
                :key="item"
                class="p-2 space-y-3.5 cursor-pointer border-theme-divider"
                :class="{'border-b':index < tableList.length-1,'bg-theme-tabs-selected border-l-2 border-selected':selectedApi?.id === item?.id}"
                @click="handleClick(item)">
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
                    :class="[item.status.substring(0,2) === '20' ? 'text-success' : 'text-danger' ]">
                    <Icon :icon="item.status === '200' ? 'icon-right' : 'icon-cuowu'" class="mr-1" />
                    {{ item.status }}
                  </div>
                </div>
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
              @change="paginationChange">
              <!-- <template #itemRender="{page,type}">
                <a v-if="type==='page'"></a>
              </template> -->
            </Pagination>
          </template>
          <template v-else>
            <NoData class="flex-1" />
          </template>
        </Spin>
        <Tabs
          v-model:activeKey="activeKey"
          size="small"
          class="flex-1 ml-3.5 h-full"
          :class="{'-mr-3.5':activeKey!==0}">
          <TabPane
            :key="0"
            :tab="t('request-tab-1')">
            <template v-if="detail">
              <Grid
                :columns="gridColumns"
                :dataSource="detail"
                :colon="false"
                :labelSpacing="40"
                class="-ml-2 grid-row">
              </Grid>
            </template>
            <template v-else>
              <NoData class="flex-1 -mt-6" />
            </template>
          </TabPane>
          <TabPane
            :key="1"
            :tab="t('request-tab-2')"
            forceRender>
            <template v-if="detail">
              <ApiRequest :data="detail" />
            </template>
            <template v-else>
              <NoData class="flex-1 -mt-6" />
            </template>
          </TabPane>
          <TabPane
            :key="2"
            :tab="t('request-tab-3')"
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
.border-selected {
  border-left-color: var(--border-divider-selected);
}

:deep(.grid-row > div) {
  padding-right: 8px;
  padding-left: 8px;
}

:deep(.grid-row > div >:first-child) {
  padding: 8px;
}

:deep(.grid-row  > div:nth-child(2n)) {
  background-color: var(--table-header-bg);
}

:deep(.ant-tabs-content-holder) {
  overflow-y: auto;
}

</style>
