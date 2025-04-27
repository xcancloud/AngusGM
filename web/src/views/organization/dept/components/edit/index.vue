<script setup lang='ts'>
import { ref, reactive, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { Form, FormItem } from 'ant-design-vue';
import { Modal, Input } from '@xcan-angus/vue-ui';

import { dept } from '@/api';

interface FormType {
  name: string | undefined
}

interface Props {
  visible: boolean,
  id: string | undefined,
  name: string | undefined,
}

const props = withDefaults(defineProps<Props>(), {});

const emit = defineEmits<{(e: 'close'): void, (e: 'save', value: string): void }>();

const { t } = useI18n();

const formRef = ref();

const state = reactive<{
  confirmLoading: boolean,
  rules: Record<string, Array<Record<string, string | boolean>>>
}>({
  confirmLoading: false,
  rules: {
    name: [
      { required: true, message: t('deptNamePlaceholder'), trigger: 'blur' }
    ]
  }
});

const form = reactive<FormType>({
  name: props.name
});

const save = () => {
  formRef.value.validate().then(async () => {
    const params = {
      id: props.id,
      name: form.name
    };
    state.confirmLoading = true;
    const [error] = await dept.updateDept([params]);
    state.confirmLoading = false;
    if (error) {
      return;
    }
    emit('save', params.name || '');
  });
};
const close = () => {
  emit('close');
};

watch(() => props.visible, newValue => {
  if (newValue) {
    form.name = props.name;
  }
});

</script>
<template>
  <Modal
    width="540px"
    :title="t('editName')"
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
      v-bind="{ labelCol: { span: 6 }, wrapperCol: { span: 15 } }">
      <FormItem :label="t('deptName')" name="name">
        <Input
          v-model:value="form.name"
          :allowClear="true"
          :maxlength="50"
          :placeholder="t('deptNameLengthTip')">
        </Input>
      </FormItem>
    </Form>
  </Modal>
</template>
