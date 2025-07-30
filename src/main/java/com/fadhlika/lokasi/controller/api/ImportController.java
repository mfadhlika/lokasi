package com.fadhlika.lokasi.controller.api;

import com.fadhlika.lokasi.dto.GetImportResponse;
import com.fadhlika.lokasi.dto.Response;
import com.fadhlika.lokasi.model.Import;
import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/import")
public class ImportController {
    @Autowired
    private ImportService importService;

    @PostMapping
    public ResponseEntity<Void> importLocations(@RequestParam("source") String source,
            @RequestParam("file") MultipartFile file) throws IOException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        importService.importLocations(user.getId(), source, file.getOriginalFilename(),
                file.getInputStream());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<GetImportResponse> getImports() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Import> imports = importService.getImports(user.getId());

        return ResponseEntity.ok(new GetImportResponse(imports));
    }
}
