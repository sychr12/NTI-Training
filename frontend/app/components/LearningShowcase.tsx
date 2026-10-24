"use client";

import { CircleCheck, Flame, Route } from "lucide-react";
import { type CSSProperties, useEffect, useState } from "react";

// Estilo da ilustração: true = cartoon (contorno de nanquim); false = flat.
const CARTOON = true;

const INK = "#14213d";
const CHAIR = "#0845ae";
const CHAIR_DARK = "#0a3580";

const highlights = [
  { title: "Aprenda no seu ritmo.", text: "Tutoriais curtos e práticos para resolver o dia a dia da TI." },
  { title: "Pratique de verdade.", text: "Exercícios e quizzes para fixar cada conteúdo aprendido." },
  { title: "Evolua a cada dia.", text: "Trilhas guiadas e progresso acompanhado de perto." },
];

const symbols = [
  { text: "</>", top: "12%", left: "8%", delay: "0s" },
  { text: "{ }", top: "22%", left: "86%", delay: "-3s" },
  { text: "01", top: "64%", left: "6%", delay: "-6s" },
  { text: "IP", top: "70%", left: "90%", delay: "-2s" },
  { text: "Wi-Fi", top: "8%", left: "58%", delay: "-8s" },
  { text: "?", top: "44%", left: "94%", delay: "-5s" },
];

type LessonKind = "network" | "wifi" | "password" | "backup";

// Aulas exibidas no quadro, em sequência: cada uma dura um ciclo da barra de progresso (6s).
type Lesson = { title: string; art: LessonKind; lines: [number, number, number]; topic: string; xp: number; trail: string; trailProgress: number };

const lessons: Lesson[] = [
  { title: "Aula 3 · Como funciona uma rede", art: "network", lines: [70, 102, 86], topic: "Redes", xp: 10, trail: "Redes", trailProgress: 75 },
  { title: "Aula 4 · Conectando ao Wi-Fi", art: "wifi", lines: [58, 96, 104], topic: "Wi-Fi", xp: 15, trail: "Internet", trailProgress: 50 },
  { title: "Aula 5 · Criando senhas fortes", art: "password", lines: [76, 88, 100], topic: "Senhas", xp: 20, trail: "Segurança", trailProgress: 40 },
  { title: "Aula 6 · Backup na nuvem", art: "backup", lines: [64, 104, 80], topic: "Backup", xp: 15, trail: "Segurança", trailProgress: 60 },
];

// Pacotes que saem do roteador (200,100) até cada computador do diagrama.
const packets = [
  { dx: -34, dy: -22, delay: "0s" },
  { dx: 34, dy: -22, delay: "-.6s" },
  { dx: -34, dy: 22, delay: "-1.2s" },
  { dx: 34, dy: 22, delay: "-1.8s" },
];

const sparkles = [
  { x: 84, y: 70, delay: "0s" },
  { x: 438, y: 58, delay: "-1s" },
  { x: 494, y: 190, delay: "-2s" },
  { x: 40, y: 220, delay: "-1.5s" },
];

// Pernas balançando sob a mesa: cada uma gira a partir do joelho (topo do grupo).
const legs = [
  { x: 160, pants: "#1f3b73", shoe: "#ff6b6b", swing: "4deg", duration: "1.4s", delay: "0s" },
  { x: 190, pants: "#1f3b73", shoe: "#ff6b6b", swing: "-4deg", duration: "1.4s", delay: "-.7s" },
  { x: 330, pants: "#2a2f45", shoe: "#ffffff", swing: "3deg", duration: "2.2s", delay: "-.3s" },
  { x: 360, pants: "#2a2f45", shoe: "#ffffff", swing: "-3deg", duration: "2.2s", delay: "-1.4s" },
];

const ROTATE_MS = 4500;

