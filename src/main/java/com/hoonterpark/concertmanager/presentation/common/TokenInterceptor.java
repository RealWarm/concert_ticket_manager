package com.hoonterpark.concertmanager.presentation.common;

import com.hoonterpark.concertmanager.application.TokenFacade;
import com.hoonterpark.concertmanager.common.error.CustomException;
import com.hoonterpark.concertmanager.common.error.ErrorCode;
import com.hoonterpark.concertmanager.domain.enums.TokenStatus;
import com.hoonterpark.concertmanager.presentation.controller.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {
    private final TokenFacade tokenFacade;

    // 요청 처리 전 실행되는 메서드
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();


        // 토큰 없이 요청 허용
        if (requestURI.equals("/api/token") ||
                requestURI.equals("/api/reserves") ||
                requestURI.equals("/api/pay")||
                requestURI.matches("/api/.*/available-seats")) {
            return true;
        }//if

        // 요청 헤더에서 Authorization 값을 가져옴
        String tokenValue = request.getHeader("Authorization");

        // Authorization 헤더가 존재하지 않음, 요청 권한 없음
        if (tokenValue == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        } else {
            try {
                TokenResponse.TokenQueueResponse queueToken = tokenFacade.getQueueToken(tokenValue);

                // 토큰이 활성 상태가 아님, 요청 권한 없음
                if (!queueToken.tokenStatus().equals(TokenStatus.ACTIVE)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    throw new CustomException(ErrorCode.UNAUTHORIZED);
                }//if-2

                return true; // 토큰 활성 상태, 요청 권한 있음
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
