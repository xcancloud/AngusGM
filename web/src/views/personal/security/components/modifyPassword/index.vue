<script setup lang="ts">
import { ref, Ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { passwordUtils, app } from '@xcan-angus/infra';
import { Popover } from 'ant-design-vue';
import { Hints, Modal, Icon, Input } from '@xcan-angus/vue-ui';

import { auth } from '@/api';

import PasswordStrength from '@/views/personal/security/components/passwordStrength/index.vue';

export type Strength = 'weak' | 'medium' | 'strong';

interface Props {
  visible: boolean,
}

const props = withDefaults(defineProps<Props>(), {
  visible: false
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'update:visible', val: boolean): void,
  (e: 'ok', val: Strength): void,
}>();

const { t } = useI18n();
const SPECIAL_CHARS = "`-=[];',\\./~!@#$%^&*()_+{}:\"<>?";// 允许输入的特殊字符
const oldPassword = ref('');// 当前密码
const oldPasswordError = ref(false);
const newPassword = ref('');// 新密码
const newPasswordError = ref(false);
const newPasswordMessage = ref('');// 新密码错误提示信息
const repeatPassword = ref('');// 二次确认密码
const repeatPasswordError = ref(false);
const repeatPasswordMessage = ref('');// 二次确认密码错误提示信息
const strength: Ref<Strength> = ref('weak');//  密码强度
const showStrength = ref(false);// 展示密码强度框
const loading = ref(false);

const getPopupContainer = (node: HTMLElement): HTMLElement => {
  if (node) {
    return node.parentNode as HTMLElement;
  }

  return document.body;
};

const cancel = (): void => {
  emit('update:visible', false);
};

const ok = async (): Promise<void> => {
  if (!validateOldPassword()) {
    return;
  }

  if (!validateNewPassword()) {
    return;
  }

  if (!validateRepeatPassword()) {
    return;
  }

  if (loading.value) {
    return;
  }

  const params = { oldPassword: oldPassword.value, newPassword: newPassword.value };

  loading.value = true;
  const [error] = await auth.updateCurrentPassword(params);
  loading.value = false;
  if (error) {
    return;
  }

  app.signOut(true);
};

const changeHandle = (event: any, passwordType: 1 | 2 | 3): void => {
  const value = event.target.value;
  switch (passwordType) {
    case 1:
      oldPasswordError.value = false;
      oldPassword.value = value;
      break;
    case 2:
      newPassword.value = value;
      newPasswordChange();
      break;
    case 3:
      repeatPasswordError.value = false;
      repeatPassword.value = value;
      break;
  }
};

const onBlur = (passwordType: 1 | 2 | 3) => {
  switch (passwordType) {
    case 1:
      validateOldPassword();
      break;
    case 2:
      validateNewPassword();
      break;
    case 3:
      validateRepeatPassword();
      break;
  }
};

const newPasswordChange = () => {
  if (!newPassword.value) {
    showStrength.value = false;
    newPasswordError.value = true;
    newPasswordMessage.value = t('personalCenter.security.newNotEmpty');

    return false;
  }

  if (newPassword.value === oldPassword.value) {
    showStrength.value = false;
    newPasswordError.value = true;
    newPasswordMessage.value = t('personalCenter.security.notTheSame');

    return false;
  }

  newPasswordError.value = false;
  showStrength.value = true;
  strength.value = passwordUtils.calcStrength(newPassword.value);

  return true;
};

const validateOldPassword = (): boolean => {
  if (!oldPassword.value) {
    oldPasswordError.value = true;
    return false;
  }

  if (newPassword.value && (newPassword.value !== oldPassword.value)) {
    const result = passwordUtils.isInvalid(newPassword.value);
    if (result.invalid) {
      showStrength.value = false;
      newPasswordError.value = true;
      newPasswordMessage.value = result.message;
    } else {
      showStrength.value = true;
      newPasswordError.value = false;
    }
  }

  return true;
};

const validateNewPassword = (): boolean => {
  const result = passwordUtils.isInvalid(newPassword.value);
  if (result.invalid) {
    showStrength.value = false;
    newPasswordError.value = true;
    newPasswordMessage.value = result.message;

    return false;
  }

  return true;
};

const validateRepeatPassword = (): boolean => {
  if (!repeatPassword.value) {
    repeatPasswordError.value = true;
    repeatPasswordMessage.value = t('personalCenter.security.confirmNotEmpty');

    return false;
  }

  if (repeatPassword.value !== newPassword.value) {
    repeatPasswordError.value = true;
    repeatPasswordMessage.value = '两次密码不一致';

    return false;
  }

  repeatPasswordError.value = false;

  return true;
};

const reset = () => {
  oldPassword.value = '';// 当前密码
  oldPasswordError.value = false;
  newPassword.value = '';// 新密码
  newPasswordError.value = false;
  newPasswordMessage.value = '';// 新密码错误提示信息
  repeatPassword.value = '';// 二次确认密码
  repeatPasswordError.value = false;
  repeatPasswordMessage.value = '';// 二次确认密码错误提示信息
  strength.value = 'weak';//  密码强度
  showStrength.value = false;// 展示密码强度框
  loading.value = false;
};

watch(() => props.visible, () => {
  reset();
});

</script>
<template>
  <Modal
    :closable="false"
    :title="t('personalCenter.security.changePassword')"
    :visible="visible"
    :confirmLoading="loading"
    :getPopupContainer="getPopupContainer"
    @ok="ok"
    @cancel="cancel">
    <Hints :text="t('personalCenter.security.updateSuccessDescription')" class="transform-gpu -translate-y-2" />
    <!-- @todo 使用grid -->
    <div class="flex mt-7.5">
      <div class="leading-8 whitespace-nowrap text-right text-theme-title mr-2.5">
        <div>{{ t('personalCenter.security.currentPassword') }}</div>
        <div :class="{ 'mt-9': oldPasswordError }" class="mt-6">{{ t('personalCenter.security.newPassword') }}</div>
        <div :class="{ 'mt-8.5': newPasswordError || showStrength }" class="mt-6">
          {{
            t('personalCenter.security.confirmPassword') }}
        </div>
      </div>
      <div class="flex flex-wrap">
        <div class="flex-freeze-full">
          <Input
            dataType="mixin-en"
            type="password"
            :allowClear="false"
            :includes="SPECIAL_CHARS"
            :trimAll="true"
            :maxlength="50"
            :class="{ 'input-error': oldPasswordError }"
            :placeholder="t('personalCenter.security.passwordPlaceholder')"
            @change="changeHandle($event, 1)"
            @blur="onBlur(1)" />
          <div v-show="oldPasswordError" class="flex items-center h-3.75 leading-3.75 mt-1 text-3 text-danger">
            <Icon icon="icon-tishi1" class="mr-1 text-3.5" />
            {{ t('personalCenter.security.emptyPasswordDescription') }}
          </div>
        </div>
        <div :class="{ 'mt-4': oldPasswordError }" class="flex-freeze-full mt-6">
          <Popover
            :getPopupContainer="getPopupContainer"
            placement="left"
            trigger="focus">
            <template #content>
              <div class="flex mt-3 text-theme-content">
                <Icon class="flex-shrink-0 mt-0.75 mr-2 text-blue-tips text-3.5" icon="icon-tishi1" />
                <span class="leading-5">{{ t('personalCenter.security.systemPasswordStrength') }}</span>
              </div>
              <div class="flex mt-3 text-theme-content">
                <Icon class="flex-shrink-0 mt-0.75 mr-2 text-blue-tips text-3.5" icon="icon-tishi1" />
                <span class="leading-5">{{ t('personalCenter.security.passwordRules') }}</span>
              </div>
              <div class="flex mt-3 text-theme-content">
                <Icon class="flex-shrink-0 mt-0.75 mr-2 text-blue-tips text-3.5" icon="icon-tishi1" />
                <span class="leading-5">{{ t('personalCenter.security.passwordSpecial') }}</span>
              </div>
              <div class="flex mt-3 text-theme-content">
                <Icon class="flex-shrink-0 mt-0.75 mr-2 text-blue-tips text-3.5" icon="icon-tishi1" />
                <span class="leading-5">{{ t('personalCenter.security.notSpaces') }}</span>
              </div>
            </template>
            <Input
              dataType="mixin-en"
              type="password"
              :allowClear="false"
              :includes="SPECIAL_CHARS"
              :trimAll="true"
              :maxlength="50"
              :class="{ 'input-error': newPasswordError }"
              :placeholder="t('personalCenter.security.notSame')"
              @change="changeHandle($event, 2)"
              @blur="onBlur(2)" />
          </Popover>
          <div v-show="newPasswordError" class="flex items-center h-3.75 leading-3.75 mt-1 text-3 text-danger">
            <Icon icon="icon-tishi1" class="mr-1 text-3.5" />
            {{ newPasswordMessage }}
          </div>
          <PasswordStrength
            v-show="showStrength"
            :strength="strength"
            class="mt-1 h-3.75" />
        </div>
        <div :class="{ 'mt-4': newPasswordError || showStrength }" class="flex-freeze-full mt-6">
          <Input
            dataType="mixin-en"
            type="password"
            :allowClear="false"
            :includes="SPECIAL_CHARS"
            :trimAll="true"
            :maxlength="50"
            :class="{ 'input-error': repeatPasswordError }"
            :placeholder="t('personalCenter.security.theSame')"
            @change="changeHandle($event, 3)"
            @blur="onBlur(3)" />
          <div v-show="repeatPasswordError" class="flex items-center h-3.75 leading-3.75 mt-1 text-3 text-danger">
            <Icon icon="icon-tishi1" class="mr-1 text-3.5" />
            {{ repeatPasswordMessage }}
          </div>
        </div>
      </div>
    </div>
  </Modal>
</template>

<style scoped>
.input-error,
.input-error:focus,
.input-error:hover {
  @apply border-danger;
}

.input-error:focus {
  box-shadow: none;
}

.ant-input-affix-wrapper-focused {
  box-shadow: none;
}

:deep(.ant-popover) {
  @apply w-45;

  left: calc(100% + 0.5rem) !important;
}

:deep(.ant-popover-arrow) {
  @apply hidden;
}

</style>
