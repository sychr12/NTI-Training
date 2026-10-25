"use client";

import { CircleCheck, CirclePlay, Route, Trophy } from "lucide-react";
import Link from "next/link";
import { useEffect, useMemo } from "react";
import { MedalGrid } from "../../components/achievements";
import { EmptyState, ErrorState, IconBadge, LoadingState, PageHeading, ProgressTrack } from "../../components/ui";
import { PROGRESS_CHANGED_EVENT, useApi } from "../../lib/api";
import type { Visual } from "../../lib/catalog";
import { relativeDate, useProgress } from "../../lib/progress";
import type { Perfil, ProgressoTrilha, Tutorial } from "../../lib/types";

const visuals: Record<string, Visual> = {
  done: { icon: CircleCheck, color: "#15956b", soft: "#e9f8f2" },
  doing: { icon: CirclePlay, color: "#7b61d1", soft: "#f1edff" },
  trails: { icon: Route, color: "#1769e0", soft: "#eaf2ff" },
  total: { icon: Trophy, color: "#d69716", soft: "#fff7df" },
};

export default function ProgressPage() {
  const perfil = useApi<Perfil>("/api/perfil");
  const trails = useApi<ProgressoTrilha[]>("/api/progresso/trilhas");
  const tutorials = useApi<Tutorial[]>("/api/tutoriais");
  const progress = useProgress();
  const reloadPerfil = perfil.reload;

  useEffect(() => {
    window.addEventListener(PROGRESS_CHANGED_EVENT, reloadPerfil);
    return () => window.removeEventListener(PROGRESS_CHANGED_EVENT, reloadPerfil);
  }, [reloadPerfil]);

  const titles = useMemo(() => new Map((tutorials.data ?? []).map((tutorial) => [tutorial.id, tutorial.titulo])), [tutorials.data]);
  const activity = useMemo(() => [...(progress.data ?? [])]
    .sort((a, b) => b.atualizadoEm.localeCompare(a.atualizadoEm))
    .filter((item) => titles.has(item.tutorialId))
    .slice(0, 6), [progress.data, titles]);

  if (perfil.error) return <ErrorState error={perfil.error} onRetry={perfil.reload} />;
  const stats = perfil.data?.estatistica;
  const metrics = stats && [
    { label: "Tutoriais concluídos", value: stats.tutoriaisConcluidos, visual: visuals.done },
    { label: "Em andamento", value: Math.max(0, stats.tutoriaisIniciados - stats.tutoriaisConcluidos), visual: visuals.doing },
    { label: "Trilhas concluídas", value: stats.trilhasConcluidas, visual: visuals.trails },
    { label: "Progresso geral", value: `${stats.progressoGeral}%`, visual: visuals.total },
  ];

  return <div className="page-stack">
    <PageHeading title="Meu progresso" description="Acompanhe sua evolução e suas atividades recentes." />
    {!metrics ? <LoadingState rows={1} /> : <div className="metric-grid">{metrics.map((metric) => <article className="metric-card" key={metric.label}>
      <IconBadge visual={metric.visual} /><span><small>{metric.label}</small><strong>{metric.value}</strong></span>
    </article>)}</div>}

    <section className="content-section" id="conquistas">
      <div className="section-heading"><div><h2>Conquistas</h2><p>Seu nível, sua sequência de estudos e as medalhas que você já ganhou</p></div></div>
      <MedalGrid />
    </section>

    <div className="progress-columns">
      <section className="panel">
        <div className="section-heading"><div><h2>Progresso por trilha</h2><p>Quanto você já concluiu de cada trilha</p></div></div>
        {trails.error ? <ErrorState error={trails.error} onRetry={trails.reload} />
          : !trails.data ? <LoadingState rows={3} />
            : trails.data.length === 0 ? <EmptyState icon={Route} title="Nenhuma trilha ainda" description="Quando houver trilhas publicadas, seu progresso aparece aqui." />
              : <div className="category-progress">{trails.data.map((trail) => <div key={trail.trilhaId}>
                <span title={trail.titulo}>{trail.titulo}</span><ProgressTrack value={trail.percentual} /><b>{trail.percentual}%</b>
              </div>)}</div>}
      </section>

      <section className="panel">
        <div className="section-heading"><div><h2>Atividade recente</h2><p>Seus últimos avanços</p></div></div>
        {progress.error ? <ErrorState error={progress.error} onRetry={progress.reload} />
          : !progress.data || !tutorials.data ? <LoadingState rows={3} />
            : activity.length === 0 ? <EmptyState icon={CirclePlay} title="Nenhuma atividade ainda" description="Abra um tutorial para começar." action={<Link className="secondary-button" href="/tutoriais">Ver tutoriais</Link>} />
              : <div className="activity-list">{activity.map((item) => {
                const done = item.status === "CONCLUIDO";
                return <Link key={item.tutorialId} href={`/tutoriais/${item.tutorialId}`}>
                  <IconBadge visual={done ? visuals.done : visuals.doing} />
                  <span><strong>{done ? "Concluiu" : "Iniciou"}: {titles.get(item.tutorialId)}</strong><small>{relativeDate(item.atualizadoEm)}</small></span>
                </Link>;
              })}</div>}
      </section>
    </div>
  </div>;
}
