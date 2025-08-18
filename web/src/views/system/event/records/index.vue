<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, reactive } from 'vue';
import { useI18n } from 'vue-i18n';
import { Badge, Button, Popover } from 'ant-design-vue';
import { AsyncComponent, Hints, Icon, IconCount, IconRefresh, PureCard, SearchPanel, Table } from '@xcan-angus/vue-ui';
import { app, GM } from '@xcan-angus/infra';
import DOMPurify from 'dompurify';

import { event } from '@/api';
import { EventRecord, EventRecordsState, CheckContentConfig } from './types';
import {
  createSearchOptions, createTableColumns, createStatusStyleMapping,
  createInitialPaginationParams, createInitialEventRecord, createInitialCheckContentConfig,
  createPaginationObject, updatePaginationParams, resetPagination, updateSearchFilters,
  getStatusStyle, hasEventError, canViewEvent, canShowReceiveConfig
} from './utils';

// Async component imports
const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));
const ReceiveConfig = defineAsyncComponent(() => import('./receiveConfig.vue'));
const ViewEvent = defineAsyncComponent(() => import('./view.vue'));

const { t } = useI18n();

// Component state management
const state = reactive<EventRecordsState>({
  showCount: true,
  eventRecordList: [],
  params: createInitialPaginationParams(),
  total: 0,
  loading: false,
  disabled: false,
  selectedItem: createInitialEventRecord(),
  visible: false
});

// Check content configuration
const checkContentConfig = reactive<CheckContentConfig>(
  createInitialCheckContentConfig()
);

// Status style mapping for push status badges
const statusStyleMapping = createStatusStyleMapping();

// Search panel options configuration
const searchOptions = computed(() => createSearchOptions(t));

// Table columns configuration
const tableColumns = computed(() => createTableColumns(t));

/**
 * Get event list from API
 * Fetches event records based on current parameters
 */
const getEventList = async (): Promise<void> => {
  if (state.loading) {
    return;
  }

  try {
    state.loading = true;
    const [error, res] = await event.getEventList({ ...state.params });

    if (error) {
      return;
    }

    state.eventRecordList = res.data.list;
    state.total = +res.data.total || 0;
  } catch (error) {
    console.error('Failed to get event list:', error);
  } finally {
    state.loading = false;
  }
};

/**
 * Handle table change events
 * Updates pagination and sorting parameters
 */
const tableChange = async (_pagination: any, _filter: any, sorter: any): Promise<void> => {
  updatePaginationParams(state.params, {
    orderSort: sorter.orderSort,
    sortBy: sorter.sortBy,
    current: _pagination.current,
    pageSize: _pagination.pageSize
  });

  state.disabled = true;
  await getEventList();
  state.disabled = false;
};

/**
 * Open check content dialog
 * Shows event content in a modal
 */
const openCheckContentDialog = (content: string): void => {
  checkContentConfig.dialogVisible = true;
  checkContentConfig.content = content;
};

/**
 * Handle search panel changes
 * Updates search filters and resets pagination
 */
const handleChange = async (filters: Record<string, string>[]): Promise<void> => {
  resetPagination(state.params);
  updateSearchFilters(state.params, filters);

  state.disabled = true;
  await getEventList();
  state.disabled = false;
};

/**
 * Open receive configuration modal
 * Shows channel configuration for selected event
 */
const openReceiveConfig = (record: EventRecord): void => {
  state.selectedItem = record;
  state.visible = true;
};

// Computed pagination object for table component
const pagination = computed(() =>
  createPaginationObject(state.params, state.total)
);

// Lifecycle hooks
onMounted(() => {
  getEventList();
});
</script>

<!-- TODO 控制台报错
 1.
 -->

<template>
  <div class="flex flex-col min-h-full">
    <!-- Event records description hint -->
    <Hints :text="t('event.records.messages.description')" class="mb-1" />

    <PureCard class="flex-1 p-3.5">
      <!-- Statistics component -->
      <Statistics
        resource="Event"
        :barTitle="t('statistics.metrics.newEvents')"
        :router="GM"
        :visible="state.showCount" />

      <!-- Search panel and controls -->
      <div class="flex items-center justify-between mb-2">
        <SearchPanel
          class="flex-1"
          :options="searchOptions"
          @change="handleChange" />
        <div class="flex">
          <IconCount v-model:value="state.showCount" />
          <IconRefresh
            :loading="state.loading"
            :disabled="state.disabled"
            class="ml-2"
            @click="getEventList" />
        </div>
      </div>

      <!-- Event records table -->
      <Table
        size="small"
        :dataSource="state.eventRecordList"
        :columns="tableColumns"
        :pagination="pagination"
        :loading="state.loading"
        bodyCell="--"
        @change="tableChange">
        <template #bodyCell="{ column, record }">
          <!-- ID column with view link -->
          <template v-if="column.dataIndex === 'id'">
            <Button
              v-if="canViewEvent(record)"
              type="link"
              class="px-0 text-xs"
              @click="openCheckContentDialog(record.eventViewUrl)">
              {{ record.id }}
            </Button>
            <span v-else>{{ record.id }}</span>
          </template>

          <!-- Event type column -->
          <template v-if="column.dataIndex === 'type'">
            {{ record.type.message }}
          </template>

          <!-- Push status column with error details -->
          <template v-if="column.dataIndex === 'pushStatus'">
            <Badge
              :status="getStatusStyle(record.pushStatus.value, statusStyleMapping)"
              :text="record.pushStatus.message">
            </Badge>
            <Popover
              v-if="hasEventError(record)"
              :overlayStyle="{ maxWidth: '500px' }">
              <Icon
                icon="icon-gantanhao-yuankuang"
                class="text-xs leading-3.5 text-theme-sub-content text-theme-text-hover ml-1" />
              <template #title>
                {{ t('event.records.messages.failureReason') }}
              </template>
              <template #content>
                <div
                  class="text-xs max-h-100 max-w-150 overflow-auto"
                  v-html="DOMPurify.sanitize(record.pushMsg)">
                </div>
              </template>
            </Popover>
          </template>

          <!-- Action column -->
          <template v-if="column.dataIndex === 'action'">
            <!-- Receive channel view button -->
            <Button
              v-if="app.show('ReceivingChannelView') && canShowReceiveConfig(record)"
              type="text"
              class="text-xs"
              @click="openReceiveConfig(record)">
              <Icon icon="icon-jiekoudaili" class="mr-1" />
              {{ app.getName('ReceivingChannelView') }}
            </Button>

            <!-- Event content view button -->
            <Button
              v-if="app.show('EventContentView') && canViewEvent(record)"
              type="text"
              class="text-xs"
              :disabled="!app.has('EventContentView')"
              @click="openCheckContentDialog(record.eventViewUrl)">
              <Icon icon="icon-shijianjilu" class="mr-1" />
              {{ app.getName('EventContentView') }}
            </Button>
          </template>
        </template>
      </Table>
    </PureCard>

    <!-- Receive configuration modal -->
    <AsyncComponent :visible="state.visible">
      <ReceiveConfig
        v-if="state.visible"
        v-model:visible="state.visible"
        :eventCode="state.selectedItem.eventCode"
        :eKey="state.selectedItem.ekey" />
    </AsyncComponent>

    <!-- Event content view modal -->
    <AsyncComponent :visible="checkContentConfig.dialogVisible">
      <ViewEvent
        v-model:visible="checkContentConfig.dialogVisible"
        :value="checkContentConfig.content" />
    </AsyncComponent>
  </div>
</template>
