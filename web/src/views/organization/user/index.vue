<script setup lang='ts'>
import { onMounted, computed, defineAsyncComponent, ref, reactive } from 'vue';
import { useI18n } from 'vue-i18n';
import { Badge, Dropdown, Menu, MenuItem } from 'ant-design-vue';
import {
  ButtonAuth,
  Hints,
  modal,
  SearchPanel,
  Icon,
  Table,
  PureCard,
  AsyncComponent,
  notification,
  Image,
  IconRefresh,
  IconCount
} from '@xcan-angus/vue-ui';
import { appContext, app, utils, GM } from '@xcan-angus/infra'

import { User, SearchParams, FilterOp } from './PropsType';
import { user } from '@/api';

const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));
const UpdatePasswd = defineAsyncComponent(() => import('@/views/organization/user/components/password/index.vue'));
const Lock = defineAsyncComponent(() => import('@/components/Lock/index.vue'));

const { t } = useI18n();
const showCount = ref(true);
const loading = ref(false);
const params = ref<SearchParams>({ pageNo: 1, pageSize: 10, filters: [], fullTextSearch: true });
const total = ref(0);
const userList = ref<User[]>([]);
const state = reactive<{
  updatePasswdVisible: boolean,
  lockModalVisible: boolean,
  currentUserId: string | undefined,
}>({
  updatePasswdVisible: false,
  lockModalVisible: false,
  currentUserId: undefined
});

const pagination = computed(() => {
  return {
    current: params.value.pageNo,
    pageSize: params.value.pageSize,
    total: total.value
  };
});

const init = async () => {
  disabled.value = true;
  await loadUserList();
  disabled.value = false;
};

const loadUserList = async (): Promise<void> => {
  loading.value = true;
  const [error, { data = { list: [], total: 0 } }] = await user.getUserList(params.value);
  loading.value = false;
  if (error) {
    return;
  }
  userList.value = data.list;
  total.value = +data.total;
};

// 禁用刷新转圈效果
const disabled = ref(false);
const searchChange = async (data: { key: string; value: string; op: FilterOp; }[]) => {
  params.value.pageNo = 1;
  params.value.filters = data;
  disabled.value = true;
  await loadUserList();
  disabled.value = false;
};

const tableChange = async (_pagination, _filters, sorter) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  params.value.orderBy = sorter.orderBy;
  params.value.orderSort = sorter.orderSort;
  disabled.value = true;
  await loadUserList();
  disabled.value = false;
};

// 设置或取消用户系统管理员弹框
const setAdminConfirm = (id: string, name: string, sysAdmin: boolean) => {
  modal.confirm({
    centered: true,
    title: sysAdmin ? t('cancelAdministrator') : t('setupAdministrator'),
    content: sysAdmin ? t('userTip2', { name }) : t('userTip3', { name }),
    async onOk () {
      await updateSysAdmin(id, sysAdmin);
    }
  });
};

// 修改用户系统身份请求
const updateSysAdmin = async (id: string, sysAdmin: boolean) => {
  const params = { id: id, sysAdmin: !sysAdmin };
  const [error] = await user.updateUserSysAdmin(params);
  if (error) {
    return;
  }
  notification.success('修改成功');
  disabled.value = true;
  await loadUserList();
  disabled.value = false;
};

// 删除用户弹框
const delUserConfirm = (id: string, name: string) => {
  modal.confirm({
    centered: true,
    title: t('delUser'),
    content: t('userTip4', { name }),
    async onOk () {
      await delUser(id);
    }
  });
};

// 删除用户请求
const delUser = async (id: string) => {
  const [error] = await user.deleteUser([id]);
  if (error) {
    return;
  }
  notification.success('删除成功');
  params.value.pageNo = utils.getCurrentPage(params.value.pageNo as number, params.value.pageSize as number, total.value);
  disabled.value = true;
  await loadUserList();
  disabled.value = false;
};

