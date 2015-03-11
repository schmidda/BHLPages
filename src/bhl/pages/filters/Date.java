/*
 * This file is part of TILT.
 *
 *  TILT is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  TILT is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TILT.  If not, see <http://www.gnu.org/licenses/>.
 *  (c) copyright Biodiversity Heritage Library 2015 
 *  http://www.biodiversitylibrary.org/
 */

package bhl.pages.filters;
import java.util.HashSet;

/**
 * A Date entry in the Brew journal
 * @author desmond
 */
public class Date implements Block {
    static 
    {
        Date.months = new HashSet<String>();
        Date.months.add("january");
        Date.months.add("february");
        Date.months.add("march");
        Date.months.add("april");
        Date.months.add("may");
        Date.months.add("june");
        Date.months.add("july");
        Date.months.add("august");
        Date.months.add("september");
        Date.months.add("october");
        Date.months.add("november");
        Date.months.add("december");
        Date.locations = new HashSet<String>();
        Date.locations.add("concord");
        Date.locations.add("massachusetts");
        Date.days = new HashSet<String>();
        Date.days.add("monday");
        Date.days.add("tuesday");
        Date.days.add("wednesday");
        Date.days.add("thursday");
        Date.days.add("friday");
        Date.days.add("saturday");
        Date.days.add("sunday");
    }
    int year;
    String month;
    int day;
    int state;
    String dayName;
    String location;
    static HashSet<String> months;
    static HashSet<String> locations;
    static HashSet<String> days;
    Date( String text )
    {
        if ( Date.isYear(text) )
            this.year = Date.toYear(text);
        state = 1;
    }
    /**
     * Convert a text year into a numerical one
     * @param text a text year maybe with punctuation and spaces
     * @return the year value
     */
    static int toYear( String text )
    {
        String trimmed = text.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();
        return Integer.parseInt(trimmed);
    }
    /**
     * Is this line a year?
    */
    static boolean isYear( String text )
    {
        String trimmed = text.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();
        if ( text.length()==4 )
        {
            try
            {
                int year = Integer.parseInt(trimmed);
                if ( year > 1800 && year <1900 )
                    return true;
                else
                    return false;
            }
            catch ( NumberFormatException nfe )
            {
                return false;
            }
        }
        else
            return false;
    }
    static boolean isMonth( String text )
    {
        return months.contains( text.toLowerCase() );
    }
    static boolean isDay( String text )
    {
        try
        {
            int num = Integer.parseInt(text.trim());
            if ( num > 0 && num <= 31 )
                return true;
            else
                return false;
        }
        catch ( NumberFormatException nfe )
        {
            return false;
        }
    }
    boolean isDate( String text )
    {
        String[] parts = text.split(" ");
        if ( parts.length==2 )
        {
            if ( Date.isMonth(parts[0]) )
                this.month = parts[0];
            if ( Date.isDay(parts[1]) )
                this.day = Integer.parseInt(parts[1]);
            return true;
        }
        else
            return false;
    }
    boolean isLocation( String text )
    {
        String[] words = text.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
        for ( int i=0;i<words.length;i++ )
            if ( !locations.contains(words[i]) )
                return false;
        return true;
    }
    boolean isDayName( String text )
    {
        String trimmed = text.replaceAll("[^a-zA-Z ]", "").toLowerCase();
        if ( Date.days.contains(trimmed) )
            return true;
        else
            return false;
    }
    /**
     * Add a line to the date
     * @param line the line to add
     * @return true if the line fit into the date else false
     */
    public boolean addLine( String line )
    {
        switch ( state )
        {
            case 1: // look for date
                if ( isDate(line) )
                    state = 2;
                else
                    return false;
                    break;
            case 2: case 3:// look for location or dayName
                if ( isLocation(line) )
                {
                    this.location = line;
                    state = 4;
                }
                else if ( isDayName(line) )
                {
                    this.dayName = line;
                    state = 3;
                }
                else
                    return false;
                break;
        }
        return true;
    }
    public String toString()
    {
        StringBuilder sb =new StringBuilder();
        sb.append("<div class=\"date\">");
        if ( year !=0 )
        {
            sb.append( year );
            sb.append("<br>");
        }
        if ( month != null )
        {
            sb.append(month);
            if ( day != 0 )
            {
                sb.append(" ");
                sb.append(day);
            }
            sb.append("<br>");
        }
        if ( dayName != null )
        {
            sb.append(dayName);
            sb.append("<br>");
        }
        sb.append("</div>");
        if ( location !=null )
        {
            sb.append("<h4>");
            sb.append(location);
            sb.append("</h4>");
        }
        return sb.toString();
    }
    public static void main(String[]args)
    {
        Date d = new Date("1871");
        d.addLine("May 13");
        d.addLine("Sunday");
        d.addLine("Concord, Massachusetts");
        System.out.println(d.toString());
    }
}
