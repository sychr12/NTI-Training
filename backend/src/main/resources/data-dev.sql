-- Conteudo de exemplo para o perfil "dev" (ver application-dev.properties).
-- Cada insercao confere se o registro ja existe, entao o script pode rodar a cada inicializacao.
-- Evite ponto e virgula dentro dos textos: o Spring separa os comandos por ele.

-- Usuario local "dev" (DevAuthenticator) com perfil ADMIN, para acessar a area de gerenciamento.
-- O GUID e o mesmo usado pelo DevAuthenticator.
INSERT INTO usuarios (ad_object_guid, login, nome, email, perfil)
SELECT '00000000-0000-0000-0000-00000000de00', 'dev', 'Dev Local', 'dev@localhost', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE ad_object_guid = '00000000-0000-0000-0000-00000000de00');
UPDATE usuarios SET perfil = 'ADMIN' WHERE ad_object_guid = '00000000-0000-0000-0000-00000000de00';

-- Tutoriais
INSERT INTO tutoriais (titulo, descricao, conteudo, categoria, nivel)
SELECT 'Como ligar e desligar o computador corretamente',
       'Evite perder arquivos e danificar o sistema com o jeito certo de ligar e desligar.',
       'Desligar o computador direto no botão pode corromper arquivos abertos e atualizações em andamento. Usar o menu Iniciar dá tempo ao Windows de salvar tudo.

1. Salve seu trabalho: feche os documentos abertos e confira se tudo foi salvo.
2. Abra o menu Iniciar: clique no ícone do Windows no canto inferior esquerdo.
3. Clique em Ligar/Desligar: escolha Desligar e aguarde a tela apagar.
4. Para ligar: aperte o botão uma vez e aguarde a tela de login aparecer.

- Só segure o botão de energia se o computador travar por completo.
- Se aparecer Atualizar e desligar, prefira essa opção no fim do expediente.',
       'Computador', 'Iniciante'
WHERE NOT EXISTS (SELECT 1 FROM tutoriais WHERE titulo = 'Como ligar e desligar o computador corretamente');

INSERT INTO tutoriais (titulo, descricao, conteudo, categoria, nivel)
SELECT 'Como criar e organizar pastas',
       'Mantenha seus arquivos fáceis de encontrar com uma estrutura simples de pastas.',
       'Uma boa organização economiza tempo e evita versões duplicadas do mesmo arquivo.

1. Abra o Explorador de Arquivos: use o atalho Windows + E.
2. Escolha o local: entre em Documentos ou na pasta do seu setor.
3. Crie a pasta: clique com o botão direito, Novo e depois Pasta.
4. Dê um nome claro: por exemplo 2026 Relatorios Mensais.

- Use nomes curtos e sem caracteres especiais.
- Agrupe por ano, projeto ou assunto.
- Apague arquivos temporários que não usa mais.',
       'Computador', 'Iniciante'
WHERE NOT EXISTS (SELECT 1 FROM tutoriais WHERE titulo = 'Como criar e organizar pastas');

INSERT INTO tutoriais (titulo, descricao, conteudo, categoria, nivel)
SELECT 'Como conectar no Wi-Fi',
       'Conecte notebooks e celulares à rede sem fio com segurança.',
       'A rede sem fio permite acessar a internet sem cabo. Prefira sempre redes conhecidas e protegidas por senha.

1. Clique no ícone de rede: ele fica perto do relógio, no canto inferior direito.
2. Escolha a rede: selecione o nome da rede indicado pelo NTI.
3. Digite a senha: confira maiúsculas e minúsculas antes de confirmar.
4. Teste a conexão: abra o navegador e acesse um site conhecido.

- Evite redes abertas de lugares públicos para acessar sistemas da empresa.
- Se a conexão cair com frequência, aproxime-se do roteador ou procure o suporte.',
       'Internet', 'Iniciante'
WHERE NOT EXISTS (SELECT 1 FROM tutoriais WHERE titulo = 'Como conectar no Wi-Fi');

INSERT INTO tutoriais (titulo, descricao, conteudo, categoria, nivel)
SELECT 'Como instalar um programa',
       'Aprenda o passo a passo para instalar programas de forma correta e segura.',
       'Instalar programas de fontes desconhecidas é uma das principais portas de entrada de vírus. Na empresa, peça sempre a liberação ao NTI.

1. Confirme a necessidade: verifique se o programa é permitido pela política do setor.
2. Baixe do site oficial: nunca use links de anúncios ou sites de download genéricos.
3. Execute o instalador: dê dois cliques no arquivo baixado e aceite apenas o necessário.
4. Recuse programas extras: desmarque barras de ferramentas e ofertas adicionais.
5. Teste o programa: abra e confira se funciona como esperado.',
       'Programas', 'Iniciante'
WHERE NOT EXISTS (SELECT 1 FROM tutoriais WHERE titulo = 'Como instalar um programa');

INSERT INTO tutoriais (titulo, descricao, conteudo, categoria, nivel)
SELECT 'Como funciona uma rede de computadores',
       'Entenda roteador, endereço IP e como os computadores conversam entre si.',
       'Uma rede é um conjunto de dispositivos ligados para trocar informações. Na empresa, os computadores se comunicam por cabos e pelo Wi-Fi.

O roteador encaminha os dados entre a rede interna e a internet. Cada dispositivo recebe um endereço IP, que funciona como o endereço de uma casa.

1. O computador envia um pedido: por exemplo, abrir um site.
2. O roteador recebe o pedido: e encaminha para a internet.
3. A resposta volta: o roteador entrega ao computador que pediu.

- IP identifica o dispositivo na rede.
- DNS traduz nomes de sites em endereços IP.
- Switch liga vários computadores da mesma rede.',
       'Redes', 'Intermediário'
WHERE NOT EXISTS (SELECT 1 FROM tutoriais WHERE titulo = 'Como funciona uma rede de computadores');

INSERT INTO tutoriais (titulo, descricao, conteudo, categoria, nivel)
SELECT 'Como atualizar o Windows',
       'Atualizações corrigem falhas de segurança e deixam o computador mais estável.',
       'O Windows recebe atualizações todo mês. Mantê-lo em dia protege contra vírus que exploram falhas conhecidas.

