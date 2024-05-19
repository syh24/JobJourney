package CSE4186.interview.utils;

import lombok.Getter;

public class FilterUtil {
    public static<T> FilterErrorResult<T> error(Integer errorCode, T message){
        return new FilterErrorResult<>("fail",errorCode,message);
    }

    @Getter
    public static class FilterErrorResult<T>{
        private final String result;
        private final Integer errorCode;
        private final T message;

        public FilterErrorResult(String result, Integer errorCode, T message){
            this.result=result;
            this.errorCode=errorCode;
            this.message=message;
        }
    }
}
