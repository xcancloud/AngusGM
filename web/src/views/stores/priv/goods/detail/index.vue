<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { Card, Grid, NoData } from '@xcan/design';
import { Tag } from 'ant-design-vue';
import { store } from '@/api';

const route = useRoute();
const id = route.params.id;

const detail = ref();

const goodsColumns = [
  [
    {
      dataIndex: 'name',
      label: '商品名称'
    },
    {
      dataIndex: 'goodsId',
      label: 'ID'
    },
    {
      dataIndex: 'code',
      label: '编码'
    },
    {
      dataIndex: 'type',
      label: '商品类型',
      customRender: ({ text }) => text?.message || '--'
    },
    {
      dataIndex: 'orderNo',
      label: '订单号'
    },
    {
      dataIndex: 'tags',
      label: '标签'
    }
  ],
  [
    {
      dataIndex: 'charge',
      label: '是否收费',
      customRender: ({ text }) => typeof text === 'boolean' ? (text ? '是' : '否') : '--'
    },
    {
      dataIndex: 'createdDate',
      label: '获取时间'
    },
    {
      dataIndex: 'expiredDate',
      label: '过期时间'
    },
    {
      dataIndex: 'editionType',
      label: '版本类型',
      customRender: ({ text }) => text?.message || '--'
    },
    {
      dataIndex: 'version',
      label: '版本号'
    }
  ]
];

const installColumns = [
  [
    {
      dataIndex: 'installAppCode',
      label: '安装应用编码'
    },
    {
      dataIndex: 'licenseNo',
      label: '许可编号'
    },
    {
      dataIndex: 'onlineInstall',
      label: '是否在线安装',
      customRender: ({ text }) => typeof text === 'boolean' ? (text ? '是' : '否') : '--'
    },
    {
      dataIndex: 'createdDate',
      label: '安装时间'
    }
  ],
  [
    {
      dataIndex: 'installMainApp',
      label: '是否安装到主应用',
      customRender: ({ text }) => typeof text === 'boolean' ? (text ? '是' : '否') : '--'
    },
    {
      dataIndex: 'relativeInstallPaths',
      label: '插件安装路径',
      customRender: ({ text }) => text ? text.join('，') : '--'
    },
    {
      dataIndex: 'uninstallable',
      label: '是否可卸载',
      customRender: ({ text }) => typeof text === 'boolean' ? (text ? '是' : '否') : '--'
    }
  ]
];
const loadGoodsInfo = async () => {
  if (!id) {
    return;
  }
  const [error, { data = {} }] = await store.getInstallationDetail(id as string);
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
  <Card title="基本信息" bodyClass="flex items-center px-8 py-5 space-x-10">
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
    title="商品介绍"
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
    title="安装信息"
    bodyClass="px-8 py-5">
    <template v-if="detail">
      <Grid :columns="installColumns" :dataSource="detail" />
    </template>
    <template v-else>
      <NoData class="mx-auto" size="small" />
    </template>
  </Card>
</template>
