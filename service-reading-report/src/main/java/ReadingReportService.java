import br.com.kafka.alura.KafkaService;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ReadingReportService {

    private static final Path SOURCE = new File("src/main/resources/reports.txt").toPath();

    public static void main(String[] args) {

        var reportService = new ReadingReportService();
        try(var service = new KafkaService<>("readingReportService",
                "USER_GENERATE_READING_REPORT",
                reportService::parse,
                User.class,
                Map.of())){
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, User> record) {

        System.out.println("---------------------------");
        System.out.println("Processing report for " + record.value());

        var user = record.value();
        var target = new File(user.getReportPath());
        IO.copyTo(SOURCE, target);
    }
}





