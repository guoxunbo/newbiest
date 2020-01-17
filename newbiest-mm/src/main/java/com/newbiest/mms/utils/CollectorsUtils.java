package com.newbiest.mms.utils;

import com.google.common.collect.Sets;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 扩展Collectors类 支持对bigdecimal做操作
 *
 * Created by guoxunbo on 2020-01-17 15:31
 */
public class CollectorsUtils {

    static final Set<Collector.Characteristics> CH_NOID = Sets.newHashSet();

    public static <T> Collector<T, ?, BigDecimal> summingBigDecimal(ToBigDecimalFunction<? super T> mapper) {
        return new CollectorImpl<>(() -> new BigDecimal[1], (a, t) -> {
            if (a[0] == null) {
                a[0] = BigDecimal.ZERO;
            }
            a[0] = a[0].add(mapper.applyAsBigDecimal(t));
        }, (a, b) -> {
            a[0] = a[0].add(b[0]);
            return a;
        }, a -> a[0], CH_NOID);
    }

    private static <I, R> Function<I, R> castingIdentity() {
        return i -> (R) i;
    }

    static class CollectorImpl<T, A, R> implements Collector<T, A, R> {
        private final Supplier<A> supplier;
        private final BiConsumer<A, T> accumulator;
        private final BinaryOperator<A> combiner;
        private final Function<A, R> finisher;
        private final Set<Characteristics> characteristics;

        CollectorImpl(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner,
                      Function<A, R> finisher, Set<Characteristics> characteristics) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
            this.finisher = finisher;
            this.characteristics = characteristics;
        }

        CollectorImpl(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner,
                      Set<Characteristics> characteristics) {
            this(supplier, accumulator, combiner, castingIdentity(), characteristics);
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return accumulator;
        }

        @Override
        public Supplier<A> supplier() {
            return supplier;
        }

        @Override
        public BinaryOperator<A> combiner() {
            return combiner;
        }

        @Override
        public Function<A, R> finisher() {
            return finisher;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return characteristics;
        }
    }

}
