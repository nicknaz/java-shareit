package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;


import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private ItemServiceImpl itemService;

    @Autowired
    public ItemController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                          @Valid @RequestBody ItemDto dto) {
        return itemService.add(dto, ownerId);
    }

    @GetMapping
    public List<ItemDto> getAllByOwner(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemService.getAllByOwner(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable("id") Long itemId) {
        return itemService.getById(itemId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                          @PathVariable("id") Long itemId,
                          @RequestBody ItemDto dto) {
        return itemService.update(itemId, dto, ownerId);
    }

    @DeleteMapping("/{id}")
    public HttpStatus delete(@PathVariable("id") Long userId) {
        itemService.delete(userId);
        return HttpStatus.OK;
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String text) {
        return itemService.search(text);
    }
}
