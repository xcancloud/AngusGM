<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { Hints, Input } from '@xcan-angus/vue-ui';
import { Checkbox, Form, FormItem } from 'ant-design-vue';

import { useI18n } from 'vue-i18n';

/**
 * Component props interface for group configuration
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

// Reactive form data for LDAP group configuration
const form: any = reactive({
  descriptionAttribute: '',         // LDAP attribute for group description
  objectFilter: '',                // LDAP filter for group search
  nameAttribute: '',               // LDAP attribute for group name
  objectClass: '',                 // LDAP object class for group entries
  ignoreSameNameGroup: true        // Flag to ignore duplicate name groups
});

// Form validation rules with dynamic required field logic
const rules = reactive({
  objectFilter: [{ required: false, message: t('ldap.config-2') }],
  nameAttribute: [{ required: false, message: t('ldap.config-3') }],
  objectClass: [{ required: false, message: t('ldap.validation.groupObjectClassRequired') }]
});

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
 * Watch form changes to dynamically update required field validation
 * Makes fields required only when at least one group configuration field is filled
 */
watch(() => form, (val) => {
  // Check if all group configuration fields are empty (except ignoreSameNameGroup)
  const isNull = Object.keys(val).every((key) =>
    (key !== 'ignoreSameNameGroup' && form[key] === '') || key === 'ignoreSameNameGroup'
  );

  // Update required validation based on whether any group config is provided
  rules.objectFilter[0].required = !isNull;
  rules.nameAttribute[0].required = !isNull;
  rules.objectClass[0].required = !isNull;
}, { deep: true, immediate: true });

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
    :model="form"
    :rules="rules"
    :colon="false"
    size="small">
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
