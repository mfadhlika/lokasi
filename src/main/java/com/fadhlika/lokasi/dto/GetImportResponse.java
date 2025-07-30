package com.fadhlika.lokasi.dto;

import java.util.List;

import com.fadhlika.lokasi.model.Import;

public class GetImportResponse extends Response {
    public List<Import> imports;

    public GetImportResponse(List<Import> imports) {
        super("success");
        this.imports = imports;
    }
}
