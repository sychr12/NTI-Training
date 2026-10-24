// Formatos devolvidos pela API do backend (ver os DTOs em backend/src/main/java).

export type SessionUser = { id: number; login: string; nome: string | null; email: string | null; perfil: string };

export type Tutorial = {
  id: number;
  titulo: string;
  descricao: string | null;
  conteudo: string;
  categoria: string | null;
  nivel: string | null;
  ativo: boolean;
};

export type TrilhaTutorial = { id: number; titulo: string; descricao: string | null; categoria: string | null; nivel: string | null; ordem: number };
export type Trilha = { id: number; titulo: string; descricao: string | null; nivel: string; tutoriais: TrilhaTutorial[] };

export type Dica = { id: number; titulo: string; conteudo: string; categoria: string };
export type TermoGlossario = { id: number; termo: string; definicao: string; categoria: string };

export type Exercicio = { id: number; tutorialId: number; pergunta: string; alternativaA: string; alternativaB: string; alternativaC: string; alternativaD: string };
export type ResultadoExercicio = { correto: boolean; respostaCorreta: string; explicacao: string | null };

export type StatusProgresso = "EM_ANDAMENTO" | "CONCLUIDO";
export type ProgressoTutorial = { tutorialId: number; status: StatusProgresso; iniciadoEm: string; concluidoEm: string | null; atualizadoEm: string };
export type ProgressoTrilha = { trilhaId: number; titulo: string; totalTutoriais: number; tutoriaisConcluidos: number; percentual: number };

export type Home = {
  nomeUsuario: string;
  estatisticas: { cursosEmAndamento: number; cursosConcluido: number; exerciciosConcluidos: number; progressoGeral: number };
  continuarAprendendo: { id: number; titulo: string; descricao: string | null; progresso: number; tipo: string }[];
};

export type Perfil = {
  id: number;
  login: string;
  nome: string | null;
  email: string | null;
  perfil: string;
  estatistica: { tutoriaisIniciados: number; tutoriaisConcluidos: number; trilhasConcluidas: number; progressoGeral: number };
};
