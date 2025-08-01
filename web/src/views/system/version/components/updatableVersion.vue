<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { Card, Colon, Grid, NoData } from '@xcan-angus/vue-ui';
import { appContext } from '@xcan-angus/infra';
import { Skeleton } from 'ant-design-vue';

import { UpgradeableVersion } from '../PropsType';
import { edition } from '@/api';

// TODO 删除多主题内容
import darkImg from '../images/dark2.png';
import grayImg from '../images/gray2.png';

interface Props {
  currVersion: string;
  installGoodsCode: string;
}

const props = withDefaults(defineProps<Props>(), {
  currVersion: '',
  installGoodsCode: ''
});

const { t } = useI18n();
const loading = ref<boolean>(true);
const showExcpetion = ref<boolean>(false);
const editionType = ref<string>();

const varsionData = ref<UpgradeableVersion>();

const init = async () => {
  editionType.value = appContext.getEditionType();
};

const loadUpgradeableVersion = async (): Promise<void> => {
  const [error, { data }] = await edition.getEditionUpgradeable({ goodsCode: props.installGoodsCode });
  loading.value = false;
  if (error) {
    showExcpetion.value = true;
    return;
  }

  if (!data.length) {
    return;
  }

  varsionData.value = data[0];
  if (!varsionData.value?.features?.length) {
    return;
  }
  // 如果特性无数据，特性不需要展示
  varsionData.value.upgradeable ? updateColumns(upgradeableColumns.value[0]) : updateColumns(currentVersionColumns.value[0]);
};

const updateColumns = (_columns) => {
  for (let i = 0; i < _columns.length; i++) {
    if (_columns[i].dataIndex === 'features') {
      _columns[i].hide = true;
      break;
    }
  }
};

const getImg = computed(() => {
  const theme = document.body.className;
  // TODO 删除多主题内容
  switch (theme) {
    case 'dark-theme':
      return darkImg;
    case 'gray-theme' || 'light-theme':
      return grayImg;
    default:
      return grayImg;
  }
});

watch(() => props.installGoodsCode, (newValue) => {
  if (newValue) {
    loadUpgradeableVersion();
  }
}, { immediate: true });

onMounted(() => {
  init();
});

// upgradeable是false || null,只展示介绍和特性(返回数据为当前版本的)
const currentVersionColumns = ref([
  [
    {
      label: t('版本介绍'),
      dataIndex: 'introduction'
    },
    {
      label: t('特性'),
      dataIndex: 'features',
      hide: true
    },
    {
      label: t('当前版本'),
      dataIndex: 'currVersion'
    }
  ]
]);

// upgradeable是true, 展示版本的介绍、特性、当前版本号、新版本号，并且可跳转去升级
const upgradeableColumns = ref([
  [
    {
      label: t('版本介绍'),
      dataIndex: 'introduction'
    },
    {
      label: t('特性'),
      dataIndex: 'features',
      hide: true
    },
    {
      label: t('当前版本'),
      dataIndex: 'currVersion'
    }
  ]
]);
</script>
<template>
  <Card bodyClass="flex px-8 py-5 space-x-20 pr-30" class="flex-1">
    <template #title>
      <template v-if="editionType === 'CLOUD_SERVICE'">
        最新版本
      </template>
      <template v-else>
        <template v-if="varsionData && varsionData?.upgradeable">
          可升级版本
        </template>
        <template v-else>
          版本介绍
        </template>
      </template>
    </template>
    <template #default>
      <img
        :src="getImg"
        class="w-60 h-60">
      <Skeleton
        active
        :loading="loading"
        :title="false"
        :paragraph="{ rows: 8 }">
        <template v-if="editionType !== 'CLOUD_SERVICE'">
          <template v-if="!showExcpetion">
            <template v-if="varsionData">
              <Grid
                class="introduction w-150"
                :columns="varsionData.upgradeable?upgradeableColumns:currentVersionColumns"
                :dataSource="varsionData">
                <template #features="{text}">
                  <p
                    v-for="item,index in text.slice(0,3)"
                    :key="index">
                    {{ item }}
                  </p>
                  <p v-if="text?.length>3">...</p>
                </template>
                <template #currVersion>
                  {{ props.currVersion || '--' }}
                  <span style="font-size: 12px;line-height: 20px;" class="ml-20">最新版本</span>
                  <Colon class="mr-2" />
                  <span>{{ varsionData?.version }}</span>
                  <!-- TODO herf 没有加私有化去升级地址-->
                  <!-- <a class="ml-5 text-theme-text-hover text-theme-special">去升级</a> -->
                </template>
              </Grid>
            </template>
            <template v-else>
              <div class="flex flex-col justify-center">
                <NoData />
              </div>
            </template>
          </template>
          <template v-else>
            <span class="text-rule ml-2 text-3 ">{{ t('网络或者请求异常，获取失败！') }}</span>
          </template>
        </template>
        <template v-else><span class="text-3 text-theme-sub-content">当前版本已经是最新版本。</span></template>
      </Skeleton>
    </template>
  </Card>
</template>
<style scoped>
:deep(.introduction > div >div > div:first-child) {
  line-height: 20px;
}

:deep(.introduction > div >div > div:last-child) {
  line-height: 20px;
}
</style>
