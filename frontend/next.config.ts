import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // O Dockerfile gera o build "standalone" (servidor mínimo); no dia a dia segue o padrão de next dev/start.
  output: process.env.NEXT_OUTPUT === "standalone" ? "standalone" : undefined,
};

export default nextConfig;
