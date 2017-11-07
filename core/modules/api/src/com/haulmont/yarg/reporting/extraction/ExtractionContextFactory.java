package com.haulmont.yarg.reporting.extraction;

import com.haulmont.yarg.structure.BandData;
import com.haulmont.yarg.structure.ReportBand;

import java.util.Map;

/**
 * This interface implementation should create immutable extraction context object
 *
 * The default implementation is com.haulmont.yarg.reporting.extraction.DefaultExtractionContextFactory
 */
public interface ExtractionContextFactory {
    /**
     * Method should always return new <b>immutable</b> context object
     *
     * @param band
     * @param parentBand - parent report band data (may be null)
     * @param params - loader type related params
     * @return context
     */
    ExtractionContext context(ReportBand band, BandData parentBand, Map<String, Object> params);
}
