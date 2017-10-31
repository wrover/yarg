package com.haulmont.yarg.reporting.extraction;

import com.haulmont.yarg.loaders.factory.ReportLoaderFactory;
import com.haulmont.yarg.structure.BandOrientation;

import java.util.function.BiFunction;

public interface ExtractionControllerFactory {
    void register(BandOrientation orientation, BiFunction<ExtractionControllerFactory, ReportLoaderFactory, ExtractionController> controllerCreator);

    ExtractionController controllerBy(BandOrientation orientation);

    ExtractionController defaultController();
}
