package me.hakyuwon.sweetCheck.service;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;
import me.hakyuwon.sweetCheck.domain.Meal;
import me.hakyuwon.sweetCheck.domain.MealItem;
import me.hakyuwon.sweetCheck.dto.MealRequest;
import me.hakyuwon.sweetCheck.enums.MealType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class MealService {
    private final Firestore firestore = FirestoreClient.getFirestore();

    public void saveMealFromAnalysis(MealRequest request) {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(request.getMealDateTime());
            Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

            Meal meal = new Meal();
            meal.setImageUrl(request.getImageUrl());
            meal.setMealDateTime(date);
            meal.setMealType(MealType.valueOf(request.getMealType())); // "DINNER" 등
            meal.setTotalSugar(request.getTotalSugar());

            DocumentReference mealRef = firestore.collection("users")
                    .document(request.getUserId())
                    .collection("meals")
                    .document();

            mealRef.set(meal).get();

            // MealItem 저장
            for (MealItem item : request.getItems()) {
                mealRef.collection("mealItems").add(item).get();
            }

            log.info("Meal and items saved for user: {}", request.getUserId());

        } catch (Exception e) {
            log.error("Error saving meal from analysis", e);
        }
    }
}
