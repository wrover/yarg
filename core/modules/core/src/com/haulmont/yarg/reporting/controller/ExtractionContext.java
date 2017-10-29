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
    protected BandData bandData;
    protected Map<String, Object> params;

    public ExtractionContext(DataExtractor extractor, ReportBand band, BandData bandData, Map<String, Object> params) {
        checkNotNull(extractor);
        checkNotNull(band);
        checkNotNull(params);

        this.extractor = extractor;
        this.band = band;
        this.bandData = bandData;
        this.params = params;
    }

    public boolean putEmptyRowIfNoDataSelected() {
        return extractor.getPutEmptyRowIfNoDataSelected();
    }

    public ReportBand getBand() {
        return band;
    }

    public BandData getBandData() {
        return bandData;
    }

    public Map<String, Object> getParams() {
        return Collections.unmodifiableMap(params);
    }

    public ExtractionContext withParams(Map<String, Object> params) {
        return new ExtractionContext(extractor, band, bandData, params);
    }
}
