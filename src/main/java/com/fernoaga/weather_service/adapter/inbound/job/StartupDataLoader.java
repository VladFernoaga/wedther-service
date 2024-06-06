package com.fernoaga.weather_service.adapter.inbound.job;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fernoaga.weather_service.domain.service.WeatherDataLoaderService;


@Component
public class StartupDataLoader implements CommandLineRunner {
    private static final List<String> DEFAULT_CITIES = Arrays.asList("Kabul", "Tirana", "Algiers", "London", "Luanda", "Saint John's", "Buenos Aires", "Yerevan", "Canberra", "Vienna", "Baku", "Nassau", "Manama", "Dhaka", "Bridgetown", "Minsk", "Brussels",
      "Belmopan", "Porto-Novo", "Thimphu", "Sucre", "Sarajevo", "Gaborone", "Brasília", "Bandar Seri Begawan", "Sofia", "Ouagadougou", "Gitega", "Praia", "Phnom Penh", "Yaoundé", "Ottawa", "Bangui", "Santiago", "Beijing", "Bogotá", "Moroni", "Kinshasa",
      "Brazzaville", "San José", "Zagreb", "Havana", "Nicosia", "Prague", "Copenhagen", "Djibouti", "Roseau", "Santo Domingo", "Quito", "Cairo", "San Salvador", "Malabo", "Asmara", "Tallinn", "Mbabane", "Addis Ababa", "Palikir", "Suva", "Helsinki", "Paris",
      "Libreville", "Banjul", "Tbilisi", "Berlin", "Bucharest", "Athens", "Saint George's", "Guatemala City", "Conakry", "Bissau", "Georgetown", "Port-au-Prince", "Tegucigalpa", "Budapest", "Reykjavik", "New Delhi", "Jakarta", "Tehran", "Baghdad", "Dublin",
      "Jerusalem", "Rome", "Kingston", "Tokyo", "Amman", "Nur-Sultan", "Nairobi", "Tarawa", "Pyongyang", "Seoul", "Pristina", "Kuwait City", "Bishkek", "Vientiane", "Riga", "Beirut", "Maseru", "Monrovia", "Tripoli", "Vaduz");

    @Autowired
    private WeatherDataLoaderService weatherDataLoaderService;

    @Override
    public void run(String... args) throws Exception {
        weatherDataLoaderService.loadCitiesWithCurrentWeatherData(DEFAULT_CITIES);
    }
}
