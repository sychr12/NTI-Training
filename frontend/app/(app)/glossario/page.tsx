"use client";

import { BookA, ChevronDown, ChevronUp, Search } from "lucide-react";
import { useMemo, useState } from "react";
import { EmptyState, ErrorState, LoadingState, PageHeading } from "../../components/ui";
import { useApi } from "../../lib/api";
import { normalizeText } from "../../lib/catalog";
import type { TermoGlossario } from "../../lib/types";

export default function GlossaryPage() {
  const terms = useApi<TermoGlossario[]>("/api/glossario");
  const [query, setQuery] = useState("");
  const [open, setOpen] = useState<number | null>(null);

  const filtered = useMemo(() => {
    const term = normalizeText(query.trim());
    return [...(terms.data ?? [])]
      .sort((a, b) => a.termo.localeCompare(b.termo, "pt-BR"))
      .filter((item) => !term || normalizeText(`${item.termo} ${item.definicao}`).includes(term));
  }, [terms.data, query]);

  return <div className="page-stack">
    <PageHeading title="Glossário de TI" description="Entenda os principais termos da área de tecnologia." />
    <div className="local-search glossary-search"><Search size={17} /><input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Buscar termo..." aria-label="Buscar termo" /></div>
    {terms.error ? <ErrorState error={terms.error} onRetry={terms.reload} />
      : !terms.data ? <LoadingState rows={5} />
        : terms.data.length === 0 ? <EmptyState icon={BookA} title="Glossário vazio" description="Os termos cadastrados pela equipe aparecerão aqui." />
          : <div className="glossary-list">
            {filtered.map((item) => {
              const expanded = open === item.id;
              return <button key={item.id} onClick={() => setOpen(expanded ? null : item.id)} aria-expanded={expanded}>
                <span><strong>{item.termo}</strong><small className="glossary-category">{item.categoria}</small>{expanded && <p>{item.definicao}</p>}</span>
                {expanded ? <ChevronUp size={18} /> : <ChevronDown size={18} />}
              </button>;
            })}
            {filtered.length === 0 && <EmptyState title="Nenhum termo encontrado" description="Tente buscar por outra palavra." />}
          </div>}
  </div>;
}
