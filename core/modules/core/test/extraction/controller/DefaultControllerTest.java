package extraction.controller;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.haulmont.yarg.loaders.factory.DefaultLoaderFactory;
import com.haulmont.yarg.loaders.impl.GroovyDataLoader;
import com.haulmont.yarg.loaders.impl.JsonDataLoader;
import com.haulmont.yarg.loaders.impl.SqlDataLoader;
import com.haulmont.yarg.reporting.extraction.DefaultExtractionContextFactory;
import com.haulmont.yarg.reporting.extraction.DefaultExtractionControllerFactory;
import com.haulmont.yarg.reporting.extraction.ExtractionContextFactory;
import com.haulmont.yarg.structure.BandData;
import com.haulmont.yarg.structure.ReportBand;
import com.haulmont.yarg.util.groovy.DefaultScriptingImpl;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Stream;

public class DefaultControllerTest {

    static TestDatabase database = new TestDatabase();
    static DefaultLoaderFactory loaderFactory = new DefaultLoaderFactory();
    DefaultExtractionControllerFactory controllerFactory
            = new DefaultExtractionControllerFactory(loaderFactory);

    ExtractionContextFactory contextFactory = new DefaultExtractionContextFactory(ExtractionUtils.emptyExtractor());

    @BeforeClass
    public static void construct() throws Exception {
        database.setUpDatabase();
        FixtureUtils.loadDb(database.getDs(), "extraction/fixture/controller_test.sql");

        loaderFactory.setSqlDataLoader(new SqlDataLoader(database.getDs()));
        loaderFactory.setGroovyDataLoader(new GroovyDataLoader(new DefaultScriptingImpl()));
        loaderFactory.setJsonDataLoader(new JsonDataLoader());
    }

    @AfterClass
    public static void destroy() throws Exception {
        database.stop();
    }

    @Test
    public void testSqlExtractionForCrosstabBand() throws IOException, URISyntaxException {
        ReportBand band = YmlDataUtil.bandFrom(FileLoader.load("extraction/fixture/default_sql_report_band.yml"));
        BandData rootBand = new BandData(BandData.ROOT_BAND_NAME);
        rootBand.setData(new HashMap<>());
        rootBand.setFirstLevelBandDefinitionNames(new HashSet<>());

        Multimap<String, BandData> reportBandMap = HashMultimap.create();

        for (ReportBand definition : band.getChildren()) {
            List<BandData> data = controllerFactory.controllerBy(definition.getBandOrientation())
                    .extract(contextFactory.context(definition, rootBand, new HashMap<>()));

            Assert.assertNotNull(data);

            data.forEach(b-> {
                Assert.assertNotNull(b);
                Assert.assertTrue(StringUtils.isNotEmpty(b.getName()));

                reportBandMap.put(b.getName(), b);
            });

            rootBand.addChildren(data);
            rootBand.getFirstLevelBandDefinitionNames().add(definition.getName());
        }

        checkHeader(reportBandMap.get("header"), 12, "MONTH_NAME", "MONTH_ID");
        checkMasterData(reportBandMap.get("master_data"), 3, 12,
                "USER_ID", "LOGIN", "HOURS");
    }

    @Test
    public void testGroovyExtractionForBand() throws IOException, URISyntaxException {
        ReportBand band = YmlDataUtil.bandFrom(FileLoader.load("extraction/fixture/default_groovy_report_band.yml"));

        BandData rootBand = new BandData(BandData.ROOT_BAND_NAME);
        rootBand.setData(new HashMap<>());
        rootBand.setFirstLevelBandDefinitionNames(new HashSet<>());

        Multimap<String, BandData> reportBandMap = HashMultimap.create();

        for (ReportBand definition : band.getChildren()) {
            List<BandData> data = controllerFactory.controllerBy(definition.getBandOrientation())
                    .extract(contextFactory.context(definition, rootBand, new HashMap<>()));

            Assert.assertNotNull(data);

            data.forEach(b-> {
                Assert.assertNotNull(b);
                Assert.assertTrue(StringUtils.isNotEmpty(b.getName()));

                reportBandMap.put(b.getName(), b);
            });

            rootBand.addChildren(data);
            rootBand.getFirstLevelBandDefinitionNames().add(definition.getName());
        }

        checkHeader(reportBandMap.get("header"), 2, "name", "id");
        checkMasterData(reportBandMap.get("master_data"), 2, 2,
                "id", "name", "value", "user_id");
    }

