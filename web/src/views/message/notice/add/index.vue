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

const labelCol = { span: 8 };
const wrapperCol = { span: 16 };

const formRef = ref();

const form: NoticeFormType = reactive({
  content: '',
  scope: NoticeScope.GLOBAL,
  appCode: undefined,
  appName: undefined,
  editionType: undefined,
  appId: undefined,
  sendType: SentType.SEND_NOW,
  sendTimingDate: undefined,
  expirationDate: undefined
});

const cancel = () => {
  resetForm(form, formRef);
  router.push('/messages/notification');
};

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

const enumsList: {
  noticeScopeList: Array<any>
  SentTypeList: Array<any>
} = reactive({
  noticeScopeList: [],
  SentTypeList: []
});

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
      <div class="text-3 pl-1/3 -mt-4">
        <Hints :text="t('notification.globalTip')" class="w-150 mb-1" />
      </div>
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
.ant-form-horizontal :deep(.ant-form-item-control) {
  flex: 1 1 50%;
  max-width: 600px;
}
</style>
