<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { http, PUB_ESS } from '@xcan-angus/tools';
import { useRouter } from 'vue-router';
import { Modal, notification } from '@xcan-angus/vue-ui';
import { Button, Checkbox } from 'ant-design-vue';
import { login } from '@/api';
import DOMPurify from 'dompurify';

import PasswordInput from '@/components/PasswordInput/index.vue';
import PasswordConfirmInput from '@/components/PasswordConfirmInput/index.vue';
import InvitationCodeInput from '@/components/InvitationCodeInput/index.vue';
import VerificationCodeInput from '@/components/VerificationCodeInput/index.vue';
import EmailInput from '@/components/EmailInput/index.vue';
import MobileInput from '@/components/MobileInput/index.vue';

interface Props {
  type: string
}

const { t } = useI18n();
const router = useRouter();
const props = withDefaults(defineProps<Props>(), {
  type: 'mobile'
});

const error = ref(false);
const errorMessage = ref(); // 注册的错误提示信息
const loading = ref(false); // 注册请求发送中
const checked = ref(false); // 注册必选条件是否已经全选

const emailForm = reactive({
  account: undefined,
  invitationCode: undefined,
  password: undefined,
  confirmPassword: undefined,
  signupType: 'EMAIL',
  socialCode: undefined,
  verificationCode: undefined
});
const mobileForm = reactive({
  account: undefined,
  country: 'CN',
  invitationCode: undefined,
  itc: '86',
  password: undefined,
  confirmPassword: undefined,
  signupType: 'MOBILE',
  socialCode: undefined,
  verificationCode: undefined
});

const terms = ref();
const privacy = ref();
const termsContent = ref();
const visible = ref(false);
const modelTitle = ref('');

const mobileRef = ref();
const emailRef = ref();
const mobileVeriRef = ref(); // TODO 命名语法错误问题
const mobilePassRef = ref();
const mobileConfirmpassRef = ref();
const emailConfirmpassRef = ref();
const emailPassRef = ref();
const emailVeriRef = ref();

const validateData = ref(false);

const isMobile = computed(() => {
  return props.type === 'mobile';
});
watch(() => props.type, () => {
  error.value = false;
});

const loadTerms = () => {
  http.get(`${PUB_ESS}/content/setting/termsAndConditions`) // TODO 删除PUB_ESS引用，服务条款和隐私条款报错前端
    .then(([error, resp]) => {
      if (error) {
        return;
      }
      terms.value = resp.data;
    });
  http.get(`${PUB_ESS}/content/setting/privacyPolicy`)
    .then(([error, resp]) => {
      if (error) {
        return;
      }
      privacy.value = resp?.data || '';
    });
};

const openModal = (key) => {
  if (key === 'termsVisible') {
    modelTitle.value = t('xcan_cloud') + t('terms_cloud');
    termsContent.value = terms.value;
  } else {
    modelTitle.value = t('xcan_cloud') + t('privacy_cloud');
    termsContent.value = privacy.value;
  }
  visible.value = !visible.value;
};

const closeModel = () => {
  visible.value = !visible.value;
};

const validateAccount = () => {
  if (mobileRef.value?.validateData) {
    return mobileRef.value?.validateData();
  }
};

const validateEmail = () => {
  if (emailRef.value.validateData) {
    return emailRef.value.validateData();
  }
};

const validateMobileForm = () => {
  let flag = 0;// 校验失败累加1
  if (!mobileRef.value?.validateData()) {
    flag++;
  }

  if (!mobileVeriRef.value?.validateData()) {
    flag++;
  }

  if (!mobilePassRef.value?.validateData()) {
    flag++;
  }

  if (!mobileConfirmpassRef.value?.validateData()) {
    flag++;
  }

  return !!flag;
};

const validateEmailForm = () => {
  let flag = 0;// 校验失败累加1
  if (!emailRef.value?.validateData()) {
    flag++;
  }

  if (!emailVeriRef.value?.validateData()) {
    flag++;
  }

  if (!emailPassRef.value?.validateData()) {
    flag++;
  }

  if (!emailConfirmpassRef.value?.validateData()) {
    flag++;
  }

  return !!flag;
};
const signup = async () => {
  let isInvalid = false;
  if (isMobile.value) {
    isInvalid = validateMobileForm();
  } else {
    isInvalid = validateEmailForm();
  }

  // 校验不通过
  if (isInvalid || !checked.value) {
    validateData.value = true;
    return;
  }
  loading.value = true;
  error.value = false;
  const params = isMobile.value ? mobileForm : emailForm;
  const [err] = await login.signup(params);
  loading.value = false;
  validateData.value = false;
  if (err) {
    error.value = true;
    errorMessage.value = err.message;
    return;
  }
  notification.success('注册成功');
  router.push('/signin');
};

