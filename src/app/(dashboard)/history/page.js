"use client";
import styles from "@/app/page.module.css";
import * as React from 'react';
import { useState } from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import { Button } from "@mui/material";
import DeleteIcon from '@mui/icons-material/Delete';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import ArrowDropUpIcon from '@mui/icons-material/ArrowDropUp';

function createData(id, date, category, description, amount) {
  return { id, date, category, description, amount };
}

export default function History() {
  const [rows, setRows] = useState([
    createData('1','01-05-2025', 'Продукты', 'Картошка для печи ууу как я люблю картошку для печи', 200),
    createData('2','22-04-2025', 'Зарплата', 'Аванс', 30000),
    createData('3','15-04-2025', 'Медицина', 'Нурофен', 450),
    createData('4','24-07-2025', 'Медицина', 'Нурофен', 450),
  ]);

  const filteredRows = [...rows].sort((a, b) => {
  const parseDate = (dateStr) => {
    const [day, month, year] = dateStr.split('-').map(Number);
    return new Date(year, month - 1, day);
  };
  
  const dateA = parseDate(a.date);
  const dateB = parseDate(b.date);
  
  return dateB - dateA;
});

  return (
    <div className={styles.table_container}>
      <div className={styles.table_header}>История операций</div>
      <TableContainer >
        <Table sx={{ minWidth: 650}}>
          <TableHead sx={{backgroundColor: "var(--foreground)"}}>
            <TableRow >
              <TableCell sx={{ color: "var(--font-color)", fontSize: "calc(10px + 1vmin)"}} align="left">Дата </TableCell>
              <TableCell sx={{ color: "var(--font-color)", fontSize: "calc(10px + 1vmin)"}} align="left">Категория </TableCell>
              <TableCell sx={{ color: "var(--font-color)", fontSize: "calc(10px + 1vmin)"}} align="left">Описание</TableCell>
              <TableCell sx={{ color: "var(--font-color)", fontSize: "calc(10px + 1vmin)"}} align="left">Значение </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {filteredRows.map((row) => (
              <TableRow
                key={row.id}
                sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
              >
                <TableCell sx={{width: "25%", color: "var(--font-color)", fontSize: "calc(6px + 1vmin)"}} align="left">{row.date}</TableCell>
                <TableCell sx={{width: "25%", color: "var(--font-color)", fontSize: "calc(6px + 1vmin)"}} align="left">{row.category}</TableCell>
                <TableCell sx={{width: "40%", color: "var(--font-color)", fontSize: "calc(6px + 1vmin)"}} align="left">{row.description}</TableCell>
                <TableCell sx={{color: "var(--font-color)", fontSize: "calc(6px + 1vmin)"}} align="left">{row.amount}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
}
