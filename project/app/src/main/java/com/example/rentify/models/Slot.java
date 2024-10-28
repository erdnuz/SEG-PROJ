package com.example.rentify.models;

import java.util.ArrayList;
import java.util.List;

public class Slot {

    private long start;
    private long end;

    public Slot() {
        start = 0;
        end = Long.MAX_VALUE;
    }
    public Slot(long start, long end) {

        this.start = start;
        this.end = end;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public boolean contains(Slot other) {
        return other.getStart() >= this.start && other.getEnd() <= this.end;
    }

    public List<Slot> split(Slot other) {
        List<Slot> slots = new ArrayList<>();

        if (this.contains(other)) {
            // Slot before `other`
            if (other.getStart() > this.start) {
                slots.add(new Slot(this.start, other.getStart() - 1));
            }

            // Slot `other` itself
            slots.add(other);

            // Slot after `other`
            if (other.getEnd() < this.end) {
                slots.add(new Slot(other.getEnd() + 1, this.end));
            }
        }

        return slots;
    }
}
