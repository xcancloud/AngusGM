<script setup lang="ts">
import { computed, ref } from 'vue';
import { Colon, FormatHighlight } from '@xcan-angus/vue-ui';
import { Col, Collapse, CollapsePanel, RadioButton, RadioGroup, Row } from 'ant-design-vue';
import { CaretRightOutlined } from '@ant-design/icons-vue';
import { useI18n } from 'vue-i18n';

import { ApiResponseProps } from '../types';
import {
  createRowConfig, processHeaders, isArrayValue, formatDataForDisplay, createResponseTabs, getContentType
} from '../utils';

// Component props with proper typing
const props = withDefaults(defineProps<ApiResponseProps>(), {});
const { t } = useI18n();

// Row configuration for grid layouts
const rowConfig = createRowConfig();

// Component state management
const activeKey = ref(['1', '2']);
const mode = ref<'summary' | 'raw'>('summary');
const currentTabId = ref<string>('pretty');

/**
 * Process response headers for display
 * Handles both string and object header formats
 */
const headers = computed(() => {
  return processHeaders(props.data?.responseHeaders);
});

/**
 * Create tab configuration for response formatting
 */
const tabs = computed(() => createResponseTabs(t));

/**
 * Check if tab is currently active
 * @param id - Tab identifier
 * @returns True if tab is active, false otherwise
 */
const isActive = (id: string): boolean => {
  return currentTabId.value === id;
};

/**
 * Handle tab selection
 * @param id - Selected tab identifier
 */
const handleSelect = (id: string): void => {
  currentTabId.value = id;
};

/**
 * Get content type for response formatting
 */
const contentType = computed(() => {
  return getContentType(props.data?.responseBody);
});

/**
 * Check if component has response body data
 */
const hasResponseBody = computed(() => {
  return !!props.data?.responseBody;
});

/**
 * Get alternating row background class
 * @param index - Row index
 * @returns CSS class for alternating backgrounds
 */
const getRowBackgroundClass = (index: number): string => {
  return index % 2 > 0 ? 'bg-gray-light' : '';
};

/**
 * Get display value with fallback
 * @param value - Value to display
 * @returns Formatted value or fallback
 */
const getDisplayValue = (value: any): string => {
  return formatDataForDisplay(value);
};
</script>

<template>
  <div class="flex-1 flex flex-col">
    <!-- Mode selection radio buttons -->
    <RadioGroup
      v-model:value="mode"
      buttonStyle="solid"
      size="small"
      class="custom-group-btn">
      <RadioButton value="summary">{{ t('log.request.messages.summary') }}</RadioButton>
      <RadioButton value="raw">{{ t('log.request.messages.raw') }}</RadioButton>
    </RadioGroup>

    <!-- Summary mode with collapsible sections -->
    <Collapse
      v-if="mode === 'summary'"
      v-model:activeKey="activeKey"
      ghost
      class="overflow-hidden hover:overflow-y-auto pr-5 flex-1"
      style="scrollbar-gutter: stable;">
      <!-- Expand icon template -->
      <template #expandIcon="{ isActive }">
        <caret-right-outlined
          class="custom-collapse-icon"
          :rotate="isActive ? 90 : 0"
          style="margin-top: -4px;" />
      </template>

      <!-- Headers section -->
      <CollapsePanel
        key="1"
        :header="t('log.request.messages.headers')"
        class="custom-collapse-item text-3 text-theme-content">
        <div
          v-for="(item, index) in headers"
          :key="index">
          <!-- Array value rendering -->
          <template v-if="isArrayValue(item.value)">
            <Row
              v-for="(_item, _index) in item.value"
              :key="_index"
              :gutter="rowConfig.gutter"
              class="px-3 py-2"
              :class="getRowBackgroundClass(index)">
              <Col :span="rowConfig.labelCol" class="font-medium">{{ item.key }}</Col>
              <Col :span="rowConfig.valueCol">{{ getDisplayValue(_item) }}</Col>
            </Row>
          </template>

          <!-- Single value rendering -->
          <template v-else>
            <Row
              :gutter="rowConfig.gutter"
              class="px-3 py-2"
              :class="getRowBackgroundClass(index)">
              <Col :span="rowConfig.labelCol" class="font-medium leading-5">{{ item.key }}</Col>
              <Col :span="rowConfig.valueCol" class="leading-5">{{ getDisplayValue(item.value) }}</Col>
            </Row>
          </template>
        </div>
      </CollapsePanel>

      <!-- Response body section -->
      <CollapsePanel
        key="2"
        class="custom-collapse-item text-3 text-theme-content"
        :header="t('log.request.messages.response')">
        <div class="w-full mt-4">
          <!-- Format selection tabs -->
          <div class="flex mb-3 flex-freeze-auto items-center rounded text-3 text-black-secondary">
            <div
              v-for="item in tabs"
              :key="item.value"
              :class="{ active: isActive(item.value) }"
              class="flex justify-center items-center h-7 px-3 cursor-pointer bg-gray-light"
              @click="handleSelect(item.value)">
              {{ item.name }}
            </div>
          </div>

          <!-- Response body content -->
          <template v-if="hasResponseBody">
            <FormatHighlight
              class="flex-1 overflow-y-auto pb-5"
              :dataSource="props.data.responseBody"
              :dataType="contentType"
              :format="currentTabId" />
          </template>
        </div>
      </CollapsePanel>
    </Collapse>

    <!-- Raw mode display -->
    <div v-if="mode === 'raw'" class="w-full text-3 text-theme-content mt-4">
      <div
        v-for="(item, index) in headers"
        :key="index">
        <!-- Array value rendering -->
        <template v-if="isArrayValue(item.value)">
          <div
            v-for="(_item, _index) in item.value"
            :key="_index"
            class="px-3 py-2 flex items-start"
            :class="getRowBackgroundClass(index)">
            <div class="">
              {{ item.key }}
              <Colon />
            </div>
            <div class="font-medium">{{ getDisplayValue(_item) }}</div>
          </div>
        </template>

        <!-- Single value rendering -->
        <template v-else>
          <div class="px-3 py-2 flex items-start">
            <div>
              {{ item.key }}
              <Colon />
            </div>
            <div class="font-medium">{{ getDisplayValue(item.value) }}</div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped>
.custom-collapse-item {
  @apply mt-4;
}

.custom-collapse-item :deep(.ant-collapse-header) {
  @apply px-0 pt-0 pb-4 border-b border-dotted font-medium;

  border-color: var(--border-divider);
  color: var(--content-text-content);
}

.custom-collapse-item :deep(.ant-collapse-header) .custom-collapse-icon {
  vertical-align: 1px;
}

.custom-collapse-item :deep(.ant-collapse-content .ant-collapse-content-box) {
  @apply p-0;
}

.custom-group-btn :deep(.ant-radio-button-wrapper:not(.ant-radio-button-wrapper-checked)) {
  @apply border-blue-1 text-blue-1;
}

.custom-group-btn :deep(.ant-radio-button-wrapper-checked:not(.ant-radio-button-wrapper-disabled):focus-within) {
  @apply shadow-none;
}

.active {
  @apply bg-blue-active;

  color: var(--content-text-content);
}

.pretty-container {
  height: calc(100% - 40px);
  border-radius: 2px;
}

:deep(.jv-container.jv-light) {
  @apply flex-1 overflow-y-auto pb-5;
}

.bg-gray-light {
  background-color: var(--table-header-bg);
}
</style>
