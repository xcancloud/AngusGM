<script setup lang='ts'>
import { defineAsyncComponent, inject, onMounted, reactive, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { Button, Form, FormItem, InputPassword, Popover, Radio, RadioGroup } from 'ant-design-vue';
import { Cropper, Icon, Image, Input, notification, PureCard, SelectItc } from '@xcan-angus/vue-ui';
import { EnumMessage, Gender, appContext, duration, enumUtils, itc, passwordUtils, regexpUtils, utils } from '@xcan-angus/infra';
import { debounce } from 'throttle-debounce';

import { FormState } from '../../PropsType';

import { user } from '@/api';

const PasswdTip = defineAsyncComponent(() => import('@/views/organization/user/components/passwordTip/index.vue'));

type Fuc = (args: Record<string, any>) => void;

const route = useRoute();
const router = useRouter();
const { t } = useI18n();
const formRef = ref();

const userId = ref<string>(route.params.id as string);
const source = ref<'home' | 'detail'>(route.query.source as 'home' | 'detail');
const formState = ref<FormState>({
  address: '',
  avatar: '',
  country: 'CN',
  email: '',
  firstName: '',
  fullName: '',
  gender: 'UNKNOWN',
  itc: '86',
  landline: '',
  lastName: '',
  mobile: '',
  password: '',
  sysAdmin: false,
  title: '',
  username: '',
  confirmPassword: ''
});
const state = reactive<{ length: boolean, chart: boolean }>({ length: false, chart: false });

const loading = ref(false);
const addUser = async (isContinueAdd?: boolean) => {
  if (loading.value) {
    return;
  }

  const _firstName = formState.value.firstName;
  const _lastName = formState.value.lastName;
  const _fullName = formState.value.fullName;
  if (regexpUtils.hasZh(_firstName) || regexpUtils.hasZh(_lastName)) {
    if (!_fullName) {
      formState.value.fullName = _lastName + _firstName;
    }
  } else {
    if (!_fullName) {
      formState.value.fullName = _firstName + ' ' + _lastName;
    }
  }

  if (!formState.value.mobile) {
    formState.value.mobile = null;
  }

  delete formState.value.confirmPassword;
  loading.value = true;
  const [error] = await user.addUser(formState.value);
  loading.value = false;
  if (error) {
    return;
  }
  notification.success('添加成功');
  // 继续添加不跳转
  if (isContinueAdd) {
    formRef.value.resetFields();
    return;
  }
  router.push('/organization/user');
};

const updateUserInfo: Fuc | undefined = inject('updateUserInfo');
const patchGroup = async () => {
  if (loading.value) {
    return;
  }
  const isEqual = utils.deepCompare(oldDetail.value as FormState, formState.value);
  if (isEqual) {
    router.push(source.value === 'home' ? '/organization/user' : `/organization/user/${userId.value}`);
    return;
  }

  if (formState.value.confirmPassword === '********' || !formState.value.confirmPassword) {
    delete formState.value.confirmPassword;
  }

  if (!formState.value.mobile) {
    formState.value.mobile = null;
  }

  const { password, ...others } = formState.value;
  let params = {};
  if (password === '********' || !password) {
    params = {
      ...others,
      id: userId.value
    };
  } else {
    params = {
      ...formState.value,
      id: userId.value
    };
  }
  loading.value = true;
  const [error] = await user.updateUser(params);
  loading.value = false;
  if (error) {
    return;
  }
  notification.success('修改成功');
  if (userId.value && userId.value === appContext.getUser()?.id) {
    if (typeof updateUserInfo === 'function') {
      const temp = {
        ...appContext.getUser(),
        ...params
      };
      updateUserInfo(temp);
    }
  }
  router.push(source.value === 'home' ? '/organization/user' : `/organization/user/${userId.value}`);
};

// 提交表单
const onFinish = () => {
  if (userId.value) {
    patchGroup();
    return;
  }
  addUser(false);
};

const continueAdd = () => {
  formRef.value.validate().then(async () => {
    addUser(true);
  });
};

const uploadParams = {
  bizKey: 'avatar'
};
const uploadVisible = ref(false);
const openUploadModal = () => {
  uploadVisible.value = true;
};

// 上传成功
const uploadSuccess = (value) => {
  formState.value.avatar = value?.data[0]?.url;
  uploadVisible.value = false;
};

const uploadError = () => {
  uploadVisible.value = false;
};

const passwordRule = ref({
  required: !userId.value, validator: (rule, value) => passwordValidator(rule, value)
});
const passwordValidator = (_rule, value) => {
  if (userId.value && (value === '********' || !value)) {
    return Promise.resolve();
  }

  if (!value) {
    return Promise.reject(new Error(t('userRule0')));
  }

  if (!state.length || !state.chart) {
    return Promise.reject(new Error(t('userRule4')));
  }

  return Promise.resolve();
};

const confirmPasswordRule = ref({
  required: !userId.value, validator: (rule, value) => confirmPasswordValidator(rule, value)
});
const confirmPasswordValidator = (_rule, value) => {
  if (userId.value && (value === '********' || !formState.value.password || formState.value.password === '********')) {
    return Promise.resolve();
  }

  if (!value) {
    return Promise.reject(new Error(t('userRule1')));
  }

  if (formState.value.password !== value) {
    return Promise.reject(new Error(t('userRule2')));
  }
  return Promise.resolve();
};

watch(() => formState.value.password, (newValue) => {
  if (!userId.value) {
    formState.value.confirmPassword = undefined;
    return;
  }

  // 编辑页面下 如果用户输入了密码 开启校验
  if (newValue !== '********' && newValue) {
    passwordRule.value.required = true;
    confirmPasswordRule.value.required = true;
    formState.value.confirmPassword = undefined;
  }

  // 如果输入为空，认为没有修改，并关闭校验
  if (!newValue || newValue === '********') {
    passwordRule.value.required = false;
    confirmPasswordRule.value.required = false;
    formRef.value.clearValidate('password');
    formRef.value.clearValidate('confirmPassword');
  }
});

watch(() => formState.value.confirmPassword, () => {
  if (!userId.value || !formState.value.password || formState.value.password === '********') {
    return;
  }

  confirmPasswordRule.value.required = true;
});

const emailRule = ref({
  required: true, validator: (rule, value) => emailValidator(rule, value)
});
const emailValidator = (_rule, value) => {
  if (!value) {
    return Promise.reject(new Error(t('userRule5')));
  }
  const isEmail = regexpUtils.isEmail(value);
  if (!isEmail) {
    return Promise.reject(new Error(t('userRule6')));
  }
  return Promise.resolve();
};

const mobileRule = ref({
  required: false, validator: (rule, value) => mobileValidator(rule, value)
});
const mobileValidator = (_rule, value) => {
  if (!value) {
    return Promise.resolve();
  } else if (!regexpUtils.isMobileNumber(formState.value.country || 'CN', value)) {
    return Promise.reject(new Error(t('userRule8')));
  }
  return Promise.resolve();
};

const userSource = ref();
const oldDetail = ref<FormState>();
const loadUserDetail = async () => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const [error, { data }] = await user.getUserDetail(userId.value);
  loading.value = false;
  if (error) {
    return;
  }
  userSource.value = data.source?.value;
  Object.keys(formState.value).every(item => {
    if (item === 'gender') {
      formState.value[item] = data[item].value;
    } else if (['password', 'confirmPassword'].includes(item)) {
      formState.value[item] = '********';
    } else {
      formState.value[item] = data[item];
    }
    return true;
  });

  oldDetail.value = JSON.parse(JSON.stringify(formState.value));
};

