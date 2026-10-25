"use client";

import { Search, ShieldAlert, Users } from "lucide-react";
import { useMemo, useState } from "react";
import { useAuth } from "../../../components/auth";
import { EmptyState, ErrorState, LoadingState } from "../../../components/ui";
import { ApiError, apiFetch, useApi } from "../../../lib/api";
import { initials, isAdmin, normalizeText, roleLabel, ROLES } from "../../../lib/catalog";
import { relativeDate } from "../../../lib/progress";
import type { Usuario } from "../../../lib/types";

// Contas vêm do AD no primeiro login; aqui o administrador só ajusta perfil e acesso.
export default function ManageUsersPage() {
  const { user: me } = useAuth();
  const users = useApi<Usuario[]>(isAdmin(me?.perfil) ? "/api/users" : null);
  const [query, setQuery] = useState("");
  const [saving, setSaving] = useState<number | null>(null);
  const [error, setError] = useState("");

  const filtered = useMemo(() => {
    const term = normalizeText(query.trim());
    return (users.data ?? []).filter((user) => !term || normalizeText(`${user.nome ?? ""} ${user.login} ${user.email ?? ""}`).includes(term));
  }, [users.data, query]);

  if (!isAdmin(me?.perfil)) {
    return <EmptyState icon={ShieldAlert} title="Somente administradores" description="Peça a um administrador para alterar perfis de acesso." />;
  }

  async function update(user: Usuario, changes: Partial<Pick<Usuario, "perfil" | "ativo">>) {
    setSaving(user.id);
    setError("");
    try {
      await apiFetch(`/api/users/${user.id}`, { method: "PATCH", body: JSON.stringify(changes) });
      users.reload();
    }
    catch (exception) {
      setError(exception instanceof ApiError ? exception.message : "Não foi possível atualizar o usuário.");
    }
    finally {
      setSaving(null);
    }
  }

  return <>
    <div className="admin-toolbar">
      <div className="local-search"><Search size={17} /><input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Buscar por nome, usuário ou e-mail..." aria-label="Buscar usuário" /></div>
    </div>
    <p className="admin-note">Os usuários aparecem aqui depois do primeiro login com a conta da rede. Alterações valem na próxima ação do usuário.</p>
    {error && <p className="login-error" role="alert">{error}</p>}
    {users.error ? <ErrorState error={users.error} onRetry={users.reload} />
      : !users.data ? <LoadingState rows={4} />
        : users.data.length === 0 ? <EmptyState icon={Users} title="Nenhum usuário" description="Ninguém entrou na plataforma ainda." />
          : <div className="admin-list">{filtered.map((user) => {
            const self = user.id === me?.id;
            return <article key={user.id} className={user.ativo ? "" : "inactive"}>
              <span className="avatar">{initials(user.nome, user.login)}</span>
              <span><strong>{user.nome ?? user.login}{self && " (você)"}</strong><small>{user.login}{user.email && ` · ${user.email}`}{user.ultimoLogin && ` · último acesso: ${relativeDate(user.ultimoLogin).toLowerCase()}`}</small></span>
              <select className="admin-select" value={user.perfil} disabled={self || saving === user.id} onChange={(event) => update(user, { perfil: event.target.value })} aria-label={`Perfil de ${user.nome ?? user.login}`} title={self ? "Você não pode alterar o próprio perfil" : undefined}>
                {ROLES.map((role) => <option key={role} value={role}>{roleLabel(role)}</option>)}
              </select>
              <button className={`switch ${user.ativo ? "on" : ""}`} role="switch" aria-checked={user.ativo} disabled={self || saving === user.id} onClick={() => update(user, { ativo: !user.ativo })} aria-label={`${user.ativo ? "Desativar" : "Ativar"} ${user.nome ?? user.login}`} title={self ? "Você não pode desativar a própria conta" : user.ativo ? "Ativo" : "Inativo"}><i /></button>
            </article>;
          })}{filtered.length === 0 && <EmptyState title="Nenhum usuário encontrado" description="Tente buscar por outro termo." />}</div>}
  </>;
}
