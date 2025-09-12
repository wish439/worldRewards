package org.wishtoday.egar.worldRewards.Fly;

public class Counter {
    private int currentSec = 0;
    private int anticipationSec = 60 * 60;
    private boolean check() {
        return currentSec >= anticipationSec;
    }
    public void setAnticipationSec(int sec) {
        anticipationSec = sec;
    }
    public boolean checkAndAdd() {
        boolean check = check();
        if (check) {
            this.resetCurrentSec();
            return true;
        }
        this.currentSec++;
        return false;
    }
    public void resetCurrentSec() {
        this.currentSec = 0;
    }
    public int getCurrentSec() {
        return this.currentSec;
    }
    public void setCurrentSec(int sec) {
        this.currentSec = sec;
    }
    public int getAnticipationSec() {
        return this.anticipationSec;
    }
    public String serialization() {
        return currentSec + ":" + anticipationSec;
    }
    public static Counter deSerialization(String serialized) {
        Counter counter = new Counter();
        String[] split = serialized.split(":");
        counter.setAnticipationSec(Integer.parseInt(split[1]));
        counter.setCurrentSec(Integer.parseInt(split[0]));
        return counter;
    }

    @Override
    public String toString() {
        return "Counter{" +
                "currentSec=" + currentSec +
                ", anticipationSec=" + anticipationSec +
                '}';
    }
}
