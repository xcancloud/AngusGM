<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { Dropdown, Menu, MenuItem } from 'ant-design-vue';
import { useI18n } from 'vue-i18n';
import { AsyncComponent, ButtonAuth, Hints, Icon, IconRefresh, modal, PureCard, Table } from '@xcan-angus/vue-ui';
import { app } from '@xcan-angus/infra';

import { email } from '@/api';
import { MailboxService } from './PropsType';

// Lazy load Test component for better performance
const Test = defineAsyncComponent(() => import('@/views/system/email/server/components/test/index.vue'));

const { t } = useI18n();

// Reactive state management
const mailboxServiceList = ref<MailboxService[]>([]);
const loading = ref(false);
const total = ref(0);

// Configuration constants
const MAX_MAILBOX_SERVICES = 10;
const DEFAULT_PAGE_SIZE = 10;

// Pagination and search parameters
const params = ref({
  pageNo: 1,
  pageSize: DEFAULT_PAGE_SIZE,
  filters: []
});

// Computed pagination object for table component
const pagination = computed(() => ({
  current: params.value.pageNo,
  pageSize: params.value.pageSize,
  total: total.value
}));

// Table column definitions with optimized rendering
const columns = [
  {
    title: t('email.columns.id'),
    dataIndex: 'id',
    key: 'id',
    width: '16%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('email.columns.name'),
    dataIndex: 'name',
    key: 'name',
    width: '24%'
  },
  {
    title: t('email.columns.protocol'),
    dataIndex: 'protocol',
    key: 'protocol',
    width: '12%',
    customCell: () => ({ style: 'white-space:nowrap;' })
  },
  {
    title: t('email.columns.sendAddress'),
    dataIndex: 'host',
    key: 'host',
    width: '20%'
  },
  {
    title: t('email.columns.prefix'),
    dataIndex: 'subjectPrefix',
    key: 'subjectPrefix',
    width: '20%'
  },
  {
    title: t('email.columns.operation'),
    dataIndex: 'action',
    key: 'action',
    align: 'center' as const,
    width: '8%'
  }
];

// Modal state management
const visible = ref(false);
const testAddress = ref('');
const testServerId = ref('');

/**
 * Load mailbox service list from API with error handling
 */
const loadEmailServiceList = async (): Promise<void> => {
  if (loading.value) return; // Prevent duplicate requests

  loading.value = true;
  try {
    const [error, { data = { list: [], total: 0 } }] = await email.getServerList(params.value);

    if (error) {
      console.error('Failed to load mailbox service list:', error);
      return;
    }

    mailboxServiceList.value = data.list;
    total.value = +data.total;
  } catch (err) {
    console.error('Unexpected error loading mailbox service list:', err);
  } finally {
    loading.value = false;
  }
};

/**
 * Show delete confirmation modal
 */
const deleteConfig = (id: string, name: string): void => {
  if (loading.value) return;

  modal.confirm({
    centered: true,
    title: t('email.messages.delete'),
    content: t('email.messages.deleteConfirm', { name }),
    onOk: () => confirmDelete(id)
  });
};

/**
 * Execute delete operation after confirmation
 */
const confirmDelete = async (id: string): Promise<void> => {
  loading.value = true;
  try {
    const [error] = await email.deleteServer({ ids: [id] });

    if (error) {
      console.error('Failed to delete mailbox service:', error);
      return;
    }

    // Remove deleted item from local state
    mailboxServiceList.value = mailboxServiceList.value.filter(item => item.id !== id);
  } catch (err) {
    console.error('Unexpected error deleting mailbox service:', err);
  } finally {
    loading.value = false;
  }
};

/**
 * Open test email modal
 */
const testEmail = (id: string, address: string): void => {
  testServerId.value = id;
  testAddress.value = address;
  visible.value = true;
};

/**
 * Toggle default status for mailbox service
 */
const defaultEmail = async (id: string, enabled: boolean): Promise<void> => {
  if (loading.value) return;

  try {
    const [error] = await email.updateServer({ id, enabled: !enabled });

    if (error) {
      console.error('Failed to update mailbox service:', error);
      return;
    }

    // Update local state to reflect changes
    mailboxServiceList.value.forEach(service => {
      if (service.id === id) {
        service.enabled = !enabled;
      } else {
        service.enabled = false; // Only one service can be default
      }
    });
  } catch (err) {
    console.error('Unexpected error updating mailbox service:', err);
  }
};

