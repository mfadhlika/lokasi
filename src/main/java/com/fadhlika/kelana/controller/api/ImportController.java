package com.fadhlika.kelana.controller.api;

import com.fadhlika.kelana.dto.Response;
import com.fadhlika.kelana.model.Import;
import com.fadhlika.kelana.model.User;
import com.fadhlika.kelana.service.ImportService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/imports")
public class ImportController {
    private final ImportService importService;

    ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping
    public Response<Void> importLocations(@RequestParam("source") String source,
            @RequestParam("file") MultipartFile file) throws IOException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Import createdImport = importService.importLocations(user.getId(), source, file.getOriginalFilename(),
                file.getInputStream());

        importService.processImport(createdImport);

        return new Response<>("uploaded");
    }

    @GetMapping
    public Response<List<Import>> getImports() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Import> imports = importService.getImports(user.getId());

        return new Response<>(imports);
    }

    @GetMapping("/{importId}/raw")
    public @ResponseBody byte[] downloadImport(@PathVariable int importId) throws IOException {
        Import anImport = importService.getImport(importId);

        return anImport.content().readAllBytes();
    }

    @DeleteMapping("/{importId}")
    public Response<Void> deleteImport(@PathVariable int importId) throws IOException {
        importService.deleteImport(importId);

        return new Response<>("success");
    }
}