export function LearningShowcase() {
  const [index, setIndex] = useState(0);
  const [lessonIndex, setLessonIndex] = useState(0);
  const lesson = lessons[lessonIndex];
  // O card do quiz mostra a aula que acabou de terminar.
  const completed = lessons[(lessonIndex + lessons.length - 1) % lessons.length];

  useEffect(() => {
    const timer = window.setInterval(() => setIndex((value) => (value + 1) % highlights.length), ROTATE_MS);
    return () => window.clearInterval(timer);
  }, [index]);

  const highlight = highlights[index];

  return <aside className="login-showcase">
    <div className="showcase-grid" />
    <span className="showcase-orb orb-a" />
    <span className="showcase-orb orb-b" />
    {symbols.map((symbol) => <span key={symbol.text} className="showcase-symbol" style={{ top: symbol.top, left: symbol.left, animationDelay: symbol.delay }} aria-hidden="true">{symbol.text}</span>)}

    <span className="showcase-chip"><i className="live-dot" />Aprendizado contínuo em TI</span>

    <div className="showcase-scene">
      <LearningScene cartoon={CARTOON} lesson={lesson} onLessonEnd={() => setLessonIndex((value) => (value + 1) % lessons.length)} />
      <div className="float-card card-quiz" aria-hidden="true">
        <span key={`quiz-icon-${completed.title}`} className="float-icon float-pop" style={{ color: "#15956b", background: "#e9f8f2" }}><CircleCheck size={18} /></span>
        <span key={`quiz-${completed.title}`} className="float-swap"><strong>Quiz de {completed.topic}</strong><small>Concluído · +{completed.xp} XP</small></span>
      </div>
      <div className="float-card card-streak" aria-hidden="true">
        <span className="float-icon" style={{ color: "#e26e2f", background: "#fff1e8" }}><Flame size={18} /></span>
        <span><strong>5 dias seguidos</strong><small>Continue assim!</small></span>
      </div>
      <div className="float-card card-trail" aria-hidden="true">
        <span className="float-icon" style={{ color: "#0b6ffb", background: "#eaf2ff" }}><Route size={18} /></span>
        <span key={`trail-${lesson.title}`} className="float-swap"><strong>Trilha: {lesson.trail}</strong><span className="mini-track"><i style={{ width: `${lesson.trailProgress}%` }} /></span></span>
      </div>
    </div>

    <div className="showcase-copy">
      <div key={index} className="showcase-text" aria-live="polite">
        <h2>{highlight.title}</h2>
        <p>{highlight.text}</p>
      </div>
      <div className="showcase-dots">
        {highlights.map((item, itemIndex) => <button key={item.title} type="button" className={itemIndex === index ? "active" : ""} onClick={() => setIndex(itemIndex)} aria-label={`Mostrar destaque ${itemIndex + 1}`} />)}
      </div>
    </div>
  </aside>;
}

function Eye({ cx, cy, cartoon, delay }: { cx: number; cy: number; cartoon: boolean; delay?: string }) {
  return <g className="scene-blink" style={delay ? { animationDelay: delay } : undefined}>
    <ellipse className="no-ink" cx={cx} cy={cy} rx={cartoon ? 2.9 : 2.3} ry={cartoon ? 3.6 : 2.6} fill="#1d2a44" />
    {cartoon && <circle className="no-ink" cx={cx + 1} cy={cy - 1.4} r="1.1" fill="#ffffff" />}
  </g>;
}

