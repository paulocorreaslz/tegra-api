package com.paulocorreaslz.tegra.util;

import java.util.Comparator;

import com.paulocorreaslz.tegra.model.Flight;

public class FlightComparator  implements Comparator<Flight> {
    public int compare(Flight flight, Flight otherFlight) {
        return flight.getDateStart().compareTo(otherFlight.getDateStart());
    }
}
