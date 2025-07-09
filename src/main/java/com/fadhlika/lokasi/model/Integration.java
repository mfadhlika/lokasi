package com.fadhlika.lokasi.model;

public record Integration(
        int userId,
        boolean owntracksEnable,
        String owntracksUsername,
        String owntracksPassword
        ) {

}
