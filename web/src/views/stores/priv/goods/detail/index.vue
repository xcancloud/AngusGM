<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { Card, Grid, NoData } from '@xcan-angus/vue-ui';
import { Tag } from 'ant-design-vue';
import { privStore } from '@/api';

const { t } = useI18n();
const route = useRoute();
const id = route.params.id;

const detail = ref();

const goodsColumns = [
  [
    {
      dataIndex: 'name',
      label: t('priv.detail.goodsName')
    },
    {
      dataIndex: 'goodsId',
      label: t('priv.detail.id')
    },
    {
      dataIndex: 'code',
      label: t('priv.detail.code')
    },
    {
      dataIndex: 'type',
      label: t('priv.detail.goodsType'),
      customRender: ({ text }) => text?.message || '--'
    },
    {
      dataIndex: 'orderNo',
      label: t('priv.detail.orderNo')
    },
    {
      dataIndex: 'tags',
      label: t('priv.detail.tags')
    }
  ],
  [
    {
      dataIndex: 'charge',
      label: t('priv.detail.charge'),
      customRender: ({ text }) => typeof text === 'boolean' ? (text ? t('priv.messages.yes') : t('priv.messages.no')) : '--'
    },
    {
      dataIndex: 'createdDate',
      label: t('priv.detail.getTime')
    },
    {
      dataIndex: 'expiredDate',
      label: t('priv.detail.expiredTime')
    },
    {
      dataIndex: 'editionType',
      label: t('priv.detail.editionType'),
      customRender: ({ text }) => text?.message || '--'
    },
    {
      dataIndex: 'version',
      label: t('priv.detail.version')
    }
  ]
];

const installColumns = [
  [
    {
      dataIndex: 'installAppCode',
      label: t('priv.detail.installAppCode')
    },
    {
      dataIndex: 'licenseNo',
      label: t('priv.detail.licenseNo')
    },
    {
      dataIndex: 'onlineInstall',
      label: t('priv.detail.onlineInstall'),
      customRender: ({ text }) => typeof text === 'boolean' ? (text ? t('priv.messages.yes') : t('priv.messages.no')) : '--'
    },
    {
      dataIndex: 'createdDate',
      label: t('priv.detail.installTime')
    }
  ],
  [
    {
      dataIndex: 'installMainApp',
      label: t('priv.detail.installMainApp'),
      customRender: ({ text }) => typeof text === 'boolean' ? (text ? t('priv.messages.yes') : t('priv.messages.no')) : '--'
    },
    {
      dataIndex: 'relativeInstallPaths',
      label: t('priv.detail.installPath'),
      customRender: ({ text }) => text ? text.join('，') : '--'
    },
    {
      dataIndex: 'uninstallable',
      label: t('priv.detail.uninstallable'),
      customRender: ({ text }) => typeof text === 'boolean' ? (text ? t('priv.messages.yes') : t('priv.messages.no')) : '--'
    }
  ]
];
const loadGoodsInfo = async () => {
  if (!id) {
    return;
  }
  const [error, { data = {} }] = await privStore.getInstallationDetail(id as string);
  if (error) {
    return;
  }
  detail.value = data;
};

onMounted(() => {
  loadGoodsInfo();
});
</script>
<template>
  <Card :title="t('priv.detail.basicInfo')" bodyClass="flex items-center px-8 py-5 space-x-10">
    <template v-if="detail">
      <div class="w-50 h-50 flex flex-col justify-center border border-border-divider rounded p-2 border-dashed">
        <img class="max-h-full w-h-full rounded mx-auto" :src="detail?.iconText" />
      </div>
      <Grid
        class="flex-1 pl-3"
        :columns="goodsColumns"
        :dataSource="detail">
        <template #tags="{text}">
          <div v-if="text?.length" class="-mt-1">
            <Tag
              v-for="tag,index in text"
              :key="index"
              class="mb-1">
              {{ tag }}
            </Tag>
          </div>
          <template v-else>
            --
          </template>
        </template>
      </Grid>
    </template>
    <template v-else>
      <NoData class="mx-auto" size="small" />
    </template>
  </Card>
  <Card
    class="mt-2"
    :title="t('priv.detail.productIntro')"
    bodyClass="flex px-8 py-5">
    <template v-if="detail?.introduction">
      {{ detail.introduction }}
    </template>
    <template v-else>
      <NoData class="mx-auto" size="small" />
    </template>
  </Card>
  <Card
    class="mt-2"
    :title="t('priv.detail.installInfo')"
    bodyClass="px-8 py-5">
    <template v-if="detail">
      <Grid :columns="installColumns" :dataSource="detail" />
    </template>
    <template v-else>
      <NoData class="mx-auto" size="small" />
    </template>
  </Card>
</template>
