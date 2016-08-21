package pl.grydzluk.goeuro.component;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Stream;

/**
 * Created by luk on 2016-08-20.
 */
@Component
public class ConnectionStorage implements CommandLineRunner {

    private static final Logger LOGGER = LogManager.getLogger(ConnectionStorage.class);

    private Map<Integer,Set<Integer>> stationWithBusRouteMap = Maps.newHashMap();

    public void run(String... args) throws Exception {
        if (args.length > 0) {
            String pathToFile = args[0];
            LOGGER.info("Start loading buss routes with file {}", pathToFile);
            fillConnectionStorage(pathToFile);
            LOGGER.info("File successfully loaded");
        } else {
            LOGGER.warn("No file name given to fill ConnectionStorage!");
        }
    }

    public Set<Integer> getBusRoutes(Integer stationId) {
        return stationWithBusRouteMap.get(stationId);
    }

    private void fillConnectionStorage(String pathToFile) {
        try {
            Path filePath = Paths.get(pathToFile);
            Stream<String> fileLines = Files.lines(filePath);
            fileLines.skip(1).forEach(line -> processBusLine(line));
        } catch (Exception e) {
            LOGGER.error("Problem with loading file", e);
        }
    }

    private void processBusLine(String line) {
        if (Strings.isNotEmpty(line)) {
            StringTokenizer lineTokenizer = new StringTokenizer(line);
            Integer busRouteId = Integer.parseInt(lineTokenizer.nextToken());
            while (lineTokenizer.hasMoreTokens()) {
                Integer stationId = Integer.parseInt(lineTokenizer.nextToken());
                Set<Integer> busRouteForStation = stationWithBusRouteMap.get(stationId);
                if (busRouteForStation == null) {
                    busRouteForStation = Sets.newHashSet();
                    stationWithBusRouteMap.put(stationId, busRouteForStation);
                }
                busRouteForStation.add(busRouteId);
            }
        }
    }
}
