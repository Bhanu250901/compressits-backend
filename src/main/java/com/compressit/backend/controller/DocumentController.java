package com.compressit.backend.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@RequestMapping("/api/document")
@CrossOrigin("*")
public class DocumentController {

    @PostMapping("/convert-to-pdf")
    public ResponseEntity<byte[]> convertToPdf(
            @RequestParam("file") MultipartFile file
    ) {

        try {

            // ORIGINAL FILE NAME
            String originalName =
                    file.getOriginalFilename();

            if (
                    originalName == null
            ) {

                return ResponseEntity
                        .badRequest()
                        .build();
            }

            // TEMP DIRECTORY
            String tempDir =
                    System.getProperty(
                            "java.io.tmpdir"
                    );

            // CREATE INPUT FILE
            File inputFile =
                    new File(
                            tempDir +
                                    File.separator +
                                    originalName
                    );

            // SAVE FILE
            file.transferTo(inputFile);

            // OUTPUT DIRECTORY
            File outputDir =
                    new File(tempDir);

            // LIBREOFFICE COMMAND
            ProcessBuilder processBuilder =
                    new ProcessBuilder(
                            "C:\\Program Files\\LibreOffice\\program\\soffice.exe",
                            "--headless",
                            "--convert-to",
                            "pdf",
                            inputFile.getAbsolutePath(),
                            "--outdir",
                            outputDir.getAbsolutePath()
                    );

            processBuilder.redirectErrorStream(true);

            Process process =
                    processBuilder.start();

            // READ OUTPUT
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    process.getInputStream()
                            )
                    );

            String line;

            while (
                    (line = reader.readLine())
                            != null
            ) {

                System.out.println(line);
            }

            // WAIT FOR PROCESS
            int exitCode =
                    process.waitFor();

            System.out.println(
                    "LibreOffice Exit Code: "
                            + exitCode
            );

            // CHECK FAILED
            if (exitCode != 0) {

                return ResponseEntity
                        .badRequest()
                        .build();
            }

            // OUTPUT PDF NAME
            String pdfName =
                    originalName.replaceAll(
                            "\\.[^.]+$",
                            ".pdf"
                    );

            File pdfFile =
                    new File(
                            outputDir,
                            pdfName
                    );

            // CHECK PDF EXISTS
            if (!pdfFile.exists()) {

                return ResponseEntity
                        .badRequest()
                        .build();
            }

            // PDF BYTES
            byte[] pdfBytes =
                    new FileInputStream(
                            pdfFile
                    ).readAllBytes();

            // DELETE TEMP FILES
            inputFile.delete();
            pdfFile.delete();

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=converted.pdf"
                    )
                    .contentType(
                            MediaType.APPLICATION_PDF
                    )
                    .body(pdfBytes);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .build();
        }
    }
}