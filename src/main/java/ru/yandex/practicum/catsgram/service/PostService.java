package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.util.*;

// Указываем, что класс PostService - является бином и его
// нужно добавить в контекст приложения
@Service
@RequiredArgsConstructor
public class PostService {
    private final Map<Long, Post> posts = new HashMap<>();
    private final UserService userService;

    public Collection<Post> findAll(Long from, Long size, String sort) {
        Collection<Post> response = posts.values();
        switch (Objects.requireNonNull(SortOrder.from(sort))) {
            case ASCENDING -> {
                response = response.stream()
                        .sorted(Comparator.comparing(Post::getPostDate))
                        .toList();
            }
            case DESCENDING -> {
                response = response.stream()
                        .sorted(Comparator.comparing(Post::getPostDate).reversed())
                        .toList();
            }
            default -> throw new IllegalStateException("Unexpected value: " + SortOrder.from(sort));
        }
        if (from != null) {
            response = response.stream()
                    .skip(from)
                    .toList();
        }
        if (size != null) {
            response = response.stream()
                    .limit(size)
                    .toList();
        }
        return response;
    }

    public Optional<Post> getPostById(Long id) {
        return Optional.ofNullable(posts.get(id));
    }

    public Post create(Post post) {
        if (userService.findUserById(post.getAuthorId()).isEmpty()) {
            throw new ConditionsNotMetException("Автор с id = " + post.getAuthorId() + " не найден");
        }
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }
        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}