<script setup lang="ts">
import { defineAsyncComponent, onMounted, reactive, ref, toRaw, computed } from 'vue';
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
} from '@xcan-angus/vue-ui';

import { sms } from '@/api';
import { Aisle } from './types';

import placeholderLogo from '@/views/system/sms/channel/images/robot.png';

// Lazy load SMS sending component for better performance
const SendMessages = defineAsyncComponent(() => import('@/views/system/sms/channel/send/index.vue'));

const { t } = useI18n();
const useForm = Form.useForm;

// Reactive state management
const loading = ref<boolean>(false);
const state: { aisles: Aisle[] } = reactive({ aisles: [] });

// SMS channel configuration form data
const aisle: Aisle = reactive({
  accessKeyId: '',
  accessKeySecret: '',
  thirdChannelNo: '',
  endpoint: '',
  enabled: false,
  id: '',
  logo: '',
  name: '',
  loading: false,
  display: false, // Configuration display state
  visible: false // SMS sending dialog visibility
});

// Form validation rules
const rules = reactive({
  accessKeyId: [
    {
      required: true,
      message: t('sms.validation.accessKeyIdRequired'),
      trigger: 'change'
    }
  ],
  accessKeySecret: [
    {
      required: true,
      message: t('sms.validation.accessKeySecretRequired'),
      trigger: 'change'
    }
  ],
  endpoint: [
    {
      required: true,
      message: t('sms.validation.endpointRequired'),
      trigger: 'change'
    }
  ]
});

const { resetFields, validate, validateInfos } = useForm(aisle, rules);

// Computed properties for better performance
const hasChannels = computed(() => state.aisles.length > 0);

/**
 * Initialize component data
 */
const init = () => {
  loadSmsConfig();
};

/**
 * Load SMS channel configurations from API
 */
const loadSmsConfig = async (): Promise<void> => {
  loading.value = true;
  try {
    const [error, res] = await sms.getChannels();
    if (error || !res.data) {
      return;
    }
    setState(res.data.list);
  } finally {
    loading.value = false;
  }
};

/**
 * Set channel state with default values
 * @param data - Raw channel data from API
 */
const setState = (data: Aisle[]) => {
  state.aisles = data.map(item => ({
    ...item,
    accessKeyId: item.accessKeyId || '',
    accessKeySecret: item.accessKeySecret || '',
    endpoint: item.endpoint || '',
    loading: false,
    display: false,
    visible: false
  }));
};

/**
 * Get logo border styling based on logo availability
 * @param value - Logo URL or empty string
 * @returns CSS class for border styling
 */
const getLogoBorder = (value: string): string => {
  return value ? '' : 'border border-gray-border border-dashed';
};

/**
 * Get logo source, fallback to placeholder if not available
 * @param value - Logo URL
 * @returns Logo URL or placeholder image
 */
const getLogo = (value: string): string => {
  return value || placeholderLogo;
};

/**
 * Get channel status text
 * @param value - Enabled state
 * @returns Localized status text
 */
const getState = (value: boolean): string => {
  return value ? t('sms.status.enabled') : t('sms.status.disabled');
};

/**
 * Show confirmation dialog for enabling/disabling channel
 * @param item - Channel item to update
 */
const updateStateConfirm = (item: Aisle) => {
  const isEnabling = !item.enabled;
  modal.confirm({
    centered: true,
    title: isEnabling ? t('sms.messages.enablePromptTitle') : t('sms.messages.disabledPromptTitle'),
    content: isEnabling ? t('sms.messages.enablePromptContent') : t('sms.messages.disabledPromptContent'),
    async onOk () {
      await patchUpdateState(item);
    }
  });
};

/**
 * Update channel enabled state via API
 * @param item - Channel item to update
 */