1. Abra as Configurações: use o atalho Windows + I.
2. Entre em Windows Update: fica no menu lateral.
3. Verifique atualizações: clique no botão e aguarde a busca.
4. Reinicie quando pedir: salve seu trabalho antes de reiniciar.

- Prefira atualizar no fim do dia para não interromper o trabalho.
- Não desligue o computador durante a instalação.',
       'Manutenção', 'Iniciante'
WHERE NOT EXISTS (SELECT 1 FROM tutoriais WHERE titulo = 'Como atualizar o Windows');

INSERT INTO tutoriais (titulo, descricao, conteudo, categoria, nivel)
SELECT 'Como criar senhas fortes',
       'Proteja suas contas usando senhas longas, únicas e difíceis de adivinhar.',
       'Uma senha segura não precisa ser uma sequência impossível de memorizar. O mais importante é que ela seja longa, exclusiva para aquela conta e não tenha informações fáceis de descobrir.

1. Prefira uma senha longa: quando o sistema permitir, use pelo menos 15 caracteres. Uma frase com palavras aleatórias pode ser mais fácil de lembrar.
2. Use uma senha diferente em cada conta: se uma delas vazar, as outras continuam protegidas.
3. Evite informações pessoais: não use nome, aniversário, matrícula, setor, time ou sequências como 123456.
4. Use um gerenciador aprovado: ele pode criar e guardar senhas únicas. Confirme com o NTI qual ferramenta é permitida.
5. Ative a autenticação multifator: quando disponível, ela adiciona outra verificação além da senha.
6. Troque a senha diante de suspeita: faça a alteração se houver indício de vazamento ou conforme a política da organização.

- Nunca reutilize a senha da rede em sites pessoais.
- Não compartilhe senhas nem códigos de verificação. A equipe do NTI não precisa deles para prestar suporte.
- Não copie o exemplo de outra pessoa. Crie uma combinação que seja exclusiva para você.',
       'Segurança', 'Iniciante'
WHERE NOT EXISTS (SELECT 1 FROM tutoriais WHERE titulo = 'Como criar senhas fortes');

-- Mantem o tutorial existente atualizado em bancos de desenvolvimento ja criados.
-- Referencias: NIST SP 800-63B-4, CISA Secure Our World e NCSC Three Random Words.
UPDATE tutoriais
SET descricao = 'Proteja suas contas usando senhas longas, únicas e difíceis de adivinhar.',
    conteudo = 'Uma senha segura não precisa ser uma sequência impossível de memorizar. O mais importante é que ela seja longa, exclusiva para aquela conta e não tenha informações fáceis de descobrir.

1. Prefira uma senha longa: quando o sistema permitir, use pelo menos 15 caracteres. Uma frase com palavras aleatórias pode ser mais fácil de lembrar.
2. Use uma senha diferente em cada conta: se uma delas vazar, as outras continuam protegidas.
3. Evite informações pessoais: não use nome, aniversário, matrícula, setor, time ou sequências como 123456.
4. Use um gerenciador aprovado: ele pode criar e guardar senhas únicas. Confirme com o NTI qual ferramenta é permitida.
5. Ative a autenticação multifator: quando disponível, ela adiciona outra verificação além da senha.
6. Troque a senha diante de suspeita: faça a alteração se houver indício de vazamento ou conforme a política da organização.

- Nunca reutilize a senha da rede em sites pessoais.
- Não compartilhe senhas nem códigos de verificação. A equipe do NTI não precisa deles para prestar suporte.
- Não copie o exemplo de outra pessoa. Crie uma combinação que seja exclusiva para você.',
    categoria = 'Segurança',
    nivel = 'Iniciante'
WHERE titulo = 'Como criar senhas fortes';

INSERT INTO tutoriais (titulo, descricao, conteudo, categoria, nivel)
SELECT 'Como usar autenticação multifator com segurança',
       'Adicione uma segunda camada de proteção e saiba reagir a pedidos de acesso inesperados.',
       'A autenticação multifator, também chamada de MFA ou verificação em duas etapas, pede duas provas diferentes de identidade. Pode ser algo que você sabe, algo que possui ou uma característica sua.

1. Procure a opção de segurança: no sistema autorizado, abra as configurações da conta e localize MFA, autenticação multifator ou verificação em duas etapas.
2. Use o método aprovado pelo NTI: aplicativo autenticador, chave de segurança, passkey, biometria ou outro método definido pela organização.
3. Guarde a recuperação com segurança: se o serviço fornecer códigos de recuperação, mantenha-os em local privado e nunca junto da senha.
4. Confirme somente pedidos iniciados por você: antes de aprovar uma notificação, confira se acabou de tentar entrar e se o serviço exibido é o correto.
5. Negue solicitações inesperadas: várias notificações seguidas podem ser uma tentativa de cansar você até que uma seja aprovada.
6. Avise o NTI pelos canais oficiais: se receber um pedido que não iniciou, não aprove, não informe códigos e comunique o ocorrido.

- Um código de MFA é secreto e temporário. Quem pede esse código pode estar tentando entrar em seu nome.
- SMS e e-mail podem aparecer como opção, mas métodos resistentes a phishing, como passkeys e chaves de segurança, oferecem proteção maior quando disponíveis.
- Biometria e o PIN do dispositivo devem ser cadastrados somente em equipamento confiável.',
       'Segurança', 'Intermediário'
WHERE NOT EXISTS (SELECT 1 FROM tutoriais WHERE titulo = 'Como usar autenticação multifator com segurança');

INSERT INTO tutoriais (titulo, descricao, conteudo, categoria, nivel)
SELECT 'Como ataques contra credenciais funcionam',
       'Entenda as técnicas usadas para roubar contas e reconheça os sinais antes de autorizar um acesso.',
       'Atacantes raramente dependem de uma única técnica. Eles combinam senhas vazadas, automação, mensagens falsas e solicitações de autenticação para tentar assumir uma conta.

