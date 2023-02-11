package com.application.mypetandroid.utils.other;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.application.mypetandroid.R;

import java.util.List;

public class SpinnerAdapter<T> extends ArrayAdapter<T> {

    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
    }

    @Override
    public boolean isEnabled(int position) {
        // Disable the first item from Spinner
        // First item will be use for hint
        return position != 0;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the item view
        View view = super.getDropDownView(
                position, convertView, parent);
        TextView textView = (TextView) view;
        if(position == 0){
            // Set the hint text color gray
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.disable_color));
        }
        else { textView.setTextColor(ContextCompat.getColor(getContext(), R.color.black)); }
        return view;
    }

}
