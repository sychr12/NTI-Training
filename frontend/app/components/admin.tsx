"use client";

import { ArrowDown, ArrowUp, Eye, Pencil, Plus, Search, Trash2, X, type LucideIcon } from "lucide-react";
import { useEffect, useMemo, useRef, useState, type FormEvent, type ReactNode } from "react";
import { ApiError, apiFetch, useApi } from "../lib/api";
import { normalizeText, type Visual } from "../lib/catalog";
import type { Tutorial } from "../lib/types";
import { LessonContent } from "./content";
import { EmptyState, ErrorState, IconBadge, LoadingState } from "./ui";

export type FormValues = Record<string, string>;

type BaseField = { name: string; label: string; required?: boolean; hint?: string };
export type FieldConfig =
  | (BaseField & { type: "text"; placeholder?: string; suggestions?: string[] })
  | (BaseField & { type: "textarea"; rows?: number; placeholder?: string })
  | (BaseField & { type: "lesson" })
  | (BaseField & { type: "select"; options: { value: string; label: string }[] })
  | (BaseField & { type: "choice"; options: string[] })
  | (BaseField & { type: "tutorials" });

function errorMessage(exception: unknown, fallback: string) {
  return exception instanceof ApiError ? exception.message : fallback;
}

/* ---------- Formulário em popup ---------- */

function FormDialog({ title, fields, initial, onSubmit, onClose }: {
  title: string;
  fields: FieldConfig[];
  initial: FormValues;
  onSubmit: (values: FormValues) => Promise<void>;
  onClose: () => void;
}) {
  const ref = useRef<HTMLDialogElement>(null);
  const [values, setValues] = useState(initial);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => { ref.current?.showModal(); }, []);
  const close = () => ref.current?.close();

  async function submit(event: FormEvent) {
    event.preventDefault();
    setSaving(true);
    setError("");
    try {
      await onSubmit(values);
      close();
    }
    catch (exception) {
      setError(errorMessage(exception, "Não foi possível salvar."));
      setSaving(false);
    }
  }

  return <dialog ref={ref} className="modal modal-wide" aria-labelledby="form-dialog-title" onClose={onClose}>
    <form className="modal-body admin-form" onSubmit={submit}>
      <button type="button" className="icon-button modal-close" onClick={close} aria-label="Fechar" title="Fechar"><X size={18} /></button>
      <h2 id="form-dialog-title">{title}</h2>
      {fields.map((field) => <FormField key={field.name} field={field} value={values[field.name] ?? ""} onChange={(value) => setValues((current) => ({ ...current, [field.name]: value }))} />)}
      {error && <p className="login-error" role="alert">{error}</p>}
      <div className="modal-actions">
        <button type="button" className="secondary-button" onClick={close}>Cancelar</button>
        <button type="submit" className="primary-button" disabled={saving}>{saving ? "Salvando..." : "Salvar"}</button>
      </div>
    </form>
  </dialog>;
}

function FormField({ field, value, onChange }: { field: FieldConfig; value: string; onChange: (value: string) => void }) {
  const id = `field-${field.name}`;
  const label = <label htmlFor={id}>{field.label}{field.required && <span aria-hidden="true"> *</span>}</label>;
  const hint = field.hint && <small className="form-hint">{field.hint}</small>;

  switch (field.type) {
    case "text":
      return <div className="form-field">{label}
        <input id={id} value={value} onChange={(event) => onChange(event.target.value)} required={field.required} placeholder={field.placeholder} list={field.suggestions ? `${id}-list` : undefined} />
        {field.suggestions && <datalist id={`${id}-list`}>{field.suggestions.map((item) => <option key={item} value={item} />)}</datalist>}
        {hint}</div>;
    case "textarea":
      return <div className="form-field">{label}<textarea id={id} value={value} onChange={(event) => onChange(event.target.value)} required={field.required} rows={field.rows ?? 4} placeholder={field.placeholder} />{hint}</div>;
    case "lesson":
      return <LessonField id={id} label={label} hint={hint} value={value} onChange={onChange} required={field.required} />;
    case "select":
      return <div className="form-field">{label}<select id={id} value={value} onChange={(event) => onChange(event.target.value)} required={field.required}>
        {!field.required && <option value="">—</option>}
        {field.options.map((option) => <option key={option.value} value={option.value}>{option.label}</option>)}
      </select>{hint}</div>;
    case "choice":
      return <fieldset className="form-field form-choice"><legend>{field.label}{field.required && <span aria-hidden="true"> *</span>}</legend>
        <div>{field.options.map((option) => <label key={option} className={value === option ? "active" : ""}>
          <input type="radio" name={id} value={option} checked={value === option} onChange={() => onChange(option)} required={field.required} />{option}
        </label>)}</div>{hint}</fieldset>;
    case "tutorials":
      return <TutorialOrderField label={field.label} hint={hint} value={value} onChange={onChange} />;
  }
}

