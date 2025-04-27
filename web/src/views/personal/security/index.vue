<script setup lang="ts">
import { computed, inject, ref, Ref, watch } from 'vue';
import { Button } from 'ant-design-vue';
import { PureCard, Icon } from '@xcan/design';
import { useI18n } from 'vue-i18n';

import ModifyPassword, { Strength } from '@/views/personal/security/components/modifyPassword/index.vue';
import PasswordStrength from '@/views/personal/security/components/passwordStrength/index.vue';
import PasswordConfirm from '@/views/personal/security/components/passwordConfirm/index.vue';
import BindMobileEmail from '@/views/personal/security/components/bindMobileEmail/index.vue';
import ModifyMobileEmail, { UserInfoParams } from '@/views/personal/security/components/modifyMobileEmail/index.vue';

type KeyType = 'email' | 'mobile';

const tenantInfo = inject('tenantInfo', ref());

const { t } = useI18n();
const email = ref('');
const mobile = ref('');
const country = ref('');
const itc = ref('');
const strength: Ref<Strength> = ref('weak');
const valueKey: Ref<KeyType> = ref('mobile');
const visible = ref(false);
const passwordVisible = ref(false);

const confirmPassword = ref<string>();
const bindVisible = ref(false);
const bindValueKey = ref<'email' | 'mobile'>('email');
const bindMobile = () => {
  bindValueKey.value = 'mobile';
  bindVisible.value = true;
};

const bindEmail = () => {
  bindValueKey.value = 'email';
  bindVisible.value = true;
};

const bindNextVisible = ref(false);
const confirmPasswordHandle = () => {
  bindNextVisible.value = true;
};

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

const _userInfo = computed(() => {
  return {
    itc: itc.value,
    country: country.value,
    email: email.value,
    mobile: mobile.value
  };
});

watch(() => tenantInfo.value, (newValue) => {
  if (!newValue) {
    return;
  }

  mobile.value = newValue.mobile;
  email.value = newValue.email;
  country.value = newValue.country;
  itc.value = newValue.itc;
  strength.value = newValue.passwordStrength?.value;
}, {
  immediate: true
});

</script>

<template>
  <div class="flex justify-center flex-nowrap leading-5">
    <PureCard class="p-10 flex flex-col w-96 flex-wrap items-start mr-6">
      <div class="relative w-full flex flex-col items-center pt-8 pb-10 border-b border-theme-divider">
        <Icon class="text-6xl text-theme-text-hover" icon="icon-denglumima" />
        <div class="mt-4 text-3.5 text-theme-title">登录密码</div>
        <div class="flex items-center mt-4 text-3.5 text-green-wechat">
          <Icon class="mr-2 text-3.5" icon="icon-right" />
          <span>已设置</span>
        </div>
        <PasswordStrength :strength="strength" class="absolute bottom-3" />
      </div>
      <div class="relative w-full flex flex-col text-3 items-center flex-grow pt-10 pb-28">
        <div
          class="whitespace-pre-line break-all text-theme-content">
          {{ t('personalCenter.security.securityDescription') }}
        </div>
        <Button
          size="small"
          class="absolute bottom-10 border-gray-line"
          type="default"
          @click="toModify('password')">
          {{ t('personalCenter.modify') }}
        </Button>
      </div>
    </PureCard>
    <PureCard class="p-10 flex flex-col w-96 flex-wrap items-start mr-6">
      <div class="w-full flex flex-col items-center pt-8 pb-10 border-b border-theme-divider">
        <Icon class="text-6xl text-theme-text-hover" icon="icon-shoujihaoma" />
        <div class="mt-4 text-3.5 text-theme-title">{{ t('personalCenter.mobile') }}</div>
        <template v-if="mobile">
          <div class="flex items-center mt-4 text-3.5 text-green-wechat">
            <Icon class="mr-2 text-3.5" icon="icon-right" />
            <span>已设置</span>
          </div>
        </template>
        <template v-else>
          <div class="mt-4 text-3.5 text-theme-content">未设置</div>
        </template>
      </div>
      <div class="relative w-full flex flex-col text-3 items-center flex-grow pt-10 pb-28">
        <template v-if="mobile">
          <div class="whitespace-pre-line break-all text-theme-content">
            {{ t('personalCenter.security.mobileDescription',{mobile}) }}
          </div>
          <Button
            size="small"
            class="absolute bottom-10 border-gray-line"
            type="default"
            @click="toModify('mobile')">
            {{ t('personalCenter.modify') }}
          </Button>
        </template>
        <template v-else>
          <div class="whitespace-pre-line break-all text-theme-content">
            {{ t('personalCenter.security.unbindMobileDesc') }}
          </div>
          <Button
            size="small"
            class="absolute bottom-10"
            type="primary"
            @click="bindMobile">
            {{ t('personalCenter.bind') }}
          </Button>
        </template>
      </div>
    </PureCard>
    <PureCard class="p-10 flex flex-col w-96 flex-wrap items-start">
      <div class="w-full flex flex-col items-center pt-8 pb-10 border-b border-theme-divider">
        <Icon class="text-6xl text-theme-text-hover" icon="icon-dianziyouxiang1" />
        <div class="mt-4 text-3.5 text-theme-title">{{ t('personalCenter.email') }}</div>
        <template v-if="email">
          <div class="flex items-center mt-4 text-3.5 text-green-wechat">
            <Icon class="mr-2 text-3.5" icon="icon-right" />
            <span>已设置</span>
          </div>
        </template>
        <template v-else>
          <div class="mt-4 text-3.5 text-theme-content">未设置</div>
        </template>
      </div>
      <div class="relative w-full flex flex-col text-3 items-center flex-grow pt-10 pb-28">
        <template v-if="email">
          <div class="whitespace-pre-line break-all text-theme-content">
            {{ t('personalCenter.security.emailDescription',{email}) }}
          </div>
          <Button
            size="small"
            class="absolute bottom-10 border-gray-line"
            type="default"
            @click="toModify('email')">
            {{ t('personalCenter.modify') }}
          </Button>
        </template>
        <template v-else>
          <div class="whitespace-pre-line break-all text-theme-content">
            {{ t('personalCenter.security.unbindEmailDesc') }}
          </div>
          <Button
            size="small"
            class="absolute bottom-10"
            type="primary"
            @click="bindEmail">
            {{ t('personalCenter.bind') }}
          </Button>
        </template>
      </div>
    </PureCard>
  </div>
  <PasswordConfirm
    v-if="bindVisible"
    v-model:value="confirmPassword"
    v-model:visible="bindVisible"
    @ok="confirmPasswordHandle" />
  <BindMobileEmail
    v-if="bindNextVisible"
    v-model:visible="bindNextVisible"
    :linkSecret="confirmPassword"
    :valueKey="bindValueKey"
    :isModify="false"
    @ok="bindMobileEmailOk" />
  <ModifyPassword v-if="passwordVisible" v-model:visible="passwordVisible" />
  <ModifyMobileEmail
    v-if="visible"
    v-model:visible="visible"
    :valueKey="valueKey"
    :tenantInfo="_userInfo"
    @ok="ok" />
</template>