1. Força bruta: muitas combinações são testadas contra uma conta. Senhas longas e limitação de tentativas reduzem o risco.
2. Password spraying: uma senha comum é testada em muitas contas para evitar bloqueios rápidos. Senhas previsíveis facilitam o ataque.
3. Credential stuffing: usuário e senha vazados de um serviço são testados em outros. Por isso cada conta precisa de uma senha exclusiva.
4. Phishing: uma mensagem ou página falsa tenta convencer você a entregar senha, código ou aprovar um acesso.
5. MFA fatigue: o atacante envia repetidas notificações e espera que a vítima aprove uma delas por engano ou cansaço.
6. Roubo de sessão: mesmo sem descobrir a senha, um invasor pode tentar capturar uma sessão já autenticada. Links desconhecidos e dispositivos não confiáveis aumentam o risco.

Se suspeitar de comprometimento, pare de aprovar solicitações, use outro canal para contatar o NTI e siga as orientações de resposta. Quando solicitado, altere a senha em um dispositivo confiável, encerre sessões desconhecidas e revise os métodos de recuperação da conta.

- MFA reduz muito o risco, mas nem todo método resiste a phishing.
- Passkeys e chaves baseadas em FIDO vinculam a autenticação ao site legítimo e dificultam páginas falsas.
- Urgência, ameaça de bloqueio, remetente estranho e pedido de segredo são sinais de alerta.',
       'Segurança', 'Avançado'
WHERE NOT EXISTS (SELECT 1 FROM tutoriais WHERE titulo = 'Como ataques contra credenciais funcionam');

INSERT INTO tutoriais (titulo, descricao, conteudo, categoria, nivel)
SELECT 'Como reconhecer links e e-mails de phishing',
       'Identifique mensagens que tentam assustar ou convencer você a entregar sua senha.',
       'Phishing é um golpe enviado por e-mail, mensagem ou outro canal para levar você a abrir um link, baixar um arquivo ou informar dados. No ambiente de trabalho, um cenário comum é a mensagem que imita uma unidade local e apresenta uma página falsa de login.

1. Pare antes de agir: desconfie de mensagens inesperadas que exigem urgência, ameaçam bloquear a conta ou prometem alguma vantagem.
2. Confira o endereço completo: o nome exibido pode dizer Unidade Local ou Suporte, mas o endereço real pode pertencer a outro domínio.
3. Questione o pedido: senha, código de autenticação e aprovação de acesso não devem ser enviados por e-mail ou mensagem.
4. Não use o link recebido: se a solicitação parecer possível, abra o sistema pelo endereço que você já conhece ou por um favorito confiável.
5. Confirme por outro canal: fale com a unidade ou com o NTI usando telefone, ramal ou contato oficial já conhecido, nunca os dados fornecidos na mensagem suspeita.
6. Comunique o NTI: reporte a mensagem pelos canais oficiais e só depois exclua. Um aviso rápido pode proteger outras pessoas do mesmo golpe.

Se você clicou, informe o NTI mesmo que nada tenha acontecido. Se também digitou a senha ou aprovou uma solicitação, avise imediatamente e siga a orientação da equipe para proteger a conta.

- Um e-mail bem escrito também pode ser falso.
- O logotipo, a assinatura e o nome de uma unidade podem ser copiados.
- A equipe do NTI nunca precisa saber sua senha para prestar suporte.',
       'Segurança', 'Iniciante'
WHERE NOT EXISTS (SELECT 1 FROM tutoriais WHERE titulo = 'Como reconhecer links e e-mails de phishing');

INSERT INTO tutoriais (titulo, descricao, conteudo, categoria, nivel)
SELECT 'Como verificar remetentes, links e páginas de login',
       'Aprenda a conferir o destino real de uma mensagem antes de abrir ou informar credenciais.',
       'Golpes mais convincentes usam nomes conhecidos, endereços parecidos e páginas que copiam o visual de sistemas legítimos. A aparência não comprova que uma mensagem ou página seja verdadeira.

1. Leia o endereço depois do arroba: o nome antes do endereço pode ser qualquer coisa. Em unidade.local@comunicados.example, o domínio real é comunicados.example. Os endereços deste tutorial são fictícios.
2. Procure pequenas alterações: letras trocadas, palavras extras, hífens e terminações diferentes podem criar um endereço parecido com o oficial.
3. Veja o destino sem clicar: no computador, passe o ponteiro sobre o link e observe o endereço mostrado. Se o texto e o destino forem diferentes, não abra.
4. Trate QR Codes e links curtos como links: eles também podem esconder o destino. Não use um QR Code inesperado para entrar em sistemas de trabalho.
5. Confira a página de login: antes de digitar usuário, senha ou código, verifique o endereço no navegador. Cadeado e HTTPS protegem a conexão, mas não provam que o site pertence à organização.
6. Desconfie de anexos inesperados: não habilite macros, não altere configurações de segurança e não instale programas solicitados por uma mensagem.
7. Confirme pedidos incomuns: se um colega ou uma unidade conhecida enviar um link inesperado, confirme por telefone ou outro canal oficial. A conta verdadeira da pessoa pode ter sido comprometida.

Em caso de dúvida, não tente testar o link. Encaminhe a situação ao NTI pelos canais oficiais e aguarde a orientação.

- Nunca compare apenas o logotipo ou a cor da página.
- Favoritos e endereços digitados manualmente são mais seguros que links recebidos.
- Senha e código de autenticação devem permanecer secretos.',
       'Segurança', 'Intermediário'
WHERE NOT EXISTS (SELECT 1 FROM tutoriais WHERE titulo = 'Como verificar remetentes, links e páginas de login');

INSERT INTO tutoriais (titulo, descricao, conteudo, categoria, nivel)
SELECT 'Como funcionam ataques de phishing direcionado',
       'Entenda golpes personalizados que imitam unidades, chefias e conversas legítimas.',
       'Phishing direcionado, também chamado de spear phishing, usa informações sobre a organização e sobre as pessoas para tornar a mensagem convincente. O atacante pode citar uma unidade local, uma atividade real, o nome de uma chefia ou um assunto em andamento.

