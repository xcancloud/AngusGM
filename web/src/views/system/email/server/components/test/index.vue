<script setup lang='ts'>
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Button } from 'ant-design-vue';
import { Colon, Input, Modal, notification } from '@xcan-angus/vue-ui';
import { email } from '@/api';

interface Props {
  visible: boolean,
  id: string,
  address: string
}

const props = withDefaults(defineProps<Props>(), {});
const emit = defineEmits<{(e: 'update:visible', value: boolean): void }>();

const { t } = useI18n();

const handleCancel = () => {
  emit('update:visible', false);
};

const loading = ref(false);
const emailAddress = ref('');
const handleOk = async () => {
  if (!emailAddress.value) {
    return;
  }
  loading.value = true;
  const [error, data] = await email.testServerConfig({ serverId: props.id, toAddress: [emailAddress.value] });
  loading.value = false;
  if (error) {
    return;
  }

  notification.success(data?.msg);
  emit('update:visible', false);
};

const inputRule = ref(false);
const handleChange = (event: any) => {
  const value = event.target.value;
  inputRule.value = !value;
};
</script>
<template>
  <Modal
    width="540px"
    :title="t('testEmail')"
    :centered="true"
    :maskClosable="false"
    :keyboard="false"
    :visible="props.visible"
    destroyOnClose>
    <template #default>
      <span class="text-3 leading-3 text-theme-content">{{ t('emailAddress') }}<Colon /></span>
      <Input
        v-model:value="emailAddress"
        placeholder="接收邮件地址"
        :maxlength="100"
        @change="handleChange" />
      <div class="h-4 text-3 leading-3 text-rule mt-0.5">
        <template v-if="inputRule">
          请输入接收邮件地址
        </template>
      </div>
    </template>
    <template #footer>
      <Button size="small" @click="handleCancel">
        {{ t('cancel') }}
      </Button>
      <Button
        :loading="loading"
        type="primary"
        size="small"
        @click="handleOk">
        {{ t('sure') }}
      </Button>
    </template>
  </Modal>
</template>
