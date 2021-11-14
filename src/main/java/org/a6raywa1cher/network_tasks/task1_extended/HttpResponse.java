package org.a6raywa1cher.network_tasks.task1_extended;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public record HttpResponse(int status, List<Pair<String, String>> headers, String body) {
}
