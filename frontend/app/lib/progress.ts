"use client";

import { useEffect, useMemo } from "react";
import { PROGRESS_CHANGED_EVENT, useApi } from "./api";
import type { ProgressoTutorial } from "./types";

/** Progresso do usuário em cada tutorial (tutorialId → progresso), atualizado quando o progresso muda. */
export function useProgress() {
  const progress = useApi<ProgressoTutorial[]>("/api/progresso");
  const { reload } = progress;

  useEffect(() => {
    window.addEventListener(PROGRESS_CHANGED_EVENT, reload);
    return () => window.removeEventListener(PROGRESS_CHANGED_EVENT, reload);
  }, [reload]);

  const byTutorial = useMemo(() => new Map<number, ProgressoTutorial>((progress.data ?? []).map((item) => [item.tutorialId, item])), [progress.data]);
  return { ...progress, byTutorial };
}

const DAY = 24 * 60 * 60 * 1000;

export function relativeDate(iso: string) {
  const date = new Date(iso);
  const startOfToday = new Date();
  startOfToday.setHours(0, 0, 0, 0);
  const days = Math.ceil((startOfToday.getTime() - date.getTime()) / DAY);
  if (days <= 0) return "Hoje";
  if (days === 1) return "Ontem";
  if (days < 30) return `${days} dias atrás`;
  return date.toLocaleDateString("pt-BR", { day: "2-digit", month: "short", year: "numeric" });
}
