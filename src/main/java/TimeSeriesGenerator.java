import org.javatuples.Pair;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TimeSeriesGenerator {

    private String topicName;
    private String bootStrapServers;
    private Boolean isAsync;

    public TimeSeriesGenerator(String topicName, String bootStrapServers, Boolean isAsync){
        this.topicName = topicName;
        this.bootStrapServers = bootStrapServers;
        this.isAsync = isAsync;
    }

    public void generate2DRandom(Integer numRows, Integer maxObjID, Double minX, Double minY, Double maxX, Double maxY, String dateFormat){

        KafkaProducer kafkaProducer = new KafkaProducer(topicName, bootStrapServers ,true);

        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateFormat);
            int lineCount = 0;
            // {"geometry": {"coordinates": [116.45115, 39.9068], "type": "Point"}, "properties": {"oID": "2792", "timestamp": "2008-02-03 15:01:16"}, "type": "Feature"}
            String line;
            while (lineCount < numRows) {
                double x = (Math.random() * (maxX - minX)) + minX;
                double y = (Math.random() * (maxY - minY)) + minY;
                int objID = (int) ((Math.random() * (maxObjID - 0)) + 0);
                String dateTimeString = dateTimeFormat.format(new Date());
                line = "{\"geometry\":{\"coordinates\":[" + x + "," + y + "], \"type\": \"Point\"}, \"properties\": {\"oID\":\"" + String.valueOf(objID) + "\", \"timestamp\":\"" + dateTimeString + "\"}, \"type\": \"Feature\"}";
                lineCount++;
                //System.out.println(line);
                kafkaProducer.sendMessage(lineCount + "", line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generate2DGaussian(Integer numRows, Integer maxObjID, Double minVariance, Double maxVariance, Double minX, Double minY, Double maxX, Double maxY, String dateFormat){

        KafkaProducer kafkaProducer = new KafkaProducer(topicName, bootStrapServers ,true);

        //Pair<Double, Double> point = new Pair<Double, Double>();
        Map<Integer, Pair> objIDMap = new HashMap();
        Random fRandom = new Random();

        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateFormat);
            int lineCount = 0;
            double x;
            double y;
            String line;
            // {"geometry": {"coordinates": [116.45115, 39.9068], "type": "Point"}, "properties": {"oID": "2792", "timestamp": "2008-02-03 15:01:16"}, "type": "Feature"}

            while (lineCount < numRows) {

                int objID = (int) (Math.random() * (maxObjID));
                org.javatuples.Pair<Double, Double> point = objIDMap.get(objID);

                if(point != null) { // if point exist, generate new point based on it
                    //System.out.println(point.getValue0());
                    //System.out.println(point.getValue1());

                    do {
                        double variance;
                        // generating random variance
                        variance = minVariance + new Random().nextDouble() * (maxVariance - minVariance);
                        x = point.getValue0() + fRandom.nextGaussian() * variance;
                        variance = minVariance + new Random().nextDouble() * (maxVariance - minVariance);
                        y = point.getValue1() + fRandom.nextGaussian() * variance;
                    } while(x < minX || x > maxX || y < minY || y > maxY);

                    objIDMap.put(objID, new org.javatuples.Pair(x,y));
                }
                else {
                    x = (Math.random() * (maxX - minX)) + minX;
                    y = (Math.random() * (maxY - minY)) + minY;

                    objIDMap.put(objID, new org.javatuples.Pair(x,y));
                }

                String dateTimeString = dateTimeFormat.format(new Date());
                line = "{\"geometry\":{\"coordinates\":[" + x + "," + y + "], \"type\": \"Point\"}, \"properties\": {\"oID\":\"" + String.valueOf(objID) + "\", \"timestamp\":\"" + dateTimeString + "\"}, \"type\": \"Feature\"}";
                lineCount++;
                //System.out.println(line);
                kafkaProducer.sendMessage(lineCount + "", line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
