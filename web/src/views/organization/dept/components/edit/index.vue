<script setup lang='ts'>
import { reactive, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { Form, FormItem } from 'ant-design-vue';
import { Input, Modal, notification } from '@xcan-angus/vue-ui';

import { dept } from '@/api';

/**
 * Form data interface for department editing
 * Defines the structure of the form data
 */
interface FormType {
  name: string | undefined
}

/**
 * Component props interface
 * Defines the properties passed to the department edit component
 */
interface Props {
  visible: boolean, // Modal visibility state
  id: string | undefined, // Department ID to edit
  name: string | undefined, // Current department name
}

const props = withDefaults(defineProps<Props>(), {});

/**
 * Component emits interface
 * Defines the events that this component can emit
 */
const emit = defineEmits<{
  (e: 'close'): void, // Emitted when modal is closed
  (e: 'save', value: string): void // Emitted when form is saved with new name
}>();

// Internationalization setup
const { t } = useI18n();

// Form reference for validation
const formRef = ref();

/**
 * Reactive state management for component
 */
const state = reactive<{
  confirmLoading: boolean, // Loading state for save operation
  rules: Record<string, Array<Record<string, string | boolean>>> // Form validation rules
}>({
  confirmLoading: false,
  rules: {
    name: [
      { required: true, message: t('department.actions.editName'), trigger: 'blur' }
    ]
  }
});

/**
 * Form data reactive object
 * Holds the current form values
 */
const form = reactive<FormType>({
  name: props.name
});

/**
 * Enhanced error handling for API operations
 * Provides consistent error messaging and logging
 */
const handleApiError = (error: any, operation: string): void => {
  console.error(`Error in ${operation}:`, error);
  notification.error(t('common.messages.operationFailed'));
};

/**
 * Save department changes
 * Validates form, calls API, and handles response
 */
const save = async (): Promise<void> => {
  try {
    // Validate form before submission
    await formRef.value.validate();
    
    const params = {
      id: props.id,
      name: form.name
    };

    // Validate required fields
    if (!params.id || !params.name) {
      notification.error(t('common.messages.operationFailed'));
      return;
    }

    state.confirmLoading = true;
    const [error] = await dept.updateDept([params]);
    
    if (error) {
      handleApiError(error, 'update department');
      return;
    }

    notification.success(t('common.messages.editSuccess'));
    emit('save', params.name);
  } catch (error) {
    handleApiError(error, 'save department');
  } finally {
    state.confirmLoading = false;
  }
};

/**
 * Close modal and reset form
 * Emits close event to parent component
 */
const close = (): void => {
  emit('close');
};

/**
 * Watch for modal visibility changes
 * Resets form data when modal opens
 */
watch(() => props.visible, (newValue) => {
  if (newValue) {
    // Reset form with current props when modal opens
    form.name = props.name;
  }
});

/**
 * Watch for props changes to update form
 * Ensures form stays in sync with props
 */
watch(() => props.name, (newName) => {
  if (newName !== undefined) {
    form.name = newName;
  }
});
</script>
<template>
  <Modal
    width="540px"
    :title="t('department.actions.editName')"
    :centered="true"
    :maskClosable="false"
    :keyboard="false"
    :confirmLoading="state.confirmLoading"
    :visible="visible"
    destroyOnClose
    @ok="save"
    @cancel="close">
    <Form
      ref="formRef"
      size="small"
      :model="form"
      :rules="state.rules"
      layout="horizontal"
      :labelCol="{ span: 6 }"
      :wrapperCol="{ span: 18 }">
      <FormItem 
        :label="t('department.columns.name')" 
        name="name"
        :rules="[
          { required: true, message: t('department.validation.nameRequired'), trigger: 'blur' },
          { min: 1, max: 50, message: t('department.validation.nameLength'), trigger: 'blur' }
        ]">
        <Input
          v-model:value="form.name"
          :allowClear="true"
          :maxlength="50"
          :placeholder="t('department.placeholder.nameLengthTip')"
          :disabled="state.confirmLoading"
          @keyup.enter="save">
        </Input>
      </FormItem>
    </Form>
  </Modal>
</template>
