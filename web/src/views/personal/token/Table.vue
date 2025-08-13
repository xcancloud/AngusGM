<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { Badge, Popconfirm } from 'ant-design-vue';
import { Card, Icon, IconCopy, Table } from '@xcan-angus/vue-ui';

import { userToken } from '@/api';

// Define interfaces for better type safety
interface TokenRecord {
  id: string;
  name: string;
  expireDate: string;
  createdDate: string;
  open?: boolean;
  token?: string;
  expiredDate?: string;
}

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

// Define the data structure for token items
const state = reactive<{
  dataSource: TokenRecord[]
}>({
  dataSource: []
});

// Load token data from API
const loadToken = async () => {
  try {
    loading.value = true;
    const [error, res] = await userToken.getToken();

    if (error || !res?.data) {
      // Handle error case - could add toast notification here
      console.error('Failed to load tokens:', error);
      return;
    }

    state.dataSource = res.data;
    total.value = res.data.length;
  } catch (err) {
    console.error('Unexpected error loading tokens:', err);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadToken();
});

// Delete token by ID
const deleteToken = async (record: TokenRecord) => {
  try {
    const [error] = await userToken.deleteToken({ ids: [record.id] });

    if (error) {
      // Handle error case - could add toast notification here
      console.error('Failed to delete token:', error);
      return;
    }

    // Update local state after successful deletion
    total.value--;
    state.dataSource = state.dataSource.filter(item => item.id !== record.id);

    // Success case - could add success notification here
    console.log('Token deleted successfully');
  } catch (err) {
    console.error('Unexpected error deleting token:', err);
  }
};

// Show token value by fetching from API or revealing cached value
const showToken = async (record: TokenRecord) => {
  try {
    if (record.token) {
      // If token is already cached, just show it
      const item = state.dataSource.find(item => item.id === record.id);
      if (item) {
        item.open = true;
      }
      return;
    }

    // Fetch token value from API
    const [error, { data = {} }] = await userToken.getTokenValue(record.id);

    if (error) {
      // Handle error case - could add toast notification here
      console.error('Failed to fetch token value:', error);
      return;
    }

    // Update the record with token value and show it
    const item = state.dataSource.find(item => item.id === record.id);
    if (item) {
      item.open = true;
      item.token = data.value;
    }
  } catch (err) {
    console.error('Unexpected error showing token:', err);
  }
};

// Hide token value
const closeToken = (id: string) => {
  const item = state.dataSource.find(item => item.id === id);
  if (item) {
    item.open = false;
  }
};

// Check if token is expired
const getIsExpired = (item: TokenRecord) => {
  try {
    if (!item.expiredDate) {
      return false; // Consider not expired if no date is provided
    }

    const expiredDate = new Date(item.expiredDate);
    const currentDate = new Date();

    // Check if the date is valid
    if (isNaN(expiredDate.getTime())) {
      console.warn('Invalid expired date:', item.expiredDate);
      return false;
    }

    return expiredDate < currentDate;
  } catch (err) {
    console.error('Error checking token expiration:', err);
    return false; // Default to not expired on error
  }
};

// Watch for total changes and emit to parent
watch(() => total.value, (newValue) => {
  emit('change', newValue);
}, {
  immediate: true
});

// Reload token data when new token is created
watch(() => props.notify, () => {
  loadToken();
});

const pagination = computed(() => {
  return {
    total: state.dataSource.length
  };
});

// Define table columns configuration with proper typing
const columns = [
  {
    title: t('token.columns.name'),
    dataIndex: 'name',
    key: 'name',
    ellipsis: true,
    width: '22%'
  },
  {
    title: t('token.columns.token'),
    dataIndex: 'token',
    key: 'token',
    ellipsis: true,
    width: '32%'
  },
  {
    title: t('token.columns.expired'),
    dataIndex: 'expired',
    key: 'expired',
    ellipsis: true,
    width: '12%'
  },
  {
    title: t('token.columns.expiredDate'),
    dataIndex: 'expiredDate',
    key: 'expiredDate',
    ellipsis: true,
    width: '13%'
  },
  {
    title: t('token.columns.createdDate'),
    dataIndex: 'createdDate',
    key: 'createdDate',
    ellipsis: true,
    width: '13%'
  },
  {
    title: t('token.columns.action'),
    dataIndex: 'action',
    key: 'action',
    width: '8%'
  }
];
</script>

<template>
  <Card
    :loading="loading"
    class="mt-2"
    bodyClass="pb-4 px-4">
    <template #title>{{ t('token.addTokenTotal', { total, maxNum: tokenQuota }) }}</template>
    <Table
      rowKey="id"
      :dataSource="state.dataSource"
      :columns="columns"
      :pagination="pagination"
      class="mt-3.5"
      size="small"
      :noDataText="t('common.noData')"
      :noDataSize="'small'">
      <template #bodyCell="{ column, record }">
        <!-- Action column: Delete button with confirmation -->
        <template v-if="column.dataIndex === 'action'">
          <Popconfirm
            :title="t('confirmDelete')"
            :okText="t('ok')"
            :cancelText="t('cancel')"
            @confirm="deleteToken(record)">
            <a
              class="text-3 content-primary-text text-theme-text-hover"
              href="javascript:;">{{ t('delete') }}</a>
          </Popconfirm>
        </template>

        <!-- Token column: Show/hide token with copy functionality -->
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

        <!-- Expired status column: Show expiration status with color indicator -->
        <template v-if="column.dataIndex === 'expired'">
          <span>
            <Badge :color="getIsExpired(record) ? 'orange' : 'green'" />
            <span>{{ getIsExpired(record) ? t('token.status.expired') : t('token.status.notExpired') }}</span>
          </span>
        </template>
      </template>
    </Table>
  </Card>
</template>