    @Test
    public void testJsonExtractionForBand() throws IOException, URISyntaxException {
        ReportBand band = YmlDataUtil.bandFrom(FileLoader.load("extraction/fixture/default_json_report_band.yml"));

        BandData rootBand = new BandData(BandData.ROOT_BAND_NAME);
        rootBand.setData(new HashMap<>());
        rootBand.setFirstLevelBandDefinitionNames(new HashSet<>());

        Multimap<String, BandData> reportBandMap = HashMultimap.create();

        for (ReportBand definition : band.getChildren()) {
            List<BandData> data = controllerFactory.controllerBy(definition.getBandOrientation())
                    .extract(contextFactory.context(definition, rootBand, new HashMap<>()));

            Assert.assertNotNull(data);

            data.forEach(b-> {
                Assert.assertNotNull(b);
                Assert.assertTrue(StringUtils.isNotEmpty(b.getName()));

                reportBandMap.put(b.getName(), b);
            });

            rootBand.addChildren(data);
            rootBand.getFirstLevelBandDefinitionNames().add(definition.getName());
        }

        checkHeader(reportBandMap.get("header"), 2, "name", "id");
        checkMasterData(reportBandMap.get("master_data"), 2, 2,
                "id", "name", "value", "user_id");
    }

    private void checkHeader(Collection<BandData> bandDataCollection, int expected, String... headerFields) {
        Assert.assertNotNull(bandDataCollection);
        Assert.assertEquals(bandDataCollection.size(), 1);
        BandData bandData = bandDataCollection.iterator().next();
        Assert.assertTrue(CollectionUtils.isNotEmpty(bandData.getChildrenList()));
        Assert.assertEquals(bandData.getChildrenList().size(), expected);

        bandData.getChildrenList().forEach(childData-> {
            Assert.assertNotNull(childData);
            Assert.assertNotNull(childData.getData());
            Stream.of(headerFields).forEach(key-> Assert.assertTrue(childData.getData().containsKey(key)));
        });
    }

    private void checkMasterData(Collection<BandData> bandDataCollection, int expectedMasterDataCount, int expectedCrossDataCount, String... fields) {
        Assert.assertNotNull(bandDataCollection);
        Assert.assertEquals(bandDataCollection.size(), expectedMasterDataCount);

        bandDataCollection.forEach(bandData-> {
            Assert.assertTrue(MapUtils.isNotEmpty(bandData.getData()));
            Assert.assertTrue(CollectionUtils.isNotEmpty(bandData.getChildrenList()));
            Assert.assertEquals(expectedCrossDataCount, bandData.getChildrenList().size());

            if (fields.length > 0) {
                Assert.assertTrue(bandData.getData().containsKey(fields[0]));
            }
            if (fields.length > 1) {
                Assert.assertTrue(bandData.getData().containsKey(fields[1]));
            }

            bandData.getChildrenList().forEach(childData-> {
                Assert.assertNotNull(childData);
                Assert.assertNotNull(childData.getData());
            });
            if (fields.length > 2) {
                List<String> childFields = Arrays.asList(fields).subList(2, fields.length - 1);
                Assert.assertTrue(bandData.getChildrenList().stream().anyMatch(childData-> {
                    for (String field : childFields) {
                        if (!childData.getData().containsKey(field)) return false;
                    }
                    return true;
                }));
            }
        });
    }
}
