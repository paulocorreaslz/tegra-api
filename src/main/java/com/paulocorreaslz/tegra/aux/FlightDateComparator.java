package com.paulocorreaslz.tegra.aux;
/**
 * @author Paulo Correa <pauloyaco@gmail.com> - 2019
 *
 */
import java.util.Comparator;
import com.paulocorreaslz.tegra.model.Flight;

public class FlightDateComparator  implements Comparator<Flight> {
    public int compare(Flight flight, Flight otherFlight) {
        return flight.getDateStart().compareTo(otherFlight.getDateStart());
    }
}

