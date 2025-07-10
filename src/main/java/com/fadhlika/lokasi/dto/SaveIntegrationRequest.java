package com.fadhlika.lokasi.dto;

public record SaveIntegrationRequest(
        boolean owntracksEnable,
        String owntracksUsername,
        String owntracksPassword,
        boolean overlandEnable,
        String overlandApiKey
        ) {

}
