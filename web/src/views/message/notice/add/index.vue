<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Button, Form, FormItem, Radio, RadioGroup, Textarea } from 'ant-design-vue';
import { DatePicker, Hints, notification, PureCard, Select, SelectEnum } from '@xcan-angus/vue-ui';
import { appContext, enumUtils, GM } from '@xcan-angus/infra';
import { NoticeScope, SentType } from '@/enums/enums';
import { useRouter } from 'vue-router';

import type { NoticeFormType } from '../types';
import { resetForm, handleScopeChange, handleSendTypeChange, handleAppChange, handleDateChange, handleExpirationDate } from '../utils';
import { notice } from '@/api';

const { t } = useI18n();
const router = useRouter();

// Form layout configuration
const labelCol = { span: 8 };
const wrapperCol = { span: 16 };

// Form reference for validation
const formRef = ref();

// Form data model with default values
const form: NoticeFormType = reactive({
  content: '', // Notice content text
  scope: NoticeScope.GLOBAL, // Notice scope (global or app-specific)
  appCode: undefined, // Application code for app-scoped notices
  appName: undefined, // Application name for app-scoped notices
  editionType: undefined, // Application edition type
  appId: undefined, // Application ID for app-scoped notices
  sendType: SentType.SEND_NOW, // Send type (immediate or scheduled)
  sendTimingDate: undefined, // Scheduled send date/time
  expirationDate: undefined // Notice expiration date
});

/**
 * Cancel form and return to notice list
 * Resets form data and navigates back
 */
const cancel = () => {
  resetForm(form, formRef);
  router.push('/messages/notification');
};

/**
 * Submit form and create notice
 * Validates form data, submits to API, and handles response
 */
const submitForm = () => {
  formRef.value
    .validate()
    .then(async () => {
      const params: NoticeFormType = {
        ...form
      };
      const [error] = await notice.addNotice(params);
      if (error) {
        return;
      }
      notification.success(t('common.messages.submitSuccess'));
      resetForm(form, formRef);
      await router.push('/messages/notification');
    });
};

// Enum lists for form options
const enumsList: {
  noticeScopeList: Array<any> // Available notice scopes
  SentTypeList: Array<any> // Available send types
} = reactive({
  noticeScopeList: [],
  SentTypeList: []
});

// Initialize enum data on component mount
onMounted(() => {
  enumsList.SentTypeList = enumUtils.enumToMessages(SentType);
});
</script>
<template>
  <PureCard class="min-h-full py-10">
    <Form
      ref="formRef"
      :model="form"
      :colon="false"
      :rules="formRules"
      :labelCol="labelCol"
      :wrapperCol="wrapperCol"
      size="small">
      <!-- Notice content input -->
      <FormItem
        colon
        :label="t('notification.columns.content')"
        name="content">
        <Textarea
          v-model:value="form.content"
          :placeholder="t('notification.placeholder.inputContent')"
          :rows="4"
          :maxlength="200"
          size="small" />
      </FormItem>

      <!-- Notice scope selection -->
      <FormItem
        colon
        :label="t('notification.columns.scope')"
        name="scope">
        <SelectEnum
          v-model:value="form.scope"
          :placeholder="t('notification.placeholder.scope')"
          internal
          size="small"
          :enumKey="NoticeScope"
          :lazy="false"
          @change="(item) => handleScopeChange(item, form)" />
      </FormItem>

      <!-- Global scope hint -->
      <div class="text-3 pl-1/3 -mt-4">
        <Hints :text="t('notification.globalTip')" class="w-150 mb-1" />
      </div>

      <!-- App selection (only shown for app-scoped notices) -->
      <template v-if="form.scope === NoticeScope.APP">
        <FormItem
          colon
          :label="t('selectApply')"
          name="appId">
          <Select
            v-model:value="form.appId"
            :fieldNames="{label:'appName',value:'appId'}"
            :placeholder="t('form-placeholder')"
            :action="`${GM}/appopen/list?tenantId=${appContext.getUser()?.tenantId}`"
            :lazy="false"
            defaultActiveFirstOption
            size="small"
            @change="(value, options) => handleAppChange(value, options, form)">
            <template #option="option">
              <span>{{ option.appName }} {{ option.editionType.message }}</span>
            </template>
          </Select>
        </FormItem>
      </template>

      <!-- Expiration date picker -->
      <FormItem
        :label="t('notification.columns.expiredDate') + ':'"
        name="expirationDate">
        <DatePicker
          v-model:value="form.expirationDate"
          class="w-full"
          size="small"
          showTime
          @change="(value) => handleExpirationDate(value, form)" />
      </FormItem>

      <!-- Send type selection -->
      <FormItem
        colon
        :label="t('notification.columns.sendType')"
        name="sendType">
        <RadioGroup v-model:value="form.sendType" size="small">
          <Radio
            v-for="item in enumsList.SentTypeList"
            :key="item.value + item.message"
            :value="item.value"
            @change="(item) => handleSendTypeChange(item.value, form)">
            {{ item.message }}
          </Radio>
        </RadioGroup>
      </FormItem>

      <!-- Scheduled send date (only shown for scheduled sends) -->
      <FormItem
        v-if="form.sendType === SentType.TIMING_SEND"
        :label="t('sendDate')"
        class="sendTimingDate"
        :name="(form.sendType === SentType.TIMING_SEND ? 'sendTimingDate' : '')">
        <DatePicker
          v-model:value="form.sendTimingDate"
          :placeholder="t('selectTime')"
          class="w-full"
          size="small"
          showTime
          @change="(value) => handleDateChange(value, form)" />
      </FormItem>

      <!-- Form action buttons -->
      <FormItem label=" " class="text-center">
        <Button
          size="small"
          type="primary"
          @click="submitForm">
          {{ t('common.actions.ok') }}
        </Button>
        <Button
          size="small"
          class="ml-5"
          @click="cancel">
          {{ t('common.actions.cancel') }}
        </Button>
      </FormItem>
    </Form>
  </PureCard>
</template>
<style scoped>
/* Custom form control styling for better layout */
.ant-form-horizontal :deep(.ant-form-item-control) {
  flex: 1 1 50%;
  max-width: 600px;
}
</style>
