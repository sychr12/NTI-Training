"use client";

import { BookOpenText, ChevronRight, CircleCheck, ClipboardCheck, Clock3, GraduationCap, Layers, Sparkles } from "lucide-react";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useEffect, useRef, useState } from "react";
import { FavoriteButton, LessonContent, StatusChip } from "../../../components/content";
import { Quiz } from "../../../components/Quiz";
import { EmptyState, ErrorState, IconBadge, LoadingState, ProgressTrack } from "../../../components/ui";
import { ApiError, apiFetch, notifyProgressChanged, useApi } from "../../../lib/api";
import { categoryVisual, levelLabel, readingTime } from "../../../lib/catalog";
import { useProgress } from "../../../lib/progress";
import { useReadingProgress } from "../../../lib/reading";
import type { Exercicio, StatusProgresso, Tutorial } from "../../../lib/types";

const tabs = ["Conteúdo", "Exercícios"] as const;

// Peso de cada parte no progresso parcial quando o tutorial tem exercícios.
const READING_WEIGHT = 70;
const EXERCISES_WEIGHT = 30;
// Envia o progresso a cada 5 pontos, para não chamar a API a cada bloco lido.
const SEND_STEP = 5;

export default function TutorialPage() {
  const { id } = useParams<{ id: string }>();
  const tutorial = useApi<Tutorial>(`/api/tutoriais/${id}`);
  const exercises = useApi<Exercicio[]>(`/api/exercicios?tutorialId=${id}`);
  const progress = useProgress();
  const [tab, setTab] = useState<(typeof tabs)[number]>("Conteúdo");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [answered, setAnswered] = useState(0);
  const contentRef = useRef<HTMLDivElement>(null);
  const lastSent = useRef(0);

  const saved = progress.byTutorial.get(Number(id));
  const done = saved?.status === "CONCLUIDO";
  const read = useReadingProgress(contentRef, tab === "Conteúdo" && !!tutorial.data);
  const totalExercises = exercises.data?.length ?? 0;
  const computed = Math.min(99, Math.round(totalExercises > 0
    ? read * READING_WEIGHT + (answered / totalExercises) * EXERCISES_WEIGHT
    : read * 100));
  const percent = done ? 100 : Math.max(saved?.percentual ?? 0, computed);
  const readyToComplete = !done && computed >= 99;

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
  const loadedWithoutProgress = !!tutorial.data && !!progress.data && saved === undefined;
  useEffect(() => {
    if (!loadedWithoutProgress) return;
    apiFetch("/api/progresso", { method: "PUT", body: JSON.stringify({ tutorialId: Number(id), status: "EM_ANDAMENTO" }) })
      .then(notifyProgressChanged, () => undefined);
  }, [loadedWithoutProgress, id]);

  // Salva o progresso parcial conforme a leitura e os exercícios avançam (o servidor nunca diminui o valor).
  const savedPercent = saved?.percentual ?? 0;
  const { reload: reloadProgress } = progress;
  useEffect(() => {
    if (!saved || done) return;
    const baseline = Math.max(lastSent.current, savedPercent);
    if (computed <= baseline || (computed < 99 && computed - baseline < SEND_STEP)) return;
    const timer = setTimeout(() => {
      lastSent.current = computed;
      apiFetch("/api/progresso", { method: "PUT", body: JSON.stringify({ tutorialId: Number(id), status: "EM_ANDAMENTO", percentual: computed }) })
        .then(reloadProgress, () => undefined);
    }, 800);
    return () => clearTimeout(timer);
  }, [computed, saved, done, savedPercent, id, reloadProgress]);

  if (tutorial.error) {
    return tutorial.error.status === 404
      ? <EmptyState icon={BookOpenText} title="Tutorial não encontrado" description="Ele pode ter sido removido ou o endereço está incorreto." action={<Link className="secondary-button" href="/tutoriais">Ver tutoriais</Link>} />
      : <ErrorState error={tutorial.error} onRetry={tutorial.reload} />;
  }
  if (!tutorial.data) return <LoadingState rows={5} />;

  const data = tutorial.data;
  const visual = categoryVisual(data.categoria);
  const VisualIcon = visual.icon;

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
        <div className="meta-row hero-meta"><span><Clock3 size={15} /> {readingTime(data.conteudo)} de leitura</span><StatusChip progress={done ? saved : undefined} /></div>
        <div className="hero-progress" title={totalExercises > 0 ? "70% leitura do conteúdo + 30% exercícios" : "Leitura do conteúdo"}>
          <span>Seu progresso</span><ProgressTrack value={percent} color={done ? "var(--success)" : undefined} /><b>{percent}%</b>
        </div>
      </div>
      <div className="hero-icon" style={{ color: visual.color }}><VisualIcon size={54} />{done && <span><CircleCheck size={20} /></span>}</div>
    </section>

    <div className="detail-grid">
      <section className="detail-main">
        <div className="tabs" role="tablist">{tabs.map((item) => <button key={item} role="tab" aria-selected={tab === item} className={tab === item ? "active" : ""} onClick={() => setTab(item)}>{item}{item === "Exercícios" && totalExercises > 0 && <i className="tab-count">{answered}/{totalExercises}</i>}</button>)}</div>
        <div className="detail-body" ref={contentRef}>{tab === "Conteúdo"
          ? <LessonContent text={data.conteudo} />
          : <Quiz tutorialId={data.id} onAnswered={(count) => setAnswered((current) => Math.max(current, count))} />}</div>
      </section>

      <aside className="lesson-aside">
        <div className="learning-card">
          <h2>Sobre este tutorial</h2>
          <p><IconBadge visual={visual} size={15} /><span><small>Categoria</small>{data.categoria ?? "Geral"}</span></p>
          <p><GraduationCap size={16} /><span><small>Nível</small>{levelLabel(data.nivel)}</span></p>
          <p><Layers size={16} /><span><small>Pratique</small><button className="link-button" onClick={() => setTab("Exercícios")}><ClipboardCheck size={14} /> {totalExercises > 0 ? `Fazer os ${totalExercises} exercícios` : "Ver exercícios"}</button></span></p>
          <div className="learning-actions"><FavoriteButton tutorialId={data.id} /><span>Salvar nos favoritos</span></div>
        </div>
        <button className={`complete-button ${done ? "done" : ""} ${readyToComplete ? "ready" : ""}`} disabled={saving || !progress.data} onClick={() => saveStatus(done ? "EM_ANDAMENTO" : "CONCLUIDO")}>
          {readyToComplete ? <Sparkles size={20} /> : <CircleCheck size={20} />}
          {saving ? "Salvando..." : done ? "Tutorial concluído" : readyToComplete ? "Tudo pronto! Marcar como concluído" : "Marcar como concluído"}
        </button>
        {error && <p className="login-error" role="alert">{error}</p>}
      </aside>
    </div>
  </div>;
}
