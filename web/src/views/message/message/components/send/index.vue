<script setup lang='ts'>
import { defineAsyncComponent, ref, inject, Ref } from 'vue';
import { PureCard, notification } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import { Button } from 'ant-design-vue';

import { message } from '@/api';
import { ReceiveObjectType, MessageReceiveType, SendType } from './PropsType';

const SendForm = defineAsyncComponent(() => import('./form.vue'));
const SendList = defineAsyncComponent(() => import('./list.vue'));

const { t } = useI18n();
const router = useRouter();
const formRef = ref();
const tenantInfo: Ref = inject('tenantInfo', ref());
const userList = ref<{ id: string; fullName: string; }[]>([]);
const deptList = ref<{ id: string; name: string; }[]>([]);
const groupList = ref<{ id: string; name: string; }[]>([]);
const title = ref<string>('');
const content = ref<string>('');
const timingDate = ref<string>('');
const receiveType = ref<MessageReceiveType>('SITE');
const sendType = ref<SendType>('SEND_NOW');
const receiveObjectType = ref<ReceiveObjectType>('USER');
const receiveTenantId = ref<string>('');
const tenantName = ref<string>('');
const notify = ref(0); // 更新表单通知

const loading = ref(false);

const titleRule = ref(false);
const contentRule = ref(false);
const propsContentRuleMsg = ref(t('请输入消息内容'));
const dateRule = ref(false);

const submit = async () => {
  if (loading.value) {
    return;
  }

  if (!formRef.value.validate()) {
    // if (!title.value) {
    //   titleRule.value = true;
    // }
    // if (!content.value) {
    //   contentRule.value = true;
    //   propsContentRuleMsg.value = t('请输入消息内容');
    // }
    //
    // if (content.value.length > 8000) {
    //   contentRule.value = true;
    //   propsContentRuleMsg.value = t('内容太长,无法发送');
    // }
    return;
  }

  if (sendType.value === 'TIMING_SEND' && !timingDate.value) {
    if (!timingDate.value) {
      dateRule.value = true;
    }
    return;
  }

  let params: {
    title: string;
    content: string;
    receiveType: MessageReceiveType;
    sendType: SendType;
    timingDate?: string;
    receiveTenantId?: string;
    receiveObjectType?: ReceiveObjectType,
    receiveObjects?: { id: string, name: string }[],

  } = {
    content: content.value,
    sendType: sendType.value,
    receiveObjectType: receiveObjectType.value,
    receiveType: receiveType.value,
    timingDate: timingDate.value,
    title: title.value
  };
  if (['TENANT', 'USER', 'GROUP', 'DEPT'].includes(receiveObjectType.value)) {
    params = { ...params, receiveTenantId: tenantInfo.value.tenantId };
    params = { ...params, receiveObjects: [{ id: tenantInfo.value.tenantId, name: tenantInfo.value.tenantName }] };
  }

  if (receiveObjectType.value === 'USER') {
    if (!userList.value.length) {
      notification.warning(t('sendTips3'));
      return;
    }
    params = { ...params, receiveObjectType: receiveObjectType.value };
    params = { ...params, receiveObjects: userList.value.map(item => ({ id: item.id, name: item.fullName })) };
  }
  if (receiveObjectType.value === 'GROUP') {
    if (!groupList.value.length) {
      notification.warning(t('sendTips4'));
      return;
    }
    params = { ...params, receiveObjectType: receiveObjectType.value };
    params = { ...params, receiveObjects: groupList.value };
  }
  if (receiveObjectType.value === 'DEPT') {
    if (!deptList.value.length) {
      notification.warning(t('sendTips5'));
      return;
    }
    params = { ...params, receiveObjectType: receiveObjectType.value };
    params = { ...params, receiveObjects: deptList.value };
  }

  loading.value = true;
  const [error, res] = await message.sendMessage(params);
  loading.value = false;
  if (error) {
    return;
  }
  notify.value++;
  notification.success(t('sentSuccessfully'));
  router.push(`/messages/message?id=${res.data.id}`);
};

const handleCancel = () => {
  router.push('/messages/message');
};

</script>
<template>
  <PureCard class="p-3.5 flex-1 h-full">
    <div class="flex space-x-3.5 pl-3.5" style="height: calc(100% - 28px);">
      <SendForm
        ref="formRef"
        v-model:propsTitle="title"
        v-model:propsContent="content"
        v-model:propsReceiveType="receiveType"
        v-model:propsSendType="sendType"
        v-model:propsTimingDate="timingDate"
        v-model:propsTitleRule="titleRule"
        v-model:propsContentRule="contentRule"
        v-model:propsContentRuleMsg="propsContentRuleMsg"
        v-model:propsDateRule="dateRule"
        :notify="notify" />
      <SendList
        v-model:userList="userList"
        v-model:deptList="deptList"
        v-model:groupList="groupList"
        v-model:receiveObjectType="receiveObjectType"
        v-model:receiveTenantId="receiveTenantId"
        v-model:tenantName="tenantName" />
    </div>
    <div class="text-center">
      <Button
        size="small"
        class="px-3 mr-3"
        @click="handleCancel">
        取消
      </Button>
      <Button
        :loading="loading"
        type="primary"
        size="small"
        class="px-3"
        @click="submit">
        {{ t('submit') }}
      </Button>
    </div>
  </PureCard>
</template>
