<script setup lang='ts'>
import { defineAsyncComponent, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Form, FormItem, InputPassword } from 'ant-design-vue';
import { Modal, notification } from '@xcan-angus/vue-ui';

import { passwordUtils } from '@xcan-angus/infra';
import { auth } from '@/api';

const PasswdTip = defineAsyncComponent(() => import('@/views/organization/user/components/passwordTip/index.vue'));

interface FormStateType {
  password: string | undefined,
  passwordConfirm?: string | undefined,
}

interface Props {
  visible: boolean,
  userId: string | undefined,
}

const props = withDefaults(defineProps<Props>(), {});

const emit = defineEmits<{(e: 'cancel'): void }>();

const { t } = useI18n();

const formRef = ref();

const state = reactive<{
  confirmLoading: boolean,
  isShowTips: boolean,
  length: boolean,
  chart: boolean,
  form: FormStateType,
}>({
  confirmLoading: false,
  isShowTips: false,
  length: false, // 密码长度规则是否满足
  chart: false, // 密码至少包含数字、字母、特殊字符任意两种
  form: {
    password: undefined,
    passwordConfirm: undefined
  }
});

const validFuncs = {
  password: () => {
    const val = (state.form.password || '');
    if (!val) {
      return Promise.reject(new Error(t('userRule0')));
    } else if (val.length < 6 || val.length > 50) {
      return Promise.reject(new Error(t('userRule3')));
    } else if (passwordUtils.getTypesNum(val.split('')) < 2) {
      return Promise.reject(new Error(t('userRule4')));
    }
    return Promise.resolve();
  },
  passwordConfirm: () => {
    if (!state.form.passwordConfirm) {
      return Promise.reject(new Error(t('userRule1')));
    } else if (state.form.passwordConfirm !== state.form.password) {
      return Promise.reject(new Error(t('userRule2')));
    }
    return Promise.resolve();
  }
};

const rules = {
  password: [
    { required: true, min: 6, max: 50, validator: validFuncs.password, trigger: 'blur' }
  ],
  passwordConfirm: [
    { required: true, validator: validFuncs.passwordConfirm, trigger: 'blur' }
  ]
};

const handleFuncs = {
  changeShowTips: (flag: boolean) => {
    state.isShowTips = flag;
  },
  changeStrength: (e: { target: { value: string } }) => {
    const { value = '' } = e.target;
    const valArr = value.split('');
    const typeNum = passwordUtils.getTypesNum(valArr);
    state.length = value.length >= 6 && value.length <= 50;
    state.chart = typeNum >= 2;
  },
  close: () => {
    emit('cancel');
  },
  save: () => {
    formRef.value.validate().then(async () => {
      state.confirmLoading = true;
      const [error] = await auth.updateUserPassword({ id: props.userId, newPassword: state.form.password });
      state.confirmLoading = false;
      if (error) {
        return;
      }
      notification.success(t('passwordSuccess'));
      handleFuncs.close();
    });
  }
};
</script>
<template>
  <Modal
    :title="t('resetPassword')"
    :maskClosable="false"
    :keyboard="false"
    :confirmLoading="state.confirmLoading"
    :visible="visible"
    destroyOnClose
    width="540px"
    class="reative"
    @ok="handleFuncs.save()"
    @cancel="handleFuncs.close()">
    <Form
      ref="formRef"
      :model="state.form"
      :rules="rules"
      v-bind="{labelCol: {span: 6}, wrapperCol: {span: 16}}">
      <FormItem :label="t('newPassword')" name="password">
        <InputPassword
          v-model:value="state.form.password"
          :maxlength="50"
          :placeholder="t('userPlaceholder7')"
          size="small"
          @focus="handleFuncs.changeShowTips(true)"
          @blur="handleFuncs.changeShowTips(false)"
          @change="handleFuncs.changeStrength" />
        <div
          class="w-42.5 bg-white p-3 absolute top-0 -right-59  transition-all"
          :class="state.isShowTips ? 'show' : 'hide'">
          <PasswdTip :length="state.length" :chart="state.chart" />
        </div>
      </FormItem>
      <FormItem :label="t('confirmPassword')" name="passwordConfirm">
        <InputPassword
          v-model:value="state.form.passwordConfirm"
          size="small"
          :maxlength="50"
          :placeholder="t('userPlaceholder8')" />
      </FormItem>
    </Form>
  </Modal>
</template>
<style scoped>
.show {
  @apply opacity-100;
}

.hide {
  @apply opacity-0;
}

:deep(.ant-form-item-label > label) {
  height: 28px;
}
</style>
