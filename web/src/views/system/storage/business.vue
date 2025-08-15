<script setup lang="ts">
import { onMounted, computed, reactive } from 'vue';
import { Button, Radio, RadioGroup } from 'ant-design-vue';
import { useI18n } from 'vue-i18n';
import { Card, Grid, Hints, Icon, Input, modal } from '@xcan-angus/vue-ui';
import { app, PlatformStoreType, enumUtils } from '@xcan-angus/infra';
import { storage as storageApi } from '@/api/index';
import { StorageParams, BusinessStorageState, FormFieldValues, ValidationState } from './types';
import {
  createAwsStorageColumns, createLocalStorageColumns, isLocalStorage, isAwsS3Storage,
  validateStorageForm, hasStorageChanges, resetValidationRules, resetFormValues, requiresForceConfirmation
} from './utils';

const { t } = useI18n();

// Reactive state management
const state = reactive<BusinessStorageState>({
  enumData: [],
  storage: undefined,
  storeType: undefined,
  isEdit: false,
  force: false,
  editLoading: false
});

// Form field values
const formValues = reactive<FormFieldValues>({
  localDir: undefined,
  proxyAddress: undefined,
  region: undefined,
  endpoint: undefined,
  accessKey: undefined,
  secretKey: undefined
});

// Validation rules for form fields
const validationState = reactive<ValidationState>({
  pathRule: false,
  proxyAddressRule: false,
  endpointRule: false,
  accessKeyRule: false,
  secretKeyRule: false
});

// Computed properties for better performance
const canEdit = computed(() => app.has('ApplicationStorageModify'));

/**
 * Load storage type enums from infrastructure
 */
const loadEnums = async () => {
  try {
    state.enumData = enumUtils.enumToMessages(PlatformStoreType);
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
  state.storeType = e.target.value;
  resetFormValuesToCurrent();
  state.isEdit = true;
};

/**
 * Reset form values to current storage settings
 */
const resetFormValuesToCurrent = () => {
  if (!state.storage) return;

  const newValues = resetFormValues(state.storage);
  Object.assign(formValues, newValues);
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

    state.storage = data;
    if (data) {
      resetFormValuesToCurrent();
      state.storeType = data?.storeType?.value;
    }
  } catch (error) {
    console.error('Failed to load storage settings:', error);
  }
};

/**
 * Enable edit mode for storage configuration
 */
const openEdit = () => {
  state.isEdit = true;
};

/**
 * Cancel edit mode and reset form values
 */
const cancelEdit = () => {
  if (state.storage) {
    state.storeType = state.storage.storeType?.value;
  }
  state.force = false;
  state.isEdit = false;
  Object.assign(validationState, resetValidationRules());
};

/**
 * Handle proxy address input change
 * @param event - Input change event
 */
const proxyAddressChange = (event: any) => {
  const value = event.target.value;
  validationState.proxyAddressRule = !value;
  formValues.proxyAddress = value;
};

/**
 * Handle local directory input change
 * @param event - Input change event
 */
const localDirChange = (event: any) => {
  const value = event.target.value;
  validationState.pathRule = !value;
  formValues.localDir = value;
};

/**
 * Handle region input change
 * @param event - Input change event
 */
const regionChange = (event: any) => {
  formValues.region = event.target.value;
};

/**
 * Handle endpoint input change
 * @param event - Input change event
 */
const endpointChange = (event: any) => {
  const value = event.target.value;
  validationState.endpointRule = !value;
  formValues.endpoint = value;
};

/**
 * Handle access key input change
 * @param event - Input change event
 */
const accessKeyChange = (event: any) => {
  const value = event.target.value;
  validationState.accessKeyRule = !value;
  formValues.accessKey = value;
};

/**
 * Handle secret key input change
 * @param event - Input change event
 */
const secretKeyChange = (event: any) => {
  const value = event.target.value;
  validationState.secretKeyRule = !value;
  formValues.secretKey = value;
};

/**
 * Handle form submission
 * Validates form and saves storage settings
 */
