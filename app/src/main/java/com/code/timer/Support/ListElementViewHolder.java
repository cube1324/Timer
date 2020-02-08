package com.code.timer.Support;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.code.timer.R;


public class ListElementViewHolder extends RecyclerView.ViewHolder {
    public TextView name_view;
    public TextView duration_view;
    public TextView repeat_view;
    public ImageButton edit_button;
    public ImageButton increase_button;
    public ImageButton decrease_button;
    public ListElementViewHolder(View v){
        super(v);
        name_view = v.findViewById(R.id.name_view);
        duration_view = v.findViewById(R.id.duration_view);
        repeat_view = v.findViewById(R.id.repeat_view);
        edit_button = v.findViewById(R.id.edit_button);
        increase_button = v.findViewById(R.id.increase_button);
        decrease_button = v.findViewById(R.id.decrease_button);
    }
}
