import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.sql.Timestamp;

public class KafkaStreamPlanned {

    private Logger log = LoggerFactory.getLogger(KafkaProducerPlanned.class);
    private Properties props;
    private StreamsBuilder builder;

    public KafkaStreamPlanned() {
        //Construktor - Data for Kafka Server
        props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:29092");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "StreamPlanned");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        builder = new StreamsBuilder();
    }

    public static List<String> convertString(String s){
        //<s> and </s> marks the beginning and end of one train
        //the string gets split by </s> so there is one string per train
        List<String> trainList = new ArrayList<String>();
        String[] strings = s.split("</s>");
        String train = "";
        boolean dateWritten = false;
        for (int i = 0; i < strings.length- 1; i++){
            //for every string data will be filtered. Only train id, name, type, arrival time and departure time are needed.
            train = "id=" + strings[i].substring(strings[i].indexOf("id=\"")+4, strings[i].indexOf("\"", strings[i].indexOf("id=\"")+4)) + ";";
            train += "type=" + strings[i].substring(strings[i].indexOf("c=\"")+3, strings[i].indexOf("\"", strings[i].indexOf("c=\"")+3));
            train += strings[i].substring(strings[i].indexOf("l=\"")+3, strings[i].indexOf("\"", strings[i].indexOf("l=\"")+3)) + ";";
            if (strings[i].contains("<ar")) {
                //only if there is arrival time (some trains start in that station, so they do not have arrival time
                //else write NULL
                train += "date=" + strings[i].substring(strings[i].indexOf("ar pt=") + 7, strings[i].indexOf("ar pt=") + 13) + ";";
                train += "ar=" + strings[i].substring(strings[i].indexOf("ar pt=") + 13, strings[i].indexOf("ar pt=") + 17) + ";";
                dateWritten = true;
            }
            if (strings[i].contains("<dp")){
                //only if there is departure time (some trains end in that station, so they do not have departure time
                //else write NULL
                if (!dateWritten) {
                    train += "date=" + strings[i].substring(strings[i].indexOf("dp pt=") + 7, strings[i].indexOf("dp pt=")+13) + ";";
                    train += "ar=NULL;";
                }
                train += "dp=" + strings[i].substring(strings[i].indexOf("dp pt=") + 13, strings[i].indexOf("dp pt=")+17) + ";";
            }else {
                train += "dp=NULL;";
            }
            dateWritten = false;
            trainList.add(train);
        }
        System.out.println(new Timestamp(System.currentTimeMillis()) + ": Converted new Stream into " + trainList.size() + " new messages.");
        return trainList;
    }

    private void stream() {
        //Take stream from raw data Kafka topic and write new stream for new data topic
        //Creates multiple Messages from one Message (raw data contains all trains per hour in one message -> new data has one message per train)
        KStream<String, String> rawData = builder.stream("RawDataPlanned");
        KStream<String, String> newData = rawData.flatMapValues(s -> convertString(s));
        newData.to("NewDataPlanned", Produced.with(Serdes.String(), Serdes.String()));
        KafkaStreams stream = new KafkaStreams(builder.build(), props);
        stream.start();
    }

    public static void main(String... args) {
        KafkaStreamPlanned stream = new KafkaStreamPlanned();
        stream.stream();
    }
}
