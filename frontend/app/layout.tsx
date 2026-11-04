import type { Metadata } from "next";
import { Providers } from "./components/providers";
import "./globals.css";

export const metadata: Metadata = {
  title: { default: "NTI Training · IDAM", template: "%s · NTI Training · IDAM" },
  description: "Portal interno do IDAM para aprendizagem e desenvolvimento em tecnologia.",
};

// Aplica o tema salvo antes da primeira pintura, evitando o "flash" claro no modo escuro.
const themeScript = `(function(){try{var t=localStorage.getItem("nti-theme");if(t)document.documentElement.setAttribute("data-theme",t)}catch(e){}})()`;

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="pt-BR" data-theme="light" suppressHydrationWarning>
      <head>
        <script dangerouslySetInnerHTML={{ __html: themeScript }} />
      </head>
      <body><Providers>{children}</Providers></body>
    </html>
  );
}
