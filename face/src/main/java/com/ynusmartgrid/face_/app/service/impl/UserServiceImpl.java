package com.ynusmartgrid.face_.app.service.impl;

import com.ynusmartgrid.face_.app.entity.User;
import com.ynusmartgrid.face_.app.mapper.UserMapper;
import com.ynusmartgrid.face_.app.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wjs
 * @since 2022-03-03
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
