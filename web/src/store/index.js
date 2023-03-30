import { createStore } from 'vuex'
import ModuleUser from './user'
import ModulerPk from './pk'

export default createStore({
  state: {
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    user:ModuleUser,
    pk:ModulerPk,
  }
})
