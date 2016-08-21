package pl.grydzluk.goeuro.component

import pl.grydzluk.goeuro.Utils
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by luk on 2016-08-20.
 */
@Unroll
class ConnectionStorageTest extends Specification {

    def testedObject = new ConnectionStorage()

    def "Should load connections"() {
        when:
            testedObject.run(args)

        then:
            testedObject.stationWithBusRouteMap.size() == expectedStationWithBusRoutesSize

        where:
            args                                                                             | expectedStationWithBusRoutesSize
            Utils.getFilePathFromResources("CorrectInputFile.txt")                           | 3
            [Utils.getFilePathFromResources("CorrectInputFile.txt"), "abc"].toArray(new String[0]) | 3
            Utils.getFilePathFromResources("EmptyInputFile.txt")                                   | 0
            Utils.getFilePathFromResources("WrongInputFile.txt")                                   | 0
            [].toArray(new String[0])                                                        | 0
    }

    def "Should load 3 stations: first(0) with bus route 0, second(1) with bus routes 0 and 1, third(2) with bus routes 0 and 1"() {
        when:
            testedObject.run(Utils.getFilePathFromResources("CorrectInputFile.txt"))

        then:
            testedObject.stationWithBusRouteMap.get(0).containsAll([0]) == true
            testedObject.stationWithBusRouteMap.get(1).containsAll([0,1]) == true
            testedObject.stationWithBusRouteMap.get(2).containsAll([0,1]) == true
    }

    def "Should keep alive if file not found"() {
        when:
            testedObject.run("noSuchFile.txt")

        then:
            testedObject.stationWithBusRouteMap.size() == 0
    }

    def "Should limit loaded lines"() {
        given:
            String file = Utils.getFilePathFromResources("FileWithMoreLinesThanExpected.txt")

        when:
            def resultMap = testedObject.parseFileToStationWithBusRouteMap(file)

        then:
            resultMap.size() == 3
    }

    def "Should not add line cause not enough tokens"() {
        given:
            def line = "1 2"
            def busRouteIds = ([] as Set)
            def resultMap = [:]

        when:
            testedObject.processBusLine(line, busRouteIds, resultMap)

        then:
            busRouteIds.size() == 0
            resultMap.size() == 0
    }

    def "Should not add line cause invalid bus route id"() {
        given:
            def line = "a 1 2 3"
            def busRouteIds = ([] as Set)
            def resultMap = [:]

        when:
            testedObject.processBusLine(line, busRouteIds, resultMap)

        then:
            busRouteIds.size() == 0
            resultMap.size() == 0
    }

    def "Should add line"() {
        given:
        def line = "0 1 2 3"
        def busRouteIds = ([] as Set)
        def resultMap = [:]

        when:
        testedObject.processBusLine(line, busRouteIds, resultMap)

        then:
        busRouteIds.size() == 1
        resultMap.size() == 3
    }

    def "Should skip station invalid id"() {
        given:
            def stationsTokenizer = new StringTokenizer("0 a 2 3")
            def busRouteId = 1

        when:
            def stationIds = testedObject.convertToStationIdSet(stationsTokenizer, busRouteId)

        then:
            stationIds.size() == 3
            stationIds.containsAll([0,2,3])
    }

    def "Should skip station id cause exists twice"() {
        given:
        def stationsTokenizer = new StringTokenizer("0 1 2 2")
        def busRouteId = 1

        when:
        def stationIds = testedObject.convertToStationIdSet(stationsTokenizer, busRouteId)

        then:
        stationIds.size() == 3
        stationIds.containsAll([0,1,2])
    }

    def "Should add all station ids"() {
        given:
            def stationsTokenizer = new StringTokenizer("0 1 2 3")
            def busRouteId = 1

        when:
            def stationIds = testedObject.convertToStationIdSet(stationsTokenizer, busRouteId)

        then:
            stationIds.size() == 4
            stationIds.containsAll([0,1,2,3])
    }

    def "Should add new stations to map"() {
        given:
            def stationIds = ([1,2,3] as Set)
            def busRouteId = 0
            def resultMap = [4:([1] as Set)]

        when:
            testedObject.fillResultMap(resultMap, busRouteId, stationIds)

        then:
            resultMap.size() == 4
    }

    def "Should not add new stations to map"() {
        given:
            def stationIds = ([1,2] as Set)
            def busRouteId = 0
            def resultMap = [1:([1] as Set), 2:([1] as Set)]

        when:
            testedObject.fillResultMap(resultMap, busRouteId, stationIds)

        then:
            resultMap.size() == 2
    }

    def "Should return correct set of bus routes"() {
        given:
            Map map = Stub()
            map.get(1) >> ([1,2,3] as Set)
            map.get(3) >> null
            testedObject.stationWithBusRouteMap = map

        expect:
            testedObject.getBusRoutes(stationId) == expectedBusRoutes

        where:
            stationId | expectedBusRoutes
            1         | [1,2,3] as Set
            3         | null
    }
}