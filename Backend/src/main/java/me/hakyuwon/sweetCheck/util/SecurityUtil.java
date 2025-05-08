package me.hakyuwon.sweetCheck.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SecurityUtil {

    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();  // 올바르게 uid 반환
        } else if (principal instanceof String) {
            return (String) principal;
        } else {
            log.error("Unexpected principal type: {}", principal.getClass().getName());
            throw new RuntimeException("Invalid principal type");
        }
    }
}
