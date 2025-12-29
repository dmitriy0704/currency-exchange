import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8098/backend_war_exploded/api/',  // Замените на URL вашего Java-сервера (например, Spring Boot обычно на 8080)
  // Дополнительно: таймаут, headers и т.д.
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export default api;