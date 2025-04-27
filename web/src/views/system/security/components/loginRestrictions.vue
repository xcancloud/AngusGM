<script setup lang="ts">
import { ref, watch } from 'vue';
import { Input, Card } from '@xcan-angus/vue-ui';
import { debounce } from 'throttle-debounce';
import { Switch } from 'ant-design-vue';
import { duration } from '@xcan-angus/tools';

import { SigninLimit, Operation } from '../PropsType';

interface Props {
  signinLimit: SigninLimit;
  signinSwitchLoading: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  signinLimit: undefined,
  signinSwitchLoading: false
});

const emit = defineEmits<{(e: 'change', value: string | boolean, type: string, operation?: Operation): void }>();

const lockedPasswordErrorNumChange = debounce(duration.search, (event: any) => {
  const value = event.target.value;
  if (!value || value === props.signinLimit?.lockedPasswordErrorNum || +value < 1 || +value > 50) {
    return;
  }
  emit('change', value, 'lockedPasswordErrorNum');
});

const lockedDurationInMinutesChange = debounce(duration.search, (event: any) => {
  const value = event.target.value;
  if (!value || value === props.signinLimit?.lockedDurationInMinutes) {
    return;
  }
  emit('change', value, 'lockedDurationInMinutes');
});

const passwordErrorIntervalInMinutesChange = debounce(duration.search, (event: any) => {
  const value = event.target.value;
  if (!value || value === props.signinLimit?.passwordErrorIntervalInMinutes) {
    return;
  }
  emit('change', value, 'passwordErrorIntervalInMinutes');
});

const currEnabled = ref(false);
const enabledChange = (value) => {
  emit('change', value, 'enabled', 'signinSwitch');
};

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
        <span>登录限制</span>
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
      在
      <Input
        class="w-20 mx-2"
        size="small"
        dataType="number"
        :value="props.signinLimit?props.signinLimit.passwordErrorIntervalInMinutes:'0'"
        :min="0"
        :disabled="!currEnabled || !props.signinLimit"
        @change="passwordErrorIntervalInMinutesChange" />
      分钟内，密码错误超出
      <Input
        class="w-20 mx-2"
        size="small"
        dataType="number"
        :value="props.signinLimit?props.signinLimit.lockedPasswordErrorNum:'1'"
        :min="1"
        :max="50"
        :disabled="!currEnabled || !props.signinLimit"
        @change="lockedPasswordErrorNumChange" />
      次，锁定账号
      <Input
        class="w-20 mx-2"
        size="small"
        dataType="number"
        :value="props.signinLimit?props.signinLimit.lockedDurationInMinutes:'0'"
        :min="0"
        :disabled="!currEnabled || !props.signinLimit"
        @change="lockedDurationInMinutesChange" />
      分钟。
    </div>
  </Card>
</template>