onMounted(() => {
  loadTerms();
});

</script>
<template>
  <div>
    <template v-if="isMobile">
      <MobileInput
        ref="mobileRef"
        v-model:value="mobileForm.account"
        class="input-container block-fixed" />
      <VerificationCodeInput
        key="mobile-veri"
        ref="mobileVeriRef"
        v-model:value="mobileForm.verificationCode"
        :validateAccount="validateAccount"
        :account="mobileForm.account"
        class="input-container block-fixed" />
      <PasswordInput
        key="mobile-pass"
        ref="mobilePassRef"
        v-model:value="mobileForm.password"
        :showTip="true"
        :placeholder="$t('enter-pass')"
        class="input-container block-fixed" />
      <PasswordConfirmInput
        key="mobile-confirmpass"
        ref="mobileConfirmpassRef"
        v-model:value="mobileForm.confirmPassword"
        :password="mobileForm.password"
        :placeholder="$t('confirm-pass')"
        class="input-container block-fixed" />
      <InvitationCodeInput
        key="mobile-invite"
        v-model:value="mobileForm.invitationCode"
        class="input-container block-fixed" />
    </template>
    <template v-else>
      <EmailInput
        ref="emailRef"
        v-model:value="emailForm.account"
        class="input-container block-fixed" />
      <VerificationCodeInput
        key="email-veri"
        ref="emailVeriRef"
        v-model:value="emailForm.verificationCode"
        sendType="email"
        :validateAccount="validateEmail"
        :account="emailForm.account"
        class="input-container block-fixed" />
      <PasswordInput
        key="email-pass"
        ref="emailPassRef"
        v-model:value="emailForm.password"
        :showTip="true"
        :placeholder="$t('enter-pass')"
        class="input-container block-fixed" />
      <PasswordConfirmInput
        key="email-confirmpass"
        ref="emailConfirmpassRef"
        v-model:value="emailForm.confirmPassword"
        :password="emailForm.password"
        :placeholder="$t('confirm-pass')"
        class="input-container block-fixed" />
      <InvitationCodeInput
        key="email-invite"
        v-model:value="emailForm.verificationCode"
        class="input-container block-fixed" />
    </template>
    <div :class="{'error':error}" class="error-container block-fixed">
      <Button
        :loading="loading"
        class="form-btn"
        type="primary"
        size="large"
        @click="signup">
        {{ t('register') }}
      </Button>
      <div class="error-message">{{ errorMessage }}</div>
    </div>
    <div class="link-container">
      <RouterLink class="link" to="/signin">{{ t('account-signin') }}</RouterLink>
    </div>
    <div class="text-group">
      <em class="require">*</em>
      <span>{{ t('xcan_cloud') }}</span>
      <span class="link" @click="openModal('termsVisible')">{{ t('terms_cloud') }}</span>
      <span class="link" @click="openModal('prvacyVisible')">{{ t('privacy_cloud') }}</span>
    </div>
    <div class="flex items-center">
      <Checkbox v-model:checked="checked" class="checkbox-container">
        {{ t('clause') }}
      </Checkbox>
      <span class="error-message !px-1" :class="{'!block': !checked && validateData}">请勾选阅读并同意</span>
    </div>
    <Modal
      key="termsVisible"
      v-model:visible="visible"
      :title="modelTitle"
      @ok="closeModel">
      <div class="modal-content" v-html="DOMPurify.sanitize(termsContent)"></div>
    </Modal>
  </div>
</template>

<style scoped>
.require {
  position: relative;
  top: 2px;
  margin-right: 2px;
  color: #f5222d;
  font-size: 14px;
  font-style: normal;
  line-height: 18px;
}

.input-container {
  margin-bottom: 20px;
}

.error-container {
  position: relative;
  margin-bottom: 16px;
}

.form-btn {
  width: 100%;
  height: 44px;
  border-radius: 24px;
}

.link-container {
  display: flex;
  justify-content: flex-end;
  font-size: 14px;
  line-height: 20px;
  user-select: none;
}

.link-container .link {
  color: rgba(24, 144, 255, 100%);
  white-space: nowrap;
}

.text-group {
  font-size: 14px;
}

.text-group .link {
  color: rgba(24, 144, 255, 100%);
  cursor: pointer;
}

.checkbox-container {
  color: rgba(68, 76, 90, 100%);
  user-select: none;
}

.modal-content {
  max-height: 600px;
  overflow: auto;
}
</style>
