package StreamGenerator.parameters;

import java.util.Map;

public class ConfigType {
    private Map<String, Object> kafka;
    private Map<String, Object> query;
    private Map<String, Object> data;

    public Map<String, Object> getKafka() {
        return kafka;
    }

    public void setKafka(Map<String, Object> kafka) {
        this.kafka = kafka;
    }

    public Map<String, Object> getQuery() {
        return query;
    }

    public void setQuery(Map<String, Object> query) {
        this.query = query;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "kafka=" + kafka + ", query=" + query + ", data=" + data;
    }
}
