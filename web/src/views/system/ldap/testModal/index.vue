<script setup lang="ts">
import { ref, watch } from 'vue';
import { Alert, Skeleton } from 'ant-design-vue';
import { Icon, Modal } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';

import { userDirectory } from '@/api';
import { TestModalProps, TestResultData } from '../types';
import { getAlertType, processLdapTestResponse } from '../utils';

const props = withDefaults(defineProps<TestModalProps>(), {
  visible: false
});

const emit = defineEmits<{(e: 'update:visible', value: boolean) }>();

const { t } = useI18n();

/**
 * Close modal and emit visibility update
 */
const cancel = (): void => {
  emit('update:visible', false);
};

// Test result data and loading state
const showData = ref<TestResultData>({} as TestResultData);
const loading = ref(false);

// Test result messages for different operations
const connectMsg = ref(''); // Connection test message
const userMsg = ref(''); // User sync test message
const groupMsg = ref(''); // Group sync test message
const memberMsg = ref(''); // Membership sync test message

/**
 * Initialize test modal and execute LDAP directory test
 * Fetches directory configuration and runs test to validate connection and sync
 */
const init = async (): Promise<void> => {
  loading.value = true;

  try {
    // Get directory configuration details
    const [error, res] = await userDirectory.getDirectoryDetail(props.id);
    if (error) {
      return;
    }

    // Execute directory test with configuration
    const [error1, res1] = await userDirectory.testDirectory(res.data);
    if (error1) {
      return;
    }

    // Process and display test results
    showData.value = processLdapTestResponse(res1);

    // Update connection test message
    connectMsg.value = showData.value.connectSuccess
      ? t('ldap.messages.connectSuccess')
      : t('ldap.messages.connectFailed');

    // Update user sync test message
    userMsg.value = showData.value.userSuccess === null
      ? ''
      : showData.value.userSuccess
        ? t('ldap.messages.userSyncSuccess', {
          total: showData.value.totalUserNum,
          new: showData.value.addUserNum,
          update: showData.value.updateUserNum,
          delete: showData.value.deleteUserNum,
          ignore: showData.value.ignoreUserNum
        })
        : t('ldap.messages.userSyncFailed');

    // Update group sync test message
    groupMsg.value = showData.value.groupSuccess === null
      ? ''
      : showData.value.groupSuccess
        ? t('ldap.messages.groupSyncSuccess', {
          total: showData.value.totalUserNum,
          new: showData.value.addUserNum,
          update: showData.value.updateUserNum,
          delete: showData.value.deleteUserNum,
          ignore: showData.value.ignoreUserNum
        })
        : t('ldap.messages.groupSyncFailed');

    // Update membership sync test message
    memberMsg.value = showData.value.membershipSuccess === null
      ? ''
      : showData.value.membershipSuccess
        ? t('ldap.messages.memberSyncSuccess', {
          total: showData.value.totalUserNum,
          new: showData.value.addUserNum,
          update: showData.value.updateUserNum,
          delete: showData.value.deleteUserNum,
          ignore: showData.value.ignoreUserNum
        })
        : t('ldap.messages.memberSyncFailed');
  } finally {
    loading.value = false;
  }
};

/**
 * Watch modal visibility changes to auto-execute test
 * Automatically runs test when modal becomes visible
 */
watch(() => props.visible, (newValue) => {
  if (newValue) {
    init();
  }
}, {
  immediate: true
});
</script>

<template>
  <Modal
    :visible="props.visible"
    :title="t('ldap.titles.testConfiguration')"
    :footer="null"
    width="600px"
    @cancel="cancel">
    <!-- Loading Skeleton -->
    <Skeleton v-if="loading" active />

    <template v-else>
      <!-- Connection Test Result -->
      <Alert
        key="connect"
        class="text-3"
        showIcon
        :message="connectMsg"
        :type="getAlertType(showData.connectSuccess)">
        <template v-if="showData.connectSuccess === null" #icon>
          <Icon icon="icon-jinyong"></Icon>
        </template>
      </Alert>

      <!-- Connection Error Message -->
      <div v-if="showData.connectSuccess === false" class="px-2 text-warn">
        {{ showData.errorMessage }}
      </div>

      <!-- User Sync Test Result -->
      <Alert
        key="user"
        :message="t('ldap.messages.userLabel') + userMsg"
        class="mt-3 text-3"
        showIcon
        :type="getAlertType(showData.userSuccess)">
        <template v-if="showData.userSuccess === null" #icon>
          <Icon icon="icon-jinyong"></Icon>
        </template>
      </Alert>

      <!-- User Sync Error Message -->
      <div v-if="showData.userSuccess === false" class="px-2 text-warn">
        {{ showData.errorMessage }}
      </div>

      <!-- Group Sync Test Result -->
      <Alert
        key="group"
        :message="t('ldap.messages.groupLabel') + groupMsg"
        class="mt-3 text-3"
        showIcon
        :type="getAlertType(showData.groupSuccess)">
        <template v-if="showData.groupSuccess === null" #icon>
          <Icon icon="icon-jinyong"></Icon>
        </template>
      </Alert>

      <!-- Group Sync Error Message -->
      <div v-if="showData.groupSuccess === false" class="px-2 text-warn">
        {{ showData.errorMessage }}
      </div>

      <!-- Membership Sync Test Result -->
      <Alert
        key="member"
        :message="t('ldap.messages.memberLabel') + memberMsg"
        class="mt-3 text-3"
        showIcon
        :type="getAlertType(showData.membershipSuccess)">
        <template v-if="showData.membershipSuccess === null" #icon>
          <Icon icon="icon-jinyong"></Icon>
        </template>
      </Alert>
    </template>
  </Modal>
</template>

<style scoped>
.ant-alert-info {
  @apply !border-gray-border !bg-gray-text-bg;
}

.ant-alert-info .ant-alert-icon {
  @apply !text-gray-icon;
}
</style>
