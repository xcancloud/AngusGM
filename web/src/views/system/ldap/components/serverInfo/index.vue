<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { Hints, Input, SelectEnum } from '@xcan-angus/vue-ui';
import { Checkbox, Form, FormItem } from 'ant-design-vue';

import { useI18n } from 'vue-i18n';

interface Props {
  index: number,
  keys: string,
  query?: any
}

const props = withDefaults(defineProps<Props>(), { index: -1, keys: '' });

const emit = defineEmits(['rules']);

const { t } = useI18n();

const labelCol = { span: 9 };
const form = ref({
  directoryType: 'OpenLDAP',
  host: '',
  name: '',
  password: '',
  port: '',
  ssl: false,
  username: ''
});

const formRef = ref();

const rules = reactive({
  name: [{ required: true, message: t('systemLdap.info-1') }],
  directoryType: [{ required: true, message: t('请选择目录类型') }],
  username: [{ required: true, message: t('systemLdap.info-3') }],
  port: [{ required: true, message: t('systemLdap.info-4') }],
  password: [{ required: true, message: t('systemLdap.info-5') }],
  host: [{ required: true, message: t('systemLdap.info-6') }]
});

const childRules = function () {
  formRef.value.validate().then(() => {
    emit('rules', 'success', props.keys, props.index, { ...form.value, port: Number(form.value.port) });
  }).catch(() => {
    emit('rules', 'error', props.keys, props.index, { ...form.value, port: Number(form.value.port) });
  });
};

// 回显
watch(() => props.query, (val) => {
  if (val) {
    Object.keys(val).forEach((key: string) => {
      form.value[key] = val[key];
    });
  }
});

watch(() => form.value.ssl, newValue => {
  if (newValue) {
    form.value.port = '636';
  }
});

defineExpose({ childRules });

</script>
<template>
  <Form
    ref="formRef"
    :labelCol="labelCol"
    :rules="rules"
    :model="form"
    :colon="false"
    size="small">
    <div class="flex">
      <FormItem
        :label="t('systemLdap.info-label-1')"
        name="name"
        class="w-150">
        <Input
          v-model:value="form.name"
          :maxlength="100"
          :placeholder="t('systemLdap.info-1')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('LDAP服务名称，不允许重复，如：MyOpenLDAP。')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <FormItem
      :label="t('systemLdap.info-label-2')"
      name="directoryType"
      class="w-150">
      <SelectEnum
        v-model:value="form.directoryType"
        placeholder="请输入目录类型"
        enumKey="DirectoryType"
        size="small" />
    </FormItem>
    <div class="flex">
      <FormItem
        :label="t('systemLdap.info-label-3')"
        name="host"
        class="w-150">
        <Input
          v-model:value="form.host"
          :maxlength="200"
          :placeholder="t('systemLdap.info-6')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('运行LDAP的服务器的主机名，例如：ldap.example.com。')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <div class="flex">
      <FormItem
        :label="t('systemLdap.info-label-4')"
        name="port"
        class="w-150">
        <Input
          v-model:value="form.port"
          :min="1"
          :max="65535"
          required
          dataType="number"
          :placeholder="t('systemLdap.info-4')"
          size="small" />
      </FormItem>
      <Checkbox
        v-model:checked="form.ssl"
        class="ml-2"
        style="transform: translateY(3px);">
        {{ t('systemLdap.info-mess-1') }}
      </Checkbox>
      <Hints
        :text="t('运行LDAP的服务的端口号，例如：389 或 SSL 636。')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <div class="flex">
      <FormItem
        :label="t('systemLdap.info-label-5')"
        name="username"
        class="w-150">
        <Input
          v-model:value="form.username"
          :maxlength="200"
          :placeholder="t('systemLdap.info-3')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('登录LDAP的服务的账号，例如：cn=admin,dc=example,dc=org。')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <div class="flex">
      <FormItem
        :label="t('systemLdap.info-label-6')"
        name="password"
        class="w-150">
        <Input
          v-model:value="form.password"
          type="password"
          class="password-input"
          autocomplete="off"
          :maxlength="400"
          :placeholder="t('systemLdap.info-5')"
          size="small">
        </Input>
      </FormItem>
      <Hints
        :text="t('登录LDAP的服务的账号密码。')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
  </Form>
</template>
<style scoped>
:deep(.password-input .ant-input-suffix) {
  @apply hidden;
}
</style>
