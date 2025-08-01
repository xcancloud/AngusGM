<script setup lang="ts">
import { ref, inject, Ref } from 'vue';
import { Button } from 'ant-design-vue';
import { Modal, Input, Hints, notification, Colon } from '@xcan-angus/vue-ui';
import { appContext } from '@xcan-angus/infra';

import { tenant } from '@/api';

interface Props {
  visible: boolean
}

withDefaults(defineProps<Props>(), { visible: false });

const emit = defineEmits<{(e: 'update:visible', value: boolean): void, (e: 'update', value: boolean): void }>();

const code = ref<string>('');
const loading = ref(false);
const count = ref(0);
const sendVerificationCode = async () => {
  loading.value = true;
  const [error] = await tenant.sendSignCancelSms();
  loading.value = false;
  if (error) {
    return;
  }
  count.value = 60;
  const timeout = setInterval(() => {
    count.value--;
    if (count.value === 0) {
      clearInterval(timeout);
    }
  }, 1000);
};

const inputError = ref(false);
const handleOk = async () => {
  if (!code.value) {
    inputError.value = true;
    return;
  }
  verificationCodeConfirm();
};

const verificationCodeConfirm = async () => {
  loading.value = true;
  const [error] = await tenant.confirmSignCancelSms(code.value);
  loading.value = false;
  if (error) {
    return;
  }
  notification.success('注销成功,将于24小时后生效');
  emit('update', true);
  emit('update:visible', false);
};

const handleCancel = () => {
  emit('update', false);
  emit('update:visible', false);
};
</script>
<template>
  <Modal
    :visible="visible"
    centered
    closable
    title="注销账号"
    @cancel="handleCancel">
    <template #default>
      <Hints text="注销人必须是系统管理员。" class="mb-4" />
      <div class="flex">
        <div class="mr-1 pt-1.25">
          <div>
            发送验证码
            <Colon />
          </div>
          <div class="mt-8">
            验证码
            <Colon />
          </div>
        </div>
        <div class="flex flex-col flex-1 space-y-5">
          <Button
            size="small"
            :disabled="!appContext.isSysAdmin() || count !== 0"
            :loading="loading"
            @click="sendVerificationCode">
            向当前登录人手机发送验证码 {{ count !== 0?`(${count})` : '' }}
          </Button>
          <Input
            v-model:value="code"
            size="small"
            dataType="number"
            :error="inputError"
            :maxlength="6" />
          <div style="margin-top: -5px;" class="h-4">
            <template v-if="inputError">请输入6位数验证码</template>
          </div>
        </div>
      </div>
    </template>
    <template #footer>
      <div>
        <Button size="small" @click="handleCancel">取消</Button>
        <Button
          size="small"
          type="primary"
          :disabled="!appContext.isSysAdmin()"
          @click="handleOk">
          确定
        </Button>
      </div>
    </template>
  </Modal>
</template>
