"use client";

import { ShieldAlert } from "lucide-react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import type { ReactNode } from "react";
import { useAuth } from "../../components/auth";
import { EmptyState, PageHeading } from "../../components/ui";
import { canManageContent, isAdmin } from "../../lib/catalog";

const tabs = [
  { href: "/gerenciar", label: "Tutoriais" },
  { href: "/gerenciar/trilhas", label: "Trilhas" },
  { href: "/gerenciar/exercicios", label: "Exercícios" },
  { href: "/gerenciar/dicas", label: "Dicas" },
  { href: "/gerenciar/glossario", label: "Glossário" },
  { href: "/gerenciar/usuarios", label: "Usuários", adminOnly: true },
];

// Área de cadastro de conteúdo: PROFESSOR, SUPERVISOR e ADMIN (o backend aplica as mesmas regras).
export default function ManageLayout({ children }: { children: ReactNode }) {
  const { user } = useAuth();
  const pathname = usePathname();

  if (!canManageContent(user?.perfil)) {
    return <EmptyState icon={ShieldAlert} title="Acesso restrito" description="Esta área é para professores, supervisores e administradores." action={<Link className="secondary-button" href="/">Voltar ao início</Link>} />;
  }

  return <div className="page-stack">
    <PageHeading title="Gerenciar conteúdo" description="Cadastre e edite o que os alunos veem na plataforma." />
    <nav className="tabs admin-tabs" aria-label="Seções de gerenciamento">
      {tabs.filter((tab) => !tab.adminOnly || isAdmin(user?.perfil)).map((tab) => {
        const active = pathname === tab.href;
        return <Link key={tab.href} href={tab.href} className={active ? "active" : ""} aria-current={active ? "page" : undefined}>{tab.label}</Link>;
      })}
    </nav>
    {children}
  </div>;
}
