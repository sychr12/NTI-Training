"use client";

import { createContext, useContext, useLayoutEffect, useState, type ReactNode } from "react";

const THEME_KEY = "nti-theme";
type Theme = "light" | "dark";

const ThemeContext = createContext<{ theme: Theme; toggleTheme: () => void } | null>(null);

export function ThemeProvider({ children }: { children: ReactNode }) {
  // O tema inicial vem do atributo que o script do layout já aplicou no <html>.
  const [theme, setTheme] = useState<Theme>(() => typeof document !== "undefined" && document.documentElement.dataset.theme === "dark" ? "dark" : "light");

  // useLayoutEffect reaplica antes da pintura (inclusive após o remount do Strict Mode em dev).
  useLayoutEffect(() => {
    document.documentElement.setAttribute("data-theme", theme);
    try { localStorage.setItem(THEME_KEY, theme); } catch { /* armazenamento indisponível */ }
  }, [theme]);

  const toggleTheme = () => setTheme((value) => (value === "dark" ? "light" : "dark"));
  return <ThemeContext.Provider value={{ theme, toggleTheme }}>{children}</ThemeContext.Provider>;
}

export function useTheme() {
  const context = useContext(ThemeContext);
  if (!context) throw new Error("useTheme precisa estar dentro de <ThemeProvider>.");
  return context;
}
