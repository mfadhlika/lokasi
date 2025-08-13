import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"

function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

function toISOLocal(date: Date) {
  if (!date) return;
  return date.toISOString().split(/:\d{2}\..*/)[0];
}

function calculateTimediff(d1: Date, d2: Date = new Date()) {
  const rtf = new Intl.RelativeTimeFormat("en", { style: "long" });
  let diff = (d1.getTime() - d2.getTime()) / 1000;
  if (Math.abs(diff) < 60) {
    return rtf.format(Math.round(diff), "seconds");
  }

  diff /= 60;
  if (Math.abs(diff) < 60) {
    return rtf.format(Math.round(diff), "minutes");
  }

  diff /= 60 / 24
  if (Math.abs(diff) < 1) {
    return rtf.format(Math.round(diff), "hours");
  }

  if (Math.abs(diff) < 7) {
    return rtf.format(Math.round(diff), "days");
  }

  if (Math.abs(diff) < 31) {
    return rtf.format(Math.round(diff / 7), "weeks");
  }

  if (Math.abs(diff) < 365) {
    return rtf.format(Math.round(diff / 31), "months");
  }

  return rtf.format(Math.round(diff / 365), "years");
}

export { cn, toISOLocal, calculateTimediff };

