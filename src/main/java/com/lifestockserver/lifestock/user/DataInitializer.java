//더미 데이터를 만드는 코드입니다!!!!
/*

package com.lifestockserver.lifestock.user;

import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.domain.UserRole;
import com.lifestockserver.lifestock.user.domain.UserStatus;
import com.lifestockserver.lifestock.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() <= 10) {
            for (int i = 1; i <= 50; i++) {
                User user = new User();
                user.setUsername("user" + i);
                user.setPassword(passwordEncoder.encode("password" + i));  // 비밀번호 인코딩
                user.setRealName("Real Name " + i);
                user.setDisplayName("Display Name " + i);
                user.setEmail("user" + i + "@example.com");
                user.setPhoneNumber("010-1234-567" + i);
                user.setStatus(UserStatus.ACTIVE);
                user.setRole(UserRole.USER);

                userRepository.save(user);
            }
            System.out.println("더미 데이터 생성 완료");
        } else {
            System.out.println("이미 데이터가 존재합니다.");
        }
    }
}

*/