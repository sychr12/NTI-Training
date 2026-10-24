"use client";

import type { ReactNode } from "react";
import { AuthProvider } from "./auth";
import { ThemeProvider } from "./theme";

export function Providers({ children }: { children: ReactNode }) {
  return <ThemeProvider><AuthProvider>{children}</AuthProvider></ThemeProvider>;
}
