package com.code.timer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.code.timer.DataBase.mDBHelper;
import com.code.timer.Support.ListElement;
import com.code.timer.Support.ListElementAdapter;
import com.code.timer.Support.LoopEndElement;
import com.code.timer.Support.LoopStartElement;
import com.code.timer.Support.Parse;
import com.code.timer.Support.TimerElement;
import com.code.timer.Support.UserInputElement;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;


public class TimerFragment extends Fragment {
    private static final String ARG_ID = "ID";
    private static final String ARG_DB = "DB";
    private static final int INDENT_INCREMENT = 2;
    private String FILENAME = "data";
    public boolean save = true;

    private String ID;
    private mDBHelper dbHelper;
    private LinkedList<ListElement> elements = new LinkedList<>();
    private ListElementAdapter adapter;

    private ListElement deletedElement1 = null;
    private ListElement deletedElement2 = null;
    private int deletedElementPos1;
    private int deletedElementPos2;
    private int[] deletedRange = new int[2];

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param ID ID of the Timer.
     * @return A new instance of fragment TimerFragment.
     */
    public static TimerFragment newInstance(String ID, mDBHelper dbHelper) {
        TimerFragment fragment = new TimerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, ID);
        args.putSerializable(ARG_DB, dbHelper);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ID = getArguments().getString(ARG_ID);
            dbHelper = (mDBHelper) getArguments().getSerializable(ARG_DB);
        }
        FILENAME += ID;

        try {
            FileInputStream fis = getContext().openFileInput(FILENAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            boolean cond = true;
            while (cond){
                ListElement obj = (ListElement) is.readObject();
                if (obj != null){
                    elements.add(obj);
                }else {
                    cond = false;
                }
            }
            is.close();
            fis.close();
        } catch (Exception e) {
            ListElement el1 = new TimerElement("Plank", 60000);
            el1.incDepthBy(INDENT_INCREMENT);
            ListElement el2 = new TimerElement("Side Plank", 60000);
            el2.incDepthBy(INDENT_INCREMENT);
            ListElement el3 = new TimerElement("Hollow Body Hold", 60000);
            el3.incDepthBy(INDENT_INCREMENT);
            ListElement el4 = new TimerElement("Rest", 120000);
            el4.incDepthBy(INDENT_INCREMENT);


            ListElement end = new LoopEndElement("Core");
            ListElement start = new LoopStartElement("Core", 3);

            start.setRelatedElement(end);
            end.setRelatedElement(start);

            elements.add(start);
            elements.add(el1);
            elements.add(el2);
            elements.add(el3);
            elements.add(el4);
            elements.add(end);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timer, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ListElementAdapter(elements, getChildFragmentManager());
        recyclerView.setAdapter(adapter);

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN | ItemTouchHelper.UP,ItemTouchHelper.LEFT){
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                //Element that is moved
                int pos_dragged = viewHolder.getAdapterPosition();
                //Element that gets switched
                int pos_target = target.getAdapterPosition();

                //If moved Element ist a  LoopStartElement
                if (elements.get(pos_dragged) instanceof  LoopStartElement) {
                    //MAKE STARTLOOPS NOT FUCK THEM SELF
                    if (elements.get(pos_dragged).getRelatedElement() == elements.get(pos_target)) {
                        //If its at the Bottom
                        if (elements.size() - 1 == pos_target) {
                            return true;
                        }
                        //Check indent
                        if (elements.get(pos_target + 1) instanceof  LoopStartElement) {
                            elements.get(pos_target).incDepthBy(INDENT_INCREMENT);
                        }
                        if (elements.get(pos_target + 1) instanceof  LoopEndElement) {
                            elements.get(pos_target).incDepthBy(-INDENT_INCREMENT);
                        }


                        //Don't let the loop fuck up
                        Collections.swap(elements, pos_target, pos_target + 1);
                        adapter.notifyItemMoved(pos_target, pos_target + 1);
                        //Fix indent of Elements
                    } else {
                        if (pos_dragged < pos_target) {
                            elements.get(pos_target).incDepthBy(-INDENT_INCREMENT);
                        } else {
                            elements.get(pos_target).incDepthBy(INDENT_INCREMENT);
                        }
                    }
                }

                //If Element is  LoopEndElement
                if (elements.get(pos_dragged) instanceof  LoopEndElement) {
                    //Make Loop not fuck itself
                    if (elements.get(pos_dragged).getRelatedElement() == elements.get(pos_target)) {
                        if (pos_target == 0) {
                            return true;
                        }
                        //Check indent
                        if (elements.get(pos_target - 1) instanceof  LoopStartElement) {
                            elements.get(pos_target).incDepthBy(-INDENT_INCREMENT);
                        }
                        if (elements.get(pos_target - 1) instanceof  LoopEndElement) {
                            elements.get(pos_target).incDepthBy(INDENT_INCREMENT);
                        }

                        //Push  LoopStartElement up
                        Collections.swap(elements, pos_target, pos_target - 1);
                        adapter.notifyItemMoved(pos_target, pos_target - 1);

                        //Fix Element indent
                    } else {
                        if (pos_dragged < pos_target) {
                            elements.get(pos_target).incDepthBy(INDENT_INCREMENT);
                        } else {
                            elements.get(pos_target).incDepthBy(-INDENT_INCREMENT);
                        }
                    }
                }

                //TODO Fix nested loops, rn can fuck themself
                /*
                if (elements.get(pos_dragged) instanceof  LoopEndElement | elements.get(pos_dragged) instanceof  LoopStartElement){
                    if (elements.get(pos_target) instanceof  LoopEndElement | elements.get(pos_target) instanceof  LoopStartElement) {
                        int pos_dragged_relative = elements.indexOf(elements.get(pos_dragged).getRelatedElement());
                        int pos_target_relative = elements.indexOf(elements.get(pos_target).getRelatedElement());

                        if (pos_dragged < pos_target){
                            if (pos_dragged_relative < pos_target_relative){
                                Collections.swap(elements, pos_dragged_relative, pos_target_relative);
                                adapter.notifyItemMoved(pos_dragged_relative, pos_target_relative);
                            }
                        }else {

                        }
                    }
                }

                 */
                //Fix Element indent
                if (elements.get(pos_target) instanceof  LoopStartElement) {
                    if (pos_target > pos_dragged) {
                        elements.get(pos_dragged).incDepthBy(INDENT_INCREMENT);
                    } else {
                        elements.get(pos_dragged).incDepthBy(-INDENT_INCREMENT);
                    }
                }

                if (elements.get(pos_target) instanceof  LoopEndElement) {
                    if (pos_target > pos_dragged) {
                        elements.get(pos_dragged).incDepthBy(-INDENT_INCREMENT);
                    } else {
                        elements.get(pos_dragged).incDepthBy(INDENT_INCREMENT);
                    }
                }

                //Swap Items
                Collections.swap(elements, pos_dragged, pos_target);
                adapter.notifyItemMoved(pos_dragged, pos_target);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //pos of deleted Element
                int pos = viewHolder.getAdapterPosition();

                if (elements.get(pos) instanceof  LoopStartElement) {
                    //Get  LoopEndElement
                    int related_pos = elements.indexOf(elements.get(pos).getRelatedElement());

                    //set indent of Loop element one back
                    for (int i = pos + 1; i < related_pos; i++) {
                        elements.get(i).incDepthBy(-INDENT_INCREMENT);
                    }

                    //delete both elements
                    deletedElement2 = elements.remove(related_pos);
                    adapter.notifyItemRemoved(related_pos);
                    deletedElementPos2 = related_pos;

                    deletedElement1 = elements.remove(pos);
                    adapter.notifyItemRemoved(pos);
                    deletedElementPos1 = pos;

                }else if (elements.get(pos) instanceof  LoopEndElement){
                    int related_pos = elements.indexOf(elements.get(pos).getRelatedElement());
                    for (int i = related_pos + 1; i < pos; i++) {
                        elements.get(i).incDepthBy(-INDENT_INCREMENT);
                    }
                    //delete both elements
                    deletedElement2 = elements.remove(pos);
                    adapter.notifyItemRemoved(pos);
                    deletedElementPos2 = pos;

                    deletedElement1 = elements.remove(related_pos);
                    adapter.notifyItemRemoved(related_pos);
                    deletedElementPos1 = related_pos;
                }else{
                    deletedElement1 = elements.remove(pos);
                    adapter.notifyItemRemoved(pos);
                    deletedElementPos1 = pos;
                    deletedElement2 = null;
                }

                Snackbar snackbar = Snackbar.make(getView(), R.string.delete_snackbar_text, Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.delete_snackbar_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        elements.add(deletedElementPos1, deletedElement1);
                        adapter.notifyItemInserted(deletedElementPos1);
                        if (deletedElement2 != null){
                            elements.add(deletedElementPos2, deletedElement2);
                            adapter.notifyItemInserted(deletedElementPos2);
                            for (int i = deletedElementPos1 + 1; i < deletedElementPos2; i++){
                                elements.get(i).incDepthBy(INDENT_INCREMENT);
                            }
                        }
                    }
                });
                snackbar.show();
            }
        });
        helper.attachToRecyclerView(recyclerView);


        RecyclerView.ItemDecoration margin = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = 20;
                if (parent.getChildAdapterPosition(view) != -1) {
                    outRect.left = 20 * elements.get(parent.getChildAdapterPosition(view)).getDepth();
                }
                outRect.right = 20;
                if (parent.getChildAdapterPosition(view) == 0){
                    outRect.top = 20;
                }
            }
        };

        recyclerView.addItemDecoration(margin);
        return v;
    }

    public void onTimerEdit(int pos, String name, int minutes, int seconds, boolean mkSound) {
        TimerElement element = (TimerElement) elements.get(pos);
        element.setName(name);
        long milliseconds = TimeUnit.MILLISECONDS.convert(seconds, TimeUnit.SECONDS) + TimeUnit.MILLISECONDS.convert(minutes, TimeUnit.MINUTES);
        element.setNumber(milliseconds);
        element.setMakeSound(mkSound);
        adapter.notifyItemChanged(pos);
    }

    public void onLoopEdit(int pos, String name, int number) {
        ListElement element = elements.get(pos);
        element.setName(name);
        element.setNumber((long) number);

        adapter.notifyItemChanged(pos);

        element.getRelatedElement().setName(name);
        adapter.notifyItemChanged(elements.indexOf(element.getRelatedElement()));
    }

    public void addTimer(){
        elements.add(new TimerElement("New Timer", 60000));
        adapter.notifyItemInserted(elements.size()-1);
    }

    public void addLoop(){
       ListElement start = new LoopStartElement("New Loop", 4);
       ListElement end = new LoopEndElement("New Loop");
       start.setRelatedElement(end);
       end.setRelatedElement(start);

       elements.add(start);
       elements.add(end);

       adapter.notifyItemRangeInserted(elements.size()-2, 2);
    }

    public void addStop(){
        elements.add(new UserInputElement("New Stop"));
        adapter.notifyItemInserted(elements.size()-1);
    }

    public void startTimer(){
        Intent intent = new Intent(getContext().getApplicationContext(), TimerActivity.class);


        LinkedList<ListElement> temp = new LinkedList<>();

        temp = Parse.parseList(elements);
        if (temp.size() > 0) {
            intent.putExtra("data", temp);
            startActivity(intent);
        }else{
            View view = getView();
            Snackbar.make(view, R.string.no_element_snackbar, Snackbar.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (save) {
            try {
                FileOutputStream fos = getContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                for (ListElement el : elements) {
                    os.writeObject(el);
                }
                //Write Null as End Flag
                os.writeObject(null);
                os.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
