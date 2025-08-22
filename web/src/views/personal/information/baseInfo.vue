<script setup lang="ts">
import { computed, nextTick, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Cropper, Grid, Icon, Image, Input, notification, PureCard } from '@xcan-angus/vue-ui';
import { appContext, AppOrServiceRoute, DomainManager, toClipboard } from '@xcan-angus/infra';

import defaultAvatar from './assets/default.jpg';
import { user } from '@/api';

// Reactive state variables
const authUrl = ref<string>(); // URL for real-name authentication
const { t } = useI18n(); // Internationalization helper
const visible = ref(false); // Controls cropper visibility

// Computed properties
/**
 * Gets user avatar or falls back to default avatar
 */
const avatarRef = ref(appContext.getUser()?.avatar || defaultAvatar);

// Upload configuration for avatar
const params = {
  bizKey: 'avatar'
};

/**
 * Opens the avatar upload cropper
 */
const upload = (): void => {
  visible.value = true;
};

/**
 * Handles successful avatar upload
 * Updates user avatar in backend and local context
 */
const uploadSuccess = async (jsonData: any): Promise<void> => {
  const avatar = jsonData.data[0].url;
  const [error] = await user.updateCurrentUser({ avatar });
  if (error) {
    return;
  }

  // Update local user context with new avatar
  const temp = {
    ...appContext.getUser(),
    avatar
  };

  appContext.setUser(temp);

  avatarRef.value = avatar;
};

// Lifecycle hook - initialize authentication URL on component mount
onMounted(() => {
  const host = DomainManager.getInstance().getApiDomain(AppOrServiceRoute.gm);
  authUrl.value = host + '/system/auth';
});

/**
 * Computes real-name authentication status display properties
 * Returns appropriate message, color, and icon based on status
 */
const tenantRealNameStatus = computed(() => {
  if (!appContext.getUser() || !appContext.getUser()?.tenantRealNameStatus) {
    return {
      message: t('information.messages.toAuth'),
      textColor: 'text-danger',
      icon: 'icon-tishi1'
    };
  }

  const tenantRealNameStatus = appContext.getUser()?.tenantRealNameStatus;
  const result = {
    message: tenantRealNameStatus?.message,
    textColor: 'text-danger',
    icon: 'icon-tishi1'
  };

  // Set appropriate styling based on authentication status
  switch (tenantRealNameStatus?.value) {
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

/**
 * Determines if user can perform real-name authentication
 * Based on current authentication status
 */
const canAuth = computed(() => {
  if (!appContext.getUser() || !appContext.getUser()?.tenantRealNameStatus) {
    return true;
  }

  const value = appContext.getUser()?.tenantRealNameStatus?.value;

  switch (value) {
    case 'NOT_SUBMITTED':
    case 'FAILED_AUDIT':
      return true;
    default:
      return false;
  }
});

/**
 * Computes table columns configuration based on tenant type
 * Adapts labels for different organization types (Enterprise/Government)
 */
const columns = computed(() => {
  let label = t('information.columns.accountName');
  const type = appContext.getTenant()?.type?.value;

  switch (type) {
    case 'ENTERPRISE':
      label = t('information.enterprise.enterpriseName');
      break;
    case 'GOVERNMENT':
      label = t('information.enterprise.entityName');
      break;
  }

  return [[
    { dataIndex: 'tenantId', label: t('information.columns.accountId') },
    { dataIndex: 'accountName', label },
    { dataIndex: 'username', label: t('information.columns.userName') }
  ]];
});

/**
 * Computes account name display value
 * Falls back to generated name if no account name is set
 */
const accountName = computed(() => {
  const name = appContext.getTenant()?.name;
  if (!name) {
    return 'xcan' + appContext.getUser()?.mobile;
  }
  return name;
});

// Username editing functionality
const usernameInputRef = ref();
const editUsername = ref(false);
const username = ref('');

/**
 * Initiates username editing mode
 * Sets up input field and focuses it
 */
const handleEditUserName = () => {
  username.value = appContext.getUser()?.username || '';
  editUsername.value = true;
  nextTick(() => {
    usernameInputRef.value?.focus();
  });
};

/**
 * Handles username input blur event
 * Saves changes to backend and updates local context
 */
const handleBlur = async () => {
  if (username.value === appContext.getUser()?.username || !username.value) {
    editUsername.value = false;
    return;
  }

  const [error] = await user.updateCurrentUser({ username: username.value });
  if (error) {
    return;
  }

  // Update local user context with new username
  appContext.setUser({
    ...appContext.getUser(),
    username: username.value
  });

  editUsername.value = false;
};

/**
 * Cancels username editing and reverts to original value
 */
const cancelEditUserName = () => {
  username.value = appContext.getUser()?.username || '';
  editUsername.value = false;
};

/**
 * Copies user's tenant ID to clipboard
 * Shows success notification on completion
 */
const copyID = () => {
  toClipboard.toClipboard(appContext.getUser()?.tenantId)
    .then(() => {
      notification.success(t('information.messages.copySuccess'));
    });
};
</script>

<template>
  <!-- Basic information card with avatar and account details -->
  <PureCard class="flex items-center w-11/12 2xl:px-6 mx-auto px-15 py-6 mb-2">
    <!-- Avatar section with upload functionality -->
    <div class="relative flex items-center justify-center w-25 h-25 overflow-hidden rounded-full hover-show">
      <Image :src="avatarRef" type="avatar" />
      <div
        class="hover-show cursor-pointer flex-col text-white absolute left-0 top-0 items-center justify-center w-full h-full hidden hover:bg-black-mask rounded-full z-2"
        @click="upload">
        <div>{{ t('information.messages.upload') }}</div>
      </div>
    </div>

    <!-- Account information grid -->
    <Grid
      class="ml-12"
      labelStyle="font-weight: 700;"
      :columns="columns">
      <!-- Tenant ID with copy functionality -->
      <template #tenantId>
        <div class="flex space-x-2 items-center">
          <span>{{ appContext.getUser()?.tenantId }}</span>
          <Icon
            icon="icon-fuzhi"
            class="text-4 text-text-link cursor-pointer"
            @click="copyID" />
        </div>
      </template>

      <!-- Account name with authentication status -->
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
              target="_blank">{{ t('information.messages.toAuth') }}</a>
          </span>
        </div>
      </template>

      <!-- Username with inline editing -->
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
                @click="cancelEditUserName">{{ t('information.messages.cancel') }}</span>
            </div>
          </template>
          <template v-else>
            <span class="flex items-center">
              {{ appContext.getUser()?.username }}
              <Icon
                icon="icon-shuxie"
                class="ml-3 text-text-link text-3.5 hover:text-text-link-hover cursor-pointer"
                @click="handleEditUserName" />
            </span>
          </template>
        </div>
      </template>
    </Grid>
  </PureCard>

  <!-- Avatar cropper modal -->
  <Cropper
    v-model:visible="visible"
    :params="params"
    @success="uploadSuccess" />
</template>

<style scoped>
/* Hover effect for avatar upload overlay */
.hover-show:hover .hover-show {
  @apply flex;
}
</style>
