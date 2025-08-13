<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { ButtonAuth, Colon, DropdownSort, Icon, IconRefresh, Image, NoData, PureCard, SearchPanel, Spin } from '@xcan-angus/vue-ui';
import { Button, Carousel, Divider, Pagination, Tag, TypographyParagraph } from 'ant-design-vue';
import { store } from '@/api';
import { useRouter } from 'vue-router';
import { app, AppOrServiceRoute, DomainManager, GoodsType, EditionType } from '@xcan-angus/infra';
import type { Goods } from './types';
import { goodsTypeColor } from './types';
import ShowButton from './showButton.vue';

const { t } = useI18n();
const router = useRouter();
const searchOpt = [
  {
    type: 'input' as const,
    valueKey: 'name',
    placeholder: t('cloud.placeholder.searchGoods'),
    allowClear: true
  },
  {
    type: 'select-enum' as const,
    valueKey: 'type',
    enumKey: GoodsType,
    placeholder: t('cloud.placeholder.selectGoodsType'),
    allowClear: true
  },
  {
    type: 'select-enum' as const,
    valueKey: 'applyEditionType',
    enumKey: EditionType,
    placeholder: t('cloud.placeholder.selectEditionType'),
    op: 'MATCH' as const,
    allowClear: true
  }
];

