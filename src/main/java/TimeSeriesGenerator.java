import org.javatuples.Pair;

import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;

import java.text.SimpleDateFormat;
import java.util.*;

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
//                line = "{\"geometry\":{\"coordinates\":[" + x + "," + y + "], \"type\": \"Point\"}, \"properties\": {\"oID\":\"" + String.valueOf(objID) + "\", \"timestamp\":\"" + dateTimeString + "\"}, \"type\": \"Feature\"}";
                line = generatePointJson(x, y, objID, dateTimeString).toString();
                lineCount++;
                System.out.println(line);
                //kafkaProducer.sendMessage(lineCount + "", line);
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
	
    private JSONObject generatePointJson(double x, double y, int objID, String dateTimeString) {
        JSONObject jsonObj = new JSONObject();

        JSONObject jsonGeometry = new JSONObject();
        double[] coordinate = {x, y};
        jsonGeometry.put("coordinates", coordinate);
        jsonGeometry.put("type", "Point");
        jsonObj.put("geometry", jsonGeometry);

        JSONObject jsonpProperties = new JSONObject();
        jsonpProperties.put("oID", String.valueOf(objID));
        jsonpProperties.put("timestamp", dateTimeString);
        jsonObj.put("properties", jsonpProperties);

        jsonObj.put("type", "Feature");
        return jsonObj;
    }

    private JSONObject generatePolygonJson(List<List<Coordinate>> nestedCoordinateList, int objID, String dateTimeString) {
        JSONObject jsonObj = new JSONObject();

        JSONObject jsonGeometry = new JSONObject();
        List<List<double[]>> jsonCoordinate = new ArrayList<List<double[]>>();
        for (List<Coordinate> polygonCoordinates : nestedCoordinateList) {
            List<double[]> coordinates = new ArrayList<double[]>();
            for (Coordinate c : polygonCoordinates) {
                double[] coordinate = {c.x, c.y};
                coordinates.add(coordinate);
            }
            jsonCoordinate.add(coordinates);
        }
        jsonGeometry.put("type", "Polygon");
        jsonGeometry.put("coordinates", jsonCoordinate);
        jsonObj.put("geometry", jsonGeometry);

        JSONObject jsonpProperties = new JSONObject();
        jsonpProperties.put("oID", String.valueOf(objID));
        jsonpProperties.put("timestamp", dateTimeString);
        jsonObj.put("properties", jsonpProperties);

        jsonObj.put("type", "Feature");
        return jsonObj;
    }

    private JSONObject generateMultiPolygonJson(List<List<List<Coordinate>>> nestedCoordinateList, int objID, String dateTimeString) {
        JSONObject jsonObj = new JSONObject();

        JSONObject jsonGeometry = new JSONObject();
        List<List<List<double[]>>> jsonCoordinate = new ArrayList<List<List<double[]>>>();
        for (List<List<Coordinate>> listCoordinate : nestedCoordinateList) {
            List<List<double[]>> coordinates = new ArrayList<>();
            for (List<Coordinate> l : listCoordinate) {
                List<double[]> arrCoordinate = new ArrayList<double[]>();
                for (Coordinate c : l) {
                    double[] coordinate = {c.x, c.y};
                    arrCoordinate.add(coordinate);
                }
                coordinates.add(arrCoordinate);
            }
            jsonCoordinate.add(coordinates);
        }
        jsonGeometry.put("type", "MultiPolygon");
        jsonGeometry.put("coordinates", jsonCoordinate);
        jsonObj.put("geometry", jsonGeometry);

        JSONObject jsonpProperties = new JSONObject();
        jsonpProperties.put("oID", String.valueOf(objID));
        jsonpProperties.put("timestamp", dateTimeString);
        jsonObj.put("properties", jsonpProperties);

        jsonObj.put("type", "Feature");
        return jsonObj;
    }

    private JSONObject generateLineStringJson(List<Coordinate> nestedCoordinateList, int objID, String dateTimeString) {
        JSONObject jsonObj = new JSONObject();

        JSONObject jsonGeometry = new JSONObject();
        List<double[]> jsonCoordinate = new ArrayList<double[]>();
        for (Coordinate c : nestedCoordinateList) {
            double[] coordinate = {c.x, c.y};
            jsonCoordinate.add(coordinate);
        }
        jsonGeometry.put("type", "LineString");
        jsonGeometry.put("coordinates", jsonCoordinate);
        jsonObj.put("geometry", jsonGeometry);

        JSONObject jsonpProperties = new JSONObject();
        jsonpProperties.put("oID", String.valueOf(objID));
        jsonpProperties.put("timestamp", dateTimeString);
        jsonObj.put("properties", jsonpProperties);

        jsonObj.put("type", "Feature");
        return jsonObj;
    }

    private JSONObject generateMultiLineStringJson(List<List<Coordinate>> nestedCoordinateList, int objID, String dateTimeString) {
        JSONObject jsonObj = new JSONObject();

        JSONObject jsonGeometry = new JSONObject();
        List<List<double[]>> jsonCoordinate = new ArrayList<>();
        for (List<Coordinate> l : nestedCoordinateList) {
            List<double[]> arrCoordinate = new ArrayList<>();
            for (Coordinate c : l) {
                double[] coordinate = {c.x, c.y};
                arrCoordinate.add(coordinate);
            }
            jsonCoordinate.add(arrCoordinate);
        }
        jsonGeometry.put("type", "MultiLineString");
        jsonGeometry.put("coordinates", jsonCoordinate);
        jsonObj.put("geometry", jsonGeometry);

        JSONObject jsonpProperties = new JSONObject();
        jsonpProperties.put("oID", String.valueOf(objID));
        jsonpProperties.put("timestamp", dateTimeString);
        jsonObj.put("properties", jsonpProperties);

        jsonObj.put("type", "Feature");
        return jsonObj;
    }
}
