<script setup lang='ts'>
import { computed, ref } from 'vue';
import { Colon } from '@xcan/design';
import { Col, Collapse, CollapsePanel, RadioButton, RadioGroup, Row } from 'ant-design-vue';
import { CaretRightOutlined } from '@ant-design/icons-vue';

import { DataInfoType } from '../PropsType';

interface Props {
  data: DataInfoType
}

const props = withDefaults(defineProps<Props>(), {});

const rowConfig = {
  gutter: 15,
  labelCol: 6,
  valueCol: 18
};

const activeKey = ref(['1', '2']);
const mode = ref('summary');

const headers = computed(() => {
  let hs = {};
  if (typeof props.data?.requestHeaders === 'string') {
    hs = props.data?.requestHeaders;
  } else {
    hs = props.data?.requestHeaders;
  }
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const rs: { key: string, value: any }[] = [];
  for (const key in hs) {
    rs.push({ key, value: hs[key] });
  }
  return rs;
});
const body = computed(() => {
  const hs = JSON.parse(props.data.requestBody);
  const rs: { key: string, value: any }[] = [];
  for (const key in hs) {
    rs.push({ key, value: hs[key] });
  }
  return rs;
});
</script>

<template>
  <div class="flex-1 flex flex-col">
    <RadioGroup
      v-model:value="mode"
      buttonStyle="solid"
      size="small"
      class="custom-group-btn">
      <RadioButton value="summary">summary</RadioButton>
      <RadioButton value="raw">raw</RadioButton>
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
        header="Headers"
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
        header="Query String">
        <template v-if="props.data?.requestBody">
          <div
            v-for="(item, index) in body"
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
                class="px-3 h-10 leading-10"
                :class="index % 2 > 0 ? 'bg-gray-light' : ''">
                <Col :span="rowConfig.labelCol" class="font-medium">{{ item.key }}</Col>
                <Col :span="rowConfig.valueCol">{{ item.value || '-' }}</Col>
              </Row>
            </template>
          </div>
        </template>
      </CollapsePanel>
    </Collapse>
    <div
      v-if="mode === 'raw'"
      class="text-3 text-theme-content mt-4 overflow-hidden hover:overflow-y-auto pr-3.5 flex-1">
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
  @apply px-0 pt-0 pb-4 border-b border-dotted  font-medium;

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
