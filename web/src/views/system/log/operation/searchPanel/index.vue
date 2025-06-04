<script lang="ts" setup>
import { computed, inject, onMounted, ref } from 'vue';
import { Colon, IconCount, IconRefresh, SearchPanel } from '@xcan-angus/vue-ui';
import dayjs, { Dayjs } from 'dayjs';
import { useI18n } from 'vue-i18n';
import { Tooltip } from 'ant-design-vue';

const { t } = useI18n();

interface Props {
  loading: boolean;
  showCount?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loading: false
});

const emits = defineEmits<{(e: 'change', value: {
    orderBy?: string;
    orderSort?: 'ASC'|'DESC';
    filters: {key: string; op: string; value: string|string[]}[];
  }):void,
  (e: 'refresh'):void;
  (e: 'openCount'):void;}>();
const tenantInfo = inject('tenantInfo', ref({ id: '' }));

const searchPanelRef = ref();
const selectedMenuMap = ref<{[key: string]: boolean}>({});

const searchPanelOptions = [
  {
    valueKey: 'description',
    placeholder: t('查询操作描述'),
    type: 'input',
    allowClear: true
  },
  {
    valueKey: 'userId',
    type: 'select-user',
    allowClear: true
  },
  {
    valueKey: 'optDate',
    type: 'date',
    placeholder: t('操作时间')
  }
];

const menuItems = computed(() => [
  {
    key: '',
    name: '全部'
  },
  {
    key: 'userId',
    name: '我的操作'
  },
  {
    key: 'lastDay',
    name: '近1天'
  },
  {
    key: 'lastThreeDays',
    name: '近3天'
  },
  {
    key: 'lastWeek',
    name: '近7天'
  }
]);

const orderBy = ref();
const orderSort = ref();
const searchFilters = ref<{key: string; op: string; value: string|string[]}[]>([]);
const quickSearchFilters = ref<{key: string; op: string; value: string|string[]}[]>([]);
const assocFilters = ref<{key: string; op: string; value: string|string[]}[]>([]);
const assocKeys = ['userId', 'optDate'];
const timeKeys = ['lastDay', 'lastThreeDays', 'lastWeek'];

const formatDateString = (key: string) => {
  let startDate: Dayjs | undefined;
  let endDate: Dayjs | undefined;

  if (key === 'lastDay') {
    startDate = dayjs().startOf('date');
    endDate = dayjs();
  }

  if (key === 'lastThreeDays') {
    startDate = dayjs().startOf('date').subtract(3, 'day').add(1, 'day');
    endDate = dayjs();
  }

  if (key === 'lastWeek') {
    startDate = dayjs().startOf('date').subtract(1, 'week').add(1, 'day');
    endDate = dayjs();
  }

  return [startDate ? startDate.format('YYYY-MM-DD HH:mm:ss') : '', endDate ? endDate.format('YYYY-MM-DD HH:mm:ss') : ''];

  // return [
  //   startDate ? {
  //     value: startDate.format('YYYY-MM-DD HH:mm:ss'),
  //     op: 'GREATER_THAN_EQUAL',
  //     key: 'createdDate'
  //   } : '',
  //   endDate ? {
  //     value: endDate.format('YYYY-MM-DD HH:mm:ss'),
  //     op: 'LESS_THAN_EQUAL',
  //     key: 'createdDate'
  //   }  : ''].filter(Boolean);
};

const getParams = () => {
  return {
    filters: [
      ...quickSearchFilters.value,
      ...searchFilters.value,
      ...assocFilters.value
    ],
    orderBy: orderBy.value,
    orderSort: orderSort.value
  };
};

const searchChange = (data: {key: string; op: string; value: string|string[]}[]) => {
  searchFilters.value = data.filter(item => !assocKeys.includes(item.key));
  assocFilters.value = data.filter(item => assocKeys.includes(item.key));

  if (!assocFilters.value.length) {
    assocKeys.forEach(i => {
      if (i === 'optDate') {
        timeKeys.forEach(t => delete selectedMenuMap.value[t]);
      } else {
        delete selectedMenuMap.value[i];
      }
    });
  } else {
    assocKeys.forEach(key => {
      if (key === 'userId') {
        const filterItem = assocFilters.value.find(i => i.key === key);
        if (!filterItem || filterItem.value !== tenantInfo.value?.id) {
          delete selectedMenuMap.value[key];
        }
      }
      if (key === 'optDate') {
        const filterItem = assocFilters.value.filter(i => i.key === key);
        const timeKey = timeKeys.find(t => selectedMenuMap.value[t]);
        if (timeKey) {
          const timeValue = formatDateString(timeKey);
          if (timeValue[0] !== filterItem[0].value || timeValue[1] !== filterItem[1].value) {
            delete selectedMenuMap.value[timeKey];
          }
        }
      }
    });
  }

  emits('change', getParams());
};
const toSort = (sortData) => {
  orderBy.value = sortData.orderBy;
  orderSort.value = sortData.orderSort;
  emits('change', getParams());
};

