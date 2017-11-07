package com.haulmont.yarg.reporting.extraction;

import com.haulmont.yarg.structure.BandData;
import com.haulmont.yarg.structure.ReportBand;

import java.util.Map;

/**
 * This interface implementation presents data extraction context dependent logic
 *
 * The default implementation is com.haulmont.yarg.reporting.extraction.ExtractionContextImpl
 */
public interface ExtractionContext {
    /**
     * @return boolean flag that controller should create empty data row if no report query data presented
     */
    boolean putEmptyRowIfNoDataSelected();

    /**
     * @return current processing report band
     */
    ReportBand getBand();

    /**
     * @return parent report band loaded data
     */
    BandData getParentBandData();

    /**
     * @return params for data loader
     */
    Map<String, Object> getParams();

    /**
     * Method must extend existed params with presented params map
     *
     * @param params
     * @return current context version
     */
    ExtractionContext extendParams(Map<String, Object> params);

    /**
     * Method must create new version of context with new params (not extended)
     *
     * @param params
     * @return new context version
     */
    ExtractionContext withParams(Map<String, Object> params);

    /**
     * Method must create new version of context with new report band and parent band data
     *
     * @param band
     * @param parentBand
     * @return new context version
     */
    ExtractionContext withBand(ReportBand band, BandData parentBand);

    /**
     * Method must create new version of context with parent band data
     *
     * @param parentBand
     * @return new context version
     */
    ExtractionContext withParentData(BandData parentBand);
}
