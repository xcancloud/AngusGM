<script setup lang="ts">
import { computed, ref, Ref, watch } from 'vue';
import { Button } from 'ant-design-vue';
import { Icon, PureCard } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import { appContext } from '@xcan-angus/infra';

// Import security-related components
import ModifyPassword, { Strength } from '@/views/personal/security/modifyPassword/index.vue';
import PasswordStrength from '@/views/personal/security/passwordStrength/index.vue';
import PasswordConfirm from '@/views/personal/security/passwordConfirm/index.vue';
import BindMobileEmail from '@/views/personal/security/bindMobileEmail/index.vue';
import ModifyMobileEmail, { UserInfoParams } from '@/views/personal/security/modifyMobileEmail/index.vue';

// Type definitions for security operations
type KeyType = 'email' | 'mobile';

// Get current user information from app context
const userInfo = ref(appContext.getUser());

// Internationalization setup
const { t } = useI18n();

// Reactive state for user security information
const email = ref('');
const mobile = ref('');
const country = ref('');
const itc = ref('');
const strength: Ref<Strength> = ref('weak');

// Modal visibility controls
const visible = ref(false);
const passwordVisible = ref(false);
const bindVisible = ref(false);
const bindNextVisible = ref(false);

// Form data for password confirmation
const confirmPassword = ref<string>();

// Current operation type for binding
const valueKey: Ref<KeyType> = ref('mobile');
const bindValueKey = ref<'email' | 'mobile'>('email');

/**
 * Handle mobile binding operation
 * Sets the binding type to mobile and shows the binding modal
 */
const bindMobile = () => {
  bindValueKey.value = 'mobile';
  bindVisible.value = true;
};

/**
 * Handle email binding operation
 * Sets the binding type to email and shows the binding modal
 */
const bindEmail = () => {
  bindValueKey.value = 'email';
  bindVisible.value = true;
};

/**
 * Handle password confirmation for binding operations
 * Shows the next step modal after password confirmation
 */
const confirmPasswordHandle = () => {
  bindNextVisible.value = true;
};

/**
 * Handle successful binding of mobile or email
 * Updates local state with the new information
 */
const bindMobileEmailOk = (info: {
  mobile: string;
  country: string;
  itc: string;
  email: string;
}, type: 'email' | 'mobile') => {
  if (type === 'mobile') {
    mobile.value = info.mobile;
    country.value = info.country;
    itc.value = info.itc;
    return;
  }

  email.value = info.email;
};

/**
 * Navigate to modification modal based on security item type
 */
const toModify = (key: KeyType | 'password') => {
  switch (key) {
    case 'email':
    case 'mobile':
      visible.value = true;
      valueKey.value = key;
      break;
    case 'password':
      passwordVisible.value = true;
      break;
  }
};

/**
 * Handle successful modification of security information
 * Updates local state and closes the modal
 */
const ok = (key: string, value: UserInfoParams) => {
  switch (key) {
    case 'email':
      email.value = value.email as string;
      break;
    case 'mobile':
      mobile.value = value.mobile as string;
      break;
  }
  visible.value = false;
};

// Computed property for user information passed to child components
const _userInfo = computed(() => {
  return {
    itc: itc.value,
    country: country.value,
    email: email.value,
    mobile: mobile.value
  };
});

// Watch for changes in user information and update local state
watch(() => userInfo.value, (newValue) => {
  if (!newValue) {
    return;
  }

  // Update local state with user information
  mobile.value = newValue.mobile || '';
  email.value = newValue.email || '';
  country.value = newValue.country || '';
  itc.value = newValue.itc || '';
  strength.value = newValue.passwordStrength?.value as Strength || 'weak';
}, {
  immediate: true
});

/**
 * Formats mobile description text to highlight the mobile number
 * @returns HTML string with highlighted mobile number
 */
