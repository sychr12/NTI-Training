"use client";

import { Search } from "lucide-react";
import { useSearchParams } from "next/navigation";
import { Suspense, useMemo } from "react";
import { FilterSelect } from "../../components/FilterSelect";
import { TutorialRow } from "../../components/content";
import { EmptyState, ErrorState, LoadingState, PageHeading } from "../../components/ui";
import { useApi } from "../../lib/api";
import { LEVELS, levelLabel, normalizeText } from "../../lib/catalog";
import { useProgress } from "../../lib/progress";
import type { Tutorial } from "../../lib/types";

const ALL_CATEGORIES = "Todas";
const ALL_LEVELS = "Todos os níveis";

export default function TutorialsPage() {
  return <div className="page-stack">
    <PageHeading title="Todos os tutoriais" description="Explore conteúdos práticos disponíveis na plataforma." />
    <Suspense fallback={<LoadingState />}><TutorialsFromUrl /></Suspense>
  </div>;
}

function TutorialsFromUrl() {
  const params = useSearchParams();
  const query = params.get("busca") ?? "";
  const category = params.get("categoria") ?? ALL_CATEGORIES;
  const level = params.get("nivel") ?? ALL_LEVELS;

  function updateFilter(name: "busca" | "categoria" | "nivel", value: string) {
    const next = new URLSearchParams(params.toString());
    const defaultValue = name === "categoria" ? ALL_CATEGORIES : name === "nivel" ? ALL_LEVELS : "";
    if (!value.trim() || value === defaultValue) next.delete(name);
    else next.set(name, value);
    const search = next.toString();
    window.history.replaceState(null, "", search ? `/tutoriais?${search}` : "/tutoriais");
  }

  return <TutorialList
    query={query}
    category={category}
    level={level}
    onQueryChange={(value) => updateFilter("busca", value)}
    onCategoryChange={(value) => updateFilter("categoria", value)}
    onLevelChange={(value) => updateFilter("nivel", value)}
  />;
}

function TutorialList({ query, category, level, onQueryChange, onCategoryChange, onLevelChange }: {
  query: string;
  category: string;
  level: string;
  onQueryChange: (value: string) => void;
  onCategoryChange: (value: string) => void;
  onLevelChange: (value: string) => void;
}) {
  const tutorials = useApi<Tutorial[]>("/api/tutoriais");
  const { byTutorial } = useProgress();

  const categories = useMemo(() => [ALL_CATEGORIES, ...new Set((tutorials.data ?? []).map((tutorial) => tutorial.categoria ?? "Geral"))], [tutorials.data]);
  const filtered = useMemo(() => {
    const term = normalizeText(query.trim());
    return (tutorials.data ?? []).filter((tutorial) =>
      (category === ALL_CATEGORIES || (tutorial.categoria ?? "Geral") === category)
      && (level === ALL_LEVELS || normalizeText(levelLabel(tutorial.nivel)) === normalizeText(level))
      && (!term || normalizeText(`${tutorial.titulo} ${tutorial.descricao ?? ""}`).includes(term)));
  }, [tutorials.data, query, category, level]);

  return <>
    <div className="filter-toolbar">
      <div className="filter-toolbar-main">
        <div className="local-search"><Search size={17} /><input value={query} onChange={(event) => onQueryChange(event.target.value)} placeholder="Buscar tutorial..." aria-label="Buscar tutorial" /></div>
        <div className="filter-selects">
          <FilterSelect label="Categoria" value={category} onChange={onCategoryChange} ariaLabel="Filtrar tutoriais por categoria"
            options={categories.map((item) => ({ value: item, label: item === ALL_CATEGORIES ? "Todas as categorias" : item }))} />
          <FilterSelect label="Nível" value={level} onChange={onLevelChange} ariaLabel="Filtrar tutoriais por nível"
            options={[ALL_LEVELS, ...LEVELS].map((item) => ({ value: item, label: item }))} />
        </div>
      </div>
    </div>
    {tutorials.error ? <ErrorState error={tutorials.error} onRetry={tutorials.reload} />
      : !tutorials.data ? <LoadingState rows={4} />
        : <div className="tutorial-list">
          {filtered.map((tutorial) => <TutorialRow key={tutorial.id} tutorial={tutorial} progress={byTutorial.get(tutorial.id)} />)}
          {filtered.length === 0 && <EmptyState title="Nenhum tutorial encontrado" description="Tente outro termo ou ajuste os filtros de categoria e nível." />}
        </div>}
  </>;
}
