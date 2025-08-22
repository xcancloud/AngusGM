<script setup lang="ts">
import { defineAsyncComponent, onMounted, ref } from 'vue';
import { TenantType, TenantRealNameStatus } from '@xcan-angus/infra';

import { tenant } from '@/api';

// Audit status component for submitted and failed authentication
const AuditStatus = defineAsyncComponent(() => import('@/components/AuditStatus/index.vue'));
const auditStatusVisible = ref(false);

// Async components for different authentication states
const Unauthorized = defineAsyncComponent(() => import('@/views/system/realname/unauthorized/index.vue'));
const unauthorizedVisible = ref(false);

// Personal authentication component
const PersonalAuth = defineAsyncComponent(() => import('@/views/system/realname/personal/index.vue'));
const personalAuthVisible = ref(false);

// Enterprise authentication component
const EnterpriseAuth = defineAsyncComponent(() => import('@/views/system/realname/enterprise/index.vue'));
const enterpriseAuthVisible = ref(false);

// Government organization authentication component
const GovernmentAuth = defineAsyncComponent(() => import('@/views/system/realname/government/index.vue'));
const governmentAuthVisible = ref(false);

// Authentication form component
const AuthForm = defineAsyncComponent(() => import('@/views/system/realname/authForm/index.vue'));
const authFormVisible = ref(false);

// Form type for authentication
const formType = ref<TenantType>();

// Authentication status and data
const _status = ref();
const message = ref();
const data = ref({});

/**
 * Initialize authentication status and show appropriate component
 * Handles routing logic based on certification audit status
 */
async function start () {
  const [error, res] = await tenant.getCertAudit();
  if (error) {
    return;
  }

  if (!res.data) {
    unauthorizedVisible.value = true;
    return;
  }

  const status = res.data?.status?.value;

  // Handle different authentication statuses
  if (status === TenantRealNameStatus.NOT_SUBMITTED) {
    unauthorizedVisible.value = true;
    return;
  }

  if (status === TenantRealNameStatus.AUDITING) {
    _status.value = status;
    auditStatusVisible.value = true;
    return;
  }

  if (status === TenantRealNameStatus.FAILED_AUDIT) {
    _status.value = status;
    message.value = res.data?.auditRecord?.reason;
    auditStatusVisible.value = true;
    return;
  }

  if (status === TenantRealNameStatus.AUDITED) {
    const type = res.data?.type?.value;
    data.value = res.data;

    // Show appropriate component based on authentication type
    if (type === TenantType.PERSONAL) {
      personalAuthVisible.value = true;
      return;
    }
    if (type === TenantType.ENTERPRISE) {
      enterpriseAuthVisible.value = true;
      return;
    }
    if (type === TenantType.GOVERNMENT) {
      governmentAuthVisible.value = true;
    }
  }
}

/**
 * Reset all component visibility states
 */
const resetVisible = () => {
  unauthorizedVisible.value = false;
  auditStatusVisible.value = false;
  personalAuthVisible.value = false;
  enterpriseAuthVisible.value = false;
  authFormVisible.value = false;
};

/**
 * Handle re-authentication request
 */
const reAudit = () => {
  resetVisible();
  unauthorizedVisible.value = true;
};

/**
 * Navigate to authentication form
 */
const toAuditForm = (type: TenantType) => {
  resetVisible();
  authFormVisible.value = true;
  formType.value = type;
};

/**
 * Return to unauthorized state
 */
const toUnauthorized = () => {
  resetVisible();
  unauthorizedVisible.value = true;
};

/**
 * Handle post-submission state
 */
const toSubmitted = () => {
  resetVisible();
  auditStatusVisible.value = true;
  _status.value = TenantRealNameStatus.AUDITING;
};

onMounted(() => {
  start();
});
</script>

<template>
  <!-- Unauthorized state - show authentication options -->
  <Unauthorized v-if="unauthorizedVisible" @clickAuth="toAuditForm" />

  <!-- Audit status - show current audit state -->
  <AuditStatus
    v-if="auditStatusVisible"
    :status="_status"
    :message="message"
    @reAudit="reAudit">
  </AuditStatus>

  <!-- Personal authentication details -->
  <PersonalAuth v-if="personalAuthVisible" :data="data" />

  <!-- Enterprise authentication details -->
  <EnterpriseAuth v-if="enterpriseAuthVisible" :data="data" />

  <!-- Government organization authentication details -->
  <GovernmentAuth v-if="governmentAuthVisible" :data="data" />

  <!-- Authentication form -->
  <AuthForm
    v-if="authFormVisible"
    :type="formType"
    @cancel="toUnauthorized"
    @confirmed="toSubmitted">
  </AuthForm>
</template>
