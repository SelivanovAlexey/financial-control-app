import env from '../../../env-config';
const URL = env.BACKEND_URL;

export function loginUser(username, password, rememberMe) {
  return fetch(`${URL}/api/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ username, password, rememberMe }),
    credentials: 'include',
  });
}

export function logoutUser () {
    return fetch(`${URL}/api/auth/logout`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include',
    })
}

export function signUpUser (username, password, confirmPassword, email) {
    return fetch(`${URL}/api/auth/signup`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password, confirmPassword, email }),
        credentials: 'include',
    })
}

export function getAllExpenses () {
    return fetch(`${URL}/api/expenses`, {
        method: 'GET',
        credentials: 'include',
    })
}

export function getAllIncomes () {
    return fetch(`${URL}/api/incomes`, {
        method: 'GET',
        credentials: 'include',
    })
}

export function createExpense (data) {
    return fetch(`${URL}/api/expenses`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
        credentials: 'include',
    })
}

export function createIncome (data) {
    return fetch(`${URL}/api/incomes`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
        credentials: 'include',
    })
}

export function deleteExpense (id) {
    return fetch(`${URL}/api/expenses/${id}`, {
        method: 'DELETE',
        credentials: 'include',
    })
}

export function deleteIncome (id) {
    return fetch(`${URL}/api/incomes/${id}`, {
        method: 'DELETE',
        credentials: 'include',
    })
}