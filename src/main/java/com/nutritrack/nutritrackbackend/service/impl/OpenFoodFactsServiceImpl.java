package com.nutritrack.nutritrackbackend.service.impl;

import com.nutritrack.nutritrackbackend.dto.response.external.openfood.OpenFoodFactsProduct;
import com.nutritrack.nutritrackbackend.dto.response.external.openfood.OpenFoodFactsResponse;
import com.nutritrack.nutritrackbackend.dto.response.food.FoodResponse;
import com.nutritrack.nutritrackbackend.mapper.OpenFoodFactsMapper;
import com.nutritrack.nutritrackbackend.mapper.FoodMapper;
import com.nutritrack.nutritrackbackend.service.OpenFoodFactsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenFoodFactsServiceImpl implements OpenFoodFactsService {

    private final FoodMapper foodMapper;
    private final RestTemplate restTemplate;

    @Value("${offfacts.url}")
    private String offUrl;
    @Value("${offfacts.username}")
    private String offUser;
    @Value("${offfacts.password}")
    private String offPass;

    @Override
    @Cacheable(
            value = "externalSearch",
            key = "#query.toLowerCase() + '_' + #page + '_' + #size",
            unless = "#result.isEmpty() || #query.length() < 3"
    )
    @Retryable(
            value = RestClientException.class,
            maxAttempts = 1,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public List<FoodResponse> searchExternalFoods(String query, int page, int size) {
        List<OpenFoodFactsProduct> accumulated = new ArrayList<>();
        int apiPage = page;
        int rawPageSize = size * 2;

        while (accumulated.size() < size) {
            String url = String.format(
                    "%s?search_terms=%s&search_simple=1&action=process&json=1&page=%d&page_size=%d",
                    offUrl,
                    URLEncoder.encode(query, StandardCharsets.UTF_8),
                    page,
                    size*2
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "NutriTrack/1.0 (https://nutritrack.app; contacto@nutritrack.app)");
            headers.set("Accept-Encoding", "gzip");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            var response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    OpenFoodFactsResponse.class
            );

            List<OpenFoodFactsProduct> products =
                    response.getBody() != null
                            ? response.getBody().getProducts()
                            : List.of();

            if (products.isEmpty()) {
                break;
            }

            List<OpenFoodFactsProduct> filtered = filterAndSortProducts(products, query);

            for (OpenFoodFactsProduct p : filtered) {
                if (accumulated.size() >= size) break;
                accumulated.add(p);
            }

            if (filtered.size() < rawPageSize) {
                break;
            }

            apiPage++;
        }

        return accumulated.stream()
                .limit(size)
                .map(OpenFoodFactsMapper::mapToFoodResponse)
                .toList();
    }

    @Recover
    public List<FoodResponse> recover(RestClientException ex, String query, int page, int size) {
        System.err.println("Recover tras fallo externo: " + ex.getMessage());
        return List.of();
    }


    public static List<OpenFoodFactsProduct> filterAndSortProducts(
            List<OpenFoodFactsProduct> products,
            String query
    ) {
        final String lowerQuery = query.toLowerCase();

        return products.stream()
                .filter(p -> {
                    boolean hasName = p.getProduct_name() != null;
                    boolean hasNutrients = p.getNutriments() != null
                            && p.getNutriments().getCalories() != null;
                    boolean nameMatches = hasName
                            && p.getProduct_name().toLowerCase().contains(lowerQuery);
                    boolean brandMatches = p.getBrands() != null
                            && p.getBrands().toLowerCase().contains(lowerQuery);
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
