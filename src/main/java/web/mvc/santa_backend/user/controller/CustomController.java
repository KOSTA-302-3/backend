package web.mvc.santa_backend.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import web.mvc.santa_backend.common.enumtype.CustomItemType;
import web.mvc.santa_backend.common.security.CustomUserDetails;
import web.mvc.santa_backend.user.dto.BadgeDTO;
import web.mvc.santa_backend.user.dto.ColorDTO;
import web.mvc.santa_backend.user.dto.UserBadgeDTO;
import web.mvc.santa_backend.user.dto.UserColorDTO;
import web.mvc.santa_backend.user.service.BadgeService;
import web.mvc.santa_backend.user.service.ColorService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/custom")
@Slf4j
@Tag(name = "CustomController API", description = "CustomController API")
public class CustomController {

    private final BadgeService badgeService;
    private final ColorService colorService;

    @Operation(summary = "배지 조회")
    @GetMapping("/badge/{page}")
    public ResponseEntity<?> getBadges(@PathVariable int page) {
        Page<BadgeDTO> badges = badgeService.getBadges(page);

        return ResponseEntity.status(HttpStatus.OK).body(badges);
    }

    @Operation(summary = "배지 구매")
    @PostMapping("/badge")
    public ResponseEntity<?> buyBadges(@RequestBody Long badgeId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getUserId();
        UserBadgeDTO ubDTO = badgeService.buyBadge(userId, badgeId);

        return ResponseEntity.status(HttpStatus.OK).body(ubDTO);
    }

    @Operation(summary = "색상 조회")
    @GetMapping("/color/{page}")
    public ResponseEntity<?> getColors(@PathVariable int page) {
        Page<ColorDTO> colors = colorService.getColors(page);

        return ResponseEntity.status(HttpStatus.OK).body(colors);
    }

    @Operation(summary = "색상 구매")
    @PostMapping("/color")
    public ResponseEntity<?> buyColors(@RequestBody Long colorId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getUserId();
        UserColorDTO ucDTO = colorService.buyColor(userId, colorId);

        return ResponseEntity.status(HttpStatus.OK).body(ucDTO);
    }

    /* 배지/색상 추가 (관리자용) (TODO 위치변경) */
    @Operation(summary = "배지 추가")
    @PostMapping("/admin/badge")
    public ResponseEntity<?> addBadge(@RequestBody BadgeDTO badgeDTO) {
        BadgeDTO badge = badgeService.addBadge(badgeDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(badge);
    }

    @Operation(summary = "색상 추가")
    @PostMapping("/admin/color")
    public ResponseEntity<?> addColor(@RequestBody ColorDTO colorDTO) {
        ColorDTO color = colorService.addColor(colorDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(color);
    }
}
