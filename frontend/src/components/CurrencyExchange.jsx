import React, {useState} from 'react';
import api from '../api/axios.js';
import {Box, Button, Grid, Input, TextField, Typography} from "@mui/material"; // твой настроенный axios-инстанс

function CurrencyExchange() {
    const [from, setFrom] = useState(null);        // Исходная валюта
    const [to, setTo] = useState(null);            // Целевая валюта
    const [amount, setAmount] = useState('0');      // Сумма для конвертации
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
            setError(
                err.response?.data?.error
            );
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box mt={5}>
            <Grid container spacing={2} maxWidth={'xs'}>
                <Grid size={12}>
                    <Box>
                        <Typography
                            variant={'h2'}
                            fontSize={24}
                            fontWeight={'bold'}>
                            Конвертер валют</Typography>
                    </Box>
                </Grid>


                <Grid size={12}>

                    <Box>
                        <form onSubmit={handleExchange}>

                            <Grid container spacing={2}>
                                <Grid size={12}>
                                    <Box mb={2} mt={2}>
                                        <TextField
                                            fullWidth={true}
                                            type="number"
                                            value={amount}
                                            onChange={(e) => setAmount(e.target.value)}
                                            placeholder="Сумма"
                                            disabled={loading}
                                            required={true}
                                            label={"Сумма перевода"}
                                        />
                                    </Box>
                                </Grid>

                                <Grid size={6}>
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
                                            required
                                        />
                                    </Box>
                                </Grid>

                                <Grid size={6}>
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
                                            required
                                        />
                                    </Box>
                                </Grid>


                                <Grid size={12}>
                                    <Button
                                        fullWidth={true}
                                        variant={'contained'}
                                        type="submit"
                                        disabled={loading}
                                    >
                                        {loading ? 'Конвертируем...' : 'Конвертировать'}
                                    </Button>
                                </Grid>
                            </Grid>
                        </form>
                    </Box>

                </Grid>


            </Grid>


            {error && <p style={{color: 'red', marginTop: '15px'}}>{error}</p>}

            {result && (
                <div style={{
                    marginTop: '25px',
                    padding: '15px',
                    background: '#f8f9fa',
                    borderRadius: '8px'
                }}>
                    <h3>Результат конвертации:</h3>
                    <p>
                        <strong>{result.amount.toFixed(2)} {result.baseCurrency.sign} ({result.baseCurrency.code})</strong>
                        {' → '}
                        <strong>{result.convertedAmount.toFixed(2)} {result.targetCurrency.sign} ({result.targetCurrency.code})</strong>
                    </p>
                    <p>
                        Курс:
                        1 {result.baseCurrency.code} = <strong>{result.rate.toFixed(4)}</strong> {result.targetCurrency.code}
                    </p>
                    <div style={{
                        fontSize: '14px',
                        color: '#555',
                        marginTop: '10px'
                    }}>
                        <p>{result.baseCurrency.name}</p>
                        <p>{result.targetCurrency.name}</p>
                    </div>
                </div>
            )}
        </Box>
    );
}

export default CurrencyExchange;