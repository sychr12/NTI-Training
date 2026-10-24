"use client";

import { BookOpenText, ChevronRight, CircleCheck, ClipboardCheck, Clock3, GraduationCap, Layers } from "lucide-react";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";
import { FavoriteButton, LessonContent, StatusChip } from "../../../components/content";
import { Quiz } from "../../../components/Quiz";
import { EmptyState, ErrorState, IconBadge, LoadingState } from "../../../components/ui";
import { ApiError, apiFetch, notifyProgressChanged, useApi } from "../../../lib/api";
import { categoryVisual, levelLabel, readingTime } from "../../../lib/catalog";
import { useProgress } from "../../../lib/progress";
import type { StatusProgresso, Tutorial } from "../../../lib/types";

const tabs = ["Conteúdo", "Exercícios"] as const;

export default function TutorialPage() {
  const { id } = useParams<{ id: string }>();
  const tutorial = useApi<Tutorial>(`/api/tutoriais/${id}`);
  const progress = useProgress();
  const [tab, setTab] = useState<(typeof tabs)[number]>("Conteúdo");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const status = progress.byTutorial.get(Number(id));

  async function saveStatus(next: StatusProgresso) {
    setSaving(true);
    setError("");
    try {
      await apiFetch("/api/progresso", { method: "PUT", body: JSON.stringify({ tutorialId: Number(id), status: next }) });
      notifyProgressChanged();
    }
    catch (exception) {
      setError(exception instanceof ApiError ? exception.message : "Não foi possível salvar o progresso.");
    }
    finally {
      setSaving(false);
    }
  }

  // Abrir um tutorial pela primeira vez o marca como "em andamento".
  const loadedWithoutProgress = !!tutorial.data && !!progress.data && status === undefined;
  useEffect(() => {
    if (!loadedWithoutProgress) return;
    apiFetch("/api/progresso", { method: "PUT", body: JSON.stringify({ tutorialId: Number(id), status: "EM_ANDAMENTO" }) })
      .then(notifyProgressChanged, () => undefined);
  }, [loadedWithoutProgress, id]);

  if (tutorial.error) {
    return tutorial.error.status === 404
      ? <EmptyState icon={BookOpenText} title="Tutorial não encontrado" description="Ele pode ter sido removido ou o endereço está incorreto." action={<Link className="secondary-button" href="/tutoriais">Ver tutoriais</Link>} />
      : <ErrorState error={tutorial.error} onRetry={tutorial.reload} />;
  }
  if (!tutorial.data) return <LoadingState rows={5} />;

  const data = tutorial.data;
  const visual = categoryVisual(data.categoria);
  const VisualIcon = visual.icon;
  const done = status === "CONCLUIDO";

  return <div className="page-stack">
    <nav className="breadcrumbs" aria-label="Você está em"><Link href="/tutoriais">Tutoriais</Link><ChevronRight size={14} />
      {data.categoria && <><Link href={`/tutoriais?categoria=${encodeURIComponent(data.categoria)}`}>{data.categoria}</Link><ChevronRight size={14} /></>}
      <strong>{data.titulo}</strong>
    </nav>

    <section className="tutorial-hero">
      <div>
        <span className="level-badge">{levelLabel(data.nivel)}</span>
        <h1>{data.titulo}</h1>
        {data.descricao && <p>{data.descricao}</p>}
        <div className="meta-row hero-meta"><span><Clock3 size={15} /> {readingTime(data.conteudo)} de leitura</span><StatusChip status={status} /></div>
      </div>
      <div className="hero-icon" style={{ color: visual.color }}><VisualIcon size={54} />{done && <span><CircleCheck size={20} /></span>}</div>
    </section>

    <div className="detail-grid">
      <section className="detail-main">
        <div className="tabs" role="tablist">{tabs.map((item) => <button key={item} role="tab" aria-selected={tab === item} className={tab === item ? "active" : ""} onClick={() => setTab(item)}>{item}</button>)}</div>
        <div className="detail-body">{tab === "Conteúdo" ? <LessonContent text={data.conteudo} /> : <Quiz tutorialId={data.id} />}</div>
      </section>

      <aside className="lesson-aside">
        <div className="learning-card">
          <h2>Sobre este tutorial</h2>
          <p><IconBadge visual={visual} size={15} /><span><small>Categoria</small>{data.categoria ?? "Geral"}</span></p>
          <p><GraduationCap size={16} /><span><small>Nível</small>{levelLabel(data.nivel)}</span></p>
          <p><Layers size={16} /><span><small>Pratique</small><button className="link-button" onClick={() => setTab("Exercícios")}><ClipboardCheck size={14} /> Fazer os exercícios</button></span></p>
          <div className="learning-actions"><FavoriteButton tutorialId={data.id} /><span>Salvar nos favoritos</span></div>
        </div>
        <button className={`complete-button ${done ? "done" : ""}`} disabled={saving || !progress.data} onClick={() => saveStatus(done ? "EM_ANDAMENTO" : "CONCLUIDO")}>
          <CircleCheck size={20} />{saving ? "Salvando..." : done ? "Tutorial concluído" : "Marcar como concluído"}
        </button>
        {error && <p className="login-error" role="alert">{error}</p>}
      </aside>
    </div>
  </div>;
}
