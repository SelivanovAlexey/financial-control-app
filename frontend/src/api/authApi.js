export function loginUser(username, password, rememberMe) {
  return fetch(`/api/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ username, password, rememberMe }),
    credentials: 'include',
  });
}

export function logoutUser () {
    return fetch(`/api/auth/logout`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include',
    })
}

export function signUpUser (username, password, confirmPassword, email) {
    return fetch(`/api/auth/signup`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password, confirmPassword, email }),
        credentials: 'include',
    })
}

export function getAllExpenses () {
    return fetch(`/api/expenses`, {
        method: 'GET',
        credentials: 'include',
    })
}

export function getAllIncomes () {
    return fetch(`/api/incomes`, {
        method: 'GET',
        credentials: 'include',
    })
}

export function createExpense (data) {
    return fetch(`/api/expenses`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
        credentials: 'include',
    })
}

export function createIncome (data) {
    return fetch(`/api/incomes`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
        credentials: 'include',
    })
}

export function deleteExpense (id) {
    return fetch(`/api/expenses/${id}`, {
        method: 'DELETE',
        credentials: 'include',
    })
}

export function deleteIncome (id) {
    return fetch(`/api/incomes/${id}`, {
        method: 'DELETE',
        credentials: 'include',
    })
}