function LessonField({ id, label, hint, value, onChange, required }: { id: string; label: ReactNode; hint: ReactNode; value: string; onChange: (value: string) => void; required?: boolean }) {
  const [preview, setPreview] = useState(false);
  return <div className="form-field">
    <div className="form-field-head">{label}<button type="button" className="link-button" onClick={() => setPreview((current) => !current)}><Eye size={14} /> {preview ? "Editar" : "Pré-visualizar"}</button></div>
    {preview
      ? <div className="lesson-preview">{value.trim() ? <LessonContent text={value} /> : <p>Nada para mostrar ainda.</p>}</div>
      : <textarea id={id} value={value} onChange={(event) => onChange(event.target.value)} required={required} rows={12} />}
    {hint}
  </div>;
}

/** Lista ordenada de tutoriais de uma trilha; o valor é "id,id,id" na ordem. */
function TutorialOrderField({ label, hint, value, onChange }: { label: string; hint: ReactNode; value: string; onChange: (value: string) => void }) {
  const tutorials = useApi<Tutorial[]>("/api/tutoriais");
  const ids = value ? value.split(",").map(Number) : [];
  const byId = new Map((tutorials.data ?? []).map((tutorial) => [tutorial.id, tutorial]));
  const available = (tutorials.data ?? []).filter((tutorial) => !ids.includes(tutorial.id));

  const update = (next: number[]) => onChange(next.join(","));
  const move = (index: number, delta: number) => {
    const next = [...ids];
    [next[index], next[index + delta]] = [next[index + delta], next[index]];
    update(next);
  };

  return <div className="form-field">
    <label>{label}</label>
    {ids.length > 0 && <ol className="order-list">{ids.map((tutorialId, index) => <li key={tutorialId}>
      <span>{index + 1}</span>
      <strong>{byId.get(tutorialId)?.titulo ?? `Tutorial ${tutorialId}`}</strong>
      <button type="button" className="icon-button" onClick={() => move(index, -1)} disabled={index === 0} aria-label="Subir"><ArrowUp size={15} /></button>
      <button type="button" className="icon-button" onClick={() => move(index, 1)} disabled={index === ids.length - 1} aria-label="Descer"><ArrowDown size={15} /></button>
      <button type="button" className="icon-button" onClick={() => update(ids.filter((idValue) => idValue !== tutorialId))} aria-label="Remover da trilha"><X size={15} /></button>
    </li>)}</ol>}
    <select value="" onChange={(event) => event.target.value && update([...ids, Number(event.target.value)])} aria-label="Adicionar tutorial à trilha" disabled={!tutorials.data}>
      <option value="">{tutorials.data ? (available.length ? "+ Adicionar tutorial..." : "Todos os tutoriais já estão na trilha") : "Carregando tutoriais..."}</option>
      {available.map((tutorial) => <option key={tutorial.id} value={tutorial.id}>{tutorial.titulo}</option>)}
    </select>
    {hint}
  </div>;
}

/* ---------- Exclusão com confirmação ---------- */

