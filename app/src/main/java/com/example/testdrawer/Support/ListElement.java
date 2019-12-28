package com.example.testdrawer.Support;

import java.io.Serializable;

public abstract class ListElement implements Serializable {
    protected String name;
    protected long number;
    protected int depth = 1;
    protected ListElement relatedElement = null;
    private String currentLoopName;
    private String currentLoopNum;

    public ListElement(String name, long number, int depth, ListElement relatedElement, String currentLoopName){
        this.name = name;
        this.number = number;
        this.depth = depth;
        this.relatedElement = relatedElement;
    }

    public ListElement(String name, long number){
        this.name = name;
        this.number = number;
    }
    public void setRelatedElement(ListElement el){
        relatedElement = el;
    }

    public ListElement getRelatedElement() {
        return relatedElement;
    }

    public void incNumberBy(long x){
        number += x;
    }

    public void incDepthBy(int x){
        depth += x;
    }

    public int getDepth(){
        return depth;
    }

    public void setNumber(Long number){
        this.number = number;
    }

    public long getNumber(){return number;}

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setCurrentLoopName(String currentLoopName) {
        this.currentLoopName = currentLoopName;
    }

    public String getCurrentLoopName() {
        return currentLoopName;
    }

    public void setCurrentLoopNum(String currentLoopNum) {
        this.currentLoopNum = currentLoopNum;
    }

    public String getCurrentLoopNum() {
        return currentLoopNum;
    }

    public abstract String getString();

    public abstract ListElement clone();
}
