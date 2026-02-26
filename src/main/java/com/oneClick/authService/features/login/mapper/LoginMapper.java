package com.oneClick.authService.features.login.mapper;

import com.oneClick.authService.features.login.dto.response.LoginResponse;
import com.oneClick.authService.shared.domain.entity.Account;
import com.oneClick.authService.shared.domain.entity.Role;
import com.oneClick.authService.shared.domain.entity.Session;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;


@Mapper(componentModel = "spring")
public interface LoginMapper {

    @Mapping(target = "accountId", source = "account.accountId")
    @Mapping(target = "status", source = "account.status")
    @Mapping(target = "email", source = "account.email")
    @Mapping(target = "emailVerifiedAt", ignore = true)
    @Mapping(target = "roles", source = "account.roles")
    @Mapping(target = "accessToken", source = "accessToken")
    @Mapping(target = "tokenType", constant = "Bearer")
    @Mapping(target = "expiresIn", source = "expiresIn")
    @Mapping(target = "sessionId", ignore = true) //source = "session.sessionId")
    LoginResponse toLoginResponse(Account account, String accessToken, Long expiresIn);

    @Named("instantToBoolean")
    default Boolean instantToBoolean(Instant value){
        return value != null;
    }

    default String roleToRoleName(Role role){
        return role.getRoleName();
    }
}
