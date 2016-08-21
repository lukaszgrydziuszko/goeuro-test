package pl.grydzluk.goeuro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pl.grydzluk.goeuro.component.ConnectionStorage;
import pl.grydzluk.goeuro.dto.DirectResponseDto;

import java.util.Set;

/**
 * Created by luk on 2016-08-20.
 */
@Service
public class ConnectionService {

    @Autowired
    ConnectionStorage connectionStorage;

    public DirectResponseDto checkDirectConnection(Integer departureId, Integer arrivalId) {
        DirectResponseDto result = new DirectResponseDto();
        result.setDepartureId(departureId);
        result.setArrivalId(arrivalId);
        result.setExist(isDirectConnection(departureId, arrivalId));
        return result;
    }

    private boolean isDirectConnection(Integer departureId, Integer arrivalId) {
        Set<Integer> departureStationBusRoutes = connectionStorage.getBusRoutes(departureId);
        Set<Integer> arrivalStationBusRoutes = connectionStorage.getBusRoutes(arrivalId);
        if (CollectionUtils.isEmpty(departureStationBusRoutes)
                || CollectionUtils.isEmpty(arrivalStationBusRoutes)) {
            return false;
        }
        return departureStationBusRoutes.stream().anyMatch(arrivalStationBusRoutes::contains);
    }
}
