"use client";

import { createContext, useCallback, useContext, useEffect, useState, type ReactNode } from "react";
import { apiFetch, SESSION_EXPIRED_EVENT } from "../lib/api";
import type { SessionUser } from "../lib/types";

type AuthState =
  | { status: "loading"; user: null }
  | { status: "anonymous"; user: null }
  | { status: "authenticated"; user: SessionUser };

type AuthContextValue = AuthState & {
  signIn: (user: SessionUser) => void;
  signOut: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [state, setState] = useState<AuthState>({ status: "loading", user: null });

  useEffect(() => {
    let active = true;
    apiFetch<{ usuario: SessionUser }>("/api/auth/me").then(
      ({ usuario }) => { if (active) setState({ status: "authenticated", user: usuario }); },
      () => { if (active) setState({ status: "anonymous", user: null }); },
    );

    // Qualquer 401 da API (sessão expirada ou derrubada no servidor) volta para o login.
    const expire = () => setState({ status: "anonymous", user: null });
    window.addEventListener(SESSION_EXPIRED_EVENT, expire);
    return () => {
      active = false;
      window.removeEventListener(SESSION_EXPIRED_EVENT, expire);
    };
  }, []);

  const signIn = useCallback((user: SessionUser) => setState({ status: "authenticated", user }), []);

  const signOut = useCallback(async () => {
    await apiFetch("/api/auth/logout", { method: "POST" }).catch(() => undefined);
    setState({ status: "anonymous", user: null });
  }, []);

  return <AuthContext.Provider value={{ ...state, signIn, signOut }}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth precisa estar dentro de <AuthProvider>.");
  return context;
}
