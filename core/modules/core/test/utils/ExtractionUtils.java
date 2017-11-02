package utils;

import com.haulmont.yarg.reporting.DataExtractor;

public class ExtractionUtils {
    private ExtractionUtils() {}

    public static DataExtractor emptyExtractor() {
        return (report, data, params)-> {};
    }
}
