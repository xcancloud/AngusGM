<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Button, Spin } from 'ant-design-vue';
import { Colon, Icon, modal, PureCard } from '@xcan-angus/vue-ui';

import { user } from '@/api';

const { t } = useI18n();

// Component state variables
const loading = ref(true); // Controls initial loading state
const userInfo = ref<Record<string, string>>({}); // User account information
const showWechatContainer = ref(false); // Controls WeChat binding modal visibility
const spinning = ref(true); // Controls spinner state for operations

/**
 * Computed property for WeChat account information
 * Extracts WeChat binding details from user data
 */
const wechatInfo = computed(() => {
  return {
    id: userInfo.value.wechatUserId,
    date: userInfo.value.wechatUserBindDate,
    account: userInfo.value.wechatUserAccount
  };
});

/**
 * Computed property for Google account information
 * Extracts Google binding details from user data
 */
const googleInfo = computed(() => {
  return {
    id: userInfo.value.googleUserId,
    date: userInfo.value.googleUserBindDate,
    account: userInfo.value.googleUserAccount
  };
});

/**
 * Computed property for GitHub account information
 * Extracts GitHub binding details from user data
 */
const githubInfo = computed(() => {
  return {
    id: userInfo.value.githubUserId,
    date: userInfo.value.githubUserBindDate,
    account: userInfo.value.githubUserAccount
  };
});

/**
 * Loads current user information from API
 * Updates component state with user data
 */
const loadUser = async () => {
  const [error, res] = await user.getCurrentUser();
  loading.value = false;
  if (error) {
    return;
  }

  userInfo.value = res.data;
};

/**
 * Initiates account binding process for specified platform
 * Currently supports Google, WeChat, and GitHub
 * @param type - Platform type to bind account to
 */
const bindAccount = async (type: 'GITHUB' | 'WECHAT' | 'GOOGLE'): Promise<void> => {
  if (type === 'GOOGLE') {
    // TODO: Implement Google account binding
  }
};

/**
 * Initiates account unbinding process with confirmation dialog
 * Shows confirmation modal before proceeding with unbinding
 * @param type - Platform type to unbind account from
 */
const unbind = (type: 'GITHUB' | 'WECHAT' | 'GOOGLE'): void => {
  let typeText = '';
  switch (type) {
    case 'WECHAT':
      typeText = t('information.enterprise.wechat');
      break;
    case 'GOOGLE':
      typeText = t('information.enterprise.google');
      break;
    case 'GITHUB':
      typeText = t('information.enterprise.github');
      break;
  }

  // Show confirmation dialog before unbinding
  modal.confirm({
    centered: true,
    title: t('information.account.hint'),
    content: t('information.account.unbindDesc', { typeText }),
    async onOk () {
      const [error] = await user.unbind({ type });
      if (error) {
        return;
      }
      // Reload user data after successful unbinding
      loadUser();
    }
  });
};

// Lifecycle hook - load user data when component mounts
onMounted(() => {
  loadUser();
});
</script>

