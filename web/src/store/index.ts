import { sessionStore } from '@xcan/utils';
import { createStore } from 'vuex';

export default createStore({
  state: {
    statusCode: 200,
    layoutCode: undefined
  },
  mutations: {
    setStatusCode (state, payload) {
      state.statusCode = payload;
    },
    setLayoutCodeCode (state, payload) {
      state.layoutCode = payload;
      sessionStore.set('__LC__', payload);
    }
  },
  actions: {},
  modules: {}
});
