"use client";

import { Bookmark, CircleCheck, ChevronRight, Clock3, CirclePlay } from "lucide-react";
import Link from "next/link";
import { categoryVisual, levelLabel, readingTime } from "../lib/catalog";
import { useFavorites } from "../lib/favorites";
import type { ProgressoTutorial, Tutorial } from "../lib/types";
import { IconBadge } from "./ui";

export function StatusChip({ progress }: { progress: ProgressoTutorial | undefined }) {
  if (progress?.status === "CONCLUIDO") return <span className="status-chip done"><CircleCheck size={13} />Concluído</span>;
  if (progress?.status === "EM_ANDAMENTO") return <span className="status-chip doing"><CirclePlay size={13} />Em andamento{progress.percentual > 0 && ` · ${progress.percentual}%`}</span>;
  return null;
}

export function FavoriteButton({ tutorialId }: { tutorialId: number }) {
  const { isFavorite, toggle, ready } = useFavorites();
  const active = isFavorite(tutorialId);
  const label = active ? "Remover dos favoritos" : "Adicionar aos favoritos";
  return <button className={`icon-button ${active ? "favorite-active" : ""}`} onClick={() => toggle(tutorialId)} disabled={!ready} aria-label={label} aria-pressed={active} title={label}>
    <Bookmark size={18} fill={active ? "currentColor" : "none"} />
  </button>;
}

export function TutorialRow({ tutorial, progress }: { tutorial: Tutorial; progress?: ProgressoTutorial }) {
  return <article className="tutorial-row">
    <Link className="tutorial-main" href={`/tutoriais/${tutorial.id}`}>
      <IconBadge visual={categoryVisual(tutorial.categoria)} />
      <span>
        <strong>{tutorial.titulo}</strong>
        <small>{tutorial.categoria ?? "Geral"}<i />{levelLabel(tutorial.nivel)}<i /><Clock3 size={13} />{readingTime(tutorial.conteudo)}<StatusChip progress={progress} /></small>
      </span>
    </Link>
    <FavoriteButton tutorialId={tutorial.id} />
    <ChevronRight size={18} />
  </article>;
}

/**
 * Exibe o texto do tutorial: blocos separados por linha em branco viram parágrafos;
 * linhas "1. ..." viram passo a passo e linhas "- ..." viram lista.
 * Cada parágrafo, lista e passo tem data-track, usado para medir o quanto já foi lido.
 */
export function LessonContent({ text }: { text: string }) {
  const blocks = text.replace(/\r\n/g, "\n").split(/\n\s*\n/).map((block) => block.trim()).filter(Boolean);
  return <div className="lesson-content">{blocks.map((block, index) => {
    const lines = block.split("\n").map((line) => line.trim()).filter(Boolean);
    if (lines.every((line) => /^\d+[.)]\s+/.test(line))) {
      return <ol key={index} className="step-list">{lines.map((line, step) => {
        const [title, ...rest] = line.replace(/^\d+[.)]\s+/, "").split(/:\s+/);
        return <li className="step-item" key={step} data-track><span>{step + 1}</span><div><strong>{title}</strong>{rest.length > 0 && <p>{rest.join(": ")}</p>}</div></li>;
      })}</ol>;
    }
    if (lines.every((line) => /^[-•]\s+/.test(line))) {
      return <ul key={index} data-track>{lines.map((line, item) => <li key={item}>{line.replace(/^[-•]\s+/, "")}</li>)}</ul>;
    }
    return <p key={index} data-track>{lines.join(" ")}</p>;
  })}</div>;
}
