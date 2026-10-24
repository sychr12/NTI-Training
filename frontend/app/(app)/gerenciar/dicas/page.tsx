"use client";

import { Lightbulb } from "lucide-react";
import { CrudPage, type CrudConfig } from "../../../components/admin";
import { categoryVisual } from "../../../lib/catalog";
import type { Dica } from "../../../lib/types";

const config: CrudConfig<Dica> = {
  listPath: "/api/dicas",
  createPath: "/api/dicas",
  itemPath: (item) => `/api/dicas/${item.id}`,
  entity: "dica",
  newLabel: "Nova dica",
  fields: (items) => [
    { name: "titulo", label: "Título", type: "text", required: true },
    { name: "categoria", label: "Categoria", type: "text", required: true, suggestions: [...new Set(items.map((item) => item.categoria))] },
    { name: "conteudo", label: "Texto da dica", type: "textarea", rows: 5, required: true },
  ],
  toForm: (item) => ({ titulo: item?.titulo ?? "", categoria: item?.categoria ?? "", conteudo: item?.conteudo ?? "" }),
  toBody: (values) => values,
  title: (item) => item.titulo,
  meta: (item) => item.categoria,
  visual: (item) => categoryVisual(item.categoria),
  searchText: (item) => `${item.titulo} ${item.categoria} ${item.conteudo}`,
  empty: { icon: Lightbulb, title: "Nenhuma dica cadastrada", description: "Compartilhe dicas rápidas para o dia a dia." },
};

export default function ManageTipsPage() {
  return <CrudPage config={config} />;
}
