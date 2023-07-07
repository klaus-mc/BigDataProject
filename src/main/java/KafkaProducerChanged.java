import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;



import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaProducerChanged implements Runnable{


    private Producer<String, String> producer;
    private int stationEvaNumber;
    private String urlString;

    public KafkaProducerChanged(){
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "127.0.0.1:29092");
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());
        properties.setProperty("acks","all");
        properties.setProperty("retries", "3");
        stationEvaNumber = 8000039; //Eva number for Binge (Rein) Hbf
        //Api url for timetable information
        //Missing in url: station id (eva number) / date (YYMMDD) / hour slot (HH)
        urlString = "https://apis.deutschebahn.com/db-api-marketplace/apis/timetables/v1/rchg/" + stationEvaNumber;
        this.producer = new KafkaProducer<>(properties);
    }

    //Pulls data from db api for Changes in the trains timing
    //This pull happens every 2 minutes time per hour
    //Data is sent to kafka cluster into topic "RawDataChanged"
    public void produce() throws ExecutionException, InterruptedException {
        long counter = 0;
        while(true) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {
                    connection.setRequestProperty("DB-Api-Key", "eb26b678d3aa1f05b2f753baea6bc4d8");
                    connection.setRequestProperty("DB-Client-Id", "9371d6f8fd0cf50d5a0e8ebe4850ded6");
                    InputStream responseStream;
                    StringBuilder textBuilder;
                    responseStream = connection.getInputStream();
                    textBuilder = new StringBuilder();
                    try (Reader reader = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
                        int c = 0;
                        while ((c = reader.read()) != -1) {
                            textBuilder.append((char) c);
                        }
                    }
                    ProducerRecord<String, String> producerRecord = new ProducerRecord<>("RawDataChanged", textBuilder+"");
                    Future<RecordMetadata> record = producer.send(producerRecord);
                    System.out.println(new Timestamp(System.currentTimeMillis()) + ": Sent Msg Nr: " + counter + " Connection Response: " + connection.getResponseCode() + " Message: " + textBuilder);
                    counter++;
                    connection.disconnect();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    connection.disconnect();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void run() {

    }

    public static void main(String[] args){

        try {
            System.out.println("Kafka Producer Changed started.");
            KafkaProducerChanged producer = new KafkaProducerChanged();
            producer.produce();
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
