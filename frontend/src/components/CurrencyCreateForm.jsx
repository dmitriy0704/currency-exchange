import React, {useState} from 'react';
import api from '../api/axios.js';
import qs from 'qs';
import {Alert, Box, Button, Input, TextField, Typography} from "@mui/material";

function CurrencyCreateForm({onSuccess}) {
    const [name, setName] = useState('');
    const [code, setCode] = useState('');
    const [rub_rate, setRub_rate] = useState(0);
    const [sign, setSign] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null)

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!name.trim() || !code.trim()) {
            setError('Заполните оба поля');
            return;
        }

        setLoading(true);
        setError(null);

        try {
            await api.post(
                '/currency',
                qs.stringify({
                    name: name.trim(),
                    code: code.trim().toUpperCase(),
                    rub_rate: rub_rate,
                    sign: sign.trim()
                }),
                {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                }
            );
            setSuccess(true);
            setName('');
            setCode('');
            setRub_rate(0)
            setSign('')


            // Если нужно обновить список
            if (onSuccess) onSuccess();

            setTimeout(() => {
                setSuccess(false);
            }, 2000);
        } catch (err) {
            setError(err.response?.data?.error);
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
                Создать новую валюту
            </Typography>
            <form onSubmit={handleSubmit}>
                <Box pb={2}>
                    <TextField
                        fullWidth={true}
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        placeholder="Например: Доллар США"
                        disabled={loading}
                        label={'Национальность валюты'}

                    />
                </Box>

                <Box pb={2}>
                    <TextField
                        fullWidth={true}
                        type="text"
                        value={code}
                        onChange={(e) => setCode(e.target.value.toUpperCase())}
                        placeholder="Например: USD"
                        maxLength="3"
                        disabled={loading}
                        label={'Код валюты'}
                    />
                </Box>


                <Box pb={2}>
                    <TextField
                        fullWidth={true}
                        type="number"
                        value={rub_rate}
                        onChange={(e) => setRub_rate(e.target.value)}
                        placeholder="Например: 100.0"
                        maxLength="10"
                        disabled={loading}
                        label={'Отношение к рублю(double)'}
                    />
                </Box>

                <Box pb={2}>
                    <TextField
                        fullWidth={true}
                        type="text"
                        value={sign}
                        onChange={(e) => setSign(e.target.value.toUpperCase())}
                        placeholder="Например: $"
                        maxLength="3"
                        disabled={loading}
                        label={'Sign валюты'}

                    />
                </Box>

                <Button
                    fullWidth={true}
                    variant={'contained'}
                    type="submit"
                    disabled={loading || !name.trim() || !code.trim()|| !rub_rate || !sign}
                    style={{padding: '10px 20px'}}
                >
                    {loading ? 'Создаётся...' : 'Создать валюту'}
                </Button>

                <Box mt={2} mb={2} height={50}>
                    {success &&
                        <Alert variant={'filled'} severity="success">Валюта создана</Alert>}
                    {error && <Alert variant={'filled'} severity="error">{error}</Alert>}
                </Box>

            </form>
        </Box>
    );
}

export default CurrencyCreateForm;