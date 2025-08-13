<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { Card, Colon, Grid, NoData } from '@xcan-angus/vue-ui';
import { appContext } from '@xcan-angus/infra';
import { Skeleton } from 'ant-design-vue';

import { UpgradeableVersion } from './types';
import { edition } from '@/api';

import grayImg from './images/gray2.png';

/**
 * Component props interface
 * Defines the properties passed from parent component
 */
interface Props {
  currentVersion: string, // Currently installed version
  installGoodsCode: string, // Installed product code
}

const props = withDefaults(defineProps<Props>(), {
  currentVersion: '',
  installGoodsCode: ''
});

const { t } = useI18n();

/**
 * Loading state for version data fetching
 * Shows skeleton loading while retrieving upgrade information
 */
const loading = ref<boolean>(true);
/**
 * Exception state flag
 * Indicates if there was an error fetching version data
 */
const showException = ref<boolean>(false);
/**
 * Current edition type
 * Determines which version information to display
 */
const editionType = ref<string>();
/**
 * Upgradeable version data
 * Contains information about available version upgrades
 */
const versionData = ref<UpgradeableVersion>();

/**
 * Initialize component
 * Sets up edition type for the component
 */
const init = async () => {
  editionType.value = appContext.getEditionType();
};

/**
 * Load upgradeable version information
 * Fetches available version upgrades from API
 */
const loadUpgradeableVersion = async (): Promise<void> => {
  try {
    const [error, { data }] = await edition.getEditionUpgradeable({ goodsCode: props.installGoodsCode });

    if (error) {
      showException.value = true;
      return;
    }

    if (!data.length) {
      return;
    }

    versionData.value = data[0];

    // Update columns based on whether upgrade is available
    if (!versionData.value?.features?.length) {
      return;
    }

    versionData.value.upgradeable
      ? updateColumns(upgradeableColumns.value[0])
      : updateColumns(currentVersionColumns.value[0]);
  } finally {
    loading.value = false;
  }
};

/**
 * Update column visibility based on upgrade status
 * Hides features column when no features are available
 */
const updateColumns = (_columns: any[]) => {
  for (let i = 0; i < _columns.length; i++) {
    if (_columns[i].dataIndex === 'features') {
      _columns[i].hide = true;
      break;
    }
  }
};

/**
 * Grid columns configuration for current version display
 * Shows version introduction and current version information
 */
const currentVersionColumns = ref([
  [
    {
      label: t('version.columns.introduction'),
      dataIndex: 'introduction'
    },
    {
      label: t('version.columns.features'),
      dataIndex: 'features',
      hide: true
    },
    {
      label: t('version.columns.currentVersion'),
      dataIndex: 'currentVersion'
    }
  ]
]);

/**
 * Grid columns configuration for upgradeable version display
 * Shows version introduction and upgrade information
 */
const upgradeableColumns = ref([
  [
    {
      label: t('version.columns.introduction'),
      dataIndex: 'introduction'
    },
    {
      label: t('version.columns.features'),
      dataIndex: 'features',
      hide: true
    },
    {
      label: t('version.columns.currentVersion'),
      dataIndex: 'currentVersion'
    }
  ]
]);

/**
 * Watch for changes in installed goods code
 * Reloads upgradeable version data when product code changes
 */
watch(() => props.installGoodsCode, (newValue) => {
  if (newValue) {
    loadUpgradeableVersion();
  }
}, { immediate: true });

/**
 * Initialize component on mount
 * Sets up initial component state
 */
onMounted(() => {
  init();
});
</script>

<template>
  <Card bodyClass="flex px-8 py-5 space-x-20 pr-30" class="flex-1">
    <!-- Dynamic Card Title -->
    <template #title>
      <template v-if="editionType === 'CLOUD_SERVICE'">
        {{ t('version.titles.latestVersion') }}
      </template>
      <template v-else>
        <template v-if="versionData && versionData?.upgradeable">
          {{ t('version.titles.upgradableVersion') }}
        </template>
        <template v-else>
          {{ t('version.titles.versionIntroduction') }}
        </template>
      </template>
    </template>

    <!-- Card Content -->
    <template #default>
      <!-- Version Icon Image -->
      <img :src="grayImg" class="w-60 h-60">

      <!-- Version Information Content -->
      <Skeleton
        active
        :loading="loading"
        :title="false"
        :paragraph="{ rows: 8 }">
        <!-- Non-Cloud Service Edition Content -->
        <template v-if="editionType !== 'CLOUD_SERVICE'">
          <template v-if="!showException">
            <template v-if="versionData">
              <!-- Version Information Grid -->
              <Grid
                class="introduction w-150"
                :columns="versionData.upgradeable?upgradeableColumns:currentVersionColumns"
                :dataSource="versionData">
                <!-- Features Display -->
                <template #features="{text}">
                  <p
                    v-for="item,index in text.slice(0,3)"
                    :key="index">
                    {{ item }}
                  </p>
                  <p v-if="text?.length>3">...</p>
                </template>

                <!-- Current Version Information -->
                <template #currentVersion>
                  {{ props.currentVersion || '--' }}
                  <span style="font-size: 12px;line-height: 20px;" class="ml-20">{{ t('version.messages.latestVersion') }}</span>
                  <Colon class="mr-2" />
                  <span>{{ versionData?.version }}</span>
                  <!-- TODO herf 没有加私有化去升级地址-->
                  <!-- <a class="ml-5 text-theme-text-hover text-theme-special">去升级</a> -->
                </template>
              </Grid>
            </template>
            <template v-else>
              <!-- No Data Display -->
              <div class="flex flex-col justify-center">
                <NoData />
              </div>
            </template>
          </template>
          <template v-else>
            <!-- Exception Error Display -->
            <span class="text-rule ml-2 text-3 ">{{ t('version.messages.networkError') }}</span>
          </template>
        </template>

        <!-- Cloud Service Edition Content -->
        <template v-else>
          <span class="text-3 text-theme-sub-content">{{ t('version.messages.alreadyLatestVersion') }}</span>
        </template>
      </Skeleton>
    </template>
  </Card>
</template>

<style scoped>
/**
 * Custom styling for introduction grid
 * Ensures consistent line height for grid content
 */
:deep(.introduction > div >div > div:first-child) {
  line-height: 20px;
}

:deep(.introduction > div >div > div:last-child) {
  line-height: 20px;
}
</style>
