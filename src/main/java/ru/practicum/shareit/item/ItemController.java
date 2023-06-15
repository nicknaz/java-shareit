package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;


import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                          @Valid @RequestBody ItemDto dto) {
        ItemDto newItem = itemService.add(dto, ownerId);
        log.info("Create item by itemId={}", newItem.getId());
        return newItem;
    }

    @GetMapping
    public List<ItemDto> getAllByOwner(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Get item by owner with userId={}", userId);
        return itemService.getAllByOwner(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable("id") Long itemId) {
        log.info("Get item by itemId={}", itemId);
        return itemService.getById(itemId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                          @PathVariable("id") Long itemId,
                          @RequestBody ItemDto dto) {
        log.info("Update item by itemId={}", itemId);
        return itemService.update(itemId, dto, ownerId);
    }

    @DeleteMapping("/{id}")
    public HttpStatus delete(@PathVariable("id") Long itemId) {
        log.info("Delete item by itemId={}", itemId);
        itemService.delete(itemId);
        return HttpStatus.OK;
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String text) {
        log.info("Search by text={}", text);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemService.search(text);
    }
}
