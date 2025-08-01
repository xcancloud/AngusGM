<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue';
import useClipboard from 'vue-clipboard3';
import { useI18n } from 'vue-i18n';
import { Badge, Radio, RadioGroup } from 'ant-design-vue';
import {
  Arrow,
  AsyncComponent,
  ButtonAuth,
  Card,
  DatePicker,
  Hints,
  Icon,
  Input,
  Modal,
  modal,
  notification,
  Table
} from '@xcan-angus/vue-ui';
import { app, appContext, enumLoader } from '@xcan-angus/infra';

import SelectApis from '@/views/system/token/components/selectApi/index.vue';
import SelectAcls from '@/views/system/token/components/selectAcl/index.vue';
import { setting, systemToken } from '@/api';
import { _columns, GrantData, Service } from './PropsType';

const { t } = useI18n();
const { toClipboard } = useClipboard();

const loading = ref(false);

const selectApisRef = ref();
const selectAclsRef = ref();

const columns = computed(() => {
  return _columns.map(item => {
    return {
      ...item,
      title: t(item.localesCode)
    };
  });
});

const tableData = reactive({
  list: []
});

const editable = computed(() => {
  return appContext.isSysAdmin() && tableData.list.length < tokenQuota.value;
});

const authTypeOpt = ref<{ value: string, message: string }[]>([]);
const fetchAuthType = async () => {
  const [error, data] = await enumLoader.load('ResourceAuthType');
  if (error) {
    return;
  }
  authTypeOpt.value = data;
};

const changeAuthType = e => {
  selectApisRef.value && selectApisRef.value.clearData();
  selectAclsRef.value && selectAclsRef.value.clearData();
  formValue.authType = e.target.value;
};

const modalData: { source: Array<Service>, visible: boolean, authType: string } = reactive({
  source: [],
  visible: false,
  authType: 'API'
});

const formValue: { resources: Array<GrantData>, name?: string, expiredDate?: string, authType: string } = reactive({
  expiredDate: undefined,
  resources: [],
  name: undefined,
  authType: 'API'
});

const onServerChange = (grantDatas) => {
  formValue.resources = grantDatas;
};

const loadDetailInfo = async function (id: string) {
  const [error, res] = await systemToken.getTokenAuth(id);
  if (error) {
    return;
  }

  setModalData(res.data);
  setVisible(true);
};

const setModalData = (data): void => {
  modalData.source = (data.resources || []).map(item => ({ ...item, open: true }));
  modalData.authType = data.authType.value;
};

const setVisible = (_visible: boolean) => {
  modalData.visible = _visible;
};

const fetchTokens = async () => {
  const [error, res] = await systemToken.getTokens();
  if (error) {
    return;
  }

  tableData.list = res.data;
};

const addToken = async () => {
  loading.value = true;
  const [error] = await systemToken.addToken(formValue);
  loading.value = false;
  if (error) {
    return;
  }

  notification.success(t('systemToken.add_success'));
  resetForm();
  fetchTokens();
};

const resetForm = () => {
  selectApisRef.value && selectApisRef.value.clearData();
  selectAclsRef.value && selectAclsRef.value.clearData();
  formValue.authType = 'API';
  formValue.name = undefined;
  formValue.expiredDate = undefined;
  formValue.resources = [];
};

const onCopy = (content) => {
  toClipboard(content)
    .then(() => {
      notification.success(t('systemToken.copy_success'));
    });
};

const showToken = async (record) => {
  if (!record.token) {
    const [error, { data = {} }] = await systemToken.getTokenValue(record.id);
    if (error) {
      return;
    }
    tableData.list.forEach(item => {
      if (item.id === record.id) {
        item.token = data.value;
        item.showToken = true;
      }
    });
  }
  tableData.list.forEach(item => {
    if (item.id === record.id) {
      item.showToken = true;
    }
  });
};

const closeToken = (id: string) => {
  tableData.list.forEach(item => {
    if (item.id === id) {
      item.showToken = false;
    }
  });
};

const ResourceAclTypeOpt = ref<{ value: string, message: string }[]>([]);
const getResourceAclType = async () => {
  const [error, data] = await enumLoader.load('ResourceAclType');
  if (error) {
    return;
  }
  ResourceAclTypeOpt.value = data;
};

const getIsExpired = (item) => {
  return new Date(item.expiredDate) < new Date();
};

watch(() => formValue.authType, () => {
  selectApisRef.value && selectApisRef.value.clearData();
  selectAclsRef.value && selectAclsRef.value.clearData();
});

// 获取令牌配额
const tokenQuota = ref(0);
const getTokenQuota = async () => {
  const [error, { data }] = await setting.getTokenQuota();
  if (error) {
    return;
  }
  tokenQuota.value = +data.quota;
};

