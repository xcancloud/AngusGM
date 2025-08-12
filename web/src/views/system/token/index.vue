<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue';
import useClipboard from 'vue-clipboard3';
import { useI18n } from 'vue-i18n';
import { Badge, Radio, RadioGroup } from 'ant-design-vue';
import {
  Arrow, AsyncComponent, ButtonAuth, Card, DatePicker, Hints, Icon, Input, Modal, modal, notification, Table
} from '@xcan-angus/vue-ui';
import { app, appContext, EnumMessage, ResourceAuthType, ResourceAclType, enumUtils } from '@xcan-angus/infra';

import SelectApis from '@/views/system/token/components/selectApi/index.vue';
import SelectAcls from '@/views/system/token/components/selectAcl/index.vue';
import { setting, systemToken } from '@/api';
import { GrantData, Service, Token, ColumnsProps } from './PropsType';

const { t } = useI18n();
const { toClipboard } = useClipboard();

// Reactive state management
const loading = ref(false);
const selectApisRef = ref();
const selectAclsRef = ref();

// Table data state
const tableData = reactive({
  list: [] as Token[]
});

// Computed properties
const editable = computed(() => {
  return appContext.isSysAdmin() && tableData.list.length < tokenQuota.value;
});

// Enum options for authentication types
const authTypeOpt = ref<EnumMessage<ResourceAuthType>[]>([]);

/**
 * Fetch authentication type options from enum utils
 */
const fetchAuthType = async () => {
  authTypeOpt.value = enumUtils.enumToMessages(ResourceAuthType);
};

/**
 * Handle authentication type change and clear related data
 */
const changeAuthType = (e: any) => {
  selectApisRef.value?.clearData();
  selectAclsRef.value?.clearData();
  formValue.authType = e.target.value;
};

// Modal state management
const modalData: { source: Array<Service>, visible: boolean, authType: string } = reactive({
  source: [],
  visible: false,
  authType: 'API'
});

// Form data state
const formValue: { resources: Array<GrantData>, name?: string, expiredDate?: string, authType: string } = reactive({
  expiredDate: undefined,
  resources: [],
  name: undefined,
  authType: 'API'
});

/**
 * Handle service change and update form resources
 */
const onServiceChange = (grantDatas: GrantData[]) => {
  formValue.resources = grantDatas;
};

/**
 * Load detailed token information for viewing
 */
const loadDetailInfo = async (id: string) => {
  const [error, res] = await systemToken.getTokenAuth(id);
  if (error) {
    return;
  }

  setModalData(res.data);
  setVisible(true);
};

/**
 * Set modal data from API response
 */
const setModalData = (data: any): void => {
  modalData.source = (data.resources || []).map((item: any) => ({ ...item, open: true }));
  modalData.authType = data.authType.value;
};

/**
 * Control modal visibility
 */
const setVisible = (_visible: boolean) => {
  modalData.visible = _visible;
};

/**
 * Fetch all tokens from API
 */
const fetchTokens = async () => {
  const [error, res] = await systemToken.getTokens();
  if (error) {
    return;
  }

  tableData.list = res.data;
};

/**
 * Add new token with form data
 */
const addToken = async () => {
  loading.value = true;
  try {
    const [error] = await systemToken.addToken(formValue);
    if (error) {
      return;
    }

    notification.success(t('systemToken.messages.addSuccess'));
    resetForm();
    await fetchTokens();
  } finally {
    loading.value = false;
  }
};

/**
 * Reset form to initial state
 */
const resetForm = () => {
  selectApisRef.value?.clearData();
  selectAclsRef.value?.clearData();
  formValue.authType = 'API';
  formValue.name = undefined;
  formValue.expiredDate = undefined;
  formValue.resources = [];
};

/**
 * Copy content to clipboard
 */
const onCopy = async (content: string) => {
  try {
    await toClipboard(content);
    notification.success(t('systemToken.messages.copySuccess'));
  } catch (error) {
    notification.error(t('systemToken.messages.copyError') || 'Copy failed');
  }
};

/**
 * Show token value by fetching from API if not available
 */
const showToken = async (record: Token) => {
  if (!record.token) {
    const [error, { data = {} }] = await systemToken.getTokenValue(record.id);
    if (error) {
      return;
    }
    // Update token in table data
    const item = tableData.list.find(item => item.id === record.id);
    if (item) {
      item.token = data.value;
      item.showToken = true;
    }
  } else {
    // Show existing token
    const item = tableData.list.find(item => item.id === record.id);
    if (item) {
      item.showToken = true;
    }
  }
};

/**
 * Hide token value
 */
const closeToken = (id: string) => {
  const item = tableData.list.find(item => item.id === id);
  if (item) {
    item.showToken = false;
  }
};

// Resource ACL type options
const ResourceAclTypeOpt = ref<EnumMessage<ResourceAclType>[]>();

/**
 * Fetch resource ACL type options
 */
const getResourceAclType = async () => {
  ResourceAclTypeOpt.value = enumUtils.enumToMessages(ResourceAclType);
};

