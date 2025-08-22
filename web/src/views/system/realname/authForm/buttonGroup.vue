<script setup lang="ts">
import { Button } from 'ant-design-vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

withDefaults(defineProps<{ confirmLoading: boolean }>(), { confirmLoading: false });

const emit = defineEmits(['confirm', 'cancel']);

/**
 * Handle button click events
 */
const buttonClick = function (type: boolean) {
  emit(type ? 'confirm' : 'cancel');
};
</script>

<template>
  <div class="flex items-center justify-center space-x-6">
    <!-- Confirm button -->
    <Button
      size="middle"
      type="primary"
      :loading="confirmLoading"
      class="confirm-btn"
      @click="buttonClick(true)">
      {{ t('realName.buttons.confirm') }}
    </Button>

    <!-- Cancel button -->
    <Button
      size="middle"
      class="cancel-btn"
      @click="buttonClick(false)">
      {{ t('realName.buttons.cancel') }}
    </Button>
  </div>
</template>

<style scoped>
.confirm-btn {
  background: linear-gradient(135deg, #3B82F6 0%, #1D4ED8 100%);
  border: none;
  border-radius: 0.15rem;
  padding: 0.75rem 2rem;
  font-weight: 500;
  font-size: 1rem;
  box-shadow: 0 4px 14px 0 rgba(59, 130, 246, 0.3);
  transition: all 0.3s ease;
  min-width: 100px;
}

.confirm-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px 0 rgba(59, 130, 246, 0.4);
  background: linear-gradient(135deg, #2563EB 0%, #1E40AF 100%);
}

.confirm-btn:active {
  transform: translateY(0);
}

.cancel-btn {
  background: white;
  border: 2px solid #E5E7EB;
  border-radius: 0.15rem;
  padding: 0.75rem 2rem;
  font-weight: 500;
  font-size: 16px;
  color: #6B7280;
  transition: all 0.3s ease;
  min-width: 100px;
}

.cancel-btn:hover {
  border-color: #D1D5DB;
  background-color: #F9FAFB;
  color: #374151;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px 0 rgba(0, 0, 0, 0.1);
}

.cancel-btn:active {
  transform: translateY(0);
}
</style>
