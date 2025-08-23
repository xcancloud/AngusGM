<script setup lang="ts">
import { defineAsyncComponent, onMounted, ref } from 'vue';
import { appContext } from '@xcan-angus/infra';
import { useI18n } from 'vue-i18n';

import { setting } from '@/api';
import { Operation, SafetyConfig } from './types';

// Async components for security settings
const LoginRestrictions = defineAsyncComponent(() => import('./components/loginRestrictions.vue'));
const Invitation = defineAsyncComponent(() => import('./components/invitation.vue'));
const PasswordPolicy = defineAsyncComponent(() => import('./components/passwordPolicy.vue'));
const EarlyWarning = defineAsyncComponent(() => import('./components/earlyWarning.vue'));
const AccountCancellation = defineAsyncComponent(() => import('./components/accountCancel.vue'));

const { t } = useI18n();

// Security configuration state
const safetyConfig = ref<SafetyConfig>();

const loading = ref(false);

/**
 * Fetch tenant security configuration
 * Retrieves security settings from API and updates local state
 */
const getTenantSafetyConfig = async function () {
  loading.value = true;
  const [error, { data }] = await setting.getTenantSafetyConfig();
  loading.value = false;
  if (error) {
    return;
  }
  safetyConfig.value = data;
};

// Loading states for different security setting operations
const updateLoading = ref({
  signinSwitchLoading: false, // Login restrictions switch loading
  signupSwitchLoading: false, // Registration switch loading
  earlySwitchLoading: false, // Security alert switch loading
  resetButtonLoading: false, // Registration reset button loading
  safetyCheckBoxLoading: false // Security alert checkbox loading
});

/**
 * Update tenant security configuration
 * Handles loading states and error recovery for different operations
 */
const updateTenantSafetyConfig = async function (params, operation ?: Operation) {
  const oldSafetyConfig = JSON.parse(JSON.stringify(safetyConfig.value));

  // Set loading state based on operation type
  switch (operation) {
    case 'signinSwitch':
      updateLoading.value.signinSwitchLoading = true;
      break;
    case 'registSwitch':
      updateLoading.value.signupSwitchLoading = true;
      break;
    case 'earlySwitch':
      updateLoading.value.earlySwitchLoading = true;
      break;
    case 'resetButton':
      updateLoading.value.resetButtonLoading = true;
      break;
    case 'safetyCheckBox':
      updateLoading.value.safetyCheckBoxLoading = true;
      break;
  }

  const [error] = await setting.updateTenantSafetyConfig(params);

  // Clear loading state based on operation type
  switch (operation) {
    case 'signinSwitch':
      updateLoading.value.signinSwitchLoading = false;
      break;
    case 'registSwitch':
      updateLoading.value.signupSwitchLoading = false;
      break;
    case 'earlySwitch':
      updateLoading.value.earlySwitchLoading = false;
      break;
    case 'resetButton':
      updateLoading.value.resetButtonLoading = false;
      break;
    case 'safetyCheckBox':
      updateLoading.value.safetyCheckBoxLoading = false;
      break;
  }

  // Restore old configuration on error
  if (error) {
    safetyConfig.value = oldSafetyConfig;
  }
};

/**
 * Handle login restrictions module changes
 * Updates signinLimit configuration and triggers API update
 */
const signinLimitChange = (value, type, operation ?: Operation) => {
  if (!safetyConfig.value) {
    return;
  }
  let keys = Object.keys(safetyConfig.value);
  if (!keys.length || !safetyConfig.value?.signinLimit) {
    return;
  }
  keys = Object.keys(safetyConfig.value.signinLimit);

  if (!keys.length) {
    return;
  }

  safetyConfig.value.signinLimit[type] = value;
  updateTenantSafetyConfig(safetyConfig.value, operation);
};

/**
 * Handle registration module changes
 * Updates signupAllow configuration and triggers API update
 */
const registrationChange = (value, type, operation?: Operation) => {
  if (!safetyConfig.value) {
    return;
  }
  let keys = Object.keys(safetyConfig.value);
  if (!keys.length || !safetyConfig.value?.signupAllow) {
    return;
  }

  keys = Object.keys(safetyConfig.value.signupAllow);

  if (!keys.length) {
    return;
  }

  safetyConfig.value.signupAllow[type] = value;
  updateTenantSafetyConfig(safetyConfig.value, operation);
};

/**
 * Handle password policy module changes
 * Updates passwordPolicy configuration and triggers API update
 */
const passwordPolicyChange = (value, type) => {
  if (!safetyConfig.value) {
    return;
  }

  const keys = Object.keys(safetyConfig.value);
  if (!keys.length) {
    return;
  }
  if (safetyConfig.value?.passwordPolicy?.minLength) {
    safetyConfig.value.passwordPolicy[type] = value;
  } else {
    safetyConfig.value.passwordPolicy = { minLength: '6' };
  }
  updateTenantSafetyConfig(safetyConfig.value);
};

/**
 * Handle security alert module changes
 * Updates alarm configuration and triggers API update
 */
const earlyWarningChange = (value, type, operation?: Operation) => {
  if (!safetyConfig.value) {
    return;
  }

  let keys = Object.keys(safetyConfig.value);
  if (!keys.length) {
    return;
  }

  // Initialize alarm configuration if it doesn't exist
  if (!safetyConfig.value?.alarm) {
    safetyConfig.value.alarm = {
      enabled: true,
      alarmWay: [],
      receiveUser: []
    };
    updateTenantSafetyConfig(safetyConfig.value, operation);
  }

  keys = Object.keys(safetyConfig.value.alarm);

  if (!keys.length) {
    return;
  }
  safetyConfig.value.alarm[type] = value;
  updateTenantSafetyConfig(safetyConfig.value, operation);
};

// Edition type for conditional component rendering
const editionType = ref<string>();

// Initialize component data on mount
onMounted(async () => {
  editionType.value = appContext.getEditionType();
  await getTenantSafetyConfig();
});
</script>
<template>
  <template v-if="safetyConfig">
    <LoginRestrictions
      class="mb-2"
      :signinLimit="safetyConfig.signinLimit"
      :signinSwitchLoading="updateLoading.signinSwitchLoading"
      @change="signinLimitChange" />
    <Invitation
      class="mb-2"
      :signupAllow="safetyConfig.signupAllow"
      :signupSwitchLoading="updateLoading.signupSwitchLoading"
      :resetButtonLoading="updateLoading.resetButtonLoading"
      @change="registrationChange" />
    <PasswordPolicy
      class="mb-2"
      :passwordPolicy="safetyConfig.passwordPolicy"
      :loading="updateLoading.safetyCheckBoxLoading"
      @change="passwordPolicyChange" />
    <template v-if="editionType === 'CLOUD_SERVICE'">
      <AccountCancellation class="mb-2" />
    </template>
    <EarlyWarning
      :alarm="safetyConfig.alarm"
      :earlySwitchLoading="updateLoading.earlySwitchLoading"
      :safetyCheckBoxLoading="updateLoading.safetyCheckBoxLoading"
      @change="earlyWarningChange" />
  </template>
  
  <!-- Loading state -->
  <template v-else>
    <div class="flex items-center justify-center h-32">
      <div class="text-theme-sub-content">{{ t('common.messages.loading') }}</div>
    </div>
  </template>
</template>
