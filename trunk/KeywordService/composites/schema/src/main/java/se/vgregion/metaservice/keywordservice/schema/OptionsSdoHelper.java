/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.vgregion.metaservice.keywordservice.schema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import se.vgregion.metaservice.keywordservice.domain.Options;
import se.vgregion.metaservice.schema.domain.FilterByPropertiesListType;
import se.vgregion.metaservice.schema.domain.IncludeSourceIdsListType;
import se.vgregion.metaservice.schema.domain.OptionsType;
import se.vgregion.metaservice.schema.domain.OptionsType.FilterByProperties;
import se.vgregion.metaservice.schema.domain.OptionsType.IncludeSourceIds;
import se.vgregion.metaservice.schema.domain.OptionsType.IncludeSourceIds.Entry;

/**
 * HelperClass for conversion between SDO representation of Options and the
 * common type POJO counterpart
 * @author tobias
 */
public class OptionsSdoHelper {

    public OptionsSdoHelper() {
    }

    /**
     * Builds an SDO OptionsType from an Options object
     *
     * @param options
     * @return OptionsType
     */
    public static OptionsType toOptionsType(Options options) {
        OptionsType optionsType = new OptionsType();
        if (options != null) {
            optionsType.setIncludeSourceIds(toIncludeSourceIds(options.getIncludeSourceIds()));
            optionsType.setWordLimit(options.getWordLimit());
            optionsType.setUrl(options.getUrl());

            FilterByProperties props = new FilterByProperties();
            if (options.getFilterByProperties() != null) {
                for (java.util.Map.Entry<String, List<String>> entry : options.getFilterByProperties().entrySet()) {
                    FilterByProperties.Entry sdoEntry = new FilterByProperties.Entry();
                    sdoEntry.setKey(entry.getKey());

                    FilterByPropertiesListType sdoEntryVal = new FilterByPropertiesListType();
                    for (String val : entry.getValue()) {
                        sdoEntryVal.getFilter().add(val);
                    }

                    sdoEntry.setValue(sdoEntryVal);
                    props.getEntry().add(sdoEntry);
                }
            }

            optionsType.setFilterByProperties(props);
            optionsType.setMatchSynonyms(options.matchSynonyms());
            optionsType.setSynomize(options.synonymize());
        }
        return optionsType;
    }

    /**
     * Builds an SDO SourceIds from a Map<Integer,String[]>
     * @param sourceIdsMap
     * @return
     */
    private static IncludeSourceIds toIncludeSourceIds(Map<Integer, String[]> sourceIdsMap) {
        IncludeSourceIds sourceIds = new IncludeSourceIds();
        if (sourceIdsMap != null) {
            for (Integer namespaceId : sourceIdsMap.keySet()) {
                IncludeSourceIds.Entry entry = new IncludeSourceIds.Entry();
                entry.setKey(namespaceId);
                entry.setValue(toIncludeSourceIdsListType(sourceIdsMap.get(namespaceId)));
            }
        }
        return sourceIds;
    }

    /**
     * Builds an SDO IncludeSourceIdsListType from an array of strings
     * @param array
     * @return
     */
    private static IncludeSourceIdsListType toIncludeSourceIdsListType(String[] array) {
        IncludeSourceIdsListType sourceIdsListType = new IncludeSourceIdsListType();
        for (String item : array) {
            sourceIdsListType.getIncludeSourceId().add(item);
        }
        return sourceIdsListType;
    }

    /**
     * Builds an Options from an SDO OptionsType object
     *
     * @param OptionsType identType
     * @return Options
     */
    public static Options fromOptionsType(OptionsType optionsType) {
        Options options = new Options();
        if (optionsType != null) {
            options = new Options();
            if (optionsType.getWordLimit() != 0) {
                options.setWordLimit(optionsType.getWordLimit());
            }
            if (optionsType.getIncludeSourceIds() != null) {
                options.setIncludeSourceIds(fromIncludeSourceIds(optionsType.getIncludeSourceIds()));
            }
            if (optionsType.getUrl() != null) {
                options.setUrl(optionsType.getUrl());
            }

            // Construct a Map<String, List<String>>  from the sequence of entries
            if (optionsType.getFilterByProperties() != null && optionsType.getFilterByProperties().getEntry() != null) {
                Map<String, List<String>> filterMap = new HashMap<String, List<String>>();
                List<FilterByProperties.Entry> entries = optionsType.getFilterByProperties().getEntry();
                for (Iterator<FilterByProperties.Entry> itr = entries.listIterator(); itr.hasNext();) {
                    FilterByProperties.Entry e = itr.next();
                    filterMap.put(e.getKey(), e.getValue().getFilter());
                }

                options.setFilterByProperties(filterMap);
            }

            options.setMatchSynonyms(optionsType.isMatchSynonyms());
            options.setSynonymize(optionsType.isSynomize());

        }
        return options;
    }

    /**
     * Builds a Map of Integer String[] pairs from an SDO IncludeSourceIds
     * @param sourceIds
     * @return
     */
    private static Map<Integer, String[]> fromIncludeSourceIds(IncludeSourceIds sourceIds) {
        if (sourceIds == null) {
            return null;
        }
        List<Entry> entries = sourceIds.getEntry();
        Map<Integer, String[]> sourceIdsMap = new HashMap<Integer, String[]>(entries.size());
        for (Entry entry : entries) {
            sourceIdsMap.put(entry.getKey(), fromIncludeSourceIdsListType(entry.getValue()));
        }
        return sourceIdsMap;
    }

    /**
     * Builds an array of String from an SDO IncludeSourceIdsListType
     * @param IncludeSourceIdsListType
     * @return
     */
    private static String[] fromIncludeSourceIdsListType(IncludeSourceIdsListType includeSourceIdsListType) {
        String[] array = includeSourceIdsListType.getIncludeSourceId().toArray(new String[0]);
        return array;
    }
}
