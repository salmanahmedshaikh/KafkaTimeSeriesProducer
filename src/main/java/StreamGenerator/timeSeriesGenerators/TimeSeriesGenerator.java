package StreamGenerator.timeSeriesGenerators;

//import jdk.internal.util.xml.impl.Pair;
import StreamGenerator.kafka.KafkaProducer;
import StreamGenerator.utils.HelperClass;
import org.locationtech.jts.geom.*;

import java.text.SimpleDateFormat;
import java.util.*;

public abstract class TimeSeriesGenerator {

    public final String topicName;
    public final String bootStrapServers;
    public final Boolean isAsync;
    public final KafkaProducer kafkaProducer;

    public TimeSeriesGenerator(String topicName, String bootStrapServers, Boolean isAsync){
        this.topicName = topicName;
        this.bootStrapServers = bootStrapServers;
        this.isAsync = isAsync;
        this.kafkaProducer = new KafkaProducer(topicName, bootStrapServers, isAsync);
    }

    public abstract void random(int nRows, int minObjID, int maxObjID, Envelope env, String dateFormat);
    public abstract void gaussian(int nRows, int minObjID, int maxObjID, Double minVariance, Double maxVariance, Envelope env, String dateFormat);

}
