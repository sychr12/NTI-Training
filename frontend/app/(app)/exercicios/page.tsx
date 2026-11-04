"use client";

import { ChevronDown, ClipboardCheck, CirclePlay } from "lucide-react";
import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { Suspense, useMemo, useState } from "react";
import { FilterSelect } from "../../components/FilterSelect";
import { Quiz } from "../../components/Quiz";
import { EmptyState, ErrorState, IconBadge, LoadingState, PageHeading } from "../../components/ui";
import { useApi } from "../../lib/api";
import { categoryVisual, LEVELS, levelLabel, normalizeText } from "../../lib/catalog";
import type { Tutorial } from "../../lib/types";

const ALL_CATEGORIES = "Todas";
const ALL_LEVELS = "Todos os níveis";

export default function ExercisesPage() {
  return <div className="page-stack">
    <PageHeading title="Exercícios" description="Pratique o que você aprendeu em cada tutorial." />
    <Suspense fallback={<LoadingState rows={4} />}><ExercisesFromUrl /></Suspense>
  </div>;
}

function ExercisesFromUrl() {
  const params = useSearchParams();
  const tutorials = useApi<Tutorial[]>("/api/tutoriais");
  const [open, setOpen] = useState<number | null>(null);
  const category = params.get("categoria") ?? ALL_CATEGORIES;
  const level = params.get("nivel") ?? ALL_LEVELS;

  const categories = useMemo(() => [ALL_CATEGORIES, ...new Set((tutorials.data ?? []).map((tutorial) => tutorial.categoria ?? "Geral"))], [tutorials.data]);
  const filtered = useMemo(() => (tutorials.data ?? []).filter((tutorial) =>
    (category === ALL_CATEGORIES || (tutorial.categoria ?? "Geral") === category)
    && (level === ALL_LEVELS || normalizeText(levelLabel(tutorial.nivel)) === normalizeText(level))),
  [tutorials.data, category, level]);

  function updateFilter(name: "categoria" | "nivel", value: string) {
    const next = new URLSearchParams(params.toString());
    const defaultValue = name === "categoria" ? ALL_CATEGORIES : ALL_LEVELS;
    if (value === defaultValue) next.delete(name);
    else next.set(name, value);
    const search = next.toString();
    window.history.replaceState(null, "", search ? `/exercicios?${search}` : "/exercicios");
    setOpen(null);
  }

  return <>{tutorials.error ? <ErrorState error={tutorials.error} onRetry={tutorials.reload} />
      : !tutorials.data ? <LoadingState rows={4} />
        : tutorials.data.length === 0 ? <EmptyState icon={ClipboardCheck} title="Nenhum exercício disponível" description="Os exercícios aparecem junto com os tutoriais publicados." />
          : <>
            <div className="filter-toolbar compact-filter-toolbar">
              <div className="filter-selects">
                <FilterSelect label="Categoria" value={category} onChange={(value) => updateFilter("categoria", value)} ariaLabel="Filtrar exercícios por categoria"
                  options={categories.map((item) => ({ value: item, label: item === ALL_CATEGORIES ? "Todas as categorias" : item }))} />
                <FilterSelect label="Nível" value={level} onChange={(value) => updateFilter("nivel", value)} ariaLabel="Filtrar exercícios por nível"
                  options={[ALL_LEVELS, ...LEVELS].map((item) => ({ value: item, label: item }))} />
              </div>
              <span className="filter-result-count">{filtered.length} {filtered.length === 1 ? "exercício" : "exercícios"}</span>
            </div>
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
            })}
              {filtered.length === 0 && <EmptyState title="Nenhum exercício encontrado" description="Ajuste os filtros de categoria e nível." />}
            </div>
          </>}
  </>;
}
