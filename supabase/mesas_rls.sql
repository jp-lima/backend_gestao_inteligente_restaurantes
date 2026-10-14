-- ============================================================
-- Supabase RLS — tabela public.mesas
-- Como usar: Supabase Dashboard > SQL Editor > New query > colar e Run.
-- Pré-requisito: backend Spring conecta com role postgres/service_role
-- (bypassa RLS). Este script BLOQUEIA acesso direto via anon key.
-- ============================================================

-- 1. Habilita RLS (tira o estado "unrestricted / RLS disabled")
ALTER TABLE public.mesas ENABLE ROW LEVEL SECURITY;

-- 2. Remove policies permissivas antigas, se existirem
DROP POLICY IF EXISTS "mesas_all_public" ON public.mesas;
DROP POLICY IF EXISTS "mesas_select_public" ON public.mesas;
DROP POLICY IF EXISTS "Enable all for anon" ON public.mesas;
DROP POLICY IF EXISTS "Enable read for anon" ON public.mesas;

-- 3. Sem nenhuma policy para anon = deny por padrão. NADA a criar para o
-- backend funcionar, pois postgres/service_role bypassam RLS.
-- Ou seja: Spring continua com CRUD total, acesso direto via anon key passa
-- a receber 403/empty.

-- 4. (OPCIONAL) Se quiser leitura pública do cardápio de mesas no frontend
-- direto via Supabase, descomente abaixo. INSERT/UPDATE/DELETE continuam
-- bloqueados para anon.
-- CREATE POLICY "mesas_select_public"
-- ON public.mesas FOR SELECT
-- TO anon, authenticated
-- USING (true);

-- 5. (OPCIONAL) Se um dia o frontend autenticado precisar escrever direto,
-- crie policies restritas por authenticated em vez de abrir para anon:
-- CREATE POLICY "mesas_write_authenticated"
-- ON public.mesas FOR ALL
-- TO authenticated
-- USING (true)
-- WITH CHECK (true);

-- 6. Verificação: deve retornar rls_enabled = true e sem policies permissivas
SELECT relname, relrowsecurity AS rls_enabled
FROM pg_class WHERE relname = 'mesas';

SELECT policyname, roles, cmd, permissive
FROM pg_policies WHERE tablename = 'mesas';
