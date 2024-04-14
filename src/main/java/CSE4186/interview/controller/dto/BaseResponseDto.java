package CSE4186.interview.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BaseResponseDto<T> {

    private final String result;
    private final String message;
    private final T body;

    public BaseResponseDto(String result, String message, T body) {
        this.result = result;
        this.message = message;
        this.body = body;
    }

    public static <T> BaseResponseDto<T> fail(String errorMessage) {
        return new BaseResponseDto<>("fail", errorMessage, null);
    }
}