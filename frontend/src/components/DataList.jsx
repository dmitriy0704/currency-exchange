import React, { useState, useEffect } from 'react';

import api from '../api/axios';

function DataList() {
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await api.get('/currencies');
        setData(response.data);  // Axios автоматически парсит JSON
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);



  if (loading) return <p>Загрузка...</p>;
  if (error) return <p>Ошибка: {error}</p>;

  return (
    <ul>
      {data.map(item => (
        <li key={item.id}>{item.full_name /* или что-то из вашего JSON */}</li>
      ))}
    </ul>
  );

}

export default DataList;