"use client";

import { Star } from "lucide-react";
import Link from "next/link";
import { TutorialRow } from "../../components/content";
import { EmptyState, ErrorState, LoadingState, PageHeading } from "../../components/ui";
import { useApi } from "../../lib/api";
import { useFavorites } from "../../lib/favorites";
import { useProgress } from "../../lib/progress";
import type { Tutorial } from "../../lib/types";

export default function FavoritesPage() {
  const tutorials = useApi<Tutorial[]>("/api/tutoriais");
  const { byTutorial } = useProgress();
  const { ids, ready } = useFavorites();
  // Mantém a ordem dos favoritos (mais recentes primeiro).
  const byId = new Map((tutorials.data ?? []).map((tutorial) => [tutorial.id, tutorial]));
  const favorites = ids.map((id) => byId.get(id)).filter((tutorial): tutorial is Tutorial => !!tutorial);

  return <div className="page-stack">
    <PageHeading title="Meus favoritos" description="Conteúdos que você salvou para acessar depois, em qualquer computador." />
    {tutorials.error ? <ErrorState error={tutorials.error} onRetry={tutorials.reload} />
      : !tutorials.data || !ready ? <LoadingState rows={3} />
        : favorites.length === 0 ? <EmptyState icon={Star} title="Nenhum favorito ainda" description="Toque no marcador de um tutorial para salvá-lo aqui." action={<Link className="secondary-button" href="/tutoriais">Ver tutoriais</Link>} />
          : <div className="tutorial-list">{favorites.map((tutorial) => <TutorialRow key={tutorial.id} tutorial={tutorial} progress={byTutorial.get(tutorial.id)} />)}</div>}
  </div>;
}
