"use client";
import { useEffect } from "react";
import { useDispatch, useSelector } from "@/app/store";
import { useRouter } from "next/navigation";
import { fetchExpenses, fetchIncomes } from "@/reducers/userReducer";

export default function AuthInitializer({ children }) {
  const dispatch = useDispatch();
  const router = useRouter();
  const { isAuthenticated, isAuthChecked } = useSelector((state) => state.user);

  useEffect(() => {
    const checkSession = async () => {
      if (isAuthenticated && isAuthChecked) {
        try {
          // Пробуем загрузить данные для проверки сессии
          await Promise.all([
            dispatch(fetchExpenses()).unwrap(),
            dispatch(fetchIncomes()).unwrap()
          ]);
        } catch (error) {
          console.log('Сессия истекла, перенаправляем на логин');
          router.push('/login');
        }
      }
    };

    checkSession();
  }, [dispatch, router, isAuthenticated, isAuthChecked]);

  return children;
}