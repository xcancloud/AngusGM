<script setup lang="ts">
import { defineAsyncComponent, onMounted, reactive, ref, toRaw } from 'vue';
import { Button, Form, FormItem, Spin, Tooltip } from 'ant-design-vue';
import { useI18n } from 'vue-i18n';
import {
  AsyncComponent,
  ButtonAuth,
  Colon,
  Hints,
  Icon,
  IconRequired,
  Image,
  Input,
  modal,
  NoData,
  notification,
  PureCard
} from '@xcan/design';

import { sms } from '@/api';
import { Aisle } from './PropsType';

import placeholderLogo from '@/views/system/sms/channel/images/robot.png';

const SendMessages = defineAsyncComponent(() => import('@/views/system/sms/channel/components/send/index.vue'));

const { t } = useI18n();
const useForm = Form.useForm;
const loading = ref<boolean>(false);
const state: { aisles: Aisle[] } = reactive({ aisles: [] });
const aisle: Aisle = reactive(
  {
    accessKeyId: '',
    accessKeySecret: '',
    thirdChannelNo: '',
    endpoint: '',
    enabled: false,
    id: '',
    logo: '',
    name: '',
    loading: false,
    display: false, // 显示配置
    visible: false // 显示短信
  }
);
const rules = reactive({
  accessKeyId: [
    {
      required: true,
      message: t('errAccessKeyID'),
      trigger: 'change'
    }
  ],
  accessKeySecret: [
    {
      required: true,
      message: t('errAccessKeySecret'),
      trigger: 'change'
    }
  ],
  endpoint: [
    {
      required: true,
      message: t('errEndpoint'),
      trigger: 'change'
    }
  ]
});
const { resetFields, validate, validateInfos } = useForm(aisle, rules);

const init = () => {
  loadSmsConfig();
};

const loadSmsConfig = async (): Promise<void> => {
  loading.value = true;
  const [error, res] = await sms.getChannels();
  loading.value = false;
  if (error || !res.data) {
    return;
  }

  setState(res.data.list);
};

const setState = (data: Aisle[]) => {
  state.aisles = data.map(item => {
    return {
      ...item,
      accessKeyId: item.accessKeyId ? item.accessKeyId : '',
      accessKeySecret: item.accessKeySecret ? item.accessKeySecret : '',
      endpoint: item.endpoint ? item.endpoint : '',
      loading: false,
      display: false,
      visible: false
    };
  });
};

const getLogoBorder = (value: string): string => {
  if (value) {
    return '';
  }

  return 'border border-gray-border border-dashed';
};

const getLogo = (value: string): string => {
  if (value) {
    return value;
  }

  return placeholderLogo;
};

const getState = (value: boolean): string => {
  if (value) {
    return t('activated');
  } else {
    return t('disabled');
  }
};

const updateStateConfirm = (item: Aisle) => {
  modal.confirm({
    centered: true,
    title: item.enabled ? t('disabledPrompt.title') : t('enablePrompt.title'),
    content: item.enabled ? t('disabledPrompt.content') : t('enablePrompt.content'),
    async onOk () {
      await patchUpdateState(item);
    }
  });
};

const patchUpdateState = async (item: Aisle): Promise<void> => {
  const [error] = await sms.toggleChannelEnabled({ id: item.id, enabled: !item.enabled });
  if (error) {
    return;
  }

  notification.success(item.enabled ? t('disableSuccess') : t('enableSuccess'));
  loadSmsConfig();
};

const patchSave = async (item: Aisle): Promise<void> => {
  item.loading = true;
  const params = {
    accessKeyId: toRaw(aisle.accessKeyId)?.trim(),
    accessKeySecret: toRaw(aisle.accessKeySecret)?.trim(),
    endpoint: toRaw(aisle.endpoint)?.trim(),
    thirdChannelNo: toRaw(aisle.thirdChannelNo)?.trim(),
    id: item.id
  };
  const [error] = await sms.updateChannelConfig(params);
  item.loading = false;
  if (error) {
    return;
  }

  resetFields();
  item.display = false;
  notification.success(t('saveSuccess'));
  loadSmsConfig();
};

const saveConfirm = (item: Aisle) => {
  validate().then(() => {
    modal.confirm({
      centered: true,
      title: t('saveConfiguration'),
      content: t('confirmSave'),
      onOk () {
        patchSave(item);
      }
    });
  }).catch();
};

const openConfig = (val: Aisle) => {
  state.aisles.forEach(item => {
    item.display = false;
  });
  aisle.accessKeyId = val.accessKeyId;
  aisle.accessKeySecret = val.accessKeySecret;
  aisle.endpoint = val.endpoint;
  aisle.thirdChannelNo = val?.thirdChannelNo;
  val.visible = false;
  val.display = true;
};

