<script setup lang="ts">
import { onMounted, ref, reactive } from 'vue';
import { Divider, Form, FormItem } from 'ant-design-vue';
import { ButtonAuth, Input, modal, notification, Select } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';

import { event } from '@/api';
import {
  ChannelAddressProps, ChannelData, ChannelFormData, ChannelUpdateData, ChannelTestConfig, ValidationRule, FormRef
} from './types';
import {
  createInitialChannelData, processChannelData, addNewChannelData, createAddressValidationRules,
  createChannelFormData, createChannelUpdateData, createChannelTestConfig, isChannelInEditMode,
  isNewChannel, canEditChannel, canDeleteChannel, canTestChannel
} from './utils';

// Component props with proper typing
const props = withDefaults(defineProps<ChannelAddressProps>(), {
  max: 5,
  channelType: 'WEBHOOK',
  placeholder: ''
});

const { t } = useI18n();

// Form references for validation
const formRefs = reactive<FormRef[]>([]);

// Component state management
const loading = ref(true);
const dataSource = ref<ChannelData[]>([
  createInitialChannelData(props.channelType)
]);

// Validation rules for address field
const addressRule = ref<ValidationRule[]>(
  createAddressValidationRules(props.channelType, t)
);

/**
 * Get receive setting details from API
 * Loads existing channel configurations and adds new channel if needed
 */
const getReceiveSettingDetail = async (): Promise<void> => {
  try {
    const [error, res] = await event.getTypeChannel(props.channelType);
    if (error && !res.data) {
      return;
    }

    dataSource.value = processChannelData(res.data, props.channelType);
    addNewChannelData(dataSource.value, props.channelType, props.max);
  } catch (error) {
    console.error('Failed to load channel details:', error);
  } finally {
    loading.value = false;
  }
};

/**
 * Add new HTTP item
 * Validates form and saves new channel configuration
 */
const addHttpItem = async (index: number, item: ChannelData): Promise<void> => {
  try {
    await formRefs[index].validate();

    loading.value = true;
    const formData: ChannelFormData = createChannelFormData(
      item.name,
      item.channelType,
      item.address
    );

    const [error] = await event.addChannel(formData);
    if (error) {
      return;
    }

    notification.success(t('event.channel.messages.addSuccess'));
    await getReceiveSettingDetail();
  } catch (error) {
    console.error('Failed to add channel:', error);
  } finally {
    loading.value = false;
  }
};

/**
 * Edit HTTP item confirmation
 * Validates form and updates existing channel configuration
 */
const editHttpItemConfirm = async (index: number, item: ChannelData): Promise<void> => {
  try {
    await formRefs[index].validate();

    loading.value = true;
    const updateData: ChannelUpdateData = createChannelUpdateData(
      item.id,
      item.name,
      item.address
    );

    const [error] = await event.replaceChannel(updateData);
    if (error) {
      return;
    }

    notification.success(t('event.channel.messages.editSuccess'));
    await getReceiveSettingDetail();
  } catch (error) {
    console.error('Failed to update channel:', error);
  } finally {
    loading.value = false;
  }
};

/**
 * Delete confirmation modal
 * Shows confirmation dialog before deleting channel
 */
const deleteConfirm = (id: string): void => {
  modal.confirm({
    centered: true,
    title: t('common.actions.delete'),
    content: t('event.channel.messages.deleteConfirm'),
    onOk () {
      delHttpConfig(id);
    }
  });
};

/**
 * Delete HTTP configuration
 * Removes channel configuration from the system
 */
const delHttpConfig = async (id: string): Promise<void> => {
  try {
    loading.value = true;
    const [error] = await event.deleteChannel(id);
    if (error) {
      return;
    }

    notification.success(t('event.channel.messages.deleteSuccess'));
    await getReceiveSettingDetail();
  } catch (error) {
    console.error('Failed to delete channel:', error);
  } finally {
    loading.value = false;
  }
};

/**
 * Test channel configuration
 * Validates and tests channel configuration
 */
const testConfig = async (index: number, item: ChannelData): Promise<void> => {
  try {
    await formRefs[index].validate();

    loading.value = true;
    const testConfig: ChannelTestConfig = createChannelTestConfig(
      item.name,
      item.address,
      item.channelType
    );

    const [error] = await event.testChannelConfig(testConfig);
    if (error) {
      return;
    }

    notification.success(t('event.channel.messages.testSuccess'));
  } catch (error) {
    console.error('Failed to test channel:', error);
  } finally {
    loading.value = false;
  }
};

