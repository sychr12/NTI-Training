"use client";

import { BookOpenText } from "lucide-react";
import { CrudPage, type CrudConfig } from "../../components/admin";
import { categoryVisual, LEVELS, levelLabel, readingTime } from "../../lib/catalog";
import type { Tutorial } from "../../lib/types";

const config: CrudConfig<Tutorial> = {
  listPath: "/api/tutoriais",
  createPath: "/api/tutoriais",
  itemPath: (item) => `/api/tutoriais/${item.id}`,
  entity: "tutorial",
  newLabel: "Novo tutorial",
  fields: (items) => [
    { name: "titulo", label: "Título", type: "text", required: true },
    { name: "descricao", label: "Descrição curta", type: "textarea", rows: 2, hint: "Aparece abaixo do título e na tela inicial." },
    { name: "categoria", label: "Categoria", type: "text", suggestions: [...new Set(items.map((item) => item.categoria).filter((value): value is string => !!value))], hint: "Escolha uma existente ou digite uma nova." },
    { name: "nivel", label: "Nível", type: "select", required: true, options: LEVELS.map((level) => ({ value: level, label: level })) },
    { name: "conteudo", label: "Conteúdo", type: "lesson", required: true, hint: "Linha em branco separa parágrafos. \"1. Título: explicação\" vira passo a passo e \"- item\" vira lista." },
  ],
  toForm: (item) => ({ titulo: item?.titulo ?? "", descricao: item?.descricao ?? "", categoria: item?.categoria ?? "", nivel: levelLabel(item?.nivel), conteudo: item?.conteudo ?? "" }),
  toBody: (values) => ({ ...values, descricao: values.descricao || null, categoria: values.categoria || null }),
  title: (item) => item.titulo,
  meta: (item) => `${item.categoria ?? "Geral"} · ${levelLabel(item.nivel)} · ${readingTime(item.conteudo)} de leitura`,
  visual: (item) => categoryVisual(item.categoria),
  searchText: (item) => `${item.titulo} ${item.categoria ?? ""}`,
  empty: { icon: BookOpenText, title: "Nenhum tutorial cadastrado", description: "Crie o primeiro tutorial da plataforma." },
};

export default function ManageTutorialsPage() {
  return <CrudPage config={config} />;
}