// 删除用户弹框
const handleDel = (id: string, name: string) => {
  modal.confirm({
    centered: true,
    title: t('删除令牌'),
    content: t('userTip4', { name }),
    async onOk () {
      await deleteToken(id);
    }
  });
};

const deleteToken = async function (id: string) {
  const [error] = await systemToken.deleteToken({ ids: [id] });
  if (error) {
    return;
  }

  fetchTokens();
  notification.success(t('systemToken.delete_success'));
};

onMounted(async () => {
  getTokenQuota();
  fetchTokens();
  fetchAuthType();
  getResourceAclType();
});
</script>
<template>
  <div class="flex flex-col min-h-full">
    <Hints :text="t('systemToken.tip_1')" class="mb-1" />
    <Card
      bodyClass="px-8 py-5 space-y-5"
      class="mb-2">
      <template #title>
        <span>{{ t('systemToken.add_token') }}</span>
      </template>
      <div class="text-3 leading-3">
        <div class="text-theme-title">
          <span class="text-red-600">*</span>
          {{ t('systemToken.token_name') }}
        </div>
        <Input
          v-model:value="formValue.name"
          class="mt-1 w-100"
          :placeholder="t('systemToken.add_token_placeholder')"
          :maxlength="100"
          :disabled="!editable"
          size="small" />
      </div>
      <div class="text-3 leading-3">
        <div class="text-theme-title">
          <span class="text-red-600">*</span>
          授权方式
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
      <div class="text-theme-title text-3 leading-3">
        <span class="text-red-600">*</span>
        {{ t('systemToken.token_auth') }}
        <SelectApis
          v-show="formValue.authType === 'API'"
          ref="selectApisRef"
          class="mt-2"
          :disabled="!editable"
          @change="onServerChange" />
        <SelectAcls
          v-show="formValue.authType === 'ACL'"
          ref="selectAclsRef"
          class="mt-2"
          :disabled="!editable"
          @change="onServerChange" />
      </div>
      <div class="text-theme-title text-3 leading-3">
        <div class="text-theme-title">
          {{ t('systemToken.limit_time') }}
        </div>
        <DatePicker
          v-model:value="formValue.expiredDate"
          class="w-70 mt-2"
          size="small"
          showTime
          :disabled="!editable"
          :placeholder="t('systemToken.date_placeholder')" />
      </div>
      <ButtonAuth
        code="SystemTokenCreate"
        :disabled="!formValue.name || !formValue.resources.length"
        icon="icon-tianjia"
        @click="addToken" />
    </Card>
    <Card class="flex-1">
      <template #title>
        <div class="flex items-center">
          <span>{{ t('systemToken.add_token_num', {n: tableData.list.length}) }}</span>
          <span class="text-theme-content text-3 leading-3 ml-1">({{ t('systemToken.support_max_num', {n: tokenQuota}) }})</span>
        </div>
      </template>
      <Table
        :columns="columns"
        :dataSource="tableData.list"
        :pagination="false"
        rowKey="id"
        size="small">
        <template #bodyCell="{ column,text, record }">
          <template v-if="column.dataIndex === 'authType'"> {{ record.authType?.message }}</template>
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
          <template v-if="column.dataIndex === 'createdByName'">
            {{ text || '--' }}
          </template>
          <template v-if="column.dataIndex === 'token'">
            <template v-if="record.showToken">
              <div>
                {{ record.token }}
                <span title="复制">
                  <Icon
                    icon="icon-fuzhi"
                    class="cursor-pointer ml-2 text-theme-text-hover"
                    @click="onCopy(record.token)">
                  </Icon>
                </span>
                <Icon
                  icon="icon-zhengyan"
                  class="cursor-pointer ml-2"
                  @click="closeToken(record.id)">
                </Icon>
              </div>
            </template>
            <template v-else>
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
          <template v-if="column.dataIndex === 'expired'">
            <Badge :color="getIsExpired(record) ? 'orange' : 'green'"></Badge>
            {{ getIsExpired(record) ? '已到期' : '未到期' }}
          </template>
        </template>
      </Table>
    </Card>
    <AsyncComponent :visible="modalData.visible">
      <Modal
        :visible="modalData.visible"
        :centered="true"
        :title="t('systemToken.action_view')"
        width="800px"
        @cancel="setVisible(false)">
        <div v-for="service in modalData.source" :key="service.serviceCode">
          <div class="flex">
            <Arrow v-model:open="service.open" />
            <div>{{ service.serviceName }}</div>
          </div>
          <div
            v-for="source in service.resources"
            v-show="service.open"
            :key="source.resource"
            class="flex ">
            <span class="w-20 text-right">{{ source.resource }}: </span>
            <div v-if="modalData.authType === 'API'" class="flex-1">
              <span
                v-for="api in source.apis"
                :key="api.id"
                class="border px-1 rounded ml-2 inline-block mb-1">{{ `${api.name}(${api.code})` }}</span>
            </div>
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
