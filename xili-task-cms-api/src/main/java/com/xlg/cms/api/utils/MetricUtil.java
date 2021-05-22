package com.xlg.cms.api.utils;

import io.prometheus.client.Counter;

public class MetricUtil {

    //流量
    public static final Counter counter_codis_load =
            Counter.build().name("wa_codis_load_total").labelNames("type").help("wa_codis_load_total").register();

    //es
    public static final Counter counter_es_load =
            Counter.build().name("wa_es_load_total").labelNames("type").help("wa_es_load_total").register();

    //kafka consumer
    public static final Counter counter_kafka_consumer =
            Counter.build().name("wa_kafka_consumer_total").labelNames("type").help("wa_kafka_consumer_total")
                    .register();

    //queue consumer count 次数
    public static final Counter counter_queue_consumer_count =
            Counter.build().name("wa_queue_consumer_total_count").labelNames("type")
                    .help("wa_queue_consumer_total_count").register();

    //queue consumer size 消费的个数
    public static final Counter counter_queue_consumer_size =
            Counter.build().name("wa_queue_consumer_total_size").labelNames("type").help("wa_queue_consumer_total_size")
                    .register();

    // 次数
    public static final Counter counter_upload_count =
            Counter.build().name("wa_upload_total_count").labelNames("type").help("wa_upload_total_count").register();

    //时间
    public static final Counter counter_upload_time =
            Counter.build().name("wa_upload_total_time").labelNames("type").help("wa_upload_total_time").register();

    public static void increaseKafkaConsumerCount(String label) {
        counter_kafka_consumer.labels(label).inc();
    }

    public static void increaseQueueConsumerCount(String label) {
        counter_queue_consumer_count.labels(label).inc();
    }

    public static void increaseQueueConsumerSize(String label, int size) {
        counter_queue_consumer_size.labels(label).inc(size);
    }

    public static void increaseUploadCount(String label) {
        counter_upload_count.labels(label).inc(1000);
    }

    public static void increaseUploadTime(String label, double time) {
        counter_upload_time.labels(label).inc(time);
    }

}