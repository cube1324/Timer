package com.code.timer.Support;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.code.timer.Dialogs.LoopEditDialog;
import com.code.timer.Dialogs.TimerEditDialog;
import com.code.timer.R;

import java.util.LinkedList;

public class ListElementAdapter extends RecyclerView.Adapter<ListElementViewHolder> {
    public LinkedList<ListElement> elements;
    private FragmentManager mFragmentManager;
    public ListElementAdapter(LinkedList<ListElement> initialElements, FragmentManager manager){
        elements = initialElements;
        mFragmentManager = manager;
    }

    @Override
    public int getItemViewType(int position) {
        ListElement current = elements.get(position);
        if (current instanceof TimerElement){
            return 0;
        }
        if (current instanceof LoopStartElement){
            return 1;
        }
        if (current instanceof LoopEndElement){
            return 2;
        }
        if (current instanceof UserInputElement){
            return 3;
        }
        return -1;
    }

    @NonNull
    @Override
    public ListElementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        View v;
        switch (viewType){
            case 0:
                v = inf.inflate(R.layout.list_element_timer_view, parent, false);
                break;
            case 1:
                v = inf.inflate(R.layout.list_element_loopstart_view, parent, false);
                break;
            case 2:
                v = inf.inflate(R.layout.list_element_loopend_view, parent, false);
                break;
            case 3:
                //TODO
                v = inf.inflate(R.layout.list_element_loopend_view, parent, false);
                break;
            default:
                v = inf.inflate(R.layout.list_element_timer_view, parent, false);
                break;
        }
        return new ListElementViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListElementViewHolder holder, final int position) {
        final ListElement current = elements.get(position);

        if (current instanceof TimerElement) {
            holder.duration_view.setText(current.getString());
            holder.name_view.setText(current.getName());

            holder.edit_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimerEditDialog d = new TimerEditDialog(holder.getAdapterPosition(), holder);
                    d.show(mFragmentManager, "Edit Timer");
                }
            });


            holder.increase_button.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        current.incNumberBy(10000);
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                    return true;
                }
            });

            holder.decrease_button.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        current.incNumberBy(-10000);
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                    return true;
                }
            });
        } else if (current instanceof LoopStartElement) {
            holder.repeat_view.setText(current.getString());
            holder.name_view.setText(current.getName());

            holder.edit_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoopEditDialog d = new LoopEditDialog(holder.getAdapterPosition(), holder);
                    d.show(mFragmentManager, "Edit Loop");
                }
            });

        } else if (current instanceof LoopEndElement) {
            holder.name_view.setText(current.getName());
        } else if(current instanceof UserInputElement){
            //TODO
        }
    }


    @Override
    public int getItemCount() {
        return elements.size();
    }
}
