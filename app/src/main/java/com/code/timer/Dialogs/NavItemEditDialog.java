package com.code.timer.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.code.timer.R;

public class NavItemEditDialog extends AppCompatDialogFragment {
    private int pos;
    private String navitem_titel;
    private NavItemEditListener listener;

    public interface NavItemEditListener{
        void onNavItemEdit(int pos, String name);
        void onNavItemDelete(int pos);
    }

    public NavItemEditDialog(int pos, String name){
        super();
        this.pos = pos;
        this.navitem_titel = name;
    }

    public void onAttach(Context context){
        super.onAttach(context);
        try {
            listener = (NavItemEditListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " no NavItemEditListener Interface implemented");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.edit_nav_item_dialog, null);

        final EditText edit_name = v.findViewById(R.id.edit_navitem_name);
        edit_name.setText(navitem_titel);

        builder.setView(v);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //empty
            }
        });

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onNavItemEdit(pos,edit_name.getText().toString());
            }
        });

        return builder.create();
    }
}
