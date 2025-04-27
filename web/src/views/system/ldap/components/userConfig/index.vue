<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { Hints, Input } from '@xcan-angus/vue-ui';
import { Checkbox, Form, FormItem } from 'ant-design-vue';

import { useI18n } from 'vue-i18n';

interface Props {
  index: number,
  keys: string,
  query?: any
}

const emit = defineEmits(['rules']);

const { t } = useI18n();

const props = withDefaults(defineProps<Props>(), { index: -1, keys: '' });

const formRef = ref();

const labelCol = { span: 9 };
const wrapperCol = { span: 15 };
const form: any = reactive({
  firstNameAttribute: '',
  emailAttribute: '',
  mobileAttribute: '',
  userIdAttribute: '',
  objectClass: '',
  objectFilter: '',
  passwordAttribute: '',
  usernameAttribute: '',
  ignoreSameIdentityUser: true
});
const rules = {
  objectClass: [{ required: true, message: t('systemLdap.user-1') }],
  objectFilter: [{ required: true, message: t('systemLdap.user-2') }],
  userIdAttribute: [{ required: true, message: t('systemLdap.user-3') }],
  passwordAttribute: [{ required: true, message: t('systemLdap.user-8') }],
  displayNameAttribute: [{ required: true, message: '请输入用户姓名属性' }],
  firstNameAttribute: [{ required: true, message: '请输入用户名字属性' }],
  lastNameAttribute: [{ required: true, message: '请输入用户姓氏属性' }],
  usernameAttribute: [{ required: true, message: '请输入用户全名名属性' }],
  emailAttribute: [{ required: true, message: '请输入邮箱属性' }]
};

const childRules = function () {
  formRef.value.validate().then(() => {
    emit('rules', 'success', props.keys, props.index, form);
  }).catch(() => {
    emit('rules', 'error', props.keys, props.index, form);
  });
};

// 回显
watch(() => props.query, (val) => {
  if (val) {
    Object.keys(val).forEach((key: string) => {
      form[key] = val[key];
    });
  }
});
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
        :label="t('systemLdap.user-label-1')"
        name="objectClass"
        class="w-150">
        <Input
          v-model:value="form.objectClass"
          :maxlength="400"
          :placeholder="t('systemLdap.user-1')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('systemLdap.user-mess-1')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <div class="flex">
      <FormItem
        :label="t('systemLdap.user-label-2')"
        name="objectFilter"
        class="w-150">
        <Input
          v-model:value="form.objectFilter"
          :maxlength="400"
          :placeholder="t('systemLdap.user-2')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('systemLdap.user-mess-2')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <div class="flex">
      <FormItem
        :label="t('systemLdap.user-label-3')"
        name="usernameAttribute"
        class="w-150">
        <Input
          v-model:value="form.usernameAttribute"
          :maxlength="160"
          :placeholder="t('systemLdap.user-3')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('systemLdap.user-mess-3')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <div class="flex">
      <FormItem
        :label="t('systemLdap.user-label-4')"
        name="firstNameAttribute"
        class="w-150">
        <Input
          v-model:value="form.firstNameAttribute"
          :maxlength="160"
          :placeholder="t('systemLdap.user-4')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('systemLdap.user-mess-4')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <div class="flex">
      <FormItem
        :label="t('systemLdap.user-label-5')"
        name="lastNameAttribute"
        class="w-150">
        <Input
          v-model:value="form.lastNameAttribute"
          :maxlength="160"
          :placeholder="t('systemLdap.user-5')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('systemLdap.user-mess-5')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <div class="flex">
      <FormItem
        :label="t('全名')"
        name="displayNameAttribute"
        class="w-150">
        <Input
          v-model:value="form.displayNameAttribute"
          :maxlength="160"
          :placeholder="t('systemLdap.user-5')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('用户全名或昵称对应的属性字段，例如：cn。')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <div class="flex">
      <FormItem
        :label="t('systemLdap.user-label-6')"
        name="mobileAttribute"
        class="w-150">
        <Input
          v-model:value="form.mobileAttribute"
          :maxlength="160"
          :placeholder="t('systemLdap.user-6')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('systemLdap.user-mess-6')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <div class="flex">
      <FormItem
        :label="t('systemLdap.user-label-7')"
        name="emailAttribute"
        class="w-150">
        <Input
          v-model:value="form.emailAttribute"
          :maxlength="160"
          size="small"
          :placeholder="t('systemLdap.user-7')" />
      </FormItem>
      <Hints
        :text="t('systemLdap.user-mess-7')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <div class="flex">
      <FormItem
        :label="t('systemLdap.user-label-8')"
        name="passwordAttribute"
        class="w-150">
        <Input
          v-model:value="form.passwordAttribute"
          :maxlength="160"
          :placeholder="t('systemLdap.user-8')"
          size="small" />
      </FormItem>
      <Hints
        :text="t('systemLdap.user-mess-8')"
        style="transform: translateY(7px);"
        class="ml-2" />
    </div>
    <FormItem
      label="存在相同用户时是否忽略"
      class="w-150"
      name="ignoreSameIdentityUser">
      <div class="flex items-center">
        <Checkbox :checked="form.ignoreSameIdentityUser" disabled></Checkbox>
        <Hints
          :text="t('当存在相同用户唯一标识(用户名,邮箱,手机号)时强制忽略')"
          style="transform: translateY(1px);"
          class="ml-2" />
      </div>
    </FormItem>
  </Form>
</template>
