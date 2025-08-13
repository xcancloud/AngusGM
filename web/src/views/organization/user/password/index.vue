<script setup lang='ts'>
import { defineAsyncComponent, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Form, FormItem, InputPassword } from 'ant-design-vue';
import { Modal, notification } from '@xcan-angus/vue-ui';

import { passwordUtils } from '@xcan-angus/infra';
import { auth } from '@/api';

/**
 * Async component for password strength tips
 * Loaded only when needed to improve performance
 */
const PasswordTip = defineAsyncComponent(() => import('@/views/organization/user/passwordTip/index.vue'));

/**
 * Form state interface for password reset
 * Contains password and confirmation fields
 */
interface FormStateType {
  password: string | undefined, // New password field
  passwordConfirm?: string | undefined, // Password confirmation field
}

/**
 * Component props interface
 * Defines the properties passed to the password reset component
 */
interface Props {
  visible: boolean, // Modal visibility flag
  userId: string | undefined, // ID of user whose password is being reset
}

const props = withDefaults(defineProps<Props>(), {});

/**
 * Component emits definition
 * Defines events that this component can emit to parent
 */
const emit = defineEmits<{(e: 'cancel'): void }>();

// Internationalization setup
const { t } = useI18n();

/**
 * Form reference for validation and submission
 */
const formRef = ref();

/**
 * Reactive state management for component
 * Controls loading states, UI visibility, and form data
 */
const state = reactive<{
  confirmLoading: boolean, // Loading state during password update
  isShowTips: boolean, // Whether to show password strength tips
  length: boolean, // Password length rule validation status
  chart: boolean, // Password complexity rule validation status
  form: FormStateType, // Form data object
}>({
  confirmLoading: false,
  isShowTips: false,
  length: false, // Password length rule validation status
  chart: false, // Password complexity rule validation status (at least 2 types: numbers, letters, special chars)
  form: {
    password: undefined,
    passwordConfirm: undefined
  }
});

/**
 * Custom validation functions for form fields
 * Provides detailed validation logic for password requirements
 */
const validate = {
  /**
   * Validates password field
   * Checks for required field, length requirements, and complexity rules
   */
  password: () => {
    const val = (state.form.password || '');
    if (!val) {
      return Promise.reject(new Error(t('user.validation.passwordRequired')));
    } else if (val.length < 6 || val.length > 50) {
      return Promise.reject(new Error(t('user.validation.passwordLengthRange')));
    } else if (passwordUtils.getTypesNum(val.split('')) < 2) {
      return Promise.reject(new Error(t('user.validation.passwordNotMeetRule')));
    }
    return Promise.resolve();
  },

  /**
   * Validates password confirmation field
   * Ensures confirmation matches the password
   */
  passwordConfirm: () => {
    if (!state.form.passwordConfirm) {
      return Promise.reject(new Error(t('user.validation.passwordConfirmRequired')));
    } else if (state.form.passwordConfirm !== state.form.password) {
      return Promise.reject(new Error(t('user.validation.passwordConfirmNotMatch')));
    }
    return Promise.resolve();
  }
};

/**
 * Form validation rules configuration
 * Defines validation rules for each form field
 */
const rules = {
  password: [
    { required: true, min: 6, max: 50, validator: validate.password, trigger: 'blur' }
  ],
  passwordConfirm: [
    { required: true, validator: validate.passwordConfirm, trigger: 'blur' }
  ]
};

/**
 * Component event handlers and business logic
 * Centralizes all user interactions and API calls
 */
const handleFuncs = {
  /**
   * Toggle password strength tips visibility
   * @param flag - Whether to show tips
   */
  changeShowTips: (flag: boolean) => {
    state.isShowTips = flag;
  },

  /**
   * Analyze password strength and update validation status
   * Called on password input change
   * @param e - Input change event
   */
  changeStrength: (e: { target: { value: string } }) => {
    const { value = '' } = e.target;
    const valArr = value.split('');
    const typeNum = passwordUtils.getTypesNum(valArr);
    state.length = value.length >= 6 && value.length <= 50;
    state.chart = typeNum >= 2;
  },

  /**
   * Close the password reset modal
   * Emits cancel event to parent component
   */
  close: () => {
    emit('cancel');
  },

  /**
   * Save new password
   * Validates form, calls API, and handles success/error states
   */
  save: () => {
    formRef.value.validate().then(async () => {
      state.confirmLoading = true;
      const [error] = await auth.updateUserPassword({ id: props.userId, newPassword: state.form.password });
      state.confirmLoading = false;
      if (error) {
        return;
      }
      notification.success(t('common.messages.editSuccess'));
      handleFuncs.close();
    });
  }
};
</script>
<template>
  <!-- Password reset modal -->
  <Modal
    :title="t('user.actions.resetPassword')"
    :maskClosable="false"
    :keyboard="false"
    :confirmLoading="state.confirmLoading"
    :visible="visible"
    destroyOnClose
    width="540px"
    class="reative"
    @ok="handleFuncs.save()"
    @cancel="handleFuncs.close()">
    <!-- Password reset form -->
    <Form
      ref="formRef"
      :model="state.form"
      :rules="rules"
      v-bind="{labelCol: {span: 6}, wrapperCol: {span: 16}}">
      <!-- New password field with strength validation -->
      <FormItem :label="t('user.security.newPassword')" name="password">
        <InputPassword
          v-model:value="state.form.password"
          :maxlength="50"
          :placeholder="t('user.security.placeholder.newPassword')"
          size="small"
          @focus="handleFuncs.changeShowTips(true)"
          @blur="handleFuncs.changeShowTips(false)"
          @change="handleFuncs.changeStrength" />

        <!-- Password strength tips overlay -->
        <div
          class="w-42.5 bg-white p-3 absolute top-0 -right-59  transition-all"
          :class="state.isShowTips ? 'show' : 'hide'">
          <PasswordTip :length="state.length" :chart="state.chart" />
        </div>
      </FormItem>

      <!-- Password confirmation field -->
      <FormItem :label="t('user.security.confirmPassword')" name="passwordConfirm">
        <InputPassword
          v-model:value="state.form.passwordConfirm"
          size="small"
          :maxlength="50"
          :placeholder="t('user.security.placeholder.confirmPassword')" />
      </FormItem>
    </Form>
  </Modal>
</template>

<style scoped>
/* Password strength tips visibility transitions */
.show {
  @apply opacity-100;
}

.hide {
  @apply opacity-0;
}

/* Form label height adjustment */
:deep(.ant-form-item-label > label) {
  height: 28px;
}
</style>
