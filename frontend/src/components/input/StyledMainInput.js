import { styled } from '@mui/material/styles';
import TextField from '@mui/material/TextField';

const CssTextField = styled(TextField)(({ theme }) => ({
  '& input': {
    color: '#fff', // Светлый цвет вводимого текста
    fontWeight: '100',
    marginLeft: 10
  },
  '& input::placeholder': {
    color: '#fff',
    opacity: 0.6,
    fontWeight: '100' // Общий селектор для всех современных браузеров
  },
  '& .MuiInput-underline:after': {
    borderBottomColor: '#B2BAC2', // Подчеркивание при фокусировке
  },
  '& .MuiOutlinedInput-root': {
    borderRadius: 30,
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

export default function StyledMainInput(props) {
  return <CssTextField {...props} />;
}