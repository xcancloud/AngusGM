<script setup lang='ts'>
import { ref, computed, onMounted, defineAsyncComponent } from 'vue';
import { useI18n } from 'vue-i18n';
import { Hints, Table, Input, Icon, AsyncComponent, IconRefresh, ButtonAuth } from '@xcan-angus/vue-ui';
import { debounce } from 'throttle-debounce';
import { duration, utils } from '@xcan-angus/tools';

import { UserTag, SearchParams } from './PropsType';
import { user } from '@/api';

const TagModal = defineAsyncComponent(() => import('@/components/TagModal/index.vue'));

interface Props {
  userId: string;
  hasAuth: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  userId: undefined,
  hasAuth: false
});

const { t } = useI18n();
const loading = ref(false);
const params = ref<SearchParams>({ pageNo: 1, pageSize: 10, filters: [] });
const total = ref(0);
const count = ref(0);
const isContUpdate = ref(true);
const dataList = ref<UserTag[]>([]);
const loadUserTag = async () => {
  if (loading.value) {
    return;
  }

  loading.value = true;
  const [error, { data = { list: [], total: 0 } }] = await user.getUserTag(props.userId, params.value);
  loading.value = false;
  if (error) {
    return;
  }

  dataList.value = data.list;
  total.value = +data.total;
  if (isContUpdate.value) {
    count.value = +data.total;
  }
};

const tagVisible = ref(false);
const addTag = () => {
  tagVisible.value = true;
};

const updateLoading = ref(false);
const isRefresh = ref(false);
const disabled = ref(false);
const tagSave = async (_tagIds: string[], _tags: { id: string, name: string }[], deleteTagIds: string[]) => {
  if (deleteTagIds.length) {
    await delUserTag(deleteTagIds, 'Modal');
  }
  if (_tagIds.length) {
    await addUserTag(_tagIds);
  }
  tagVisible.value = false;
  updateLoading.value = false;

  if (isRefresh.value) {
    disabled.value = true;
    await loadUserTag();
    disabled.value = false;
    isRefresh.value = false;
  }
};

const addUserTag = async (_tagIds: string[]) => {
  updateLoading.value = true;
  const [error] = await user.addUserTag(props.userId, _tagIds);
  if (error) {
    return;
  }
  isRefresh.value = true;
};

const delUserTag = async (_tagIds: string[], type?: 'Modal' | 'Table') => {
  updateLoading.value = true;
  const [error] = await user.deleteUserTag(props.userId, _tagIds);
  if (type === 'Table') {
    updateLoading.value = false;
  }
  if (error) {
    return;
  }

  if (type === 'Modal') {
    isRefresh.value = true;
  }

  params.value.pageNo = utils.getCurrentPage(params.value.pageNo as number, params.value.pageSize as number, total.value);

  if (type === 'Table') {
    disabled.value = true;
    await loadUserTag();
    disabled.value = false;
  }
};

const handleCancel = async (id) => {
  delUserTag([id], 'Table');
};

const handleSearch = debounce(duration.search, async (event: any) => {
  const value = event.target.value;
  params.value.pageNo = 1;
  if (value) {
    params.value.filters = [{ key: 'tagName', op: 'MATCH_END', value }];
  } else {
    params.value.filters = [];
  }

  disabled.value = true;
  isContUpdate.value = false;
  await loadUserTag();
  disabled.value = false;
  isContUpdate.value = true;
});

const pagination = computed(() => {
  return {
    current: params.value.pageNo,
    pageSize: params.value.pageSize,
    total: total.value
  };
});

const handleChange = async (_pagination) => {
  const { current, pageSize } = _pagination;
  params.value.pageNo = current;
  params.value.pageSize = pageSize;
  disabled.value = true;
  await loadUserTag();
  disabled.value = false;
};

onMounted(() => {
  loadUserTag();
});

const columns = [
  {
    title: 'ID',
    dataIndex: 'tagId',
    width: '20%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('name'),
    dataIndex: 'tagName',
    width: '30%'
  },
  {
    title: t('associatedTime'),
    dataIndex: 'createdDate',
    width: '20%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    title: t('associatedPerson'),
    dataIndex: 'createdByName',
    width: '20%'
  },
  {
    title: t('operation'),
    dataIndex: 'action',
    width: '10%',
    align: 'center'
  }
];

const hintTip = t(`每个用户最多允许关联10个标签，当前用户已关联${count.value}个标签。`);
</script>
<template>
  <div>
    <Hints :text="hintTip" class="mb-1" />
    <div class="flex items-center justify-between mb-2">
      <Input
        placeholder="查询标签名称"
        class="w-60"
        size="small"
        allowClear
        @change="handleSearch">
        <template #suffix>
          <Icon class="text-theme-content text-theme-text-hover text-3 leading-3" icon="icon-sousuo" />
        </template>
      </Input>
      <div class="flex space-x-2 items-center">
        <ButtonAuth
          code="UserTagsAssociate"
          type="primary"
          icon="icon-tianjia"
          :disabled="hasAuth || total>=10"
          @click="addTag" />
        <IconRefresh
          :loading="loading"
          :disabled="disabled"
          @click="loadUserTag" />
      </div>
    </div>
    <Table
      size="small"
      rowKey="id"
      :loading="loading"
      :dataSource="dataList"
      :columns="columns"
      :pagination="pagination"
      @change="handleChange">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'action'">
          <ButtonAuth
            code="UserTagsUnassociate"
            type="text"
            :disabled="hasAuth"
            icon="icon-quxiao"
            @click="handleCancel(record.tagId)" />
        </template>
      </template>
    </Table>
  </div>
  <AsyncComponent :visible="tagVisible">
    <TagModal
      v-if="tagVisible"
      v-model:visible="tagVisible"
      :userId="userId"
      :updateLoading="updateLoading"
      type="User"
      @change="tagSave" />
  </AsyncComponent>
</template>
