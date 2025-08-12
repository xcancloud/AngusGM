<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, computed, watch } from 'vue';
import { Colon, Icon, PureCard, Select } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import { Button, Radio, RadioGroup, Switch } from 'ant-design-vue';
import dayjs from 'dayjs';

import { service, systemLog } from '@/api';

const { t } = useI18n();

// Reactive data for service selection and log viewing
const serviceOptions = ref<{ label: string, value: string }[]>([]);
const instancesOptions = ref<{ label: string, value: string }[]>([]);
const logOptions = ref<{ label: string, value: string }[]>([]);
const serviceId = ref<string>();
const instances = ref<string>();
const logFile = ref<string>();
const browse = ref('50');
const logContent = ref('');

// Auto-refresh and fullscreen state management
const autoRefresh = ref(false);
const autoRefreshLoading = ref(false);
const fullScreen = ref(false);

// Timer for auto-refresh functionality
let timer: ReturnType<typeof setTimeout> | null = null;

// Computed properties for better performance and readability
const currentServiceLabel = computed(() => {
  return serviceOptions.value.find(item => item.value === serviceId.value)?.label || '';
});

const currentBrowseOption = computed(() => {
  return browseOptions.find(item => item.value === browse.value);
});

// Browse options configuration for log line selection
const browseOptions = [
  { value: '50', label: t('log.system.messages.browseLines', { lines: '50' }), type: 0 },
  { value: '500', label: t('log.system.messages.browseLines', { lines: '500' }), type: 0 },
  { value: '1000', label: t('log.system.messages.browseLines', { lines: '1000' }), type: 0 },
  { value: '10000', label: t('log.system.messages.browseLines', { lines: '10000' }), type: 0 }
];

/**
 * Initialize the component by loading discovery services
 */
const init = async () => {
  await loadDiscoveryServices();
};

/**
 * Load available discovery services from the API
 * Sets the first service as default and loads its instances
 */
const loadDiscoveryServices = async () => {
  try {
    const [error, { data = [] }] = await service.getDiscoveryServices();
    if (error || !data?.length) {
      return;
    }
    
    serviceId.value = data[0];
    serviceOptions.value = data.map((item: string) => ({
      label: item,
      value: item
    }));
    
    await loadServiceInstances();
  } catch (error) {
    console.error('Failed to load discovery services:', error);
  }
};

/**
 * Load service instances for the selected service
 * Sets the first instance as default and loads available log files
 */
const loadServiceInstances = async () => {
  if (!serviceId.value) return;
  
  try {
    const [error, { data = [] }] = await service.getServiceInstances(serviceId.value);
    if (error || !data?.length) {
      return;
    }
    
    instancesOptions.value = data.map((item: string) => ({
      value: item,
      label: item
    }));
    
    instances.value = data[0];
    await loadLogFile();
  } catch (error) {
    console.error('Failed to load service instances:', error);
  }
};

/**
 * Load available log files for the selected instance
 * Sets the first log file as default and loads its content
 */
const loadLogFile = async () => {
  if (!instances.value) return;
  
  try {
    const [error, { data = [] }] = await systemLog.getInstanceLogFiles(instances.value);
    if (error) return;

    logOptions.value = data.map((item: string) => ({
      value: item,
      label: item
    }));

    // Keep current log file if it's still available
    if (logFile.value && data.includes(logFile.value)) {
      await loadLogContent();
      return;
    }
    
    logFile.value = data[0];
    await loadLogContent();
  } catch (error) {
    console.error('Failed to load log files:', error);
  }
};

/**
 * Load log content based on current instance, log file, and browse settings
 */
const loadLogContent = async () => {
  if (!instances.value || !logFile.value) return;
  
  try {
    const [error, { data }] = await systemLog.getInstanceLogFile(
      instances.value, 
      logFile.value, 
      {
        linesNum: browse.value,
        tail: currentBrowseOption.value?.type === 0
      }
    );
    
    if (error) return;
    logContent.value = data;
  } catch (error) {
    console.error('Failed to load log content:', error);
  }
};

/**
 * Handle service selection change
 * Resets dependent selections and loads new instances
 */
const serviceChange = (value: any) => {
  if (value) {
    instances.value = undefined;
    logFile.value = undefined;
    logContent.value = '';
    loadServiceInstances();
  }
};

/**
 * Handle instance selection change
 * Resets log file selection and loads available log files
 */
const instancesChange = (value: any) => {
  if (value) {
    logFile.value = undefined;
    logContent.value = '';
    loadLogFile();
  }
};

/**
 * Handle log file selection change
 * Loads log content and stops auto-refresh if no file selected
 */
const logFileChange = (value: any) => {
  if (value) {
    loadLogContent();
  } else {
    autoRefresh.value = false;
    logFile.value = undefined;
    if (timer) clearTimeout(timer);
  }
};

/**
 * Handle auto-refresh toggle
 * Starts or stops the refresh timer based on user selection
 */
const autoRefreshChange = (value: boolean) => {
  if (value) {
    startAutoRefresh();
  } else {
    stopAutoRefresh();
  }
};

/**
 * Start auto-refresh timer with 2-second interval
 */
const startAutoRefresh = () => {
  stopAutoRefresh(); // Clear existing timer first
  
  timer = setTimeout(async () => {
    await loadLogContent();
    startAutoRefresh(); // Recursive call for continuous refresh
  }, 2000);
};

/**
 * Stop auto-refresh timer and clear it
 */
