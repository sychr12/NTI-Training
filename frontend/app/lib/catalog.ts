import {
  BookOpenText, Code2, Monitor, Network, ShieldCheck, Wifi, Wrench, type LucideIcon,
} from "lucide-react";

export type Visual = { icon: LucideIcon; color: string; soft: string };

// Aparência de cada categoria de conteúdo; categorias novas usam o visual padrão.
const categoryVisuals: Record<string, Visual> = {
  computador: { icon: Monitor, color: "#1769e0", soft: "#eaf2ff" },
  internet: { icon: Wifi, color: "#15956b", soft: "#e9f8f2" },
  programas: { icon: Code2, color: "#e26e2f", soft: "#fff1e8" },
  redes: { icon: Network, color: "#1688c9", soft: "#e9f7fe" },
  manutencao: { icon: Wrench, color: "#d69716", soft: "#fff7df" },
  seguranca: { icon: ShieldCheck, color: "#df4d5c", soft: "#ffedf0" },
};

const defaultVisual: Visual = { icon: BookOpenText, color: "#7b61d1", soft: "#f1edff" };

/** Texto em minúsculas e sem acentos, para buscas e comparações ("Manutenção" → "manutencao"). */
export function normalizeText(value: string) {
  return value.normalize("NFD").replace(/\p{Diacritic}/gu, "").trim().toLowerCase();
}

export function categoryVisual(categoria: string | null | undefined): Visual {
  return (categoria && categoryVisuals[normalizeText(categoria)]) || defaultVisual;
}

const levelLabels: Record<string, string> = { iniciante: "Iniciante", intermediario: "Intermediário", avancado: "Avançado" };

export function levelLabel(nivel: string | null | undefined) {
  if (!nivel) return "Iniciante";
  return levelLabels[normalizeText(nivel)] ?? nivel;
}

const roleLabels: Record<string, string> = { ALUNO: "Aluno", PROFESSOR: "Professor", SUPERVISOR: "Supervisor", ADMIN: "Administrador" };

export function roleLabel(perfil: string | null | undefined) {
  return (perfil && roleLabels[perfil]) ?? "Aluno";
}

export const ROLES = Object.keys(roleLabels);

/** Mesmas regras do SecurityConfig do backend: quem pode criar/editar conteúdo. */
export function canManageContent(perfil: string | null | undefined) {
  return perfil === "PROFESSOR" || perfil === "SUPERVISOR" || perfil === "ADMIN";
}

export function isAdmin(perfil: string | null | undefined) {
  return perfil === "ADMIN";
}

export const LEVELS = ["Iniciante", "Intermediário", "Avançado"];

/** Tempo estimado de leitura, a partir do conteúdo do tutorial (~180 palavras por minuto). */
export function readingTime(conteudo: string) {
  const words = conteudo.trim().split(/\s+/).length;
  return `${Math.max(1, Math.round(words / 180))} min`;
}

export function initials(nome: string | null | undefined, fallback = "?") {
  const parts = (nome ?? "").trim().split(/\s+/).filter(Boolean);
  if (parts.length === 0) return fallback.slice(0, 2).toUpperCase();
  return ((parts[0][0] ?? "") + (parts.length > 1 ? parts[parts.length - 1][0] : "")).toUpperCase();
}

export function firstName(nome: string | null | undefined, fallback: string) {
  return (nome ?? "").trim().split(/\s+/)[0] || fallback;
}
