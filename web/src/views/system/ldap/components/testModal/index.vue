<script setup lang="ts">
import { ref, watch } from 'vue';
import { Alert, Skeleton } from 'ant-design-vue';
import { Icon, Modal } from '@xcan/design';


import {userDirectory} from '@/api';

interface Props {
  visible: boolean;
  id: string
}

const emit = defineEmits<{(e: 'update:visible', value: boolean) }>();
const props = withDefaults(defineProps<Props>(), {
  visible: false
});

const getType = (flag = null) => {
  if (flag) {
    return 'success';
  }

  if (flag === false) {
    return 'error';
  }

  return 'info';
};

const cancel = () => {
  emit('update:visible', false);
};

const showData = ref<any>({});
const loading = ref(false);

// 链接
const connetMsg = ref('');

const userMsg = ref('');

const groupMsg = ref('');

const memberMsg = ref('');

const init = async () => {
  loading.value = true;
  const [error, res] = await userDirectory.getDirectoryDetail(props.id);
  if (error) {
    loading.value = false;
    return;
  }
  const [error1, res1] = await userDirectory.testDirectory(res.data);
  if (error1) {
    loading.value = false;
    return;
  }

  showData.value = res1.data;
  connetMsg.value = showData.value.connectSuccess ? '链接成功' : '链接失败';
  userMsg.value = showData.value.userSuccess === null
    ? ''
    : showData.value.userSuccess
      ? `同步用户成功，用户总数${showData.value.totalUserNum}个，新增用户${showData.value.addUserNum} 个，更新用户${showData.value.updateUserNum}个，删除用户${showData.value.deleteUserNum}个，忽略用户${showData.value.ignoreUserNum}个。`
      : '同步用户成功失败';
  groupMsg.value = showData.value.groupSuccess === null
    ? ''
    : showData.value.groupSuccess
      ? `同步组成功，组总数${showData.value.totalUserNum}个，新增组${showData.value.addUserNum} 个，更新组${showData.value.updateUserNum}个，删除组${showData.value.deleteUserNum}个，忽略组${showData.value.ignoreUserNum}个。`
      : '同步组成功失败';

  memberMsg.value = showData.value.membershipSuccess === null
    ? ''
    : showData.value.membershipSuccess
      ? `同步组成员成功，成员总数${showData.value.totalUserNum}个，新增成员${showData.value.addUserNum} 个，更新成员${showData.value.updateUserNum}个，删除成员${showData.value.deleteUserNum}个，忽略成员${showData.value.ignoreUserNum}个。`
      : '同步组成员成功失败';
  loading.value = false;
};

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
    title="测试"
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
        :message="'用户：' + userMsg"
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
        :message="'组：' + groupMsg"
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
        :message="'组成员：' + memberMsg"
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