const formatMobileDescription = (): string => {
  if (!mobile.value) return '';
  const baseText = t('securities.messages.mobileDescription', { mobile: mobile.value });
  return baseText.replace(
    mobile.value,
    `<span class="highlight-mobile">${mobile.value}</span>`
  );
};

/**
 * Formats email description text to highlight the email address
 * @returns HTML string with highlighted email address
 */
const formatEmailDescription = (): string => {
  if (!email.value) return '';
  const baseText = t('securities.messages.emailDescription', { email: email.value });
  return baseText.replace(
    email.value,
    `<span class="highlight-email">${email.value}</span>`
  );
};

</script>

<template>
  <!-- Security Settings Container -->
  <div class="flex justify-center flex-nowrap leading-5">
    <!-- Login Password Card -->
    <PureCard class="p-10 flex flex-col w-106 flex-wrap items-start mr-6">
      <div class="relative w-full flex flex-col items-center pt-8 pb-10 border-b border-theme-divider">
        <Icon class="text-6xl text-theme-text-hover" icon="icon-denglumima" />
        <div class="mt-4 text-3.5 text-theme-title">{{ t('securities.columns.loginPassword') }}</div>
        <div class="flex items-center mt-4 text-3.5 text-green-wechat">
          <Icon class="mr-2 text-3.5" icon="icon-right" />
          <span>{{ t('securities.status.set') }}</span>
        </div>
        <!-- Password strength indicator positioned at bottom -->
        <PasswordStrength :strength="strength" class="absolute bottom-3" />
      </div>
      <div class="relative w-full flex flex-col text-3 items-center flex-grow pt-10 pb-28">
        <div class="security-description text-theme-content">
          {{ t('securities.messages.securityDescription') }}
        </div>
        <Button
          size="small"
          class="absolute bottom-10 border-gray-line"
          type="primary"
          @click="toModify('password')">
          {{ t('securities.messages.modify') }}
        </Button>
      </div>
    </PureCard>

    <!-- Mobile Number Card -->
    <PureCard class="p-10 flex flex-col w-106 flex-wrap items-start mr-6">
      <div class="w-full flex flex-col items-center pt-8 pb-10 border-b border-theme-divider">
        <Icon class="text-6xl text-theme-text-hover" icon="icon-shoujihaoma" />
        <div class="mt-4 text-3.5 text-theme-title">{{ t('securities.columns.mobile') }}</div>
        <!-- Show status based on whether mobile is bound -->
        <template v-if="mobile">
          <div class="flex items-center mt-4 text-3.5 text-green-wechat">
            <Icon class="mr-2 text-3.5" icon="icon-right" />
            <span>{{ t('securities.status.set') }}</span>
          </div>
        </template>
        <template v-else>
          <div class="mt-4 text-3.5 text-theme-content">{{ t('securities.status.notSet') }}</div>
        </template>
      </div>
      <div class="relative w-full flex flex-col text-3 items-center flex-grow pt-10 pb-28">
        <!-- Conditional rendering based on mobile binding status -->
        <template v-if="mobile">
          <div class="security-description text-theme-content">
            <span v-html="formatMobileDescription()"></span>
          </div>
          <Button
            size="small"
            class="absolute bottom-10 border-gray-line"
            type="primary"
            @click="toModify('mobile')">
            {{ t('securities.messages.modify') }}
          </Button>
        </template>
        <template v-else>
          <div class="security-description text-theme-content">
            {{ t('securities.messages.unbindMobileDesc') }}
          </div>
          <Button
            size="small"
            class="absolute bottom-10"
            type="primary"
            @click="bindMobile">
            {{ t('securities.messages.bind') }}
          </Button>
        </template>
      </div>
    </PureCard>

    <!-- Email Address Card -->
    <PureCard class="p-10 flex flex-col w-106 flex-wrap items-start">
      <div class="w-full flex flex-col items-center pt-8 pb-10 border-b border-theme-divider">
        <Icon class="text-6xl text-theme-text-hover" icon="icon-dianziyouxiang1" />
        <div class="mt-4 text-3.5 text-theme-title">{{ t('securities.columns.email') }}</div>
        <!-- Show status based on whether email is bound -->
        <template v-if="email">
          <div class="flex items-center mt-4 text-3.5 text-green-wechat">
            <Icon class="mr-2 text-3.5" icon="icon-right" />
            <span>{{ t('securities.status.set') }}</span>
          </div>
        </template>
        <template v-else>
          <div class="mt-4 text-3.5 text-theme-content">{{ t('securities.status.notSet') }}</div>
        </template>
      </div>
      <div class="relative w-full flex flex-col text-3 items-center flex-grow pt-10 pb-28">
        <!-- Conditional rendering based on email binding status -->
        <template v-if="email">
          <div class="security-description text-theme-content">
            <span v-html="formatEmailDescription()"></span>
          </div>
          <Button
            size="small"
            class="absolute bottom-10 border-gray-line"
            type="primary"
            @click="toModify('email')">
            {{ t('securities.messages.modify') }}
          </Button>
        </template>
        <template v-else>
          <div class="security-description text-theme-content">
            {{ t('securities.messages.unbindEmailDesc') }}
          </div>
          <Button
            size="small"
            class="absolute bottom-10"
            type="primary"
            @click="bindEmail">
            {{ t('securities.messages.bind') }}
          </Button>
        </template>
      </div>
    </PureCard>
  </div>

  <!-- Modal Components -->
  <!-- Password confirmation modal for binding operations -->
  <PasswordConfirm
    v-if="bindVisible"
    v-model:value="confirmPassword"
    v-model:visible="bindVisible"
    @ok="confirmPasswordHandle" />

  <!-- Mobile/Email binding modal -->
  <BindMobileEmail
    v-if="bindNextVisible"
    v-model:visible="bindNextVisible"
    :linkSecret="confirmPassword"
    :valueKey="bindValueKey"
    :isModify="false"
    @ok="bindMobileEmailOk" />

  <!-- Password modification modal -->
  <ModifyPassword v-if="passwordVisible" v-model:visible="passwordVisible" />

  <!-- Mobile/Email modification modal -->
  <ModifyMobileEmail
    v-if="visible"
    v-model:visible="visible"
    :valueKey="valueKey"
    :userInfo="_userInfo"
    @ok="ok" />
