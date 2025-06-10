import type { DateRange } from "react-day-picker"

import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Calendar } from "@/components/ui/calendar";
import { format, subDays, subMonths, subWeeks } from "date-fns";
import { Popover, PopoverContent } from "./ui/popover";
import { PopoverTrigger } from "@radix-ui/react-popover";
import { CalendarIcon } from "lucide-react";

export type DatePickerProps = {
    className: string,
    date: DateRange | undefined,
    setDate: (date: DateRange | undefined) => void
}

export function DatePicker({
    className,
    date,
    setDate
}: DatePickerProps) {

    const endOfDay = () => {
        const end = new Date();
        end.setHours(23, 59, 59, 59);
        return end;
    }

    const setToday = () => {
        const start = new Date();
        start.setHours(0, 0, 0, 0);

        setDate({
            from: start,
            to: endOfDay(),
        });
    };

    const setYesterday = () => {
        const start = subDays(new Date(), 1);
        start.setHours(0, 0, 0, 0);

        const end = subDays(new Date(), 1);
        end.setHours(23, 59, 59, 59);

        setDate({
            from: start,
            to: endOfDay(),
        });
    };

    const setLastWeek = () => {
        const start = subWeeks(new Date(), 1);
        start.setHours(0, 0, 0, 0);

        const end = new Date();
        end.setHours(23, 59, 59, 59);

        setDate({
            from: start,
            to: endOfDay(),
        });
    };

    const setLastMonth = () => {
        const start = subMonths(new Date(), 1);
        start.setHours(0, 0, 0, 0);

        const end = new Date();
        end.setHours(23, 59, 59, 59);

        setDate({
            from: start,
            to: endOfDay(),
        });
    };

    return (
        <div className={cn("grid gap-2", className)}>
            <Popover>
                <PopoverTrigger asChild>
                    <Button id="date" variant="outline" className={cn(
                        "w-auto justify-start text-left font-normal",
                        !date && "text-muted-foreground"
                    )}>
                        <CalendarIcon />
                        {date?.from ? (
                            date.to ? (
                                <>
                                    {format(date.from, "LLL dd, y")} -{" "}
                                    {format(date.to, "LLL dd, y")}
                                </>
                            ) : (
                                format(date.from, "LLL dd, y")
                            )
                        ) : (
                            <span>Pick a date</span>
                        )}
                    </Button>
                </PopoverTrigger>
                <PopoverContent className="w-auto p-0 z-100000" align="start">
                    <div className={cn("flex rounded-md gap-2 shadow-md", className)}>
                        <Calendar
                            autoFocus
                            mode="range"
                            selected={date}
                            onSelect={setDate}
                            className="border-r-2"
                        />
                        <div className="flex flex-col gap-2 pt-2 pr-2 items-start">
                            <Button variant="ghost" className="w-full justify-start" onClick={setToday}>Today</Button>
                            <Button variant="ghost" className="w-full justify-start" onClick={setYesterday}>Yesterday</Button>
                            <Button variant="ghost" className="w-full justify-start" onClick={setLastWeek}>Last Week</Button>
                            <Button variant="ghost" className="w-full justify-start" onClick={setLastMonth}>Last Month</Button>
                        </div>
                    </div>
                </PopoverContent>
            </Popover>
        </div>
    )
}
