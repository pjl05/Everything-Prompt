package com.everything.prompt.service;

import com.everything.prompt.mapper.StatsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsMapper statsMapper;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userStats", statsMapper.getUserStats());
        stats.put("visitStats", statsMapper.getVisitStats());
        stats.put("aiStats", statsMapper.getAiStats());
        stats.put("topTemplates", statsMapper.getTopTemplates());
        return stats;
    }
}
