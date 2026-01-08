import React, {useState, useEffect} from 'react';
import ReactJsonView from '@microlink/react-json-view'

// import api from '../api/axios';
import {Box, Grid, Paper, Typography} from "@mui/material";
import {DataGrid} from '@mui/x-data-grid';



const columns = [
    {field: 'id', headerName: 'ID', width: 70},
    {field: 'rate', headerName: 'Курс обмена валют', width: 150},
    {
        field: 'baseCurrency-id',
        headerName: 'ID базовой валюты',
        description: 'description.',
        sortable: false,
        width: 170,
        valueGetter: (value, row) => `${row.baseCurrency?.id || ''}`,

    },
    {
        field: 'baseCurrency-code',
        headerName: 'Код базовой валюты',
        description: 'description.',
        sortable: false,
        width: 170,
        valueGetter: (value, row) => `${row.baseCurrency?.code || ''}`,

    },
    {
        field: 'baseCurrency-name',
        headerName: 'Национальность базовой валюты',
        description: 'description.',
        sortable: false,
        width: 250,
        valueGetter: (value, row) => `${row.baseCurrency?.name || ''}`,

    },
    {
        field: 'baseCurrency-rub_curr',
        headerName: 'Курс базовой валюты',
        description: 'description.',
        sortable: false,
        width: 170,
        valueGetter: (value, row) => `${row.baseCurrency?.rub_rate || ''}`,
    },
    {
        field: 'targetCurrency-id',
        headerName: 'ID целевой валюты',
        description: 'description.',
        sortable: false,
        width: 170,
        valueGetter: (value, row) => `${row.targetCurrency?.id || ''}`,

    },
    {
        field: 'targetCurrency-code',
        headerName: 'Код целевой валюты',
        description: 'description.',
        sortable: false,
        width: 170,
        valueGetter: (value, row) => `${row.targetCurrency?.code || ''}`,

    },
    {
        field: 'targetCurrency-name',
        headerName: 'Национальность целевой валюты',
        description: 'description.',
        sortable: false,
        width: 170,
        valueGetter: (value, row) => `${row.targetCurrency?.name || ''}`,

    },
    {
        field: 'targetCurrency-rub_rate',
        headerName: 'Курс целевой валюты',
        description: 'description.',
        sortable: false,
        width: 170,
        valueGetter: (value, row) => `${row.targetCurrency?.rub_rate || ''}`,
    },
];

const paginationModel = {page: 0, pageSize: 5};


function ExchangeRates({exchangeRates, loadingExchangeRates, errorExchangeRates}) {
    if (loadingExchangeRates) return <p>Загрузка списка курсов обмена валют...</p>;
    if (!exchangeRates || exchangeRates.length === 0) return <p>Список курсов обмена валют не загружен</p>;
    if (loadingExchangeRates) return <p>Загрузка...</p>;
    if (errorExchangeRates) return <p>Ошибка: {errorExchangeRates}</p>;

    return (
        <Box>
            <Grid container>

                <Grid size={12}>
                    <Box>
                        <Typography variant={'h2'} fontWeight={'bold'}
                                    fontSize={18} mt={4} mb={2}>
                            Курсы обмена валют
                        </Typography>
                    </Box>
                </Grid>

                <Grid size={8} p={2}>

                    <Box>
                        <Paper sx={{
                            maxHeight: 400,
                            width: '100%',
                            border: '1px solid #cccccc'
                        }}>
                            <DataGrid
                                label={'LABEL'}
                                rows={exchangeRates}
                                columns={columns}
                                initialState={{pagination: {paginationModel}}}
                                pageSizeOptions={[5, 10]}
                                checkboxSelection
                                sx={{border: 0}}
                            />
                        </Paper>
                    </Box>
                </Grid>
                <Grid size={4} p={2}>
                    <Box>
                        <Box sx={{textAlign: 'left'}}>
                            <ReactJsonView
                                src={exchangeRates}
                                theme="rjv-default"
                                displayObjectSize={true}
                                collapsed={false}
                                style={{
                                    maxHeight: 370,
                                    overflow: "auto",
                                    fontSize: 14,
                                    border: '1px solid #cccccc'
                                }}
                            />
                        </Box>
                    </Box>
                </Grid>
            </Grid>
        </Box>
    );

}

export default ExchangeRates;