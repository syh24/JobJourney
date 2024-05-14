package CSE4186.interview.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class LikesDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "likeCreateRequest", description = "좋아요 DTO")
    public static class creteRequest {
        @NotNull private Long userId;
    }
}
