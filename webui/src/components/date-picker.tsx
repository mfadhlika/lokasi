import { type DateRange } from "react-day-picker"

import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Calendar } from "@/components/ui/calendar";
import { compareAsc, format, subDays } from "date-fns";
import { Popover, PopoverContent } from "./ui/popover";
import { PopoverTrigger } from "@radix-ui/react-popover";
import { CalendarIcon } from "lucide-react";
import { Card, CardContent, CardFooter } from "@/components/ui/card";

export type DatePickerProps = {
    className?: string,
    date: DateRange | undefined,
    setDate: (date: DateRange | undefined) => void,
    variant?: "default" | "link" | "destructive" | "outline" | "secondary" | "ghost" | null | undefined
}

export function DatePicker({
    date,
    setDate,
    variant
}: DatePickerProps) {
    const selectDate = (value?: DateRange) => {
        value?.from?.setHours(0, 0, 0, 0);
        value?.to?.setHours(23, 59, 59, 59);
        setDate(value);
    }

    const formatDateRange = (range?: DateRange): string => {
        if (!range) return "Pick a date";
        if (compareAsc(range.from!.toDateString(), range.to!.toDateString()) === 0) return format(range.from!, "LLL dd, y");
        return `${format(range.from!, "LLL dd, y")} - ${format(range.to!, "LLL dd, y")}`
    }

    return (
        <Popover>
            <PopoverTrigger asChild>
                <Button id="date" variant={variant ?? "ghost"} className={cn(
                    "w-auto justify-start text-left font-normal",
                    !date && "text-muted-foreground"
                )}>
                    <CalendarIcon />
                    {formatDateRange(date)}
                </Button>
            </PopoverTrigger>
            <PopoverContent className="p-0 z-100000" align="start" asChild>
                <Card className="max-w-[300px] py-4">
                    <CardContent>
                        <Calendar
                            autoFocus
                            mode="range"
                            selected={date}
                            onSelect={selectDate}
                            captionLayout="dropdown"
                        />
                    </CardContent>
                    <CardFooter className="flex flex-wrap gap-2 border-t px-4 !pt-4">
                        {[
                            { label: "Today", value: 0 },
                            { label: "Yesterday", value: 1 },
                            { label: "Last week", value: 7 },
                            { label: "Last 2 weeks", value: 14 },
                            { label: "Last Month", value: 31 },
                        ].map((preset) => (
                            <Button
                                key={preset.value}
                                variant="outline"
                                size="sm"
                                className="flex-1"
                                onClick={() => {
                                    const todayDate = new Date();
                                    todayDate.setHours(23, 59, 59, 59);
                                    const newDate = subDays(todayDate, preset.value);
                                    newDate.setHours(0, 0, 0, 0);
                                    setDate({
                                        from: newDate,
                                        to: todayDate
                                    });
                                }}
                            >
                                {preset.label}
                            </Button>
                        ))}
                    </CardFooter>
                </Card>
            </PopoverContent>
        </Popover>
    )
}
