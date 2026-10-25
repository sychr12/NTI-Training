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
import type { Exercicio, MinhaResposta, StatusProgresso, Tutorial } from "../../../lib/types";

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
  const myAnswers = useApi<MinhaResposta[]>(`/api/exercicios/minhas-respostas?tutorialId=${id}`);
  const [answeredNow, setAnsweredNow] = useState<number[]>([]);
  const contentRef = useRef<HTMLDivElement>(null);
  const lastSent = useRef(0);

  const saved = progress.byTutorial.get(Number(id));
  const done = saved?.status === "CONCLUIDO";
  const read = useReadingProgress(contentRef, tab === "Conteúdo" && !!tutorial.data);
  const totalExercises = exercises.data?.length ?? 0;
  // Exercícios feitos = respondidos em visitas anteriores + nesta visita.
  const answeredIds = new Set([...(myAnswers.data ?? []).map((item) => item.exercicioId), ...answeredNow]);
  const answered = (exercises.data ?? []).filter((exercise) => answeredIds.has(exercise.id)).length;
  // O servidor só deixa concluir com todos os exercícios respondidos.
  const pending = totalExercises - answered;
  const canComplete = pending === 0 && !!exercises.data && !!myAnswers.data;
  const goToExercises = () => { setTab("Exercícios"); contentRef.current?.scrollIntoView({ behavior: "smooth", block: "start" }); };
  const pendingText = !exercises.data || !myAnswers.data ? "Carregando exercícios..."
    : pending === 1 ? "Responda o exercício para concluir" : `Responda os ${pending} exercícios para concluir`;
  const computed = Math.min(99, Math.round(totalExercises > 0
    ? read * READING_WEIGHT + (answered / totalExercises) * EXERCISES_WEIGHT
    : read * 100));
  const percent = done ? 100 : Math.max(saved?.percentual ?? 0, computed);
  const readyToComplete = !done && canComplete && computed >= 99;

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
          : <Quiz tutorialId={data.id} onAnswered={(exerciseId) => setAnsweredNow((current) => [...current, exerciseId])} />}
          {/* Fim do conteúdo/exercícios: onde a pessoa termina, ela pode concluir (depois de fazer os exercícios) */}
          <div className={`lesson-finish ${done ? "done" : ""}`}>
            {done
              ? <><CircleCheck size={22} /><span><strong>Você concluiu este tutorial!</strong><small>Ele já conta no seu progresso e nas suas conquistas.</small></span>
                <Link className="secondary-button" href="/tutoriais">Ver outros tutoriais</Link></>
              : !canComplete
                ? <><ClipboardCheck size={22} /><span>
                  <strong>{tab === "Conteúdo" ? "Agora é hora de praticar!" : `Faltam ${pending} de ${totalExercises} exercícios`}</strong>
                  <small>{tab === "Conteúdo" ? `Responda os exercícios (${answered}/${totalExercises}) para poder concluir este tutorial.` : "Responda todos para poder concluir este tutorial."}</small>
                </span>
                  {tab === "Conteúdo" && <button className="primary-button" onClick={goToExercises}><ClipboardCheck size={16} /> Fazer exercícios</button>}</>
                : <><Sparkles size={22} /><span>
                  <strong>{tab === "Conteúdo" ? "Terminou a leitura?" : "Exercícios feitos!"}</strong>
                  <small>Marque como concluído para ganhar 50 XP.</small>
                </span>
                  <button className="primary-button" disabled={saving || !progress.data} onClick={() => saveStatus("CONCLUIDO")}><CircleCheck size={16} /> {saving ? "Salvando..." : "Marcar como concluído"}</button></>}
          </div>
        </div>
      </section>

      <aside className="lesson-aside">
        <div className="learning-card">
          <h2>Sobre este tutorial</h2>
          <p><IconBadge visual={visual} size={15} /><span><small>Categoria</small>{data.categoria ?? "Geral"}</span></p>
          <p><GraduationCap size={16} /><span><small>Nível</small>{levelLabel(data.nivel)}</span></p>
          <p><Layers size={16} /><span><small>Pratique</small><button className="link-button" onClick={() => setTab("Exercícios")}><ClipboardCheck size={14} /> {totalExercises === 1 ? "Fazer o exercício" : totalExercises > 1 ? `Fazer os ${totalExercises} exercícios` : "Ver exercícios"}</button></span></p>
          <div className="learning-actions"><FavoriteButton tutorialId={data.id} /><span>Salvar nos favoritos</span></div>
        </div>
        {!done && !canComplete
          ? <button className="complete-button locked" onClick={goToExercises} disabled={!exercises.data || !myAnswers.data}>
            <ClipboardCheck size={20} />{pendingText} ({answered}/{totalExercises})
          </button>
          : <button className={`complete-button ${done ? "done" : ""} ${readyToComplete ? "ready" : ""}`} disabled={saving || !progress.data} onClick={() => saveStatus(done ? "EM_ANDAMENTO" : "CONCLUIDO")}>
            {readyToComplete ? <Sparkles size={20} /> : <CircleCheck size={20} />}
            {saving ? "Salvando..." : done ? "Tutorial concluído" : readyToComplete ? "Tudo pronto! Marcar como concluído" : "Marcar como concluído"}
          </button>}
        {error && <p className="login-error" role="alert">{error}</p>}
      </aside>
    </div>
  </div>;
}
