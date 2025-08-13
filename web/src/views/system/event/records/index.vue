<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Badge, Button, Popover } from 'ant-design-vue';
import { AsyncComponent, Hints, Icon, IconCount, IconRefresh, PureCard, SearchPanel, Table } from '@xcan-angus/vue-ui';
import { app, GM, EventPushStatus, EventType } from '@xcan-angus/infra';
import DOMPurify from 'dompurify';

import { event } from '@/api';
import { EventRecord } from './types';

const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));
const ReceiveConfig = defineAsyncComponent(() => import('./receiveConfig.vue'));
const ViewEvent = defineAsyncComponent(() => import('./view.vue'));

const { t } = useI18n();
const visible = ref(false);
// Selected event record item for viewing details
const selectedItem = ref<EventRecord>({
  description: '',
  eventCode: '',
  eKey: '',
  errMsg: '',
  eventContent: '',
  execNo: '',
  id: '',
  pushStatus: { value: '', message: '' },
  tenantId: '',
  tenantName: '',
  triggerTime: '',
  type: { value: '', message: '' }
});
// Search panel options configuration
const Options = [
  {
    placeholder: t('event.records.placeholder.searchEventCode'),
    valueKey: 'code',
    type: 'input' as const,
    op: 'MATCH' as const,
    allowClear: true
  },
  {
    valueKey: 'userId',
    type: 'select-user' as const,
    allowClear: true,
    placeholder: t('event.records.placeholder.selectReceiver'),
    showSearch: true
  },
  {
    valueKey: 'type',
    type: 'select-enum' as const,
    enumKey: EventType,
    allowClear: true,
    placeholder: t('event.records.placeholder.selectEventType')
  },
  {
    valueKey: 'pushStatus',
    type: 'select-enum' as const,
    enumKey: EventPushStatus,
    allowClear: true,
    placeholder: t('event.records.placeholder.selectPushStatus')
  },
  {
    valueKey: 'createdDate',
    type: 'date-range' as const
  }
];

// Table columns configuration
const Columns = [
  {
    title: t('event.records.columns.id'),
    dataIndex: 'id',
    key: 'id',
    width: '13%'
  },
  {
    title: t('event.records.columns.receiverName'),
    dataIndex: 'fullName',
    groupName: 'user',
    key: 'receiverName',
    width: '13%'
  },
  {
    title: t('event.records.columns.receiverId'),
    dataIndex: 'userId',
    groupName: 'user',
    key: 'receiverId',
    hide: true,
    width: '13%'
  },
  {
    title: t('event.records.columns.eventCode'),
    dataIndex: 'code',
    key: 'eventCode',
    width: '8%',
    groupName: 'event',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('event.records.columns.eventName'),
    dataIndex: 'name',
    key: 'eventName',
    width: '8%',
    groupName: 'event',
    hide: true
  },
  {
    title: t('event.records.columns.eKey'),
    dataIndex: 'ekey',
    key: 'eKey',
    width: '10%',
    ellipsis: true,
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('event.records.columns.content'),
    dataIndex: 'description',
    key: 'description'
  },
  {
    title: t('event.records.columns.type'),
    dataIndex: 'type',
    key: 'type',
    width: '6%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('event.records.columns.pushStatus'),
    dataIndex: 'pushStatus',
    key: 'pushStatus',
    width: '8%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('event.records.columns.triggerTime'),
    dataIndex: 'createdDate',
    key: 'createdDate',
    width: '11%',
    sorter: {
      compare: (a, b) => a.createdDate > b.createdDate
    },
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('event.records.columns.operate'),
    dataIndex: 'action',
    key: 'action',
    width: 280,
    align: 'center'
  }
];

const showCount = ref(true);

const eventRecordList = ref<EventRecord[]>([]);
const params = reactive({
  pageNo: 1,
  pageSize: 10,
  orderSort: 'DESC',
  sortBy: 'createdDate',
  fullTextSearch: true,
  filters: [] as Record<string, string>[]
});

const total = ref(0);
const loading = ref(false);
const disabled = ref(false);
const getEventList = async () => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const [error, res] = await event.getEventList({ ...params });
  loading.value = false;
  if (error) {
    return;
  }

  eventRecordList.value = res.data.list;
  total.value = +res.data.total || 0;
};

const tableChange = async (_pagination, _filter, sorter) => {
  params.orderSort = sorter.orderSort;
  params.sortBy = sorter.sortBy;
  const { current, pageSize } = _pagination;
  params.pageNo = current;
  params.pageSize = pageSize;
  disabled.value = true;
  await getEventList();
  disabled.value = false;
};

const checkContentConfig: {
  dialogVisible: boolean
  content: string
} = reactive({
  dialogVisible: false,
  content: ''
});

const openCheckContentDialog = (item: string) => {
  checkContentConfig.dialogVisible = true;
  checkContentConfig.content = item;
};