const patchUpdateState = async (item: Aisle): Promise<void> => {
  try {
    const [error] = await sms.toggleChannelEnabled({
      id: item.id,
      enabled: !item.enabled
    });
    if (error) {
      return;
    }

    const message = item.enabled
      ? t('sms.messages.disableSuccess')
      : t('sms.messages.enableSuccess');
    notification.success(message);
    await loadSmsConfig();
  } catch (error) {
    console.error('Failed to update channel state:', error);
  }
};

/**
 * Save channel configuration via API
 * @param item - Channel item to save
 */
const patchSave = async (item: Aisle): Promise<void> => {
  item.loading = true;
  try {
    const params = {
      accessKeyId: toRaw(aisle.accessKeyId)?.trim(),
      accessKeySecret: toRaw(aisle.accessKeySecret)?.trim(),
      endpoint: toRaw(aisle.endpoint)?.trim(),
      thirdChannelNo: toRaw(aisle.thirdChannelNo)?.trim(),
      id: item.id
    };

    const [error] = await sms.updateChannelConfig(params);
    if (error) {
      return;
    }

    resetFields();
    item.display = false;
    notification.success(t('sms.messages.saveSuccess'));
    await loadSmsConfig();
  } finally {
    item.loading = false;
  }
};

/**
 * Show save confirmation dialog after form validation
 * @param item - Channel item to save
 */
const saveConfirm = (item: Aisle) => {
  validate().then(() => {
    modal.confirm({
      centered: true,
      title: t('sms.messages.saveConfiguration'),
      content: t('sms.messages.confirmSave'),
      onOk () {
        patchSave(item);
      }
    });
  }).catch(() => {
    // Validation failed, do nothing
  });
};

/**
 * Open configuration form for a specific channel
 * @param val - Channel item to configure
 */
const openConfig = (val: Aisle) => {
  // Close all other configuration forms
  state.aisles.forEach(item => {
    item.display = false;
  });

  // Populate form with current values
  aisle.accessKeyId = val.accessKeyId;
  aisle.accessKeySecret = val.accessKeySecret;
  aisle.endpoint = val.endpoint;
  aisle.thirdChannelNo = val?.thirdChannelNo;

  // Update UI state
  val.visible = false;
  val.display = true;
};

/**
 * Open SMS sending dialog for a channel
 * @param item - Channel item to send SMS from
 */
const openSmsMessage = (item: Aisle) => {
  item.display = false;
  item.visible = true;
};

/**
 * Close SMS sending dialog
 * @param value - Dialog visibility state
 * @param aisle - Channel item
 */
const closeSmsMessage = (value: boolean, aisle: Aisle) => {
  aisle.visible = value;
};

/**
 * Close configuration form and reset form data
 * @param item - Channel item
 */
const closeConfig = (item: Aisle) => {
  resetFields();
  item.display = false;
};

/**
 * Get popup container for tooltips
 * @param triggerNode - DOM element that triggered the tooltip
 * @returns Parent node or document body as fallback
 */
const getPopupContainer = (triggerNode: HTMLElement) => {
  return (triggerNode?.parentNode as HTMLElement) || document.body;
};

// Lifecycle hooks
onMounted(() => {
  init();
});
</script>

