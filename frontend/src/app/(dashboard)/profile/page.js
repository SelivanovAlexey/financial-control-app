"use client";
import styles from "@/app/page.module.css";
import { useTheme } from '@mui/material/styles';
import { useMediaQuery } from '@mui/material';
import { useDispatch, useSelector } from "../../store";
import { Avatar, Input } from 'antd';
import { UserOutlined } from '@ant-design/icons';
import StyledButton from "@/components/button/StyledButton";
import StyledInput from "@/components/input/StyledInput";

export default function Profile() {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('768'));

  const dispatch = useDispatch();
  const { isLoading, userError, user } = useSelector(state => state.user);

  if (isLoading) {
    return (
      <div className={styles.table_container}>
        <div className={styles.table_header}>Личный кабинет</div>
        <div style={{ color: "var(--font-color)", textAlign: "center", padding: "2rem" }}>
          Загрузка...
        </div>
      </div>
    );
  }

  if (userError) {
    return (
      <div className={styles.table_container}>
        <div className={styles.table_header}>Личный кабинет</div>
        <div style={{ color: "var(--red-color)", textAlign: "center", padding: "2rem" }}>
          Ошибка: {userError}
        </div>
      </div>
    );
  }

  return (
    isMobile ? (
      <div className={styles.history_container}>
        <div className={styles.user_container}>
          <div className={styles.user_avatar_container}>
            <div className={styles.user_avatar}>
              <Avatar shape="circle" style={{backgroundColor: "var(--main-color)"}} size={150} icon={<UserOutlined /> }/>
            </div>
            <div className={styles.user_change_avatar}>
              Изменить аватар
            </div>
          </div>

          <div className={styles.user_info}>
            <div className={styles.user_input}>
              <StyledInput label="Имя пользователя" style={{height: "100%", width: "100%", backgroundColor: "transparent", color: "var(--main-color)"}}/>
            </div>
            <div className={styles.user_input}>
              <StyledInput label="Email" style={{height: "100%", width: "100%", backgroundColor: "transparent", color: "var(--main-color)"}}/>
            </div>
          </div>

          <div className={styles.user_button}>
            <StyledButton type="submit" disabled style={{width: "100%"}}>Сохранить</StyledButton>
          </div>
        </div>
        
      </div>
    ) : (
      <div className={styles.history_container}>
        <div className={styles.user_container}>
          <div className={styles.user_avatar_container}>
            <div className={styles.user_avatar}>
              <Avatar shape="circle" style={{backgroundColor: "var(--main-color)"}} size={150} icon={<UserOutlined /> }/>
            </div>
            <div className={styles.user_change_avatar}>
              Изменить аватар
            </div>
          </div>

          <div className={styles.user_info}>
            <div className={styles.user_input}>
              <StyledInput label="Имя пользователя" style={{height: "100%", width: "100%", backgroundColor: "transparent", color: "var(--main-color)"}}/>
            </div>
            <div className={styles.user_input}>
              <StyledInput label="Email" style={{height: "100%", width: "100%", backgroundColor: "transparent", color: "var(--main-color)"}}/>
            </div>
          </div>

          <div className={styles.user_button}>
            <StyledButton type="submit" disabled style={{width: "100%"}}>Сохранить</StyledButton>
          </div>
        </div>
        
      </div>
    )
  );
}