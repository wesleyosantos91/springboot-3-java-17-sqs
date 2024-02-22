package io.github.wesleyosantos91.domain.event;

import java.util.List;

public record Person(String name, String cpf, List<String> ceps) {
}
