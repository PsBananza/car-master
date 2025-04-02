package com.autoresourse.car_master.service.file;

import com.autoresourse.car_master.dto.FileDto;
import com.autoresourse.car_master.entity.Files;
import com.autoresourse.car_master.entity.OrganizationCards;
import com.autoresourse.car_master.mapper.FileMapper;
import com.autoresourse.car_master.repository.FileRepository;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final FileMapper fileMapper;

    @Override
    @Transactional
    public void uploadFile(MultipartFile file, OrganizationCards card) {
        if (file.isEmpty()) {
            log.error("Uploaded file is empty");
            return;
        }

        try {
            byte[] compressedData = compressImage(file);

            Files fileEntity = new Files();
            fileEntity.setCards(card);
            fileEntity.setOriginalFileName(file.getOriginalFilename());
            fileEntity.setContentType(file.getContentType());
            fileEntity.setSize(compressedData.length);
            fileEntity.setFileData(compressedData);

            fileRepository.save(fileEntity);
        } catch (IOException | ImageProcessingException e) {
            log.error("Error compressing file: {}", e.getMessage());
        }
    }

    @Override
    public FileDto getFileData(UUID fileId) {
        Files file = fileRepository.findById(fileId).orElse(null);
        return fileMapper.toDto(file);
    }

    @Override
    public void deleteFile(UUID uuid) {
        fileRepository.deleteById(uuid);
    }

    /**
     * Сжатие изображения перед сохранением
     */
    public byte[] compressImage(MultipartFile file) throws IOException, ImageProcessingException {
        if (!file.getContentType().startsWith("image")) {
            return file.getBytes(); // If it's not an image, save it unchanged
        }

        // Read the original image
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new IOException("Invalid image file");
        }

        // Extract EXIF metadata using Metadata Extractor
        InputStream inputStream = file.getInputStream();
        Metadata metadata = ImageMetadataReader.readMetadata(inputStream);

        int orientation = 1; // Default orientation is 1 (no rotation)

        // Iterate through directories to find EXIF information
        for (Directory directory : metadata.getDirectories()) {
            if (directory instanceof ExifIFD0Directory) {
                ExifIFD0Directory exifIFD0Directory = (ExifIFD0Directory) directory;
                try {
                    // Get the orientation from the EXIF data
                    orientation = exifIFD0Directory.getInt(ExifDirectoryBase.TAG_ORIENTATION);
                } catch (Exception e) {
                    // If orientation is not available, leave it as the default (1)
                }
            }
        }

        // Rotate the image based on the EXIF orientation
        originalImage = rotateImageBasedOnExifOrientation(originalImage, orientation);

        // Resize the image
        BufferedImage resizedImage = resizeImage(originalImage, originalImage.getWidth(), originalImage.getHeight());

        // Compress the image into a byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        compressToJPEG(resizedImage, outputStream, 0.4f); // Here 0.4f is the compression quality (from 0 to 1)

        return outputStream.toByteArray();
    }

    private BufferedImage rotateImageBasedOnExifOrientation(BufferedImage image, int orientation) {
        switch (orientation) {
            case 1:
                return image; // No rotation
            case 3:
                return rotateImage(image, 180);
            case 6:
                return rotateImage(image, 90);
            case 8:
                return rotateImage(image, 270);
            default:
                return image; // If orientation is not found, return the image as is
        }
    }

    private BufferedImage rotateImage(BufferedImage image, int angle) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage rotatedImage = new BufferedImage(h, w, image.getType());
        Graphics2D g2d = rotatedImage.createGraphics();
        g2d.rotate(Math.toRadians(angle), h / 2, h / 2);
        g2d.drawImage(image, (h - w) / 2, (w - h) / 2, null);
        g2d.dispose();
        return rotatedImage;
    }

    /**
     * Метод для изменения размера изображения.
     *
     * @param originalImage исходное изображение
     * @param width желаемая ширина
     * @param height желаемая высота
     * @return новое изображение
     */
    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        // Создаем новое изображение с новыми размерами
        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        // Создаем новый BufferedImage, куда будет записано масштабированное изображение
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Рисуем масштабированное изображение
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();

        return resizedImage;
    }

    /**
     * Сжать изображение в формат JPEG с заданным качеством.
     *
     * @param image изображение, которое нужно сжать
     * @param outputStream поток, куда будет записано сжатое изображение
     * @param quality качество сжатия (от 0 до 1)
     */
    private void compressToJPEG(BufferedImage image, ByteArrayOutputStream outputStream, float quality) throws IOException {
        // Получаем ImageWriter для JPEG
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();

        // Создаем параметр сжатия
        JPEGImageWriteParam param = new JPEGImageWriteParam(null);
        param.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality); // Устанавливаем качество сжатия

        // Создаем поток для записи изображения
        ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream);
        writer.setOutput(ios);

        // Записываем изображение
        writer.write(null, new IIOImage(image, null, null), param);

        ios.close();
        writer.dispose();
    }
}
