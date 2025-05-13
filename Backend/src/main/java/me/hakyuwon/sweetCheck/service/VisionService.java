package me.hakyuwon.sweetCheck.service;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class VisionService {

    // get menu from menu image
    public List<String> menuFromImage(MultipartFile imageFile) throws Exception {
        List<String> menuList = new ArrayList<>();

        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
            ByteString imgBytes = ByteString.copyFrom(imageFile.getBytes());

            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();

            List<AnnotateImageRequest> requests = new ArrayList<>();
            requests.add(request);

            List<AnnotateImageResponse> responses = vision.batchAnnotateImages(requests).getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    throw new RuntimeException("Vision API Error: " + res.getError().getMessage());
                }

                String fullText = res.getFullTextAnnotation().getText();
                String[] lines = fullText.split("\n");
                for (String line : lines) {
                    String cleanedLine = removePrice(line);
                    if (!cleanedLine.isEmpty()) {
                        menuList.add(cleanedLine);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Vision API failed", e);
        }
        return menuList;
    }

    private String removePrice(String line) {
        return line.replaceAll("\\d+[,.]?\\d*원?", "").trim();
    }
}