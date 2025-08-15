<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { Hints, Input } from '@xcan-angus/vue-ui';
import { Checkbox, Form, FormItem } from 'ant-design-vue';

import { useI18n } from 'vue-i18n';
import { LdapComponentProps, GroupSchemaConfig, FormValidationRules } from '../types';
import {
  createFormLayoutConfig, createGroupSchemaValidationRules,
  createInitialGroupSchemaConfig, updateGroupSchemaValidationRules
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

// Reactive form data for LDAP group configuration
const form = reactive<GroupSchemaConfig>(createInitialGroupSchemaConfig());

// Form validation rules with dynamic required field logic
const rules = reactive<FormValidationRules>(createGroupSchemaValidationRules(t));

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
 * Makes fields required only when at least one group configuration field is filled
 */
watch(() => form, (val) => {
  updateGroupSchemaValidationRules(rules, val);
}, { deep: true, immediate: true });

/**
 * Watch for query changes to populate form fields
 * Automatically fills form when editing existing configuration
 */
watch(() => props.query, (val) => {
  if (val) {
    Object.keys(val).forEach((key: string) => {
      form[key as keyof GroupSchemaConfig] = val[key];
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
    <!-- Object Class Field -->
    <div class="flex">
      <FormItem
        class="w-150"
        :label="t('ldap.config-5')"
        name="objectClass">
        <Input
          v-model:value="form.objectClass"
          :placeholder="t('ldap.config-1')"
          :maxlength="400"
          size="small" />
      </FormItem>
      <Hints
        :text="t('ldap.config-mess-1')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>

    <!-- Object Filter Field -->
    <div class="flex">
      <FormItem
        id="group-objectFilter"
        class="w-150"
        :label="t('ldap.config-6')"
        name="objectFilter">
        <Input
          v-model:value="form.objectFilter"
          :maxlength="400"
          :placeholder="t('ldap.config-2')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('ldap.config-mess-2')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>

    <!-- Name Attribute Field -->
    <div class="flex">
      <FormItem
        id="group-nameAttribute"
        class="w-150"
        :label="t('ldap.config-7')"
        name="nameAttribute">
        <Input
          v-model:value="form.nameAttribute"
          :maxlength="160"
          :placeholder="t('ldap.config-3')" />
      </FormItem>
      <Hints
        :text="t('ldap.config-mess-3')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>

    <!-- Description Attribute Field -->
    <div class="flex">
      <FormItem
        class="w-150"
        :label="t('ldap.config-8')">
        <Input
          v-model:value="form.descriptionAttribute"
          :maxlength="160"
          :placeholder="t('ldap.config-4')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('ldap.config-mess-4')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>

    <!-- Ignore Same Name Group Field -->
    <FormItem
      class="w-225"
      :labelCol="{span: 6}"
      :label="t('ldap.labels.ignoreSameNameGroup')">
      <div class="flex">
        <Checkbox v-model:checked="form.ignoreSameNameGroup" />
        <Hints
          :text="t('ldap.messages.ignoreSameNameGroupTip')"
          style="transform: translateY(3px);"
          class="ml-2" />
      </div>
    </FormItem>
  </Form>
</template>
