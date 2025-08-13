<script setup lang="ts">
import { Hints } from '@xcan-angus/vue-ui';
import ChannelAddress from './channelAddress.vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

interface Props {
  max: number
}

const props = withDefaults(defineProps<Props>(), {
  max: 5
});
</script>

<template>
  <div>
    <Hints :text="$t('event.channel.titles.dingTalkTitle')" />
    <ChannelAddress
      :max="props.max"
      channelType="DINGTALK"
      :placeholder="t('event.channel.placeholder.inputDingTalkKeyword')" />
  </div>
</template>
<style scoped>
:deep(.ant-steps-item-icon) {
  border: 0;
  background-color: var(--content-special-text-hover) !important;
}

:deep(.ant-steps-item-title) {
  color: var(--content-text-title) !important;
}

:deep(.ant-steps-item-description) {
  color: var(--content-text-content) !important;
}

:deep(.ant-steps-item-icon .ant-steps-icon) {
  color: white;
}

:deep(.anticon.anticon-check svg) {
  margin-bottom: 7px;
}

:deep(.ant-steps-item-container:hover .ant-steps-item-title) {
  color: var(--content-special-text-hover) !important;
}

:deep(.ant-steps-item-container:hover .ant-steps-icon) {
  color: white !important;
}

:deep(.ant-steps-item-container:hover .ant-steps-item-description) {
  color: var(--content-special-text-hover) !important;
}

</style>
