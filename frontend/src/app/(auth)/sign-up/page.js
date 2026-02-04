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
    const { isAuthenticated, status, error } = useSelector(state => state.user);
    
    const validateEmail = (email) =>
        !email || /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

    const validatePassword = (password, minLength = 4) => {
        if (typeof password !== 'string') {
            return {
                isValid: false,
                error: 'Пароль должен быть строкой'
            };
        }
        
        const trimmedPassword = password.trim();
        
        if (trimmedPassword.length < minLength) {
            return {
                isValid: false,
                error: `Пароль должен содержать минимум ${minLength} символа`
            };
        }
        
        return {
            isValid: true,
            error: null
        };
    };

    const handleChange = e => {
        const { name, value } = e.target;
        setValues(prev => ({...prev, [name]: value}));
        
        if (name === 'email') {
            if (!validateEmail(value)) {
                setErrors(prev => ({...prev, email: 'Некорректный email'}));
            } else {
                const newErrors = {...errors};
                delete newErrors.email;
                setErrors(newErrors);
            }
        }

        if (name === 'password') {
            const validation = validatePassword(value);
            if (!validation.isValid) {
                setErrors(prev => ({...prev, password: validation.error}));
            } else {
                const newErrors = {...errors};
                delete newErrors.password;
                setErrors(newErrors);
            }
            
            if (values.confirmPassword && value !== values.confirmPassword) {
                setErrors(prev => ({...prev, confirmPassword: 'Пароли не совпадают'}));
            } else if (values.confirmPassword && value === values.confirmPassword) {
                const newErrors = {...errors};
                delete newErrors.confirmPassword;
                setErrors(newErrors);
            }
        }
        
        if (name === 'confirmPassword') {
            if (value !== values.password) {
                setErrors(prev => ({...prev, confirmPassword: 'Пароли не совпадают'}));
            } else {
                const newErrors = {...errors};
                delete newErrors.confirmPassword;
                setErrors(newErrors);
            }
        }
    };

    async function handleSubmit (event) {
        event.preventDefault();
        setLoading(true);
        
        // Валидация
        const emailValid = validateEmail(values.email);
        const passwordValidation = validatePassword(values.password);
        const passwordsMatch = values.password === values.confirmPassword;
        
        if (!emailValid) {
            setErrors(prev => ({...prev, email: 'Некорректный email'}));
            setLoading(false);
            return;
        }
        
        if (!passwordValidation.isValid) {
            setErrors(prev => ({...prev, password: passwordValidation.error}));
            setLoading(false);
            return;
        }
        
        if (!passwordsMatch) {
            setErrors(prev => ({...prev, confirmPassword: 'Пароли не совпадают'}));
            setLoading(false);
            return;
        }
        
        if (Object.keys(errors).length > 0) {
            setLoading(false);
            return;
        }
        
        try {
            const formData = new FormData(event.currentTarget);
            const username = formData.get('username');
            const password = formData.get('password');
            const confirmPassword = formData.get('confirmPassword');
            const email = formData.get('email');
            
            const result = await dispatch(userSignUp({username, password, confirmPassword, email}));

            if (result.meta.requestStatus === 'fulfilled') {
                // Успешная регистрация - редирект произойдет автоматически через useEffect
                // Можно очистить форму или показать сообщение об успехе
                setValues({username: '', email: '', password: '', confirmPassword: ''});
            } else if (result.meta.requestStatus === 'rejected') {
                // Обработка ошибок сервера
                if (result.payload && result.payload.message) {
                    setErrors(prev => ({...prev, form: result.payload.message}));
                } else {
                    setErrors(prev => ({...prev, form: 'Не удалось зарегистрироваться'}));
                }
            }
            
        } catch (error) {
            setErrors(prev => ({...prev, form: 'Произошла ошибка'}));
            console.error('Registration error:', error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (isAuthenticated) {
            router.push('/');
        }
    }, [isAuthenticated, router]);

    // Добавьте этот useEffect для обработки ошибок из Redux
    useEffect(() => {
        if (error) {
            // Обработка ошибок от сервера
            if (error.includes('Email') || error.includes('email')) {
                setErrors(prev => ({...prev, email: error}));
            } else if (error.includes('Username') || error.includes('username')) {
                setErrors(prev => ({...prev, username: error}));
            } else {
                setErrors(prev => ({...prev, form: error}));
            }
        }
    }, [error]);

    return (
        <Card style={{backgroundColor: "#1a1a1a", color: "#fff", borderRadius: 15}} sx={{ width: 350 }}>
            <CardContent style={{display: "flex", gap: 20, flexDirection: "column", alignItems: "center"}}>
                <div style={{display: "flex", gap: 20, flexDirection: "column", alignItems: "center"}}>
                    <Typography variant="h4" style={{fontSize: 32}}>Регистрация</Typography>
                    <Divider style={{width: "100%", backgroundColor: "#303030"}}/>
                    <Typography variant="h6" style={{fontSize: 16}}>Заполните обязательные поля</Typography>
                </div>
                
                {/* Отображение общей ошибки формы */}
                {errors.form && (
                    <Typography color="error" variant="body2" align="center">
                        {errors.form}
                    </Typography>
                )}
                
                <form onSubmit={handleSubmit} style={{display: "flex", gap: 20, flexDirection: "column", alignItems: "center", width: "100%"}}>
                    <StyledInput  
                        label="Имя пользователя" 
                        variant="outlined" 
                        style={{width: "100%"}} 
                        name="username" 
                        value={values.username} 
                        onChange={handleChange} 
                        required
                        error={!!errors.username}
                        helperText={errors.username}
                    />
                    <StyledInput 
                        type="email" 
                        label="E-mail" 
                        variant="outlined" 
                        style={{width: "100%"}} 
                        name="email" 
                        value={values.email} 
                        onChange={handleChange} 
                        required 
                        error={!!errors.email}
                        helperText={errors.email}
                    />
                    <StyledInput 
                        label="Пароль" 
                        variant="outlined" 
                        type="password" 
                        style={{width: "100%"}} 
                        name="password" 
                        value={values.password} 
                        onChange={handleChange} 
                        required 
                        error={!!errors.password}
                        helperText={errors.password}
                    />
                    <StyledInput 
                        label="Повторите пароль" 
                        variant="outlined" 
                        type="password" 
                        style={{width: "100%"}} 
                        name="confirmPassword" 
                        value={values.confirmPassword} 
                        onChange={handleChange} 
                        required 
                        error={!!errors.confirmPassword}
                        helperText={errors.confirmPassword}
                    />
                    <StyledButton 
                        variant="outlined" 
                        style={{width: "100%"}} 
                        type="submit"
                        disabled={loading}
                    >
                        {loading ? <CircularProgress size={24} color="inherit" /> : "Зарегистрироваться"}
                    </StyledButton>
                </form>
                
                <Divider style={{width: "100%", backgroundColor: "#303030"}}/>
                <div style={{display: "flex", gap: 10, flexDirection: "row", alignItems: "center"}}>
                    <Typography variant="h6" style={{fontSize: 16}}>Уже есть аккаунт?</Typography>
                    <Typography className={styles.link} variant="h7">
                        <Link href="/login">Войти</Link>
                    </Typography>
                </div>
            </CardContent>
        </Card>
    );
}