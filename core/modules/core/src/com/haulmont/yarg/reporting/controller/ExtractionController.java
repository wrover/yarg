package com.haulmont.yarg.reporting.controller;

import com.haulmont.yarg.loaders.factory.ReportLoaderFactory;
import com.haulmont.yarg.structure.BandData;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface ExtractionController {
    List<BandData> extract(ReportLoaderFactory loaderFactory, ExtractionContext context);

    default List<Map<String, Object>> extractData(ReportLoaderFactory loaderFactory, ExtractionContext context) {
        return Collections.emptyList();
    }
}