</template>

<style scoped>
/* Security description text styling */
.security-description {
  white-space: pre-line;
  word-break: break-word;
  line-height: 1.8;
  text-align: left;
  max-width: 100%;
  color: #6b7280;
  font-size: 0.675rem;
  letter-spacing: 0.025em;
  hyphens: auto;
  overflow-wrap: break-word;
}

/* Enhanced text readability */
.security-description:first-line {
  font-weight: 600;
  color: #374151;
  text-align: center;
  font-size: 0.9rem;
}

/* Better handling for English text */
.security-description {
  text-align: left;
  text-justify: inter-word;
}

/* Responsive font size for different languages */
@media (min-width: 768px) {
  .security-description {
    font-size: 0.9rem;
    line-height: 1.9;
  }
}

/* Hover effect for better interaction */
.security-description:hover {
  color: #4b5563;
  transition: color 0.2s ease;
}

/* Highlight styles for mobile and email */
.highlight-mobile {
  background: linear-gradient(135deg, #10B981 0%, #059669 100%);
  color: white;
  padding: 0.25rem 0.5rem;
  border-radius: 0.5rem;
  font-weight: 600;
  box-shadow: 0 2px 4px rgba(16, 185, 129, 0.3);
}

.highlight-email {
  background: linear-gradient(135deg, #3B82F6 0%, #1D4ED8 100%);
  color: white;
  padding: 0.25rem 0.5rem;
  border-radius: 0.5rem;
  font-weight: 600;
  box-shadow: 0 2px 4px rgba(59, 130, 246, 0.3);
}
</style>
