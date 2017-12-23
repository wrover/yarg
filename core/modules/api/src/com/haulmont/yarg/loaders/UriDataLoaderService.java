package com.haulmont.yarg.loaders;

import com.haulmont.yarg.exception.DataLoadingException;

/**
 * Interface to be implemented by client to provide a way to load data from external API.
 * It's highly recommended to make it cacheable.
 */
public interface UriDataLoaderService {
    String loadFromUri(String uri) throws DataLoadingException;
}
