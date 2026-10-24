"use client";

import { Route } from "lucide-react";
import { CrudPage, type CrudConfig } from "../../../components/admin";
import { categoryVisual, LEVELS, levelLabel } from "../../../lib/catalog";
import type { Trilha } from "../../../lib/types";

const config: CrudConfig<Trilha> = {
  listPath: "/api/trilhas",
  createPath: "/api/trilhas",
  itemPath: (item) => `/api/trilhas/${item.id}`,
  entity: "trilha",
  newLabel: "Nova trilha",
  fields: [
    { name: "titulo", label: "Título", type: "text", required: true },
    { name: "descricao", label: "Descrição", type: "textarea", rows: 2 },
    { name: "nivel", label: "Nível", type: "select", required: true, options: LEVELS.map((level) => ({ value: level, label: level })) },
    { name: "tutorialIds", label: "Tutoriais da trilha", type: "tutorials", hint: "Os alunos seguem os tutoriais nesta ordem." },
  ],
  toForm: (item) => ({
    titulo: item?.titulo ?? "",
    descricao: item?.descricao ?? "",
    nivel: levelLabel(item?.nivel),
    tutorialIds: [...(item?.tutoriais ?? [])].sort((a, b) => a.ordem - b.ordem).map((tutorial) => tutorial.id).join(","),
  }),
  toBody: (values) => ({
    titulo: values.titulo,
    descricao: values.descricao || null,
    nivel: values.nivel,
    tutorialIds: values.tutorialIds ? values.tutorialIds.split(",").map(Number) : [],
  }),
  title: (item) => item.titulo,
  meta: (item) => `${levelLabel(item.nivel)} · ${item.tutoriais.length} ${item.tutoriais.length === 1 ? "tutorial" : "tutoriais"}`,
  visual: (item) => categoryVisual(item.tutoriais[0]?.categoria),
  empty: { icon: Route, title: "Nenhuma trilha cadastrada", description: "Monte uma trilha juntando tutoriais em sequência." },
};

export default function ManageTrailsPage() {
  return <CrudPage config={config} />;
}
