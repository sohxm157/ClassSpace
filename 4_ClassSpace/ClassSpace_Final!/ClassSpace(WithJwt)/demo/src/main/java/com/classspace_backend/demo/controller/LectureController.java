package com.classspace_backend.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.classspace_backend.demo.dto.LectureDetailsDto;
import com.classspace_backend.demo.dto.ResolveLectureRequest;
import com.classspace_backend.demo.service.LectureService;

@RestController
@RequestMapping("/api/lecture")
public class LectureController {

    private final LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    // POST /api/lecture/resolve
    @PostMapping("/resolve")
    public ResponseEntity<LectureDetailsDto> resolve(@RequestBody ResolveLectureRequest req) {
        return ResponseEntity.ok(lectureService.resolveLecture(req));
    }

    @GetMapping("/{lectureId}")
    public ResponseEntity<LectureDetailsDto> getLecture(@PathVariable Long lectureId) {
        return ResponseEntity.ok(lectureService.getLectureDetails(lectureId));
    }

    @PostMapping("/{lectureId}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long lectureId, @RequestBody java.util.Map<String, String> body) {
        String reason = body.getOrDefault("reason", "No reason provided");
        lectureService.cancelLecture(lectureId, reason);
        return ResponseEntity.ok().build();
    }

}
