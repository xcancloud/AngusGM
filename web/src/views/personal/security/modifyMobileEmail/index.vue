<script setup lang="ts">
import { computed, ref, watch } from 'vue';

import VerifyModal from '@/views/personal/security/verificationModal/index.vue';
import BindMobileEmailModal from '@/views/personal/security/bindMobileEmail/index.vue';

export interface UserInfoParams {
  country?: string,
  email?: string,
  itc?: string,
  mobile?: string,
}

interface Props {
  valueKey?: 'mobile' | 'email',
  visible: boolean,
  userInfo: UserInfoParams
}

const props = withDefaults(defineProps<Props>(), {
  valueKey: 'mobile',
  visible: false
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'update:visible', val: boolean): void,
  (e: 'ok', key: string, text: UserInfoParams): void,
}>();

const linkSecret = ref('');
const step = ref(1);

const cancel = () => {
  emit('update:visible', false);
};

const ok = (secret: string) => {
  step.value = 2;
  linkSecret.value = secret;
};

const confirm = (value: UserInfoParams) => {
  emit('ok', props.valueKey, value);
};

watch(() => props.visible, () => {
  step.value = isModify.value ? 1 : 2;
  linkSecret.value = '';
});

watch(() => props.userInfo, () => {
  step.value = isModify.value ? 1 : 2;
});

const isModify = computed(() => {
  const { valueKey, userInfo } = props;
  return !!userInfo[valueKey];
});
</script>

<template>
  <template v-if="step === 1">
    <VerifyModal
      :userInfo="userInfo"
      :visible="visible"
      :valueKey="valueKey"
      :isModify="isModify"
      @cancel="cancel"
      @ok="ok" />
  </template>
  <template v-else-if="step === 2">
    <BindMobileEmailModal
      :visible="visible"
      :valueKey="valueKey"
      :linkSecret="linkSecret"
      :isModify="isModify"
      @cancel="cancel"
      @ok="confirm" />
  </template>
</template>
