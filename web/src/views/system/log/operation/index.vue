<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { Hints, PureCard, Table } from '@xcan-angus/vue-ui';
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
const Statistics = defineAsyncComponent(() => import('./Statistics/index.vue'));
const SearchPanel = defineAsyncComponent((() => import('./SearchPanel/index.vue')));

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
  const [error, { data = { list: [], total: 0 } }] = await userLog.getOperationLog(params.value);
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

const tableChange = async (_pagination) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
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

const toggeleOpenCount = () => {
  showCount.value = !showCount.value;
};

onMounted(() => {
  getSettings();
  getList();
});


const columns = [
  {
    title: t('日志ID'),
    dataIndex: 'id',
    width: '12%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('op-table-1'),
    dataIndex: 'fullName',
    width: '10%'
  },
  {
    title: t('请求ID'),
    dataIndex: 'requestId',
    width: '20%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('操作资源'),
    dataIndex: 'resourceName',
    width: '10%'
  },
  {
    title: t('op-table-2'),
    dataIndex: 'description'
  },
  {
    title: t('op-table-3'),
    dataIndex: 'success',
    width: '10%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('op-table-4'),
    dataIndex: 'failureReason'
  },
  {
    title: t('op-table-5'),
    dataIndex: 'optDate',
    width: '11%',
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
        @openCount="toggeleOpenCount"
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
          <div v-if="column.dataIndex === 'success'" class="flex items-center">
            <span :class="['w-1.5 h-1.5 mr-2 rounded-full', record.success ? 'bg-success' : 'bg-danger']"></span>
            <span>{{ record.success ? t('op-search-4') : t('op-search-3') }}</span>
          </div>
        </template>
      </Table>
    </PureCard>
  </div>
</template>
