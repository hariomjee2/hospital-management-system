package com.hms.backend.modules.ipd.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class WardResponseDto {
    private UUID id;
    private String wardName;
    private String wardType;
    private BigDecimal dailyRate;
    private Integer totalBeds;
    private Long availableBedsCount;
    private Long occupiedBedsCount;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    public String getWardType() {
        return wardType;
    }

    public void setWardType(String wardType) {
        this.wardType = wardType;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public Integer getTotalBeds() {
        return totalBeds;
    }

    public void setTotalBeds(Integer totalBeds) {
        this.totalBeds = totalBeds;
    }

    public Long getAvailableBedsCount() {
        return availableBedsCount;
    }

    public void setAvailableBedsCount(Long availableBedsCount) {
        this.availableBedsCount = availableBedsCount;
    }

    public Long getOccupiedBedsCount() {
        return occupiedBedsCount;
    }

    public void setOccupiedBedsCount(Long occupiedBedsCount) {
        this.occupiedBedsCount = occupiedBedsCount;
    }
}
