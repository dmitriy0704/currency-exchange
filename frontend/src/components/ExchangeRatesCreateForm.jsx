import React, {useState} from 'react';
import api from '../api/axios.js';
import qs from 'qs';
import {Box, Button, Input, TextField, Typography} from "@mui/material";

function ExchangeRatesCreateForm({onSuccess}) {
    const [baseCode, setBaseCode] = useState('');
    const [targetCode, setTargetCode] = useState('');
    // const [rate, setRate] = useState(0);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

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

            // alert('Валюта создана!');
            setBaseCode('');
            setTargetCode('');
            // setRate(0)

            // Если нужно обновить список
            if (onSuccess) onSuccess();

        } catch (err) {
            setError(err.response?.data?.message || 'Не удалось создать валюту');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box mb={2} mt={3}>
            <Typography
                mt={3}
                mb={2}
                variant={'h3'}
                fontSize={18}
                fontWeight={'bold'}>
                Создать новый курс обмена
            </Typography>
            <form onSubmit={handleSubmit}>
                <Box pb={2}>
                    <TextField
                        fullWidth={true}
                        type="text"
                        value={baseCode}
                        onChange={(e) => setBaseCode(e.target.value)}
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
                        onChange={(e) => setTargetCode(e.target.value)}
                        placeholder="Например: EUR"
                        // maxLength="3"
                        disabled={loading}
                        label={'Код целевой валюты'}

                    />
                </Box>
                <Box>
                    <Typography variant={'body1'}>
                        Обменный курс высчитывается <br/>
                        автоматически как кросс-курс на <br/>
                        основании отношения курса валют к курсу
                        рубля</Typography>
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


                {error && <p style={{color: 'red'}}>{error}</p>}

                <Button
                    fullWidth={true}
                    variant={'contained'}
                    type="submit"
                    disabled={loading || !baseCode.trim() || !targetCode.trim()}
                    style={{padding: '10px 20px'}}
                >
                    {loading ? 'Создаётся...' : 'Создать курс обмена'}
                </Button>
            </form>
        </Box>
    );
}

export default ExchangeRatesCreateForm;