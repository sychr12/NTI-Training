# Backend NTI Training

Java 17, Spring Boot e PostgreSQL. Execute na pasta `backend`:

```powershell
.\mvnw.cmd spring-boot:run
```

O backend conecta a um PostgreSQL externo. A inicializacao automatica pelo Docker Compose esta desativada.
Antes de executar, configure na IDE ou no ambiente:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/nti_training"
$env:SPRING_DATASOURCE_USERNAME="seu_usuario"
$env:SPRING_DATASOURCE_PASSWORD="sua_senha"
```

Substitua host, porta, nome do banco e credenciais pelos dados da sua conexao.
O banco deve existir; as tabelas da aplicacao e das sessoes continuam sendo inicializadas automaticamente.
As variaveis do Active Directory estao descritas no README da raiz. O login real requer AD configurado.

## API e permissoes

As rotas usam sessao HTTP; envie cookies com `credentials: "include"` no frontend.
Novos usuarios recebem o perfil `ALUNO`. Usuarios legados `USUARIO` sao convertidos para `ALUNO`
pelo esquema de inicializacao. O primeiro administrador deve ser definido por um operador no banco.
Alteracoes de perfil e desativacoes passam a valer na proxima requisicao, inclusive em sessoes abertas.

| Rotas | Acesso |
| --- | --- |
| POST `/api/auth/login`, GET `/api/auth/me`, POST `/api/auth/logout` | Publico; `/me` retorna 401 sem sessao |
| GET `/api/home`, `/api/perfil`, `/api/progresso`, `/api/progresso/trilhas`, `/api/conquistas` | Autenticado, dados do proprio usuario |
| PUT `/api/progresso` | Autenticado; `tutorialId`, `status` (`EM_ANDAMENTO` ou `CONCLUIDO`) e `percentual` opcional |
| POST `/api/conquistas/vistas` | Autenticado; marca as medalhas novas como vistas |
| GET `/api/favoritos`, PUT e DELETE `/api/favoritos/{tutorialId}` | Autenticado |
| GET e PUT `/api/perfil/preferencias` | Autenticado |
| GET `/api/tutorials`, `/api/tutoriais`, `/api/trilhas`, `/api/dicas`, `/api/glossario`, `/api/exercicios?tutorialId=...` | Autenticado |
| GET `/api/exercicios/minhas-respostas?tutorialId=...` | Autenticado; respostas ja dadas pelo usuario |
| POST `/api/exercicios/{id}/responder` | Autenticado; `resposta` A, B, C ou D |
| GET `/api/exercicios/gabarito?tutorialId=...` | PROFESSOR, SUPERVISOR ou ADMIN |
| POST, PUT e DELETE de conteudo | PROFESSOR, SUPERVISOR ou ADMIN |
| `/api/users` e `/api/users/{id}` | ADMIN |

`/api/tutorials` e `/api/tutoriais` sao aliases. Exclusoes de conteudo sao logicas.
Erros voltam como `{ "mensagem": ... }` com o status HTTP adequado (401, 403, 404, 409, 429...).

Regras de progresso:

- `EM_ANDAMENTO` guarda de 0 a 99% e nunca diminui; `CONCLUIDO` e sempre 100%.
- So e possivel concluir um tutorial depois de responder todos os exercicios ativos dele; senao a API responde 409.
- XP: 50 por tutorial concluido, 10 por resposta correta e 100 por trilha concluida.
  Niveis: Iniciante (0), Aprendiz (100), Explorador (300), Especialista (600) e Mestre (1000).

## Perfil dev (fora da rede do AD)

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

Sobe o PostgreSQL do `compose.yaml` pelo Docker, carrega o conteudo de exemplo de `data-dev.sql` e troca o AD
por um usuario local ADMIN (`dev` / `dev123`, ou `NTI_DEV_USERNAME` e `NTI_DEV_PASSWORD`). Nunca use em producao.

## Testes

```powershell
.\mvnw.cmd test
```

A suite padrao usa H2 para testar inicializacao, leituras, validacao, respostas HTTP e permissoes.
Os testes de gravacao usam PostgreSQL real, pois `ON CONFLICT` e `RETURNING` nao sao simulados pelo H2.
Para executa-los, use exclusivamente um banco de teste isolado:

```powershell
$env:NTI_TEST_DATABASE_URL="jdbc:postgresql://localhost:5432/nti_training_test"
$env:NTI_TEST_DATABASE_DRIVER="org.postgresql.Driver"
$env:NTI_TEST_DATABASE_USER="nti_test"
$env:NTI_TEST_DATABASE_PASSWORD="senha-do-banco-de-teste"
.\mvnw.cmd verify
```

Os testes usam rollback das alteracoes de dados; a inicializacao cria as tabelas no banco informado.
O Active Directory e simulado nos testes de API; a conexao LDAP real precisa ser validada no ambiente corporativo.
