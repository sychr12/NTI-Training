"use client";

import {
  BookA, BookOpenText, ChartNoAxesColumnIncreasing, ChevronDown, ClipboardCheck, House, Lightbulb, LogOut,
  Menu, Moon, PencilRuler, Route, Search, Settings, Star, Sun, Trophy, X, type LucideIcon,
} from "lucide-react";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { PROGRESS_CHANGED_EVENT, useApi } from "../lib/api";
import { canManageContent, initials, roleLabel } from "../lib/catalog";
import type { Perfil, SessionUser } from "../lib/types";
import { useAuth } from "./auth";
import { useTheme } from "./theme";
import { Brand, ProgressTrack } from "./ui";

const navItems: { href: string; label: string; icon: LucideIcon }[] = [
  { href: "/", label: "Início", icon: House },
  { href: "/tutoriais", label: "Tutoriais", icon: BookOpenText },
  { href: "/trilhas", label: "Trilhas", icon: Route },
  { href: "/exercicios", label: "Exercícios", icon: ClipboardCheck },
  { href: "/dicas", label: "Dicas", icon: Lightbulb },
  { href: "/glossario", label: "Glossário", icon: BookA },
  { href: "/progresso", label: "Meu progresso", icon: ChartNoAxesColumnIncreasing },
  { href: "/favoritos", label: "Favoritos", icon: Star },
  { href: "/configuracoes", label: "Configurações", icon: Settings },
];

function isActive(pathname: string, href: string) {
  return href === "/" ? pathname === "/" : pathname === href || pathname.startsWith(`${href}/`);
}

const manageItem = { href: "/gerenciar", label: "Gerenciar", icon: PencilRuler };

export function Sidebar({ open, onClose, onLogout }: { open: boolean; onClose: () => void; onLogout: () => void }) {
  const pathname = usePathname();
  const { user } = useAuth();
  const items = canManageContent(user?.perfil) ? [...navItems, manageItem] : navItems;
  const perfil = useApi<Perfil>("/api/perfil");
  const { reload } = perfil;

  // Concluir um tutorial em qualquer tela atualiza o progresso geral aqui.
  useEffect(() => {
    window.addEventListener(PROGRESS_CHANGED_EVENT, reload);
    return () => window.removeEventListener(PROGRESS_CHANGED_EVENT, reload);
  }, [reload]);

  const progress = perfil.data?.estatistica.progressoGeral ?? 0;

  return <>{open && <button className="sidebar-backdrop" aria-label="Fechar menu" onClick={onClose} />}<aside className={`sidebar ${open ? "sidebar-open" : ""}`}>
    <div className="sidebar-brand-row"><Brand compact /><button className="icon-button mobile-only" onClick={onClose} aria-label="Fechar menu" title="Fechar menu"><X size={20} /></button></div>
    <nav className="sidebar-nav" aria-label="Navegação principal">{items.map((item) => {
      const Icon = item.icon;
      const active = isActive(pathname, item.href);
      return <Link key={item.href} href={item.href} className={active ? "active" : ""} aria-current={active ? "page" : undefined} onClick={onClose}><Icon size={17} /><span>{item.label}</span></Link>;
    })}</nav>
    <div className="sidebar-footer">
      <Link href="/progresso" className="sidebar-progress" onClick={onClose}><span><Trophy size={16} /> Progresso geral</span><strong>{perfil.data ? `${progress}%` : "—"}</strong><ProgressTrack value={progress} /></Link>
      <button className="logout-button" onClick={onLogout}><LogOut size={17} /> Sair</button>
    </div>
  </aside></>;
}

export function Topbar({ user, onMenu }: { user: SessionUser; onMenu: () => void }) {
  const router = useRouter();
  const { theme, toggleTheme } = useTheme();
  const [query, setQuery] = useState("");
  const dark = theme === "dark";
  const name = user.nome ?? user.login;

  return <header className="topbar">
    <button className="icon-button menu-button" onClick={onMenu} aria-label="Abrir menu" title="Abrir menu"><Menu size={21} /></button>
    <form className="top-search" role="search" onSubmit={(event) => { event.preventDefault(); router.push(`/tutoriais?busca=${encodeURIComponent(query.trim())}`); }}>
      <Search size={17} /><input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Buscar tutoriais..." aria-label="Buscar tutoriais" />
    </form>
    <div className="topbar-actions">
      <button className="icon-button" onClick={toggleTheme} aria-label={dark ? "Ativar modo claro" : "Ativar modo escuro"} title={dark ? "Ativar modo claro" : "Ativar modo escuro"}>{dark ? <Sun size={19} /> : <Moon size={19} />}</button>
      <Link href="/configuracoes" className="profile-button"><span className="avatar">{initials(user.nome, user.login)}</span><span><strong>{name}</strong><small>{roleLabel(user.perfil)}</small></span><ChevronDown size={15} /></Link>
    </div>
  </header>;
}
