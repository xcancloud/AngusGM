<script setup lang="ts">
import { defineAsyncComponent, onMounted, ref } from 'vue';

import { tenant } from '@/api';

// 未认证
const Unauthorized = defineAsyncComponent(() => import('@/views/system/auth/components/unauthorized/index.vue'));
const unauthorizedVisible = ref(false);

// 已提交 、认证失败
const AuditStatus = defineAsyncComponent(() => import('@/components/AuditStatus/index.vue'));
const auditStatusVisible = ref(false);

// 已认证 - 个人
const PersonalAuth = defineAsyncComponent(() => import('@/views/system/auth/components/personal/index.vue'));
const personalAuthVisible = ref(false);

// 已认证 - 公司
const EnterpriseAuth = defineAsyncComponent(() => import('@/views/system/auth/components/enterprise/index.vue'));
const enterpriseAuthVisible = ref(false);

// 已认证 - 政府组织机构
const GovernmentAuth = defineAsyncComponent(() => import('@/views/system/auth/components/government/index.vue'));
const governmentAuthVisible = ref(false);

// 认证表单
const AuthForm = defineAsyncComponent(() => import('@/views/system/auth/components/authForm/index.vue'));
const authFormVisible = ref(false);

const formType = ref<'PERSONAL' | 'ENTERPRISE' | 'GOVERNMENT'>();

const _status = ref();
const message = ref();
const data = ref({});

// const firstLoad = ref(true);
async function start () {
  // 类型判断跳转路由写这里
  const [error, res] = await tenant.getCertAudit();
  // firstLoad.value = true;
  if (error) {
    return;
  }
  if (!res.data) {
    unauthorizedVisible.value = true;
    return;
  }

  const status = res.data?.status?.value;
  if (status === 'NOT_SUBMITTED') {
    unauthorizedVisible.value = true;
    return;
  }
  if (status === 'AUDITING') {
    _status.value = status;
    auditStatusVisible.value = true;
    return;
  }
  if (status === 'FAILED_AUDIT') {
    _status.value = status;
    message.value = res.data?.auditRecord?.reason;
    auditStatusVisible.value = true;
    return;
  }

  if (status === 'AUDITED') {
    const type = res.data?.type?.value;
    data.value = res.data;
    if (type === 'PERSONAL') {
      personalAuthVisible.value = true;
      return;
    }
    if (type === 'ENTERPRISE') {
      enterpriseAuthVisible.value = true;
      return;
    }
    if (type === 'GOVERNMENT') {
      governmentAuthVisible.value = true;
    }
  }
}

const resetVisible = () => {
  unauthorizedVisible.value = false;
  auditStatusVisible.value = false;
  personalAuthVisible.value = false;
  enterpriseAuthVisible.value = false;
  authFormVisible.value = false;
};
// 重新认证
const reAudit = () => {
  resetVisible();
  unauthorizedVisible.value = true;
};
// 立即认证
const toAuditForm = (type: 'PERSONAL' | 'ENTERPRISE' | 'GOVERNMENT') => {
  resetVisible();
  authFormVisible.value = true;
  formType.value = type;
};

// 回到未认证页面
const toUnauthorized = () => {
  resetVisible();
  unauthorizedVisible.value = true;
};

// 提交后
const toSubmitted = () => {
  resetVisible();
  auditStatusVisible.value = true;
  _status.value = 'AUDITING';
};
onMounted(() => {
  start();
});
</script>
<template>
  <Unauthorized v-if="unauthorizedVisible" @clickAuth="toAuditForm" />

  <AuditStatus
    v-if="auditStatusVisible"
    :status="_status"
    :message="message"
    @reAudit="reAudit">
  </AuditStatus>

  <PersonalAuth v-if="personalAuthVisible" :data="data" />

  <EnterpriseAuth v-if="enterpriseAuthVisible" :data="data" />

  <GovernmentAuth v-if="governmentAuthVisible" :data="data" />

  <AuthForm
    v-if="authFormVisible"
    :type="formType"
    @cancel="toUnauthorized"
    @confrimed="toSubmitted">
  </AuthForm>
</template>
