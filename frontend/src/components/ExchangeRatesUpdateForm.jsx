import React, {useState} from 'react';
import api from '../api/axios.js';
import qs from 'qs';
import {Alert, Box, Button, Input, TextField, Typography} from "@mui/material";

function ExchangeRatesUpdateForm({onSuccess}) {
    const [rate, setRate] = useState(0);

    const [codesExchangeRate, setCodesExchangeRate] = useState('')

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null)

    const handleSubmit = async (e) => {
        e.preventDefault();


        setLoading(true);
        setError(null);

        try {
            await api.patch(
                `/exchangeRate/${codesExchangeRate.toUpperCase()}`,
                qs.stringify({
                    rate: rate
                }),
                {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                }
            );

            setSuccess(true)
            setCodesExchangeRate('')
            setRate(0)

            // Если нужно обновить список
            if (onSuccess) onSuccess();
            setTimeout(() => {
                setSuccess(false);
            }, 2500);
        } catch (err) {
            setError(err.response?.data?.error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box mb={2}>
            <Box p={2}
                 mb={2}
                 sx={{backgroundColor: '#3d5afe'}}>
                <Typography
                    component={'h2'}
                    textAlign={'left'}
                    fontSize={18}
                    color={'#fff'}
                >Обновление обменного курса
                </Typography>
            </Box>

            <Box p={2}>
                <form onSubmit={handleSubmit}>
                    <Box pb={2}>
                        <TextField
                            fullWidth={true}
                            type="text"
                            value={codesExchangeRate}
                            onChange={(e) => setCodesExchangeRate(e.target.value.toUpperCase())}
                            placeholder="Например: USDEUR"
                            disabled={loading}
                            label={'Код  валютной пары'}
                        />
                    </Box>
                    <Box pb={2}>
                        <TextField
                            fullWidth={true}
                            type="number"
                            value={rate}
                            onChange={(e) => setRate(e.target.value)}
                            placeholder="Например: 100.0"
                            maxLength="10"
                            disabled={loading}
                            label={'Отношения валют(double)'}
                        />
                    </Box>
                    <Box>
                        <Button
                            variant={'contained'}
                            type="submit"
                            disabled={loading || !codesExchangeRate.trim()}
                        >
                            {loading ? 'Обновляется...' : 'Обновить обменный курс'}
                        </Button>
                    </Box>
                    <Box mt={2} mb={2} height={50}>
                        {success &&
                            <Alert
                                variant={'filled'}
                                severity="success">Курс обмена валют успешно
                                обновлен</Alert>}
                        {error && <Alert variant={'filled'}
                                         severity="error">{error}</Alert>}
                    </Box>
                </form>
            </Box>
        </Box>
    );
}

export default ExchangeRatesUpdateForm;