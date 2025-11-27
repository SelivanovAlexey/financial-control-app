'use client';
import StyledButton from "@/components/button/StyledButton";
import styles from "../page.module.css";
import { LineChart } from '@mui/x-charts/LineChart';
import {PieChart, pieArcLabelClasses } from '@mui/x-charts/PieChart';
import { useEffect, useState } from "react";
import ToggleButton from '@mui/material/ToggleButton';
import ToggleButtonGroup, {
  toggleButtonGroupClasses,
} from '@mui/material/ToggleButtonGroup';
import { styled } from "@mui/material";
import {Paper} from "@mui/material";
import Modal from '@mui/material/Modal';
import IncomesModal from "@/components/modal/IncomesModal";
import ExpensesModal from "@/components/modal/ExpensesModal";
import { useDispatch, useSelector } from "../store";
import { fetchExpenses, fetchIncomes } from "@/reducers/userReducer";
import CircularProgress from '@mui/material/CircularProgress';

const margin = { right: 24, left: 24, bottom: 28 };

const StyledToggleButtonGroup = styled(ToggleButtonGroup)(({ theme }) => ({
  [`& .${toggleButtonGroupClasses.grouped}`]: {
    margin: theme.spacing(0.5),
    border: 0,
    borderRadius: theme.shape.borderRadius,
    [`&.${toggleButtonGroupClasses.disabled}`]: {
      border: 0,
    },
  },
  [`& .${toggleButtonGroupClasses.middleButton},& .${toggleButtonGroupClasses.lastButton}`]:
    {
      marginLeft: -1,
      borderLeft: '1px solid transparent',
    },
}));

// Функция для фильтрации данных по периоду
const filterDataByPeriod = (data, period) => {
  if (!data || !Array.isArray(data)) return [];
  
  const now = new Date();
  let startDate;
  
  switch (period) {
    case 'left': // Неделя
      startDate = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
      break;
    case 'center': // Месяц
      startDate = new Date(now.getFullYear(), now.getMonth(), 1);
      break;
    case 'right': // Год
      startDate = new Date(now.getFullYear(), 0, 1);
      break;
    default:
      return data;
  }
  
  return data.filter(item => {
    if (!item?.createDate) return false;
    // Преобразуем timestamp в Date объект
    const itemDate = new Date(item.createDate * 1000);
    return itemDate >= startDate;
  });
};

// Функция для группировки данных по категориям
const groupDataByCategory = (data) => {
  if (!data || !Array.isArray(data)) return [];
  
  const grouped = data.reduce((acc, item) => {
    const category = item.category || 'Другое';
    if (!acc[category]) {
      acc[category] = 0;
    }
    acc[category] += item.amount || 0;
    return acc;
  }, {});

  return Object.entries(grouped).map(([label, value]) => ({
    label,
    value,
    id: label
  }));
};

// Функция для получения данных по временным периодам
const getDataByTimePeriod = (data, period) => {
  if (!data || !Array.isArray(data)) return { data: Array(7).fill(0), labels: Array(7).fill('') };
  
  const now = new Date();
  let periods = [];
  let labels = [];
  
  switch (period) {
    case 'left': // Неделя (последние 7 дней)
      periods = Array(7).fill(0);
      labels = Array(7).fill('');
      
      for (let i = 6; i >= 0; i--) {
        const date = new Date(now.getTime() - i * 24 * 60 * 60 * 1000);
        labels[6-i] = date.toLocaleDateString('ru-RU', { day: 'numeric', month: 'short' });
        
        data.forEach(item => {
          if (item.createDate) {
            const itemDate = new Date(item.createDate * 1000);
            if (itemDate.toDateString() === date.toDateString()) {
              periods[6-i] += item.amount || 0;
            }
          }
        });
      }
      return { data: periods, labels };

    case 'center': // Месяц (последние 4 недели) - ИСПРАВЛЕНО
      periods = Array(4).fill(0);
      labels = Array(4).fill('');
      
      // Получаем начало текущей недели (понедельник)
      const startOfWeek = (date) => {
        const d = new Date(date);
        const day = d.getDay();
        const diff = d.getDate() - day + (day === 0 ? -6 : 1); // adjust when day is sunday
        return new Date(d.setDate(diff));
      };
      
      for (let i = 3; i >= 0; i--) {
        const weekStart = new Date(now.getTime() - i * 7 * 24 * 60 * 60 * 1000);
        const weekStartAdjusted = startOfWeek(weekStart);
        const weekEnd = new Date(weekStartAdjusted.getTime() + 6 * 24 * 60 * 60 * 1000);
        
        // Форматируем метку для отображения
        labels[3-i] = `${weekStartAdjusted.getDate()}-${weekEnd.getDate()} ${weekStartAdjusted.toLocaleDateString('ru-RU', { month: 'short' })}`;
        
        data.forEach(item => {
          if (item.createDate) {
            const itemDate = new Date(item.createDate * 1000);
            if (itemDate >= weekStartAdjusted && itemDate <= weekEnd) {
              periods[3-i] += item.amount || 0;
            }
          }
        });
      }
      return { data: periods, labels };

    case 'right': // Год (последние 12 месяцев)
      periods = Array(12).fill(0);
      labels = Array(12).fill('');
      
      for (let i = 11; i >= 0; i--) {
        const month = new Date(now.getFullYear(), now.getMonth() - i, 1);
        labels[11-i] = month.toLocaleDateString('ru-RU', { month: 'short' });
        
        data.forEach(item => {
          if (item.createDate) {
            const itemDate = new Date(item.createDate * 1000);
            if (itemDate.getMonth() === month.getMonth() && 
                itemDate.getFullYear() === month.getFullYear()) {
              periods[11-i] += item.amount || 0;
            }
          }
        });
      }
      return { data: periods, labels };

    default:
      return { data: Array(7).fill(0), labels: Array(7).fill('') };
  }
};

