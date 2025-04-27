<script setup lang="ts">
import { computed, inject, ref, nextTick, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { Grid, PureCard, Icon, Image, Input } from '@xcan/design';
import { site } from '@xcan/utils';
import Cropper from '@xcan/cropper';
import '@xcan/cropper/style.css';

import defaultAvatar from '../assets/default.jpg';

import { user } from '@/api';

type UpdateInfo = (data: Record<string, string>) => void

const updateTenantInfo: UpdateInfo = inject('updateTenantInfo') as UpdateInfo;
const tenantInfo = inject('tenantInfo', ref());

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
    ...tenantInfo.value,
    avatar
  };
  updateTenantInfo(temp);
};

onMounted(async () => {
  const host = await site.getUrl('gm');
  authUrl.value = host + '/system/auth';
});

const avatar = computed(() => tenantInfo.value.avatar || defaultAvatar);

const tenantRealNameStatus = computed(() => {
  if (!tenantInfo.value || !tenantInfo.value.tenantRealNameStatus) {
    return {
      message: t('personalCenter.information.toAuth'),
      textColor: 'text-danger',
      icon: 'icon-tishi1'
    };
  }

  const { message, value } = tenantInfo.value.tenantRealNameStatus;
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
  if (!tenantInfo.value || !tenantInfo.value.tenantRealNameStatus) {
    return true;
  }

  const { value } = tenantInfo.value.tenantRealNameStatus;

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
  const type = tenantInfo.value?.type?.value;
  switch (type) {
    case 'ENTERPRISE':
      label = t('personalCenter.information.enterpriseName');
      break;
    case 'GOVERNMENT':
      label = t('personalCenter.information.entityName');
      break;
  }
  return [[
    { dataIndex: 'accountName', label },
    { dataIndex: 'username', label: t('personalCenter.information.userName') }
  ]];
});

const accountName = computed(() => {
  const name = tenantInfo.value?.tenantName;
  if (!name) {
    return 'xcan' + tenantInfo.value?.mobile;
  }

  return name;
});

const userNameInputRef = ref();
const editUserName = ref(false);
const userName = ref('');
const handleEditUserName = () => {
  userName.value = tenantInfo.value?.username;
  editUserName.value = true;
  nextTick(() => {
    userNameInputRef.value?.focus();
  });
};

const handleBlur = async () => {
  if (userName.value === tenantInfo.value?.username || !userName.value) {
    editUserName.value = false;
    return;
  }

  const [error] = await user.updateCurrentUser({ username: userName.value });
  if (error) {
    return;
  }
  updateTenantInfo({
    ...tenantInfo.value,
    username: userName.value
  });
  editUserName.value = false;
};

const cancelEditUserName = () => {
  userName.value = tenantInfo.value?.tenantName;
  editUserName.value = false;
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
          <template v-if="editUserName">
            <div class="flex items-center absolute -top-1">
              <Input
                ref="userNameInputRef"
                v-model:value="userName"
                class="w-50 mr-2"
                @blur="handleBlur" />
              <span
                class="whitespace-nowrap hover:text-text-link-hover cursor-pointer"
                @click="cancelEditUserName">取消</span>
            </div>
          </template>
          <template v-else>
            <span class="flex items-center"> {{ tenantInfo.username }}<Icon
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