const menuItemClick = (data) => {
  const key = data.key;
  // const statusTypeKeys = planStatusTypeOpt.value.map(i => i.key);
  let searchChangeFlag = false;
  if (selectedMenuMap.value[key]) {
    delete selectedMenuMap.value[key];
    if (timeKeys.includes(key) && assocKeys.includes('createdDate')) {
      searchPanelRef.value.setConfigs([
        { valueKey: 'createdDate', value: undefined }
      ]);
      searchChangeFlag = true;
    } else if (assocKeys.includes(key)) {
      searchPanelRef.value.setConfigs([
        { valueKey: key, value: undefined }
      ]);
      searchChangeFlag = true;
    }
  } else {
    if (key === '') {
      selectedMenuMap.value = { '': true };
      quickSearchFilters.value = [];
      // 清空搜索面板
      if (typeof searchPanelRef.value?.clear === 'function') {
        searchPanelRef.value.clear();
        searchChangeFlag = true;
      }
    } else {
      delete selectedMenuMap.value[''];
    }
    if (timeKeys.includes(key)) {
      timeKeys.forEach(timeKey => delete selectedMenuMap.value[timeKey]);
      selectedMenuMap.value[key] = true;
    } else {
      selectedMenuMap.value[key] = true;
    }
  }
  const userId = tenantInfo.value?.id;
  const timeFilters: {key: string; op: string; value: string}[] = [];
  const assocFiltersInQuick = [];
  quickSearchFilters.value = Object.keys(selectedMenuMap.value).map(key => {
    if (key === '') {
      return undefined;
    } else if (['lastDay', 'lastThreeDays', 'lastWeek'].includes(key)) {
      // timeFilters = formatDateString(key);
      assocFiltersInQuick.push({ valueKey: 'optDate', value: formatDateString(key) });
      return undefined;
    } else if (assocKeys.includes(key)) {
      if (key === 'userId') {
        assocFiltersInQuick.push({ valueKey: key, value: userId });
      }
      return undefined;
    } else {
      return {
        key,
        op: 'EQUAL',
        value: userId
      };
    }
  }).filter(Boolean);
  quickSearchFilters.value.push(...timeFilters);
  if (assocFiltersInQuick.length) {
    searchPanelRef.value.setConfigs([
      ...assocFiltersInQuick
    ]);
    searchChangeFlag = true;
  }
  if (!searchChangeFlag) {
    emits('change', getParams());
  }
};

const refresh = () => {
  emits('refresh');
};

const openCount = () => {
  emits('openCount');
};

onMounted(() => {
  // loadStatusEnum();
});
</script>
<template>
  <div class="mt-2.5 mb-3.5">
    <div class="flex">
      <div class="whitespace-nowrap text-3 text-text-sub-content transform-gpu translate-y-0.5">
        <span>快速查询</span>
        <Colon />
      </div>
      <div class="flex  flex-wrap ml-2 text-3">
        <div
          v-for="item in menuItems"
          :key="item.key"
          :class="{ 'active-key': selectedMenuMap[item.key] }"
          class="px-2.5 h-6 leading-6 mr-3 mb-3 rounded bg-gray-light cursor-pointer"
          @click="menuItemClick(item)">
          {{ item.name }}
        </div>
      </div>
    </div>
    <div class="flex items-start justify-between ">
      <SearchPanel
        ref="searchPanelRef"
        :options="searchPanelOptions"
        class="flex-1 mr-3.5"
        @change="searchChange" />

      <div class="inline-flex items-center space-x-3 h-7">
        <Tooltip
          arrowPointAtCenter
          placement="topLeft"
          :title="props.showCount?'收起统计':'查看统计'">
          <IconCount
            :value="props.showCount"
            class="flex-none text-4.5"
            @click="openCount" />
        </Tooltip>
        <Tooltip
          arrowPointAtCenter
          placement="topLeft"
          title="刷新">
          <IconRefresh
            :loading="loading"
            class="text-4.5 ml-2"
            @click="refresh" />
        </Tooltip>
      </div>
    </div>
  </div>
</template>
<style scoped>
.active-key {
  background-color: #4ea0fd;
  color: #fff;
}
</style>
