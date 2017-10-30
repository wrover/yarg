package com.haulmont.yarg.reporting.controller;

import com.haulmont.yarg.reporting.DataExtractor;
import com.haulmont.yarg.structure.BandData;
import com.haulmont.yarg.structure.ReportBand;

import java.util.Collections;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ExtractionContext {
    protected DataExtractor extractor;
    protected ReportBand band;
    protected BandData parentBand;
    protected Map<String, Object> params;

    public ExtractionContext(DataExtractor extractor, ReportBand band, BandData parentBand, Map<String, Object> params) {
        checkNotNull(extractor);
        checkNotNull(band);
        checkNotNull(params);

        this.extractor = extractor;
        this.band = band;
        this.parentBand = parentBand;
        this.params = params;
    }

    public boolean putEmptyRowIfNoDataSelected() {
        return extractor.getPutEmptyRowIfNoDataSelected();
    }

    public ReportBand getBand() {
        return band;
    }

    public BandData getParentBandData() {
        return parentBand;
    }

    public Map<String, Object> getParams() {
        return Collections.unmodifiableMap(params);
    }

    public ExtractionContext extendParams(Map<String, Object> params) {
        this.params.putAll(params);
        return this;
    }

    public ExtractionContext withParams(Map<String, Object> params) {
        return new ExtractionContext(extractor, band, parentBand, params);
    }

    public ExtractionContext withBand(ReportBand band) {
        return new ExtractionContext(extractor, band, parentBand, params);
    }

    public ExtractionContext withParentData(BandData parentBand) {
        return new ExtractionContext(extractor, band, parentBand, params);
    }
}
