"use client";

import { useEffect, useSyncExternalStore } from "react";
import { apiFetch, PROGRESS_CHANGED_EVENT } from "./api";
import type { Conquistas, Medalha } from "./types";

// Conquistas do usuário (XP, nível, sequência e medalhas), compartilhadas entre todos os componentes.
// "toasts" guarda as medalhas recém-conquistadas que ainda precisam ser anunciadas na tela.
type State = { status: "idle" | "loading" | "ready" | "error"; data?: Conquistas; toasts: Medalha[] };

const INITIAL: State = { status: "idle", toasts: [] };
let state: State = INITIAL;
const listeners = new Set<() => void>();
let listeningProgress = false;

function setState(next: State) {
  state = next;
  listeners.forEach((listener) => listener());
}

async function fetchAchievements() {
  if (state.status === "loading") return;
  setState({ ...state, status: "loading" });
  try {
    const data = await apiFetch<Conquistas>("/api/conquistas");
    const fresh = data.medalhas.filter((medalha) => medalha.nova);
    setState({
      status: "ready",
      data: { ...data, medalhas: data.medalhas.map((medalha) => ({ ...medalha, nova: false })) },
      toasts: [...state.toasts, ...fresh],
    });
    // Cada medalha é anunciada uma única vez, em qualquer computador.
    if (fresh.length > 0) await apiFetch("/api/conquistas/vistas", { method: "POST" }).catch(() => undefined);
  }
  catch {
    setState({ ...state, status: "error" });
  }
}

function subscribe(listener: () => void) {
  listeners.add(listener);
  // Concluir tutorial ou responder exercício pode render XP e medalhas: recarrega.
  if (!listeningProgress) {
    listeningProgress = true;
    window.addEventListener(PROGRESS_CHANGED_EVENT, () => { if (state.status !== "idle") void fetchAchievements(); });
  }
  return () => { listeners.delete(listener); };
}

export function resetAchievements() {
  setState(INITIAL);
}

export function dismissAchievementToast(codigo: string) {
  setState({ ...state, toasts: state.toasts.filter((medalha) => medalha.codigo !== codigo) });
}

export function useAchievements() {
  const current = useSyncExternalStore(subscribe, () => state, () => INITIAL);
  useEffect(() => { if (state.status === "idle") void fetchAchievements(); }, []);
  return { data: current.data, toasts: current.toasts, error: current.status === "error" && !current.data, reload: fetchAchievements };
}

/** Porcentagem do caminho até o próximo nível (100 no nível máximo). */
export function levelProgress(data: Conquistas) {
  const { xpInicio, xpProximo } = data.nivel;
  if (xpProximo === null) return 100;
  return Math.round(((data.xp - xpInicio) / (xpProximo - xpInicio)) * 100);
}

export function streakMessage(sequencia: Conquistas["sequencia"]) {
  if (sequencia.estudouHoje) return "Você já estudou hoje. Mandou bem!";
  if (sequencia.atual > 0) return "Estude hoje para não perder a sequência.";
  return "Estude hoje para começar uma sequência.";
}
