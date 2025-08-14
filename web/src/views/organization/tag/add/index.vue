<script setup lang="ts">
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { IconRequired, Input, Modal } from '@xcan-angus/vue-ui';
import type { TagAddProps } from '@/views/organization/tag/types';

// Component props with default values
const props = withDefaults(defineProps<TagAddProps>(), {
  visible: false,
  loading: false
});

// Component event emissions
const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void,
  (e: 'ok', name: string | undefined): void
}>();

const { t } = useI18n();

// Form data state
const name = ref<string | undefined>(undefined);

// Validation state
const showRule = ref(false);

// Handle form submission
const handleOk = async (): Promise<void> => {
  if (!name.value) {
    showRule.value = true;
    return;
  }
  emit('ok', name.value);
};

// Handle modal cancellation
const handleCancel = () => {
  emit('update:visible', false);
};

// Handle input change for validation
const handleChange = (event: any) => {
  showRule.value = !event.target.value;
};
</script>

<template>
  <Modal
    width="540px"
    :title="t('tag.actions.addTag')"
    :visible="props.visible"
    :centered="true"
    :keyboard="true"
    :confirmLoading="props.loading"
    @cancel="handleCancel"
    @ok="handleOk">
    <!-- Form content -->
    <div class="flex items-start">
      <!-- Required field label -->
      <div class="whitespace-nowrap mr-2 pt-1">
        <IconRequired class="mr-1" />
        {{ t('tag.columns.name') }}
      </div>

      <!-- Input field and validation -->
      <div class="flex-1">
        <Input
          v-model:value="name"
          :maxlength="100"
          :placeholder="t('tag.placeholder.addNameTip')"
          @change="handleChange" />

        <!-- Validation message -->
        <div class="h-6 text-rule text-3 leading-3 mt-1">
          <template v-if="showRule">
            {{ t('tag.placeholder.addNameTip') }}
          </template>
        </div>
      </div>
    </div>
  </Modal>
</template>
