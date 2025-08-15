<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { Hints, Input } from '@xcan-angus/vue-ui';
import { Form, FormItem } from 'ant-design-vue';

import { useI18n } from 'vue-i18n';
import { LdapComponentProps, MembershipConfig, FormValidationRules } from '../types';
import {
  createFormLayoutConfig, createMembershipValidationRules,
  createInitialMembershipConfig, updateMembershipValidationRules
} from '../utils';

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

// Reactive form data for LDAP membership configuration
const form = reactive<MembershipConfig>(createInitialMembershipConfig());

// Form validation rules with dynamic required field logic
const rules = reactive<FormValidationRules>(createMembershipValidationRules(t));

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
 * Watch form changes to dynamically update required field validation
 * Makes fields required only when at least one membership configuration field is filled
 */
watch(() => form, (val) => {
  updateMembershipValidationRules(rules, val);
}, { deep: true, immediate: true });

/**
 * Watch for query changes to populate form fields
 * Automatically fills form when editing existing configuration
 */
watch(() => props.query, (val) => {
  if (val) {
    Object.keys(val).forEach((key: string) => {
      form[key as keyof MembershipConfig] = val[key];
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
    :model="form"
    :rules="rules"
    :colon="false"
    size="small">
    <!-- Group Member Attribute Field -->
    <div class="flex">
      <FormItem
        class="w-150"
        :label="t('ldap.member-label-1')"
        name="groupMemberAttribute">
        <Input
          v-model:value="form.groupMemberAttribute"
          :maxlength="160"
          :placeholder="t('ldap.member-1')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('ldap.member-mess-1')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>

    <!-- Member Group Attribute Field -->
    <div class="flex">
      <FormItem
        class="w-150"
        :label="t('ldap.member-label-2')"
        name="memberGroupAttribute">
        <Input
          v-model:value="form.memberGroupAttribute"
          :maxlength="160"
          :placeholder="t('ldap.member-2')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('ldap.member-mess-2')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
  </Form>
</template>
