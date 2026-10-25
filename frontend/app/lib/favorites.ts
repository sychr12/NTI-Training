"use client";

import { useEffect, useSyncExternalStore } from "react";
import { apiFetch } from "./api";
import type { Favorito } from "./types";

// Favoritos do usuário, salvos no servidor (/api/favoritos) e compartilhados entre todos os componentes.
type State = { status: "idle" | "loading" | "ready"; ids: number[] };

const INITIAL: State = { status: "idle", ids: [] };
// Antes os favoritos ficavam só no navegador; na primeira carga eles são enviados ao servidor.
const LEGACY_KEY = "nti-favoritos";

let state: State = INITIAL;
const listeners = new Set<() => void>();

function setState(next: State) {
  state = next;
  listeners.forEach((listener) => listener());
}

function subscribe(listener: () => void) {
  listeners.add(listener);
  return () => { listeners.delete(listener); };
}

async function migrateLegacy(ids: number[]) {
  let legacy: number[] = [];
  try { legacy = JSON.parse(localStorage.getItem(LEGACY_KEY) ?? "[]"); } catch { /* valor inválido */ }
  const missing = legacy.filter((id) => Number.isFinite(id) && !ids.includes(id));
  // Tutoriais removidos respondem 404: são simplesmente ignorados.
  const migrated = await Promise.all(missing.map((id) => apiFetch(`/api/favoritos/${id}`, { method: "PUT" }).then(() => id, () => null)));
  try { localStorage.removeItem(LEGACY_KEY); } catch { /* armazenamento indisponível */ }
  return [...migrated.filter((id): id is number => id !== null), ...ids];
}

async function load() {
  if (state.status !== "idle") return;
  setState({ ...state, status: "loading" });
  try {
    const favorites = await apiFetch<Favorito[]>("/api/favoritos");
    setState({ status: "ready", ids: await migrateLegacy(favorites.map((favorite) => favorite.tutorialId)) });
  }
  catch {
    // Volta para "idle" para tentar de novo quando outro componente montar.
    setState(INITIAL);
  }
}

/** Limpa os favoritos em memória (ao sair ou trocar de usuário). */
export function resetFavorites() {
  setState(INITIAL);
}

async function toggle(id: number) {
  const wasFavorite = state.ids.includes(id);
  const previous = state.ids;
  setState({ ...state, ids: wasFavorite ? previous.filter((value) => value !== id) : [id, ...previous] });
  try {
    await apiFetch(`/api/favoritos/${id}`, { method: wasFavorite ? "DELETE" : "PUT" });
  }
  catch {
    setState({ ...state, ids: previous });
  }
}

export function useFavorites() {
  const current = useSyncExternalStore(subscribe, () => state, () => INITIAL);
  useEffect(() => { void load(); }, []);
  return {
    ids: current.ids,
    ready: current.status === "ready",
    isFavorite: (id: number) => current.ids.includes(id),
    toggle,
  };
}
