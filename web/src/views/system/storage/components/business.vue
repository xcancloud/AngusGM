<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { Button, Radio, RadioGroup } from 'ant-design-vue';
import { useI18n } from 'vue-i18n';
import { Card, Grid, Hints, Icon, Input, modal } from '@xcan-angus/vue-ui';
import { app, enumLoader } from '@xcan-angus/infra';
import { storage as storageApi } from '@/api/index';

const { t } = useI18n();
const enumData = ref<{ value: string, message: string }[]>([]);

const loadEnums = async () => {
  const [error, data] = await enumLoader.load('PlatformStoreType');
  if (error) {
    return;
  }
  enumData.value = data;
};

const handleChange = e => {
  storeType.value = e.target.value;
  const _data = JSON.parse(JSON.stringify(storage.value));
  proxyAddress.value = _data?.proxyAddress;
  localDir.value = _data?.localDir;
  accessKey.value = _data?.accessKey;
  secretKey.value = _data?.secretKey;
  region.value = _data?.region;
  endpoint.value = _data?.endpoint;
  isEdit.value = true;
};

const storage = ref();

const loadStorage = async () => {
  const [error, { data }] = await storageApi.getSetting();
  if (error) {
    return;
  }
  storage.value = data;

  if (data) {
    const _data = JSON.parse(JSON.stringify(data));
    storeType.value = _data?.storeType?.value;
    proxyAddress.value = _data?.proxyAddress;
    localDir.value = _data?.localDir;
    accessKey.value = _data?.accessKey;
    secretKey.value = _data?.secretKey;
    region.value = _data?.region;
    endpoint.value = _data?.endpoint;
  }
};

const storeType = ref();
const isEdit = ref(false);
const pathRule = ref(false);

const openEdit = () => {
  isEdit.value = true;
};

const cancelEdit = () => {
  storeType.value = storage.value?.storeType?.value;
  force.value = false;
  isEdit.value = false;
  pathRule.value = false;
  endpointRule.value = false;
  accessKeyRule.value = false;
  secretKeyRule.value = false;
};

const force = ref<boolean>(false);
const localDir = ref<string>();
const proxyAddress = ref<string>();
const region = ref<string>();
const endpoint = ref<string>();
const endpointRule = ref<boolean>(false);
const accessKey = ref<string>();
const accessKeyRule = ref<boolean>(false);
const secretKey = ref<string>();
const secretKeyRule = ref<boolean>(false);

const proxyAddressRule = ref<boolean>(false);
const proxyAddressChange = (event: any) => {
  const value = event.target.value;
  if (!value) {
    proxyAddressRule.value = true;
    return;
  }

  proxyAddressRule.value = false;
  proxyAddress.value = value;
};

const localDirChange = (event: any) => {
  const value = event.target.value;
  pathRule.value = !value;
  localDir.value = value;
};

const regionChange = (event: any) => {
  const value = event.target.value;
  region.value = value;
};
const endpointChange = (event: any) => {
  const value = event.target.value;
  endpointRule.value = !value;
  endpoint.value = value;
};
const accessKeyChange = (event: any) => {
  const value = event.target.value;
  accessKeyRule.value = !value;
  accessKey.value = value;
};
const secretKeyChange = (event: any) => {
  const value = event.target.value;
  secretKeyRule.value = !value;
  secretKey.value = value;
};

const editLoading = ref(false);
const handleSure = async () => {
  if (!proxyAddress.value) {
    proxyAddressRule.value = true;
    return;
  }
  if (storeType.value === 'LOCAL') {
    if (!localDir.value) {
      pathRule.value = true;
      return;
    }
  }
  if (storeType.value === 'AWS_S3') {
    if (!endpoint.value) {
      endpointRule.value = true;
      return;
    }
    if (!accessKey.value) {
      accessKeyRule.value = true;
      return;
    }
    if (!secretKey.value) {
      secretKeyRule.value = true;
      return;
    }
  }

  const diff = handleCheck();

  if (!diff || editLoading.value) {
    isEdit.value = false;
    return;
  }

  const params = {
    accessKey: accessKey.value,
    endpoint: endpoint.value,
    force: force.value,
    proxyAddress: proxyAddress.value,
    localDir: localDir.value,
    region: region.value,
    secretKey: secretKey.value,
    storeType: storeType.value
  };

  editLoading.value = true;
  const [error] = await storageApi.putSetting(params, { silence: true });
  editLoading.value = false;
  if (error) {
    if (error.code === 'BST001') {
      modal.confirm({
        centered: true,
        title: t('confirmTitle'),
        content: error.message,
        async onOk () {
          force.value = true;
          handleSure();
        },
        onCancel () {
          cancelEdit();
        }
      });
    }
    return;
  }

  loadStorage();
  isEdit.value = false;
  force.value = false;
};

