<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, reactive, ref } from 'vue';
import { Button, Popover } from 'ant-design-vue';
import { AsyncComponent, Hints, Icon, IconRefresh, PureCard, SearchPanel, Table } from '@xcan-angus/vue-ui';
import { app, enumUtils, CombinedTargetType } from '@xcan-angus/infra';
import { useI18n } from 'vue-i18n';
import { appopen, event } from '@/api';

import { EventConfigList, EventTemplateState } from './types';
import {
  createSearchOptions, createTableColumns, createInitialPaginationParams, createInitialEventConfigList,
  createPaginationObject, updatePaginationParams, resetPagination, updateSearchFilters,
  getTargetTypeName, getAppName, needsTruncation, truncateText, getChannelTypeDisplayName,
  processAppListForSearch, loadAppOptionsRecursively, canConfigureEvent
} from './utils';

// Async component imports
const ReceiveConfig = defineAsyncComponent(() => import('./receiveConfig.vue'));

const { t } = useI18n();

// Component state management
const state = reactive<EventTemplateState>({
  visible: false,
  selectedItem: createInitialEventConfigList(),
  eventConfigList: [],
  params: createInitialPaginationParams(),
  total: 0,
  loading: false,
  disabled: false,
  targetTypeEnums: [],
  appList: [],
  appLoaded: false
});

// Search panel options configuration
const searchOptions = ref(createSearchOptions(t));

// Table columns configuration
const tableColumns = ref(createTableColumns(t));

/**
 * Query event templates with search filters
 * Updates search filters and resets pagination
 */
const query = async (filters: Record<string, string>[]): Promise<void> => {
  resetPagination(state.params);
  updateSearchFilters(state.params, filters);

  state.disabled = true;
  await getEventTemplate();
  state.disabled = false;
};

/**
 * Load enums for target types
 */
const loadEnums = (): void => {
  state.targetTypeEnums = enumUtils.enumToMessages(CombinedTargetType);
};

/**
 * Load app options for search panel
 * Recursively loads all apps to populate the dropdown
 */
const loadAppOptions = async (pageSize = 10): Promise<void> => {
  try {
    const appList = await loadAppOptionsRecursively(appopen.getList, pageSize);
    state.appList = appList;
    processAppListForSearch(appList, searchOptions.value);
    state.appLoaded = true;
  } catch (error) {
    console.error('Failed to load app options:', error);
  }
};

/**
 * Get event template list from API
 * Fetches event templates based on current parameters
 */
const getEventTemplate = async (): Promise<void> => {
  if (state.loading) {
    return;
  }

  try {
    state.loading = true;
    const [error, res] = await event.getTemplateList(state.params);

    if (error) {
      return;
    }

    state.eventConfigList = res.data.list;
    state.total = Number(res.data.total) || 0;
  } catch (error) {
    console.error('Failed to get event template:', error);
  } finally {
    state.loading = false;
  }
};

/**
 * Open receive configuration modal
 * Shows channel configuration for selected event template
 */
const openReceiveConfig = (record: EventConfigList): void => {
  state.selectedItem = record;
  state.visible = true;
};

/**
 * Handle pagination changes
 * Updates pagination parameters and reloads data
 */
const changePagination = async (pagination: any): Promise<void> => {
  updatePaginationParams(state.params, {
    current: pagination.current,
    pageSize: pagination.pageSize
  });

  state.disabled = true;
  await getEventTemplate();
  state.disabled = false;
};

// Computed pagination object for table component
const pagination = computed(() =>
  createPaginationObject(state.params, state.total)
);

// Lifecycle hooks
onMounted(() => {
  loadAppOptions();
  loadEnums();
  getEventTemplate();
});
</script>

<!-- TODO 控制台报错：`onSearch` should work with `showSearch` instead of use alone.-->

<template>
  <div class="flex flex-col min-h-full">
    <!-- Event template description hint -->
    <Hints :text="t('event.template.messages.description')" class="mb-1" />

    <PureCard class="flex-1 p-3.5">
      <!-- Search panel and refresh controls -->
      <div class="flex justify-between items-center mb-2">
        <SearchPanel
          v-if="state.appLoaded"
          :options="searchOptions"
          @change="query" />
        <IconRefresh
          :loading="state.loading"
          :disabled="state.disabled"
          class="ml-2"
          @click="getEventTemplate" />
      </div>

      <!-- Event template table -->
      <Table
        :loading="state.loading"
        :dataSource="state.eventConfigList"
        :columns="tableColumns"
        :pagination="pagination"
        rowKey="id"
        size="small"
        @change="changePagination">
        <template #bodyCell="{ column, text, record }">
          <!-- Event code column with truncation -->
          <template v-if="column.dataIndex === 'eventCode'">
            <template v-if="!needsTruncation(text)">
              {{ text }}
            </template>
            <template v-else>
              <Popover
                :title="null"
                :overlayStyle="{ width: '380px' }"
                placement="bottomLeft"
                :mouseLeaveDelay="0">
                {{ truncateText(text) }}
                <template #content>
                  {{ text }}
                </template>
              </Popover>
            </template>
          </template>

          <!-- Event name column with truncation -->
          <template v-if="column.dataIndex === 'eventName'">
            <template v-if="!needsTruncation(text)">
              {{ text }}
            </template>
            <template v-else>
              <Popover
                :title="null"
                :overlayStyle="{ width: '380px' }"
                placement="bottomLeft"
                :mouseLeaveDelay="0">
                {{ truncateText(text) }}
                <template #content>
                  {{ text }}
                </template>
              </Popover>
            </template>
          </template>

          <!-- Event type column -->
          <template v-if="column.key === 'eventType'">
            {{ text?.message }}
          </template>

          <!-- Allowed channel types column -->
          <template v-if="column.key === 'allowedChannelTypes'">
            {{ getChannelTypeDisplayName(record.allowedChannelTypes) }}
          </template>

          <!-- Target type column -->
          <template v-if="column.dataIndex === 'targetType'">
            {{ getTargetTypeName(record.targetType, state.targetTypeEnums, t) }}
          </template>

          <!-- App code column -->
          <template v-if="column.dataIndex === 'appCode'">
            {{ getAppName(record.appCode, state.appList) }}
          </template>

          <!-- Action column -->
          <template v-if="column.key === 'operate'">
            <Button
              v-if="app.show('ReceiveChannelConfigure') && canConfigureEvent(record)"
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

    <!-- Receive configuration modal -->
    <AsyncComponent :visible="state.visible">
      <ReceiveConfig
        :id="state.selectedItem.id"
        v-model:visible="state.visible" />
    </AsyncComponent>
  </div>
</template>
