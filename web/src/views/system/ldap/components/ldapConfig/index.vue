<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { Hints, Input } from '@xcan-angus/vue-ui';
import { Form, FormItem } from 'ant-design-vue';
import { useI18n } from 'vue-i18n';

/**
 * Component props interface for LDAP configuration
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

// Reactive form data for LDAP base configuration
const form: any = reactive({
  baseDn: '', // Base Distinguished Name for LDAP search
  additionalUserDn: '', // Additional user DN for extended user search
  additionalGroupDn: '' // Additional group DN for extended group search
});

// Form validation rules
const rules = {
  baseDn: [{ required: true, message: t('ldap.model-1') }]
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
