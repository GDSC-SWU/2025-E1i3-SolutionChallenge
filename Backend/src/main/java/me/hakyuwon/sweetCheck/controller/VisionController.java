package me.hakyuwon.sweetCheck.controller;

import lombok.AllArgsConstructor;
import me.hakyuwon.sweetCheck.dto.RecommendResponse;
import me.hakyuwon.sweetCheck.service.RecommendService;
import me.hakyuwon.sweetCheck.service.VisionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class VisionController {
    private final RecommendService recommendService;
    private final VisionService visionService;

    // 메뉴 사진 찍으면 메뉴 리스트 반환
    @PostMapping("/api/photo/menu")
    public ResponseEntity<RecommendResponse> recommendMenu(
            @RequestParam("userId") String userId,
            @RequestParam("image") MultipartFile image) throws Exception {

        RecommendResponse response = recommendService.recommendMenu(userId, image);
        return ResponseEntity.ok(response);
    }


}
