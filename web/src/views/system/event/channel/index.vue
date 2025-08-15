<script setup lang="ts">
import { defineAsyncComponent, onMounted, reactive } from 'vue';
import { ReceiveChannelType, enumUtils } from '@xcan-angus/infra';
import { AsyncComponent, Card } from '@xcan-angus/vue-ui';

import { ChannelState } from './types';
import { convertEnumToTabs } from './utils';

// Async component imports for different channel types
const ReceivingConfigurationHttps = defineAsyncComponent(() => import('./http.vue'));
const ReceivingConfigurationEmail = defineAsyncComponent(() => import('./email.vue'));
const ReceivingConfigurationNailingRobot = defineAsyncComponent(() => import('./dingRobot.vue'));
const ReceivingConfigurationEnterpriseRobot = defineAsyncComponent(() => import('./wechatRobot.vue'));

// Reactive state management
const state = reactive<ChannelState>({
  activeKey: 'WEBHOOK',
  pKeyEnumList: []
});

/**
 * Get directory of available channel types
 * Converts enum values to tab configurations
 */
const getDirectory = async (): Promise<void> => {
  try {
    const data = enumUtils.enumToMessages(ReceiveChannelType);
    if (data && Array.isArray(data)) {
      state.pKeyEnumList = convertEnumToTabs(data);
    }
  } catch (error) {
    console.error('Failed to load channel types:', error);
  }
};

// Lifecycle hooks
onMounted(() => {
  getDirectory();
});
</script>

<template>
  <Card
    v-model:value="state.activeKey"
    class="flex-1"
    :tabList="state.pKeyEnumList">
    <!-- Webhook channel configuration -->
    <AsyncComponent :visible="state.activeKey === 'WEBHOOK'">
      <ReceivingConfigurationHttps
        v-show="state.activeKey === 'WEBHOOK'"
        :max="5" />
    </AsyncComponent>

    <!-- Email channel configuration -->
    <AsyncComponent :visible="state.activeKey === 'EMAIL'">
      <ReceivingConfigurationEmail
        v-show="state.activeKey === 'EMAIL'"
        :max="20" />
    </AsyncComponent>

    <!-- DingTalk channel configuration -->
    <AsyncComponent :visible="state.activeKey === 'DINGTALK'">
      <ReceivingConfigurationNailingRobot
        v-show="state.activeKey === 'DINGTALK'"
        :max="10" />
    </AsyncComponent>

    <!-- WeChat channel configuration -->
    <AsyncComponent :visible="state.activeKey === 'WECHAT'">
      <ReceivingConfigurationEnterpriseRobot
        v-show="state.activeKey === 'WECHAT'"
        :max="10" />
    </AsyncComponent>
  </Card>
</template>
