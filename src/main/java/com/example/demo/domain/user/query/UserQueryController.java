package com.example.demo.domain.user.query;

import com.example.demo.domain.role.RoleService;
import com.example.demo.domain.role.dto.RoleDTO;
import com.example.demo.domain.role.dto.RoleMapper;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.dto.UserDTO;
import com.example.demo.domain.user.dto.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/user")
public class UserQueryController {

    private final UserQueryService userQueryService;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    @Autowired
    public UserQueryController(UserQueryService userQueryService, RoleService roleService, UserMapper userMapper, RoleMapper roleMapper) {
        this.userQueryService = userQueryService;
        this.roleService = roleService;
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get User")
    @PreAuthorize("hasAuthority('ADMIN_READ') || @userPermissionEvaluator.isUser(authentication.principal.user, #id)")
    public ResponseEntity<UserDTO> retrieveById(@PathVariable UUID id) {
        User user = userQueryService.findById(id);
        return new ResponseEntity<>(userMapper.toDTO(user), HttpStatus.OK);
    }

    @GetMapping("/roles")
    @Operation(summary= "Get all roles present")
    @PreAuthorize("hasAuthority('ADMIN_READ')")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity
                .ok()
                .body(roleMapper.toDTOs(roleService.getAllRoles()));
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> allUsers = userQueryService.findAll();

        return new ResponseEntity<>(userMapper.toDTOs(allUsers), HttpStatus.OK);
    }
}