// 禁用启用用户弹框
const updateStatusConfirm = (id: string, name: string, enabled: boolean) => {
  modal.confirm({
    centered: true,
    title: enabled ? t('disable') : t('enable'),
    content: enabled ? `确定禁用【${name}】吗？` : `确定启用【${name}】吗？`,
    async onOk () {
      await updateStatus(id, enabled);
    }
  });
};

// 禁用启用用户请求
const updateStatus = async (id: string, enabled: boolean) => {
  const params = [{ id: id, enabled: !enabled }];
  const [error] = await user.toggleUserEnabled(params);
  if (error) {
    return;
  }
  notification.success(enabled ? '禁用成功' : '启用成功');
  disabled.value = true;
  await loadUserList();
  disabled.value = false;
};

// 打开锁定
const openLockedModal = (id: string) => {
  state.currentUserId = id;
  state.lockModalVisible = true;
};

// 关闭锁定
const closeLockModal = () => {
  state.lockModalVisible = false;
};

// 保存锁定修改
const saveLock = async () => {
  state.lockModalVisible = false;
  disabled.value = true;
  await loadUserList();
  disabled.value = false;
};

// 解锁用户
const unlock = (id: string, name: string) => {
  modal.confirm({
    centered: true,
    title: t('unlockUsers'),
    content: t('userTip7', { name }),
    async onOk () {
      const [error] = await user.toggleUserLocked({ id: id, locked: false });
      if (error) {
        return;
      }
      notification.success('解锁成功');
      disabled.value = true;
      await loadUserList();
      disabled.value = false;
    }
  });
};

// 重置密码弹框
const openUpdatePasswdModal = (id: string) => {
  state.currentUserId = id;
  state.updatePasswdVisible = true;
};

// 取消重置密码
const closeUpdatePasswdModal = () => {
  state.updatePasswdVisible = false;
};

const handleRefresh = () => {
  if (loading.value) {
    return;
  }
  loadUserList();
};

onMounted(() => {
  init();
});

const searchOptions = ref([
  {
    placeholder: t('查询用户ID'),
    valueKey: 'id',
    type: 'input',
    op: 'EQUAL',
    allowClear: true
  },
  {
    placeholder: t('userPlaceholder1'),
    valueKey: 'fullName',
    type: 'input',
    allowClear: true
  },
  {
    placeholder: t('selectState'),
    valueKey: 'enabled',
    type: 'select-enum',
    enumKey: 'Enabled',
    allowClear: true
  },
  {
    placeholder: t('selectSource'),
    valueKey: 'source',
    type: 'select-enum',
    enumKey: 'UserSource',
    allowClear: true
  },
  {
    valueKey: 'createdDate',
    type: 'date-range',
    allowClear: true
  },
  {
    placeholder: '选择或查询标签',
    valueKey: 'tagId',
    type: 'select',
    action: `${GM}/org/tag`,
    fieldNames: { label: 'name', value: 'id' },
    showSearch: true,
    allowClear: true,
    lazy: false
  }
]);

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: '11%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('name2'),
    dataIndex: 'fullName',
    ellipsis: true
  },
  {
    title: t('userName'),
    dataIndex: 'username',
    width: '12%'
  },
  {
    title: t('status'),
    dataIndex: 'enabled',
    width: '5%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('source'),
    dataIndex: 'source',
    width: '6%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('lockedStatus'),
    dataIndex: 'locked',
    width: '6%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('onlineStatus'),
    dataIndex: 'online',
    width: '6%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('mobileNumber'),
    dataIndex: 'mobile',
    groupName: 'contact',
    width: '11%',
    customRender: ({ text }): string => text || '--',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('landline'),
    dataIndex: 'landline',
    groupName: 'contact',
    customRender: ({ text }): string => text || '--',
    hide: true,
    width: '11%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('email'),
    dataIndex: 'email',
    groupName: 'contact',
    customRender: ({ text }): string => text || '--',
    hide: true,
    width: '15%'
  },
  {
    title: t('systemIdentity'),
    dataIndex: 'sysAdmin',
    groupName: 'auth',
    width: '8%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('sectorIdentity'),
    dataIndex: 'deptHead',
    groupName: 'auth',
    hide: true,
    width: '8%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('position'),
    dataIndex: 'title',
    groupName: 'auth',
    hide: true,
    width: '8%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('joinTime'),
    sorter: true,
    dataIndex: 'createdDate',
    groupName: 'date',
    width: '11%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('recentlyOnline'),
    dataIndex: 'onlineDate',
    groupName: 'date',
    hide: true,
    width: '11%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('添加人'),
    dataIndex: 'createdByName',
    groupName: 'date',
    hide: true,
    width: '11%',
    ellipsis: true,
    customRender: ({ text }): string => text || '--'
  },
  {
    title: t('最后修改人'),
    dataIndex: 'lastModifiedByName',
    groupName: 'date',
    hide: true,
    width: '11%',
    ellipsis: true,
    customRender: ({ text }): string => text || '--'
  },
  {
    title: t('最后修改时间'),
    dataIndex: 'lastModifiedDate',
    groupName: 'date',
    customRender: ({ text }): string => text || '--',
    hide: true,
    width: '11%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('operation'),
    dataIndex: 'action',
    width: 160,
    align: 'center'
  }
];

