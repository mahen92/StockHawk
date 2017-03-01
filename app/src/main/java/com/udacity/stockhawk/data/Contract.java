package com.udacity.stockhawk.data;


import android.net.Uri;
import android.provider.BaseColumns;

import com.google.common.collect.ImmutableList;

public final class Contract {

    static final String AUTHORITY = "com.udacity.stockhawk";
    static final String PATH_QUOTE = "quote";
    static final String PATH_QUOTE_WITH_SYMBOL = "quote/*";
    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    private Contract() {
    }

    @SuppressWarnings("unused")
    public static final class Quote implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_QUOTE).build();
        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_ABSOLUTE_CHANGE = "absolute_change";
        public static final String COLUMN_PERCENTAGE_CHANGE = "percentage_change";
        public static final String COLUMN_HISTORY = "history";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_MARKETCAP = "marketcap";
        public static final String COLUMN_DAYS_LOW = "days_low";
        public static final String COLUMN_DAYS_HIGH = "days_high";
        public static final String COLUMN_YEARS_LOW = "years_low";
        public static final String COLUMN_YEARS_HIGH = "years_high";
        public static final String COLUMN_QUARTERLY_ESTIMATE = "quarterly_estimate";
        public static final String COLUMN_YEARLY_ESTIMATE = "yearly_estimate";


        public static final int POSITION_ID = 0;
        public static final int POSITION_SYMBOL = 1;
        public static final int POSITION_PRICE = 2;
        public static final int POSITION_ABSOLUTE_CHANGE = 3;
        public static final int POSITION_PERCENTAGE_CHANGE = 4;
        public static final int POSITION_HISTORY = 5;
        public static final int POSITION_NAME = 6;
        public static final int POSITION_MARKETCAP = 7;
        public static final int POSITION_DAYS_LOW = 8;
        public static final int POSITION_DAYS_HIGH = 9;
        public static final int POSITION_YEARS_LOW = 10;
        public static final int POSITION_YEARS_HIGH = 11;
        public static final int POSITION_QUARTERLY_ESTIMATE = 12;
        public static final int POSITION_YEARLY_ESTIMATE = 13;


        public static final ImmutableList<String> QUOTE_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_SYMBOL,
                COLUMN_PRICE,
                COLUMN_ABSOLUTE_CHANGE,
                COLUMN_PERCENTAGE_CHANGE,
                COLUMN_HISTORY,
                COLUMN_NAME,
                COLUMN_MARKETCAP,
                COLUMN_DAYS_LOW,
                COLUMN_DAYS_HIGH,
                COLUMN_YEARS_LOW,
                COLUMN_YEARS_HIGH,
                COLUMN_QUARTERLY_ESTIMATE,
                COLUMN_YEARLY_ESTIMATE
        );
        static final String TABLE_NAME = "quotes";

        public static Uri makeUriForStock(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        static String getStockFromUri(Uri queryUri) {
            return queryUri.getLastPathSegment();
        }


    }

}
