package StreamGenerator.parameters;

import org.apache.flink.shaded.jackson2.org.yaml.snakeyaml.Yaml;
import org.apache.flink.shaded.jackson2.org.yaml.snakeyaml.constructor.Constructor;
import java.io.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Params {
    /**
     * Config File
     */
    public final String YAML_CONFIG = "kafkaproducer-conf.yml";
    public final String YAML_PATH = new File(".").getAbsoluteFile().getParent() + File.separator +
            "conf" + File.separator + YAML_CONFIG;

    /**
     * Parameters
     */
    /* kafka */
    public String topicName;
    public String bootStrapServers;
    public boolean asyncLoading;

    /* query */
    public String option;

    /* data */
    public String format;
    public String dateFormat;
    //public int numberOfObjects;
    public int nRows;
    public int generationRate;
    public List<Double> bBox;
    public List<Double> varianceRange;
    public List<Integer> nSidesRange;
    public List<Integer> nLineSegmentsRange;
    public List<Integer> objIDRange;

    public Params() throws NullPointerException, IllegalArgumentException, NumberFormatException {
        ConfigType config = getYamlConfig(YAML_PATH);

        /* kafka */
        try {
            if ((topicName = (String)config.getKafka().get("topicName")) == null) {
                throw new NullPointerException("topicName is " + config.getKafka().get("topicName"));
            }
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException("topicName : " + e);
        }
        try {
            if ((bootStrapServers = (String)config.getKafka().get("bootStrapServers")) == null) {
                throw new NullPointerException("bootStrapServers is " + config.getKafka().get("bootStrapServers"));
            }
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException("bootStrapServers : " + e);
        }
        try {
            if(config.getKafka().get("asyncLoading") == null) {
                throw new NullPointerException("asyncLoading is " + config.getKafka().get("asyncLoading"));
            }
            else {
                asyncLoading = (boolean)config.getKafka().get("asyncLoading");
            }
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException("asyncLoading : " + e);
        }

        /* query */
        try {
            if ((option = (String)config.getQuery().get("option")) == null) {
                throw new NullPointerException("option is " + config.getQuery().get("option"));
            }
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException("option : " + e);
        }

        /* data */
        try {
            if ((format = (String)config.getData().get("format")) == null) {
                throw new NullPointerException("format is " + config.getData().get("format"));
            }
            else {
                List<String> validParam = Arrays.asList("GeoJSON", "CSV");
                if (!validParam.contains(format)) {
                    throw new IllegalArgumentException(
                            "format is " + format + ". " +
                                    "Valid value is \"GeoJSON\" or \"CSV\".");
                }
            }
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException("format : " + e);
        }
        try {
            if ((dateFormat = (String)config.getData().get("dateFormat")) == null) {
                throw new NullPointerException("dateFormat is " + config.getData().get("dateFormat"));
            }
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException("dateFormat : " + e);
        }
        try {
            if(config.getData().get("nRows") == null) {
                throw new NullPointerException("nRows is " + config.getData().get("nRows"));
            }
            else {
                nRows = (int)config.getData().get("nRows");
            }
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException("nRows : " + e);
        }
        try {
            if(config.getData().get("generationRate") == null) {
                throw new NullPointerException("generationRate is " + config.getData().get("generationRate"));
            }
            else {
                generationRate = (int)config.getData().get("generationRate");
            }
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException("generationRate : " + e);
        }
        try {
            if ((bBox = (ArrayList)config.getData().get("bBox")) == null) {
                throw new NullPointerException("bBox is " + config.getData().get("bBox"));
            }
            if (bBox.size() != 4) {
                throw new IllegalArgumentException("bBox num is " + bBox.size());
            }
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException("bBox : " + e);
        }
        try {
            if ((varianceRange = (ArrayList)config.getData().get("varianceRange")) == null) {
                throw new NullPointerException("varianceRange is " + config.getData().get("varianceRange"));
            }
            if (varianceRange.size() != 2) {
                throw new IllegalArgumentException("varianceRange num is " + varianceRange.size());
            }
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException("varianceRange : " + e);
        }
        try {
            if ((nSidesRange = (ArrayList)config.getData().get("nSidesRange")) == null) {
                throw new NullPointerException("nSidesRange is " + config.getData().get("nSidesRange"));
            }
            if (nSidesRange.size() != 2) {
                throw new IllegalArgumentException("nSidesRange num is " + nSidesRange.size());
            }
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException("nSidesRange : " + e);
        }
        try {
            if ((nLineSegmentsRange = (ArrayList)config.getData().get("nLineSegmentsRange")) == null) {
                throw new NullPointerException("nLineSegmentsRange is " + config.getData().get("nLineSegmentsRange"));
            }
            if (nLineSegmentsRange.size() != 2) {
                throw new IllegalArgumentException("nLineSegmentsRange num is " + nLineSegmentsRange.size());
            }
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException("nLineSegmentsRange : " + e);
        }
        try {
            if ((objIDRange = (ArrayList)config.getData().get("objIDRange")) == null) {
                throw new NullPointerException("objIDRange is " + config.getData().get("objIDRange"));
            }
            if (objIDRange.size() != 2) {
                throw new IllegalArgumentException("objIDRange num is " + objIDRange.size());
            }
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException("objIDRange : " + e);
        }
    }

    private ConfigType getYamlConfig(String path) {
        File file = new File(path);
        Constructor constructor = new Constructor(ConfigType.class);
        Yaml yaml = new Yaml(constructor);
        FileInputStream input;
        InputStreamReader stream;
        try {
            input = new FileInputStream(file);
            stream = new InputStreamReader(input, "UTF-8");
            return (ConfigType) yaml.load(stream);
        }
        catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        return "topicName = " + topicName + ", " +
                "bootStrapServers = " + bootStrapServers + ", " +
                "asyncLoading = " + asyncLoading + ", " +
                "\n" +
                "option = " + option + ", " +
                "\n" +
                "format = " + format + ", " +
                "dateFormat = " + dateFormat + ", " +
                "nRows = " + nRows + ", " +
                "generationRate = " + generationRate + ", " +
                "bBox = " + bBox + ", " +
                "varianceRange = " + varianceRange + ", " +
                "\n";
    }
}