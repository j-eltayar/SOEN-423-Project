package killian.core;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Date")
public class Date implements Comparable<Date>, Serializable {

    @XmlElement(name = "year")
    private int year;

    @XmlElement(name = "month")
    private int month;

    @XmlElement(name = "day")
    private int day;

    public Date() {

    }

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @Override
    public int compareTo(Date date) {
        if (this.year == date.year && this.month == date.month && this.day == date.day) {
            return 0;
        } else if (this.year == date.year) {
            if (this.month == date.month) {
                if (this.day > date.day) {
                    return 1;
                } else {
                    return -1;
                }
            } else if (this.month > date.month) {
                return 1;
            } else {
                return -1;
            }
        } else if (this.year > date.year) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Date date = (Date) obj;
        return this.year == date.year && this.month == date.month && this.day == date.day;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month, day);
    }

    @Override
    public String toString() {
        return String.format("%02d-%02d-%04d", day, month, year);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
