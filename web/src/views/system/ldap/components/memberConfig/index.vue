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

const props = withDefaults(defineProps<Props>(), { index: -1, keys: '' });

const { t } = useI18n();
const formRef = ref();
const labelCol = { span: 9 };
const form: any = reactive({
  groupMemberAttribute: '',
  memberGroupAttribute: ''
});
const rules = reactive({
  groupMemberAttribute: [{ required: false, message: t('systemLdap.member-label-1') }],
  memberGroupAttribute: [{ required: false, message: t('systemLdap.member-label-2') }]
});

// 表单验证方法
const childRules = function () {
  formRef.value.validate().then(() => {
    emit('rules', 'success', props.keys, props.index, form);
  }).catch(() => {
    emit('rules', 'error', props.keys, props.index, form);
  });
};

// 监听表单 动态必填
watch(() => form, (val) => {
  const isNull = Object.keys(val).every((key) => form[key] === '');
  rules.groupMemberAttribute[0].required = !isNull;
  rules.memberGroupAttribute[0].required = !isNull;
}, { deep: true, immediate: true });

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
    :model="form"
    :rules="rules"
    :colon="false"
    size="small">
    <div class="flex">
      <FormItem
        class="w-150"
        :label="t('systemLdap.member-label-1')"
        name="groupMemberAttribute">
        <Input
          v-model:value="form.groupMemberAttribute"
          :maxlength="160"
          :placeholder="t('systemLdap.member-1')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('systemLdap.member-mess-1')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <div class="flex">
      <FormItem
        class="w-150"
        :label="t('systemLdap.member-label-2')"
        name="memberGroupAttribute">
        <Input
          v-model:value="form.memberGroupAttribute"
          :maxlength="160"
          :placeholder="t('systemLdap.member-2')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('systemLdap.member-mess-2')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
  </Form>
</template>