const statusStyle: {
  [key: string]: string
} = {
  PENDING: 'warning',
  PUSHING: 'processing',
  PUSH_SUCCESS: 'success',
  PUSH_FAIL: 'error',
  IGNORED: 'default'
};

const getStatusStyle = (key: string): string => {
  return statusStyle[key];
};

const handleChange = async (data: Record<string, string>[]) => {
  params.pageNo = 1;
  params.filters = data;
  disabled.value = true;
  await getEventList();
  disabled.value = false;
};

const openReceiveConfig = (value) => {
  selectedItem.value = value;
  visible.value = true;
};

const pagination = computed(() => {
  return {
    current: params.pageNo,
    pageSize: params.pageSize,
    total: total.value
  };
});

onMounted(() => {
  getEventList();
});
</script>
<template>
  <div class="flex flex-col min-h-full">
    <!-- Event records description hint -->
    <Hints :text="t('event.records.messages.description')" class="mb-1" />
    <PureCard class="flex-1 p-3.5">
      <Statistics
        resource="Event"
        :barTitle="t('statistics.metrics.newEvents')"
        :router="GM"
        :visible="showCount" />
      <div class="flex items-center justify-between mb-2">
        <SearchPanel
          class="flex-1"
          :options="Options"
          @change="handleChange" />
        <div class="flex">
          <IconCount v-model:value="showCount" />
          <IconRefresh
            :loading="loading"
            :disabled="disabled"
            class="ml-2"
            @click="getEventList" />
        </div>
      </div>
      <Table
        size="small"
        :dataSource="eventRecordList"
        :columns="Columns"
        :pagination="pagination"
        :loading="loading"
        bodyCell="--"
        @change="tableChange">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'id'">
            <Button
              type="link"
              class="px-0 text-xs"
              @click="openCheckContentDialog(record.eventViewUrl)">
              {{ record.id }}
            </Button>
          </template>
          <template v-if="column.dataIndex === 'type'">
            {{ record.type.message }}
          </template>
          <template v-if="column.dataIndex === 'pushStatus'">
            <Badge
              :status="getStatusStyle(record.pushStatus.value)"
              :text="record.pushStatus.message">
            </Badge>
            <Popover
              :overlayStyle="{maxWidth:'500px'}">
              <Icon
                v-if="record.pushStatus?.value === 'PUSH_FAIL' "
                icon="icon-gantanhao-yuankuang"
                class="text-xs leading-3.5 text-theme-sub-content text-theme-text-hover ml-1" />
              <template #title>
                {{ t('event.records.messages.failureReason') }}
              </template>
              <template #content>
                <div class="text-xs max-h-100 max-w-150 overflow-auto" v-html=" DOMPurify.sanitize(record.pushMsg)"></div>
              </template>
            </Popover>
          </template>
          <template v-if="column.dataIndex ==='action'">
            <Button
              v-if="app.show('ReceivingChannelView')"
              type="text"
              class="text-xs"
              @click="openReceiveConfig(record)">
              <Icon icon="icon-jiekoudaili" class="mr-1" />
              {{ app.getName('ReceivingChannelView') }}
            </Button>
            <Button
              v-if="app.show('EventContentView')"
              type="text"
              class="text-xs"
              :disabled="!app.has('EventContentView')"
              @click="openCheckContentDialog(record.eventViewUrl)">
              <Icon icon="icon-shijianjilu" class="mr-1" />
              {{ app.getName('EventContentView') }}
            </Button>
            <!-- <Dropdown overlayClassName="table-oper-menu ant-dropdown-sm">
              <Icon icon="icon-gengduo" class="cursor-pointer outline-none" />
              <template #overlay>
                <Menu>
                  <MenuItem
                    v-if="app.show('ReceivingChannelView')"
                    :disabled="!app.has('ReceivingChannelView')"
                    @click="openReceiveConfig(record)">
                    <template #icon>
                      <Icon icon="icon-jiekoudaili" />
                    </template>
                    <a href="javascript:;" class="text-theme-text-hover">{{ app.getName('ReceivingChannelView') }}</a>
                  </MenuItem>
                  <MenuItem
                    v-if="app.show('EventContentView')"
                    :disabled="!app.has('EventContentView')"
                    @click="openCheckContentDialog(record.eventViewUrl)">
                    <template #icon>
                      <Icon icon="icon-shijianjilu" />
                    </template>
                    <a href="javascript:;" class="text-theme-text-hover">{{ app.getName('EventContentView') }}</a>
                  </MenuItem>
                </Menu>
              </template>
            </Dropdown> -->
          </template>
        </template>
      </Table>
    </PureCard>
    <AsyncComponent :visible="visible">
      <ReceiveConfig
        v-if="visible"
        v-model:visible="visible"
        :eventCode="selectedItem.eventCode"
        :eKey="selectedItem.eKey" />
    </AsyncComponent>
    <AsyncComponent :visible="checkContentConfig.dialogVisible">
      <ViewEvent v-model:visible="checkContentConfig.dialogVisible" :value="checkContentConfig.content" />
    </AsyncComponent>
  </div>
</template>
