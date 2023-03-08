package com.example.demo.core.generic;

import com.example.demo.core.exception.NotCheckedException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Optional;

/**
 * This class is used to bundle the status of a service execution and an item together.
 *
 * @param <T>
 */
public class StatusOr<T> {
    private final Optional<T> item;

    @Getter
    private final HttpStatus status;

    private boolean checkedForItemStatus = false;


    public StatusOr(HttpStatus status) {
        this.status = status;
        item = Optional.empty();
    }

    public StatusOr(Optional<T> item) {
        this.item = item;
        this.status = HttpStatus.OK;
    }

    public StatusOr(T item) {
        this.item = Optional.of(item);
        this.status = HttpStatus.OK;
    }

    public boolean isOkAndPresent() {
        checkedForItemStatus = true;
        return item.isPresent() && isOk();
    }

    public boolean isOk() {
        return status == HttpStatus.OK;
    }

    public T getItem() throws NotCheckedException {
        if (!checkedForItemStatus) {
            throw new NotCheckedException("You need to check if the item is present and valid using isOkAndPresent() before accessing it.");
        }
        return item.get();
    }
}
