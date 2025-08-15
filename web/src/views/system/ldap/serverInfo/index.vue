<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { Hints, Input, SelectEnum } from '@xcan-angus/vue-ui';
import { Checkbox, Form, FormItem } from 'ant-design-vue';
import { DirectoryType } from '@/enums/enums';

import { useI18n } from 'vue-i18n';
import { LdapComponentProps, ServerConfig, FormValidationRules } from '../types';
import { createFormLayoutConfig, createServerValidationRules, createInitialServerConfig } from '../utils';

const props = withDefaults(defineProps<LdapComponentProps>(), {
  index: -1,
  keys: ''
});

const emit = defineEmits(['rules']);

const { t } = useI18n();

// Form layout configuration
const labelCol = createFormLayoutConfig(9);

// Form reference
const formRef = ref();

// Reactive form data for server configuration
const form = reactive<ServerConfig>(createInitialServerConfig());

// Form validation rules
const rules = reactive<FormValidationRules>(createServerValidationRules(t));

/**
 * Execute form validation and emit result to parent
 * Validates form fields and sends success/error status with form data
 */
const childRules = (): void => {
  formRef.value.validate().then(() => {
    // Emit success with validated form data
    emit('rules', 'success', props.keys, props.index, {
      ...form,
      port: Number(form.port)
    });
  }).catch(() => {
    // Emit error with form data for error handling
    emit('rules', 'error', props.keys, props.index, {
      ...form,
      port: Number(form.port)
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
      form[key as keyof ServerConfig] = val[key];
    });
  }
});

/**
 * Watch SSL checkbox changes to update port automatically
 * Sets port to 636 (LDAPS) when SSL is enabled, user can override
 */
watch(() => form.ssl, (newValue) => {
  if (newValue) {
    form.port = '636';
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
    <!-- Server Name Field -->
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

    <!-- Directory Type Field -->
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

    <!-- Host Field -->
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

    <!-- Port and SSL Fields -->
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

    <!-- Username Field -->
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

    <!-- Password Field -->
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
