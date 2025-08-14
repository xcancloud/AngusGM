<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { Colon, DropdownSort, Icon, IconRefresh, Image, NoData, PureCard, SearchPanel, Spin } from '@xcan-angus/vue-ui';
import { Button, Carousel, Divider, Pagination, Tag, TypographyParagraph } from 'ant-design-vue';
import { useRouter } from 'vue-router';
import { API, AppOrServiceRoute, DomainManager, http, VERSION, GoodsType, EditionType } from '@xcan-angus/infra';
import { useI18n } from 'vue-i18n';

import type { Goods } from './types';
import { getEnumMessages, goodsTypeColor } from './types';
import { store } from '@/api';
import ShowButton from './components/showButton.vue';

const { t } = useI18n();
const router = useRouter();

// Search configuration options for filtering goods
const searchOpt = [
  {
    type: 'input' as const,
    valueKey: 'name',
    placeholder: t('open2p.placeholder.searchGoods'),
    allowClear: true
  },
  {
    type: 'select-enum' as const,
    valueKey: 'type',
    enumKey: GoodsType,
    placeholder: t('open2p.placeholder.selectGoodsType'),
    allowClear: true
  },
  {
    type: 'select-enum' as const,
    valueKey: 'applyEditionType',
    enumKey: EditionType,
    placeholder: t('open2p.placeholder.selectEditionType'),
    op: 'MATCH' as const,
    excludes: ['CLOUD_SERVICE'],
    allowClear: true
  }
];

// Sorting menu configuration for goods list
const sortMenus = [
  {
    key: 'starNum',
    name: t('open2p.messages.sortByLikes'),
    orderSort: 'ASC' as const
  },
  {
    key: 'createdDate',
    name: t('open2p.messages.sortByTime'),
    orderSort: 'ASC' as const
  }
];

// Pagination state management
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
});

// Search and filter parameters
const params = reactive({
  orderBy: undefined as string | undefined,
  orderSort: undefined as 'ASC' | 'DESC' | undefined,
  filters: [] as any[]
});

const loading = ref(false);

// Goods list data
const goodsList = ref<Goods[]>([]);

// Banner goods list - featuring Angus Tester as default
const hotList = ref([{
  name: t('open2p.descriptions.angusTesterName'),
  tags: [] as string[],
  introduction: t('open2p.descriptions.angusTesterIntro'),
  id: '',
  purchaseUrl: '',
  purchase: false
}]);

/**
 * Load goods list from API with pagination and filters
 * Handles loading state and updates goods list and pagination total
 */
const loadGoods = async () => {
  if (loading.value) {
    return;
  }
  loading.value = true;

  try {
    const { current, pageSize } = pagination;
    const [error, res] = await http.get(`${window.location.ancestorOrigins[0]}/${API}/${VERSION}/store/priv/goods`, {
      pageNo: current,
      pageSize,
      ...params
    });

    if (error) {
      return;
    }

    goodsList.value = res.data?.list || [];
    pagination.total = +res.data.total;
  } finally {
    loading.value = false;
  }
};

/**
 * Load Angus Tester specific information
 * Updates the banner with real Angus Tester data if available
 */
const loadAngus = async () => {
  const [error, { data }] = await http.get(`${window.location.ancestorOrigins[0]}/${API}/${VERSION}/store/priv/goods`, { code: 'AngusTester' });
  if (error) {
    return;
  }

  const cloudAngus = data?.list?.[0];
  if (cloudAngus) {
    hotList.value[0].purchaseUrl = cloudAngus.pricingUrl;
    hotList.value[0].id = cloudAngus.goodsId;
    hotList.value[0].purchase = cloudAngus.purchase;
  }
};

/**
 * Handle pagination change
 * Updates current page and page size, then reloads goods
 */
const changPage = (page: number, pageSize: number) => {
  pagination.current = page;
  pagination.pageSize = pageSize;
  loadGoods();
};

/**
 * Handle search filter changes
 * Resets pagination to first page and reloads goods with new filters
 */
const changeSearch = (val: any[]) => {
  params.filters = val;
  pagination.current = 1;
  loadGoods();
};

/**
 * Handle sorting order changes
 * Updates order parameters and reloads goods with new sorting
 */
const changeOrder = ({ orderBy, orderSort }: { orderBy: string, orderSort: 'ASC' | 'DESC' }) => {
  params.orderBy = orderBy;
  params.orderSort = orderSort;
  loadGoods();
};

/**
 * Navigate to goods detail page
 * Handles both standalone and embedded iframe scenarios
 */
const gotoDetail = (id: string) => {
  if (window.top === window.self) {
    router.push(`/stores/cloud/open2p/${id}`);
  } else {
    window.parent.postMessage(id, '*');
  }
};

/**
 * Toggle star status for goods
 * Updates star count and star status locally after successful API call
 */
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

