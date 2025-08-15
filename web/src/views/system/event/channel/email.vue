<script setup lang="ts">
import { Hints } from '@xcan-angus/vue-ui';
import ChannelAddress from './channelAddress.vue';
import { useI18n } from 'vue-i18n';

import { ChannelTypeProps } from './types';

const { t } = useI18n();

// Component props with proper typing
const props = withDefaults(defineProps<ChannelTypeProps>(), {
  max: 5
});
</script>

<template>
  <div>
    <Hints :text="$t('event.channel.titles.emailTitle')" />
    <ChannelAddress
      :max="props.max"
      channelType="EMAIL"
      :placeholder="t('event.channel.placeholder.inputEmailAddress')" />
  </div>
</template>
