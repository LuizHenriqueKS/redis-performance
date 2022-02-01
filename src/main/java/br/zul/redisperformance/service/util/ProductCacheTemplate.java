package br.zul.redisperformance.service.util;

import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.zul.redisperformance.entity.Product;
import br.zul.redisperformance.repository.ProductRepository;
import reactor.core.publisher.Mono;

@Component
public class ProductCacheTemplate extends CacheTemplate<Integer, Product> {

    @Autowired
    private ProductRepository productRepository;

    private RMapReactive<Integer, Product> map;

    public ProductCacheTemplate(RedissonReactiveClient client) {
        this.map = client.getMap("product", new TypedJsonJacksonCodec(Integer.class, Product.class));
    }

    @Override
    protected Mono<Product> getFromSource(Integer id) {
        return productRepository.findById(id);
    }

    @Override
    protected Mono<Product> getFromCache(Integer id) {
        return map.get(id);
    }

    @Override
    protected Mono<Product> updateSource(Integer id, Product entity) {
        return this.productRepository.findById(id)
                .doOnNext(p -> entity.setId(id))
                .flatMap(p -> productRepository.save(entity));
    }

    @Override
    protected Mono<Product> updateCache(Integer id, Product entity) {
        return this.map.fastPut(id, entity).thenReturn(entity);
    }

    @Override
    protected Mono<Void> deleteFromSource(Integer id) {
        return this.productRepository.deleteById(id);
    }

    @Override
    protected Mono<Void> deleteFromCache(Integer id) {
        return this.map.fastRemove(id).then();
    }

}
