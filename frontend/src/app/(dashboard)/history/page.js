"use client";
import styles from "@/app/page.module.css";
import * as React from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import { useDispatch, useSelector } from "../../store";
import { useEffect, useRef } from "react";
import { fetchExpenses, fetchIncomes } from "@/reducers/userReducer";

const formatDate = (dateString) => {
  if (!dateString) return '';
  
  try {
    const date = new Date(dateString);
    
    if (isNaN(date.getTime())) {
      const timestamp = parseInt(dateString);
      if (!isNaN(timestamp)) {
        return formatDate(new Date(timestamp * 1000).toISOString());
      }
      return '';
    }
    
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    
    return `${day}.${month}.${year}`;
  } catch (error) {
    console.error('Ошибка форматирования даты:', error);
    return '';
  }
};

export default function History() {

  const dataCheckedRef = useRef(false);

  const dispatch = useDispatch();
  const { expenses, incomes, isLoading, userError } = useSelector(state => state.user);
  
  useEffect(() => {
    // Проверяем, есть ли данные, и загружаем только если их нет
    if (!dataCheckedRef.current && expenses.length === 0 && incomes.length === 0) {
      dispatch(fetchExpenses());
      dispatch(fetchIncomes());
    }
    dataCheckedRef.current = true;
  }, [dispatch, expenses.length, incomes.length]);

  const sortedRows = React.useMemo(() => {
    const allTransactions = [];

    expenses.forEach(expense => {
      allTransactions.push({
        id: `expense_${expense.id}`,
        date: formatDate(expense.createDate),
        category: expense.category,
        description: expense.description,
        amount: -expense.amount,
        type: 'expense',
        timestamp: expense.createDate
      });
    });

    (incomes || []).forEach(income => {
      allTransactions.push({
        id: `income_${income.id}`,
        date: formatDate(income.createDate),
        category: income.category,
        description: income.description,
        amount: income.amount,
        type: 'income',
        timestamp: income.createDate
      });
    });

    const sorted = [...allTransactions].sort((a, b) => {
      return b.timestamp - a.timestamp;
    });

    return sorted;
  }, [expenses, incomes]);

  if (isLoading) {
    return (
      <div className={styles.table_container}>
        <div className={styles.table_header}>История операций</div>
        <div style={{ color: "var(--font-color)", textAlign: "center", padding: "2rem" }}>
          Загрузка...
        </div>
      </div>
    );
  }

  // Показываем ошибку
  if (userError) {
    return (
      <div className={styles.table_container}>
        <div className={styles.table_header}>История операций</div>
        <div style={{ color: "var(--red-color)", textAlign: "center", padding: "2rem" }}>
          Ошибка: {userError}
        </div>
      </div>
    );
  }

  return (
    <div className={styles.table_container}>
      <div className={styles.table_header}>История операций</div>
      <TableContainer>
        <Table sx={{ minWidth: 650 }}>
          <TableHead sx={{ backgroundColor: "var(--foreground)" }}>
            <TableRow>
              <TableCell sx={{ color: "var(--font-color)", fontSize: "calc(10px + 1vmin)" }} align="left">
                Дата
              </TableCell>
              <TableCell sx={{ color: "var(--font-color)", fontSize: "calc(10px + 1vmin)" }} align="left">
                Категория
              </TableCell>
              <TableCell sx={{ color: "var(--font-color)", fontSize: "calc(10px + 1vmin)" }} align="left">
                Описание
              </TableCell>
              <TableCell sx={{ color: "var(--font-color)", fontSize: "calc(10px + 1vmin)" }} align="left">
                Значение
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {sortedRows.length > 0 ? (
              sortedRows.map((row) => (
                <TableRow
                  key={row.id}
                  sx={{ 
                    '&:last-child td, &:last-child th': { border: 0 },
                  }}
                >
                  <TableCell 
                    sx={{ 
                      width: "25%", 
                      color: "var(--font-color)", 
                      fontSize: "calc(6px + 1vmin)" 
                    }} 
                    align="left"
                  >
                    {row.date}
                  </TableCell>
                  <TableCell 
                    sx={{ 
                      width: "25%", 
                      color: "var(--font-color)", 
                      fontSize: "calc(6px + 1vmin)" 
                    }} 
                    align="left"
                  >
                    {row.category}
                  </TableCell>
                  <TableCell 
                    sx={{ 
                      width: "40%", 
                      color: "var(--font-color)", 
                      fontSize: "calc(6px + 1vmin)" 
                    }} 
                    align="left"
                  >
                    {row.description}
                  </TableCell>
                  <TableCell 
                    sx={{ 
                      color: row.type === 'income' ? 'var(--accent-color)' : 'var(--red-color)', 
                      fontSize: "calc(6px + 1vmin)",
                      fontWeight: 'bold'
                    }} 
                    align="left"
                  >
                    {row.type === 'income' ? '+' : ''}{row.amount.toLocaleString('ru-RU')} ₽
                  </TableCell>
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell 
                  colSpan={4} 
                  sx={{ 
                    color: "var(--font-color)", 
                    textAlign: "center",
                    fontSize: "calc(8px + 1vmin)"
                  }}
                >
                  Нет данных о операциях
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
}
