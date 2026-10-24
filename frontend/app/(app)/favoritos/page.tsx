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
  const { ids } = useFavorites();
  const favorites = (tutorials.data ?? []).filter((tutorial) => ids.includes(tutorial.id));

  return <div className="page-stack">
    <PageHeading title="Meus favoritos" description="Conteúdos que você salvou para acessar depois. Ficam guardados neste navegador." />
    {tutorials.error ? <ErrorState error={tutorials.error} onRetry={tutorials.reload} />
      : !tutorials.data ? <LoadingState rows={3} />
        : favorites.length === 0 ? <EmptyState icon={Star} title="Nenhum favorito ainda" description="Toque no marcador de um tutorial para salvá-lo aqui." action={<Link className="secondary-button" href="/tutoriais">Ver tutoriais</Link>} />
          : <div className="tutorial-list">{favorites.map((tutorial) => <TutorialRow key={tutorial.id} tutorial={tutorial} status={byTutorial.get(tutorial.id)} />)}</div>}
  </div>;
}
