<script lang="ts" setup>
import { ref, watch } from 'vue';
import { regexpUtils } from '@xcan-angus/infra';
import { Input } from '@xcan-angus/vue-ui';

/**
 * Component props interface
 * Defines the structure for email input configuration
 */
interface Props {
  value: string;
  disabled?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  value: '',
  disabled: false
});

/**
 * Component emits definition
 * Defines the events that this component can emit
 */
const emit = defineEmits<{
  (e: 'update:value', value: string | undefined): void;
}>();

// Reactive state variables
const error = ref(false);
const inputValue = ref<string>('');

/**
 * Watch for changes in input value and validate email format
 * Updates parent component and clears error state for valid emails
 */
watch(() => inputValue.value, (newValue) => {
  emit('update:value', newValue);
  if (newValue && regexpUtils.isEmail(newValue)) {
    error.value = false;
  }
});

/**
 * Validate email input data
 * Returns true if email format is valid, false otherwise
 */
const validateData = (): boolean => {
  if (inputValue.value && regexpUtils.isEmail(inputValue.value)) {
    error.value = false;
    return true;
  }

  error.value = true;
  return false;
};

/**
 * Handle blur event
 * Triggers email validation when input loses focus
 */
const blur = (): void => {
  validateData();
};

// Expose validation method for parent components
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
      :placeholder="$t('components.emailInput.placeholder.enterEmail')"
      @blur="blur">
      <template #prefix>
        <img src="./assets/email.png" alt="Email icon" />
      </template>
    </Input>
    <div class="error-message">{{ $t('components.emailInput.messages.emailError') }}</div>
  </div>
</template>
