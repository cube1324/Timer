package com.code.timer.Support;

public class UserInputElement extends ListElement {
    public UserInputElement(String name) {
        super(name, 0);
    }

    @Override
    public ListElement clone() {
        return new UserInputElement(name);
    }

    @Override
    public String getString() {
        return null;
    }
}
