package com.everything.prompt.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface StatsMapper {

    @Select("""
        SELECT
            COUNT(DISTINCT id) as total_users,
            COUNT(CASE WHEN role = 'VIP' THEN 1 END) as vip_users,
            COUNT(CASE WHEN role = 'USER' THEN 1 END) as normal_users,
            COUNT(CASE WHEN create_time >= CURDATE() THEN 1 END) as today_new_users
        FROM sys_user
        WHERE deleted = 0
        """)
    Map<String, Object> getUserStats();

    @Select("SELECT COUNT(*) as total_visits FROM prompt_template WHERE status = 1")
    Map<String, Object> getVisitStats();

    @Select("""
        SELECT
            COUNT(*) as total_calls,
            COALESCE(SUM(tokens_used), 0) as total_tokens,
            COUNT(CASE WHEN create_time >= CURDATE() THEN 1 END) as today_calls
        FROM ai_call_log
        WHERE create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY)
        """)
    Map<String, Object> getAiStats();

    @Select("""
        SELECT title, usage_count as count
        FROM prompt_template
        WHERE status = 1
        ORDER BY usage_count DESC
        LIMIT 10
        """)
    List<Map<String, Object>> getTopTemplates();
}
