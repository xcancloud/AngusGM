<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { Popover, Tag } from 'ant-design-vue';
import { Grid, Modal, NoData } from '@xcan-angus/vue-ui';

import { event } from '@/api';
import { ReceiveConfigProps, ReceiveConfigState } from './types';
import { processChannelData, isChannelDataEmpty } from './utils';

// Component props with proper typing
const props = withDefaults(defineProps<ReceiveConfigProps>(), {
  visible: false,
  eventCode: '',
  eKey: ''
});

// Component emits
// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void;
}>();

// Component state management
const state = reactive<ReceiveConfigState>({
  dataSource: {},
  columns: [[]]
});

// Loading state
const loading = ref(false);

/**
 * Initialize component data
 * Fetches event channel configuration from API
 */
const init = async (): Promise<void> => {
  try {
    loading.value = true;
    const [error, res] = await event.getEventChannel(props.eventCode, props.eKey);

    if (error || !res.data || res.data.length === 0) {
      return;
    }

    const { dataSource, columns } = processChannelData(res.data);
    state.dataSource = dataSource;
    state.columns = columns;
  } catch (error) {
    console.error('Failed to load event channel:', error);
  } finally {
    loading.value = false;
  }
};

/**
 * Handle modal OK button click
 * Closes the modal
 */
const handleOk = async (): Promise<void> => {
  emit('update:visible', false);
};

/**
 * Handle modal cancel button click
 * Closes the modal
 */
const handleCancel = (): void => {
  emit('update:visible', false);
};

const { t } = useI18n();

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
    :title="t('event.records.messages.viewPushSettings')"
    :centered="true"
    width="600px"
    @ok="handleOk"
    @cancel="handleCancel">
    <!-- Channel configuration grid -->
    <Grid
      :columns="state.columns"
      :dataSource="state.dataSource"
      class="max-h-100 overflow-auto">
      <!-- DingTalk channel template -->
      <template #DINGTALK="{ text }">
        <Popover
          v-for="channel in text"
          :key="channel.id">
          <Tag class="mb-1">
            {{ channel.name }}
          </Tag>
          <template #content>
            <div class="max-w-100 break-words">{{ channel.address }}</div>
          </template>
        </Popover>
      </template>

      <!-- Webhook channel template -->
      <template #WEBHOOK="{ text }">
        <Popover
          v-for="channel in text"
          :key="channel.id">
          <Tag class="mb-1">
            {{ channel.name }}
          </Tag>
          <template #content>
            <div class="max-w-100 break-words">{{ channel.address }}</div>
          </template>
        </Popover>
      </template>

      <!-- Email channel template -->
      <template #EMAIL="{ text }">
        <Popover
          v-for="channel in text"
          :key="channel.id">
          <Tag class="mb-1">
            {{ channel.name }}
          </Tag>
          <template #content>
            <div class="max-w-100 break-words">{{ channel.address }}</div>
          </template>
        </Popover>
      </template>

      <!-- WeChat channel template -->
      <template #WECHAT="{ text }">
        <Popover
          v-for="channel in text"
          :key="channel.id">
          <Tag class="mb-1">
            {{ channel.name }}
          </Tag>
          <template #content>
            <div class="max-w-100 break-words">{{ channel.address }}</div>
          </template>
        </Popover>
      </template>
    </Grid>

    <!-- No data message when no channels available -->
    <NoData v-if="isChannelDataEmpty(state.columns)" />
  </Modal>
</template>
