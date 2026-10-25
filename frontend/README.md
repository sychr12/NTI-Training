# Frontend NTI Training

Next.js 16 (App Router), React 19, Tailwind 4 e ícones `lucide-react`. Na pasta `frontend`:

```powershell
npm.cmd install
npm.cmd run dev     # http://localhost:3000
npm.cmd run lint
npm.cmd run build
```

A API é lida de `NEXT_PUBLIC_API_URL` (padrão `http://localhost:8080`). As chamadas enviam o cookie de sessão
(`credentials: "include"`), então o backend precisa liberar a origem do frontend no CORS.

## Estrutura

| Pasta | Conteúdo |
| --- | --- |
| `app/login` | Tela de login com a animação de estudo |
| `app/(app)` | Páginas autenticadas; o `layout.tsx` redireciona para `/login` sem sessão |
| `app/(app)/gerenciar` | Cadastros de conteúdo (Professor, Supervisor, Admin) e usuários (Admin) |
| `app/components` | Componentes compartilhados: layout, tutoriais, exercícios, conquistas, CRUD genérico |
| `app/lib` | Acesso à API (`api.ts`), tipos, progresso, favoritos, conquistas e helpers de exibição |
| `app/globals.css` | Todos os estilos, separados por seção, com tema claro/escuro e regras para celular |

## Convenções

- `useApi(path)` busca dados e devolve `{ data, error, loading, reload }`; escritas usam `apiFetch`.
- Uma resposta 401 dispara o evento `nti:session-expired` e a sessão é encerrada na tela.
- Ações que mudam progresso chamam `notifyProgressChanged()` para atualizar resumos e conquistas.
- O tema fica em `data-theme` no `<html>` e as cores vêm de variáveis CSS em `globals.css`.
