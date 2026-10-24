"use client";

import { ClipboardCheck } from "lucide-react";
import { useState } from "react";
import { CrudPage, type CrudConfig } from "../../../components/admin";
import { useApi } from "../../../lib/api";
import type { ExercicioGabarito, Tutorial } from "../../../lib/types";

const LETTERS = ["A", "B", "C", "D"];

export default function ManageExercisesPage() {
  const tutorials = useApi<Tutorial[]>("/api/tutoriais");
  const [selected, setSelected] = useState("");
  const tutorialId = selected || String(tutorials.data?.[0]?.id ?? "");

  const config: CrudConfig<ExercicioGabarito> = {
    // O gabarito (resposta correta) só é devolvido para quem edita conteúdo.
    listPath: tutorialId ? `/api/exercicios/gabarito?tutorialId=${tutorialId}` : null,
    createPath: "/api/exercicios",
    itemPath: (item) => `/api/exercicios/${item.id}`,
    entity: "exercício",
    newLabel: "Novo exercício",
    fields: [
      { name: "pergunta", label: "Pergunta", type: "textarea", rows: 2, required: true },
      ...LETTERS.map((letter) => ({ name: `alternativa${letter}`, label: `Alternativa ${letter}`, type: "text" as const, required: true })),
      { name: "respostaCorreta", label: "Resposta correta", type: "choice", options: LETTERS, required: true },
      { name: "explicacao", label: "Explicação", type: "textarea", rows: 2, hint: "Mostrada ao aluno depois que ele responde." },
    ],
    toForm: (item) => ({
      pergunta: item?.pergunta ?? "",
      alternativaA: item?.alternativaA ?? "", alternativaB: item?.alternativaB ?? "",
      alternativaC: item?.alternativaC ?? "", alternativaD: item?.alternativaD ?? "",
      respostaCorreta: item?.respostaCorreta ?? "",
      explicacao: item?.explicacao ?? "",
    }),
    toBody: (values) => ({ ...values, tutorialId: Number(tutorialId), explicacao: values.explicacao || null }),
    title: (item) => item.pergunta,
    meta: (item) => `Resposta correta: ${item.respostaCorreta} · ${item[`alternativa${item.respostaCorreta}` as "alternativaA"]}`,
    empty: { icon: ClipboardCheck, title: "Sem exercícios neste tutorial", description: "Crie perguntas de múltipla escolha para os alunos praticarem." },
    toolbar: <select className="admin-select" value={tutorialId} onChange={(event) => setSelected(event.target.value)} aria-label="Tutorial" disabled={!tutorials.data}>
      {!tutorials.data && <option>Carregando tutoriais...</option>}
      {tutorials.data?.map((tutorial) => <option key={tutorial.id} value={tutorial.id}>{tutorial.titulo}</option>)}
    </select>,
  };

  if (tutorials.data && tutorials.data.length === 0) {
    return <p className="admin-note">Cadastre um tutorial antes de criar exercícios.</p>;
  }
  return <CrudPage key={tutorialId} config={config} />;
}
