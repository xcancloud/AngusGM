<script setup lang='ts'>
import { computed, defineAsyncComponent, inject, onMounted, reactive, ref, watch } from 'vue';
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
 * Computed property for mobile input to handle null values
 */
const mobileValue = computed({
  get: () => formState.value.mobile || '',
  set: (value: string) => {
    formState.value.mobile = value || null;
  }
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
  <PureCard class="user-edit-container">
    <!-- Main form container -->
    <Form
      ref="formRef"
      size="small"
      layout="vertical"
      :scrollToFirstError="true"
      :model="formState"
      class="user-form"
      @finish="onFinish">
      <!-- Avatar section with simple styling -->
      <div class="avatar-section">
        <div class="avatar-container">
          <div class="avatar-wrapper">
            <Cropper
              v-model:visible="uploadVisible"
              :params="uploadParams"
              @success="uploadSuccess"
              @error="uploadError" />
            <Image
              class="avatar-image"
              type="avatar"
              :src="formState.avatar" />
            <div class="avatar-overlay" @click="openUploadModal">
              <Icon icon="icon-camera" class="camera-icon" />
              <span class="upload-text">{{ t('user.uploadAvatarTip') }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Form content with improved layout -->
      <div class="form-content">
        <!-- Basic Information Section -->
        <div class="form-section">
          <div class="section-header">
            <Icon icon="icon-jibenxinxi" class="info-icon" />
            <h3 class="section-title">{{ t('user.profile.basicInfo') }}</h3>
          </div>

          <div class="form-grid">
            <!-- Name fields with improved spacing -->
            <div class="group-fields">
              <FormItem
                :label="t('user.profile.firstName')"
                :rules="[{ required: true, message:t('user.validation.firstNameRequired') }]"
                name="firstName"
                class="name-field">
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
                class="name-field">
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
            <div class="group-fields">
              <FormItem
                :label="t('user.profile.fullName')"
                :rules="[{ required: true, message: t('user.validation.nameRequired') }]"
                name="fullName"
                class="full-name-field">
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
                :rules="[{ required: true, message:t('user.validation.usernameRequired') }]"
                class="username-field">
                <Input
                  v-model:value="formState.username"
                  size="small"
                  :maxlength="100"
                  :placeholder="t('user.placeholder.username',{name:t('user.profile.username'),num:100,chart:' - _ . '})"
                  dataType="mixin-en"
                  includes="-_." />
              </FormItem>
            </div>

            <!-- Password fields (only for new users) -->
            <div v-if="!userId" class="password-fields">
              <FormItem
                :label="t('user.profile.password')"
                :rules="passwordRule"
                name="password"
                class="password-field">
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

              <FormItem
                :label="t('user.profile.confirmPassword')"
                :rules="confirmPasswordRule"
                name="confirmPassword"
                class="password-field">
                <InputPassword
                  v-model:value="formState.confirmPassword"
                  size="small"
                  :disabled="userId && userSource === UserSource.LDAP_SYNCHRONIZE"
                  :maxlength="50"
                  :placeholder="t('user.placeholder.confirmPassword')">
                </InputPassword>
              </FormItem>
            </div>

            <!-- Contact Information -->
            <div class="contact-fields">
              <!-- Mobile phone field -->
              <FormItem
                :label="t('user.profile.mobile')"
                name="mobile"
                :rules="mobileRule"
                class="contact-field">
                <Input
                  v-model:value="mobileValue"
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
                name="landline"
                class="contact-field">
                <Input
                  v-model:value="formState.landline"
                  size="small"
                  :maxlength="40"
                  :placeholder="t('user.placeholder.landline')" />
              </FormItem>
            </div>

            <!-- Email field -->
            <FormItem
              :label="t('user.profile.email')"
              name="email"
              :rules="emailRule"
              class="email-field">
              <Input
                v-model:value="formState.email"
                size="small"
                :disabled="!!(userId && userSource === UserSource.LDAP_SYNCHRONIZE)"
                :maxlength="100"
                :placeholder="t('user.placeholder.email')" />
            </FormItem>
          </div>
        </div>

        <!-- Additional Information Section -->
        <div class="form-section">
          <div class="section-header">
            <Icon icon="icon-shezhi" class="info-icon" />
            <h3 class="section-title">{{ t('user.profile.additionalInfo') }}</h3>
          </div>

          <div class="form-grid">
            <!-- Put gender and identity on the same row */ -->
            <div class="gender-identity-row">
              <FormItem
                :label="t('user.profile.gender')"
                name="gender"
                class="gender-field">
                <RadioGroup v-model:value="formState.gender" size="small">
                  <Radio
                    v-for="item in userGender"
                    :key="item.value"
                    :value="item.value">
                    {{ item.message }}
                  </Radio>
                </RadioGroup>
              </FormItem>

              <!-- System admin role selection -->
              <FormItem
                :label="t('user.profile.identity')"
                name="sysAdmin"
                class="role-field">
                <RadioGroup
                  v-model:value="formState.sysAdmin"
                  size="small"
                  :disabled="!appContext.isSysAdmin()">
                  <Radio :value="false">{{ t('user.profile.generalUser') }}</Radio>
                  <Radio :value="true">{{ t('user.profile.systemAdmin') }}</Radio>
                </RadioGroup>
              </FormItem>
            </div>

            <div class="group-fields">
              <!-- Job title field -->
              <FormItem
                :label="t('user.profile.title')"
                name="title"
                class="title-field">
                <Input
                  v-model:value="formState.title"
                  size="small"
                  :maxlength="100"
                  :placeholder="t('user.placeholder.title')" />
              </FormItem>

              <!-- Address field -->
              <FormItem
                :label="t('user.profile.address')"
                name="address"
                class="address-field">
                <Input
                  v-model:value="formState.address"
                  size="small"
                  :maxlength="200"
                  :placeholder="t('user.placeholder.address')" />
              </FormItem>
            </div>
          </div>
        </div>

        <!-- Form action buttons -->
        <div class="form-actions">
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
      </div>
    </Form>
  </PureCard>
</template>

<style scoped>
/* User edit page styling - simplified */
.user-edit-container {
  padding: 24px;
  min-height: 100vh;
}

/* Avatar section styling - simple */
.avatar-section {
  padding: 12px;
  text-align: center;
}

.avatar-container {
  display: inline-block;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  cursor: pointer;
  transition: all 0.3s ease;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border: none;
  border-radius: 0;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s ease;
  color: white;
}

.camera-icon {
  font-size: 16px;
  margin-bottom: 4px;
}

.upload-text {
  font-size: 12px;
  font-weight: 500;
}

/* Form content styling */
.form-content {
  padding: 18px;
}

/* Section styling */
.form-section {
  margin-bottom: 32px;
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.info-icon {
  margin-right: 6px;
  font-size: 12px;
  color: #1890ff;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #262626;
  margin: 0;
}

/* Form grid layout */
.form-grid {
  display: grid;
  gap: 12px;
}
/* Put gender and identity on the same row */
.gender-identity-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

/* Unify spacing for specific form items */
.title-field,
.address-field,
.gender-field,
.role-field {
  margin-bottom: 0;
}

/* Normalize placeholder font size for inputs and password inputs */
:deep(.ant-input::placeholder),
:deep(.ant-input-affix-wrapper .ant-input::placeholder) {
  font-size: 12px;
}

/* Name fields styling */
.group-fields {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.name-field {
  margin-bottom: 0;
}

.full-name-field {
  margin-bottom: 0;
}

/* Contact fields styling */
.contact-fields {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.contact-field {
  margin-bottom: 0;
}

/* Password fields styling */
.password-fields {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.password-field {
  margin-bottom: 0;
}

/* Form actions styling */
.form-actions {
  margin-top: 18px;
  padding-top: 12px;
}

/* Responsive design */
@media (max-width: 768px) {
  .user-edit-container {
    padding: 12px;
  }

  .form-content {
    padding: 12px;
  }

  .group-fields,
  .contact-fields,
  .password-fields {
    grid-template-columns: 1fr;
  }
  .gender-identity-row {
    grid-template-columns: 1fr;
  }
}

/* Custom styling for input group addon */
:deep(.ant-input-group-addon) {
  border-color: var(--border-text-box);
}

/* Normalize password placeholder font size for various browsers */
:deep(input[type='password']::placeholder) {
  font-size: 12px;
}
</style>
