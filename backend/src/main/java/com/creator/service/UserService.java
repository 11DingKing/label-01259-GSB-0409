package com.creator.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creator.common.BusinessException;
import com.creator.common.PageResult;
import com.creator.dto.LoginDTO;
import com.creator.dto.RechargeDTO;
import com.creator.dto.RegisterDTO;
import com.creator.entity.Creator;
import com.creator.entity.User;
import com.creator.mapper.CreatorMapper;
import com.creator.mapper.UserMapper;
import com.creator.util.JwtUtil;
import com.creator.util.UserContext;
import com.creator.vo.LoginVO;
import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    private final CreatorMapper creatorMapper;
    private final JwtUtil jwtUtil;
    
    public LoginVO login(LoginDTO dto) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, dto.getUsername())
        );
        
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        LoginVO vo = new LoginVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setEmail(user.getEmail());
        vo.setBalance(user.getBalance());
        vo.setRole(user.getRole());
        vo.setToken(token);
        
        if (user.getRole() == 1) {
            Creator creator = creatorMapper.selectOne(
                    new LambdaQueryWrapper<Creator>()
                            .eq(Creator::getUserId, user.getId())
                            .eq(Creator::getStatus, 1)
            );
            if (creator != null) {
                vo.setCreatorId(creator.getId());
            }
        }
        
        log.info("用户登录成功: {}", user.getUsername());
        return vo;
    }
    
    @Transactional
    public void register(RegisterDTO dto) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, dto.getUsername())
        );
        
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setBalance(BigDecimal.ZERO);
        user.setRole(0);
        user.setStatus(1);
        
        userMapper.insert(user);
        log.info("用户注册成功: {}", user.getUsername());
    }
    
    public User getCurrentUser() {
        Long userId = UserContext.getUserId();
        return userMapper.selectById(userId);
    }
    
    @Transactional
    public void updateProfile(User user) {
        Long userId = UserContext.getUserId();
        User existUser = userMapper.selectById(userId);
        
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        existUser.setNickname(user.getNickname());
        existUser.setAvatar(user.getAvatar());
        existUser.setEmail(user.getEmail());
        
        userMapper.updateById(existUser);
        log.info("用户更新资料: {}", existUser.getUsername());
    }
    
    @Transactional
    public void recharge(RechargeDTO dto) {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setBalance(user.getBalance().add(dto.getAmount()));
        userMapper.updateById(user);
        
        log.info("用户充值成功: {}, 金额: {}", user.getUsername(), dto.getAmount());
    }
    
    public PageResult<User> getUserList(Integer page, Integer size, String keyword) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(User::getUsername, keyword)
                    .or()
                    .like(User::getNickname, keyword);
        }
        
        wrapper.orderByDesc(User::getCreatedAt);
        
        Page<User> result = userMapper.selectPage(pageParam, wrapper);
        
        result.getRecords().forEach(u -> u.setPassword(null));
        
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Transactional
    public void updateUserStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setStatus(status);
        userMapper.updateById(user);
        
        log.info("更新用户状态: {}, status: {}", user.getUsername(), status);
    }
    
    @Transactional
    public void resetPassword(String username, String newPassword) {
        if (username == null || username.isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new BusinessException("密码长度至少6位");
        }
        
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
        );
        
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setPassword(BCrypt.hashpw(newPassword));
        userMapper.updateById(user);
        
        log.info("用户重置密码: {}", username);
    }
}
