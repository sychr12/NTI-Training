"use client";

import Image from "next/image";
import {
  ArrowRight, Bell, BookA, BookOpenText, Bookmark, Building2,
  ChartNoAxesColumnIncreasing, CheckCircle2, ChevronDown, ChevronRight,
  ChevronUp, ClipboardCheck, Clock3, Code2, Eye, EyeOff, FolderOpen,
  GraduationCap, House, KeyRound, Lightbulb, Link2, LockKeyhole, LogOut,
  Mail, Menu, Monitor, Moon, Network, PlayCircle, RefreshCw, Route, Save,
  Search, Settings, ShieldCheck, Star, Timer, Trophy, UserRound, Wifi, Wrench,
  X, type LucideIcon,
} from "lucide-react";
import { FormEvent, useEffect, useMemo, useState } from "react";

type View = "inicio" | "tutoriais" | "tutorial" | "trilhas" | "exercicios" |
  "dicas" | "glossario" | "progresso" | "favoritos" | "configuracoes";

type Category = { name: string; count: number; icon: LucideIcon; color: string; soft: string };

const categories: Category[] = [
  { name: "Computador", count: 24, icon: Monitor, color: "#1769e0", soft: "#eaf2ff" },
  { name: "Internet", count: 18, icon: Wifi, color: "#15956b", soft: "#e9f8f2" },
  { name: "Programas", count: 16, icon: Code2, color: "#e26e2f", soft: "#fff1e8" },
  { name: "Redes", count: 12, icon: Network, color: "#1688c9", soft: "#e9f7fe" },
  { name: "Manutenção", count: 14, icon: Wrench, color: "#d69716", soft: "#fff7df" },
  { name: "Segurança", count: 16, icon: ShieldCheck, color: "#df4d5c", soft: "#ffedf0" },
];

const tutorials = [
  { title: "Como ligar e desligar o computador corretamente", category: "Computador", level: "Iniciante", time: "3 min", icon: Monitor, color: "#1769e0", soft: "#eaf2ff" },
  { title: "Como instalar um programa", category: "Programas", level: "Iniciante", time: "5 min", icon: Code2, color: "#e26e2f", soft: "#fff1e8" },
  { title: "Como conectar no Wi-Fi", category: "Internet", level: "Iniciante", time: "4 min", icon: Wifi, color: "#15956b", soft: "#e9f8f2" },
  { title: "Como criar uma pasta", category: "Computador", level: "Iniciante", time: "3 min", icon: FolderOpen, color: "#1688c9", soft: "#e9f7fe" },
  { title: "Como fazer backup de arquivos", category: "Segurança", level: "Intermediário", time: "6 min", icon: Save, color: "#df4d5c", soft: "#ffedf0" },
  { title: "Como atualizar o Windows", category: "Manutenção", level: "Iniciante", time: "5 min", icon: RefreshCw, color: "#d69716", soft: "#fff7df" },
];

const navItems: { id: View; label: string; icon: LucideIcon }[] = [
  { id: "inicio", label: "Início", icon: House },
  { id: "tutoriais", label: "Tutoriais", icon: BookOpenText },
  { id: "trilhas", label: "Trilhas", icon: Route },
  { id: "exercicios", label: "Exercícios", icon: ClipboardCheck },
  { id: "dicas", label: "Dicas", icon: Lightbulb },
  { id: "glossario", label: "Glossário", icon: BookA },
  { id: "progresso", label: "Meu progresso", icon: ChartNoAxesColumnIncreasing },
  { id: "favoritos", label: "Favoritos", icon: Star },
  { id: "configuracoes", label: "Configurações", icon: Settings },
];

const API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

function Brand({ compact = false }: { compact?: boolean }) {
  return <div className={`brand ${compact ? "brand-compact" : ""}`}>
    <span className="brand-mark"><GraduationCap size={22} strokeWidth={2.4} /></span>
    <span><strong>NTI Training</strong>{!compact && <small>Aprendizado para futuros talentos</small>}</span>
  </div>;
}

