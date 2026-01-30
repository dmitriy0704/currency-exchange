import React, {useState} from 'react';
import api from '../api/axios.js';
import qs from 'qs';
import {Alert, Box, Button, Input, TextField, Typography} from "@mui/material";

function ExchangeRatesCreateForm({onSuccess}) {
    const [baseCode, setBaseCode] = useState('');
    const [targetCode, setTargetCode] = useState('');
    // const [rate, setRate] = useState(0);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null)

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!baseCode.trim() || !targetCode.trim()) {
            setError('Заполните оба поля');
            return;
        }

        setLoading(true);
        setError(null);

        try {
            await api.post(
                '/exchangeRates',
                qs.stringify({
                    baseCurrencyCode: baseCode.trim().toUpperCase(),
                    targetCurrencyCode: targetCode.trim().toUpperCase()
                    // rate: rate
                }),
                {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                }
            );

            setSuccess(true)
            setBaseCode('');
            setTargetCode('');
            // setRate(0)

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
                    >Добавление обменного курса
                    </Typography>
                </Box>

                <Box p={2}>

                    <form onSubmit={handleSubmit}>
                        <Box pb={2}>
                            <TextField
                                fullWidth={true}
                                type="text"
                                value={baseCode}
                                onChange={(e) => setBaseCode(e.target.value.toUpperCase())}
                                placeholder="Например: USD"
                                disabled={loading}
                                label={'Код базовой валюты'}
                            />
                        </Box>

                        <Box pb={2}>
                            <TextField
                                fullWidth={true}
                                type="text"
                                value={targetCode}
                                onChange={(e) => setTargetCode(e.target.value.toUpperCase())}
                                placeholder="Например: EUR"
                                // maxLength="3"
                                disabled={loading}
                                label={'Код целевой валюты'}
                            />
                        </Box>
                        <Box maxWidth={350} ml={'auto'} mr={'auto'} mt={2} mb={3}>

                            <Alert variant={'filled'} severity="info">
                                Обменный курс высчитывается <br/>
                                автоматически как кросс-курс на <br/>
                                основании отношения курса валют к курсу
                                рубля
                            </Alert>

                        </Box>

                        {/*<Box pb={2}>*/}
                        {/*    <TextField*/}
                        {/*        fullWidth={true}*/}
                        {/*        type="number"*/}
                        {/*        value={rate}*/}
                        {/*        onChange={(e) => setRate(e.target.value)}*/}
                        {/*        placeholder="Например: 100.0"*/}
                        {/*        maxLength="10"*/}
                        {/*        disabled={loading}*/}
                        {/*        label={'Отношения валют(double)'}*/}
                        {/*    />*/}
                        {/*</Box>*/}

                        <Box>
                            <Button
                                variant={'contained'}
                                type="submit"
                                disabled={loading || !baseCode.trim() || !targetCode.trim()}
                            >
                                {loading ? 'Создаётся...' : 'Создать обменный курс'}
                            </Button>
                        </Box>

                        <Box mt={2} mb={2} height={50}>
                            {success &&
                                <Alert
                                    variant={'filled'}
                                    severity="success">Курс обмена валют успешно создан</Alert>}
                            {error && <Alert variant={'filled'}
                                             severity="error">{error}</Alert>}
                        </Box>
                    </form>
                </Box>
        </Box>
    );
}

export default ExchangeRatesCreateForm;