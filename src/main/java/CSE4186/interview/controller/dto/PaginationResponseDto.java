package CSE4186.interview.controller.dto;

import lombok.Getter;

@Getter
public class PaginationResponseDto<T> extends BaseResponseDto<T> {

    private final int pageCount;

    public PaginationResponseDto(String result, String message, T body, int pageCount) {
        super(result, message, body);
        this.pageCount = pageCount;
    }

    public static <T> PaginationResponseDto<T> p_fail(String errorMessage) {
        return new PaginationResponseDto<>("fail", errorMessage, null, 1);
    }
}
