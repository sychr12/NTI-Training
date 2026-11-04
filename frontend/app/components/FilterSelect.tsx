"use client";

import { Check, ChevronDown } from "lucide-react";
import { useEffect, useId, useRef, useState, type KeyboardEvent } from "react";

export type FilterOption = {
  value: string;
  label: string;
};

export function FilterSelect({ label, value, options, onChange, ariaLabel }: {
  label: string;
  value: string;
  options: FilterOption[];
  onChange: (value: string) => void;
  ariaLabel: string;
}) {
  const [open, setOpen] = useState(false);
  const [activeIndex, setActiveIndex] = useState(0);
  const rootRef = useRef<HTMLDivElement>(null);
  const labelId = useId();
  const listId = useId();
  const selectedIndex = Math.max(0, options.findIndex((option) => option.value === value));
  const selected = options[selectedIndex] ?? options[0];

  useEffect(() => {
    if (!open) return;

    function closeOnOutsideClick(event: PointerEvent) {
      if (!rootRef.current?.contains(event.target as Node)) setOpen(false);
    }

    document.addEventListener("pointerdown", closeOnOutsideClick);
    return () => document.removeEventListener("pointerdown", closeOnOutsideClick);
  }, [open]);

  function choose(index: number) {
    const option = options[index];
    if (!option) return;
    onChange(option.value);
    setOpen(false);
  }

  function handleKeyDown(event: KeyboardEvent<HTMLButtonElement>) {
    if (event.key === "Escape") {
      setOpen(false);
      return;
    }

    if (event.key === "ArrowDown" || event.key === "ArrowUp") {
      event.preventDefault();
      if (!open) {
        setOpen(true);
        setActiveIndex(selectedIndex);
        return;
      }
      const direction = event.key === "ArrowDown" ? 1 : -1;
      setActiveIndex((current) => (current + direction + options.length) % options.length);
      return;
    }

    if (open && event.key === "Home") {
      event.preventDefault();
      setActiveIndex(0);
      return;
    }

    if (open && event.key === "End") {
      event.preventDefault();
      setActiveIndex(options.length - 1);
      return;
    }

    if (open && (event.key === "Enter" || event.key === " ")) {
      event.preventDefault();
      choose(activeIndex);
    }
  }

  function toggle() {
    if (!open) setActiveIndex(selectedIndex);
    setOpen((current) => !current);
  }

  return <div ref={rootRef} className={`filter-select ${open ? "open" : ""}`}>
    <span id={labelId} className="filter-select-label">{label}</span>
    <button
      type="button"
      className="filter-select-trigger"
      role="combobox"
      aria-label={ariaLabel}
      aria-haspopup="listbox"
      aria-expanded={open}
      aria-controls={listId}
      aria-activedescendant={open ? `${listId}-option-${activeIndex}` : undefined}
      onClick={toggle}
      onKeyDown={handleKeyDown}
    >
      <span id={`${labelId}-value`}>{selected?.label ?? value}</span>
      <ChevronDown size={15} aria-hidden="true" />
    </button>
    {open && <div id={listId} className="filter-select-menu" role="listbox" aria-labelledby={labelId}>
      {options.map((option, index) => {
        const isSelected = option.value === value;
        return <button
          type="button"
          id={`${listId}-option-${index}`}
          key={option.value}
          className={`${isSelected ? "selected" : ""} ${activeIndex === index ? "active" : ""}`}
          role="option"
          aria-selected={isSelected}
          onMouseEnter={() => setActiveIndex(index)}
          onClick={() => choose(index)}
        >
          <span>{option.label}</span>
          {isSelected && <Check size={15} strokeWidth={2.5} aria-hidden="true" />}
        </button>;
      })}
    </div>}
  </div>;
}
