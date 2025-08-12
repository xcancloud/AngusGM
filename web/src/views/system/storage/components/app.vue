<script setup lang="ts">
import { onMounted, ref, computed } from 'vue';
import { Card, Grid, Hints, Select } from '@xcan-angus/vue-ui';
import { pubProxy, service as serviceApi } from '@/api';
import { useI18n } from 'vue-i18n';
import { ServiceOption, AppStorageData, StorageColumn } from '../PropsType';

const { t } = useI18n();

// Reactive data for service and instance selection
const serviceOptions = ref<ServiceOption[]>([]);
const instancesOptions = ref<ServiceOption[]>([]);
const service = ref<string>();
const instances = ref<string>();
const dataSource = ref<AppStorageData>();

// Computed properties for better performance
const hasService = computed(() => !!service.value);

/**
 * Initialize component data
 * Loads service list and sets default values
 */
const init = async () => {
  await loadServiceList();
};

/**
 * Load available services from API
 * Sets the first service as default and loads its instances
 */
const loadServiceList = async () => {
  try {
    const [error, { data = [] }] = await serviceApi.getDiscoveryServices();
    if (error || !data?.length) {
      return;
    }

    // Set first service as default
    service.value = data[0];
    serviceOptions.value = data.map((item: string) => ({
      label: item,
      value: item
    }));

    // Load instances for the selected service
    await loadInstancesList();
  } catch (error) {
    console.error('Failed to load service list:', error);
  }
};

/**
 * Handle service selection change
 * @param value - Selected service value
 */
const serviceChange = async (value: any) => {
  if (value) {
    await loadInstancesList();
  } else {
    // Reset instances when service is cleared
    instances.value = undefined;
    dataSource.value = undefined;
  }
};

/**
 * Load instances for the selected service
 * Sets the first instance as default and loads app storage data
 */
const loadInstancesList = async () => {
  if (!service.value) {
    return;
  }

  try {
    const [error, { data = [] }] = await serviceApi.getServiceInstances(service.value);
    if (error || !data?.length) {
      return;
    }

    // Set first instance as default
    instances.value = data[0];
    instancesOptions.value = data.map((item: string) => ({
      label: item,
      value: item
    }));

    // Load app storage data for the selected instance
    await loadApp(data[0]);
  } catch (error) {
    console.error('Failed to load instances list:', error);
  }
};

/**
 * Handle instance selection change
 * @param value - Selected instance value
 */
const instancesChange = async (value: any) => {
  await loadApp(value);
};

/**
 * Load app storage data for the specified instance address
 * @param address - Instance address to load data from
 */
const loadApp = async (address: string) => {
  try {
    const [error, res] = await pubProxy.getApp(address);
    if (error) {
      return;
    }
    dataSource.value = res.data;
  } catch (error) {
    console.error('Failed to load app storage data:', error);
  }
};

// Table columns configuration for storage paths display
const columns = computed<StorageColumn[][]>(() => [
  [
    { dataIndex: 'homeDir', label: t('storage.columns.homeDir') },
    { dataIndex: 'workDir', label: t('storage.columns.workDir') },
    { dataIndex: 'dataDir', label: t('storage.columns.dataDir') },
    { dataIndex: 'logsDir', label: t('storage.columns.logsDir') },
    { dataIndex: 'tmpDir', label: t('storage.columns.tmpDir') },
    { dataIndex: 'confDir', label: t('storage.columns.confDir') }
  ]
]);

// Lifecycle hook - initialize component on mount
onMounted(() => {
  init();
});
</script>

<template>
  <Card bodyClass="px-8 py-5">
    <!-- Card title with storage information hint -->
    <template #title>
      <div class="text-theme-title flex items-center py-1">
        {{ t('storage.messages.appStorageTitle') }}
        <Hints
          :text="t('storage.messages.appStorageDesc')"
          class="ml-2"
          style="transform: translateY(1px);" />
      </div>
    </template>

    <template #default>
      <!-- Service and instance selection controls -->
      <div class="mb-5 text-3 leading-3">
        <span>{{ t('storage.messages.service') }}</span>
        <em class="not-italic mr-1">:</em>
        <Select
          v-model:value="service"
          :options="serviceOptions"
          defaultActiveFirstOption
          class="w-60 mr-5"
          size="small"
          @change="serviceChange" />

        <span>{{ t('storage.messages.instance') }}</span>
        <em class="not-italic mr-1">:</em>
        <Select
          v-model:value="instances"
          :disabled="!hasService"
          :options="instancesOptions"
          class="w-60 mr-5"
          size="small"
          @change="instancesChange" />
      </div>

      <!-- Storage paths data grid -->
      <Grid
        :columns="columns"
        :dataSource="dataSource"
        style="width: 80%;"
        class="grid-row" />
    </template>
  </Card>
</template>

<style scoped>
/* Custom styling for grid rows */
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
