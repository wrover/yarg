package com.haulmont.yarg.reporting.controller;

import com.haulmont.yarg.loaders.factory.ReportLoaderFactory;

import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface ExtractionController {
    List<Map<String, Object>> extract(ReportLoaderFactory loaderFactory, ExtractionContext context);
}
