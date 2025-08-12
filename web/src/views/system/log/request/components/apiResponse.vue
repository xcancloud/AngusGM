<script setup lang='ts'>
import { computed, ref } from 'vue';
import { Colon, FormatHighlight } from '@xcan-angus/vue-ui';
import { Col, Collapse, CollapsePanel, RadioButton, RadioGroup, Row } from 'ant-design-vue';
import { CaretRightOutlined } from '@ant-design/icons-vue';
import { useI18n } from 'vue-i18n';

import { DataInfoType } from '../PropsType';

interface Props {
  data: DataInfoType
}

const props = withDefaults(defineProps<Props>(), {});
const { t } = useI18n();

const rowConfig = {
  gutter: 15,
  labelCol: 6,
  valueCol: 18
};

const activeKey = ref(['1', '2']);
const mode = ref('summary');

const headers = computed(() => {
  let hs = {};
  if (typeof props.data?.responseHeaders === 'string') {
    hs = props.data?.responseHeaders;
  } else {
    hs = props.data?.responseHeaders;
  }
  const rs: { key: string, value: any }[] = [];
  for (const key in hs) {
    rs.push({ key, value: hs[key] });
  }
  return rs;
});

const currentTabId = ref<string>('pretty');
const tabs = [
  {
    name: t('log.request.messages.prettyFormat'),
    value: 'pretty'
  },
  {
    name: t('log.request.messages.rawFormat'),
    value: 'raw'
  },
  {
    name: t('log.request.messages.preview'),
    value: 'preview'
  }

];
const isActive = (id: string): boolean => {
  return currentTabId.value === id;
};
const handleSelect = (id: string): void => {
  currentTabId.value = id;
};
const contentType = computed(() => {
  return 'json';
});

</script>

<template>
  <div class="flex-1 flex flex-col">
    <RadioGroup
      v-model:value="mode"
      buttonStyle="solid"
      size="small"
      class="custom-group-btn">
      <RadioButton value="summary">{{ t('log.request.messages.summary') }}</RadioButton>
      <RadioButton value="raw">{{ t('log.request.messages.raw') }}</RadioButton>
    </RadioGroup>
    <Collapse
      v-if="mode === 'summary'"
      v-model:activeKey="activeKey"
      ghost
      class="overflow-hidden hover:overflow-y-auto pr-5 flex-1"
      style="scrollbar-gutter: stable;">
      <template #expandIcon="{ isActive }">
        <caret-right-outlined
          class="custom-collapse-icon"
          :rotate="isActive ? 90 : 0"
          style="margin-top: -4px;" />
      </template>
      <CollapsePanel
        key="1"
        :header="t('log.request.messages.headers')"
        class="custom-collapse-item text-3 text-theme-content">
        <div
          v-for="(item, index) in headers"
          :key="index">
          <template v-if="Object.prototype.toString.call(item.value ) === '[object Array]'">
            <Row
              v-for="(_item, _index) in item.value"
              :key="_index"
              :gutter="rowConfig.gutter"
              class="px-3 py-2"
              :class="index % 2 > 0 ? 'bg-gray-light' : ''">
              <Col :span="rowConfig.labelCol" class="font-medium">{{ item.key }}</Col>
              <Col :span="rowConfig.valueCol">{{ _item || '-' }}</Col>
            </Row>
          </template>
          <template v-else>
            <Row
              :gutter="rowConfig.gutter"
              class="px-3 py-2"
              :class="index % 2 > 0 ? 'bg-gray-light' : ''">
              <Col :span="rowConfig.labelCol" class="font-medium leading-5">{{ item.key }}</Col>
              <Col :span="rowConfig.valueCol" class="leading-5">{{ item.value || '-' }}</Col>
            </Row>
          </template>
        </div>
      </CollapsePanel>
      <CollapsePanel
        key="2"
        class="custom-collapse-item text-3 text-theme-content"
        :header="t('log.request.messages.response')">
        <div class="w-full mt-4">
          <div class="flex mb-3 flex-freeze-auto items-center rounded text-3 text-black-secondary">
            <div
              v-for="item in tabs"
              :key="item.value"
              :class="{active:isActive(item.value)}"
              class="flex justify-center items-center h-7 px-3 cursor-pointer bg-gray-light"
              @click="handleSelect(item.value)">
              {{ item.name }}
            </div>
          </div>
          <template v-if="props.data?.responseBody">
            <FormatHighlight
              class="flex-1 overflow-y-auto pb-5"
              :dataSource="props.data.responseBody"
              :dataType="contentType"
              :format="currentTabId" />
          </template>
        </div>
      </CollapsePanel>
    </Collapse>
    <div v-if="mode === 'raw'" class="w-full text-3 text-theme-content mt-4">
      <div
        v-for="(item, index) in headers"
        :key="index">
        <template v-if="Object.prototype.toString.call(item.value ) === '[object Array]'">
          <div
            v-for="(_item, _index) in item.value"
            :key="_index"
            :gutter="rowConfig.gutter"
            class="px-3 py-2 flex items-start"
            :class="index % 2 > 0 ? 'bg-gray-light' : ''">
            <div class="">
              {{ item.key }}
              <Colon />
            </div>
            <div class="font-medium">{{ _item || '-' }}</div>
          </div>
        </template>
        <template v-else>
          <div class="px-3 py-2 flex items-start">
            <div>
              {{ item.key }}
              <Colon />
            </div>
            <div class="font-medium">{{ item.value || '-' }}</div>
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
