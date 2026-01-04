import React, {useState} from 'react';
import api from '../api/axios.js';
import qs from 'qs';
import {Button, Input, Typography} from "@mui/material";

function CurrencyCreateForm({onSuccess}) {
    const [name, setName] = useState('');
    const [code, setCode] = useState('');
    const [rub_rate, setRub_rate] = useState(0);
    const [sign, setSign] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

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

            // alert('Валюта создана!');
            setName('');
            setCode('');
            setRub_rate(0)
            setSign('')

            // Если нужно обновить список
            if (onSuccess) onSuccess();

        } catch (err) {
            setError(err.response?.data?.message || 'Не удалось создать валюту');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{
            margin: '20px 0',
            padding: '20px',
            border: '1px solid #ccc',
            borderRadius: '8px'
        }}>
            <Typography
                variant={'h3'}
                fontSize={18}
                fontWeight={'bold'}>
                Создать новую валюту
            </Typography>

            <form onSubmit={handleSubmit}>
                <div style={{marginBottom: '10px'}}>
                    <label>
                        Название валюты:<br/>
                        <Input
                            fullWidth={true}
                            type="text"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            placeholder="Например: Доллар США"
                            disabled={loading}
                        />
                    </label>
                </div>

                <div style={{marginBottom: '10px'}}>
                    <label>
                        Код валюты:<br/>
                        <Input
                            fullWidth={true}
                            type="text"
                            value={code}
                            onChange={(e) => setCode(e.target.value.toUpperCase())}
                            placeholder="Например: USD"
                            maxLength="3"
                            style={{
                                padding: '8px',
                                textTransform: 'uppercase'
                            }}
                            disabled={loading}
                        />
                    </label>
                </div>


                <div style={{marginBottom: '10px'}}>
                    <label>
                        Отношение к рублю(double):<br/>
                        <Input
                            fullWidth={true}
                            type="number"
                            value={rub_rate}
                            onChange={(e) => setRub_rate(e.target.value)}
                            placeholder="Например: 100.0"
                            maxLength="10"
                            disabled={loading}
                        />
                    </label>
                </div>

                <div style={{marginBottom: '10px'}}>
                    <label>
                        Sign валюты:<br/>
                        <Input
                            fullWidth={true}
                            type="text"
                            value={sign}
                            onChange={(e) => setSign(e.target.value.toUpperCase())}
                            placeholder="Например: $"
                            maxLength="3"
                            disabled={loading}
                        />
                    </label>
                </div>

                {error && <p style={{color: 'red'}}>{error}</p>}

                <Button
                    fullWidth={true}
                    variant={'contained'}
                    type="submit"
                    disabled={loading || !name.trim() || !code.trim()}
                    style={{padding: '10px 20px'}}
                >
                    {loading ? 'Создаётся...' : 'Создать валюту'}
                </Button>
            </form>
        </div>
    );
}

export default CurrencyCreateForm;