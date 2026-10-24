# NTI-Training

Portal de treinamento interno com frontend em Next.js e backend em Spring Boot.

## Rodando o frontend

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

O frontend roda em `http://localhost:3000` e espera o backend em `http://localhost:8080`.
Para mudar a URL da API:

```powershell
$env:NEXT_PUBLIC_API_URL="http://localhost:8080"
```

## Rodando o backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

O backend usa PostgreSQL externo, com Docker Compose automatico desativado.
Configure `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD`
no ambiente ou na IDE antes de iniciar. Veja os exemplos em [backend/README.md](backend/README.md).
As tabelas iniciais continuam sendo criadas com `schema.sql`.

## Login com Active Directory

O login usa as credenciais do Active Directory via LDAP/LDAPS. A senha serve somente para validar
o bind no AD; ela nao e salva no banco, na sessao ou no frontend.

Configure as variaveis de ambiente antes de subir o backend:

```powershell
$env:NTI_AD_URL="ldap://10.46.1.2:389"
$env:NTI_AD_DOMAIN="idam.am.gov.br"
$env:NTI_AD_BASE_DN="DC=idam,DC=am,DC=gov,DC=br"
$env:NTI_AD_ALLOWED_GROUPS="GTI,GTI_ESTAGIARIO"
$env:NTI_AD_ALLOW_INSECURE_LDAP="true"
```

O uso de LDAP na porta 389 e temporario e transmite credenciais sem a protecao do TLS.
Mantenha `NTI_AD_ALLOW_INSECURE_LDAP=false` fora do ambiente local e migre para
`ldaps://THOR.idam.am.gov.br:636` quando o LDAPS estiver corretamente configurado.

Para desenvolvimento local, o script abaixo solicita a senha do PostgreSQL sem grava-la em arquivo e
inicia o backend com as demais variaveis preenchidas:

```powershell
cd backend
.\run-local.ps1
```

Na tela de login, informe o usuario da rede e a senha pessoal do Windows/AD. A aplicacao faz
o bind com `usuario@idam.am.gov.br` e usa a mesma conexao autenticada para consultar atributos e grupos.

Opcionalmente:

```powershell
$env:NTI_CORS_ALLOWED_ORIGINS="http://localhost:3000"
$env:NTI_SESSION_COOKIE_SECURE="false"
$env:NTI_LOGIN_MAX_ATTEMPTS="5"            # senhas erradas antes de bloquear o usuario
$env:NTI_LOGIN_BLOCK_MINUTES="15"          # duracao do bloqueio
$env:NTI_AD_CONNECT_TIMEOUT_MILLIS="5000"
$env:NTI_AD_READ_TIMEOUT_MILLIS="5000"
```

Em producao, use `NTI_SESSION_COOKIE_SECURE=true` com HTTPS.

## Login local para desenvolvimento (sem AD)

Fora da rede do AD, suba o backend com o perfil `dev`. Ele troca o login do AD por um usuario local
e sobe o PostgreSQL pelo `backend/compose.yaml` (precisa do Docker Desktop aberto):

```powershell
cd backend
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

Usuario `dev`, senha `dev123` (altere com `NTI_DEV_USERNAME` e `NTI_DEV_PASSWORD`).
O perfil tambem carrega conteudo de exemplo (tutoriais, trilhas, dicas, glossario e exercicios) a partir de
`backend/src/main/resources/data-dev.sql`, sem duplicar registros a cada inicializacao.
Nunca use o perfil `dev` em producao.

## Telas do frontend

Cada tela tem sua propria rota e busca os dados na API do backend:
`/login`, `/` (inicio), `/tutoriais`, `/tutoriais/[id]`, `/trilhas`, `/exercicios`, `/dicas`,
`/glossario`, `/progresso`, `/favoritos` e `/configuracoes`. Sem sessao, o app redireciona para `/login`.
Os favoritos ficam salvos no navegador, pois o backend ainda nao tem API para eles.

## Gerenciar conteudo

Usuarios com perfil PROFESSOR, SUPERVISOR ou ADMIN veem o item **Gerenciar** no menu (`/gerenciar`), onde
cadastram, editam e excluem tutoriais, trilhas (com a ordem dos tutoriais), exercicios (com gabarito),
dicas e termos do glossario. Somente ADMIN acessa `/gerenciar/usuarios` para mudar o perfil de acesso
ou desativar contas. Os perfis sao definidos no banco; o primeiro administrador precisa ser configurado
por um operador. No perfil `dev`, o usuario `dev` ja e ADMIN.
