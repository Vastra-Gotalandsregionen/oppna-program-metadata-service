/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.vgregion.metaservice.keywordservice.schema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import se.vgregion.metaservice.keywordservice.domain.Options;
import se.vgregion.metaservice.schema.domain.OptionsType;
import se.vgregion.metaservice.schema.domain.OptionsType.SourceIds;
import se.vgregion.metaservice.schema.domain.OptionsType.SourceIds.Entry;
import se.vgregion.metaservice.schema.domain.StringArray;

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
        optionsType.setSourceIds(toSourceIds(options.getSourceIds()));
        optionsType.setWordLimit(options.getWordLimit());

        return optionsType;
    }

    /**
     * Builds an SDO SourceIds from a Map<Integer,String[]>
     * @param sourceIdsMap
     * @return
     */
    private static SourceIds toSourceIds(Map<Integer, String[]> sourceIdsMap) {
        SourceIds sourceIds = new SourceIds();
        for (Integer namespaceId : sourceIdsMap.keySet()) {
            SourceIds.Entry entry = new SourceIds.Entry();
            entry.setKey(namespaceId);
            entry.setValue(toStringArray(sourceIdsMap.get(namespaceId)));
        }
        return sourceIds;
    }

    /**
     * Builds an SDO StringArray from an array of strings
     * @param array
     * @return
     */
    private static StringArray toStringArray(String[] array) {
        StringArray stringArray = new StringArray();
        for (String item : array) {
            stringArray.getItem().add(item);
        }
        return stringArray;
    }

    /**
     * Builds an Options from an SDO OptionsType object
     *
     * @param OptionsType identType
     * @return Options
     */
    public static Options fromOptionsType(OptionsType optionsType) {
        Options options = new Options(optionsType.getWordLimit(),fromSourceIds(optionsType.getSourceIds()));
        return options;
    }

    /**
     * Builds a Map of Integer String[] pairs from an SDO SourceIds
     * @param sourceIds
     * @return
     */
    private static Map<Integer,String[]> fromSourceIds(SourceIds sourceIds) {
        List<Entry> entries = sourceIds.getEntry();
        Map<Integer,String[]> sourceIdsMap = new HashMap<Integer, String[]>(entries.size());
        for(Entry entry : entries) {
            sourceIdsMap.put(entry.getKey(), fromStringArray(entry.getValue()));
        }
        return sourceIdsMap;
    }

    /**
     * Builds an array of String from an SDO StringArray
     * @param stringArray
     * @return
     */
    private static String[] fromStringArray(StringArray stringArray) {
        String[] array = stringArray.getItem().toArray(new String[0]);
        return array;
    }
    
}
