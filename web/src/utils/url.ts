import { binary, site } from '@xcan-angus/tools';

// Redirect URL after successful login
const redirectTo = async (data?: { accessToken: string; refreshToken: string; clientId: string; }): Promise<void> => {
  const href = await getRedirectUrl();
  const url = new URL(href);
  if (data) {
    const { accessToken, refreshToken, clientId } = data;
    url.searchParams.set('_aptx_', window.btoa(accessToken));
    url.searchParams.set('_rptx_', window.btoa(refreshToken));
    url.searchParams.set('_cpix_', window.btoa(clientId));
  }

  window.location.href = url.href;
};

const getRedirectUrl = async (): Promise<string> => {
  const { searchParams } = new URL(location.href);
  const target = searchParams.get('t');
  let host = await site.getUrl('at');
  if (!host || host === 'undefined') {
    host = await site.getUrl('gm');
  }
  if (!target) {
    return host;
  }

  try {
    const url = new URL(binary.fromBinary(target));
    return url.href;
  } catch (error) {
    return host;
  }
};

export { redirectTo, getRedirectUrl };
