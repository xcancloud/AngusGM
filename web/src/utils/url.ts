import { codeUtils, site } from '@xcan-angus/infra';

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
    const decodedTarget = codeUtils.fromBinary(target);
    const url = new URL(decodedTarget);

    // Validate that the URL is from a trusted domain
    const allowedHosts = [window.location.hostname, 'localhost', '127.0.0.1'];
    if (!allowedHosts.includes(url.hostname) && !url.hostname.endsWith('.xcan.company')) {
      console.warn('Untrusted redirect URL detected:', url.hostname);
      return host;
    }

    return url.href;
  } catch (error) {
    return host;
  }
};

export { redirectTo, getRedirectUrl };
