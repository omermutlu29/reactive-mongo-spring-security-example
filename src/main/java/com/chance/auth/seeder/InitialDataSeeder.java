package com.chance.auth.seeder;

import com.chance.auth.entity.User;
import com.chance.auth.enums.EnumRole;
import com.chance.auth.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitialDataSeeder implements ApplicationListener<ApplicationStartedEvent> {

    private final UserRepository userRepository;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        userRepository
                .findByUsername("admin")
                .doOnNext(i -> System.out.println("User already created with following id : " + i.getId()))
                .switchIfEmpty(createAdminUser())
                .subscribe();
    }

    private Mono<User> createAdminUser() {
        User userDetails = User.builder()
                .id(UUID.randomUUID().toString())
                .username("admin")
                .msisdn("5xxxxxxxxx")
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .isAccountNonExpired(true)
                .isEnabled(true)
                .createdAt(LocalDateTime.now())
                .email("aliveli@xyz.com")
                .roles(List.of(EnumRole.ROLE_USER, EnumRole.ROLE_ADMIN))
                .build();
        return userRepository.save(userDetails).doOnNext(user -> log.info("Admin user created successfully"));
    }
}
