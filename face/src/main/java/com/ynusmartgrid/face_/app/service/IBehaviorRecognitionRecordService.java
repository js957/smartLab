package com.ynusmartgrid.face_.app.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ynusmartgrid.face_.app.entity.BehaviorRecognitionRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wjs
 * @since 2022-03-03
 */
public interface IBehaviorRecognitionRecordService extends IService<BehaviorRecognitionRecord> {

        public IPage<BehaviorRecognitionRecord> selectPage(int pageIndex, int pageSize, QueryWrapper<BehaviorRecognitionRecord> queryWrapper);
}
