import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { loginUser, logoutUser, signUpUser, getAllExpenses, getAllIncomes, createExpense, createIncome, deleteExpense, deleteIncome } from "../api/authApi";

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
    if (response.status === 200 || response.status === 201) {
      return response.data;
    } else {
      throw new Error(response.headers.get('error'));
    }
  }
);

export const fetchExpenses = createAsyncThunk(
  'expenses/fetchExpenses',
  async (_, { signal, rejectWithValue }) => {
    try {
      const response = await getAllExpenses();
      
      if (signal.aborted) {
        throw new Error('Request aborted');
      }
      
      if (response.ok) {
        const data = await response.json();
        return data;
      } else {
        if (response.status === 401) {
          return rejectWithValue('Unauthorized');
        }
        return rejectWithValue(`Failed to get expenses: ${response.status}`);
      }
    } catch (error) {
      if (error.name === 'AbortError') {
        throw error;
      }
      return rejectWithValue(error.message || 'Failed to fetch expenses');
    }
  }
);

export const fetchIncomes = createAsyncThunk(
  'incomes/fetchIncomes',
  async (_, { signal, rejectWithValue }) => {
    try {
      const response = await getAllIncomes();
      
      if (signal.aborted) {
        throw new Error('Request aborted');
      }
      
      if (response.ok) {
        const data = await response.json();
        return data;
      } else {
        if (response.status === 401) {
          return rejectWithValue('Unauthorized');
        }
        return rejectWithValue(`Failed to get incomes: ${response.status}`);
      }
    } catch (error) {
      if (error.name === 'AbortError') {
        throw error;
      }
      return rejectWithValue(error.message || 'Failed to fetch incomes');
    }
  }
)

export const fetchCreateExpense = createAsyncThunk(
  'expense/createExpense',
  async (data) => {
    const response = await createExpense(data);
    
    if (response.ok) {
      const result = await response.json();
      return result;
    } else {
      const errorText = await response.text();
      throw new Error(errorText || 'Failed to create expense');
    }
  }
);

export const fetchCreateIncome = createAsyncThunk(
  'income/createIncome',
  async (data) => {
    const response = await createIncome(data);
    
    if (response.ok) {
      const result = await response.json();
      return result;
    } else {
      const errorText = await response.text();
      throw new Error(errorText || 'Failed to create income');
    }
  }
)

export const fetchDeleteExpense = createAsyncThunk(
  'expense/deleteExpense',
  async (id) => {
    const response = await deleteExpense(id);
    
    if (response.ok) {
      const result = await response.json();
      return result;
    } else {
      const errorText = await response.text();
      throw new Error(errorText || 'Failed to delete expense');
    }
  })

export const fetchDeleteIncome = createAsyncThunk(
  'income/deleteIncome',
  async (id) => {
    const response = await deleteIncome(id);
    
    if (response.ok) {
      const result = await response.json();
      return result;
    } else {
      const errorText = await response.text();
      throw new Error(errorText || 'Failed to delete income');
    }
  })

