package ru.yandex.practicum.catsgram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;
import ru.yandex.practicum.catsgram.service.SortOrder;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public Collection<Post> findAll(@RequestParam(required = false) Long from,
                                    @RequestParam(defaultValue = "10") Long size,
                                    @RequestParam(defaultValue = "desc") String sort) {
        if (SortOrder.from(sort) == null) {
            throw new ParameterNotValidException("sort", "Получен параметр sort = " + sort + ", должен быть asc или desc");
        }
        if (size <= 0) {
            throw new ParameterNotValidException("size", "Некорректный размер выборки. Размер должен быть больше нуля");
        }
        if (from != null && from < 0) {
            throw new ParameterNotValidException("from", "Некорректное начало выборки. Начало не может быть отрицательным");
        }

        return postService.findAll(from, size, sort);
    }

    @GetMapping("/post/{id}")
    public Post getPostById(@PathVariable Long id) {
        Optional<Post> maybePost = postService.getPostById(id);
        if (maybePost.isEmpty()) {
            throw new NotFoundException("Пост с id = " + id + " не найден");
        }
        return maybePost.get();
    }

//    @PostMapping("/posts")
//    @ResponseStatus(HttpStatus.CREATED)
//    public Post create(@RequestBody Post post) {
//        return postService.create(post);
//    }

    @PutMapping("/posts")
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }
}