1. Preparação: informações públicas, redes sociais, sites institucionais e mensagens vazadas ajudam o criminoso a descobrir nomes, funções e relações entre unidades.
2. Imitação: o golpe pode usar um domínio parecido, alterar o nome exibido ou partir de uma conta legítima que já foi comprometida.
3. Contexto convincente: a mensagem menciona um processo, documento, reunião ou atualização e cria um motivo para abrir um link ou anexo.
4. Captura: a página falsa solicita senha, código de MFA, aprovação de acesso ou autorização para um aplicativo. Alguns golpes tentam capturar também a sessão autenticada.
5. Expansão: com uma conta comprometida, o atacante pode responder a conversas existentes e enviar novos golpes para colegas e unidades que confiam naquele remetente.
6. Resposta rápida: reporte mesmo depois de clicar. Se informou credenciais, avise imediatamente, não aprove notificações de MFA e siga o procedimento indicado pelo NTI.

Solicitações sensíveis devem ser confirmadas por um segundo canal conhecido. A equipe técnica também utiliza camadas como filtros de e-mail, MFA, proteção do navegador e controles de domínio. Nenhuma camada isolada detecta todos os ataques, por isso o relato rápido continua essencial.

- Uma mensagem personalizada não é prova de autenticidade.
- Uma conta conhecida pode ter sido invadida.
- Não esconda um clique por receio de punição. Quanto antes o NTI souber, menor pode ser o impacto.',
       'Segurança', 'Avançado'
WHERE NOT EXISTS (SELECT 1 FROM tutoriais WHERE titulo = 'Como funcionam ataques de phishing direcionado');

INSERT INTO tutoriais (titulo, descricao, conteudo, categoria, nivel)
SELECT 'Como reconhecer falso suporte técnico',
       'Saiba identificar ligações, mensagens e alertas de pessoas que fingem representar o suporte.',
       'O falso suporte técnico começa quando alguém entra em contato sem que você tenha solicitado atendimento. A pessoa pode dizer que encontrou um vírus, que sua conta está em risco ou que precisa fazer uma manutenção urgente.

1. Observe quem iniciou o contato: se você não abriu um chamado nem pediu ajuda, trate a abordagem como suspeita.
2. Desconfie da urgência: frases como faça agora, sua conta será bloqueada ou o computador será perdido tentam impedir que você confirme a história.
3. Nunca informe sua senha: o NTI não precisa conhecer sua senha, código de MFA ou resposta de recuperação para prestar suporte.
4. Não instale nada por solicitação inesperada: aplicativos enviados por WhatsApp, e-mail ou link podem dar controle do computador a outra pessoa.
5. Interrompa e confirme: encerre a conversa e procure o NTI usando telefone, ramal, e-mail ou sistema de chamados que você já conhece.
6. Reporte a tentativa: informe o canal usado, o horário, o número ou perfil e o que foi solicitado. Não continue conversando com o suspeito.

O nome exibido no telefone ou no aplicativo de mensagens não prova a identidade. Números, fotos, logotipos e nomes de pessoas do setor podem ser copiados.

- Atendimento verdadeiro não exige segredo ou pressa para impedir uma confirmação.
- Não ligue para o número mostrado em um alerta inesperado na tela.
- Na dúvida, pare e confirme pelo canal oficial.',
       'Segurança', 'Iniciante'
WHERE NOT EXISTS (SELECT 1 FROM tutoriais WHERE titulo = 'Como reconhecer falso suporte técnico');

INSERT INTO tutoriais (titulo, descricao, conteudo, categoria, nivel)
SELECT 'Como agir diante de pedidos de acesso remoto',
       'Proteja seu computador ao receber pedidos de instalação, compartilhamento de tela ou controle remoto.',
       'Ferramentas de acesso remoto podem ser legítimas quando fazem parte de um atendimento confirmado, mas também permitem que um golpista veja a tela, mova o mouse, abra arquivos e instale programas.

1. Confirme o chamado: antes de aceitar qualquer acesso, verifique se existe atendimento aberto e confirme o técnico pelo canal oficial do NTI.
2. Não use links enviados por desconhecidos: instale apenas a ferramenta e a versão orientadas no procedimento oficial.
3. Não compartilhe códigos sem confirmação: códigos de sessão e convites de acesso dão entrada ao computador. Eles não devem ser enviados a um contato inesperado.
4. Proteja informações visíveis: feche e-mails, documentos e sistemas que não fazem parte do atendimento. Nunca digite senha ou código de MFA para outra pessoa observar.
5. Acompanhe a sessão: não deixe o computador sozinho durante o suporte e questione ações que não estejam relacionadas ao problema informado.
6. Encerre diante de comportamento estranho: se o suposto técnico abrir arquivos, pedir segredo, solicitar pagamento ou tentar acessar contas, interrompa a conexão e contate o NTI.
7. Se você já permitiu o acesso: pare a interação, encerre a ferramenta se for seguro e avise imediatamente o NTI por outro canal confiável. Siga as orientações antes de voltar a usar o equipamento.

Alertas verdadeiros do Windows e de empresas de tecnologia não apresentam um telefone aleatório para resolver vírus. Um pop-up alarmante no navegador pode ser apenas uma página tentando assustar você.

- Compartilhar a tela também pode revelar dados.
- Modo anônimo não torna uma ferramenta de acesso remoto segura.
- O código de MFA confirma uma ação em seu nome e deve permanecer secreto.',
       'Segurança', 'Intermediário'
WHERE NOT EXISTS (SELECT 1 FROM tutoriais WHERE titulo = 'Como agir diante de pedidos de acesso remoto');

INSERT INTO tutoriais (titulo, descricao, conteudo, categoria, nivel)
SELECT 'Como a engenharia social explora confiança e autoridade',
       'Entenda como golpistas combinam informações reais, pressão e vários canais para manipular decisões.',
       'Engenharia social é a manipulação de pessoas para obter acesso, informações ou uma ação que normalmente seria recusada. O falso suporte é uma de suas formas, mas a mesma técnica pode imitar chefias, fornecedores, unidades e colegas.

