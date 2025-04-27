import { createRouter, createWebHistory } from 'vue-router';
import { guard, security } from '@xcan/security';

import routes from './routes';
import store from '@/store';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});

function setCode (code: number): void {
  store.commit('setStatusCode', code);
}

const startupGuard = (): void => {
  guard.navigationGuard(router, security.menuList, setCode);
};

export { startupGuard };
export default router;