const handleCheck = () => {
  if (!isEdit.value) {
    return false;
  }

  // 切换了存储类型
  if (storeType.value !== storage.value?.storeType?.value) {
    return true;
  }
  // 本地存储
  if (storeType.value === 'LOCAL') {
    // localDir有没有修改
    if (localDir.value !== storage.value?.localDir) {
      return true;
    }
    // hostname有没有修改
    if (proxyAddress.value !== storage.value?.proxyAddress) {
      return true;
    }

    return false;
  }
  // s3协议对象存储
  if (storeType.value === 'AWS_S3') {
    if (region.value !== storage.value?.region) {
      return true;
    }
    if (endpoint.value !== storage.value?.endpoint) {
      return true;
    }
    if (accessKey.value !== storage.value?.accessKey) {
      return true;
    }
    if (secretKey.value !== storage.value?.secretKey) {
      return true;
    }

    return false;
  }
};

const awsColumns = [
  [
    {
      dataIndex: 'proxyAddress',
      label: t('storage17'),
      required: true
    },
    {
      dataIndex: 'region',
      label: 'region (S3_REGION)'
    },
    {
      dataIndex: 'endpoint',
      label: 'endpoint (S3_ENDPOINT)',
      required: true
    },
    {
      dataIndex: 'accessKey',
      label: 'accessKey (S3_ACCESSKEY)',
      required: true
    },
    {
      dataIndex: 'secretKey',
      label: 'secretKey (S3_SECRETKEY)',
      required: true
    }
  ]
];

const localColumns = [
  [
    {
      dataIndex: 'origin',
      label: t('storage17'),
      required: true
    },
    {
      dataIndex: 'url',
      label: t('storage12'),
      required: true
    }
  ]
];
onMounted(() => {
  loadEnums();
  loadStorage();
});

