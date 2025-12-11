package web.mvc.santa_backend.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.common.enumtype.ReportType;
import web.mvc.santa_backend.user.dto.ReportRequestDTO;
import web.mvc.santa_backend.user.dto.ReportResponseDTO;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Override
    public ReportResponseDTO report(Long userId, ReportRequestDTO reportRequestDTO) {
        return null;
    }

    @Override
    public boolean checkReport(Long userId, ReportType type, Long targetId) {
        return false;
    }

    @Override
    public Page<Object> getReportsByUserId(Long userId, ReportType type, int page) {
        return null;
    }

    @Override
    public Page<Object> getAllReports(ReportType type, int page) {
        return null;
    }
}
