package com.code.timer.Support;

public class LoopStartElement extends ListElement {
    public LoopStartElement(String name, long number) {
        super(name, number);
    }

    public LoopStartElement(String name, long number, ListElement related) {
        super(name, number);
        this.relatedElement = related;
    }

    @Override
    public String getString() {
       return Long.toString(number);
    }

    @Override
    public ListElement clone() {
        return new LoopStartElement(name, number, relatedElement);
    }
}