const handleSubmit = async () => {
  const { isValid, validationState: newValidationState } = validateStorageForm(formValues, state.storeType);

  if (!isValid) {
    Object.assign(validationState, newValidationState);
    return;
  }

  if (!hasStorageChanges(formValues, state.storage, state.storeType) || state.editLoading) {
    state.isEdit = false;
    return;
  }

  const params: StorageParams = {
    accessKey: formValues.accessKey || '',
    endpoint: formValues.endpoint || '',
    force: state.force,
    proxyAddress: formValues.proxyAddress || '',
    localDir: formValues.localDir || '',
    region: formValues.region || '',
    secretKey: formValues.secretKey || '',
    storeType: state.storeType || ''
  };

  state.editLoading = true;

  try {
    const [error] = await storageApi.putSetting(params, { silence: true });

    if (error) {
      // Handle specific error code for force confirmation
      if (requiresForceConfirmation((error as any).code)) {
        modal.confirm({
          centered: true,
          title: t('storage.messages.confirmTitle'),
          content: (error as any).message,
          async onOk () {
            state.force = true;
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
    state.isEdit = false;
    state.force = false;
  } catch (error) {
    console.error('Failed to save storage settings:', error);
  } finally {
    state.editLoading = false;
  }
};

// Create table columns using utility functions
const awsColumns = computed(() => createAwsStorageColumns(t));
const localColumns = computed(() => createLocalStorageColumns(t));

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
          :value="state.storeType"
          :disabled="!canEdit"
          size="small"
          @change="handleChange">
          <Radio
            v-for="item in state.enumData"
            :key="item.value"
            :value="item.value">
            {{ item.message }}
          </Radio>
        </RadioGroup>
      </div>

      <!-- Storage configuration forms -->
      <div>
        <!-- Local storage configuration -->
        <template v-if="isLocalStorage(state.storeType)">
          <Grid
            :columns="localColumns"
            class="mt-5"
            style="width: 80%;">
            <template #origin>
              <div class="relative">
                <Input
                  :value="formValues.proxyAddress"
                  :disabled="!state.isEdit"
                  :maxlength="200"
                  size="small"
                  @change="proxyAddressChange" />
                <div v-show="validationState.proxyAddressRule" class="absolute top-7.5 text-3 leading-3 text-rule">
                  {{ t('storage.messages.proxyAddressRequired') }}
                </div>
                <template v-if="!state.isEdit && canEdit">
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
                  :value="formValues.localDir"
                  :disabled="!state.isEdit"
                  :maxlength="400"
                  size="small"
                  @change="localDirChange" />
                <div v-show="validationState.pathRule" class="absolute top-9 text-3 leading-3 text-rule">
                  {{ t('storage.messages.storagePathRequired') }}
                </div>
                <template v-if="!state.isEdit && canEdit">
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
        <template v-if="isAwsS3Storage(state.storeType)">
          <Grid
            :columns="awsColumns"
            class="mt-5"
            style="width: 80%;">
            <template #proxyAddress>
              <div class="relative">
                <Input
                  :value="formValues.proxyAddress"
                  :disabled="!state.isEdit"
                  :maxlength="200"
                  size="small"
                  @change="proxyAddressChange" />
                <div v-show="validationState.proxyAddressRule" class="absolute top-7.5 text-3 leading-3 text-rule">
                  {{ t('storage.messages.proxyAddressRequired') }}
                </div>
                <template v-if="!state.isEdit && canEdit">
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
                  :value="formValues.region"
                  :disabled="!state.isEdit"
                  :maxlength="160"
                  size="small"
                  @change="regionChange" />
                <template v-if="!state.isEdit && canEdit">
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
                  :value="formValues.endpoint"
                  :disabled="!state.isEdit"
                  :maxlength="200"
                  size="small"
                  @change="endpointChange" />
                <div v-show="validationState.endpointRule" class="absolute top-8 text-3 leading-3 text-rule">
                  {{ t('storage.messages.endpointRequired') }}
                </div>
                <template v-if="!state.isEdit && canEdit">
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
                  :value="formValues.accessKey"
                  :disabled="!state.isEdit"
                  :maxlength="4096"
                  size="small"
                  @change="accessKeyChange" />
                <div v-show="validationState.accessKeyRule" class="absolute top-8 text-3 leading-3 text-rule">
                  {{ t('storage.messages.accessKeyRequired') }}
                </div>
                <template v-if="!state.isEdit && canEdit">
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
                  :value="formValues.secretKey"
                  :disabled="!state.isEdit"
                  :maxlength="4096"
                  size="small"
                  @change="secretKeyChange" />
                <div v-show="validationState.secretKeyRule" class="absolute top-8 text-3 leading-3 text-rule">
                  {{ t('storage.messages.secretKeyRequired') }}
                </div>
                <template v-if="!state.isEdit && canEdit">
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
        <template v-if="state.isEdit">
          <div class="text-center space-x-8 mt-5">
            <Button
              :disabled="!state.isEdit"
              size="small"
              type="primary"
              class="px-3"
              @click="handleSubmit">
              {{ t('storage.buttons.confirm') }}
            </Button>
            <Button
              :disabled="!state.isEdit"
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
