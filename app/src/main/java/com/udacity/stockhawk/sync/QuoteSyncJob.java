package com.udacity.stockhawk.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;
import yahoofinance.quotes.stock.StockStats;

public final class QuoteSyncJob {

    private static final int ONE_OFF_ID = 2;
    private static final String ACTION_DATA_UPDATED = "com.udacity.stockhawk.ACTION_DATA_UPDATED";
    private static final int PERIOD = 300000;
    private static final int INITIAL_BACKOFF = 10000;
    private static final int PERIODIC_ID = 1;
    private static final int YEARS_OF_HISTORY = 1;
    public static Cursor cursor;
    private QuoteSyncJob() {
    }

    public static void setCursor(Cursor c)
    {
        cursor=c;
    }
    static void getQuotes(Context context) {

        Timber.d("Running sync job");

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -YEARS_OF_HISTORY);

        try {
            Set<String> stockPref = PrefUtils.getStocks(context);
            Set<String> stockCopy = new HashSet<>();
            if(cursor!=null) {
                if(cursor.getCount()!=0) {
                    for (int i = 0; i <= cursor.getCount() - 1; i++) {
                        cursor.moveToPosition(i);
                        stockPref.add(cursor.getString(Contract.Quote.POSITION_SYMBOL));
                    }
                }

            }
            ArrayList<String> arrList=new ArrayList<>();
            for(String sym:stockPref)
            {
                arrList.add(sym);
            }
            for(String sym:arrList)
            {
                Stock testStock=YahooFinance.get(sym);
                if(testStock.getName()==null)
                {
                    stockPref.remove(sym);
                    Intent intent = new Intent();
                    intent.setAction("com.udacity.stockhawk.ui.MainActivity.STOCK_NOT_FOUND");
                    context.sendBroadcast(intent);
                }
            }
            stockCopy.addAll(stockPref);
            String[] stockArray = stockPref.toArray(new String[stockPref.size()]);
            Timber.d(stockCopy.toString());
            if (stockArray.length == 0) {
                return;
            }
            Map<String, Stock> quotes = YahooFinance.get(stockArray,true);
            Iterator<String> iterator = stockCopy.iterator();
            Timber.d(quotes.toString());

            ArrayList<ContentValues> quoteCVs = new ArrayList<>();


            while (iterator.hasNext()) {
                String symbol = iterator.next();
                Stock stock = quotes.get(symbol);
                StockQuote quote = stock.getQuote();
                StockStats stat = stock.getStats();


                String name = stock.getName();
                BigDecimal marketcap=stat.getMarketCap();
                BigDecimal dayLow=quote.getDayLow();
                BigDecimal dayHigh=quote.getDayHigh();
                BigDecimal yearLow=quote.getYearLow();
                BigDecimal yearHigh=quote.getYearHigh();
                BigDecimal quarterlyEstimate=stat.getEpsEstimateNextQuarter();
                BigDecimal yearlyEstimate=stat.getEpsEstimateNextYear();

                float price = quote.getPrice().floatValue();
                float change = quote.getChange().floatValue();
                float percentChange = quote.getChangeInPercent().floatValue();

                // WARNING! Don't request historical data for a stock that doesn't exist!
                // The request will hang forever X_x

                List<HistoricalQuote> history = stock.getHistory(from, to, Interval.MONTHLY);

                StringBuilder historyBuilder = new StringBuilder();

                for (HistoricalQuote it : history){

                    historyBuilder.append(it.getClose());
                    historyBuilder.append("%");
                }
                historyBuilder.append(",");
                for (HistoricalQuote it : history){


                    Date date=it.getDate().getTime();
                    SimpleDateFormat newFormat = new SimpleDateFormat("MM-dd-yy");
                    String finalString = newFormat.format(date);
                    historyBuilder.append(finalString);
                    historyBuilder.append("%");
                }

                ContentValues quoteCV = new ContentValues();
                quoteCV.put(Contract.Quote.COLUMN_SYMBOL, symbol);
                quoteCV.put(Contract.Quote.COLUMN_PRICE, price);
                quoteCV.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, percentChange);
                quoteCV.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, change);
                quoteCV.put(Contract.Quote.COLUMN_HISTORY, historyBuilder.toString());
                quoteCV.put(Contract.Quote.COLUMN_NAME, name);
                quoteCV.put(Contract.Quote.COLUMN_MARKETCAP, marketcap.toString());
                quoteCV.put(Contract.Quote.COLUMN_DAYS_LOW, dayLow.toString());
                quoteCV.put(Contract.Quote.COLUMN_DAYS_HIGH, dayHigh.toString());
                quoteCV.put(Contract.Quote.COLUMN_YEARS_LOW, yearLow.toString());
                quoteCV.put(Contract.Quote.COLUMN_YEARS_HIGH, yearHigh.toString());
                quoteCV.put(Contract.Quote.COLUMN_QUARTERLY_ESTIMATE, quarterlyEstimate.toString());
                quoteCV.put(Contract.Quote.COLUMN_YEARLY_ESTIMATE, yearlyEstimate.toString());
                quoteCVs.add(quoteCV);
            }
            context.getContentResolver()
                    .bulkInsert(
                            Contract.Quote.URI,
                            quoteCVs.toArray(new ContentValues[quoteCVs.size()]));

            Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
            context.sendBroadcast(dataUpdatedIntent);

        } catch (IOException exception) {
            Timber.e(exception, "Error fetching stock quotes");
        }
    }

    private static void schedulePeriodic(Context context) {
        Timber.d("Scheduling a periodic task");


        JobInfo.Builder builder = new JobInfo.Builder(PERIODIC_ID, new ComponentName(context, QuoteJobService.class));


        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(PERIOD)
                .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        scheduler.schedule(builder.build());
    }


    public static synchronized void initialize(final Context context) {

        schedulePeriodic(context);
        syncImmediately(context);

    }

    public static synchronized void syncImmediately(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            Intent nowIntent = new Intent(context, QuoteIntentService.class);
            context.startService(nowIntent);
        } else {

            JobInfo.Builder builder = new JobInfo.Builder(ONE_OFF_ID, new ComponentName(context, QuoteJobService.class));


            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            scheduler.schedule(builder.build());


        }
    }


}