function LessonArt({ art, cartoon }: { art: LessonKind; cartoon: boolean }) {
  switch (art) {
    case "wifi":
      return <g>
        {[12, 24, 36].map((r, index) => <path key={r} className="scene-wifi" style={{ animationDelay: `${index * .25}s` }}
          d={`M${200 - r * .707} ${118 - r * .707} A${r} ${r} 0 0 1 ${200 + r * .707} ${118 - r * .707}`}
          stroke="#0b6ffb" strokeWidth="5" fill="none" strokeLinecap="round" />)}
        <circle cx="200" cy="118" r="5" fill="#0b6ffb" />
      </g>;
    case "password":
      return <g>
        <path className="scene-shackle" d="M188 98 v-8 a12 12 0 0 1 24 0 v8" stroke={cartoon ? INK : "#1f2a44"} strokeWidth="5" fill="none" strokeLinecap="round" />
        <rect x="180" y="96" width="40" height="30" rx="7" fill="#ffc94d" />
        <circle className="no-ink" cx="200" cy="108" r="4" fill={INK} />
        <rect className="no-ink" x="198.5" y="110" width="3" height="8" rx="1.5" fill={INK} />
      </g>;
    case "backup":
      return <g>
        <path d="M184 104 h34 a8 8 0 0 0 0 -16 a12 12 0 0 0 -22 -5 a9 9 0 0 0 -12 8.5 a6.25 6.25 0 0 0 0 12.5 Z" fill="#eaf2ff" stroke={cartoon ? INK : "#0b6ffb"} strokeWidth="2" strokeLinejoin="round" />
        <path className="scene-upload" d="M201 100 v-10 M197 94 l4 -4 l4 4" stroke="#0b6ffb" strokeWidth="2.4" fill="none" strokeLinecap="round" strokeLinejoin="round" />
        <path d="M186 114 h10 l3 3 h17 a2 2 0 0 1 2 2 v13 a2 2 0 0 1 -2 2 h-30 a2 2 0 0 1 -2 -2 v-16 a2 2 0 0 1 2 -2 Z" fill="#ffd166" />
      </g>;
    default:
      return <g>
        <g stroke={cartoon ? INK : "#9cbdf0"} strokeWidth="2">
          <line x1="200" y1="100" x2="166" y2="78" />
          <line x1="200" y1="100" x2="234" y2="78" />
          <line x1="200" y1="100" x2="166" y2="122" />
          <line x1="200" y1="100" x2="234" y2="122" />
        </g>
        {packets.map((packet) => <circle key={packet.delay} className="scene-packet no-ink" cx="200" cy="100" r="2.8" fill="#ff9f43" style={{ "--dx": `${packet.dx}px`, "--dy": `${packet.dy}px`, animationDelay: packet.delay } as CSSProperties} />)}
        {[[166, 78], [234, 78], [166, 122], [234, 122]].map(([x, y]) => <g key={`${x}-${y}`}>
          <rect x={x - 9} y={y - 7} width="18" height="12" rx="3" fill="#eaf2ff" stroke={cartoon ? INK : "#0b6ffb"} strokeWidth={cartoon ? 2 : 1.6} />
          <rect className="no-ink" x={x - 4} y={y + 5} width="8" height="2.5" rx="1" fill={cartoon ? INK : "#0b6ffb"} />
        </g>)}
        <circle cx="200" cy="100" r="10" fill="#0b6ffb" />
        <path d="M195 100 h10 M200 95 v10" stroke="#ffffff" strokeWidth="2" strokeLinecap="round" />
      </g>;
  }
}

