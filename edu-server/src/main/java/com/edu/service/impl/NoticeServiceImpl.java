package com.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.dto.NoticeDto;
import com.edu.entity.SysNotice;
import com.edu.entity.SysNoticeRead;
import com.edu.mapper.SysNoticeMapper;
import com.edu.mapper.SysNoticeReadMapper;
import com.edu.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements NoticeService {

    private final SysNoticeReadMapper noticeReadMapper;

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getPrincipal();
    }

    @Override
    public Page<SysNotice> pageNotices(Integer pageNum, Integer size) {
        Page<SysNotice> page = new Page<>(pageNum, size);
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<SysNotice>()
                .eq(SysNotice::getStatus, 1)
                .orderByDesc(SysNotice::getPriority)
                .orderByDesc(SysNotice::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public SysNotice getNoticeDetail(Long id) {
        SysNotice notice = getById(id);
        if (notice != null) {
            Long userId = getCurrentUserId();
            SysNoticeRead exist = noticeReadMapper.selectOne(new LambdaQueryWrapper<SysNoticeRead>()
                    .eq(SysNoticeRead::getNoticeId, id)
                    .eq(SysNoticeRead::getUserId, userId));
            if (exist == null) {
                SysNoticeRead read = new SysNoticeRead();
                read.setNoticeId(id);
                read.setUserId(userId);
                noticeReadMapper.insert(read);
            }
        }
        return notice;
    }

    @Override
    public SysNotice createNotice(NoticeDto dto) {
        SysNotice notice = new SysNotice();
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setPublisherId(getCurrentUserId());
        notice.setNoticeType(dto.getNoticeType());
        notice.setPriority(dto.getPriority());
        notice.setTargetRole(dto.getTargetRole());
        notice.setStatus(1);
        notice.setPublishTime(LocalDateTime.now());
        save(notice);
        return notice;
    }

    @Override
    public SysNotice updateNotice(Long id, NoticeDto dto) {
        SysNotice notice = getById(id);
        if (notice == null) {
            throw new IllegalArgumentException("公告不存在");
        }
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setNoticeType(dto.getNoticeType());
        notice.setPriority(dto.getPriority());
        notice.setTargetRole(dto.getTargetRole());
        updateById(notice);
        return notice;
    }

    @Override
    public void deleteNotice(Long id) {
        removeById(id);
    }

    @Override
    public Long unreadCount() {
        Long userId = getCurrentUserId();
        Long total = baseMapper.selectCount(new LambdaQueryWrapper<SysNotice>()
                .eq(SysNotice::getStatus, 1));
        Long readCount = noticeReadMapper.selectCount(new LambdaQueryWrapper<SysNoticeRead>()
                .eq(SysNoticeRead::getUserId, userId));
        return total - readCount;
    }
}
