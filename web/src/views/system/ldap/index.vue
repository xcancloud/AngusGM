<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { Alert, Badge, Button, Dropdown, Form, FormItem, Menu, MenuItem, Skeleton } from 'ant-design-vue';
import { ButtonAuth, Hints, Icon, IconRefresh, Input, modal, notification, PureCard, Table } from '@xcan-angus/vue-ui';
import { app } from '@xcan-angus/infra';
import { useI18n } from 'vue-i18n';

import { userDirectory } from '@/api';

const { t } = useI18n();
const router = useRouter();

// Reactive data for LDAP directory management
const dataSource = ref([]);
const maxlength = 10; // Maximum number of LDAP directories allowed
const testLoading = ref(false);

// Table columns configuration for LDAP directory list
const columns = [
  {
    title: t('ldap.columns.id'),
    dataIndex: 'id',
    width: '12%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('ldap.columns.name'),
    dataIndex: 'name',
    width: '10%'
  },
  {
    title: t('ldap.columns.host'),
    dataIndex: 'host',
    key: 'host',
    width: '10%'
  },
  {
    title: t('ldap.columns.port'),
    dataIndex: 'port',
    key: 'port',
    width: '5%'
  },
  {
    title: t('ldap.columns.username'),
    dataIndex: 'username',
    key: 'username'
  },
  {
    title: t('ldap.columns.status'),
    dataIndex: 'enabled',
    key: 'enabled',
    width: '5%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('ldap.columns.createdByName'),
    dataIndex: 'createdByName',
    key: 'createdByName',
    width: '7%'
  },
  {
    title: t('ldap.columns.createdDate'),
    dataIndex: 'createdDate',
    key: 'createdDate',
    width: '12%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('ldap.columns.operation'),
    dataIndex: 'operation',
    width: '14%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  }
];

// Loading states and navigation
const listSpin = ref(false);
const r = encodeURIComponent(location.pathname);

/**
 * Fetch LDAP directory list from API
 * Filters out items with null server and maps server properties
 */
const getList = async function () {
  if (listSpin.value) {
    return;
  }
  listSpin.value = true;
  const [error, res] = await userDirectory.getDirectories();
  listSpin.value = false;
  if (error) {
    return;
  }

  const resp = res.data;
  // Filter out invalid server entries and merge server properties
  dataSource.value = resp.filter((item: { server: any; }) => item.server !== null)
    .map(item => ({ ...item, ...item.server, id: item.id, enabled: item.enabled }));
};

// Initialize directory list on component mount
getList();

/**
 * Navigate to add LDAP directory page
 */
const addLdapList = function () {
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
 * @param {Object} params - Object containing directory id
 * @param {string} params.id - Directory ID to edit
 */
const editLdap = function ({ id }: { id: string }) {
  router.push({
    path: '/system/ldap/detail',
    query: {
      f: 'edit',
      q: id,
      r
    }
  });
};

// Test modal visibility and sync loading states
const testVisible = ref(false);
const syncLoading = ref<boolean[]>([]);

/**
 * Synchronize LDAP directory data
 * @param {Object} params - Object containing directory id
 * @param {string} params.id - Directory ID to sync
 * @param {number} index - Index in the data source array
 */
const syncLdap = async function ({ id }: { id: string }, index) {
  testRecord.value = { server: {} };
  syncLoading.value[index] = true;
  const [error, res] = await userDirectory.syncDirectory(id);
  syncLoading.value[index] = false;
  if (error) {
    return;
  }
  notification.success(t('ldap.messages.syncSuccess'));
  handleTestResult(res.data);
};

/**
 * Toggle LDAP directory enabled/disabled status
 * @param {Object} params - Object containing directory id and enabled status
 * @param {string} params.id - Directory ID
 * @param {boolean} params.enabled - Current enabled status
 */
const enabledLdap = async function ({ id, enabled }) {
  const [error] = await userDirectory.toggleDirectoryEnabled([{
    enabled: !enabled,
    id: id
  }]);
  if (error) {
    return;
  }

  notification.success(t('ldap.messages.statusUpdateSuccess'));
  getList();
};

/**
 * Show delete confirmation modal
 * @param {Object} record - Directory record to delete
 * @param {boolean} deleteSync - Whether to delete sync data
 */
const deleteConfirm = (record, deleteSync) => {
  modal.confirm({
    centered: true,
    title: t('ldap.delete-config'),
    content: t('ldap.delete-content', { name: record.name }),
    onOk () {
      deleteLdap(record.id, deleteSync);
    }
  });
};

/**
 * Delete LDAP directory
 * @param {string} id - Directory ID to delete
 * @param {boolean} deleteSync - Whether to delete sync data
 */
const deleteLdap = async (id: string, deleteSync): Promise<void> => {
  testLoading.value = true;
  const [error] = await userDirectory.deleteDirectory(id, deleteSync);
  testLoading.value = false;
  if (error) {
    return;
  }

  notification.success(t('ldap.messages.ldapDataDeleted'));
  getList();
};

/**
 * Update directory sequence order (move up/down)
 * @param {Object} item - Directory item to move
 * @param {number} sequence - Direction (-1 for up, 1 for down)
 * @param {number} index - Current index in array
 */
const updateSequece = async (item, sequence: number, index) => {
  const otherItem = dataSource.value[index + sequence];
  const [error] = await userDirectory.updateDirectorySequence([{ id: item.id, sequence: index + sequence + 1 }, {
    id: otherItem.id,
    sequence: index + 1
  }]);
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
 * Get alert type based on success flag
 * @param {boolean|null} flag - Success flag
 * @returns {string} Alert type: 'success', 'error', or 'info'
 */
const getType = (flag = null) => {
  if (flag) {
    return 'success';
  }

  if (flag === false) {
    return 'error';
  }

  return 'info';
};

/**
 * Hide test modal
 */
const hideTest = () => {
  testVisible.value = false;
};

// Test result display data
const showData = ref<any>({});

// Test result messages for different operations
const connectMsg = ref(t('ldap.messages.connectServer') + ': ' + t('ldap.messages.notExecuted'));
const userMsg = ref(t('ldap.messages.notExecuted'));
const groupMsg = ref(t('ldap.messages.notExecuted'));
const memberMsg = ref(t('ldap.messages.notExecuted'));

// Test record for LDAP connection testing
const testRecord = ref<any>({
  server: {}
});

/**
 * Show test modal and prepare test data
 * @param {Object} record - Directory record to test
 */
const test = (record) => {
  testVisible.value = true;
  testRecord.value = JSON.parse(JSON.stringify(record));
  testRecord.value.server.password = '';
  // Reset test messages
  connectMsg.value = t('ldap.messages.connectServer') + ': ' + t('ldap.messages.notExecuted');
  userMsg.value = t('ldap.messages.notExecuted');
  groupMsg.value = t('ldap.messages.notExecuted');
  memberMsg.value = t('ldap.messages.notExecuted');
  showData.value = {};
};

/**
 * Execute LDAP connection test
 */
const testLdap = async () => {
  testLoading.value = true;
  const [error, res] = await userDirectory.testDirectory(testRecord.value);
  testLoading.value = false;
  if (error) {
    testLoading.value = false;
    return;
  }
  testVisible.value = true;
  handleTestResult(res.data);
};

/**
 * Process and display test results
 * @param {Object} data - Test result data from API
 */
const handleTestResult = (data) => {
  showData.value = data;
  testVisible.value = true;

  // Update connection message
  connectMsg.value = showData.value.connectSuccess ?
    t('ldap.messages.connectSuccess') :
    t('ldap.messages.connectFailed') + ': ' + showData.value.errorMessage;

  // Update user sync message
  userMsg.value = showData.value.userSuccess === null
    ? t('ldap.messages.notExecuted')
    : showData.value.userSuccess
      ? t('ldap.messages.totalCount', { count: showData.value.totalUserNum }) + '，' +
        t('ldap.messages.newCount', { count: showData.value.addUserNum }) + '，' +
        t('ldap.messages.updateCount', { count: showData.value.updateUserNum }) + '，' +
        t('ldap.messages.deleteCount', { count: showData.value.deleteUserNum }) + '，' +
        t('ldap.messages.ignoreCount', { count: showData.value.ignoreUserNum }) + '。'
      : showData.value.errorMessage;

  // Update group sync message
  groupMsg.value = showData.value.groupSuccess === null
    ? t('ldap.messages.notExecuted')
    : showData.value.groupSuccess
      ? t('ldap.messages.totalCount', { count: showData.value.totalGroupNum }) + '，' +
        t('ldap.messages.newCount', { count: showData.value.addGroupNum }) + '，' +
        t('ldap.messages.updateCount', { count: showData.value.updateGroupNum }) + '，' +
        t('ldap.messages.deleteCount', { count: showData.value.deleteGroupNum }) + '，' +
        t('ldap.messages.ignoreCount', { count: showData.value.ignoreGroupNum }) + '。'
      : showData.value.errorMessage;

  // Update membership sync message
  memberMsg.value = showData.value.membershipSuccess === null
    ? t('ldap.messages.notExecuted')
    : showData.value.membershipSuccess
      ? t('ldap.messages.newCount', { count: showData.value.addMembershipNum }) + '，' +
        t('ldap.messages.deleteCount', { count: showData.value.deleteMembershipNum }) + '。'
      : showData.value.errorMessage;

  testLoading.value = false;
};
</script>
<template>
  <div class="flex flex-col min-h-full">
    <Hints :text="t('ldap.messages.description')" class="mb-1" />
    <div class="flex pr-3">
      <div v-show="testVisible" class="flex-1">
        <Skeleton
          v-if="testLoading"
          active
          :paragraph="{rows: 4}"
          :title="false" />
        <template v-else>
          <Alert
            key="connect"
            class="text-3 min-w-50"
            showIcon
            :message="connectMsg"
            :type="getType(showData.connectSuccess)">
            <template v-if="showData.connectSuccess === null" #icon>
              <Icon icon="icon-jinyong"></Icon>
            </template>
          </Alert>
          <Alert
            key="user"
            :message="t('ldap.messages.user') + '：' + userMsg"
            class="text-3  mt-2"
            showIcon
            :type="getType(showData.userSuccess)">
            <template v-if="showData.userSuccess === null" #icon>
              <Icon icon="icon-jinyong"></Icon>
            </template>
          </Alert>
          <Alert
            key="group"
            :message="t('ldap.messages.group') + '：' + groupMsg"
            class="text-3  mt-2"
            showIcon
            :type="getType(showData.groupSuccess)">
            <template v-if="showData.groupSuccess === null" #icon>
              <Icon icon="icon-jinyong"></Icon>
            </template>
          </Alert>
          <Alert
            key="member"
            :message="t('ldap.messages.member') + '：' + memberMsg"
            class="text-3 mt-2"
            showIcon
            :type="getType(showData.membershipSuccess)">
            <template v-if="showData.membershipSuccess === null" #icon>
              <Icon icon="icon-jinyong"></Icon>
            </template>
          </Alert>
        </template>
        <Form
          v-show="testRecord.id"
          size="small"
          class="mt-2 py-2"
          :colon="false"
          :labelCol="{'span':3}">
          <FormItem class="w-120" :label="t('ldap.labels.username')">
            <Input v-model:value="testRecord.server.name" class="bg-white" />
          </FormItem>
          <FormItem
            class="w-120 -mt-2"
            :labelCol="{span: 3}"
            :label="t('ldap.labels.password')">
            <div class="whitespace-nowrap">
              <Input v-model:value="testRecord.server.password" type="password" />
              <Button
                size="small"
                type="primary"
                class="ml-2"
                :disabled="!testRecord.server.name || !testRecord.server.password"
                @click="testLdap">
                {{ t('ldap.buttons.test') }}
              </Button>
            </div>
          </FormItem>
        </Form>
      </div>
      <Icon
        v-show="testVisible"
        icon="icon-shanchuguanbi"
        class="text-4 ml-4 cursor-pointer leading-7 text-theme-text-hover"
        @click="hideTest">
      </Icon>
    </div>
    <PureCard class="p-3.5 flex-1">
      <div class="flex justify-end pb-2">
        <ButtonAuth
          code="LdapAdd"
          type="primary"
          :disabled="dataSource.length >= maxlength"
          icon="icon-tianjia"
          @click="addLdapList" />
        <IconRefresh
          class="text-4 mx-2 mt-1.5"
          :loading="listSpin"
          @click="getList" />
      </div>
      <Table
        :dataSource="dataSource"
        :pagination="false"
        :loading="listSpin"
        :columns="columns"
        rowKey="id"
        size="small">
        <template #bodyCell="{text, column, record, index }">
          <template v-if="column.dataIndex === 'enabled'">
            <Badge
              :color="text ? 'green' : 'red'"
              :text="text ? t('ldap.status.enabled') : t('ldap.status.disabled')"
              class="whitespace-nowrap" />
          </template>
          <template v-if="column.dataIndex === 'sequece'">
            <div class="flex flex-col items-center justify-between h-full">
              <Icon
                v-show="index > 0"
                icon="icon-shangla"
                class="flex-1 cursor-pointer hover:text-blue-1"
                @click="updateSequece(record, -1, index)"></Icon>
              <Icon
                v-show="index + 1 < dataSource.length"
                icon="icon-xiala"
                class="flex-1 cursor-pointer hover:text-blue-1"
                @click="updateSequece(record, 1, index)"></Icon>
            </div>
          </template>
          <template v-if="column.dataIndex === 'createdByName'">
            {{ text || '--' }}
          </template>
          <template v-if="column.dataIndex === 'operation'">
            <ButtonAuth
              key="setting:1"
              code="LdapModify"
              type="text"
              icon="icon-bianji"
              class="mr-2.5"
              @click="editLdap(record)" />
            <ButtonAuth
              key="setting:2"
              code="LdapSyncData"
              type="text"
              icon="icon-tongbu"
              class="mr-2.5"
              :testLoading="syncLoading[index]"
              :disabled="!record.enabled"
              @click="syncLdap(record, index)" />
            <!-- <ButtonAuth
              key="setting:3"
              code="LdapTest"
              type="link"
              icon="icon-zhihangceshi"
              class="mr-2.5"
              @click="test(record)" /> -->
            <!-- <Button
              key="setting:7"
              :disabled="index === 0"
              size="small"
              type="link"
              class="px-0 mr-2.5"
              @click="updateSequece(record, -1, index)">
              <Icon icon="icon-xiangshang" class="text-3 mr-0.5" />
              上移
            </Button>
            <Button
              key="setting:8"
              :disabled="index+1 === dataSource.length"
              size="small"
              type="link"
              class="px-0 mr-2.5"
              @click="updateSequece(record, 1, index)">
              <Icon icon="icon-xiangxia" class="text-3 mr-0.5" />
              下移
            </Button> -->
            <Dropdown overlayClassName="ant-dropdown-sm">
              <Icon
                class="cursor-pointer"
                icon="icon-gengduo"
                @click.prevent />
              <template #overlay>
                <Menu>
                  <MenuItem
                    v-if="app.show('LdapTest')"
                    :disabled="!app.has('LdapTest')"
                    @click="test(record)">
                    <Icon icon="icon-zhihangceshi" class="text-3 mr-0.5" />
                    {{ app.getName('LdapTest') }}
                  </MenuItem>
                  <MenuItem
                    :disabled="index === 0"
                    @click="updateSequece(record, -1, index)">
                    <Icon icon="icon-xiangshang" class="text-3 mr-0.5" />
                    {{ t('ldap.buttons.moveUp') }}
                  </MenuItem>
                  <MenuItem
                    :disabled="index+1 === dataSource.length"
                    @click="updateSequece(record, 1, index)">
                    <Icon icon="icon-xiangxia" class="text-3 mr-0.5" />
                    {{ t('ldap.buttons.moveDown') }}
                  </MenuItem>
                                      <MenuItem
                      v-if="app.show('LdapDelete')"
                      :disabled="!app.has('LdapDelete')"
                      @click="deleteConfirm(record, false)">
                      <Icon icon="icon-lajitong" />
                      {{ t('ldap.messages.deleteDirectory') }}
                    </MenuItem>
                  <MenuItem
                    v-if="app.show('LdapDelete')"
                    :disabled="!app.has('LdapDelete')"
                    @click="deleteConfirm(record, true)">
                    <Icon icon="icon-lajitong" />
                    {{ t('ldap.messages.deleteDirectoryAndData') }}
                  </MenuItem>
                  <MenuItem
                    v-if="app.show('LdapSetDefault')"
                    :disabled="!app.has('LdapSetDefault')"
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
