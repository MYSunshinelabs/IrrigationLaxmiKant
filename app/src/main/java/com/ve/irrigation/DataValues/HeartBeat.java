package com.ve.irrigation.DataValues;

public class HeartBeat {
    private String valve1;

    private String v1vol;

    private String press;

    private String rgbled;

    private String ts;

    private String ttvol;

    private String devid;

    private String pump;

    private String flow;

    private String mid;

    private String rqdvol;

    private String omode;

    public String getValve1() {
        return valve1;
    }

    public void setValve1(String valve1) {
        this.valve1 = valve1;
    }

    public String getV1vol() {
        return v1vol;
    }

    public void setV1vol(String v1vol) {
        this.v1vol = v1vol;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public String getRgbled() {
        return rgbled;
    }

    public void setRgbled(String rgbled) {
        this.rgbled = rgbled;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getTtvol() {
        return ttvol;
    }

    public void setTtvol(String ttvol) {
        this.ttvol = ttvol;
    }

    public String getDevid() {
        return devid;
    }

    public void setDevid(String devid) {
        this.devid = devid;
    }

    public String getPump() {
        return pump;
    }

    public void setPump(String pump) {
        this.pump = pump;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getRqdvol() {
        return rqdvol;
    }

    public void setRqdvol(String rqdvol) {
        this.rqdvol = rqdvol;
    }

    public String getOmode() {
        return omode;
    }

    public void setOmode(String omode) {
        this.omode = omode;
    }

    @Override
    public String toString() {
        return "ClassPojo [valve1 = " + valve1 + ", v1vol = " + v1vol + ", press = " + press + ", rgbled = " + rgbled + ", ts = " + ts + ", ttvol = " + ttvol + ", devid = " + devid + ", pump = " + pump + ", flow = " + flow + ", mid = " + mid + ", rqdvol = " + rqdvol + ", omode = " + omode + "]";
    }
}

