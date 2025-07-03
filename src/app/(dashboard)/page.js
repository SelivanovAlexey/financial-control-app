'use client';
import StyledButton from "@/components/button/StyledButton";
import styles from "../page.module.css";
import { LineChart } from '@mui/x-charts/LineChart';
import {PieChart, pieArcLabelClasses } from '@mui/x-charts/PieChart';
import { useState } from "react";
import ToggleButton from '@mui/material/ToggleButton';
import ToggleButtonGroup, {
  toggleButtonGroupClasses,
} from '@mui/material/ToggleButtonGroup';
import { styled } from "@mui/material";
import {Paper} from "@mui/material";
import Modal from '@mui/material/Modal';
import IncomesModal from "@/components/modal/IncomesModal";
import ExpensesModal from "@/components/modal/ExpensesModal";


const margin = { right: 24, left: 24, bottom: 28 };

const incomes = [40000, 30000, 20000, 27800, 18900, 23900, 34900];
const expenses = [45000, 32000, 48000, 24800, 10000, 15000, 31200];

const expensesInMonth = [{
  label: 'Продукты',
  value: 10000,
}, {
  label: 'Развлечения',
  value: 5000,
}, {
  label: 'Медицина',
  value: 3000,
}, {
  label: 'Подарки',
  value: 7000,
},
{
  label: 'Другое',
  value: 2000,
},
]

const expensesAll = expensesInMonth.reduce((acc, item) => acc + item.value, 0);

const date = [
  'Январь',
  'Ферваль',
  'Март',
  'Апрель',
  'Май',
  'Июнь',
  'Июль',
];

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

export default function Home() {
  const [interval, setInterval] = useState('left');
  const [incomesModalOpen, setIncomesModalOpen] = useState(false);
  const [expensesModalOpen, setExpensesModalOpen] = useState(false);
  const handleIncomeModalOpen = () => setIncomesModalOpen(true);
  const handleIncomeModalClose = () => setIncomesModalOpen(false);
  const handleExpensesModalOpen = () => setExpensesModalOpen(true);
  const handleExpensesModalClose = () => setExpensesModalOpen(false);

  const handleChange = (event) => {
    setInterval(event.target.value);
  };

  return (
    <div className={styles.container}>
      <div className={styles.card_graph}>
        <div style={{display: "flex", justifyContent: "space-between", width: "100%", padding: "1.5rem 1.5rem 0 1.5rem", fontSize: "calc(40px + 2vmin)", fontWeight: "bold"}}>
          {expensesAll} ₽
          <div width="30%">
            <Paper
              elevation={0}
              sx={{
                display: 'flex',
                backgroundColor: "transparent",
              }}
            >
              <StyledToggleButtonGroup
                value={interval}
                exclusive
                onChange={handleChange}
                aria-label="text alignment"
                sx={{display: 'grid',
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
              data: expensesInMonth,
              innerRadius: "80%",
              outerRadius: "100%",
              // startAngle: -90,
              // endAngle: 90,
              paddingAngle: 5,
              cornerRadius: 5,
              highlightScope: { fade: 'global', highlight: 'item' },
              arcLabelRadius: "60%",
              arcLabel: (item) => `${(item.value / expensesAll * 100).toFixed(2)} %`,
            }
          ]}
        />
      </div>
      <div className={styles.card}>
        <div className={styles.card_header}>
          Доходы
          <StyledButton onClick={handleIncomeModalOpen}>Добавить</StyledButton>
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
              { data: incomes, color: `var(--accent-color)` },
            ]}
            xAxis={[
              {
                scaleType: 'point',
                data: date,
                
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
                //change left yAxis label styles
              "& .MuiChartsAxis-left .MuiChartsAxis-tickLabel":{
                fill:"#fff",
                opacity:0.7
              },
                // change bottom label styles
                "& .MuiChartsAxis-bottom .MuiChartsAxis-tickLabel":{
                    fill:"#fff",
                    opacity:0.7
                },
                  // bottomAxis Line Styles
                "& .MuiChartsAxis-bottom .MuiChartsAxis-line":{
                  stroke:"#fff",
                  opacity:0.7
                },
                // leftAxis Line Styles
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
          <StyledButton onClick={handleExpensesModalOpen}>Добавить</StyledButton>
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
              { data: expenses, color: `var(--red-color)` },
            ]}
            xAxis={[
              {
                scaleType: 'point',
                data: date,
                
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
                //change left yAxis label styles
              "& .MuiChartsAxis-left .MuiChartsAxis-tickLabel":{
                fill:"#fff",
                opacity:0.7
              },
                // change bottom label styles
                "& .MuiChartsAxis-bottom .MuiChartsAxis-tickLabel":{
                    fill:"#fff",
                    opacity:0.7
                },
                  // bottomAxis Line Styles
                "& .MuiChartsAxis-bottom .MuiChartsAxis-line":{
                  stroke:"#fff",
                  opacity:0.7
                },
                // leftAxis Line Styles
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
