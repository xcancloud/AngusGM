<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { Hints, Input } from '@xcan-angus/vue-ui';
import { Form, FormItem } from 'ant-design-vue';
import { useI18n } from 'vue-i18n';

interface Props {
  index: number,
  keys: string,
  query?: any
}

const emit = defineEmits(['rules']);

const { t } = useI18n();

const props = withDefaults(defineProps<Props>(), { index: -1, keys: '' });

const formRef = ref();

const labelCol = { span: 9 };
const wrapperCol = { span: 15 };
const form: any = reactive({
  baseDn: '',
  additionalUserDn: '',
  additionalGroupDn: ''
});
const rules = {
  baseDn: [{ required: true, message: t('systemLdap.model-1') }]
};

const childRules = function () {
  formRef.value.validate().then(() => {
    emit('rules', 'success', props.keys, props.index, form);
  }).catch(() => {
    emit('rules', 'error', props.keys, props.index, form);
  });
};

// 回显
watch(() => props.query, (val) => {
  if (val) {
    Object.keys(val).forEach((key: string) => {
      form[key] = val[key];
    });
  }
});

defineExpose({ childRules });

</script>
<template>
  <Form
    ref="formRef"
    :labelCol="labelCol"
    :wrapperCol="wrapperCol"
    :rules="rules"
    :model="form"
    :colon="false"
    size="small">
    <div class="flex">
      <FormItem
        :label="t('systemLdap.model-label-1')"
        name="baseDn"
        class="w-150">
        <Input
          v-model:value="form.baseDn"
          :maxlength="400"
          :placeholder="t('systemLdap.model-1')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('LDAP中用于搜索用户和组的根节点。')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <div class="flex">
      <FormItem
        :label="t('systemLdap.model-label-2')"
        class="w-150">
        <Input
          v-model:value="form.additionalUserDn"
          :maxlength="400"
          :placeholder="t('systemLdap.model-2')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('用于附加到基本DN以限制搜索用户的范围。')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <div class="flex">
      <FormItem
        :label="t('systemLdap.model-label-3')"
        class="w-150">
        <Input
          v-model:value="form.additionalGroupDn"
          :maxlength="400"
          :placeholder="t('systemLdap.model-3')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('用于附加到基本DN以限制搜索组的范围。')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
  </Form>
</template>
