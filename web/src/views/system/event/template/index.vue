<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, reactive, ref } from 'vue';
import { Button, Popover } from 'ant-design-vue';
import { AsyncComponent, Hints, Icon, IconRefresh, PureCard, SearchPanel, Table } from '@xcan-angus/vue-ui';
import { EnumMessage, app, enumUtils } from '@xcan-angus/infra';
import { useI18n } from 'vue-i18n';
import { appopen } from '@/api/index';

import { EventConfigList } from './PropsType';
import { event } from '@/api';

const ReceiveConfig = defineAsyncComponent(() => import('./components/receiveConfig.vue'));

const { t } = useI18n();
const visible = ref(false);
const selectedItem = ref<EventConfigList>({
  id: '',
  eKey: '',
  eventCode: '',
  eventName: '',
  eventType: undefined,
  allowedChannelTypes: [],
  pushSetting: [],
  bizKey: '',
  bigBizKey: ''
});
const Options = ref([
  {
    placeholder: t('placeholder.p1'),
    valueKey: 'eventName',
    type: 'input',
    allowClear: true
  },
  {
    valueKey: 'eventType',
    type: 'select-enum',
    op: 'EQUAL',
    enumKey: 'EventType',
    allowClear: true,
    placeholder: t('placeholder.p2')
  },
  {
    valueKey: 'appCode',
    type: 'select',
    placeholder: '选择应用',
    options: [],
    fieldNames: {
      value: 'appCode',
      label: 'appShowName'
    }
  }
]);

const Columns = [
  {
    title: t('事件名称'),
    dataIndex: 'eventName',
    width: '12%'
  },
  {
    title: t('事件编码'),
    dataIndex: 'eventCode',
    width: '12%'
  },
  {
    title: t('type'),
    dataIndex: 'eventType',
    key: 'eventType',
    customRender: ({ text }): string => text?.message,
    width: '8%'
  },
  {
    title: '分类',
    dataIndex: 'targetType'
  },
  {
    title: '应用',
    dataIndex: 'appCode'
  },
  {
    title: t('table-push-method'),
    dataIndex: 'allowedChannelTypes',
    key: 'allowedChannelTypes',
    width: '15%'
  },
  {
    title: t('operate'),
    key: 'operate',
    dataIndex: 'operate',
    width: '8%'
  }
];

const eventConfigList = ref<EventConfigList[]>([]);

const params: { pageNo: number, pageSize: number, filters: Record<string, string>[] } = reactive({
  pageNo: 1,
  pageSize: 10,
  filters: [],
  fullTextSearch: true
});

const total = ref(0);

const query = async (data: Record<string, string>[]) => {
  params.pageNo = 1;
  params.filters = data;
  disabled.value = true;
  await getEventTemplate();
  disabled.value = false;
};

const loading = ref(false);
const disabled = ref(false);
const targetTypeEnums = ref<EnumMessage<string>[]>([]);
const appList = ref<{ appCode: string; appName: string; appShowName: string }[]>([]);
const appLoaded = ref(false);

const loadEnums = async () => {
  targetTypeEnums.value = enumUtils.enumToMessages('CombinedTargetType');
};

const loadAppOptions = async (pageSize = 10) => {
  const [, { data = { list: [] } }] = await appopen.getList({ pageSize });
  appList.value = data.list || [];
  if (+data.total > appList.value.length) {
    await loadAppOptions(+data.total);
    return;
  }
  Options.value[2].options = appList.value;
  appLoaded.value = true;
};

const getTargetTypeName = (value: string) => {
  const target = targetTypeEnums.value.find(i => i.value === value);
  return target?.message || '公共';
};

const getAppName = (value: string) => {
  const target = appList.value.find(i => i.appCode === value);
  return target?.appShowName || ' ';
};

const getEventTemplate = async () => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const [error, res] = await event.getTemplateList(params);
  loading.value = false;
  if (error) {
    return;
  }

  eventConfigList.value = res.data.list;
  total.value = Number(res.data.total) || 0;
};

const openReceiveConfig = (value) => {
  selectedItem.value = value;
  visible.value = true;
};

const changePagination = async (_pagination) => {
  const { current, pageSize } = _pagination;
  params.pageNo = current;
  params.pageSize = pageSize;
  disabled.value = true;
  await getEventTemplate();
  disabled.value = false;
};

const pagination = computed(() => {
  return {
    current: params.pageNo,
    pageSize: params.pageSize,
    total: total.value
  };
});

onMounted(async () => {
  await loadAppOptions();
  await loadEnums();
  getEventTemplate();
});
</script>
<template>
  <div class="flex flex-col min-h-full">
    <Hints :text="t('eventTip1')" class="mb-1" />
    <PureCard class="flex-1 p-3.5">
      <div class="flex justify-between items-center mb-2">
        <SearchPanel
          v-if="appLoaded"
          :options="Options"
          @change="query" />
        <IconRefresh
          :loading="loading"
          :disabled="disabled"
          class="ml-2"
          @click="getEventTemplate" />
      </div>
      <Table
        :loading="loading"
        :dataSource="eventConfigList"
        :columns="Columns"
        :pagination="pagination"
        rowKey="id"
        size="small"
        @change="changePagination">
        <template #bodyCell="{ column,text, record }">
          <template v-if="column.dataIndex === 'eventCode' && text.length <= 15">
            {{ text }}
          </template>
          <template v-if="column.dataIndex === 'eventCode' && text.length > 15">
            <Popover
              :title="null"
              :overlayStyle="{width:'380px'}"
              placement="bottomLeft"
              :mouseLeaveDelay="0">
              {{ text.slice(0,15)+'...' }}
              <template #content>
                {{ text }}
              </template>
            </Popover>
          </template>
          <template v-if="column.dataIndex === 'eventName' && text.length <= 15">
            {{ text }}
          </template>
          <template v-if="column.dataIndex === 'eventName' && text.length > 15">
            <Popover
              :title="null"
              :overlayStyle="{width:'380px'}"
              placement="bottomLeft"
              :mouseLeaveDelay="0">
              {{ text.slice(0,15)+'...' }}
              <template #content>
                {{ text }}
              </template>
            </Popover>
          </template>
          <template v-if="column.key === 'eventType'">
            {{ text?.message }}
          </template>
          <template v-if="column.key === 'allowedChannelTypes'">
            {{ record.allowedChannelTypes?.map(type => type.message).join('，') }}
          </template>
          <template v-if="column.dataIndex === 'targetType'">
            {{ getTargetTypeName(record.targetType) }}
          </template>
          <template v-if="column.dataIndex === 'appCode'">
            {{ getAppName(record.appCode) }}
          </template>
          <template v-if="column.key ==='operate'">
            <Button
              v-if="app.show('ReceiveChannelConfigure')"
              :disabled="!app.has('ReceiveChannelConfigure')"
              size="small"
              type="text"
              class="px-0"
              @click="openReceiveConfig(record)">
              <template #icon>
                <Icon icon="icon-shezhi" class="mr-1" />
              </template>
              <a class="text-theme-text-hover">{{ app.getName('ReceiveChannelConfigure') }}</a>
            </Button>
          </template>
        </template>
      </Table>
    </PureCard>
    <AsyncComponent :visible="visible">
      <ReceiveConfig
        :id="selectedItem.id"
        v-model:visible="visible" />
    </AsyncComponent>
  </div>
</template>
