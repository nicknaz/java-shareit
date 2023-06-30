package ru.practicum.gateway.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.item.dto.CommentDto;
import ru.practicum.gateway.item.dto.ItemDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private ItemClient itemClient;

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                                         @Valid @RequestBody ItemDto dto) {
        log.info("Create item");
        return itemClient.create(ownerId, dto);
    }

    @GetMapping
    public ResponseEntity<Object>  getAllByOwner(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Get item by owner with userId={}", userId);
        return itemClient.findAllByOwner(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object>  getById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                    @PathVariable("id") Long itemId) {
        log.info("Get item by itemId={}", itemId);
        return itemClient.findById(userId, itemId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object>  update(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                          @PathVariable("id") Long itemId,
                          @RequestBody ItemDto dto) {
        log.info("Update item by itemId={}", itemId);
        return itemClient.update(ownerId, itemId, dto);
    }

    @DeleteMapping("/{id}")
    public HttpStatus delete(@PathVariable("id") Long itemId) {
        log.info("Delete item by itemId={}", itemId);
        itemClient.delete(itemId);
        return HttpStatus.OK;
    }

    @GetMapping("/search")
    public ResponseEntity<Object>  search(@RequestParam("text") String text) {
        log.info("Search by text={}", text);
        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object>  createComment(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                             @PathVariable("itemId") Long itemId,
                             @Valid @RequestBody CommentDto comment) {
        log.info("Create comment by userId={} for itemId = {}", userId, itemId);
        return itemClient.createComment(itemId, userId, comment);
    }
}
