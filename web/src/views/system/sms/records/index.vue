<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { IconCount, IconRefresh, PureCard, SearchPanel, Table } from '@xcan-angus/vue-ui';
import { app, GM } from '@xcan-angus/infra';
import { Badge } from 'ant-design-vue';

import { sms } from '@/api';
import { FilterOp, SearchParams, SmsRecord } from './PropsType';

const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));

const { t } = useI18n();
const smsList = ref<SmsRecord[]>([]);

const showCount = ref(true);
const loading = ref(false);
const params = ref<SearchParams>({ pageNo: 1, pageSize: 10, filters: [] });
const total = ref(0);

const pagination = computed(() => {
  return {
    current: params.value.pageNo,
    pageSize: params.value.pageSize,
    total: total.value
  };
});

const loadSmsRecordList = async function () {
  loading.value = true;
  const [error, { data = { list: [], total: 0 } }] = await sms.getSmsList(params.value);
  loading.value = false;
  if (error) {
    return;
  }
  smsList.value = data.list;
  total.value = +data.total;
};

const searchChange = (data: { key: string; value: string; op: FilterOp; }[]) => {
  params.value.pageNo = 1;
  params.value.filters = data;
  loadSmsRecordList();
};

const tableChange = (_pagination, _filters, sorter) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  params.value.orderBy = sorter.orderBy;
  params.value.orderSort = sorter.orderSort;
  loadSmsRecordList();
};

const handleRefresh = () => {
  if (loading.value) {
    return;
  }
  loadSmsRecordList();
};

onMounted(() => {
  loadSmsRecordList();
});

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: '10%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('发送状态'),
    dataIndex: 'sendStatus',
    key: 'sendStatus',
    width: '8%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('发送用户ID'),
    dataIndex: 'sendUserId',
    key: 'sendUserId',
    width: '10%',
    customRender: ({ text }): string => text && text !== '-1' ? text : '--',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('模板编码'),
    dataIndex: 'templateCode',
    key: 'templateCode',
    width: '20%',
    customRender: ({ text }): string => text || '--'
  },
  {
    title: t('是否加急'),
    dataIndex: 'urgent',
    width: '7%',
    customRender: ({ text }): string => text ? '是' : '否'
  },
  {
    title: t('是否验证码'),
    dataIndex: 'verificationCode',
    width: '7%',
    customRender: ({ text }): string => text ? '是' : '否'
  },
  {
    title: t('批量发送'),
    dataIndex: 'batch',
    width: '7%',
    customRender: ({ text }): string => text ? '是' : '否'
  },
  {
    title: t('发送时间'),
    dataIndex: 'actualSendDate',
    key: 'actualSendDate',
    sorter: true,
    width: '10%',
    customRender: ({ text }): string => text || '--'
  },
  {
    title: t('期望发送时间'),
    dataIndex: 'expectedSendDate',
    key: 'expectedSendDate',
    sorter: true,
    width: '10%',
    customRender: ({ text }): string => text || '--'
  }
];

const searchoptions = ref([
  {
    valueKey: 'sendUserId',
    type: 'select-user',
    allowClear: true,
    placeholder: '选择发送用户',
    axiosConfig: { headers: { 'XC-Opt-Tenant-Id': '' } }
  },
  {
    valueKey: 'sendStatus',
    type: 'select-enum',
    enumKey: 'ProcessStatus',
    placeholder: t('选择发送状态'),
    allowClear: true
  },
  {
    valueKey: 'templateCode',
    type: 'input',
    placeholder: t('查询模板编码'),
    allowClear: true
  },
  {
    valueKey: 'urgent',
    type: 'select',
    options: [{ value: true, label: '是' }, { value: false, label: '否' }],
    placeholder: t('是否加急'),
    allowClear: true
  },
  {
    valueKey: 'verificationCode',
    type: 'select',
    options: [{ value: true, label: '是' }, { value: false, label: '否' }],
    placeholder: t('是否验证码'),
    allowClear: true
  },
  {
    valueKey: 'batch',
    type: 'select',
    options: [{ value: true, label: '是' }, { value: false, label: '否' }],
    placeholder: t('是否批量发送'),
    allowClear: true
  },
  {
    valueKey: 'actualSendDate',
    type: 'date-range',
    placeholder: [t('发送时间'), t('发送时间')],
    allowClear: true
  },
  {
    valueKey: 'expectedSendDate',
    type: 'date-range',
    placeholder: [t('期望时间'), t('期望时间')],
    allowClear: true
  }
]);

const getSendStatusColor = (value: 'SUCCESS' | 'PENDING' | 'FAILURE') => {
  switch (value) {
    case 'SUCCESS': // 成功
      return 'rgba(82,196,26,1)';
    case 'PENDING': // 待处理
      return 'rgba(255,165,43,1)';
    case 'FAILURE': // 失败
      return 'rgba(245,34,45,1)';
  }
};
</script>
<template>
  <PureCard class="flex-1 p-3.5">
    <Statistics
      resource="Sms"
      :router="GM"
      :barTitle="t('发送记录')"
      :visible="showCount" />
    <div class="flex items-start  mb-2">
      <SearchPanel
        :options="searchoptions"
        class="flex-1 mr-2"
        @change="searchChange" />
      <div class="flex items-center flex-none h-7">
        <IconCount v-model:value="showCount" class="mr-2" />
        <IconRefresh
          :loading="loading"
          @click="handleRefresh" />
      </div>
    </div>
    <Table
      :loading="loading"
      :dataSource="smsList"
      :pagination="pagination"
      :columns="columns"
      rowKey="id"
      size="small"
      @change="tableChange">
      <template #bodyCell="{column,text}">
        <template v-if="column.dataIndex === 'id'">
          <RouterLink
            v-if="app.has('SMSSendRecordsDetail')"
            :to="`/system/sms/records/${text}`"
            class="text-theme-special text-theme-text-hover">
            {{ text }}
          </RouterLink>
          <template v-else>
            {{ text }}
          </template>
        </template>
        <template v-if="column.dataIndex === 'sendStatus'">
          <Badge :color="getSendStatusColor(text?.value)" :text="text?.message" />
        </template>
      </template>
    </Table>
  </PureCard>
</template>
