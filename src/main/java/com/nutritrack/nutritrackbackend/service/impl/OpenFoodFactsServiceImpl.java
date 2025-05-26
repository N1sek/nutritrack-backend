package com.nutritrack.nutritrackbackend.service.impl;

import com.nutritrack.nutritrackbackend.dto.response.external.openfood.OpenFoodFactsProduct;
import com.nutritrack.nutritrackbackend.dto.response.external.openfood.OpenFoodFactsResponse;
import com.nutritrack.nutritrackbackend.dto.response.food.FoodResponse;
import com.nutritrack.nutritrackbackend.mapper.FoodMapper;
import com.nutritrack.nutritrackbackend.mapper.OpenFoodFactsMapper;
import com.nutritrack.nutritrackbackend.service.OpenFoodFactsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OpenFoodFactsServiceImpl implements OpenFoodFactsService {

    private final FoodMapper foodMapper;
    private final RestTemplate restTemplate;

    @Override
    public List<FoodResponse> searchExternalFoods(String query, int page, int size) {
        String apiUrl = String.format("https://world.openfoodfacts.org/cgi/search.pl?search_terms=%s&search_simple=1&action=process&json=1&page=%d&page_size=%d",
                query, page, size);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "NutriTrack/1.0");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            var response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    entity,
                    OpenFoodFactsResponse.class
            );

            List<OpenFoodFactsProduct> filtered = filterAndSortProducts(
                    response.getBody() != null ? response.getBody().getProducts() : List.of(), query
            );

            return filtered.stream()
                    .map(OpenFoodFactsMapper::mapToFoodResponse)
                    .toList();

        } catch (Exception e) {
            System.err.println("Error al llamar a OpenFoodFacts: " + e.getMessage());
            return List.of();
        }
    }


    public static List<OpenFoodFactsProduct> filterAndSortProducts(List<OpenFoodFactsProduct> products, String query) {
        final String lowerQuery = query.toLowerCase();

        return products.stream()
                .filter(p -> {
                    boolean hasName = p.getProduct_name() != null;
                    boolean hasNutrients = p.getNutriments() != null && p.getNutriments().getCalories() != null;
                    boolean nameMatches = hasName && p.getProduct_name().toLowerCase().contains(lowerQuery);
                    boolean brandMatches = p.getBrands() != null && p.getBrands().toLowerCase().contains(lowerQuery);
                    return hasName && hasNutrients && (nameMatches || brandMatches);
                })
                .sorted(Comparator.comparingInt(p -> {
                    String name = p.getProduct_name().toLowerCase();
                    if (name.equals(lowerQuery)) return 0;
                    if (name.startsWith(lowerQuery)) return 1;
                    return 2;
                }))
                .toList();
    }
}
