<script setup lang="ts">
import { computed, h, onMounted, ref } from 'vue';
import { Badge, Spin } from 'ant-design-vue';
import { Icon, IconRefresh, Image, PureCard, SearchPanel, Table } from '@xcan-angus/vue-ui';
import { PageQuery, SearchCriteria, app } from '@xcan-angus/infra';
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

const { t } = useI18n();

const params = ref<PageQuery>({
  pageNo: 1,
  pageSize: 10,
  filters: [],
  orderBy: 'id',
  orderSort: PageQuery.OrderSort.Desc,
  fullTextSearch: true
});
const total = ref(0);
const onlineList = ref<Online[]>([]);
const loading = ref(false);
const disabled = ref(false);
const getList = async function () {
  if (loading.value) {
    return;
  }

  loading.value = true;
  const [error, { data = { list: [], total: 0 } }] = await online.getOnlineUserList(params.value);
  loading.value = false;
  if (error) {
    return;
  }

  onlineList.value = data?.list.map(item => ({ ...item, loading: false }));
  total.value = +data.total;
};

const handleLogOut = async function (item: Online) {
  item.loading = true;
  await online.offlineUser({ receiveObjectIds: [item.userId], receiveObjectType: 'USER', broadcast: false });
  item.loading = false;
  disabled.value = true;
  await getList();
  disabled.value = false;
};

const tableChange = async (_pagination, _filters, sorter: {
  orderBy: string;
  orderSort: PageQuery.OrderSort
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

const searchChange = async (data: { key: string; value: string; op: SearchCriteria.OpEnum; }[]) => {
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
    placeholder: t('online.placeholder.searchNameIp'),
    valueKey: 'fullName',
    type: 'input',
    op: 'EQUAL',
    allowClear: true
  },
  {
    placeholder: t('online.placeholder.searchDeviceId'),
    valueKey: 'deviceId',
    type: 'input',
    op: 'EQUAL',
    allowClear: true
  },
  {
    valueKey: 'onlineDate',
    type: 'date-range',
    placeholder: [t('online.placeholder.onlineTimeFrom'), t('online.placeholder.onlineTimeTo')],
    allowClear: true
  },
  {
    valueKey: 'offlineDate',
    type: 'date-range',
    placeholder: [t('online.placeholder.offlineTimeFrom'), t('online.placeholder.offlineTimeTo')],
    allowClear: true
  }
];

const columns = [
  {
    title: t('online.columns.id'),
    dataIndex: 'id',
    hide: true
  },
  {
    title: t('online.columns.user'),
    dataIndex: 'fullName',
    width: '10%',
    sorter: true
  },
  {
    title: t('online.columns.onlineStatus'),
    dataIndex: 'online',
    width: '8%'
  },
  {
    title: t('online.columns.upTime'),
    dataIndex: 'onlineDate',
    key: 'onlineDate',
    width: '12%',
    customRender: ({ text }) => text || '--',
    sorter: {
      compare: (a, b) => a.onlineDate > b.onlineDate
    }
  },
  {
    title: t('online.columns.offlineTime'),
    dataIndex: 'offlineDate',
    key: 'offlineDate',
    width: '12%',
    customRender: ({ text }) => text || '--',
    sorter: {
      compare: (a, b) => a.offlineDate > b.offlineDate
    }
  },
  {
    title: t('online.columns.terminalEquipment'),
    dataIndex: 'userAgent',
    width: '42%',
    groupName: 'userAgent',
    customRender: ({ text }) => text || '--'
  },
  {
    title: t('online.columns.deviceId'),
    dataIndex: 'deviceId',
    width: '42%',
    hide: true,
    groupName: 'userAgent',
    customRender: ({ text }) => text || '--'
  },
  {
    title: t('online.columns.ip'),
    dataIndex: 'remoteAddress',
    width: '8%'
  },
  {
    title: t('online.columns.logOut'),
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
            <Image
              type="avatar"
              class="w-6 rounded-full mr-1"
              :src="record.avatar" />
            <span class="flex-1 truncate" :tite="record.fullName">{{ record.fullName }}</span>
          </div>
        </template>
        <template v-if="column.dataIndex === 'online'">
          <Badge :color="text?'rgba(82,196,26,1)':'rgba(217, 217, 217,1)'" :text="text?t('online.messages.online'): t('online.messages.offline')" />
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
