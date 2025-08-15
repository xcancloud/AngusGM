<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { Hints, Input } from '@xcan-angus/vue-ui';
import { Checkbox, Form, FormItem } from 'ant-design-vue';

import { useI18n } from 'vue-i18n';
import { LdapComponentProps, UserSchemaConfig } from '../types';
import { createFormLayoutConfig, createUserSchemaValidationRules, createInitialUserSchemaConfig } from '../utils';

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

// Reactive form data for LDAP user configuration
const form = reactive<UserSchemaConfig>(createInitialUserSchemaConfig());

// Form validation rules for required fields
const rules = createUserSchemaValidationRules(t);

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
      if (key in form) {
        (form as any)[key] = val[key];
      }
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
    <!-- Object Class Field -->
    <div class="flex">
      <FormItem
        :label="t('ldap.user-label-1')"
        name="objectClass"
        class="w-150">
        <Input
          v-model:value="form.objectClass"
          :maxlength="400"
          :placeholder="t('ldap.user-1')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('ldap.user-mess-1')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>

    <!-- Object Filter Field -->
    <div class="flex">
      <FormItem
        :label="t('ldap.user-label-2')"
        name="objectFilter"
        class="w-150">
        <Input
          v-model:value="form.objectFilter"
          :maxlength="400"
          :placeholder="t('ldap.user-2')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('ldap.user-mess-2')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>

    <!-- Username Attribute Field -->
    <div class="flex">
      <FormItem
        :label="t('ldap.user-label-3')"
        name="usernameAttribute"
        class="w-150">
        <Input
          v-model:value="form.usernameAttribute"
          :maxlength="160"
          :placeholder="t('ldap.user-3')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('ldap.user-mess-3')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>

    <!-- First Name Attribute Field -->
    <div class="flex">
      <FormItem
        :label="t('ldap.user-label-4')"
        name="firstNameAttribute"
        class="w-150">
        <Input
          v-model:value="form.firstNameAttribute"
          :maxlength="160"
          :placeholder="t('ldap.user-4')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('ldap.user-mess-4')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>

    <!-- Last Name Attribute Field -->
    <div class="flex">
      <FormItem
        :label="t('ldap.user-label-5')"
        name="lastNameAttribute"
        class="w-150">
        <Input
          v-model:value="form.lastNameAttribute"
          :maxlength="160"
          :placeholder="t('ldap.user-5')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('ldap.user-mess-5')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>

    <!-- Display Name Attribute Field -->
    <div class="flex">
      <FormItem
        :label="t('ldap.labels.displayName')"
        name="displayNameAttribute"
        class="w-150">
        <Input
          v-model:value="form.displayNameAttribute"
          :maxlength="160"
          :placeholder="t('ldap.user-5')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('ldap.messages.displayNameTip')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>

    <!-- Mobile Attribute Field -->
    <div class="flex">
      <FormItem
        :label="t('ldap.user-label-6')"
        name="mobileAttribute"
        class="w-150">
        <Input
          v-model:value="form.mobileAttribute"
          :maxlength="160"
          :placeholder="t('ldap.user-6')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('ldap.user-mess-6')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>

    <!-- Email Attribute Field -->
    <div class="flex">
      <FormItem
        :label="t('ldap.user-label-7')"
        name="emailAttribute"
        class="w-150">
        <Input
          v-model:value="form.emailAttribute"
          :maxlength="160"
          size="small"
          :placeholder="t('ldap.user-7')" />
      </FormItem>
      <Hints
        :text="t('ldap.user-mess-7')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>

    <!-- Password Attribute Field -->
    <div class="flex">
      <FormItem
        :label="t('ldap.user-label-8')"
        name="passwordAttribute"
        class="w-150">
        <Input
          v-model:value="form.passwordAttribute"
          :maxlength="160"
          :placeholder="t('ldap.user-8')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('ldap.user-mess-8')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>

    <!-- Ignore Same Identity User Field -->
    <FormItem
      :label="t('ldap.labels.ignoreSameIdentityUser')"
      class="w-150"
      name="ignoreSameIdentityUser">
      <div class="flex items-center">
        <Checkbox :checked="form.ignoreSameIdentityUser" disabled></Checkbox>
        <Hints
          :text="t('ldap.messages.ignoreSameIdentityUserTip')"
          style="transform: translateY(1px);"
          class="ml-2" />
      </div>
    </FormItem>
  </Form>
</template>
