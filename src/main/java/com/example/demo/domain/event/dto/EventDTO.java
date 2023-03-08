package com.example.demo.domain.event.dto;

import com.example.demo.core.generic.AbstractDTO;
import com.example.demo.domain.user.dto.UserDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class EventDTO extends AbstractDTO {
    private String eventName;
    private Integer participantsLimit;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String description;
    private String imageUrl;
    private UserDTO eventOwner;
}
