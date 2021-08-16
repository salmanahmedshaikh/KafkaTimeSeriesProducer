package StreamGenerator.timeSeriesGenerators;

//import jdk.internal.util.xml.impl.Pair;
import StreamGenerator.kafka.KafkaProducer;
import StreamGenerator.utils.HelperClass;
import org.locationtech.jts.geom.*;

import java.text.SimpleDateFormat;
import java.util.*;

public abstract class TimeSeriesGenerator {

    protected final int NUM_OF_SEGMENTS = 3;
    protected final double SEGMENT_LENGTH_VARIANCE = 0.0001;
    protected final int NUM_OF_POLYGON_HOLES = 0;

    protected final String topicName;
    protected final String bootStrapServers;
    protected final Boolean isAsync;
    protected final KafkaProducer kafkaProducer;

    protected TimeSeriesGenerator(String topicName, String bootStrapServers, Boolean isAsync){
        this.topicName = topicName;
        this.bootStrapServers = bootStrapServers;
        this.isAsync = isAsync;
        this.kafkaProducer = new KafkaProducer(topicName, bootStrapServers, isAsync);
    }

    public abstract void random(int nRows, int minObjID, int maxObjID, Envelope env, String dateFormat);
    public abstract void gaussian(int nRows, int minObjID, int maxObjID, Envelope seriesBBox, String dateFormat, double minSeriesVar, double maxSeriesVar);

}
