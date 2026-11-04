# Fontes dos conteúdos de treinamento

Este arquivo registra as referências usadas para elaborar os conteúdos genéricos do NTI Training. Procedimentos próprios do setor devem ser documentados somente depois de validados pela equipe do NTI.

## Proteção de contas e acessos

Revisão realizada em 4 de novembro de 2026.

### Fontes principais

- [NIST SP 800-63B-4 — Authentication and Authenticator Management](https://pages.nist.gov/800-63-4/sp800-63b.html): comprimento de senhas, exclusividade, gerenciadores, autenticação multifator e resistência a phishing.
- [CISA — Secure Our World: Multifactor Authentication](https://www.cisa.gov/sites/default/files/2024-09/Secure-Our-World-MFA-Tip-Sheet.pdf): ativação de MFA e uso de um segundo fator.
- [CISA — Avoid Phishing Scams](https://www.cisa.gov/sites/default/files/2024-09/Secure-Our-World-Phishing-Tip-Sheet.pdf): sinais de phishing e resposta a mensagens suspeitas.
- [NCSC — Securing your users’ accounts](https://www.ncsc.gov.uk/collection/using-online-services-safely/securing-your-users-accounts): senhas únicas, gerenciadores e frases com palavras aleatórias.
- [OWASP — Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html): credential stuffing, password spraying e boas práticas de autenticação.
- [OWASP — Multifactor Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Multifactor_Authentication_Cheat_Sheet.html): MFA fatigue e preferência por métodos resistentes a phishing.
- [Microsoft — What are passkeys and why they matter](https://support.microsoft.com/en-us/accounts-billing/security/what-are-passkeys-why-they-matter): passkeys, Windows Hello e gerenciadores de credenciais.

### Decisões editoriais

- O material ensina conceitos e comportamentos que independem da infraestrutura do IDAM.
- O tutorial não indica um aplicativo autenticador ou gerenciador de senhas específico. A ferramenta precisa ser aprovada pelo NTI.
- O texto não descreve configurações do Active Directory, políticas de domínio ou resposta técnica a incidentes, pois esses procedimentos precisam ser demonstrados e validados pelo setor.
- Exemplos de senha são apenas didáticos e não devem ser reutilizados.

## Defesa contra phishing

Revisão realizada em 4 de novembro de 2026.

### Fontes principais

- [CISA — Secure Our World: Avoid Phishing Scams](https://www.cisa.gov/sites/default/files/2024-09/Secure-Our-World-Phishing-Tip-Sheet.pdf): urgência, pedidos de dados, links incorretos e a sequência reconhecer, reportar e excluir.
- [Microsoft — Protect yourself from phishing](https://support.microsoft.com/en-gb/security/protect-yourself-from-phishing): confirmação por outro canal, acesso direto ao site oficial e relato de mensagens suspeitas.
- [Microsoft Defender — How to protect against phishing attacks](https://learn.microsoft.com/en-us/defender-endpoint/malware/phishing): domínios parecidos, divergência entre texto e destino do link, anexos e solicitações incomuns.
- [NCSC — Phishing attacks: defending your organisation](https://www.ncsc.gov.uk/guidance/phishing): spear phishing, cultura de relato sem punição, defesa em camadas e resposta rápida a incidentes.

### Decisões editoriais

- Os exemplos usam o domínio reservado `.example` e não reproduzem endereços reais do IDAM ou de unidades locais.
- O conteúdo considera o cenário informado pela equipe: mensagens que imitam unidades e links criados para capturar senhas.
- O treinamento não pede que o usuário teste links suspeitos. A orientação é usar um endereço conhecido e reportar ao NTI pelos canais oficiais.
- O procedimento técnico após o comprometimento permanece sob orientação do NTI, pois depende das ferramentas e políticas internas.

## Engenharia social e falso suporte técnico

Revisão realizada em 4 de novembro de 2026.

### Fontes principais

- [Microsoft — Avoid and report Microsoft technical support scams](https://support.microsoft.com/en-us/security/avoid-and-report-microsoft-technical-support-scams): contatos não solicitados, falsos alertas e números de telefone em mensagens de erro.
- [Microsoft — Protect yourself from tech support scams](https://support.microsoft.com/en-au/office/protect-yourself-from-tech-support-scams): aplicativos de acesso remoto, falsos diagnósticos e roubo de dados.
- [FTC — How to spot, avoid, and report tech support scams](https://consumer.ftc.gov/articles/how-spot-avoid-and-report-tech-support-scams): pressão, pedidos de controle remoto, contatos falsos e resposta após o golpe.
- [NCSC — Business communications: SMS and telephone best practice](https://www.ncsc.gov.uk/files/Business-communications-SMS-and-telephone-best-practice.pdf): falsificação de chamadas e mensagens e necessidade de comunicações reconhecíveis.

### Decisões editoriais

- O material não cita uma ferramenta específica de acesso remoto, pois o software autorizado deve ser definido pelo NTI.
- A regra institucional ensinada é que a equipe do NTI não solicita a senha nem o código de MFA do usuário.
- A confirmação deve usar um contato oficial já conhecido, nunca o telefone ou link fornecido pelo próprio solicitante.
- A resposta técnica após um acesso indevido permanece sob orientação do NTI.
