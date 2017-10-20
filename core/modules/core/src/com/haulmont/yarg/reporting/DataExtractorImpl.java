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
import com.haulmont.yarg.reporting.controller.ExtractionContext;
import com.haulmont.yarg.structure.BandData;
import com.haulmont.yarg.structure.Report;
import com.haulmont.yarg.structure.ReportBand;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class DataExtractorImpl implements DataExtractor {
    protected static final Map<String, Object> EMPTY_MAP = Collections.emptyMap();

    protected ReportLoaderFactory loaderFactory;

    protected boolean putEmptyRowIfNoDataSelected = true;

    public DataExtractorImpl(ReportLoaderFactory loaderFactory) {
        Preconditions.checkNotNull(loaderFactory, "\"loaderFactory\" parameter can not be null");
        this.loaderFactory = loaderFactory;
    }

    public void extractData(Report report, Map<String, Object> params, BandData rootBand) {
        List<Map<String, Object>> rootBandData = getBandData(report.getRootBand(), null, params);
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

    public boolean getPutEmptyRowIfNoDataSelected() {
        return putEmptyRowIfNoDataSelected;
    }

    protected List<BandData> createBands(ReportBand definition, BandData parentBand, Map<String, Object> params) {
        List<Map<String, Object>> outputData = getBandData(definition, parentBand, params);
        return createBandsList(definition, parentBand, outputData, params);
    }

    protected List<BandData> createBandsList(ReportBand definition, BandData parentBand, List<Map<String, Object>> outputData, Map<String, Object> params) {
        List<BandData> bandsList = new ArrayList<>();
        for (Map<String, Object> data : outputData) {
            BandData band = new BandData(definition.getName(), parentBand, definition.getBandOrientation());
            band.setData(data);
            Collection<ReportBand> childrenBandDefinitions = definition.getChildren();
            if (childrenBandDefinitions != null) {
                for (ReportBand childDefinition : childrenBandDefinitions) {
                    List<BandData> childBands = createBands(childDefinition, band, params);
                    band.addChildren(childBands);
                }
            }
            bandsList.add(band);
        }
        return bandsList;
    }

    protected List<Map<String, Object>> getBandData(ReportBand band, BandData parentBand, Map<String, Object> params) {
        return OrientationExtractionRegistry.controller(band.getBandOrientation())
                .extract(loaderFactory, new ExtractionContext(this, band, parentBand, params));
    }
}
