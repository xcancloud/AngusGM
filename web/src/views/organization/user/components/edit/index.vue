<script setup lang='ts'>
import { defineAsyncComponent, inject, onMounted, reactive, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { Button, Form, FormItem, InputPassword, Popover, Radio, RadioGroup } from 'ant-design-vue';
import { Cropper, Icon, Image, Input, notification, PureCard, SelectItc } from '@xcan-angus/vue-ui';
import {
  EnumMessage, Gender, appContext, duration, enumUtils, itc, passwordUtils,
  regexpUtils, utils, UserSource
} from '@xcan-angus/infra';
import { debounce } from 'throttle-debounce';

import { FormState } from '../../PropsType';

import { user } from '@/api';

/**
 * Async component for password strength tips
 * Loaded only when needed to improve performance
 */
const PasswordTip = defineAsyncComponent(() => import('@/views/organization/user/components/passwordTip/index.vue'));

/**
 * Function type for updating user information
 * Used for dependency injection pattern
 */
type Fuc = (args: Record<string, any>) => void;

// TODO: User edit page and list table font sizes are inconsistent, should we unify UI component font sizes to 12px?

// Routing and internationalization setup
const route = useRoute();
const router = useRouter();
const { t } = useI18n();
const formRef = ref();

/**
 * Reactive state management for component
 */
const userId = ref<string>(route.params.id as string); // Current user ID for editing
const source = ref<'home' | 'detail'>(route.query.source as 'home' | 'detail'); // Source page for navigation
const formState = ref<FormState>({
  address: '',
  avatar: '',
  country: 'CN',
  email: '',
  firstName: '',
  fullName: '',
  gender: Gender.UNKNOWN,
  itc: '86',
  landline: '',
  lastName: '',
  mobile: null,
  password: '',
  sysAdmin: false,
  title: '',
  username: '',
  confirmPassword: undefined
});

/**
 * Password strength validation state
 */
const state = reactive<{ length: boolean, chart: boolean }>({
  length: false, // Password length validation status
  chart: false // Password complexity validation status
});

const loading = ref(false); // Loading state for form submission

/**
 * Add new user to system
 * Handles form validation, API call, and navigation
 * @param isContinueAdd - Whether to continue adding after successful submission
 */
const addUser = async (isContinueAdd?: boolean) => {
  if (loading.value) {
    return;
  }

  // Auto-generate full name from first and last name
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

  // Handle null mobile number
  if (!formState.value.mobile) {
    formState.value.mobile = null;
  }

  // Remove confirmation password from submission
  delete formState.value.confirmPassword;
  loading.value = true;
  const [error] = await user.addUser(formState.value);
  loading.value = false;
  if (error) {
    return;
  }
  notification.success(t('common.messages.addSuccess'));

  // Continue adding without navigation
  if (isContinueAdd) {
    formRef.value.resetFields();
    return;
  }
  router.push('/organization/user');
};

/**
 * Injected function for updating user information
 * TODO: Entry point not found
 */
const updateUserInfo: Fuc | undefined = inject('updateUserInfo'); // TODO 没有找到入口

/**
 * Update existing user information
 * Handles form validation, API call, and navigation
 */
const patchGroup = async () => {
  if (loading.value) {
    return;
  }

  // Check if form data has changed
  const isEqual = utils.deepCompare(oldDetail.value as FormState, formState.value);
  if (isEqual) {
    router.push(source.value === 'home' ? '/organization/user' : `/organization/user/${userId.value}`);
    return;
  }

  // Handle password field for updates
  if (formState.value.confirmPassword === '********' || !formState.value.confirmPassword) {
    delete formState.value.confirmPassword;
  }

  if (!formState.value.mobile) {
    formState.value.mobile = null;
  }

  // Prepare update parameters
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
  notification.success(t('common.messages.editSuccess'));

  // Update current user context if editing own profile
  if (userId.value && userId.value === appContext.getUser()?.id?.toString()) {
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

/**
 * Handle form submission
 * Routes to appropriate function based on whether adding or editing
 */
const onFinish = () => {
  if (userId.value) {
    patchGroup();
    return;
  }
  addUser(false);
};

/**
 * Continue adding users after successful submission
 * Validates form and calls addUser with continue flag
 */
const continueAdd = () => {
  formRef.value.validate().then(async () => {
    addUser(true);
  });
};

/**
 * Avatar upload configuration and state
 */
const uploadParams = {
  bizKey: 'avatar'
};
const uploadVisible = ref(false); // Avatar upload modal visibility

/**
 * Open avatar upload modal
 */
const openUploadModal = () => {
  uploadVisible.value = true;
};

/**
 * Handle successful avatar upload
 * @param value - Upload response data
 */
const uploadSuccess = (value) => {
  formState.value.avatar = value?.data[0]?.url;
  uploadVisible.value = false;
};

/**
 * Handle avatar upload error
 */
const uploadError = () => {
  uploadVisible.value = false;
};

/**
 * Password validation rule configuration
 * Dynamic validation based on whether editing or creating
 */
const passwordRule = ref({
  required: !userId.value,
  validator: (rule, value) => passwordValidator(rule, value)
});

/**
 * Password validation function
 * Validates password requirements and strength
 */
const passwordValidator = (_rule, value) => {
  if (userId.value && (value === '********' || !value)) {
    return Promise.resolve();
  }

  if (!value) {
    return Promise.reject(new Error(t('user.validation.passwordRequired')));
  }

  if (!state.length || !state.chart) {
    return Promise.reject(new Error(t('user.validation.passwordNotMeetRule')));
  }

  return Promise.resolve();
};

/**
 * Password confirmation validation rule configuration
 */
const confirmPasswordRule = ref({
  required: !userId.value,
  validator: (rule, value) => confirmPasswordValidator(rule, value)
});

/**
 * Password confirmation validation function
 * Ensures confirmation matches password
 */
const confirmPasswordValidator = (_rule, value) => {
  if (userId.value && (value === '********' || !formState.value.password || formState.value.password === '********')) {
    return Promise.resolve();
  }

  if (!value) {
    return Promise.reject(new Error(t('user.validation.passwordConfirmRequired')));
  }

  if (formState.value.password !== value) {
    return Promise.reject(new Error(t('user.validation.passwordConfirmNotMatch')));
  }
  return Promise.resolve();
};

/**
 * Watch password changes to update validation rules
 * Handles dynamic validation for edit mode
 */
watch(() => formState.value.password, (newValue) => {
  if (!userId.value) {
    formState.value.confirmPassword = undefined;
    return;
  }

  // Enable validation when user enters password in edit mode
  if (newValue !== '********' && newValue) {
    passwordRule.value.required = true;
    confirmPasswordRule.value.required = true;
    formState.value.confirmPassword = undefined;
  }

  // Disable validation when password is empty or unchanged
  if (!newValue || newValue === '********') {
    passwordRule.value.required = false;
    confirmPasswordRule.value.required = false;
    formRef.value.clearValidate('password');
    formRef.value.clearValidate('confirmPassword');
  }
});

/**
 * Watch password confirmation changes
 * Updates validation rules for confirmation field
 */
watch(() => formState.value.confirmPassword, () => {
  if (!userId.value || !formState.value.password || formState.value.password === '********') {
    return;
  }
  confirmPasswordRule.value.required = true;
});

/**
 * Email validation rule configuration
 */
const emailRule = ref({
  required: true,
  validator: (rule, value) => emailValidator(rule, value)
});

/**
 * Email validation function
 * Validates email format and requirements
 */
const emailValidator = (_rule, value) => {
  if (!value) {
    return Promise.reject(new Error(t('user.validation.emailRequired')));
  }
  const isEmail = regexpUtils.isEmail(value);
  if (!isEmail) {
    return Promise.reject(new Error(t('user.validation.emailInvalidFormat')));
  }
  return Promise.resolve();
};

/**
 * Mobile number validation rule configuration
 */
const mobileRule = ref({
  required: false,
  validator: (rule, value) => mobileValidator(rule, value)
});

/**
 * Mobile number validation function
 * Validates phone number format based on country
 */
const mobileValidator = (_rule, value) => {
  if (!value) {
    return Promise.resolve();
  } else if (!regexpUtils.isMobileNumber(formState.value.country || 'CN', value)) {
    return Promise.reject(new Error(t('user.validation.phoneInvalidFormat')));
  }
  return Promise.resolve();
};

/**
 * User source and original data for comparison
 */
const userSource = ref(); // User source (LDAP, manual, etc.)
const oldDetail = ref<FormState>(); // Original form data for change detection

/**
 * Load user detail information for editing
 * Populates form with existing user data
 */
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

  // Populate form fields with user data
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

/**
 * Analyze password strength and update validation status
 * Called on password input change
 * @param e - Input change event
 */
const changeStrength = (e: { target: { value: string } }) => {
  const { value = '' } = e.target;
  const valArr = value.split('');
  const typeNum = passwordUtils.getTypesNum(valArr);
  state.length = value.length >= 6 && value.length <= 50;
  state.chart = typeNum >= 2;
};

/**
 * Handle ITC (International Telephone Code) change
 * Updates country code when ITC changes
 * @param value - Selected ITC value
 */
const changeItc = (value: string) => {
  formState.value.itc = value;
  const item: { value: string, code: string } | undefined = itc.find((v: { value: string }) => v.value === value);
  formState.value.country = item ? item.code : 'country';
};

/**
 * User gender options and loading function
 */
const userGender = ref<EnumMessage<Gender>[]>([]);

/**
 * Load gender enum options
 */
const loadUserGender = async () => {
  userGender.value = enumUtils.enumToMessages(Gender);
};

/**
 * Handle first name changes with debouncing
 * Auto-generates full name from first and last name
 * @param event - Input change event
 */
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

/**
 * Handle last name changes with debouncing
 * Auto-generates full name from first and last name
 * @param event - Input change event
 */
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

/**
 * Lifecycle hook - initialize component on mount
 * Loads user data for editing and gender options
 */
onMounted(() => {
  if (userId.value) {
    loadUserDetail();
  }

  loadUserGender();
});
</script>
<template>
  <PureCard class="p-15 min-h-full">
    <!-- User edit form -->
    <Form
      ref="formRef"
      size="small"
      layout="vertical"
      :scrollToFirstError="true"
      :model="formState"
      @finish="onFinish">
      <div class="flex space-x-10 5xl:space-x-20">
        <!-- Avatar upload section -->
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
            <span>{{ t('user.uploadAvatarTip') }}</span>
          </Button>
        </FormItem>
        <!-- Form fields section -->
        <div class="flex flex-1 space-x-10 5xl:space-x-20">
          <!-- Left column - Basic information -->
          <div class="w-2/4 align-top flex-1">
            <!-- Name fields -->
            <div class="flex flex-1 -mt-0.5 space-x-2">
              <FormItem
                :label="t('user.profile.firstName')"
                :rules="[{ required: true, message:t('user.validation.firstNameRequired') }]"
                name="firstName"
                class="w-1/2">
                <Input
                  v-model:value="formState.firstName"
                  size="small"
                  :disabled="!!(userId && userSource === UserSource.LDAP_SYNCHRONIZE)"
                  :maxlength="100"
                  :placeholder="t('user.placeholder.firstName')"
                  @change="firstNameChange" />
              </FormItem>
              <FormItem
                :label="t('user.profile.lastName')"
                :rules="[{ required: true, message:t('user.validation.lastNameRequired') }]"
                name="lastName"
                class="w-1/2">
                <Input
                  v-model:value="formState.lastName"
                  size="small"
                  :disabled="!!(userId && userSource === UserSource.LDAP_SYNCHRONIZE)"
                  :maxlength="100"
                  :placeholder="t('user.placeholder.lastName')"
                  @change="lastNameChange" />
              </FormItem>
            </div>

            <!-- Full name field -->
            <FormItem
              :label="t('user.profile.fullName')"
              :rules="[{ required: true, message: t('user.validation.nameRequired') }]"
              name="fullName">
              <Input
                v-model:value="formState.fullName"
                size="small"
                :disabled="!!(userId && userSource === UserSource.LDAP_SYNCHRONIZE)"
                :maxlength="100"
                :placeholder="t('user.placeholder.fullName')" />
            </FormItem>

            <!-- Username field -->
            <FormItem
              :label="t('user.profile.username')"
              name="username"
              :rules="[{ required: true, message:t('user.validation.usernameRequired') }]">
              <Input
                v-model:value="formState.username"
                size="small"
                :maxlength="100"
                :placeholder="t('user.placeholder.username',{name:t('user.profile.username'),num:100,chart:' - _ . '})"
                dataType="mixin-en"
                includes="-_." />
            </FormItem>

            <!-- Mobile phone field -->
            <FormItem
              :label="t('user.profile.mobile')"
              name="mobile"
              :rules="mobileRule">
              <Input
                v-model:value="formState.mobile"
                size="small"
                :maxlength="11"
                :disabled="!!(userId && userSource === UserSource.LDAP_SYNCHRONIZE)"
                :placeholder="t('user.placeholder.mobile')">
                <template #addonBefore>
                  <SelectItc
                    :value="formState.itc"
                    :disabled="!!(userId && userSource === UserSource.LDAP_SYNCHRONIZE)"
                    style="width: 100px;"
                    internal
                    size="small"
                    @change="changeItc" />
                </template>
              </Input>
            </FormItem>

            <!-- Landline field -->
            <FormItem
              :label="t('user.profile.landline')"
              name="landline">
              <Input
                v-model:value="formState.landline"
                size="small"
                :maxlength="40"
                :placeholder="t('user.placeholder.landline')" />
            </FormItem>

            <!-- Email field -->
            <FormItem
              :label="t('user.profile.email')"
              name="email"
              :rules="emailRule">
              <Input
                v-model:value="formState.email"
                size="small"
                :disabled="!!(userId && userSource === UserSource.LDAP_SYNCHRONIZE)"
                :maxlength="100"
                :placeholder="t('user.placeholder.email')" />
            </FormItem>

            <!-- Form action buttons -->
            <FormItem>
              <Button
                :loading="loading"
                type="primary"
                size="small"
                htmlType="submit"
                class="px-3">
                {{ t('common.actions.submit') }}
              </Button>
              <template v-if="!userId">
                <Button
                  :loading="loading"
                  size="small"
                  class="ml-5"
                  @click="continueAdd">
                  {{ t('common.actions.continueAdding') }}
                </Button>
              </template>
              <RouterLink :to="source === 'home'?'/organization/user':`/organization/user/${userId}`">
                <Button class="ml-5" size="small">{{ t('common.actions.cancel') }}</Button>
              </RouterLink>
            </FormItem>
          </div>

          <!-- Right column - Additional information -->
          <div class="inline-block w-2/4 align-top">
            <!-- Gender selection -->
            <FormItem
              :label="t('user.profile.gender')"
              name="gender">
              <RadioGroup v-model:value="formState.gender" size="small">
                <Radio
                  v-for="item in userGender"
                  :key="item.value"
                  :value="item.value">
                  {{ item.message }}
                </Radio>
              </RadioGroup>
            </FormItem>

            <!-- Password field (only for new users) -->
            <FormItem
              v-if="!userId"
              :label="t('user.profile.password')"
              :rules="passwordRule"
              name="password">
              <Popover placement="leftTop" trigger="focus">
                <template #content>
                  <div v-if="passwordRule.required" class="w-35">
                    <PasswordTip :length="state.length" :chart="state.chart" />
                  </div>
                </template>
                <InputPassword
                  v-model:value="formState.password"
                  size="small"
                  :disabled="userId && userSource === UserSource.LDAP_SYNCHRONIZE"
                  :maxlength="50"
                  :placeholder="t('user.placeholder.password')"
                  autocomplete="new-password"
                  @change="changeStrength">
                </InputPassword>
              </Popover>
            </FormItem>

            <!-- Password confirmation field (only for new users) -->
            <FormItem
              v-if="!userId"
              :label="t('user.profile.confirmPassword')"
              :rules="confirmPasswordRule"
              name="confirmPassword">
              <InputPassword
                v-model:value="formState.confirmPassword"
                size="small"
                :disabled="userId && userSource === UserSource.LDAP_SYNCHRONIZE"
                :maxlength="50"
                :placeholder="t('user.placeholder.confirmPassword')">
              </InputPassword>
            </FormItem>

            <!-- Job title field -->
            <FormItem
              :label="t('user.profile.title')"
              name="title">
              <Input
                v-model:value="formState.title"
                size="small"
                :maxlength="100"
                :placeholder="t('user.placeholder.title')" />
            </FormItem>

            <!-- Address field -->
            <FormItem
              :label="t('user.profile.address')"
              name="address">
              <Input
                v-model:value="formState.address"
                size="small"
                :maxlength="200"
                :placeholder="t('user.placeholder.address')" />
            </FormItem>

            <!-- System admin role selection -->
            <FormItem
              :label="t('user.profile.identity')"
              name="sysAdmin">
              <RadioGroup
                v-model:value="formState.sysAdmin"
                size="small"
                :disabled="!appContext.isSysAdmin()">
                <Radio :value="false">{{ t('user.profile.generalUser') }}</Radio>
                <Radio :value="true">{{ t('user.profile.systemAdmin') }}</Radio>
              </RadioGroup>
            </FormItem>
          </div>
        </div>
      </div>
    </Form>
  </PureCard>
</template>

<style scoped>
/* Custom styling for input group addon */
:deep(.ant-input-group-addon) {
  border-color: var(--border-text-box);
}
</style>