<template>
  <!-- WeChat binding modal overlay -->
  <div
    v-show="showWechatContainer"
    class="wechat-container-mask top-0 left-0 right-0 bottom-0"
    @click="showWechatContainer=false">
    <div class="wechat-inner-container">
      <div class="flex justify-between items-center">
        <div class="text-3.5 text-theme-title">{{ t('information.account.bindWechat') }}</div>
        <Icon
          class="text-3.5 cursor-pointer text-theme-sub-content"
          icon="icon-shanchuguanbi"
          @click="showWechatContainer=false" />
      </div>
      <Spin :spinning="spinning" :tip="t('information.messages.loading')">
        <div id="wechat-container" class="wechat-container"></div>
      </Spin>
      <div class="wechat-desc text-theme-content">
        {{ t('information.account.bindWechatDesc') }}
      </div>
    </div>
  </div>
  <div class="flex justify-center flex-nowrap">
    <PureCard
      key="1"
      :loading="loading"
      class="p-6 flex flex-col flex-freeze-auto w-96 items-center mr-6">
      <div class="w-full flex flex-col items-center py-10 border-b border-theme-divider">
        <Icon
          :class="{ 'active-wechat': wechatInfo.id }"
          class="text-6xl text-gray-placeholder"
          icon="icon-weixin" />
        <span class="mt-4 text-3.5 text-theme-title">{{ $t('information.enterprise.wechat') }}</span>
        <template v-if="wechatInfo.id">
          <span class="flex mt-6 text-3.5 text-green-wechat">
            <Icon class="mr-2 text-3.5" icon="icon-right" />{{ $t('information.account.bound') }}
          </span>
        </template>
        <template v-else>
          <span class="flex mt-6 text-3.5 text-theme-content">{{ $t('information.account.unbound') }}</span>
        </template>
      </div>
      <div class="w-full flex flex-col text-3 items-center py-10">
        <template v-if="wechatInfo.id">
          <div class="flex flex-nowrap">
            <div class="flex flex-col flex-wrap items-end text-theme-content mr-2.5">
              <span class="flex-freeze-auto">{{ $t('information.account.bindAccount') }}<Colon /></span>
              <span class="flex-freeze-auto mt-6">{{ $t('information.account.bindTime') }}<Colon /></span>
            </div>
            <div class="flex flex-col flex-wrap items-start text-theme-title">
              <span class="flex-freeze-auto">{{ wechatInfo.account }}</span>
              <span class="flex-freeze-auto mt-6">{{ wechatInfo.date }}</span>
            </div>
          </div>
          <Button
            size="small"
            class="mt-10 border-gray-line"
            type="default"
            @click="unbind('WECHAT')">
            {{ $t('information.account.removeBind') }}
          </Button>
        </template>
        <template v-else>
          <Button
            size="small"
            class="mt-10"
            type="primary"
            @click="bindAccount('WECHAT')">
            {{ $t('information.account.immediatelyBinding') }}
          </Button>
        </template>
      </div>
    </PureCard>
    <PureCard
      key="2"
      :loading="loading"
      class="p-6 flex flex-col flex-freeze-auto w-96 items-center mr-6">
      <div class="w-full flex flex-col items-center py-10 border-b border-theme-divider">
        <Icon
          :class="{ 'active-google': googleInfo.id }"
          class="text-6xl text-gray-placeholder"
          icon="icon-google" />
        <span class="mt-4 text-3.5 text-theme-title">{{ $t('information.enterprise.google') }}</span>
        <template v-if="googleInfo.id">
          <span class="flex mt-6 text-3.5 text-green-wechat">
            <Icon class="mr-2 text-3.5" icon="icon-right" />{{ $t('information.account.bound') }}
          </span>
        </template>
        <template v-else>
          <span class="flex mt-6 text-3.5 text-theme-content">{{ $t('information.account.unbound') }}</span>
        </template>
      </div>
      <div class="w-full flex flex-col text-3 items-center py-10">
        <template v-if="googleInfo.id">
          <div class="flex flex-nowrap">
            <div class="flex flex-col flex-wrap items-end text-theme-content mr-2.5">
              <span class="flex-freeze-auto">{{ $t('information.account.bindAccount') }}<Colon /></span>
              <span class="flex-freeze-auto mt-6">{{ $t('information.account.bindTime') }}<Colon /></span>
            </div>
            <div class="flex flex-col flex-wrap items-start text-theme-title">
              <span class="flex-freeze-auto">{{ googleInfo.account }}</span>
              <span class="flex-freeze-auto mt-6">{{ googleInfo.date }}</span>
            </div>
          </div>
          <Button
            size="small"
            class="mt-10 border-gray-line"
            type="default"
            @click="unbind('GOOGLE')">
            {{ $t('information.account.removeBind') }}
          </Button>
        </template>
        <template v-else>
          <Button
            size="small"
            class="mt-10"
            type="primary"
            @click="bindAccount('GOOGLE')">
            {{ $t('information.account.immediatelyBinding') }}
          </Button>
        </template>
      </div>
    </PureCard>
    <PureCard
      key="3"
      :loading="loading"
      class="p-6 flex flex-col flex-freeze-auto w-96 items-center mr-6">
      <div class="w-full flex flex-col items-center py-10 border-b border-theme-divider">
        <Icon
          :class="{ 'active-github': githubInfo.id }"
          class="text-6xl text-gray-placeholder"
          icon="icon-Github" />
        <span class="mt-4 text-3.5 text-theme-title">{{ $t('information.enterprise.github') }}</span>
        <template v-if="githubInfo.id">
          <span class="flex mt-6 text-3.5 text-green-wechat">
            <Icon class="mr-2 text-3.5" icon="icon-right" />{{ $t('information.account.bound') }}
          </span>
        </template>
        <template v-else>
          <span class="flex mt-6 text-3.5 text-theme-content">{{ $t('information.account.unbound') }}</span>
        </template>
      </div>
      <div class="w-full flex flex-col text-3 items-center py-10">
        <template v-if="githubInfo.id">
          <div class="flex flex-nowrap">
            <div class="flex flex-col flex-wrap items-end text-theme-content mr-2.5">
              <span class="flex-freeze-auto">{{ $t('information.account.bindAccount') }}<Colon /></span>
              <span class="flex-freeze-auto mt-6">{{ $t('information.account.bindTime') }}<Colon /></span>
            </div>
            <div class="flex flex-col flex-wrap items-start text-theme-title">
              <span class="flex-freeze-auto">{{ githubInfo.account }}</span>
              <span class="flex-freeze-auto mt-6">{{ githubInfo.date }}</span>
            </div>
          </div>
          <Button
            size="small"
            class="mt-10 border-gray-line"
            type="default"
            @click="unbind('GITHUB')">
            {{ $t('information.account.removeBind') }}
          </Button>
        </template>
        <template v-else>
          <Button
            size="small"
            class="mt-10"
            type="primary"
            @click="bindAccount('GITHUB')">
            {{ $t('information.account.immediatelyBinding') }}
          </Button>
        </template>
      </div>
    </PureCard>
  </div>
</template>

<style scoped>
.wechat-container-mask {
  position: fixed;
  z-index: 999;
  background: rgba(0, 24, 52, 10%);
  backdrop-filter: blur(0);
}

.wechat-inner-container {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 540px;
  height: 376px;
  padding: 30px 40px;
  transform: translate(-50%, -50%);
  border-radius: 10px;
  background-color: #fff;
  box-shadow: 0 10px 50px 0 rgba(0, 56, 124, 20%);
}

.wechat-container {
  flex: 1;
  width: 250px;
  height: 286px;

  @apply flex justify-center overflow-hidden mx-auto;
}

.wechat-desc {
  @apply absolute bottom-5 left-2/4 text-center;

  transform: translateX(-50%);
  font-size: 14px;
  line-height: 18px;
}

.active-wechat {
  @apply text-green-wechat;
}

.active-google {
  @apply text-blue-google;
}

.active-github {
  @apply text-black-title;
}
</style>
