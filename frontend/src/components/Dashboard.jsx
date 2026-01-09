import React, {useEffect, useState} from "react";
import api from "../api/axios.js"; // твой настроенный axios
import Currencies from "./Currencies.jsx";
import {
    Alert,
    Box,
    Button,
    FormHelperText,
    Grid,
    Input,
    Typography
} from "@mui/material";
import CurrencyCreateForm from "./CurrencyCreateForm.jsx";
import ExchangeRates from "./ExchangeRates.jsx";
import ExchangeRatesCreateForm from "./ExchangeRatesCreateForm.jsx";
import CurrencyExchange from "./CurrencyExchange.jsx";

function Dashboard() {
    const [currencies, setCurrencies] = useState([]);
    const [codeCurrency, setCodeCurrency] = useState(""); // Значение из input
    const [isSearchingCurrency, setIsSearchingCurrency] = useState(false); // Чтобы знать, в каком мы режиме
    const [loadingCurrencies, setLoadingCurrencies] = useState(false);
    const [errorCurrencies, setErrorCurrencies] = useState(null);
    const [errorNotFoundCurrencies, setErrorNotFoundCurrencies] = useState(null);


    const [exchangeRates, setExchangeRates] = useState([])
    const [codesExchangeRate, setCodesExchangeRate] = useState(""); // Значение из input
    const [isSearchingExchangeRates, setIsSearchingExchangeRates] = useState(false); // Чтобы знать, в каком мы режиме
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

    useEffect(() => {
        loadAllCurrencies();
    }, []);




    //-> Поиск валюты по коду
    const handleCurrenciesLoadData = async () => {
        // Проверка, что поле не пустое
        if (!codeCurrency.trim()) {
            setErrorCurrencies("Введите код валюты");
            return;
        }

        setLoadingCurrencies(true);
        setErrorCurrencies(null);
        setIsSearchingCurrency(true);
        setCurrencies(null); // опционально: очистить предыдущие данные

        try {
            const res = await api.get(`/currency/${codeCurrency.toUpperCase()}`);
            setCurrencies([res.data]);
            setCodeCurrency("");
        } catch (err) {
            if (err.response?.status === 404) {
                setErrorNotFoundCurrencies(err.response?.data.error);
                setCurrencies([]);
            } else {
                setErrorCurrencies(err.response?.data?.message || "Не удалось загрузить данные");
            }
            console.error(err);
        } finally {
            setLoadingCurrencies(false);
        }
    };

    const handleInputChangeCurrency = (e) => {
        setCodeCurrency(e.target.value.toUpperCase()); // удобно сразу в верхний регистр
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
        setCodeCurrency("");
        setErrorNotFoundCurrencies(null);
        loadAllCurrencies();
        setIsSearchingCurrency(false)
    };

    // ======================================================================//
    // ======================================================================//
    // ======================================================================//

    //-> Загрузка обменного курса
    const loadAllExchangeRates = async () => {
        setLoadingExchangeRates(true);
        setErrorExchangeRates(null);
        try {
            const exchangeRatesRes = await api.get("/exchangeRates");
            setExchangeRates(exchangeRatesRes.data);
        } catch (err) {
            setErrorExchangeRates(err.response?.data?.message || err.message || "Ошибка загрузки");
            console.error(err);
        } finally {
            setLoadingExchangeRates(false);
        }
    };


    //-> Поиск валюты по коду
    const handleExchangeRatesLoadData = async () => {
        // Проверка, что поле не пустое
        if (!codesExchangeRate.trim()) {
            setErrorExchangeRates("Введите код обменного курса");
            return;
        }

        setLoadingExchangeRates(true);
        setErrorExchangeRates(null);
        setIsSearchingExchangeRates(true);
        setExchangeRates(null); // опционально: очистить предыдущие данные

        try {
            const res = await api.get(`/exchangeRate/${codesExchangeRate.toUpperCase()}`);
            setExchangeRates([res.data]);
            setCodesExchangeRate("");
        } catch (err) {
            if (err.response?.status === 404) {
                setErrorExchangeRates(`Валюта с кодом "${codeCurrency}" не найдена`);
                setExchangeRates([]);
            } else {
                setErrorExchangeRates(err.response?.data?.message || "Не удалось загрузить данные");
            }
            console.error(err);
        } finally {
            setLoadingExchangeRates(false);
        }
    };

    const handleInputChangeExchangeRates = (e) => {
        setCodesExchangeRate(e.target.value.toUpperCase()); // удобно сразу в верхний регистр
        setErrorExchangeRates(null); // сбрасываем ошибку при вводе
    };


    const resetSearchExchangeRates = () => {
        setExchangeRates("");
        setErrorExchangeRates(null);
        loadAllExchangeRates();
        setIsSearchingExchangeRates(false)
    };


    //-> Создание новой валюты
    const handleExchangeRatesCreated = () => {
        loadAllExchangeRates();
    };

    return (
        <Box>
            <Grid container width={"xl"}>
                <Grid size={12}>
                    <Box>
                        <Typography
                            variant={"h1"}
                            fontSize={24}
                            fontWeight={"bold"}
                            mb={5}
                        >
                            Обмен валют
                        </Typography>
                        {errorCurrencies &&
                            <p style={{color: "red"}}>Ошибка: {errorCurrencies}</p>}
                    </Box>
                </Grid>
                <Grid size={12}>
                    <Box>
                        <Button
                            fullWidth={true}
                            variant={"contained"}
                            onClick={loadAllCurrencies}
                            disabled={loadingCurrencies}
                        >
                            {loadingCurrencies ? "Загружается..." : "Обновить список валют"}
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
                            value={codeCurrency}
                            onChange={handleInputChangeCurrency}
                            // onKeyPress={handleKeyPress}
                            placeholder="Введите код валюты (например, USD)"
                            style={{
                                marginTop: 16,
                                marginBottom: 16,
                                textTransform: "uppercase",
                            }}
                        />
                        <Button
                            fullWidth={true}
                            variant={"contained"}
                            onClick={handleCurrenciesLoadData}
                            disabled={loadingCurrencies || !codeCurrency.trim()}
                        >
                            {loadingCurrencies ? "Загружается..." : "Найти"}
                        </Button>
                        {isSearchingCurrency && (
                            <Button
                                variant={"text"}
                                onClick={resetSearch}
                                style={{marginLeft: "10px"}}
                            >
                                Сбросить поиск
                            </Button>
                        )}

                        <Box>
                            {errorNotFoundCurrencies &&
                                <Alert severity="error">{errorNotFoundCurrencies}</Alert>
                            }
                        </Box>
                    </Box>
                    <Box>
                        <Currencies
                            currencies={currencies}
                            loading={loadingCurrencies}
                            error={errorCurrencies}
                            isSearching={isSearchingCurrency}
                        />
                    </Box>
                </Grid>
            </Grid>
            <Grid size={12}>
                <Box>
                    <Typography variant={'h2'} fontWeight={'bold'}
                                fontSize={24} mt={4} mb={2}>
                        Обменные курсы валют
                    </Typography>
                </Box>

                <Box>
                    <Button
                        fullWidth={true}
                        variant={"contained"}
                        onClick={loadAllExchangeRates}
                        disabled={loadingCurrencies}
                    >
                        {loadingExchangeRates ? "Загружается..." : "Обновить список обменного курса валют"}
                    </Button>
                </Box>
                <Box>
                    <ExchangeRatesCreateForm onSuccess={handleExchangeRatesCreated} />
                </Box>
                <Box>
                    <Typography variant={'h2'} fontWeight={'bold'}
                                fontSize={18} mt={4} mb={2}>
                        Поиск по коду курсов обмена валют(например USDEUR)
                    </Typography>
                    <Input
                        fullWidth={true}
                        type="text"
                        value={codesExchangeRate}
                        onChange={handleInputChangeExchangeRates}
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
                        onClick={handleExchangeRatesLoadData}
                        disabled={loadingExchangeRates || !codesExchangeRate.trim()}
                        style={{marginLeft: "10px", padding: "8px 16px"}}
                    >
                        {loadingExchangeRates ? "Загружается..." : "Найти"}
                    </Button>
                    {isSearchingExchangeRates && (
                        <Button
                            variant={"text"}
                            onClick={resetSearchExchangeRates}
                            style={{marginLeft: "10px"}}
                        >
                            Сбросить поиск
                        </Button>
                    )}
                </Box>
                <Box>
                    <ExchangeRates
                        exchangeRates={exchangeRates}
                        loadingExchangeRates={loadingExchangeRates}
                        errorExchangeRates={errorExchangeRates}
                    />
                </Box>
            </Grid>

            <Grid size={12}>
                <Box>
                    <CurrencyExchange/>
                </Box>
            </Grid>
        </Box>
    );
}

export default Dashboard;
