package com.haulmont.yarg.reporting.controller;

import com.haulmont.yarg.loaders.factory.ReportLoaderFactory;
import com.haulmont.yarg.reporting.OrientationExtractionRegistry;
import com.haulmont.yarg.structure.BandOrientation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrossTabExtractionController extends DefaultExtractionController {
    private static final String VERTICAL_BAND = "vertical";
    private static final String HORIZONTAL_BAND = "horizontal";

    static {
        OrientationExtractionRegistry.register(BandOrientation.CROSS, new CrossTabExtractionController());
    }

    @Override
    protected List<Map<String, Object>> getQueriesResult(ReportLoaderFactory loaderFactory, ExtractionContext context) {
        Map<String, Object> crossTabParams = new HashMap<>(context.getParams());
        getQueries(context).stream()
                .filter(e-> e.getName().endsWith(VERTICAL_BAND) || e.getName().endsWith(HORIZONTAL_BAND))
                .forEach(q-> {
                    crossTabParams.put(q.getName(), getQueryData(loaderFactory, context, q));
                });
        return getQueriesResult(getQueries(context).stream()
                .filter(e-> !e.getName().endsWith(VERTICAL_BAND) && !e.getName().endsWith(HORIZONTAL_BAND))
                .iterator(), loaderFactory, context.withParams(crossTabParams));
    }
}
