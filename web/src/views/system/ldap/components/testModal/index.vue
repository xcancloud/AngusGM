<script setup lang="ts">
import { ref, watch } from 'vue';
import { Alert, Skeleton } from 'ant-design-vue';
import { Icon, Modal } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';

import { userDirectory } from '@/api';

/**
 * Component props interface for test modal
 * @param {boolean} visible - Modal visibility state
 * @param {string} id - Directory ID to test
 */
interface Props {
  visible: boolean;
  id: string
}

const emit = defineEmits<{(e: 'update:visible', value: boolean) }>();
const props = withDefaults(defineProps<Props>(), {
  visible: false
});

const { t } = useI18n();

/**
 * Get alert type based on success flag
 * @param {boolean|null} flag - Success flag
 * @returns {string} Alert type: 'success', 'error', or 'info'
 */
const getType = (flag = null) => {
  if (flag) {
    return 'success';
  }

  if (flag === false) {
    return 'error';
  }

  return 'info';
};

/**
 * Close modal and emit visibility update
 */
const cancel = () => {
  emit('update:visible', false);
};

// Test result data and loading state
const showData = ref<any>({});
const loading = ref(false);

// Test result messages for different operations
const connetMsg = ref(''); // Connection test message
const userMsg = ref(''); // User sync test message
const groupMsg = ref(''); // Group sync test message
const memberMsg = ref(''); // Membership sync test message

/**
 * Initialize test modal and execute LDAP directory test
 * Fetches directory configuration and runs test to validate connection and sync
 */
const init = async () => {
  loading.value = true;

  // Get directory configuration details
  const [error, res] = await userDirectory.getDirectoryDetail(props.id);
  if (error) {
    loading.value = false;
    return;
  }

  // Execute directory test with configuration
  const [error1, res1] = await userDirectory.testDirectory(res.data);
  if (error1) {
    loading.value = false;
    return;
  }

  // Process and display test results
  showData.value = res1.data;

  // Update connection test message
  connetMsg.value = showData.value.connectSuccess
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

  loading.value = false;
};

/**
 * Watch modal visibility changes to auto-execute test
 * Automatically runs test when modal becomes visible
 */
watch(() => props.visible, newValue => {
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
    <Skeleton v-if="loading" active />
    <template v-else>
      <Alert
        key="connect"
        class="text-3"
        showIcon
        :message="connetMsg"
        :type="getType(showData.connectSuccess)">
        <template v-if="showData.connectSuccess === null" #icon>
          <Icon icon="icon-jinyong"></Icon>
        </template>
      </Alert>
      <div v-if="showData.connectSuccess === false" class="px-2 text-warn">{{ showData.errorMessage }}</div>
      <Alert
        key="user"
        :message="t('ldap.messages.userLabel') + userMsg"
        class="mt-3 text-3"
        showIcon
        :type="getType(showData.userSuccess)">
        <template v-if="showData.userSuccess === null" #icon>
          <Icon icon="icon-jinyong"></Icon>
        </template>
      </Alert>
      <div v-if="showData.userSuccess === false" class="px-2 text-warn">{{ showData.errorMessage }}</div>
      <Alert
        key="group"
        :message="t('ldap.messages.groupLabel') + groupMsg"
        class="mt-3 text-3"
        showIcon
        :type="getType(showData.groupSuccess)">
        <template v-if="showData.groupSuccess === null" #icon>
          <Icon icon="icon-jinyong"></Icon>
        </template>
      </Alert>
      <div v-if="showData.groupSuccess === false" class="px-2 text-warn">{{ showData.errorMessage }}</div>
      <Alert
        key="member"
        :message="t('ldap.messages.memberLabel') + memberMsg"
        class="mt-3 text-3"
        showIcon
        :type="getType(showData.membershipSuccess)">
        <template v-if="showData.membershipSuccess === null" #icon>
          <Icon icon="icon-jinyong"></Icon>
        </template>
      </Alert>
      <div v-if="showData.membershipSuccess === false" class="px-2 text-warn">{{ showData.errorMessage }}</div>
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
