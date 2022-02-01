package br.zul.redisperformance.service;

import org.redisson.client.protocol.ScoredEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.zul.redisperformance.entity.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BusinessMetricsService {

    @Autowired
    private ProductServiceV3 productService;

    @Autowired
    private ProductVisitService productVisitService;

    public Flux<ScoredEntry<Product>> top3Products() {
        return productVisitService.getTodaySet()
                .entryRangeReversed(0, 2)
                .flatMapMany(Flux::fromIterable)
                .flatMap(e -> this.buildProductScoredEntry(e.getValue(), e.getScore()));
    }

    private Mono<ScoredEntry<Product>> buildProductScoredEntry(int productId, Double score) {
        return productService.getProductWithoutAddVisit(productId)
                .flatMap(p -> Mono.just(new ScoredEntry<Product>(score, p)));
    }

}
