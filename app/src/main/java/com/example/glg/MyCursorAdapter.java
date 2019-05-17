package com.example.glg;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import static com.example.glg.MainActivity.KEY_NAME;
import static com.example.glg.MainActivity.KEY_PRICE;

class MyCursorAdapter extends CursorAdapter {
    @SuppressWarnings("deprecation")
    public MyCursorAdapter(Context context, Cursor c) {
        super(context, c); }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvName = (TextView) view.findViewById( R.id.tv_name );
        String name = cursor.getString( cursor.getColumnIndex( KEY_NAME ) );
        TextView tvPrice = (TextView) view.findViewById( R.id.tv_price );
        String price = cursor.getString( cursor.getColumnIndex( KEY_PRICE ) );

        Log.d("스트링 확인", name );
        Log.d("스트링 확인", price );
        tvName.setText( name );
        tvPrice.setText( price );
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from( context );
        View v = inflater.inflate( R.layout.list_item, parent, false );
        return v;
    }
}
