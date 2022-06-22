package team9.LoadBalancerThread.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Reservation {

    private String name;
    private String dateOfBirth;
    private String phoneNumber;
    private String seatNumber;


    public Reservation(){


    }
    public Reservation(String name, String dateOfBirth, String phoneNumber, String seatNumber) {
        super();
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.seatNumber = seatNumber;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "name='" + name + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", seatNumber='" + seatNumber + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getSeatNumber() {
        return seatNumber;
    }
}

