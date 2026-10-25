"use client";

import { ArrowRight, BookOpenText, ChevronRight, CirclePlay, Clock3 } from "lucide-react";
import Link from "next/link";
import { useMemo, useState } from "react";
import { AchievementsSummary } from "../components/achievements";
import { useAuth } from "../components/auth";
import { FavoriteButton } from "../components/content";
import { EmptyState, ErrorState, IconBadge, LoadingState, ProgressTrack } from "../components/ui";
import { useApi } from "../lib/api";
import { categoryVisual, firstName, levelLabel, readingTime } from "../lib/catalog";
import type { Home, Tutorial } from "../lib/types";

function today() {
  const text = new Date().toLocaleDateString("pt-BR", { weekday: "long", day: "numeric", month: "long" });
  return text.charAt(0).toUpperCase() + text.slice(1);
}

export default function DashboardPage() {
  const { user } = useAuth();
  const home = useApi<Home>("/api/home");
  const tutorials = useApi<Tutorial[]>("/api/tutoriais");
  const [date] = useState(today);

  const categories = useMemo(() => {
    const counts = new Map<string, number>();
    for (const tutorial of tutorials.data ?? []) {
      const name = tutorial.categoria ?? "Geral";
      counts.set(name, (counts.get(name) ?? 0) + 1);
    }
    return [...counts.entries()].sort((a, b) => b[1] - a[1]);
  }, [tutorials.data]);

  const progress = home.data?.estatisticas.progressoGeral ?? 0;
  const next = home.data?.continuarAprendendo[0];

  return <div className="page-stack">
    <section className="welcome-panel">
      <div>
        <span className="eyebrow">{date}</span>
        <h1>Olá, {firstName(home.data?.nomeUsuario ?? user?.nome, user?.login ?? "")}! <span aria-hidden="true">👋</span></h1>
        <p>Continue aprendendo e evolua cada dia mais.</p>
      </div>
      <div className="overall-progress">
        <div style={{ "--value": progress } as React.CSSProperties}><strong>{progress}%</strong><span>concluído</span></div>
        <p><b>Seu progresso geral</b><span>{home.data ? `${home.data.estatisticas.cursosConcluido} tutoriais concluídos · ${home.data.estatisticas.exerciciosConcluidos} exercícios certos` : "Carregando..."}</span></p>
      </div>
    </section>

    <AchievementsSummary />

    {home.error && <ErrorState error={home.error} onRetry={home.reload} />}

    {next && <section className="continue-band">
      <div className="continue-icon"><CirclePlay size={24} /></div>
      <div><span>Continue de onde parou</span><strong>{next.titulo}</strong><p>{next.descricao ?? "Tutorial em andamento"}</p></div>
      {/* A API só registra progresso parcial ao concluir; com 0% mostramos o status no lugar da barra */}
      <div className="continue-meter">{next.progresso > 0 ? <><span>{next.progresso}%</span><ProgressTrack value={next.progresso} /></> : <span className="status-chip doing">Em andamento</span>}</div>
      <Link className="secondary-button" href={`/tutoriais/${next.id}`}>Continuar <ArrowRight size={16} /></Link>
    </section>}

    <section className="content-section">
      <div className="section-heading"><div><h2>Categorias</h2><p>Encontre conteúdos por assunto</p></div><Link className="text-button" href="/tutoriais">Ver todas <ChevronRight size={15} /></Link></div>
      {tutorials.error ? <ErrorState error={tutorials.error} onRetry={tutorials.reload} />
        : !tutorials.data ? <LoadingState rows={1} />
          : categories.length === 0 ? <EmptyState icon={BookOpenText} title="Nenhum conteúdo publicado" description="Os tutoriais cadastrados pela equipe aparecerão aqui." />
            : <div className="category-grid">{categories.map(([name, count]) => <Link className="category-card" key={name} href={`/tutoriais?categoria=${encodeURIComponent(name)}`}>
              <IconBadge visual={categoryVisual(name)} /><span><strong>{name}</strong><small>{count} {count === 1 ? "tutorial" : "tutoriais"}</small></span><ChevronRight size={16} />
            </Link>)}</div>}
    </section>

    {tutorials.data && tutorials.data.length > 0 && <section className="content-section">
      <div className="section-heading"><div><h2>Tutoriais recentes</h2><p>Os conteúdos publicados mais recentemente</p></div><Link className="text-button" href="/tutoriais">Ver todos <ChevronRight size={15} /></Link></div>
      <div className="featured-grid">{tutorials.data.slice(0, 4).map((tutorial) => <article className="featured-card" key={tutorial.id}>
        <div className="featured-top"><IconBadge visual={categoryVisual(tutorial.categoria)} /><FavoriteButton tutorialId={tutorial.id} /></div>
        <Link href={`/tutoriais/${tutorial.id}`} className="featured-link"><strong>{tutorial.titulo}</strong></Link>
        <div className="meta-row"><span className="level-badge">{levelLabel(tutorial.nivel)}</span><span><Clock3 size={14} />{readingTime(tutorial.conteudo)}</span></div>
      </article>)}</div>
    </section>}
  </div>;
}
