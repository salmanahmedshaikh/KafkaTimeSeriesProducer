/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package StreamGenerator;

import StreamGenerator.geometryGenerator.GeometryGenerator;
import StreamGenerator.geometryGenerator.PolygonGenerator;
import StreamGenerator.inputParameters.Params;
import StreamGenerator.timeSeriesGenerators.LineStringStream;
import StreamGenerator.timeSeriesGenerators.PointStream;
import StreamGenerator.timeSeriesGenerators.PolygonStream;
import StreamGenerator.timeSeriesGenerators.TimeSeriesGenerator;
import StreamGenerator.utils.ConvexPolygonGenerator;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;

import java.awt.geom.Point2D;
import java.util.List;

public class mainClass extends Thread {

    //private static final String topicName = "allDetections_rep1_par8";
    //public static final String folderName = "/home/ubuntu/data/prediction_stream/";
    //public static final String folderName = "/mnt/NFS/gpfs1/projects/g-nedo-dprt/salman/data/prediction_stream";
    //private static final String topicName = "test";
    //public static final String folderName = "/home/salman/Data/prediction_stream/";

    public static void main(String [] args){

        Params params = new Params();
        System.out.println(params);

        String queryOption = params.option;
        String topicName = params.topicName;
        String bootStrapServers = params.bootStrapServers;
        Boolean isAsync = params.asyncLoading;
        int numRows = params.nRows;
        double minX = params.bBox.get(0);
        double minY = params.bBox.get(1);
        double maxX = params.bBox.get(2);
        double maxY = params.bBox.get(3);
        Envelope env = new Envelope(minX, maxX, minY, maxY);
        int minObjID = params.objIDRange.get(0);
        int maxObjID = params.objIDRange.get(1);
        String dateTimeFormat = params.dateFormat;
        double minSeriesVar = params.varianceRange.get(0);
        double maxSeriesVar = params.varianceRange.get(1);
        int minPolygonSides = params.nSidesRange.get(0);
        int maxPolygonSides = params.nSidesRange.get(1);
        int minLineStringSegments = params.nLineSegmentsRange.get(0);
        int maxLineStringSegments = params.nLineSegmentsRange.get(1);

        //SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long startTime = System.nanoTime();

        PointStream pointStream = new PointStream(topicName, bootStrapServers, isAsync);
        PolygonStream polygonStream = new PolygonStream(topicName, bootStrapServers, isAsync, minPolygonSides, maxPolygonSides, 0.0005, 0.005);
        LineStringStream lineStringStream = new LineStringStream(topicName, bootStrapServers, isAsync, minLineStringSegments, maxLineStringSegments);

        //System.out.println(ConvexPolygonGenerator.generateRandomConvexPolygon(5));

        GeometryFactory geometryFactory = new GeometryFactory();
        PolygonGenerator polygonGenerator = GeometryGenerator.createPolygonGenerator();
        polygonGenerator.setGeometryFactory(geometryFactory);
        polygonGenerator.setBoundingBox(env);
        polygonGenerator.setNumberPoints(9);
        polygonGenerator.setNumberHoles(0);
        //polygonGenerator.setGenerationAlgorithm(0);
        System.out.println(polygonGenerator.create());


        switch(queryOption) {
            case "randomPoints": {
                pointStream.random(numRows, minObjID, maxObjID, env, dateTimeFormat);
                break;
            }
            case "gaussianPoints": {
                pointStream.gaussian(numRows, minObjID, maxObjID, env, dateTimeFormat, minSeriesVar, maxSeriesVar);
                break;
            }
            case "randomLineStrings": {
                lineStringStream.random(numRows, minObjID, maxObjID, env, dateTimeFormat);
                break;
            }
            case "randomPolygons": {
                polygonStream.random(numRows, minObjID, maxObjID, env, dateTimeFormat);
                break;
            }
            case "gaussianPolygons": {
                polygonStream.gaussian(numRows, minObjID, maxObjID, env, dateTimeFormat, minSeriesVar, maxSeriesVar);
                break;
            }
            default:
                System.out.println("Unrecognized query option. Please input the appropriate query option.");


        }

        long endTime = System.nanoTime();
        // get difference of two nanoTime values
        long timeElapsed = endTime - startTime;
        System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000);
    }
}


