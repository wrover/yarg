package com.haulmont.yarg.reporting;

import com.haulmont.yarg.reporting.controller.DefaultExtractionController;
import com.haulmont.yarg.reporting.controller.ExtractionController;
import com.haulmont.yarg.structure.BandOrientation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

public final class OrientationExtractionRegistry {
    private static final ExtractionController DEFAULT = new DefaultExtractionController();
    private static final Map<BandOrientation, ExtractionController> extractionControllerMap = new ConcurrentHashMap<>();

    public static void register(BandOrientation orientation, ExtractionController controller) {
        checkNotNull(orientation);
        checkNotNull(controller);

        extractionControllerMap.put(orientation, controller);
    }

    public static ExtractionController controller(BandOrientation orientation) {
        return extractionControllerMap.getOrDefault(BandOrientation.NN(orientation), DEFAULT);
    }

    public static ExtractionController defaultController() {
        return DEFAULT;
    }
}
