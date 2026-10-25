"use client";

import { useEffect, useRef, useState, type RefObject } from "react";

const VISIBLE_RATIO = 0.6;
const DWELL_MS = 1200;

/**
 * Fração (0 a 1) dos blocos [data-track] de `container` que já foram lidos.
 * Um bloco conta como lido depois de ficar ao menos 1,2 s visível, então rolar direto até o fim não vale.
 * Os blocos lidos são lembrados enquanto a página estiver aberta, mesmo trocando de aba.
 */
export function useReadingProgress(container: RefObject<HTMLElement | null>, active: boolean) {
  const seen = useRef(new Set<number>());
  const [read, setRead] = useState(0);

  useEffect(() => {
    const root = container.current;
    if (!active || !root) return;

    const blocks = [...root.querySelectorAll<HTMLElement>("[data-track]")];
    if (blocks.length === 0) return;
    const timers = new Map<Element, ReturnType<typeof setTimeout>>();

    const observer = new IntersectionObserver((entries) => {
      for (const entry of entries) {
        const index = blocks.indexOf(entry.target as HTMLElement);
        if (entry.isIntersecting && entry.intersectionRatio >= VISIBLE_RATIO) {
          if (seen.current.has(index) || timers.has(entry.target)) continue;
          timers.set(entry.target, setTimeout(() => {
            timers.delete(entry.target);
            seen.current.add(index);
            setRead(seen.current.size / blocks.length);
            observer.unobserve(entry.target);
          }, DWELL_MS));
        }
        else {
          clearTimeout(timers.get(entry.target));
          timers.delete(entry.target);
        }
      }
    }, { threshold: [VISIBLE_RATIO] });

    blocks.forEach((block, index) => { if (!seen.current.has(index)) observer.observe(block); });
    return () => {
      observer.disconnect();
      timers.forEach((timer) => clearTimeout(timer));
    };
  }, [container, active]);

  return read;
}
