package com.haulmont.yarg.reporting.extraction;

import com.haulmont.yarg.loaders.factory.ReportLoaderFactory;
import com.haulmont.yarg.structure.BandOrientation;

import java.util.function.BiFunction;

/**
 * This interface implementation may holding relation between report band orientation and related controller logic
 * if relation not set, default controller should be returned
 * The default controller implementation is com.haulmont.yarg.reporting.extraction.controller.DefaultExtractionController
 *
 * The default implementation is com.haulmont.yarg.reporting.extraction.DefaultExtractionControllerFactory
 */
public interface ExtractionControllerFactory {
    /**
     * Method for runtime configuring data extraction logic by orientation
     *
     * @param orientation
     * @param controllerCreator - custom logic implementation ex: com.haulmont.yarg.reporting.extraction.controller.CrossTabExtractionController
     */
    void register(BandOrientation orientation, BiFunction<ExtractionControllerFactory, ReportLoaderFactory, ExtractionController> controllerCreator);

    /**
     * @param orientation
     * @return data extraction controller
     */
    ExtractionController controllerBy(BandOrientation orientation);

    /**
     * @return default data extraction controller
     */
    ExtractionController defaultController();
}