1. Pretexto: o atacante cria uma história plausível, como manutenção, auditoria, atualização de cadastro ou problema urgente em uma unidade.
2. Autoridade: nomes de chefias, cargos técnicos e linguagem interna são usados para desencorajar perguntas.
3. Contexto real: informações públicas, redes sociais e dados vazados ajudam a citar pessoas, locais e atividades verdadeiras.
4. Pressão: urgência, medo, culpa, promessa de benefício ou pedido de segredo reduzem o tempo para pensar.
5. Vários canais: o contato pode começar por e-mail e continuar por telefone ou WhatsApp para parecer confirmado. Um segundo canal só ajuda quando você o escolhe por uma fonte oficial.
6. Conta comprometida: uma mensagem pode vir do perfil verdadeiro de um colega. Pedidos incomuns ainda precisam ser confirmados fora daquela conversa.
7. Verificação independente: encerre o contato, consulte o diretório oficial e retorne para um número conhecido. Não use telefone, link ou contato fornecido pelo próprio solicitante.
8. Resposta sem culpa: reporte rapidamente mesmo que tenha clicado, instalado algo ou fornecido informação. O objetivo é conter o incidente e alertar outras pessoas.

Processos claros protegem mais do que confiar apenas na percepção individual. Pedidos sensíveis devem exigir confirmação e o NTI deve manter canais fáceis de reconhecer e usar.

- Identificador de chamada, foto e voz não bastam para provar identidade.
- Conhecer detalhes internos não transforma o solicitante em pessoa confiável.
- Senha e código de MFA nunca fazem parte da validação de um técnico.',
       'Segurança', 'Avançado'
WHERE NOT EXISTS (SELECT 1 FROM tutoriais WHERE titulo = 'Como a engenharia social explora confiança e autoridade');

INSERT INTO tutoriais (titulo, descricao, conteudo, categoria, nivel)
SELECT 'Como fazer backup de arquivos',
       'Guarde cópias dos seus arquivos importantes para não perder nada.',
       'Backup é uma cópia de segurança. Se o computador estragar ou for infectado, você recupera seus arquivos a partir dela.

1. Escolha o que salvar: documentos de trabalho, planilhas e relatórios.
2. Escolha o destino: use a pasta de rede do setor ou o armazenamento indicado pelo NTI.
3. Copie os arquivos: arraste as pastas para o destino escolhido.
4. Confira a cópia: abra alguns arquivos no destino para garantir que estão íntegros.

- Faça backup com frequência, de preferência toda semana.
- Pendrives se perdem e estragam com facilidade. Não use como única cópia.',
       'Segurança', 'Intermediário'
WHERE NOT EXISTS (SELECT 1 FROM tutoriais WHERE titulo = 'Como fazer backup de arquivos');

-- Trilhas
INSERT INTO trilhas (titulo, descricao, nivel)
SELECT 'Primeiros passos no computador', 'Aprenda o básico para usar o computador com confiança.', 'Iniciante'
WHERE NOT EXISTS (SELECT 1 FROM trilhas WHERE titulo = 'Primeiros passos no computador');

INSERT INTO trilhas (titulo, descricao, nivel)
SELECT 'Internet e redes', 'Entenda como a internet funciona e como se conectar.', 'Iniciante'
WHERE NOT EXISTS (SELECT 1 FROM trilhas WHERE titulo = 'Internet e redes');

INSERT INTO trilhas (titulo, descricao, nivel)
SELECT 'Segurança no dia a dia', 'Hábitos simples para proteger você e a empresa.', 'Iniciante'
WHERE NOT EXISTS (SELECT 1 FROM trilhas WHERE titulo = 'Segurança no dia a dia');

INSERT INTO trilhas (titulo, descricao, nivel)
SELECT 'Proteção de contas e acessos', 'Aprenda do básico ao avançado a proteger senhas, usar MFA e reconhecer ataques contra credenciais.', 'Iniciante'
WHERE NOT EXISTS (SELECT 1 FROM trilhas WHERE titulo = 'Proteção de contas e acessos');

INSERT INTO trilhas (titulo, descricao, nivel)
SELECT 'Defesa contra phishing', 'Reconheça links falsos, confira remetentes e responda a tentativas que imitam unidades e pessoas conhecidas.', 'Iniciante'
WHERE NOT EXISTS (SELECT 1 FROM trilhas WHERE titulo = 'Defesa contra phishing');

INSERT INTO trilhas (titulo, descricao, nivel)
SELECT 'Engenharia social e falso suporte', 'Reconheça pessoas que imitam o suporte e proteja senhas, códigos e acessos remotos.', 'Iniciante'
WHERE NOT EXISTS (SELECT 1 FROM trilhas WHERE titulo = 'Engenharia social e falso suporte');

INSERT INTO trilha_tutoriais (trilha_id, tutorial_id, ordem)
SELECT tr.id, tu.id, v.ordem
FROM (VALUES
    ('Primeiros passos no computador', 'Como ligar e desligar o computador corretamente', 1),
    ('Primeiros passos no computador', 'Como criar e organizar pastas', 2),
    ('Primeiros passos no computador', 'Como instalar um programa', 3),
    ('Primeiros passos no computador', 'Como atualizar o Windows', 4),
    ('Internet e redes', 'Como conectar no Wi-Fi', 1),
    ('Internet e redes', 'Como funciona uma rede de computadores', 2),
    ('Segurança no dia a dia', 'Como criar senhas fortes', 1),
    ('Segurança no dia a dia', 'Como fazer backup de arquivos', 2),
    ('Segurança no dia a dia', 'Como atualizar o Windows', 3),
    ('Proteção de contas e acessos', 'Como criar senhas fortes', 1),
    ('Proteção de contas e acessos', 'Como usar autenticação multifator com segurança', 2),
    ('Proteção de contas e acessos', 'Como ataques contra credenciais funcionam', 3),
    ('Defesa contra phishing', 'Como reconhecer links e e-mails de phishing', 1),
    ('Defesa contra phishing', 'Como verificar remetentes, links e páginas de login', 2),
    ('Defesa contra phishing', 'Como funcionam ataques de phishing direcionado', 3),
    ('Engenharia social e falso suporte', 'Como reconhecer falso suporte técnico', 1),
    ('Engenharia social e falso suporte', 'Como agir diante de pedidos de acesso remoto', 2),
    ('Engenharia social e falso suporte', 'Como a engenharia social explora confiança e autoridade', 3)
) AS v(trilha, tutorial, ordem)
JOIN trilhas tr ON tr.titulo = v.trilha
JOIN tutoriais tu ON tu.titulo = v.tutorial
WHERE NOT EXISTS (SELECT 1 FROM trilha_tutoriais tt WHERE tt.trilha_id = tr.id AND tt.tutorial_id = tu.id);

