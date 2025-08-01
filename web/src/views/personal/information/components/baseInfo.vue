<script setup lang="ts">
import { computed, nextTick, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Cropper, Grid, Icon, Image, Input, notification, PureCard } from '@xcan-angus/vue-ui';
import { appContext, AppOrServiceRoute, DomainManager, toClipboard } from '@xcan-angus/infra';

import defaultAvatar from '../assets/default.jpg';

import { user } from '@/api';

const authUrl = ref<string>();
const { t } = useI18n();
const visible = ref(false);

const params = {
  bizKey: 'avatar'
};
const upload = (): void => {
  visible.value = true;
};

const success = async (jsonData): Promise<void> => {
  const avatar = jsonData.data[0].url;
  const [error] = await user.updateCurrentUser({ avatar });
  if (error) {
    return;
  }

  const temp = {
    ...appContext.getUser(),
    avatar
  };
  appContext.setUser(temp);
};

// TODO 提到api
onMounted(async () => {
  const host = await DomainManager.getInstance().getApiDomain(AppOrServiceRoute.gm);
  authUrl.value = host + '/system/auth';
});

const avatar = computed(() => appContext.getUser()?.avatar || defaultAvatar);

const tenantRealNameStatus = computed(() => {
  if (!appContext.getUser() || !appContext.getUser()?.tenantRealNameStatus) {
    return {
      message: t('personalCenter.information.toAuth'),
      textColor: 'text-danger',
      icon: 'icon-tishi1'
    };
  }

  const { message, value } = appContext.getUser()?.tenantRealNameStatus;
  const result = {
    message,
    textColor: 'text-danger',
    icon: 'icon-tishi1'
  };
  switch (value) {
    case 'AUDITING':
      result.textColor = 'text-warn';
      break;
    case 'AUDITED':
      result.textColor = 'text-success';
      result.icon = 'icon-right';
      break;
  }

  return result;
});

const canAuth = computed(() => {
  if (!appContext.getUser() || !appContext.getUser()?.tenantRealNameStatus) {
    return true;
  }

  const { value } = appContext.getUser()?.tenantRealNameStatus;

  switch (value) {
    case 'NOT_SUBMITTED':
    case 'FAILED_AUDIT':
      return true;
    default:
      return false;
  }
});

const columns = computed(() => {
  let label = t('personalCenter.information.accountName');
  const type = appContext.getTenant()?.type?.value;
  switch (type) {
    case 'ENTERPRISE':
      label = t('personalCenter.information.enterpriseName');
      break;
    case 'GOVERNMENT':
      label = t('personalCenter.information.entityName');
      break;
  }
  return [[
    { dataIndex: 'tenantId', label: t('账号ID') },
    { dataIndex: 'accountName', label },
    { dataIndex: 'username', label: t('personalCenter.information.userName') }
  ]];
});

const accountName = computed(() => {
  const name = appContext.getTenant()?.name;
  if (!name) {
    return 'xcan' + appContext.getUser()?.mobile;
  }
  return name;
});

const usernameInputRef = ref();
const editUsername = ref(false);
const username = ref('');
const handleEditUserName = () => {
  username.value = appContext.getUser()?.username;
  editUsername.value = true;
  nextTick(() => {
    usernameInputRef.value?.focus();
  });
};

const handleBlur = async () => {
  if (username.value === appContext.getUser()?.username || !username.value) {
    editUsername.value = false;
    return;
  }

  const [error] = await user.updateCurrentUser({ username: username.value });
  if (error) {
    return;
  }
  appContext.setUser({
    ...appContext.getUser(),
    username: username.value
  });

  editUsername.value = false;
};

const cancelEditUserName = () => {
  username.value = appContext.getUser()?.username;
  editUsername.value = false;
};

const copyID = () => {
  toClipboard.toClipboard(appContext.getUser()?.tenantId)
    .then(() => {
      notification.success('复制到剪贴板');
    });
};

</script>
<template>
  <PureCard class="flex items-center w-11/12 2xl:px-6 mx-auto px-15 py-6 mb-2">
    <div class="relative flex items-center justify-center w-25 h-25 overflow-hidden rounded-full hover-show">
      <Image :src="avatar" type="avatar" />
      <div
        class="hover-show cursor-pointer flex-col text-white absolute left-0 top-0 items-center justify-center w-full h-full hidden hover:bg-black-mask rounded-full z-2"
        @click="upload">
        <div>{{ t('personalCenter.information.upload') }}</div>
      </div>
    </div>
    <Grid class="ml-12" :columns="columns">
      <template #tenantId>
        <div class="flex space-x-2 items-center">
          <span>{{ appContext.getUser()?.tenantId }}</span>
          <Icon
            icon="icon-fuzhi"
            class="text-4 text-text-link cursor-pointer"
            @click="copyID" />
        </div>
      </template>
      <template #accountName>
        <div class="flex">
          <span>{{ accountName }}</span>
          <span :class="tenantRealNameStatus?.textColor" class="flex items-center ml-3 text-3 leading-3">
            <Icon class="text-3.5" :icon="tenantRealNameStatus?.icon"></Icon>
            <span class="transform-gpu translate-y-0.25 ml-1">{{ tenantRealNameStatus?.message }}</span>
            <a
              v-if="canAuth"
              :href="authUrl"
              class="transform-gpu translate-y-px ml-6 text-theme-special"
              target="_blank">{{ t('personalCenter.information.toAuth') }}</a>
          </span>
        </div>
      </template>
      <template #username>
        <div class="relative">
          <template v-if="editUsername">
            <div class="flex items-center absolute -top-1">
              <Input
                ref="usernameInputRef"
                v-model:value="username"
                class="w-50 mr-2"
                @blur="handleBlur" />
              <span
                class="whitespace-nowrap hover:text-text-link-hover cursor-pointer"
                @click="cancelEditUserName">取消</span>
            </div>
          </template>
          <template v-else>
            <span class="flex items-center"> {{ appContext.getUser()?.username }}<Icon
              icon="icon-shuxie"
              class="ml-3 text-text-link text-3.5 hover:text-text-link-hover cursor-pointer"
              @click="handleEditUserName" /></span>
          </template>
        </div>
      </template>
    </Grid>
  </PureCard>
  <Cropper
    v-model:visible="visible"
    :params="params"
    @success="success" />
</template>
<style scoped>
.hover-show:hover .hover-show {
  @apply flex;
}
</style>
