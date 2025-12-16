'use client';
import StyledButton from "@/components/button/StyledButton";
import styles from "../page.module.css";
import { LineChart } from '@mui/x-charts/LineChart';
import {PieChart, pieArcLabelClasses } from '@mui/x-charts/PieChart';
import { useEffect, useState } from "react";
import ToggleButton from '@mui/material/ToggleButton';
import { StyledToggleButtonGroup } from "@/components/toggleButtonGroup/ToggleButtonGroup";
import {Paper} from "@mui/material";
import Modal from '@mui/material/Modal';
import IncomesModal from "@/components/modal/IncomesModal";
import ExpensesModal from "@/components/modal/ExpensesModal";
import { useDispatch, useSelector } from "../store";
import { fetchExpenses, fetchIncomes } from "@/reducers/userReducer";
import CircularProgress from '@mui/material/CircularProgress';
import { useMediaQuery } from '@mui/material';
import { useTheme } from '@mui/material/styles';
import { useRef } from "react";
import { parseDateToTimestamp,  filterDataByPeriod, groupDataByCategory, getDataByTimePeriod, calculateTotalAmount} from "@/utils/Utils";

const margin = { right: 24, left: 24, bottom: 28 };

export default function Home() {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('768'));

  const dataLoadedRef = useRef(false);
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

  useEffect(() => {
    // Загружаем данные только один раз
    if (!dataLoadedRef.current) {
      dispatch(fetchExpenses());
      dispatch(fetchIncomes());
      dataLoadedRef.current = true;
    }
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
        <div className={styles.card_graph_header}>
          {isMobile ? (
            <div className={styles.card_graph_header_title}>
              <div style={{display: "flex", flexDirection: "column", justifyContent: "center", alignItems: "start"}}>
                {expensesAll.toLocaleString('ru-RU')} ₽
                <div style={{width: "auto", fontSize: "calc(12px + 2vmin)", fontWeight: "normal"}}>
                Траты</div>
              </div>
            </div>
          ) : (
            <div style={{display: "flex", flexDirection: "column", justifyContent: "center", alignItems: "start"}}>
              {expensesAll.toLocaleString('ru-RU')} ₽
              <div style={{width: "auto", fontSize: "calc(8px + 2vmin)", fontWeight: "normal"}}>
              Траты</div>
            </div>
          )}
          <div style={isMobile ? {width: "100%"} : {}}>
            <Paper
              elevation={0}
              sx={isMobile ? {
                backgroundColor: "transparent",
                justifyContent: 'end',
              } :
              {
                backgroundColor: "transparent",
              }}
            >
              <StyledToggleButtonGroup
                value={period}
                exclusive
                onChange={handlePeriodChange}
                aria-label="text alignment"
                sx={{display: 'grid', gridTemplateColumns: '1fr 1fr 1fr'}}>
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
        <div className={styles.chart_container}>
          {expensesByCategory.length > 0 ? (
            <PieChart
              slotProps={{
                legend: {
                  sx: {
                    width: "100%",
                    display: "flex",
                    justifyContent: "center",
                    color: "var(--font-color)",
                    fontSize: isMobile ? "12px" : "14px",
                    padding: isMobile ? "0.5rem" : "2rem",
                  },
                  direction: 'horizontal',
                  position: { 
                    vertical: 'bottom',
                    horizontal: 'center'
                  }
                }
              }}
              sx={{
                width: "100%",
                height: "100%",
                [`& .${pieArcLabelClasses.root}`]: {
                  fontWeight: 'bold',
                  fill: 'var(--font-color)',
                  fontSize: isMobile ? '14px' : '18px'
                },
                '& .MuiPieArc-root, & .MuiPieArc-highlighted': {
                  stroke: 'transparent',
                  strokeWidth: 0,
                }
              }}
              series={[
                {
                  data: expensesByCategory,
                  startAngle: -270,
                  endAngle: 90,
                  innerRadius: isMobile ? "75%" : "80%",
                  outerRadius: isMobile ? "100%" : "100%",
                  paddingAngle: 5,
                  cornerRadius: 5,
                  highlightScope: { fade: 'global', highlight: 'item' },
                  arcLabelRadius: isMobile ? "50%" : "60%",
                  arcLabel: (item) => expensesAll > 0 ? `${(item.value / expensesAll * 100).toFixed(2)} %` : '0%',
                  
                }
              ]}
            />
          ) : (
            <div style={{ 
              display: 'flex', 
              justifyContent: 'center', 
              alignItems: 'center', 
              height: '100%',
              color: 'var(--font-color)',
              fontSize: 'calc(10px + 1vmin)'
            }}>
              Нет данных о расходах
            </div>
          )}
        </div>
      </div>
      
      <div className={styles.card}>
        <div className={styles.card_header}>
          Доходы
          <div style={{display: 'flex', alignItems: 'center', gap: '1rem'}}>
            {isMobile ?  
              <div style={{fontSize: 'calc(16px + 2vmin)', fontWeight: 'bold'}}>
                {incomesAll.toLocaleString('ru-RU')} ₽
              </div>
            :
              <div style={{fontSize: 'calc(16px + 1vmin)', fontWeight: 'bold'}}>
                {incomesAll.toLocaleString('ru-RU')} ₽
              </div>
            }
            {!isMobile && (
              <StyledButton onClick={handleIncomeModalOpen}>Добавить</StyledButton>
            )}
          </div>
          <Modal
            open={incomesModalOpen}
            onClose={handleIncomeModalClose}
            aria-labelledby="modal-modal-title"
            aria-describedby="modal-modal-description"
          >
            <div className={styles.modal}>
              <IncomesModal isMobile={isMobile} closeModal={handleIncomeModalClose}/>
            </div>
          </Modal>
        </div>
        <div className={styles.card_body}>
          {incomesChartData.data.some((item) => item !== 0) ? (
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
                  valueFormatter: (value) => {
                    if (Math.abs(value) >= 1000000) {
                      const num = Math.abs(value) / 1000000;
                      const sign = value < 0 ? "-" : "";
                      const formattedNum = num % 1 === 0 ? num.toString() : num.toFixed(1);
                      return `${sign}${formattedNum.replace('.0', '')}M`;
                    } else if (Math.abs(value) >= 1000) {
                      const num = Math.abs(value) / 1000;
                      const sign = value < 0 ? "-" : "";
                      const formattedNum = num % 1 === 0 ? num.toString() : num.toFixed(1);
                      return `${sign}${formattedNum.replace('.0', '')}K`;
                    }
                    return value.toString();
                  }
                }
              ]}
              margin={margin}
              grid={{ vertical: true, horizontal: true }}
              sx={{
                "& .MuiMarkElement-root": {
                  r: 4
                },
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
                },
                width: "100%",
                height: "100%",
              }}
            />
          ) : (
            <div className={styles.card_body} style={{
              color: 'var(--font-color)',
              fontSize: 'calc(10px + 1vmin)'
            }}>
              Нет данных о доходах
            </div>
          )}
          {isMobile && (
            <div className={styles.card_footer}>
              <StyledButton style={{width: '100%'}} onClick={handleIncomeModalOpen}>Добавить</StyledButton>
            </div>
          )}
        </div>
      </div>
      
      <div className={styles.card} style={{marginBottom: "2rem"}}>
        <div className={styles.card_header}>
          Расходы
          <div style={{display: 'flex', alignItems: 'center', gap: '1rem'}}>
            {isMobile ?  
              <div style={{fontSize: 'calc(16px + 2vmin)', fontWeight: 'bold'}}>
                {expensesAll.toLocaleString('ru-RU')} ₽
              </div>
            :
              <div style={{fontSize: 'calc(16px + 1vmin)', fontWeight: 'bold'}}>
                {expensesAll.toLocaleString('ru-RU')} ₽
              </div>
            }
            {!isMobile && (
              <StyledButton onClick={handleExpensesModalOpen}>Добавить</StyledButton>
            )}
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
          {expensesChartData.data.some((item) => item !== 0) ? (
            <LineChart
              height={250}
              series={[
                { 
                  data: expensesChartData.data,
                  color: `var(--red-color)`,
                },
              ]}
              xAxis={[
                {
                  scaleType: 'point',
                  data: expensesChartData.labels,
                }
              ]}
              yAxis={[
                {
                  valueFormatter: (value) => {
                    if (Math.abs(value) >= 1000000) {
                      const num = Math.abs(value) / 1000000;
                      const sign = value < 0 ? "-" : "";
                      const formattedNum = num % 1 === 0 ? num.toString() : num.toFixed(1);
                      return `${sign}${formattedNum.replace('.0', '')}M`;
                    } else if (Math.abs(value) >= 1000) {
                      const num = Math.abs(value) / 1000;
                      const sign = value < 0 ? "-" : "";
                      const formattedNum = num % 1 === 0 ? num.toString() : num.toFixed(1);
                      return `${sign}${formattedNum.replace('.0', '')}K`;
                    }
                    return value.toString();
                  }
                }
              ]}
              margin={margin}
              grid={{ vertical: true, horizontal: true }}
              sx={{
                "& .MuiMarkElement-root": {
                  r: 4
                },
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
                },
                width: "100%",
                height: "100%",
              }}
              slotProps={{
                axis: {
                  left: {
                    disableTicks: true,
                    tickLabelStyle: {
                      fontSize: 12,
                    },
                  },
                },
              }}
            />
          ) : (
            <div className={styles.card_body} style={{
              color: 'var(--font-color)',
              fontSize: 'calc(10px + 1vmin)'
            }}>
              Нет данных о расходах
            </div>
          )}
          {isMobile && (
            <div className={styles.card_footer}>
              <StyledButton style={{width: '100%'}} onClick={handleExpensesModalOpen}>Добавить</StyledButton>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}