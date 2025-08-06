<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { Divider } from 'ant-design-vue';
import { ButtonAuth, IconRefresh, Input, notification, PureCard, Select, SelectEnum, Table } from '@xcan-angus/vue-ui';
import { GM, SupportedLanguage } from '@xcan-angus/infra';

import { sms } from '@/api';
import { _columns, Options, Template } from './PropsType';

const { t } = useI18n();
const loading = ref<boolean>(false);
const disabled = ref<boolean>(true);
const state: { dataSource: Template[], options: Options[] } = reactive({ dataSource: [], options: [] });
const params: {
  channelId: string | undefined,
  language: string | undefined,
  pageNo: number,
  pageSize: number,
} = reactive({
  channelId: undefined,
  language: undefined,
  pageNo: 1,
  pageSize: 10
});
const pageTotal = ref<number>(10);

const changePagination = (_pagination) => {
  const { current, pageSize } = _pagination;
  params.pageNo = current;
  params.pageSize = pageSize;
};

const columns = computed(() => {
  return _columns.map((item: any) => {
    return {
      ...item,
      title: t(item.title)
    };
  });
});

const loadSmsTemplates = async (): Promise<void> => {
  loading.value = true;
  const [error, res] = await sms.getTemplates(params);
  loading.value = false;
  if (error || !res.data) {
    return;
  }
  pageTotal.value = parseInt(res.data.total);
  state.dataSource = getNewSmsTemplates(res.data.list);
};

const getNewSmsTemplates = (values: Template[]): Template[] => {
  if (values.length) {
    return values.map((item) => {
      return {
        ...item,
        showEdit: false
      };
    });
  }
  return [];
};

const handleEdit = (record: Template): void => {
  record.showEdit = true;
  const { name, thirdCode, language, signature, content } = record;
  record.editValues = {
    name,
    thirdCode,
    language,
    signature,
    content
  };
};

const handleCancel = (record: Template): void => {
  record.showEdit = false;
};

const saveEdit = (record: Template, _columns, checkPassed = true): void => {
  const editValueKeys = ['name', 'thirdCode', 'language', 'signature', 'content'];
  editValueKeys.forEach(key => {
    if (!record.editValues[key]) {
      checkPassed = false;
      _columns.forEach(e => {
        if (key === e.key) {
          notification.error(e.title + t('smsConfig.isNull'));
        }
      });
    }
  });
  if (editValueKeys.every(key => record[key] === record.editValues[key])) {
    record.showEdit = false;
    return;
  }

  if (checkPassed) {
    handleEditTemplate(record);
  }
};

const handleEditTemplate = async (record: Template): Promise<void> => {
  loading.value = true;
  const [error] = await sms.updateTemplate(record.id, record.editValues);
  loading.value = false;
  if (error) {
    return;
  }
  const { name, thirdCode, language, signature, content } = record.editValues;
  record.name = name;
  record.thirdCode = thirdCode;
  record.language = language;
  record.signature = signature;
  record.content = content;
  record.showEdit = false;
  notification.success(t('saveSuccess'));
};

const pagination = computed(() => {
  return {
    current: params.pageNo,
    pageSize: params.pageSize,
    total: pageTotal.value
  };
});

watch(() => params, async () => {
  disabled.value = true;
  await loadSmsTemplates();
  disabled.value = false;
}, {
  deep: true
});

onMounted(() => {
  loadSmsTemplates();
});
</script>
<template>
  <PureCard class="min-h-full p-3.5">
    <div class="flex items-center justify-between">
      <div class="flex items-center">
        <Select
          v-model:value="params.channelId"
          :action="`${GM}/sms/channel`"
          :placeholder="t('请选择通道')"
          allowClear
          :fieldNames="{label:'name',value:'id'}"
          class="w-80 mr-2"
          size="small" />
        <SelectEnum
          v-model:value="params.language"
          :placeholder="t('selectLanguage')"
          internal
          allowClear
          :enumKey="SupportedLanguage"
          class="w-80"
          size="small" />
      </div>
      <IconRefresh
        class="ml-2"
        :loading="loading"
        :disabled="disabled"
        @click="loadSmsTemplates" />
    </div>
    <Table
      class="mt-2"
      rowKey="id"
      size="small"
      :dataSource="state.dataSource"
      :columns="columns"
      :loading="loading"
      :pagination="pagination"
      @change="changePagination">
      <template #bodyCell="{ column,text, record }">
        <template v-if="column.key === 'name'">
          <span v-if="record.showEdit">
            <Input v-model:value="record.editValues.name" />
          </span>
          <span v-else>{{ record.name }}</span>
        </template>
        <template v-if="column.key=== 'thirdCode'">
          <span v-if="record.showEdit">
            <Input v-model:value="record.editValues.thirdCode" />
          </span>
          <span v-else>{{ record.thirdCode }}</span>
        </template>
        <template v-if="column.key === 'language'">
          <span v-if="record.showEdit">
            <SelectEnum
              v-model:value="record.editValues.language"
              :enumKey="SupportedLanguage" />
          </span>
          <span v-else>{{ text.message }}</span>
        </template>
        <template v-if="column.key === 'signature'">
          <span v-if="record.showEdit">
            <Input v-model:value="record.editValues.signature" class="" />
          </span>
          <span v-else>{{ record.signature }}</span>
        </template>
        <template v-if="column.key === 'content'">
          <span v-if="record.showEdit">
            <Input v-model:value="record.editValues.content" />
          </span>
          <div v-else class="w-full overflow-ellipsis overflow-hidden">{{ record.content }}</div>
        </template>
        <template v-if="column.key === 'operate'">
          <div v-if="!record.showEdit">
            <ButtonAuth
              code="SMSTemplateModify"
              type="text"
              icon="icon-shuxie"
              iconStyle="font-size:12px;"
              @click="handleEdit(record)" />
          </div>
          <div v-else>
            <a class="text-theme-text-hover" @click="saveEdit(record, columns)">{{ t('save') }}</a>
            <Divider type="vertical" />
            <a class="text-theme-text-hover" @click="handleCancel(record)">{{ t('cancel') }}</a>
          </div>
        </template>
      </template>
    </Table>
  </PureCard>
</template>
