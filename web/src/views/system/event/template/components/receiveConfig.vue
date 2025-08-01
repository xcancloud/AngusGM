<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { enumLoader } from '@xcan-angus/infra';
import { CheckboxGroup, Form, FormItem, Popover, Select, Tag } from 'ant-design-vue';
import { Modal } from '@xcan-angus/vue-ui';

import { event } from '@/api';

interface Options {
  address: string,
  id: string,
  name: string,
  pkey: { value: string, message: string },
  secret: string
}

interface Props {
  visible: boolean;
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  id: ''
});

const emit = defineEmits<{(e: 'update:visible', value: boolean): void }>();

const { t } = useI18n();

const eventTypes = ref<{ value: string, message: string, models: string[] | undefined }[]>([]);
const selectetType = ref<string[]>([]);
const init = async () => {
  await loadCurrentChannels();
  loadEventTypes();
};

const loadEventTypes = async () => {
  const [error, data] = await enumLoader.load('ReceiveChannelType');
  if (error) {
    return;
  }
  eventTypes.value = data?.map(m => {
    selectetType.value.includes(m.value) && loadConfigOptions(m.value);
    return {
      ...m,
      label: m.message
    };
  });
};

const channelValus = reactive({
  WEBHOOK: [],
  EMAIL: [],
  DINGTALK: [],
  WECHAT: []
});

const loadCurrentChannels = async () => {
  const [error, res] = await event.getCurrentTemplates(props.id);
  if (error) {
    return;
  }
  selectetType.value = (res.data.allowedChannelTypes || []).map(type => type.value);
  channelValus.WEBHOOK = res.data.receiveSetting?.channels?.filter(channel => channel.channelType.value === 'WEBHOOK').map(channel => channel.id) || [];
  channelValus.EMAIL = res.data.receiveSetting?.channels?.filter(channel => channel.channelType.value === 'EMAIL').map(channel => channel.id) || [];
  channelValus.DINGTALK = res.data.receiveSetting?.channels?.filter(channel => channel.channelType.value === 'DINGTALK').map(channel => channel.id) || [];
  channelValus.WECHAT = res.data.receiveSetting?.channels?.filter(channel => channel.channelType.value === 'WECHAT').map(channel => channel.id) || [];
};

const webOptions = ref<Options[]>([]);
const emailOptions = ref<Options[]>([]);
const dingtalkOptions = ref<Options[]>([]);
const wechatOptions = ref<Options[]>([]);

const loadReceiveSettingDetail = async (key: string) => {
  const [error, res] = await event.getTypeChannel(key);
  if (error || !res.data) {
    return [];
  }
  return res.data.map(i => ({ ...i }));
};

const loadConfigOptions = async (key: string) => {
  if (key === 'WEBHOOK') {
    loadReceiveSettingDetail(key).then(res => {
      webOptions.value = res;
    });
  }
  if (key === 'EMAIL') {
    loadReceiveSettingDetail(key).then(res => {
      emailOptions.value = res;
    });
  }
  if (key === 'DINGTALK') {
    loadReceiveSettingDetail(key).then(res => {
      dingtalkOptions.value = res;
    });
  }
  if (key === 'WECHAT') {
    loadReceiveSettingDetail(key).then(res => {
      wechatOptions.value = res;
    });
  }
};

const getOptions = (key: string) => {
  if (key === 'WEBHOOK') {
    return webOptions.value;
  }
  if (key === 'EMAIL') {
    return emailOptions.value;
  }
  if (key === 'DINGTALK') {
    return dingtalkOptions.value;
  }
  if (key === 'WECHAT') {
    return wechatOptions.value;
  }
};
const getPlaceholder = (key: string) => {
  if (key === 'WEBHOOK') {
    return t('form-2');
  }
  if (key === 'EMAIL') {
    return t('form-3');
  }
  if (key === 'DINGTALK') {
    return t('form-4');
  }
  if (key === 'WECHAT') {
    return t('form-5');
  }
};

const handleOk = async () => {
  let ids: string[] = [];
  for (const eventType of selectetType.value) {
    ids = ids.concat(channelValus[eventType]);
  }
  const [error] = await event.replaceTemplateChannel({ id: props.id, channelIds: ids });
  if (error) {
    return;
  }

  emit('update:visible', false);
};
const handleCancel = () => {
  emit('update:visible', false);
};
watch(() => props.visible, newValue => {
  if (newValue) {
    init();
  }
}, { immediate: true });
</script>

<template>
  <Modal
    :visible="visible"
    :title="t('renew.r2')"
    :centered="true"
    width="800px"
    @ok="handleOk"
    @cancel="handleCancel">
    <Form size="small" :labelCol="{span: 6}">
      <FormItem
        :label="t('renew.r5')"
        name="receiveSetting">
        <CheckboxGroup
          :options="eventTypes"
          :value="selectetType"
          disabled>
        </CheckboxGroup>
      </FormItem>
      <div class="border-t border-theme-divider pt-6 -mt-2">
        <FormItem
          v-for="type in eventTypes"
          :key="type.value"
          :label="type.message"
          :name="type.value">
          <Select
            v-model:value="channelValus[type.value]"
            :fieldNames="{value: 'id', label: 'name'}"
            :options="getOptions(type.value)"
            :placeholder="getPlaceholder(type.value)"
            :disabled="!selectetType.includes(type.value)"
            mode="multiple">
            <template #tagRender="{option, label, closable, onClose}">
              <Popover>
                <template #content>
                  <div class="max-w-60 text-3 leading-3 leading-4">{{ option.address }}</div>
                </template>
                <Tag :closable="closable" @close="onClose">{{ label[0] }}</Tag>
              </Popover>
            </template>
          </Select>
        </FormItem>
      </div>
    </Form>
  </Modal>
</template>
