package github.abnvanand.washeteria.ui.assistant;

import java.util.Date;

public class Interval {
    long start;
    long end;

    public Interval(long start, long end) {
        this.start = start;
        this.end = end;
    }


    @Override
    public String toString() {
        return "Interval{" +
                "start=" + new Date(start) +
                ", end=" + new Date(end) +
                '}';
    }
}