const sortMenus = [
  {
    key: 'starNum',
    name: t('cloud.messages.sortByLikes'),
    orderSort: 'ASC' as const
  },
  {
    key: 'createdDate',
    name: t('cloud.messages.sortByTime'),
    orderSort: 'ASC' as const
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
const downloading = ref(false);

/**
 * <p>Goods list on current page.</p>
 */
const goodsList = ref<Goods[]>([]);

/**
 * <p>Banner area with a highlighted product (AngusTester placeholder by default).</p>
 */
const hotList = ref([{
  name: t('cloud.descriptions.angusTesterName'),
  tags: [],
  introduction: t('cloud.descriptions.angusTesterIntro'),
  id: '',
  purchaseUrl: '',
  purchase: false
}]);

/**
 * <p>Load goods list by current pagination and filters.</p>
 * <p>Guard: skip when already loading.</p>
 */
const loadGoods = async () => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const { current, pageSize } = pagination;
  const [error, res] = await store.getCloudGoodsList({ pageNo: current, pageSize, ...params });
  loading.value = false;
  if (error) {
    return;
  }
  goodsList.value = res.data?.list || [];
  pagination.total = +res.data.total;
};

/**
 * <p>Fetch highlighted AngusTester in CLOUD_SERVICE edition and update banner CTA.</p>
 */
const loadAngus = async () => {
  const [error, { data = { list: [] } }] = await store.getCloudGoodsList({ code: 'AngusTester', editionType: 'CLOUD_SERVICE' });
  if (error) {
    return;
  }
  const cloudAngus = (data.list || []).find(angus => angus.editionType.value === 'CLOUD_SERVICE');
  if (cloudAngus) {
    hotList.value[0].purchaseUrl = cloudAngus.pricingUrl;
    hotList.value[0].id = cloudAngus.goodsId;
    hotList.value[0].purchase = cloudAngus.purchase;
  }
};

/**
 * <p>Handle pagination change.</p>
 */
const changPage = (page, pageSize) => {
  pagination.current = page;
  pagination.pageSize = pageSize;
  loadGoods();
};

/**
 * <p>Handle search filter change and reset to first page.</p>
 */
const changeSearch = (val) => {
  params.filters = val;
  pagination.current = 1;
  loadGoods();
};
/**
 * <p>Handle sorting change.</p>
 */
const changeOrder = ({ orderBy, orderSort }) => {
  params.orderBy = orderBy;
  params.orderSort = orderSort;
  loadGoods();
};

/**
 * <p>Navigates to goods detail page.</p>
 */
const gotoDetail = (id: string) => {
  router.push(`/stores/cloud/${id}`);
};

/**
 * <p>Toggle star for a goods card and update counts optimistically.</p>
 */
const starGoods = async (goods: Goods) => {
  const [error] = await store.starGoods({ goodsId: goods.goodsId, star: !goods.star });
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
/**
 * <p>Open pricing page in a new tab.</p>
 */
const topay = (purchaseUrl) => {
  // window.parent.postMessage({ e: 'purchase', value: purchaseUrl }, '*');
  // window.parent.location.href = purchaseUrl;
  window.open(purchaseUrl, '_blank');
};

onMounted(async () => {
  const host = await DomainManager.getInstance().getAppDomain(AppOrServiceRoute.www);
  hotList.value[0].purchaseUrl = host + '/purchase';
  loadGoods();
  loadAngus();
});
</script>
<template>
  <div class="pr-2">
    <Carousel :autoplay="true">
      <div
        v-for="item in hotList"
        :key="item.id"
        class="my-bg px-20 2xl:px-10 relative py-7.5"
        style="min-height: 260px;">
        <div class="flex space-x-40 justify-between relative">
          <div class="space-y-2">
            <div class="my-title text-blue-text-deep-light">{{ item.name }}</div>
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
                @click="topay(item.purchaseUrl)">
                {{ t('cloud.messages.buyNow') }}
              </Button>
              <ButtonAuth
                code="GoodsDetail"
                type="primary"
                ghost
                @click="gotoDetail(item.id)">
              </ButtonAuth>
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
        <Icon icon="icon-shunxu" class="text-4 cursor-pointer"></Icon>
      </DropdownSort>
      <IconRefresh
        :loading="loading"
        class="ml-2"
        @click="loadGoods" />
    </PureCard>
    <Spin :spinning="loading || downloading">
      <div class="space-y-2">
        <NoData v-show="!goodsList.length" class="py-20" />
        <PureCard
          v-for="goods in goodsList"
          :key="goods.id"
          class="px-4 py-3 border text-3 bg-white w-full">
          <div class="flex">
            <Image
              type="image"
              :src="goods.iconUrl"
              class="w-18 h-18" />
            <div class="ml-3 flex-1 self-center">
              <div>
                <ButtonAuth
                  code="GoodsDetail"
                  type="link"
                  class="font-medium text-4 mr-4"
                  style="user-select: text;"
                  :text="goods.name"
                  @click="gotoDetail(goods.goodsId)">
                </ButtonAuth>
                <span
                  v-if="!app.show('GoodsDetail')"
                  class="font-medium text-4 mr-4 text-blue-1 cursor-pointer">{{ goods.name }}</span>
                {{ 'V' + goods.version }}
                <span class="inline-flex items-center ml-4 text-3.5 cursor-pointer" @click="starGoods(goods)">
                  <Icon
                    icon="icon-a-tuijiandianzan"
                    class="text-3 mr-2"
                    :class="{'text-blue-1': goods.star}" />{{ goods.starNum }}</span>
              </div>
              <Tag
                v-for="tag in goods.tags"
                :key="tag"
                class="h-5 leading-4 px-1 mt-1">
                {{ tag }}
              </Tag>
              <div class="flex items-center mt-2 flex-wrap flex-1">
                <div class="flex-none mr-8">
                  <label class="mr-1 text-black-label">{{ t('cloud.labels.version') }}
                    <Colon class="mr-1" />
                  </label><span>{{ goods.editionType.message }}</span>
                </div>
                <div v-if="goods.applyEditionTypes" class="flex-none mr-8">
                  <label class="mr-1 text-black-label">{{ t('cloud.labels.applicableVersion') }}
                    <Colon class="mr-1" />
                  </label><span>{{ (goods.applyEditionTypes || []).map(i => i.message).join('、') }}</span>
                </div>
                <div class="flex-none mr-8">
                  <label class="mr-1 text-black-label">{{ t('cloud.labels.type') }}
                    <Colon class="mr-1" />
                  </label><span :class="goodsTypeColor[goods.type?.value]">{{ goods.type.message }}</span>
                </div>
                <div class="flex-none mr-8">
                  <label class="mr-1 text-black-label">{{ t('cloud.labels.releaseTime') }}
                    <Colon class="mr-1" />
                  </label><span>{{ goods.onlineDate }}</span>
                </div>
                <div class="flex-none">
                  <label class="mr-1 text-black-label">{{ t('cloud.labels.price') }}
                    <Colon class="mr-1" />
                  </label>
                  <template v-if="goods.charge">
                    <template v-if="goods.price">
                      <span class="mr-1">¥</span><span>{{ goods.price?.finalPrice }}</span><span
                        v-show="goods.price?.totalSpecPrice !== goods.price?.finalPrice"
                        class="line-through border rounded-full ml-2 px-2 border-orange-text  text-orange-text">{{
                          "¥" + goods.price?.totalSpecPrice
                        }}</span>
                    </template>
                    <template v-else>--</template>
                  </template>
                  <template v-else>{{ t('cloud.messages.free') }}</template>
                </div>
              </div>
            </div>
            <div class="self-center ml-5">
              <ShowButton
                v-model:downLoading="downloading"
                :goods="goods"
                :disabled="false"
                @reload="loadGoods" />
            </div>
          </div>
          <Divider class="divider" />
          <div class="bg-gray-text-bg-1 p-2 text-black-label mt-3">{{ goods.introduction }}</div>
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
