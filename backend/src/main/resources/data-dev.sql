-- Conteudo de exemplo para o perfil "dev" (ver application-dev.properties).
-- Cada insercao confere se o registro ja existe, entao o script pode rodar a cada inicializacao.
-- Evite ponto e virgula dentro dos textos: o Spring separa os comandos por ele.

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
       'Proteja suas contas com senhas difíceis de adivinhar e fáceis de lembrar.',
       'Senhas fracas são a forma mais comum de invasão de contas. Uma boa senha é longa, única e difícil de adivinhar.

1. Use uma frase: junte palavras que só você associa, como Cafe Azul Na Janela 42.
2. Misture caracteres: inclua letras maiúsculas, minúsculas, números e símbolos.
3. Não repita senhas: cada sistema deve ter a sua.
4. Troque quando suspeitar: se achar que alguém viu sua senha, altere na hora.

- Nunca anote a senha em papel colado no monitor.
- Ninguém do NTI vai pedir sua senha por e-mail ou telefone.',
       'Segurança', 'Iniciante'
WHERE NOT EXISTS (SELECT 1 FROM tutoriais WHERE titulo = 'Como criar senhas fortes');

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
    ('Segurança no dia a dia', 'Como atualizar o Windows', 3)
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
    ('Como criar senhas fortes', 'Qual destas senhas é mais segura?', '123456', 'nome da empresa', 'Cafe Azul Na Janela 42!', 'data de nascimento', 'C', 'Frases longas com números e símbolos são difíceis de adivinhar e fáceis de lembrar.'),
    ('Como criar senhas fortes', 'Alguém do NTI pediu sua senha por telefone. O que fazer?', 'Informar a senha', 'Recusar e avisar o NTI pelos canais oficiais', 'Enviar por e-mail', 'Trocar a senha e informar a nova', 'B', 'A equipe do NTI nunca pede sua senha. Pedidos assim são tentativas de golpe.'),
    ('Como fazer backup de arquivos', 'Qual é o melhor destino para o backup de arquivos de trabalho?', 'Um pendrive pessoal', 'A área de trabalho', 'A pasta de rede indicada pelo NTI', 'A lixeira', 'C', 'A pasta de rede tem cópias e controle de acesso mantidos pela equipe.')
) AS v(tutorial, pergunta, a, b, c, d, correta, explicacao)
JOIN tutoriais tu ON tu.titulo = v.tutorial
WHERE NOT EXISTS (SELECT 1 FROM exercicios e WHERE e.tutorial_id = tu.id AND e.pergunta = v.pergunta);