/**
 * Check if token is expired
 */
const getIsExpired = (item: Token): boolean => {
  return new Date(item.expiredDate) < new Date();
};

// Token quota management
const tokenQuota = ref(0);

/**
 * Get token quota from settings
 */
const getTokenQuota = async () => {
  const [error, { data }] = await setting.getTokenQuota();
  if (error) {
    return;
  }
  tokenQuota.value = +data.quota;
};

/**
 * Show delete confirmation modal
 */
const handleDel = (id: string, name: string) => {
  modal.confirm({
    centered: true,
    title: t('systemToken.titles.deleteToken'),
    content: t('systemToken.messages.deleteConfirmTip', { name }),
    async onOk () {
      await deleteToken(id);
    }
  });
};

/**
 * Delete token by ID
 */
const deleteToken = async (id: string) => {
  const [error] = await systemToken.deleteToken({ ids: [id] });
  if (error) {
    return;
  }

  await fetchTokens();
  notification.success(t('systemToken.messages.deleteSuccess'));
};

// Table column definitions
const _columns: ColumnsProps[] = [
  {
    key: 'name',
    title: t('systemToken.columns.name'),
    dataIndex: 'name',
    localesCode: 'systemToken.columns.name'
  },
  {
    key: 'authType',
    title: t('systemToken.columns.authType'),
    dataIndex: 'authType',
    localesCode: 'systemToken.columns.authType'
  },
  {
    key: 'expiredDate',
    title: t('systemToken.columns.expiredDate'),
    dataIndex: 'expiredDate',
    localesCode: 'systemToken.columns.expiredDate'
  },
  {
    key: 'token',
    title: t('systemToken.columns.token'),
    dataIndex: 'token',
    localesCode: 'systemToken.columns.token',
    width: '550px'
  },
  {
    key: 'expired',
    title: t('systemToken.columns.expired'),
    dataIndex: 'expired',
    localesCode: 'systemToken.columns.expired'
  },
  {
    key: 'createdByName',
    title: t('systemToken.columns.createdByName'),
    dataIndex: 'createdByName',
    localesCode: 'common.columns.createdByName'
  },
  {
    key: 'createdDate',
    title: t('systemToken.columns.createdDate'),
    dataIndex: 'createdDate',
    localesCode: 'common.columns.createdDate'
  },
  {
    key: 'action',
    title: t('systemToken.columns.action'),
    dataIndex: 'action',
    localesCode: 'systemToken.columns.action',
    width: 200
  }
];

// Computed columns with localized titles
const columns = computed(() => {
  return _columns.map(item => ({
    ...item,
    title: t(item.localesCode)
  }));
});

// Watch for auth type changes to clear related data
watch(() => formValue.authType, () => {
  selectApisRef.value?.clearData();
  selectAclsRef.value?.clearData();
});

