<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { Hints, PureCard, Table, Image } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import { GM } from '@xcan-angus/tools';

import { setting, userLog } from '@/api';

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

const showCount = ref(true);
const Statistics = defineAsyncComponent(() => import('@/views/system/log/operation/statistics/index.vue'));
const SearchPanel = defineAsyncComponent((() => import('@/views/system/log/operation/searchPanel/index.vue')));

const { t } = useI18n();
const params = ref<SearchParams>({ pageNo: 1, pageSize: 10, filters: [] });
const total = ref(0);
const loading = ref(false);
const tableList = ref<any[]>([]);

const searchChange = async (data:{filters:{ key: string; value: string; op: FilterOp; }[]} ) => {
  params.value.pageNo = 1;
  params.value.filters = data.filters;
  await getList();
};

const getList = async () => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const [error, { data = { list: [], total: 0 } }] = await userLog.searchOperationLog(params.value);
  loading.value = false;
  if (error) {
    return;
  }

  tableList.value = data.list;
  total.value = +data.total;
};

const pagination = computed(() => {
  return {
    current: params.value.pageNo,
    pageSize: params.value.pageSize,
    total: total.value
  };
});

const tableChange = async (_pagination, _filters, sorter: {
  orderBy: string;
  orderSort: 'DESC' | 'ASC'
}) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  params.value.orderBy = sorter.orderBy;
  params.value.orderSort = sorter.orderSort;
  await getList();
};

const clearBeforeDay = ref<string>();
const getSettings = async () => {
  const [error, { data }] = await setting.getSettingDetail('OPERATION_LOG_CONFIG');
  if (error) {
    return;
  }

  clearBeforeDay.value = data.operationLog?.clearBeforeDay;
};

const toggleOpenCount = () => {
  showCount.value = !showCount.value;
};

onMounted(() => {
  getSettings();
  getList();
});

const columns = [
  {
    title: t('ID'),
    dataIndex: 'id',
    hide: true
  },
  {
    title: t('操作人'),
    dataIndex: 'fullName',
    width: '15%',
    sorter: true
  },
  {
    title: t('操作内容'),
    dataIndex: 'description',
    width: '35%',
  },
  {
    title: t('操作资源'),
    dataIndex: 'resource',
    width: '15%',
    customRender: ({ text }) => text?.message
  },
  {
    title: t('操作资源名称'),
    dataIndex: 'resourceName',
    groupName: 'resource',
    width: '20%',
  },
  {
    title: t('操作资源ID'),
    dataIndex: 'resourceId',
    groupName: 'resource',
    hide: true,
    width: '20%',
  },
  {
    title: t('op-table-5'),
    dataIndex: 'optDate',
    width: '15%',
    sorter: true,
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  }
];
</script>
<template>
  <div class="flex flex-col min-h-full">
    <Hints
      :text="'实时记录用户对系统的操作行为和资源变更信息；通过操作日志您可以实现安全分析、资源变更追踪、异常问题定位等。默认保存日志'+clearBeforeDay+'天。'"
      class="mb-1" />
    <PureCard class="flex-1 p-3.5">
      <Statistics
        :visible="showCount"
        :barTitle="t('操作日志')"
        dateType="DAY"
        resource="OperationLog"
        :geteway="GM" />
      <SearchPanel
        :loading="loading"
        :showCount="showCount"
        @openCount="toggleOpenCount"
        @refresh="getList"
        @change="searchChange"/>
      <Table
        :loading="loading"
        :dataSource="tableList"
        :columns="columns"
        :pagination="pagination"
        rowKey="id"
        size="small"
        class="mt-2"
        @change="tableChange">
        <template #bodyCell="{column, record}">
          <template v-if="column.dataIndex === 'fullName'">
            <div class="flex items-center">
              <Image
                type="avatar"
                :src="record.avatar"
                class="w-4 h-4 rounded-full mr-1" />
              <span class="flex-1 truncate" :title="record.fullName">{{ record.fullName }}</span>
            </div>
          </template>
        </template>
      </Table>
    </PureCard>
  </div>
</template>
