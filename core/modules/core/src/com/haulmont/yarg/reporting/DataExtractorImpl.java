/*
 * Copyright 2013 Haulmont
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.haulmont.yarg.reporting;

import com.google.common.base.Preconditions;
import com.haulmont.yarg.loaders.factory.ReportLoaderFactory;
import com.haulmont.yarg.reporting.controller.CrossTabExtractionController;
import com.haulmont.yarg.reporting.controller.ExtractionContext;
import com.haulmont.yarg.structure.BandData;
import com.haulmont.yarg.structure.BandOrientation;
import com.haulmont.yarg.structure.Report;
import com.haulmont.yarg.structure.ReportBand;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DataExtractorImpl implements DataExtractor {
    protected static final Map<String, Object> EMPTY_MAP = Collections.emptyMap();

    protected ReportLoaderFactory loaderFactory;

    protected boolean putEmptyRowIfNoDataSelected = true;

    static {
        OrientationExtractionRegistry.register(BandOrientation.CROSS, new CrossTabExtractionController());
    }

    public DataExtractorImpl(ReportLoaderFactory loaderFactory) {
        Preconditions.checkNotNull(loaderFactory, "\"loaderFactory\" parameter can not be null");
        this.loaderFactory = loaderFactory;
    }

    public void extractData(Report report, Map<String, Object> params, BandData rootBand) {
        List<Map<String, Object>> rootBandData = OrientationExtractionRegistry.defaultController().extractData(
                loaderFactory,
                new ExtractionContext(this, report.getRootBand(), null, params)
        );
        if (CollectionUtils.isNotEmpty(rootBandData)) {
            rootBand.getData().putAll(rootBandData.get(0));
        }

        List<ReportBand> firstLevelBands = report.getRootBand().getChildren();
        if (firstLevelBands != null) {
            for (ReportBand definition : firstLevelBands) {
                List<BandData> bands = createBands(definition, rootBand, params);
                rootBand.addChildren(bands);
                rootBand.getFirstLevelBandDefinitionNames().add(definition.getName());
            }
        }
    }

    public void setPutEmptyRowIfNoDataSelected(boolean putEmptyRowIfNoDataSelected) {
        this.putEmptyRowIfNoDataSelected = putEmptyRowIfNoDataSelected;
    }

    @Override
    public boolean getPutEmptyRowIfNoDataSelected() {
        return putEmptyRowIfNoDataSelected;
    }

    protected List<BandData> createBands(ReportBand definition, BandData parentBandData, Map<String, Object> params) {
        return OrientationExtractionRegistry.controller(definition.getBandOrientation())
                .extract(loaderFactory, new ExtractionContext(this, definition, parentBandData, params));
    }
}
