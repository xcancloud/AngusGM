<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { Dropdown, Menu, MenuItem } from 'ant-design-vue';
import { useI18n } from 'vue-i18n';
import { AsyncComponent, ButtonAuth, Hints, Icon, IconRefresh, modal, PureCard, Table } from '@xcan-angus/vue-ui';
import { app } from '@xcan-angus/tools';

import { email } from '@/api';
import { MailboxService } from './PropsType';

const Test = defineAsyncComponent(() => import('@/views/system/email/server/components/test/index.vue'));

const { t } = useI18n();
const mailboxServiceList = ref<MailboxService[]>([]);
const maxlength = 10;

const loading = ref(false);
const params = ref({ pageNo: 1, pageSize: 10, filters: [] });
const total = ref(0);

const pagination = computed(() => {
  return {
    current: params.value.pageNo,
    pageSize: params.value.pageSize,
    total: total.value
  };
});

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: '16%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('name'),
    dataIndex: 'name',
    width: '24%'
  },
  {
    title: t('protocol'),
    dataIndex: 'protocol',
    key: 'protocol',
    width: '12%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('sendAddress'),
    dataIndex: 'host',
    key: 'host',
    width: '20%'
  },
  {
    title: t('prefix'),
    dataIndex: 'subjectPrefix',
    key: 'subjectPrefix',
    width: '20%'
  },
  {
    title: t('operation'),
    dataIndex: 'action',
    align: 'center',
    width: '8%'
  }
];

// 获取邮箱服务器列表
const loadEmailServiceList = async function () {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const [error, { data = { list: [], total: 0 } }] = await email.getServerList(params.value);
  loading.value = false;
  if (error) {
    return;
  }
  mailboxServiceList.value = data.list;
  total.value = +data.total;
};

const deleteConfig = (id: string, name: string) => {
  if (loading.value) {
    return;
  }
  modal.confirm({
    centered: true,
    title: t('删除'),
    content: t('userTip4', { name }),
    onOk () {
      confimOk(id);
    }
  });
};

const confimOk = async function (id) {
  loading.value = true;
  const [error] = await email.deleteServer({ ids: [id] });
  loading.value = false;
  if (error) {
    return;
  }

  mailboxServiceList.value = mailboxServiceList.value.filter(item => item.id !== id);
};

const visible = ref(false);
const testAddress = ref('');
const testServerId = ref('');
// 测试邮箱点击
const testEmail = (id: string, address: string): void => {
  testServerId.value = id;
  testAddress.value = address;
  visible.value = true;
};

// 设为默认
const defaultEmail = async function (id, enabled) {
  if (loading.value) {
    return;
  }
  const [error] = await email.updateServer({ id, enabled: !enabled });
  if (error) {
    return;
  }

  for (let i = 0; i < mailboxServiceList.value.length; i++) {
    if (mailboxServiceList.value[i].id === id) {
      mailboxServiceList.value[i].enabled = !enabled;
    } else {
      mailboxServiceList.value[i].enabled = false;
    }
  }
};

onMounted(() => {
  loadEmailServiceList();
});
</script>
<template>
  <div class="flex flex-col min-h-full">
    <Hints :text="$t('mailboxTip')" class="mb-1" />
    <PureCard class="flex-1 p-3.5">
      <div class="flex justify-end items-center mb-2">
        <ButtonAuth
          code="MailServerAdd"
          href="/system/email/service/add?type=add"
          type="primary"
          size="small"
          icon="icon-tianjia"
          :disabled="mailboxServiceList.length >= maxlength" />
        <IconRefresh
          class="text-4 mx-2"
          :loading="loading"
          @click="loadEmailServiceList" />
      </div>
      <Table
        :loading="loading"
        :dataSource="mailboxServiceList"
        :pagination="pagination"
        :columns="columns"
        rowKey="id"
        size="small">
        <template #bodyCell="{column, record}">
          <template v-if="column.dataIndex === 'name'">
            <RouterLink
              v-if="app.has('MailServerDetail')"
              :to="`/system/email/service/${record.id}?type=detail`"
              class="text-theme-special text-theme-text-hover">
              {{ record.name }}<span v-if="record.enabled" class="text-theme-special ml-1">({{
                t('default')
              }})</span>
            </RouterLink>
            <template v-else>
              {{ record.name }}<span v-if="record.enabled" class="text-theme-special ml-1">({{
                t('default')
              }})</span>
            </template>
          </template>
          <template v-if="column.dataIndex === 'protocol'">
            {{ record.protocol?.message }}
          </template>
          <template v-if="column.dataIndex === 'action'">
            <Dropdown
              overlayClassName="ant-dropdown-sm"
              placement="bottomRight">
              <Icon
                class=" cursor-pointer"
                icon="icon-gengduo"
                @click.prevent />
              <template #overlay>
                <Menu>
                  <MenuItem
                    v-if="app.show('MailServerSetDefault')"
                    :disabled="!app.has('MailServerSetDefault')"
                    @click="defaultEmail(record.id,record.enabled)">
                    <template #icon>
                      <Icon :icon="record.enabled?'icon-quxiao':'icon-sheweimoren'" />
                    </template>
                    {{
                      record.enabled ? app.getName('MailServerSetDefault', 1) : app.getName('MailServerSetDefault', 0)
                    }}
                  </MenuItem>
                  <MenuItem v-if="app.show('MailServerModify')" :disabled="!app.has('MailServerModify')">
                    <template #icon>
                      <Icon icon="icon-shuxie" />
                    </template>
                    <RouterLink :to="`/system/email/service/edit/${record.id}?type=edit`">
                      {{ app.getName('MailServerModify') }}
                    </RouterLink>
                  </MenuItem>
                  <MenuItem
                    v-if="app.show('MailServerDelete')"
                    :disabled="!app.has('MailServerDelete')"
                    @click="deleteConfig(record.id,record.name)">
                    <template #icon>
                      <Icon icon="icon-lajitong" />
                    </template>
                    {{ app.getName('MailServerDelete') }}
                  </MenuItem>
                  <MenuItem
                    v-if="app.show('MailServerTest')"
                    :disabled="!app.has('MailServerTest')"
                    @click="testEmail(record.id,record.host)">
                    <template #icon>
                      <Icon icon="icon-zhihangceshi" />
                    </template>
                    {{ app.getName('MailServerTest') }}
                  </MenuItem>
                </Menu>
              </template>
            </Dropdown>
          </template>
        </template>
      </Table>
    </PureCard>
    <AsyncComponent :visible="visible">
      <Test
        :id="testServerId"
        v-model:visible="visible"
        :address="testAddress" />
    </AsyncComponent>
  </div>
</template>
