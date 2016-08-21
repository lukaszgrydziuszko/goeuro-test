package pl.grydzluk.goeuro.service

import pl.grydzluk.goeuro.component.ConnectionStorage
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by luk on 2016-08-20.
 */
class ConnectionServiceTest extends Specification {

    @Shared
    def testedObject = new ConnectionService()

    def setup() {
        ConnectionStorage connectionStorage = Mock()
        testedObject.connectionStorage = connectionStorage

        connectionStorage.getBusRoutes(1) >> null
        connectionStorage.getBusRoutes(2) >> ([1,2,3] as Set)
        connectionStorage.getBusRoutes(3) >> ([1,3,4] as Set)
        connectionStorage.getBusRoutes(4) >> ([4,5] as Set)
    }

    def "Should return direct connection details"() {
        when:
            def directResponse = testedObject.checkDirectConnection(departureId, arrivalId)

        then:
            directResponse.departureId == returnedDepartureId
            directResponse.arrivalId == returnedArrivalId
            directResponse.exist == returnedExist

        where:
            departureId | arrivalId | returnedDepartureId | returnedArrivalId | returnedExist
            1           | 2         | 1                   | 2                 | false
            3           | 4         | 3                   | 4                 | true
    }

    def "Should return true for direct connection"() {
        expect:
            testedObject.isDirectConnection(departureId, arrivalId) == directConnection

        where:
            departureId | arrivalId | directConnection
            1           | 4         | false
            4           | 1         | false
            2           | 3         | true
            3           | 2         | true
            2           | 4         | false
            3           | 4         | true
    }
}