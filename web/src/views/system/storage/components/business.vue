<script setup lang="ts">
import { onMounted, ref, computed } from 'vue';
import { Button, Radio, RadioGroup } from 'ant-design-vue';
import { useI18n } from 'vue-i18n';
import { Card, Grid, Hints, Icon, Input, modal } from '@xcan-angus/vue-ui';
import { app, EnumMessage, PlatformStoreType, enumUtils } from '@xcan-angus/infra';
import { storage as storageApi } from '@/api/index';
import { StorageSetting, StorageParams, StorageColumn } from '../PropsType';

// Storage type constants
const STORAGE_TYPES = {
  LOCAL: 'LOCAL',
  AWS_S3: 'AWS_S3'
} as const;

// Error code constants
const ERROR_CODES = {
  BST001: 'BST001'
} as const;

const { t } = useI18n();

// Reactive data for storage configuration
const enumData = ref<EnumMessage<PlatformStoreType>[]>([]);
const storage = ref<StorageSetting>();
const storeType = ref<string>();
const isEdit = ref(false);
const force = ref<boolean>(false);
const editLoading = ref(false);

// Form field values
const localDir = ref<string>();
const proxyAddress = ref<string>();
const region = ref<string>();
const endpoint = ref<string>();
const accessKey = ref<string>();
const secretKey = ref<string>();

// Validation rules for form fields
const pathRule = ref<boolean>(false);
const proxyAddressRule = ref<boolean>(false);
const endpointRule = ref<boolean>(false);
const accessKeyRule = ref<boolean>(false);
const secretKeyRule = ref<boolean>(false);

// Computed properties for better performance
const isLocalStorage = computed(() => storeType.value === STORAGE_TYPES.LOCAL);
const isAwsS3Storage = computed(() => storeType.value === STORAGE_TYPES.AWS_S3);
const canEdit = computed(() => app.has('ApplicationStorageModify'));

/**
 * Load storage type enums from infrastructure
 */
const loadEnums = async () => {
  try {
    enumData.value = enumUtils.enumToMessages(PlatformStoreType);
  } catch (error) {
    console.error('Failed to load storage type enums:', error);
  }
};

/**
 * Handle storage type change
 * Resets form values and enables edit mode
 * @param e - Radio change event
 */
const handleChange = (e: any) => {
  storeType.value = e.target.value;
  resetFormValues();
  isEdit.value = true;
};

/**
 * Reset form values to current storage settings
 */
const resetFormValues = () => {
  if (!storage.value) return;

  const currentData = JSON.parse(JSON.stringify(storage.value));
  proxyAddress.value = currentData?.proxyAddress;
  localDir.value = currentData?.localDir;
  accessKey.value = currentData?.accessKey;
  secretKey.value = currentData?.secretKey;
  region.value = currentData?.region;
  endpoint.value = currentData?.endpoint;
};

/**
 * Load current storage settings from API
 */
const loadStorage = async () => {
  try {
    const [error, { data }] = await storageApi.getSetting();
    if (error) {
      return;
    }

    storage.value = data;
    if (data) {
      resetFormValues();
      storeType.value = data?.storeType?.value;
    }
  } catch (error) {
    console.error('Failed to load storage settings:', error);
  }
};

/**
 * Enable edit mode for storage configuration
 */
const openEdit = () => {
  isEdit.value = true;
};

/**
 * Cancel edit mode and reset form values
 */
const cancelEdit = () => {
  if (storage.value) {
    storeType.value = storage.value.storeType?.value;
  }
  force.value = false;
  isEdit.value = false;
  resetValidationRules();
};

/**
 * Reset all validation rules
 */
const resetValidationRules = () => {
  pathRule.value = false;
  proxyAddressRule.value = false;
  endpointRule.value = false;
  accessKeyRule.value = false;
  secretKeyRule.value = false;
};

/**
 * Handle proxy address input change
 * @param event - Input change event
 */
const proxyAddressChange = (event: any) => {
  const value = event.target.value;
  proxyAddressRule.value = !value;
  proxyAddress.value = value;
};

