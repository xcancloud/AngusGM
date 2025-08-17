<script setup lang="ts">
import { nextTick, onMounted, reactive, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Card, notification } from '@xcan-angus/vue-ui';
import { TabPane, Tabs } from 'ant-design-vue';

// Component imports for LDAP configuration
import ServerInfo from '@/views/system/ldap/serverInfo/index.vue';
import LdapConfig from '@/views/system/ldap/ldapConfig/index.vue';
import UserConfig from '@/views/system/ldap/userConfig/index.vue';
import GroupConfig from '@/views/system/ldap/groupConfig/index.vue';
import MemberConfig from '@/views/system/ldap/memberConfig/index.vue';
import SubmitButton from '@/views/system/ldap/submitButton/index.vue';

import { useI18n } from 'vue-i18n';
import { userDirectory } from '@/api';
import { LdapDetailState, FormSubmissionData } from '../types';
import { createInitialLdapDetailState, validateFormData, cleanFormDataForSubmission } from '../utils';

const { t } = useI18n();
const router = useRouter();
const route = useRoute();
const query = route.query;

// Component state management
const state = reactive<LdapDetailState>(createInitialLdapDetailState());

// Page state flags
const isAdd = query.f === 'add';
const isTest = query.f === 'test';

/**
 * Load LDAP directory information for editing
 * Fetches existing configuration and populates form fields
 */
const loadInfo = async (): Promise<void> => {
  // Load existing configuration for editing
  if (!isAdd) {
    const [error, res] = await userDirectory.getDirectoryDetail(query.q as string);
    if (error) {
      return;
    }

    // Prepare data for form population
    state.editShow = {
      ...res.data,
      server: {
        ...res.data.server,
        directoryType: res.data.server.directoryType.value
      },
      userSchema: {
        ...res.data.userSchema,
        passwordEncoderType: res.data.userSchema?.passwordEncoderType?.value
      }
    };

    // Auto-trigger test if in test mode
    if (isTest) {
      await nextTick(() => {
        state.submitRef?.testClick();
      });
    }
  }
};

/**
 * Execute validation rules for all child components
 * Ensures all form sections are validated before submission
 */
const publicConfig = (): void => {
  state.server?.childRules();
  state.mode?.childRules();
  state.userMode?.childRules();
  state.groupMode?.childRules();
  state.groupMemberMode?.childRules();
};

/**
 * Test configuration event
 */
const testConfig = (): void => {
  state.submitType = 'test';
  publicConfig();
  state.rulesCount = 5;
};

/**
 * Save configuration
 */
const saveConfig = (): void => {
  state.submitType = 'save';
  publicConfig();
  state.rulesCount = 5;
};

/**
 * Validation callback (emit)
 */
const childRules = (type: string, label: string, index: number, form: any): void => {
  state.rulesCount--;

  if (type === 'error' && state.errorStop) {
    // Stop execution after error
    state.errorStop = false;
    // Navigate to form error page
    state.activeKey = index;
    // Clear data
    state.childrenData[label] = null;
    return;
  }

  if (type === 'success') {
    // Assign form content
    state.childrenData[label] = form;
  }
};

/**
 * Save submission
 */
const saveSubmit = async (val: FormSubmissionData): Promise<void> => {
  state.saveLoading = true;

  try {
    const submitApi = isAdd ? userDirectory.addDirectory : userDirectory.updateDirectory;

    if (!isAdd) {
      state.childrenData.id = query.q;
    }

    const [error] = await submitApi({
      ...state.childrenData,
      sequence: state.editShow.sequence
    });

    if (error) {
      return;
    }

    // Clear form data
    Object.keys(val).forEach(key => {
      val[key] = null;
    });

    notification.success(`ldap${isAdd ? t('ldap.detail-1') : t('ldap.detail-2')}${t('ldap.detail-3')}`);
    await router.push(decodeURIComponent(query.r as string));
  } finally {
    state.saveLoading = false;
  }
};

/**
 * Tab selection event
 */
const tabChange = (index: any): void => {
  state.activeKey = Number(index);
};

// Watch content collection, if all content exists, form validation passes, then trigger submission
watch(() => state.rulesCount, (val) => {
  if (val === 0) {
    const isSubmit = validateFormData(state.childrenData);

    if (isSubmit) {
      // Clean up form data for submission
      const cleanedData = cleanFormDataForSubmission(state.childrenData);

      // Determine submission method based on clicked button
      saveSubmit(cleanedData);
    }
  }
}, { deep: true });

onMounted(() => {
  loadInfo();
});
</script>

<template>
  <Card bodyClass="p-0" class="min-h-full">
    <!-- Configuration Tabs -->
    <Tabs
      v-model:activeKey="state.activeKey"
      :centered="true"
      size="small"
      @change="tabChange">
      <TabPane :key="0" :tab="t('ldap.titles.serverInfo')" />
      <TabPane :key="1" :tab="t('ldap.titles.ldapConfig')" />
      <TabPane :key="2" :tab="t('ldap.titles.userConfig')" />
      <TabPane :key="3" :tab="t('ldap.titles.groupConfig')" />
      <TabPane :key="4" :tab="t('ldap.titles.memberConfig')" />
    </Tabs>

    <!-- Configuration Components -->
    <div class="flex flex-col items-center w-full text-3 leading-3">
      <!-- Server Information Configuration -->
      <ServerInfo
        v-show="state.activeKey === 0"
        ref="state.server"
        keys="server"
        :query="state.editShow.server"
        :index="0"
        class="mt-7"
        @rules="childRules" />

      <!-- LDAP Configuration -->
      <LdapConfig
        v-show="state.activeKey === 1"
        ref="state.mode"
        keys="schema"
        :query="state.editShow.schema"
        :index="1"
        class="mt-7"
        @rules="childRules" />

      <!-- User Configuration -->
      <UserConfig
        v-show="state.activeKey === 2"
        ref="state.userMode"
        keys="userSchema"
        class="mt-7"
        :query="state.editShow.userSchema"
        :index="2"
        @rules="childRules" />

      <!-- Group Configuration -->
      <GroupConfig
        v-show="state.activeKey === 3"
        ref="state.groupMode"
        keys="groupSchema"
        class="mt-7"
        :query="state.editShow.groupSchema"
        :index="3"
        @rules="childRules" />

      <!-- Member Configuration -->
      <MemberConfig
        v-show="state.activeKey === 4"
        ref="state.groupMemberMode"
        keys="membershipSchema"
        class="mt-7"
        :query="state.editShow.membershipSchema"
        :index="4"
        @rules="childRules" />

      <!-- Submit Button -->
      <submit-button
        ref="state.submitRef"
        :saveLoading="state.saveLoading"
        :testLoading="state.testLoading"
        @testConfig="testConfig"
        @saveConfig="saveConfig" />
    </div>
  </Card>
</template>

<style scoped>
:deep(.ant-tabs-nav-wrap) {
  @apply px-50 text-3 leading-3;
  height: 45px;
}

:deep(.ant-tabs-nav) {
  height: 46px;
}

:deep(.ant-tabs-nav-list) {
  @apply flex justify-between flex-1;
}

:deep(.ant-tabs-top > .ant-tabs-nav) {
  margin: 0;
}
</style>
