package com.fadhlika.kelana.controller.api;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fadhlika.kelana.dto.Response;
import com.fadhlika.kelana.model.Export;
import com.fadhlika.kelana.model.User;
import com.fadhlika.kelana.service.ExportService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/exports")
public class ExportController {
    private final ExportService exportService;

    ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @PostMapping
    public ResponseEntity<Response<Void>> exportLocations(@RequestBody Export export) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        export = exportService.exportLocations(new Export(user.getId(), export.startAt(), export.endAt()));

        exportService.processExportLocations(export);

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>("export queued"));
    }

    @GetMapping
    public Response<List<Export>> fetchExports() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Export> exports = exportService.getExports(user.getId());

        return new Response<List<Export>>(exports);
    }

    @GetMapping("/{exportId}/raw")
    public @ResponseBody byte[] getExportContent(@PathVariable int exportId) throws IOException {
        Export export = exportService.getExport(exportId);

        return export.content().readAllBytes();
    }

    @DeleteMapping("/{exportId}")
    public Response<Void> deleteExport(@PathVariable int exportId) {
        exportService.deleteExport(exportId);

        return new Response<>(String.format("export {} deleted", exportId));
    }
}
