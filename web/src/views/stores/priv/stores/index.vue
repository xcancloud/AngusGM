<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { appContext, cookieUtils } from '@xcan-angus/infra';

const router = useRouter();
const route = useRoute();

const accessToken = cookieUtils.get('access_token') || '';
const refreshToken = cookieUtils.get('refresh_token') || '';
const localeCookie = cookieUtils.get('localeCookie') || '';
const timezone = cookieUtils.get('timezone') || '';
const clientSecret = import.meta.env.VITE_OAUTH_CLIENT_SECRET || '';

const id = route.params.id || '';

const cloudRoute = ref(`/stores/cloud/open2p${id ? '/' + id : ''}`);
const iframeSrc = ref<string>();

watch(() => route.path, newValue => {
  if (newValue === '/storespriv/cloud') {
    const iframe = document.getElementById('iframe') as HTMLIFrameElement;
    if (!iframe.contentWindow) return;
    cloudRoute.value = '/stores/cloud/open2p';
  }
}, { deep: true });

onMounted(async () => {
  // const host = await site.getUrl('gm', true); // 本地联调使用

  const host = 'http://store.xcan.cloud'; // 提交使用
  const url = new URL(host + cloudRoute.value);
  url.searchParams.append('at', accessToken);
  url.searchParams.append('rt', refreshToken);
  url.searchParams.append('lc', localeCookie);
  url.searchParams.append('tz', timezone);
  url.searchParams.append('cs', clientSecret);
  url.searchParams.append('uid', appContext.getUser()?.id);
  iframeSrc.value = url.href;

  window.addEventListener('message', function (e) {
    if (typeof e.data === 'object' && e.data.e === 'purchase') {
      window.open(e.data.value, '_blank');
      return;
    }
    if (typeof e.data === 'string') {
      cloudRoute.value = '/stores/cloud/open2p/' + e.data;
      router.push(`/storespriv/cloud/info/${e.data}`);
    }
  }, false);
});
</script>
<template>
  <div class="h-full w-full overflow-hidden bg-theme-main">
    <iframe
      id="iframe"
      class="bg-theme-main"
      :src="iframeSrc"
      style="width: calc(100% + 2px); height: calc(100vh - 70px);"
      frameborder="0"
      border="0"
      marginwidth="0"
      marginheight="0"
      allowtransparency="true">
    </iframe>
  </div>
</template>
