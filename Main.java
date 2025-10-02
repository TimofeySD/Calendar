import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

final class CalendarMath {
    private CalendarMath() {}

    static boolean isLeap(int y) {
        return (y % 400 == 0) || ((y % 4 == 0) && (y % 100 != 0));
    }

    static int monthLength(int y, int m) {
        final int[] base = {31,28,31,30,31,30,31,31,30,31,30,31};
        if (m == 2 && isLeap(y)) return 29;
        return base[m - 1];
    }

    static String monthNameRu(int m) {
        final String[] names = {
                "январь","февраль","март","апрель","май","июнь",
                "июль","август","сентябрь","октябрь","ноябрь","декабрь"
        };
        return names[m - 1];
    }

    static int dayOfWeekSakamotoMon0(int y, int m, int d) {
        int[] t = {0,3,2,5,0,3,5,1,4,6,2,4};
        if (m < 3) y -= 1;
        int w = (y + y/4 - y/100 + y/400 + t[m - 1] + d) % 7;
        w = (w + 7) % 7;
        return (w + 6) % 7;
    }
}

class MonthCalendar {
    private final int year, month;

    MonthCalendar(int year, int month) {
        this.year = year;
        this.month = month;
    }

    String nameRu() { return CalendarMath.monthNameRu(month); }
    int length()    { return CalendarMath.monthLength(year, month); }

    int firstWeekdayMon0() {
        return CalendarMath.dayOfWeekSakamotoMon0(year, month, 1);
    }

    List<int[]> weeksMatrix() {
        List<int[]> weeks = new ArrayList<>();
        int offset = firstWeekdayMon0();
        int days = length();
        int day = 1;

        int[] row = new int[7];
        Arrays.fill(row, 0);
        for (int c = offset; c < 7 && day <= days; c++) row[c] = day++;
        weeks.add(row);

        while (day <= days) {
            row = new int[7];
            for (int c = 0; c < 7 && day <= days; c++) row[c] = day++;
            weeks.add(row);
        }
        return weeks;
    }

    void print(Locale locale) {
        String title = (nameRu() + " " + year).toUpperCase(locale);
        System.out.println(center(title, 28));
        System.out.println(" Пн  Вт  Ср  Чт  Пт  Сб  Вс");
        for (int[] row : weeksMatrix()) {
            for (int i = 0; i < 7; i++) {
                System.out.print(row[i] == 0 ? "    " : String.format("%3d ", row[i]));
            }
            System.out.println();
        }
    }

    private String center(String s, int w) {
        if (s.length() >= w) return s;
        int left = (w - s.length()) / 2;
        return " ".repeat(left) + s;
    }
}

class YearCalendar {
    private final int year;
    YearCalendar(int year) {
        if (year < 1600) throw new IllegalArgumentException("Год ≥ 1600");
        this.year = year;
    }

    int year() { return year; }
    boolean isLeap() { return CalendarMath.isLeap(year); }
    MonthCalendar month(int m) { return new MonthCalendar(year, m); }

    void print(Locale locale) {
        System.out.printf("Календарь %d (%s)%n%n",
                year, isLeap() ? "високосный" : "обычный");
        for (int m = 1; m <= 12; m++) {
            month(m).print(locale);
            System.out.println();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Используйте: java Main <год>");
            return;
        }
        int year = Integer.parseInt(args[0]);
        YearCalendar yc = new YearCalendar(year);
        yc.print(new Locale("ru", "RU"));
    }
}