function DeleteButton({ onConfirm }: { onConfirm: () => Promise<void> }) {
  const [confirming, setConfirming] = useState(false);
  const [busy, setBusy] = useState(false);
  if (!confirming) {
    return <button className="icon-button danger" onClick={() => setConfirming(true)} aria-label="Excluir" title="Excluir"><Trash2 size={16} /></button>;
  }
  return <span className="confirm-delete" role="group" aria-label="Confirmar exclusão">
    Excluir?
    <button className="danger-button" disabled={busy} onClick={async () => { setBusy(true); try { await onConfirm(); } finally { setBusy(false); setConfirming(false); } }}>Sim</button>
    <button className="text-button" onClick={() => setConfirming(false)}>Não</button>
  </span>;
}

/* ---------- Página de cadastro genérica ---------- */

export type CrudConfig<T extends { id: number }> = {
  listPath: string | null;
  createPath: string;
  itemPath: (item: T) => string;
  entity: string;
  newLabel: string;
  fields: FieldConfig[] | ((items: T[]) => FieldConfig[]);
  toForm: (item?: T) => FormValues;
  toBody: (values: FormValues) => unknown;
  title: (item: T) => string;
  meta?: (item: T) => ReactNode;
  visual?: (item: T) => Visual;
  searchText?: (item: T) => string;
  empty: { icon: LucideIcon; title: string; description: string };
  toolbar?: ReactNode;
};

export function CrudPage<T extends { id: number }>({ config }: { config: CrudConfig<T> }) {
  const list = useApi<T[]>(config.listPath);
  const [editing, setEditing] = useState<{ item?: T } | null>(null);
  const [query, setQuery] = useState("");
  const [error, setError] = useState("");

  const items = useMemo(() => list.data ?? [], [list.data]);
  const fields = typeof config.fields === "function" ? config.fields(items) : config.fields;
  const filtered = useMemo(() => {
    const term = normalizeText(query.trim());
    return term ? items.filter((item) => normalizeText(config.searchText?.(item) ?? config.title(item)).includes(term)) : items;
  }, [items, query, config]);

  async function save(values: FormValues, item?: T) {
    await apiFetch(item ? config.itemPath(item) : config.createPath, { method: item ? "PUT" : "POST", body: JSON.stringify(config.toBody(values)) });
    list.reload();
  }

  async function remove(item: T) {
    setError("");
    try {
      await apiFetch(config.itemPath(item), { method: "DELETE" });
      list.reload();
    }
    catch (exception) {
      setError(errorMessage(exception, "Não foi possível excluir."));
    }
  }

  return <>
    <div className="admin-toolbar">
      {config.toolbar}
      <div className="local-search"><Search size={17} /><input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Buscar..." aria-label="Buscar" /></div>
      <button className="primary-button" onClick={() => setEditing({})} disabled={!config.listPath}><Plus size={16} /> {config.newLabel}</button>
    </div>
    {error && <p className="login-error" role="alert">{error}</p>}

    {!config.listPath ? null
      : list.error ? <ErrorState error={list.error} onRetry={list.reload} />
        : !list.data ? <LoadingState rows={4} />
          : items.length === 0 ? <EmptyState icon={config.empty.icon} title={config.empty.title} description={config.empty.description} action={<button className="secondary-button" onClick={() => setEditing({})}><Plus size={15} /> {config.newLabel}</button>} />
            : <div className="admin-list">
              {filtered.map((item) => <article key={item.id}>
                {config.visual && <IconBadge visual={config.visual(item)} />}
                <span><strong>{config.title(item)}</strong>{config.meta && <small>{config.meta(item)}</small>}</span>
                <button className="icon-button" onClick={() => setEditing({ item })} aria-label={`Editar ${config.entity}`} title="Editar"><Pencil size={16} /></button>
                <DeleteButton onConfirm={() => remove(item)} />
              </article>)}
              {filtered.length === 0 && <EmptyState title="Nada encontrado" description="Tente buscar por outro termo." />}
            </div>}

    {editing && <FormDialog
      title={editing.item ? `Editar ${config.entity}` : config.newLabel}
      fields={fields}
      initial={config.toForm(editing.item)}
      onSubmit={(values) => save(values, editing.item)}
      onClose={() => setEditing(null)} />}
  </>;
}
