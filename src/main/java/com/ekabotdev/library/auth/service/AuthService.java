package com.ekabotdev.library.auth.service;

import com.ekabotdev.library.auth.dto.LoginRequest;
import com.ekabotdev.library.auth.dto.LoginResponse;
import com.ekabotdev.library.auth.dto.RegisterRequest;
import com.ekabotdev.library.auth.dto.RegisterResponse;
import com.ekabotdev.library.enums.MembershipStatus;
import com.ekabotdev.library.enums.Role;
import com.ekabotdev.library.member.entity.Member;
import com.ekabotdev.library.member.exception.EmailAlreadyExistsException;
import com.ekabotdev.library.member.repository.MemberRepository;
import com.ekabotdev.library.security.CustomUserDetails;
import com.ekabotdev.library.security.JwtService;
import com.ekabotdev.library.user.entity.User;
import com.ekabotdev.library.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public RegisterResponse register(RegisterRequest request){

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email is already exists");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password())
        );
        user.setRole(Role.MEMBER);

        User savedUser = userRepository.save(user);

        Member member = new Member();
        member.setUser(user);
        member.setPhone(request.phone());
        member.setMembershipStatus(MembershipStatus.ACTIVE);

        memberRepository.save(member);

        return  new RegisterResponse(
               savedUser.getId(),
               savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        );
    }

    public LoginResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email(),
                                request.password()
                        )
                );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(
                userDetails.getUser()
        );

        return new LoginResponse(
                token,
                "Bearer"
        );
    }
}
