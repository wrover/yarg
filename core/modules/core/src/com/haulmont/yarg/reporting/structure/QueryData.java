package com.haulmont.yarg.reporting.structure;

import com.haulmont.yarg.structure.ReportQuery;

import java.util.Map;

public class QueryData {

    ReportQuery query;
    Map<String, Object> data;

    public QueryData(ReportQuery query, Map<String, Object> data) {
        this.query = query;
        this.data = data;
    }

    public ReportQuery getQuery() {
        return query;
    }

    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueryData queryData = (QueryData) o;

        return query != null && query.getName().equals(queryData.query.getName());
    }

    @Override
    public int hashCode() {
        return query != null ? query.getName().hashCode() : 0;
    }
}
