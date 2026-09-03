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

O backend usa PostgreSQL via `compose.yaml` e cria as tabelas iniciais com `schema.sql`.

## Login com Active Directory

O login usa as credenciais do Active Directory via LDAP/LDAPS. A senha serve somente para validar
o bind no AD; ela nao e salva no banco, na sessao ou no frontend.

Configure as variaveis de ambiente antes de subir o backend:

```powershell
$env:NTI_AD_URL="ldaps://ad.seudominio.local:636"
$env:NTI_AD_BASE_DN="DC=seudominio,DC=local"
$env:NTI_AD_SERVICE_USER_DN="CN=usuario-servico,OU=Servicos,DC=seudominio,DC=local"
$env:NTI_AD_SERVICE_PASSWORD="senha-do-usuario-servico"
$env:NTI_AD_REQUIRED_GROUP_DN="CN=GG_NTI_TRAINING_USUARIOS,OU=Grupos,DC=seudominio,DC=local"
```

Opcionalmente:

```powershell
$env:NTI_CORS_ALLOWED_ORIGINS="http://localhost:3000"
$env:NTI_SESSION_COOKIE_SECURE="false"
```

Em producao, use `NTI_SESSION_COOKIE_SECURE=true` com HTTPS.
