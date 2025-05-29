<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { Badge, Popconfirm } from 'ant-design-vue';
import { Table, Card, Icon, IconCopy } from '@xcan-angus/vue-ui';

import { userToken } from '@/api';

interface Props {
  notify: string,
  tokenQuota: number
}

const props = withDefaults(defineProps<Props>(), {
  notify: undefined,
  tokenQuota: 3
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'change', text: number): void,
}>();

const { t } = useI18n();
const total = ref(0);
const loading = ref(true);
const state = reactive<{
  dataSource: { id: string, name: string, expireDate: string, createdDate: string, open?: boolean }[]
}>({
  dataSource: []
});

const loadToken = async () => {
  loading.value = true;
  const [error, res] = await userToken.getToken();
  loading.value = false;
  if (error || !res?.data) {
    return;
  }

  state.dataSource = res.data;
  total.value = res.data.length;
};

onMounted(() => {
  loadToken();
});

const deleteToken = async (record: any) => {
  const [error] = await userToken.deleteToken({ ids: [record.id] });
  if (error) {
    return;
  }

  total.value--;
  const _data = state.dataSource;
  for (let i = _data.length; i--;) {
    if (_data[i].id === record.id) {
      _data.splice(i, 1);
      break;
    }
  }
};

const showToken = async (record) => {
  if (record.token) {
    state.dataSource.forEach(item => {
      if (item.id === record.id) {
        item.open = true;
      }
    });
    return;
  }
  const [error, { data = {} }] = await userToken.getTokenValue(record.id);
  if (error) {
    return;
  }
  state.dataSource.forEach(item => {
    if (item.id === record.id) {
      item.open = true;
      item.token = data.value;
    }
  });
};

const closeToken = (id: string) => {
  state.dataSource.forEach(item => {
    if (item.id === id) {
      item.open = false;
    }
  });
};

const getIsExpired = (item) => {
  return new Date(item.expiredDate) < new Date();
};

watch(() => total.value, (newValue) => {
  emit('change', newValue);
}, {
  immediate: true
});

watch(() => props.notify, () => {
  loadToken();
});

const pagination = computed(() => {
  return {
    total: state.dataSource.length
  };
});

const columns = [
  {
    title: '名称',
    dataIndex: 'name',
    key: 'name',
    ellipsis: true,
    width: '25%'
  },
  {
    title: '令牌',
    dataIndex: 'token',
    key: 'token',
    ellipsis: true,
    width: '40%'
  },
  {
    title: '是否到期',
    dataIndex: 'expired',
    key: 'expired',
    ellipsis: true,
    width: '10%'
  },
  {
    title: '到期时间',
    dataIndex: 'expiredDate',
    key: 'expiredDate',
    ellipsis: true,
    width: '10%'
  },
  {
    title: '创建时间',
    dataIndex: 'createdDate',
    key: 'createdDate',
    ellipsis: true,
    width: '10%'
  },
  {
    title: '操作',
    dataIndex: 'action',
    width: '5%'
  }
];
</script>

<template>
  <Card
    :loading="loading"
    class="mt-2"
    bodyClass="pb-4 px-4">
    <template #title>{{ t('personalCenter.token.addTokenTotal', { total, maxNum: tokenQuota }) }}</template>
    <Table
      rowKey="value"
      :dataSource="state.dataSource"
      :columns="columns"
      :pagination="pagination"
      class="mt-3.5"
      size="small">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'action'">
          <Popconfirm
            :title="t('personalCenter.confirmDelete')"
            :okText="t('personalCenter.ok')"
            :cancelText="t('personalCenter.cancel')"
            @confirm="deleteToken(record)">
            <a
              class="text-3 content-primary-text text-theme-text-hover"
              href="javascript:;">{{ t('personalCenter.delete') }}</a>
          </Popconfirm>
        </template>
        <template v-if="column.dataIndex === 'token'">
          <template v-if="record.open">
            <div class="flex items-center space-x-2">
              <span class="truncate">{{ record.token }}</span>
              <IconCopy class="flex-shrink-0" :copyText="record.token" />
              <Icon
                icon="icon-zhengyan"
                class="flex-shrink-0 cursor-pointer text-3.5 leading-3.5"
                @click="closeToken(record.id)" />
            </div>
          </template>
          <template v-else>
            <Icon
              icon="icon-biyan"
              class="cursor-pointer text-3.5 leading-3.5"
              @click="showToken(record)" />
          </template>
        </template>
        <template v-if="column.dataIndex === 'expired'">
          <span>
            <Badge :color="getIsExpired(record) ? 'orange' : 'green'" />
            <span>{{ getIsExpired(record) ? '已到期' : '未到期' }}</span>
          </span>
        </template>
      </template>
    </Table>
  </Card>
</template>