// Функция для расчета общей суммы
const calculateTotalAmount = (data) => {
  if (!data || !Array.isArray(data)) return 0;
  return data.reduce((total, item) => total + (item.amount || 0), 0);
};

export default function Home() {
  const dispatch = useDispatch();
  const { expenses, incomes, isLoading, userError } = useSelector(state => state.user);
  
  const [period, setPeriod] = useState('center'); // по умолчанию "Месяц"
  const [incomesModalOpen, setIncomesModalOpen] = useState(false);
  const [expensesModalOpen, setExpensesModalOpen] = useState(false);
  
  const handleIncomeModalOpen = () => setIncomesModalOpen(true);
  const handleIncomeModalClose = () => setIncomesModalOpen(false);
  const handleExpensesModalOpen = () => setExpensesModalOpen(true);
  const handleExpensesModalClose = () => setExpensesModalOpen(false);

  const handlePeriodChange = (event, newPeriod) => {
    if (newPeriod !== null) {
      setPeriod(newPeriod);
    }
  };

  // Загружаем расходы при монтировании компонента
  useEffect(() => {
    dispatch(fetchExpenses());
    dispatch(fetchIncomes());
  }, [dispatch]);

  // Фильтруем данные по выбранному периоду
  const filteredExpenses = filterDataByPeriod(expenses, period);
  const filteredIncomes = filterDataByPeriod(incomes, period);
  
  // Рассчитываем данные для графиков
  const expensesAll = calculateTotalAmount(filteredExpenses);
  const incomesAll = calculateTotalAmount(filteredIncomes);
  
  const expensesByCategory = groupDataByCategory(filteredExpenses);
  const incomesByCategory = groupDataByCategory(filteredIncomes);
  
  const expensesChartData = getDataByTimePeriod(filteredExpenses, period);
  const incomesChartData = getDataByTimePeriod(filteredIncomes, period);

  // Показываем загрузку или ошибку
  if (isLoading) {
    return <div style={{margin: 0, display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh'}}>
      <CircularProgress size={100}/>
    </div>;
  }

  if (userError) {
    return <div className={styles.container}>Ошибка: {userError}</div>;
  }

  return (
    <div className={styles.container}>
      <div className={styles.card_graph}>
        <div style={{display: "flex", justifyContent: "space-between", width: "100%", padding: "1.5rem 1.5rem 0 1.5rem", fontSize: "calc(40px + 2vmin)", fontWeight: "bold"}}>
          {expensesAll.toLocaleString('ru-RU')} ₽
          <div width="30%">
            <Paper
              elevation={0}
              sx={{
                display: 'flex',
                backgroundColor: "transparent",
              }}
            >
              <StyledToggleButtonGroup
                value={period}
                exclusive
                onChange={handlePeriodChange}
                aria-label="text alignment"
                sx={{
                  display: 'grid',
                  gridTemplateColumns: '1fr 1fr 1fr',
                }}
              >
                <ToggleButton value="left" aria-label="left aligned" sx={{ color: "var(--main-color)", '&.Mui-selected': { color: "var(--font-color)", backgroundColor: "var(--accent-color)", transition: 'all 0.3s ease-in-out', '&:hover': { backgroundColor: "var(--accent-color)"}}}}>
                  Неделя
                </ToggleButton>
                <ToggleButton value="center" aria-label="centered" sx={{ color: "var(--main-color)", '&.Mui-selected': { color: "var(--font-color)", backgroundColor: "var(--accent-color)", transition: 'all 0.3s ease-in-out', '&:hover': { backgroundColor: "var(--accent-color)"}}}}>
                  Месяц
                </ToggleButton>
                <ToggleButton value="right" aria-label="right aligned" sx={{ color: "var(--main-color)", '&.Mui-selected': { color: "var(--font-color)", backgroundColor: "var(--accent-color)", transition: 'all 0.3s ease-in-out', '&:hover': { backgroundColor: "var(--accent-color)"}}}}>
                  Год
                </ToggleButton>
              </StyledToggleButtonGroup>
            </Paper>
          </div>
        </div>
        <div style={{display: "flex", justifyContent: "start", width: "100%", padding: "0 0 0 1.5rem", fontSize: "calc(8px + 2vmin)"}}>
          Траты
        </div>
        <PieChart
          slotProps={{
            legend: {
              sx: {
                width: "100%",
                display: "flex",
                justifyContent: "center",
                color: "var(--font-color)",
                fontSize: "14px",
                padding: "2rem"      
              },
              direction: 'horizontal',
              position: { 
                vertical: 'bottom',
                horizontal: 'center'
              }
            }
          }}
          sx={{
            "& .css-yqw5vn-MuiPieArc-root":{
              strokeWidth: 0
            },
            [`& .${pieArcLabelClasses.root}`]: {
              fontWeight: 'bold',
              fill: 'var(--font-color)',
              fontSize: '18px'
            },
            width: "100%",
          }}
          series={[
            {
              data: expensesByCategory,
              startAngle: -270,
              endAngle: 90,
              innerRadius: "80%",
              outerRadius: "100%",
              paddingAngle: 5,
              cornerRadius: 5,
              highlightScope: { fade: 'global', highlight: 'item' },
              arcLabelRadius: "60%",
              arcLabel: (item) => expensesAll > 0 ? `${(item.value / expensesAll * 100).toFixed(2)} %` : '0%',
            }
          ]}
        />
      </div>
      
      <div className={styles.card}>
        <div className={styles.card_header}>
          Доходы
          <div style={{display: 'flex', alignItems: 'center', gap: '1rem'}}>
            <div style={{fontSize: 'calc(16px + 1vmin)', fontWeight: 'bold'}}>
              {incomesAll.toLocaleString('ru-RU')} ₽
            </div>
            <StyledButton onClick={handleIncomeModalOpen}>Добавить</StyledButton>
          </div>
          <Modal
            open={incomesModalOpen}
            onClose={handleIncomeModalClose}
            aria-labelledby="modal-modal-title"
            aria-describedby="modal-modal-description"
          >
            <div className={styles.modal}>
              <IncomesModal closeModal={handleIncomeModalClose}/>
            </div>
          </Modal>
        </div>
        <div className={styles.card_body}>
          <LineChart
            height={250}
            series={[
              { data: incomesChartData.data, color: `var(--accent-color)` },
            ]}
            xAxis={[
              {
                scaleType: 'point',
                data: incomesChartData.labels,
              }
            ]}
            yAxis={[
              {
                width: 50,
              }
            ]}
            margin={margin}
            grid={{ vertical: true, horizontal: true }}
            sx={{
                "& .MuiChartsAxis-left .MuiChartsAxis-tickLabel":{
                  fill:"#fff",
                  opacity:0.7
                },
                "& .MuiChartsAxis-bottom .MuiChartsAxis-tickLabel":{
                    fill:"#fff",
                    opacity:0.7
                },
                "& .MuiChartsAxis-bottom .MuiChartsAxis-line":{
                  stroke:"#fff",
                  opacity:0.7
                },
                "& .MuiChartsAxis-left .MuiChartsAxis-line":{
                  stroke:"#fff",
                  opacity:0.7
                }
              }}
          />
        </div>
      </div>
      
      <div className={styles.card}>
        <div className={styles.card_header}>
          Расходы
          <div style={{display: 'flex', alignItems: 'center', gap: '1rem'}}>
            <div style={{fontSize: 'calc(16px + 1vmin)', fontWeight: 'bold'}}>
              {expensesAll.toLocaleString('ru-RU')} ₽
            </div>
            <StyledButton onClick={handleExpensesModalOpen}>Добавить</StyledButton>
          </div>
          <Modal
            open={expensesModalOpen}
            onClose={handleExpensesModalClose}
            aria-labelledby="modal-modal-title"
            aria-describedby="modal-modal-description"
          >
            <div className={styles.modal}>
              <ExpensesModal closeModal={handleExpensesModalClose}/>
            </div>
          </Modal>
        </div>
        <div className={styles.card_body}>
          <LineChart
            height={250}
            series={[
              { data: expensesChartData.data, color: `var(--red-color)` },
            ]}
            xAxis={[
              {
                scaleType: 'point',
                data: expensesChartData.labels,
              }
            ]}
            yAxis={[
              {
                width: 50,
              }
            ]}
            margin={margin}
            grid={{ vertical: true, horizontal: true }}
            sx={{
                "& .MuiChartsAxis-left .MuiChartsAxis-tickLabel":{
                  fill:"#fff",
                  opacity:0.7
                },
                "& .MuiChartsAxis-bottom .MuiChartsAxis-tickLabel":{
                    fill:"#fff",
                    opacity:0.7
                },
                "& .MuiChartsAxis-bottom .MuiChartsAxis-line":{
                  stroke:"#fff",
                  opacity:0.7
                },
                "& .MuiChartsAxis-left .MuiChartsAxis-line":{
                  stroke:"#fff",
                  opacity:0.7
                }
              }}
          />
        </div>
      </div>
    </div>
  );
}