"use client";

import { ArrowBigUp, ArrowRight, CircleAlert, Eye, EyeOff, Info, LockKeyhole, ShieldCheck, UserRound } from "lucide-react";
import { FormEvent, useState } from "react";
import { ApiError, apiFetch } from "../lib/api";
import type { SessionUser } from "../lib/types";
import { LearningShowcase } from "./LearningShowcase";
import { Brand } from "./ui";

const loginErrors: Record<number, string> = {
  0: "Não foi possível conectar ao servidor de autenticação.",
  401: "Usuário ou senha inválidos. Confira os dados e tente novamente.",
  403: "Seu usuário não tem permissão para acessar o NTI Training.",
  503: "O serviço de autenticação está indisponível. Tente novamente em instantes.",
};

function greeting() {
  const hour = new Date().getHours();
  if (hour < 12) return "Bom dia";
  if (hour < 18) return "Boa tarde";
  return "Boa noite";
}

export function LoginScreen({ onLogin }: { onLogin: (user: SessionUser) => void }) {
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [errorCount, setErrorCount] = useState(0);
  const [capsLock, setCapsLock] = useState(false);
  const [showHelp, setShowHelp] = useState(false);
  const [hello] = useState(greeting);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setLoading(true);

    const formData = new FormData(event.currentTarget);
    const usuario = String(formData.get("username") ?? "");
    const senha = String(formData.get("password") ?? "");
    const lembrar = formData.get("remember") === "on";

    try {
      const { usuario: user } = await apiFetch<{ usuario: SessionUser }>("/api/auth/login", {
        method: "POST",
        body: JSON.stringify({ usuario, senha, lembrar }),
      });
      onLogin(user);
    }
    catch (exception) {
      // 429 (muitas tentativas) usa a mensagem do servidor, que informa quanto tempo esperar.
      const status = exception instanceof ApiError ? exception.status : 0;
      setError(loginErrors[status] ?? (exception instanceof ApiError ? exception.message : loginErrors[0]));
      setErrorCount((value) => value + 1);
    }
    finally {
      setLoading(false);
    }
  }

  return <main className="login-page">
    <section className="login-panel">
      <Brand />
      <div className="login-content">
        <span className="login-greeting"><span className="wave" aria-hidden="true">👋</span>{hello}! Que bom te ver.</span>
        <h1>Continue sua <em>jornada</em> de aprendizado</h1>
        <p className="login-lead">Entre com seu usuário e senha da rede para acessar tutoriais, trilhas e exercícios.</p>

        <form className="login-form" onSubmit={handleSubmit}>
          <div className="field">
            <label htmlFor="username">Usuário</label>
            <div className="input-wrap"><UserRound size={18} /><input id="username" name="username" placeholder="ex.: nome.sobrenome" autoComplete="username" autoCapitalize="none" spellCheck={false} required /></div>
          </div>
          <div className="field">
            <label htmlFor="password">Senha</label>
            <div className="input-wrap">
              <LockKeyhole size={18} />
              <input id="password" name="password" type={showPassword ? "text" : "password"} placeholder="Digite sua senha" autoComplete="current-password" required
                onKeyUp={(event) => setCapsLock(event.getModifierState("CapsLock"))} onBlur={() => setCapsLock(false)} />
              <button className="icon-button input-action" type="button" onClick={() => setShowPassword((value) => !value)} aria-label={showPassword ? "Ocultar senha" : "Mostrar senha"} title={showPassword ? "Ocultar senha" : "Mostrar senha"}>{showPassword ? <EyeOff size={18} /> : <Eye size={18} />}</button>
            </div>
            {capsLock && <p className="caps-warning"><ArrowBigUp size={15} />Caps Lock está ativado</p>}
          </div>

          <div className="login-options">
            <label className="remember-check" htmlFor="remember">
              <input id="remember" name="remember" type="checkbox" />
              <span>Lembrar de mim</span>
            </label>
            <button className="link-button" type="button" onClick={() => setShowHelp((value) => !value)} aria-expanded={showHelp}>Esqueci minha senha</button>
          </div>
          {showHelp && <p className="login-note"><Info size={17} /><span>Sua senha é a mesma usada para entrar no computador da rede. Para redefini-la, procure o suporte do NTI.</span></p>}

          {error && <p key={errorCount} className="login-error" role="alert"><CircleAlert size={17} /><span>{error}</span></p>}

          <button className="login-button" type="submit" disabled={loading}>
            {loading ? <><span className="spinner" aria-hidden="true" />Entrando...</> : <>Entrar<ArrowRight className="login-arrow" size={18} /></>}
          </button>
        </form>

        <p className="login-secure"><ShieldCheck size={15} />Acesso seguro com a sua conta da rede</p>
      </div>
      <footer className="login-footer">© {new Date().getFullYear()} NTI Training · Portal interno de aprendizagem</footer>
    </section>
    <LearningShowcase />
  </main>;
}
