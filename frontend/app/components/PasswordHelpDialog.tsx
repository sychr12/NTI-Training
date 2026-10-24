"use client";

import { LifeBuoy, ShieldCheck, X } from "lucide-react";
import { useEffect, useRef } from "react";

export function PasswordHelpDialog({ onClose }: { onClose: () => void }) {
  const ref = useRef<HTMLDialogElement>(null);
  useEffect(() => { ref.current?.showModal(); }, []);
  const close = () => ref.current?.close();
  return <dialog ref={ref} className="modal" aria-labelledby="password-help-title" onClose={onClose} onClick={(event) => { if (event.target === event.currentTarget) close(); }}>
    <div className="modal-body">
      <button className="icon-button modal-close" onClick={close} aria-label="Fechar" title="Fechar"><X size={18} /></button>
      <span className="modal-icon"><LifeBuoy size={24} /></span>
      <h2 id="password-help-title">Alteração de senha</h2>
      <p>Sua senha de acesso é a mesma da rede da empresa, por isso ela não pode ser alterada aqui no NTI Training. Para alterá-la, abra um chamado para o NTI:</p>
      <ol className="modal-steps">
        <li>Abra um chamado para o NTI solicitando a alteração da senha de rede.</li>
        <li>Informe seu usuário de rede e um contato para retorno.</li>
        <li>Aguarde a equipe do NTI entrar em contato com as orientações.</li>
      </ol>
      <p className="modal-tip"><ShieldCheck size={16} /><span>Nunca compartilhe sua senha, nem mesmo com a equipe do NTI.</span></p>
      <div className="modal-actions"><button className="primary-button" onClick={close}>Entendi</button></div>
    </div>
  </dialog>;
}
