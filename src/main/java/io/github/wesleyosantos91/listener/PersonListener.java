package io.github.wesleyosantos91.listener;

import brave.Span;
import brave.Tracer;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import io.github.wesleyosantos91.domain.event.Person;
import io.github.wesleyosantos91.servive.CepService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class PersonListener {

    private final static Logger logger = LoggerFactory.getLogger(PersonListener.class);
    private final CepService service;
    private final Tracer tracer;

    public PersonListener(CepService service, Tracer tracer) {
        this.service = service;
        this.tracer = tracer;
    }

    @SqsListener("${spring.cloud.aws.sqs.queue.name}")
    public void listener(Message<Person> message, Acknowledgement ack) throws InterruptedException {

        try {
            Span span = tracer.currentSpan();
            logger.info("Iniciando o procesamento do evento {}", message.getPayload());
            CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(() -> {
                try(Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
                    service.findByCeps(message.getPayload().ceps());
                    return null;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });


            CompletableFuture.allOf(completableFuture).join();

            ack.acknowledge();
            logger.info("Finalizado o processamento do evento {}", message.getPayload());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