function LoginScreen({ onLogin }: { onLogin: () => void }) {
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setLoading(true);

    const formData = new FormData(event.currentTarget);
    const usuario = String(formData.get("username") ?? "");
    const senha = String(formData.get("password") ?? "");
    const lembrar = formData.get("remember") === "on";

    try {
      const response = await fetch(`${API_URL}/api/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ usuario, senha, lembrar }),
      });

      if (!response.ok) {
        const data = await response.json().catch(() => null);
        setError(data?.detail ?? data?.message ?? "Login ou senha inválidos.");
        return;
      }

      onLogin();
    }
    catch {
      setError("Não foi possível conectar ao servidor de autenticação.");
    }
    finally {
      setLoading(false);
    }
  }
  return <main className="login-page"><section className="login-shell">
    <div className="login-panel">
      <Brand />
      <div className="login-content">
      <div className="login-copy"><span className="eyebrow">Portal de aprendizagem</span><h1>Acesse sua conta</h1><p>Entre com seu usuário da rede para continuar.</p></div>
      <form className="login-form" onSubmit={handleSubmit}>
        <label htmlFor="username">Usuário</label>
        <div className="input-wrap"><UserRound size={17} /><input id="username" name="username" placeholder="Digite seu usuário" autoComplete="username" required /></div>
        <label htmlFor="password">Senha</label>
        <div className="input-wrap"><LockKeyhole size={17} /><input id="password" name="password" type={showPassword ? "text" : "password"} placeholder="Digite sua senha" autoComplete="current-password" required /><button className="icon-button input-action" type="button" onClick={() => setShowPassword((value) => !value)} aria-label={showPassword ? "Ocultar senha" : "Mostrar senha"} title={showPassword ? "Ocultar senha" : "Mostrar senha"}>{showPassword ? <EyeOff size={17} /> : <Eye size={17} />}</button></div>
        <div className="remember-row">
          <label className="remember-check" htmlFor="remember">
            <input id="remember" name="remember" type="checkbox" />
            <span>Lembrar de mim</span>
          </label>
        </div>
        {error && <p className="login-error" role="alert">{error}</p>}
        <button className="primary-button login-button" type="submit" disabled={loading}>{loading ? "Entrando..." : "Entrar"}{!loading && <ArrowRight size={17} />}</button>
      </form>
      </div>
    </div>
    <div className="login-art">
      <Image className="login-image" src="/login-hero.png" alt="Dois estudantes usando notebooks para aprender tecnologia" width={1536} height={1024} priority />
    </div>
  </section></main>;
}

function Sidebar({ current, onNavigate, open, onClose, onLogout }: { current: View; onNavigate: (view: View) => void; open: boolean; onClose: () => void; onLogout: () => void }) {
  const currentNav = current === "tutorial" ? "tutoriais" : current;
  return <>{open && <button className="sidebar-backdrop" aria-label="Fechar menu" onClick={onClose} />}<aside className={`sidebar ${open ? "sidebar-open" : ""}`}>
    <div className="sidebar-brand-row"><Brand compact /><button className="icon-button mobile-only" onClick={onClose} aria-label="Fechar menu" title="Fechar menu"><X size={20} /></button></div>
    <nav className="sidebar-nav" aria-label="Navegação principal">{navItems.map((item) => { const Icon = item.icon; return <button key={item.id} className={currentNav === item.id ? "active" : ""} onClick={() => { onNavigate(item.id); onClose(); }}><Icon size={17} /><span>{item.label}</span></button>; })}</nav>
    <div className="sidebar-footer"><div className="sidebar-progress"><span><Trophy size={16} /> Progresso geral</span><strong>65%</strong><div className="progress-track"><span style={{ width: "65%" }} /></div></div><button className="logout-button" onClick={onLogout}><LogOut size={17} /> Sair</button></div>
  </aside></>;
}

function Topbar({ onMenu, onSearch, onProfile }: { onMenu: () => void; onSearch: (value: string) => void; onProfile: () => void }) {
  const [query, setQuery] = useState("");
  return <header className="topbar">
    <button className="icon-button menu-button" onClick={onMenu} aria-label="Abrir menu" title="Abrir menu"><Menu size={21} /></button>
    <form className="top-search" onSubmit={(event) => { event.preventDefault(); onSearch(query); }}><Search size={17} /><input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Buscar conteúdos..." aria-label="Buscar conteúdos" /></form>
    <div className="topbar-actions"><button className="icon-button notification-button" aria-label="Notificações" title="Notificações"><Bell size={19} /><span /></button><button className="profile-button" onClick={onProfile}><span className="avatar">LF</span><span><strong>Luiz Felipe</strong><small>Estagiário de TI</small></span><ChevronDown size={15} /></button></div>
  </header>;
}

function PageHeading({ title, description, action }: { title: string; description: string; action?: React.ReactNode }) {
  return <div className="page-heading"><div><h1>{title}</h1><p>{description}</p></div>{action}</div>;
}

function IconBadge({ icon: Icon, color, soft }: { icon: LucideIcon; color: string; soft: string }) {
  return <span className="icon-badge" style={{ color, backgroundColor: soft }}><Icon size={21} /></span>;
}

function Dashboard({ onNavigate, onTutorial }: { onNavigate: (view: View) => void; onTutorial: () => void }) {
  return <div className="page-stack">
    <section className="welcome-panel"><div><span className="eyebrow">Terça-feira, 1 de setembro</span><h1>Olá, Luiz! <span aria-hidden="true">👋</span></h1><p>Continue aprendendo e evolua cada dia mais.</p></div><div className="overall-progress"><div><strong>65%</strong><span>concluído</span></div><p><b>Seu progresso geral</b><span>Parabéns! Continue assim.</span></p></div></section>
    <section className="content-section"><div className="section-heading"><div><h2>Categorias</h2><p>Encontre conteúdos por assunto</p></div><button className="text-button" onClick={() => onNavigate("tutoriais")}>Ver todas <ChevronRight size={15} /></button></div><div className="category-grid">{categories.map((category) => { const Icon = category.icon; return <button className="category-card" key={category.name} onClick={() => onNavigate("tutoriais")}><IconBadge icon={Icon} color={category.color} soft={category.soft} /><span><strong>{category.name}</strong><small>{category.count} tutoriais</small></span><ChevronRight size={16} /></button>; })}</div></section>
    <section className="content-section"><div className="section-heading"><div><h2>Tutoriais em destaque</h2><p>Seleções rápidas para continuar aprendendo</p></div><button className="text-button" onClick={() => onNavigate("tutoriais")}>Ver todos <ChevronRight size={15} /></button></div><div className="featured-grid">{tutorials.slice(0, 4).map((tutorial) => { const Icon = tutorial.icon; return <button className="featured-card" key={tutorial.title} onClick={onTutorial}><div className="featured-top"><IconBadge icon={Icon} color={tutorial.color} soft={tutorial.soft} /><Bookmark size={17} /></div><strong>{tutorial.title}</strong><div className="meta-row"><span className="level-badge">{tutorial.level}</span><span><Clock3 size={14} />{tutorial.time}</span></div></button>; })}</div></section>
    <section className="continue-band"><div className="continue-icon"><PlayCircle size={24} /></div><div><span>Continue de onde parou</span><strong>Internet para Iniciantes</strong><p>2 de 4 módulos concluídos</p></div><div className="continue-meter"><span>50%</span><div className="progress-track"><i style={{ width: "50%" }} /></div></div><button className="secondary-button" onClick={() => onNavigate("trilhas")}>Continuar <ArrowRight size={16} /></button></section>
  </div>;
}

function TutorialsPage({ initialSearch, onOpen }: { initialSearch: string; onOpen: () => void }) {
  const [category, setCategory] = useState("Todas");
  const [query, setQuery] = useState(initialSearch);
  const filtered = useMemo(() => tutorials.filter((tutorial) => (category === "Todas" || tutorial.category === category) && tutorial.title.toLowerCase().includes(query.toLowerCase())), [category, query]);
  return <div className="page-stack"><PageHeading title="Todos os tutoriais" description="Explore conteúdos práticos disponíveis na plataforma." />
    <div className="filter-toolbar"><div className="local-search"><Search size={17} /><input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Buscar tutorial..." /></div><div className="filter-pills">{["Todas", ...categories.map((item) => item.name)].map((item) => <button key={item} className={category === item ? "active" : ""} onClick={() => setCategory(item)}>{item}</button>)}</div></div>
    <div className="tutorial-list">{filtered.map((tutorial) => { const Icon = tutorial.icon; return <article className="tutorial-row" key={tutorial.title}><button className="tutorial-main" onClick={onOpen}><IconBadge icon={Icon} color={tutorial.color} soft={tutorial.soft} /><span><strong>{tutorial.title}</strong><small>{tutorial.category}<i />{tutorial.level}<i /><Clock3 size={13} />{tutorial.time}</small></span></button><button className="icon-button" aria-label="Adicionar aos favoritos" title="Adicionar aos favoritos"><Bookmark size={18} /></button><ChevronRight size={18} /></article>; })}{filtered.length === 0 && <div className="empty-state"><Search size={28} /><strong>Nenhum tutorial encontrado</strong><p>Tente outro termo ou escolha uma categoria diferente.</p></div>}</div>
  </div>;
}

function TutorialDetail({ onBack }: { onBack: () => void }) {
  const [tab, setTab] = useState("Passo a passo");
  const [done, setDone] = useState(false);
  const steps = [["Baixar o programa", "Acesse o site oficial e faça o download do instalador."], ["Abrir o instalador", "Localize o arquivo baixado e dê um duplo clique para abrir."], ["Seguir as instruções", "Siga as orientações do instalador na tela."], ["Finalizar a instalação", "Clique em concluir e abra o programa para testar."]];
  return <div className="page-stack"><div className="breadcrumbs"><button onClick={onBack}>Tutoriais</button><ChevronRight size={14} /><span>Programas</span><ChevronRight size={14} /><strong>Como instalar um programa</strong></div>
    <section className="tutorial-hero"><div><span className="level-badge">Iniciante</span><h1>Como instalar um programa</h1><p>Aprenda o passo a passo para instalar um programa no seu computador de forma correta e segura.</p><div className="meta-row hero-meta"><span><Clock3 size={15} /> 5 min</span><span><BookOpenText size={15} /> 4 etapas</span></div></div><div className="hero-icon"><Monitor size={54} /><span><ArrowRight size={20} /></span></div></section>
    <div className="detail-grid"><section className="detail-main"><div className="tabs" role="tablist">{["Passo a passo", "Exemplo", "Exercício", "Resumo"].map((item) => <button key={item} className={tab === item ? "active" : ""} onClick={() => setTab(item)}>{item}</button>)}</div>{tab === "Passo a passo" ? <div className="step-list">{steps.map(([title, description], index) => <div className="step-item" key={title}><span>{index + 1}</span><div><strong>{title}</strong><p>{description}</p></div><ChevronRight size={18} /></div>)}</div> : <div className="lesson-placeholder"><BookOpenText size={30} /><strong>{tab}</strong><p>Esta área já está preparada para receber o conteúdo cadastrado no sistema.</p></div>}</section>
      <aside className="lesson-aside"><div className="learning-card"><h2>Neste tutorial você vai aprender:</h2>{["Baixar um programa com segurança", "Abrir e instalar o programa", "Seguir as etapas de instalação", "Concluir a instalação e testar"].map((item) => <p key={item}><CheckCircle2 size={16} />{item}</p>)}</div><button className={`complete-button ${done ? "done" : ""}`} onClick={() => setDone((value) => !value)}><CheckCircle2 size={20} />{done ? "Tutorial concluído" : "Marcar como concluído"}</button></aside>
    </div></div>;
}

const trails = [
  { title: "Primeiros Passos no Computador", description: "Aprenda o básico para usar o computador com confiança.", modules: 5, progress: 60, icon: Monitor, color: "#1769e0", soft: "#eaf2ff" },
  { title: "Internet para Iniciantes", description: "Entenda como a internet funciona e como se conectar.", modules: 4, progress: 50, icon: Wifi, color: "#15956b", soft: "#e9f8f2" },
  { title: "Programas Essenciais", description: "Aprenda a instalar e usar os principais programas.", modules: 6, progress: 10, icon: Code2, color: "#df4d5c", soft: "#ffedf0" },
  { title: "Manutenção Básica", description: "Cuidados importantes para manter seu computador sempre bem.", modules: 5, progress: 20, icon: Wrench, color: "#d69716", soft: "#fff7df" },
];

function TrailsPage() {
  return <div className="page-stack"><PageHeading title="Trilhas de aprendizado" description="Siga uma trilha e aprenda passo a passo." /><div className="trail-list">{trails.map((trail) => <article className="trail-card" key={trail.title}><IconBadge icon={trail.icon} color={trail.color} soft={trail.soft} /><div className="trail-copy"><strong>{trail.title}</strong><p>{trail.description}</p></div><div className="trail-stat"><span>{trail.modules} módulos</span><div className="progress-track"><i style={{ width: `${trail.progress}%` }} /></div><b>{trail.progress}%</b></div><button className="icon-button" aria-label={`Abrir ${trail.title}`} title="Abrir trilha"><ChevronRight size={19} /></button></article>)}</div></div>;
}

function ProgressPage() {
  const metrics = [{ label: "Tutoriais concluídos", value: "18", icon: CheckCircle2, color: "#15956b", soft: "#e9f8f2" }, { label: "Em andamento", value: "7", icon: PlayCircle, color: "#7b61d1", soft: "#f1edff" }, { label: "Horas de estudo", value: "12h 30m", icon: Timer, color: "#1769e0", soft: "#eaf2ff" }, { label: "Progresso geral", value: "65%", icon: Trophy, color: "#d69716", soft: "#fff7df" }];
  return <div className="page-stack"><PageHeading title="Meu progresso" description="Acompanhe sua evolução e suas atividades recentes." /><div className="metric-grid">{metrics.map((metric) => <article className="metric-card" key={metric.label}><IconBadge icon={metric.icon} color={metric.color} soft={metric.soft} /><span><small>{metric.label}</small><strong>{metric.value}</strong></span></article>)}</div>
    <div className="progress-columns"><section className="panel"><div className="section-heading"><div><h2>Progresso por categoria</h2><p>Seu desempenho em cada tema</p></div></div><div className="category-progress">{categories.map((item, index) => { const value = [60, 50, 40, 30, 20, 70][index]; const Icon = item.icon; return <div key={item.name}><span><Icon size={16} style={{ color: item.color }} />{item.name}</span><div className="progress-track"><i style={{ width: `${value}%`, background: item.color }} /></div><b>{value}%</b></div>; })}</div></section>
      <section className="panel"><div className="section-heading"><div><h2>Atividade recente</h2><p>Seus últimos avanços</p></div></div><div className="activity-list">{[[CheckCircle2, "Concluiu: Como instalar um programa", "Hoje", "#15956b", "#e9f8f2"], [CheckCircle2, "Concluiu: Como conectar no Wi-Fi", "Ontem", "#1688c9", "#e9f7fe"], [PlayCircle, "Iniciou: Internet para Iniciantes", "2 dias atrás", "#7b61d1", "#f1edff"], [CheckCircle2, "Concluiu: Como criar uma pasta", "3 dias atrás", "#df4d5c", "#ffedf0"]].map(([Icon, text, date, color, soft]) => { const ActivityIcon = Icon as LucideIcon; return <div key={text as string}><IconBadge icon={ActivityIcon} color={color as string} soft={soft as string} /><span><strong>{text as string}</strong><small>{date as string}</small></span></div>; })}</div></section>
    </div></div>;
}

function ExercisesPage() {
  const items = [["Quiz: Conceitos básicos de computador", "10 questões", Monitor], ["Quiz: Internet e Navegação", "8 questões", Wifi], ["Quiz: Instalação de Programas", "9 questões", Code2], ["Quiz: Segurança Básica", "8 questões", ShieldCheck], ["Quiz: Redes Wi-Fi", "6 questões", Network]] as const;
  return <div className="page-stack"><PageHeading title="Exercícios" description="Pratique o que você aprendeu." /><div className="filter-pills page-filters">{["Todos", "Computador", "Internet", "Programas", "Redes", "Segurança"].map((item, index) => <button className={index === 0 ? "active" : ""} key={item}>{item}</button>)}</div><div className="exercise-list">{items.map(([title, questions, Icon], index) => <article key={title}><IconBadge icon={Icon} color={categories[index % categories.length].color} soft={categories[index % categories.length].soft} /><span><strong>{title}</strong><small>{questions}<i className="level-badge">Iniciante</i></small></span><button className="secondary-button">Iniciar <PlayCircle size={16} /></button></article>)}</div></div>;
}

function TipsPage() {
  const tips = [[LockKeyhole, "Use senhas fortes", "Misture letras, números e símbolos para aumentar sua segurança.", "#1769e0", "#eaf2ff"], [RefreshCw, "Mantenha tudo atualizado", "Atualizações corrigem erros e deixam seu computador mais seguro.", "#15956b", "#e9f8f2"], [Save, "Faça backup", "Salve seus arquivos importantes em locais seguros.", "#e26e2f", "#fff1e8"], [Link2, "Cuidado com links", "Não clique em links suspeitos recebidos por e-mail ou mensagem.", "#df4d5c", "#ffedf0"], [FolderOpen, "Organize seus arquivos", "Mantenha suas pastas organizadas para encontrar tudo com rapidez.", "#15956b", "#e9f8f2"], [Wrench, "Desative o que não usa", "Programas iniciando com o Windows podem deixar o PC lento.", "#1769e0", "#eaf2ff"]] as const;
  return <div className="page-stack"><PageHeading title="Dicas rápidas" description="Dicas práticas para o dia a dia." /><div className="tips-grid">{tips.map(([Icon, title, description, color, soft]) => <article key={title}><IconBadge icon={Icon} color={color} soft={soft} /><strong>{title}</strong><p>{description}</p><button className="text-button">Ler dica <ChevronRight size={15} /></button></article>)}</div></div>;
}

function GlossaryPage() {
  const [query, setQuery] = useState("");
  const [open, setOpen] = useState("Backup");
  const terms = [["Backup", "Cópia de segurança de dados para evitar perda de informações."], ["CPU", "Unidade central de processamento, o cérebro do computador."], ["Wi-Fi", "Tecnologia que permite conexão sem fio à internet."], ["IP", "Endereço que identifica um dispositivo em uma rede."], ["Antivírus", "Programa que protege o computador contra ameaças."]];
  return <div className="page-stack"><PageHeading title="Glossário de TI" description="Entenda os principais termos da área de tecnologia." /><div className="local-search glossary-search"><Search size={17} /><input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Buscar termo..." /></div><div className="glossary-list">{terms.filter(([term]) => term.toLowerCase().includes(query.toLowerCase())).map(([term, definition]) => <button key={term} onClick={() => setOpen(open === term ? "" : term)}><span><strong>{term}</strong>{open === term && <p>{definition}</p>}</span>{open === term ? <ChevronUp size={18} /> : <ChevronDown size={18} />}</button>)}</div></div>;
}

function FavoritesPage({ onOpen }: { onOpen: () => void }) {
  return <div className="page-stack"><PageHeading title="Meus favoritos" description="Conteúdos que você salvou para acessar depois." /><div className="tutorial-list">{tutorials.slice(1, 5).map((tutorial) => { const Icon = tutorial.icon; return <article className="tutorial-row" key={tutorial.title}><button className="tutorial-main" onClick={onOpen}><IconBadge icon={Icon} color={tutorial.color} soft={tutorial.soft} /><span><strong>{tutorial.title}</strong><small>{tutorial.category}<i />{tutorial.level}<i /><Clock3 size={13} />{tutorial.time}</small></span></button><button className="icon-button favorite-active" aria-label="Remover dos favoritos" title="Remover dos favoritos"><Bookmark size={18} fill="currentColor" /></button><ChevronRight size={18} /></article>; })}</div></div>;
}

function SettingsPage() {
  const [preferences, setPreferences] = useState({ reminders: true, email: true, dark: false });
  return <div className="page-stack"><PageHeading title="Meu perfil" description="Gerencie suas informações e preferências." /><div className="settings-grid"><section className="profile-panel"><h2>Informações</h2><div className="profile-summary"><span className="avatar avatar-large">LF</span><div><strong>Luiz Felipe</strong><small>Estagiário de TI</small></div></div><div className="profile-info"><p><Mail size={16} /><span><small>E-mail</small>luiz.felipe@empresa.com</span></p><p><Building2 size={16} /><span><small>Área</small>Tecnologia da Informação</span></p><p><KeyRound size={16} /><span><small>Nível</small>Estagiário</span></p></div></section>
    <section className="preferences-panel"><h2>Preferências</h2>{[["reminders", "Lembretes de estudo", Bell], ["email", "Notificações por e-mail", Mail], ["dark", "Modo escuro", Moon]].map(([key, label, Icon]) => { const PreferenceIcon = Icon as LucideIcon; const preferenceKey = key as keyof typeof preferences; return <div className="preference-row" key={key as string}><span><PreferenceIcon size={17} />{label as string}</span><button className={`switch ${preferences[preferenceKey] ? "on" : ""}`} onClick={() => setPreferences((value) => ({ ...value, [preferenceKey]: !value[preferenceKey] }))} aria-label={`Alternar ${label}`}><i /></button></div>; })}<div className="password-box"><div><LockKeyhole size={18} /><span><strong>Senha de acesso</strong><small>Gerenciada pela rede interna</small></span></div><button className="secondary-button">Solicitar alteração</button></div></section>
  </div></div>;
}

export default function Home() {
  const [loggedIn, setLoggedIn] = useState(false);
  const [view, setView] = useState<View>("inicio");
  const [mobileMenu, setMobileMenu] = useState(false);
  const [search, setSearch] = useState("");
  const [authChecked, setAuthChecked] = useState(false);

  useEffect(() => {
    let active = true;

    fetch(`${API_URL}/api/auth/me`, { credentials: "include" })
      .then((response) => {
        if (active && response.ok) {
          setLoggedIn(true);
        }
      })
      .catch(() => undefined)
      .finally(() => {
        if (active) {
          setAuthChecked(true);
        }
      });

    return () => {
      active = false;
    };
  }, []);

  function handleLogout() {
    fetch(`${API_URL}/api/auth/logout`, { method: "POST", credentials: "include" })
      .catch(() => undefined)
      .finally(() => {
        setLoggedIn(false);
        setView("inicio");
      });
  }

  if (!authChecked) return <main className="login-page"><section className="login-shell login-checking"><Brand /></section></main>;
  if (!loggedIn) return <LoginScreen onLogin={() => setLoggedIn(true)} />;
  const openTutorial = () => setView("tutorial");
  let content: React.ReactNode;
  switch (view) {
    case "tutoriais": content = <TutorialsPage initialSearch={search} onOpen={openTutorial} />; break;
    case "tutorial": content = <TutorialDetail onBack={() => setView("tutoriais")} />; break;
    case "trilhas": content = <TrailsPage />; break;
    case "exercicios": content = <ExercisesPage />; break;
    case "dicas": content = <TipsPage />; break;
    case "glossario": content = <GlossaryPage />; break;
    case "progresso": content = <ProgressPage />; break;
    case "favoritos": content = <FavoritesPage onOpen={openTutorial} />; break;
    case "configuracoes": content = <SettingsPage />; break;
    default: content = <Dashboard onNavigate={setView} onTutorial={openTutorial} />;
  }
  return <div className="app-shell"><Sidebar current={view} onNavigate={setView} open={mobileMenu} onClose={() => setMobileMenu(false)} onLogout={handleLogout} /><div className="app-main"><Topbar onMenu={() => setMobileMenu(true)} onSearch={(value) => { setSearch(value); setView("tutoriais"); }} onProfile={() => setView("configuracoes")} /><main className="page-content">{content}</main></div></div>;
}
