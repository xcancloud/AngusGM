<script setup lang="ts">
import { ref, watch } from 'vue';
import { Card, Colon } from '@xcan-angus/vue-ui';
import { debounce } from 'throttle-debounce';
import { duration } from '@xcan-angus/tools';
import { Button, Switch } from 'ant-design-vue';

import { SignupAllow, Operation } from '../PropsType';
import { setting } from '@/api';

interface Props {
  signupAllow: SignupAllow;
  registSwitchLoading: boolean;
  resetButtonLoading: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  signupAllow: undefined,
  registSwitchLoading: false,
  resetButtonLoading: false
});

const emit = defineEmits<{(e: 'change', value: string | boolean, type: string, operation?: Operation): void }>();

const currEnabled = ref(false);
const enabledChange = debounce(duration.search, async (value) => {
  if (!props.signupAllow?.invitationCode && value) {
    await getSignupInvitationCode();
  }
  emit('change', value, 'enabledregistSwitch', 'registSwitch');
});

const code = ref();
const getSignupInvitationCode = async () => {
  const [error, { data }] = await setting.getSignupInvitationCode();
  if (error) {
    return;
  }

  code.value = data;
  emit('change', data, 'invitationCode', 'resetButton');
};

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
        <span>允许注册</span>
        <Switch
          v-model:checked="currEnabled"
          :loading="props.registSwitchLoading"
          size="small"
          class="ml-6 mt-0.5"
          @change="enabledChange" />
        <span
          class="text-3 leading-3 text-theme-sub-content mt-1.25 ml-2">注册码开启后可以通过邀请码注册为当前账号下的用户。</span>
      </div>
    </template>
    <span>邀请码<Colon /></span>
    <template v-if="currEnabled"><span>{{ code }}</span></template>
    <Button
      size="small"
      type="primary"
      class="ml-5"
      :disabled="!currEnabled"
      :loading="props.resetButtonLoading"
      @click="getSignupInvitationCode">
      重置
    </Button>
  </Card>
</template>
