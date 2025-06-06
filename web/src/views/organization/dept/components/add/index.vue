<script setup lang='ts'>
import { ref, reactive } from 'vue';
import { useI18n } from 'vue-i18n';
import { Form, FormItem } from 'ant-design-vue';
import { notification, Modal, Input, Select } from '@xcan-angus/vue-ui';

import { GM } from '@xcan-angus/tools';
import { dept } from '@/api';

interface FormType {
  pid: string | undefined,
  name: string | undefined,
  code: string | undefined,
  tags: string[]
}

interface Props {
  visible: boolean,
  pid?: string | undefined;
  pname?: string
}

const props = withDefaults(defineProps<Props>(), {
  pid: '-1'
});
const emit = defineEmits<{(e: 'close'): void, (e: 'save', value): void }>();
const { t } = useI18n();
const formRef = ref();

const state = reactive<{
  confirmLoading: boolean,
  form: FormType
}>({
  confirmLoading: false,
  form: {
    pid: props.pid,
    name: undefined,
    code: undefined,
    tags: []
  }
});

const rules = {
  name: [
    { required: true, message: t('addNameTip'), trigger: 'blur' }
  ],
  code: [
    { required: true, message: t('addCodeTip'), trigger: 'blur' }
  ]
};
const save = () => {
  formRef.value.validate().then(async () => {
    const params = {
      code: state.form.code,
      name: state.form.name,
      pid: state.form.pid,
      tagIds: state.form.tags
    };
    state.confirmLoading = true;
    const [error, { data = [] }] = await dept.addDept([params]);
    state.confirmLoading = false;
    if (error) {
      return;
    }
    const id = data[0].id;

    notification.success(t('addSuccess'));
    emit('save', { ...params, id });
  });
};
const close = () => {
  emit('close');
};

</script>

<template>
  <Modal
    width="450px"
    destroyOnClose
    :title="t('addDept')"
    :maskClosable="false"
    :centered="true"
    :keyboard="false"
    :confirmLoading="state.confirmLoading"
    :visible="visible"
    @ok="save"
    @cancel="close">
    <Form
      ref="formRef"
      :model="state.form"
      :rules="rules"
      size="small"
      v-bind="{labelCol: {span: 6}, wrapperCol: {span: 16}}">
      <FormItem v-if="props.pid !== '-1'" label="上级">
        <span class="text-3">{{ props.pname }}</span>
      </FormItem>
      <FormItem :label="t('name')" name="name">
        <Input
          v-model:value="state.form.name"
          size="small"
          :maxlength="80"
          :placeholder="t('addNameTip')" />
      </FormItem>
      <FormItem :label="t('code')" name="code">
        <Input
          v-model:value="state.form.code"
          size="small"
          dataType="mixin-en"
          :maxlength="80"
          includes=":_-."
          :placeholder="t('addCodeTip')" />
      </FormItem>
      <FormItem :label="t('label')" name="tags">
        <Select
          v-model:value="state.form.tags"
          size="small"
          showSearch
          internal
          :fieldNames="{ label: 'name', value: 'id' }"
          :maxTags="10"
          :action="`${GM}/org/tag/search`"
          :placeholder="t('tagPlaceholder')"
          mode="multiple" />
      </FormItem>
    </Form>
  </Modal>
</template>