// Lifecycle hooks
onMounted(async () => {
  // Initialize data in parallel for better performance
  await Promise.all([
    getTokenQuota(),
    fetchTokens(),
    fetchAuthType(),
    getResourceAclType()
  ]);
});
</script>
<template>
  <!-- Main container with flexbox layout -->
  <div class="flex flex-col min-h-full">
    <!-- Token creation hints -->
    <Hints :text="t('systemToken.messages.tip')" class="mb-1" />

    <!-- Token creation form card -->
    <Card
      bodyClass="px-8 py-5 space-y-5"
      class="mb-2">
      <template #title>
        <span>{{ t('systemToken.messages.addToken') }}</span>
      </template>
      <!-- Token name input field -->
      <div class="text-3 leading-3">
        <div class="text-theme-title">
          <span class="text-red-600">*</span>
          {{ t('systemToken.messages.tokenName') }}
        </div>
        <Input
          v-model:value="formValue.name"
          class="mt-1 w-100"
          :placeholder="t('systemToken.placeholder.addTokenName')"
          :maxlength="100"
          :disabled="!editable"
          size="small" />
      </div>
      <!-- Authentication type selection -->
      <div class="text-3 leading-3">
        <div class="text-theme-title">
          <span class="text-red-600">*</span>
          {{ t('systemToken.columns.authType') }}
        </div>
        <RadioGroup
          :value="formValue.authType"
          :disabled="!editable"
          class="mt-1"
          @change="changeAuthType">
          <Radio
            v-for="item in authTypeOpt"
            :key="item.value"
            :value="item.value">
            {{ item.message }}
          </Radio>
        </RadioGroup>
      </div>
      <!-- Token authorization resources selection -->
      <div class="text-theme-title text-3 leading-3">
        <span class="text-red-600">*</span>
        {{ t('systemToken.messages.tokenAuth') }}
        <!-- API resources selector -->
        <SelectApis
          v-show="formValue.authType === 'API'"
          ref="selectApisRef"
          class="mt-2"
          :disabled="!editable"
          @change="onServiceChange" />
        <!-- ACL resources selector -->
        <SelectAcls
          v-show="formValue.authType === 'ACL'"
          ref="selectAclsRef"
          class="mt-2"
          :disabled="!editable"
          @click="onServiceChange" />
      </div>
      <!-- Expiration date picker -->
      <div class="text-theme-title text-3 leading-3">
        <div class="text-theme-title">
          {{ t('systemToken.messages.limitTime') }}
        </div>
        <DatePicker
          v-model:value="formValue.expiredDate"
          class="w-70 mt-2"
          size="small"
          showTime
          :disabled="!editable"
          :placeholder="t('systemToken.placeholder.expiredDate')" />
      </div>
      <!-- Create token button -->
      <ButtonAuth
        code="SystemTokenCreate"
        :disabled="!formValue.name || !formValue.resources.length"
        icon="icon-tianjia"
        @click="addToken" />
    </Card>

    <!-- Token list table card -->
    <Card class="flex-1">
      <template #title>
        <div class="flex items-center">
          <span>{{ t('systemToken.messages.addTokenNum', {n: tableData.list.length}) }}</span>
          <span class="text-theme-content text-3 leading-3 ml-1">({{ t('systemToken.messages.supportMaxNum', {n: tokenQuota}) }})</span>
        </div>
      </template>
      <Table
        :columns="columns"
        :dataSource="tableData.list"
        :pagination="false"
        rowKey="id"
        size="small"
        :loading="loading"
        noDataText=""
        noDataSize="small">
        <template #bodyCell="{ column,text, record }">
          <!-- Authentication type column display -->
          <template v-if="column.dataIndex === 'authType'"> {{ record.authType?.message }}</template>

          <!-- Action buttons column -->
          <template v-if="column.dataIndex === 'action'">
            <ButtonAuth
              code="SystemTokenAuthView"
              type="text"
              icon="icon-chakanshouquan"
              class="mr-2.5"
              @click="loadDetailInfo(record.id)" />
            <ButtonAuth
              code="SystemTokenDelete"
              type="text"
              icon="icon-lajitong"
              :disabled="!appContext.isSysAdmin()"
              @click="handleDel(record.id,record.name)" />
          </template>

          <!-- Created by name column with fallback -->
          <template v-if="column.dataIndex === 'createdByName'">
            {{ text || '--' }}
          </template>
          <!-- Token value column with copy and hide functionality -->
          <template v-if="column.dataIndex === 'token'">
            <template v-if="record.showToken">
              <div>
                {{ record.token }}
                <!-- Copy token button -->
                <span :title="t('systemToken.placeholder.copy')">
                  <Icon
                    icon="icon-fuzhi"
                    class="cursor-pointer ml-2 text-theme-text-hover"
                    @click="onCopy(record.token)">
                  </Icon>
                </span>
                <!-- Hide token button -->
                <Icon
                  icon="icon-zhengyan"
                  class="cursor-pointer ml-2"
                  @click="closeToken(record.id)">
                </Icon>
              </div>
            </template>
            <template v-else>
              <!-- Show token button with permission check -->
              <Icon
                v-if="app.has('SystemTokenView')"
                icon="icon-biyan"
                class="cursor-pointer"
                @click="showToken(record)" />
              <Icon
                v-else
                icon="icon-biyan"
                class="cursor-not-allowed text-theme-sub-content" />
            </template>
          </template>
          <!-- Expiration status column with color-coded badge -->
          <template v-if="column.dataIndex === 'expired'">
            <Badge :color="getIsExpired(record) ? 'orange' : 'green'"></Badge>
            {{ getIsExpired(record) ? t('systemToken.messages.expired') : t('systemToken.messages.notExpired') }}
          </template>
        </template>
      </Table>
    </Card>

    <!-- Token detail view modal -->
    <AsyncComponent :visible="modalData.visible">
      <Modal
        :visible="modalData.visible"
        :centered="true"
        :title="t('systemToken.messages.actionView')"
        width="800px"
        @cancel="setVisible(false)">
        <!-- Service list with expandable resources -->
        <div v-for="service in modalData.source" :key="service.serviceCode">
          <!-- Service header with expand/collapse arrow -->
          <div class="flex">
            <Arrow v-model:open="service.open" />
            <div>{{ service.serviceName }}</div>
          </div>

          <!-- Resource list for each service -->
          <div
            v-for="source in service.resources"
            v-show="service.open"
            :key="source.resource"
            class="flex ">
            <span class="w-20 text-right">{{ source.resource }}: </span>

            <!-- API resources display -->
            <div v-if="modalData.authType === 'API'" class="flex-1">
              <span
                v-for="api in source.apis"
                :key="api.id"
                class="border px-1 rounded ml-2 inline-block mb-1">{{ `${api.name}(${api.code})` }}</span>
            </div>

            <!-- ACL resources display -->
            <div v-else class="flex-1">
              <span
                v-for="acl in source.acls"
                :key="acl.value"
                class="border px-1 rounded ml-2">
                {{ acl.message }}
              </span>
            </div>
          </div>
        </div>
      </Modal>
    </AsyncComponent>
  </div>
</template>
