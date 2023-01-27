package org.configureme.configwithmap;

import org.configureme.annotations.Configure;
import org.configureme.annotations.SetIf;
import org.configureme.util.StringUtils;

import java.util.HashMap;

public class ChildConfiguration {

    @Configure
    private int pubAttribute;


    private HashMap<String,String> data = new HashMap<>();

    public int getPubAttribute() {
        return pubAttribute;
    }

    public void setPubAttribute(int pubAttribute) {
        this.pubAttribute = pubAttribute;
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public void setData(HashMap<String, String> map) {
        this.data = map;
    }

    @Override
    public String toString() {
        return "ChildConfiguration{" +
                "pubAttribute=" + pubAttribute +
                ", data=" + data +
                '}';
    }

    @SetIf(condition=SetIf.SetIfCondition.matches, value="data")
    public void setMapData(String key, String value){
        value = StringUtils.removeChar(value, '{');
        value = StringUtils.removeChar(value, '}');

        String[] pairs = StringUtils.tokenize(value, ',');
        for (String pair : pairs){
            String tokens[] = StringUtils.tokenize(pair, ':');
            data.put(
                    StringUtils.removeChar(tokens[0], '\"'),
                    StringUtils.removeChar(tokens[1], '\"')
            );
        }

    }
}
