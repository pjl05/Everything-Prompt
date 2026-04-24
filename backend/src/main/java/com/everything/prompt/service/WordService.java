package com.everything.prompt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.everything.prompt.entity.UserWord;
import com.everything.prompt.exception.BusinessException;
import com.everything.prompt.mapper.UserWordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WordService {

    private final UserWordMapper wordMapper;
    private final StringRedisTemplate redisTemplate;

    public List<UserWord> getUserWords(Long userId) {
        return wordMapper.selectList(
            new LambdaQueryWrapper<UserWord>()
                .eq(UserWord::getUserId, userId)
                .eq(UserWord::getStatus, 1)
                .orderByDesc(UserWord::getCreateTime)
        );
    }

    @CacheEvict(value = "userWords", key = "#userId")
    public UserWord addWord(Long userId, String title, String content, String groupName) {
        UserWord word = new UserWord();
        word.setUserId(userId);
        word.setTitle(title);
        word.setContent(content);
        word.setGroupName(groupName != null ? groupName : "默认分组");
        word.setStatus(1);
        wordMapper.insert(word);
        return word;
    }

    @CacheEvict(value = "userWords", key = "#userId")
    public UserWord updateWord(Long userId, Long wordId, String title, String content, String groupName) {
        UserWord word = wordMapper.selectOne(
            new LambdaQueryWrapper<UserWord>()
                .eq(UserWord::getId, wordId)
                .eq(UserWord::getUserId, userId)
        );
        if (word == null) {
            throw new BusinessException(404, "词条不存在");
        }
        word.setTitle(title);
        word.setContent(content);
        if (groupName != null) {
            word.setGroupName(groupName);
        }
        wordMapper.updateById(word);
        return word;
    }

    @CacheEvict(value = "userWords", key = "#userId")
    public void deleteWord(Long userId, Long wordId) {
        wordMapper.delete(
            new LambdaQueryWrapper<UserWord>()
                .eq(UserWord::getId, wordId)
                .eq(UserWord::getUserId, userId)
        );
    }
}
