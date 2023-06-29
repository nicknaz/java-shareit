package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForResponse;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    private ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDtoForResponse create(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Create request by userId={}", userId);
        return itemRequestService.create(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDtoForResponse> getForUser(@RequestHeader(name = "X-Sharer-User-Id")
                                                          Long userId) {
        log.info("Get requests by userId={}", userId);
        return itemRequestService.getForUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoForResponse> getOtherUsers(@RequestHeader(name = "X-Sharer-User-Id")
                                                      Long userId,
                                                      @RequestParam(name = "from", defaultValue = "0")
                                                      Integer from,
                                                      @RequestParam(name = "size", defaultValue = "10")
                                                      Integer size) {
        log.info("Get all requests by userId={}", userId);
        return itemRequestService.getOtherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoForResponse getRequestById(@RequestHeader(name = "X-Sharer-User-Id")
                                                 Long userId,
                                                 @PathVariable(name = "requestId") Long requestId) {
        log.info("Get request by requestId={}", requestId);
        return itemRequestService.getRequestById(userId, requestId);
    }
}
