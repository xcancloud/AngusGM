<script setup lang='ts'>
import { defineAsyncComponent, nextTick, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Pagination, Tag, Tooltip } from 'ant-design-vue';
import { AsyncComponent, ButtonAuth, IconRefresh, Input, modal, notification, PureCard, Spin } from '@xcan-angus/vue-ui';
import { app, duration } from '@xcan-angus/infra';
import { debounce } from 'throttle-debounce';

import { OrgTag, SearchParams } from '../../PropsType';

import { orgTag } from '@/api';

const AddModal = defineAsyncComponent(() => import('@/views/organization/tag/components/add/index.vue'));

const emit = defineEmits<{
  (e: 'update:tag', tag: OrgTag): void,
  (e: 'update:tenantName', name: string): void,
  (e: 'update'): void
}>();

const { t } = useI18n();

const loading = ref(false);
const disabled = ref(false);
const params = ref<SearchParams>({ pageNo: 1, pageSize: 20, filters: [], fullTextSearch: true });
const total = ref(0);
const tagList = ref<OrgTag[]>([]);
const checkedTag = ref<OrgTag | undefined>(undefined); // 选择的标签
const init = () => {
  loadTagList();
};

const loadTagList = async (): Promise<void> => {
  if (loading.value) {
    return;
  }

  loading.value = true;
  const [error, { data }] = await orgTag.getTagList(params.value);
  loading.value = false;
  if (error) {
    return;
  }

  tagList.value = data?.list?.map(item => ({ ...item, isEdit: false }));
  total.value = +data?.total;
  if (tagList.value.length && !checkedTag.value) {
    checkedTag.value = tagList.value[0];
    emit('update:tag', tagList.value[0]);
  }
};

const deleteConfirm = (id: string, name: string): void => {
  modal.confirm({
    centered: true,
    title: t('删除标签'),
    content: t(`确定删除【${name}】吗?`),
    async onOk () {
      await deleteTag(id);
    }
  });
};
const deleteTag = async (id: string): Promise<void> => {
  const params = {
    ids: [id]
  };
  const [error] = await orgTag.deleteTag(params);
  if (error) {
    return;
  }

  if (checkedTag.value?.id === id) {
    checkedTag.value = undefined;
  }
  notification.success('删除成功');
  disabled.value = true;
  await loadTagList();
  disabled.value = false;
};

const handleSearch = debounce(duration.search, async (event: any) => {
  const value = event.target.value;
  if (value) {
    params.value.filters = [{ key: 'name', value: value, op: 'MATCH_END' }];
  } else {
    params.value.filters = [];
  }
  disabled.value = true;
  await loadTagList();
  disabled.value = false;
});

const addVisible = ref(false);
const addTag = () => {
  addVisible.value = true;
};

const addLoading = ref(false);
const addOK = async (name: string | undefined) => {
  if (!name || loading.value) {
    return;
  }
  const params = [{ name }];
  addLoading.value = true;
  const [error] = await orgTag.addTag(params);
  addLoading.value = false;
  addVisible.value = false;
  if (error) {
    return;
  }
  notification.success(t('common.messages.addSuccess'));
  disabled.value = true;
  await loadTagList();
  disabled.value = false;
};

const handleRefresh = () => {
  loadTagList();
};

const pageSizeOptions = ['10', '20', '30', '40', '50'];

const paginationChange = async (page: number, size: number) => {
  params.value.pageNo = page;
  params.value.pageSize = size;
  disabled.value = true;
  await loadTagList();
  disabled.value = false;
};

const editNameLoading = ref(false);
const inputRef = ref();
let timer;
const openEditName = (tag: OrgTag) => {
  clearTimeout(timer);
  for (let i = 0; i < tagList.value.length; i++) {
    if (tag.id !== tagList.value[i].id) {
      tagList.value[i].isEdit = false;
    } else {
      tagList.value[i].isEdit = true;
    }
  }

  nextTick(() => {
    inputRef.value[0]?.focus();
  });
};

const editName = async (event, item) => {
  const value = event.target.value;
  const nams = tagList.value.map(item => item.name);
  if (nams.includes(value) || !value) {
    item.isEdit = false;
    return;
  }

  editNameLoading.value = true;
  const [error] = await orgTag.updateTag([{ id: item.id, name: value }]);
  editNameLoading.value = false;
  item.isEdit = false;
  if (error) {
    return;
  }

  const [error1, { data }] = await orgTag.getDetail(item.id);
  if (error1) {
    return;
  }

  const index = tagList.value.findIndex(f => f.id === item.id);
  if (index > -1) {
    tagList.value[index] = data;
  }

  if (checkedTag.value?.id === item.id) {
    emit('update:tag', data);
  }
};

const selectTag = (value: OrgTag): void => {
  clearTimeout(timer);
  timer = setTimeout(() => {
    emit('update:tag', value);
    checkedTag.value = value;
  }, 400);
};

onMounted(() => {
  init();
});

defineExpose({ openEditName });
</script>
<template>
  <PureCard class="pr-0 flex flex-col justify-between p-3.5 w-100">
    <div class="flex items-center mb-2 space-x-2 mr-3.5">
      <Input
        size="small"
        allowClear
        :placeholder="t('tagPlaceholder1')"
        @change="handleSearch" />
      <ButtonAuth
        code="TagAdd"
        type="primary"
        icon="icon-tianjia"
        @click="addTag" />
      <IconRefresh
        :loading="loading"
        :disabled="disabled"
        @click="handleRefresh" />
    </div>
    <Spin :spinning="loading" class="flex-1 overflow-hidden hover:overflow-y-auto">
      <template
        v-for="item in tagList"
        :key="item.id">
        <template v-if="item.isEdit">
          <Input
            ref="inputRef"
            :value="item.name "
            :maxlength="100"
            class="mr-3.5 mb-2"
            size="small"
            style="width: 370px;"
            :placeholder="t('pubPlaceholder',{name:t('name'),num:100})"
            @blur="editName($event,item)"
            @pressEnter="editName($event, item)" />
        </template>
        <template v-else>
          <Tag
            :closable="app.has('TagDelete')"
            class="mb-0.75 truncate cursor-pointer"
            :class="{'border-theme-divider-selected':item.id === checkedTag?.id}"
            style="max-width: 372px;"
            @close.prevent="deleteConfirm(item.id,item.name)"
            @click="selectTag(item)"
            @dblclick="app.has('TagModify') && openEditName(item)">
            <template v-if="item.name.length<=15">
              {{ item.name }}
            </template>
            <template v-else>
              <Tooltip
                :title="item.name"
                placement="bottomLeft">
                {{ item.name.slice(0,15) }}...
              </Tooltip>
            </template>
          </Tag>
        </template>
      </template>
    </Spin>
    <Pagination
      :current="params.pageNo"
      :pageSize="params.pageSize"
      :pageSizeOptions="pageSizeOptions"
      :total="total"
      showLessItems
      :hideOnSinglePage="false"
      :showTotal="false"
      :showSizeChanger="false"
      size="small"
      class="text-right mr-4.5 mt-2"
      @change="paginationChange" />
  </PureCard>
  <AsyncComponent :visible="addVisible">
    <AddModal
      v-if="addVisible"
      v-model:visible="addVisible"
      :loading="addLoading"
      @ok="addOK" />
  </AsyncComponent>
</template>
<style scoped>
.border-theme-divider-selected {
  border-color: var(--border-divider-selected);
}

@keyframes circle {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

.circle-move {
  animation-name: circle;
  animation-duration: 1000ms;
  animation-timing-function: linear;
  animation-iteration-count: infinite;
  animation-direction: normal;
}
</style>
