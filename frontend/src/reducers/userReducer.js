import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { loginUser, logoutUser, signUpUser, getAllExpenses, getAllIncomes, createExpense, createIncome } from "../api/authApi";

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
  async (_, { signal, getState }) => {
    const response = await getAllExpenses();
    if (signal.aborted) {
      throw new Error('Request aborted');
    }
    
    if (response.ok) {
      const data = await response.json();
      // В реальном приложении здесь должна быть фильтрация по userId
      // В вашем API, вероятно, сервер сам возвращает данные для текущего пользователя
      return data;
    } else {
      if (response.status === 401) {
        throw new Error('Unauthorized');
      }
      throw new Error(`Failed to get expenses: ${response.status}`);
    }
  }
);

export const fetchIncomes = createAsyncThunk(
  'incomes/fetchIncomes',
  async (_, { signal }) => {
    const response = await getAllIncomes();
    if (signal.aborted) {
      throw new Error('Request aborted');
    }
    
    if (response.ok) {
      const data = await response.json();
      return data;
    } else {
      if (response.status === 401) {
        throw new Error('Unauthorized');
      }
      throw new Error(`Failed to get incomes: ${response.status}`);
    }
  }
);

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
        state.incomes = action.payload; // сохраняем полученные доходы
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
        // Используем timestamp из даты формы, а не из ответа сервера
        const formDate = action.meta.arg.createDate; // ISO строка из формы
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
        // Используем timestamp из даты формы, а не из ответа сервера
        const formDate = action.meta.arg.createDate; // ISO строка из формы
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
      });
  },
});

export const { resetAuthState } = userSlice.actions;
export const userReducer = userSlice.reducer;