<script setup lang="ts">
import { onMounted, ref, h, computed } from 'vue';
import { Badge, Spin } from 'ant-design-vue';
import { PureCard, Icon, SearchPanel, Table, IconRefresh } from '@xcan/design';
import { security } from '@xcan/security';
import { LoadingOutlined } from '@ant-design/icons-vue';

import { useI18n } from 'vue-i18n';
import {online} from '@/api';

type User = {
  userId: string;
  fullName: string;
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

const params = ref<SearchParams>({ pageNo: 1, pageSize: 10, filters: [] });
const total = ref(0);
const userList = ref<User[]>([]);
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

  userList.value = data?.list.map(item => ({ ...item, loading: false }));
  total.value = +data.total;
};

const handleLogOut = async function (item: User) {
  item.loading = true;
  await online.offlineUser({ receiveObjectIds: [item.userId], receiveObjectType: 'USER' });
  item.loading = false;
  disabled.value = true;
  await getList();
  disabled.value = false;
};

const tableChange = async (_pagination) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
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
  }
];

const columns = [
  {
    title: t('用户名称'),
    dataIndex: 'fullName',
    width: 150
  },
  {
    title: t('设备ID'),
    dataIndex: 'deviceId',
    width: 260
  },
  {
    title: t('terminalEquipment'),
    dataIndex: 'userAgent'
  },
  {
    title: 'IP',
    dataIndex: 'remoteAddress',
    width: 160
  },
  {
    title: t('onlineStatus'),
    dataIndex: 'online',
    width: 80
  },
  {
    title: t('upTime'),
    dataIndex: 'onlineDate',
    width: 140,
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('logOut'),
    dataIndex: 'option',
    width: 60,
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
      :dataSource="userList"
      :columns="columns"
      :pagination="pagination"
      :loading="loading"
      rowKey="userId"
      size="small"
      @change="tableChange">
      <template #bodyCell="{column,text,record}">
        <template v-if="column.dataIndex === 'online'">
          <Badge :color="text?'rgba(82,196,26,1)':'rgba(217, 217, 217,1)'" :text="text?t('online1'): t('offline1')" />
        </template>
        <template v-if="column.dataIndex === 'option' && security.show('SignOut')">
          <template v-if="record.loading">
            <Spin :indicator="indicator" />
          </template>
          <template v-else>
            <Icon
              v-if="record.online && security.has('SignOut')"
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
<style scoped>
@keyframes circle {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

.circle-move {
  animation-name: circle;
  animation-duration: 1000ms;
  animation-timing-function: linear;
  animation-iteration-count: infinite;
  animation-direction: normal;
}
</style>
