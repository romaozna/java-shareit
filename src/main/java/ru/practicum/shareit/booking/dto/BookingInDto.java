package ru.practicum.shareit.booking.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookingInDto {
    @NotNull(groups = {Create.class})
    private Long itemId;
    @NotNull(groups = {Create.class})
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    private LocalDateTime start;
    @NotNull(groups = {Create.class})
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    private LocalDateTime end;
}
