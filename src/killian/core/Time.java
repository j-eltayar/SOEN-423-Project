package killian.core;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Time")
public class Time implements Comparable<Time>, Serializable {

    @XmlElement(name = "hour")
    private int hour;

    @XmlElement(name = "minutes")
    private int minutes;

    public Time() {

    }

    public Time(int hour, int minutes) {
        this.hour = hour;
        this.minutes = minutes;
    }

    @Override
    public int compareTo(Time time) {
        if (this.hour == time.hour && this.minutes == time.minutes) {
            return 0;
        } else if (this.hour == time.hour) {
            if (this.minutes > time.minutes) {
                return 1;
            } else {
                return -1;
            }
        } else if (this.hour > time.hour) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Time time = (Time) obj;
        return this.hour == time.hour && this.minutes == time.minutes;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int hashCode() {
        return Objects.hash(hour, minutes);
    }

    @Override
    public String toString() {
        return hour + ":" + minutes;
    }
}