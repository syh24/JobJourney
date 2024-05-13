package CSE4186.interview.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ReportDto {

    @Data
    @AllArgsConstructor
    @Schema(name = "reportCreateRequest", description = "신고 생성 DTO")
    public static class createRequest {
        @NotNull
        private String reportType;
        @NotNull
        private Long userId;
        @NotNull
        private Long targetId;
    }
}
