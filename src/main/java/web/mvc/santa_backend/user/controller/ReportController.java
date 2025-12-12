package web.mvc.santa_backend.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import web.mvc.santa_backend.common.enumtype.ReportType;
import web.mvc.santa_backend.common.security.CustomUserDetails;
import web.mvc.santa_backend.user.dto.ReportRequestDTO;
import web.mvc.santa_backend.user.dto.ReportResponseDTO;
import web.mvc.santa_backend.user.service.ReportService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/report")
@Slf4j
@Tag(name = "ReportController API", description = "ReportController API")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "신고하기")
    @PostMapping
    public ResponseEntity<?> report(@RequestBody ReportRequestDTO reportRequestDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ReportResponseDTO reportDTO = reportService.report(customUserDetails.getUser().getUserId(), reportRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(reportDTO);
    }

    @Operation(summary = "신고 확인")
    @GetMapping("/{type}/{targetId}")
    public ResponseEntity<?> checkReport(@PathVariable ReportType type, @PathVariable Long targetId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        boolean isReporting = reportService.isReporting(customUserDetails.getUser().getUserId(), type, targetId);

        return ResponseEntity.status(HttpStatus.OK).body(isReporting);
    }
}
