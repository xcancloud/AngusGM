<script setup lang="ts">
import { onMounted, reactive, ref, computed, defineAsyncComponent } from 'vue';
import { useI18n } from 'vue-i18n';
import { Badge, Popover, Button } from 'ant-design-vue';
import { Hints, SearchPanel, PureCard, Icon, Table, AsyncComponent, IconCount, IconRefresh } from '@xcan-angus/vue-ui';
import { app, GM } from '@xcan-angus/tools';
import DOMPurify from 'dompurify';

import { event } from '@/api';
import { EventRecord } from './PropsType';

const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));
const ReceiveConfig = defineAsyncComponent(() => import('./components/receiveConfig.vue'));
const ViewEvent = defineAsyncComponent(() => import('./components/view.vue'));
const { t } = useI18n();
const visible = ref(false);
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
const Options = [
  {
    placeholder: t('placeholder.p1'),
    valueKey: 'description',
    type: 'input',
    op: 'MATCH',
    allowClear: true
  },
  {
    valueKey: 'userId',
    type: 'select-user',
    allowClear: true,
    placeholder: '请选择接收人',
    showSearch: true
  },
  {
    valueKey: 'type',
    type: 'select-enum',
    enumKey: 'EventType',
    allowClear: true,
    placeholder: t('placeholder.p2')
  },
  {
    valueKey: 'pushStatus',
    type: 'select-enum',
    enumKey: 'EventPushStatus',
    allowClear: true,
    placeholder: t('placeholder.p3')
  },
  {
    valueKey: 'createdDate',
    type: 'date-range'
  }
];

const Columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: '13%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('接收人名称'),
    dataIndex: 'fullName',
    groupName: 'user',
    width: '13%'
  },
  {
    title: t('接收人ID'),
    dataIndex: 'userId',
    groupName: 'user',
    hide: true,
    width: '13%'
  },
  {
    title: t('事件编码'),
    dataIndex: 'code',
    key: 'eventCode',
    width: '8%',
    groupName: 'event',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('事件名称'),
    dataIndex: 'name',
    key: 'eventName',
    width: '8%',
    groupName: 'event',
    hide: true
  },
  {
    title: t('eKey'),
    dataIndex: 'ekey',
    key: 'ekey',
    width: '10%',
    ellipsis: true,
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('内容'),
    dataIndex: 'description',
    key: 'description'
  },
  {
    title: t('type'),
    dataIndex: 'type',
    key: 'type',
    width: '6%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('push-status'),
    dataIndex: 'pushStatus',
    key: 'pushStatus',
    width: '8%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('table-triggerTime'),
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
    title: t('table-operate'),
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
  filters: [] as Record<string, string>[],
  orderSort: 'DESC',
  sortBy: 'createdDate'
});

const total = ref(0);
const loading = ref(false);
const disabled = ref(false);
const getEventList = async () => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const [error, res] = await event.searchEventList({ ...params });
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

const obj: {
  [key: string]: string
} = {
  UN_PUSH: 'default',
  PUSHING: 'processing',
  PUSH_SUCCESS: 'success',
  PUSH_FAIL: 'error'
};

const getStatus = (key: string): string => {
  return obj[key];
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
    <Hints :text="t('eventLogTip')" class="mb-1" />
    <PureCard class="flex-1 p-3.5">
      <Statistics
        :visible="showCount"
        resource="Event"
        :barTitle="t('事件')"
        :geteway="GM" />
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
              size="small"
              class="px-0"
              @click="openCheckContentDialog(record.eventViewUrl)">
              {{ record.id }}
            </Button>
          </template>
          <template v-if="column.dataIndex === 'type'">
            {{ record.type.message }}
          </template>
          <template v-if="column.dataIndex === 'pushStatus'">
            <Badge
              :status="getStatus(record.pushStatus.value)"
              :text="record.pushStatus.message">
            </Badge>
            <Popover
              :overlayStyle="{maxWidth:'500px'}">
              <Icon
                v-if="record.pushStatus?.value === 'PUSH_FAIL' "
                icon="icon-gantanhao-yuankuang"
                class="text-3.5 leading-3.5 text-theme-sub-content text-theme-text-hover ml-1" />
              <template #title>
                {{ t('errMsg') }}
              </template>
              <template #content>
                <div class="max-h-100 max-w-150 overflow-auto" v-html=" DOMPurify.sanitize(record.pushMsg)"></div>
              </template>
            </Popover>
          </template>
          <template v-if="column.dataIndex ==='action'">
            <Button
              v-if="app.show('ReceivingChannelView')"
              type="text"
              size="small"
              :disabled="!app.has('ReceivingChannelView')"
              @click="openReceiveConfig(record)">
              <Icon icon="icon-jiekoudaili" class="mr-1" />
              {{ app.getName('ReceivingChannelView') }}
            </Button>
            <Button
              v-if="app.show('EventContentView')"
              type="text"
              size="small"
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
        :ekey="selectedItem.ekey" />
    </AsyncComponent>
    <AsyncComponent :visible="checkContentConfig.dialogVisible">
      <ViewEvent v-model:visible="checkContentConfig.dialogVisible" :value="checkContentConfig.content" />
    </AsyncComponent>
  </div>
</template>
