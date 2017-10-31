package com.haulmont.yarg.structure;

public interface ProxyWrapper {
    Object unwrap();

    static<T> T unwrap(T obj) {
        return obj instanceof ProxyWrapper ? (T)((ProxyWrapper)obj).unwrap() : obj;
    }
}
