package ru.practicum.shareit.booking.model;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State from(String bookingState) {
        for (State value : State.values()) {
            if (value.name().equalsIgnoreCase(bookingState)) {
                return value;
            }
        }
        return null;
    }
}
