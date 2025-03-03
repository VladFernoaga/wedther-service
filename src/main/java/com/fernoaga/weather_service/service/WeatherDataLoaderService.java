package com.fernoaga.weather_service.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fernoaga.weather_service.config.Constants;
import com.fernoaga.weather_service.model.CityTemperature;
import com.fernoaga.weather_service.outbound.outbound.repository.CityTemperatureRepository;
import com.fernoaga.weather_service.outbound.outbound.weatherdataprovider.WeatherDataFetchingService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WeatherDataLoaderService {

    private static final String OPEN_WEATHER_CITY_LIST_JSON_PATH = "city.list.json";

    @Autowired
    private WeatherDataFetchingService fetchingService;

    @Autowired
    private CityTemperatureRepository cityTemperatureRepository;

    public void loadCitiesWithCurrentWeatherData(List<String> cites) {
        Map<String, Integer> citiesMap = mapCityIdForCities(cites);

        var cityWeatherData = getCityWeatherData(citiesMap);

        cityTemperatureRepository.deleteAll();

        cityWeatherData.stream()
          .map(cityData -> new CityTemperature(cityData.cityName, cityData.temperature, cityData.openWeatherId))
          .forEach(cityTemperature -> cityTemperatureRepository.save(cityTemperature));
    }

    public void updateCitesTemperature() {
        var allEntries = cityTemperatureRepository.findAll();
        if(allEntries.isEmpty()){
         loadCitiesWithCurrentWeatherData(Constants.DEFAULT_CITIES);
        }else {
            allEntries.parallelStream()
              .forEach(cityTemperature -> {
                  var newData = getCityWeatherData(cityTemperature.getOpenWeatherId(), cityTemperature.getCityName());
                  cityTemperature.setTemperature(newData.temperature);
                  cityTemperatureRepository.save(cityTemperature);
              });
        }
    }

    private List<CityWeatherData> getCityWeatherData(Map<String, Integer> citiesMap) {
        return citiesMap.entrySet()
          .stream()
          .map(entry -> getCityWeatherData(entry.getValue(), entry.getKey()))
          .toList();
    }

    private CityWeatherData getCityWeatherData(int openWeatherId, String cityName) {
        var temp = fetchingService.getTemperature(openWeatherId, cityName);
        log.debug(cityName + " " + temp);
        return new CityWeatherData(cityName, temp, openWeatherId);
    }

    private Map<String, Integer> mapCityIdForCities(List<String> cities) {
        Map<String, Integer> cityIdMap = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ClassPathResource cityListResource = new ClassPathResource(OPEN_WEATHER_CITY_LIST_JSON_PATH);
            JsonNode rootNode = objectMapper.readTree(cityListResource.getInputStream());
            for (JsonNode node : rootNode) {
                String cityName = node.get("name")
                  .asText();
                int cityId = node.get("id")
                  .asInt();
                if (cities.contains(cityName)) {
                    cityIdMap.put(cityName, cityId);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return cityIdMap;
    }

    private record CityWeatherData(String cityName, Double temperature, int openWeatherId) {
    }
}
