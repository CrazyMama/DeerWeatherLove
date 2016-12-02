package com.lu.deerweatherlove.modules.main.domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by L on 16/11/22.
 * <p>
 * <p>    关于 serializable 接口
 * 为了保存在内存中的各种对象的状态（也就是实例变量，不是方法），并且可以把保存的对象状态再读出来。虽然你可以用你自己的各种各样的方法来保存object states，但是Java为我们提供一种很好保存对象状态的机制，那就是序列化。
 * <p>
 * 　　简单来说序列化就是一种用来处理对象流的机制，所谓对象流也就是将对象的内容进行流化，流的概念这里不用多说(就是I/O)，我们可以对流化后的对象进行读写操作，也可将流化后的对象传输于网络之间！在对对象流进行读写操作时会引发一些问题，而序列化机制正是用来解决这些问题的！
 * <p>
 * 　　它面向那些实现了Serializable接口的对象，可将它们转换成一系列字节，并可在以后完全恢复回原来的样子。这一过程亦可通过网络进行。这意味着序列化机制能自动补偿操作系统间的差异。换句话说，可以先在Windows机器上创建一个对象，对其序列化，然后通过网络发给一台Unix机器，然后在那里准确无误地重新“装配”。不必关心数据在不同机器上如何表示，也不必关心字节的顺序或者其他任何细节。
 * <p>
 * 　　那么我们在什么情况下需要使用到Serializable接口呢？？
 * <p>
 * 　　1、当你想把的内存中的对象状态保存到一个文件中或者数据库中时候；
 * 　　2、当你想用套接字在网络上传送对象的时候；
 * 　　3、当你想通过RMI传输对象的时候；
 */
public class Weather implements Serializable {
    @SerializedName("aqi")
    public AqiEntity aqi;


    @SerializedName("basic")
    public BasicEntity basic;


    @SerializedName("now")
    public NowEntity now;


    @SerializedName("status")
    public String status;


    @SerializedName("suggestion")
    public SuggestionEntity suggestion;


    @SerializedName("daily_forecast")
    public List<DailyForecastEntity> dailyForecast;


    @SerializedName("hourly_forecast")
    public List<HourlyForecastEntity> hourlyForecast;

    public static class AqiEntity implements Serializable {
        /**
         * "aqi": "60",
         * "co": "0",
         * "no2": "14",
         * "o3": "95",
         * "pm10": "67",
         * "pm25": "15",
         * "qlty": "良",  //共六个级别，分别：优，良，轻度污染，中度污染，重度污染，严重污染
         * "so2": "10"
         */

        @SerializedName("city")
        public CityEntity city;

        public static class CityEntity implements Serializable {
            @SerializedName("aqi")
            public String aqi;
            @SerializedName("co")
            public String co;
            @SerializedName("no2")
            public String no2;
            @SerializedName("o3")
            public String o3;
            @SerializedName("pm10")
            public String pm10;
            @SerializedName("pm25")
            public String pm25;
            @SerializedName("qlty")
            public String qlty;
            @SerializedName("so2")
            public String so2;
        }
    }

    public static class BasicEntity implements Serializable {
        @SerializedName("city")
        public String city;
        @SerializedName("cnty")
        public String cnty;
        @SerializedName("id")
        public String id;
        @SerializedName("lat")
        public String lat;
        @SerializedName("lon")
        public String lon;
        /**
         * "loc": "2016-08-30 11:52",
         * "utc": "2016-08-30 03:52"
         */

        @SerializedName("update")
        public UpdateEntity update;

        public static class UpdateEntity implements Serializable {
            @SerializedName("loc")
            public String loc;
            @SerializedName("utc")
            public String utc;
        }
    }

    public static class NowEntity implements Serializable {
        /**
         * "code": "100",
         * "txt": "晴"
         */

        @SerializedName("cond")
        public CondEntity cond;
        @SerializedName("fl")
        public String fl;
        @SerializedName("hum")
        public String hum;
        @SerializedName("pcpn")
        public String pcpn;
        @SerializedName("pres")
        public String pres;
        @SerializedName("tmp")
        public String tmp;
        @SerializedName("vis")
        public String vis;
        /**
         * "deg": "330",
         * "dir": "西北风",
         * "sc": "6-7",
         * "spd": "34"
         */

        @SerializedName("wind")
        public WindEntity wind;

        public static class CondEntity implements Serializable {
            @SerializedName("code")
            public String code;
            @SerializedName("txt")
            public String txt;
        }

        public static class WindEntity implements Serializable {
            @SerializedName("deg")
            public String deg;
            @SerializedName("dir")
            public String dir;
            @SerializedName("sc")
            public String sc;
            @SerializedName("spd")
            public String spd;
        }
    }

    public static class SuggestionEntity implements Serializable {
        /**
         * "brf": "较舒适",
         * "txt": "白天天气晴好，您在这种天气条件下，会感觉早晚凉爽、舒适，午后偏热。"
         */

