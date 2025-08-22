<script setup lang="ts">
import { ref, watch } from 'vue';
import { Card, Input } from '@xcan-angus/vue-ui';
import { debounce } from 'throttle-debounce';
import { Switch } from 'ant-design-vue';
import { duration } from '@xcan-angus/infra';
import { useI18n } from 'vue-i18n';

import { Operation, SigninLimit } from '../types';

const { t } = useI18n();

interface Props {
  signinLimit: SigninLimit;
  signinSwitchLoading: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  signinLimit: undefined,
  signinSwitchLoading: false
});

const emit = defineEmits<{(e: 'change', value: string | boolean, type: string, operation?: Operation): void }>();

/**
 * Handle locked password error number change with debounce
 * Validates input value and emits change event
 */
const lockedPasswordErrorNumChange = debounce(duration.search, (event: any) => {
  const value = event.target.value;
  if (!value || value === props.signinLimit?.lockedPasswordErrorNum || +value < 1 || +value > 50) {
    return;
  }
  emit('change', value, 'lockedPasswordErrorNum');
});

/**
 * Handle locked duration change with debounce
 * Validates input value and emits change event
 */
const lockedDurationInMinutesChange = debounce(duration.search, (event: any) => {
  const value = event.target.value;
  if (!value || value === props.signinLimit?.lockedDurationInMinutes) {
    return;
  }
  emit('change', value, 'lockedDurationInMinutes');
});

/**
 * Handle password error interval change with debounce
 * Validates input value and emits change event
 */
const passwordErrorIntervalInMinutesChange = debounce(duration.search, (event: any) => {
  const value = event.target.value;
  if (!value || value === props.signinLimit?.passwordErrorIntervalInMinutes) {
    return;
  }
  emit('change', value, 'passwordErrorIntervalInMinutes');
});

// Current login restrictions switch state
const currEnabled = ref(false);

/**
 * Handle login restrictions switch change
 */
const enabledChange = (value) => {
  emit('change', value, 'enabled', 'signinSwitch');
};

// Watch for signinLimit prop changes and update local state
watch(() => props.signinLimit, (newValue) => {
  if (newValue) {
    currEnabled.value = newValue.enabled;
  }
}, {
  immediate: true
});

</script>
<template>
  <Card bodyClass="px-8 py-5">
    <template #title>
      <div class="flex items-center">
        <span>{{ t('security.titles.loginRestrictions') }}</span>
        <Switch
          v-model:checked="currEnabled"
          :loading="props.signinSwitchLoading"
          size="small"
          :disabled="!props.signinLimit"
          class="ml-6 mt-0.5"
          @change="enabledChange" />
      </div>
    </template>
    <div class="flex items-center text-3 leading-3 text-theme-content">
      {{ t('security.labels.within') }}
      <Input
        class="w-20 mx-2"
        size="small"
        dataType="number"
        :value="props.signinLimit?props.signinLimit.passwordErrorIntervalInMinutes:'0'"
        :min="0"
        :disabled="!currEnabled || !props.signinLimit"
        @change="passwordErrorIntervalInMinutesChange" />
      {{ t('security.labels.minutes') }}，{{ t('security.labels.ifPasswordErrorsExceed') }}
      <Input
        class="w-20 mx-2"
        size="small"
        dataType="number"
        :value="props.signinLimit?props.signinLimit.lockedPasswordErrorNum:'1'"
        :min="1"
        :max="50"
        :disabled="!currEnabled || !props.signinLimit"
        @change="lockedPasswordErrorNumChange" />
      {{ t('security.labels.times') }}，{{ t('security.labels.lockAccountFor') }}
      <Input
        class="w-20 mx-2"
        size="small"
        dataType="number"
        :value="props.signinLimit?props.signinLimit.lockedDurationInMinutes:'0'"
        :min="0"
        :disabled="!currEnabled || !props.signinLimit"
        @change="lockedDurationInMinutesChange" />
      {{ t('security.labels.minutes') }}
    </div>
  </Card>
</template>
