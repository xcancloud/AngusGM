<script setup lang="ts">
import { onBeforeUnmount, onMounted, reactive, computed, watch } from 'vue';
import { Colon, Icon, PureCard, Select } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import { Button, Radio, RadioGroup, Switch } from 'ant-design-vue';

import { service, systemLog } from '@/api';
import {
  SystemLogState,
  BrowseOption,
  LogContentParams,
  ServiceChangeParams,
  InstanceChangeParams,
  LogFileChangeParams,
  AutoRefreshChangeParams,
  FullscreenToggleParams
} from './types';
import {
  createBrowseOptions,
  createServiceOptions,
  createInstanceOptions,
  createLogFileOptions,
  createLogContentParams,
  getCurrentServiceLabel,
  getCurrentBrowseOption,
  resetServiceDependencies,
  resetInstanceDependencies,
  resetLogFileSelection,
  exportLogToFile,
  startAutoRefreshTimer,
  stopAutoRefreshTimer,
  processApiResponseData,
  handleApiError,
  isReadyForLogContent,
  isReadyForInstanceLoading,
  isReadyForLogFileLoading
} from './utils';

const { t } = useI18n();

// Reactive state management
const state = reactive<SystemLogState>({
  serviceOptions: [],
  instancesOptions: [],
  logOptions: [],
  serviceId: undefined,
  instances: undefined,
  logFile: undefined,
  browse: '50',
  logContent: '',
  autoRefresh: false,
  autoRefreshLoading: false,
  fullScreen: false
});

// Timer for auto-refresh functionality
let timer: ReturnType<typeof setTimeout> | null = null;

// Browse options configuration for log line selection
const browseOptions = computed<BrowseOption[]>(() => createBrowseOptions(t));

// Computed properties for better performance and readability
const currentServiceLabel = computed(() => {
  return getCurrentServiceLabel(state.serviceOptions, state.serviceId);
});

const currentBrowseOption = computed(() => {
  return getCurrentBrowseOption(browseOptions.value, state.browse);
});

/**
 * Initialize the component by loading discovery services
 */
const init = async (): Promise<void> => {
  await loadDiscoveryServices();
};

/**
 * Load available discovery services from the API
 * Sets the first service as default and loads its instances
 */
const loadDiscoveryServices = async (): Promise<void> => {
  try {
    const [error, { data = [] }] = await service.getDiscoveryServices();
    if (error || !data?.length) {
      return;
    }

    state.serviceId = data[0];
    state.serviceOptions = createServiceOptions(processApiResponseData(data));

    await loadServiceInstances();
  } catch (error) {
    handleApiError(error, 'load discovery services');
  }
};

/**
 * Load service instances for the selected service
 * Sets the first instance as default and loads available log files
 */
const loadServiceInstances = async (): Promise<void> => {
  if (!isReadyForInstanceLoading(state.serviceId)) return;

  try {
    const [error, { data = [] }] = await service.getServiceInstances(state.serviceId!);
    if (error || !data?.length) {
      return;
    }

    state.instancesOptions = createInstanceOptions(processApiResponseData(data));
    state.instances = data[0];

    await loadLogFile();
  } catch (error) {
    handleApiError(error, 'load service instances');
  }
};

/**
 * Load available log files for the selected instance
 * Sets the first log file as default and loads its content
 */
const loadLogFile = async (): Promise<void> => {
  if (!isReadyForLogFileLoading(state.instances)) return;

  try {
    const [error, { data = [] }] = await systemLog.getInstanceLogFiles(state.instances!);
    if (error) return;

    state.logOptions = createLogFileOptions(processApiResponseData(data));

    // Keep current log file if it's still available
    if (state.logFile && data.includes(state.logFile)) {
      await loadLogContent();
      return;
    }

    state.logFile = data[0];
    await loadLogContent();
  } catch (error) {
    handleApiError(error, 'load log files');
  }
};

/**
 * Load log content based on current instance, log file, and browse settings
 */
