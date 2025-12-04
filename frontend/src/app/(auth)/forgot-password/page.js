"use client";
import { Card, CardContent, Typography, Divider } from "@mui/material";
import styles from "../page.module.css"
import StyledInput from "@/components/input/StyledInput";
import StyledButton from "@/components/button/StyledButton";
import Link from "next/link";
import { useState } from "react";
import CircularProgress from '@mui/material/CircularProgress';
import { useRouter } from 'next/navigation';

const validateEmail = (email) =>
  !email || /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

export default function ForgotPassword() {
    const router = useRouter();
    const [errors, setErrors] = useState({});
    const [values, setValues] = useState({ email: '', password: '' });
    const [loading, setLoading] = useState(false);
    
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
    };

    const handleSubmit = event => {
        event.preventDefault();
        let newErrors = {};
        
        if (!values.email) {
            newErrors.email = 'Необходимо ввести Email';
        } else if (!validateEmail(values.email)) {
            newErrors.email = 'Некорректный email';
        }
        
        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
        } else {
            setLoading(true);
            setTimeout(() => {
                setLoading(false);
                router.push('/login');
            }, 2000);
        }
    };

    return (
                    <Card style={{backgroundColor: "#1a1a1a", color: "#fff", borderRadius: 15}} sx={{ width: 350 }}>
                        <CardContent style={{display: "flex", gap: 20, flexDirection: "column", alignItems: "center"}}>
                            <div style={{display: "flex", gap: 20, flexDirection: "column", alignItems: "center", textAlign: "center"}}>
                                <Typography variant="h4" style={{fontSize: 32}}>Восстановление пароля</Typography>
                                <Divider style={{width: "70%", backgroundColor: "#303030"}}/>
                                <Typography variant="h6" style={{fontSize: 16}}>Укажите email, на нее придет новый пароль</Typography>
                            </div>
                            <form onSubmit={handleSubmit} style={{display: "flex", gap: 20, flexDirection: "column", alignItems: "center", width: "100%"}}>
                                <StyledInput type="email" label="Email" variant="outlined" style={{width: "100%"}} name="email" value={values.email} onChange={handleChange} required error={errors.email} helperText={errors.email}/>
                                <StyledButton variant="outlined" style={{width: "100%"}} type="submit">{loading ? <CircularProgress size={24} color="inherit" /> : "Отправить"}</StyledButton>
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