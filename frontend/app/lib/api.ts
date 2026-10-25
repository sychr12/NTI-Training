"use client";

import { useCallback, useEffect, useState } from "react";

const API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

/** Disparado quando a API responde 401 fora das rotas de login: a sessão expirou. */
export const SESSION_EXPIRED_EVENT = "nti:session-expired";

/** Disparado quando o usuário conclui/inicia um tutorial ou responde exercícios, para atualizar resumos de progresso. */
export const PROGRESS_CHANGED_EVENT = "nti:progress-changed";

export function notifyProgressChanged() {
  window.dispatchEvent(new Event(PROGRESS_CHANGED_EVENT));
}

export class ApiError extends Error {
  constructor(public status: number, message: string) {
    super(message);
  }
}

export async function apiFetch<T>(path: string, init: RequestInit = {}): Promise<T> {
  let response: Response;
  try {
    response = await fetch(`${API_URL}${path}`, {
      ...init,
      credentials: "include",
      headers: init.body ? { "Content-Type": "application/json", ...init.headers } : init.headers,
    });
  }
  catch {
    throw new ApiError(0, "Não foi possível conectar ao servidor.");
  }

  if (!response.ok) {
    const data = await response.json().catch(() => null);
    if (response.status === 401 && !path.startsWith("/api/auth/")) {
      window.dispatchEvent(new Event(SESSION_EXPIRED_EVENT));
    }
    throw new ApiError(response.status, data?.mensagem ?? "Não foi possível carregar os dados.");
  }

  return response.status === 204 ? (undefined as T) : response.json();
}

type ApiState<T> = { path: string | null; version: number; data?: T; error?: ApiError };

/**
 * Busca `path` na API e devolve { data, error, loading, reload }.
 * `path` nulo não busca nada. Ao recarregar, os dados anteriores continuam visíveis.
 */
export function useApi<T>(path: string | null) {
  const [version, setVersion] = useState(0);
  const [state, setState] = useState<ApiState<T>>({ path: null, version: -1 });

  useEffect(() => {
    if (!path) return;
    let active = true;
    apiFetch<T>(path).then(
      (data) => { if (active) setState({ path, version, data }); },
      (error: ApiError) => { if (active) setState({ path, version, error }); },
    );
    return () => { active = false; };
  }, [path, version]);

  const reload = useCallback(() => setVersion((value) => value + 1), []);
  const samePath = state.path === path;
  return {
    data: samePath ? state.data : undefined,
    error: samePath && state.version === version ? state.error : undefined,
    loading: path !== null && (!samePath || state.version !== version),
    reload,
  };
}
