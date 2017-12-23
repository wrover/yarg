package com.haulmont.yarg.loaders.impl;

import com.haulmont.yarg.exception.DataLoadingException;
import com.haulmont.yarg.loaders.UriDataLoaderService;
import com.haulmont.yarg.structure.BandData;
import com.haulmont.yarg.structure.ReportQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Загрузчик JSON, позволяющий загружать данные из внешнего API через
 * {@link UriDataLoaderService}
 * <p>В дополнение к <em>"parameter=parameterName JSONP-выражение"</em> позволяет использовать
 * <em>"url=url JSONP-выражение"</em></p>
 *
 * <p>Чтобы не дергать удаленный REST много раз, допускается использование в дочерних
 * Band результатов, загруженных в родительских полосах (в т.ч. банда Root). Для
 * этого достаточно использовать синтаксис: <em>prameter=Band.parameter</em></p>
 *
 * <p>Однако этот подход порождает много параметров, что расходует память.
 * Реализация {@link UriDataLoaderService} должна поддерживать кеширование.
 * Однако разбор JSON + выполнение JSONP в этом случае работают каждый раз, что время.</p>
 */
@SuppressWarnings("unused")
public class ServiceJsonDataLoader extends JsonDataLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceJsonDataLoader.class);

    static protected final String PARAMETER = "parameter=";
    static protected final String URL = "url=";

    // FIXME Разрешим использовать точки в названии параметров, в YARG это не разрешено
    protected Pattern parameterPattern = Pattern.compile(PARAMETER + "([A-z0-9_\\.]+)");
    protected Pattern urlPattern = Pattern.compile(URL + "([\\S]+)");

    private final UriDataLoaderService uriDataLoaderService;

    public ServiceJsonDataLoader(UriDataLoaderService dataLoaderService) {
        this.uriDataLoaderService = dataLoaderService;
    }

    @Override
    public List<Map<String, Object>> loadData(ReportQuery reportQuery, BandData parentBand, Map<String, Object> reportParams) {

        Map<String, Object> currentParams = copyParameters(reportParams);
        addParentBandDataToParametersRecursively(parentBand, currentParams);

        LOG.debug("loading reportQuery name={} script={}", reportQuery.getName(), reportQuery.getScript());
        LOG.debug("currentParams={}", currentParams);

        List<Map<String, Object>> result;

        if (reportQuery.getScript().startsWith(PARAMETER)) {

            Matcher parameterMatcher = parameterPattern.matcher(reportQuery.getScript());
            String parameterName = getParameterName(parameterMatcher);

            if (parameterName != null) {
                Object parameterValue = currentParams.get(parameterName);

                LOG.debug("parameterName={}, parameterValue={}, type={}", parameterName, parameterValue, parameterValue.getClass());

                if (parameterValue instanceof Map) {
                    LOG.debug("use Map parameter as a ready result");
                    //noinspection unchecked
                    result = Collections.singletonList((Map<String, Object>) parameterValue);

                } else if (StringUtils.isNotBlank(parameterValue.toString())) {
                    LOG.debug("using JSONP loader");
                    result = loadDataFromScript(reportQuery, currentParams, parameterMatcher, parameterValue);
                } else {
                    return Collections.emptyList();
                }
            } else {
                throw new DataLoadingException(String.format("Query string doesn't contain link to parameter. " +
                        "Script [%s]", reportQuery.getScript()));
            }

        } else if (reportQuery.getScript().startsWith(URL)) {

            Matcher urlMatcher = urlPattern.matcher(reportQuery.getScript());
            String url = getParameterName(urlMatcher);


            Matcher matcher = AbstractDbDataLoader.COMMON_PARAM_PATTERN.matcher(url);
            while (matcher.find()) {
                String parameter = matcher.group(1);
                url = matcher.replaceAll(String.valueOf(currentParams.get(parameter)));
            }

            LOG.debug("url={}", url);

            String data;

            try {
                data = uriDataLoaderService.loadFromUri(url);
            } catch (Exception e) {
                throw new DataLoadingException(String.format("Error while retrieving data from url [%s]. Script [%s]", url, reportQuery.getScript()));

            }

            LOG.debug("data={}", data);

            if (StringUtils.isNotBlank(data)) {
                result = loadDataFromScript(reportQuery, currentParams, urlMatcher, data);
            } else {
                return Collections.emptyList();
            }

        } else {
            throw new DataLoadingException(String.format("Query string doesn't contain neither URL nor link to parameter. " +
                    "Script [%s]", reportQuery.getScript()));
        }

        LOG.debug("JSONPath result: {}", result);
        return result;
    }
}
