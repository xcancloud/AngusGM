<script setup lang="ts">
import { ref, watch } from 'vue';
import { regexpUtils } from '@xcan-angus/infra';
import { Input } from '@xcan-angus/vue-ui';

interface Props {
  value: string;
  blurPure?: boolean;
  disabled?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  value: '',
  blurPure: false,
  disabled: false
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'pressEnter'): void;
  (e: 'update:value', value: string | undefined): void;
}>();

const inputValue = ref<string>();
const error = ref(false);

watch(() => inputValue.value, (newValue) => {
  emit('update:value', newValue);
  if (newValue && regexpUtils.isMobileNumber('CN', newValue)) {
    error.value = false;
  }
});

const validateData = () => {
  if (inputValue.value && regexpUtils.isMobileNumber('CN', inputValue.value)) {
    error.value = false;
    return true;
  }

  error.value = true;
  return false;
};

const blur = () => {
  if (props.blurPure) {
    return;
  }

  validateData();
};

const pressEnter = () => {
  emit('pressEnter');
};

defineExpose({ validateData });
</script>
<template>
  <div class="relative" :class="{'error':error}">
    <Input
      v-model:value="inputValue"
      :maxlength="11"
      :disabled="disabled"
      :placeholder="$t('enter-mobile')"
      class="pl22"
      size="large"
      dataType="number"
      @pressEnter="pressEnter"
      @blur="blur">
      <template #prefix>
        <div class="select-none flex items-center text-gray-content">
          <img src="./assets/mobile.png" />
          <span class="ml-1">+86</span>
          <span class="divider"></span>
        </div>
      </template>
    </Input>
    <div class="error-message">{{ $t('error-mobile') }}</div>
  </div>
</template>
<style scoped>
:deep(.ant-input-affix-wrapper input) {
  color: rgba(68, 76, 90, 100%);
}

.divider {
  width: 1px;
  height: 16px;
  margin-right: 6px;
  margin-left: 10px;
  background-color: #f0f0f0;
}

</style>
