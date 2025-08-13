<script setup lang="ts">
import { computed, ref, Ref, watch } from 'vue';
import { Button } from 'ant-design-vue';
import { Icon, PureCard } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import { appContext } from '@xcan-angus/infra';

// Import security-related components
import ModifyPassword, { Strength } from '@/views/personal/security/components/modifyPassword/index.vue';
import PasswordStrength from '@/views/personal/security/components/passwordStrength/index.vue';
import PasswordConfirm from '@/views/personal/security/components/passwordConfirm/index.vue';
import BindMobileEmail from '@/views/personal/security/components/bindMobileEmail/index.vue';
import ModifyMobileEmail, { UserInfoParams } from '@/views/personal/security/components/modifyMobileEmail/index.vue';

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
 * @param info - Object containing mobile/email information
 * @param type - Type of binding operation ('email' or 'mobile')
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
 * @param key - Security item to modify ('email', 'mobile', or 'password')
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
 * @param key - Type of modified information
 * @param value - New value for the information
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

</script>

<template>
  <!-- Security Settings Container -->
  <div class="flex justify-center flex-nowrap leading-5">
    <!-- Login Password Card -->
    <PureCard class="p-10 flex flex-col w-96 flex-wrap items-start mr-6">
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
        <div class="whitespace-pre-line break-all text-theme-content">
          {{ t('securities.messages.securityDescription') }}
        </div>
        <Button
          size="small"
          class="absolute bottom-10 border-gray-line"
          type="default"
          @click="toModify('password')">
          {{ t('securities.messages.modify') }}
        </Button>
      </div>
    </PureCard>

    <!-- Mobile Number Card -->
    <PureCard class="p-10 flex flex-col w-96 flex-wrap items-start mr-6">
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
          <div class="whitespace-pre-line break-all text-theme-content">
            {{ t('securities.messages.mobileDescription',{mobile}) }}
          </div>
          <Button
            size="small"
            class="absolute bottom-10 border-gray-line"
            type="default"
            @click="toModify('mobile')">
            {{ t('securities.messages.modify') }}
          </Button>
        </template>
        <template v-else>
          <div class="whitespace-pre-line break-all text-theme-content">
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
    <PureCard class="p-10 flex flex-col w-96 flex-wrap items-start">
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
          <div class="whitespace-pre-line break-all text-theme-content">
            {{ t('securities.messages.emailDescription',{email}) }}
          </div>
          <Button
            size="small"
            class="absolute bottom-10 border-gray-line"
            type="default"
            @click="toModify('email')">
            {{ t('securities.messages.modify') }}
          </Button>
        </template>
        <template v-else>
          <div class="whitespace-pre-line break-all text-theme-content">
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
