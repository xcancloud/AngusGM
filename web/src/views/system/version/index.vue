<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import clipboard from 'vue-clipboard3';
import { Skeleton, Tooltip } from 'ant-design-vue';
import { Card, Grid, Icon } from '@xcan-angus/vue-ui';
import { appContext } from '@xcan-angus/infra';
import dayjs from 'dayjs';

import { edition } from '@/api';
import { InstallVesion } from './PropsType';
import UpdatedVersion from './components/updatableVersion.vue';
import darkImg from './images/dark1.png';
import grayImg from './images/gray1.png';

const { t } = useI18n();
const loading = ref<boolean>(true);
const copyContent = ref<string>('version.copy');

const installVersion = ref<InstallVesion>();
const editionType = ref<string>();

const gridColumnsCloud = [
  [
    {
      label: t('版本类型'),
      dataIndex: 'editionType'
    },
    {
      label: t('应用编码'),
      dataIndex: 'goodsCode'
    },
    {
      label: t('应用版本'),
      dataIndex: 'goodsVersion'
    },
    {
      label: t('systemVersion.provider'),
      dataIndex: 'provider'
    },
    {
      label: t('systemVersion.publisher'),
      dataIndex: 'issuer'
    },
    {
      label: t('systemVersion.holder'),
      dataIndex: 'holder'
    },
    {
      label: t('发行日期'),
      dataIndex: 'beginDate'
    },
    {
      label: t('过期日期'),
      dataIndex: 'endDate'
    }
  ]
];

const gridColumnsPriv = [
  [
    {
      label: t('版本类型'),
      dataIndex: 'editionType'
    },
    {
      label: t('应用编码'),
      dataIndex: 'goodsCode'
    },
    {
      label: t('应用版本'),
      dataIndex: 'goodsVersion'
    },
    {
      label: t('systemVersion.provider'),
      dataIndex: 'provider'
    },
    {
      label: t('systemVersion.publisher'),
      dataIndex: 'issuer'
    },
    {
      label: t('systemVersion.holder'),
      dataIndex: 'holder'
    },
    {
      label: t('许可编号'),
      dataIndex: 'licenseNo'
    },
    {
      label: t('发行日期'),
      dataIndex: 'beginDate'
    },
    {
      label: t('过期日期'),
      dataIndex: 'endDate'
    },
    {
      label: t('MD5签名'),
      dataIndex: 'signature'
    }
  ]
];

const init = async () => {
  editionType.value = appContext.getEditionType();
  loadInstalledVersion();
};

const loadInstalledVersion = async (): Promise<void> => {
  const [error, { data }] = await edition.getEditionInstalled('AngusGM');
  loading.value = false;
  if (error) {
    return;
  }
  installVersion.value = data;
};

const getVersionTypeIcon = (key: string) => {
  switch (key) {
    case 'DATACENTER':
      return 'icon-shujuzhongxin';
    case 'CLOUD_SERVICE':
      return 'icon-yunfuwu';
    case 'ENTERPRISE':
      return 'icon-qiye';
    case 'COMMUNITY':
      return 'icon-shequ';
  }
};

const copy = async () => {
  const { toClipboard } = clipboard();

  try {
    if (!installVersion.value) {
      return;
    }
    await toClipboard(installVersion.value.licenseNo);
    copyContent.value = 'systemVersion.copyRes';
  } catch (error) {
    copyContent.value = 'systemVersion.copyRes1';
  }
};

const moveIn = () => {
  copyContent.value = 'systemVersion.copy';
};

const getImg = computed(() => {
  const theme = document.body.className;
  switch (theme) {
    case 'dark-theme':
      return darkImg;
    case 'gray-theme' || 'light-theme':
      return grayImg;
    default:
      return grayImg;
  }
});

const tooltipTitle = computed(() => {
  return t(copyContent.value);
});

onMounted(() => {
  init();
});

const columns = computed(() => {
  if (editionType.value === 'CLOUD_SERVICE') {
    return gridColumnsCloud;
  }

  return gridColumnsPriv;
});
</script>
<template>
  <Card :title="t('systemVersion.systemVersion')" bodyClass="flex px-8 py-5 items-center space-x-20 pr-30">
    <img :src="getImg" class="w-60 h-60" />
    <Skeleton
      active
      :loading="loading"
      :title="false"
      :paragraph="{ rows: 9 }">
      <Grid :columns="columns" :dataSource="installVersion">
        <template #licenseNo="{text}">
          {{ text }}
          <Tooltip :title="tooltipTitle">
            <Icon
              v-if="text"
              icon="icon-fuzhi"
              class="ml-1.5 mr-0.5  mb-0.5 text-3 leading-3 cursor-pointer content-primary-text-hover"
              @mouseenter="moveIn"
              @click="copy" />
          </Tooltip>
        </template>
        <template #editionType="{text}">
          <Icon :icon="getVersionTypeIcon(text?.value)" class="text-4 -mt-0.5" />
          {{ text?.message }}
        </template>
        <template #goodsType="{text}">
          {{ text?.message }}
        </template>
        <template #endDate="{text}">
          {{ text }} ( 剩余{{ dayjs(text).diff(dayjs().format(),'day') }}天 )
        </template>
      </Grid>
    </Skeleton>
  </Card>
  <UpdatedVersion
    class="mt-2"
    :currVersion="installVersion?.goodsVersion"
    :installGoodsCode="installVersion?.goodsCode" />
</template>
