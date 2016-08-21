package pl.grydzluk.goeuro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by luk on 2016-08-20.
 */
public class DirectResponseDto {

    @JsonProperty("dep_sid")
    private Integer departureId;
    @JsonProperty("arr_sid")
    private Integer arrivalId;
    @JsonProperty("direct_bus_route")
    private boolean exist;

    public Integer getDepartureId() {
        return departureId;
    }

    public void setDepartureId(Integer departureId) {
        this.departureId = departureId;
    }

    public Integer getArrivalId() {
        return arrivalId;
    }

    public void setArrivalId(Integer arrivalId) {
        this.arrivalId = arrivalId;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }
}
