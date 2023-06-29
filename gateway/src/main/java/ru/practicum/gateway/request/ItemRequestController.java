package ru.practicum.gateway.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    private ItemRequestClient itemRequestClient;

    @Autowired
    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(name = "X-Sharer-User-Id") @PositiveOrZero Long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Create request by userId={}", userId);
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getForUser(@RequestHeader(name = "X-Sharer-User-Id")
                                                          @PositiveOrZero Long userId) {
        log.info("Get requests by userId={}", userId);
        return itemRequestClient.findByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsers(@RequestHeader(name = "X-Sharer-User-Id")
                                                      @PositiveOrZero Long userId,
                                                      @RequestParam(name = "from", defaultValue = "0")
                                                      @PositiveOrZero Integer from,
                                                      @RequestParam(name = "size", defaultValue = "10")
                                                      @Positive Integer size) {
        log.info("Get all requests by userId={}", userId);
        return itemRequestClient.findAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(name = "X-Sharer-User-Id")
                                                 @PositiveOrZero Long userId,
                                                 @PathVariable(name = "requestId") Long requestId) {
        log.info("Get request by requestId={}", requestId);
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
