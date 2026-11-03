"use client";

import { BookOpenText, CircleCheck, ClipboardCheck, RotateCcw, CircleX } from "lucide-react";
import Link from "next/link";
import { useMemo, useState } from "react";
import { ApiError, apiFetch, notifyProgressChanged, useApi } from "../lib/api";
import type { Exercicio, MinhaResposta } from "../lib/types";
import { EmptyState, ErrorState, LoadingState } from "./ui";

const LETTERS = ["A", "B", "C", "D"] as const;
type Letter = (typeof LETTERS)[number];

function options(exercise: Exercicio): Record<Letter, string> {
  return { A: exercise.alternativaA, B: exercise.alternativaB, C: exercise.alternativaC, D: exercise.alternativaD };
}

/**
 * Exercícios de múltipla escolha de um tutorial; cada resposta é corrigida pela API.
 * Cada exercício tem 2 tentativas: errar a primeira não mostra a resposta certa, só a opção de reler e tentar de novo.
 * Acertar ou gastar as tentativas encerra o exercício e mostra a correção, também em visitas futuras.
 */
export function Quiz({ tutorialId, onAnswered, onReread }: { tutorialId: number; onAnswered?: (exerciseId: number) => void; onReread?: () => void }) {
  const exercises = useApi<Exercicio[]>(`/api/exercicios?tutorialId=${tutorialId}`);
  const previous = useApi<MinhaResposta[]>(`/api/exercicios/minhas-respostas?tutorialId=${tutorialId}`);
  const [local, setLocal] = useState<Record<number, MinhaResposta>>({});
  const [retrying, setRetrying] = useState<number[]>([]);
  const [sending, setSending] = useState<number | null>(null);
  const [error, setError] = useState("");

  const fromServer = useMemo(() => Object.fromEntries((previous.data ?? []).map((item) => [item.exercicioId, item])), [previous.data]);
  const answers: Record<number, MinhaResposta> = { ...fromServer, ...local };

  async function answer(exercise: Exercicio, letter: Letter) {
    setSending(exercise.id);
    setError("");
    try {
      const result = await apiFetch<MinhaResposta>(`/api/exercicios/${exercise.id}/responder`, {
        method: "POST",
        body: JSON.stringify({ resposta: letter }),
      });
      setLocal((current) => ({ ...current, [exercise.id]: result }));
      setRetrying((current) => current.filter((id) => id !== exercise.id));
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

  const closed = exercises.data.filter((exercise) => answers[exercise.id]?.encerrada);
  const correct = closed.filter((exercise) => answers[exercise.id].correto).length;

  return <div className="quiz">
    {exercises.data.map((exercise, index) => {
      const given = answers[exercise.id] as MinhaResposta | undefined;
      const isRetrying = !!given && !given.encerrada && retrying.includes(exercise.id);
      return <div className="quiz-item" key={exercise.id}>
        <fieldset className="quiz-question" disabled={(!!given && !isRetrying) || sending === exercise.id}>
          <legend><span>{index + 1}</span>{exercise.pergunta}</legend>
          <div className="quiz-options">{LETTERS.map((letter) => {
            const state = !given ? "" : letter === given.respostaCorreta ? "correct" : letter === given.resposta ? "wrong" : "";
            // Na segunda tentativa, a alternativa já errada não pode ser escolhida de novo.
            return <button key={letter} type="button" className={`quiz-option ${state}`} disabled={isRetrying && letter === given.resposta} onClick={() => answer(exercise, letter)}>
              <b>{letter}</b><span>{options(exercise)[letter]}</span>
              {state === "correct" && <CircleCheck size={18} />}{state === "wrong" && <CircleX size={18} />}
            </button>;
          })}</div>
        </fieldset>

        {isRetrying && <p className="quiz-feedback hint"><strong>Última tentativa: vale 5 XP.</strong></p>}
        {given && !given.encerrada && !isRetrying && <>
          <p className="quiz-feedback wrong" role="status">
            <strong>Resposta incorreta.</strong>
            <span>Releia o conteúdo e tente novamente. Você ainda tem 1 tentativa.</span>
          </p>
          <div className="quiz-retry">
            {onReread
              ? <button type="button" className="secondary-button" onClick={onReread}><BookOpenText size={15} /> Reler o conteúdo</button>
              : <Link className="secondary-button" href={`/tutoriais/${tutorialId}`}><BookOpenText size={15} /> Reler o conteúdo</Link>}
            <button type="button" className="primary-button" onClick={() => setRetrying((current) => [...current, exercise.id])}><RotateCcw size={15} /> Tentar de novo</button>
          </div>
        </>}
        {given?.encerrada && <p className={`quiz-feedback ${given.correto ? "correct" : "wrong"}`} role="status">
          <strong>{given.correto ? `Resposta certa! +${given.tentativas > 1 ? 5 : 10} XP` : `Resposta correta: ${given.respostaCorreta}`}</strong>
          {given.explicacao && <span>{given.explicacao}</span>}
        </p>}
      </div>;
    })}
    {error && <p className="login-error" role="alert">{error}</p>}
    {closed.length === exercises.data.length && <div className="quiz-summary">
      <strong>Você acertou {correct} de {exercises.data.length}.</strong>
    </div>}
  </div>;
}
