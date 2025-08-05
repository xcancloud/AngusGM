<script setup lang="ts">
import { reactive, onMounted, computed, ref, defineAsyncComponent } from 'vue';
import { SearchPanel, Table, PureCard, IconCount, IconRefresh, ButtonAuth } from '@xcan-angus/vue-ui';
import { app, GM } from '@xcan-angus/infra';
import { useI18n } from 'vue-i18n';
import { Badge } from 'ant-design-vue';

import { message } from '@/api';
import type { TableColumnType } from './PropsType';

const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));

const { t } = useI18n();

const state = reactive<{
  loading: boolean,
  columns: Array<TableColumnType>,
  dataSource: never[]
}>({
  loading: false,
  columns: [],
  dataSource: []
});

let searchForm: any = [];

const sortForm: Record<string, any> = {};

const receiveObjectType = ref();

const searchOptions = computed(() => {
  return [
    {
      placeholder: t('orderPlaceholder1'),
      valueKey: 'title',
      allowClear: true,
      type: 'input'
    },
    {
      valueKey: 'receiveType',
      type: 'select-enum',
      enumKey: 'MessageReceiveType',
      placeholder: t('selectReceivingMethod'),
      allowClear: true
    },
    {
      valueKey: 'status',
      type: 'select-enum',
      enumKey: 'MessageStatus',
      allowClear: true,
      placeholder: t('placeholder.p3')
    },
    {
      valueKey: 'receiveObjectType',
      type: 'select-enum',
      enumKey: 'ReceiveObjectType',
      placeholder: t('placeholder.p4'),
      allowClear: true
    }
  ];
}
);

const columns = [
  {
    title: t('title'),
    key: 'title',
    dataIndex: 'title',
    width: '17%'
  },
  {
    title: t('receivingMethod'),
    key: 'receiveType',
    width: '8%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('status'),
    key: 'status',
    width: '8%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('errMsg'),
    dataIndex: 'failureReason',
    width: '17%'
  },
  {
    title: t('sender'),
    dataIndex: 'createdByName',
    width: '8%'
  },
  {
    title: t('receivedObjType'),
    key: 'receiveObjectType',
    width: '8%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('receiveNum'),
    dataIndex: 'sentNum',
    key: 'sentNum',
    width: '8%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('selectEmail'),
    dataIndex: 'readNum',
    width: '8%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('sendDate'),
    dataIndex: 'timingDate',
    width: '10%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  }
];

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
});

const disabled = ref(false);

const load = async () => {
  if (state.loading) {
    return;
  }
  const params: Record<string, any> = {
    filters: searchForm,
    pageNo: pagination.current,
    pageSize: pagination.pageSize,
    fullTextSearch: true
  };
  state.loading = true;
  const [error, res] = await message.searchMessageList(params);
  state.loading = false;
  if (error) {
    return;
  }

  pagination.total = Number(res.data.total || 0);
  state.dataSource = res.data.list;
};

const searchTable = async (searchParams) => {
  receiveObjectType.value = searchParams.find(i => i.key === 'receiveObjectType')?.value || undefined;
  searchForm = searchParams.filter(i => i.key !== 'sendTenantId');
  pagination.current = 1;
  disabled.value = true;
  await load();
  disabled.value = false;
};

const listChange = async (page: Record<string, any>, _filters: unknown, sorter: Record<string, any>) => {
  if (sorter.orderBy) {
    sortForm.orderBy = sorter.orderBy;
    sortForm.orderSort = sorter.orderSort;
  }
  pagination.current = page.current;
  pagination.pageSize = page.pageSize;
  disabled.value = true;
  await load();
  disabled.value = false;
};

const obj: {
  [key: string]: string
} = {
  PENDING: 'warning',
  SENT: 'success',
  FAILURE: 'error'
};

const getStatusText = (key: string): string => {
  return obj[key];
};

const showCount = ref(true);

onMounted(() => {
  load();
});
</script>
<template>
  <PureCard class="min-h-full p-3.5">
    <Statistics
      resource="Message"
      :barTitle="t('statistics.metrics.newMessages')"
      :router="GM"
      dateType="MONTH"
      :visible="showCount" />
    <div class="flex items-center justify-between my-2">
      <SearchPanel
        :options="searchOptions"
        class="flex-1"
        @change="searchTable" />
      <div class="flex items-center space-x-2">
        <ButtonAuth
          code="MessageSend"
          type="primary"
          icon="icon-tuisongtongzhi"
          href="/messages/message/send" />
        <IconCount v-model:value="showCount" />
        <IconRefresh
          :loading="state.loading"
          :disabled="disabled"
          @click="load" />
      </div>
    </div>
    <Table
      :columns="columns"
      :pagination="pagination"
      :dataSource="state.dataSource"
      :loading="state.loading"
      rowKey="id"
      size="small"
      @change="listChange">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'title'">
          <RouterLink v-if="app.has('MessageDetail')" :to="`/messages/message/${record.id}`">
            <a
              :title="record.title"
              href="javascript:;"
              class="text-theme-special text-theme-text-hover">{{ record.title }}</a>
          </RouterLink>
        </template>
        <template v-if="column.key === 'receiveType'">{{ record.receiveType.message }}</template>
        <template v-if="column.key === 'status'">
          <Badge :status="getStatusText(record.status.value)" :text="record.status.message" />
        </template>
        <template v-if="column.key === 'receiveObjectType'">
          {{ record.receiveObjectType?.message }}
        </template>
        <template v-if="column.dataIndex === 'receiveTenantName'">
          {{ record.receiveTenantName || t('allUser') }}
        </template>
        <template v-if="column.key === 'sentNum'">
          {{ +record.sentNum > -1 ? record.sentNum : '--' }}
        </template>
      </template>
    </Table>
  </PureCard>
</template>
