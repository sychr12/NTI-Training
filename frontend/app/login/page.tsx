"use client";

import { useRouter } from "next/navigation";
import { useEffect } from "react";
import { useAuth } from "../components/auth";
import { LoginScreen } from "../components/LoginScreen";
import { Brand } from "../components/ui";

export default function LoginPage() {
  const auth = useAuth();
  const router = useRouter();

  // Quem já tem sessão vai direto para o início.
  useEffect(() => {
    if (auth.status === "authenticated") router.replace("/");
  }, [auth.status, router]);

  if (auth.status !== "anonymous") return <main className="auth-checking"><Brand /></main>;
  return <LoginScreen onLogin={auth.signIn} />;
}
