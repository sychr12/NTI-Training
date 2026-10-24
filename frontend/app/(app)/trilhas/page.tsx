"use client";

import { ChevronDown, ChevronRight, CircleCheck, Route } from "lucide-react";
import Link from "next/link";
import { useMemo, useState } from "react";
import { EmptyState, ErrorState, IconBadge, LoadingState, PageHeading, ProgressTrack } from "../../components/ui";
import { useApi } from "../../lib/api";
import { categoryVisual, levelLabel } from "../../lib/catalog";
import { useProgress } from "../../lib/progress";
import type { ProgressoTrilha, Trilha } from "../../lib/types";

export default function TrailsPage() {
  const trails = useApi<Trilha[]>("/api/trilhas");
  const trailProgress = useApi<ProgressoTrilha[]>("/api/progresso/trilhas");
  const { byTutorial } = useProgress();
  const [open, setOpen] = useState<number | null>(null);

  const percentByTrail = useMemo(() => new Map((trailProgress.data ?? []).map((item) => [item.trilhaId, item.percentual])), [trailProgress.data]);

  return <div className="page-stack">
    <PageHeading title="Trilhas de aprendizado" description="Siga uma trilha e aprenda passo a passo." />
    {trails.error ? <ErrorState error={trails.error} onRetry={trails.reload} />
      : !trails.data ? <LoadingState rows={4} />
        : trails.data.length === 0 ? <EmptyState icon={Route} title="Nenhuma trilha publicada" description="As trilhas montadas pela equipe aparecerão aqui." />
          : <div className="trail-list">{trails.data.map((trail) => {
            const percent = percentByTrail.get(trail.id) ?? 0;
            const expanded = open === trail.id;
            const tutorials = [...trail.tutoriais].sort((a, b) => a.ordem - b.ordem);
            return <article className={`trail-card ${expanded ? "expanded" : ""}`} key={trail.id}>
              <button className="trail-summary" onClick={() => setOpen(expanded ? null : trail.id)} aria-expanded={expanded}>
                <IconBadge visual={categoryVisual(tutorials[0]?.categoria)} />
                <div className="trail-copy"><strong>{trail.titulo}</strong><p>{trail.descricao ?? `Trilha de nível ${levelLabel(trail.nivel).toLowerCase()}.`}</p></div>
                <div className="trail-stat"><span>{tutorials.length} {tutorials.length === 1 ? "tutorial" : "tutoriais"}</span><ProgressTrack value={percent} /><b>{percent}%</b></div>
                {expanded ? <ChevronDown size={19} /> : <ChevronRight size={19} />}
              </button>
              {expanded && <ol className="trail-steps">{tutorials.map((item) => {
                const done = byTutorial.get(item.id) === "CONCLUIDO";
                return <li key={item.id} className={done ? "done" : ""}>
                  <span>{done ? <CircleCheck size={16} /> : item.ordem}</span>
                  <Link href={`/tutoriais/${item.id}`}>{item.titulo}</Link>
                  <small>{levelLabel(item.nivel)}</small>
                </li>;
              })}</ol>}
            </article>;
          })}</div>}
  </div>;
}
