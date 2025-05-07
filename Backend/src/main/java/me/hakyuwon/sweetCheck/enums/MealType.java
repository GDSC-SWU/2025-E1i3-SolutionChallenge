package me.hakyuwon.sweetCheck.enums;

import java.util.Optional;

public enum MealType {
    MORNING, LUNCH, DINNER, SNACK;

    public static Optional<MealType> fromKey(String key) {
        switch (key.toLowerCase()) {
            case "morning":
                return Optional.of(MORNING);
            case "lunch":
                return Optional.of(LUNCH);
            case "dinner":
                return Optional.of(DINNER);
            case "snack":
                return Optional.of(SNACK);
            default:
                return Optional.empty();
        }
    }
}
