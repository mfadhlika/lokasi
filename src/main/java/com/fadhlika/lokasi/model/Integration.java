package com.fadhlika.lokasi.model;

public record Integration(
                int userId,
                String owntracksUsername,
                String owntracksPassword,
                String overlandApiKey) {
        public Integration(int userId) {
                this(userId, "", "", "");
        }

        public Integration {
                if (owntracksUsername == null)
                        owntracksUsername = "";
                if (owntracksPassword == null)
                        owntracksPassword = "";
                if (overlandApiKey == null)
                        overlandApiKey = "";
        }

}
