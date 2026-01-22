import React, {useState, useEffect} from 'react';
import ReactJsonView from '@microlink/react-json-view'

// import api from '../api/axios';
import {Box, Grid, Paper, Typography} from "@mui/material";
import {DataGrid} from '@mui/x-data-grid';


const columns = [
    {field: 'id', headerName: 'ID', width: 70},
    {field: 'code', headerName: 'Код валюты', width: 100},
    {
        field: 'name',
        headerName: 'Национальность валюты',
        description: 'This column has a value getter and is not sortable.',
        sortable: false,
        width: 200,
    },
    {
        field: 'rub_rate',
        headerName: 'Курс рубля',
        type: 'number',
        width: 100,
    },
    {field: 'sign', headerName: 'Знак валюты', width: 150},
];

const paginationModel = {page: 0, pageSize: 5};


function Currencies({currencies, loading, error, isSearching}) {
    if (loading) return <p>Загрузка списка валют...</p>;
    if (!currencies || currencies.length === 0) return <Typography variant={'body1'}>Список валют не загружен</Typography>;
    if (loading) return <p>Загрузка...</p>;
    if (error) return <p>Ошибка: {error}</p>;


    return (

            <Grid container>
                <Grid size={12}>
                    {!loading && currencies.length > 0 && (
                        <Box>
                            <Typography component={'h6'}>
                                {isSearching
                                    ? `Найдена валюта (${currencies.length})`
                                    : `Все валюты (${currencies.length})`}
                            </Typography>
                        </Box>
                    )}
                </Grid>
                <Grid size={12}>
                    <Box className={"currencies-table"}>
                        <Paper sx={{
                            maxHeight: 400,
                            width: '100%',
                            border: '1px solid #aaa'
                        }}>
                            <DataGrid
                                label={'LABEL'}
                                rows={currencies}
                                columns={columns}
                                initialState={{pagination: {paginationModel}}}
                                pageSizeOptions={[5, 10]}
                                checkboxSelection={false}
                                sx={{border: 0}}
                            />
                        </Paper>
                    </Box>
                </Grid>
                {/*<Grid size={5} p={2}>*/}
                {/*    <Box>*/}
                {/*        <Box sx={{textAlign: 'left'}}>*/}
                {/*            <ReactJsonView*/}
                {/*                src={currencies}*/}
                {/*                theme="rjv-default"*/}
                {/*                displayObjectSize={true}*/}
                {/*                collapsed={false}*/}
                {/*                style={{*/}
                {/*                    maxHeight: 370,*/}
                {/*                    overflow: "auto",*/}
                {/*                    fontSize: 14,*/}
                {/*                    border: '1px solid #cccccc'*/}
                {/*                }}*/}
                {/*            />*/}
                {/*        </Box>*/}
                {/*    </Box>*/}
                {/*</Grid>*/}
            </Grid>
    );
}

export default Currencies;