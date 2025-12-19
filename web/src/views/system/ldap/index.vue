<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { Alert, Badge, Button, Dropdown, Form, FormItem, Menu, MenuItem, Skeleton } from 'ant-design-vue';
import { ButtonAuth, Hints, Icon, IconRefresh, Input, modal, notification, PureCard, Table } from '@xcan-angus/vue-ui';
import { app } from '@xcan-angus/infra';
import { useI18n } from 'vue-i18n';

import { userDirectory } from '@/api';
import { LdapDirectoryConfig, LdapMainState, TestResultData, DirectoryOperationParams } from './types';
import {
  createTableColumns, createInitialLdapMainState, getAlertType, processLdapDirectoriesResponse,
  createTestRecord, formatConnectionMessage, formatUserSyncMessage, formatGroupSyncMessage,
  formatMembershipSyncMessage, canMoveUp, canMoveDown, canDeleteDirectory, canTestDirectory,
  canSetDefaultDirectory, canModifyDirectory, canSyncDirectory
} from './utils';

const { t } = useI18n();
const router = useRouter();

// Component state management
const state = reactive<LdapMainState>(createInitialLdapMainState());

// Table columns configuration
const columns = ref(createTableColumns(t));

// Constants
const maxlength = 10; // Maximum number of LDAP directories allowed
const r = encodeURIComponent(location.pathname);

/**
 * Fetch LDAP directory list from API
 * Filters out items with null server and maps server properties
 */
const getList = async (): Promise<void> => {
  if (state.listSpin) {
    return;
  }

  try {
    state.listSpin = true;
    const [error, res] = await userDirectory.getDirectories();
    if (error) {
      return;
    }

    state.dataSource = processLdapDirectoriesResponse(res);
  } finally {
    state.listSpin = false;
  }
};

// Initialize directory list on component mount
getList();

/**
 * Navigate to add LDAP directory page
 */
const addLdapList = (): void => {
  router.push({
    path: '/system/ldap/add',
    query: {
      f: 'add',
      r
    }
  });
};

/**
 * Navigate to edit LDAP directory page
 */
const editLdap = ({ id }: DirectoryOperationParams): void => {
  router.push({
    path: '/system/ldap/detail',
    query: {
      f: 'edit',
      q: id,
      r
    }
  });
};

/**
 * Synchronize LDAP directory data
 */
const syncLdap = async ({ id }: DirectoryOperationParams, index: number): Promise<void> => {
  state.testRecord = { server: {} };
  state.syncLoading[index] = true;

  try {
    const [error, res] = await userDirectory.syncDirectory(id);

    if (error) {
      return;
    }

    notification.success(t('ldap.messages.syncSuccess'));
    handleTestResult(res.data);
  } finally {
    state.syncLoading[index] = false;
  }
};

/**
 * Toggle LDAP directory enabled/disabled status
 */
const enabledLdap = async ({ id, enabled }: DirectoryOperationParams): Promise<void> => {
  const [error] = await userDirectory.toggleDirectoryEnabled([{
    enabled: !enabled,
    id: id
  }]);

  if (error) {
    return;
  }

  notification.success(t('ldap.messages.statusUpdateSuccess'));
  await getList();
};

/**
 * Show delete confirmation modal
 */
const deleteConfirm = (record: LdapDirectoryConfig, deleteSync: boolean): void => {
  modal.confirm({
    centered: true,
    title: t('ldap.delete-config'),
    content: t('ldap.delete-content', { name: record.name }),
    onOk () {
      deleteLdap(record.id!, deleteSync);
    }
  });
};

/**
 * Delete LDAP directory
 */
const deleteLdap = async (id: string, deleteSync: boolean): Promise<void> => {
  state.testLoading = true;

  try {
    const [error] = await userDirectory.deleteDirectory(id, deleteSync);

    if (error) {
      return;
    }

    notification.success(t('ldap.messages.ldapDataDeleted'));
    await getList();
  } finally {
    state.testLoading = false;
  }
};

/**
 * Update directory sequence order (move up/down)
 */
const updateSequence = async (item: LdapDirectoryConfig, sequence: number, index: number): Promise<void> => {
  const otherItem = state.dataSource[index + sequence];
  const [error] = await userDirectory.updateDirectorySequence([
    { id: item.id, sequence: index + sequence + 1 },
    { id: otherItem.id, sequence: index + 1 }
  ]);

  if (error) {
    return;
  }

  if (sequence > 0) {
    notification.success(t('ldap.messages.moveDownSuccess'));
  } else {
    notification.success(t('ldap.messages.moveUpSuccess'));
  }

  getList();
};

