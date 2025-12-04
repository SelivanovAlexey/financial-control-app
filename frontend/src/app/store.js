import { combineReducers, configureStore } from '@reduxjs/toolkit';
import { userReducer } from '../reducers/userReducer';
import { useDispatch as dispatchHook , useSelector as selectorHook} from 'react-redux';
import { persistReducer, persistStore, FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER } from 'redux-persist';
import createWebStorage from "redux-persist/lib/storage/createWebStorage";

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
};

const persistedReducer = persistReducer(persistConfig, rootReducer);

export const store = configureStore({
  reducer: persistedReducer,
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER], // игнорируем стандартные действия persistence
      },
    }),
});

export const persistor = persistStore(store);

export const useDispatch = () => dispatchHook();
export const useSelector = selectorHook;

export default store;