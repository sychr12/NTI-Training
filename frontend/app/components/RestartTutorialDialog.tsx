"use client";

import { RotateCcw, X } from "lucide-react";
import { useEffect, useRef, useState } from "react";
import { ApiError, apiFetch } from "../lib/api";

/** Pede confirmação antes de refazer: o progresso e as respostas do tutorial são apagados. */
export function RestartTutorialDialog({ tutorialId, onClose, onRestarted }: { tutorialId: number; onClose: () => void; onRestarted: () => void }) {
  const ref = useRef<HTMLDialogElement>(null);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState("");
  useEffect(() => { ref.current?.showModal(); }, []);
  const close = () => ref.current?.close();

  async function confirm() {
    setSending(true);
    setError("");
    try {
      await apiFetch(`/api/progresso/${tutorialId}/refazer`, { method: "POST" });
      onRestarted();
    }
    catch (exception) {
      setError(exception instanceof ApiError ? exception.message : "Não foi possível refazer o tutorial.");
      setSending(false);
    }
  }

  return <dialog ref={ref} className="modal" aria-labelledby="restart-tutorial-title" onClose={onClose} onClick={(event) => { if (event.target === event.currentTarget) close(); }}>
    <div className="modal-body">
      <button className="icon-button modal-close" onClick={close} aria-label="Fechar" title="Fechar"><X size={18} /></button>
      <span className="modal-icon"><RotateCcw size={24} /></span>
      <h2 id="restart-tutorial-title">Refazer este tutorial?</h2>
      <p>Você vai começar de novo: o progresso volta a 0% e suas respostas dos exercícios deste tutorial serão apagadas.</p>
      <ul className="modal-steps modal-notes">
        <li>Você perde o XP deste tutorial e dos exercícios dele até concluir de novo.</li>
        <li>Se ele completava uma trilha, o XP da trilha também sai até lá.</li>
        <li>As medalhas que você já ganhou continuam com você.</li>
      </ul>
      {error && <p className="login-error restart-error" role="alert">{error}</p>}
      <div className="modal-actions">
        <button className="secondary-button" onClick={close} disabled={sending}>Cancelar</button>
        <button className="primary-button" onClick={confirm} disabled={sending}>{sending ? "Refazendo..." : "Sim, refazer"}</button>
      </div>
    </div>
  </dialog>;
}
