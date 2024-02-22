package io.github.wesleyosantos91.servive;

import brave.Span;
import brave.Tracer;
import io.github.wesleyosantos91.client.CepClient;
import io.github.wesleyosantos91.domain.response.Root;
import io.github.wesleyosantos91.listener.PersonListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public record CepService(CepClient client, Tracer tracer) {

    private final static Logger logger = LoggerFactory.getLogger(CepService.class);

    public Root findByCep(String cep) throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            logger.info("Iniciando a busca pelo cep {}", cep);
            if (cep.equals("72405033")) {
                Thread.sleep(10000L);
            }
            Root root = client.findByCep(cep);
            stopWatch.stop();
            logger.info("Finalizado o processamento {}, executado em {} (ms)", root, stopWatch.getTotalTimeMillis());
            return root;
        } catch (InterruptedException e) {
            logger.error("erro ao executar a busca pelo cep {}, erro: {}", cep, e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
        }
    }

    public void findByCeps(List<String> ceps) throws InterruptedException {
        try {
            List<CompletableFuture<Root>> futures = ceps.stream()
                    .map(cep -> CompletableFuture.supplyAsync(() -> {
                        try {
                            if (cep.equals("72860000")) {
                                throw new RuntimeException("erro " + cep);
                            }
                            return findByCep(cep);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            List<Root> results = futures.stream()
                    .map(CompletableFuture::join)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
