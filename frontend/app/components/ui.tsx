import { CircleAlert, GraduationCap, RefreshCw, SearchX, type LucideIcon } from "lucide-react";
import type { ReactNode } from "react";
import type { ApiError } from "../lib/api";
import type { Visual } from "../lib/catalog";

export function Brand({ compact = false }: { compact?: boolean }) {
  return <div className={`brand ${compact ? "brand-compact" : ""}`}>
    <span className="brand-mark"><GraduationCap size={22} strokeWidth={2.4} /></span>
    <span><strong>NTI Training</strong>{!compact && <small>Aprendizado para futuros talentos</small>}</span>
  </div>;
}

export function PageHeading({ title, description, action }: { title: string; description: string; action?: ReactNode }) {
  return <div className="page-heading"><div><h1>{title}</h1><p>{description}</p></div>{action}</div>;
}

export function IconBadge({ visual, size = 21 }: { visual: Visual; size?: number }) {
  const Icon = visual.icon;
  return <span className="icon-badge" style={{ color: visual.color, backgroundColor: visual.soft }}><Icon size={size} /></span>;
}

export function ProgressTrack({ value, color }: { value: number; color?: string }) {
  return <div className="progress-track" role="progressbar" aria-valuenow={value} aria-valuemin={0} aria-valuemax={100}>
    <i style={{ width: `${Math.min(100, Math.max(0, value))}%`, background: color }} />
  </div>;
}

/** Blocos "fantasmas" exibidos enquanto os dados carregam. */
export function LoadingState({ rows = 3, label = "Carregando..." }: { rows?: number; label?: string }) {
  return <div className="state-loading" role="status" aria-label={label}>
    {Array.from({ length: rows }, (_, index) => <span key={index} className="skeleton" />)}
  </div>;
}

export function ErrorState({ error, onRetry }: { error: ApiError; onRetry?: () => void }) {
  return <div className="empty-state state-error" role="alert">
    <CircleAlert size={28} />
    <strong>{error.status === 0 ? "Servidor indisponível" : "Não foi possível carregar"}</strong>
    <p>{error.message}</p>
    {onRetry && <button className="secondary-button" onClick={onRetry}><RefreshCw size={15} /> Tentar novamente</button>}
  </div>;
}

export function EmptyState({ icon: Icon = SearchX, title, description, action }: { icon?: LucideIcon; title: string; description: string; action?: ReactNode }) {
  return <div className="empty-state"><Icon size={28} /><strong>{title}</strong><p>{description}</p>{action}</div>;
}