-- Dicas
INSERT INTO dicas (titulo, conteudo, categoria)
SELECT v.titulo, v.conteudo, v.categoria
FROM (VALUES
    ('Bloqueie a tela ao sair da mesa', 'Use o atalho Windows + L sempre que se afastar do computador. Assim ninguém acessa seus e-mails e sistemas no seu nome, mesmo que seja só por alguns minutos.', 'Segurança'),
    ('Desconfie de links urgentes', 'Mensagens que pedem ação imediata, como confirmar senha ou evitar bloqueio da conta, são a forma mais comum de golpe. Na dúvida, não clique e confirme com o NTI por outro canal.', 'Segurança'),
    ('Reinicie de vez em quando', 'Deixar o computador ligado por dias acumula programas em segundo plano e deixa tudo mais lento. Reiniciar uma vez por semana também conclui atualizações pendentes.', 'Manutenção'),
    ('Use Ctrl + Shift + T no navegador', 'Fechou uma aba sem querer? Esse atalho reabre a última aba fechada, com o histórico de navegação dela.', 'Internet'),
    ('Copie e cole sem formatação', 'Ao colar texto vindo da internet, use Ctrl + Shift + V para colar só o texto, sem cores e fontes diferentes.', 'Programas'),
    ('Nomeie arquivos com data', 'Comece o nome do arquivo com a data no formato ano-mês-dia, como 2026-03-15 Relatorio. Assim eles ficam em ordem cronológica automaticamente.', 'Computador')
) AS v(titulo, conteudo, categoria)
WHERE NOT EXISTS (SELECT 1 FROM dicas d WHERE d.titulo = v.titulo);

-- Glossario
INSERT INTO glossario (termo, definicao, categoria)
SELECT v.termo, v.definicao, v.categoria
FROM (VALUES
    ('Backup', 'Cópia de segurança de arquivos, usada para recuperá-los em caso de perda, defeito ou ataque.', 'Segurança'),
    ('CPU', 'Unidade central de processamento. É o componente que executa as instruções dos programas, considerado o cérebro do computador.', 'Computador'),
    ('DNS', 'Sistema que traduz nomes de sites, como www.exemplo.com, para os endereços IP que os computadores usam.', 'Redes'),
    ('Endereço IP', 'Número que identifica um dispositivo em uma rede, como um endereço de casa.', 'Redes'),
    ('Firewall', 'Barreira que controla o tráfego entre redes e bloqueia conexões não autorizadas.', 'Segurança'),
    ('Memória RAM', 'Memória temporária usada pelos programas abertos. É apagada quando o computador desliga.', 'Computador'),
    ('Phishing', 'Golpe em que o criminoso se passa por alguém confiável para roubar senhas ou dados.', 'Segurança'),
    ('Roteador', 'Equipamento que encaminha os dados entre redes, por exemplo entre a rede interna e a internet.', 'Redes'),
    ('Sistema operacional', 'Programa principal do computador, que gerencia o hardware e roda os outros programas. Exemplo: Windows.', 'Computador'),
    ('Wi-Fi', 'Tecnologia de rede sem fio que conecta dispositivos usando ondas de rádio.', 'Internet')
) AS v(termo, definicao, categoria)
WHERE NOT EXISTS (SELECT 1 FROM glossario g WHERE g.termo = v.termo);

