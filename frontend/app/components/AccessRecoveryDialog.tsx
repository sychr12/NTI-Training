"use client";

import { Building2, Headset, Mail, Phone, TicketCheck, X } from "lucide-react";
import { useEffect, useRef } from "react";

const supportContacts = [
  { label: "Setor", value: "NTI", icon: Building2 },
  { label: "Telefone", value: "(92) 98446-9237", href: "tel:+5592984469237", icon: Phone },
  { label: "Ramal", value: "8216", href: "tel:8216", icon: Phone },
  { label: "E-mail", value: "nti@idam.am.gov.br", href: "mailto:nti@idam.am.gov.br", icon: Mail },
];

type AccessRecoveryDialogProps = {
  onClose: () => void;
  mode?: "recovery" | "password";
};

export function AccessRecoveryDialog({ onClose, mode = "recovery" }: AccessRecoveryDialogProps) {
  const ref = useRef<HTMLDialogElement>(null);
  const passwordChange = mode === "password";
  const title = passwordChange ? "Alteração de senha" : "Recuperação de acesso";
  const description = passwordChange
    ? "Sua senha é a mesma da rede da empresa. Para solicitar a alteração, entre em contato com o NTI."
    : "Para recuperar seu acesso, entre em contato com o NTI.";
  const ticketDescription = passwordChange
    ? "Informe seu nome completo, matrícula, setor, usuário da rede e um contato para retorno."
    : "Informe seu nome completo, matrícula, setor e descreva o problema de acesso.";

  useEffect(() => { ref.current?.showModal(); }, []);

  const close = () => ref.current?.close();

  return <dialog
    ref={ref}
    className="modal recovery-modal"
    aria-labelledby="access-recovery-title"
    aria-describedby="access-recovery-description"
    onClose={onClose}
    onClick={(event) => { if (event.target === event.currentTarget) close(); }}
  >
    <div className="modal-body recovery-body">
      <button type="button" className="icon-button modal-close" onClick={close} aria-label={`Fechar ${title.toLowerCase()}`} title="Fechar"><X size={18} /></button>

      <span className="modal-icon recovery-icon"><Headset size={25} /></span>
      <strong className="recovery-eyebrow">Suporte</strong>
      <h2 id="access-recovery-title">{title}</h2>
      <p id="access-recovery-description">{description}</p>

      <div className="recovery-contacts" aria-label="Contatos do NTI">
        {supportContacts.map(({ label, value, href, icon: Icon }) => <div className="recovery-contact" key={label}>
          <Icon size={19} aria-hidden="true" />
          <span>
            <strong>{label}</strong>
            {href ? <a href={href}>{value}</a> : <small>{value}</small>}
          </span>
        </div>)}
      </div>

      <div className="recovery-ticket">
        <TicketCheck size={20} aria-hidden="true" />
        <span>
          <strong>Crie um chamado</strong>
          <small>{ticketDescription}</small>
        </span>
      </div>

      <div className="modal-actions recovery-actions">
        <button type="button" className="primary-button" onClick={close}>Fechar</button>
      </div>
    </div>
  </dialog>;
}
