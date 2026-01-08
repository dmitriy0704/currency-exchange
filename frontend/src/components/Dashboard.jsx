import React, {useState} from "react";
import api from "../api/axios.js"; // твой настроенный axios
import Currencies from "./Currencies.jsx";
import {Box, Button, Grid, Input, Typography} from "@mui/material";
import CurrencyCreateForm from "./CurrencyCreateForm.jsx";
import ExchangeRates from "./ExchangeRates.jsx";

function Dashboard() {
    const [currencies, setCurrencies] = useState([]);
    const [code, setCode] = useState(""); // Значение из input
    const [isSearching, setIsSearching] = useState(false); // Чтобы знать, в каком мы режиме
    const [loadingCurrencies, setLoadingCurrencies] = useState(false);
    const [errorCurrencies, setErrorCurrencies] = useState(null);

    const [exchange_rates, setExchange_rates] = useState([])
    const [loadingExchangeRates, setLoadingExchangeRates] = useState(false);
    const [errorExchangeRates, setErrorExchangeRates] = useState(null);



    //-> Загрузка списка валют
    const loadAllCurrencies = async () => {
        setLoadingCurrencies(true);
        setErrorCurrencies(null);
        try {
            const currenciesRes = await api.get("/currencies");
            setCurrencies(currenciesRes.data);
        } catch (err) {
            setErrorCurrencies(err.response?.data?.message || err.message || "Ошибка загрузки");
            console.error(err);
        } finally {
            setLoadingCurrencies(false);
        }
    };

    //-> Поиск валюты по коду
    const handleCurrenciesLoadData = async () => {
        // Проверка, что поле не пустое
        if (!code.trim()) {
            setErrorCurrencies("Введите код валюты");
            return;
        }

        setLoadingCurrencies(true);
        setErrorCurrencies(null);
        setIsSearching(true);
        setCurrencies(null); // опционально: очистить предыдущие данные

        try {
            const res = await api.get(`/currency/${code.toUpperCase()}`);
            setCurrencies([res.data]);
            setCode("");
        } catch (err) {
            if (err.response?.status === 404) {
                setErrorCurrencies(`Валюта с кодом "${code}" не найдена`);
                setCurrencies([]);
            } else {
                setErrorCurrencies(err.response?.data?.message || "Не удалось загрузить данные");
            }
            console.error(err);
        } finally {
            setLoadingCurrencies(false);
        }
    };

    const handleInputChange = (e) => {
        setCode(e.target.value.toUpperCase()); // удобно сразу в верхний регистр
        setErrorCurrencies(null); // сбрасываем ошибку при вводе
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
        setErrorCurrencies(null);
        loadAllCurrencies();
        setIsSearching(false)
    };


    //-> Загрузка списка валют
    const loadAllExchangeRates = async () => {
        setLoadingExchangeRates(true);
        setErrorExchangeRates(null);
        try {
            const exchangeRatesRes = await api.get("/exchangeRates");
            setExchange_rates(exchangeRatesRes.data);
        } catch (err) {
            setErrorExchangeRates(err.response?.data?.message || err.message || "Ошибка загрузки");
            console.error(err);
        } finally {
            setLoadingExchangeRates(false);
        }
    };


    return (
        <Box>
            <Typography variant={"h1"} fontSize={24} fontWeight={"bold"}>
                Обмен валют
            </Typography>

            {errorCurrencies && <p style={{color: "red"}}>Ошибка: {errorCurrencies}</p>}

            <Grid container width={"xl"}>
                <Grid size={12}>
                    <Box width={300}>
                        <Button
                            variant={"contained"}
                            sx={{width: 300}}
                            onClick={loadAllCurrencies}
                            disabled={loadingCurrencies}
                        >
                            {loadingCurrencies ? "Загружается..." : "Обновить список валют"}
                        </Button>

                        <Button
                            variant={"contained"}
                            sx={{width: 300}}
                            onClick={loadAllExchangeRates}
                            disabled={loadingCurrencies}
                        >
                            {loadingExchangeRates ? "Загружается..." : "Обновить список обменных валют"}
                        </Button>
                    </Box>
                </Grid>
                <Grid size={12}>

                    <Box>
                        <CurrencyCreateForm onSuccess={handleCurrencyCreated}/>
                    </Box>
                    <Box>
                        <Typography variant={'h2'} fontWeight={'bold'}
                                    fontSize={18} mt={4} mb={2}>
                            Поиск по коду валюты
                        </Typography>
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
                            onClick={handleCurrenciesLoadData}
                            disabled={loadingCurrencies || !code.trim()}
                            style={{marginLeft: "10px", padding: "8px 16px"}}
                        >
                            {loadingCurrencies ? "Загружается..." : "Найти"}
                        </Button>
                        {isSearching && (
                            <Button
                                variant={"text"}
                                onClick={resetSearch}
                                style={{marginLeft: "10px"}}
                            >
                                Сбросить поиск
                            </Button>
                        )}
                    </Box>
                    <Box>
                        <Currencies
                            currencies={currencies}
                            loading={loadingCurrencies}
                            error={errorCurrencies}
                            isSearching={isSearching}
                        />
                    </Box>
                </Grid>
            </Grid>
            <Grid size={12}>
                <ExchangeRates
                    exchange_rates={exchange_rates}
                    loading={loadingExchangeRates}
                    error={errorExchangeRates}
                />
            </Grid>
        </Box>
    );
}

export default Dashboard;
