package com.fadhlika.lokasi.controller.api;

import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.ByteBuffer;

@RestController
public class ImportController {
    private final ImportService importService;

    @Autowired
    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/api/v1/import")
    public ResponseEntity<Void> importData(@RequestParam("source") String source, @RequestParam("file") MultipartFile file) throws IOException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        importService.doImport(user.getId(), source, file.getOriginalFilename(), ByteBuffer.wrap(file.getBytes()));

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
