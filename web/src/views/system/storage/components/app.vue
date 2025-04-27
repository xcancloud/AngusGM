<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { http } from '@xcan/utils';
import { Card, Grid, Hints, Select } from '@xcan/design';
import { PUB_GM } from '@xcan/sdk';

import { service as serviceApi } from '@/api';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const serviceOptions = ref<string[]>([]);
const instancesOptions = ref<string[]>([]);
const service = ref(undefined);
const instances = ref(undefined);

const init = () => {
  loadSeriveList();
};

const loadSeriveList = async () => {
  const [error, { data = [] }] = await serviceApi.getDiscoveryServices();
  if (error && !data?.length) {
    return;
  }
  service.value = data[0];
  serviceOptions.value = data.map((item: string) => ({
    label: item,
    value: item
  }));
  loadInstancesList();
};

const serviceChange = (value: string) => {
  if (value) {
    loadInstancesList();
  } else {
    instances.value = undefined;
  }
};

const loadInstancesList = async () => {
  if (!service.value) {
    return;
  }

  const [error, { data = [] }] = await serviceApi.getServiceInstances(service.value);
  if (error && !data?.length) {
    return;
  }
  instances.value = data[0];

  loadApp(getUrl(data[0]));
  instancesOptions.value = data.map((item: string) => ({
    label: item,
    value: item
  }));
};

const getUrl = (value) => {
  const PATH = 'actuator/appworkspace';                // TODO 移动到 api
  return `${PUB_GM}/proxy/${PATH}?targetAddr=http://${value}`;
};

const instancesChange = (value: string) => {
  loadApp(getUrl(value));
};

const columns = [
  [
    { dataIndex: 'homeDir', label: t('storage3') },
    { dataIndex: 'workDir', label: t('storage4') },
    { dataIndex: 'dataDir', label: t('storage5') },
    { dataIndex: 'logsDir', label: t('storage6') },
    { dataIndex: 'tmpDir', label: t('storage7') },
    { dataIndex: 'confDir', label: t('storage8') }
  ]
];

const dataSource = ref();

const loadApp = async (address: string) => {
  const [error, res] = await http.get(address);
  if (error) {
    return;
  }
  dataSource.value = res.data;
};

onMounted(() => {
  init();
});

</script>
<template>
  <Card bodyClass="px-8 py-5">
    <template #title>
      <div class="text-theme-title flex items-center py-1">
        {{ t('storage1') }}
        <Hints
          :text="t('storage2')"
          class="ml-2"
          style="transform: translateY(1px);" />
      </div>
    </template>
    <template #default>
      <div class="mb-5 text-3 leading-3">
        <span>{{ t('service') }}</span>
        <em class="not-italic mr-1">:</em>
        <Select
          v-model:value="service"
          :options="serviceOptions"
          defaultActiveFirstOption
          class="w-60 mr-5"
          size="small"
          @change="serviceChange" />
        <span>{{ t('example') }}</span><em class="not-italic mr-1">:</em>
        <Select
          v-model:value="instances"
          :disabled="!service"
          :options="instancesOptions"
          class="w-60 mr-5"
          size="small"
          @change="instancesChange" />
      </div>
      <Grid
        :columns="columns"
        :dataSource="dataSource"
        style="width: 80%;"
        class="grid-row" />
    </template>
  </Card>
</template>
<style scoped>
:deep(.grid-row > div > div >:first-child) {
  padding: 8px;
  padding-left: 0;
}

:deep(.grid-row > div > div >:last-child) {
  padding: 6px;
  border-radius: 4px;
  background-color: var(--bg-theme-disabled);
}
</style>
