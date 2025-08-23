<script setup lang="ts">
import { reactive, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { ReceiveChannelType, enumUtils } from '@xcan-angus/infra';
import { CheckboxGroup, Form, FormItem, Popover, Select, Tag } from 'ant-design-vue';
import { Modal } from '@xcan-angus/vue-ui';

import { event } from '@/api';
import { ReceiveConfigProps, ReceiveConfigState, ChannelOption } from './types';
import {
  createInitialChannelValues, getChannelOptionsByType, getPlaceholderForChannelType,
  collectSelectedChannelIds, filterChannelsByType
} from './utils';

// Component props with proper typing
const props = withDefaults(defineProps<ReceiveConfigProps>(), {
  visible: false,
  id: ''
});

// Component emits
// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void;
}>();

const { t } = useI18n();

// Component state management
const state = reactive<ReceiveConfigState>({
  selectedType: [],
  eventTypes: [],
  channelValues: createInitialChannelValues(),
  webOptions: [],
  emailOptions: [],
  dingtalkOptions: [],
  wechatOptions: []
});

/**
 * Initialize component data
 * Loads current channels and event types
 */
const init = async (): Promise<void> => {
  await loadCurrentChannels();
  await loadEventTypes();
};

/**
 * Load event types from enum
 * Maps enum values to component options
 */
const loadEventTypes = async (): Promise<void> => {
  try {
    const eventTypes = enumUtils.enumToMessages(ReceiveChannelType)?.map(m => {
      // Load config options for selected types
      if (state.selectedType.includes(m.value)) {
        loadConfigOptions(m.value);
      }
      return {
        ...m,
        label: m.message
      };
    });
    state.eventTypes = eventTypes || [];
  } catch (error) {
    console.error('Failed to load event types:', error);
  }
};

/**
 * Load current channels for the template
 * Fetches existing channel configuration
 */
const loadCurrentChannels = async (): Promise<void> => {
  try {
    const [error, res] = await event.getCurrentTemplates(props.id);
    if (error) {
      return;
    }

    // Set selected channel types
    state.selectedType = (res.data.allowedChannelTypes || []).map(type => type.value);

    // Filter channels by type and set values
    if (res.data.receiveSetting?.channels) {
      state.channelValues.WEBHOOK = filterChannelsByType(
        res.data.receiveSetting.channels,
        'WEBHOOK'
      );
      state.channelValues.EMAIL = filterChannelsByType(
        res.data.receiveSetting.channels,
        'EMAIL'
      );
      state.channelValues.DINGTALK = filterChannelsByType(
        res.data.receiveSetting.channels,
        'DINGTALK'
      );
      state.channelValues.WECHAT = filterChannelsByType(
        res.data.receiveSetting.channels,
        'WECHAT'
      );
    }
  } catch (error) {
    console.error('Failed to load current channels:', error);
  }
};

/**
 * Load receive setting details for a specific channel type
 * Fetches available channels for the given type
 */
const loadReceiveSettingDetail = async (key: string): Promise<ChannelOption[]> => {
  try {
    const [error, res] = await event.getTypeChannel(key);
    if (error || !res.data) {
      return [];
    }
    return res.data.map(i => ({ ...i }));
  } catch (error) {
    console.error(`Failed to load receive setting for ${key}:`, error);
    return [];
  }
};

/**
 * Load configuration options for a specific channel type
 * Updates the corresponding options array
 */
const loadConfigOptions = async (key: string): Promise<void> => {
  try {
    const options = await loadReceiveSettingDetail(key);

    switch (key) {
      case 'WEBHOOK':
        state.webOptions = options;
        break;
      case 'EMAIL':
        state.emailOptions = options;
        break;
      case 'DINGTALK':
        state.dingtalkOptions = options;
        break;
      case 'WECHAT':
        state.wechatOptions = options;
        break;
    }
  } catch (error) {
    console.error(`Failed to load config options for ${key}:`, error);
  }
};

/**
 * Get channel options for a specific type
 * Returns the appropriate options array based on type
 */
const getOptions = (key: string): ChannelOption[] => {
  return getChannelOptionsByType(
    key,
    state.webOptions,
    state.emailOptions,
    state.dingtalkOptions,
    state.wechatOptions
  );
};

/**
 * Get placeholder text for a specific channel type
 * Returns localized placeholder text
 */
const getPlaceholder = (key: string): string => {
  return getPlaceholderForChannelType(key, t);
};

/**
 * Handle modal OK button click
 * Saves channel configuration and closes modal
 */
const handleOk = async (): Promise<void> => {
  try {
    const channelIds = collectSelectedChannelIds(state.selectedType, state.channelValues);
    const [error] = await event.replaceTemplateChannel({
      id: props.id,
      channelIds
    });
    if (error) {
      return;
    }
    emit('update:visible', false);
  } catch (error) {
    console.error('Failed to save template channel:', error);
  }
};

/**
 * Handle modal cancel button click
 * Closes the modal without saving
 */
const handleCancel = (): void => {
  emit('update:visible', false);
};

// Watch for visible changes to initialize data
watch(() => props.visible, (newValue) => {
  if (newValue) {
    init();
  }
}, {
  immediate: true
});
</script>

<template>
  <Modal
    :visible="visible"
    :title="t('event.config.messages.configReceiveChannel')"
    :centered="true"
    width="800px"
    @ok="handleOk"
    @cancel="handleCancel">
    <Form size="small" :labelCol="{ span: 6 }">
      <!-- Receive method selection -->
      <FormItem
        :label="t('event.config.messages.selectReceiveMethod')"
        name="receiveSetting">
        <CheckboxGroup
          :options="state.eventTypes"
          :value="state.selectedType"
          disabled>
        </CheckboxGroup>
      </FormItem>

      <!-- Channel configuration for each type -->
      <div class="border-t border-theme-divider pt-6 -mt-2">
        <FormItem
          v-for="type in state.eventTypes"
          :key="type.value"
          :label="type.message"
          :name="type.value">
          <Select
            v-model:value="state.channelValues[type.value]"
            :fieldNames="{ value: 'id', label: 'name' }"
            :options="getOptions(type.value)"
            :placeholder="getPlaceholder(type.value)"
            :disabled="!state.selectedType.includes(type.value)"
            mode="multiple">
            <!-- Custom tag render with address popover -->
            <template #tagRender="{ option, label, closable, onClose }">
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
