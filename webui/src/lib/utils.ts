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
  const diff = (d1.getTime() - d2.getTime()) / 1000 / 60 / 60 / 24;
  if (Math.abs(diff) < 7) {
    console.log(diff);
    return rtf.format(diff, "days");
  } else if (Math.abs(diff) < 31) {
    const weeks = diff / 7;
    return rtf.format(Math.round(weeks), "weeks");
  } else if (Math.abs(diff) < 365) {
    const months = diff / 31;
    return rtf.format(Math.round(months), "months");
  } else {
    const years = diff / 365;
    return rtf.format(Math.round(years), "years");
  }
}

export { cn, toISOLocal, calculateTimediff };

