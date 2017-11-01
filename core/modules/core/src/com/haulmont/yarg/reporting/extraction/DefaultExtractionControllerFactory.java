package com.haulmont.yarg.reporting.extraction;

import com.haulmont.yarg.loaders.factory.ReportLoaderFactory;
import com.haulmont.yarg.reporting.extraction.controller.DefaultExtractionController;
import com.haulmont.yarg.structure.BandOrientation;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultExtractionControllerFactory implements ExtractionControllerFactory {
    protected ReportLoaderFactory loaderFactory;
    protected ExtractionController defaultExtractionController;
    protected Map<BandOrientation, ExtractionController> extractionControllerMap = new ConcurrentHashMap<>();

    public DefaultExtractionControllerFactory(ReportLoaderFactory loaderFactory) {
        this(loaderFactory, DefaultExtractionController::new);
    }

    public DefaultExtractionControllerFactory(ReportLoaderFactory loaderFactory,
                                              BiFunction<ExtractionControllerFactory, ReportLoaderFactory, ExtractionController> controllerCreator) {
        this.loaderFactory = loaderFactory;
        this.defaultExtractionController = controllerCreator.apply(this, loaderFactory);
    }

    @Override
    public void register(BandOrientation orientation, BiFunction<ExtractionControllerFactory, ReportLoaderFactory, ExtractionController> controllerCreator) {
        checkNotNull(orientation);
        checkNotNull(controllerCreator);

        extractionControllerMap.put(orientation, controllerCreator.apply(this, loaderFactory));
    }

    @Override
    public ExtractionController controllerBy(BandOrientation orientation) {
        return extractionControllerMap.getOrDefault(BandOrientation.NN(orientation), defaultExtractionController);
    }

    @Override
    public ExtractionController defaultController() {
        return defaultExtractionController;
    }

    @Override
    public Map<BandOrientation, ExtractionController> getExtractionControllers() {
        return Collections.unmodifiableMap(extractionControllerMap);
    }

    @Override
    public void setExtractionControllers(Map<BandOrientation, ExtractionController> extractionControllers) {
        checkNotNull(extractionControllers);

        extractionControllerMap = extractionControllers;
    }
}
