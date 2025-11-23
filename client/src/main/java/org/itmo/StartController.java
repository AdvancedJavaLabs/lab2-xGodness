package org.itmo;

import lombok.extern.slf4j.Slf4j;
import org.itmo.broker.BrokerReader;
import org.itmo.broker.BrokerWriter;
import org.itmo.util.EnvReader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/start")
@Slf4j
public class StartController {
    private static final String START_WRITE_QUEUE = EnvReader.readEnvVar("CLIENT_START_WRITE_QUEUE");
    private static final String TIME_INFO_READ_QUEUE = EnvReader.readEnvVar("CLIENT_TIME_INFO_READ_QUEUE");
    private static final String TIME_INFO_WRITE_QUEUE = EnvReader.readEnvVar("CLIENT_TIME_INFO_WRITE_QUEUE");

    private static final BrokerWriter brokerStartWriter = new BrokerWriter(START_WRITE_QUEUE);
    private static final BrokerReader brokerTimeInfoReader = new BrokerReader(TIME_INFO_READ_QUEUE);
    private static final BrokerWriter brokerTimeInfoWriter = new BrokerWriter(TIME_INFO_WRITE_QUEUE);

    @GetMapping
    public void start() {
        log.info("Starting...");

        long startTime = System.currentTimeMillis();
        brokerStartWriter.publish("/start");

        String message = brokerTimeInfoReader.consume(String.class);
        while (message == null || !message.equals("/done")) {
            message = brokerTimeInfoReader.consume(String.class);
        }

        long endTime = System.currentTimeMillis();
        brokerTimeInfoWriter.publish(new long[]{startTime, endTime});

        log.info("Completed");

        brokerStartWriter.shutdown();
        brokerTimeInfoReader.shutdown();
        brokerTimeInfoWriter.shutdown();
    }
}
