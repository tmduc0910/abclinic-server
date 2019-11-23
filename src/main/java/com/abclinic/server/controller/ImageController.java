package com.abclinic.server.controller;

import com.abclinic.server.base.BaseController;
import com.abclinic.server.base.Views;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.ImageAlbum;
import com.abclinic.server.model.entity.Image;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.repository.*;
import com.abclinic.server.service.GooglePhotosService;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.photos.types.proto.Album;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @package com.abclinic.server.controller
 * @author tmduc
 * @created 11/23/2019 3:38 PM
 */
@RestController
@RequestMapping(value = "/image")
public class ImageController extends BaseController {

    @Value("${file.upload-dir}")
    private String uploadDirectory;

    @Autowired
    public ImageController(AlbumRepository albumRepository, ImageRepository imageRepository, PatientRepository patientRepository) {
        this.albumRepository = albumRepository;
        this.imageRepository = imageRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public void init() {
        logger = LoggerFactory.getLogger(ImageController.class);
    }

    /**
     * @apiNote hàm xử lý việc upload ảnh hoặc nhiều ảnh lên server
     * @uri /image/upload
     * @param userId mã người dùng được lưu vào HttpHeader
     * @param fileName tên file ảnh
     * @param fileType đuôi của file ảnh. VD: "jpg", "png", "gif"...
     * @param files
     * @return
     */
    @PostMapping(value = "/upload")
    @JsonView(Views.Public.class)
    public ResponseEntity processUpload(@NotNull @RequestHeader("user-id") int userId, @RequestParam("file-name") String fileName, @RequestParam("file-type") String fileType, @RequestParam("file") MultipartFile... files) {
        Optional<Patient> patient = patientRepository.findById(userId);
        if (!patient.isPresent())
            throw new ForbiddenException(userId);
        Album album = GooglePhotosService.makeAlbum();
        ImageAlbum imageAlbum = new ImageAlbum(album.getId(), patient.get(), album.getTitle());
        save(imageAlbum);

        try {
            List<Image> images = new ArrayList<>();
            imageAlbum = albumRepository.findById(imageAlbum.getId()).get();
            for (MultipartFile file: files) {
                File f = toFile(fileName, fileType, file.getBytes());
                String token = GooglePhotosService.getToken(fileName, f.getPath());
                String path = GooglePhotosService.createItem(token, album.getId(), f.getName());
                images.add(new Image(imageAlbum, fileName, fileType, path));
            }
            images.forEach(this::save);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/album/all")
    @JsonView(Views.Public.class)
    public ResponseEntity<List<ImageAlbum>> processGetAllAlbums(@NotNull @RequestHeader("user-id") int userId) {
        Patient patient = patientRepository.findById(userId).get();
        Optional<List<ImageAlbum>> opt = albumRepository.findByPatient(patient);
        if (opt.isPresent()) {
            return new ResponseEntity<>(opt.get(), HttpStatus.OK);
        } else throw new NotFoundException(userId);
    }

    @GetMapping(value = "/album/{album-id}")
    @JsonView(Views.Private.class)
    public ResponseEntity<ImageAlbum> processGetAlbum(@NotNull @RequestHeader("user-id") int userId, @PathVariable("album-id") int albumId) {
        Patient patient = patientRepository.findById(userId).get();
        Optional<ImageAlbum> opt = albumRepository.findById(albumId);
        if (opt.isPresent()) {
            if (opt.get().getPatient().equals(patient))
                return new ResponseEntity<>(opt.get(), HttpStatus.OK);
            else throw new ForbiddenException(userId);
        } else throw new NotFoundException(userId);
    }

    private File toFile(String name, String type, byte[] bytes) {
        try {
            File file = new File(uploadDirectory + name + "." + type);
            FileUtils.writeByteArrayToFile(file, bytes);
            try (InputStream is = new FileInputStream(file)) {
                BufferedImage image = ImageIO.read(is);
                try (OutputStream os = new FileOutputStream(file)) {
                    ImageIO.write(image, type, os);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            }
            return file;
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }
}
