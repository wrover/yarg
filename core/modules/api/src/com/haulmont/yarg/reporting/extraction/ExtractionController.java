package com.haulmont.yarg.reporting.extraction;

import com.haulmont.yarg.structure.BandData;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface ExtractionController {

    List<BandData> extract(ExtractionContext context);

    default List<Map<String, Object>> extractData(ExtractionContext context) {
        return Collections.emptyList();
    }

}