/**
 * Handle local directory input change
 * @param event - Input change event
 */
const localDirChange = (event: any) => {
  const value = event.target.value;
  pathRule.value = !value;
  localDir.value = value;
};

/**
 * Handle region input change
 * @param event - Input change event
 */
const regionChange = (event: any) => {
  region.value = event.target.value;
};

/**
 * Handle endpoint input change
 * @param event - Input change event
 */
const endpointChange = (event: any) => {
  const value = event.target.value;
  endpointRule.value = !value;
  endpoint.value = value;
};

/**
 * Handle access key input change
 * @param event - Input change event
 */
const accessKeyChange = (event: any) => {
  const value = event.target.value;
  accessKeyRule.value = !value;
  accessKey.value = value;
};

/**
 * Handle secret key input change
 * @param event - Input change event
 */
const secretKeyChange = (event: any) => {
  const value = event.target.value;
  secretKeyRule.value = !value;
  secretKey.value = value;
};

/**
 * Validate form fields based on storage type
 * @returns true if validation passes, false otherwise
 */
const validateForm = (): boolean => {
  // Validate proxy address (required for all storage types)
  if (!proxyAddress.value) {
    proxyAddressRule.value = true;
    return false;
  }

  // Validate local storage specific fields
  if (isLocalStorage.value && !localDir.value) {
    pathRule.value = true;
    return false;
  }

  // Validate AWS S3 specific fields
  if (isAwsS3Storage.value) {
    if (!endpoint.value) {
      endpointRule.value = true;
      return false;
    }
    if (!accessKey.value) {
      accessKeyRule.value = true;
      return false;
    }
    if (!secretKey.value) {
      secretKeyRule.value = true;
      return false;
    }
  }

  return true;
};

/**
 * Check if storage settings have changed
 * @returns true if changes detected, false otherwise
 */
const hasChanges = (): boolean => {
  if (!isEdit.value || !storage.value) {
    return false;
  }

  // Check if storage type changed
  if (storeType.value !== storage.value.storeType?.value) {
    return true;
  }

  // Check local storage changes
  if (isLocalStorage.value) {
    if (localDir.value !== storage.value.localDir ||
        proxyAddress.value !== storage.value.proxyAddress) {
      return true;
    }
  }

  // Check AWS S3 changes
  if (isAwsS3Storage.value) {
    if (region.value !== storage.value.region ||
        endpoint.value !== storage.value.endpoint ||
        accessKey.value !== storage.value.accessKey ||
        secretKey.value !== storage.value.secretKey) {
      return true;
    }
  }

  return false;
};

/**
 * Handle form submission
 * Validates form and saves storage settings
 */
const handleSubmit = async () => {
  if (!validateForm()) {
    return;
  }

  if (!hasChanges() || editLoading.value) {
    isEdit.value = false;
    return;
  }

  const params: StorageParams = {
    accessKey: accessKey.value || '',
    endpoint: endpoint.value || '',
    force: force.value,
    proxyAddress: proxyAddress.value || '',
    localDir: localDir.value || '',
    region: region.value || '',
    secretKey: secretKey.value || '',
    storeType: storeType.value || ''
  };

  editLoading.value = true;

  try {
    const [error] = await storageApi.putSetting(params, { silence: true });

    if (error) {
      // Handle specific error code for force confirmation
      if (error.code === ERROR_CODES.BST001) {
        modal.confirm({
          centered: true,
          title: t('storage.messages.confirmTitle'),
          content: error.message,
          async onOk () {
            force.value = true;
            await handleSubmit();
          },
          onCancel () {
            cancelEdit();
          }
        });
      }
      return;
    }

    // Success - reload storage settings and reset form
    await loadStorage();
    isEdit.value = false;
    force.value = false;
  } catch (error) {
    console.error('Failed to save storage settings:', error);
  } finally {
    editLoading.value = false;
  }
};

