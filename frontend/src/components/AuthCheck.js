"use client";
import { useSelector } from "@/app/store";
import { useRouter } from "next/navigation";
import { useDispatch } from "react-redux";
import { useEffect, useRef } from "react";
import { fetchExpenses, fetchIncomes } from "@/reducers/userReducer";

const AuthCheck = ({ children }) => {
  const dispatch = useDispatch();
  const router = useRouter();
  const { isAuthenticated, isAuthChecked } = useSelector((state) => state.user);
  const expensesLoaded = useRef(false);

  useEffect(() => {
    // Перенаправляем на логин, если не авторизован
    if (!isAuthenticated && isAuthChecked) {
      router.push('/login');
      return;
    }
    
    // Если авторизован и данные еще не загружены - загружаем
    if (isAuthenticated && isAuthChecked && !expensesLoaded.current) {
      console.log('Загрузка данных для авторизованного пользователя');
      dispatch(fetchExpenses());
      dispatch(fetchIncomes());
      expensesLoaded.current = true;
    }
  }, [dispatch, router, isAuthenticated, isAuthChecked]);

  // Показываем лоадер пока проверяем авторизацию
  if (!isAuthChecked) {
    return <div>Loading...</div>;
  }

  // Разрешаем доступ только если авторизован
  return isAuthenticated ? <>{children}</> : null;
};

export default AuthCheck;