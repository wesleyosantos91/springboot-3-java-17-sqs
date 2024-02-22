package io.github.wesleyosantos91.config;

import brave.Tracer;
import brave.Tracing;
import brave.http.HttpTracing;
import brave.instrumentation.awsv2.AwsSdkTracing;
import brave.propagation.CurrentTraceContext;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.propagation.TraceContext;
import io.awspring.cloud.autoconfigure.sqs.SqsProperties.Listener;
import io.awspring.cloud.sqs.config.SqsBootstrapConfiguration;
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import org.aspectj.weaver.tools.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;
import java.time.Duration;

@Import(SqsBootstrapConfiguration.class)
@Configuration
public class SqsConfig {

    @Value("${spring.cloud.aws.sqs.endpoint}")
    private String endpoint;

    @Value("${spring.cloud.aws.sqs.region}")
    private String region;

    @Value("${spring.cloud.aws.sqs.listener.max-concurrent-messages}")
    private Integer maxConcurrentMessages;


    @Value("${spring.cloud.aws.sqs.listener.max-messages-per-poll}")
    private Integer maxMessagesPerPoll;


    @Value("${spring.cloud.aws.sqs.listener.poll-timeout}")
    private Duration pollTimeout;

    @Bean
    public SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory() {
        return SqsMessageListenerContainerFactory
                .builder()
                .sqsAsyncClient(sqsAsyncClient())
                .configure(options -> {
                    options.acknowledgementMode(AcknowledgementMode.MANUAL);
                    options.maxConcurrentMessages(maxConcurrentMessages);
                    options.maxMessagesPerPoll(maxMessagesPerPoll);
                    options.pollTimeout(pollTimeout);
                })
                .build();
    }

    @Bean
    public SqsAsyncClient sqsAsyncClient() {

        return SqsAsyncClient
                .builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .build();
    }

}