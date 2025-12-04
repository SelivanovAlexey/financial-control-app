"use client";
import { Card, CardContent, Typography, Divider } from "@mui/material";
import styles from "../page.module.css"
import StyledInput from "@/components/input/StyledInput";
import StyledButton from "@/components/button/StyledButton";
import Link from "next/link";
import { useEffect, useState } from "react";
import CircularProgress from '@mui/material/CircularProgress';
import { useRouter } from 'next/navigation';
import { useDispatch } from "@/app/store";
import { useSelector } from "react-redux";
import { userSignUp } from "@/reducers/userReducer";

export default function SignUp() {
    const router = useRouter();
    const [errors, setErrors] = useState({});
    const [values, setValues] = useState({username: '', email: '', password: '', confirmPassword: '' });
    const [loading, setLoading] = useState(false);
    const dispatch = useDispatch();
    const isAuthenticated = useSelector(state => state.user.isAuthenticated);
    const isLoading = useSelector(state => state.user.isLoading);
    
    const validateEmail = (email) =>
        !email || /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

    const handleChange = e => {
        const { name, value } = e.target;
        setValues(prev => ({...prev, [name]: value}));
        
        if (name === 'email') {
            if (!validateEmail(value)) {
                setErrors(prev => ({...prev, email: 'Некорректный email'}));
            } else {
                delete errors.email;
                setErrors({...errors});
            }
        }
        
        if (name === 'confirmPassword') {
            if (value !== values.password) {
                setErrors(prev => ({...prev, confirmPassword: 'Пароли не совпадают'}));
            } else {
                if (value === '') {
                    delete errors.confirmPassword;
                    setErrors({...errors});
                }
                delete errors.confirmPassword;
                setErrors({...errors});
            }
        }
    };

    async function handleSubmit (event) {
        event.preventDefault();
        try {
            const formData = new FormData(event.currentTarget);
            const username = formData.get('username');
            const password = formData.get('password');
            const confirmPassword = formData.get('confirmPassword');
            const email = formData.get('email');
            const result = await dispatch(userSignUp({username, password, confirmPassword, email}));
            console.log(result);

            if (result.meta.requestStatus === 'rejected') {
                setErrors(prev => ({...prev, email: 'Не удалось зарегистрироваться'}));
            }
            
        } catch (error) {
            throw new Error(error);
        }
    };

    useEffect(() => {
        if (isAuthenticated) {
            router.push('/');
        }
    }, [isAuthenticated, router]);

    return (
                    <Card style={{backgroundColor: "#1a1a1a", color: "#fff", borderRadius: 15}} sx={{ width: 350 }}>
                        <CardContent style={{display: "flex", gap: 20, flexDirection: "column", alignItems: "center"}}>
                            <div style={{display: "flex", gap: 20, flexDirection: "column", alignItems: "center"}}>
                                <Typography variant="h4" style={{fontSize: 32}}>Регистрация</Typography>
                                <Divider style={{width: "100%", backgroundColor: "#303030"}}/>
                                <Typography variant="h6" style={{fontSize: 16}}>Заполните обязательные поля</Typography>
                            </div>
                            <form onSubmit={handleSubmit} style={{display: "flex", gap: 20, flexDirection: "column", alignItems: "center", width: "100%"}}>
                                <StyledInput  label="Имя пользователя" variant="outlined" style={{width: "100%"}} name="username" value={values.username} onChange={handleChange} required/>
                                <StyledInput type="email" label="E-mail" variant="outlined" style={{width: "100%"}} name="email" value={values.email} onChange={handleChange} required error={errors.email} helperText={errors.email}/>
                                <StyledInput label="Пароль" variant="outlined" type="password" style={{width: "100%"}} name="password" value={values.password} onChange={handleChange} required error={errors.password} helperText={errors.password}/>
                                <StyledInput label="Повторите пароль" variant="outlined" type="password" style={{width: "100%"}} name="confirmPassword" value={values.confirmPassword} onChange={handleChange} required error={errors.confirmPassword} helperText={errors.confirmPassword}/>
                                <StyledButton variant="outlined" style={{width: "100%"}} type="submit">{loading ? <CircularProgress size={24} color="inherit" /> : "Зарегистрироваться"}</StyledButton>
                            </form>
                            
                            <Divider style={{width: "100%", backgroundColor: "#303030"}}/>
                            <div style={{display: "flex", gap: 10, flexDirection: "row", alignItems: "center"}}>
                                <Typography variant="h6" style={{fontSize: 16}}>Уже есть аккаунт?</Typography>
                                <Typography className={styles.link} variant="h7"><Link href="/login">Войти</Link></Typography>
                            </div>
                        </CardContent>
                    </Card>
    );
}