<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { store } from '@/api';
import { API, cookieUtils, http, VERSION } from '@xcan-angus/infra';
import { Carousel, Colon, Icon, Image, PureCard, StoreComment } from '@xcan-angus/vue-ui';
import { Tag } from 'ant-design-vue';
import { useI18n } from 'vue-i18n';

import VideoLink from '@/views/stores/open2Priv/info/videoLink/index.vue';
import ContentInfo from '@/views/stores/open2Priv/info/contentInfo/index.vue';
import ShowButton from '../components/showButton.vue';

import type { Goods } from '../PropsType';
import { getEnumMessages, goodsTypeColor } from '../PropsType';

const { t } = useI18n();
const route = useRoute();

// Route parameters and user context
const id = route.params.id as string;
const userId = cookieUtils.get('uid') as string;

const loading = ref(true);

// Goods detail data with default values
const goods = ref<Goods>({
  id: '',
  name: '',
  hot: false,
  introduction: '',
  tags: [],
  editionType: { value: '', message: '' },
  iconUrl: '',
  starNum: 0,
  goodsId: '',
  videos: [],
  features: [],
  bannerUrls: [],
  type: { value: '', message: '' },
  version: '',
  charge: true,
  createdByName: '',
  createdDate: '',
  price: {
    finalPrice: '0',
    totalSpecPrice: '0'
  },
  pricingUrl: '',
  onlineDate: '',
  applyEditionTypes: [],
  allowComment: false,
  productType: { value: '', message: '' }
});

/**
 * Load goods detail from API
 * Fetches complete goods information by ID
 */
const loadDetail = async () => {
  loading.value = true;

  try {
    const [error, res] = await http.get(`${window.location.ancestorOrigins[0]}/${API}/${VERSION}/store/priv/goods/${id}`);

    if (error) {
      return;
    }

    goods.value = res.data;
  } finally {
    loading.value = false;
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

// Load goods detail on component mount
onMounted(() => {
  loadDetail();
});
</script>

<template>
  <div class="h-full pr-2 bg-theme-main overflow-auto">
    <!-- Goods header information card -->
    <PureCard
      :key="goods.id"
      class="px-8 py-7 border text-3 bg-white">
      <div class="flex">
        <!-- Goods icon -->
        <Image
          type="image"
          :src="goods.iconUrl"
          class="w-28 h-28" />

        <!-- Goods details section -->
        <div class="ml-6 flex-1 self-center">
          <div>
            <span class="font-medium text-4 mr-4 text-blue-1">{{ goods.name }}</span>
            {{ goods.version }}
            <span class="inline-flex items-center ml-4 text-3.5 cursor-pointer" @click="starGoods(goods)">
              <Icon
                icon="icon-a-tuijiandianzan"
                class="text-3 mr-2"
                :class="{'text-blue-text': goods.star}" />{{ goods.starNum }}
            </span>
          </div>

          <!-- Goods tags -->
          <Tag
            v-for="tag in goods.tags"
            :key="tag"
            class="h-5 leading-5 px-1 mt-4">
            {{ tag }}
          </Tag>

          <!-- Goods metadata -->
          <div class="flex items-center mt-5 space-x-8">
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
          <ShowButton :goods="goods" />
        </div>
      </div>
    </PureCard>

    <!-- Banner carousel if available -->
    <PureCard v-if="goods?.bannerUrls?.length" class="mt-3">
      <Carousel :dataSource="goods?.bannerUrls" />
    </PureCard>

    <!-- Content and video sections -->
    <div class="flex mt-3">
      <div class="flex-1">
        <!-- Goods content information -->
        <ContentInfo :goods="goods" :loading="loading" />

        <!-- Comments section if enabled -->
        <StoreComment
          v-if="goods.goodsId && goods.allowComment"
          :goodsId="goods.goodsId"
          :tenantId="userId"
          class="mt-4" />
      </div>

      <!-- Video links sidebar -->
      <VideoLink
        class="ml-3"
        :goods="goods"
        :loading="loading" />
    </div>
  </div>
</template>
