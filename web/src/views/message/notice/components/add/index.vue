<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Button, Form, FormItem, RadioGroup, Radio, Textarea } from 'ant-design-vue';
import { notification, DatePicker, Select, SelectEnum, PureCard, Hints } from '@xcan-angus/vue-ui';
import { enumUtils, appContext, GM } from '@xcan-angus/infra';
import { useRouter } from 'vue-router';

import type { FormDataType } from '../../interface';
import { notice } from '@/api';

const { t } = useI18n();
const router = useRouter();

const form: FormDataType = reactive({
  content: '',
  scope: 'GLOBAL',
  appCode: undefined,
  appName: undefined,
  editionType: undefined,
  appId: undefined,
  sendType: 'SEND_NOW',
  sendTimingDate: undefined,
  expirationDate: undefined
});

const validateSendTime = () => {
  if (!form.sendTimingDate) {
    return Promise.reject(new Error(t('sendTimeRequired')));
  }
  if (new Date(form.sendTimingDate as string) < new Date()) {
    return Promise.reject(new Error(t('sendTimeMoreThanNow')));
  }
  if (new Date(form.sendTimingDate as string) > new Date(form.expirationDate || '')) {
    return Promise.reject(new Error(t('sendTimeMoreExpriedDate')));
  }
  return Promise.resolve();
};

const validateExpirationDate = () => {
  if (!form.expirationDate) {
    return Promise.reject(new Error(t('expriedDateRequired')));
  }
  if (new Date(form.expirationDate as string) < new Date()) {
    return Promise.reject(new Error(t('expriedDateMoreThanNow')));
  }
  return Promise.resolve();
};

const formRules: any = reactive({
  content: [
    { required: true, message: t('noticeContentRequired'), trigger: 'change' }
  ],
  sendTimingDate: [
    { required: true, validator: validateSendTime, type: 'string' }
  ],
  expirationDate: [
    { required: true, validator: validateExpirationDate, type: 'string' }
  ],
  appId: [
    { required: true, message: t('applyRequired'), type: 'string' }
  ],
  scope: [
    { required: true }
  ]
});

const labelCol = {
  span: 8
};

const wrapperCol = {
  span: 16
};

const formRef = ref();

const handleDateChange = (value: string): void => {
  form.sendTimingDate = value;
};

const handleExpirationDate = (value: string): void => {
  form.expirationDate = value;
};

// 公告时间改变
const handleScopeChange = (item: string) => {
  if (item === 'GLOBAL') {
    form.appCode = undefined;
    form.appName = undefined;
    form.appId = undefined;
    form.editionType = undefined;
  }
};

// 发送方式改变
const handleSendTypeChange = (item: string) => {
  if (item === 'SEND_NOW') {
    form.sendTimingDate = undefined;
  }
};

const handleChange = (_value, options) => {
  form.appCode = options.appCode;
  form.appName = options.appName;
  form.appId = options.appId;
  form.editionType = options.editionType.value;
};

const cancel = () => {
  resetForm();
  router.push('/messages/notice');
};

// 提交功能
const submitForm = () => {
  formRef.value
    .validate()
    .then(async () => {
      const params: FormDataType = {
        ...form
      };
      const [error] = await notice.addNotice(params);
      if (error) {
        return;
      }
      notification.success(t('successSubmit'));
      resetForm();
      router.push('/messages/notice');
    });
};

// 取消功能
const resetForm = () => {
  formRef.value.resetFields();
  form.content = '';
  form.scope = 'GLOBAL';
  form.sendType = 'SEND_NOW';
  form.sendTimingDate = undefined;
  form.expirationDate = undefined;
  form.appCode = undefined;
  form.appName = undefined;
};

// 字典常量
const enumsList: {
  noticeScopeList: Array<any>
  SentTypeList: Array<any>
} = reactive({
  noticeScopeList: [], // 公告范围
  SentTypeList: [] // 发送方式
});

// 获取字典
const getDirectory = async () => {
  enumsList.SentTypeList = enumUtils.enumToMessages('SentType');
};

onMounted(() => {
  getDirectory();
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
        :label="t('noticeDescription')"
        name="content">
        <Textarea
          v-model:value="form.content"
          :placeholder="t('noticeContentPlace')"
          :rows="4"
          :maxlength="200"
          size="small" />
      </FormItem>
      <FormItem
        colon
        :label="t('noticeScope')"
        name="scope">
        <SelectEnum
          v-model:value="form.scope"
          :placeholder="t('noticeScopePlaceholder')"
          internal
          size="small"
          enumKey="NoticeScope"
          :lazy="false"
          @change="handleScopeChange" />
      </FormItem>
      <div class="text-3 pl-1/3 -mt-4">
        <Hints :text="t('globalNoticeTip')" class="w-150 mb-1" />
      </div>
      <template v-if="form.scope === 'APP'">
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
            @change="handleChange">
            <template #option="option">
              <span>{{ option.appName }} {{ option.editionType.message }}</span>
            </template>
          </Select>
        </FormItem>
      </template>
      <FormItem :label="t('expirationDate') + ':'" name="expirationDate">
        <DatePicker
          v-model:value="form.expirationDate"
          class="w-full"
          size="small"
          showTime
          @change="handleExpirationDate" />
      </FormItem>
      <FormItem
        colon
        :label="t('sendType')"
        name="sendType">
        <RadioGroup v-model:value="form.sendType" size="small">
          <Radio
            v-for="item in enumsList.SentTypeList"
            :key="item.value + item.message"
            :value="item.value"
            @change="handleSendTypeChange(item.value)">
            {{ item.message }}
          </Radio>
        </RadioGroup>
      </FormItem>
      <FormItem
        v-if="form.sendType === 'TIMING_SEND'"
        :label="t('sendDate')"
        class="sendTimingDate"
        :name="(form.sendType === 'TIMING_SEND' ? 'sendTimingDate' : '')">
        <DatePicker
          v-model:value="form.sendTimingDate"
          :placeholder="t('selectTime')"
          class="w-full"
          size="small"
          showTime
          @change="handleDateChange" />
      </FormItem>
      <FormItem label=" " class="text-center">
        <Button
          size="small"
          type="primary"
          @click="submitForm">
          {{ t('sure') }}
        </Button>
        <Button
          size="small"
          class="ml-5"
          @click="cancel">
          {{ t('cancel') }}
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
