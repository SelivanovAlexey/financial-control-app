import { styled } from '@mui/material/styles';
import TextField from '@mui/material/TextField';

const CssTextField = styled(TextField)(({ theme }) => ({
  '& label': {
    color: 'var(--main-color)', // Светлый цвет неактивного лейбла
  },
  '& label.Mui-focused': {
    color: '#A0AAB4', // Цвет активного лейбла
  },
  '& input': {
    color: '#D0DCE0', // Светлый цвет вводимого текста
    fontWeight: 'bold'
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