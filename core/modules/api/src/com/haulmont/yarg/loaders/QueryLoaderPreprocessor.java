package com.haulmont.yarg.loaders;

import com.haulmont.yarg.structure.ReportQuery;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@FunctionalInterface
public interface QueryLoaderPreprocessor {
     List<Map<String, Object>> preprocess(ReportQuery query, Map<String, Object> params,
                                          BiFunction<ReportQuery, Map<String, Object>, List<Map<String, Object>>> consumer);
}
