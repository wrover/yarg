package extraction.controller;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.haulmont.yarg.loaders.factory.DefaultLoaderFactory;
import com.haulmont.yarg.loaders.impl.SqlDataLoader;
import com.haulmont.yarg.reporting.extraction.DefaultExtractionContextFactory;
import com.haulmont.yarg.reporting.extraction.DefaultExtractionControllerFactory;
import com.haulmont.yarg.reporting.extraction.ExtractionContextFactory;
import com.haulmont.yarg.reporting.extraction.controller.CrossTabExtractionController;
import com.haulmont.yarg.structure.BandData;
import com.haulmont.yarg.structure.BandOrientation;
import com.haulmont.yarg.structure.ReportBand;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.*;
import utils.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CrosstabControllerTest {

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
    }

    @AfterClass
    public static void destroy() throws Exception {
        database.stop();
    }

    @Before
    public void configure() {
        controllerFactory.register(BandOrientation.CROSS, CrossTabExtractionController::new);
    }

    @Test
    public void testExtractionForCrosstabBand() throws IOException, URISyntaxException {
        ReportBand band = YmlDataUtil.bandFrom(FileLoader.load("extraction/fixture/cross_report_band.yml"));
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

        checkHeader(reportBandMap.get("crosstab_header"));
        checkMasterData(reportBandMap.get("crosstab_master_data"));
    }

    private void checkMasterData(Collection<BandData> bandDataCollection) {
        Assert.assertNotNull(bandDataCollection);
        Assert.assertEquals(bandDataCollection.size(), 3);
        bandDataCollection.forEach(bandData-> {
            Assert.assertTrue(MapUtils.isNotEmpty(bandData.getData()));
            Assert.assertTrue(CollectionUtils.isNotEmpty(bandData.getChildrenList()));
            Assert.assertEquals(12, bandData.getChildrenList().size());

            Assert.assertTrue(bandData.getData().containsKey("USER_ID"));
            Assert.assertTrue(bandData.getData().containsKey("LOGIN"));

            bandData.getChildrenList().forEach(childData-> {
                Assert.assertNotNull(childData);
                Assert.assertNotNull(childData.getData());
            });

            Assert.assertTrue(bandData.getChildrenList().stream().anyMatch(childData->
                    childData.getData().containsKey("HOURS")));
        });
    }

    private void checkHeader(Collection<BandData> bandDataCollection) {
        Assert.assertNotNull(bandDataCollection);
        Assert.assertEquals(1, bandDataCollection.size());
        BandData bandData = bandDataCollection.iterator().next();
        Assert.assertTrue(CollectionUtils.isNotEmpty(bandData.getChildrenList()));
        Assert.assertEquals(12, bandData.getChildrenList().size());

        bandData.getChildrenList().forEach(childData-> {
            Assert.assertNotNull(childData);
            Assert.assertNotNull(childData.getData());
            Assert.assertTrue(childData.getData().containsKey("MONTH_NAME"));
            Assert.assertTrue(childData.getData().containsKey("MONTH_ID"));
        });
    }
}
