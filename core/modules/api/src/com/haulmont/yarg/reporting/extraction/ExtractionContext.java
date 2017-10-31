package com.haulmont.yarg.reporting.extraction;

import com.haulmont.yarg.structure.BandData;
import com.haulmont.yarg.structure.ReportBand;

import java.util.Map;

public interface ExtractionContext {
    boolean putEmptyRowIfNoDataSelected();

    ReportBand getBand();

    BandData getParentBandData();

    Map<String, Object> getParams();

    ExtractionContext extendParams(Map<String, Object> params);

    ExtractionContext withParams(Map<String, Object> params);

    ExtractionContext withBand(ReportBand band);

    ExtractionContext withParentData(BandData parentBand);
}
