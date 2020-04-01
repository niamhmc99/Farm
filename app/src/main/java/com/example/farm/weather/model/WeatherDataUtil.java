package com.example.farm.weather.model;

import com.example.farm.R;

public class WeatherDataUtil {

    public static int updateWeatherIcon(int condition) {

        if (condition >= 0 && condition < 300) {
            return R.drawable.tstorm1;
        } else if (condition >= 300 && condition < 500) {
            return R.drawable.light_rain;
        } else if (condition >= 500 && condition < 600) {
            return R.drawable.shower3;
        } else if (condition >= 600 && condition <= 700) {
            return R.drawable.snow4;
        } else if (condition >= 701 && condition <= 771) {
            return R.drawable.fog;
        } else if (condition >= 772 && condition < 800) {
            return R.drawable.tstorm3;
        } else if (condition == 800) {
            return R.drawable.sunny;
        } else if (condition >= 801 && condition <= 804) {
            return R.drawable.cloudy2;
        } else if (condition >= 900 && condition <= 902) {
            return R.drawable.tstorm3;
        } else if (condition == 903) {
            return R.drawable.snow5;
        } else if (condition == 904) {
            return R.drawable.sunny;
        } else if (condition >= 905 && condition <= 1000) {
            return R.drawable.tstorm3;
        }

        return R.drawable.dunno;
    }
}
