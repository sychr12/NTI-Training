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
```

Em producao, use `NTI_SESSION_COOKIE_SECURE=true` com HTTPS.
