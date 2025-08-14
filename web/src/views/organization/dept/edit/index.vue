<script setup lang='ts'>
import { reactive, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { Form, FormItem } from 'ant-design-vue';
import { Input, Modal, notification } from '@xcan-angus/vue-ui';

import { EditProps, EditEmits, FormType } from '../types';
import { updateDepartment } from '../utils';

const props = withDefaults(defineProps<EditProps>(), {});

const emit = defineEmits<EditEmits>();

// Internationalization setup
const { t } = useI18n();

// Form reference for validation
const formRef = ref();

/**
 * Reactive state management for component
 */
const state = reactive<{
  confirmLoading: boolean;
  rules: Record<string, Array<Record<string, string | boolean>>>;
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
 * Save department changes
 * Validates form, calls API, and handles response
 */
const save = async (): Promise<void> => {
  try {
    // Validate form before submission
    await formRef.value.validate();

    // Ensure id is present
    if (!props.id) {
      notification.error(t('common.messages.operationFailed'));
      return;
    }

    const params = {
      id: props.id as string,
      name: form.name
    };

    // Validate required fields
    if (!params.name) {
      notification.error(t('common.messages.operationFailed'));
      return;
    }

    state.confirmLoading = true;
    const success = await updateDepartment([params], t);

    if (success) {
      notification.success(t('common.messages.editSuccess'));
      emit('save', params.name);
    }
  } catch (error) {
    console.error('Error saving department:', error);
    notification.error(t('common.messages.operationFailed'));
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
        :label="t('common.columns.name')"
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
