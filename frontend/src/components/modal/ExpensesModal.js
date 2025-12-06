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
import { useDispatch } from "../../app/store";
import { fetchCreateExpense, fetchExpenses } from "../../reducers/userReducer";
import dayjs from 'dayjs';
import timezone from 'dayjs/plugin/timezone';
import utc from 'dayjs/plugin/utc';

dayjs.extend(utc);
dayjs.extend(timezone);

export default function Transactions({closeModal}) {
   const dispatch = useDispatch();

  const expenseCategories = [
    { label: 'Продукты' }, 
    { label: 'Развлечения' },
    { label: 'Медицина' },
    { label: 'Подарки' },
    { label: 'Другое' }
  ];

  const [formData, setFormData] = useState({
    amount: '',
    category: '',
    createDate: dayjs(),
    description: ''
  });

  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleInputChange = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleExpenseChange = (event) => {
    handleInputChange('category', event.target.value);
  };

  const handleDateChange = (date) => {
    if (date) {
      handleInputChange('createDate', date);
    }
  };

  const handleAmountChange = (event) => {
    const value = event.target.value;
    if (value === '' || /^\d*\.?\d*$/.test(value)) {
      handleInputChange('amount', value);
    }
  };

  const handleDescriptionChange = (event) => {
    handleInputChange('description', event.target.value);
  };

  const formatDateForBackend = (dayjsDate) => {
    if (!dayjsDate || !dayjsDate.isValid()) {
      dayjsDate = dayjs();
    }
    
    const timezoneOffset = dayjsDate.format('Z');
  
    const dateString = dayjsDate.format('YYYY-MM-DDTHH:mm:ss');
    
    return `${dateString}${timezoneOffset}`;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    
    if (!formData.amount || !formData.category) {
      alert('Пожалуйста, заполните обязательные поля: Сумма и Категория');
      return;
    }

    const amountValue = parseFloat(formData.amount);
    if (amountValue <= 0 || isNaN(amountValue)) {
      alert('Сумма должна быть числом больше 0');
      return;
    }

    setIsSubmitting(true);

    try {
      const createDateFormatted = formatDateForBackend(formData.createDate);

      const expenseData = {
        amount: amountValue,
        category: formData.category,
        createDate: createDateFormatted, // Формат: 2025-12-06T19:29:20+03:00
        description: formData.description || ''
      };

      await dispatch(fetchCreateExpense(expenseData)).unwrap();
      await dispatch(fetchExpenses()).unwrap();
      
      closeModal();
      
    } catch (error) {
      console.error('Error creating expense:', error);
      alert(`Ошибка при создании расхода: ${error.message}`);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
      <div className={styles.card_transactions}>
         <div className={styles.card_header} style={{paddingBottom: "2%"}}>
          Добавить расход
          <Button onClick={closeModal} style={{position: "absolute", right: "10px", top: "10px"}}>
            <ClearIcon style={{color: "var(--main-color)"}} disabled={isSubmitting}/>
          </Button>
        </div>
        <Divider style={{width: "100%", backgroundColor: "var(--main-color)", opacity: "0.5"}}/>
        <form onSubmit={handleSubmit} style={{width: "100%"}}>
          <div className={styles.transactions_card_body}>

            <div className={styles.card_input}>
              <StyledInput 
                style={{width: "100%"}} 
                label="Сумма" 
                value={formData.amount} 
                onChange={handleAmountChange} 
                required 
                type="text" 
                inputMode="decimal" 
                placeholder="0"
              /> ₽
            </div>

            <div className={styles.card_input}>
              <FormControl fullWidth>
                <InputLabel id="expense-category-label" style={{color: "var(--main-color)"}}>
                  Категория
                </InputLabel>
                <Select
                  labelId="expense-category-label"
                  id="expense-category-select"
                  value={formData.category}
                  label="Category"
                  onChange={handleExpenseChange}
                  required
                  sx={{
                    width: "100%", 
                    borderRadius: "30px", 
                    color: "var(--font-color)", 
                    textAlign: "left",
                    '& .MuiOutlinedInput-notchedOutline': {borderColor: "#303030"},
                    '&:hover .MuiOutlinedInput-notchedOutline': {borderColor: "var(--font-color)"},
                    '&.Mui-focused .MuiOutlinedInput-notchedOutline': {borderColor: "var(--font-color)"},
                    '& .MuiSvgIcon-root': {fill: "var(--main-color)"}
                  }}
                >
                  {expenseCategories.map((category) => (
                    <MenuItem key={category.label} value={category.label}>
                      {category.label}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>

              <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="en-gb">
                <DatePicker 
                  sx={{
                    '& .MuiPickersInputBase-root': {color: "var(--font-color)", borderRadius: "30px"},
                    '& fieldset': {borderColor: "#303030"},
                    '&:hover fieldset': {borderColor: "var(--font-color) !important"},
                    '&.Mui-focused fieldset': {borderColor: "var(--font-color) !important"},
                    '& .MuiSvgIcon-root': {fill: "var(--main-color)"}
                  }}
                  value={formData.createDate}
                  onChange={handleDateChange}
                  format="DD.MM.YYYY"
                  slotProps={{
                    textField: {
                      fullWidth: true,
                    }
                  }}
                />
              </LocalizationProvider>
              
            </div>

            <div className={styles.card_input}>
              <StyledInput 
                style={{width: "100%", borderRadius: "0"}} 
                label="Комментарий" 
                value={formData.description} 
                onChange={handleDescriptionChange}
              />
            </div>
            
            <StyledButton 
              style={{width: "100%"}} 
              disabled={isSubmitting} 
              type="submit"
            >
              {isSubmitting ? 'Добавление...' : 'Добавить'}
            </StyledButton>
            
        </div> 
        </form>    
      </div>
  );
}