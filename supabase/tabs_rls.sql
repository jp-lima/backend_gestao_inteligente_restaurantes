-- ============================================================
-- Supabase RLS — tabela public.tabs
-- Estado atual: RLS habilitado, sem policies (acesso anon já negado).
-- Backend Spring conecta como postgres/service_role (bypassa RLS).
-- Como usar: SQL Editor > New query > colar e Run (somente verificação).
-- ============================================================

-- Verificação: RLS ligado e nenhuma policy permissiva
SELECT relname, relrowsecurity AS rls_enabled
FROM pg_class WHERE relname = 'tabs';

SELECT policyname, roles, cmd, permissive
FROM pg_policies WHERE tablename = 'tabs';

-- (OPCIONAL) Leitura pública direta via Supabase. Descomente se o frontend
-- precisar ler comandas sem passar pelo Spring:
-- CREATE POLICY "tabs_select_public"
-- ON public.tabs FOR SELECT
-- TO anon, authenticated
-- USING (true);
