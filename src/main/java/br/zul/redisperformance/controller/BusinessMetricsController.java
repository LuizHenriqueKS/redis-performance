package br.zul.redisperformance.controller;

import java.time.Duration;

import org.redisson.client.protocol.ScoredEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.zul.redisperformance.entity.Product;
import br.zul.redisperformance.service.BusinessMetricsService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("product/metrics")
public class BusinessMetricsController {

    @Autowired
    private BusinessMetricsService metricsService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ScoredEntry<Product>> getMetrics() {
        return metricsService.top3Products();
    }

}
