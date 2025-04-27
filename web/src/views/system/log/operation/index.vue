<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { Hints, IconCount, IconRefresh, PureCard, SearchPanel, Table } from '@xcan-angus/vue-ui';
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
const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));

const { t } = useI18n();
const params = ref<SearchParams>({ pageNo: 1, pageSize: 10, filters: [] });
const total = ref(0);
const loading = ref(false);
const disabled = ref(false);
const tableList = ref<any[]>([]);

const searchChange = async (data: { key: string; value: string; op: FilterOp; }[]) => {
  params.value.pageNo = 1;
  params.value.filters = data;
  disabled.value = true;
  await getList();
  disabled.value = false;
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
  disabled.value = true;
  await getList();
  disabled.value = false;
};

const clearBeforeDay = ref<string>();
const getSettings = async () => {
  const [error, { data }] = await setting.getSettingDetail('OPERATION_LOG_CONFIG');
  if (error) {
    return;
  }

  clearBeforeDay.value = data.operationLog?.clearBeforeDay;
};
onMounted(() => {
  getSettings();
  getList();
});

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
    type: 'input',
    op: 'EQUAL',
    allowClear: true
  },
  {
    valueKey: 'resourceName',
    placeholder: t('查询操作资源'),
    type: 'input',
    allowClear: true
  },
  {
    valueKey: 'success',
    type: 'select',
    allowClear: true,
    options: [
      {
        label: t('op-search-3'),
        value: false
      },
      {
        label: t('op-search-4'),
        value: true
      }
    ],
    placeholder: t('查询是否成功')
  },
  {
    valueKey: 'optDate',
    type: 'date',
    placeholder: t('操作时间')
  }
];

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
      <div class="flex items-start">
        <SearchPanel
          :options="options"
          class="flex-1 mr-2"
          @change="searchChange" />
        <IconCount v-model:value="showCount" class="mt-2" />
        <IconRefresh
          class="ml-2 mt-2"
          :loading="loading"
          :disabled="disabled"
          @click="getList" />
      </div>
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
