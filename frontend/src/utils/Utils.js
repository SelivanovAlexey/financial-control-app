// Функция для преобразования даты в timestamp (в миллисекундах)
export const parseDateToTimestamp = (dateString) => {
  if (!dateString) return null;
  
  try {
    // Если это уже число (timestamp в секундах)
    if (typeof dateString === 'number') {
      return dateString * 1000; // конвертируем в миллисекунды
    }
    
    // Если это строка даты в ISO формате
    if (typeof dateString === 'string') {
      // Проверяем, может это уже timestamp как строка
      if (/^\d+$/.test(dateString)) {
        return parseInt(dateString) * 1000;
      }
      
      // Парсим ISO строку
      const date = new Date(dateString);
      return isNaN(date.getTime()) ? null : date.getTime();
    }
    
    return null;
  } catch (error) {
    console.error('Ошибка парсинга даты:', error);
    return null;
  }
};

// Функция для фильтрации данных по периоду
export const filterDataByPeriod = (data, period) => {
  if (!data || !Array.isArray(data)) return [];
  
  const now = new Date();
  
  switch (period) {
    case 'left': // Последние 7 дней
      const weekAgo = new Date();
      weekAgo.setDate(now.getDate() - 7);
      weekAgo.setHours(0, 0, 0, 0);
      
      return data.filter(item => {
        const timestamp = parseDateToTimestamp(item?.createDate);
        if (!timestamp) return false;
        
        const itemDate = new Date(timestamp);
        return itemDate >= weekAgo;
      });
      
    case 'center': // Последние 30 дней
      const monthAgo = new Date();
      monthAgo.setDate(now.getDate() - 30);
      monthAgo.setHours(0, 0, 0, 0);
      
      return data.filter(item => {
        const timestamp = parseDateToTimestamp(item?.createDate);
        if (!timestamp) return false;
        
        const itemDate = new Date(timestamp);
        return itemDate >= monthAgo;
      });
      
    case 'right': // Последние 365 дней
      const yearAgo = new Date();
      yearAgo.setDate(now.getDate() - 365);
      yearAgo.setHours(0, 0, 0, 0);
      
      return data.filter(item => {
        const timestamp = parseDateToTimestamp(item?.createDate);
        if (!timestamp) return false;
        
        const itemDate = new Date(timestamp);
        return itemDate >= yearAgo;
      });
      
    default:
      return data;
  }
};

// Функция для группировки данных по категориям
export const groupDataByCategory = (data) => {
  if (!data || !Array.isArray(data)) return [];
  
  const grouped = data.reduce((acc, item) => {
    const category = item.category || 'Другое';
    if (!acc[category]) {
      acc[category] = 0;
    }
    acc[category] += item.amount || 0;
    return acc;
  }, {});

  return Object.entries(grouped).map(([label, value]) => ({
    label,
    value,
    id: label
  }));
};

// Функция для получения данных по временным периодам
export const getDataByTimePeriod = (data, period) => {
  if (!data || !Array.isArray(data)) return { data: [], labels: [] };
  
  const now = new Date();
  let periods = [];
  let labels = [];
  
  switch (period) {
    case 'left': // Неделя (последние 7 дней)
      periods = Array(7).fill(0);
      labels = Array(7).fill('');
      
      for (let i = 6; i >= 0; i--) {
        const date = new Date(now.getTime() - i * 24 * 60 * 60 * 1000);
        labels[6-i] = date.toLocaleDateString('ru-RU', { day: 'numeric', month: 'short' });
        
        data.forEach(item => {
          const timestamp = parseDateToTimestamp(item.createDate);
          if (timestamp) {
            const itemDate = new Date(timestamp);
            if (itemDate.toDateString() === date.toDateString()) {
              periods[6-i] += item.amount || 0;
            }
          }
        });
      }
      return { data: periods, labels };

    case 'center': // Месяц (последние 30 дней)
      periods = Array(30).fill(0);
      labels = Array(30).fill('');
      
      for (let i = 29; i >= 0; i--) {
        const date = new Date(now.getTime() - i * 24 * 60 * 60 * 1000);
        labels[29-i] = date.toLocaleDateString('ru-RU', { day: 'numeric', month: 'short' });
        
        data.forEach(item => {
          const timestamp = parseDateToTimestamp(item.createDate);
          if (timestamp) {
            const itemDate = new Date(timestamp);
            if (itemDate.toDateString() === date.toDateString()) {
              periods[29-i] += item.amount || 0;
            }
          }
        });
      }
      return { data: periods, labels };

    case 'right': // Год (последние 12 месяцев)
      periods = Array(12).fill(0);
      labels = Array(12).fill('');
      
      for (let i = 11; i >= 0; i--) {
        const month = new Date(now.getFullYear(), now.getMonth() - i, 1);
        labels[11-i] = month.toLocaleDateString('ru-RU', { month: 'short' });
        
        data.forEach(item => {
          const timestamp = parseDateToTimestamp(item.createDate);
          if (timestamp) {
            const itemDate = new Date(timestamp);
            if (itemDate.getMonth() === month.getMonth() && 
                itemDate.getFullYear() === month.getFullYear()) {
              periods[11-i] += item.amount || 0;
            }
          }
        });
      }
      return { data: periods, labels };

    default:
      return { data: Array(7).fill(0), labels: Array(7).fill('') };
  }
};

