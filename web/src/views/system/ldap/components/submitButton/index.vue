<script setup lang="ts">
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import { Button } from 'ant-design-vue';

/**
 * Component props interface for submit button states
 * @param {boolean} testLoading - Loading state for test operation
 * @param {boolean} saveLoading - Loading state for save operation
 */
interface Props {
  testLoading: boolean,
  saveLoading: boolean
}

withDefaults(defineProps<Props>(), {
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
const saveConfig = function () {
  emit('saveConfig');
};

/**
 * Navigate back to LDAP directory list page
 * Cancels current operation and returns to main view
 */
const goBackList = () => {
  router.push('/system/ldap');
};

/**
 * Programmatically trigger test button click
 * Exposed method for parent components to trigger testing
 */
const testClick = function () {
  testButton.value.onClick();
};

// Expose testClick method for external access
defineExpose({ testClick });

</script>
<template>
  <div class="my-10 -ml-50">
    <Button
      size="small"
      :loading="saveLoading"
      :disabled="testLoading"
      type="primary"
      @click="saveConfig">
      {{ t('ldap.buttons.saveConfig') }}
    </Button>
    <Button
      size="small"
      class="ml-2"
      :loading="saveLoading"
      :disabled="testLoading"
      @click="goBackList">
      {{ t('common.actions.cancel') }}
    </Button>
  </div>
</template>