function LearningScene({ cartoon, lesson, onLessonEnd }: { cartoon: boolean; lesson: Lesson; onLessonEnd: () => void }) {
  return <svg className={`scene${cartoon ? " scene-cartoon" : ""}`} viewBox="-40 0 600 420" role="img" aria-label="Duas pessoas estudando juntas em seus notebooks enquanto uma aula sobre redes aparece no quadro entre elas">
    <defs>
      <linearGradient id="scene-board-fill" x1="0" y1="0" x2="0" y2="1">
        <stop offset="0" stopColor="#ffffff" />
        <stop offset="1" stopColor="#eef4ff" />
      </linearGradient>
      <radialGradient id="scene-glow-fill">
        <stop offset="0" stopColor="#9fd0ff" stopOpacity=".55" />
        <stop offset="1" stopColor="#9fd0ff" stopOpacity="0" />
      </radialGradient>
    </defs>

    <ellipse className="scene-glow no-ink" cx="260" cy="110" rx="200" ry="115" fill="url(#scene-glow-fill)" />
    <ellipse className="no-ink" cx="260" cy="396" rx="220" ry="13" fill="#04205a" opacity=".28" />

    {sparkles.map((sparkle) => <g key={`${sparkle.x}-${sparkle.y}`} transform={`translate(${sparkle.x} ${sparkle.y})`}>
      <path className="scene-twinkle" style={{ animationDelay: sparkle.delay }} d="M0 -7 L2 -2 L7 0 L2 2 L0 7 L-2 2 L-7 0 L-2 -2 Z" fill="#ffffff" />
    </g>)}

    {/* Fluxo de dados dos notebooks até o quadro */}
    <path className="scene-stream no-ink" d="M214 250 C 226 220, 222 190, 228 158" />
    <path className="scene-stream no-ink" d="M306 250 C 294 220, 298 190, 292 158" />

    {/* Quadro da aula */}
    <g className="scene-board">
      <rect x="140" y="26" width="240" height="130" rx="14" fill="url(#scene-board-fill)" />
      <circle cx="157" cy="43" r="4" fill="#ff6b6b" />
      <circle cx="170" cy="43" r="4" fill="#ffc94d" />
      <circle cx="183" cy="43" r="4" fill="#34d399" />
      <line x1="140" y1="58" x2="380" y2="58" stroke={cartoon ? INK : "#e3ebf8"} strokeWidth={cartoon ? 1.6 : 1} />

      {/* A key reinicia as animações do conteúdo a cada nova aula */}
      <g key={lesson.title} className="scene-lesson">
        <text className="scene-board-title" x="198" y="46">{lesson.title}</text>
        <LessonArt art={lesson.art} cartoon={cartoon} />
        <rect className="scene-line" x="264" y="70" width={lesson.lines[0]} height="7" rx="3.5" fill="#0b6ffb" />
        <rect className="scene-line" style={{ animationDelay: ".25s" }} x="264" y="84" width={lesson.lines[1]} height="6" rx="3" fill="#c9d9f3" />
        <rect className="scene-line" style={{ animationDelay: ".5s" }} x="264" y="96" width={lesson.lines[2]} height="6" rx="3" fill="#c9d9f3" />
        <g className="scene-answer">
          <rect x="264" y="110" width="104" height="22" rx="7" fill="#e9f8f2" stroke={cartoon ? INK : "#34d399"} strokeWidth={cartoon ? 2 : 1} />
          <circle cx="277" cy="121" r="6.5" fill="#15956b" />
          <path d="M274 121 l2.2 2.2 l4 -4.4" stroke="#ffffff" strokeWidth="1.8" fill="none" strokeLinecap="round" strokeLinejoin="round" />
          <text className="scene-answer-text" x="288" y="124">Resposta certa!</text>
        </g>
      </g>

      <rect x="152" y="140" width="216" height="6" rx="3" fill="#e3ebf8" />
      {/* Quando a barra completa o ciclo, a aula termina e o quadro passa para a próxima */}
      <rect className="scene-progress no-ink" x="152" y="140" width="216" height="6" rx="3" fill="#15956b"
        onAnimationIteration={onLessonEnd} />
    </g>

    {/* Aprendiz */}
    <rect x="133" y="205" width="84" height="110" rx="22" fill={CHAIR} />
    <g className="scene-breathe">
      <path d="M131 304 C131 256 148 236 175 236 C202 236 219 256 219 304 Z" fill="#ffb547" />
      {cartoon && <path className="no-ink" d="M183 238 C204 242 217 262 218 302 L200 302 C200 272 196 250 183 238 Z" fill="#f29a2e" />}
      <path d="M164 238 L175 251 L186 238" fill="none" stroke={cartoon ? INK : "#e8962a"} strokeWidth={cartoon ? 2.4 : 3} strokeLinejoin="round" />
      <rect x="168" y="212" width="14" height="27" rx="6" fill="#f2c4a0" />
      <g className="scene-nod">
        <path d="M195 186 C 214 192, 216 216, 205 230 C 204 214, 200 202, 192 195 Z" fill="#3b2416" />
        <circle cx="175" cy="196" r="23" fill="#f2c4a0" />
        <path d="M152 198 C150 173 163 166 177 166 C192 166 201 176 199 197 C193 185 183 180 171 181 C162 182 156 189 152 198 Z" fill="#3b2416" />
        {cartoon && <path d="M162 176 Q170 170 180 171" stroke="#ffffff" strokeOpacity=".55" strokeWidth="2.6" fill="none" strokeLinecap="round" />}
        <Eye cx={167} cy={199} cartoon={cartoon} />
        <Eye cx={183} cy={199} cartoon={cartoon} />
        <circle className="no-ink" cx="161" cy="207" r="3.2" fill="#ff8f7a" opacity={cartoon ? .7 : .45} />
        <circle className="no-ink" cx="189" cy="207" r="3.2" fill="#ff8f7a" opacity={cartoon ? .7 : .45} />
        {cartoon
          ? <path d="M168 206 Q175 214 182 206 Z" fill="#c2414f" stroke={INK} strokeWidth="1.8" strokeLinejoin="round" />
          : <path d="M169 207 Q175 212.5 181 207" stroke="#1d2a44" strokeWidth="2" fill="none" strokeLinecap="round" />}
        <path d="M149 197 C146 160 204 160 201 197" stroke="#1f2a44" strokeWidth="4" fill="none" strokeLinecap="round" />
        <rect x="143" y="189" width="11" height="18" rx="5" fill="#1f2a44" />
        <rect x="196" y="189" width="11" height="18" rx="5" fill="#1f2a44" />
        <rect className="no-ink" x="145.5" y="193" width="3" height="10" rx="1.5" fill="#0b6ffb" />
        <rect className="no-ink" x="201.5" y="193" width="3" height="10" rx="1.5" fill="#0b6ffb" />
      </g>
    </g>

    {/* Momento "entendi!" do aprendiz */}
    <g className="scene-idea">
      <g stroke={cartoon ? INK : "#ffd166"} strokeWidth="2.5" strokeLinecap="round">
        <line x1="112" y1="140" x2="112" y2="134" />
        <line x1="94" y1="148" x2="89" y2="144" />
        <line x1="130" y1="148" x2="135" y2="144" />
        <line x1="90" y1="166" x2="84" y2="166" />
        <line x1="134" y1="166" x2="140" y2="166" />
      </g>
      <circle cx="112" cy="164" r="13" fill="#ffd166" />
      {cartoon && <path d="M104 158 Q107 153 112 152" stroke="#ffffff" strokeWidth="2.4" fill="none" strokeLinecap="round" />}
      <path d="M107 166 l2.5 -4 l2.5 4 l2.5 -4 l2.5 4" stroke="#e08e0b" strokeWidth="1.6" fill="none" strokeLinecap="round" strokeLinejoin="round" />
      <rect x="105.5" y="175" width="13" height="8" rx="2.5" fill="#f0b43a" />
    </g>

    {/* Mentor */}
    <rect x="303" y="205" width="84" height="110" rx="22" fill={CHAIR} />
    <g className="scene-breathe" style={{ animationDelay: "-1.2s" }}>
      <path d="M301 304 C301 256 318 236 345 236 C372 236 389 256 389 304 Z" fill="#34d399" />
      {cartoon && <path className="no-ink" d="M353 238 C374 242 387 262 388 302 L370 302 C370 272 366 250 353 238 Z" fill="#22b07d" />}
      <path d="M334 238 L345 251 L356 238" fill="none" stroke={cartoon ? INK : "#1fae7d"} strokeWidth={cartoon ? 2.4 : 3} strokeLinejoin="round" />
      <rect x="338" y="212" width="14" height="27" rx="6" fill="#a86b45" />
      <g className="scene-tilt">
        <circle cx="345" cy="196" r="23" fill="#a86b45" />
        <path d="M322 195 C318 171 333 164 346 164 C360 164 372 172 368 195 C364 183 356 178 345 178 C334 178 326 184 322 195 Z" fill="#1b1b26" />
        <circle cx="329" cy="174" r="6.5" fill="#1b1b26" />
        <circle cx="339" cy="167" r="7" fill="#1b1b26" />
        <circle cx="352" cy="167" r="7" fill="#1b1b26" />
        <circle cx="362" cy="174" r="6.5" fill="#1b1b26" />
        {cartoon && <path d="M336 165 Q342 161 348 163" stroke="#ffffff" strokeOpacity=".45" strokeWidth="2.4" fill="none" strokeLinecap="round" />}
        <Eye cx={337} cy={199} cartoon={cartoon} delay="-2s" />
        <Eye cx={353} cy={199} cartoon={cartoon} delay="-2s" />
        <circle cx="337" cy="199" r="6.5" stroke="#1d2a44" strokeWidth="2" fill="#ffffff" fillOpacity=".18" />
        <circle cx="353" cy="199" r="6.5" stroke="#1d2a44" strokeWidth="2" fill="#ffffff" fillOpacity=".18" />
        <line x1="343.5" y1="199" x2="346.5" y2="199" stroke="#1d2a44" strokeWidth="2" />
        {cartoon
          ? <path d="M338 208 Q345 216 352 208 Z" fill="#c2414f" stroke={INK} strokeWidth="1.8" strokeLinejoin="round" />
          : <path d="M338 209 Q345 215 352 209" stroke="#1d2a44" strokeWidth="2" fill="none" strokeLinecap="round" />}
      </g>
    </g>

    {/* Braço do mentor apontando para o quadro */}
    <g className="scene-point">
      {cartoon && <path d="M324 252 C 312 238, 304 224, 300 206" stroke={INK} strokeWidth="18" fill="none" strokeLinecap="round" />}
      <path d="M324 252 C 312 238, 304 224, 300 206" stroke="#34d399" strokeWidth="13" fill="none" strokeLinecap="round" />
      {/* Mão desenhada apontando para cima e girada na direção do quadro */}
      <g transform="translate(299 201) rotate(-27)">
        <g className="scene-finger">
          <rect x="-6.5" y="-19" width="6" height="15" rx="3" fill="#a86b45" />
          {cartoon && <ellipse className="no-ink" cx="-3.5" cy="-16.5" rx="1.4" ry="1.8" fill="#ffffff" opacity=".35" />}
        </g>
        <rect x="-7.5" y="-7" width="15" height="14" rx="5.5" fill="#a86b45" />
        <g stroke={cartoon ? INK : "#7d4a2c"} strokeWidth="1.4" strokeLinecap="round">
          <line x1="0" y1="-2.5" x2="6" y2="-2.5" />
          <line x1="0" y1="1.5" x2="6" y2="1.5" />
        </g>
        <rect x="-11" y="-5" width="6" height="10" rx="3" fill="#a86b45" transform="rotate(-18 -8 0)" />      </g>
    </g>

    {/* Balão de fala do mentor */}
    <g className="scene-bubble">
      <path d="M403 144 h28 a15 15 0 0 1 0 30 h-19 l-20 11 l8 -11.5 a15 15 0 0 1 3 -29.5 Z" fill="#ffffff" />
      <circle className="scene-dot no-ink" cx="404" cy="159" r="3.3" fill="#0b6ffb" />
      <circle className="scene-dot no-ink" style={{ animationDelay: ".15s" }} cx="417" cy="159" r="3.3" fill="#0b6ffb" />
      <circle className="scene-dot no-ink" style={{ animationDelay: ".3s" }} cx="430" cy="159" r="3.3" fill="#0b6ffb" />
    </g>

    {/* Cadeiras de escritório: assento, coluna e base com rodinhas (atrás das pernas) */}
    {[175, 345].map((x) => <g key={x}>
      <rect x={x - 4} y="318" width="8" height="52" fill={CHAIR_DARK} />
      <rect x={x - 6} y="318" width="12" height="16" rx="2" fill={CHAIR_DARK} />
      <rect x={x - 32} y="368" width="64" height="7" rx="3.5" fill={CHAIR_DARK} />
      {[-28, 0, 28].map((dx) => <circle key={dx} cx={x + dx} cy="381" r="4.5" fill={INK} />)}
      <rect x={x - 38} y="311" width="76" height="10" rx="5" fill={CHAIR} />
    </g>)}

    {/* Pernas (desenhadas antes da mesa para o joelho ficar escondido sob o tampo) */}
    {legs.map((leg) => <g key={leg.x} className="scene-leg" style={{ "--swing": leg.swing, animationDuration: leg.duration, animationDelay: leg.delay } as CSSProperties}>
      <rect x={leg.x - 7} y="306" width="14" height="58" rx="7" fill={leg.pants} />
      <rect x={leg.x - 11} y="358" width="22" height="12" rx="6" fill={leg.shoe} />
      <rect className="no-ink" x={leg.x - 10} y="366.5" width="20" height="3" rx="1.5" fill={leg.shoe === "#ffffff" ? "#1b1b26" : "#ffffff"} />
    </g>)}

    {/* Linhas de movimento dos pés da aprendiz */}
    {cartoon && <g className="scene-motion" stroke="#ffffff" strokeWidth="2.2" fill="none" strokeLinecap="round">
      <path d="M138 352 q-6 8 0 16" />
      <path d="M131 355 q-4 5 0 10" />
      <path d="M212 352 q6 8 0 16" />
      <path d="M219 355 q4 5 0 10" />
    </g>}

    {/* Mesa */}
    <rect x="40" y="300" width="440" height="13" rx="6.5" fill="#eaf2ff" />
    <rect className="no-ink" x="54" y="313" width="412" height="6" fill="#a9c3f0" opacity=".55" />
    <rect x="74" y="313" width="9" height="80" rx="3" fill="#cfdcf5" />
    <rect x="437" y="313" width="9" height="80" rx="3" fill="#cfdcf5" />

    {/* Notebooks */}
    {[175, 345].map((x, laptopIndex) => <g key={x}>
      <rect x={x - 55} y="294" width="110" height="7" rx="3.5" fill="#c3d3ef" />
      <rect x={x - 47} y="246" width="94" height="52" rx="7" fill="#f7faff" />
      <circle className="scene-logo no-ink" style={{ animationDelay: laptopIndex ? "-1.2s" : "0s" }} cx={x} cy="272" r="10" fill="#0b6ffb" opacity=".18" />
      <circle cx={x} cy="272" r="4.5" fill="#0b6ffb" />
    </g>)}

    {/* Livros */}
    <rect x="243" y="293" width="36" height="7" rx="2" fill="#ff8a65" />
    <rect x="246" y="286" width="31" height="7" rx="2" fill="#ffd166" />
    <rect x="242" y="279" width="34" height="7" rx="2" fill="#7b61ff" />

    {/* Caneca */}
    <path className="scene-steam" d="M76 274 C72 268 80 264 76 258" stroke="#ffffff" strokeWidth="2" fill="none" strokeLinecap="round" />
    <path className="scene-steam" style={{ animationDelay: "-1.5s" }} d="M84 274 C80 268 88 264 84 258" stroke="#ffffff" strokeWidth="2" fill="none" strokeLinecap="round" />
    {cartoon && <path d="M90 285 C98 285 98 295 90 295" stroke={INK} strokeWidth="6.5" fill="none" />}
    <path d="M90 285 C98 285 98 295 90 295" stroke="#ffffff" strokeWidth="3" fill="none" />
    <rect x="70" y="280" width="20" height="20" rx="4" fill="#ffffff" />

    {/* Planta */}
    <g className="scene-sway">
      <path d="M455 284 C445 272 440 260 446 246 C452 258 457 268 455 284 Z" fill="#34d399" />
      <path d="M455 284 C462 270 470 262 479 258 C475 271 467 279 455 284 Z" fill="#1fae7d" />
      <path d="M455 284 C452 266 456 250 463 239 C465 254 461 270 455 284 Z" fill="#2ec48a" />
    </g>
    <path d="M441 300 L445 282 L466 282 L470 300 Z" fill="#ff8a65" />
  </svg>;
}
