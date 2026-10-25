"use client";

import { CircleCheck, ClipboardCheck, RotateCcw, CircleX } from "lucide-react";
import { useMemo, useState } from "react";
import { ApiError, apiFetch, notifyProgressChanged, useApi } from "../lib/api";
import type { Exercicio, MinhaResposta, ResultadoExercicio } from "../lib/types";
import { EmptyState, ErrorState, LoadingState } from "./ui";

const LETTERS = ["A", "B", "C", "D"] as const;
type Letter = (typeof LETTERS)[number];

function options(exercise: Exercicio): Record<Letter, string> {
  return { A: exercise.alternativaA, B: exercise.alternativaB, C: exercise.alternativaC, D: exercise.alternativaD };
}

type Answer = { letter: Letter; result: ResultadoExercicio };

/**
 * Exercícios de múltipla escolha de um tutorial; cada resposta é corrigida pela API.
 * Respostas dadas em visitas anteriores aparecem já corrigidas; "Refazer" limpa a tela para responder de novo.
 */
export function Quiz({ tutorialId, onAnswered }: { tutorialId: number; onAnswered?: (exerciseId: number) => void }) {
  const exercises = useApi<Exercicio[]>(`/api/exercicios?tutorialId=${tutorialId}`);
  const previous = useApi<MinhaResposta[]>(`/api/exercicios/minhas-respostas?tutorialId=${tutorialId}`);
  const [local, setLocal] = useState<Record<number, Answer>>({});
  const [restarted, setRestarted] = useState(false);
  const [sending, setSending] = useState<number | null>(null);
  const [error, setError] = useState("");

  const fromServer = useMemo(() => Object.fromEntries((previous.data ?? []).map((item) => [item.exercicioId, {
    letter: item.resposta as Letter,
    result: { correto: item.correto, respostaCorreta: item.respostaCorreta, explicacao: item.explicacao },
  }])) as Record<number, Answer>, [previous.data]);
  const answers = restarted ? local : { ...fromServer, ...local };

  async function answer(exercise: Exercicio, letter: Letter) {
    setSending(exercise.id);
    setError("");
    try {
      const result = await apiFetch<ResultadoExercicio>(`/api/exercicios/${exercise.id}/responder`, {
        method: "POST",
        body: JSON.stringify({ resposta: letter }),
      });
      setLocal((current) => ({ ...current, [exercise.id]: { letter, result } }));
      onAnswered?.(exercise.id);
      notifyProgressChanged();
    }
    catch (exception) {
      setError(exception instanceof ApiError ? exception.message : "Não foi possível enviar a resposta.");
    }
    finally {
      setSending(null);
    }
  }

  if (exercises.error) return <ErrorState error={exercises.error} onRetry={exercises.reload} />;
  if (!exercises.data || (!previous.data && !previous.error)) return <LoadingState rows={2} />;
  if (exercises.data.length === 0) {
    return <EmptyState icon={ClipboardCheck} title="Sem exercícios por enquanto" description="Este tutorial ainda não tem exercícios cadastrados." />;
  }

  const answered = Object.keys(answers).length;
  const correct = Object.values(answers).filter((item) => item.result.correto).length;
  const finished = answered === exercises.data.length;

  return <div className="quiz">
    {exercises.data.map((exercise, index) => {
      const given = answers[exercise.id];
      return <fieldset className="quiz-question" key={exercise.id} disabled={!!given || sending === exercise.id}>
        <legend><span>{index + 1}</span>{exercise.pergunta}</legend>
        <div className="quiz-options">{LETTERS.map((letter) => {
          const state = !given ? "" : letter === given.result.respostaCorreta ? "correct" : letter === given.letter ? "wrong" : "";
          return <button key={letter} type="button" className={`quiz-option ${state}`} onClick={() => answer(exercise, letter)}>
            <b>{letter}</b><span>{options(exercise)[letter]}</span>
            {state === "correct" && <CircleCheck size={18} />}{state === "wrong" && <CircleX size={18} />}
          </button>;
        })}</div>
        {given && <p className={`quiz-feedback ${given.result.correto ? "correct" : "wrong"}`}>
          <strong>{given.result.correto ? "Resposta certa!" : `Resposta correta: ${given.result.respostaCorreta}`}</strong>
          {given.result.explicacao && <span>{given.result.explicacao}</span>}
        </p>}
      </fieldset>;
    })}
    {error && <p className="login-error" role="alert">{error}</p>}
    {finished && <div className="quiz-summary">
      <strong>Você acertou {correct} de {exercises.data.length}.</strong>
      <button className="secondary-button" onClick={() => { setLocal({}); setRestarted(true); }}><RotateCcw size={15} /> Refazer</button>
    </div>}
  </div>;
}