/**
 * Hide test modal
 */
const hideTest = (): void => {
  state.testVisible = false;
};

/**
 * Show test modal and prepare test data
 */
const test = (record: LdapDirectoryConfig): void => {
  state.testVisible = true;
  state.testRecord = createTestRecord(record);

  // Reset test messages
  state.connectMsg = t('ldap.messages.connectServer') + ': ' + t('ldap.messages.notExecuted');
  state.userMsg = t('ldap.messages.notExecuted');
  state.groupMsg = t('ldap.messages.notExecuted');
  state.memberMsg = t('ldap.messages.notExecuted');
  state.showData = {};
};

/**
 * Execute LDAP connection test
 */
const testLdap = async (): Promise<void> => {
  state.testLoading = true;

  try {
    const [error, res] = await userDirectory.testDirectory(state.testRecord);

    if (error) {
      return;
    }

    state.testVisible = true;
    handleTestResult(res.data);
  } finally {
    state.testLoading = false;
  }
};

/**
 * Process and display test results
 */
const handleTestResult = (data: TestResultData): void => {
  state.showData = data;
  state.testVisible = true;

  // Update test result messages
  state.connectMsg = formatConnectionMessage(data, t);
  state.userMsg = formatUserSyncMessage(data, t);
  state.groupMsg = formatGroupSyncMessage(data, t);
  state.memberMsg = formatMembershipSyncMessage(data, t);

  state.testLoading = false;
};
</script>

