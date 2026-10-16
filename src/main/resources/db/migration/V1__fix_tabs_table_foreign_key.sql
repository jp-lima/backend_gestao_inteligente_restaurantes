-- Corrige a foreign key de tabs.table_id para apontar para a tabela mesas,
-- ja que o sistema nao possui uma tabela tables; o conceito de mesa eh representado por "mesas".
--
-- Aplicar manualmente no banco de dados (ex: Supabase) ate que uma ferramenta de migration
-- como Flyway/Liquibase seja adicionada ao projeto.

ALTER TABLE tabs
    DROP CONSTRAINT IF EXISTS tabs_table_id_foreign;

ALTER TABLE tabs
    ADD CONSTRAINT tabs_table_id_foreign
        FOREIGN KEY (table_id) REFERENCES mesas(id);