/**
 * Edit channel address
 * Enables edit mode for existing channel
 */
const editAddress = (item: ChannelData): void => {
  item.oldName = item.name;
  item.oldAddress = item.address;
  item.isEdit = true;
};

/**
 * Cancel edit operation
 * Restores original values and disables edit mode
 */
const cancelEdit = (item: ChannelData, index: number): void => {
  if (item.oldName !== undefined) {
    item.name = item.oldName;
  }
  if (item.oldAddress !== undefined) {
    item.address = item.oldAddress;
  }
  item.isEdit = false;
  formRefs[index].clearValidate();
};

// Lifecycle hooks
onMounted(() => {
  getReceiveSettingDetail();
});
</script>

<template>
  <div class="p-3.5 max-w-300">
    <Form
      v-for="(item, index) in dataSource"
      :key="index"
      :ref="(dom: any) => formRefs[index] = dom"
      :model="item"
      size="small"
      layout="vertical"
      class="flex space-x-5 mb-2">
      <!-- Channel name field -->
      <div class="flex-1/3 break-all">
        <FormItem
          :label="index === 0 && t('event.channel.columns.name')"
          :colon="false"
          name="name"
          :rules="[{ required: true, message: t('event.channel.messages.nameRequired') }]"
          class="text-theme-content">
          <Input
            v-model:value="item.name"
            :placeholder="t('event.channel.placeholder.inputName')"
            :maxlength="80"
            :disabled="!isChannelInEditMode(item)" />
        </FormItem>
      </div>

      <!-- Channel address field -->
      <div class="flex-2/3 break-all">
        <FormItem
          :label="index === 0 && t('event.channel.columns.address')"
          :colon="false"
          name="address"
          :rules="addressRule"
          class="text-theme-content flex-1">
          <div class="flex items-center">
            <!-- Email address input (tags mode) -->
            <Select
              v-if="props.channelType === 'EMAIL'"
              v-model:value="item.address"
              mode="tags"
              class="flex-1"
              :maxlength="100"
              :disabled="!isChannelInEditMode(item)"
              :placeholder="props.placeholder">
            </Select>

            <!-- Webhook/other address input -->
            <Input
              v-else
              v-model:value="item.address"
              :maxlength="1000"
              class="flex-1"
              :disabled="!isChannelInEditMode(item)"
              :placeholder="props.placeholder">
            </Input>

            <!-- Edit mode action buttons -->
            <div v-if="isChannelInEditMode(item) && item.id" class="pl-4">
              <a
                class="text-theme-special text-theme-text-hover text-3"
                @click="editHttpItemConfirm(index, item)">
                {{ t('common.actions.confirm') }}
              </a>
              <Divider type="vertical" />
              <a
                class="text-theme-special text-theme-text-hover text-3"
                @click="cancelEdit(item, index)">
                {{ t('common.actions.cancel') }}
              </a>
            </div>
          </div>
        </FormItem>
      </div>

      <!-- Action buttons -->
      <div class="w-80 pt-1 space-x-2.5" :class="{ 'mt-6': index === 0 }">
        <!-- Test channel button -->
        <ButtonAuth
          v-if="canTestChannel(item)"
          code="ChannelTest"
          type="link"
          icon="icon-zhihangceshi"
          @click="testConfig(index, item)" />

        <!-- Delete channel button -->
        <ButtonAuth
          v-if="canDeleteChannel(item)"
          code="ChannelDelete"
          type="link"
          icon="icon-lajitong"
          @click="deleteConfirm(item.id)" />

        <!-- Add new channel button -->
        <template v-if="isNewChannel(item)">
          <ButtonAuth
            code="ChannelAdd"
            type="link"
            icon="icon-tianjia"
            @click="addHttpItem(index, item)" />
        </template>

        <!-- Edit channel button -->
        <template v-if="canEditChannel(item)">
          <ButtonAuth
            code="ChannelEdit"
            type="link"
            icon="icon-shuxie"
            @click="editAddress(item)" />
        </template>
      </div>
    </Form>
  </div>
</template>
