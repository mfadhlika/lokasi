import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"

function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

function toISOLocal(date: Date) {
  if (!date) return;
  return date.toISOString().split(/:\d{2}\..*/)[0];
}

export { cn, toISOLocal };
