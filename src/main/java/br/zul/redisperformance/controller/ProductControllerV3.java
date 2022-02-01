package br.zul.redisperformance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.zul.redisperformance.entity.Product;
import br.zul.redisperformance.service.ProductServiceV3;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("product/v3")
public class ProductControllerV3 {

    @Autowired
    private ProductServiceV3 service;

    @GetMapping("{id}")
    public Mono<Product> getProduct(@PathVariable int id) {
        return this.service.getProduct(id);
    }

    @PutMapping("{id}")
    public Mono<Product> updateProduct(@PathVariable int id, @RequestBody Product productMono) {
        return this.service.updateProduct(id, Mono.just(productMono));
    }

    @DeleteMapping("{id}")
    public Mono<Void> deleteProduct(@PathVariable int id) {
        return this.service.deleteProduct(id);
    }

}
