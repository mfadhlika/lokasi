import { type DateRange } from "react-day-picker"

import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Calendar } from "@/components/ui/calendar";
import { format, subDays, subMonths, subWeeks } from "date-fns";
import { Popover, PopoverContent } from "./ui/popover";
import { PopoverTrigger } from "@radix-ui/react-popover";
import { CalendarIcon } from "lucide-react";
import { Select, SelectContent, SelectItem, SelectValue, SelectTrigger } from "@/components/ui/select";
import { useState } from "react";

export type DatePickerProps = {
    className?: string,
    date: DateRange | undefined,
    setDate: (date: DateRange | undefined) => void,
    variant?: "default" | "link" | "destructive" | "outline" | "secondary" | "ghost" | null | undefined
}

export function DatePicker({
    className,
    date,
    setDate,
    variant
}: DatePickerProps) {
    const [value, setValue] = useState<string>();
    const [key, setKey] = useState<number>(0);

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

    const onValueChange = (value: string) => {
        switch (value) {
            case "today":
                setToday();
                break;
            case "yesterday":
                setYesterday();
                break;
            case "lastweek":
                setLastWeek();
                break;
            case "lastmonth":
                setLastMonth();
                break;
        }

        setValue(value);
    }

    return (
        <div className={cn("grid gap-2", className)}>
            <Popover>
                <PopoverTrigger asChild>
                    <Button id="date" variant={variant ?? "ghost"} className={cn(
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
                    <div className={cn("flex flex-1 flex-col rounded-md gap-2 shadow-md", className)}>
                        <div className="flex flex-1 pr-2 pt-2 justify-end">
                            <Select key={key} value={value} onValueChange={onValueChange}>
                                <SelectTrigger>
                                    <SelectValue placeholder="Select a period" />
                                </SelectTrigger>
                                <SelectContent className="z-10000000">
                                    <SelectItem value="today">Today</SelectItem>
                                    <SelectItem value="yesterday">Yesterday</SelectItem>
                                    <SelectItem value="lastweek">Last Week</SelectItem>
                                    <SelectItem value="lastmonth">Last Month</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>
                        <Calendar
                            autoFocus
                            mode="range"
                            selected={date}
                            onSelect={(d) => {
                                setDate(d);
                                setValue(undefined);
                                setKey((k) => k + 1);
                            }}
                            className="border-r-2"
                        />
                    </div>
                </PopoverContent>
            </Popover>
        </div>
    )
}
