import { RouteRecordRaw } from 'vue-router';
import { Component } from 'vue';

import Signin from '@/layout/Signin.vue';
import Default from '@/layout/Default.vue';
import Personal from '@/layout/Personal.vue';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/signin',
    component: Signin,
    children: [
      {
        path: '/signin',
        component: () => import('@/views/sign/signin/index.vue'),
        meta: {
          noAuth: true
        }
      },
      {
        path: '/password/init',
        component: () => import('@/views/sign/password/init/index.vue'),
        meta: {
          noAuth: true
        }
      }
    ]
  },
  {
    path: '/signup',
    component: () => import('@/views/sign/signup/index.vue'),
    meta: {
      noAuth: true
    }
  },
  {
    path: '/password/reset',
    component: () => import('@/views/sign/password/reset/index.vue'),
    meta: {
      noAuth: true
    }
  },
  {
    path: '/stores',
    children: [
      {
        path: '/stores/cloud/open2p',
        component: () => import('@/views/stores/open2Priv/index.vue'),
        meta: {
          noAuth: true
        }
      },
      {
        path: '/stores/cloud/open2p/:id',
        component: () => import('@/views/stores/open2Priv/info/index.vue'),
        meta: {
          noAuth: true
        }
      }
    ]
  },
  {
    path: '/gm',
    component: Default,
    children: [
      {
        path: '/organization/user',
        component: (): Component => import('@/views/organization/user/index.vue')
      },
      {
        path: '/organization/user/add',
        component: (): Component => import('@/views/organization/user/components/edit/index.vue'),
        meta: {
          breadcrumb: [
            { name: 'router.breadcrumb.user', path: '/organization/user' },
            { name: 'router.breadcrumb.addUser' }
          ]
        }
      },
      {
        path: '/organization/user/edit/:id',
        component: (): Component => import('@/views/organization/user/components/edit/index.vue'),
        meta: {
          breadcrumb: [
            { name: 'router.breadcrumb.user', path: '/organization/user' },
            { name: 'router.breadcrumb.editUser' }
          ]
        }
      },
      {
        path: '/organization/user/:id',
        component: (): Component => import('@/views/organization/user/components/detail/index.vue'),
        meta: {
          breadcrumb: [
            { name: 'router.breadcrumb.user', path: '/organization/user' },
            { name: 'router.breadcrumb.detail' }
          ]
        }
      },
      {
        path: '/organization/dept',
        component: (): Component => import('@/views/organization/dept/index.vue')
      },
      {
        path: '/organization/group',
        component: (): Component => import('@/views/organization/group/index.vue')
      },
      {
        path: '/organization/group/add',
        component: (): Component => import('@/views/organization/group/components/edit/index.vue'),
        meta: {
          breadcrumb: [
            { name: 'router.breadcrumb.group', path: '/organization/group' },
            { name: 'router.breadcrumb.add' }
          ]
        }
      },
      {
        path: '/organization/group/edit/:id',
        component: (): Component => import('@/views/organization/group/components/edit/index.vue'),
        meta: {
          breadcrumb: [
            { name: 'router.breadcrumb.group', path: '/organization/group' },
            { name: 'router.breadcrumb.edit' }
          ]
        }
      },
      {
        path: '/organization/group/:id',
        component: (): Component => import('@/views/organization/group/components/detail/index.vue'),
        meta: {
          breadcrumb: [
            { name: 'router.breadcrumb.group', path: '/organization/group' },
            { name: 'router.breadcrumb.detail' }
          ]
        }
      },
      {
        path: '/organization/tag',
        component: (): Component => import('@/views/organization/tag/index.vue'),
        meta: {}
      },
      {
        path: '/permissions/policy',
        component: () => import('@/views/permission/policy/index.vue')
      },
      {
        path: '/permissions/policy/auth/:id',
        component: () => import('@/views/permission/policy/components/auth/index.vue'),
        meta: {
          breadcrumb: [
            { name: 'router.breadcrumb.strategy', path: '/permissions/policy' },
            { name: 'router.breadcrumb.authorisation' }
          ]
        }
      },
      {
        path: '/permissions/policy/:id',
        component: () => import('@/views/permission/policy/components/detail/index.vue'),
        meta: {
          breadcrumb: [
            // { name: 'common.permissions' },
            { name: 'router.breadcrumb.strategy', path: '/permissions/policy' },
            { name: 'router.breadcrumb.strategyDetail' }
          ]

        }
      },
      {
        path: '/permissions/view',
        component: () => import('@/views/permission/view/index.vue')
      },
      {
        path: '/stores/cloud',
        component: () => import('@/views/stores/cloud/stores/index.vue'),
        meta: {
          class: 'pr-0'
        }
      },
      {
        path: '/stores/cloud/:id',
        component: () => import('@/views/stores/cloud/stores/info/index.vue'),
        meta: {
          class: 'pr-0'
        }
      },
      {
        path: '/stores/goods',
        component: () => import('@/views/stores/cloud/goods/index.vue')
      },
      {
        path: '/stores/license',
        component: () => import('@/views/stores/cloud/license/index.vue')
      },
      // {
      //   path: '/storespriv/cloud',
      //   component: () => import('@/views/storesPriv/cloud/index.vue'),
      //   meta: {
      //     class: 'pr-0'
      //   }
      // },
      {
        path: '/storespriv/cloud/info/:id',
        component: () => import('@/views/stores/priv/stores/index.vue'),
        meta: {
          class: 'pr-0'
        }
      },
      {
        path: '/storespriv/goods',
        component: () => import('@/views/stores/priv/goods/index.vue')
      },
      {
        path: '/storespriv/goods/:id',
        component: () => import('@/views/stores/priv/goods/detail/index.vue')
      },
      {
        path: '/storespriv/license',
        component: () => import('@/views/stores/priv/license/index.vue')
      },
      {
        path: '/messages/notification',
        component: () => import('@/views/message/notice/index.vue')
      },
      {
        path: '/messages/notification/:id',
        component: () => import('@/views/message/notice/components/detail/index.vue'),
        meta: {
          breadcrumb: [
            { name: 'router.breadcrumb.announcement', path: '/messages/notification' },
            { name: 'router.breadcrumb.detail' }
          ]
        }
      },
      {
        path: '/messages/notification/send',
        component: () => import('@/views/message/notice/components/add/index.vue')
      },
      {
        path: '/messages/message',
        component: () => import('@/views/message/message/index.vue')
      },
      {
        path: '/messages/message/send',
        component: () => import('@/views/message/message/components/send/index.vue'),
        meta: {
          breadcrumb: [
            { name: 'router.breadcrumb.sendMessage', path: '/messages/message' },
            { name: 'router.breadcrumb.send' }
          ]
        }
      },
      {
        path: '/messages/message/:id',
        component: () => import('@/views/message/message/components/detail/index.vue'),
        meta: {
          breadcrumb: [
            { name: 'router.breadcrumb.sendMessage', path: '/messages/message' },
            { name: 'router.breadcrumb.detail' }
          ]
        }
      },
      {
        path: '/system/version',
        name: 'Version',
        component: () => import('@/views/system/version/index.vue')
      },
      {
        path: '/system/auth',
        name: 'Auth',
        component: () => import('@/views/system/realname/index.vue')
      },
      {
        path: '/system/ldap',
        name: 'ldap',
        component: () => import('@/views/system/ldap/index.vue')
      },
      {
        path: '/system/ldap/detail',
        name: 'ldapDetail',
        component: () => import('@/views/system/ldap/components/detail/index.vue'),
        meta: {
          breadcrumb: [
            { name: 'router.breadcrumb.ldap', path: '/system/ldap' },
            { name: 'router.breadcrumb.edit' }
          ]
        }
      },
      {
        path: '/system/ldap/add',
        name: 'add',
        component: () => import('@/views/system/ldap/components/detail/index.vue'),
        meta: {
          breadcrumb: [
            { name: 'router.breadcrumb.ldap', path: '/system/ldap' },
            { name: 'router.breadcrumb.add' }
          ]
        }
      },
      {
        path: '/system/security',
        name: 'securityConfig',
        component: () => import('@/views/system/security/index.vue')
      },
      {
        path: '/system/email/server',
        component: (): Component => import('@/views/system/email/server/index.vue'),
        meta: {
          flexCol: true
        }
      },
      {
        path: '/system/email/server/:id',
        component: (): Component => import('@/views/system/email/server/components/edit/index.vue'),
        meta: {
          breadcrumb: [
            { name: 'router.breadcrumb.mailboxServer', path: '/system/email' },
            { name: 'router.breadcrumb.detail' }
          ],
          flexCol: true
        }
      },
      {
        path: '/system/email/server/add',
        component: (): Component => import('@/views/system/email/server/components/edit/index.vue'),
        meta: {
          breadcrumb: [
            { name: 'router.breadcrumb.mailboxServer', path: '/system/email' },
            { name: 'router.breadcrumb.add' }
          ],
          flexCol: true
        }
      },
      {
        path: '/system/email/server/edit/:id',
        component: (): Component => import('@/views/system/email/server/components/edit/index.vue'),
        meta: {
          breadcrumb: [
            { name: 'router.breadcrumb.mailboxServer', path: '/system/email' },
            { name: 'router.breadcrumb.edit' }
          ],
          flexCol: true
        }
      },
      {
        path: '/system/email/records',
        component: (): Component => import('@/views/system/email/records/index.vue'),
        meta: {
          flexCol: true
        }
      },
      {
        path: '/system/email/records/:id',
        component: (): Component => import('@/views/system/email/records/components/detail/index.vue'),
        meta: {
          breadcrumb: [
            { name: 'router.breadcrumb.sendRecords', path: '/system/email/records' },
            { name: 'router.breadcrumb.detail' }
          ],
          flexCol: true
        }
      },
      {
        path: '/system/sms/channel',
        component: () => import('@/views/system/sms/channel/index.vue')
      },
      {
        path: '/system/sms/template',
        component: () => import('@/views/system/sms/template/index.vue')
      },
      {
        path: '/system/sms/records',
        component: (): Component => import('@/views/system/sms/records/index.vue'),
        meta: {
          flexCol: true
        }
      },
      {
        path: '/system/sms/records/:id',
        component: (): Component => import('@/views/system/sms/records/components/detail/index.vue'),
        meta: {
          breadcrumb: [
            { name: 'router.breadcrumb.sendRecords', path: '/system/sms/records' },
            { name: 'router.breadcrumb.detail' }
          ],
          flexCol: true
        }
      },
      {
        path: '/system/event/template',
        name: 'EventTemplate',
        component: () => import('@/views/system/event/template/index.vue')
      },
      {
        path: '/system/event/log',
        name: 'EventLog',
        component: () => import('@/views/system/event/records/index.vue')
      },
      {
        path: '/system/event/channel',
        name: 'ReceivingConfiguration',
        component: () => import('@/views/system/event/channel/index.vue')
      },
      {
        path: '/system/quota',
        component: (): Component => import('@/views/system/quota/index.vue')
      },
      {
        path: '/system/storage',
        name: 'Storage',
        component: () => import('@/views/system/storage/index.vue')
      },
      {
        path: '/system/log/operation',
        name: 'logOp',
        component: () => import('@/views/system/log/operation/index.vue')
      },
      {
        path: '/system/log/system',
        name: 'logSystem',
        component: () => import('@/views/system/log/system/index.vue')
      },
      {
        path: '/system/log/request',
        name: 'logRequest',
        component: () => import('@/views/system/log/request/index.vue')
      },
      {
        path: '/system/online',
        name: 'onlineUser',
        component: () => import('@/views/system/online/index.vue')
      },
      {
        path: '/system/appearance',
        name: 'appearance',
        component: () => import('@/views/system/appearance/index.vue')
      },
      {
        path: '/system/token',
        name: 'Token',
        component: () => import('@/views/system/token/index.vue')
      },
      {
        path: '/system/other',
        component: (): Component => import('@/views/system/other/index.vue'),
        meta: {
          flexCol: true
        }
      }
    ]
  },
  {
    path: '/personal',
    component: Personal,
    children: [
      {
        path: '/personal/information',
        component: () => import('@/views/personal/information/index.vue'),
        meta: {
          noAuth: true
        }
      },
      {
        path: '/personal/information/other',
        component: () => import('@/views/personal/information/components/other.vue'),
        meta: {
          breadcrumb: [
            { name: 'router.breadcrumb.basicInfo', path: '/information' },
            { name: 'router.breadcrumb.otherAccount' }
          ],
          noAuth: true
        }
      },
      {
        path: '/personal/security',
        component: () => import('@/views/personal/security/index.vue'),
        meta: {
          noAuth: true
        }
      },
      {
        path: '/personal/token',
        component: () => import('@/views/personal/token/index.vue'),
        meta: {
          noAuth: true
        }
      },
      {
        path: '/personal/messages/:id?',
        component: () => import('@/views/personal/messages/index.vue'),
        meta: {
          noAuth: true
        }
      }
    ]
  }
];

export default routes;
