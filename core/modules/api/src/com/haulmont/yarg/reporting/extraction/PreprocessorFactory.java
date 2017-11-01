package com.haulmont.yarg.reporting.extraction;

import com.haulmont.yarg.loaders.QueryLoaderPreprocessor;

import java.util.Map;

public interface PreprocessorFactory {
    void register(String loaderType, QueryLoaderPreprocessor preprocessor);

    QueryLoaderPreprocessor processorBy(String loaderType);

    void setPreprocessors(Map<String, QueryLoaderPreprocessor> preprocessors);

    Map<String, QueryLoaderPreprocessor>  getPreprocessors();
}
