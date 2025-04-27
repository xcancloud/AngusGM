<script lang="ts" setup>
import { ref, watch } from 'vue';
import { Select } from '@xcan/design';

interface Props {
  dataSource: Array<any>;
  value: string
}

const props = withDefaults(defineProps<Props>(), {
  dataSource: () => []
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'change', value: string | undefined): void;
}>();

const checkedValue = ref<string>();
const error = ref(false);

const getPopupContainer = (triggerNode) => {
  if (triggerNode.parentNode) {
    return triggerNode.parentNode;
  }

  return document.body;
};

watch(() => checkedValue.value, (newValue) => {
  emit('change', newValue);

  if (newValue) {
    error.value = false;
  }
});

const validateData = () => {
  if (checkedValue.value) {
    error.value = false;
    return true;
  }

  error.value = true;
  return false;
};

defineExpose({ validateData });
</script>
<template>
  <div class="relative" :class="{'error':error}">
    <img class="prefix-icon absolute w-4 z-10" src="./assets/sa.png">
    <Select
      v-model:value="checkedValue"
      :getPopupContainer="getPopupContainer"
      :options="props.dataSource"
      :fieldNames="{label:'tenantName',value:'userId'}"
      :placeholder="$t('chose-account')"
      dropdownClassName="rounded-select"
      class="w-full rounded-select"
      size="large" />
    <div class="error-message">{{ $t('select-login-account') }}</div>
  </div>
</template>
<style scoped>
.prefix-icon {
  top: 50%;
  left: 12px;
  transform: translateY(-50%);
}

.rounded-select :deep(.ant-select-selector) {
  padding-left: 40px !important;
  border: 1px solid rgba(200, 202, 208, 100%);
  border-radius: 44px;
}

.error-message {
  display: none;
  color: rgba(245, 34, 45, 100%);
  font-size: 13px;
  line-height: 16px;
}

.ant-select-focused :deep(.ant-select-selection),
:deep(.ant-select-selection:focus),
:deep(.ant-select-selection:active) {
  border-color: rgba(234, 248, 255, 100%);
  background-color: rgba(179, 215, 255, 100%);
  box-shadow: none;
}

.ant-select :deep(.ant-select-selection) {
  height: 44px;
  border-radius: 22px;
}

.ant-select :deep(.ant-select-selection__rendered) {
  height: 100%;
  margin-left: 40px;
}

.ant-select :deep(.ant-select-selection-selected-value) {
  display: flex !important;
  align-items: center;
  height: 100%;
  font-size: 16px;
}

.ant-select :deep(.ant-select-selection__placeholder) {
  font-size: 16px;
}

:deep(.ant-select-dropdown-menu-item) {
  padding-left: 40px;
}

:deep(.ant-select-dropdown-menu) {
  padding-top: 12px;
  padding-bottom: 12px;
}

:deep(.ant-select-dropdown-menu-item-selected) {
  color: rgba(234, 248, 255, 100%);
}

</style>
<style>
.ant-select-dropdown.rounded-select {
  padding: 8px 0;
}

.ant-select-dropdown.rounded-select .ant-select-item {
  line-height: 32px;
}
</style>
