package org.example.priacticeppimage.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;

public interface FileService {
    String saveFile(MultipartFile file) throws IOException;
    Resource getFileByFileName(String fileName) throws IOException;
}
