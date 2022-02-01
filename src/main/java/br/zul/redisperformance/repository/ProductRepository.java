package br.zul.redisperformance.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import br.zul.redisperformance.entity.Product;

public interface ProductRepository extends ReactiveMongoRepository<Product, Integer> {

}