<script setup lang="ts">
import { ref, watch } from 'vue';
import { Card, Colon } from '@xcan-angus/vue-ui';
import { debounce } from 'throttle-debounce';
import { duration } from '@xcan-angus/infra';
import { Button, Switch } from 'ant-design-vue';
import { useI18n } from 'vue-i18n';

import { Operation, SignupAllow } from '../types';
import { setting } from '@/api';

const { t } = useI18n();

interface Props {
  signupAllow: SignupAllow;
  signupSwitchLoading: boolean;
  resetButtonLoading: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  signupAllow: undefined,
  signupSwitchLoading: false,
  resetButtonLoading: false
});

const emit = defineEmits<{(e: 'change', value: string | boolean, type: string, operation?: Operation): void }>();

// Current registration switch state
const currEnabled = ref(false);

/**
 * Handle registration switch change with debounce
 * Automatically generates invitation code if enabled and none exists
 */
const enabledChange = debounce(duration.search, async (value) => {
  if (!props.signupAllow?.invitationCode && value) {
    await getSignupInvitationCode();
  }
  emit('change', value, 'enabledregistSwitch', 'registSwitch');
});

// Invitation code value
const code = ref();

/**
 * Generate new signup invitation code
 * Updates local state and emits change event
 */
const getSignupInvitationCode = async () => {
  const [error, { data }] = await setting.getSignupInvitationCode();
  if (error) {
    return;
  }

  code.value = data;
  emit('change', data, 'invitationCode', 'resetButton');
};

// Watch for signupAllow prop changes and update local state
watch(() => props.signupAllow, (newValue) => {
  if (newValue) {
    currEnabled.value = newValue.enabled;
    code.value = newValue.invitationCode;
  }
}, {
  immediate: true
});
</script>
<template>
  <Card bodyClass="px-8 py-5 text-3 leading-3 text-theme-content">
    <template #title>
      <div class="flex items-center">
        <span>{{ t('security.titles.allowRegistration') }}</span>
        <Switch
          v-model:checked="currEnabled"
          :loading="props.signupSwitchLoading"
          size="small"
          class="ml-6 mt-0.5"
          @change="enabledChange" />
        <span
          class="text-3 leading-3 text-theme-sub-content mt-1.25 ml-2">{{ t('security.messages.registrationDesc') }}</span>
      </div>
    </template>
    <span>{{ t('security.columns.invitationCode') }}<Colon /></span>
    <template v-if="currEnabled"><span>{{ code }}</span></template>
    <Button
      size="small"
      type="primary"
      class="ml-5"
      :disabled="!currEnabled"
      :loading="props.resetButtonLoading"
      @click="getSignupInvitationCode">
      {{ t('security.buttons.reset') }}
    </Button>
  </Card>
</template>
