"use client";
import { useSelector, useDispatch } from "@/app/store";
import { useRouter } from "next/navigation";
import { useEffect, useRef } from "react";
import { checkAuth } from "@/reducers/userReducer";
import CircularProgress from '@mui/material/CircularProgress';
import { Box } from "@mui/material";

const AuthCheck = ({ children }) => {
  const dispatch = useDispatch();
  const router = useRouter();
  const { isAuthenticated, isAuthChecked, isLoading } = useSelector((state) => state.user);
  
  // Refs для отслеживания состояния
  const initStarted = useRef(false);
  const dataLoadStarted = useRef(false);
  const redirectAttempted = useRef(false);

  // Эффект 1: Проверка авторизации (только один раз)
  useEffect(() => {
    // Защита от повторных вызовов
    if (initStarted.current) return;
    initStarted.current = true;
    
    console.log('🔒 Начинаем проверку авторизации...');
    
    dispatch(checkAuth());
  }, [dispatch]);

  // Эффект 2: Обработка результата проверки авторизации
  useEffect(() => {
    // Пропускаем если проверка еще не завершена
    if (!isAuthChecked) return;
    
    // Пропускаем если уже пытались редиректить
    if (redirectAttempted.current) return;

    if (!isAuthenticated) {
      console.log('🚫 Не авторизован, выполняю редирект...');

      redirectAttempted.current = true;
      router.push('/login');
      return;
    }
    
    // Если авторизован - начинаем загрузку данных
    if (isAuthenticated && !dataLoadStarted.current) {
      console.log('✅ Авторизован, начинаю загрузку данных...');
      dataLoadStarted.current = true;
    }
  }, [isAuthenticated, isAuthChecked, router, dispatch]);

  // Если не авторизован (но проверка завершена) - ничего не показываем
  if (!isAuthenticated) {
    return null;
  }

  // Показываем детей
  return <>{children}</>;
};

export default AuthCheck;