export const checkAuth = createAsyncThunk(
  'user/checkAuth',
  async (_, { rejectWithValue, dispatch }) => {
    try {
      // Пробуем загрузить расходы - если получится, значит авторизован
      const result = await dispatch(fetchExpenses()).unwrap();
      
      // Если запрос прошел без ошибок - авторизован
      return { 
        isAuthenticated: true,
        initialExpenses: result 
      };
    } catch (error) {
      // Если ошибка 401 или другая - не авторизован
      return rejectWithValue({
        isAuthenticated: false,
        error: error.message || 'Auth check failed'
      });
    }
  }
);

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
    // Добавляем редьюсер для принудительного сброса авторизации
    resetAuthState: (state) => {
      state.isAuthenticated = false;
      state.isAuthChecked = true;
      state.expenses = [];
      state.incomes = [];
      state.userError = null;
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
        state.expenses = [];
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
      
      .addCase(fetchExpenses.pending, (state) => {
        state.isLoading = true;
        state.userError = null;
      })
      .addCase(fetchExpenses.fulfilled, (state, action) => {
        state.isLoading = false;
        state.expenses = action.payload;
        state.userError = null;
      })
      .addCase(fetchExpenses.rejected, (state, action) => {
        state.isLoading = false;
        state.userError = action.error.message || 'Failed to fetch expenses';
        if (action.error.message === 'Unauthorized') {
          state.isAuthenticated = false;
          state.expenses = [];
        }
      })

      .addCase(fetchIncomes.pending, (state) => {
        state.isLoading = true;
        state.userError = null;
      })
      .addCase(fetchIncomes.fulfilled, (state, action) => {
        state.isLoading = false;
        state.incomes = action.payload;
        state.userError = null;
      })
      .addCase(fetchIncomes.rejected, (state, action) => {
        state.isLoading = false;
        state.userError = action.error.message || 'Failed to fetch incomes';
        if (action.error.message === 'Unauthorized') {
          state.isAuthenticated = false;
          state.incomes = [];
        }
      })

      .addCase(fetchCreateExpense.pending, (state) => {
        state.userError = null;
        state.isLoading = true;
      })
      .addCase(fetchCreateExpense.fulfilled, (state, action) => {
        const formDate = action.meta.arg.createDate;
        const timestampFromForm = Math.floor(new Date(formDate).getTime() / 1000);
        
        const expenseWithCorrectDate = {
          ...action.payload,
          createDate: timestampFromForm
        };
        
        state.expenses.push(expenseWithCorrectDate);
        state.userError = null;
        state.isLoading = false;
      })
      .addCase(fetchCreateExpense.rejected, (state, action) => {
        state.userError = action.error.message || 'Failed to create expense';
      })

      .addCase(fetchCreateIncome.pending, (state) => {
        state.userError = null;
        state.isLoading = true;
      })
      .addCase(fetchCreateIncome.fulfilled, (state, action) => {
        const formDate = action.meta.arg.createDate;
        const timestampFromForm = Math.floor(new Date(formDate).getTime() / 1000);
        
        const incomeWithCorrectDate = {
          ...action.payload,
          createDate: timestampFromForm
        };
        
        state.incomes.push(incomeWithCorrectDate);
        state.userError = null;
        state.isLoading = false;
      })
      .addCase(fetchCreateIncome.rejected, (state, action) => {
        state.userError = action.error.message || 'Failed to create income';
      })

      .addCase(checkAuth.pending, (state) => {
    state.isLoading = true;
    state.userError = null;
      })
      .addCase(checkAuth.fulfilled, (state, action) => {
        state.isAuthenticated = true;
        state.isAuthChecked = true;
        state.isLoading = false;
        state.userError = null;
      })
      .addCase(checkAuth.rejected, (state, action) => {
        state.isAuthenticated = false;
        state.isAuthChecked = true;
        state.isLoading = false;
        state.userError = action.payload?.error || null;
      })

      .addCase(fetchDeleteExpense.pending, (state) => {
        state.isLoading = true;
        state.userError = null;
      })
      .addCase(fetchDeleteExpense.fulfilled, (state, action) => {
        state.isLoading = false;
        state.userError = null;
        state.expenses = state.expenses.filter((expense) => expense.id !== action.payload);
      })
      .addCase(fetchDeleteExpense.rejected, (state, action) => {
        state.isLoading = false;
        state.userError = action.error.message || 'Failed to delete expense';
      })

      .addCase(fetchDeleteIncome.pending, (state) => {
        state.isLoading = true;
        state.userError = null;
      })
      .addCase(fetchDeleteIncome.fulfilled, (state, action) => {
        state.isLoading = false;
        state.userError = null;
        state.incomes = state.incomes.filter((income) => income.id !== action.payload);
      })
      .addCase(fetchDeleteIncome.rejected, (state, action) => {
        state.isLoading = false;
        state.userError = action.error.message || 'Failed to delete income';
      })
  },
});

export const { resetAuthState } = userSlice.actions;
export const userReducer = userSlice.reducer;