const changeStrength = (e: { target: { value: string } }) => {
  const { value = '' } = e.target;
  const valArr = value.split('');
  const typeNum = passwordUtils.getTypesNum(valArr);
  state.length = value.length >= 6 && value.length <= 50;
  state.chart = typeNum >= 2;
};

const changeItc = (value: string) => {
  formState.value.itc = value;
  const item: { value: string, code: string } | undefined = itc.find((v: { value: string }) => v.value === value);
  formState.value.country = item ? item.code : 'country';
};

const userGender = ref<EnumMessage<Gender>[]>([]);
const loadUserGender = async () => {
  userGender.value = enumUtils.enumToMessages(Gender);
};

const firstNameChange = debounce(duration.search, (event: any) => {
  const value = event.target.value;
  const _lastName = formState.value.lastName;
  if (!value || !_lastName || formState.value.fullName) {
    return;
  }

  if (regexpUtils.hasZh(value) || regexpUtils.hasZh(_lastName)) {
    formState.value.fullName = _lastName + value;
    formRef.value.clearValidate('fullName');
    return;
  }

  formState.value.fullName = value + ' ' + _lastName;
  formRef.value.clearValidate('fullName');
});

const lastNameChange = debounce(duration.search, (event: any) => {
  const value = event.target.value;
  const _firstName = formState.value.firstName;
  if (!value || !_firstName || formState.value.fullName) {
    return;
  }

  if (regexpUtils.hasZh(value) || regexpUtils.hasZh(_firstName)) {
    formState.value.fullName = value + _firstName;
    formRef.value.clearValidate('fullName');
    return;
  }

  formState.value.fullName = _firstName + ' ' + value;
  formRef.value.clearValidate('fullName');
});

