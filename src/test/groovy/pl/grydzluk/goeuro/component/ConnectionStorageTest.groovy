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