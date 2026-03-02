import { styled } from '@mui/material/styles';
import TextField from '@mui/material/TextField';

const CssTextField = styled(TextField)(({ theme }) => ({
  '& label': {
    color: 'var(--main-color)', // Светлый цвет неактивного лейбла
  },
  '& label.Mui-focused': {
    color: '#A0AAB4', // Цвет активного лейбла
  },
  // Стили для disabled состояния лейбла
  '& .MuiInputLabel-root.Mui-disabled': {
    color: '#9E9E9E', // Серый цвет лейбла в disabled
  },
  '& input': {
    color: '#D0DCE0', // Светлый цвет вводимого текста
    fontWeight: 'regular',
  },
  // Стили для disabled состояния
  '& .MuiInputBase-root.Mui-disabled': {
    color: '#9E9E9E', // Серый цвет текста в disabled
  },
  '& .MuiInputBase-root.Mui-disabled input': {
    WebkitTextFillColor: '#9E9E9E', // Дополнительно для -webkit-
  },
  '& .MuiOutlinedInput-root.Mui-disabled': {
    '& fieldset': {
      borderColor: '#B2BAC2', // Серая граница в disabled
    },
  },
  '& .MuiOutlinedInput-root.Mui-disabled:hover fieldset': {
    borderColor: '#B2BAC2',
  },
  '& .MuiInput-underline:after': {
    borderBottomColor: '#B2BAC2', // Подчеркивание при фокусировке
  },
  '& .MuiOutlinedInput-root': {
    borderRadius: 30, // Скругление углов границы
    '& fieldset': {
      borderColor: '#303030', // Обычная граница
    },
    '&:hover fieldset': {
      borderColor: '#B2BAC2', // Граница при наведении мыши
    },
    '&.Mui-focused fieldset': {
      borderColor: '#6F7E8C', // Граница при активном состоянии
    },
  },
}));

export default function StyledInput(props) {
  return <CssTextField {...props} />;
}