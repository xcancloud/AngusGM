<script setup lang="ts">
import { onMounted, ref, reactive } from 'vue';
import { PureCard, SearchPanel, DropdownSort, Icon, Image, NoData, Colon, Spin, IconRefresh } from '@xcan-angus/vue-ui';
import { Carousel, TypographyParagraph, Tag, Divider, Pagination, Button } from 'ant-design-vue';
import { useRouter } from 'vue-router';
import { site, http, API, VERSION } from '@xcan-angus/tools';

import type { Goods } from './PropsType';
import { goodsTypeColor, getEnumMessages } from './PropsType';
import { store } from '@/api';
import ShowButton from './components/showButton.vue';

const router = useRouter();
const searchOpt = [
  {
    type: 'input',
    valueKey: 'name',
    placeholder: '应用、插件',
    allowClear: true
  },
  {
    type: 'select-enum',
    valueKey: 'type',
    enumKey: 'GoodsType',
    placeholder: '选择商品类型',
    allowClear: true
  },
  {
    type: 'select-enum',
    valueKey: 'applyEditionType',
    enumKey: 'EditionType',
    placeholder: '选择适用版本类型',
    op: 'MATCH',
    excludes: ['CLOUD_SERVICE'],
    allowClear: true
  }
];

const sortMenus = [
  {
    key: 'starNum',
    name: '按点赞量',
    orderSort: 'ASC'
  },
  {
    key: 'createdDate',
    name: '按时间',
    orderSort: 'ASC'
  }
];

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
});
const params = reactive({
  orderBy: undefined,
  orderSort: undefined,
  filters: []
});

const loading = ref(false);

// 商品列表
const goodsList = ref<Goods[]>([]);

// banner 商品列表
const hotList = ref([{
  name: 'AngusTester 云测试平台',
  tags: [],
  introduction: 'AngusTester是一个现代化的云测试服务平台。 使用AngusTester可以帮助测试人员和开发人员更高效地完成“性能、功能、稳定性、自动化”等测试工作。AngusTester主要能力由“接口管理、测试管理、数据模拟与生成、服务模拟”四部分组成，支持测试对象包括：服务、接口、协议、中间件等。',
  id: '',
  purcahseUrl: '',
  purchase: false
}]);

// 商品列表 load
const loadGoods = async () => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const { current, pageSize } = pagination;
  const [error, res] = await http.get(`${window.location.ancestorOrigins[0]}/${API}/${VERSION}/store/priv/goods/search`, {
    pageNo: current,
    pageSize,
    ...params
  });
  loading.value = false;
  if (error) {
    return;
  }

  goodsList.value = res.data?.list || [];
  pagination.total = +res.data.total;
};

// 获取 angus
const loadAngus = async () => {
  const [error, { data }] = await http.get(`${window.location.ancestorOrigins[0]}/${API}/${VERSION}/store/priv/goods/search`, { code: 'AngusTester' });
  if (error) {
    return;
  }

  const cloudAngus = data?.list?.[0];
  if (cloudAngus) {
    hotList.value[0].purcahseUrl = cloudAngus.pricingUrl;
    hotList.value[0].id = cloudAngus.goodsId;
    hotList.value[0].purchase = cloudAngus.purchase;
  }
};

const changPage = (page, pageSize) => {
  pagination.current = page;
  pagination.pageSize = pageSize;
  loadGoods();
};

const changeSearch = (val) => {
  params.filters = val;
  pagination.current = 1;
  loadGoods();
};
const changeOrder = ({ orderBy, orderSort }) => {
  params.orderBy = orderBy;
  params.orderSort = orderSort;
  loadGoods();
};

const gotoDetail = (id: string) => {
  if (window.top === window.self) {
    router.push(`/stores/cloud/open2p/${id}`);
  } else {
    window.parent.postMessage(id, '*');
  }
};

const starGoods = async (goods: Goods) => {
  const [error] = await store.starCloudGoodsInPriv({
    goodsId: goods.goodsId,
    star: !goods.star
  }, window.location.ancestorOrigins[0]);
  if (error) {
    return;
  }
  goods.star = !goods.star;
  if (goods.star) {
    goods.starNum = 1 + +goods.starNum;
  } else {
    goods.starNum = goods.starNum - 1;
  }
};
const topay = (purcahseUrl) => {
  window.parent.postMessage({ e: 'purchase', value: purcahseUrl }, '*');
};

