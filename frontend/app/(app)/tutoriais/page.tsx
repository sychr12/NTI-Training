"use client";

import { Search } from "lucide-react";
import { useSearchParams } from "next/navigation";
import { Suspense, useMemo, useState } from "react";
import { TutorialRow } from "../../components/content";
import { EmptyState, ErrorState, LoadingState, PageHeading } from "../../components/ui";
import { useApi } from "../../lib/api";
import { useProgress } from "../../lib/progress";
import type { Tutorial } from "../../lib/types";

const ALL = "Todas";

function normalize(value: string) {
  return value.normalize("NFD").replace(/[̀-ͯ]/g, "").toLowerCase();
}

export default function TutorialsPage() {
  return <div className="page-stack">
    <PageHeading title="Todos os tutoriais" description="Explore conteúdos práticos disponíveis na plataforma." />
    <Suspense fallback={<LoadingState />}><TutorialsFromUrl /></Suspense>
  </div>;
}

// A key faz a lista reiniciar os filtros quando a busca do topo muda a URL.
function TutorialsFromUrl() {
  const params = useSearchParams();
  const busca = params.get("busca") ?? "";
  const categoria = params.get("categoria") ?? ALL;
  return <TutorialList key={`${busca}|${categoria}`} initialQuery={busca} initialCategory={categoria} />;
}

function TutorialList({ initialQuery, initialCategory }: { initialQuery: string; initialCategory: string }) {
  const tutorials = useApi<Tutorial[]>("/api/tutoriais");
  const { byTutorial } = useProgress();
  const [query, setQuery] = useState(initialQuery);
  const [category, setCategory] = useState(initialCategory);

  const categories = useMemo(() => [ALL, ...new Set((tutorials.data ?? []).map((tutorial) => tutorial.categoria ?? "Geral"))], [tutorials.data]);
  const filtered = useMemo(() => {
    const term = normalize(query.trim());
    return (tutorials.data ?? []).filter((tutorial) =>
      (category === ALL || (tutorial.categoria ?? "Geral") === category)
      && (!term || normalize(`${tutorial.titulo} ${tutorial.descricao ?? ""}`).includes(term)));
  }, [tutorials.data, query, category]);

  return <>
    <div className="filter-toolbar">
      <div className="local-search"><Search size={17} /><input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Buscar tutorial..." aria-label="Buscar tutorial" /></div>
      <div className="filter-pills">{categories.map((item) => <button key={item} className={category === item ? "active" : ""} aria-pressed={category === item} onClick={() => setCategory(item)}>{item}</button>)}</div>
    </div>
    {tutorials.error ? <ErrorState error={tutorials.error} onRetry={tutorials.reload} />
      : !tutorials.data ? <LoadingState rows={4} />
        : <div className="tutorial-list">
          {filtered.map((tutorial) => <TutorialRow key={tutorial.id} tutorial={tutorial} status={byTutorial.get(tutorial.id)} />)}
          {filtered.length === 0 && <EmptyState title="Nenhum tutorial encontrado" description="Tente outro termo ou escolha uma categoria diferente." />}
        </div>}
  </>;
}
