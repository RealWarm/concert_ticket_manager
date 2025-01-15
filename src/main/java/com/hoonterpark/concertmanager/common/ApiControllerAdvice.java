package com.hoonterpark.concertmanager.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Slf4j
@RestControllerAdvice
class ApiControllerAdvice extends ResponseEntityExceptionHandler {

//    @ExceptionHandler(value = IllegalArgumentException.class)
//    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
//        return ResponseEntity.status(500).body(new ErrorResponse("400", e.getMessage()));
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
//        log.error("에러:", ex);
//        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
//                ErrorCode.INTERNAL_SERVER_ERROR.getDescription());
//        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    @ExceptionHandler(CustomException.class)
//    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
//        log.error("에러:", ex);
//        ErrorCode errorCode = ex.getErrorCode();
//        ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), errorCode.getDescription());
//        return new ResponseEntity<>(errorResponse, getHttpStatus(errorCode));
//    }

    private HttpStatus getHttpStatus(ErrorCode errorCode) {
        switch (errorCode) {
            case BAD_REQUEST:
                return HttpStatus.BAD_REQUEST;
            case NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            case UNAUTHORIZED:
                return HttpStatus.UNAUTHORIZED;
            case INTERNAL_SERVER_ERROR:
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }


}//end