const stopAutoRefresh = () => {
  if (timer) {
    clearTimeout(timer);
    timer = null;
  }
};

/**
 * Toggle fullscreen mode for log viewing
 */
const toggleFullScreen = (value: boolean) => {
  fullScreen.value = value;
};

/**
 * Export log content to a downloadable file
 * Creates a blob with timestamp and service information in filename
 */
const saveToFile = () => {
  if (!logContent.value) return;
  
  const timestamp = dayjs().format('YYYY/MM/DD HH/mm/ss');
  const filename = `${currentServiceLabel.value}(${browse.value})-${timestamp}.log`;
  
  const blob = new Blob([logContent.value], { type: 'text/plain;charset=utf-8' });
  const url = URL.createObjectURL(blob);
  
  const link = document.createElement('a');
  link.download = filename;
  link.href = url;
  link.click();
  
  // Clean up the created URL object
  URL.revokeObjectURL(url);
};

/**
 * Handle browse line count change
 * Reloads log content with new line count
 */
const handleBrowseChange = () => {
  loadLogContent();
};

// Watch for changes in browse value to reload content
watch(browse, handleBrowseChange);

// Lifecycle hooks
onMounted(() => {
  init();
});

onBeforeUnmount(() => {
  stopAutoRefresh();
});
</script>

<template>
  <!-- Fullscreen log viewer overlay -->
  <teleport to="body">
    <div v-if="fullScreen" class="absolute top-0 left-0 right-0 bottom-0 z-9999 bg-black-log">
      <div class="relative w-full h-full overflow-auto box-border px-2 py-1 text-3 leading-3">
        <!-- Close fullscreen button -->
        <div
          class="flex items-center cursor-pointer hover:opacity-80 fixed right-10 top-4 text-white"
          @click="toggleFullScreen(false)">
          <Icon icon="icon-quanping" class="mr-1" />
          <span>{{ t('log.system.messages.close') }}</span>
        </div>
        
        <!-- Fullscreen log content -->
        <div
          class="bg-black-log text-3 font-normal whitespace-pre-wrap p-3.5 leading-4 flex-1 overflow-x-hidden overflow-y-auto my-3.5"
          style="scrollbar-gutter: stable;color: #f2f2f2;font-family: Monaco, Consolas, monospace !important;">
          {{ logContent }}
        </div>
        <span id="scrollInto-big"></span>
      </div>
    </div>
  </teleport>

  <!-- Main log viewer interface -->
  <PureCard class="h-full p-3.5 flex flex-col">
    <!-- Service, instance, and log file selection controls -->
    <div class="flex items-center text-3">
      {{ t('log.system.labels.service') }}
      <Select
        v-model:value="serviceId"
        :options="serviceOptions"
        class="w-60 ml-3 mr-5"
        :placeholder="t('log.system.placeholder.selectService')"
        size="small"
        @change="serviceChange" />
      
      {{ t('log.system.labels.instance') }}
      <Select
        v-model:value="instances"
        :disabled="!serviceId"
        :options="instancesOptions"
        class="w-60 ml-3 mr-5"
        :placeholder="t('log.system.placeholder.selectInstance')"
        size="small"
        @change="instancesChange" />
      
      {{ t('log.system.labels.logFile') }}
      <Select
        v-model:value="logFile"
        :disabled="!instances"
        :options="logOptions"
        class="w-60 ml-3"
        :placeholder="t('log.system.placeholder.selectLogFile')"
        size="small"
        @change="logFileChange" />
    </div>

    <!-- Log viewing controls and actions -->
    <div class="mt-3.5 flex item-center justify-between text-3 leading-3">
      <!-- Browse line count selection -->
      <div>
        <span class="text-theme-content">{{ t('log.system.labels.browseLogs') }}<Colon /></span>&nbsp;
        <RadioGroup
          v-model:value="browse"
          :disabled="!logFile"
          @change="handleBrowseChange">
          <Radio
            v-for="item in browseOptions"
            :key="item.value"
            :value="item.value"
            class="text-3 leading-3">
            {{ item.label }}
          </Radio>
        </RadioGroup>
      </div>

      <!-- Action buttons and controls -->
      <div class="flex items-center">
        <!-- Auto-refresh toggle -->
        <div class="mr-6 flex items-center">
          <span class="text-theme-content m-2">{{ t('log.system.labels.autoRefresh') }}</span>
          <Switch
            v-model:checked="autoRefresh"
            :disabled="!logFile"
            :loading="autoRefreshLoading"
            size="small"
            @change="autoRefreshChange" />
        </div>

        <!-- Save to file button -->
        <Button
          size="small"
          class="mr-6"
          :disabled="!logContent"
          @click="saveToFile">
          {{ t('log.system.labels.saveToFile') }}
        </Button>

        <!-- Fullscreen toggle -->
        <div class="flex items-center cursor-pointer hover:opacity-80" @click="toggleFullScreen(true)">
          <Icon icon="icon-quanping" class="mr-1" />
          <span class="text-theme-content">{{ t('log.system.labels.fullScreen') }}</span>
        </div>
      </div>
    </div>

    <!-- Log content display area -->
    <div
      class="bg-black-log text-3 font-normal whitespace-pre-wrap p-3.5 leading-4 flex-1 overflow-x-hidden overflow-y-auto my-3.5"
      style="scrollbar-gutter: stable;color: #f2f2f2;font-family: Monaco, Consolas, monospace !important;">
      {{ logContent }}
    </div>
  </PureCard>
</template>
