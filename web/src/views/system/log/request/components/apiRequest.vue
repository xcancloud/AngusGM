<script setup lang="ts">
import { computed, ref } from 'vue';
import { Colon } from '@xcan-angus/vue-ui';
import { Col, Collapse, CollapsePanel, RadioButton, RadioGroup, Row } from 'ant-design-vue';
import { CaretRightOutlined } from '@ant-design/icons-vue';
import { useI18n } from 'vue-i18n';

import { ApiRequestProps } from '../types';
import { createRowConfig, processHeaders, processRequestBody, isArrayValue, formatDataForDisplay } from '../utils';

// Component props with proper typing
const props = withDefaults(defineProps<ApiRequestProps>(), {});
const { t } = useI18n();

// Row configuration for grid layouts
const rowConfig = createRowConfig();

// Component state management
const activeKey = ref(['1', '2']);
const mode = ref<'summary' | 'raw'>('summary');

/**
 * Process request headers for display
 * Handles both string and object header formats
 */
const headers = computed(() => {
  return processHeaders(props.data?.requestHeaders);
});

/**
 * Process request body for display
 * Parses JSON body and converts to key-value pairs
 */
const body = computed(() => {
  return processRequestBody(props.data.requestBody);
});

/**
 * Check if component has data to display
 */
const hasData = computed(() => {
  return !!props.data?.requestBody;
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

      <!-- Query string section -->
      <CollapsePanel
        key="2"
        class="custom-collapse-item text-3 text-theme-content"
        :header="t('log.request.messages.queryString')">
        <template v-if="hasData">
          <div
            v-for="(item, index) in body"
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
                class="px-3 h-10 leading-10"
                :class="getRowBackgroundClass(index)">
                <Col :span="rowConfig.labelCol" class="font-medium">{{ item.key }}</Col>
                <Col :span="rowConfig.valueCol">{{ getDisplayValue(item.value) }}</Col>
              </Row>
            </template>
          </div>
        </template>
      </CollapsePanel>
    </Collapse>

    <!-- Raw mode display -->
    <div
      v-if="mode === 'raw'"
      class="text-3 text-theme-content mt-4 overflow-hidden hover:overflow-y-auto pr-3.5 flex-1">
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

.bg-gray-light {
  background-color: var(--table-header-bg);
}
</style>
