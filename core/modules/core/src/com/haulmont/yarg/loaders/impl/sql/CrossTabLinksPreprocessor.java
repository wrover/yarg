package com.haulmont.yarg.loaders.impl.sql;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.ObjectUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public class CrossTabLinksPreprocessor {
    private static final Pattern REF_PATTERN = Pattern.compile("(\\w+)@(.+)\\b");
    private static final String REF_NAME = "%s_%s";

    Multimap<String, String> references = HashMultimap.create();

    String processedQuery;

    public CrossTabLinksPreprocessor(String sql) {
        Matcher matcher = REF_PATTERN.matcher(checkNotNull(sql));
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            references.put(matcher.group(1), matcher.group(2));
            matcher.appendReplacement(sb, String.format(REF_NAME, matcher.group(1), matcher.group(2)));
        }
        matcher.appendTail(sb);
        processedQuery = sb.toString();
    }

    public String processedQuery() {
        return processedQuery;
    }

    public Map<String, Object> decorateParams(Map<String, Object> params) {
        Map<String, Object> decoratedParams = new HashMap<>(params);
        references.entries().forEach(e-> {
            List<Map<String, Object>> qValues = ObjectUtils
                    .defaultIfNull((List<Map<String, Object>>)params.get(e.getKey()), Collections.emptyList());
            decoratedParams.put(String.format(REF_NAME, e.getKey(), e.getValue()),
                    qValues.stream().map(data-> data.get(e.getValue()))
                            .filter(Objects::nonNull).collect(Collectors.toList()));

        });
        return decoratedParams;
    }
}
