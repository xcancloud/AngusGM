<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue';
import { Colon, Icon, PureCard, Select } from '@xcan/design';
import { useI18n } from 'vue-i18n';
import { Button, Radio, RadioGroup, Switch } from 'ant-design-vue';
import dayjs from 'dayjs';

import {service, systemLog} from '@/api';

const { t } = useI18n();
const serviceOptions = ref<{ label: string, value: string }[]>([]);
const instancesOptions = ref<{ label: string, value: string }[]>([]);
const logOptions = ref<{ label: string, value: string }[]>([]);
const serviceId = ref<string>();
const instances = ref<string>();
const logFile = ref<string>();
const browse = ref('50');
const logContent = ref('');
let timer: NodeJS.Timer | null = null;

const init = () => {
  loadDiscoveryServices();
};

const loadDiscoveryServices = async () => {
  const [error, { data = [] }] = await service.getDiscoveryServices();
  if (error && !data?.length) {
    return;
  }
  serviceId.value = data[0];
  serviceOptions.value = data.map((item: string) => ({
    label: item,
    value: item
  }));
  loadServiceInstances();
};

const loadServiceInstances = async () => {
  if (!serviceId.value) {
    return;
  }
  const [error, { data = [] }] = await service.getServiceInstances(serviceId.value);
  if (error && !data?.length) {
    return;
  }
  instancesOptions.value = data.map((item: string) => ({
    value: item,
    label: item
  }));
  instances.value = data[0];
  loadLogFile();
};

const loadLogFile = async () => {
  const [error, { data = [] }] = await systemLog.getInstanceLogFiles(instances.value as string);
  if (error) {
    return;
  }

  logOptions.value = data.map((item: string) => ({
    value: item,
    label: item
  }));

  if (logFile.value && data.includes(logFile.value)) {
    loadLogContent();
    return;
  }
  logFile.value = data[0];
  loadLogContent();
};

const serviceChange = (value: string) => {
  if (value) {
    loadServiceInstances();
  } else {
    instances.value = undefined;
  }
};

const instancesChange = (value: string) => {
  if (value) {
    loadLogFile();
  } else {
    logFile.value = undefined;
    logContent.value = '';
  }
};

const logFileChange = (value: string) => {
  if (value) {
    loadLogContent();
  } else {
    autoRefresh.value = false;
    logFile.value = undefined;
    if (timer) clearTimeout(timer);
  }
};

const loadLogContent = async () => {
  const [error, { data }] = await systemLog.getInstanceLogFile(instances.value as string, logFile.value as string, {
    linesNum: browse.value,
    tail: browseOptions.find(item => item.value === browse.value)?.type === 0
  });
  if (error) {
    return;
  }

  logContent.value = data;
};

const autoRefresh = ref(false);
const autoRefreshLoading = ref(false);

const fullScreen = ref(false);
const autoRefreshChange = (value: boolean) => {
  if (value) {
    openTimeout();
  } else {
    if (timer) {
      clearTimeout(timer);
    }
  }
};

const openTimeout = () => {
  if (timer) {
    clearTimeout(timer);
  }
  timer = setTimeout(async () => {
    await loadLogContent();
    openTimeout();
  }, 2000);
};

const browseOptions = [
  {
    value: '50',
    label: `${t('mess-2')}50${t('mess-3')}`,
    type: 0
  },
  {
    value: '500',
    label: `${t('mess-2')}500${t('mess-3')}`,
    type: 0
  },
  {
    value: '1000',
    label: `${t('mess-2')}1000${t('mess-3')}`,
    type: 0
  },
  {
    value: '10000',
    label: `${t('mess-2')}10000${t('mess-3')}`,
    type: 0
  }
];

const fullScreenEvent = function (value: boolean) {
  fullScreen.value = value;
};

