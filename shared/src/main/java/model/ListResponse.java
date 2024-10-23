package model;

import java.util.Collection;

public record ListResponse(Collection<GameResult> games) {
}
