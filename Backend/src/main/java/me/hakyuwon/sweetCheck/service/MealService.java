package me.hakyuwon.sweetCheck.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;
import me.hakyuwon.sweetCheck.domain.Meal;
import me.hakyuwon.sweetCheck.domain.MealItem;
import me.hakyuwon.sweetCheck.dto.DailyMealResponse;
import me.hakyuwon.sweetCheck.dto.MealRequest;
import me.hakyuwon.sweetCheck.enums.MealStatus;
import me.hakyuwon.sweetCheck.enums.MealType;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class MealService {
    private final Firestore firestore = FirestoreClient.getFirestore();

    public String saveMealDraft(MealRequest request, MealType mealType) {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(request.getMealDateTime());
            Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

            Meal meal = new Meal();
            meal.setImageUrl(request.getImageUrl());
            meal.setMealDateTime(date);
            meal.setMealType(mealType);
            meal.setTotalSugar(request.getTotalSugar());
            meal.setStatus(MealStatus.DRAFT); // (임시저장)

            DocumentReference mealRef = firestore.collection("users")
                    .document(request.getUserId())
                    .collection("meals")
                    .document();

            mealRef.set(meal).get();

            // MealItem 저장
            for (MealItem item : request.getItems()) {
                mealRef.collection("mealItems").add(item).get();
            }
            String mealId = mealRef.getId();
            log.info("Meal and items draft saved for user: {}", request.getUserId());
            return mealId; // 수정을 위한 mealId 반환

        } catch (Exception e) {
            log.error("Error saving meal draft", e);
        }
        return null;
    }

    // 식단 내용 수정
    public void updateMealDraft(String userId, String mealId, MealRequest request) {
        try {
            DocumentReference mealRef = firestore.collection("users")
                    .document(userId)
                    .collection("meals")
                    .document(mealId);

            // 기존 meal 덮어쓰기
            mealRef.update(
                    "mealType", MealType.valueOf(request.getMealType()),
                    "totalSugar", request.getTotalSugar()
            ).get();

            // 기존 mealItems 삭제 후 새로 저장
            CollectionReference itemsRef = mealRef.collection("mealItems");
            ApiFuture<QuerySnapshot> future = itemsRef.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : documents) {
                doc.getReference().delete();
            }

            for (MealItem item : request.getItems()) {
                itemsRef.add(item).get();
            }

            log.info("Meal draft updated for user: {}", userId);

        } catch (Exception e) {
            log.error("Error updating meal draft", e);
        }
    }

    // 식단 내용 확정
    public void confirmMeal(String userId, String mealId) {
        try {
            DocumentReference mealRef = firestore.collection("users")
                    .document(userId)
                    .collection("meals")
                    .document(mealId);

            mealRef.update("status", MealStatus.CONFIRMED).get();

            log.info("Meal confirmed for user: {}", userId);

        } catch (Exception e) {
            log.error("Error confirming meal", e);
        }
    }

    // 일별 식단 조회
    public DailyMealResponse getDailyMeals(String userId, LocalDate date) {
        DailyMealResponse response = new DailyMealResponse();
        List<Meal> meals = new ArrayList<>();

        try {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);

            Instant startInstant = startOfDay.atZone(ZoneId.systemDefault()).toInstant();
            Instant endInstant = endOfDay.atZone(ZoneId.systemDefault()).toInstant();

            Query query = firestore.collection("users")
                    .document(userId)
                    .collection("meals")
                    .whereGreaterThanOrEqualTo("mealDateTime", startInstant)
                    .whereLessThanOrEqualTo("mealDateTime", endInstant);

            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

            double dailyTotalSugar = 0.0;

            for (QueryDocumentSnapshot document : documents) {
                Meal meal = document.toObject(Meal.class);
                meals.add(meal);

                dailyTotalSugar += meal.getTotalSugar();
            }

            response.setDate(date.toString());
            response.setDailyTotalSugar(dailyTotalSugar);
            response.setMeals(meals);

        } catch (Exception e) {
            log.error("Error retrieving daily meals for userId: {}, date: {}", userId, date, e);
        }

        return response;
    }
}

