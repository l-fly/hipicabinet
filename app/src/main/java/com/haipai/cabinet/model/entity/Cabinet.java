package com.haipai.cabinet.model.entity;

import java.util.ArrayList;
import java.util.List;

public class Cabinet {
    CcuEntity ccu = new CcuEntity();

    MeterEntity meter = new MeterEntity();
    List<PmsEntity> pmsList = new ArrayList();

    public CcuEntity getCcu() {
        return ccu;
    }

    public void setCcu(CcuEntity ccu) {
        this.ccu = ccu;
    }

    public MeterEntity getMeter() {
        return meter;
    }

    public void setMeter(MeterEntity meter) {
        this.meter = meter;
    }

    public List<PmsEntity> getPmsList() {
        return pmsList;
    }

    public void setPmsList(List<PmsEntity> pmsList) {
        this.pmsList = pmsList;
    }
}
