package br.zul.redisperformance.service.util;

import reactor.core.publisher.Mono;

public abstract class CacheTemplate<ID, E> {

    abstract protected Mono<E> getFromSource(ID id);

    abstract protected Mono<E> getFromCache(ID id);

    abstract protected Mono<E> updateSource(ID id, E entity);

    abstract protected Mono<E> updateCache(ID id, E entity);

    abstract protected Mono<Void> deleteFromSource(ID id);

    abstract protected Mono<Void> deleteFromCache(ID id);

    public Mono<E> get(ID id) {
        return this.getFromCache(id)
                .switchIfEmpty(
                        this.getFromSource(id).flatMap(
                                entity -> this.updateCache(id, entity)));
    }

    public Mono<E> update(ID id, E entity) {
        return this.updateSource(id, entity)
                .flatMap(e -> this.deleteFromCache(id).thenReturn(e));
    }

    public Mono<Void> delete(ID id) {
        return this.deleteFromSource(id).then(this.deleteFromCache(id));
    }

}
