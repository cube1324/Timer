package com.code.timer.Support;

public class TimerElement extends ListElement {
    private boolean makeSound = true;

    public TimerElement(String name, long number) {
        super(name, number);
    }

    public TimerElement(String name, long number, boolean makeSound) {
        super(name, number);
        this.makeSound = makeSound;
    }

    @Override
    public ListElement clone() {
        return new TimerElement(name, number, makeSound);
    }

    @Override
    public String getString() {
        int min = (int) number / 1000 / 60;
        int sec = (int) number / 1000 % 60;
        return  String.format("%02d : %02d", min, sec);
    }

    public void setMakeSound(boolean makeSound){
        this.makeSound = makeSound;
    }

    public boolean getMakeSound(){
        return this.makeSound;
    }
}
