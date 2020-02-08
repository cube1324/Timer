package com.code.timer.Support;

public class TimerElement extends ListElement {

    public TimerElement(String name, long number) {
        super(name, number);
    }

    @Override
    public ListElement clone() {
        return new TimerElement(name, number);
    }

    @Override
    public String getString() {
        int min = (int) number / 1000 / 60;
        int sec = (int) number / 1000 % 60;
        return  String.format("%02d : %02d", min, sec);
    }
}