onMounted(async () => {
  const purcahseUrl = await site.getUrl('www');
  hotList.value[0].purcahseUrl = purcahseUrl + '/purchase';
  loadGoods();
  loadAngus();
});
</script>
<template>
  <div class="h-full pr-2 bg-theme-main overflow-auto">
    <Carousel :autoplay="true">
      <div
        v-for="item in hotList"
        :key="item.id"
        class="my-bg px-20 relative py-7.5"
        style="min-height: 260px;">
        <div class="flex space-x-40 justify-between relative">
          <div class="space-y-2">
            <div class="my-title text-blue-text-deep">{{ item.name }}</div>
            <div class="flex flex-wrap">
              <div
                v-for="item1 in item.tags"
                :key="item1"
                class="flex items-center my-list text-3.5 leading-3.5 space-x-1 mb-2 mr-10">
                <div class="w-1.5 h-1.5 my-square"></div>
                <div>{{ item1 }}</div>
              </div>
            </div>
            <TypographyParagraph :ellipsis="{ rows: 3, expandable: false }" :content="item.introduction" />
            <div class="absolute bottom-4">
              <Button
                v-if="!item.purchase"
                type="primary"
                class="mr-3"
                @click="topay(item.purcahseUrl)">
                立即购买
              </Button>
              <Button
                type="primary"
                ghost
                @click="gotoDetail(item.id)">
                查看详情
              </Button>
            </div>
          </div>
          <img class="my-image" src="./images/img1.png" />
        </div>
      </div>
    </Carousel>
    <PureCard class="py-3 px-4 flex items-center bg-white mt-3">
      <SearchPanel
        :options="searchOpt"
        class="flex-1"
        @change="changeSearch" />
      <DropdownSort :menuItems="sortMenus" @click="changeOrder">
        <Icon icon="icon-shunxu" class="text-4 cursor-pointer" />
      </DropdownSort>
      <IconRefresh
        :loading="loading"
        class="ml-2"
        @click="loadGoods" />
    </PureCard>
    <Spin :spinning="loading" :delay="300">
      <div class="space-y-2">
        <NoData v-show="!goodsList.length" class="py-20" />
        <PureCard
          v-for="goods in goodsList"
          :key="goods.id"
          class="px-4 py-3 border text-3 bg-white">
          <div class="flex">
            <Image
              type="image"
              :src="goods.iconUrl"
              class="w-18 h-18" />
            <div class="ml-3 flex-1 self-center">
              <div>
                <span class="font-medium text-4 mr-4 text-blue-1 cursor-pointer" @click="gotoDetail(goods.goodsId)">{{ goods.name }}</span>
                {{ 'V' + goods.version }}
                <span class="inline-flex items-center ml-4 text-3.5 cursor-pointer" @click="starGoods(goods)">
                  <Icon
                    icon="icon-a-tuijiandianzan"
                    class="text-3 mr-2"
                    :class="{'text-blue-text': goods.star}" />{{ goods.starNum }}</span>
              </div>
              <Tag
                v-for="tag in goods.tags"
                :key="tag"
                class="h-5 leading-4 px-1 mt-1">
                {{ tag }}
              </Tag>
              <div class="flex items-center mt-2 space-x-8">
                <div>
                  <label class="mr-1 text-black-label">适用版本
                    <Colon class="mr-1" />
                  </label><span>{{ getEnumMessages(goods.applyEditionTypes) }}</span>
                </div>
                <div>
                  <label class="mr-1 text-black-label">类型
                    <Colon class="mr-1" />
                  </label><span :class="goodsTypeColor[goods.type?.value]">{{ goods.type.message }}</span>
                </div>
                <div>
                  <label class="mr-1 text-black-label">发布时间
                    <Colon class="mr-1" />
                  </label><span>{{ goods.onlineDate }}</span>
                </div>
                <div class="">
                  <label class="mr-1 text-black-label">价格
                    <Colon class="mr-1" />
                  </label>
                  <template v-if="goods.charge">
                    <template v-if="goods.price">
                      <span class="mr-1">¥</span><span>{{ goods.price?.finalPrice }}</span><span
                        v-show="goods.price?.totalSpecPrice !== goods.price?.finalPrice"
                        class="line-through border rounded-full ml-2 px-2 border-orange-text  text-orange-text">{{ "¥" + goods.price?.totalSpecPrice }}</span>
                    </template>
                    <template v-else>--</template>
                  </template>
                  <template v-else>免费</template>
                </div>
              </div>
            </div>
            <div class="self-center">
              <ShowButton :goods="goods" @reload="loadGoods" />
            </div>
          </div>
          <Divider class="divider" />
          <div class="bg-gray-text-bg p-2 text-black-label mt-3">{{ goods.introduction }}</div>
        </PureCard>
      </div>
    </Spin>
    <Pagination
      v-bind="pagination"
      class="my-3"
      @change="changPage" />
  </div>
</template>
<style scoped>
.my-bg {
  background-image: linear-gradient(to left top, rgba(223, 234, 253, 50%), rgba(175, 200, 255, 50%));
}

.divider {
  @apply bg-gray-divider;
}

.my-bg .my-title {
  font-size: 36px;
  font-weight: 600;
}

.my-bg .my-title .my-list {
  color: #4b5b77;
}

.my-square {
  border-radius: 1px;
  background-color: #0b53ff;
}

.my-image {
  width: 20.125rem;
}

:deep(.slick-dots.slick-dots-bottom li:not(slick-active)) {
  background-color: rgba(0, 119, 255, 30%);
}

:deep(.ant-carousel li.slick-active button) {
  background-color: rgba(0, 119, 255, 100%);
}

</style>
