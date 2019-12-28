package com.example.testdrawer.Support;

public class LoopEndElement extends ListElement {
    public LoopEndElement(String name) {
        super(name, 0);
    }

    public LoopEndElement(String name, ListElement related){
        super(name, 0);
        relatedElement = related;
    }

    @Override
    public ListElement clone() {
        return new LoopEndElement(name, relatedElement);
    }

    @Override
    public String getString() {
        return null;
    }
}
