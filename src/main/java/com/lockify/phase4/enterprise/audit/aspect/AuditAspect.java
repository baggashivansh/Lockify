package com.lockify.phase4.enterprise.audit.aspect;

import com.lockify.phase4.enterprise.audit.annotation.Auditable;
import com.lockify.phase4.enterprise.audit.service.AuditService;
import com.lockify.phase1.coreauth.security.LockifyUserDetails;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @Auditable methods ke around audit log likhta hai.
 * Success/failure dono cases me entry ban sakti hai.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;

    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        Long userId = resolveCurrentUserId();
        String argSummary = summarizeArgs(joinPoint.getArgs());

        try {
            Object result = joinPoint.proceed();
            auditService.logAction(userId, auditable.action(), auditable.resource(),
                    "status=SUCCESS args=" + argSummary);
            return result;
        } catch (Throwable ex) {
            auditService.logAction(userId, auditable.action(), auditable.resource(),
                    "status=FAILURE error=" + ex.getClass().getSimpleName());
            throw ex;
        }
    }

    private Long resolveCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof LockifyUserDetails details) {
            return details.getId();
        }
        return null;
    }

    private String summarizeArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        return Arrays.stream(args)
                .map(arg -> arg == null ? "null" : arg.getClass().getSimpleName())
                .collect(Collectors.joining(",", "[", "]"));
    }
}
