import React, { useState } from "react";
import api from "../api/axios.js"; // твой настроенный axios
import Currencies from "./Currencies.jsx";
import { Box, Button, Grid, Input, Typography } from "@mui/material";
import CurrencyCreateForm from "./CurrencyCreateForm.jsx";

function Dashboard() {
  const [currencies, setCurrencies] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [code, setCode] = useState(""); // Значение из input
  const [isSearching, setIsSearching] = useState(false); // Чтобы знать, в каком мы режиме

  //-> Загрузка списка валют
  const loadAllCurrencies = async () => {
    setLoading(true);
    setError(null);
    try {
      const currenciesRes = await api.get("/currencies");
      setCurrencies(currenciesRes.data);
      // setProducts(productsRes.data);
      // setStats(statsRes.data);
    } catch (err) {
      setError(err.response?.data?.message || err.message || "Ошибка загрузки");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  //-> Поиск валюты по коду
  const handleLoadData = async () => {
    // Проверка, что поле не пустое
    if (!code.trim()) {
      setError("Введите код валюты");
      return;
    }

    setLoading(true);
    setError(null);
    setIsSearching(true);
    setCurrencies(null); // опционально: очистить предыдущие данные

    try {
      const res = await api.get(`/currency/${code.toUpperCase()}`);
      setCurrencies([res.data]);
      setCode("");
    } catch (err) {
      if (err.response?.status === 404) {
        setError(`Валюта с кодом "${code}" не найдена`);
        setCurrencies([]);
      } else {
        setError(err.response?.data?.message || "Не удалось загрузить данные");
      }
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    setCode(e.target.value.toUpperCase()); // удобно сразу в верхний регистр
    setError(null); // сбрасываем ошибку при вводе
  };

  //-> Создание новой валюты
  const handleCurrencyCreated = () => {
    loadAllCurrencies();
  };

  // // Опционально: возможность отправки по Enter
  // const handleKeyPress = (e) => {
  //     if (e.key === 'Enter') {
  //         handleLoadData();
  //     }
  // };

  const resetSearch = () => {
    setCode("");
    setError(null);
    loadAllCurrencies();
    setIsSearching(false)
  };

  return (
    <div>
      <Typography variant={"h1"} fontSize={24} fontWeight={"bold"}>
        Обмен валют
      </Typography>

      {error && <p style={{ color: "red" }}>Ошибка: {error}</p>}

      <Grid container width={"xl"}>
        <Grid size={3}>
          <Box width={300}>
            <Button
              variant={"contained"}
              sx={{ width: 300 }}
              onClick={loadAllCurrencies}
              disabled={loading}
            >
              {loading ? "Загружается..." : "Обновить список валют"}
            </Button>
          </Box>
        </Grid>
        <Grid size={9}>

          <Box>
            <CurrencyCreateForm onSuccess={handleCurrencyCreated} />
          </Box>

          <Box>
            <Input
              fullWidth={true}
              type="text"
              value={code}
              onChange={handleInputChange}
              // onKeyPress={handleKeyPress}
              placeholder="Введите код валюты (например, USD)"
              style={{
                padding: "8px",
                width: "200px",
                textTransform: "uppercase",
              }}
            />
            <Button
              variant={"contained"}
              onClick={handleLoadData}
              disabled={loading || !code.trim()}
              style={{ marginLeft: "10px", padding: "8px 16px" }}
            >
              {loading ? "Загружается..." : "Найти"}
            </Button>
            {isSearching && (
              <Button
                variant={"text"}
                onClick={resetSearch}
                style={{ marginLeft: "10px" }}
              >
                Сбросить поиск
              </Button>
            )}
          </Box>
          <Box>
            {!loading && currencies.length === 0 && !error && (
              <p>Нет данных для отображения</p>
            )}

            {!loading && currencies.length > 0 && (
              <Box>
                <h3>
                  {isSearching
                    ? `Найдена валюта (${currencies.length})`
                    : `Все валюты (${currencies.length})`}
                </h3>
              </Box>
            )}

            <Currencies
              currencies={currencies}
              loading={loading}
              error={error}
            />
          </Box>
        </Grid>
      </Grid>
    </div>
  );
}

export default Dashboard;
