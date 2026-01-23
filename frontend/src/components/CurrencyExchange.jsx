import React, {useState} from 'react';
import api from '../api/axios.js';
import {
    Alert,
    Box,
    Button,
    Grid,
    Input,
    TextField,
    Typography
} from "@mui/material"; // твой настроенный axios-инстанс

function CurrencyExchange() {
    const [from, setFrom] = useState(null);        // Исходная валюта
    const [to, setTo] = useState(null);            // Целевая валюта
    const [amount, setAmount] = useState(0);      // Сумма для конвертации
    const [result, setResult] = useState(null);     // Результат от сервера
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const handleExchange = async (e) => {
        e.preventDefault();

        // Базовая валидация
        if (!from.trim() || !to.trim() || !amount || parseFloat(amount) <= 0) {
            setError('Введите корректную сумму и выберите валюты');
            return;
        }

        setLoading(true);
        setError(null);
        setResult(null);

        try {
            const response = await api.get('/exchange', {
                params: {
                    from: from.toUpperCase(),
                    to: to.toUpperCase(),
                    amount: parseFloat(amount),  // можно отправить как число или строку
                },
            });

            setResult(response.data);
        } catch (err) {
            setError(err.response?.data?.error);
            // console.error(err);
        } finally {
            setLoading(false);
            setFrom('')
            setTo('')
            setAmount(0)
            setTimeout(()=>{
                setError(null)
            }, 2500)
        }
    };

    return (
        <Box mb={2}>
            <Grid container>
                <Grid size={12}>
                    <Box p={2}
                         mb={4}
                         sx={{backgroundColor: '#3d5afe'}}>
                        <Typography
                            component={'h2'}
                            textAlign={'left'}
                            fontSize={18}
                            color={'#fff'}
                        >Конвертер валют
                        </Typography>
                    </Box>
                </Grid>
            </Grid>
            <Grid container spacing={2} maxWidth={'xs'}>
                <Grid size={12}>
                    <Box pl={2} pr={2}>
                        <form onSubmit={handleExchange}>
                            <Grid container spacing={2}>
                                <Grid size={12}>
                                    <Box >
                                        <TextField
                                            fullWidth={true}
                                            type="number"
                                            value={amount}
                                            onChange={(e) => setAmount(e.target.value)}
                                            placeholder="Сумма"
                                            disabled={loading}
                                            label={"Сумма перевода"}
                                        />
                                    </Box>
                                </Grid>

                                <Grid size={{xs:12, sm:6, md:6}}>
                                    <Box>
                                        <TextField
                                            fullWidth={true}
                                            type="text"
                                            value={from}
                                            onChange={(e) => setFrom(e.target.value.toUpperCase())}
                                            placeholder="USD"
                                            maxLength="3"
                                            label={'Из валюты'}
                                            disabled={loading}
                                        />
                                    </Box>
                                </Grid>

                                <Grid size={{xs:12, sm:6, md:6}}>
                                    <Box>
                                        <TextField
                                            fullWidth={true}
                                            type="text"
                                            value={to}
                                            onChange={(e) => setTo(e.target.value.toUpperCase())}
                                            placeholder="EUR"
                                            maxLength="3"
                                            label={'В валюту'}
                                            disabled={loading}
                                        />
                                    </Box>
                                </Grid>
                                <Grid size={12} mt={2}>
                                    <Button
                                        fullWidth={true}
                                        variant={'contained'}
                                        type="submit"
                                        disabled={loading || !from || !to}
                                    >
                                        {loading ? 'Конвертируем...' : 'Конвертировать'}
                                    </Button>
                                </Grid>
                            </Grid>
                        </form>
                    </Box>

                </Grid>
                <Grid size={12}>
                    <Box p={2}>
                        {error &&
                            <Alert variant={'filled'}
                                   severity={'error'}>{error}</Alert>}
                    </Box>
                    <Box>
                        {result && (
                            <Box pb={3}>
                                <Typography
                                    variant={"h3"}
                                    fontSize={18}
                                    fontWeight={'bold'}>
                                    Результат конвертации:</Typography>
                                <Box>
                                    <strong>{result.amount.toFixed(2)} {result.baseCurrency.sign} ({result.baseCurrency.code})</strong>
                                    {' → '}
                                    <strong>{result.convertedAmount.toFixed(2)} {result.targetCurrency.sign} ({result.targetCurrency.code})</strong>
                                </Box>
                                <Box>
                                    Курс:
                                    1 {result.baseCurrency.code} = <strong>{result.rate.toFixed(4)}</strong> {result.targetCurrency.code}
                                </Box>
                                <Box style={{
                                    fontSize: '14px',
                                    color: '#555',
                                    marginTop: '10px'
                                }}>
                                    <Box>{result.baseCurrency.name}</Box>
                                    <Box>{result.targetCurrency.name}</Box>
                                </Box>
                            </Box>
                        )}
                    </Box>
                </Grid>
            </Grid>
        </Box>
    );
}

export default CurrencyExchange;