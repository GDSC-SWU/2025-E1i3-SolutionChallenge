package me.hakyuwon.sweetCheck.controller;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.sweetCheck.service.ArchiveService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArchiveController {
    private final ArchiveService archiveService;

}
