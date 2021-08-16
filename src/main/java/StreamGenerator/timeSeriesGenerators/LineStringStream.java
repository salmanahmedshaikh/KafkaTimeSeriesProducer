package StreamGenerator.timeSeriesGenerators;

import StreamGenerator.kafka.KafkaProducer;
import StreamGenerator.utils.HelperClass;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LineStringStream extends TimeSeriesGenerator{

    private final int minSegments;
    private final int maxSegments;

    public LineStringStream(String topicName, String bootStrapServers, Boolean isAsync) {
        super(topicName, bootStrapServers, isAsync);

        this.minSegments = NUM_OF_SEGMENTS;
        this.maxSegments = NUM_OF_SEGMENTS;
    }

    public LineStringStream(String topicName, String bootStrapServers, Boolean isAsync, int minLineStringSegments, int maxLineStringSegments) {
        super(topicName, bootStrapServers, isAsync);

        this.minSegments = minLineStringSegments;
        this.maxSegments = maxLineStringSegments;
    }

    @Override
    //public void randomLineStrings(int nRows, int minObjID, int maxObjID, int minLineStringSegments, int maxLineStringSegments, Envelope env, String dateFormat)
    public void random(int nRows, int minObjID, int maxObjID, Envelope seriesBBox, String dateFormat) {

        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateFormat);
            int rowCount = 0;

            while (rowCount < nRows) {
                int nLineSegments = (int) HelperClass.getRandomDoubleInRange(minSegments, maxSegments);
                int objID = (int)HelperClass.getRandomDoubleInRange(minObjID, maxObjID);
                String dateTimeString = dateTimeFormat.format(new Date());

                List<Coordinate> lineStringCoord = HelperClass.generateRandomLineString(nLineSegments, seriesBBox);
                assert lineStringCoord != null;

                //GeometryFactory geofact = new GeometryFactory();
                //LineString ls = geofact.createLineString(lineStringCoord.toArray(new Coordinate[0]));
                //System.out.println(ls);

                String row = HelperClass.generateLineStringJson(lineStringCoord, objID, dateTimeString).toString();

                rowCount++;
                System.out.println(row);
                //kafkaProducer.sendMessage(lineCount + "", row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void gaussian(int nRows, int minObjID, int maxObjID, Envelope seriesBBox, String dateFormat, double minSeriesVar, double maxSeriesVar){

    }
}
