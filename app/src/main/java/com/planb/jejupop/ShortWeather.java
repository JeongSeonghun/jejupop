package com.planb.jejupop;

/**
 * Created by Jeong on 2016-09-11.
 */
public class ShortWeather {
    private String hour;  // 시간
    private String day;
    private String temp;  // 온도
    private String wfKor; // 상태
    private String pop; // 강수확률
    private String reh; // 습도
    private String tmx;
    private String tmn;
    //하늘상태 (1맑음,2구름조금,3구름많음,4흐림) sky
    // 강수상태 (0없음,1비,2비/눈,3눈/비,4눈) pty
    // 날씨 wfKor
    // 날씨 영어 wfEn
    // 온도 temp
    // 습도 reh
    // 강수확률 pop (%)
    // 예상강수량 r12 (mm)
    // 예상적설량 s12 (mm)
    // 풍속 ws (m/s)
    // 풍향 wdKor (북, 북동, 동, 남동, 남, 남서, 서, 북서)
    // 풍향 영어 wdEn

    public String getTmx() {
        return tmx;
    }

    public void setTmx(String tmx) {
        this.tmx = tmx;
    }

    public String getTmn() {
        return tmn;
    }

    public void setTmn(String tmn) {
        this.tmn = tmn;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getReh() {
        return reh;
    }

    public void setReh(String reh) {
        this.reh = reh;
    }

    public String getPop() {
        return pop;
    }

    public void setPop(String pop) {
        this.pop = pop;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void setWfKor(String wfKor) {
        this.wfKor = wfKor;
    }

    public String getHour() {
        return hour;
    }

    public String getTemp() {
        return temp;
    }

    public String getWfKor() {
        return wfKor;
    }
}
