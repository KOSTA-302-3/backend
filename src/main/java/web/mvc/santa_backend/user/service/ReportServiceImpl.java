package web.mvc.santa_backend.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.common.enumtype.ReportType;
import web.mvc.santa_backend.common.exception.ErrorCode;
import web.mvc.santa_backend.common.exception.InvalidException;
import web.mvc.santa_backend.common.exception.NotFoundException;
import web.mvc.santa_backend.common.exception.WrongTargetException;
import web.mvc.santa_backend.post.dto.PostDTO;
import web.mvc.santa_backend.post.dto.RepliesDTO;
import web.mvc.santa_backend.post.entity.Posts;
import web.mvc.santa_backend.post.entity.Replies;
import web.mvc.santa_backend.post.repository.PostResository;
import web.mvc.santa_backend.post.repository.RepliesRepository;
import web.mvc.santa_backend.user.dto.ReportRequestDTO;
import web.mvc.santa_backend.user.dto.ReportResponseDTO;
import web.mvc.santa_backend.user.dto.UserResponseDTO;
import web.mvc.santa_backend.user.dto.UserSimpleDTO;
import web.mvc.santa_backend.user.entity.Reports;
import web.mvc.santa_backend.user.entity.Users;
import web.mvc.santa_backend.user.repository.ReportRepository;
import web.mvc.santa_backend.user.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PostResository postRepository;
    private final RepliesRepository repliesRepository;

    @Override
    public ReportResponseDTO report(Long userId, ReportRequestDTO reportRequestDTO) {
        ReportType type = reportRequestDTO.getReportType();
        Long targetId = reportRequestDTO.getTargetId();

        this.isReportable(userId, type, targetId);  // orElseThrow 로 처리
        Users loginUser = userRepository.findById(userId).get();

        Reports report = Reports.builder()
                .user(loginUser)
                .reportType(type)
                .targetId(targetId)
                .content(reportRequestDTO.getContent())
                .createdAt(LocalDateTime.now())
                .build();
        Reports saveReport = reportRepository.save(report);
        log.info("user {} reported {} {}", userId, type, targetId);

        return modelMapper.map(saveReport, ReportResponseDTO.class);
    }

    /**
     * 신고 가능한 상태인지 확인 (가능하지 않다면 throw)
     * 1. 자기 자신 신고, 자신의 게시물 신고(TODO)
     * 2. 레코드 존재 여부 확인
     * 3. 재신고 시 12시간이 지났는지 확인
     */
    private boolean isReportable(Long userId, ReportType type, Long targetId) {
        // 자기 자신 신고 시
        if (type == ReportType.USER && userId.equals(targetId))
            throw new WrongTargetException(ErrorCode.WRONG_TARGET);

        // 레코드 존재 여부 확인
        if (userRepository.existsById(userId) == false) throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        if ((type == ReportType.USER && userRepository.existsById(targetId) == false) ||
                (type == ReportType.POST && userRepository.existsById(targetId) == false) ||
                (type == ReportType.REPLY && userRepository.existsById(targetId) == false))
            throw new WrongTargetException(ErrorCode.WRONG_TARGET);

        // 중복 신고 시 12시간 지났는지 확인
        if (canReport(userId, type, targetId) == false)
            throw new InvalidException(ErrorCode.INVALID_REPORT);

        return true;
    }

    /**
     * 이미 신고한 전적이 있으면 신고일로부터 12시간이 지나야 신고 가능
     */
    private boolean canReport(Long userId, ReportType type, Long targetId) {
        Reports report = reportRepository.findByUser_UserIdAndReportTypeAndTargetId(userId, type, targetId)
                .orElse(null);

        // 최초 신고
        if (report == null) return true;
        // 12시간이 지났다면 true, 아니면 false (신고일+12h < 현재시간)
        return report.getCreatedAt().plusHours(12).isBefore(LocalDateTime.now());
    }

    @Override
    public boolean isReporting(Long userId, ReportType type, Long targetId) {
        return reportRepository.existsByUser_UserIdAndReportTypeAndTargetId(userId, type, targetId);
    }

    @Override
    public Page<Object> getReportsByUserId(Long userId, ReportType type, int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Reports> reports = reportRepository.findByUser_UserIdAndReportType(userId, type, pageable);

        return toDTOByType(reports, type);
    }

    @Override
    public Page<Object> getAllReports(ReportType type, int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Reports> reports = reportRepository.findByReportType(type, pageable);

        return toDTOByType(reports, type);
    }

    /**
     * 각 type 에 맞는 DTO 변환
     */
    private Page<Object> toDTOByType(Page<Reports> reports, ReportType type) {
        return switch (type) {
            case USER -> reports.map(report -> {
                Users target = userRepository.findById(report.getTargetId())
                        .orElseThrow(()->new NotFoundException(ErrorCode.USER_NOT_FOUND));
                return modelMapper.map(target, UserSimpleDTO.class);
            });
            case POST -> reports.map(report -> {
                Posts target = postRepository.findById(report.getTargetId())
                        .orElseThrow(()->new NotFoundException(ErrorCode.TARGET_NOT_FOUND));
                return modelMapper.map(target, PostDTO.class);
            });
            case REPLY ->  reports.map(report -> {
                Replies target = repliesRepository.findById(report.getTargetId())
                        .orElseThrow(()->new NotFoundException(ErrorCode.TARGET_NOT_FOUND));
                return modelMapper.map(target, RepliesDTO.class);
            });
        };
    }
}
