import { combineReducers, configureStore } from '@reduxjs/toolkit';
import { userReducer } from '../reducers/userReducer';
import { useDispatch as dispatchHook , useSelector as selectorHook} from 'react-redux';
import { persistReducer, persistStore, FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER } from 'redux-persist';
import createWebStorage from "redux-persist/lib/storage/createWebStorage";
import autoMergeLevel2 from 'redux-persist/lib/stateReconciler/autoMergeLevel2';

const createNoopStorage = () => {
  return {
    getItem(_key) {
      return Promise.resolve(null);
    },
    setItem(_key, value) {
      return Promise.resolve(value);
    },
    removeItem(_key) {
      return Promise.resolve();
    },
  };
};

const storage = typeof window !== "undefined" ? createWebStorage("local") : createNoopStorage();

const rootReducer = combineReducers({
    user: userReducer,
});

// Настройки для хранения состояния
const persistConfig = {
  key: 'root',
  storage,
  stateReconciler: autoMergeLevel2,
  // Указываем, какие поля НЕ нужно сохранять при перезагрузке
  // если пользователь не выбрал "запомнить меня"
  blacklist: ['user.expenses', 'user.incomes'], // Не сохраняем данные при сессии
  // Или используем миграцию для управления состоянием
  migrate: (state) => {
    // Проверяем, нужно ли сохранять данные сессии
    // Здесь вы можете добавить логику проверки "запомнить меня"
    return Promise.resolve(state);
  }
};

const persistedReducer = persistReducer(persistConfig, rootReducer);

export const store = configureStore({
  reducer: persistedReducer,
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER],
      },
    }),
});

export const persistor = persistStore(store);

export const useDispatch = () => dispatchHook();
export const useSelector = selectorHook;

export default store;