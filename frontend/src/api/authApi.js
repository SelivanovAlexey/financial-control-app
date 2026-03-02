import { apiClient } from './apiClient';

/**
 * AUTH
 */
export const loginUser = (username, password, rememberMe) =>
    apiClient('/auth/login', {
        method: 'POST',
        body: { username, password, rememberMe }
    });
export const logoutUser = () =>
    apiClient('/auth/logout', { method: 'POST' });
export const signUpUser = (username, password, confirmPassword, email) =>
    apiClient('/auth/signup', {
        method: 'POST',
        body: { username, password, confirmPassword, email }
    });
/**
 * EXPENSES & INCOMES
 */
export const getAllExpenses = () =>
    apiClient('/expenses');
export const getAllIncomes = () =>
    apiClient('/incomes');
export const getCurrentUser = () =>
    apiClient('/users/me');
export const createExpense = (data) =>
    apiClient('/expenses', { method: 'POST', body: data });
export const createIncome = (data) =>
    apiClient('/incomes', { method: 'POST', body: data });
export const deleteExpense = (id) =>
    apiClient(`/expenses/${id}`, { method: 'DELETE' });
export const deleteIncome = (id) =>
    apiClient(`/incomes/${id}`, { method: 'DELETE' });