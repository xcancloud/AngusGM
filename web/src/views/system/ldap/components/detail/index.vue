<script setup lang="ts">
import { nextTick, onMounted, reactive, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Card, notification } from '@xcan-angus/vue-ui';
import { TabPane, Tabs } from 'ant-design-vue';

// Component imports for LDAP configuration
import ServerInfo from '@/views/system/ldap/components/serverInfo/index.vue';
import LdapConfig from '@/views/system/ldap/components/ldapConfig/index.vue';
import UserConfig from '@/views/system/ldap/components/userConfig/index.vue';
import GroupConfig from '@/views/system/ldap/components/groupConfig/index.vue';
import MemberConfig from '@/views/system/ldap/components/memberConfig/index.vue';
import SubmitButton from '@/views/system/ldap/components/submitButton/index.vue';

import { useI18n } from 'vue-i18n';
import { userDirectory } from '@/api';

const { t } = useI18n();
const router = useRouter();
const route = useRoute();
const query = route.query;

// Active tab key for configuration tabs
const activeKey = ref(0);

// Configuration mode references for different LDAP aspects
const groupMemberMode = ref();
const groupMode = ref();
const mode = ref();
const server = ref();
const userMode = ref();

// Reactive data object to store child component data
const childrenData: any = reactive({
  server: null,
  groupSchema: null,
  membershipSchema: null,
  schema: null,
  userSchema: null
});

// Error handling flag
const errorStop = ref(true);

// Loading states for save and test operations
const saveLoading = ref(false);
const testLoading = ref(false);

// Submit operation type (save or test)
const submitType = ref('save');

// Data for editing existing LDAP configuration
const editShow: any = ref({});

// Page state flags
const isAdd = query.f === 'add';
const isTest = query.f === 'test';

// Validation rules count for form validation
const rulesCount = ref(5);

// Reference to submit button component
const submitRef = ref();

/**
 * Load LDAP directory information for editing
 * Fetches existing configuration and populates form fields
 */
const loadInfo = async () => {
  // Load existing configuration for editing
  if (!isAdd) {
    const [error, res] = await userDirectory.getDirectoryDetail(query.q as string);
    if (error) {
      return;
    }

    // Prepare data for form population
    editShow.value = {
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
      nextTick(() => {
        submitRef.value.testClick();
      });
    }
  }
};

/**
 * Execute validation rules for all child components
 * Ensures all form sections are validated before submission
 */
const publicConfig = function () {
  server.value && server.value.childRules();
  mode.value && mode.value.childRules();
  userMode.value && userMode.value.childRules();
  groupMode.value && groupMode.value.childRules();
  groupMemberMode.value && groupMemberMode.value.childRules();
};

// Test configuration event
const testConfig = function () {
  submitType.value = 'test';
  publicConfig();
  rulesCount.value = 5;
};

// Save configuration
const saveConfig = function () {
  submitType.value = 'save';
  publicConfig();
  rulesCount.value = 5;
};

// Validation callback (emit)
const childRules = function (type: string, label: string, index: number, form: any) {
  rulesCount.value--;
  if (type === 'error' && errorStop.value) {
    // Stop execution after error
    errorStop.value = false;
    // Navigate to form error page
    activeKey.value = index;
    // Clear data
    childrenData[label] = null;
    return;
  }
  if (type === 'success') {
    // Assign form content
    childrenData[label] = form;
  }
};

// Save submission
const saveSubmit = async function (val: { [x: string]: null; }) {
  saveLoading.value = true;
  const submitApi = isAdd ? userDirectory.addDirectory : userDirectory.updateDirectory;
  if (!isAdd) {
    childrenData.id = query.q;
  }
  const [error] = await submitApi({ ...childrenData, sequence: editShow.value.sequence });
  saveLoading.value = false;
  Object.keys(val).forEach(key => {
    val[key] = null;
  });
  if (error) {
    return;
  }
  notification.success(`ldap${isAdd ? t('ldap.detail-1') : t('ldap.detail-2')}${t('ldap.detail-3')}`);
  router.push(decodeURIComponent(query.r as string));
};

// Tab selection event
function tabChange (index: any) {
  activeKey.value = Number(index);
}

// Watch content collection, if all content exists, form validation passes, then trigger submission
watch(() => rulesCount.value, (val) => {
  if (val === 0) {
    const isSubmit = Object.keys(childrenData).every(key => childrenData[key] !== null);
    if (isSubmit) {
      // Don't pass unnecessary group keys groupSchema, membershipSchema are not required, if passed then required field validation
      Object.keys(childrenData).forEach(key => {
        if (['groupSchema', 'membershipSchema'].includes(key)) {
          const isNull = Object.keys(childrenData[key]).every(ckey => childrenData[key][ckey] === '' || ckey === 'ignoreSameNameGroup');
          if (isNull) {
            delete childrenData[key];
          }
        }
      });
      // Determine submission method based on clicked button
      saveSubmit(childrenData);
    }
  }
}, { deep: true });

onMounted(() => {
  loadInfo();
});
</script>
<template>
  <Card bodyClass="p-0" class="min-h-full">
    <Tabs
      v-model:activeKey="activeKey"
      :centered="true"
      size="small"
      @change="tabChange">
      <TabPane :key="0" :tab="t('ldap.titles.serverInfo')" />
      <TabPane :key="1" :tab="t('ldap.titles.ldapConfig')" />
      <TabPane :key="2" :tab="t('ldap.titles.userConfig')" />
      <TabPane :key="3" :tab="t('ldap.titles.groupConfig')" />
      <TabPane :key="4" :tab="t('ldap.titles.memberConfig')" />
    </Tabs>
    <div class="flex flex-col items-center w-full text-3 leading-3">
      <ServerInfo
        v-show="activeKey===0"
        ref="server"
        keys="server"
        :query="editShow.server"
        :index="0"
        class="mt-7"
        @rules="childRules" />
      <LdapConfig
        v-show="activeKey===1"
        ref="mode"
        keys="schema"
        :query="editShow.schema"
        :index="1"
        class="mt-7"
        @rules="childRules" />
      <UserConfig
        v-show="activeKey===2"
        ref="userMode"
        keys="userSchema"
        class="mt-7"
        :query="editShow.userSchema"
        :index="2"
        @rules="childRules" />
      <GroupConfig
        v-show="activeKey===3"
        ref="groupMode"
        keys="groupSchema"
        class="mt-7"
        :query="editShow.groupSchema"
        :index="3"
        @rules="childRules" />
      <MemberConfig
        v-show="activeKey===4"
        ref="groupMemberMode"
        keys="membershipSchema"
        class="mt-7"
        :query="editShow.membershipSchema"
        :index="4"
        @rules="childRules" />
      <submit-button
        ref="submitRef"
        :saveLoading="saveLoading"
        :testLoading="testLoading"
        @testConfig="testConfig"
        @saveConfig="saveConfig" />
    </div>
  </Card>
</template>

<style scoped>
:deep(.ant-tabs-nav-wrap) {
  @apply px-50 text-3 leading-3 ;

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
