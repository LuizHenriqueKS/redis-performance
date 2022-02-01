package br.zul.redisperformance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.zul.redisperformance.entity.Product;
import br.zul.redisperformance.service.util.ProductLocalCacheTemplate;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceV3 {

    @Autowired
    private ProductLocalCacheTemplate cacheTemplate;

    @Autowired
    private ProductVisitService productVisitService;

    public Mono<Product> getProduct(int id) {
        return productVisitService.addVisit(id)
                .then(cacheTemplate.get(id));
    }

    public Mono<Product> updateProduct(int id, Mono<Product> productMono) {
        return productMono.flatMap(p -> cacheTemplate.update(id, p));
    }

    public Mono<Void> deleteProduct(int id) {
        return cacheTemplate.delete(id);
    }

    public Mono<Product> getProductWithoutAddVisit(int id) {
        return cacheTemplate.get(id);
    }

}
