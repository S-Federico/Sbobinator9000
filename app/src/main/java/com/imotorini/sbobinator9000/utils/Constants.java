package com.imotorini.sbobinator9000.utils;

import java.time.format.DateTimeFormatter;

public final class Constants {


    public static final String STT_PATH = "/api/stt";
    public static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final float RD = 6f;
    public static final float W = 9f;
    public static final float SH = 600f;
    public static final float THRESHOLD = 21000f;

    private Constants() {
    }
}
