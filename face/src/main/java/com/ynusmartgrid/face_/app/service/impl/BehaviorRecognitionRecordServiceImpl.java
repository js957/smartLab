package com.ynusmartgrid.face_.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ynusmartgrid.face_.app.entity.BehaviorRecognitionRecord;
import com.ynusmartgrid.face_.app.mapper.BehaviorRecognitionRecordMapper;
import com.ynusmartgrid.face_.app.service.IBehaviorRecognitionRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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
public class BehaviorRecognitionRecordServiceImpl extends ServiceImpl<BehaviorRecognitionRecordMapper, BehaviorRecognitionRecord> implements IBehaviorRecognitionRecordService {

    @Autowired
    BehaviorRecognitionRecordMapper behaviorRecognitionRecordMapper;

    @Override
    public IPage<BehaviorRecognitionRecord> selectPage(int pageIndex, int pageSize, QueryWrapper<BehaviorRecognitionRecord> queryWrapper) {
        IPage<BehaviorRecognitionRecord> iPage =new Page<>(pageIndex,pageSize);
        return behaviorRecognitionRecordMapper.selectPage(iPage,queryWrapper);
    }
}
