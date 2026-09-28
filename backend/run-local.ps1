param(
    [string]$DatabaseUrl = "jdbc:postgresql://localhost:5432/TrainingNTI",
    [string]$DatabaseUser = "postgres"
)

$databasePassword = Read-Host "Senha do PostgreSQL" -AsSecureString
$databaseCredential = [PSCredential]::new($DatabaseUser, $databasePassword)
$exitCode = 1

try {
    $env:SPRING_DATASOURCE_URL = $DatabaseUrl
    $env:SPRING_DATASOURCE_USERNAME = $DatabaseUser
    $env:SPRING_DATASOURCE_PASSWORD = $databaseCredential.GetNetworkCredential().Password

    $env:NTI_AD_URL = "ldap://10.46.1.2:389"
    $env:NTI_AD_DOMAIN = "idam.am.gov.br"
    $env:NTI_AD_BASE_DN = "DC=idam,DC=am,DC=gov,DC=br"
    $env:NTI_AD_ALLOWED_GROUPS = "GTI,GTI_ESTAGIARIO"
    $env:NTI_AD_ALLOW_INSECURE_LDAP = "true"

    & "$PSScriptRoot\mvnw.cmd" spring-boot:run
    $exitCode = $LASTEXITCODE
}
finally {
    Remove-Item Env:SPRING_DATASOURCE_URL -ErrorAction SilentlyContinue
    Remove-Item Env:SPRING_DATASOURCE_USERNAME -ErrorAction SilentlyContinue
    Remove-Item Env:SPRING_DATASOURCE_PASSWORD -ErrorAction SilentlyContinue
    Remove-Item Env:NTI_AD_URL -ErrorAction SilentlyContinue
    Remove-Item Env:NTI_AD_DOMAIN -ErrorAction SilentlyContinue
    Remove-Item Env:NTI_AD_BASE_DN -ErrorAction SilentlyContinue
    Remove-Item Env:NTI_AD_ALLOWED_GROUPS -ErrorAction SilentlyContinue
    Remove-Item Env:NTI_AD_ALLOW_INSECURE_LDAP -ErrorAction SilentlyContinue
}

exit $exitCode
