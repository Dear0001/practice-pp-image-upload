package org.example.priacticeppimage.controller;

import org.springframework.core.io.Resource;
import lombok.AllArgsConstructor;
import org.example.priacticeppimage.model.ApiResponse;
import org.example.priacticeppimage.model.FileResponse;
import org.example.priacticeppimage.service.FileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("files")
@AllArgsConstructor
public class FileController {
    private final FileService fileService;
//single post
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, value= "/single_post")
    public ResponseEntity<?> uploadFile(@RequestParam MultipartFile file) throws IOException {
        String fileName = fileService.saveFile(file);
//        String fileUrl = "http://localhost:8080/files?fileName=" + fileName;
        String fileUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(fileName).toUriString();
        FileResponse fileResponse = new FileResponse(fileName,fileUrl,file.getContentType(), file.getSize());
        ApiResponse<FileResponse> response = ApiResponse.<FileResponse>builder()
                .message("successfully uploaded file")
                .status(HttpStatus.OK).code(200)
                .payload(fileResponse).build();
        return ResponseEntity.ok(response);
    }
//multiple post
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, value= "/multiple_post")
    public ResponseEntity<?> uploadFile(@RequestParam List<MultipartFile> files) throws IOException {
        FileResponse fileResponse = null;
        List<FileResponse> fileResponses = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = fileService.saveFile(file);
//            String fileUrl = "http://localhost:8080/files?fileName=" + fileName;
            String fileUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path(fileName).toUriString();
            fileResponse = new FileResponse(fileName, fileUrl, file.getContentType(), file.getSize());
            fileResponses.add(fileResponse);
        }

        ApiResponse<List<FileResponse>> responses = ApiResponse.<List<FileResponse>>builder()
                .message("successfully uploaded file")
                .status(HttpStatus.OK).code(200)
                .payload(fileResponses).build();
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<?> getFile(@RequestParam String fileName) throws IOException {
        Resource resource = fileService.getFileByFileName(fileName);
        MediaType mediaType;
        if (fileName.endsWith(".pdf")) {
            mediaType = MediaType.APPLICATION_PDF;
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".gif")) {
            mediaType = MediaType.IMAGE_PNG;
        } else {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .contentType(mediaType).body(resource);

    }
}