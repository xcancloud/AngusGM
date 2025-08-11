<script setup lang="ts">
import { Button } from 'ant-design-vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

withDefaults(defineProps<{ confirmLoading: boolean }>(), { confirmLoading: false });

const emit = defineEmits(['confirm', 'cancel']);

/**
 * Handle button click events
 * @param type - true for confirm, false for cancel
 */
const buttonClick = function (type: boolean) {
  emit(type ? 'confirm' : 'cancel');
};

</script>

<template>
  <div class="flex items-center justify-center">
    <!-- Confirm button -->
    <Button
      size="small"
      type="primary"
      :loading="confirmLoading"
      class="mr-5"
      @click="buttonClick(true)">
      {{ t('realname.buttons.confirm') }}
    </Button>

    <!-- Cancel button -->
    <Button
      size="small"
      class="mr-22"
      @click="buttonClick(false)">
      {{ t('realname.buttons.cancel') }}
    </Button>
  </div>
</template>
