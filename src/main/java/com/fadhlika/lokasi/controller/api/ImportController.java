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

@RestController
public class ImportController {
    @Autowired
    private ImportService importService;

    @PostMapping("/api/v1/import")
    public ResponseEntity<Void> importLocations(@RequestParam("source") String source,
            @RequestParam("file") MultipartFile file) throws IOException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        importService.importLocations(user.getId(), source, file.getOriginalFilename(),
                file.getInputStream());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