const saveToFile = () => {
  const blob = new Blob([logContent.value], { type: 'text/plain;charset=utf-8' });
  const a = document.createElement('a');
  a.download = `${serviceOptions.value.filter(item => item.value === serviceId.value)[0].label}(${browse.value})-${dayjs().format('YYYY/MM/DD HH/mm/ss')}.log`;
  a.href = URL.createObjectURL(blob);
  a.click();
  URL.revokeObjectURL(a.href);
};

const lineChange = () => {
  loadLogContent();
};

onMounted(() => {
  init();
});

onBeforeUnmount(() => {
  if (timer) {
    clearTimeout(timer);
  }
});
</script>
<template>
  <teleport to="body">
    <div v-if="fullScreen" class="absolute top-0 left-0 right-0 bottom-0 z-9999 bg-black-log">
      <div class="relative w-full h-full overflow-auto box-border px-2 py-1 text-3 leading-3">
        <div
          class="flex items-center cursor-pointer hover:opacity-80 fixed right-10 top-4 text-white"
          @click="fullScreenEvent(false)">
          <Icon icon="icon-quanping" class="mr-1" />
          <span>{{ t('mess-6') }}</span>
        </div>
        <div
          class="bg-black-log text-3 font-normal whitespace-pre-wrap p-3.5 leading-4 flex-1 overflow-x-hidden overflow-y-auto my-3.5"
          style="scrollbar-gutter: stable;color: #f2f2f2;font-family: Monaco, Consolas, monospace !important;">
          {{ logContent }}
        </div>
        <span id="scrollInto-big"></span>
      </div>
    </div>
  </teleport>
  <PureCard class="h-full p-3.5 flex flex-col">
    <div class="flex items-center text-3">
      {{ t('service') }}
      <Select
        v-model:value="serviceId"
        :options="serviceOptions"
        class="w-60 ml-3 mr-5"
        placeholder="选择服务"
        size="small"
        @change="serviceChange" />
      {{ t('example') }}
      <Select
        v-model:value="instances"
        :disabled="!serviceId"
        :options="instancesOptions"
        class="w-60 ml-3 mr-5"
        placeholder="选择实例"
        size="small"
        @change="instancesChange" />
      {{ t('logFile') }}
      <Select
        v-model:value="logFile"
        :disabled="!instances"
        :options="logOptions"
        class="w-60 ml-3"
        placeholder="选择日志文件"
        size="small"
        @change="logFileChange" />
    </div>
    <div class="mt-3.5 flex item-center justify-between text-3 leading-3">
      <div>
        <span class="text-theme-content">{{ t('browseLogs') }}<Colon /></span>&nbsp;
        <RadioGroup
          v-model:value="browse"
          :disabled="!logFile"
          @change="lineChange">
          <Radio
            v-for="item in browseOptions"
            :key="item.value"
            :value="item.value"
            class="text-3 leading-3">
            {{ item.label }}
          </Radio>
        </RadioGroup>
      </div>
      <div class="flex items-center">
        <Button
          size="small"
          class="mr-6"
          @click="saveToFile">
          保存到文件
        </Button>
        <div class="mr-6 flex items-center">
          <span class="text-theme-content">{{ t('mess-b') }}<Colon /></span>
          <Switch
            v-model:checked="autoRefresh"
            :disabled="!logFile"
            :loading="autoRefreshLoading"
            size="small"
            @change="autoRefreshChange" />
        </div>
        <div class="flex items-center cursor-pointer hover:opacity-80 " @click="fullScreenEvent(true)">
          <Icon icon="icon-quanping" class="mr-1" />
          <span class="text-theme-content">{{ t('mess-d') }}</span>
        </div>
      </div>
    </div>
    <div
      class="bg-black-log text-3 font-normal whitespace-pre-wrap p-3.5 leading-4 flex-1 overflow-x-hidden overflow-y-auto my-3.5"
      style="scrollbar-gutter: stable;color: #f2f2f2;font-family: Monaco, Consolas, monospace !important;">
      {{ logContent }}
    </div>
  </PureCard>
</template>
