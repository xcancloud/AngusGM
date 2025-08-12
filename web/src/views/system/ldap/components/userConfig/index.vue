<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { Hints, Input } from '@xcan-angus/vue-ui';
import { Checkbox, Form, FormItem } from 'ant-design-vue';

import { useI18n } from 'vue-i18n';

/**
 * Component props interface for user configuration
 * @param {number} index - Component index in parent
 * @param {string} keys - Unique key identifier
 * @param {any} query - Query parameters for data population
 */
interface Props {
  index: number,
  keys: string,
  query?: any
}

const emit = defineEmits(['rules']);

const { t } = useI18n();

const props = withDefaults(defineProps<Props>(), { index: -1, keys: '' });

const formRef = ref();

// Form layout configuration
const labelCol = { span: 9 };
const wrapperCol = { span: 15 };

// Reactive form data for LDAP user configuration
const form: any = reactive({
  firstNameAttribute: '',           // LDAP attribute for user's first name
  emailAttribute: '',              // LDAP attribute for user's email
  mobileAttribute: '',             // LDAP attribute for user's mobile number
  userIdAttribute: '',             // LDAP attribute for user's unique ID
  objectClass: '',                 // LDAP object class for user entries
  objectFilter: '',                // LDAP filter for user search
  passwordAttribute: '',           // LDAP attribute for user's password
  usernameAttribute: '',           // LDAP attribute for user's username
  ignoreSameIdentityUser: true     // Flag to ignore duplicate identity users
});

// Form validation rules for required fields
const rules = {
  objectClass: [{ required: true, message: t('ldap.user-1') }],
  objectFilter: [{ required: true, message: t('ldap.user-2') }],
  userIdAttribute: [{ required: true, message: t('ldap.user-3') }],
  passwordAttribute: [{ required: true, message: t('ldap.user-8') }],
  displayNameAttribute: [{ required: true, message: t('ldap.validation.displayNameAttributeRequired') }],
  firstNameAttribute: [{ required: true, message: t('ldap.validation.userFirstNameAttributeRequired') }],
  lastNameAttribute: [{ required: true, message: t('ldap.validation.userLastNameAttributeRequired') }],
  usernameAttribute: [{ required: true, message: t('ldap.validation.userUidAttributeRequired') }],
  emailAttribute: [{ required: true, message: t('ldap.validation.userEmailAttributeRequired') }]
};

/**
 * Execute form validation and emit result to parent
 * Validates form fields and sends success/error status with form data
 */
const childRules = function () {
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
      form[key] = val[key];
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