</script>
<template>
  <Card class="flex-1" bodyClass="px-8 py-5">
    <template #title>
      <div class="text-theme-title flex items-center py-1">
        {{ t('storage9') }}
        <Hints
          :text="t('storage10')"
          class="ml-2"
          style="transform: translateY(1px);" />
      </div>
    </template>
    <template #default>
      <div class="flex items-center text-3 leading-3">
        <span class="mr-3">{{ t('storage11') }}</span>
        <RadioGroup
          :value="storeType"
          :disabled="!app.has('ApplicationStorageModify')"
          size="small"
          @change="handleChange">
          <Radio
            v-for="item in enumData"
            :key="item.value"
            :value="item.value">
            {{ item.message }}
          </Radio>
        </RadioGroup>
      </div>
      <div>
        <template v-if="storeType ==='LOCAL'">
          <Grid
            :columns="localColumns"
            class="mt-5"
            style="width: 80%;">
            <template #origin>
              <div class="relative">
                <Input
                  :value="proxyAddress"
                  :disabled="!isEdit"
                  :maxlength="200"
                  size="small"
                  @change="proxyAddressChange" />
                <div v-show="proxyAddressRule" class="absolute top-7.5 text-3 leading-3 text-rule">
                  {{ t('storage18')
                  }}
                </div>
                <template v-if="!isEdit && app.has('ApplicationStorageModify')">
                  <Icon
                    icon="icon-shuxie"
                    class="text-theme-special text-theme-text-hover cursor-pointer absolute -right-5 top-2"
                    @click="openEdit" />
                </template>
              </div>
            </template>
            <template #url>
              <div class="relative">
                <Input
                  :value="localDir"
                  :disabled="!isEdit"
                  :maxlength="400"
                  size="small"
                  @change="localDirChange" />
                <div v-show="pathRule" class="absolute top-9 text-3 leading-3 text-rule">{{ t('storage13') }}</div>
                <template v-if="!isEdit && app.has('ApplicationStorageModify')">
                  <Icon
                    icon="icon-shuxie"
                    class="text-theme-special text-theme-text-hover cursor-pointer absolute -right-5 top-2"
                    @click="openEdit" />
                </template>
              </div>
            </template>
          </Grid>
        </template>
        <template v-if="storeType ==='AWS_S3'">
          <Grid
            :columns="awsColumns"
            class="mt-5"
            style="width: 80%;">
            <template #proxyAddress>
              <div class="relative">
                <Input
                  :value="proxyAddress"
                  :disabled="!isEdit"
                  :maxlength="200"
                  size="small"
                  @change="proxyAddressChange" />
                <div v-show="proxyAddressRule" class="absolute top-7.5 text-3 leading-3 text-rule">
                  {{ t('storage18')
                  }}
                </div>
                <template v-if="!isEdit && app.has('ApplicationStorageModify')">
                  <Icon
                    icon="icon-shuxie"
                    class="text-theme-special text-theme-text-hover cursor-pointer absolute -right-5 top-2"
                    @click="openEdit" />
                </template>
              </div>
            </template>
            <template #region>
              <div class="relative">
                <Input
                  :value="region"
                  :disabled="!isEdit"
                  :maxlength="160"
                  size="small"
                  @change="regionChange" />
                <template v-if="!isEdit && app.has('ApplicationStorageModify')">
                  <Icon
                    icon="icon-shuxie"
                    class="text-theme-special text-theme-text-hover cursor-pointer absolute -right-5 top-2"
                    @click="openEdit" />
                </template>
              </div>
            </template>
            <template #endpoint>
              <div class="relative -mt-1">
                <Input
                  :value="endpoint"
                  :disabled="!isEdit"
                  :maxlength="200"
                  size="small"
                  @change="endpointChange" />
                <div v-show="endpointRule" class="absolute top-8 text-3 leading-3 text-rule">{{ t('storage14') }}</div>
                <template v-if="!isEdit && app.has('ApplicationStorageModify')">
                  <Icon
                    icon="icon-shuxie"
                    class="text-theme-special text-theme-text-hover cursor-pointer absolute -right-5 top-2"
                    @click="openEdit" />
                </template>
              </div>
            </template>
            <template #accessKey>
              <div class="relative -mt-1">
                <Input
                  :value="accessKey"
                  :disabled="!isEdit"
                  :maxlength="4096"
                  size="small"
                  @change="accessKeyChange" />
                <div v-show="accessKeyRule" class="absolute top-8 text-3 leading-3 text-rule">{{ t('storage15') }}</div>
                <template v-if="!isEdit && app.has('ApplicationStorageModify')">
                  <Icon
                    icon="icon-shuxie"
                    class="text-theme-special text-theme-text-hover cursor-pointer absolute -right-5 top-2"
                    @click="openEdit" />
                </template>
              </div>
            </template>
            <template #secretKey>
              <div class="relative -mt-1">
                <Input
                  :value="secretKey"
                  :disabled="!isEdit"
                  :maxlength="4096"
                  size="small"
                  @change="secretKeyChange" />
                <div v-show="secretKeyRule" class="absolute top-8 text-3 leading-3 text-rule">{{ t('storage16') }}</div>
                <template v-if="!isEdit && app.has('ApplicationStorageModify')">
                  <Icon
                    icon="icon-shuxie"
                    class="text-theme-special text-theme-text-hover cursor-pointer absolute -right-5 top-2"
                    @click="openEdit" />
                </template>
              </div>
            </template>
          </Grid>
        </template>
        <template v-if="isEdit">
          <div class="text-center space-x-8 mt-5">
            <Button
              :disabled="!isEdit"
              size="small"
              type="primary"
              class="px-3"
              @click="handleSure">
              确定
            </Button>
            <Button
              :disabled="!isEdit"
              size="small"
              class="px-3"
              @click="cancelEdit">
              取消
            </Button>
          </div>
        </template>
      </div>
    </template>
  </Card>
</template>
