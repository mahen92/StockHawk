package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.StockProvider;

/**
 * Created by Mahendran on 20-02-2017.
 */

public class StockWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockRVFactory(this.getApplicationContext(), intent);
    }

    /**
     * Equivalent to a CursorAdapter/ArrayAdapter with ListView.
     */
    public class StockRVFactory implements RemoteViewsFactory {

        private Context context;
        private Cursor cursor;
        private int appWidgetId;

        public StockRVFactory(Context context, Intent intent) {
            this.context = context;
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        /**
         * Method where we should initialize all our data collections.
         * In this case, our cursor.
         */
        @Override
        public void onCreate() {

            cursor = getContentResolver().query(
                    Contract.Quote.URI,
                    new String[]{Contract.Quote.COLUMN_SYMBOL, //0
                            Contract.Quote.COLUMN_PRICE, //1
                            Contract.Quote.COLUMN_ABSOLUTE_CHANGE, //2
                            Contract.Quote.COLUMN_PERCENTAGE_CHANGE, //3
                            Contract.Quote.COLUMN_HISTORY},//4
                             //13
                    null,
                    null,
                    null
            );
        }

        /**
         * Called when notifyDataSetChanged() is called.
         * Hence we can update the widget with new data!
         */
        @Override
        public void onDataSetChanged() {
            cursor = getContentResolver().query(
                    Contract.Quote.URI,
                    new String[]{Contract.Quote.COLUMN_SYMBOL, //0
                            Contract.Quote.COLUMN_PRICE, //1
                            Contract.Quote.COLUMN_ABSOLUTE_CHANGE, //2
                            Contract.Quote.COLUMN_PERCENTAGE_CHANGE, //3
                            Contract.Quote.COLUMN_HISTORY},//4
                    //13
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onDestroy() {
            //close the cursor.
            if (this.cursor != null)
                this.cursor.close();
        }

        @Override
        public int getCount() {
            //Meta-function for the AppWidgetManager
            return (this.cursor != null) ? this.cursor.getCount() : 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews remoteViews = new RemoteViews(this.context.getPackageName(), R.layout.list_item_quote);

            if (this.cursor.moveToPosition(position)) {
                String symbol = cursor.getString(0);
                remoteViews.setTextViewText(R.id.symbol, symbol);
                remoteViews.setTextViewText(R.id.price, cursor.getString(1));
                remoteViews.setTextViewText(R.id.change, cursor.getString(2));
               /* String name = cursor.getString(5);
                String currency = cursor.getString(6);
                String lasttradedate = cursor.getString(7);
                String daylow = cursor.getString(8);
                String dayhigh = cursor.getString(9);
                String yearlow = cursor.getString(10);
                String yearhigh = cursor.getString(11);
                String earningsshare = cursor.getString(12);
                String marketcapitalization = cursor.getString(13);*/
                if (cursor.getInt(4) == 1) {
                    remoteViews.setInt(R.id.change, "setBackgroundResource",
                            R.drawable.percent_change_pill_green);
                } else {
                    remoteViews.setInt(R.id.change, "setBackgroundResource",
                            R.drawable.percent_change_pill_red);
                }

                Bundle extras = new Bundle();
                extras.putString(StockWidgetProvider.EXTRA_SYMBOL, symbol);
                /*extras.putString(StockWidgetProvider.EXTRA_NAME, name);
                extras.putString(StockWidgetProvider.EXTRA_CURRENCY, currency);
                extras.putString(StockWidgetProvider.EXTRA_LASTTRADEDATE, lasttradedate);
                extras.putString(StockWidgetProvider.EXTRA_DAYLOW, daylow);
                extras.putString(StockWidgetProvider.EXTRA_DAYHIGH, dayhigh);
                extras.putString(StockWidgetProvider.EXTRA_YEARLOW, yearlow);
                extras.putString(StockWidgetProvider.EXTRA_YEARHIGH, yearhigh);
                extras.putString(StockWidgetProvider.EXTRA_EARNINGSSHARE, earningsshare);
                extras.putString(StockWidgetProvider.EXTRA_MARKETCAPITALIZATION, marketcapitalization);*/

                Intent fillInIntent = new Intent();
                fillInIntent.putExtras(extras);
                remoteViews.setOnClickFillInIntent(R.id.list_item_layout, fillInIntent);

            }


            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            //we have only one type of view to display so returning 1.
            return 1;
        }

        @Override
        public long getItemId(int position) {
            //Return the data from the ID column of the table.
            return this.cursor.getInt(0);
        }

        @Override
        public boolean hasStableIds() {
            /**
             * As the table contains a column called ID,
             * whose value we are returning at getItemId(),
             * and also is a primary column,
             * every Id is unique and hence stable.
             */
            return true;
        }
    }
}

