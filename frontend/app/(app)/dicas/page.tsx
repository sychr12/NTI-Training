"use client";

import { ChevronRight, ChevronUp, Lightbulb } from "lucide-react";
import { useMemo, useState } from "react";
import { EmptyState, ErrorState, IconBadge, LoadingState, PageHeading } from "../../components/ui";
import { useApi } from "../../lib/api";
import { categoryVisual } from "../../lib/catalog";
import type { Dica } from "../../lib/types";

const ALL = "Todas";
const PREVIEW = 110;

export default function TipsPage() {
  const tips = useApi<Dica[]>("/api/dicas");
  const [category, setCategory] = useState(ALL);
  const [open, setOpen] = useState<number | null>(null);

  const categories = useMemo(() => [ALL, ...new Set((tips.data ?? []).map((tip) => tip.categoria))], [tips.data]);
  const filtered = (tips.data ?? []).filter((tip) => category === ALL || tip.categoria === category);

  return <div className="page-stack">
    <PageHeading title="Dicas rápidas" description="Dicas práticas para o dia a dia." />
    {tips.error ? <ErrorState error={tips.error} onRetry={tips.reload} />
      : !tips.data ? <LoadingState rows={3} />
        : tips.data.length === 0 ? <EmptyState icon={Lightbulb} title="Nenhuma dica publicada" description="As dicas cadastradas pela equipe aparecerão aqui." />
          : <>
            {categories.length > 2 && <div className="filter-pills page-filters">{categories.map((item) => <button key={item} className={category === item ? "active" : ""} aria-pressed={category === item} onClick={() => setCategory(item)}>{item}</button>)}</div>}
            <div className="tips-grid">{filtered.map((tip) => {
              const expanded = open === tip.id;
              const long = tip.conteudo.length > PREVIEW;
              return <article key={tip.id} className={expanded ? "expanded" : ""}>
                <IconBadge visual={categoryVisual(tip.categoria)} />
                <strong>{tip.titulo}</strong>
                <p>{expanded || !long ? tip.conteudo : `${tip.conteudo.slice(0, PREVIEW).trimEnd()}…`}</p>
                {long && <button className="text-button" onClick={() => setOpen(expanded ? null : tip.id)} aria-expanded={expanded}>
                  {expanded ? <>Mostrar menos <ChevronUp size={15} /></> : <>Ler dica <ChevronRight size={15} /></>}
                </button>}
              </article>;
            })}</div>
          </>}
  </div>;
}
