<script setup lang="ts">
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import { Button } from 'ant-design-vue';

import { SubmitButtonProps } from '../types';

const props = withDefaults(defineProps<SubmitButtonProps>(), {
  testLoading: false,
  saveLoading: false
});

const emit = defineEmits(['testConfig', 'saveConfig']);

const { t } = useI18n();
const testButton = ref();
const router = useRouter();

/**
 * Emit save configuration event to parent component
 * Triggers form validation and data submission
 */
const saveConfig = (): void => {
  emit('saveConfig');
};

/**
 * Navigate back to LDAP directory list page
 * Cancels current operation and returns to main view
 */
const goBackList = (): void => {
  router.push('/system/ldap');
};

/**
 * Programmatically trigger test button click
 * Exposed method for parent components to trigger testing
 */
const testClick = (): void => {
  testButton.value.onClick();
};

// Expose testClick method for external access
defineExpose({ testClick });
</script>

<template>
  <div class="my-10 -ml-50">
    <!-- Save Configuration Button -->
    <Button
      size="small"
      :loading="props.saveLoading"
      :disabled="props.testLoading"
      type="primary"
      @click="saveConfig">
      {{ t('ldap.buttons.saveConfig') }}
    </Button>

    <!-- Cancel Button -->
    <Button
      size="small"
      class="ml-2"
      :loading="props.saveLoading"
      :disabled="props.testLoading"
      @click="goBackList">
      {{ t('common.actions.cancel') }}
    </Button>
  </div>
</template>
