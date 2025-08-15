<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { Hints, Input } from '@xcan-angus/vue-ui';
import { Form, FormItem } from 'ant-design-vue';
import { useI18n } from 'vue-i18n';

import { LdapComponentProps, LdapBaseConfig } from '../types';
import { createFormLayoutConfig, createLdapBaseValidationRules, createInitialLdapBaseConfig } from '../utils';

const props = withDefaults(defineProps<LdapComponentProps>(), {
  index: -1,
  keys: ''
});

const emit = defineEmits(['rules']);

const { t } = useI18n();

// Form reference
const formRef = ref();

// Form layout configuration
const labelCol = createFormLayoutConfig(9);
const wrapperCol = createFormLayoutConfig(15);

// Reactive form data for LDAP base configuration
const form = reactive<LdapBaseConfig>(createInitialLdapBaseConfig());

// Form validation rules
const rules = createLdapBaseValidationRules(t);

/**
 * Execute form validation and emit result to parent
 * Validates form fields and sends success/error status with form data
 */
const childRules = (): void => {
  formRef.value.validate().then(() => {
    emit('rules', 'success', props.keys, props.index, form);
  }).catch(() => {
    emit('rules', 'error', props.keys, props.index, form);
  });
};

/**
 * Watch for query changes to populate form fields
 * Automatically fills form when editing existing configuration
 */
watch(() => props.query, (val) => {
  if (val) {
    Object.keys(val).forEach((key: string) => {
      form[key as keyof LdapBaseConfig] = val[key];
    });
  }
});

// Expose validation method to parent component
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
    <!-- Base DN Field -->
    <div class="flex">
      <FormItem
        :label="t('ldap.model-label-1')"
        name="baseDn"
        class="w-150">
        <Input
          v-model:value="form.baseDn"
          :maxlength="400"
          :placeholder="t('ldap.model-1')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('ldap.messages.baseDnTip')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>

    <!-- Additional User DN Field -->
    <div class="flex">
      <FormItem
        :label="t('ldap.model-label-2')"
        class="w-150">
        <Input
          v-model:value="form.additionalUserDn"
          :maxlength="400"
          :placeholder="t('ldap.model-2')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('ldap.messages.userDnTip')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>

    <!-- Additional Group DN Field -->
    <div class="flex">
      <FormItem
        :label="t('ldap.model-label-3')"
        class="w-150">
        <Input
          v-model:value="form.additionalGroupDn"
          :maxlength="400"
          :placeholder="t('ldap.model-3')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('ldap.messages.groupDnTip')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
  </Form>
</template>
