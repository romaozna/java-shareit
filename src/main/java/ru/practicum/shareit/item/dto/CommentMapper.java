package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class CommentMapper {

    public static Comment toComment(CommentDto commentDto, Item item, User user) {
        return Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(user)
                .created(commentDto.getCreated())
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }
}
