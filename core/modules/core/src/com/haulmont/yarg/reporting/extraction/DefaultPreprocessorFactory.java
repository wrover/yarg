package com.haulmont.yarg.reporting.extraction;

import com.haulmont.yarg.loaders.QueryLoaderPreprocessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultPreprocessorFactory implements PreprocessorFactory {
    protected QueryLoaderPreprocessor defaultPreprocessor;
    protected Map<String, QueryLoaderPreprocessor> preprocessorMap = new ConcurrentHashMap<>();

    public DefaultPreprocessorFactory() {
        this((query, params, consumer)-> consumer.apply(query, params));
    }

    public DefaultPreprocessorFactory(QueryLoaderPreprocessor defaultPreprocessor) {
        this.defaultPreprocessor = defaultPreprocessor;
    }

    @Override
    public void register(String loaderType, QueryLoaderPreprocessor preprocessor) {
        preprocessorMap.put(loaderType, preprocessor);
    }

    @Override
    public QueryLoaderPreprocessor processorBy(String loaderType) {
        return preprocessorMap.getOrDefault(loaderType, defaultPreprocessor);
    }
}
