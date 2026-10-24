"use client";

import { useRouter } from "next/navigation";
import { useEffect, useState, type ReactNode } from "react";
import { Sidebar, Topbar } from "../components/AppChrome";
import { useAuth } from "../components/auth";
import { Brand } from "../components/ui";

// Layout de todas as telas que exigem login: sem sessão, manda para /login.
export default function AppLayout({ children }: { children: ReactNode }) {
  const auth = useAuth();
  const router = useRouter();
  const [mobileMenu, setMobileMenu] = useState(false);

  useEffect(() => {
    if (auth.status === "anonymous") router.replace("/login");
  }, [auth.status, router]);

  if (auth.status !== "authenticated") return <main className="auth-checking"><Brand /></main>;

  return <div className="app-shell">
    <Sidebar open={mobileMenu} onClose={() => setMobileMenu(false)} onLogout={auth.signOut} />
    <div className="app-main">
      <Topbar user={auth.user} onMenu={() => setMobileMenu(true)} />
      <main className="page-content">{children}</main>
    </div>
  </div>;
}
