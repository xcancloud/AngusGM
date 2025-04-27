<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { Hints, Input } from '@xcan-angus/vue-ui';
import { Checkbox, Form, FormItem } from 'ant-design-vue';

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
const form: any = reactive({
  descriptionAttribute: '',
  objectFilter: '',
  nameAttribute: '',
  objectClass: '',
  ignoreSameNameGroup: true
});

const rules = reactive({
  objectFilter: [{ required: false, message: t('systemLdap.config-2') }],
  nameAttribute: [{ required: false, message: t('systemLdap.config-3') }],
  objectClass: [{ required: false, message: '请输入组对象类' }]
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
  const isNull = Object.keys(val).every((key) => (key !== 'ignoreSameNameGroup' && form[key] === '') || key === 'ignoreSameNameGroup');
  rules.objectFilter[0].required = !isNull;
  rules.nameAttribute[0].required = !isNull;
  rules.objectClass[0].required = !isNull;
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
        :label="t('systemLdap.config-5')"
        name="objectClass">
        <Input
          v-model:value="form.objectClass"
          :placeholder="t('systemLdap.config-1')"
          :maxlength="400"
          size="small" />
      </FormItem>
      <Hints
        :text="t('systemLdap.config-mess-1')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <div class="flex">
      <FormItem
        id="group-objectFilter"
        class="w-150"
        :label="t('systemLdap.config-6')"
        name="objectFilter">
        <Input
          v-model:value="form.objectFilter"
          :maxlength="400"
          :placeholder="t('systemLdap.config-2')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('systemLdap.config-mess-2')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <div class="flex">
      <FormItem
        id="group-nameAttribute"
        class="w-150"
        :label="t('systemLdap.config-7')"
        name="nameAttribute">
        <Input
          v-model:value="form.nameAttribute"
          :maxlength="160"
          :placeholder="t('systemLdap.config-3')" />
      </FormItem>
      <Hints
        :text="t('systemLdap.config-mess-3')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <div class="flex">
      <FormItem
        class="w-150"
        :label="t('systemLdap.config-8')">
        <Input
          v-model:value="form.descriptionAttribute"
          :maxlength="160"
          :placeholder="t('systemLdap.config-4')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('systemLdap.config-mess-4')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <FormItem
      class="w-225"
      :labelCol="{span: 6}"
      :label="t('存在相同组名时是否忽略')">
      <div class="flex">
        <Checkbox v-model:checked="form.ignoreSameNameGroup" />
        <Hints
          :text="t('当存在相应组名称时忽略当前目录组(重复不忽略时将存在两个同名组)')"
          style="transform: translateY(3px);"
          class="ml-2" />
      </div>
    </FormItem>
  </Form>
</template>
