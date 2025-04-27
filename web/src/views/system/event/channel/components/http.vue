<script setup lang="ts">
import { Hints } from '@xcan/design';
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
    <Hints :text="$t('https-title')" />
    <ChannelAddress
      :max="props.max"
      channelType="WEBHOOK"
      :placeholder="t('address-placeholder')" />
  </div>
</template>
