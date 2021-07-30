public class KafkaTimeSeriesProducer extends Thread {

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
        Integer numRows = params.numberOfRows;
        Double minX = params.bBox.get(0);
        Double minY = params.bBox.get(1);
        Double maxX = params.bBox.get(2);
        Double maxY = params.bBox.get(3);
        Integer maxObjID = params.numberOfObjects;
        String dateTimeFormat = params.dateFormat;
        Double minVariance = params.varianceRange.get(0);
        Double maxVariance = params.varianceRange.get(1);
        //SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        long startTime = System.nanoTime();

        TimeSeriesGenerator timeSeriesGenerator = new TimeSeriesGenerator(topicName, bootStrapServers, isAsync);

        switch(queryOption) {
            case "random": {
                timeSeriesGenerator.generate2DRandom(numRows, maxObjID, minX, minY, maxX, maxY, dateTimeFormat);
                break;
            }
            case "gaussian": {
                timeSeriesGenerator.generate2DGaussian(numRows, maxObjID, minVariance, maxVariance, minX, minY, maxX, maxY, dateTimeFormat);
                break;
            }
        }

        long endTime = System.nanoTime();
        // get difference of two nanoTime values
        long timeElapsed = endTime - startTime;
        System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000);
    }
}


