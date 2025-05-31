<script setup lang="ts">
import { onMounted, ref, h, computed } from 'vue';
import { Badge, Spin } from 'ant-design-vue';
import { PureCard, Icon, SearchPanel, Table, Image, IconRefresh } from '@xcan-angus/vue-ui';
import { app } from '@xcan-angus/tools';
import { LoadingOutlined } from '@ant-design/icons-vue';

import { useI18n } from 'vue-i18n';
import { online } from '@/api';

type Online = {
  id: bigint;
  userId: string;
  fullName: string;
  deviceId: string;
  userAgent: string;
  remoteAddress: string;
  online: boolean;
  onlineDate: string;
  offlineDate: string;
  loading: boolean;
}

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

const { t } = useI18n();

const params = ref<SearchParams>({ pageNo: 1, pageSize: 10, filters: [], orderBy: 'id', orderSort: 'DESC' });
const total = ref(0);
const onlineList = ref<Online[]>([]);
const loading = ref(false);
const disabled = ref(false);
const getList = async function () {
  if (loading.value) {
    return;
  }

  loading.value = true;
  const [error, { data = { list: [], total: 0 } }] = await online.searchOnlineUsers(params.value);
  loading.value = false;
  if (error) {
    return;
  }

  onlineList.value = data?.list.map(item => ({ ...item, loading: false }));
  total.value = +data.total;
};

const handleLogOut = async function (item: Online) {
  item.loading = true;
  await online.offlineUser({ receiveObjectIds: [item.userId], receiveObjectType: 'USER' });
  item.loading = false;
  disabled.value = true;
  await getList();
  disabled.value = false;
};

const tableChange = async (_pagination, _filters, sorter: {
  orderBy: string;
  orderSort: 'DESC' | 'ASC'
}) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  params.value.orderBy = sorter.orderBy;
  params.value.orderSort = sorter.orderSort;

  disabled.value = true;
  await getList();
  disabled.value = false;
};

const pagination = computed(() => {
  return {
    current: params.value.pageNo,
    pageSize: params.value.pageSize,
    total: total.value
  };
});

const searchChange = async (data: { key: string; value: string; op: FilterOp; }[]) => {
  params.value.pageNo = 1;
  params.value.filters = data;
  disabled.value = true;
  await getList();
  disabled.value = false;
};

onMounted(() => {
  getList();
});

const searchOptions = [
  {
    placeholder: t('onLinePlaceHolder'),
    valueKey: 'fullName',
    type: 'input',
    op: 'EQUAL',
    allowClear: true
  },
  {
    placeholder: t('查询设备ID'),
    valueKey: 'deviceId',
    type: 'input',
    op: 'EQUAL',
    allowClear: true
  },
  {
    valueKey: 'onlineDate',
    type: 'date-range',
    placeholder: [t('上线时间从'), t('上线时间截止')],
    allowClear: true
  },
  {
    valueKey: 'offlineDate',
    type: 'date-range',
    placeholder: [t('离线时间从'), t('离线时间截止')],
    allowClear: true
  }
];

const columns = [
  {
    title: t('ID'),
    dataIndex: 'id',
    hide: true
  },
  {
    title: t('用户'),
    dataIndex: 'fullName',
    width: '10%',
    sorter: true
  },
  {
    title: t('onlineStatus'),
    dataIndex: 'online',
    width: '8%'
  },
  {
    title: t('upTime'),
    dataIndex: 'onlineDate',
    key: 'onlineDate',
    width: '12%',
    customRender: ({ text }) => text || '--',
    sorter: {
      compare: (a, b) => a.onlineDate > b.onlineDate
    }
  },
  {
    title: '离线时间',
    dataIndex: 'offlineDate',
    key: 'offlineDate',
    width: '12%',
    customRender: ({ text }) => text || '--',
    sorter: {
      compare: (a, b) => a.offlineDate > b.offlineDate
    }
  },
  {
    title: t('terminalEquipment'),
    dataIndex: 'userAgent',
    width: '42%',
    groupName: 'userAgent',
    customRender: ({ text }) => text || '--'
  },
  {
    title: t('设备ID'),
    dataIndex: 'deviceId',
    width: '42%',
    hide: true,
    groupName: 'userAgent',
    customRender: ({ text }) => text || '--'
  },
  {
    title: 'IP',
    dataIndex: 'remoteAddress',
    width: '8%'
  },
  {
    title: t('logOut'),
    dataIndex: 'option',
    width: '8%',
    align: 'center'
  }
];

const indicator = h(LoadingOutlined, {
  style: {
    fontSize: '24px'
  },
  spin: true
});

</script>
<template>
  <PureCard class="min-h-full p-3.5">
    <div class="flex items-center justify-between mb-2">
      <SearchPanel
        class="flex-1"
        :options="searchOptions"
        @change="searchChange" />
      <IconRefresh
        :loading="loading"
        :disabled="disabled"
        @click="getList" />
    </div>
    <Table
      :columns="columns"
      :dataSource="onlineList"
      :pagination="pagination"
      :loading="loading"
      rowKey="id"
      @change="tableChange">
      <template #bodyCell="{column,text,record}">
        <template v-if="column.dataIndex === 'fullName'">
          <div class="inline-flex items-center truncate">
            <Image type="avatar" class="w-6 rounded-full mr-1" :src="record.avatar" />
            <span class="flex-1 truncate" :tite="record.fullName">{{record.fullName}}</span>
          </div>
        </template>
        <template v-if="column.dataIndex === 'online'">
          <Badge :color="text?'rgba(82,196,26,1)':'rgba(217, 217, 217,1)'" :text="text?t('online1'): t('offline1')" />
        </template>
        <template v-if="column.dataIndex === 'option' && app.show('SignOut')">
          <template v-if="record.loading">
            <Spin :indicator="indicator" />
          </template>
          <template v-else>
            <Icon
              v-if="record.online && app.has('SignOut')"
              icon="icon-xuanzezhanghao1"
              class="cursor-pointer text-theme-special text-theme-text-hover"
              @click="handleLogOut(record)" />
            <Icon
              v-else
              icon="icon-xuanzezhanghao1"
              class="cursor-pointer text-theme-sub-content" />
          </template>
        </template>
      </template>
    </Table>
  </PureCard>
</template>

