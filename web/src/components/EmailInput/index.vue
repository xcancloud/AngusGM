<script lang="ts" setup>
import { ref, watch } from 'vue';
import { regexp } from '@xcan/utils';
import { Input } from '@xcan/design';

const error = ref(false);
const inputValue = ref<string>();

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
  (e: 'update:value', value: string | undefined): void;
}>();

watch(() => inputValue.value, (newValue) => {
  emit('update:value', newValue);
  if (newValue && regexp.isEmail(newValue)) {
    error.value = false;
  }
});

const validateData = () => {
  if (inputValue.value && regexp.isEmail(inputValue.value)) {
    error.value = false;
    return true;
  }

  error.value = true;
  return false;
};

const blur = () => {
  validateData();
};

defineExpose({ validateData });
</script>

<template>
  <div class="relative" :class="{'error':error}">
    <Input
      v-model:value="inputValue"
      :maxlength="500"
      :disabled="props.disabled"
      trimAll
      size="large"
      :placeholder="$t('enter-email')"
      @blur="blur">
      <template #prefix>
        <img src="./assets/email.png" />
      </template>
    </Input>
    <div class="error-message">{{ $t('email-error') }}</div>
  </div>
</template>
