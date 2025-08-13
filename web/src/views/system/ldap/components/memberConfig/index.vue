<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { Hints, Input } from '@xcan-angus/vue-ui';
import { Form, FormItem } from 'ant-design-vue';

import { useI18n } from 'vue-i18n';

/**
 * Component props interface for member configuration
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

const props = withDefaults(defineProps<Props>(), { index: -1, keys: '' });

const { t } = useI18n();
const formRef = ref();

// Form layout configuration
const labelCol = { span: 9 };

// Reactive form data for LDAP membership configuration
const form: any = reactive({
  groupMemberAttribute: '', // LDAP attribute for group membership (user -> group)
  memberGroupAttribute: '' // LDAP attribute for member groups (group -> user)
});

// Form validation rules with dynamic required field logic
const rules = reactive({
  groupMemberAttribute: [{ required: false, message: t('ldap.member-label-1') }],
  memberGroupAttribute: [{ required: false, message: t('ldap.member-label-2') }]
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
 * Makes fields required only when at least one membership configuration field is filled
 */
watch(() => form, (val) => {
  // Check if all membership configuration fields are empty
  const isNull = Object.keys(val).every((key) => form[key] === '');

  // Update required validation based on whether any membership config is provided
  rules.groupMemberAttribute[0].required = !isNull;
  rules.memberGroupAttribute[0].required = !isNull;
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
