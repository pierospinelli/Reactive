package it.pjsoft.reactive.generic.transfer.model.util;

import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import it.pjsoft.reactive.generic.transfer.model.msg.Feature;
import it.pjsoft.reactive.generic.transfer.model.msg.Features;

public class FeaturesAdapter extends XmlAdapter<Features, Map<String, String>> {
    public FeaturesAdapter() {
    }

    public Features marshal(Map<String, String> map) throws Exception {
    	Features features = new Features();
        for (Map.Entry<String, String> entry : map.entrySet())
        	features.getFeatures().add(new Feature(entry.getKey(), entry.getValue()));

        return features;
    }

    public Map<String, String> unmarshal(Features features) throws Exception {
        Map<String, String> r = new TreeMap<String, String>();
        for (Feature feature : features.getFeatures())
            r.put(feature.key, feature.value);
        return r;
    }
}