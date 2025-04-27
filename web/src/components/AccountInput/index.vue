<script lang="ts" setup>
import { ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';

import { Input } from '@xcan/design';

const { t } = useI18n();

interface Props {
  value: string;
  disabled?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  value: '',
  disabled: false
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'pressEnter'): void;
  (e: 'update:value', value: string | undefined): void;
}>();

const error = ref(false);
const inputValue = ref<string>();

const pressEnter = () => {
  emit('pressEnter');
};

const validateData = () => {
  if (inputValue.value) {
    error.value = false;
    return true;
  }

  error.value = true;
  return false;
};

watch(() => inputValue.value, (newValue) => {
  emit('update:value', newValue);
  if (newValue) {
    error.value = false;
  }
});

defineExpose({ validateData });
</script>
<template>
  <div class="relative" :class="{'error':error}">
    <Input
      v-model:value="inputValue"
      :maxlength="500"
      :disabled="props.disabled"
      :placeholder="t('enter-account')"
      trimAll
      size="large"
      @pressEnter="pressEnter">
      <template #prefix>
        <img src="./assets/user.png" />
      </template>
    </Input>
    <div class="error-message">{{ t('no-empty-account') }}</div>
  </div>
</template>
