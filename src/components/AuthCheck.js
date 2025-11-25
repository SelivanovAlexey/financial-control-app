"use client";
import { useSelector } from "@/app/store";
import { useRouter } from "next/navigation";
import { useDispatch } from "react-redux";
import { useEffect, useRef } from "react";
import { fetchExpenses } from "@/reducers/userReducer";

const AuthCheck = ({ children }) => {
  const dispatch = useDispatch();
  const router = useRouter();
  const { isAuthenticated, isAuthChecked } = useSelector((state) => state.user);
  const expensesLoaded = useRef(false);

  useEffect(() => {
    if (!isAuthenticated && isAuthChecked) {
      router.push('/login');
    }
    
    if (isAuthenticated && isAuthChecked && !expensesLoaded.current) {
      dispatch(fetchExpenses());
      expensesLoaded.current = true;
    }
  }, [dispatch, router, isAuthenticated, isAuthChecked]);

  if (!isAuthChecked) {
    return <div>Loading...</div>;
  }

  if (isAuthenticated) {
    return <>{children}</>;
  }

  return null;
};

export default AuthCheck;