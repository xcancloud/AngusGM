// Get top right menu items
import { DomainManager, AppOrServiceRoute, appContext } from '@xcan-angus/infra';

export const getTopRightMenu = async () => {
  const domainManager = DomainManager.getInstance();
  const expenseUrl = domainManager.getAppDomain(AppOrServiceRoute.expense);
  const ticketUrl = domainManager.getAppDomain(AppOrServiceRoute.wo);
  const officialWebsiteUrl = domainManager.getAppDomain(AppOrServiceRoute.www);

  let menus = [
    { code: 'GlobalSearch', hasAuth: true, showName: 'fixedTopMenu.globalSearch' },
    { code: 'Expense', hasAuth: true, showName: 'fixedTopMenu.personalCenter.expense', url: expenseUrl },
    { code: 'Ticket', hasAuth: true, showName: 'fixedTopMenu.personalCenter.ticket', url: ticketUrl },
    { code: 'OfficialWebsite', hasAuth: true, showName: 'fixedTopMenu.personalCenter.officialWebsite', url: officialWebsiteUrl },
    { code: 'Information', hasAuth: true, showName: 'fixedTopMenu.information' }
  ];

  if (!appContext.isCloudServiceEdition()) {
    menus = menus.filter(item => !['Expense', 'Ticket', 'OfficialWebsite'].includes(item.code));
  }
  return menus;
};
