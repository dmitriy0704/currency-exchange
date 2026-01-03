import React, { useState, useEffect } from 'react';

import api from '../api/axios';

function Currencies({currencies, loading, error}) {
  if (loading) return <p>Загрузка списка валют...</p>;
  if (!currencies || currencies.length === 0) return <p>Нет данных</p>;

  // const [data, setData] = useState([]);
  // const [loading, setLoading] = useState(true);
  // const [error, setError] = useState(null);

  // useEffect(() => {
  //   const fetchData = async () => {
  //     try {
  //       const response = await api.get('/currencies');
  //       setData(response.data);  // Axios автоматически парсит JSON
  //     } catch (err) {
  //       setError(err.message);
  //     } finally {
  //       setLoading(false);
  //     }
  //   };
  //
  //   fetchData();
  // }, []);


  if (loading) return <p>Загрузка...</p>;
  if (error) return <p>Ошибка: {error}</p>;

  return (
    <ul>
      {currencies.map((currency) => (
        <li key={currency.id}>{currency.full_name }{currency.rub_rate}</li>
      ))}
    </ul>
  );

}

export default Currencies;