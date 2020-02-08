package com.code.timer.Support;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Parse {
    public static LinkedList<ListElement> parseList(LinkedList<ListElement> elements){
        LinkedList<ListElement> result = new LinkedList<>();

        //Connects ListElement with the loop iterations left at index 0 and the total loop iterations at index 1
        Map<ListElement, int[]> map = new HashMap<>();

        ListElement current;
        String currentLoop = "None";
        int i = 0;
        ListElement currentLoopELement = null;


        while (i < elements.size()){
            current = elements.get(i);
            if (current instanceof LoopStartElement){
                //Add element to the map
                if(!map.containsKey(current)){
                    int[] temp = {(int)current.getNumber(), (int)current.getNumber()};
                    map.put(current, temp);
                }

                //Decrease the loop iterations by one
                map.get(current)[0] -= 1;

                //Set currentLoop variables for later
                currentLoop = current.getName();
                currentLoopELement = current;

                i++;

            } else if (current instanceof LoopEndElement){
                //if loops has repeats left jump to the start of the loop
                if (map.get(current.getRelatedElement())[0] > 0) {
                    i = elements.indexOf(current.getRelatedElement());
                } else {
                    //else just continue
                    currentLoop = "None";
                    currentLoopELement = null;
                    i++;
                }
            } else{
                //Clone element to change attributes individually
                current = current.clone();

                //Set loop name
                current.setCurrentLoopName(currentLoop);

                String temp = "";
                //If within a loop set loop iterations
                if (currentLoopELement != null){
                    int[] num = map.get(currentLoopELement);
                    temp = (num[1] - num[0]) + " / " + num[1];
                }
                current.setCurrentLoopNum(temp);

                //Add Timer to results
                result.add(current);
                i++;
            }
        }

        return result;
    }
}