// Initialize data on component mount
onMounted(() => {
  loadEmailServiceList();
});
</script>

<template>
  <div class="flex flex-col min-h-full">
    <!-- Helpful hints for users -->
    <Hints :text="$t('email.messages.mailboxTip')" class="mb-1" />

    <PureCard class="flex-1 p-3.5">
      <!-- Action buttons and refresh control -->
      <div class="flex justify-end items-center mb-2">
        <ButtonAuth
          code="MailServerAdd"
          href="/system/email/server/add?type=add"
          type="primary"
          size="small"
          icon="icon-tianjia"
          :disabled="mailboxServiceList.length >= MAX_MAILBOX_SERVICES" />
        <IconRefresh
          class="text-4 mx-2"
          :loading="loading"
          @click="loadEmailServiceList" />
      </div>

      <!-- Mailbox services table -->
      <Table
        :loading="loading"
        :dataSource="mailboxServiceList"
        :pagination="pagination"
        :columns="columns"
        rowKey="id"
        size="small">
        <template #bodyCell="{ column, record }">
          <!-- Name column with detail link and default indicator -->
          <template v-if="column.dataIndex === 'name'">
            <RouterLink
              v-if="app.has('MailServerDetail')"
              :to="`/system/email/server/${record.id}?type=detail`"
              class="text-theme-special text-theme-text-hover">
              {{ record.name }}
              <span v-if="record.enabled" class="text-theme-special ml-1">
                ({{ t('email.messages.default') }})
              </span>
            </RouterLink>
            <template v-else>
              {{ record.name }}
              <span v-if="record.enabled" class="text-theme-special ml-1">
                ({{ t('email.messages.default') }})
              </span>
            </template>
          </template>

          <!-- Protocol column with message display -->
          <template v-if="column.dataIndex === 'protocol'">
            {{ record.protocol?.message }}
          </template>

          <!-- Action column with dropdown menu -->
          <template v-if="column.dataIndex === 'action'">
            <Dropdown
              overlayClassName="ant-dropdown-sm"
              placement="bottomRight">
              <Icon
                class="cursor-pointer"
                icon="icon-gengduo"
                @click.prevent />
              <template #overlay>
                <Menu>
                  <!-- Set as default option -->
                  <MenuItem
                    v-if="app.show('MailServerSetDefault')"
                    :disabled="!app.has('MailServerSetDefault')"
                    @click="defaultEmail(record.id, record.enabled)">
                    <template #icon>
                      <Icon :icon="record.enabled ? 'icon-quxiao' : 'icon-sheweimoren'" />
                    </template>
                    {{
                      record.enabled
                        ? app.getName('MailServerSetDefault', 1)
                        : app.getName('MailServerSetDefault', 0)
                    }}
                  </MenuItem>

                  <!-- Edit option -->
                  <MenuItem
                    v-if="app.show('MailServerModify')"
                    :disabled="!app.has('MailServerModify')">
                    <template #icon>
                      <Icon icon="icon-shuxie" />
                    </template>
                    <RouterLink :to="`/system/email/server/edit/${record.id}?type=edit`">
                      {{ app.getName('MailServerModify') }}
                    </RouterLink>
                  </MenuItem>

                  <!-- Delete option -->
                  <MenuItem
                    v-if="app.show('MailServerDelete')"
                    :disabled="!app.has('MailServerDelete')"
                    @click="deleteConfig(record.id, record.name)">
                    <template #icon>
                      <Icon icon="icon-lajitong" />
                    </template>
                    {{ app.getName('MailServerDelete') }}
                  </MenuItem>

                  <!-- Test option -->
                  <MenuItem
                    v-if="app.show('MailServerTest')"
                    :disabled="!app.has('MailServerTest')"
                    @click="testEmail(record.id, record.host)">
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

    <!-- Test email modal -->
    <AsyncComponent :visible="visible">
      <Test
        :id="testServerId"
        v-model:visible="visible"
        :address="testAddress" />
    </AsyncComponent>
  </div>
</template>