const loadLogContent = async (): Promise<void> => {
  if (!isReadyForLogContent(state.instances, state.logFile)) return;

  try {
    const params: LogContentParams = createLogContentParams(
      state.browse,
      currentBrowseOption.value?.type || 0
    );

    const [error, { data }] = await systemLog.getInstanceLogFile(
      state.instances!,
      state.logFile!,
      params
    );

    if (error) return;
    state.logContent = data;
  } catch (error) {
    handleApiError(error, 'load log content');
  }
};

/**
 * Handle service selection change
 * Resets dependent selections and loads new instances
 */
const serviceChange = (value: ServiceChangeParams['value']): void => {
  if (value) {
    resetServiceDependencies(state);
    loadServiceInstances();
  }
};

/**
 * Handle instance selection change
 * Resets log file selection and loads available log files
 */
const instancesChange = (value: InstanceChangeParams['value']): void => {
  if (value) {
    resetInstanceDependencies(state);
    loadLogFile();
  }
};

/**
 * Handle log file selection change
 * Loads log content and stops auto-refresh if no file selected
 */
const logFileChange = (value: LogFileChangeParams['value']): void => {
  if (value) {
    loadLogContent();
  } else {
    state.autoRefresh = false;
    resetLogFileSelection(state);
    stopAutoRefreshTimer(timer);
    timer = null;
  }
};

/**
 * Handle auto-refresh toggle
 * Starts or stops the refresh timer based on user selection
 */
const autoRefreshChange = (value: AutoRefreshChangeParams['value']): void => {
  if (value) {
    startAutoRefresh();
  } else {
    stopAutoRefresh();
  }
};

/**
 * Start auto-refresh timer with 2-second interval
 */
const startAutoRefresh = (): void => {
  stopAutoRefresh(); // Clear existing timer first

  timer = startAutoRefreshTimer(async () => {
    await loadLogContent();
  }, 2000);
};

/**
 * Stop auto-refresh timer and clear it
 */
const stopAutoRefresh = (): void => {
  stopAutoRefreshTimer(timer);
  timer = null;
};

/**
 * Toggle fullscreen mode for log viewing
 */
const toggleFullScreen = (value: FullscreenToggleParams['value']): void => {
  state.fullScreen = value;
};

/**
 * Export log content to a downloadable file
 * Creates a blob with timestamp and service information in filename
 */
const saveToFile = (): void => {
  exportLogToFile(state.logContent, currentServiceLabel.value, state.browse);
};

/**
 * Handle browse line count change
 * Reloads log content with new line count
 */
const handleBrowseChange = (): void => {
  loadLogContent();
};

// Watch for changes in browse value to reload content
watch(() => state.browse, handleBrowseChange);

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
    <div v-if="state.fullScreen" class="absolute top-0 left-0 right-0 bottom-0 z-9999 bg-black-log">
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
          {{ state.logContent }}
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
        v-model:value="state.serviceId"
        :options="state.serviceOptions"
        class="w-60 ml-3 mr-5"
        :placeholder="t('log.system.placeholder.selectService')"
        size="small"
        @change="serviceChange" />

      {{ t('log.system.labels.instance') }}
      <Select
        v-model:value="state.instances"
        :disabled="!state.serviceId"
        :options="state.instancesOptions"
        class="w-60 ml-3 mr-5"
        :placeholder="t('log.system.placeholder.selectInstance')"
        size="small"
        @change="instancesChange" />

      {{ t('log.system.labels.logFile') }}
      <Select
        v-model:value="state.logFile"
        :disabled="!state.instances"
        :options="state.logOptions"
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
          v-model:value="state.browse"
          :disabled="!state.logFile"
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
            v-model:checked="state.autoRefresh"
            :disabled="!state.logFile"
            :loading="state.autoRefreshLoading"
            size="small"
            @change="(checked: any) => autoRefreshChange(!!checked)" />
        </div>

        <!-- Save to file button -->
        <Button
          size="small"
          class="mr-6"
          :disabled="!state.logContent"
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
      {{ state.logContent }}
    </div>
  </PureCard>
</template>
