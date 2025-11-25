import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { loginUser, logoutUser, signUpUser, getAllExpenses, getAllIncomes } from "../api/authApi";

export const userLogin = createAsyncThunk(
  'user/loginUser',
  async (data) => {
    const response = await loginUser(data.username, data.password, data.rememberMe);
    if (response.status === 200) {
      return response.data;
    } else {
      throw new Error('Failed to login');
    }
  }
);

export const userLogout = createAsyncThunk(
  'user/logoutUser',
  async () => {
    const response = await logoutUser();
    if (response.status === 200) {
      return response.data;
    } else {
      throw new Error('Failed to logout');
    }
  }
);

export const userSignUp = createAsyncThunk(
  'user/signUpUser',
  async (data) => {
    const response = await signUpUser(data.username, data.password, data.confirmPassword, data.email);
    if (response.status === 200) {
      return response.data;
    } else {
      throw new Error(response.headers.get('error'));
    }
  }
);

export const fetchExpenses = createAsyncThunk(
  'expenses/fetchExpenses',
  async (userId = null, { signal }) => {
    const response = await getAllExpenses();
    if (signal.aborted) {
      throw new Error('Request aborted');
    }
    if (response.status === 200) {
      const data = await response.json();
      const filteredData = userId 
        ? data.filter(expense => expense.user_id === userId)
        : data;
      return filteredData;
    } else {
      throw new Error('Failed to get expenses');
    }
  }
);

export const fetchIncomes = createAsyncThunk(
  'incomes/fetchIncomes',
  async (userId = null, { signal }) => {
    const response = await getAllIncomes();
    if (signal.aborted) {
      throw new Error('Request aborted');
    }
    if (response.status === 200) {
      const data = await response.json();
      const filteredData = userId 
        ? data.filter(income => income.user_id === userId)
        : data;
      return filteredData;
    } else {
      throw new Error('Failed to get incomes');
    }
  }
)

const initialState = {
  userRequest: false,
  expenses: [],
  incomes: [],
  userError: null,
  isAuthenticated: false,
  isAuthChecked: false,
  isLoading: false
};

const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    setUser: (state, action) => {
      state.isAuthenticated = true;
      state.expenses = action.payload.expenses;
      state.incomes = action.payload.incomes;
      state.name = action.payload.name;
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(userLogin.pending, (state) => {
        state.userRequest = true;
        state.userError = null;
        state.isLoading = true;
      })
      .addCase(userLogin.fulfilled, (state, action) => {
        state.isAuthenticated = true;
        state.userRequest = false;
        state.userError = null;
        state.isAuthChecked = true;
        state.isLoading = false;
      })
      .addCase(userLogin.rejected, (state, action) => {
        state.isAuthenticated = false;
        state.userRequest = false;
        state.userError = action.error.message || 'Authorization failed.';
        state.isLoading = false;
      })
      
      .addCase(userLogout.pending, (state) => {
        state.userRequest = true;
        state.userError = null;
      })
      .addCase(userLogout.fulfilled, (state) => {
        state.isAuthenticated = false;
        state.userRequest = false;
        state.userError = null;
        state.isAuthChecked = true;
        state.expenses = []; // очищаем expenses при выходе
      })
      .addCase(userLogout.rejected, (state, action) => {
        state.isAuthenticated = false;
        state.userRequest = false;
        state.userError = action.error.message || 'Logout failed.';
      })
      
      .addCase(userSignUp.pending, (state) => {
        state.userRequest = true;
        state.userError = null;
        state.isLoading = true;
      })
      .addCase(userSignUp.fulfilled, (state) => {
        state.isAuthenticated = true;
        state.userRequest = false;
        state.userError = null;
        state.isAuthChecked = true;
        state.isLoading = false;
      })
      .addCase(userSignUp.rejected, (state, action) => {
        state.isAuthenticated = false;
        state.userRequest = false;
        state.userError = action.error.message || 'Registration failed.';
        state.isLoading = false;
      })
      
      // Добавляем обработку fetchExpenses
      .addCase(fetchExpenses.pending, (state) => {
        state.isLoading = true;
        state.userError = null;
      })
      .addCase(fetchExpenses.fulfilled, (state, action) => {
        state.isLoading = false;
        state.expenses = action.payload; // сохраняем полученные расходы
        state.userError = null;
      })
      .addCase(fetchExpenses.rejected, (state, action) => {
        state.isLoading = false;
        state.userError = action.error.message || 'Failed to fetch expenses';
      })

      .addCase(fetchIncomes.pending, (state) => {
        state.isLoading = true;
        state.userError = null;
      })
      .addCase(fetchIncomes.fulfilled, (state, action) => {
        state.isLoading = false;
        state.incomes = action.payload; // сохраняем полученные доходы
        state.userError = null;
      })
      .addCase(fetchIncomes.rejected, (state, action) => {
        state.isLoading = false;
        state.userError = action.error.message || 'Failed to fetch incomes';
      })
  },
});

export const userReducer = userSlice.reducer;