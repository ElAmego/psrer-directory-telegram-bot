package by.psrer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class ImageDTO implements Serializable {
    private Long chatId;
    private byte[] fileBytes;
    private String fileName;
}