const openSmsMessage = (item: Aisle) => {
  item.display = false;
  item.visible = true;
};

const closeSmsMessage = (value: boolean, aisle: Aisle) => {
  aisle.visible = value;
};

const closeConfig = (item: Aisle) => {
  resetFields();
  item.display = false;
};

const getPopupContainer = (triggerNode: HTMLElement) => {
  if (triggerNode) {
    return triggerNode.parentNode;
  }
  return document.body;
};

onMounted(() => {
  init();
});
</script>
<template>
  <Hints :text="t('smsChanneldescribe1')" />
  <template v-if="state.aisles.length">
    <PureCard
      v-for="item in state.aisles"
      :key="item.id"
      class="px-10 mt-2">
      <Spin
        tip="Loading..."
        size="large"
        :spinning="item.loading">
        <div class="h-40 py-7.5 flex items-center justify-between space-x-8">
          <div v-if="!item.display" class="flex items-center space-x-8">
            <div
              v-if="!item.logo"
              class="w-25 h-25"
              :class="getLogoBorder(item.logo)">
            </div>
            <Image
              v-else
              class="w-25"
              :src="getLogo(item.logo)" />
            <div class="flex space-x-6 flex-1 justify-between">
              <Tooltip
                :title="item.name"
                :getPopupContainer="getPopupContainer"
                :mouseLeaveDelay="0"
                placement="bottomLeft">
                <span class="whitespace-nowrap overflow-ellipsis overflow-hidden w-40 h-8 leading-8 text-4 font-normal">{{
                  item.name
                }}</span>
              </Tooltip>
              <span class="space-x-2 ml-8 whitespace-nowrap h-8 leading-8">
                <Icon :icon="item.enabled ? 'icon-right' : 'icon-jinyong'" :class="item.enabled ? 'text-success' : ''" />
                {{ getState(item.enabled) }}
              </span>
            </div>
          </div>
          <Form v-if="item.display" class="flex space-x-10 flex-1">
            <div class="space-y-1 w-1/4">
              <div class="text-theme-title">
                <IconRequired class="mr-1" />
                {{ t('smsInterfaceAddress') }}(endpoint)
                <Colon />
              </div>
              <FormItem class="h-13.5" v-bind="validateInfos.endpoint">
                <Input v-model:value="aisle.endpoint" />
              </FormItem>
            </div>
            <div class="space-y-1 w-1/4">
              <div class="text-theme-title">
                <IconRequired class="mr-1" />
                {{ t('accessKeyID') }}
                <Colon />
              </div>
              <FormItem class="h-13.5 flex-1" v-bind="validateInfos.accessKeyId">
                <Input v-model:value="aisle.accessKeyId" />
              </FormItem>
            </div>
            <div class="space-y-1 w-1/4">
              <div class="text-theme-title">
                <IconRequired class="mr-1" />
                {{ t('accessKeySecret') }}
                <Colon />
              </div>
              <FormItem class="h-13.5" v-bind="validateInfos.accessKeySecret">
                <Input v-model:value="aisle.accessKeySecret" />
              </FormItem>
            </div>
            <div class="space-y-1 w-1/4">
              <div class="text-theme-title">
                {{ t('thirdChannelNo') }}
                <Colon />
              </div>
              <FormItem class="h-13.5" :required="false">
                <Input v-model:value="aisle.thirdChannelNo" />
              </FormItem>
            </div>
          </Form>
          <AsyncComponent :visible="item.visible">
            <SendMessages
              v-if="item.visible"
              :aisle="item"
              @closeSmsMessage="closeSmsMessage" />
          </AsyncComponent>
          <div v-if="item.display" class="flex space-x-5 items-center">
            <Button type="primary" @click="saveConfirm(item)">
              {{ t('saveConfiguration') }}
            </Button>
            <Button @click="closeConfig(item)">{{ t('cancel') }}</Button>
          </div>
          <div v-if="!item.display && !item.visible" class="space-x-5">
            <ButtonAuth
              v-if="item.enabled"
              code="SMSChannelTest"
              size="middle"
              @click="openSmsMessage(item)" />
            <ButtonAuth
              code="SMSChannelConfiguration"
              size="middle"
              @click="openConfig(item)" />
            <ButtonAuth
              code="SMSChannelEnable"
              size="middle"
              :showTextIndex="item.enabled?1:0"
              @click="updateStateConfirm(item)" />
          </div>
        </div>
      </Spin>
    </PureCard>
  </template>
  <NoData v-else style="height: calc(100% - 50px);" />
</template>