        @SerializedName("comf")
        public ComfEntity comf;
        /**
         * "brf": "较不宜",
         * "txt": "较不宜洗车，未来一天无雨，风力较大，如果执意擦洗汽车，要做好蒙上污垢的心理准备。"
         */

        @SerializedName("cw")
        public CwEntity cw;
        /**
         * "brf": "热",
         * "txt": "天气热，建议着短裙、短裤、短薄外套、T恤等夏季服装。"
         */

        @SerializedName("drsg")
        public DrsgEntity drsg;
        /**
         * "brf": "较易发",
         * "txt": "虽然温度适宜但风力较大，仍较易发生感冒，体质较弱的朋友请注意适当防护。"
         */

        @SerializedName("flu")
        public FluEntity flu;
        /**
         * "brf": "较适宜",
         * "txt": "天气较好，但风力较大，推荐您进行室内运动，若在户外运动请注意防风。"
         */

        @SerializedName("sport")
        public SportEntity sport;
        /**
         * "brf": "适宜",
         * "txt": "天气较好，风稍大，但温度适宜，是个好天气哦。适宜旅游，您可以尽情地享受大自然的无限风光。"
         */

        @SerializedName("trav")
        public TravEntity trav;
        /**
         * "brf": "强",
         * "txt": "紫外线辐射强，建议涂擦SPF20左右、PA++的防晒护肤品。避免在10点至14点暴露于日光下。"
         */

        @SerializedName("uv")
        public UvEntity uv;

        public static class ComfEntity implements Serializable {
            @SerializedName("brf")
            public String brf;
            @SerializedName("txt")
            public String txt;
        }

        public static class CwEntity implements Serializable {
            @SerializedName("brf")
            public String brf;
            @SerializedName("txt")
            public String txt;
        }

        public static class DrsgEntity implements Serializable {
            @SerializedName("brf")
            public String brf;
            @SerializedName("txt")
            public String txt;
        }

        public static class FluEntity implements Serializable {
            @SerializedName("brf")
            public String brf;
            @SerializedName("txt")
            public String txt;
        }

        public static class SportEntity implements Serializable {
            @SerializedName("brf")
            public String brf;
            @SerializedName("txt")
            public String txt;
        }

        public static class TravEntity implements Serializable {
            @SerializedName("brf")
            public String brf;
            @SerializedName("txt")
            public String txt;
        }

        public static class UvEntity implements Serializable {
            @SerializedName("brf")
            public String brf;
            @SerializedName("txt")
            public String txt;
        }
    }

    public static class DailyForecastEntity implements Serializable {
        /**
         * "sr": "05:28",
         * "ss": "18:29"
         */

        @SerializedName("astro")
        public AstroEntity astro;
        /**
         * "code_d": "100",
         * "code_n": "100",
         * "txt_d": "晴",
         * "txt_n": "晴"
         */

        @SerializedName("cond")
        public CondEntity cond;
        @SerializedName("date")
        public String date;
        @SerializedName("hum")
        public String hum;
        @SerializedName("pcpn")
        public String pcpn;
        @SerializedName("pop")
        public String pop;
        @SerializedName("pres")
        public String pres;
        /**
         * max : 19
         * min : 7
         */

        @SerializedName("tmp")
        public TmpEntity tmp;
        @SerializedName("vis")
        public String vis;
        /**
         * deg : 54
         * dir : 无持续风向
         * sc : 微风
         * spd : 6
         */

        @SerializedName("wind")
        public WindEntity wind;

        public static class AstroEntity implements Serializable {
            @SerializedName("sr")
            public String sr;
            @SerializedName("ss")
            public String ss;
        }

        public static class CondEntity implements Serializable {
            @SerializedName("code_d")
            public String codeD;
            @SerializedName("code_n")
            public String codeN;
            @SerializedName("txt_d")
            public String txtD;
            @SerializedName("txt_n")
            public String txtN;
        }

        public static class TmpEntity implements Serializable {
            @SerializedName("max")
            public String max;
            @SerializedName("min")
            public String min;
        }

        public static class WindEntity implements Serializable {
            @SerializedName("deg")
            public String deg;
            @SerializedName("dir")
            public String dir;
            @SerializedName("sc")
            public String sc;
            @SerializedName("spd")
            public String spd;
        }
    }

    public static class HourlyForecastEntity implements Serializable {
        @SerializedName("date")
        public String date;
        @SerializedName("hum")
        public String hum;
        @SerializedName("pop")
        public String pop;
        @SerializedName("pres")
        public String pres;
        @SerializedName("tmp")
        public String tmp;
        /**
         * deg : 13
         * dir : 东北风
         * sc : 微风
         * spd : 16
         */

        @SerializedName("wind")
        public WindEntity wind;

        public static class WindEntity implements Serializable {
            @SerializedName("deg")
            public String deg;
            @SerializedName("dir")
            public String dir;
            @SerializedName("sc")
            public String sc;
            @SerializedName("spd")
            public String spd;
        }
    }
}
