package br.zul.redisperformance.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.IntegerCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class ProductVisitService {

    @Autowired
    private RedissonReactiveClient client;

    public Mono<Void> addVisit(int productId) {
        return this.getTodaySet().addScore(productId, 1).then();
    }

    public RScoredSortedSetReactive<Integer> getTodaySet() {
        String todayDate = DateTimeFormatter.ofPattern("YYYYMMdd").format(LocalDate.now());
        RScoredSortedSetReactive<Integer> set = client.getScoredSortedSet("product:visit:" + todayDate,
                IntegerCodec.INSTANCE);
        return set;
    }

}
