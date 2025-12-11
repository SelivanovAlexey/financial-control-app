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
import { useEffect, useRef, useState } from "react";
import { fetchExpenses, fetchIncomes } from "@/reducers/userReducer";
import { parseDateSafely, formatDateForDisplay, getTimestampForSorting, formatToISOWithTimezone, createDateForBackend } from "@/utils/Utils";
import { useTheme } from '@mui/material/styles';
import { IconButton, useMediaQuery, Menu, MenuItem, Typography } from '@mui/material';
import { Avatar, Card, Flex, Grid, Divider, Collapse, Button } from 'antd'; 
import { ArrowDownOutlined, ArrowUpOutlined, MoreOutlined  } from '@ant-design/icons';

const historySettings = ['Редактировать','Удалить'];

export default function History() {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('768'));

  const dataCheckedRef = useRef(false);
  const dispatch = useDispatch();
  const { expenses, incomes, isLoading, userError } = useSelector(state => state.user);

  const [deletingId, setDeletingId] = useState(null);
  const [anchorEl, setAnchorEl] = useState(null);
  const [selectedItem, setSelectedItem] = useState(null);
  
  useEffect(() => {
    if (!dataCheckedRef.current && expenses.length === 0 && incomes.length === 0) {
      dispatch(fetchExpenses());
      dispatch(fetchIncomes());
    }
    dataCheckedRef.current = true;
  }, [dispatch, expenses.length, incomes.length]);

  const sortedRows = React.useMemo(() => {
    const allTransactions = [];

    // Обрабатываем расходы
    expenses.forEach(expense => {
      const displayDate = formatDateForDisplay(expense.createDate);
      const timestamp = getTimestampForSorting(expense.createDate);
      const isoWithTZ = formatToISOWithTimezone(expense.createDate);
      
      allTransactions.push({
        id: `expense_${expense.id}`,
        date: displayDate,
        category: expense.category,
        description: expense.description,
        amount: -expense.amount,
        type: 'expense',
        timestamp: timestamp,
        isoDate: isoWithTZ,
        rawDate: expense.createDate,
        dataType: 'expense'
      });
    });

    // Обрабатываем доходы
    (incomes || []).forEach(income => {
      const displayDate = formatDateForDisplay(income.createDate);
      const timestamp = getTimestampForSorting(income.createDate);
      const isoWithTZ = formatToISOWithTimezone(income.createDate);
      
      allTransactions.push({
        id: `income_${income.id}`,
        date: displayDate,
        category: income.category,
        description: income.description,
        amount: income.amount,
        type: 'income',
        timestamp: timestamp,
        isoDate: isoWithTZ,
        rawDate: income.createDate,
        dataType: 'income'
      });
    });

    // Сортируем по timestamp (от новых к старым)
    const sorted = [...allTransactions].sort((a, b) => {
      return b.timestamp - a.timestamp;
    });

    return sorted;
  }, [expenses, incomes]);

  // Обработчики меню
  const handleMenuClick = (event, item) => {
    setAnchorEl(event.currentTarget);
    setSelectedItem(item);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    setSelectedItem(null);
  };

  const handleDelete = async (id, dataType) => {
    setDeletingId(id);
    
    try {
      // АПИ для удаления операции
      // if (dataType === 'expense') {
      //   // await dispatch(deleteExpense(id));
      // } else if (dataType === 'income') {
      //   // await dispatch(deleteIncome(id));
      // }
      
      // После успешного удаления можно обновить данные
      // dispatch(fetchExpenses());
      // dispatch(fetchIncomes());
      
      console.log(`Удаление операции ${dataType} с ID: ${id}`);
      
    } catch (error) {
      console.error('Ошибка при удалении:', error);
    } finally {
      setDeletingId(null);
    }
  };

  const handleEdit = (id, dataType) => {
    console.log(`Редактирование операции ${dataType} с ID: ${id}`);
    // Реализация редактирования
  };

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
    sortedRows.length > 0 ? (
      <div className={styles.history_container}>
        <div className={styles.history_title}>История операций</div>
        {!isMobile && (
          <div className={styles.history_list}>
          {sortedRows.map((row, index) => (
            <Card key={index} className={styles.history_item}>
            <div className={styles.history_item_content}>
              <div className={styles.history_item_type}>
                <Avatar shape="square" style={{borderRadius: "10px"}} size={50} icon={row.type === "expense" ? <ArrowUpOutlined style={{color: "var(--red-color)"}}/> : <ArrowDownOutlined style={{color: "var(--accent-color)"}}/>} />
                <div className={styles.history_item_info_left}>
                  <div className={styles.history_item_category}>
                    {row.category}
                  </div>
                  {row.description.length > 0 && <div className={styles.history_item_desc}>{row.description}</div>}
                </div>
              </div>
              
              <Divider variant="dashed" vertical style={{height: '60px', margin: '0 20px', width: '1px', borderColor: 'var(--main-color)'}} />
              
              <div className={styles.history_item_info_right}>
                <div className={styles.history_item_date}>
                  {row.date}
                </div>
                <Divider variant="dashed" vertical style={{height: '60px', margin: '0 20px', width: '1px', borderColor: 'var(--main-color)'}} />
                <div className={styles.history_item_amount}>
                  {row.type === "expense" ? "" : "+"} {row.amount} ₽
                </div>
              </div>
              
              <div className={styles.history_item_actions}>
                <IconButton size='large' onClick={(e) => handleMenuClick(e, row)}>
                  <MoreOutlined style={{color: "var(--main-color)"}}/>
                </IconButton>
                <Menu
                  sx={{ mt: '65px'}}
                  id="menu-appbar"
                  anchorEl={anchorEl}
                  anchorOrigin={{
                    vertical: 'top',
                    horizontal: 'right',
                  }}
                  keepMounted
                  transformOrigin={{
                    vertical: 'top',
                    horizontal: 'right',
                  }}
                  open={Boolean(anchorEl)}
                  onClose={handleMenuClose}
                >
                  {historySettings.map((setting) => (
                    <MenuItem key={setting} onClick={handleMenuClose}>
                      <Typography sx={{ textAlign: 'center' }}>{setting}</Typography>
                    </MenuItem>
                        ))}
                  </Menu>
              </div>
            </div>
          </Card>
          ))}
        </div>
        )}
        {isMobile && (
          <div className={styles.history_list}>
          {sortedRows.map((row, index) => (
            <Collapse 
              key={index} 
              className={styles.history_item}
              style={{
                backgroundColor: 'var(--foreground)',
                color: 'var(--font-color)',
              }}
              items={[{
                key: index,
                showArrow: false,
                label: (
                  <div className={styles.collapse_label}>
                    <div className={styles.collapse_icon}>
                      <Avatar shape="square" style={{borderRadius: "10px"}} size={40} icon={row.type === "expense" ? <ArrowUpOutlined style={{color: "var(--red-color)"}}/> : <ArrowDownOutlined style={{color: "var(--accent-color)"}}/>} />
                    </div>
                  </div>
                )
              }]}
            />
          ))}
        </div>
        )}
    </div>
    ) : (
      <div className={styles.history_container}>
        <div className={styles.history_title}>История операций</div>
        <div style={{ color: "var(--font-color)", textAlign: "center", marginTop: "30vh"}}>
          Здесь пока ничего нет...
        </div>
      </div>
    )
    )
}