"use client";

import { ChevronRight, Compass, Flame, Library, Lock, Medal, PartyPopper, Rocket, Route, Target, Trophy, X, Zap, type LucideIcon } from "lucide-react";
import Link from "next/link";
import { useEffect } from "react";
import { dismissAchievementToast, levelProgress, streakMessage, useAchievements } from "../lib/achievements";
import type { Medalha } from "../lib/types";
import { LoadingState, ProgressTrack } from "./ui";

// O backend envia o nome do ícone de cada medalha (ver ConquistaService).
const medalIcons: Record<string, LucideIcon> = { rocket: Rocket, books: Library, compass: Compass, target: Target, route: Route, flame: Flame, trophy: Trophy };

function MedalIcon({ icone, size }: { icone: string; size: number }) {
  const Icon = medalIcons[icone] ?? Medal;
  return <Icon size={size} />;
}

/** Faixa do Início: sequência de dias, XP/nível e medalhas. */
export function AchievementsSummary() {
  const { data, error } = useAchievements();
  if (error) return null;
  if (!data) return <LoadingState rows={1} />;

  const earned = data.medalhas.filter((medalha) => medalha.conquistada);
  const toNext = data.nivel.xpProximo === null ? null : data.nivel.xpProximo - data.xp;

  return <section className="achievement-strip" aria-label="Suas conquistas">
    <article className={`achievement-card streak ${data.sequencia.estudouHoje ? "lit" : ""}`}>
      <span className="achievement-icon"><Flame size={22} /></span>
      <span><small>Sequência de estudos</small><strong>{data.sequencia.atual} {data.sequencia.atual === 1 ? "dia" : "dias"}</strong><em>{streakMessage(data.sequencia)}</em></span>
    </article>
    <article className="achievement-card xp">
      <span className="achievement-icon"><Zap size={22} /></span>
      <span>
        <small>Nível {data.nivel.numero} · {data.nivel.nome}</small>
        <strong>{data.xp} XP</strong>
        <ProgressTrack value={levelProgress(data)} />
        <em>{toNext === null ? "Nível máximo alcançado!" : `Faltam ${toNext} XP para o próximo nível`}</em>
      </span>
    </article>
    <Link href="/progresso#conquistas" className="achievement-card medals">
      <span className="achievement-icon"><Medal size={22} /></span>
      <span>
        <small>Medalhas</small>
        <strong>{earned.length} de {data.medalhas.length}</strong>
        <span className="medal-row">{data.medalhas.map((medalha) => <i key={medalha.codigo} className={medalha.conquistada ? "earned" : ""} title={medalha.titulo}><MedalIcon icone={medalha.icone} size={13} /></i>)}</span>
      </span>
      <ChevronRight size={18} />
    </Link>
  </section>;
}

/** Grade completa de medalhas da tela Meu progresso. */
export function MedalGrid() {
  const { data, error } = useAchievements();
  if (error) return <p className="admin-note">Não foi possível carregar as conquistas.</p>;
  if (!data) return <LoadingState rows={2} />;

  return <>
    <div className="level-banner">
      <span className="level-number"><small>Nível</small>{data.nivel.numero}</span>
      <span>
        <strong>{data.nivel.nome}</strong>
        <small>{data.xp} XP{data.nivel.xpProximo !== null && ` · próximo nível em ${data.nivel.xpProximo} XP`}</small>
        <ProgressTrack value={levelProgress(data)} />
      </span>
      <span className="level-streak"><Flame size={18} /><b>{data.sequencia.atual}</b><small>dias seguidos<br />recorde: {data.sequencia.melhor}</small></span>
    </div>
    <div className="medal-grid">{data.medalhas.map((medalha) => <MedalCard key={medalha.codigo} medalha={medalha} />)}</div>
    <p className="admin-note">XP: 50 por tutorial concluído, 10 por exercício acertado e 100 por trilha completa.</p>
  </>;
}

function MedalCard({ medalha }: { medalha: Medalha }) {
  return <article className={`medal-card ${medalha.conquistada ? "earned" : "locked"}`}>
    <span className="medal-icon"><MedalIcon icone={medalha.icone} size={26} />{!medalha.conquistada && <Lock className="medal-lock" size={13} />}</span>
    <strong>{medalha.titulo}</strong>
    <p>{medalha.descricao}</p>
    {medalha.conquistada
      ? <small className="medal-date">Conquistada{medalha.conquistadaEm && ` em ${new Date(medalha.conquistadaEm).toLocaleDateString("pt-BR")}`}</small>
      : <span className="medal-progress"><ProgressTrack value={(medalha.progresso / medalha.meta) * 100} /><small>{medalha.progresso}/{medalha.meta}</small></span>}
  </article>;
}

const TOAST_MS = 7000;
// Posições fixas dos confetes (evita aleatoriedade na renderização).
const CONFETTI = Array.from({ length: 14 }, (_, index) => ({ left: `${(index * 37) % 100}%`, delay: `${(index % 7) * 0.08}s`, hue: (index * 47) % 360 }));

/** Aviso animado de medalha nova; fica no layout das telas logadas. */
export function AchievementToaster() {
  const { toasts } = useAchievements();
  const current = toasts[0];

  useEffect(() => {
    if (!current) return;
    const timer = setTimeout(() => dismissAchievementToast(current.codigo), TOAST_MS);
    return () => clearTimeout(timer);
  }, [current]);

  if (!current) return null;
  return <div className="achievement-toast" role="status" aria-live="polite" key={current.codigo}>
    <span className="confetti" aria-hidden="true">{CONFETTI.map((piece, index) => <i key={index} style={{ left: piece.left, animationDelay: piece.delay, background: `hsl(${piece.hue} 85% 60%)` }} />)}</span>
    <span className="medal-icon"><MedalIcon icone={current.icone} size={26} /></span>
    <span>
      <small><PartyPopper size={13} /> Nova conquista!</small>
      <strong>{current.titulo}</strong>
      <em>{current.descricao}</em>
    </span>
    <button className="icon-button" onClick={() => dismissAchievementToast(current.codigo)} aria-label="Fechar aviso" title="Fechar"><X size={16} /></button>
  </div>;
}

/** Linha curta da barra lateral com a sequência de dias. */
export function StreakLine() {
  const { data } = useAchievements();
  if (!data) return null;
  return <span className={`sidebar-streak ${data.sequencia.estudouHoje ? "lit" : ""}`}><Flame size={14} /> {data.sequencia.atual} {data.sequencia.atual === 1 ? "dia seguido" : "dias seguidos"} · {data.xp} XP</span>;
}