<template>
  <!-- SMS channel description -->
  <Hints :text="t('sms.messages.channelDescribe1')" />

  <!-- Channel list -->
  <template v-if="hasChannels">
    <PureCard
      v-for="item in state.aisles"
      :key="item.id"
      class="px-10 mt-2">
      <Spin
        tip="Loading..."
        size="large"
        :spinning="item.loading">
        <div class="h-40 py-7.5 flex items-center justify-between space-x-8">
          <!-- Channel display mode -->
          <div v-if="!item.display" class="flex items-center space-x-8">
            <!-- Channel logo -->
            <div
              v-if="!item.logo"
              class="w-25 h-25"
              :class="getLogoBorder(item.logo)">
            </div>
            <Image
              v-else
              class="w-25"
              :src="getLogo(item.logo)" />

            <!-- Channel info and status -->
            <div class="flex space-x-6 flex-1 justify-between">
              <Tooltip
                :title="item.name"
                :getPopupContainer="getPopupContainer"
                :mouseLeaveDelay="0"
                placement="bottomLeft">
                <span class="whitespace-nowrap overflow-ellipsis overflow-hidden w-40 h-8 leading-8 text-4 font-normal">
                  {{ item.name }}
                </span>
              </Tooltip>
              <span class="space-x-2 ml-8 whitespace-nowrap h-8 leading-8">
                <Icon
                  :icon="item.enabled ? 'icon-right' : 'icon-jinyong'"
                  :class="item.enabled ? 'text-success' : ''" />
                {{ getState(item.enabled) }}
              </span>
            </div>
          </div>

          <!-- Configuration form -->
          <Form v-if="item.display" class="flex space-x-10 flex-1">
            <!-- Endpoint configuration -->
            <div class="space-y-1 w-1/4">
              <div class="text-theme-title">
                <IconRequired class="mr-1" />
                {{ t('sms.messages.smsInterfaceAddress') }}(endpoint)
                <Colon />
              </div>
              <FormItem class="h-13.5" v-bind="validateInfos.endpoint">
                <Input v-model:value="aisle.endpoint" />
              </FormItem>
            </div>

            <!-- Access Key ID configuration -->
            <div class="space-y-1 w-1/4">
              <div class="text-theme-title">
                <IconRequired class="mr-1" />
                {{ t('sms.messages.accessKeyID') }}
                <Colon />
              </div>
              <FormItem class="h-13.5 flex-1" v-bind="validateInfos.accessKeyId">
                <Input v-model:value="aisle.accessKeyId" />
              </FormItem>
            </div>

            <!-- Access Key Secret configuration -->
            <div class="space-y-1 w-1/4">
              <div class="text-theme-title">
                <IconRequired class="mr-1" />
                {{ t('sms.messages.accessKeySecret') }}
                <Colon />
              </div>
              <FormItem class="h-13.5" v-bind="validateInfos.accessKeySecret">
                <Input v-model:value="aisle.accessKeySecret" />
              </FormItem>
            </div>

            <!-- Third party channel number (optional) -->
            <div class="space-y-1 w-1/4">
              <div class="text-theme-title">
                {{ t('sms.messages.thirdChannelNo') }}
                <Colon />
              </div>
              <FormItem class="h-13.5" :required="false">
                <Input v-model:value="aisle.thirdChannelNo" />
              </FormItem>
            </div>
          </Form>

          <!-- SMS sending dialog -->
          <AsyncComponent :visible="item.visible">
            <SendMessages
              v-if="item.visible"
              :aisle="item"
              @closeSmsMessage="closeSmsMessage" />
          </AsyncComponent>

          <!-- Configuration form actions -->
          <div v-if="item.display" class="flex space-x-5 items-center">
            <Button type="primary" @click="saveConfirm(item)">
              {{ t('sms.messages.saveConfiguration') }}
            </Button>
            <Button @click="closeConfig(item)">{{ t('common.actions.cancel') }}</Button>
          </div>

          <!-- Channel actions -->
          <div v-if="!item.display && !item.visible" class="space-x-5">
            <!-- Test SMS button (only for enabled channels) -->
            <ButtonAuth
              v-if="item.enabled"
              code="SMSChannelTest"
              size="small"
              @click="openSmsMessage(item)" />

            <!-- Configuration button -->
            <ButtonAuth
              code="SMSChannelConfiguration"
              size="small"
              @click="openConfig(item)" />

            <!-- Enable/Disable button -->
            <ButtonAuth
              code="SMSChannelEnable"
              size="small"
              :showTextIndex="item.enabled ? 1 : 0"
              @click="updateStateConfirm(item)" />
          </div>
        </div>
      </Spin>
    </PureCard>
  </template>

  <!-- Empty state when no channels exist -->
  <NoData v-else style="height: calc(100% - 50px);" />
</template>
