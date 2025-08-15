<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, reactive, ref } from 'vue';
import { Dropdown, Menu, MenuItem } from 'ant-design-vue';
import { useI18n } from 'vue-i18n';
import { AsyncComponent, ButtonAuth, Hints, Icon, IconRefresh, modal, PureCard, Table } from '@xcan-angus/vue-ui';
import { app } from '@xcan-angus/infra';

import { email } from '@/api';
import { EmailServerState } from './types';
import {
  createTableColumns, createInitialPaginationParams, createPaginationObject, createDeleteServerRequest,
  createUpdateServerRequest, processServerListResponse, removeServerFromState, setSingleDefaultServer,
  canSetAsDefault, canEditServer, canDeleteServer, canTestServer, getDefaultStatusText, formatProtocolDisplay
} from './utils';

// Lazy load Test component for better performance
const Test = defineAsyncComponent(() => import('@/views/system/email/server/test/index.vue'));

const { t } = useI18n();

// Configuration constants
const MAX_MAILBOX_SERVICES = 10;

// Component state management
const state = reactive<EmailServerState>({
  mailboxServiceList: [],
  loading: false,
  total: 0,
  params: createInitialPaginationParams(),
  visible: false,
  testAddress: '',
  testServerId: ''
});

// Table columns configuration
const columns = ref(createTableColumns(t));

// Computed pagination object for table component
const pagination = computed(() =>
  createPaginationObject(state.params, state.total)
);

/**
 * Load mailbox service list from API with error handling
 */
const loadEmailServiceList = async (): Promise<void> => {
  if (state.loading) return; // Prevent duplicate requests

  try {
    state.loading = true;
    const [error, response] = await email.getServerList(state.params);

    if (error) {
      console.error('Failed to load mailbox service list:', error);
      return;
    }

    const { list, total } = processServerListResponse(response);
    state.mailboxServiceList = list;
    state.total = total;
  } catch (err) {
    console.error('Unexpected error loading mailbox service list:', err);
  } finally {
    state.loading = false;
  }
};

/**
 * Show delete confirmation modal
 */
const deleteConfig = (id: string, name: string): void => {
  if (state.loading) return;

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
  try {
    state.loading = true;
    const [error] = await email.deleteServer(createDeleteServerRequest(id));

    if (error) {
      console.error('Failed to delete mailbox service:', error);
      return;
    }

    // Remove deleted item from local state
    state.mailboxServiceList = removeServerFromState(state.mailboxServiceList, id);
  } catch (err) {
    console.error('Unexpected error deleting mailbox service:', err);
  } finally {
    state.loading = false;
  }
};

/**
 * Open test email modal
 */
const testEmail = (id: string, address: string): void => {
  state.testServerId = id;
  state.testAddress = address;
  state.visible = true;
};

/**
 * Toggle default status for mailbox service
 */
const defaultEmail = async (id: string, enabled: boolean): Promise<void> => {
  if (state.loading) return;

  try {
    const [error] = await email.updateServer(createUpdateServerRequest(id, enabled));

    if (error) {
      console.error('Failed to update mailbox service:', error);
      return;
    }

    // Update local state to reflect changes
    state.mailboxServiceList = setSingleDefaultServer(state.mailboxServiceList, id);
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
          :disabled="state.mailboxServiceList.length >= MAX_MAILBOX_SERVICES" />
        <IconRefresh
          class="text-4 mx-2"
          :loading="state.loading"
          @click="loadEmailServiceList" />
      </div>

      <!-- Mailbox services table -->
      <Table
        :loading="state.loading"
        :dataSource="state.mailboxServiceList"
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
                {{ getDefaultStatusText(record.enabled, t) }}
              </span>
            </RouterLink>
            <template v-else>
              {{ record.name }}
              <span v-if="record.enabled" class="text-theme-special ml-1">
                {{ getDefaultStatusText(record.enabled, t) }}
              </span>
            </template>
          </template>

          <!-- Protocol column with message display -->
          <template v-if="column.dataIndex === 'protocol'">
            {{ formatProtocolDisplay(record.protocol) }}
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
                    v-if="app.show('MailServerSetDefault') && canSetAsDefault(record.enabled)"
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
                    v-if="app.show('MailServerModify') && canEditServer(record)"
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
                    v-if="app.show('MailServerDelete') && canDeleteServer(record)"
                    :disabled="!app.has('MailServerDelete')"
                    @click="deleteConfig(record.id, record.name)">
                    <template #icon>
                      <Icon icon="icon-lajitong" />
                    </template>
                    {{ app.getName('MailServerDelete') }}
                  </MenuItem>

                  <!-- Test option -->
                  <MenuItem
                    v-if="app.show('MailServerTest') && canTestServer(record)"
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
    <AsyncComponent :visible="state.visible">
      <Test
        :id="state.testServerId"
        v-model:visible="state.visible"
        :address="state.testAddress" />
    </AsyncComponent>
  </div>
</template>
