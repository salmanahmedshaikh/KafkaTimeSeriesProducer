package StreamGenerator.timeSeriesGenerators;

import StreamGenerator.kafka.KafkaProducer;
import StreamGenerator.utils.HelperClass;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LineStringStream extends TimeSeriesGenerator{

    private final int minLineStringSegments;
    private final int maxLineStringSegments;

    public LineStringStream(String topicName, String bootStrapServers, Boolean isAsync, int minLineStringSegments, int maxLineStringSegments) {
        super(topicName, bootStrapServers, isAsync);

        this.minLineStringSegments = minLineStringSegments;
        this.maxLineStringSegments = maxLineStringSegments;
    }

    @Override
    //public void randomLineStrings(int nRows, int minObjID, int maxObjID, int minLineStringSegments, int maxLineStringSegments, Envelope env, String dateFormat)
    public void random(int nRows, int minObjID, int maxObjID, Envelope env, String dateFormat) {

        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateFormat);
            int lineCount = 0;

            while (lineCount < nRows) {
                int nLineSegments = (int) HelperClass.getRandomDoubleInRange(minLineStringSegments, maxLineStringSegments);
                int objID = (int)HelperClass.getRandomDoubleInRange(minObjID, maxObjID);
                String dateTimeString = dateTimeFormat.format(new Date());

                List<Coordinate> lineStringCoord = HelperClass.generateRandomLineString(nLineSegments, env);
                assert lineStringCoord != null;

                //GeometryFactory geofact = new GeometryFactory();
                //LineString ls = geofact.createLineString(lineStringCoord.toArray(new Coordinate[0]));
                //System.out.println(ls);

                String line = HelperClass.generateLineStringJson(lineStringCoord, objID, dateTimeString).toString();

                lineCount++;
                System.out.println(line);
                //kafkaProducer.sendMessage(lineCount + "", line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void gaussian(int nRows, int minObjID, int maxObjID, Double minVariance, Double maxVariance, Envelope env, String dateFormat){

    }
}
