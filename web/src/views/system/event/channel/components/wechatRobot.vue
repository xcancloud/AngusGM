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
    <Hints :text="$t('event.channel.titles.weChatTitle')" />
    <ChannelAddress
      channelType="WECHAT"
      :max="props.max"
      :placeholder="t('event.channel.placeholder.inputWeChatAddress')" />
  </div>
</template>
