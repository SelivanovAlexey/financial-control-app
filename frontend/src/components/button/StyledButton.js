import { Button } from "@mui/material";
import { styled } from '@mui/material/styles';

const ColorButton = styled(Button)(() => ({
  color: "var(--accent-color)", // Изначально текст зеленый
  border: `1px solid var(--accent-color)`, // Зеленая граница вокруг кнопки
  transition: 'all 0.3s ease-in-out', // Плавная анимация перехода состояний
  fontWeight: 'bold',
  borderRadius: 30,
  height: 50,
  '&:hover': {
    backgroundColor: "var(--accent-color)", // Фон становится темно-зеленым при наведении
    color: '#ffffff', // Текст становится белым при наведении
  },
}));

export default function StyledButton(props) {
  return <ColorButton {...props} />;
}