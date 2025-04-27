import { site } from '@xcan-angus/tools';

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
  const expenseUrl = await site.getUrl('expense');
  const workOrderUrl = await site.getUrl('wo');
  const officialWebsiteUrl = await site.getUrl('www');
  const envConfigs = await site.getProfiles();

  let menus = [
    { code: 'GlobalSearch', hasAuth: true, showName: '搜索框' },
    { code: 'Expense', hasAuth: true, showName: 'personalCenter.expense', url: expenseUrl },
    { code: 'WorkOrder', hasAuth: true, showName: 'personalCenter.workOrder', url: workOrderUrl },
    { code: 'OfficialWebsite', hasAuth: true, showName: 'personalCenter.officialWebsite', url: officialWebsiteUrl },
    { code: 'Information', hasAuth: true, showName: '消息中心' }
  ];

  if (envConfigs.VITE_EDITION_TYPE !== 'CLOUD_SERVICE') {
    menus = menus.filter(item => !['Expense', 'WorkOrder', 'OfficialWebsite'].includes(item.code));
  }

  return menus;
};

export { personalCenterMenus, getTopRightMenu };
