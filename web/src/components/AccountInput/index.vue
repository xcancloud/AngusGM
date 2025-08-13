<script lang="ts" setup>
import { ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { Input } from '@xcan-angus/vue-ui';

const { t } = useI18n();

/**
 * Component props interface
 * Defines the structure for account input configuration
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
  (e: 'pressEnter'): void;
  (e: 'update:value', value: string | undefined): void;
}>();

// Reactive state variables
const error = ref(false);
const inputValue = ref<string>('');

/**
 * Handle Enter key press event
 * Emits pressEnter event to parent component
 */
const pressEnter = (): void => {
  emit('pressEnter');
};

/**
 * Validate account input data
 * Returns true if input is valid (non-empty), false otherwise
 */
const validateData = (): boolean => {
  if (inputValue.value) {
    error.value = false;
    return true;
  }

  error.value = true;
  return false;
};

/**
 * Watch for changes in input value and update parent component
 * Also clears error state when input becomes non-empty
 */
watch(() => inputValue.value, (newValue) => {
  emit('update:value', newValue);
  if (newValue) {
    error.value = false;
  }
});

// Expose validation method for parent components
defineExpose({ validateData });
</script>

<template>
  <div class="relative" :class="{'error':error}">
    <Input
      v-model:value="inputValue"
      :maxlength="500"
      :disabled="props.disabled"
      :placeholder="t('components.accountInput.placeholder.enterAccount')"
      trimAll
      size="large"
      @pressEnter="pressEnter">
      <template #prefix>
        <img src="./assets/user.png" alt="User icon" />
      </template>
    </Input>
    <div class="error-message">{{ t('components.accountInput.messages.noEmptyAccount') }}</div>
  </div>
</template>
