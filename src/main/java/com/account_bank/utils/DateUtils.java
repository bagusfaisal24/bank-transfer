package com.account_bank.utils;

import java.time.LocalDate;

public class DateUtils {

    public static String getDateCustom() {
        LocalDate date = LocalDate.now();
        final String formatYYYYMMMDD = "%d%s%d";
        String getMonth = getMonth(date);
        return String.format(formatYYYYMMMDD, date.getYear(), getMonth, date.getDayOfMonth());
    }


    private static String getMonth(LocalDate date) {
        String[] romans = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        return romans[date.getMonthValue() - 1];
    }
}
