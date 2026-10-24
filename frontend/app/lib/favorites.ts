"use client";

import { useSyncExternalStore } from "react";

// Favoritos ficam no navegador: o backend ainda não tem API para isso.
const STORAGE_KEY = "nti-favoritos";
const EMPTY: number[] = [];
const listeners = new Set<() => void>();
let cache: { raw: string | null; ids: number[] } = { raw: null, ids: EMPTY };

function read(): number[] {
  let raw: string | null = null;
  try { raw = localStorage.getItem(STORAGE_KEY); } catch { /* armazenamento indisponível */ }
  if (raw !== cache.raw) {
    let ids = EMPTY;
    try { ids = raw ? (JSON.parse(raw) as number[]).filter(Number.isFinite) : EMPTY; } catch { /* valor inválido */ }
    cache = { raw, ids };
  }
  return cache.ids;
}

function subscribe(listener: () => void) {
  listeners.add(listener);
  window.addEventListener("storage", listener);
  return () => {
    listeners.delete(listener);
    window.removeEventListener("storage", listener);
  };
}

export function useFavorites() {
  const ids = useSyncExternalStore(subscribe, read, () => EMPTY);

  function toggle(id: number) {
    const next = ids.includes(id) ? ids.filter((value) => value !== id) : [...ids, id];
    try { localStorage.setItem(STORAGE_KEY, JSON.stringify(next)); } catch { /* armazenamento indisponível */ }
    listeners.forEach((listener) => listener());
  }

  return { ids, isFavorite: (id: number) => ids.includes(id), toggle };
}
