"use client";

import { BookA } from "lucide-react";
import { CrudPage, type CrudConfig } from "../../../components/admin";
import type { TermoGlossario } from "../../../lib/types";

const config: CrudConfig<TermoGlossario> = {
  listPath: "/api/glossario",
  createPath: "/api/glossario",
  itemPath: (item) => `/api/glossario/${item.id}`,
  entity: "termo",
  newLabel: "Novo termo",
  fields: (items) => [
    { name: "termo", label: "Termo", type: "text", required: true },
    { name: "categoria", label: "Categoria", type: "text", required: true, suggestions: [...new Set(items.map((item) => item.categoria))] },
    { name: "definicao", label: "Definição", type: "textarea", rows: 4, required: true },
  ],
  toForm: (item) => ({ termo: item?.termo ?? "", categoria: item?.categoria ?? "", definicao: item?.definicao ?? "" }),
  toBody: (values) => values,
  title: (item) => item.termo,
  meta: (item) => `${item.categoria} · ${item.definicao.length > 90 ? `${item.definicao.slice(0, 90)}…` : item.definicao}`,
  searchText: (item) => `${item.termo} ${item.definicao}`,
  empty: { icon: BookA, title: "Glossário vazio", description: "Cadastre os termos de TI que os alunos precisam conhecer." },
};

export default function ManageGlossaryPage() {
  return <CrudPage config={config} />;
}
