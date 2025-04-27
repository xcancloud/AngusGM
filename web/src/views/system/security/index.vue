<script setup lang="ts">
import { ref, onMounted, defineAsyncComponent } from 'vue';
import { site } from '@xcan-angus/tools';

import { setting } from '@/api';
import { SafetyConfig, Operation } from './PropsType';

const LoginRestrictions = defineAsyncComponent(() => import('./components/loginRestrictions.vue'));
const Invitation = defineAsyncComponent(() => import('./components/invitation.vue'));
const PasswordPolicy = defineAsyncComponent(() => import('./components/passwordPolicy.vue'));
const EarlyWarning = defineAsyncComponent(() => import('./components/earlyWarning.vue'));
const AccountCancellation = defineAsyncComponent(() => import('./components/accountCancel.vue'));

const safetyConfig = ref<SafetyConfig>();

const loading = ref(false);
// 获取安全配置
const getTenantSafetyConfig = async function () {
  loading.value = true;
  const [error, { data }] = await setting.getTenantSafetyConfig();
  loading.value = false;
  if (error) {
    return;
  }
  safetyConfig.value = data;
};

const updateLoading = ref({
  signinSwitchLoading: false, // 登录限制开关loading
  registSwitchLoading: false, // 允许注册开关loading
  earlySwitchLoading: false, // 安全告警开关oading
  resetButtonLoading: false, // 允许注册重置按钮loading
  safetyCheckBoxLoading: false // 安全告警复选框loading
});

// 更新安全配置
const updateTenantSafetyConfig = async function (params, operation ?: Operation) {
  const oldSafetyConfig = JSON.parse(JSON.stringify(safetyConfig.value));
  switch (operation) {
    case 'signinSwitch':
      updateLoading.value.signinSwitchLoading = true;
      break;
    case 'registSwitch':
      updateLoading.value.registSwitchLoading = true;
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
  switch (operation) {
    case 'signinSwitch':
      updateLoading.value.signinSwitchLoading = false;
      break;
    case 'registSwitch':
      updateLoading.value.registSwitchLoading = false;
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
  if (error) {
    safetyConfig.value = oldSafetyConfig;
  }
};

// 登录限制模块变化
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

// 允许注册模块变化
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

// 密码策略模块变化
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

// 安全告警模块块变化
const earlyWarningChange = (value, type, operation?: Operation) => {
  if (!safetyConfig.value) {
    return;
  }

  let keys = Object.keys(safetyConfig.value);
  if (!keys.length) {
    return;
  }

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

const editionType = ref<string>();
onMounted(async () => {
  const envContent = await site.getEnvContent();
  editionType.value = envContent?.VITE_EDITION_TYPE;
  getTenantSafetyConfig();
});

</script>
<template>
  <LoginRestrictions
    class="mb-2"
    :signinLimit="safetyConfig?.signinLimit"
    :signinSwitchLoading="updateLoading.signinSwitchLoading"
    @change="signinLimitChange" />
  <Invitation
    class="mb-2"
    :signupAllow="safetyConfig?.signupAllow"
    :registSwitchLoading="updateLoading.registSwitchLoading"
    :resetButtonLoading="updateLoading.resetButtonLoading"
    @change="registrationChange" />
  <PasswordPolicy
    class="mb-2"
    :passwordPolicy="safetyConfig?.passwordPolicy"
    @change="passwordPolicyChange" />
  <template v-if="editionType === 'CLOUD_SERVICE'">
    <AccountCancellation class="mb-2" />
  </template>
  <EarlyWarning
    :alarm="safetyConfig?.alarm"
    :earlySwitchLoading="updateLoading.earlySwitchLoading"
    :safetyCheckBoxLoading="updateLoading.safetyCheckBoxLoading"
    @change="earlyWarningChange" />
</template>
