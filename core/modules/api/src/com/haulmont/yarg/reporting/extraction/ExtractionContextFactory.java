package com.haulmont.yarg.reporting.extraction;

import com.haulmont.yarg.structure.BandData;
import com.haulmont.yarg.structure.ReportBand;

import java.util.Map;

public interface ExtractionContextFactory {
    ExtractionContext context(ReportBand band, BandData parentBand, Map<String, Object> params);
}
