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
    <Hints :text="$t('event.channel.titles.emailTitle')" />
    <ChannelAddress
      :max="props.max"
      channelType="EMAIL"
      :placeholder="t('event.channel.placeholder.inputEmailAddress')" />
  </div>
</template>
