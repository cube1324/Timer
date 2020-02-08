package com.code.timer.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;


import com.code.timer.R;
import com.code.timer.Support.ListElementViewHolder;

public class TimerEditDialog extends AppCompatDialogFragment {
    private NumberPicker minute_picker;
    private NumberPicker seconds_picker;
    private EditText edit_name;
    private int minutes;
    private int seconds;
    private int pos;
    private ListElementViewHolder holder;
    private TimerEditListener listener;


    public interface TimerEditListener {
        void onTimerEdit(int pos, String name, int seconds, int hour);
    }

    public TimerEditDialog(int pos, ListElementViewHolder holder){
        super();
        this.pos = pos;
        this.holder = holder;
    }

    public void onAttach(Context context){
        super.onAttach(context);
        try {
            listener = (TimerEditListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " no TimerEditListener");
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.edit_timer_dialog, null);
        minute_picker = v.findViewById(R.id.hour_picker);
        seconds_picker = v.findViewById(R.id.seconds_picker);
        edit_name = v.findViewById(R.id.edit_name);

        edit_name.setText(holder.name_view.getText());


        String value = holder.duration_view.getText().toString();


        minute_picker.setMaxValue(99);
        minute_picker.setMinValue(0);
        minute_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                minutes = newVal;
            }
        });
        minute_picker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        });
        minutes = Integer.valueOf(value.substring(0, 2));
        minute_picker.setValue(minutes);

        seconds_picker.setMaxValue(59);
        seconds_picker.setMinValue(0);
        seconds_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                seconds = newVal;
            }
        });
        seconds_picker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        });
        seconds = Integer.valueOf(value.substring(5,7));
        seconds_picker.setValue(seconds);

        builder.setView(v);
        builder.setNegativeButton(R.string.edit_dialog_negativ, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //empty
            }
        });
        builder.setPositiveButton(R.string.edit_dialog_positiv, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onTimerEdit(pos, edit_name.getText().toString(), minutes, seconds);
            }
        });
        return builder.create();
    }
}
