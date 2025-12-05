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