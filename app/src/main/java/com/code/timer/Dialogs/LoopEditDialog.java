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

/**
 * Dialog which is used to edit the LoopStartElements in the recyclerView
 */
public class LoopEditDialog extends AppCompatDialogFragment {
    private EditText edit_name;
    private NumberPicker number_picker;
    private ListElementViewHolder holder;
    private int number;
    private int pos;
    private LoopEditListener listener;

    /**
     *
     */
    public interface LoopEditListener {
        void onLoopEdit(int pos, String name, int number);
    }

    /**
     * Constructor for the Dialog
     * @param pos of the element that is being edited
     * @param holder The View holder of the current ListElement
     */
    public LoopEditDialog(int pos, ListElementViewHolder holder){
        super();
        this.pos = pos;
        this.holder = holder;
    }

    public void onAttach(Context context){
        super.onAttach(context);
        try {
            listener = (LoopEditListener)context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " no LoopEditListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.edit_loop_dialog, null);
        edit_name = v.findViewById(R.id.edit_name);
        edit_name.setText(holder.name_view.getText());

        number_picker = v.findViewById(R.id.number_picker);
        number_picker.setMaxValue(100);
        number_picker.setMinValue(2);
        number_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                number = newVal;
            }
        });
        number = Integer.valueOf(holder.repeat_view.getText().toString());
        number_picker.setValue(number);


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
                listener.onLoopEdit(pos, edit_name.getText().toString(), number);
            }
        });
        return builder.create();
    }
}
