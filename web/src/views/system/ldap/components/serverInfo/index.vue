<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { Hints, Input, SelectEnum } from '@xcan-angus/vue-ui';
import { Checkbox, Form, FormItem } from 'ant-design-vue';
import { DirectoryType } from '@/enums/enums';

import { useI18n } from 'vue-i18n';

/**
 * Component props interface
 * @param {number} index - Component index in parent
 * @param {string} keys - Unique key identifier
 * @param {any} query - Query parameters for data population
 */
interface Props {
  index: number,
  keys: string,
  query?: any
}

const props = withDefaults(defineProps<Props>(), { index: -1, keys: '' });

const emit = defineEmits(['rules']);

const { t } = useI18n();

// Form layout configuration
const labelCol = { span: 9 };

// Reactive form data for server configuration
const form = ref({
  directoryType: DirectoryType.OpenLDAP, // Default directory type
  host: '',                              // Server host address
  name: '',                              // Server name/identifier
  password: '',                          // Connection password
  port: '',                              // Connection port
  ssl: false,                            // SSL connection flag
  username: ''                           // Connection username
});

const formRef = ref();

// Form validation rules
const rules = reactive({
  name: [{ required: true, message: t('ldap.info-1') }],
  directoryType: [{ required: true, message: t('请选择目录类型') }],
  username: [{ required: true, message: t('ldap.info-3') }],
  port: [{ required: true, message: t('ldap.info-4') }],
  password: [{ required: true, message: t('ldap.info-5') }],
  host: [{ required: true, message: t('ldap.info-6') }]
});

/**
 * Execute form validation and emit result to parent
 * Validates form fields and sends success/error status with form data
 */
const childRules = function () {
  formRef.value.validate().then(() => {
    // Emit success with validated form data
    emit('rules', 'success', props.keys, props.index, {
      ...form.value,
      port: Number(form.value.port)
    });
  }).catch(() => {
    // Emit error with form data for error handling
    emit('rules', 'error', props.keys, props.index, {
      ...form.value,
      port: Number(form.value.port)
    });
  });
};

/**
 * Watch for query changes to populate form fields
 * Automatically fills form when editing existing configuration
 */
watch(() => props.query, (val) => {
  if (val) {
    Object.keys(val).forEach((key: string) => {
      form.value[key] = val[key];
    });
  }
});

/**
 * Watch SSL checkbox changes to update port automatically
 * Sets port to 636 (LDAPS) when SSL is enabled, user can override
 */
watch(() => form.value.ssl, newValue => {
  if (newValue) {
    form.value.port = '636';
  }
});

// Expose validation method to parent component
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
        :label="t('ldap.info-label-1')"
        name="name"
        class="w-150">
        <Input
          v-model:value="form.name"
          :maxlength="100"
          :placeholder="t('ldap.info-1')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('ldap.messages.serverNameTip')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <FormItem
      :label="t('ldap.info-label-2')"
      name="directoryType"
      class="w-150">
      <SelectEnum
        v-model:value="form.directoryType"
        :placeholder="t('ldap.placeholder.selectDirectoryType')"
        :enumKey="DirectoryType"
        size="small" />
    </FormItem>
    <div class="flex">
      <FormItem
        :label="t('ldap.info-label-3')"
        name="host"
        class="w-150">
        <Input
          v-model:value="form.host"
          :maxlength="200"
          :placeholder="t('ldap.info-6')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('ldap.messages.hostTip')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <div class="flex">
      <FormItem
        :label="t('ldap.info-label-4')"
        name="port"
        class="w-150">
        <Input
          v-model:value="form.port"
          :min="1"
          :max="65535"
          required
          dataType="number"
          :placeholder="t('ldap.info-4')"
          size="small" />
      </FormItem>
      <Checkbox
        v-model:checked="form.ssl"
        class="ml-2"
        style="transform: translateY(3px);">
        {{ t('ldap.info-mess-1') }}
      </Checkbox>
      <Hints
        :text="t('ldap.messages.portTip')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <div class="flex">
      <FormItem
        :label="t('ldap.info-label-5')"
        name="username"
        class="w-150">
        <Input
          v-model:value="form.username"
          :maxlength="200"
          :placeholder="t('ldap.info-3')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('ldap.messages.usernameTip')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <div class="flex">
      <FormItem
        :label="t('ldap.info-label-6')"
        name="password"
        class="w-150">
        <Input
          v-model:value="form.password"
          type="password"
          class="password-input"
          autocomplete="off"
          :maxlength="400"
          :placeholder="t('ldap.info-5')"
          size="small">
        </Input>
      </FormItem>
      <Hints
        :text="t('ldap.messages.passwordTip')"
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
