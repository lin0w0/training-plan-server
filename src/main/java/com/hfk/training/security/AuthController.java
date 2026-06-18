package com.hfk.training.security;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hfk.training.common.BusinessException;
import com.hfk.training.common.Result;
import com.hfk.training.modules.system.entity.SysRole;
import com.hfk.training.modules.system.entity.SysUser;
import com.hfk.training.modules.system.mapper.SysRoleMapper;
import com.hfk.training.modules.system.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 认证控制器 - 登录、登出、验证码
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "登录、登出、验证码等接口")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final StringRedisTemplate redisTemplate;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest loginRequest) {
        // 验证码校验 (开发环境可跳过)
        // String captchaKey = "captcha:" + loginRequest.getCaptchaKey();
        // String captchaCode = redisTemplate.opsForValue().get(captchaKey);
        // if (captchaCode == null || !captchaCode.equalsIgnoreCase(loginRequest.getCaptcha())) {
        //     return Result.badRequest("验证码错误或已过期");
        // }

        try {
            // Spring Security 认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 查询用户信息
            SysUser user = sysUserMapper.selectOne(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getUsername, loginRequest.getUsername())
            );

            // 查询角色和权限
            List<SysRole> roles = sysRoleMapper.findRolesByUserId(user.getId());
            List<String> permissions = sysRoleMapper.findPermissionsByUserId(user.getId());

            // 更新最后登录时间
            user.setLastLoginTime(java.time.LocalDateTime.now());
            sysUserMapper.updateById(user);

            // 生成 JWT
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("userType", user.getUserType());
            claims.put("roles", roles.stream().map(SysRole::getRoleCode).collect(Collectors.toList()));

            String token = jwtUtils.generateToken(user.getId(), user.getUsername(), claims);

            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("accessToken", token);
            result.put("tokenType", "Bearer");
            result.put("expiresIn", 86400);

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("realName", user.getRealName());
            userInfo.put("email", user.getEmail());
            userInfo.put("phone", user.getPhone());
            userInfo.put("avatar", user.getAvatar());
            userInfo.put("userType", user.getUserType());
            userInfo.put("roles", roles);
            result.put("userInfo", userInfo);
            result.put("permissions", permissions);
            result.put("roles", roles);

            return Result.success("登录成功", result);
        } catch (BadCredentialsException e) {
            return Result.unauthorized("用户名或密码错误");
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public Result<Void> logout() {
        SecurityContextHolder.clearContext();
        return Result.ok("退出成功");
    }

    @GetMapping("/captcha")
    @Operation(summary = "获取验证码")
    public void captcha(jakarta.servlet.http.HttpServletResponse response) throws Exception {
        // 使用 Hutool 生成验证码
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 20);
        String code = captcha.getCode();
        String key = UUID.randomUUID().toString(true);

        // 存入 Redis，5分钟过期
        redisTemplate.opsForValue().set("captcha:" + key, code, 5, TimeUnit.MINUTES);

        response.setContentType("image/png");
        response.setHeader("Captcha-Key", key);
        captcha.write(response.getOutputStream());
    }

    @GetMapping("/user-info")
    @Operation(summary = "获取当前用户信息")
    public Result<Map<String, Object>> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null) {
            return Result.unauthorized("用户不存在");
        }

        List<SysRole> roles = sysRoleMapper.findRolesByUserId(user.getId());
        List<String> permissions = sysRoleMapper.findPermissionsByUserId(user.getId());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("email", user.getEmail());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("userType", user.getUserType());
        userInfo.put("roles", roles);
        userInfo.put("permissions", permissions);

        return Result.success(userInfo);
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
        private String captcha;
        private String captchaKey;
        private String userType;
    }
}
