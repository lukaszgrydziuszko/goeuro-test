package pl.grydzluk.goeuro.component;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
            Map<Integer,Set<Integer>> stationWithBusRouteMap = parseFileToStationWithBusRouteMap(pathToFile);
            this.stationWithBusRouteMap = stationWithBusRouteMap;
        } else {
            LOGGER.warn("No file name given to fill ConnectionStorage!");
        }
    }

    public Set<Integer> getBusRoutes(Integer stationId) {
        return stationWithBusRouteMap.get(stationId);
    }

    private Map<Integer,Set<Integer>> parseFileToStationWithBusRouteMap(String pathToFile) {
        Map<Integer,Set<Integer>> resultMap = Maps.newHashMap();
        try {
            Path filePath = Paths.get(pathToFile);
            Integer busRoutesNumber = getBusRoutesNumber(filePath);
            if (busRoutesNumber == null) {
                LOGGER.error("Problem while parsing first file line. No configuration loaded.");
            } else {
                Set<Integer> busRouteIds = Sets.newHashSet();
                Long countBusRoutesInFile = Files.lines(filePath).skip(1).count();
                Stream<String> fileLines = Files.lines(filePath).skip(1);

                if (countBusRoutesInFile < busRoutesNumber) {
                    LOGGER.warn("File contains {} bus routes, while expected {}. All line will be loaded.", countBusRoutesInFile, busRoutesNumber);
                } else if (countBusRoutesInFile > busRoutesNumber) {
                    LOGGER.warn("File contains {} bus routes, while expected {}. Extra line will be skipped.", countBusRoutesInFile, busRoutesNumber);
                    fileLines = fileLines.limit(busRoutesNumber);
                } else {
                    LOGGER.info("File contains {} bus routes.", busRoutesNumber);
                }

                fileLines.forEach(line -> processBusLine(line, busRouteIds, resultMap));
                LOGGER.info("File loaded");
            }
        } catch (Exception e) {
            LOGGER.error("Problem with loading file", e);
        }
        return resultMap;
    }

    private Integer getBusRoutesNumber(Path filePath) throws IOException {
        Optional<String> firstLineOptional = Files.lines(filePath).findFirst();
        String firstFileLine = firstLineOptional.isPresent() ? firstLineOptional.get() : null;
        return parseInteger(firstFileLine);
    }

    private void processBusLine(String line, Set<Integer> busRouteIds, Map<Integer, Set<Integer>> resultMap) {
        if (Strings.isNotEmpty(line)) {
            StringTokenizer lineTokenizer = new StringTokenizer(line);
            Integer countElementsInLine = lineTokenizer.countTokens();
            if (countElementsInLine < 3) {
                LOGGER.error("Line '{}' has less than 3 element. Line will be skipped", line);
            } else {
                Integer busRouteId = parseInteger(lineTokenizer.nextToken());
                if (busRouteId == null) {
                    LOGGER.error("Unable to parse bus route id. Line: '{}' will be skipped", line);
                } else if (busRouteIds.contains(busRouteId)) {
                    LOGGER.error("Bus route id {} is already loaded. Line will be skipped", busRouteId);
                } else {
                    busRouteIds.add(busRouteId);
                    Set<Integer> stationIds = convertToStationIdSet(lineTokenizer, busRouteId);
                    fillResultMap(resultMap, busRouteId, stationIds);
                }
            }
        }
    }

    private Set<Integer> convertToStationIdSet(StringTokenizer lineTokenizer, Integer busRouteId) {
        Set<Integer> stationIds = Sets.newHashSet();
        while (lineTokenizer.hasMoreTokens()) {
            String stationIdString = lineTokenizer.nextToken();
            Integer stationId = parseInteger(stationIdString);
            if (stationId == null) {
                LOGGER.error("Unable to parse station id {} for bus route id {}", stationIdString, busRouteId);
            } else if (stationIds.contains(stationIds)) {
                LOGGER.warn("Station {} exists more than one time for bus route id {}", stationId, busRouteId);
            } else {
                stationIds.add(stationId);
            }
        }
        return stationIds;
    }

    private void fillResultMap(Map<Integer, Set<Integer>> resultMap, Integer busRouteId, Set<Integer> stationIds) {
        for (Integer stationId : stationIds) {
            Set<Integer> busRoutesForStation = resultMap.get(stationId);
            if (busRoutesForStation == null) {
                busRoutesForStation = Sets.newHashSet();
                resultMap.put(stationId, busRoutesForStation);
            }
            busRoutesForStation.add(busRouteId);
        }
    }

    private Integer parseInteger(String integer) {
        try {
            return Integer.parseInt(integer);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
