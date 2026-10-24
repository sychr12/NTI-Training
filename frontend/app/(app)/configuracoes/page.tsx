"use client";

import { AtSign, Bell, KeyRound, LockKeyhole, Mail, Moon, type LucideIcon } from "lucide-react";
import { useState } from "react";
import { useAuth } from "../../components/auth";
import { PasswordHelpDialog } from "../../components/PasswordHelpDialog";
import { useTheme } from "../../components/theme";
import { ErrorState, LoadingState, PageHeading } from "../../components/ui";
import { useApi } from "../../lib/api";
import { initials, roleLabel } from "../../lib/catalog";
import type { Perfil } from "../../lib/types";

export default function SettingsPage() {
  const { user } = useAuth();
  const perfil = useApi<Perfil>("/api/perfil");
  const { theme, toggleTheme } = useTheme();
  const [preferences, setPreferences] = useState({ reminders: true, email: true });
  const [passwordHelp, setPasswordHelp] = useState(false);

  const data = perfil.data;
  const name = data?.nome ?? user?.nome ?? user?.login ?? "";
  const rows: { key: string; label: string; icon: LucideIcon; on: boolean; toggle: () => void }[] = [
    { key: "reminders", label: "Lembretes de estudo", icon: Bell, on: preferences.reminders, toggle: () => setPreferences((value) => ({ ...value, reminders: !value.reminders })) },
    { key: "email", label: "Notificações por e-mail", icon: Mail, on: preferences.email, toggle: () => setPreferences((value) => ({ ...value, email: !value.email })) },
    { key: "dark", label: "Modo escuro", icon: Moon, on: theme === "dark", toggle: toggleTheme },
  ];

  return <div className="page-stack">
    <PageHeading title="Meu perfil" description="Gerencie suas informações e preferências." />
    <div className="settings-grid">
      <section className="profile-panel">
        <h2>Informações</h2>
        {perfil.error ? <ErrorState error={perfil.error} onRetry={perfil.reload} /> : !data ? <LoadingState rows={3} /> : <>
          <div className="profile-summary"><span className="avatar avatar-large">{initials(name, data.login)}</span><div><strong>{name}</strong><small>{roleLabel(data.perfil)}</small></div></div>
          <div className="profile-info">
            <p><AtSign size={16} /><span><small>Usuário da rede</small>{data.login}</span></p>
            <p><Mail size={16} /><span><small>E-mail</small>{data.email ?? "Não informado no AD"}</span></p>
            <p><KeyRound size={16} /><span><small>Perfil de acesso</small>{roleLabel(data.perfil)}</span></p>
          </div>
        </>}
      </section>
      <section className="preferences-panel">
        <h2>Preferências</h2>
        {rows.map((row) => {
          const Icon = row.icon;
          return <div className="preference-row" key={row.key}><span><Icon size={17} />{row.label}</span><button className={`switch ${row.on ? "on" : ""}`} onClick={row.toggle} role="switch" aria-checked={row.on} aria-label={`Alternar ${row.label}`}><i /></button></div>;
        })}
        <div className="password-box"><div><LockKeyhole size={18} /><span><strong>Senha de acesso</strong><small>Gerenciada pela rede interna</small></span></div><button className="secondary-button" onClick={() => setPasswordHelp(true)}>Solicitar alteração</button></div>
      </section>
    </div>
    {passwordHelp && <PasswordHelpDialog onClose={() => setPasswordHelp(false)} />}
  </div>;
}