const getOperationPermissions = (sysAdmin: boolean): boolean => {
  return !appContext.isSysAdmin() && sysAdmin;
};
</script>
<template>
  <PureCard class="w-full min-h-full p-3.5">
    <Statistics
      :visible="showCount"
      :barTitle="t('user') "
      resource="User"
      dateType="YEAR"
      :geteway="GM" />
    <Hints :text="t('userTip1')" class="mb-1" />
    <div class="flex items-start my-2 justify-between">
      <SearchPanel
        class="flex-1 mr-2"
        :options="searchOptions"
        @change="searchChange" />
      <div class="flex items-center space-x-2">
        <ButtonAuth
          code="UserAdd"
          type="primary"
          href="/organization/user/add?source=home"
          icon="icon-tianjia" />
        <IconCount v-model:value="showCount" />
        <IconRefresh
          :loading="loading"
          :disabled="disabled"
          @click="handleRefresh" />
      </div>
    </div>
    <Table
      :dataSource="userList"
      :loading="loading"
      :columns="columns"
      :pagination="pagination"
      rowKey="id"
      size="small"
      @change="tableChange">
      <template #bodyCell="{ column,text, record }">
        <template v-if="column.dataIndex === 'fullName'">
          <div class="flex items-center">
            <Image
              :src="record.avatar"
              type="avatar"
              class="w-5 h-5 rounded-full inline-block mr-1"
              style="min-width: 20px;" />
            <template v-if="app.has('UserDetail')">
              <RouterLink
                :to="`/organization/user/${record.id}`"
                class="text-theme-special text-theme-text-hover cursor-pointer">
                {{ text }}
              </RouterLink>
            </template>
            <span
              v-else
              :title="text"
              class="cursor-pointer">{{ text }}</span>
          </div>
        </template>
        <template v-if="column.dataIndex === 'enabled'">
          <Badge
            v-if="record.enabled"
            status="success"
            :text="t('enable')" />
          <Badge
            v-else
            status="error"
            :text="t('disable')" />
        </template>
        <template v-if="column.dataIndex === 'sysAdmin'">
          {{ record.sysAdmin ? t('systemAdministrator') : t('generalUsers') }}
        </template>
        <template v-if="column.dataIndex === 'deptHead'">
          {{ record.deptHead ? t('responsiblePerson') : t('generalUsers') }}
        </template>
        <template v-if="column.dataIndex === 'locked'">
          <Badge
            v-if="record.locked"
            status="error"
            :text="t('locked')" />
          <Badge
            v-else
            status="success"
            :text="t('unlocked')" />
        </template>
        <template v-if="column.dataIndex === 'online'">
          <Badge
            v-if="record.online"
            status="success"
            :text="t('onLine')" />
          <Badge
            v-else
            status="error"
            :text="t('offLine')" />
        </template>
        <template v-if="column.dataIndex === 'source'">
          {{ record.source?.message }}
        </template>
        <template v-if="column.dataIndex === 'action'">
          <div class="flex items-center space-x-2.5">
            <ButtonAuth
              code="UserModify"
              type="text"
              :href="`/organization/user/edit/${record.id}?source=home`"
              :disabled="getOperationPermissions(record.sysAdmin)"
              icon="icon-shuxie" />
            <ButtonAuth
              code="ResetPassword"
              type="text"
              :disabled="getOperationPermissions(record.sysAdmin)"
              icon="icon-zhongzhimima"
              @click="openUpdatePasswdModal(record.id)" />
            <Dropdown
              placement="bottomRight"
              overlayClassName="ant-dropdown-sm">
              <Icon icon="icon-gengduo" class="cursor-pointer outline-none" />
              <template #overlay>
                <Menu class="text-3.5 leading-3.5 font-normal">
                  <MenuItem
                    v-if="app.show('UserEnable')"
                    :disabled="getOperationPermissions(record.sysAdmin)||!app.has('UserEnable')"
                    @click="updateStatusConfirm(record.id,record.fullName,record.enabled)">
                    <template #icon>
                      <Icon :icon="record.enabled?'icon-jinyong1':'icon-qiyong'" />
                    </template>
                    {{ record.enabled?app.getName('UserEnable', 1):app.getName('UserEnable',0) }}
                  </MenuItem>
                  <MenuItem
                    v-if="app.show('UserDelete')"
                    :disabled="getOperationPermissions(record.sysAdmin)||!app.has('UserDelete')"
                    @click="delUserConfirm(record.id,record.fullName)">
                    <template #icon>
                      <Icon icon="icon-lajitong" />
                    </template>
                    {{ app.getName('UserDelete') }}
                  </MenuItem>
                  <MenuItem
                    v-if="!record.locked && app.show('LockingUser')"
                    :disabled="getOperationPermissions(record.sysAdmin)||!app.has('LockingUser')"
                    @click="openLockedModal(record.id)">
                    <template #icon>
                      <Icon icon="icon-lock" />
                    </template>
                    {{ app.getName('LockingUser', 0) }}
                  </MenuItem>
                  <MenuItem
                    v-if="record.locked && app.show('LockingUser')"
                    :disabled="getOperationPermissions(record.sysAdmin)||!app.has('LockingUser')"
                    @click="unlock(record.id,record.fullName)">
                    <template #icon>
                      <Icon icon="icon-kaibiaojiemi" />
                    </template>
                    {{ app.getName('LockingUser', 1) }}
                  </MenuItem>
                  <MenuItem
                    v-if="appContext.isSysAdmin() && app.show('SetIdentity')"
                    :disabled="getOperationPermissions(record.sysAdmin)||!app.has('SetIdentity')"
                    @click="setAdminConfirm(record.id,record.fullName,record.sysAdmin)">
                    <template #icon>
                      <Icon :icon="record.sysAdmin?'icon-yonghu':'icon-guanliyuan'" />
                    </template>
                    {{ record.sysAdmin?t('setCommonBtn'):t('setAdminBtn') }}
                  </MenuItem>
                </Menu>
              </template>
            </Dropdown>
          </div>
        </template>
      </template>
    </Table>
  </PureCard>
  <AsyncComponent :visible="state.updatePasswdVisible">
    <UpdatePasswd
      v-if="state.updatePasswdVisible"
      :visible="state.updatePasswdVisible"
      :userId="state.currentUserId"
      @cancel="closeUpdatePasswdModal" />
  </AsyncComponent>
  <AsyncComponent :visible="state.lockModalVisible">
    <Lock
      v-if="state.lockModalVisible"
      :id="state.currentUserId"
      :visible="state.lockModalVisible"
      :title="t('lockUser')"
      :tip="t('userTip8')"
      width="550px"
      :action="`${GM}/user/locked`"
      @cancel="closeLockModal"
      @save="saveLock()" />
  </AsyncComponent>
</template>
