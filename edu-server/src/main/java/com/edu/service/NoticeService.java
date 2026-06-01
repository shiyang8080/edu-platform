package com.edu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.dto.NoticeDto;
import com.edu.entity.SysNotice;
import java.util.Map;

public interface NoticeService {
    Page<SysNotice> pageNotices(Integer page, Integer size);
    SysNotice getNoticeDetail(Long id);
    SysNotice createNotice(NoticeDto dto);
    SysNotice updateNotice(Long id, NoticeDto dto);
    void deleteNotice(Long id);
    Long unreadCount();
}
