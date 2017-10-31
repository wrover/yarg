package com.haulmont.yarg.reporting.extraction;

import com.haulmont.yarg.loaders.QueryLoaderPreprocessor;

public interface PreprocessorFactory {
    void register(String loaderType, QueryLoaderPreprocessor preprocessor);

    QueryLoaderPreprocessor processorBy(String loaderType);
}