// Функция для расчета общей суммы
export const calculateTotalAmount = (data) => {
  if (!data || !Array.isArray(data)) return 0;
  return data.reduce((total, item) => total + (item.amount || 0), 0);
};

// Функция для безопасного парсинга даты
export const parseDateSafely = (dateInput) => {
  if (!dateInput) return new Date();
  
  try {
    // Если это уже Date объект
    if (dateInput instanceof Date) {
      return isNaN(dateInput.getTime()) ? new Date() : dateInput;
    }
    
    // Если это число (timestamp)
    if (typeof dateInput === 'number') {
      const date = new Date(dateInput);
      return isNaN(date.getTime()) ? new Date() : date;
    }
    
    // Если это строка
    if (typeof dateInput === 'string') {
      let date;
      
      // Проверяем различные форматы строк
      if (dateInput.includes('T')) {
        // ISO формат или формат с временной зоной
        if (dateInput.endsWith('Z') || dateInput.includes('+') || dateInput.includes('-')) {
          // ISO 8601 с Z или смещением временной зоны
          date = new Date(dateInput);
        } else {
          // ISO без временной зоны - добавляем Z чтобы парсить как UTC
          date = new Date(dateInput + 'Z');
        }
      } 
      // Формат YYYY-MM-DD (без времени)
      else if (/^\d{4}-\d{2}-\d{2}$/.test(dateInput)) {
        // Ключевое изменение: создаем дату с учетом локального времени
        // Разбиваем на части и создаем Date с локальным временем
        const [year, month, day] = dateInput.split('-').map(Number);
        date = new Date(year, month - 1, day, 12, 0, 0); // Полдень по локальному времени
      }
      // Любая другая строка
      else {
        date = new Date(dateInput);
      }
      
      if (isNaN(date.getTime())) {
        console.warn('Не удалось распарсить дату:', dateInput);
        return new Date();
      }
      
      return date;
    }
    
    return new Date();
  } catch (error) {
    console.error('Ошибка парсинга даты:', error, dateInput);
    return new Date();
  }
};

// Функция для форматирования даты в DD.MM.YYYY
export const formatDateForDisplay = (dateInput) => {
  try {
    const date = parseDateSafely(dateInput);
    
    // Всегда используем локальные методы для отображения
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    
    return `${day}.${month}.${year}`;
  } catch (error) {
    console.error('Ошибка форматирования даты:', error, dateInput);
    return '';
  }
};

// Функция для получения корректного timestamp для сортировки
export const getTimestampForSorting = (dateInput) => {
  try {
    const date = parseDateSafely(dateInput);
    return date.getTime();
  } catch (error) {
    console.error('Ошибка получения timestamp:', error, dateInput);
    return Date.now();
  }
};

// Функция для форматирования даты в ISO строку с временной зоной
export const formatToISOWithTimezone = (dateInput) => {
  try {
    const date = parseDateSafely(dateInput);
    
    // Получаем компоненты даты в локальном времени
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const seconds = date.getSeconds().toString().padStart(2, '0');
    
    // Получаем смещение временной зоны в минутах
    const timezoneOffset = date.getTimezoneOffset();
    const offsetHours = Math.abs(Math.floor(timezoneOffset / 60));
    const offsetMinutes = Math.abs(timezoneOffset % 60);
    const offsetSign = timezoneOffset > 0 ? '-' : '+';
    
    // Формат: 2025-12-06T19:29:20+03:00
    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}${offsetSign}${offsetHours.toString().padStart(2, '0')}:${offsetMinutes.toString().padStart(2, '0')}`;
  } catch (error) {
    console.error('Ошибка форматирования в ISO:', error, dateInput);
    // Fallback: текущая дата с временной зоной
    const now = new Date();
    const timezoneOffset = now.getTimezoneOffset();
    const offsetHours = Math.abs(Math.floor(timezoneOffset / 60));
    const offsetMinutes = Math.abs(timezoneOffset % 60);
    const offsetSign = timezoneOffset > 0 ? '-' : '+';
    
    return `${now.getFullYear()}-${(now.getMonth() + 1).toString().padStart(2, '0')}-${now.getDate().toString().padStart(2, '0')}T${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}:${now.getSeconds().toString().padStart(2, '0')}${offsetSign}${offsetHours.toString().padStart(2, '0')}:${offsetMinutes.toString().padStart(2, '0')}`;
  }
};

// Вспомогательная функция для создания корректной даты при отправке на бэкенд
export const createDateForBackend = (dateString, timeString = '12:00') => {
  // Если пришла строка в формате YYYY-MM-DD
  if (typeof dateString === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(dateString)) {
    const [year, month, day] = dateString.split('-').map(Number);
    const [hours = 12, minutes = 0] = timeString.split(':').map(Number);
    
    // Создаем дату с указанным временем в локальном часовом поясе
    const date = new Date(year, month - 1, day, hours, minutes, 0);
    
    // Форматируем с временной зоной
    return formatToISOWithTimezone(date);
  }
  
  // Любая другая дата - форматируем как есть
  return formatToISOWithTimezone(dateString);
};