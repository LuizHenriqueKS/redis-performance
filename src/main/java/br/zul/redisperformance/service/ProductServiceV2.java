package br.zul.redisperformance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.zul.redisperformance.entity.Product;
import br.zul.redisperformance.service.util.ProductCacheTemplate;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceV2 {

    @Autowired
    private ProductCacheTemplate cacheTemplate;

    public Mono<Product> getProduct(int id) {
        return cacheTemplate.get(id);
    }

    public Mono<Product> updateProduct(int id, Mono<Product> productMono) {
        return productMono.flatMap(p -> cacheTemplate.update(id, p));
    }

    public Mono<Void> deleteProduct(int id) {
        return cacheTemplate.delete(id);
    }

}