<template>
  <div class="flex flex-col min-h-full">
    <Hints :text="t('ldap.messages.description')" class="mb-1" />

    <!-- Test Results Panel -->
    <div class="flex pr-3">
      <div v-show="state.testVisible" class="flex-1">
        <Skeleton
          v-if="state.testLoading"
          active
          :paragraph="{rows: 4}"
          :title="false" />
        <template v-else>
          <Alert
            key="connect"
            class="text-3 min-w-50"
            showIcon
            :message="state.connectMsg"
            :type="getAlertType(state.showData.connectSuccess)">
            <template v-if="state.showData.connectSuccess === null" #icon>
              <Icon icon="icon-jinyong"></Icon>
            </template>
          </Alert>
          <Alert
            key="user"
            :message="t('ldap.messages.user') + '：' + state.userMsg"
            class="text-3 mt-2"
            showIcon
            :type="getAlertType(state.showData.userSuccess)">
            <template v-if="state.showData.userSuccess === null" #icon>
              <Icon icon="icon-jinyong"></Icon>
            </template>
          </Alert>
          <Alert
            key="group"
            :message="t('ldap.messages.group') + '：' + state.groupMsg"
            class="text-3 mt-2"
            showIcon
            :type="getAlertType(state.showData.groupSuccess)">
            <template v-if="state.showData.groupSuccess === null" #icon>
              <Icon icon="icon-jinyong"></Icon>
            </template>
          </Alert>
          <Alert
            key="member"
            :message="t('ldap.messages.member') + '：' + state.memberMsg"
            class="text-3 mt-2"
            showIcon
            :type="getAlertType(state.showData.membershipSuccess)">
            <template v-if="state.showData.membershipSuccess === null" #icon>
              <Icon icon="icon-jinyong"></Icon>
            </template>
          </Alert>
        </template>

        <!-- Test Configuration Form -->
        <Form
          v-show="state.testRecord.id"
          size="small"
          class="mt-2 py-2"
          :colon="false"
          :labelCol="{'span':3}">
          <FormItem class="w-120" :label="t('ldap.labels.username')">
            <Input v-model:value="state.testRecord.server.name" class="bg-white" />
          </FormItem>
          <FormItem
            class="w-120 -mt-2"
            :labelCol="{span: 3}"
            :label="t('ldap.labels.password')">
            <div class="whitespace-nowrap">
              <Input v-model:value="state.testRecord.server.password" type="password" />
              <Button
                size="small"
                type="primary"
                class="ml-2"
                :disabled="!state.testRecord.server.name || !state.testRecord.server.password"
                @click="testLdap">
                {{ t('ldap.buttons.test') }}
              </Button>
            </div>
          </FormItem>
        </Form>
      </div>

      <!-- Close Test Panel Button -->
      <Icon
        v-show="state.testVisible"
        icon="icon-shanchuguanbi"
        class="text-4 ml-4 cursor-pointer leading-7 text-theme-text-hover"
        @click="hideTest">
      </Icon>
    </div>

    <!-- Main Content -->
    <PureCard class="p-3.5 flex-1">
      <!-- Action Buttons -->
      <div class="flex justify-end pb-2">
        <ButtonAuth
          code="LdapAdd"
          type="primary"
          :disabled="state.dataSource.length >= maxlength"
          icon="icon-tianjia"
          @click="addLdapList" />
        <IconRefresh
          class="text-4 mx-2 mt-1.5"
          :loading="state.listSpin"
          @click="getList" />
      </div>

      <!-- LDAP Directories Table -->
      <Table
        :dataSource="state.dataSource"
        :pagination="false"
        :loading="state.listSpin"
        :columns="columns"
        rowKey="id"
        size="small">
        <template #bodyCell="{text, column, record, index }">
          <!-- Status Column -->
          <template v-if="column.dataIndex === 'enabled'">
            <Badge
              :color="text ? 'green' : 'red'"
              :text="text ? t('ldap.status.enabled') : t('ldap.status.disabled')"
              class="whitespace-nowrap" />
          </template>

          <!-- Sequence Column -->
          <template v-if="column.dataIndex === 'sequece'">
            <div class="flex flex-col items-center justify-between h-full">
              <Icon
                v-show="canMoveUp(index)"
                icon="icon-shangla"
                class="flex-1 cursor-pointer hover:text-blue-1"
                @click="updateSequence(record, -1, index)"></Icon>
              <Icon
                v-show="canMoveDown(index, state.dataSource.length)"
                icon="icon-xiala"
                class="flex-1 cursor-pointer hover:text-blue-1"
                @click="updateSequence(record, 1, index)"></Icon>
            </div>
          </template>

          <!-- Created By Name Column -->
          <template v-if="column.dataIndex === 'creator'">
            {{ text || '--' }}
          </template>

          <!-- Operation Column -->
          <template v-if="column.dataIndex === 'operation'">
            <ButtonAuth
              v-if="canModifyDirectory(app)"
              key="setting:1"
              code="LdapModify"
              type="text"
              icon="icon-bianji"
              class="mr-2.5"
              @click="editLdap(record)" />
            <ButtonAuth
              v-if="canSyncDirectory(app)"
              key="setting:2"
              code="LdapSyncData"
              type="text"
              icon="icon-tongbu"
              class="mr-2.5"
              :testLoading="state.syncLoading[index]"
              :disabled="!record.enabled"
              @click="syncLdap(record, index)" />

            <!-- More Actions Dropdown -->
            <Dropdown overlayClassName="ant-dropdown-sm">
              <Icon
                class="cursor-pointer"
                icon="icon-gengduo"
                @click.prevent />
              <template #overlay>
                <Menu>
                  <MenuItem
                    v-if="canTestDirectory(app)"
                    @click="test(record)">
                    <Icon icon="icon-zhihangceshi" class="text-3 mr-0.5" />
                    {{ app.getName('LdapTest') }}
                  </MenuItem>
                  <MenuItem
                    :disabled="!canMoveUp(index)"
                    @click="updateSequence(record, -1, index)">
                    <Icon icon="icon-xiangshang" class="text-3 mr-0.5" />
                    {{ t('ldap.buttons.moveUp') }}
                  </MenuItem>
                  <MenuItem
                    :disabled="!canMoveDown(index, state.dataSource.length)"
                    @click="updateSequence(record, 1, index)">
                    <Icon icon="icon-xiangxia" class="text-3 mr-0.5" />
                    {{ t('ldap.buttons.moveDown') }}
                  </MenuItem>
                  <MenuItem
                    v-if="canDeleteDirectory(app)"
                    @click="deleteConfirm(record, false)">
                    <Icon icon="icon-lajitong" />
                    {{ t('ldap.messages.deleteDirectory') }}
                  </MenuItem>
                  <MenuItem
                    v-if="canDeleteDirectory(app)"
                    @click="deleteConfirm(record, true)">
                    <Icon icon="icon-lajitong" />
                    {{ t('ldap.messages.deleteDirectoryAndData') }}
                  </MenuItem>
                  <MenuItem
                    v-if="canSetDefaultDirectory(app)"
                    @click="enabledLdap(record)">
                    <Icon :icon="record.enabled ? 'icon-jinyong1' : 'icon-qiyong'" />
                    {{ record.enabled ? t('ldap.buttons.disable') : t('ldap.buttons.enable') }}
                  </MenuItem>
                </Menu>
              </template>
            </Dropdown>
          </template>
        </template>
      </Table>
    </PureCard>
  </div>
</template>
