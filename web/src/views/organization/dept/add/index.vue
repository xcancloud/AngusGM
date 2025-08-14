<script setup lang='ts'>
import { reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Form, FormItem } from 'ant-design-vue';
import { Input, Modal, notification, Select } from '@xcan-angus/vue-ui';

import { GM } from '@xcan-angus/infra';
import { DeptFormData, AddProps, AddEmits, FormRules } from '../types';
import { createDepartment } from '../utils';

// Component props with default values
const props = withDefaults(defineProps<AddProps>(), {
  pid: '-1'
});

// Component emit events for add operation
const emit = defineEmits<AddEmits>();

// Internationalization instance
const { t } = useI18n();

// Form reference for validation
const formRef = ref();

// Component state management
const state = reactive<{
  confirmLoading: boolean;
  form: DeptFormData;
}>({
  confirmLoading: false,
  form: {
    pid: props.pid,
    name: undefined,
    code: undefined,
    tags: []
  }
});

// Form validation rules for required fields
const rules: FormRules = {
  name: [
    { required: true, message: t('department.placeholder.addNameTip'), trigger: 'blur' }
  ],
  code: [
    { required: true, message: t('department.placeholder.addCodeTip'), trigger: 'blur' }
  ]
};

/**
 * Save department operation
 * Validates form data and creates new department
 * Handles success/error notifications and emits save event
 */
const save = async (): Promise<void> => {
  try {
    // Validate form before submission
    await formRef.value.validate();

    // Ensure all required fields are present
    if (!state.form.name || !state.form.code || !state.form.pid) {
      notification.error(t('common.messages.operationFailed'));
      return;
    }

    // Prepare department creation parameters
    const params = {
      code: state.form.code,
      name: state.form.name,
      pid: state.form.pid,
      tagIds: state.form.tags
    };

    // Set loading state and create department
    state.confirmLoading = true;
    const result = await createDepartment(params, t);

    if (result) {
      notification.success(t('common.messages.addSuccess'));
      emit('save', { ...params, id: result.id });
    }
  } catch (error) {
    console.error('Error saving department:', error);
    notification.error(t('common.messages.operationFailed'));
  } finally {
    state.confirmLoading = false;
  }
};

/**
 * Close modal and emit close event
 */
const close = (): void => {
  emit('close');
};

</script>

<template>
  <Modal
    width="450px"
    destroyOnClose
    :title="t('department.actions.addDept')"
    :maskClosable="false"
    :centered="true"
    :keyboard="false"
    :confirmLoading="state.confirmLoading"
    :visible="visible"
    @ok="save"
    @cancel="close">
    <!-- Department creation form -->
    <Form
      ref="formRef"
      :model="state.form"
      :rules="rules"
      size="small"
      v-bind="{labelCol: {span: 6}, wrapperCol: {span: 16}}">
      <!-- Parent department display (only shown when not root) -->
      <FormItem
        v-if="props.pid !== '-1'"
        :label="t('department.columns.parent')">
        <span class="text-3">{{ props.pname }}</span>
      </FormItem>

      <!-- Department name input field -->
      <FormItem
        :label="t('common.columns.name')"
        name="name">
        <Input
          v-model:value="state.form.name"
          size="small"
          :maxlength="80"
          :placeholder="t('department.placeholder.nameLengthTip')" />
      </FormItem>

      <!-- Department code input field with validation -->
      <FormItem
        :label="t('common.columns.code')"
        name="code">
        <Input
          v-model:value="state.form.code"
          size="small"
          dataType="mixin-en"
          :maxlength="80"
          includes=":_-."
          :placeholder="t('department.placeholder.codeLengthTip')" />
      </FormItem>

      <!-- Department tags selection -->
      <FormItem
        :label="t('common.columns.tags')"
        name="tags">
        <Select
          v-model:value="state.form.tags"
          size="small"
          showSearch
          internal
          :fieldNames="{ label: 'name', value: 'id' }"
          :maxTags="10"
          :action="`${GM}/org/tag`"
          :placeholder="t('department.placeholder.tag')"
          mode="multiple" />
      </FormItem>
    </Form>
  </Modal>
</template>
