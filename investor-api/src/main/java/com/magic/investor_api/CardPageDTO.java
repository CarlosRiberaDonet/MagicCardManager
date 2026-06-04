package com.magic.investor_api;

import com.magic.investor_api.scryfall.dto.ScryfallCardDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardPageDTO {

    private int totalCards;
    private int page;
    private List<ScryfallCardDTO> cardDTOList;
}