-- Exercicios (alternativas A-D e a correta)
INSERT INTO exercicios (tutorial_id, pergunta, alternativa_a, alternativa_b, alternativa_c, alternativa_d, resposta_correta, explicacao)
SELECT tu.id, v.pergunta, v.a, v.b, v.c, v.d, v.correta, v.explicacao
FROM (VALUES
    ('Como ligar e desligar o computador corretamente', 'Qual é a forma recomendada de desligar o computador?', 'Tirar da tomada', 'Segurar o botão de energia', 'Usar a opção Desligar do menu Iniciar', 'Fechar a tampa do monitor', 'C', 'O menu Iniciar dá tempo ao Windows para salvar arquivos e concluir atualizações.'),
    ('Como ligar e desligar o computador corretamente', 'Quando é aceitável segurar o botão de energia?', 'Sempre que quiser desligar rápido', 'Quando o computador travar por completo', 'Durante uma atualização', 'Nunca há problema', 'B', 'Forçar o desligamento só deve ser usado quando o sistema não responde.'),
    ('Como criar e organizar pastas', 'Qual atalho abre o Explorador de Arquivos?', 'Windows + E', 'Windows + L', 'Ctrl + P', 'Alt + F4', 'A', 'Windows + E abre o Explorador. Windows + L bloqueia a tela.'),
    ('Como conectar no Wi-Fi', 'Qual cuidado é importante ao usar Wi-Fi fora da empresa?', 'Preferir redes abertas', 'Evitar redes públicas para acessar sistemas da empresa', 'Desligar o antivírus', 'Compartilhar a senha da rede', 'B', 'Redes públicas podem ser monitoradas por terceiros.'),
    ('Como instalar um programa', 'De onde você deve baixar um programa?', 'De qualquer site de downloads', 'De um link recebido por mensagem', 'Do site oficial, com liberação do NTI', 'De um anúncio no navegador', 'C', 'Sites oficiais reduzem o risco de instalar vírus junto com o programa.'),
    ('Como funciona uma rede de computadores', 'O que o DNS faz?', 'Protege contra vírus', 'Traduz nomes de sites em endereços IP', 'Aumenta a velocidade do Wi-Fi', 'Guarda cópias dos arquivos', 'B', 'O DNS funciona como uma agenda que converte nomes em endereços IP.'),
    ('Como funciona uma rede de computadores', 'Qual equipamento encaminha dados entre a rede interna e a internet?', 'Monitor', 'Teclado', 'Roteador', 'Impressora', 'C', 'O roteador liga redes diferentes e encaminha os pacotes de dados.'),
    ('Como atualizar o Windows', 'Por que manter o Windows atualizado?', 'Para mudar o papel de parede', 'Para corrigir falhas de segurança', 'Para liberar espaço no disco', 'Não faz diferença', 'B', 'Atualizações fecham brechas que vírus usam para invadir o computador.'),
    ('Como criar senhas fortes', 'Qual destas senhas é mais segura?', '123456', 'nome da empresa', 'Cafe Azul Na Janela 42!', 'data de nascimento', 'C', 'Uma frase longa com palavras sem relação entre si é menos previsível. O exemplo é apenas ilustrativo e não deve ser reutilizado.'),
    ('Como criar senhas fortes', 'Alguém do NTI pediu sua senha por telefone. O que fazer?', 'Informar a senha', 'Recusar e avisar o NTI pelos canais oficiais', 'Enviar por e-mail', 'Trocar a senha e informar a nova', 'B', 'A equipe do NTI nunca pede sua senha. Pedidos assim são tentativas de golpe.'),
    ('Como criar senhas fortes', 'Por que usar uma senha diferente em cada conta?', 'Para facilitar a digitação', 'Para impedir que um vazamento comprometa as demais contas', 'Para não precisar de MFA', 'Para evitar atualizações', 'B', 'Senhas exclusivas limitam o impacto de um vazamento e impedem a reutilização automatizada de credenciais.'),
    ('Como usar autenticação multifator com segurança', 'Você recebeu uma solicitação de aprovação que não iniciou. O que deve fazer?', 'Aprovar para a mensagem parar', 'Informar o código ao suporte', 'Negar e avisar o NTI pelos canais oficiais', 'Ignorar e aprovar a próxima', 'C', 'Uma solicitação inesperada pode indicar que alguém já possui sua senha e tenta concluir o acesso.'),
    ('Como usar autenticação multifator com segurança', 'Qual informação também deve permanecer secreta?', 'O nome do setor', 'O código temporário de autenticação', 'O modelo do computador', 'O horário de trabalho', 'B', 'Códigos temporários confirmam a identidade e não devem ser compartilhados com ninguém.'),
    ('Como ataques contra credenciais funcionam', 'O que é credential stuffing?', 'Testar credenciais vazadas em outros serviços', 'Enviar muitas notificações de atualização', 'Copiar arquivos para um backup', 'Bloquear a tela automaticamente', 'A', 'O ataque explora a reutilização da mesma senha em contas diferentes.'),
    ('Como ataques contra credenciais funcionam', 'O que caracteriza MFA fatigue?', 'Uma senha muito longa', 'Atualizações frequentes do sistema', 'Solicitações repetidas para induzir uma aprovação indevida', 'Uso de uma chave de segurança', 'C', 'O atacante insiste em notificações esperando que a vítima aprove uma delas por cansaço ou engano.'),
    ('Como reconhecer links e e-mails de phishing', 'Uma mensagem diz que sua conta será bloqueada e pede login imediato por um link. O que fazer?', 'Abrir o link para evitar o bloqueio', 'Responder com a senha', 'Acessar o sistema pelo endereço conhecido e confirmar com o NTI', 'Encaminhar o link para colegas testarem', 'C', 'Urgência e ameaça de bloqueio são sinais comuns. Use o endereço oficial que você já conhece e confirme por um canal seguro.'),
    ('Como reconhecer links e e-mails de phishing', 'O nome exibido é Unidade Local, mas o endereço pertence a um domínio desconhecido. O que isso indica?', 'A mensagem está garantida pelo nome exibido', 'É um sinal de possível falsificação', 'O endereço não importa quando há logotipo', 'A unidade mudou de domínio automaticamente', 'B', 'O nome exibido e o logotipo podem ser copiados. O endereço completo do remetente precisa ser conferido.'),
    ('Como reconhecer links e e-mails de phishing', 'Você clicou e digitou sua senha em uma página suspeita. Qual deve ser a primeira atitude?', 'Esperar para ver se algo acontece', 'Apagar o histórico e não contar a ninguém', 'Avisar imediatamente o NTI por um canal oficial', 'Responder ao remetente pedindo explicações', 'C', 'O relato imediato permite iniciar a proteção da conta e reduzir o impacto. Não esconda o incidente.'),
    ('Como verificar remetentes, links e páginas de login', 'Ao passar o ponteiro sobre um link, o destino mostrado é diferente do texto. O que fazer?', 'Não abrir e reportar a mensagem', 'Abrir para conferir a página', 'Copiar o link para outro navegador', 'Desativar o antivírus antes de abrir', 'A', 'A diferença entre o texto e o destino é um forte sinal de alerta. Não teste o endereço.'),
    ('Como verificar remetentes, links e páginas de login', 'Um colega conhecido enviou um link inesperado pedindo sua senha. Qual é a resposta mais segura?', 'Confiar porque o remetente é conhecido', 'Confirmar o pedido com o colega por outro canal oficial', 'Enviar apenas parte da senha', 'Abrir o link em modo anônimo', 'B', 'Contas legítimas também podem ser comprometidas. Pedidos inesperados devem ser confirmados por outro canal.'),
    ('Como verificar remetentes, links e páginas de login', 'Como deve ser tratado um QR Code inesperado que promete acesso a um documento interno?', 'Como seguro porque não mostra um link', 'Como um link que pode esconder o destino', 'Como seguro se estiver em PDF', 'Como uma atualização obrigatória', 'B', 'QR Codes podem levar às mesmas páginas falsas usadas em links comuns.'),
    ('Como funcionam ataques de phishing direcionado', 'Uma mensagem cita sua unidade, sua chefia e uma atividade real. Isso prova que ela é legítima?', 'Sim, somente pessoas internas saberiam', 'Não, informações públicas ou vazadas podem ser usadas no golpe', 'Sim, se a mensagem estiver bem escrita', 'Sim, se tiver assinatura e logotipo', 'B', 'Ataques direcionados usam contexto real para aumentar a confiança. O pedido ainda precisa ser verificado.'),
    ('Como funcionam ataques de phishing direcionado', 'Por que uma mensagem vinda de uma conta verdadeira ainda pode ser perigosa?', 'Porque contas conhecidas também podem ser comprometidas', 'Porque todo e-mail interno é bloqueado', 'Porque mensagens verdadeiras não possuem links', 'Porque o domínio muda diariamente', 'A', 'O invasor pode usar uma conta comprometida para continuar conversas e atacar os contatos daquela pessoa.'),
    ('Como funcionam ataques de phishing direcionado', 'Por que é importante reportar mesmo depois de clicar?', 'Para receber uma nova mensagem', 'Para que o NTI possa agir rapidamente e proteger outras contas', 'Porque o navegador apaga o link', 'Para confirmar que o site estava funcionando', 'B', 'A resposta rápida ajuda a conter o incidente, bloquear o golpe e alertar outras pessoas.'),
    ('Como reconhecer falso suporte técnico', 'Uma pessoa liga sem chamado aberto, diz ser do NTI e pede sua senha. O que fazer?', 'Informar porque ela conhece seu nome', 'Pedir que confirme parte da senha', 'Encerrar e procurar o NTI pelo canal oficial', 'Enviar a senha por mensagem depois', 'C', 'O NTI não precisa da senha do usuário. Um contato inesperado deve ser interrompido e confirmado de forma independente.'),
    ('Como reconhecer falso suporte técnico', 'Um alerta no navegador mostra um telefone para remover um vírus. Qual é a atitude segura?', 'Ligar imediatamente para o número', 'Fechar o alerta e procurar o NTI pelo contato conhecido', 'Instalar o programa oferecido', 'Compartilhar o alerta em grupos', 'B', 'Pop-ups podem imitar avisos técnicos. Não ligue para o número exibido nem instale o que a página oferece.'),
    ('Como reconhecer falso suporte técnico', 'Qual informação um técnico legítimo não precisa pedir?', 'Descrição do problema', 'Número do patrimônio', 'Senha e código de autenticação', 'Horário em que o erro ocorreu', 'C', 'Senha e código de MFA são secretos. O suporte pode investigar sem conhecer essas informações.'),
    ('Como agir diante de pedidos de acesso remoto', 'Quando um pedido de acesso remoto pode ser aceito?', 'Sempre que a pessoa usar linguagem técnica', 'Quando houver atendimento confirmado pelo canal oficial', 'Quando chegar por WhatsApp', 'Quando o aplicativo for gratuito', 'B', 'O acesso deve estar ligado a um atendimento real e a identidade do técnico precisa ser confirmada pelo procedimento oficial.'),
    ('Como agir diante de pedidos de acesso remoto', 'Durante a sessão, o técnico tenta abrir documentos sem relação com o problema. O que fazer?', 'Deixar porque ele controla o computador', 'Informar também a senha dos arquivos', 'Interromper a sessão e contatar o NTI', 'Sair da mesa para não atrapalhar', 'C', 'Ações fora do atendimento são um sinal de risco. Interrompa a conexão e confirme a situação por outro canal.'),
    ('Como agir diante de pedidos de acesso remoto', 'Você forneceu um código de acesso remoto a um contato suspeito. Qual é a melhor resposta?', 'Esperar o contato terminar', 'Encerrar a ferramenta se for seguro e avisar imediatamente o NTI', 'Apagar apenas a conversa', 'Reiniciar e não contar a ninguém', 'B', 'A resposta rápida permite verificar o equipamento e conter possíveis acessos indevidos.'),
    ('Como a engenharia social explora confiança e autoridade', 'Por que citar o nome de uma chefia não prova que o pedido é verdadeiro?', 'Porque nomes e funções podem ser obtidos em fontes públicas ou vazamentos', 'Porque chefias nunca usam telefone', 'Porque todo pedido urgente é verdadeiro', 'Porque apenas o NTI conhece nomes', 'A', 'Informações reais são usadas para tornar histórias falsas mais convincentes.'),
    ('Como a engenharia social explora confiança e autoridade', 'O suspeito envia e-mail e depois liga para confirmar o próprio pedido. Isso é validação por segundo canal?', 'Sim, porque foram usados dois meios', 'Não, porque os dois contatos foram controlados pelo solicitante', 'Sim, se a voz parecer conhecida', 'Sim, se houver logotipo', 'B', 'A confirmação deve partir de você usando um número ou contato obtido em fonte oficial e independente.'),
    ('Como a engenharia social explora confiança e autoridade', 'Qual atitude reduz o impacto depois de uma possível manipulação?', 'Esconder o ocorrido', 'Apagar tudo antes de avisar', 'Reportar rapidamente e seguir a orientação do NTI', 'Continuar conversando para investigar sozinho', 'C', 'O relato rápido permite conter o incidente, preservar informações úteis e alertar outras pessoas.'),
    ('Como fazer backup de arquivos', 'Qual é o melhor destino para o backup de arquivos de trabalho?', 'Um pendrive pessoal', 'A área de trabalho', 'A pasta de rede indicada pelo NTI', 'A lixeira', 'C', 'A pasta de rede tem cópias e controle de acesso mantidos pela equipe.')
) AS v(tutorial, pergunta, a, b, c, d, correta, explicacao)
JOIN tutoriais tu ON tu.titulo = v.tutorial
WHERE NOT EXISTS (SELECT 1 FROM exercicios e WHERE e.tutorial_id = tu.id AND e.pergunta = v.pergunta);
