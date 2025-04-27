<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { Alert, Badge, Button, Dropdown, Form, FormItem, Menu, MenuItem, Skeleton } from 'ant-design-vue';
import { ButtonAuth, Hints, Icon, IconRefresh, Input, modal, notification, PureCard, Table } from '@xcan/design';
import { security } from '@xcan/security';
import { useI18n } from 'vue-i18n';


import {userDirectory} from '@/api';

const { t } = useI18n();
const router = useRouter();
const dataSource = ref([]);
const maxlength = 10;
const testLoading = ref(false);
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: '12%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('systemLdap.tabel-title-1'),
    dataIndex: 'name',
    width: '10%'
  },
  {
    title: t('systemLdap.tabel-title-2'),
    dataIndex: 'host',
    key: 'host',
    width: '10%'
  },
  {
    title: t('systemLdap.tabel-title-3'),
    dataIndex: 'port',
    key: 'port',
    width: '5%'
  },
  {
    title: t('systemLdap.tabel-title-4'),
    dataIndex: 'username',
    key: 'username'
  },
  {
    title: '状态',
    dataIndex: 'enabled',
    key: 'enabled',
    width: '5%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: '创建人',
    dataIndex: 'createdByName',
    key: 'createdByName',
    width: '7%'
  },
  {
    title: ' 创建时间',
    dataIndex: 'createdDate',
    key: 'createdDate',
    width: '12%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('systemLdap.tabel-title-5'),
    dataIndex: 'operation',
    width: '14%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  }
];

const listSpin = ref(false);
const r = encodeURIComponent(location.pathname);

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
  dataSource.value = resp.filter((item: { server: any; }) => item.server !== null)
    .map(item => ({ ...item, ...item.server, id: item.id, enabled: item.enabled }));
};

getList();

/* 添加ldap信息 跳转页面 */
const addLdapList = function () {
  router.push({
    path: '/system/ldap/add',
    query: {
      f: 'add',
      r
    }
  });
};

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
const testVisible = ref(false);
const syncLoading = ref<boolean[]>([]);

const syncLdap = async function ({ id }: { id: string }, index) {
  testRecord.value = { server: {} };
  syncLoading.value[index] = true;
  const [error, res] = await userDirectory.syncDirectory(id);
  syncLoading.value[index] = false;
  if (error) {
    return;
  }
  notification.success('同步成功');
  handleTestResult(res.data);
};

const enabledLdap = async function ({ id, enabled }) {
  const [error] = await userDirectory.toggleDirectoryEnabled([{
    enabled: !enabled,
    id: id
  }]);
  if (error) {
    return;
  }

  notification.success(t('systemLdap.mess-4'));
  getList();
};

const deleteConfirm = (record, deleteSync) => {
  modal.confirm({
    centered: true,
    title: t('systemLdap.delete-config'),
    content: t('systemLdap.delete-content', { name: record.name }),
    onOk () {
      deleteLdap(record.id, deleteSync);
    }
  });
};

const deleteLdap = async (id: string, deleteSync): Promise<void> => {
  testLoading.value = true;
  const [error] = await userDirectory.deleteDirectory(id, deleteSync);
  testLoading.value = false;
  if (error) {
    return;
  }

  notification.success(t('systemLdap.mess-5'));
  getList();
};

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
    notification.success('下移成功');
  } else {
    notification.success('上移成功');
  }
  getList();
};

const getType = (flag = null) => {
  if (flag) {
    return 'success';
  }

  if (flag === false) {
    return 'error';
  }

  return 'info';
};

const hideTest = () => {
  testVisible.value = false;
};
const showData = ref<any>({});

const connectMsg = ref('连接服务器: 未执行');

const userMsg = ref('未执行');

const groupMsg = ref('未执行');

const memberMsg = ref('未执行');

const testRecord = ref<any>({
  server: {}
});

const test = (record) => {
  testVisible.value = true;
  testRecord.value = JSON.parse(JSON.stringify(record));
  testRecord.value.server.password = '';
  connectMsg.value = '连接服务器: 未执行';
  userMsg.value = '未执行';
  groupMsg.value = '未执行';
  memberMsg.value = '未执行';
  showData.value = {};
};

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

