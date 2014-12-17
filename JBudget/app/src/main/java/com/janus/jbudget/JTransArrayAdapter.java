package com.janus.jbudget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by Janus on 2014-12-16.
 * For JBudget Android App
 */
public class JTransArrayAdapter extends ArrayAdapter {
    private final Context context;
    private final List<JTransaction> values;

    public JTransArrayAdapter(Context context, List<JTransaction> values) {
        super(context, R.layout.trans_list_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.trans_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.category);
        textView.setText(values.get(position).category);

        textView = (TextView) rowView.findViewById(R.id.description);
        textView.setText(values.get(position).description);

        textView = (TextView) rowView.findViewById(R.id.amount);
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String amountString = formatter.format(values.get(position).amount);
        textView.setText(amountString);

        return rowView;
    }
} 

