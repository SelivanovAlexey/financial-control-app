"use client";
import { Card, CardContent, Typography, Divider } from "@mui/material";
import styles from "../page.module.css"
import StyledInput from "@/components/input/StyledInput";
import StyledButton from "@/components/button/StyledButton";
import FormControlLabel from "@mui/material/FormControlLabel";
import Checkbox from "@mui/material/Checkbox";
import Link from "next/link";
import { useEffect, useState } from "react";
import CircularProgress from '@mui/material/CircularProgress';
import { useRouter } from 'next/navigation';
import { useDispatch, useSelector } from "@/app/store";
import { userLogin } from "@/reducers/userReducer";

export default function Login() {
    const router = useRouter();
    const [errors, setErrors] = useState({});
    const [values, setValues] = useState({ email: '', password: '' });
    const dispatch = useDispatch();
    const isAuthenticated = useSelector(state => state.user.isAuthenticated);
    const isLoading = useSelector(state => state.user.isLoading);
    const [checked, setChecked] = useState(true);
    
    const handleChange = e => {
        const { name, value } = e.target;
        setValues(prev => ({...prev, [name]: value}));

        if (name === 'email') {
            if (!value) {
                setErrors(prev => ({...prev, email: ''}));
            }
        }

        if (name === 'password') {
            if (!value) {
                setErrors(prev => ({...prev, password: ''}));
            }
        }

    };

    async function handleSubmit (event) {
        event.preventDefault();

        try {
            const formData = new FormData(event.currentTarget);
            const username = formData.get('email');
            const password = formData.get('password');
            const result = await dispatch(userLogin({username, password, rememberMe: checked}));

            if (result.meta.requestStatus === 'rejected') {
                setErrors(prev => ({...prev, email: 'Неверные данные', password: 'Неверные данные'}));
            }
            
        } catch (error) {
            throw new Error(error);
        }

        
    }

    useEffect(() => {
        if (isAuthenticated) {
            router.push('/');
        }
    }, [isAuthenticated, router]);

    return (
                    <Card style={{backgroundColor: "var(--foreground)", color: "var(--font-color)", borderRadius: 15}} sx={{ width: 350 }}>
                        <CardContent style={{display: "flex", gap: 20, flexDirection: "column", alignItems: "center"}}>
                            <div style={{display: "flex", gap: 20, flexDirection: "column", alignItems: "center"}}>
                                <Typography variant="h4" style={{fontSize: 32}}>Авторизация</Typography>
                                <Divider style={{width: "100%", backgroundColor: "#303030"}}/>
                                <Typography variant="h6" style={{fontSize: 16}}>Введите данные</Typography>
                            </div>
                            <form onSubmit={handleSubmit} style={{display: "flex", gap: 20, flexDirection: "column", alignItems: "center", width: "100%"}}>
                                <StyledInput label="Имя пользователя" variant="outlined" style={{width: "100%"}} name="email" value={values.email} onChange={handleChange} error={errors.email} helperText={errors.email} />
                                <StyledInput label="Пароль" variant="outlined" type="password" style={{width: "100%"}} name="password" value={values.password} onChange={handleChange} error={errors.password} helperText={errors.password} />
                                <StyledButton variant="outlined" style={{width: "100%"}} type="submit">{isLoading ? <CircularProgress size={24} color="inherit" /> : "Войти"}</StyledButton>
                            </form>
                            <div style={{display: "flex", gap: 10, flexDirection: "row", alignItems: "center"}}>
                                <FormControlLabel control={<Checkbox checked={checked} onChange={(e) => setChecked(e.target.checked)} color="success"/>} label="Запомнить меня" />
                                <Typography className={styles.link} variant="h7"><Link  href="/forgot-password">Забыли пароль?</Link></Typography>
                            </div>
                            <Divider style={{width: "100%", backgroundColor: "#303030"}}/>
                            <div style={{display: "flex", gap: 10, flexDirection: "row", alignItems: "center"}}>
                                <Typography variant="h6" style={{fontSize: 16}}>Нет аккаунта?</Typography>
                                <Typography className={styles.link} variant="h7"><Link href="/sign-up">Зарегистрироваться</Link></Typography>
                            </div>
                        </CardContent>
                    </Card>
    );
}