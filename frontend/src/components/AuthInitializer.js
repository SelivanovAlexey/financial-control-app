"use client";
import { useEffect } from "react";
import { useDispatch, useSelector } from "@/app/store";
import { useRouter } from "next/navigation";

export default function AuthInitializer({ children }) {
  const dispatch = useDispatch();
  const router = useRouter();
  const { isAuthenticated, isAuthChecked } = useSelector((state) => state.user);

  useEffect(() => {
    const checkSession = async () => {
      if (isAuthenticated && isAuthChecked) {
        try {
          console.log('Данные загружены')
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