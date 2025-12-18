"use client";
import styles from "@/app/page.module.css";
import * as React from 'react';
import { useDispatch, useSelector } from "../../store";
import { useEffect, useRef, useState } from "react";
import { fetchExpenses, fetchIncomes, fetchDeleteExpense, fetchDeleteIncome } from "@/reducers/userReducer";
import { formatDateForDisplay, getTimestampForSorting, formatToISOWithTimezone, createDateForBackend } from "@/utils/Utils";
import { useTheme } from '@mui/material/styles';
import { IconButton, useMediaQuery, Menu, MenuItem, Typography } from '@mui/material';
import { Avatar, Card, Flex, Grid, Divider, Collapse, Select } from 'antd'; 
import { ArrowDownOutlined, ArrowUpOutlined, MoreOutlined, RightOutlined, DownOutlined  } from '@ant-design/icons';

const historySettings = ['Удалить'];
const filterOptions =
  [
    {
      value: 'all',
      label: 'Все',
    },
    {
      value: 'expenses',
      label: 'Расходы',
    },
    {
      value: 'incomes',
      label: 'Доходы',
    },
  ]


export default function History() {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('768'));

  const dataCheckedRef = useRef(false);
  const dispatch = useDispatch();
  const { expenses, incomes, isLoading, userError } = useSelector(state => state.user);

  const [deletingId, setDeletingId] = useState(null);
  const [anchorEl, setAnchorEl] = useState(null);
  const [selectedItem, setSelectedItem] = useState(null);
  const [filter, setFilter] = useState('all');

  
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

  const filteredRows = React.useMemo(() => {
    if (filter === 'all') {
      return sortedRows;
    } else if (filter === 'expenses') {
      return sortedRows.filter(row => row.type === 'expense');
    } else if (filter === 'incomes') {
      return sortedRows.filter(row => row.type === 'income');
    }
    return sortedRows;
  }, [sortedRows, filter]);

  // Обработчики меню
  const handleMenuClick = (event, item) => {
    setAnchorEl(event.currentTarget);
    setSelectedItem(item);
  };

  const handleMenuClose = (e) => {
    setAnchorEl(null);
    setSelectedItem(null);
  };

  const handleDelete = async (id, dataType) => {
    const prefix = dataType === 'expense' ? 'expense_' : 'income_';
    const numericId = id.startsWith(prefix) 
      ? parseInt(id.replace(prefix, ''), 10)
      : parseInt(id, 10);
    
    if (isNaN(numericId)) {
      console.error('Некорректный ID для удаления:', id);
      return;
    }
    
    setDeletingId(id);
    
    try {
      if (dataType === 'expense') {
        await dispatch(fetchDeleteExpense(numericId));
      } else if (dataType === 'income') {
        await dispatch(fetchDeleteIncome(numericId));
      }
      
      dispatch(fetchExpenses());
      dispatch(fetchIncomes());
      
      handleMenuClose();
      
    } catch (error) {
      console.error('Ошибка при удалении:', error);
    } finally {
      setDeletingId(null);
    }
  };

  const handleFilterChange = (value) => {
    setFilter(value);
    if (value === 'all') {
      dispatch(fetchExpenses());
      dispatch(fetchIncomes());
    } else if (value === 'expenses') {
      dispatch(fetchExpenses());
    } else if (value === 'incomes') {
      dispatch(fetchIncomes());
    }
  };

  // const handleEdit = (id, dataType) => {
  //   console.log(`Редактирование операции ${dataType} с ID: ${id}`);
  //   // Реализация редактирования
  // };

  if (isLoading) {
    return (
      <div className={styles.table_container}>
        <div className={styles.history_title}>История операций</div>
        <div style={{ color: "var(--font-color)", textAlign: "center", padding: "2rem" }}>
          Загрузка...
        </div>
      </div>
    );
  }

  if (userError) {
    return (
      <div className={styles.table_container}>
        <div className={styles.history_title}>История операций</div>
        <div style={{ color: "var(--red-color)", textAlign: "center", padding: "2rem" }}>
          Ошибка: {userError}
        </div>
      </div>
    );
  }

  return (
    sortedRows.length > 0 ? (
      <div className={styles.history_container}>
        <div className={styles.history_title}>
          <div className={styles.history_title_text}>История операций</div>
          <div className={styles.history_title_filter}>
            <Select
              variant="underlined"
              style={{ 
                width: "100%", 
                background: "var(--background)", 
                color: "var(--font-color)", 
                fontSize: "calc(8px + 1vh)"
              }}
              suffixIcon={
                <DownOutlined style={{ color: "var(--main-color)" }} />
              }
              value={filter}
              options={filterOptions}
              onChange={handleFilterChange}
              popupRender={menu => (
                <div className={styles.select_menu}>
                  {menu}
                </div>
              )}
            />
          </div>
        </div>
        {!isMobile && (
          <div className={styles.history_list}>
          {filteredRows.map((row, index) => (
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
                <IconButton size='large' onClick={(e) => (
                        e.stopPropagation(),
                        handleMenuClick(e, row)
                )}>
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
                    <MenuItem key={setting} onClick={() => {
                      if (selectedItem) {
                        handleDelete(selectedItem.id, selectedItem.dataType);
                      }
                    }}>
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
          {filteredRows.map((row, index) => (
            <Collapse 
              key={index} 
              className={`${styles.history_item} custom-collapse-item`}
              ghost
              style={{
                backgroundColor: 'var(--foreground)',
                color: 'var(--font-color)',
              }}
              expandIconPlacement="start"
              expandIcon={({ isActive }) => (
                <div style={{
                  paddingTop: '26px',
                }}>
                  <RightOutlined 
                    rotate={isActive ? 90 : 0}
                    style={{
                      color: 'var(--main-color)',
                      transition: 'transform 0.3s',
                      fontSize: '12px',
                    }}
                  />
                </div>
              )}
              items={[{
                key: index,
                showArrow: true,
                label: (
                  <div style={{
                    display: 'flex',
                    alignItems: 'center',
                    width: '100%',
                    justifyContent: 'space-between',
                    color: 'var(--font-color)',
                  }}>
                    {/* Avatar */}
                    <div className={styles.collapse_icon}>
                      <Avatar 
                        shape="square" 
                        style={{borderRadius: "10px"}} 
                        size={40} 
                        icon={row.type === "expense" ? 
                          <ArrowUpOutlined style={{color: "var(--red-color)"}}/> : 
                          <ArrowDownOutlined style={{color: "var(--accent-color)"}}/>
                        } 
                      />
                    </div>
                    
                    <div className={styles.collapse_category}>
                      {row.category}
                    </div>

                    <div className={styles.collapse_right}>
                      <div className={styles.collapse_date_amount}>
                        <div className={styles.collapse_date}>{row.date}</div>
                        <div>{row.type === "expense" ? "" : "+"} {row.amount} ₽</div>
                      </div>
                      <div className={styles.collapse_actions}>
                      <IconButton size='small' onClick={(e) => (
                        e.stopPropagation(),
                        handleMenuClick(e, row)
                      )}>
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
                          <MenuItem key={setting} onClick={() => {
                            if (selectedItem) {
                              handleDelete(selectedItem.id, selectedItem.dataType);
                            }
                          }}>
                            <Typography sx={{ textAlign: 'center' }}>{setting}</Typography>
                          </MenuItem>
                              ))}
                        </Menu>
                    </div>
                    </div>
                    
                  </div>
                ),
                children: row.description.length > 0 ? (
                  <div className={styles.description_content}>
                    {row.description}
                  </div>
                ) : <div className={styles.no_description}>Комментария еще нет</div> 
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