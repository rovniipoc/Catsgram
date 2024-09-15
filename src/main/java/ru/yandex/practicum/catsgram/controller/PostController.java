package ru.yandex.practicum.catsgram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;

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

    @PostMapping("/posts")
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping("/posts")
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }
}