import { useEffect } from 'react';

/**
 * Cursor-follow botanical glow (Blueprint 15). Tracks the pointer and writes
 * --cursor-x / --cursor-y CSS vars consumed by .al-cursor-glow. Auto-disabled
 * on touch devices via the (hover: none) media query in index.css.
 */
export function BotanicalCursorGlow() {
  useEffect(() => {
    const hasPointer = window.matchMedia('(hover: hover)').matches;
    if (!hasPointer) return;

    let frame = 0;
    const handler = (e: PointerEvent) => {
      if (frame) return;
      frame = requestAnimationFrame(() => {
        document.documentElement.style.setProperty('--cursor-x', `${e.clientX}px`);
        document.documentElement.style.setProperty('--cursor-y', `${e.clientY}px`);
        frame = 0;
      });
    };
    window.addEventListener('pointermove', handler);
    return () => {
      window.removeEventListener('pointermove', handler);
      if (frame) cancelAnimationFrame(frame);
    };
  }, []);

  return <div className="al-cursor-glow" aria-hidden="true" />;
}
