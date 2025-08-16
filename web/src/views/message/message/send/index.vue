<script setup lang='ts'>
import { defineAsyncComponent, ref } from 'vue';
import { notification, PureCard } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import { Button } from 'ant-design-vue';
import { appContext, ReceiveObjectType } from '@xcan-angus/infra';
import { MessageReceiveType, SentType } from '@/enums/enums';

import { message } from '@/api';

/**
 * Async component imports for better performance
 * Lazy loading components to improve initial page load time
 */
const SendForm = defineAsyncComponent(() => import('./form.vue'));
const SendList = defineAsyncComponent(() => import('./list.vue'));

const { t } = useI18n();
const router = useRouter();

/**
 * Form reference for validation
 * Used to access form validation methods
 */
const formRef = ref();

// Recipient lists for different object types
const userList = ref<{ id: number; fullName: string; }[]>([]);
const deptList = ref<{ id: number; name: string; }[]>([]);
const groupList = ref<{ id: number; name: string; }[]>([]);

// Form data fields
const title = ref<string>('');
const content = ref<string>('');
const timingDate = ref<string>('');
const receiveType = ref<MessageReceiveType>(MessageReceiveType.SITE);
const sendType = ref<SentType>(SentType.SEND_NOW);
const receiveObjectType = ref<ReceiveObjectType>(ReceiveObjectType.USER);
const receiveTenantId = ref<string>('');
const tenantName = ref<string>('');

/**
 * Notification counter for form reset
 * Incremented after successful submission to trigger form reset
 */
const notify = ref(0);

/**
 * Loading state for form submission
 * Prevents multiple submissions while processing
 */
const loading = ref(false);

// Form validation state
const titleRule = ref(false);
const contentRule = ref(false);
const propsContentRuleMsg = ref(t('messages.placeholder.content'));
const dateRule = ref(false);

/**
 * Submit message form
 * Validates form data and sends message to selected recipients
 */
const submit = async () => {
  if (loading.value) {
    return;
  }

  // Validate form using form component's validate method
  if (!formRef.value.validate()) {
    return;
  }

  // Validate timing date for scheduled messages
  if (sendType.value === SentType.TIMING_SEND && !timingDate.value) {
    dateRule.value = true;
    return;
  }

  /**
   * Build message parameters object
   * Contains all necessary data for message creation
   */
  let params: {
    title: string;
    content: string;
    receiveType: MessageReceiveType;
    sendType: SentType;
    timingDate?: string;
    receiveTenantId?: number;
    receiveObjectType?: ReceiveObjectType,
    receiveObjects?: { id: number, name: string }[],
  } = {
    content: content.value,
    sendType: sendType.value,
    receiveObjectType: receiveObjectType.value,
    receiveType: receiveType.value,
    timingDate: timingDate.value || undefined,
    title: title.value
  };

  // Handle tenant-level recipients
  if ([ReceiveObjectType.TENANT, ReceiveObjectType.USER, ReceiveObjectType.GROUP, ReceiveObjectType.DEPT].includes(receiveObjectType.value)) {
    const tenantId = appContext.getTenant()?.id;
    const tenantName = appContext.getTenant()?.name;
    if (tenantId && tenantName) {
      params = { ...params, receiveTenantId: tenantId };
      params = { ...params, receiveObjects: [{ id: tenantId, name: tenantName }] };
    }
  }

  // Handle user recipients
  if (receiveObjectType.value === ReceiveObjectType.USER) {
    if (!userList.value.length) {
      notification.warning(t('messages.messages.selectReceiveUser'));
      return;
    }
    params = { ...params, receiveObjectType: receiveObjectType.value };
    params = { ...params, receiveObjects: userList.value.map(user => ({ id: user.id, name: user.fullName })) };
  }

  // Handle group recipients
  if (receiveObjectType.value === ReceiveObjectType.GROUP) {
    if (!groupList.value.length) {
      notification.warning(t('messages.messages.selectReceiveGroup'));
      return;
    }
    params = { ...params, receiveObjectType: receiveObjectType.value };
    params = { ...params, receiveObjects: groupList.value };
  }

  // Handle department recipients
  if (receiveObjectType.value === ReceiveObjectType.DEPT) {
    if (!deptList.value.length) {
      notification.warning(t('messages.messages.selectReceiveDept'));
      return;
    }
    params = { ...params, receiveObjectType: receiveObjectType.value };
    params = { ...params, receiveObjects: deptList.value };
  }

  // Submit message to API
  loading.value = true;

  try {
    const [error, res] = await message.sendMessage(params);
    if (error) {
      return;
    }

    // Success handling
    notify.value++;
    notification.success(t('submitSuccessfully'));
    await router.push(`/messages/message?id=${res.data.id}`);
  } finally {
    loading.value = false;
  }
};

/**
 * Handle form cancellation
 * Navigates back to message list without saving
 */
const handleCancel = () => {
  router.push('/messages/message');
};
</script>

<template>
  <PureCard class="p-3.5 flex-1 h-full">
    <!-- Message Form and Recipient Selection Layout -->
    <div class="flex space-x-3.5 pl-3.5" style="height: calc(100% - 28px);">
      <!-- Message Form Component -->
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

      <!-- Recipient Selection Component -->
      <SendList
        v-model:userList="userList"
        v-model:deptList="deptList"
        v-model:groupList="groupList"
        v-model:receiveObjectType="receiveObjectType"
        v-model:receiveTenantId="receiveTenantId"
        v-model:tenantName="tenantName" />
    </div>

    <!-- Form Action Buttons -->
    <div class="text-center">
      <Button
        :loading="loading"
        type="primary"
        size="small"
        class="px-3  mr-3"
        @click="submit">
        {{ t('common.actions.submit') }}
      </Button>

      <Button
        size="small"
        class="px-3"
        @click="handleCancel">
        {{ t('common.actions.cancel') }}
      </Button>
    </div>
  </PureCard>
</template>
