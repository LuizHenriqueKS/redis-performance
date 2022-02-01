package br.zul.redisperformance.service;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import br.zul.redisperformance.entity.Product;
import br.zul.redisperformance.repository.ProductRepository;
import reactor.core.publisher.Flux;

@Service
public class DataSetupService implements CommandLineRunner {
    
    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        Flux.range(1, 1000)
            .map(i -> new Product(i, "Product " + i, ThreadLocalRandom.current().nextInt(1, 100)))
            .collectList()
            .flatMapMany(l -> this.productRepository.saveAll(l))    
            .doFinally(s -> System.out.println("Data setup done " + s))
            .subscribe();
    }

}
