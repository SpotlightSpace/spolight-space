package com.spotlightspace.core.attachment.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.spotlightspace.common.entity.TableRole;
import com.spotlightspace.core.attachment.domain.Attachment;
import com.spotlightspace.core.attachment.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttachmentService {
    private final AttachmentRepository attachmentRepository;

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public void addAttachmentList(List<MultipartFile> files, Long id, TableRole tableRole) throws IOException {
        for (MultipartFile file : files) {
            saveAttachment(file, id, tableRole);
        }
    }

    @Transactional
    public void addAttachment(MultipartFile file, Long id, TableRole tableRole) throws IOException {
        saveAttachment(file, id, tableRole);
    }

    private void saveAttachment(MultipartFile file, Long id, TableRole tableRole) throws IOException {
        String randomName = UUID.randomUUID().toString().substring(0, 8);;
        String fileName = randomName + file.getOriginalFilename();
        String fileUrl = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + fileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
        attachmentRepository.save(Attachment.of(fileUrl, tableRole, id));
    }
}