onMounted(() => {
  if (userId.value) {
    loadUserDetail();
  }

  loadUserGender();
});
</script>
<template>
  <PureCard class="p-15 min-h-full">
    <Form
      ref="formRef"
      size="small"
      layout="vertical"
      :scrollToFirstError="true"
      :model="formState"
      @finish="onFinish">
      <div class="flex space-x-10 5xl:space-x-20">
        <FormItem
          :labelCol="{span: 0}"
          :wrapperCol="{span: 24}"
          name="avatar">
          <Cropper
            v-model:visible="uploadVisible"
            :params="uploadParams"
            @success="uploadSuccess"
            @error="uploadError" />
          <Image
            class="w-25 h-25 rounded-full"
            type="avatar"
            :src="formState.avatar" />
          <Button
            size="small"
            class="mt-6 text-3 leading-3 flex items-center mx-auto"
            @click="openUploadModal">
            <Icon icon="icon-shangchuan" class="text-3 leading-3 mr-1" />
            <span>{{ t('uploadAvatar') }}</span>
          </Button>
        </FormItem>
        <div class="flex flex-1 space-x-10 5xl:space-x-20">
          <div class="w-2/4 align-top flex-1">
            <div class="flex flex-1 -mt-0.5 space-x-2">
              <FormItem
                :label="t('名')"
                :rules="[{ required: true, message:t('userRule9') }]"
                name="firstName"
                class="w-1/2">
                <Input
                  v-model:value="formState.firstName"
                  size="small"
                  :disabled="userId && userSource === 'LDAP_SYNCHRONIZE'"
                  :maxlength="100"
                  :placeholder="t('userPlaceholder2')"
                  @change="firstNameChange" />
              </FormItem>
              <FormItem
                :label="t('姓')"
                :rules="[{ required: true, message:t('userRule10') }]"
                name="lastName"
                class="w-1/2">
                <Input
                  v-model:value="formState.lastName"
                  size="small"
                  :disabled="userId && userSource === 'LDAP_SYNCHRONIZE'"
                  :maxlength="100"
                  :placeholder="t('userPlaceholder3')"
                  @change="lastNameChange" />
              </FormItem>
            </div>
            <FormItem
              :label="t('name2')"
              :rules="[{ required: true, message: t('请输入姓名') }]"
              name="fullName">
              <Input
                v-model:value="formState.fullName"
                size="small"
                :disabled="userId && userSource === 'LDAP_SYNCHRONIZE'"
                :maxlength="100"
                :placeholder="t('name2')" />
            </FormItem>
            <FormItem
              :label="t('userName')"
              name="username"
              :rules="[{ required: true, message:t('userRule11') }]">
              <Input
                v-model:value="formState.username"
                size="small"
                :maxlength="100"
                :placeholder="t('pubPlaceholder1',{name:t('userName'),num:100,chart:' - _ . '})"
                dataType="mixin-en"
                includes="-_." />
            </FormItem>
            <FormItem
              :label="t('mobileNumber')"
              name="mobile"
              :rules="mobileRule">
              <Input
                v-model:value="formState.mobile"
                size="small"
                :maxlength="11"
                :disabled="userId && userSource === 'LDAP_SYNCHRONIZE'"
                :placeholder="t('userPlaceholder4')">
                <template #addonBefore>
                  <SelectItc
                    :value="formState.itc"
                    :disabled="userId && userSource === 'LDAP_SYNCHRONIZE'"
                    style="width: 100px;"
                    internal
                    size="small"
                    @change="changeItc" />
                </template>
              </Input>
            </FormItem>
            <FormItem :label="t('landline')" name="landline">
              <Input
                v-model:value="formState.landline"
                size="small"
                :maxlength="40"
                :placeholder="t('userPlaceholder5')" />
            </FormItem>
            <FormItem
              :label="t('email')"
              name="email"
              :rules="emailRule">
              <Input
                v-model:value="formState.email"
                size="small"
                :disabled="userId && userSource === 'LDAP_SYNCHRONIZE'"
                :maxlength="100"
                :placeholder="t('userPlaceholder6')" />
            </FormItem>
            <FormItem>
              <Button
                :loading="loading"
                type="primary"
                size="small"
                htmlType="submit"
                class="px-3">
                {{ t('submit') }}
              </Button>
              <template v-if="!userId">
                <Button
                  :loading="loading"
                  size="small"
                  class="ml-5"
                  @click="continueAdd">
                  {{ t('keepAdd') }}
                </Button>
              </template>
              <RouterLink :to="source === 'home'?'/organization/user':`/organization/user/${userId}`">
                <Button class="ml-5" size="small">{{ t('cancel') }}</Button>
              </RouterLink>
            </FormItem>
          </div>
          <div class="inline-block w-2/4 align-top">
            <FormItem :label="t('gender')" name="gender">
              <RadioGroup v-model:value="formState.gender" size="small">
                <Radio
                  v-for="item in userGender"
                  :key="item.value"
                  :value="item.value">
                  {{ item.message }}
                </Radio>
              </RadioGroup>
            </FormItem>
            <FormItem
              v-if="!userId"
              :label="t('password')"
              :rules="passwordRule"
              name="password">
              <Popover placement="leftTop" trigger="focus">
                <template #content>
                  <div v-if="passwordRule.required" class="w-35">
                    <PasswdTip :length="state.length" :chart="state.chart" />
                  </div>
                </template>
                <InputPassword
                  v-model:value="formState.password"
                  size="small"
                  :disabled="userId && userSource === 'LDAP_SYNCHRONIZE'"
                  :maxlength="50"
                  :placeholder="t('userPlaceholder7')"
                  autocomplete="new-password"
                  @change="changeStrength">
                </InputPassword>
              </Popover>
            </FormItem>
            <FormItem
              v-if="!userId"
              :label="t('confirmPassword')"
              :rules="confirmPasswordRule"
              name="confirmPassword">
              <InputPassword
                v-model:value="formState.confirmPassword"
                size="small"
                :disabled="userId && userSource === 'LDAP_SYNCHRONIZE'"
                :maxlength="50"
                :placeholder="t('userPlaceholder8')">
              </InputPassword>
            </FormItem>
            <FormItem :label="t('position')" name="title">
              <Input
                v-model:value="formState.title"
                size="small"
                :maxlength="100"
                :placeholder="t('userPlaceholder9')" />
            </FormItem>
            <FormItem :label="t('address')" name="address">
              <Input
                v-model:value="formState.address"
                size="small"
                :maxlength="200"
                :placeholder="t('userPlaceholder10')" />
            </FormItem>
            <FormItem :label="t('systemIdentity')" name="sysAdmin">
              <RadioGroup
                v-model:value="formState.sysAdmin"
                size="small"
                :disabled="!appContext.isSysAdmin()">
                <Radio :value="false">{{ t('generalUsers') }}</Radio>
                <Radio :value="true">{{ t('systemAdministrator') }}</Radio>
              </RadioGroup>
            </FormItem>
          </div>
        </div>
      </div>
    </Form>
  </PureCard>
</template>
<style scoped>
:deep(.ant-input-group-addon) {
  border-color: var(--border-text-box);
}
</style>
