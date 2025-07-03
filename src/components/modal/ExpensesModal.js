import styles from "@/app/page.module.css";
import { Divider } from "@mui/material";
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import { useState } from "react";
import StyledInput from "@/components/input/StyledInput";
import StyledButton from "@/components/button/StyledButton";
import 'dayjs/locale/en-gb'
import ClearIcon from '@mui/icons-material/Clear';
import Button from '@mui/material/Button';

export default function Transactions({closeModal}) {

  const expenseCategories = [
    {label: 'Продукты'}, 
    {label: 'Развлечения'},
    {label: 'Медицина'},
    {label: 'Подарки'},
    {label: 'Другое'}
  ]

   const [expenseCat, setExpenseCat ] = useState('');

    const handleExpenseChange = (event) => {
      setExpenseCat(event.target.value);
    };

  return (
      <div className={styles.card_transactions}>
         <div className={styles.card_header} style={{paddingBottom: "2%"}}>
          Добавить расход
          <Button onClick={closeModal} style={{position: "absolute", right: "10px", top: "10px"}}><ClearIcon style={{color: "var(--main-color)"}}/></Button>
        </div>
        <Divider style={{width: "100%", backgroundColor: "var(--main-color)", opacity: "0.5"}}/>
        <div className={styles.transactions_card_body}>
          <div className={styles.card_input}>
            <StyledInput style={{width: "100%"}} label="Сумма"/> ₽
          </div>
          <div className={styles.card_input}>
            <FormControl fullWidth>
              <InputLabel id="demo-simple-select-label" style={{color: "var(--main-color)"}}>Категория</InputLabel>
              <Select
                labelId="demo-simple-select-label"
                id="demo-simple-select"
                value={expenseCat}
                label="Category"
                onChange={handleExpenseChange}
                sx={{width: "100%", borderRadius: "30px", color: "var(--font-color)", textAlign: "left",
                  '& .MuiOutlinedInput-notchedOutline': {borderColor: "#303030"},
                  '&:hover .MuiOutlinedInput-notchedOutline': {borderColor: "var(--font-color)"},
                  '&.Mui-focused .MuiOutlinedInput-notchedOutline': {borderColor: "var(--font-color)"},
                  '& .MuiSvgIcon-root': {fill: "var(--main-color)"}
                }}
              >
                {expenseCategories.map((category) => (
                  <MenuItem value={category.label}>{category.label}</MenuItem>
                ))}
              </Select>
            </FormControl>
            <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="en-gb">
              <DatePicker sx={{
                '& .MuiPickersInputBase-root': {color: "var(--font-color)", borderRadius: "30px"},
                '& fieldset': {borderColor: "#303030"},
                '&:hover fieldset': {borderColor: "var(--font-color) !important"},
                '&.Mui-focused fieldset': {borderColor: "var(--font-color) !important"},
                '& .MuiSvgIcon-root': {fill: "var(--main-color)"}
                }
              } />
            </LocalizationProvider>
          </div>
          <div className={styles.card_input}>
            <StyledInput style={{width: "100%", borderRadius: "0"}} label="Комментарий"/>
          </div>
          <StyledButton style={{width: "100%"}}>Добавить</StyledButton>
        </div>     
      </div>
  );
}