// Table columns configuration for different storage types
const awsColumns = computed<StorageColumn[][]>(() => [
  [
    {
      dataIndex: 'proxyAddress',
      label: t('storage.columns.proxyAddress'),
      required: true
    },
    {
      dataIndex: 'region',
      label: t('storage.columns.region')
    },
    {
      dataIndex: 'endpoint',
      label: t('storage.columns.endpoint'),
      required: true
    },
    {
      dataIndex: 'accessKey',
      label: t('storage.columns.accessKey'),
      required: true
    },
    {
      dataIndex: 'secretKey',
      label: t('storage.columns.secretKey'),
      required: true
    }
  ]
]);

const localColumns = computed<StorageColumn[][]>(() => [
  [
    {
      dataIndex: 'origin',
      label: t('storage.columns.proxyAddress'),
      required: true
    },
    {
      dataIndex: 'url',
      label: t('storage.columns.url'),
      required: true
    }
  ]
]);

// Lifecycle hook - initialize component on mount
onMounted(() => {
  loadEnums();
  loadStorage();
});
</script>

<template>
  <Card class="flex-1" bodyClass="px-8 py-5">
    <!-- Card title with storage configuration hint -->
    <template #title>
      <div class="text-theme-title flex items-center py-1">
        {{ t('storage.messages.businessStorageTitle') }}
        <Hints
          :text="t('storage.messages.businessStorageDesc')"
          class="ml-2"
          style="transform: translateY(1px);" />
      </div>
    </template>

    <template #default>
      <!-- Storage type selection -->
      <div class="flex items-center text-3 leading-3">
        <span class="mr-3">{{ t('storage.messages.storageType') }}</span>
        <RadioGroup
          :value="storeType"
          :disabled="!canEdit"
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

      <!-- Storage configuration forms -->
      <div>
        <!-- Local storage configuration -->
        <template v-if="isLocalStorage">
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
                  {{ t('storage.messages.proxyAddressRequired') }}
                </div>
                <template v-if="!isEdit && canEdit">
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
                <div v-show="pathRule" class="absolute top-9 text-3 leading-3 text-rule">
                  {{ t('storage.messages.storagePathRequired') }}
                </div>
                <template v-if="!isEdit && canEdit">
                  <Icon
                    icon="icon-shuxie"
                    class="text-theme-special text-theme-text-hover cursor-pointer absolute -right-5 top-2"
                    @click="openEdit" />
                </template>
              </div>
            </template>
          </Grid>
        </template>

        <!-- AWS S3 storage configuration -->
        <template v-if="isAwsS3Storage">
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
                  {{ t('storage.messages.proxyAddressRequired') }}
                </div>
                <template v-if="!isEdit && canEdit">
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
                <template v-if="!isEdit && canEdit">
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
                <div v-show="endpointRule" class="absolute top-8 text-3 leading-3 text-rule">
                  {{ t('storage.messages.endpointRequired') }}
                </div>
                <template v-if="!isEdit && canEdit">
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
                <div v-show="accessKeyRule" class="absolute top-8 text-3 leading-3 text-rule">
                  {{ t('storage.messages.accessKeyRequired') }}
                </div>
                <template v-if="!isEdit && canEdit">
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
                <div v-show="secretKeyRule" class="absolute top-8 text-3 leading-3 text-rule">
                  {{ t('storage.messages.secretKeyRequired') }}
                </div>
                <template v-if="!isEdit && canEdit">
                  <Icon
                    icon="icon-shuxie"
                    class="text-theme-special text-theme-text-hover cursor-pointer absolute -right-5 top-2"
                    @click="openEdit" />
                </template>
              </div>
            </template>
          </Grid>
        </template>

        <!-- Action buttons for edit mode -->
        <template v-if="isEdit">
          <div class="text-center space-x-8 mt-5">
            <Button
              :disabled="!isEdit"
              size="small"
              type="primary"
              class="px-3"
              @click="handleSubmit">
              {{ t('storage.buttons.confirm') }}
            </Button>
            <Button
              :disabled="!isEdit"
              size="small"
              class="px-3"
              @click="cancelEdit">
              {{ t('storage.buttons.cancel') }}
            </Button>
          </div>
        </template>
      </div>
    </template>
  </Card>
</template>
