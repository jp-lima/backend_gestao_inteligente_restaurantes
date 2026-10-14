package com.app.restaurante.entity;

/**
 * Espelha os valores do enum Postgres {@code tab_status} da tabela
 * {@code public.tabs}. Os nomes são minúsculos de propósito para coincidir
 * exatamente com as labels do banco.
 */
public enum TabStatus {
    aberto,
    cancelado,
    pago
}
