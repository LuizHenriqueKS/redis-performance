package br.zul.redisperformance.service.util;

import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.LocalCachedMapOptions.ReconnectionStrategy;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.zul.redisperformance.entity.Product;
import br.zul.redisperformance.repository.ProductRepository;
import reactor.core.publisher.Mono;

@Component
public class ProductLocalCacheTemplate extends CacheTemplate<Integer, Product> {

    @Autowired
    private ProductRepository productRepository;

    private RLocalCachedMap<Integer, Product> map;

    public ProductLocalCacheTemplate(RedissonClient client) {
        LocalCachedMapOptions<Integer, Product> mapOptions = LocalCachedMapOptions.<Integer, Product>defaults()
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                .reconnectionStrategy(ReconnectionStrategy.CLEAR);
        this.map = client.getLocalCachedMap("product", new TypedJsonJacksonCodec(Integer.class, Product.class),
                mapOptions);
    }

    @Override
    protected Mono<Product> getFromSource(Integer id) {
        return productRepository.findById(id);
    }

    @Override
    protected Mono<Product> getFromCache(Integer id) {
        // return Mono.fromSupplier(() -> map.get(id));
        return Mono.create(sink -> {
            map.getAsync(id)
                    .thenAccept(sink::success)
                    .exceptionally(e -> {
                        sink.error(e);
                        return null;
                    });
        });
    }

    @Override
    protected Mono<Product> updateSource(Integer id, Product entity) {
        return this.productRepository.findById(id)
                .doOnNext(p -> entity.setId(id))
                .flatMap(p -> productRepository.save(entity));
    }

    @Override
    protected Mono<Product> updateCache(Integer id, Product entity) {
        return Mono.create(sink -> {
            map.fastPutAsync(id, entity)
                    .thenAccept(b -> sink.success(entity))
                    .exceptionally(ex -> {
                        sink.error(ex);
                        return null;
                    });
        });
    }

    @Override
    protected Mono<Void> deleteFromSource(Integer id) {
        return this.productRepository.deleteById(id);
    }

    @Override
    protected Mono<Void> deleteFromCache(Integer id) {
        return Mono.create(sink -> {
            map.fastRemoveAsync(id)
                    .thenAccept(b -> sink.success(null))
                    .exceptionally(ex -> {
                        sink.error(ex);
                        return null;
                    });
        });
    }

}
