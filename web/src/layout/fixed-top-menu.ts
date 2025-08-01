import {appContext, DomainManager, EditionType} from '@xcan-angus/infra';
import {AppOrServiceRoute} from '@xcan-angus/infra/lib/router/apiRouterPrefix';

const personalCenterMenus = [
  {
    hasAuth: true,
    tags: ['DYNAMIC_POSITION'],
    authCtrl: false,
    type: 'MENU',
    id: '1',
    showName: 'personalCenter.baseInfo',
    url: '/personal/information',
    show: true
  },
  {
    hasAuth: true,
    tags: ['DYNAMIC_POSITION'],
    authCtrl: false,
    type: 'MENU',
    id: '2',
    showName: 'personalCenter.securitySetting',
    url: '/personal/security',
    show: true
  },
  {
    hasAuth: true,
    tags: ['DYNAMIC_POSITION'],
    authCtrl: false,
    type: 'MENU',
    id: '4',
    showName: 'personalCenter.accessToken',
    url: '/personal/token',
    show: true
  },
  {
    hasAuth: true,
    tags: ['DYNAMIC_POSITION'],
    authCtrl: false,
    type: 'MENU',
    id: '5',
    showName: 'personalCenter.messages',
    url: '/personal/messages',
    show: true
  }
];

const getTopRightMenu = async () => {
  const domainManager = DomainManager.getInstance();
  const expenseUrl = await domainManager.getAppDomain(AppOrServiceRoute.expense);
  const workOrderUrl = await domainManager.getUrl(AppOrServiceRoute.wo);
  const officialWebsiteUrl = await domainManager.getUrl(AppOrServiceRoute.www);

  let menus = [
    { code: 'GlobalSearch', hasAuth: true, showName: '搜索框' },
    { code: 'Expense', hasAuth: true, showName: 'personalCenter.expense', url: expenseUrl },
    { code: 'WorkOrder', hasAuth: true, showName: 'personalCenter.workOrder', url: workOrderUrl },
    { code: 'OfficialWebsite', hasAuth: true, showName: 'personalCenter.officialWebsite', url: officialWebsiteUrl },
    { code: 'Information', hasAuth: true, showName: '消息中心' }
  ];

  if (appContext.getProfile() !== EditionType.CLOUD_SERVICE) {
    menus = menus.filter(item => !['Expense', 'WorkOrder', 'OfficialWebsite'].includes(item.code));
  }

  return menus;
};

export { personalCenterMenus, getTopRightMenu };
