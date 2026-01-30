import React, {useEffect, useState} from "react";
import api from "../api/axios.js";
import Currencies from "./Currencies.jsx";
import {
    Alert,
    Box,
    Button, Container,
    FormHelperText,
    Grid, IconButton,
    Input, InputAdornment, Popover, Tooltip,
    Typography
} from "@mui/material";
import CurrencyCreateForm from "./CurrencyCreateForm.jsx";
import ExchangeRates from "./ExchangeRates.jsx";
import ExchangeRatesCreateForm from "./ExchangeRatesCreateForm.jsx";
import CurrencyExchange from "./CurrencyExchange.jsx";
import InfoIcon from "@mui/icons-material/Info";
import ClearIcon from '@mui/icons-material/Clear';
import ExchangeRatesUpdateForm from "./ExchangeRatesUpdateForm.jsx";

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
            // console.error(err);
        } finally {
            setLoadingCurrencies(false);
        }
    };

    useEffect(() => {
        loadAllCurrencies();
        loadAllExchangeRates()
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
                setErrorCurrencies(err.response?.data?.error || "Не удалось загрузить данные");
            }
            // console.error(err);
        } finally {
            setLoadingCurrencies(false);
        }
    };

    const handleInputChangeCurrency = (e) => {
        setCodeCurrency(e.target.value.toUpperCase());
        setErrorCurrencies(null); //-> сбрасываем ошибку при вводе
    };

    //-> Создание новой валюты
    const handleCurrencyCreated = () => {
        loadAllCurrencies();
    };


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
            // console.error(err);
        } finally {
            setLoadingExchangeRates(false);
        }
    };


    //-> Поиск обменного курса по коду валютной пары
    const handleInputChangeExchangeRates = (e) => {
        setCodesExchangeRate(e.target.value.toUpperCase());
        setErrorExchangeRates(null); // сбрасываем ошибку при вводе
        if (e.target.value.length > 6) {
            setErrorExchangeRates("Длина значения не равна 6")
        }
    };

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
            if (err.response?.data?.error != null) {
                setErrorExchangeRates(err.response?.data?.error);
                setExchangeRates([]);
            }
        } finally {
            setLoadingExchangeRates(false);
        }
    };


    const resetSearchExchangeRates = () => {
        setCodesExchangeRate("")
        setExchangeRates("");
        setErrorExchangeRates(null);
        loadAllExchangeRates();
        setIsSearchingExchangeRates(false)
    };


    //-> Создание нового обменного курса
    const handleExchangeRatesCreated = () => {
        loadAllExchangeRates();
    };

    //-> Обновление обменного курса


    const handleExchangeRatesUpdated = () => {
        loadAllExchangeRates();
    };


    const [anchorEl1, setAnchorEl1] = React.useState(null);
    const [anchorEl2, setAnchorEl2] = React.useState(null);

    const handleClick1 = (event) => {
        setAnchorEl1(event.currentTarget);
    };
    const handleClick2 = (event) => {
        setAnchorEl2(event.currentTarget);
    };

    const handleClose1 = () => {
        setAnchorEl1(null);
    };

    const handleClose2 = () => {
        setAnchorEl2(null);
    };

    const open1 = Boolean(anchorEl1);
    const open2 = Boolean(anchorEl2);
    const id1 = open1 ? 'simple-popover1' : undefined;
    const id2 = open2 ? 'simple-popover2' : undefined;

    return (
        <Container
            maxWidth={'xl'}
            sx={{
                width: '100%',
            }}>
            <Grid container>
                <Grid size={12}>
                    <Box p={2} sx={{backgroundColor: '#2a3eb1'}}></Box>
                    <Box p={2}
                         sx={{
                             backgroundColor: '#3d5afe'
                         }}>
                        <Typography
                            variant={'h1'}
                            component={'h2'}
                            fontSize={24}
                            fontWeight={'bold'}
                            textAlign={'left'}
                            color={'#fff'}
                        >Обмен валют</Typography>
                    </Box>
                </Grid>
            </Grid>
            <Grid container spacing={2}>
                <Grid size={{xs:12, sm:12, md:8}}>
                    <Box mt={2}
                         sx={{
                             backgroundColor: '#fff',
                             border: '1px solid #aaa'
                         }}>
                        <Box p={2}
                             sx={{
                                 backgroundColor: '#3d5afe'
                             }}>
                            <Typography
                                component={'h2'}
                                textAlign={'left'}
                                fontSize={18}
                                color={'#fff'}
                            >Курсы валют по данным ЦБ РФ
                            </Typography>
                        </Box>
                        {/*        <Box sx={{backgroundColor: '#fff'}}>*/}
                        {/*            <Button*/}
                        {/*                variant={"contained"}*/}
                        {/*                onClick={loadAllCurrencies}*/}
                        {/*                disabled={loadingCurrencies}*/}
                        {/*            >*/}
                        {/*                {loadingCurrencies ? "Загружается..." : "Обновить список валют"}*/}
                        {/*            </Button>*/}
                        {/*        </Box>*/}
                        <Box p={2}>
                            <Typography
                                component={'h2'}
                                fontSize={18}
                                fontWeight={'bold'}
                            >Поиск валюты
                            </Typography>
                        </Box>
                        <Box p={2} maxWidth={350} ml={'auto'} mr={'auto'}>
                            <Input
                                fullWidth={true}
                                type="text"
                                value={codeCurrency}
                                onChange={handleInputChangeCurrency}
                                placeholder="Введите код валюты"
                                sx={{
                                    paddingLeft: 1,
                                    paddingRight: 1,
                                    marginTop: 2,
                                    marginBottom: 3,
                                    textTransform: "uppercase",
                                }}
                                endAdornment={<InputAdornment
                                    position="end">
                                    {isSearchingCurrency && (
                                        // <Button
                                        //     variant={"text"}
                                        //     onClick={resetSearch}
                                        //     style={{marginLeft: "10px"}}
                                        // >
                                        <ClearIcon onClick={resetSearch}
                                                   color={'error'}/>
                                        // </Button>
                                    )}

                                    {!isSearchingCurrency && (
                                        <InfoIcon aria-describedby={id1}
                                                  onClick={handleClick1}/>
                                    )}

                                </InputAdornment>}
                            />
                            <Popover
                                id={id1}
                                open={open1}
                                anchorEl={anchorEl1}
                                onClose={handleClose1}
                                anchorOrigin={{
                                    vertical: 'top',
                                    horizontal: 'right',
                                }}
                                transformOrigin={{
                                    vertical: 'bottom',
                                    horizontal: 'right',
                                }}
                            >
                                <Typography sx={{p: 2}}>Например
                                    USD</Typography>
                            </Popover>
                            <Button
                                variant={"contained"}
                                onClick={handleCurrenciesLoadData}
                                disabled={loadingCurrencies || !codeCurrency.trim()}
                            >
                                {loadingCurrencies ? "Загружается..." : "Найти"}
                            </Button>


                            {/*{isSearchingCurrency && (*/}
                            {/*    <Button*/}
                            {/*        variant={"text"}*/}
                            {/*        onClick={resetSearch}*/}
                            {/*        style={{marginLeft: "10px"}}*/}
                            {/*    >*/}
                            {/*        Сбросить поиск*/}
                            {/*    </Button>)}*/}
                            <Box mt={2}>
                                {errorNotFoundCurrencies &&
                                    <Alert variant={'filled'}
                                           severity="error">{errorNotFoundCurrencies}</Alert>
                                }
                            </Box>
                        </Box>
                        <Box p={2}>
                            <Currencies
                                currencies={currencies}
                                loading={loadingCurrencies}
                                error={errorCurrencies}
                                isSearching={isSearchingCurrency}
                            />
                        </Box>
                        {/*<Box>*/}
                        {/*    {errorCurrencies &&*/}
                        {/*        <p style={{color: "red"}}>Ошибка: {errorCurrencies}</p>}*/}
                        {/*</Box>*/}
                    </Box>
                </Grid>
                <Grid size={{xs:12, sm:12, md:4}}>
                    <Box mt={2} sx={{
                        backgroundColor: '#fff',
                        border: '1px solid #aaa'
                    }}>
                        <Box p={2}
                             sx={{backgroundColor: '#3d5afe'}}>
                            <Typography
                                component={'h2'}
                                textAlign={'left'}
                                fontSize={18}
                                color={'#fff'}
                            >Добавление новой валюты
                            </Typography>
                        </Box>
                        <Box p={2}>
                            <CurrencyCreateForm
                                onSuccess={handleCurrencyCreated}/>
                        </Box>

                    </Box>
                </Grid>
            </Grid>

            <Grid container mt={5} mb={10}
                  sx={{border: '1px solid #aaa'}}>
                <Grid size={12}>
                    <Box  pb={1}>
                        <Box p={2}
                             sx={{backgroundColor: '#3d5afe'}}>
                            <Typography
                                component={'h2'}
                                textAlign={'left'}
                                fontSize={18}
                                color={'#fff'}
                            >Обменные курсы валют
                            </Typography>
                        </Box>
                        <Box>
                            <Box maxWidth={350} ml={'auto'} mr={'auto'}>

                                <Typography variant={'h2'}
                                            fontWeight={'bold'}
                                            fontSize={18} mt={4} mb={2}>
                                    Поиск по коду обменных курсов валют
                                </Typography>

                                <Box>
                                    <Input
                                        fullWidth={true}
                                        type="text"
                                        value={codesExchangeRate}
                                        onChange={handleInputChangeExchangeRates}
                                        // onKeyPress={handleKeyPress}
                                        placeholder="USDEUR"
                                        sx={{
                                            padding: 1,
                                            marginBottom: 2,
                                            textTransform: "uppercase",
                                        }}
                                        endAdornment={<InputAdornment
                                            position="end">
                                            {isSearchingExchangeRates && (
                                                // <Button
                                                //     onClick={resetSearchExchangeRates}
                                                //     style={{marginLeft: "10px"}}
                                                // >
                                                <ClearIcon
                                                    onClick={resetSearchExchangeRates}
                                                    color={'error'}/>
                                                // </Button>
                                            )}

                                            {!isSearchingExchangeRates && (
                                                <InfoIcon aria-describedby={id2}
                                                          onClick={handleClick2}/>
                                            )}
                                        </InputAdornment>}

                                    />

                                    <Popover
                                        id={id2}
                                        open={open2}
                                        anchorEl={anchorEl2}
                                        onClose={handleClose2}
                                        anchorOrigin={{
                                            vertical: 'top',
                                            horizontal: 'right',
                                        }}
                                        transformOrigin={{
                                            vertical: 'bottom',
                                            horizontal: 'right',
                                        }}
                                    >
                                        <Typography sx={{p: 2}}>Например
                                            USDEUR</Typography>
                                    </Popover>
                                </Box>
                                <Box mt={2}>
                                    <Button
                                        variant={"contained"}
                                        onClick={handleExchangeRatesLoadData}
                                        disabled={loadingExchangeRates || !codesExchangeRate.trim() || errorExchangeRates}
                                    >
                                        {loadingExchangeRates ? "Загружается..." : "Найти"}
                                    </Button>
                                    {/*{isSearchingExchangeRates && (*/}
                                    {/*    <Button*/}
                                    {/*        variant={"text"}*/}
                                    {/*        onClick={resetSearchExchangeRates}*/}
                                    {/*        style={{marginLeft: "10px"}}*/}
                                    {/*    >*/}
                                    {/*        <ClearIcon/>*/}
                                    {/*    </Button>*/}
                                    {/*)}*/}
                                </Box>
                            </Box>
                            <Box maxWidth={350} ml={'auto'} mr={'auto'} mt={2}
                                 mb={2} height={50}>
                                {errorExchangeRates && <Alert
                                    variant={'filled'}
                                    severity="error">{errorExchangeRates}</Alert>}
                            </Box>

                            <Box>
                                <Typography variant={'h2'} fontWeight={'bold'}
                                            fontSize={18} mt={4} mb={2}>
                                    Все обменные курсы
                                </Typography>
                                {/*<Box>*/}
                                {/*    <Button*/}
                                {/*        fullWidth={true}*/}
                                {/*        variant={"contained"}*/}
                                {/*        onClick={loadAllExchangeRates}*/}
                                {/*        disabled={loadingCurrencies}*/}
                                {/*    >*/}
                                {/*        {loadingExchangeRates ? "Загружается..." : "Обновить список обменного курса валют"}*/}
                                {/*    </Button>*/}
                                {/*</Box>*/}
                                <ExchangeRates
                                    exchangeRates={exchangeRates}
                                    loadingExchangeRates={loadingExchangeRates}
                                    errorExchangeRates={errorExchangeRates}
                                />
                            </Box>
                        </Box>
                    </Box>
                </Grid>

                <Grid container size={12} spacing={2} p={2}>
                    <Grid size={{xs:12, sm:12, md:4}}>
                        <Box mt={3} sx={{
                            backgroundColor: '#fff',
                            border: '1px solid #aaa'
                        }}>
                            <ExchangeRatesCreateForm
                                onSuccess={handleExchangeRatesCreated}/>
                        </Box>
                    </Grid>

                    <Grid size={{xs:12, sm:12, md:4}}>
                        <Box mt={3} sx={{
                            backgroundColor: '#fff',
                            border: '1px solid #aaa'
                        }}>
                            <ExchangeRatesUpdateForm
                                onSuccess={handleExchangeRatesUpdated}/>
                        </Box>
                    </Grid>

                    <Grid size={{xs:12, sm:12, md:4}}>
                        <Box mt={3} sx={{
                            backgroundColor: '#fff',
                            border: '1px solid #aaa'
                        }}>
                            <CurrencyExchange/>
                        </Box>
                    </Grid>
                </Grid>
            </Grid>
        </Container>
    );
}

export default Dashboard;
