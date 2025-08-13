import { createRouter, createWebHistory } from 'vue-router';
import { appContext, guard } from '@xcan-angus/infra';

import routes from './routes';
import store from '@/utils/store';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});

function setCode (code: number): void {
  store.commit('setStatusCode', code);
}

const startupGuard = (): void => {
  guard.navigationGuard(router, appContext.getAccessAppFuncTree() || [], setCode);
};

export { startupGuard };
export default router;
