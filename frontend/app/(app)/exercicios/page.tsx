"use client";

import { ChevronDown, ClipboardCheck, CirclePlay } from "lucide-react";
import Link from "next/link";
import { useMemo, useState } from "react";
import { Quiz } from "../../components/Quiz";
import { EmptyState, ErrorState, IconBadge, LoadingState, PageHeading } from "../../components/ui";
import { useApi } from "../../lib/api";
import { categoryVisual, levelLabel } from "../../lib/catalog";
import type { Tutorial } from "../../lib/types";

const ALL = "Todos";

export default function ExercisesPage() {
  const tutorials = useApi<Tutorial[]>("/api/tutoriais");
  const [category, setCategory] = useState(ALL);
  const [open, setOpen] = useState<number | null>(null);

  const categories = useMemo(() => [ALL, ...new Set((tutorials.data ?? []).map((tutorial) => tutorial.categoria ?? "Geral"))], [tutorials.data]);
  const filtered = (tutorials.data ?? []).filter((tutorial) => category === ALL || (tutorial.categoria ?? "Geral") === category);

  return <div className="page-stack">
    <PageHeading title="Exercícios" description="Pratique o que você aprendeu em cada tutorial." />
    {tutorials.error ? <ErrorState error={tutorials.error} onRetry={tutorials.reload} />
      : !tutorials.data ? <LoadingState rows={4} />
        : tutorials.data.length === 0 ? <EmptyState icon={ClipboardCheck} title="Nenhum exercício disponível" description="Os exercícios aparecem junto com os tutoriais publicados." />
          : <>
            <div className="filter-pills page-filters">{categories.map((item) => <button key={item} className={category === item ? "active" : ""} aria-pressed={category === item} onClick={() => setCategory(item)}>{item}</button>)}</div>
            <div className="exercise-list">{filtered.map((tutorial) => {
              const expanded = open === tutorial.id;
              return <article key={tutorial.id} className={expanded ? "expanded" : ""}>
                <div className="exercise-head">
                  <IconBadge visual={categoryVisual(tutorial.categoria)} />
                  <span><strong>Quiz: {tutorial.titulo}</strong><small>{tutorial.categoria ?? "Geral"}<i className="level-badge">{levelLabel(tutorial.nivel)}</i><Link href={`/tutoriais/${tutorial.id}`}>Ver tutorial</Link></small></span>
                  <button className="secondary-button" onClick={() => setOpen(expanded ? null : tutorial.id)} aria-expanded={expanded}>
                    {expanded ? <>Fechar <ChevronDown size={16} /></> : <>Iniciar <CirclePlay size={16} /></>}
                  </button>
                </div>
                {expanded && <div className="exercise-body"><Quiz tutorialId={tutorial.id} /></div>}
              </article>;
            })}</div>
          </>}
  </div>;
}
