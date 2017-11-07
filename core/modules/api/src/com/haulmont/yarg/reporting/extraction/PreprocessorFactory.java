package com.haulmont.yarg.reporting.extraction;

import com.haulmont.yarg.loaders.QueryLoaderPreprocessor;

/**
 * This interface implementation may holding relation between name of data loader type (ex: sql)
 * and custom params preprocessor
 * if relation not set implementation should present default params processor
 * ex: {@code (query, params, consumer)-> consumer.apply(query, params) }
 *
 * The default implementation is com.haulmont.yarg.reporting.extraction.DefaultPreprocessorFactory
 */
public interface PreprocessorFactory {
    /**
     * Method for runtime configuring params preprocessing by loader type
     *
     * @param loaderType
     * @param preprocessor
     */
    void register(String loaderType, QueryLoaderPreprocessor preprocessor);

    QueryLoaderPreprocessor processorBy(String loaderType);
}
