<script setup lang="ts">
import { ref, Ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { app, passwordUtils } from '@xcan-angus/infra';
import { Popover } from 'ant-design-vue';
import { Hints, Icon, Input, Modal } from '@xcan-angus/vue-ui';

import { auth } from '@/api';

import PasswordStrength from '@/views/personal/security/passwordStrength/index.vue';

// Password strength levels for validation feedback
export type Strength = 'weak' | 'medium' | 'strong';

// Component props interface
interface Props {
  visible: boolean; // Controls modal visibility
}

const props = withDefaults(defineProps<Props>(), {
  visible: false
});

// Component event emissions
const emit = defineEmits<{
  (e: 'update:visible', val: boolean): void; // Update modal visibility
  (e: 'ok', val: Strength): void; // Password update success
}>();

// Internationalization setup
const { t } = useI18n();

// Constants and reactive state
const SPECIAL_CHARS = "`-=[];',\\./~!@#$%^&*()_+{}:\"<>?"; // Allowed special characters for passwords

// Form field states
const oldPassword = ref(''); // Current password input
const oldPasswordError = ref(false); // Current password validation error flag
const newPassword = ref(''); // New password input
const newPasswordError = ref(false); // New password validation error flag
const newPasswordMessage = ref(''); // New password error message
const repeatPassword = ref(''); // Password confirmation input
const repeatPasswordError = ref(false); // Password confirmation error flag
const repeatPasswordMessage = ref(''); // Password confirmation error message

// Password strength and UI states
const strength: Ref<Strength> = ref('weak'); // Current password strength level
const showStrength = ref(false); // Show password strength indicator
const loading = ref(false); // Loading state for form submission

/**
 * Get popup container for tooltips and hints
 * @param node - HTML element to find parent for
 * @returns Parent element or document body as fallback
 */
const getPopupContainer = (node: HTMLElement): HTMLElement => {
  if (node) {
    return node.parentNode as HTMLElement;
  }

  return document.body;
};

/**
 * Cancel password modification operation
 * Closes the modal without saving changes
 */
const cancel = (): void => {
  emit('update:visible', false);
};

/**
 * Submit password modification form
 * Validates all fields and updates password if validation passes
 */
const ok = async (): Promise<void> => {
  // Validate all password fields before submission
  if (!validateOldPassword()) {
    return;
  }

  if (!validateNewPassword()) {
    return;
  }

  if (!validateRepeatPassword()) {
    return;
  }

  // Prevent multiple submissions
  if (loading.value) {
    return;
  }

  // Prepare password update parameters
  const params = {
    oldPassword: oldPassword.value,
    newPassword: newPassword.value
  };

  // Submit password update request
  loading.value = true;
  const [error] = await auth.updateCurrentPassword(params);
  loading.value = false;

  if (error) {
    return;
  }

  // Sign out user after successful password change
  app.signOut(true);
};

/**
 * Handle password input changes for all password fields
 * @param event - Input change event
 * @param passwordType - Type of password field (1: old, 2: new, 3: confirm)
 */
const changeHandle = (event: any, passwordType: 1 | 2 | 3): void => {
  const value = event.target.value;

  switch (passwordType) {
    case 1: // Old password field
      oldPasswordError.value = false; // Clear error state
      oldPassword.value = value;
      break;
    case 2: // New password field
      newPassword.value = value;
      newPasswordChange(); // Trigger validation and strength calculation
      break;
    case 3: // Password confirmation field
      repeatPasswordError.value = false; // Clear error state
      repeatPassword.value = value;
      break;
  }
};

/**
 * Handle blur events for password fields
 * Triggers validation when user leaves a password field
 * @param passwordType - Type of password field to validate
 */
const onBlur = (passwordType: 1 | 2 | 3) => {
  switch (passwordType) {
    case 1: // Old password field
      validateOldPassword();
      break;
    case 2: // New password field
      validateNewPassword();
      break;
    case 3: // Password confirmation field
      validateRepeatPassword();
      break;
  }
};

/**
 * Handle new password input changes
 * Validates new password and calculates strength
 * @returns true if password is valid, false otherwise
 */
const newPasswordChange = () => {
  // Check if new password is empty
  if (!newPassword.value) {
    showStrength.value = false;
    newPasswordError.value = true;
    newPasswordMessage.value = t('securities.messages.newNotEmpty');
    return false;
  }

  // Check if new password is same as old password
  if (newPassword.value === oldPassword.value) {
    showStrength.value = false;
    newPasswordError.value = true;
    newPasswordMessage.value = t('securities.messages.notTheSame');
    return false;
  }

  // Password is valid, show strength indicator and calculate strength
  newPasswordError.value = false;
  showStrength.value = true;
  strength.value = passwordUtils.calcStrength(newPassword.value) as Strength;
  return true;
};

/**
 * Validate old password field
 * Checks if old password is provided and validates new password if present
 * @returns true if validation passes, false otherwise
 */
const validateOldPassword = (): boolean => {
  // Check if old password is provided
  if (!oldPassword.value) {
    oldPasswordError.value = true;
    return false;
  }

  // If new password exists and is different from old password, validate it
  if (newPassword.value && (newPassword.value !== oldPassword.value)) {
    const result = passwordUtils.isInvalid(newPassword.value);
    if (result.invalid) {
      // Hide strength indicator and show error for invalid new password
      showStrength.value = false;
      newPasswordError.value = true;
      newPasswordMessage.value = result.message;
    } else {
      // Show strength indicator for valid new password
      showStrength.value = true;
      newPasswordError.value = false;
    }
  }

  return true;
};

/**
 * Validate new password field
 * Uses password utility to check password complexity requirements
 * @returns true if password meets requirements, false otherwise
 */
const validateNewPassword = (): boolean => {
  const result = passwordUtils.isInvalid(newPassword.value);
  if (result.invalid) {
    // Hide strength indicator and show validation error
    showStrength.value = false;
    newPasswordError.value = true;
    newPasswordMessage.value = result.message;
    return false;
  }

  return true;
};

/**
 * Validate password confirmation field
 * Ensures confirmation password matches new password
 * @returns true if passwords match, false otherwise
 */
const validateRepeatPassword = (): boolean => {
  // Check if confirmation password is provided
  if (!repeatPassword.value) {
    repeatPasswordError.value = true;
    repeatPasswordMessage.value = t('securities.messages.confirmNotEmpty');
    return false;
  }

  // Check if confirmation password matches new password
  if (repeatPassword.value !== newPassword.value) {
    repeatPasswordError.value = true;
    repeatPasswordMessage.value = t('securities.messages.twoNotSame');
    return false;
  }

  // Passwords match, clear error state
  repeatPasswordError.value = false;
  return true;
};

/**
 * Reset all form fields and error states
 * Clears all password inputs and validation errors
 */
const reset = () => {
  // Reset old password field
  oldPassword.value = '';
  oldPasswordError.value = false;

  // Reset new password field
  newPassword.value = '';
  newPasswordError.value = false;
  newPasswordMessage.value = '';

  // Reset password confirmation field
  repeatPassword.value = '';
  repeatPasswordError.value = false;
  repeatPasswordMessage.value = '';
  // Reset password strength and UI states
  strength.value = 'weak';
  showStrength.value = false;
  loading.value = false;
};

watch(() => props.visible, () => {
  reset();
});

</script>
<template>
  <!-- Password Modification Modal -->
  <Modal
    :closable="false"
    :title="t('securities.messages.changePassword')"
    :visible="visible"
    :confirmLoading="loading"
    :getPopupContainer="getPopupContainer"
    @ok="ok"
    @cancel="cancel">
    <!-- Success Description Hint -->
    <Hints :text="t('securities.messages.updateSuccessDescription')" class="transform-gpu -translate-y-2" />

    <!-- Password Form Layout -->
    <!-- TODO: Consider using CSS Grid for better layout management -->
    <div class="flex mt-7.5">
      <!-- Form Labels Column -->
      <div class="leading-8 whitespace-nowrap text-right text-theme-title mr-2.5">
        <div>{{ t('securities.columns.currentPassword') }}</div>
        <!-- Dynamic margin adjustment based on error states -->
        <div :class="{ 'mt-9': oldPasswordError }" class="mt-6">{{ t('securities.columns.newPassword') }}</div>
        <div :class="{ 'mt-8.5': newPasswordError || showStrength }" class="mt-6">
          {{ t('securities.columns.confirmPassword') }}
        </div>
      </div>
      <!-- Form Inputs Column -->
      <div class="flex flex-wrap">
        <!-- Current Password Input Field -->
        <div class="flex-freeze-full">
          <Input
            dataType="mixin-en"
            type="password"
            :allowClear="false"
            :includes="SPECIAL_CHARS"
            :trimAll="true"
            :maxlength="50"
            :class="{ 'input-error': oldPasswordError }"
            :placeholder="t('securities.placeholder.passwordPlaceholder')"
            @change="changeHandle($event, 1)"
            @blur="onBlur(1)" />
          <!-- Current Password Error Message -->
          <div v-show="oldPasswordError" class="flex items-center h-3.75 leading-3.75 mt-1 text-3 text-danger">
            <Icon icon="icon-tishi1" class="mr-1 text-3.5" />
            {{ t('securities.messages.emptyPasswordDescription') }}
          </div>
        </div>
        <!-- New Password Input Field with Help Popover -->
        <div :class="{ 'mt-4': oldPasswordError }" class="flex-freeze-full mt-6">
          <Popover
            :getPopupContainer="getPopupContainer"
            placement="left"
            trigger="focus">
            <template #content>
              <!-- Password Requirements Help Content -->
              <div class="flex mt-3 text-theme-content">
                <Icon class="flex-shrink-0 mt-0.75 mr-2 text-blue-tips text-3.5" icon="icon-tishi1" />
                <span class="leading-5">{{ t('securities.messages.systemPasswordStrength') }}</span>
              </div>
              <div class="flex mt-3 text-theme-content">
                <Icon class="flex-shrink-0 mt-0.75 mr-2 text-blue-tips text-3.5" icon="icon-tishi1" />
                <span class="leading-5">{{ t('securities.messages.passwordRules') }}</span>
              </div>
              <div class="flex mt-3 text-theme-content">
                <Icon class="flex-shrink-0 mt-0.75 mr-2 text-blue-tips text-3.5" icon="icon-tishi1" />
                <span class="leading-5">{{ t('securities.messages.passwordSpecial') }}</span>
              </div>
              <div class="flex mt-3 text-theme-content">
                <Icon class="flex-shrink-0 mt-0.75 mr-2 text-blue-tips text-3.5" icon="icon-tishi1" />
                <span class="leading-5">{{ t('securities.messages.notSpaces') }}</span>
              </div>
            </template>
            <!-- New Password Input with Special Character Support -->
            <Input
              dataType="mixin-en"
              type="password"
              :allowClear="false"
              :includes="SPECIAL_CHARS"
              :trimAll="true"
              :maxlength="50"
              :class="{ 'input-error': newPasswordError }"
              :placeholder="t('securities.placeholder.passwordPlaceholder')"
              @change="changeHandle($event, 2)"
              @blur="onBlur(2)" />
          </Popover>

          <!-- New Password Error Message Display -->
          <div v-show="newPasswordError" class="flex items-center h-3.75 leading-3.75 mt-1 text-3 text-danger">
            <Icon icon="icon-tishi1" class="mr-1 text-3.5" />
            {{ newPasswordMessage }}
          </div>

          <!-- Password Strength Indicator -->
          <PasswordStrength
            v-show="showStrength"
            :strength="strength"
            class="mt-1 h-3.75" />
        </div>
        <!-- Password Confirmation Input Field -->
        <div :class="{ 'mt-4': newPasswordError || showStrength }" class="flex-freeze-full mt-6">
          <Input
            dataType="mixin-en"
            type="password"
            :allowClear="false"
            :includes="SPECIAL_CHARS"
            :trimAll="true"
            :maxlength="50"
            :class="{ 'input-error': repeatPasswordError }"
            :placeholder="t('securities.placeholder.passwordPlaceholder')"
            @change="changeHandle($event, 3)"
            @blur="onBlur(3)" />

          <!-- Password Confirmation Error Message -->
          <div v-show="repeatPasswordError" class="flex items-center h-3.75 leading-3.75 mt-1 text-3 text-danger">
            <Icon icon="icon-tishi1" class="mr-1 text-3.5" />
            {{ repeatPasswordMessage }}
          </div>
        </div>
      </div>
    </div>
  </Modal>
</template>

<style scoped>
/* Error state styling for input fields */
.input-error,
.input-error:focus,
.input-error:hover {
  @apply border-danger;
}

/* Remove focus box shadow for error inputs */
.input-error:focus {
  box-shadow: none;
}

/* Remove focus box shadow for all input wrappers */
.ant-input-affix-wrapper-focused {
  box-shadow: none;
}

/* Custom popover positioning and sizing */
:deep(.ant-popover) {
  @apply w-45;
  left: calc(100% + 0.5rem) !important;
}

/* Hide popover arrow for cleaner appearance */
:deep(.ant-popover-arrow) {
  @apply hidden;
}
</style>
