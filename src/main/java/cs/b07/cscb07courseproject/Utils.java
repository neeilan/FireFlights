package cs.b07.cscb07courseproject;

import org.threeten.bp.Duration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Neeilan on 11/27/2016.
 */
public class Utils {
    public static final Duration MIN_LAYOVER = Duration.ofMinutes(120);
    public static final Duration MAX_LAYOVER = Duration.ofHours(4);
    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static DateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public static String getDbKeyFromEmail(String email){
        return email.trim().toLowerCase().replaceAll("\\.","DOT")
                .replaceAll("-","DASH")
                .replaceAll("_","US");
    }

    public static String getEmailFromDbKey(String key){
        return key.trim().replaceAll("DOT",".")
                .replaceAll("DASH","-")
                .replaceAll("US","_").toLowerCase();
    }

    /**
     * Created by Neeilan on 11/27/2016.
     * Because the Firebase Android client is asynchronous, we will be passing in callbacks
     * to Date Stores rather than wait for the function to return one or more Users/Flights etc.
     */
    public static interface Callback<T> {
        void call(T argument);
    }
}
