<script setup lang="ts">
import { onMounted, reactive, ref, defineAsyncComponent } from 'vue';
import { enumLoader } from '@xcan-angus/tools';
import { Card, AsyncComponent } from '@xcan-angus/vue-ui';

const ReceivingConfigurationHttps = defineAsyncComponent(() => import('./components/http.vue'));
const ReceivingConfigurationEmail = defineAsyncComponent(() => import('./components/email.vue'));
const ReceivingConfigurationNailingRobot = defineAsyncComponent(() => import('./components/dingRobot.vue'));
const ReceivingConfigurationEnterpriseRobot = defineAsyncComponent(() => import('./components/wechatRobot.vue'));

const activeKey = ref('WEBHOOK');
const enumsList: {
  pKeyEnumList: Record<string, any>[]
} = reactive({
  pKeyEnumList: []
});

const getDictory = async () => {
  const [error, data] = await enumLoader.load('ReceiveChannelType');
  if (error) {
    return;
  }

  enumsList.pKeyEnumList = data?.map(item => {
    return {
      key: item.value,
      tab: item.message
    };
  });
};

onMounted(() => {
  getDictory();
});
</script>
<template>
  <Card
    v-model:value="activeKey"
    class="flex-1"
    :tabList="enumsList.pKeyEnumList">
    <AsyncComponent :visible="activeKey==='WEBHOOK'">
      <ReceivingConfigurationHttps v-show="activeKey==='WEBHOOK'" :max="5" />
    </AsyncComponent>
    <AsyncComponent :visible="activeKey==='EMAIL'">
      <ReceivingConfigurationEmail v-show="activeKey==='EMAIL'" :max="20" />
    </AsyncComponent>
    <AsyncComponent :visible="activeKey==='DINGTALK'">
      <ReceivingConfigurationNailingRobot v-show="activeKey==='DINGTALK'" :max="10" />
    </AsyncComponent>
    <AsyncComponent :visible="activeKey==='WECHAT'">
      <ReceivingConfigurationEnterpriseRobot v-show="activeKey==='WECHAT'" :max="10" />
    </AsyncComponent>
  </Card>
</template>
