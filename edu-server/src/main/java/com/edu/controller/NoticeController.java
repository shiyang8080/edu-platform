package com.edu.controller;

import com.edu.common.Result;
import com.edu.dto.NoticeDto;
import com.edu.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer page,
                          @RequestParam(defaultValue = "10") Integer size) {
        return Result.ok(noticeService.pageNotices(page, size));
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        return Result.ok(noticeService.getNoticeDetail(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> create(@Valid @RequestBody NoticeDto dto) {
        return Result.ok(noticeService.createNotice(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody NoticeDto dto) {
        return Result.ok(noticeService.updateNotice(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> delete(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return Result.ok("删除成功");
    }

    @GetMapping("/unread-count")
    public Result<?> unreadCount() {
        return Result.ok(noticeService.unreadCount());
    }
}
