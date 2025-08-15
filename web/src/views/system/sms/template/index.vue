<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { Divider } from 'ant-design-vue';
import { ButtonAuth, IconRefresh, Input, notification, PureCard, Select, SelectEnum, Table } from '@xcan-angus/vue-ui';
import { GM, SupportedLanguage } from '@xcan-angus/infra';

import { sms } from '@/api';
import { Template, TemplateState, TemplateQueryParams, PaginationConfig } from './types';
import {
  createSmsTemplateColumns, processSmsTemplates, enableTemplateEdit, cancelTemplateEdit,
  validateTemplateEdit, hasTemplateChanges, updateTemplateValues, createPaginationConfig
} from './utils';

const { t } = useI18n();

// Reactive state management
const loading = ref<boolean>(false);
const disabled = ref<boolean>(true);
const state = reactive<TemplateState>({
  dataSource: [],
  options: []
});

// Query parameters for SMS templates
const params = reactive<TemplateQueryParams>({
  channelId: undefined,
  language: undefined,
  pageNo: 1,
  pageSize: 10
});

const pageTotal = ref<number>(10);

/**
 * Handle pagination changes
 * @param _pagination - Pagination object from table
 */
const changePagination = (_pagination: any): void => {
  const { current, pageSize } = _pagination;
  params.pageNo = current;
  params.pageSize = pageSize;
};

/**
 * Load SMS templates from API
 */
const loadSmsTemplates = async (): Promise<void> => {
  loading.value = true;
  try {
    const [error, res] = await sms.getTemplates(params);
    if (error || !res.data) {
      return;
    }
    pageTotal.value = parseInt(res.data.total);
    state.dataSource = processSmsTemplates(res.data.list);
  } finally {
    loading.value = false;
  }
};

/**
 * Save template edits after validation
 * @param record - Template record to save
 */
const saveEdit = (record: Template): void => {
  const validation = validateTemplateEdit(record, t);

  if (!validation.isValid) {
    validation.errors.forEach(error => {
      notification.error(error);
    });
    return;
  }

  // Check if any values have changed
  if (!hasTemplateChanges(record)) {
    record.showEdit = false;
    return;
  }

  handleEditTemplate(record);
};

/**
 * Update template via API
 * @param record - Template record to update
 */
const handleEditTemplate = async (record: Template): Promise<void> => {
  loading.value = true;
  try {
    const [error] = await sms.updateTemplate(record.id, record.editValues);
    if (error) {
      return;
    }

    // Update local state with new values
    updateTemplateValues(record);
    notification.success(t('sms.messages.saveSuccess'));
  } finally {
    loading.value = false;
  }
};

// Computed pagination object
const pagination = computed<PaginationConfig>(() => {
  return createPaginationConfig(params.pageNo, params.pageSize, pageTotal.value);
});

// Create table columns using utility function
const columns = computed(() => createSmsTemplateColumns(t));

// Watch for parameter changes and reload data
watch(() => params, async () => {
  disabled.value = true;
  await loadSmsTemplates();
  disabled.value = false;
}, {
  deep: true
});

// Lifecycle hooks
onMounted(() => {
  loadSmsTemplates();
});
</script>

<template>
  <PureCard class="min-h-full p-3.5">
    <!-- Search and filter controls -->
    <div class="flex items-center justify-between">
      <div class="flex items-center">
        <!-- Channel selection -->
        <Select
          v-model:value="params.channelId"
          :action="`${GM}/sms/channel`"
          :placeholder="t('sms.placeholder.selectChannel')"
          allowClear
          :fieldNames="{label:'name',value:'id'}"
          class="w-80 mr-2"
          size="small" />

        <!-- Language selection -->
        <SelectEnum
          v-model:value="params.language"
          :placeholder="t('sms.placeholder.selectLanguage')"
          internal
          allowClear
          :enumKey="SupportedLanguage"
          class="w-80"
          size="small" />
      </div>

      <!-- Refresh button -->
      <IconRefresh
        class="ml-2"
        :loading="loading"
        :disabled="disabled"
        @click="loadSmsTemplates" />
    </div>

    <!-- Templates table -->
    <Table
      class="mt-2"
      rowKey="id"
      size="small"
      :dataSource="state.dataSource"
      :columns="columns"
      :loading="loading"
      :pagination="pagination"
      @change="changePagination">
      <!-- Custom cell rendering -->
      <template #bodyCell="{ column, text, record }">
        <!-- Template name column -->
        <template v-if="column.key === 'name'">
          <span v-if="record.showEdit">
            <Input v-model:value="record.editValues.name" />
          </span>
          <span v-else>{{ record.name }}</span>
        </template>

        <!-- Third-party code column -->
        <template v-if="column.key === 'thirdCode'">
          <span v-if="record.showEdit">
            <Input v-model:value="record.editValues.thirdCode" />
          </span>
          <span v-else>{{ record.thirdCode }}</span>
        </template>

        <!-- Language column -->
        <template v-if="column.key === 'language'">
          <span v-if="record.showEdit">
            <SelectEnum
              v-model:value="record.editValues.language"
              :enumKey="SupportedLanguage" />
          </span>
          <span v-else>{{ text.message }}</span>
        </template>

        <!-- Signature column -->
        <template v-if="column.key === 'signature'">
          <span v-if="record.showEdit">
            <Input v-model:value="record.editValues.signature" />
          </span>
          <span v-else>{{ record.signature }}</span>
        </template>

        <!-- Content column -->
        <template v-if="column.key === 'content'">
          <span v-if="record.showEdit">
            <Input v-model:value="record.editValues.content" />
          </span>
          <div v-else class="w-full overflow-ellipsis overflow-hidden">
            {{ record.content }}
          </div>
        </template>

        <!-- Operations column -->
        <template v-if="column.key === 'operate'">
          <div v-if="!record.showEdit">
            <!-- Edit button -->
            <ButtonAuth
              code="SMSTemplateModify"
              type="text"
              icon="icon-shuxie"
              iconStyle="font-size:12px;"
              @click="enableTemplateEdit(record)" />
          </div>
          <div v-else>
            <!-- Save and cancel buttons -->
            <a class="text-theme-text-hover" @click="saveEdit(record)">
              {{ t('sms.buttons.save') }}
            </a>
            <Divider type="vertical" />
            <a class="text-theme-text-hover" @click="cancelTemplateEdit(record)">
              {{ t('sms.buttons.cancel') }}
            </a>
          </div>
        </template>
      </template>
    </Table>
  </PureCard>
</template>
