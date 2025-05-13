package me.hakyuwon.sweetCheck.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;
import me.hakyuwon.sweetCheck.dto.DailyImageResponse;
import me.hakyuwon.sweetCheck.dto.MonthlyResponse;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class ArchiveService {
    private final Firestore firestore = FirestoreClient.getFirestore();

    // get daily images
    public DailyImageResponse getDailyImageUrls(LocalDate date, String userId) {
        DailyImageResponse response = new DailyImageResponse();
        List<String> imageUrls = new ArrayList<>();

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

            for (QueryDocumentSnapshot document : documents) {
                String imageUrl = document.getString("imageUrl");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    imageUrls.add(imageUrl);
                }
            }

            response.setDate(date.toString());
            response.setImageUrls(imageUrls);

        } catch (Exception e) {
            log.error("Error retrieving daily image URLs for userId: {}, date: {}", userId, date, e);
        }

        return response;
    }

    // get monthly recorded dates
    public MonthlyResponse getMonthlyMealDates(String userId, YearMonth month) {
        List<String> recordedDates = new ArrayList<>();

        try {
            LocalDate startDate = month.atDay(1);
            LocalDate endDate = month.atEndOfMonth();

            Instant startInstant = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
            Instant endInstant = endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();

            Query query = firestore.collection("users")
                    .document(userId)
                    .collection("meals")
                    .whereGreaterThanOrEqualTo("mealDateTime", startInstant)
                    .whereLessThanOrEqualTo("mealDateTime", endInstant);

            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

            Set<String> dateSet = new HashSet<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (QueryDocumentSnapshot document : documents) {
                Date mealDate = document.getDate("mealDateTime");
                if (mealDate != null) {
                    LocalDate localDate = mealDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    dateSet.add(localDate.format(formatter));
                }
            }

            recordedDates = new ArrayList<>(dateSet);
            Collections.sort(recordedDates);

            return new MonthlyResponse(month.toString(), recordedDates);

        } catch (Exception e) {
            log.error("Error retrieving monthly meal dates for userId: {}, month: {}", userId, month, e);
            return new MonthlyResponse(month.toString(), Collections.emptyList());
        }
    }
}
