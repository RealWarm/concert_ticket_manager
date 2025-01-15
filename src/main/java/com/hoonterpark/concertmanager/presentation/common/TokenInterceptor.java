package com.hoonterpark.concertmanager.presentation.common;

import com.hoonterpark.concertmanager.application.TokenUseCase;
import com.hoonterpark.concertmanager.common.CustomException;
import com.hoonterpark.concertmanager.common.ErrorCode;
import com.hoonterpark.concertmanager.domain.enums.TokenStatus;
import com.hoonterpark.concertmanager.presentation.controller.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    TokenUseCase tokenUseCase;

    // 요청 처리 전 실행되는 메서드
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("요청 URI: {}", requestURI);

        if (requestURI.equals("/api/point/charge") ||
                requestURI.equals("/api/point") ||
                requestURI.equals("/api/token/issue")||
                requestURI.equals("/api/token/status")||
                requestURI.matches("/api/.*/saveConcert")||
                requestURI.matches("/api/.*/available-dates")) {
            log.info("URI에 대한 토큰 없이 요청 허용: {}", requestURI);
            return true;
        }//if


        // 요청 헤더에서 Authorization 값을 가져옴
        String tokenValue = request.getHeader("Authorization");
        log.info("Authorization 헤더: {}", tokenValue);

        if (tokenValue == null) {
            log.warn("Authorization 헤더가 존재하지 않음, 요청 권한 없음");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        } else {
            try {
                TokenResponse.TokenQueueResponse queueToken = tokenUseCase.getQueueToken(tokenValue);
                log.info("토큰 상태: {}", queueToken.tokenStatus());

                if (!queueToken.tokenStatus().equals(TokenStatus.ACTIVE)) {
                    log.warn("토큰이 활성 상태가 아님, 요청 권한 없음");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    throw new CustomException(ErrorCode.UNAUTHORIZED);
                }//if-2

                return true; //"토큰이 활성 상태, 요청 권한 있음"
            } catch (Exception e) {
                log.error("토큰 검증 중 오류: {}", e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                throw new CustomException(ErrorCode.UNAUTHORIZED, e.getMessage());
            }//try-catch
        }//if-1

    }//preHandle



    // 요청 처리 후 실행되는 메서드
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    // 요청 처리 완료 후 실행되는 메서드
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

}//end
