import { expect, describe, it, vi, beforeEach } from 'vitest';
import { relativeTime } from './utils';

describe('relativeTime', () => {
    beforeEach(() => {
        vi.useFakeTimers();
        vi.setSystemTime(1755056936577);
    })

    it('30 seconds ago', () => {
        expect(relativeTime(new Date(1755056906577))).toBe("30 seconds ago");
    });

    it('30 minutes ago', () => {
        expect(relativeTime(new Date(1755055136577))).toBe("30 minutes ago");
    });

    it('2 months ago', () => {
        expect(relativeTime(new Date('2025-06-08T19:50:16+07:00'))).toBe("2 months ago");
    });
})