const handleTestResult = (data) => {
  showData.value = data;
  testVisible.value = true;
  connectMsg.value = showData.value.connectSuccess ? '连接服务器成功。' : '连接服务器失败: ' + showData.value.errorMessage;
  userMsg.value = showData.value.userSuccess === null
    ? '未执行'
    : showData.value.userSuccess
      ? `总数${showData.value.totalUserNum}个，新增${showData.value.addUserNum} 个，更新${showData.value.updateUserNum}个，删除${showData.value.deleteUserNum}个，忽略${showData.value.ignoreUserNum}个。`
      : showData.value.errorMessage;
  groupMsg.value = showData.value.groupSuccess === null
    ? '未执行'
    : showData.value.groupSuccess
      ? `总数${showData.value.totalGroupNum}个，新增${showData.value.addGroupNum} 个，更新${showData.value.updateGroupNum}个，删除${showData.value.deleteGroupNum}个，忽略${showData.value.ignoreGroupNum}个。`
      : showData.value.errorMessage;
  memberMsg.value = showData.value.membershipSuccess === null
    ? '未执行'
    : showData.value.membershipSuccess
      ? `新增${showData.value.addMembershipNum} 个，删除${showData.value.deleteMembershipNum}个。`
      : showData.value.errorMessage;
  testLoading.value = false;
};
</script>
<template>
  <div class="flex flex-col min-h-full">
    <Hints :text="t('systemLdap.mess-1')" class="mb-1" />
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
            :message="'用户：' + userMsg"
            class="text-3  mt-2"
            showIcon
            :type="getType(showData.userSuccess)">
            <template v-if="showData.userSuccess === null" #icon>
              <Icon icon="icon-jinyong"></Icon>
            </template>
          </Alert>
          <Alert
            key="group"
            :message="'组：' + groupMsg"
            class="text-3  mt-2"
            showIcon
            :type="getType(showData.groupSuccess)">
            <template v-if="showData.groupSuccess === null" #icon>
              <Icon icon="icon-jinyong"></Icon>
            </template>
          </Alert>
          <Alert
            key="member"
            :message="'组成员：' + memberMsg"
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
          <FormItem class="w-120" label="用户名">
            <Input v-model:value="testRecord.server.name" class="bg-white" />
          </FormItem>
          <FormItem
            class="w-120 -mt-2"
            :labelCol="{span: 3}"
            label="密码">
            <div class="whitespace-nowrap">
              <Input v-model:value="testRecord.server.password" type="password" />
              <Button
                size="small"
                type="primary"
                class="ml-2"
                :disabled="!testRecord.server.name || !testRecord.server.password"
                @click="testLdap">
                测试
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
              :text="text ? '启用' : '禁用'"
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
                    v-if="security.show('LdapTest')"
                    :disabled="!security.has('LdapTest')"
                    @click="test(record)">
                    <Icon icon="icon-zhihangceshi" class="text-3 mr-0.5" />
                    {{ security.getName('LdapTest') }}
                  </MenuItem>
                  <MenuItem
                    :disabled="index === 0"
                    @click="updateSequece(record, -1, index)">
                    <Icon icon="icon-xiangshang" class="text-3 mr-0.5" />
                    上移
                  </MenuItem>
                  <MenuItem
                    :disabled="index+1 === dataSource.length"
                    @click="updateSequece(record, 1, index)">
                    <Icon icon="icon-xiangxia" class="text-3 mr-0.5" />
                    下移
                  </MenuItem>
                  <MenuItem
                    v-if="security.show('LdapDelete')"
                    :disabled="!security.has('LdapDelete')"
                    @click="deleteConfirm(record, false)">
                    <Icon icon="icon-lajitong" />
                    删除目录
                  </MenuItem>
                  <MenuItem
                    v-if="security.show('LdapDelete')"
                    :disabled="!security.has('LdapDelete')"
                    @click="deleteConfirm(record, true)">
                    <Icon icon="icon-lajitong" />
                    删除目录和数据
                  </MenuItem>
                  <MenuItem
                    v-if="security.show('LdapSetDefault')"
                    :disabled="!security.has('LdapSetDefault')"
                    @click="enabledLdap(record)">
                    <Icon :icon="record.enabled ? 'icon-jinyong1' : 'icon-qiyong'" />
                    {{ record.enabled ? '禁用' : '启用' }}
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