/**
 * Handle purchase action
 * Sends purchase message to parent window for embedded scenarios
 */
const topay = (purchaseUrl: string) => {
  window.parent.postMessage({ e: 'purchase', value: purchaseUrl }, '*');
};

// Component lifecycle - initialize data on mount
onMounted(async () => {
  const purchaseUrl = DomainManager.getInstance().getAppDomain(AppOrServiceRoute.www);
  hotList.value[0].purchaseUrl = purchaseUrl + '/purchase';
  await loadGoods();
  await loadAngus();
});
</script>

<template>
  <div class="h-full pr-2 bg-theme-main overflow-auto">
    <!-- Banner carousel section -->
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
                @click="topay(item.purchaseUrl)">
                {{ t('open2p.messages.buyNow') }}
              </Button>
              <Button
                type="primary"
                ghost
                @click="gotoDetail(item.id)">
                {{ t('open2p.messages.viewDetails') }}
              </Button>
            </div>
          </div>
          <img class="my-image" src="./images/img1.png" />
        </div>
      </div>
    </Carousel>

    <!-- Search and filter panel -->
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

    <!-- Goods list with loading state -->
    <Spin :spinning="loading" :delay="300">
      <div class="space-y-2">
        <NoData v-show="!goodsList.length" class="py-20" />
        <PureCard
          v-for="goods in goodsList"
          :key="goods.id"
          class="px-4 py-3 border text-3 bg-white">
          <div class="flex">
            <!-- Goods image -->
            <Image
              type="image"
              :src="goods.iconUrl"
              class="w-18 h-18" />

            <!-- Goods information section -->
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

              <!-- Goods tags -->
              <Tag
                v-for="tag in goods.tags"
                :key="tag"
                class="h-5 leading-4 px-1 mt-1">
                {{ tag }}
              </Tag>

              <!-- Goods metadata -->
              <div class="flex items-center mt-2 space-x-8">
                <div>
                  <label class="mr-1 text-black-label">{{ t('open2p.labels.applicableVersion') }}
                    <Colon class="mr-1" />
                  </label><span>{{ getEnumMessages(goods.applyEditionTypes) }}</span>
                </div>
                <div>
                  <label class="mr-1 text-black-label">{{ t('open2p.labels.type') }}
                    <Colon class="mr-1" />
                  </label><span :class="goodsTypeColor[goods.type?.value || '']">{{ goods.type?.message }}</span>
                </div>
                <div>
                  <label class="mr-1 text-black-label">{{ t('open2p.labels.releaseTime') }}
                    <Colon class="mr-1" />
                  </label><span>{{ goods.onlineDate }}</span>
                </div>
                <div class="">
                  <label class="mr-1 text-black-label">{{ t('open2p.labels.price') }}
                    <Colon class="mr-1" />
                  </label>
                  <!-- Price display logic -->
                  <template v-if="goods.charge">
                    <template v-if="goods.price">
                      <span class="mr-1">{{ t('open2p.messages.priceSymbol') }}</span><span>{{ goods.price?.finalPrice }}</span><span
                        v-show="goods.price?.totalSpecPrice !== goods.price?.finalPrice"
                        class="line-through border rounded-full ml-2 px-2 border-orange-text  text-orange-text">{{ t('open2p.messages.priceSymbol') + goods.price?.totalSpecPrice }}</span>
                    </template>
                    <template v-else>{{ t('open2p.messages.noPrice') }}</template>
                  </template>
                  <template v-else>{{ t('open2p.messages.free') }}</template>
                </div>
              </div>
            </div>

            <!-- Action buttons -->
            <div class="self-center">
              <ShowButton :goods="goods" @reload="loadGoods" />
            </div>
          </div>
          <Divider class="divider" />
          <div class="bg-gray-text-bg p-2 text-black-label mt-3">{{ goods.introduction }}</div>
        </PureCard>
      </div>
    </Spin>

    <!-- Pagination component -->
    <Pagination
      v-bind="pagination"
      class="my-3"
      @change="changPage" />
  </div>
</template>

<style scoped>
/* Banner background gradient */
.my-bg {
  background-image: linear-gradient(to left top, rgba(223, 234, 253, 50%), rgba(175, 200, 255, 50%));
}

.divider {
  @apply bg-gray-divider;
}

/* Banner title styling */
.my-bg .my-title {
  font-size: 36px;
  font-weight: 600;
}

.my-bg .my-title .my-list {
  color: #4b5b77;
}

/* Decorative square element */
.my-square {
  border-radius: 1px;
  background-color: #0b53ff;
}

/* Banner image sizing */
.my-image {
  width: 20.125rem;
}

/* Carousel dots styling */
:deep(.slick-dots.slick-dots-bottom li:not(slick-active)) {
  background-color: rgba(0, 119, 255, 30%);
}

:deep(.ant-carousel li.slick-active button) {
  background-color: rgba(0, 119, 255, 100%);
}
</style>
