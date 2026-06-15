package com.learningPlatform.registration.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.file.Path;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    public S3Service(@Value("${aws.accessKeyId}") String accessKey,
                     @Value("${aws.secretKey}") String secretKey,
                     @Value("${aws.region}") String region) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    // 1. Método para subir (o modificar)
    public String subirArchivo(Long idInscripcion, Path rutaArchivoLocal) {
        String s3Key = idInscripcion + "/" + rutaArchivoLocal.getFileName().toString();
        PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();
        s3Client.putObject(putOb, rutaArchivoLocal);
        return "Archivo guardado exitosamente en S3: " + s3Key;
    }

    // 2. Método para DESCARGAR 
    public byte[] descargarArchivo(Long idInscripcion, String nombreArchivo) {
        String s3Key = idInscripcion + "/" + nombreArchivo;
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();
        try {
            return s3Client.getObject(getObjectRequest).readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Error al descargar archivo de S3: " + e.getMessage());
        }
    }

    // 3. Método para BORRAR
    public String borrarArchivo(Long idInscripcion, String nombreArchivo) {
        String s3Key = idInscripcion + "/" + nombreArchivo;
        DeleteObjectRequest deleteObj = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();
        s3Client.deleteObject(deleteObj);
        return "Archivo borrado de S3: " + s3Key;
    }
}
